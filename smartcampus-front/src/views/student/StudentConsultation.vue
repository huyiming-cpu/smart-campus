<template>
  <div class="consultation-page">
    <div class="page-header">
      <h2>心理咨询预约</h2>
      <el-button type="primary" @click="openDialog">预约咨询</el-button>
    </div>

    <el-card shadow="never" v-loading="loading">
      <el-table :data="consultations" stripe empty-text="暂无预约记录">
        <el-table-column prop="consultantName" label="咨询师" width="120" />
        <el-table-column prop="date" label="预约日期" width="120" />
        <el-table-column prop="timeSlot" label="时间段" width="160" />
        <el-table-column prop="topic" label="咨询主题" min-width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="预约时间" width="180" />
      </el-table>
    </el-card>

    <!-- 预约弹窗 -->
    <el-dialog v-model="dialogVisible" title="心理咨询预约" width="500px" :close-on-click-modal="false">
      <el-form :model="form" label-width="90px">
        <el-form-item label="咨询师">
          <el-input v-model="form.consultantName" placeholder="请输入咨询师姓名" />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="form.date" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="时间段">
          <el-select v-model="form.timeSlot" placeholder="请选择时间段" style="width: 100%">
            <el-option label="09:00 - 10:00" value="09:00-10:00" />
            <el-option label="10:00 - 11:00" value="10:00-11:00" />
            <el-option label="14:00 - 15:00" value="14:00-15:00" />
            <el-option label="15:00 - 16:00" value="15:00-16:00" />
            <el-option label="16:00 - 17:00" value="16:00-17:00" />
          </el-select>
        </el-form-item>
        <el-form-item label="咨询主题">
          <el-input v-model="form.topic" type="textarea" :rows="4" placeholder="请简要描述您想咨询的内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyConsultations, bookConsultation } from '@/api/student'

const consultations = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const form = reactive({ consultantName: '', date: '', timeSlot: '', topic: '' })

const statusTag = (s) => {
  if (s === 'CONFIRMED') return 'success'
  if (s === 'CANCELLED') return 'info'
  return 'warning'
}
const statusLabel = (s) => {
  if (s === 'CONFIRMED') return '已确认'
  if (s === 'CANCELLED') return '已取消'
  if (s === 'PENDING') return '待确认'
  return s
}

const openDialog = () => {
  form.consultantName = ''
  form.date = ''
  form.timeSlot = '09:00-10:00'
  form.topic = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.consultantName.trim()) { ElMessage.warning('请输入咨询师姓名'); return }
  if (!form.date) { ElMessage.warning('请选择预约日期'); return }
  if (!form.topic.trim()) { ElMessage.warning('请输入咨询主题'); return }
  submitting.value = true
  try {
    const res = await bookConsultation({ ...form })
    if (res.code === 200) {
      ElMessage.success('预约成功')
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.message || '预约失败')
    }
  } catch {
    ElMessage.error('预约失败')
  } finally {
    submitting.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listMyConsultations()
    if (res.code === 200) {
      consultations.value = res.data || []
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
.consultation-page { max-width: 900px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
