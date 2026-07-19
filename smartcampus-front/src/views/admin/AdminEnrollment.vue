<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Select /></el-icon></div>
      <div><h2 class="page-title">选课管理</h2><p class="page-desc">管理学生选课记录，支持选课登记与退课操作</p></div>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="openEnroll"><el-icon style="margin-right:4px"><Plus /></el-icon>登记选课</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border>
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
            <el-button v-if="row.status === 'ENROLLED'" size="small" type="danger" plain @click="handleDrop(row)">退课</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

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
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listStudentCourses, enrollStudent, dropStudent } from '@/api/course'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Select, Plus } from '@element-plus/icons-vue'

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
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;margin:0;line-height:1.3;color:#1a1a2e}
.page-desc{font-size:13px;color:#8c8c8c;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
