<template>
  <div class="applications-page">
    <div class="page-header">
      <h2>学生申请</h2>
      <el-button type="primary" @click="openCreateDialog">新建申请</el-button>
    </div>

    <el-card shadow="never">
      <el-tabs v-model="activeTab" @tab-change="loadData">
        <el-tab-pane label="奖学金" name="奖学金" />
        <el-tab-pane label="助学金" name="助学金" />
        <el-tab-pane label="贷款" name="贷款" />
        <el-tab-pane label="困难生" name="困难生" />
      </el-tabs>

      <el-table :data="applications" stripe v-loading="loading" empty-text="暂无申请记录">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="130" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="appStatusTag(row.status)" size="small">{{ appStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="申请时间" width="170" />
        <el-table-column prop="reviewComment" label="审核意见" min-width="140">
          <template #default="{ row }">
            {{ row.reviewComment || '-' }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新建申请弹窗 -->
    <el-dialog v-model="dialogVisible" title="新建申请" width="500px" :close-on-click-modal="false">
      <el-form :model="form" label-width="80px">
        <el-form-item label="申请类型">
          <el-select v-model="form.type" placeholder="请选择" style="width: 100%">
            <el-option label="奖学金" value="奖学金" />
            <el-option label="助学金" value="助学金" />
            <el-option label="贷款" value="贷款" />
            <el-option label="困难生" value="困难生" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入申请标题" />
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="form.semester" placeholder="请选择" style="width: 100%">
            <el-option label="2025-2026-2" value="2025-2026-2" />
            <el-option label="2025-2026-1" value="2025-2026-1" />
            <el-option label="2024-2025-2" value="2024-2025-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="申请内容">
          <el-input v-model="form.content" type="textarea" :rows="5" placeholder="请输入申请内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyApplications, submitApplication } from '@/api/student'

const activeTab = ref('奖学金')
const applications = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ type: '奖学金', title: '', content: '', semester: '2025-2026-2' })

const appStatusTag = (s) => {
  if (s === 'APPROVED') return 'success'
  if (s === 'REJECTED') return 'danger'
  return 'warning'
}
const appStatusLabel = (s) => {
  if (s === 'APPROVED') return '已通过'
  if (s === 'REJECTED') return '已拒绝'
  if (s === 'PENDING') return '审核中'
  return s
}

const openCreateDialog = () => {
  form.type = activeTab.value
  form.title = ''
  form.content = ''
  form.semester = '2025-2026-2'
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入申请内容'); return }
  submitting.value = true
  try {
    const res = await submitApplication({ ...form })
    if (res.code === 200) {
      ElMessage.success('申请提交成功')
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listMyApplications(activeTab.value)
    if (res.code === 200) {
      applications.value = res.data || []
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
.applications-page { max-width: 1000px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
