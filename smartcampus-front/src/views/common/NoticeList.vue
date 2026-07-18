<template>
  <div class="notice-page">
    <el-card shadow="never">
      <template #header>
        <h2 style="margin:0">公告通知</h2>
      </template>

      <div class="notice-layout" v-loading="loading">
        <!-- 左侧列表 -->
        <div class="notice-list-panel">
          <el-table
            :data="notices"
            style="width: 100%"
            highlight-current-row
            @row-click="handleRowClick"
            :row-class-name="tableRowClass"
          >
            <el-table-column label="标题" min-width="180">
              <template #default="{ row }">
                <div class="title-cell">
                  <el-tag v-if="row.isTop" size="small" type="danger" effect="dark">置顶</el-tag>
                  <el-tag v-if="row.isUrgent" size="small" type="warning" effect="dark">紧急</el-tag>
                  <span class="title-text">{{ row.title }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="publishTime" label="发布时间" width="160" />
            <el-table-column prop="viewCount" label="浏览量" width="80" align="center" />
          </el-table>
          <div v-if="notices.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无公告" />
          </div>
        </div>

        <!-- 右侧详情 -->
        <div class="notice-detail-panel">
          <template v-if="detailLoading">
            <el-skeleton :rows="8" animated />
          </template>
          <template v-else-if="selectedNotice">
            <div class="detail-header">
              <h3>{{ selectedNotice.title }}</h3>
              <div class="detail-meta">
                <span>发布者：{{ selectedNotice.publisher || '-' }}</span>
                <span>发布时间：{{ selectedNotice.publishTime || '-' }}</span>
                <span>浏览量：{{ selectedNotice.viewCount || 0 }}</span>
              </div>
            </div>
            <el-divider />
            <div class="detail-content" v-html="selectedNotice.content || '暂无内容'"></div>
          </template>
          <template v-else>
            <el-empty description="请选择一条公告查看详情" />
          </template>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listAnnouncements, getAnnouncement } from '@/api/notice'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const detailLoading = ref(false)
const notices = ref([])
const selectedNotice = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const res = await listAnnouncements()
    const list = res.data || []
    // 置顶优先、紧急其次、时间倒序
    list.sort((a, b) => {
      if (a.isTop !== b.isTop) return b.isTop ? 1 : -1
      if (a.isUrgent !== b.isUrgent) return b.isUrgent ? 1 : -1
      return (b.publishTime || '').localeCompare(a.publishTime || '')
    })
    notices.value = list
  } catch {
    ElMessage.error('加载公告列表失败')
  } finally {
    loading.value = false
  }
}

const handleRowClick = async (row) => {
  detailLoading.value = true
  try {
    const res = await getAnnouncement(row.id)
    selectedNotice.value = res.data
  } catch {
    ElMessage.error('加载公告详情失败')
  } finally {
    detailLoading.value = false
  }
}

const tableRowClass = ({ row }) => {
  if (row.isTop && row.isUrgent) return 'row-top-urgent'
  if (row.isTop) return 'row-top'
  if (row.isUrgent) return 'row-urgent'
  return ''
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.notice-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.notice-layout {
  display: flex;
  gap: 20px;
  min-height: 500px;
}

.notice-list-panel {
  flex: 1;
  min-width: 0;
}

.notice-detail-panel {
  flex: 1;
  min-width: 0;
  border-left: 1px solid #ebeef5;
  padding-left: 20px;
}

.title-cell {
  display: flex;
  align-items: center;
  gap: 6px;
}

.title-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-header h3 {
  margin: 0 0 12px 0;
  font-size: 20px;
  color: #303133;
}

.detail-meta {
  display: flex;
  gap: 20px;
  font-size: 13px;
  color: #909399;
}

.detail-content {
  font-size: 14px;
  line-height: 1.8;
  color: #303133;
}

.detail-content :deep(img) {
  max-width: 100%;
}

.empty-state {
  padding-top: 40px;
}

/* 高亮行 */
:deep(.row-top-urgent) {
  background-color: #fef0f0 !important;
}

:deep(.row-top) {
  background-color: #fdf6ec !important;
}

:deep(.row-urgent) {
  background-color: #fde2e2 !important;
}
</style>
