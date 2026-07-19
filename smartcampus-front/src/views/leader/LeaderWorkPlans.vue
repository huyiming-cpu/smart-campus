<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#6366F1,#4F46E5);">
        <el-icon :size="20" color="#fff"><Calendar /></el-icon>
      </div>
      <div>
        <h2 class="page-title">工作计划</h2>
        <p class="page-desc">制定与跟踪学校工作计划及任务安排</p>
      </div>
    </div>

    <div class="toolbar">
      <el-button type="primary" @click="openAdd"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>新增计划</el-button>
    </div>

    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border>
        <el-table-column prop="planDate" label="日期" width="120" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column label="类型" width="80">
          <template #default="{row}"><el-tag size="small" type="info">{{planTypeMap[row.planType]||row.planType}}</el-tag></template>
        </el-table-column>
        <el-table-column label="优先级" width="80">
          <template #default="{row}">
            <el-tag :type="row.priority==='HIGH'?'danger':row.priority==='MEDIUM'?'warning':'info'" size="small">{{priorityMap[row.priority]||row.priority}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{row}">
            <el-tag :type="row.status==='COMPLETED'?'success':row.status==='IN_PROGRESS'?'warning':'info'" size="small">{{statusMap[row.status]||row.status}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" plain @click="openEdit(row)"><el-icon :size="14"><Edit /></el-icon></el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(row)"><el-icon :size="14"><Delete /></el-icon></el-button>
            <el-button v-if="row.status!=='COMPLETED'" size="small" type="success" @click="markDone(row)"><el-icon :size="14" style="margin-right:3px"><Check /></el-icon>完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="isEdit?'编辑计划':'新增计划'" v-model="dialogVisible" width="550px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title"/></el-form-item>
        <el-form-item label="具体内容"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="请填写工作计划的具体内容..."/></el-form-item>
        <el-form-item label="计划日期"><el-date-picker v-model="form.planDate" type="date" value-format="YYYY-MM-DD" style="width:100%"/></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.planType" style="width:100%">
            <el-option label="教学" value="TEACHING"/><el-option label="科研" value="RESEARCH"/>
            <el-option label="行政" value="ADMIN"/><el-option label="个人" value="PERSONAL"/>
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="form.priority" style="width:100%">
            <el-option label="高" value="HIGH"/><el-option label="中" value="MEDIUM"/><el-option label="低" value="LOW"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="待开始" value="PENDING"/><el-option label="进行中" value="IN_PROGRESS"/><el-option label="已完成" value="COMPLETED"/>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Calendar, Plus, Edit, Check, Delete } from '@element-plus/icons-vue'
import { listWorkPlans, createWorkPlan, updateWorkPlan, deleteWorkPlan } from '@/api/document'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), dialogVisible = ref(false), isEdit = ref(false)
const form = ref({}), editId = ref(null)

const planTypeMap = { TEACHING:'教学', RESEARCH:'科研', ADMIN:'行政', PERSONAL:'个人' }
const priorityMap = { HIGH:'高', MEDIUM:'中', LOW:'低' }
const statusMap = { PENDING:'待开始', IN_PROGRESS:'进行中', COMPLETED:'已完成' }

const loadData = async () => {
  loading.value = true
  try { const res = await listWorkPlans(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false; editId.value = null
  form.value = { title:'', content:'', planDate:new Date().toISOString().slice(0,10), planType:'TEACHING', priority:'MEDIUM', status:'PENDING', department:'', userName:'' }
  dialogVisible.value = true
}

const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; editId.value = row.id; dialogVisible.value = true }

const handleSave = async () => {
  try {
    if (isEdit.value) { await updateWorkPlan(editId.value, form.value); ElMessage.success('更新成功') }
    else { await createWorkPlan(form.value); ElMessage.success('创建成功') }
    dialogVisible.value = false; loadData()
  } catch { ElMessage.error('保存失败') }
}

const markDone = async (row) => {
  try { await updateWorkPlan(row.id, {...row, status:'COMPLETED'}); ElMessage.success('已标记完成'); loadData() }
  catch { ElMessage.error('操作失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该计划？', '提示', { type:'warning' })
    await deleteWorkPlan(row.id); ElMessage.success('删除成功'); loadData()
  } catch {}
}

onMounted(() => loadData())
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{display:flex;align-items:center;gap:12px;margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
