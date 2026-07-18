<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>工作计划</span><el-button type="primary" @click="openAdd">新增计划</el-button></div>
    </template>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="planDate" label="日期" width="120" />
      <el-table-column prop="title" label="标题" min-width="180" />
      <el-table-column label="类型" width="80">
        <template #default="{row}"><el-tag size="small">{{planTypeMap[row.planType]||row.planType}}</el-tag></template>
      </el-table-column>
      <el-table-column label="优先级" width="80">
        <template #default="{row}">
          <el-tag :type="row.priority==='HIGH'?'danger':row.priority==='MEDIUM'?'warning':'info'">{{priorityMap[row.priority]||row.priority}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="row.status==='COMPLETED'?'success':row.status==='IN_PROGRESS'?'':'info'">{{statusMap[row.status]||row.status}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="success" v-if="row.status!=='COMPLETED'" @click="markDone(row)">完成</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

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
</template>

<script setup>
import { ref, onMounted } from 'vue'
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
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
