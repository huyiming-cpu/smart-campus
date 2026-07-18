<template>
  <div class="floating-ai" v-if="show">
    <div v-if="!chatOpen" class="ai-bubble" @click="chatOpen=true" title="小智AI助手">
      <span style="font-size:24px">🤖</span>
    </div>
    <div v-else class="ai-panel">
      <div class="ai-header">
        <span>🤖 小智</span>
        <el-button text @click="chatOpen=false" style="color:#fff">✕</el-button>
      </div>
      <div class="ai-body" ref="bodyRef">
        <div v-for="(m,i) in msgs" :key="i" :class="m.role==='user'?'msg-user':'msg-ai'">{{ m.content }}</div>
        <div v-if="loading" class="msg-ai">小智思考中...</div>
      </div>
      <div class="ai-input">
        <el-input v-model="input" @keyup.enter="send" placeholder="问小智..." size="small"/>
        <el-button type="primary" size="small" @click="send">发送</el-button>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, nextTick, watch } from 'vue'
import request from '@/utils/request'
const show=ref(true),chatOpen=ref(false),input=ref(''),msgs=ref([]),loading=ref(false),bodyRef=ref(null)

// 点开弹窗时加载历史
watch(chatOpen, async (open) => {
  if (open && msgs.value.length === 0) {
    try {
      const r = await request.get('/api/ai/history')
      const list = r?.data || r
      if (list && Array.isArray(list)) {
        msgs.value = list.flatMap(h => [
          { role: 'user', content: h.question || '' },
          { role: 'assistant', content: h.answer || '' }
        ])
      }
    } catch {}
  }
})

const send=async()=>{
  const q=input.value.trim();if(!q)return
  msgs.value.push({role:'user',content:q});input.value='';loading.value=true
  try{
    const r=await request.post('/api/ai/chat',{question:q,sessionId:'float'})
    const reply=typeof r.data==='string'?r.data:(r.data?.reply||r.data?.content||'...')
    msgs.value.push({role:'assistant',content:reply})
  }catch{msgs.value.push({role:'assistant',content:'小智网络出问题了~'})}
  finally{loading.value=false;await nextTick();if(bodyRef.value)bodyRef.value.scrollTop=bodyRef.value.scrollHeight}
}
</script>
<style scoped>
.floating-ai{position:fixed;bottom:24px;right:24px;z-index:9999}
.ai-bubble{width:56px;height:56px;border-radius:50%;background:linear-gradient(135deg,#667eea,#764ba2);display:flex;align-items:center;justify-content:center;cursor:pointer;box-shadow:0 4px 16px rgba(102,126,234,.4);transition:transform .2s}
.ai-bubble:hover{transform:scale(1.1)}
.ai-panel{width:360px;height:480px;background:#fff;border-radius:12px;box-shadow:0 8px 32px rgba(0,0,0,.15);display:flex;flex-direction:column;overflow:hidden}
.ai-header{background:linear-gradient(135deg,#667eea,#764ba2);color:#fff;padding:10px 16px;display:flex;justify-content:space-between;align-items:center;font-weight:bold}
.ai-body{flex:1;overflow-y:auto;padding:12px;background:#f5f7fa}
.msg-user{text-align:right;margin:8px 0}.msg-user>div{display:none}
.msg-user{background:#409eff;color:#fff;padding:8px 12px;border-radius:12px 12px 4px 12px;display:inline-block;max-width:80%;margin-left:auto;font-size:13px}
.msg-ai{background:#fff;color:#303133;padding:8px 12px;border-radius:12px 12px 12px 4px;margin:8px 0;max-width:85%;font-size:13px;box-shadow:0 1px 3px rgba(0,0,0,.06)}
.ai-input{display:flex;gap:8px;padding:10px;border-top:1px solid #ebeef5;background:#fff}
</style>
