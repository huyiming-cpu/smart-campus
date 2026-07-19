<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><EditPen /></el-icon></div>
      <div><h2 class="page-title">补考重修</h2><p class="page-desc">查看未通过课程，申请补考或重修</p></div>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="retakes" stripe empty-text="暂无未通过课程">
        <el-table-column prop="courseName" label="课程名称" min-width="160" />
        <el-table-column prop="originalScore" label="原成绩" width="100">
          <template #default="{ row }">
            <span class="fail-score">{{ row.originalScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === '重考' ? 'warning' : 'info'" size="small">
              {{ row.type || '补考' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="申请状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button
              v-if="!row.status"
              type="primary" size="small"
              @click="handleApply(row)"
            >
              申请补考
            </el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" title="确认申请" width="400px">
      <p>确认申请课程 <strong>{{ currentItem?.courseName }}</strong> 的补考/重修？</p>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmApply" :loading="confirming">确认申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyRetakes, applyRetake } from '@/api/course'
import { EditPen } from '@element-plus/icons-vue'

const loading = ref(false)
const retakes = ref([])
const dialogVisible = ref(false)
const currentItem = ref(null)
const confirming = ref(false)

const statusTag = (s) => {
  if (s === 'APPROVED') return 'success'
  if (s === 'REJECTED') return 'danger'
  return 'warning'
}
const statusLabel = (s) => {
  if (s === 'APPROVED') return '已通过'
  if (s === 'REJECTED') return '已拒绝'
  if (s === 'PENDING') return '审核中'
  return '待申请'
}

const handleApply = (row) => {
  currentItem.value = row
  dialogVisible.value = true
}

const confirmApply = async () => {
  confirming.value = true
  try {
    const item = currentItem.value
    const res = await applyRetake({ courseId: item.courseId||item.id, semester: item.semester||'2025-2026-2', type: item.type||'MAKEUP', originalScore: item.originalScore||0 })
    if (res.code === 200) {
      ElMessage.success('申请提交成功')
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.message || '申请失败')
    }
  } catch {
    ElMessage.error('申请失败')
  } finally {
    confirming.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyRetakes()
    if (res.code === 200) {
      retakes.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载失败')
    }
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page{padding:20px 24px;max-width:900px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.fail-score{color:#f56c6c;font-weight:bold}
</style>
