<template>
  <div v-if="visible && suggestions.length > 0" class="suggest-dropdown" @mousedown.prevent>
    <div
      v-for="(item, index) in suggestions"
      :key="index"
      class="suggest-item"
      :class="{ active: index === activeIndex }"
      @click="$emit('navigate', item.url, item.title)"
      @mouseenter="activeIndex = index"
    >
      <div class="suggest-icon">
        <el-icon v-if="item.icon === 'scores'" :size="18"><Trophy /></el-icon>
        <el-icon v-else-if="item.icon === 'calendar'" :size="18"><Calendar /></el-icon>
        <el-icon v-else-if="item.icon === 'exam'" :size="18"><Timer /></el-icon>
        <el-icon v-else-if="item.icon === 'wallet'" :size="18"><Wallet /></el-icon>
        <el-icon v-else-if="item.icon === 'profile'" :size="18"><User /></el-icon>
        <el-icon v-else-if="item.icon === 'notice'" :size="18"><Bell /></el-icon>
        <el-icon v-else-if="item.icon === 'news'" :size="18"><Notebook /></el-icon>
        <el-icon v-else-if="item.icon === 'lab'" :size="18"><School /></el-icon>
        <el-icon v-else-if="item.icon === 'graduation'" :size="18"><DocumentChecked /></el-icon>
        <el-icon v-else-if="item.icon === 'evaluation'" :size="18"><Star /></el-icon>
        <el-icon v-else-if="item.icon === 'competition'" :size="18"><Medal /></el-icon>
        <el-icon v-else-if="item.icon === 'consultation'" :size="18"><ChatDotRound /></el-icon>
        <el-icon v-else-if="item.icon === 'research'" :size="18"><Reading /></el-icon>
        <el-icon v-else-if="item.icon === 'plan'" :size="18"><Edit /></el-icon>
        <el-icon v-else-if="item.icon === 'directory'" :size="18"><Phone /></el-icon>
        <el-icon v-else-if="item.icon === 'document'" :size="18"><Document /></el-icon>
        <el-icon v-else :size="18"><Search /></el-icon>
      </div>
      <div class="suggest-body">
        <span class="suggest-title">{{ item.title }}</span>
        <span class="suggest-desc">{{ item.description }}</span>
      </div>
      <span class="suggest-tag" :class="item.type === 'FUNCTION' ? 'tag-func' : 'tag-content'">
        {{ item.type === 'FUNCTION' ? '功能' : '内容' }}
      </span>
    </div>
  </div>
  <div v-if="visible && loading" class="suggest-dropdown">
    <div class="suggest-loading">搜索中...</div>
  </div>
  <div v-if="visible && !loading && keyword.length >= 2 && suggestions.length === 0 && searched" class="suggest-dropdown">
    <div class="suggest-empty">未找到相关结果，按回车搜索公告</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { getSearchSuggestions } from '@/api/search'
import {
  Search, Trophy, Calendar, Timer, Wallet, User, Bell, Notebook,
  School, DocumentChecked, Star, Medal, ChatDotRound, Reading,
  Edit, Phone, Document
} from '@element-plus/icons-vue'

const props = defineProps({
  keyword: { type: String, default: '' },
  role: { type: String, default: 'STUDENT' },
  visible: { type: Boolean, default: false }
})

defineEmits(['navigate', 'close'])

const suggestions = ref([])
const loading = ref(false)
const searched = ref(false)
const activeIndex = ref(-1)
let debounceTimer = null

const fetchSuggestions = async () => {
  if (!props.keyword || props.keyword.trim().length < 2) {
    suggestions.value = []
    searched.value = false
    return
  }
  loading.value = true
  searched.value = false
  try {
    const res = await getSearchSuggestions(props.keyword.trim(), props.role)
    suggestions.value = res.data || []
    searched.value = true
    activeIndex.value = -1
  } catch {
    suggestions.value = []
    searched.value = true
  } finally {
    loading.value = false
  }
}

watch(() => props.keyword, () => {
  if (debounceTimer) clearTimeout(debounceTimer)
  debounceTimer = setTimeout(fetchSuggestions, 300)
})

watch(() => props.visible, (v) => {
  if (!v) { suggestions.value = []; activeIndex.value = -1; searched.value = false }
})

// Click outside to close
const handleClickOutside = (e) => {
  if (!e.target.closest('.suggest-dropdown') && !e.target.closest('.hero-search')) {
    // parent handles closing
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
  if (debounceTimer) clearTimeout(debounceTimer)
})
</script>

<style scoped>
.suggest-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  margin-top: 6px;
  background: #fff;
  border: 1px solid #EEF0F4;
  border-radius: 12px;
  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.08);
  z-index: 200;
  overflow: hidden;
  max-height: 360px;
  overflow-y: auto;
}
.suggest-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: background 0.15s;
}
.suggest-item:hover,
.suggest-item.active {
  background: #F5F9FD;
}
.suggest-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #EBF3FC;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #5B9BD5;
  flex-shrink: 0;
}
.suggest-body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.suggest-title {
  font-size: 14px;
  font-weight: 500;
  color: #1A1A2E;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.suggest-desc {
  font-size: 12px;
  color: #9CA3AF;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.suggest-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  flex-shrink: 0;
}
.tag-func {
  background: #EBF3FC;
  color: #5B9BD5;
}
.tag-content {
  background: #F3F4F6;
  color: #6B7280;
}
.suggest-loading,
.suggest-empty {
  padding: 16px;
  text-align: center;
  font-size: 13px;
  color: #9CA3AF;
}
</style>
