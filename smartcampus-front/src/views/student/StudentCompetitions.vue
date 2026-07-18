<template>
  <div class="competitions-page">
    <div class="page-header">
      <h2>竞赛报名</h2>
      <el-button type="primary" @click="openDialog">报名参赛</el-button>
    </div>

    <el-card shadow="never" v-loading="loading">
      <el-table :data="competitions" stripe empty-text="暂无参赛记录">
        <el-table-column prop="competitionName" label="竞赛名称" min-width="160" />
        <el-table-column prop="teamName" label="团队名称" min-width="140" />
        <el-table-column prop="teammates" label="队员" min-width="160">
          <template #default="{ row }">
            {{ Array.isArray(row.teammates) ? row.teammates.join('、') : (row.teammates || '-') }}
          </template>
        </el-table-column>
        <el-table-column prop="advisor" label="指导老师" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'APPROVED' ? 'success' : 'warning'" size="small">
              {{ row.status === 'APPROVED' ? '已通过' : row.status === 'PENDING' ? '审核中' : row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="报名时间" width="180" />
      </el-table>
    </el-card>

    <!-- 报名弹窗 -->
    <el-dialog v-model="dialogVisible" title="竞赛报名" width="520px" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-form-item label="竞赛名称">
          <el-input v-model="form.competitionName" placeholder="请输入竞赛名称" />
        </el-form-item>
        <el-form-item label="团队名称">
          <el-input v-model="form.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="队员">
          <el-input v-model="form.teammates" placeholder="请输入队员姓名，用顿号分隔" />
        </el-form-item>
        <el-form-item label="指导老师">
          <el-input v-model="form.advisor" placeholder="请输入指导老师" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交报名</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyCompetitions, registerCompetition } from '@/api/student'

const competitions = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ competitionName: '', teamName: '', teammates: '', advisor: '' })

const openDialog = () => {
  form.competitionName = ''
  form.teamName = ''
  form.teammates = ''
  form.advisor = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.competitionName.trim()) { ElMessage.warning('请输入竞赛名称'); return }
  if (!form.teamName.trim()) { ElMessage.warning('请输入团队名称'); return }
  submitting.value = true
  try {
    const res = await registerCompetition({
      competitionName: form.competitionName,
      teamName: form.teamName,
      teammates: form.teammates.split('、').filter(Boolean),
      advisor: form.advisor
    })
    if (res.code === 200) {
      ElMessage.success('报名成功')
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.message || '报名失败')
    }
  } catch {
    ElMessage.error('报名失败')
  } finally {
    submitting.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listMyCompetitions()
    if (res.code === 200) {
      competitions.value = res.data || []
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
.competitions-page { max-width: 1000px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
