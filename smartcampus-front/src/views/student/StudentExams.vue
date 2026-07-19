<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Clock /></el-icon></div>
      <div><h2 class="page-title">我的考试</h2><p class="page-desc">查看本学期考试安排详情</p></div>
    </div>
    <div class="toolbar">
      <el-select v-model="semester" @change="loadData" style="width: 200px">
        <el-option label="2025-2026-2" value="2025-2026-2" />
        <el-option label="2025-2026-1" value="2025-2026-1" />
        <el-option label="2024-2025-2" value="2024-2025-2" />
      </el-select>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="exams" stripe empty-text="暂无考试安排" :row-class-name="tableRowClassName">
        <el-table-column prop="examName" label="考试名称" min-width="160" />
        <el-table-column prop="courseName" label="考试课程" min-width="160" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="examTime" label="考试时间" width="140" />
        <el-table-column prop="location" label="考场" width="140" />
        <el-table-column prop="seatNumber" label="座位号" width="80" />
        <el-table-column prop="supervisor" label="监考教师" width="120" />
        <el-table-column prop="status" label="考试状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'UPCOMING' ? 'warning' : 'info'" size="small">
              {{ row.status === 'UPCOMING' ? '即将开始' : row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getExamArrangements } from '@/api/exam'
import { Clock } from '@element-plus/icons-vue'

const semester = ref('2025-2026-2')
const exams = ref([])
const loading = ref(false)

const tableRowClassName = ({ row }) => {
  return row.status === 'UPCOMING' ? 'upcoming-row' : ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getExamArrangements(semester.value)
    if (res.code === 200) {
      exams.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载考试安排失败')
    }
  } catch {
    ElMessage.error('加载考试安排失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
:deep(.upcoming-row){background-color:#fdf6ec!important}
</style>
