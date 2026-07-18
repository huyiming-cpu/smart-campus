<template>
  <div class="teacher-research">
    <div class="page-header">
      <h2>科研情况管理</h2>
      <el-button type="primary" @click="openDialog()">新增科研项目</el-button>
    </div>

    <el-card v-loading="loading">
      <el-table :data="researchList" border stripe>
        <el-table-column prop="projectName" label="项目名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="projectCode" label="项目编号" width="140" />
        <el-table-column prop="projectType" label="项目类型" width="110" />
        <el-table-column prop="fund" label="经费(万元)" width="110" />
        <el-table-column label="开始日期" width="120">
          <template #default="{ row }">{{ row.startDate?.substring(0, 10) || '-' }}</template>
        </el-table-column>
        <el-table-column label="结束日期" width="120">
          <template #default="{ row }">{{ row.endDate?.substring(0, 10) || '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ row.status || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="members" label="成员" min-width="140" show-overflow-tooltip />
        <el-table-column prop="achievements" label="成果" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑科研项目' : '新增科研项目'" width="620px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="项目编号" prop="projectCode">
          <el-input v-model="form.projectCode" placeholder="请输入项目编号" />
        </el-form-item>
        <el-form-item label="项目类型" prop="projectType">
          <el-select v-model="form.projectType" placeholder="请选择项目类型" style="width:100%">
            <el-option label="国家级" value="国家级" />
            <el-option label="省部级" value="省部级" />
            <el-option label="市厅级" value="市厅级" />
            <el-option label="校级" value="校级" />
            <el-option label="横向课题" value="横向课题" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="经费(万元)" prop="fund">
          <el-input-number v-model="form.fund" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker v-model="form.startDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker v-model="form.endDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width:100%">
            <el-option label="进行中" value="进行中" />
            <el-option label="已完成" value="已完成" />
            <el-option label="已结题" value="已结题" />
            <el-option label="暂停" value="暂停" />
          </el-select>
        </el-form-item>
        <el-form-item label="成员" prop="members">
          <el-input v-model="form.members" type="textarea" :rows="2" placeholder="请输入项目成员，多人用逗号分隔" />
        </el-form-item>
        <el-form-item label="成果" prop="achievements">
          <el-input v-model="form.achievements" type="textarea" :rows="3" placeholder="请输入项目成果描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMyResearch, saveResearch, updateResearch, deleteResearch } from '@/api/teacher'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const researchList = ref([])
const editId = ref(null)

const form = reactive({
  projectName: '', projectCode: '', projectType: '', fund: null,
  startDate: '', endDate: '', status: '', members: '', achievements: ''
})

const rules = {
  projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }],
  projectType: [{ required: true, message: '请选择项目类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const statusTag = (status) => {
  if (status === '进行中') return ''
  if (status === '已完成' || status === '已结题') return 'success'
  if (status === '暂停') return 'warning'
  return 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listMyResearch()
    researchList.value = res.data || []
  } catch { ElMessage.error('加载科研数据失败') }
  finally { loading.value = false }
}

const openDialog = (row) => {
  if (row) {
    isEdit.value = true
    editId.value = row.id
    Object.assign(form, {
      projectName: row.projectName || '',
      projectCode: row.projectCode || '',
      projectType: row.projectType || '',
      fund: row.fund ?? null,
      startDate: row.startDate || '',
      endDate: row.endDate || '',
      status: row.status || '',
      members: row.members || '',
      achievements: row.achievements || ''
    })
  } else {
    isEdit.value = false
    editId.value = null
    resetForm()
  }
  dialogVisible.value = true
}

const resetForm = () => {
  form.projectName = ''; form.projectCode = ''; form.projectType = ''
  form.fund = null; form.startDate = ''; form.endDate = ''
  form.status = ''; form.members = ''; form.achievements = ''
  formRef.value?.resetFields()
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateResearch(editId.value, { ...form })
      ElMessage.success('更新成功')
    } else {
      await saveResearch({ ...form })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { submitting.value = false }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除项目"${row.projectName}"吗？此操作不可恢复。`, '删除确认', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
    await deleteResearch(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch { /* 取消或失败 */ }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.teacher-research { padding: 20px; max-width: 1200px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
</style>
