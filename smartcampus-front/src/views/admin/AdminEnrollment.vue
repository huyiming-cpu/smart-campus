<template>
  <el-card>
    <template #header>
      <div class="card-header"><span>选课管理</span><el-button type="primary" @click="openEnroll">登记选课</el-button></div>
    </template>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="studentId" label="学生ID" width="100" />
      <el-table-column prop="studentName" label="学生姓名" width="100" />
      <el-table-column prop="scheduleId" label="排课ID" width="100" />
      <el-table-column prop="courseName" label="课程" width="140" />
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ENROLLED' ? 'success' : 'info'">{{ row.status === 'ENROLLED' ? '在读' : '已退课' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="score" label="成绩" width="80" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 'ENROLLED'" size="small" type="danger" @click="handleDrop(row)">退课</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog title="登记选课" v-model="dialogVisible" width="450px">
    <el-form :model="form" label-width="100px">
      <el-form-item label="学生ID"><el-input v-model="form.studentId" /></el-form-item>
      <el-form-item label="排课ID"><el-input v-model="form.scheduleId" /></el-form-item>
      <el-form-item label="学期"><el-input v-model="form.semester" placeholder="如：2025-2026-1" /></el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleEnroll">确认</el-button>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listStudentCourses, enrollStudent, dropStudent } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = ref({ studentId:'', scheduleId:'', semester:'' })

const loadData = async () => {
  loading.value = true
  try { const res = await listStudentCourses(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const openEnroll = () => { form.value = { studentId:'', scheduleId:'', semester:'' }; dialogVisible.value = true }

const handleEnroll = async () => {
  try { await enrollStudent(form.value); ElMessage.success('选课成功'); dialogVisible.value = false; loadData() }
  catch { ElMessage.error('选课失败') }
}

const handleDrop = async (row) => {
  try {
    await ElMessageBox.confirm('确定退课？', '提示', { confirmButtonText:'确定', cancelButtonText:'取消', type:'warning' })
    await dropStudent(row.id)
    ElMessage.success('退课成功')
    loadData()
  } catch { /* cancelled */ }
}

onMounted(() => loadData())
</script>
<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
