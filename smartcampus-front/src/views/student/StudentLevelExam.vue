<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Document /></el-icon></div>
      <div><h2 class="page-title">等级考试</h2><p class="page-desc">英语四六级等全国等级考试报名与管理</p></div>
    </div>

    <div class="card" style="margin-bottom:20px">
      <div class="section-title">可报名的考试</div>
      <el-table :data="availableExams" stripe v-loading="examLoading" empty-text="暂无可报名的考试" style="margin-top:12px">
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
    </div>

    <div class="card">
      <div class="section-title">我的报名记录</div>
      <el-table :data="myRegistrations" stripe v-loading="regLoading" empty-text="暂无报名记录" style="margin-top:12px">
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listLevelExams, registerLevelExam, getMyExamRegistrations } from '@/api/exam'
import { Document } from '@element-plus/icons-vue'

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
.page{padding:20px 24px;max-width:1000px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.section-title{font-weight:700;font-size:15px;color:#1A1A2E;padding-bottom:12px;border-bottom:1px solid #EEF0F4}
</style>
