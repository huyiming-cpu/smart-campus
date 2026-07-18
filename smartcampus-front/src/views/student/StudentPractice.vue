<template>
  <div class="practice-page">
    <div class="page-header">
      <h2>实践项目</h2>
      <el-button type="primary" @click="openDialog">报名实践项目</el-button>
    </div>

    <el-card shadow="never" v-loading="loading">
      <el-table :data="practices" stripe empty-text="暂无实践项目记录">
        <el-table-column prop="projectName" label="项目名称" min-width="160" />
        <el-table-column prop="teacherName" label="负责教师" width="120" />
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="duration" label="时长" width="100" />
        <el-table-column prop="labLocation" label="实验室地点" min-width="140" />
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
    <el-dialog v-model="dialogVisible" title="报名实践项目" width="500px" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px">
        <el-form-item label="项目名称">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="负责教师">
          <el-input v-model="form.teacherName" placeholder="请输入教师姓名" />
        </el-form-item>
        <el-form-item label="日期">
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时长(小时)">
          <el-input-number v-model="form.duration" :min="1" :max="40" style="width: 100%" />
        </el-form-item>
        <el-form-item label="实验室">
          <el-input v-model="form.labLocation" placeholder="请输入实验室地点" />
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
import { listMyPractices, registerPractice } from '@/api/student'

const practices = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ projectName: '', teacherName: '', date: '', duration: 2, labLocation: '' })

const openDialog = () => {
  form.projectName = ''
  form.teacherName = ''
  form.date = ''
  form.duration = 2
  form.labLocation = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.projectName.trim()) { ElMessage.warning('请输入项目名称'); return }
  if (!form.teacherName.trim()) { ElMessage.warning('请输入教师姓名'); return }
  if (!form.date) { ElMessage.warning('请选择日期'); return }
  submitting.value = true
  try {
    const res = await registerPractice({ ...form })
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
    const res = await listMyPractices()
    if (res.code === 200) {
      practices.value = res.data || []
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
.practice-page { max-width: 1000px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
