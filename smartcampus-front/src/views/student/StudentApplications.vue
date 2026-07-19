<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Postcard /></el-icon></div>
      <div><h2 class="page-title">校园服务申请</h2><p class="page-desc">奖学金/助学金/贷款/困难生申请管理</p></div>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="openCreateDialog">新建申请</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-tabs v-model="activeTab" @tab-change="loadData" class="custom-tabs">
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
    </div>

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
import { Postcard } from '@element-plus/icons-vue'

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
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.custom-tabs :deep(.el-tabs__header){margin-bottom:16px}
.custom-tabs :deep(.el-tabs__item){font-size:14px;font-weight:500;color:#6B7280}
.custom-tabs :deep(.el-tabs__item.is-active){color:#5B9BD5;font-weight:600}
.custom-tabs :deep(.el-tabs__active-bar){background:#5B9BD5}
</style>
