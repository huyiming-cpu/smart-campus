<template>
  <div class="ai-chat-page">
    <el-card class="chat-card" shadow="never">
      <template #header>
        <div class="chat-header">
          <h2>AI 智能助手</h2>
          <span class="header-sub">校园智能问答</span>
        </div>
      </template>

      <!-- 消息列表 -->
      <div class="message-container" ref="msgContainerRef">
        <el-skeleton :loading="historyLoading" animated :count="3" />
        <div v-if="!historyLoading && messages.length === 0" class="empty-hint">
          <el-empty description="暂无消息，开始对话吧" />
        </div>
        <div
          v-for="(msg, idx) in messages"
          :key="idx"
          class="message-row"
          :class="msg.role === 'user' ? 'row-user' : 'row-assistant'"
        >
          <div class="message-bubble" :class="msg.role">
            <div class="bubble-avatar">
              <el-avatar :size="32" :icon="msg.role === 'user' ? 'UserFilled' : 'Service'" />
            </div>
            <div class="bubble-content">
              <div class="bubble-role">{{ msg.role === 'user' ? '我' : 'AI 助手' }}</div>
              <div class="bubble-text">{{ msg.content }}</div>
            </div>
          </div>
        </div>
        <!-- 加载中 -->
        <div v-if="sending" class="message-row row-assistant">
          <div class="message-bubble assistant">
            <div class="bubble-avatar">
              <el-avatar :size="32" icon="Service" />
            </div>
            <div class="bubble-content">
              <div class="bubble-role">AI 助手</div>
              <div class="bubble-text typing">
                <span class="dot"></span><span class="dot"></span><span class="dot"></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部输入区 -->
      <div class="input-area">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="2"
          placeholder="输入您的问题..."
          :disabled="sending"
          resize="none"
          @keydown.enter.exact.prevent="handleSend"
        />
        <el-button
          type="primary"
          :loading="sending"
          :disabled="!inputText.trim() || sending"
          @click="handleSend"
          class="send-btn"
        >
          发送
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { chat, getChatHistory } from '@/api/ai'
import { ElMessage } from 'element-plus'

const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const historyLoading = ref(false)
const msgContainerRef = ref(null)

const sessionId = 'default'

const scrollToBottom = async () => {
  await nextTick()
  const el = msgContainerRef.value
  if (el) el.scrollTop = el.scrollHeight
}

onMounted(async () => {
  historyLoading.value = true
  try {
    const res = await getChatHistory()
    const list = res?.data || res
    if (list && Array.isArray(list) && list.length > 0) {
      messages.value = list.flatMap(h => [
        { role: 'user', content: h.question || '' },
        { role: 'assistant', content: h.answer || '' }
      ])
    }
  } catch {
    // 静默失败，不影响使用
  } finally {
    historyLoading.value = false
    await scrollToBottom()
  }
})

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  await scrollToBottom()

  sending.value = true
  try {
    const res = await chat(text, sessionId)
    const reply = (typeof res.data === 'string' ? res.data : (res.data?.reply || res.data?.content)) || '（未获取到回复）'
    messages.value.push({ role: 'assistant', content: reply })
  } catch {
    ElMessage.error('请求失败，请稍后重试')
    messages.value.push({ role: 'assistant', content: '抱歉，请求出现了问题，请稍后重试。' })
  } finally {
    sending.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
.ai-chat-page {
  padding: 20px;
  height: calc(100vh - 100px);
  max-width: 900px;
  margin: 0 auto;
}

.chat-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
  overflow: hidden;
}

.chat-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
}

.chat-header h2 {
  margin: 0;
  font-size: 18px;
}

.header-sub {
  color: #909399;
  font-size: 13px;
}

.message-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.empty-hint {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.message-row {
  margin-bottom: 16px;
}

.message-bubble {
  display: flex;
  gap: 10px;
  max-width: 80%;
}

.row-user .message-bubble {
  margin-left: auto;
  flex-direction: row-reverse;
}

.bubble-content {
  padding: 10px 16px;
  border-radius: 12px;
  line-height: 1.6;
}

.message-bubble.user .bubble-content {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.message-bubble.assistant .bubble-content {
  background: #fff;
  color: #303133;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.bubble-role {
  font-size: 12px;
  margin-bottom: 4px;
  opacity: 0.75;
}

.bubble-text {
  font-size: 14px;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 打字动画 */
.typing .dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #909399;
  margin-right: 4px;
  animation: blink 1.4s infinite both;
}

.typing .dot:nth-child(2) { animation-delay: 0.2s; }
.typing .dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0%, 80%, 100% { opacity: 0.3; }
  40% { opacity: 1; }
}

.input-area {
  display: flex;
  gap: 12px;
  align-items: flex-end;
  padding: 16px 20px;
  border-top: 1px solid #ebeef5;
  background: #fff;
}

.input-area :deep(.el-textarea__inner) {
  border-radius: 8px;
}

.send-btn {
  flex-shrink: 0;
  height: 40px;
  border-radius: 8px;
}
</style>
