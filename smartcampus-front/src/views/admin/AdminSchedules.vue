<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>任课分配</span><el-button type="primary" @click="openAdd">新增排课</el-button></div>
    </template>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="courseName" label="课程" width="140" />
      <el-table-column prop="teacherName" label="教师" width="100" />
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column prop="className" label="班级" width="120" />
      <el-table-column prop="weekday" label="星期" width="80" />
      <el-table-column prop="sections" label="节次" width="100" />
      <el-table-column prop="classroom" label="教室" width="120" />
      <el-table-column prop="maxStudents" label="容量" width="80" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog :title="isEdit ? '编辑排课' : '新增排课'" v-model="dialogVisible" width="600px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="课程"><el-input v-model="form.courseName" /></el-form-item>
      <el-form-item label="教师"><el-input v-model="form.teacherName" /></el-form-item>
      <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如：2025-2026-1" /></el-form-item>
      <el-form-item label="班级"><el-input v-model="form.className" /></el-form-item>
      <el-row :gutter="16">
        <el-col :span="8"><el-form-item label="星期"><el-input-number v-model="form.weekday" :min="1" :max="7" style="width:100%" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="节次"><el-input v-model="form.sections" placeholder="如：1-2" /></el-form-item></el-col>
        <el-col :span="8"><el-form-item label="最大人数"><el-input-number v-model="form.maxStudents" :min="0" style="width:100%" /></el-form-item></el-col>
      </el-row>
      <el-form-item label="教室"><el-input v-model="form.classroom" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listSchedules, saveSchedule, deleteSchedule } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({})
const editId = ref(null)

const loadData = async () => {
  loading.value = true
  try { const res = await listSchedules(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openAdd = () => { isEdit.value = false; form.value = { courseName:'', teacherName:'', semester:'', className:'', weekday:1, sections:'', classroom:'', maxStudents:0 }; editId.value = null; dialogVisible.value = true }
const openEdit = (row) => { isEdit.value = true; form.value = { ...row }; editId.value = row.id; dialogVisible.value = true }

const handleSave = async () => {
  try { await saveSchedule(form.value); ElMessage.success('保存成功'); dialogVisible.value = false; loadData() }
  catch { ElMessage.error('保存失败') }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该排课？', '提示', { confirmButtonText:'确定', cancelButtonText:'取消', type:'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => loadData())
</script>
<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
