<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Wallet /></el-icon></div>
      <div><h2 class="page-title">缴费管理</h2><p class="page-desc">管理学生缴费记录，支持新增、编辑与删除</p></div>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="openAdd"><el-icon style="margin-right:4px"><Plus /></el-icon>新增缴费记录</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border>
        <el-table-column prop="studentName" label="学生" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="type" label="缴费类型" width="120" />
        <el-table-column prop="totalAmount" label="应缴(元)" width="110">
          <template #default="{ row }">{{ (row.totalAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴(元)" width="110">
          <template #default="{ row }">{{ (row.paidAmount || 0).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="unpaidAmount" label="未缴(元)" width="110">
          <template #default="{ row }">{{ ((row.totalAmount || 0) - (row.paidAmount || 0)).toFixed(2) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PAID' ? 'success' : row.status === 'PARTIAL' ? 'warning' : 'danger'">
              {{ row.status === 'PAID' ? '已缴清' : row.status === 'PARTIAL' ? '部分缴纳' : '未缴' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentTime" label="缴费时间" width="170" />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="isEdit ? '编辑缴费' : '新增缴费记录'" v-model="dialogVisible" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="学生"><el-input v-model="form.studentName" /></el-form-item>
        <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如：2025-2026-1" /></el-form-item>
        <el-form-item label="缴费类型"><el-input v-model="form.type" placeholder="如：学费、住宿费" /></el-form-item>
        <el-form-item label="应缴金额"><el-input-number v-model="form.totalAmount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="已缴金额"><el-input-number v-model="form.paidAmount" :min="0" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="未缴" value="UNPAID" /><el-option label="部分缴纳" value="PARTIAL" /><el-option label="已缴清" value="PAID" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listAllPayments, savePayment, deletePayment } from '@/api/card'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Wallet, Plus } from '@element-plus/icons-vue'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const editId = ref(null)

const loadData = async () => {
  loading.value = true
  try { const res = await listAllPayments(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; form.value = { studentName:'', semester:'', type:'', totalAmount:0, paidAmount:0, status:'UNPAID' }; editId.value = null; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; editId.value = row.id; dialogVisible.value = true }

const handleSave = async () => {
  try { await savePayment(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  catch { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除？', '提示', { confirmButtonText:'确定', cancelButtonText:'取消', type:'warning' })
    await deletePayment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
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
