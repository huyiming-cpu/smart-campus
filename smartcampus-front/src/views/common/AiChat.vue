<template>
  <div class="ai-page">
    <!-- 顶部导航条 -->
    <header class="ai-header">
      <div class="ai-header-inner">
        <div class="ai-logo">
          <div class="logo-sphere"><img src="/robo.png" class="sphere-img" /></div>
          <span class="logo-text">小智 AI</span>
          <el-tag size="small" type="success" effect="plain">在线</el-tag>
        </div>
        <div class="ai-header-actions">
          <el-button text size="small" @click="clearChat">清空对话</el-button>
        </div>
      </div>
    </header>

    <!-- 主区域 -->
    <div class="ai-body">
      <div class="ai-container" ref="scrollRef">
        <!-- 欢迎态 -->
        <div v-if="msgs.length===0" class="welcome-area">
          <div class="welcome-sphere">
            <div class="big-sphere">
              <img src="/robo.png" class="sphere-img" />
              <div class="sphere-hl"></div>
              <div class="sphere-sd"></div>
            </div>
            <div class="sphere-ring"></div>
            <div class="sphere-floor"></div>
          </div>
          <h1 class="welcome-title">你好，我是小智</h1>
          <p class="welcome-sub">你的校园智能助手，随时为你解答问题</p>
          <div class="suggest-grid">
            <div v-for="s in suggests" :key="s" class="suggest-card" @click="quickSend(s)">
              <span class="suggest-icon">{{ s.icon }}</span>
              <span class="suggest-text">{{ s.text }}</span>
            </div>
          </div>
        </div>

        <!-- 对话区 -->
        <div v-else class="chat-area">
          <div v-for="(m,i) in msgs" :key="i" :class="['chat-msg', m.role]">
            <div v-if="m.role==='assistant'" class="chat-avatar">
              <div class="mini-sphere"><img src="/robo.png" class="sphere-img" /></div>
            </div>
            <div class="chat-bubble-wrap">
              <div class="chat-name">{{ m.role==='user'?'我':'小智' }}</div>
              <div class="chat-bubble">{{ m.content }}</div>
            </div>
            <div v-if="m.role==='user'" class="chat-avatar user-avatar">
              <el-icon :size="18"><UserFilled /></el-icon>
            </div>
          </div>
          <div v-if="loading" class="chat-msg assistant">
            <div class="chat-avatar">
              <div class="mini-sphere thinking"><img src="/robo.png" class="sphere-img" /></div>
            </div>
            <div class="chat-bubble-wrap">
              <div class="chat-name">小智</div>
              <div class="chat-bubble typing"><span/><span/><span/></div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部输入区 -->
    <div class="ai-footer">
      <div class="ai-input-wrap">
        <div class="ai-input-box">
          <el-input
            v-model="input"
            placeholder="输入你的问题，小智为你解答..."
            :disabled="loading"
            :rows="1"
            type="textarea"
            resize="none"
            class="ai-input"
            @keydown.enter.exact.prevent="send"
          />
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!input.trim()"
            @click="send"
            class="send-btn"
          >
            <el-icon :size="18"><Promotion /></el-icon>
          </el-button>
        </div>
        <p class="input-hint">小智可能会产生不准确信息，请注意甄别</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue'
import { chat, getChatHistory } from '@/api/ai'
import { Promotion, UserFilled } from '@element-plus/icons-vue'

const msgs = ref([]), input = ref(''), loading = ref(false), scrollRef = ref(null)

const suggests = [
  { icon: '📚', text: '如何查询我的课程成绩？' },
  { icon: '📅', text: '本学期校历安排是怎样的？' },
  { icon: '💳', text: '一卡通如何充值？' },
  { icon: '🎓', text: '毕业设计的流程是什么？' },
  { icon: '🏫', text: '学校有哪些奖学金可以申请？' },
  { icon: '📝', text: '等级考试什么时候报名？' },
]

const scrollBottom = async () => { await nextTick(); const el = scrollRef.value; if (el) el.scrollTop = el.scrollHeight }

onMounted(async () => {
  try {
    const res = await getChatHistory()
    const list = res?.data || res
    if (list && Array.isArray(list) && list.length) {
      msgs.value = list.flatMap(h => [{ role: 'user', content: h.question || '' }, { role: 'assistant', content: h.answer || '' }])
    }
  } catch {}
  await scrollBottom()
})

watch(msgs, () => scrollBottom(), { deep: true })

const quickSend = (s) => { input.value = s.text; send() }

const send = async () => {
  const text = input.value.trim()
  if (!text || loading.value) return
  msgs.value.push({ role: 'user', content: text }); input.value = ''; loading.value = true
  try {
    const res = await chat(text, 'default')
    const reply = (typeof res.data === 'string' ? res.data : (res.data?.reply || res.data?.content)) || '（未获取到回复）'
    msgs.value.push({ role: 'assistant', content: reply })
  } catch {
    msgs.value.push({ role: 'assistant', content: '抱歉，请求出现了问题，请稍后重试。' })
  } finally {
    loading.value = false
  }
}

const clearChat = () => { msgs.value = [] }
</script>

<style scoped>
.ai-page {
  display: flex; flex-direction: column; height: 100vh;
  background: linear-gradient(180deg, #F8FAFE 0%, #EDF2F9 100%);
  font-family: "Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif;
}

/* ===== 顶栏 ===== */
.ai-header {
  background: rgba(255,255,255,.85); backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0,0,0,.04);
  flex-shrink: 0; z-index: 10;
}
.ai-header-inner {
  max-width: 900px; margin: 0 auto; display: flex; align-items: center;
  justify-content: space-between; padding: 12px 24px;
}
.ai-logo { display: flex; align-items: center; gap: 10px; }
.logo-sphere { width: 34px; height: 34px; border-radius: 50%; overflow: hidden; box-shadow: 0 2px 8px rgba(91,155,213,.2); }
.logo-text { font-size: 17px; font-weight: 700; color: #5B9BD5; }
.sphere-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.ai-header-actions { display: flex; gap: 8px; }

/* ===== 主区 ===== */
.ai-body { flex: 1; overflow-y: auto; padding: 0 24px; }
.ai-container { max-width: 800px; margin: 0 auto; padding: 24px 0; min-height: 100%; display: flex; flex-direction: column; }

/* 欢迎区 */
.welcome-area { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; text-align: center; padding: 40px 0 60px; }
.welcome-sphere { position: relative; margin-bottom: 28px; }
.big-sphere {
  width: 120px; height: 120px; border-radius: 50%; overflow: hidden; position: relative;
  box-shadow: 0 12px 40px rgba(91,155,213,.2);
  animation: sphereFloat 4s ease-in-out infinite;
}
.sphere-hl {
  position: absolute; inset: 0; border-radius: 50%;
  background: radial-gradient(ellipse at 32% 28%, rgba(255,255,255,.35) 0%, rgba(255,255,255,.08) 22%, transparent 55%, rgba(0,0,0,.12) 78%, rgba(0,0,0,.3) 100%);
  pointer-events: none;
}
.sphere-sd {
  position: absolute; inset: 0; border-radius: 50%;
  background: radial-gradient(ellipse at 62% 72%, rgba(0,0,0,.18) 0%, transparent 45%);
  pointer-events: none;
}
.sphere-ring {
  position: absolute; inset: -10px; border-radius: 50%;
  background: radial-gradient(circle, rgba(91,155,213,.08) 45%, transparent 70%);
  pointer-events: none; z-index: -1;
}
.sphere-floor {
  position: absolute; bottom: -18px; left: 50%; transform: translateX(-50%);
  width: 60px; height: 12px; border-radius: 50%;
  background: radial-gradient(ellipse, rgba(0,0,0,.08) 0%, transparent 70%);
  animation: floorPulse 4s ease-in-out infinite;
}

.welcome-title { font-size: 28px; font-weight: 700; color: #1A1A2E; margin: 0 0 8px; }
.welcome-sub { font-size: 15px; color: #9CA3AF; margin: 0 0 32px; }

.suggest-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; width: 100%; max-width: 500px; }
.suggest-card {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 16px; border-radius: 14px;
  background: #fff; border: 1px solid #EEF0F4;
  cursor: pointer; transition: all .2s;
  box-shadow: 0 2px 8px rgba(0,0,0,.02);
}
.suggest-card:hover { transform: translateY(-2px); box-shadow: 0 6px 18px rgba(91,155,213,.08); border-color: #D4E2F4; }
.suggest-icon { font-size: 18px; flex-shrink: 0; }
.suggest-text { font-size: 13px; color: #4B5563; text-align: left; line-height: 1.4; }

/* 对话区 */
.chat-area { display: flex; flex-direction: column; gap: 20px; padding-bottom: 8px; }
.chat-msg { display: flex; gap: 10px; max-width: 85%; }
.chat-msg.user { align-self: flex-end; flex-direction: row-reverse; }
.chat-avatar { flex-shrink: 0; margin-top: 2px; }
.mini-sphere { width: 32px; height: 32px; border-radius: 50%; overflow: hidden; box-shadow: 0 2px 6px rgba(91,155,213,.15); }
.mini-sphere.thinking { animation: thinkingShake .4s ease-in-out infinite; }
.user-avatar {
  width: 32px; height: 32px; border-radius: 50%;
  background: linear-gradient(135deg, #5B9BD5, #4A8AD4);
  display: flex; align-items: center; justify-content: center; color: #fff;
}
.chat-bubble-wrap { min-width: 0; }
.chat-name { font-size: 12px; color: #9CA3AF; margin-bottom: 4px; }
.chat-msg.user .chat-name { text-align: right; }
.chat-bubble {
  padding: 10px 16px; border-radius: 16px; font-size: 14px; line-height: 1.6;
  word-break: break-word; white-space: pre-wrap;
}
.chat-msg.assistant .chat-bubble { background: #fff; color: #1A1A2E; border-bottom-left-radius: 4px; box-shadow: 0 1px 4px rgba(0,0,0,.04); }
.chat-msg.user .chat-bubble { background: linear-gradient(135deg, #5B9BD5, #4A8AD4); color: #fff; border-bottom-right-radius: 4px; }
.typing { display: flex; gap: 4px; padding: 6px 0; }
.typing span { width: 6px; height: 6px; border-radius: 50%; background: #C4C9D1; animation: blink 1.4s infinite both; }
.typing span:nth-child(2) { animation-delay: .2s; } .typing span:nth-child(3) { animation-delay: .4s; }

/* 底部 */
.ai-footer { flex-shrink: 0; padding: 16px 24px 24px; background: rgba(255,255,255,.6); backdrop-filter: blur(8px); border-top: 1px solid rgba(0,0,0,.03); }
.ai-input-wrap { max-width: 800px; margin: 0 auto; }
.ai-input-box {
  display: flex; gap: 10px; align-items: flex-end;
  background: #fff; border: 1px solid #E5E7EB; border-radius: 18px;
  padding: 8px 8px 8px 18px;
  box-shadow: 0 4px 16px rgba(0,0,0,.04);
  transition: border-color .2s, box-shadow .2s;
}
.ai-input-box:focus-within { border-color: #5B9BD5; box-shadow: 0 4px 20px rgba(91,155,213,.1); }
.ai-input :deep(.el-textarea__inner) {
  border: none; box-shadow: none; padding: 6px 0; font-size: 14px; background: transparent; resize: none;
}
.send-btn { flex-shrink: 0; width: 42px; height: 42px; border-radius: 14px; }
.input-hint { font-size: 11px; color: #C4C9D1; text-align: center; margin: 8px 0 0; }

/* 动画 */
@keyframes sphereFloat { 0%,100% { transform: translateY(0); } 50% { transform: translateY(-8px); } }
@keyframes floorPulse { 0%,100% { transform: translateX(-50%) scale(1); opacity: .8; } 50% { transform: translateX(-50%) scale(1.4); opacity: .4; } }
@keyframes thinkingShake { 0%,100% { transform: rotate(0); } 25% { transform: rotate(-5deg); } 75% { transform: rotate(5deg); } }
@keyframes blink { 0%,96%,100% { opacity: .2; } 50% { opacity: 1; } }

@media (max-width: 640px) {
  .suggest-grid { grid-template-columns: 1fr; }
  .chat-msg { max-width: 92%; }
}
</style>
