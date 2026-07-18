<template>
  <div class="page">
    <h3>📋 个人工作计划</h3>
    <el-button type="primary" size="small" @click="openAdd" style="margin-bottom:16px">新增计划</el-button>
    <el-table :data="list" v-loading="loading" border stripe size="small">
      <el-table-column prop="title" label="标题" min-width="160"/>
      <el-table-column prop="planDate" label="计划日期" width="110"/>
      <el-table-column label="类型" width="80"><template #default="{row}"><el-tag size="small">{{planTypeMap[row.planType]||row.planType}}</el-tag></template></el-table-column>
      <el-table-column label="优先级" width="80"><template #default="{row}"><el-tag size="small" :type="row.priority==='HIGH'?'danger':row.priority==='MEDIUM'?'warning':'info'">{{priorityMap[row.priority]||row.priority}}</el-tag></template></el-table-column>
      <el-table-column label="状态" width="90"><template #default="{row}"><el-tag size="small" :type="row.status==='COMPLETED'?'success':row.status==='IN_PROGRESS'?'warning':'info'">{{statusMap[row.status]||row.status}}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{row}">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="success" v-if="row.status!=='COMPLETED'" @click="markDone(row)">完成</el-button>
          <el-button size="small" type="danger" @click="del(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dv" :title="isEdit?'编辑计划':'新增计划'" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title"/></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="3"/></el-form-item>
        <el-form-item label="计划日期"><el-date-picker v-model="form.planDate" type="date" value-format="YYYY-MM-DD" style="width:100%"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.planType" style="width:100%"><el-option label="教学" value="TEACHING"/><el-option label="科研" value="RESEARCH"/><el-option label="行政" value="ADMIN"/><el-option label="个人" value="PERSONAL"/></el-select></el-form-item>
        <el-form-item label="优先级"><el-select v-model="form.priority" style="width:100%"><el-option label="高" value="HIGH"/><el-option label="中" value="MEDIUM"/><el-option label="低" value="LOW"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([]), loading = ref(false), dv = ref(false), isEdit = ref(false), editId = ref(null)
const form = ref({ title:'', content:'', planDate:'', planType:'TEACHING', priority:'MEDIUM', department:'', userName:'' })
const planTypeMap = { TEACHING:'教学', RESEARCH:'科研', ADMIN:'行政', PERSONAL:'个人' }
const priorityMap = { HIGH:'高', MEDIUM:'中', LOW:'低' }
const statusMap = { PENDING:'待开始', IN_PROGRESS:'进行中', COMPLETED:'已完成' }

const load = async () => {
  loading.value = true
  try { const r = await request.get('/api/teacher/work-plans'); list.value = r.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openAdd = () => {
  isEdit.value = false; editId.value = null
  form.value = { title:'', content:'', planDate:new Date().toISOString().slice(0,10), planType:'TEACHING', priority:'MEDIUM', department:'', userName:'' }
  dv.value = true
}

const openEdit = (row) => {
  isEdit.value = true; editId.value = row.id; form.value = { ...row }
  dv.value = true
}

const save = async () => {
  try {
    if (isEdit.value) await request.put(`/api/teacher/work-plans/${editId.value}`, form.value)
    else await request.post('/api/teacher/work-plans', form.value)
    ElMessage.success('保存成功'); dv.value = false; load()
  } catch { ElMessage.error('保存失败') }
}

const markDone = async (row) => {
  try { await request.put(`/api/teacher/work-plans/${row.id}`, {...row, status:'COMPLETED'}); load(); ElMessage.success('已标记完成') }
  catch { ElMessage.error('操作失败') }
}

const del = async (id) => {
  try { await ElMessageBox.confirm('确定删除？','提示',{type:'warning'}); await request.delete(`/api/teacher/work-plans/${id}`); ElMessage.success('已删除'); load() }
  catch {}
}

onMounted(load)
</script>
<style scoped>.page{padding:20px;max-width:1100px}h3{margin-bottom:16px}</style>
