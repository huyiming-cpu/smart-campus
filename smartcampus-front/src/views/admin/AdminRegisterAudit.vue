<template>
  <el-card>
    <template #header><span>注册审核</span></template>
    <el-table :data="list" v-loading="loading" stripe border @row-click="showDetail">
      <el-table-column prop="identityNumber" label="身份证号" width="180" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="userType" label="用户类型" width="100" />
      <el-table-column prop="department" label="部门/学院" width="140" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="applyTime" label="申请时间" width="170" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'PENDING' ? 'warning' : row.status === 'APPROVED' ? 'success' : 'danger'">
            {{ row.status === 'PENDING' ? '待审核' : row.status === 'APPROVED' ? '已通过' : '已驳回' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog title="审核详情" v-model="detailVisible" width="600px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
      <el-descriptions-item label="身份证号">{{ detail.identityNumber }}</el-descriptions-item>
      <el-descriptions-item label="用户类型">{{ detail.userType }}</el-descriptions-item>
      <el-descriptions-item label="部门/学院">{{ detail.department }}</el-descriptions-item>
      <el-descriptions-item label="手机号">{{ detail.phone }}</el-descriptions-item>
      <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
      <el-descriptions-item label="申请时间">{{ detail.applyTime }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="detail.status === 'PENDING' ? 'warning' : detail.status === 'APPROVED' ? 'success' : 'danger'">
          {{ detail.status === 'PENDING' ? '待审核' : detail.status === 'APPROVED' ? '已通过' : '已驳回' }}
        </el-tag>
      </el-descriptions-item>
    </el-descriptions>
    <el-form v-if="detail.status === 'PENDING'" label-width="80px" style="margin-top:20px">
      <el-form-item label="审核意见"><el-input v-model="auditReason" type="textarea" :rows="3" placeholder="审核意见（驳回时必填）" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
      <el-button v-if="detail.status === 'PENDING'" type="success" @click="audit('APPROVED')">通过</el-button>
      <el-button v-if="detail.status === 'PENDING'" type="danger" @click="audit('REJECTED')">驳回</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listRegisterApplications, auditRegistration } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const detailVisible = ref(false)
const detail = ref({})
const auditReason = ref('')

const loadData = async () => {
  loading.value = true
  try { const res = await listRegisterApplications(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const showDetail = (row) => {
  detail.value = { ...row }
  auditReason.value = ''
  detailVisible.value = true
}

const audit = async (status) => {
  if (status === 'REJECTED' && !auditReason.value) { ElMessage.warning('驳回时请填写审核意见'); return }
  try {
    await auditRegistration(detail.value.id, { status, reason: auditReason.value })
    ElMessage.success(status === 'APPROVED' ? '已通过' : '已驳回')
    detailVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
}

onMounted(() => loadData())
</script>
