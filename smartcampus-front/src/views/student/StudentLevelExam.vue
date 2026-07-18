<template>
  <div class="level-exam-page">
    <div class="page-header">
      <h2>等级考试</h2>
    </div>

    <el-card shadow="never">
      <template #header><strong>可报名的考试</strong></template>
      <el-table :data="availableExams" stripe v-loading="examLoading" empty-text="暂无可报名的考试">
        <el-table-column prop="examName" label="考试名称" min-width="140" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="fee" label="报名费" width="100">
          <template #default="{ row }">
            &yen; {{ row.fee?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="registerStart" label="报名开始" width="120" />
        <el-table-column prop="registerEnd" label="报名截止" width="120" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button
              v-if="!row.registered"
              type="primary" size="small"
              @click="handleRegister(row)"
              :loading="row.registering"
            >
              报名
            </el-button>
            <el-tag v-else type="success" size="small">已报名</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never" style="margin-top: 20px">
      <template #header><strong>我的报名记录</strong></template>
      <el-table :data="myRegistrations" stripe v-loading="regLoading" empty-text="暂无报名记录">
        <el-table-column prop="examName" label="考试名称" min-width="140" />
        <el-table-column prop="examDate" label="考试日期" width="120" />
        <el-table-column prop="fee" label="报名费" width="100">
          <template #default="{ row }">
            &yen; {{ row.fee?.toFixed(2) || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="报名时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'CONFIRMED' ? 'success' : 'info'" size="small">
              {{ row.status === 'CONFIRMED' ? '已确认' : row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listLevelExams, registerLevelExam, getMyExamRegistrations } from '@/api/exam'

const availableExams = ref([])
const myRegistrations = ref([])
const examLoading = ref(false)
const regLoading = ref(false)

const handleRegister = async (row) => {
  try {
    await ElMessageBox.confirm(`确认报名「${row.examName}」？报名费 ¥${row.fee?.toFixed(2) || '0.00'}`, '确认报名', {
      confirmButtonText: '确认报名',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }
  row.registering = true
  try {
    const res = await registerLevelExam(row.id || row.examId)
    if (res.code === 200) {
      ElMessage.success('报名成功')
      await loadAll()
    } else {
      ElMessage.error(res.message || '报名失败')
    }
  } catch {
    ElMessage.error('报名失败')
  } finally {
    row.registering = false
  }
}

const loadAll = async () => {
  examLoading.value = true
  regLoading.value = true
  try {
    const [examRes, regRes] = await Promise.all([
      listLevelExams(),
      getMyExamRegistrations()
    ])
    if (examRes.code === 200) {
      const regIds = new Set(((regRes.code === 200 ? regRes.data : []) || []).map(r => r.examId || r.id))
      availableExams.value = (examRes.data || []).map(e => ({
        ...e,
        registered: regIds.has(e.id || e.examId),
        registering: false
      }))
    }
    if (regRes.code === 200) {
      myRegistrations.value = regRes.data || []
    }
  } catch {
    ElMessage.error('加载失败')
  } finally {
    examLoading.value = false
    regLoading.value = false
  }
}

onMounted(() => loadAll())
</script>

<style scoped>
.level-exam-page { max-width: 1000px; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
