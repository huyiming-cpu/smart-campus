<template>
  <div class="exams-page">
    <div class="page-header">
      <h2>考试安排</h2>
      <el-select v-model="semester" @change="loadData" style="width: 200px">
        <el-option label="2025-2026-2" value="2025-2026-2" />
        <el-option label="2025-2026-1" value="2025-2026-1" />
        <el-option label="2024-2025-2" value="2024-2025-2" />
      </el-select>
    </div>

    <el-card shadow="never" v-loading="loading">
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getExamArrangements } from '@/api/exam'

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
.exams-page { max-width: 1100px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
:deep(.upcoming-row) { background-color: #fdf6ec !important; }
</style>
