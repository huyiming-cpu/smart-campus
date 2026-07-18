<template>
  <div class="news-page">
    <el-card shadow="never">
      <template #header>
        <div class="news-header">
          <h2 style="margin:0">校园新闻</h2>
          <el-select
            v-model="category"
            placeholder="新闻分类"
            clearable
            style="width: 160px"
            @change="handleCategoryChange"
          >
            <el-option label="全部" value="" />
            <el-option label="学校要闻" value="学校要闻" />
            <el-option label="就业创业" value="就业创业" />
            <el-option label="校园生活" value="校园生活" />
          </el-select>
        </div>
      </template>

      <!-- 列表视图 -->
      <div v-loading="loading">
        <template v-if="newsList.length > 0">
          <div class="news-grid">
            <div
              v-for="item in newsList"
              :key="item.id"
              class="news-card"
              @click="handleOpenDetail(item)"
            >
              <div class="news-cover">
                <img v-if="item.coverImage" :src="'/uploads/news/' + item.coverImage" alt="封面" />
                <div v-else class="cover-placeholder">
                  <el-icon :size="40"><PictureFilled /></el-icon>
                  <span>暂无封面</span>
                </div>
              </div>
              <div class="news-card-body">
                <h4 class="news-title">{{ item.title }}</h4>
                <p class="news-summary">{{ item.summary || '暂无摘要' }}</p>
                <div class="news-card-footer">
                  <span class="news-time">{{ item.publishTime || '-' }}</span>
                  <span class="news-views">浏览 {{ item.viewCount || 0 }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="pagination-wrapper" v-if="total > pageSize">
            <el-pagination
              v-model:current-page="currentPage"
              :page-size="pageSize"
              :total="total"
              layout="total, prev, pager, next"
              @current-change="fetchList"
            />
          </div>
        </template>
        <el-empty v-else-if="!loading" description="暂无新闻" />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="detail?.title || '新闻详情'"
      width="800px"
      top="5vh"
      destroy-on-close
    >
      <template v-if="detailLoading">
        <el-skeleton :rows="10" animated />
      </template>
      <template v-else-if="detail">
        <img v-if="detail.coverImage" :src="'/uploads/news/' + detail.coverImage" style="width:100%;max-height:300px;object-fit:cover;border-radius:8px;margin-bottom:16px" alt="封面"/>
        <div class="detail-meta-bar">
          <span>来源：{{ detail.source || '-' }}</span>
          <span>发布者：{{ detail.publisher || '-' }}</span>
          <span>发布时间：{{ detail.publishTime || '-' }}</span>
          <span>浏览量：{{ detail.viewCount || 0 }}</span>
          <span>点赞：{{ detail.likeCount || 0 }}</span>
        </div>
        <el-divider />
        <div class="detail-content" v-html="detail.content || '暂无内容'"></div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listNews, getNews } from '@/api/notice'
import { ElMessage } from 'element-plus'
import { PictureFilled } from '@element-plus/icons-vue'

const category = ref('')
const loading = ref(false)
const newsList = ref([])
const currentPage = ref(1)
const pageSize = ref(12)
const total = ref(0)

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const fetchList = async () => {
  loading.value = true
  try {
    const params = currentPage.value > 1 ? { page: currentPage.value, size: pageSize.value } : undefined
    const res = await listNews(category.value || undefined)
    const data = res.data
    if (Array.isArray(data)) {
      // 简单数组模式
      newsList.value = data
      total.value = data.length
    } else if (data && data.records) {
      // 分页模式
      newsList.value = data.records
      total.value = data.total || 0
    } else {
      newsList.value = []
      total.value = 0
    }
  } catch {
    ElMessage.error('加载新闻列表失败')
  } finally {
    loading.value = false
  }
}

const handleCategoryChange = () => {
  currentPage.value = 1
  fetchList()
}

const handleOpenDetail = async (item) => {
  detail.value = null
  detailVisible.value = true
  detailLoading.value = true
  try {
    const res = await getNews(item.id)
    detail.value = res.data
  } catch {
    ElMessage.error('加载新闻详情失败')
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.news-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.news-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.news-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.news-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;
}

.news-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.news-cover {
  width: 100%;
  height: 160px;
  overflow: hidden;
  background: #f5f7fa;
}

.news-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
  gap: 6px;
}

.news-card-body {
  padding: 14px 16px;
}

.news-title {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.news-summary {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #909399;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  line-height: 1.5;
}

.news-card-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #c0c4cc;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

/* 详情弹窗内容 */
.detail-meta-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
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
</style>
