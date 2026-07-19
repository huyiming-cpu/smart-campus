<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Checked /></el-icon></div>
      <div><h2 class="page-title">注册审核</h2><p class="page-desc">审核用户注册申请，支持审批通过与驳回</p></div>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border @row-click="showDetail" style="cursor:pointer" empty-text="暂无待审核注册申请">
        <el-table-column prop="identityNumber" label="身份证号" width="180" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="userType" label="用户类型" width="110">
          <template #default="{row}"><el-tag size="small" type="info">{{ row.userType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="department" label="部门/学院" min-width="140" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="applyTime" label="申请时间" width="180" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status==='PENDING'?'warning':row.status==='APPROVED'?'success':'danger'" size="small">
              {{ row.status==='PENDING'?'待审核':row.status==='APPROVED'?'已通过':'已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

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
          <el-tag :type="detail.status==='PENDING'?'warning':detail.status==='APPROVED'?'success':'danger'" size="small">
            {{ detail.status==='PENDING'?'待审核':detail.status==='APPROVED'?'已通过':'已驳回' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <el-form v-if="detail.status==='PENDING'" label-width="80px" style="margin-top:20px">
        <el-form-item label="审核意见"><el-input v-model="auditReason" type="textarea" :rows="3" placeholder="驳回时请填写审核意见"/></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="detailVisible=false">关闭</el-button>
        <el-button v-if="detail.status==='PENDING'" type="success" @click="audit('APPROVED')"><el-icon :size="14" style="margin-right:4px"><Check /></el-icon>通过</el-button>
        <el-button v-if="detail.status==='PENDING'" type="danger" @click="audit('REJECTED')"><el-icon :size="14" style="margin-right:4px"><Close /></el-icon>驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listRegisterApplications, auditRegistration } from '@/api/admin'
import { ElMessage } from 'element-plus'
import { Checked, Check, Close } from '@element-plus/icons-vue'

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
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;margin:0;line-height:1.3;color:#1a1a2e}
.page-desc{font-size:13px;color:#8c8c8c;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
