<template>
  <div v-if="show" class="floating-root" :style="{ left: pos.x + 'px', top: pos.y + 'px' }">
    <transition name="panel">
      <div v-if="chatOpen" class="chat-panel">
        <div class="panel-head">
          <div class="panel-title">
            <div class="panel-ball"></div>
            <div><span class="panel-name">小智</span><span class="panel-dot">在线</span></div>
          </div>
          <el-button :icon="Close" circle size="small" text @click="chatOpen=false"/>
        </div>
        <div class="panel-body" ref="bodyRef">
          <div v-if="msgs.length===0" class="panel-empty">
            <div class="empty-ball"><div class="ball-face happy"><div class="eb-eyes"></div><div class="eb-mouth"></div></div></div>
            <p>你好！我是小智，有什么可以帮你的？</p>
          </div>
          <div v-for="(m,i) in msgs" :key="i" :class="['msg', m.role]">
            <div v-if="m.role==='assistant'" class="msg-ball" :class="mood"></div>
            <span v-else class="msg-avatar-user">🙂</span>
            <div class="msg-bubble">{{ m.content }}</div>
          </div>
          <div v-if="loading" class="msg assistant">
            <div class="msg-ball thinking"></div>
            <div class="msg-bubble typing"><span/><span/><span/></div>
          </div>
        </div>
        <div class="panel-foot">
          <el-input v-model="input" placeholder="问小智任何问题..." :disabled="loading" @keyup.enter="send"/>
          <el-button type="primary" circle :loading="loading" :disabled="!input.trim()" @click="send" class="send-icon">
            <el-icon :size="16"><Promotion /></el-icon>
          </el-button>
        </div>
      </div>
    </transition>

    <!-- 3D 球体 -->
    <div class="ball-wrap" :class="{ dragging: isDrag }" @mousedown.prevent="startDrag" @click="togglePanel">
      <div class="ball-3d" :style="ballStyle">
        <div class="ball-hl"></div>
        <div class="ball-sd"></div>
        <div class="ball-face" :class="mood">
          <div class="bf-eyes"></div>
          <div class="bf-blush"></div>
          <div class="bf-mouth"></div>
        </div>
      </div>
      <div class="ball-glow"></div>
      <div class="ball-floor"></div>
      <div class="ball-label">小智</div>
      <div class="ball-dot"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted, onBeforeUnmount } from 'vue'
import { chat, getChatHistory } from '@/api/ai'
import { Close, Promotion } from '@element-plus/icons-vue'

const show=ref(true),chatOpen=ref(false),input=ref(''),msgs=ref([]),loading=ref(false),bodyRef=ref(null)
const isDrag=ref(false),mood=ref('happy')
const pos=reactive({x:0,y:0}),mouse=reactive({x:0,y:0})
let dragStart={x:0,y:0},mouseStart={x:0,y:0},hasMoved=false

const om=(e)=>{mouse.x=e.clientX;mouse.y=e.clientY}
onMounted(()=>{pos.x=window.innerWidth-120;pos.y=window.innerHeight-240;document.addEventListener('mousemove',om)})
onBeforeUnmount(()=>{document.removeEventListener('mousemove',om)})

const ballStyle=computed(()=>{const cx=pos.x+40,cy=pos.y+40;return{transform:`rotateX(${(mouse.y-cy)/window.innerHeight*20}deg) rotateY(${(mouse.x-cx)/window.innerWidth*25}deg) scale(${isDrag.value?1.12:1})`,transition:isDrag.value?'none':'transform .5s cubic-bezier(.23,1,.32,1)'}})

watch(chatOpen,async(v)=>{if(v){mood.value='listening';if(!msgs.value.length){try{const r=await getChatHistory();const list=r?.data||r;if(list&&Array.isArray(list)&&list.length)msgs.value=list.flatMap(h=>[{role:'user',content:h.question||''},{role:'assistant',content:h.answer||''}])}catch{}await nextTick();if(bodyRef.value)bodyRef.value.scrollTop=bodyRef.value.scrollHeight}}else{mood.value='happy'}})

const startDrag=(e)=>{hasMoved=false;mouseStart={x:e.clientX,y:e.clientY};dragStart={x:pos.x,y:pos.y};document.addEventListener('mousemove',onDrag);document.addEventListener('mouseup',stopDrag)}
const onDrag=(e)=>{const dx=e.clientX-mouseStart.x,dy=e.clientY-mouseStart.y;if(Math.abs(dx)>3||Math.abs(dy)>3)hasMoved=true;if(hasMoved)isDrag.value=true;pos.x=Math.min(window.innerWidth-30,Math.max(-40,dragStart.x+dx));pos.y=Math.min(window.innerHeight-30,Math.max(-40,dragStart.y+dy))}
const stopDrag=()=>{document.removeEventListener('mousemove',onDrag);document.removeEventListener('mouseup',stopDrag);setTimeout(()=>{isDrag.value=false},100)}
const togglePanel=()=>{if(!hasMoved)chatOpen.value=!chatOpen.value}

const send=async()=>{const q=input.value.trim();if(!q)return;msgs.value.push({role:'user',content:q});input.value='';loading.value=true;mood.value='thinking';try{const r=await chat(q,'float');const reply=typeof r==='string'?r:(r?.data?.reply||r?.data?.content||r?.data||'...');msgs.value.push({role:'assistant',content:reply});mood.value='happy'}catch{msgs.value.push({role:'assistant',content:'小智网络出问题了~'});mood.value='happy'}finally{loading.value=false;await nextTick();if(bodyRef.value)bodyRef.value.scrollTop=bodyRef.value.scrollHeight}}

onBeforeUnmount(()=>{document.removeEventListener('mousemove',onDrag);document.removeEventListener('mouseup',stopDrag)})
</script>

<style scoped>
.floating-root{position:fixed;z-index:9999}

/* 面板 */
.chat-panel{position:absolute;bottom:120px;right:0;width:380px;height:480px;background:#fff;border-radius:18px;box-shadow:0 12px 40px rgba(0,0,0,.12);display:flex;flex-direction:column;overflow:hidden;border:1px solid #EEF0F4}
.panel-head{display:flex;align-items:center;justify-content:space-between;padding:10px 16px;background:linear-gradient(135deg,#5B9BD5,#4A8AD4);color:#fff}
.panel-title{display:flex;align-items:center;gap:10px}
.panel-ball{width:32px;height:32px;border-radius:50%;background:radial-gradient(circle at 35% 30%,#D8EEF8,#A0D0F0 60%,#7BB8E0 100%);box-shadow:0 2px 8px rgba(145,190,225,.25)}
.panel-name{font-size:15px;font-weight:600}.panel-dot{font-size:11px;opacity:.7;display:block}
.panel-body{flex:1;overflow-y:auto;padding:14px 16px;background:#F8FAFE;display:flex;flex-direction:column;gap:12px}
.panel-empty{text-align:center;margin:auto;color:#9CA3AF}
.empty-ball{width:60px;height:60px;border-radius:50%;margin:0 auto 10px;position:relative;background:radial-gradient(circle at 35% 28%,#D8EEF8,#A0D0F0 55%,#7BB8E0 100%);box-shadow:0 6px 20px rgba(145,190,225,.25);animation:float 3.5s ease-in-out infinite}
.eb-eyes::before,.eb-eyes::after{content:'';display:inline-block;width:7px;height:7px;border-radius:50%;background:#fff;margin:0 3px}

.msg{display:flex;gap:8px;align-items:flex-start}.msg.user{flex-direction:row-reverse}
.msg-ball{width:26px;height:26px;border-radius:50%;flex-shrink:0;background:radial-gradient(circle at 35% 30%,#D8EEF8,#A0D0F0 60%,#7BB8E0 100%);box-shadow:0 2px 6px rgba(145,190,225,.15)}
.msg-ball.thinking{animation:shake .4s ease-in-out infinite}
.msg-avatar-user{flex-shrink:0;font-size:18px}
.msg-bubble{max-width:78%;padding:9px 13px;border-radius:14px;font-size:13px;line-height:1.5;word-break:break-word}
.msg.user .msg-bubble{background:#5B9BD5;color:#fff;border-bottom-right-radius:4px}
.msg.assistant .msg-bubble{background:#fff;color:#1A1A2E;border-bottom-left-radius:4px;box-shadow:0 1px 3px rgba(0,0,0,.04)}
.typing{display:flex;gap:4px;padding:10px 14px}
.typing span{width:5px;height:5px;border-radius:50%;background:#C4C9D1;animation:blink 1.4s infinite both}
.typing span:nth-child(2){animation-delay:.2s}.typing span:nth-child(3){animation-delay:.4s}
.panel-foot{display:flex;gap:8px;align-items:center;padding:10px 14px;border-top:1px solid #F0F2F5;background:#fff}
.panel-foot :deep(.el-input__wrapper){border-radius:20px;background:#F5F6F8}
.send-icon{flex-shrink:0;width:36px;height:36px}

/* 3D 球体 */
.ball-wrap{width:70px;cursor:grab;user-select:none;text-align:center}.ball-wrap:active{cursor:grabbing}
.ball-3d{width:70px;height:70px;border-radius:50%;position:relative;will-change:transform;transform-style:preserve-3d;box-shadow:0 4px 14px rgba(145,190,225,.18);background:radial-gradient(circle at 35% 30%,#D8EEF8 0%,#A0D0F0 35%,#7BB8E0 65%,#5A9CC8 100%);animation:float 3.5s ease-in-out infinite}
.ball-hl{position:absolute;inset:0;border-radius:50%;background:radial-gradient(ellipse at 30% 25%,rgba(255,255,255,.45) 0%,rgba(255,255,255,.12) 22%,transparent 50%,rgba(0,0,0,.04) 75%,rgba(0,0,0,.1) 100%);pointer-events:none}
.ball-sd{position:absolute;inset:0;border-radius:50%;background:radial-gradient(ellipse at 68% 72%,rgba(0,0,0,.05) 0%,transparent 38%);pointer-events:none}
.ball-face{position:absolute;top:50%;left:50%;transform:translate(-50%,-50%);display:flex;flex-direction:column;align-items:center;pointer-events:none;margin-top:-1px}
.bf-eyes{display:flex;gap:8px;position:relative}.bf-eyes::before,.bf-eyes::after{content:'';width:7px;height:7px;border-radius:50%;background:#fff;box-shadow:0 1px 2px rgba(0,0,0,.08)}
.bf-blush{position:absolute;top:10px;display:flex;gap:18px}
.bf-blush::before,.bf-blush::after{content:'';width:9px;height:5px;border-radius:50%;background:rgba(255,160,160,.35)}
.bf-mouth{margin-top:5px}
.ball-face.happy .bf-mouth{width:15px;height:6px;border-radius:0 0 7px 7px;border:2.5px solid #fff;border-top:none}
.ball-face.listening .bf-mouth{width:8px;height:8px;border-radius:50%;background:#fff}
.ball-face.thinking .bf-eyes::before{transform:scaleY(.3)}.ball-face.thinking .bf-mouth{width:6px;height:6px;border-radius:50%;background:#fff;margin-top:7px;margin-left:8px}

.ball-glow{position:absolute;top:-5px;left:50%;transform:translateX(-50%);width:82px;height:82px;border-radius:50%;background:radial-gradient(circle,rgba(145,200,230,.08) 40%,transparent 70%);pointer-events:none;z-index:-1;animation:glowPulse 3s ease-in-out infinite}
.ball-floor{position:absolute;bottom:-12px;left:50%;transform:translateX(-50%);width:38px;height:9px;border-radius:50%;background:radial-gradient(ellipse,rgba(145,180,210,.16) 0%,transparent 75%);animation:floorPulse 3.5s ease-in-out infinite}
.ball-label{font-size:10px;font-weight:600;color:#7BAED0;margin-top:6px;letter-spacing:1px}
.ball-dot{width:5px;height:5px;border-radius:50%;background:#10B981;margin:1px auto 0;box-shadow:0 0 4px rgba(16,185,129,.4)}

/* 动画 */
.panel-enter-active{animation:panelIn .3s ease-out}.panel-leave-active{animation:panelOut .2s ease-in}
@keyframes panelIn{from{opacity:0;transform:translateY(12px) scale(.95)}}
@keyframes panelOut{to{opacity:0;transform:translateY(8px) scale(.97)}}
@keyframes float{0%,100%{transform:translateY(0)}50%{transform:translateY(-5px)}}
@keyframes shake{0%,100%{transform:rotate(0)}25%{transform:rotate(-5deg)}75%{transform:rotate(5deg)}}
@keyframes glowPulse{0%,100%{opacity:.4;transform:translateX(-50%) scale(1)}50%{opacity:.7;transform:translateX(-50%) scale(1.06)}}
@keyframes floorPulse{0%,100%{transform:translateX(-50%) scale(1);opacity:.6}50%{transform:translateX(-50%) scale(1.25);opacity:.25}}
@keyframes blink{0%,96%,100%{opacity:.2}50%{opacity:1}}
</style>
