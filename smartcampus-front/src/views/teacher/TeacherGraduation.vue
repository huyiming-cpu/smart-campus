<template>
  <div class="teacher-graduation">
    <div class="page-header">
      <h2>毕业设计管理</h2>
    </div>

    <el-card v-loading="loading">
      <el-table :data="designList" border stripe @row-click="openDetail" highlight-current-row style="cursor:pointer">
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="130" />
        <el-table-column prop="topicName" label="课题名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="designStatusTag(row.status)" size="small">{{ row.status || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="170">
          <template #default="{ row }">{{ row.submitTime || '-' }}</template>
        </el-table-column>
        <el-table-column prop="score" label="成绩" width="80">
          <template #default="{ row }">
            <span :style="{ color: scoreColor(row.score), fontWeight: 'bold' }">{{ row.score ?? '-' }}</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!designList.length && !loading" class="empty-tip">暂无毕业设计数据</div>
    </el-card>

    <!-- 详情/编辑弹窗 -->
    <el-dialog v-model="detailVisible" title="毕业设计详情" width="680px" @close="detailData = null">
      <template v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="学生姓名">{{ detailData.studentName }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
          <el-descriptions-item label="课题名称" :span="2">{{ detailData.topicName }}</el-descriptions-item>
          <el-descriptions-item label="课题描述" :span="2">{{ detailData.topicDescription || '暂无描述' }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detailData.submitTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前状态">{{ detailData.status || '-' }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <el-form :model="editForm" label-width="100px">
          <el-form-item label="教师评语">
            <el-input v-model="editForm.comment" type="textarea" :rows="3" placeholder="请输入评语" />
          </el-form-item>
          <el-form-item label="公示公告">
            <el-input v-model="editForm.announcement" type="textarea" :rows="3" placeholder="请输入公示公告内容" />
          </el-form-item>
          <el-form-item label="成绩">
            <el-input-number v-model="editForm.score" :min="0" :max="100" :precision="1" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="editForm.status" style="width:100%">
              <el-option label="待审核" value="PENDING" />
              <el-option label="已通过" value="APPROVED" />
              <el-option label="已拒绝" value="REJECTED" />
              <el-option label="进行中" value="IN_PROGRESS" />
              <el-option label="已完成" value="COMPLETED" />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button v-if="detailData?.status==='PENDING'" type="success" @click="approve('APPROVED')" :loading="approving">通过</el-button>
        <el-button v-if="detailData?.status==='PENDING'" type="danger" @click="approve('REJECTED')" :loading="approving">驳回</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMyDesigns, updateDesign } from '@/api/teacher'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false), designList = ref([]), detailVisible = ref(false), detailData = ref(null), saving = ref(false), approving = ref(false)

const editForm = reactive({ comment: '', announcement: '', score: null, status: '' })

const designStatusTag = (status) => {
  if (status === '已通过') return 'success'
  if (status === '未通过') return 'danger'
  if (status === '进行中' || status === '答辩中') return ''
  if (status === '已提交') return 'warning'
  return 'info'
}

const scoreColor = (val) => {
  const n = parseFloat(val)
  if (isNaN(n)) return '#303133'
  if (n >= 90) return '#67c23a'
  if (n >= 70) return '#e6a23c'
  return '#f56c6c'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await listMyDesigns()
    designList.value = res.data || []
  } catch { ElMessage.error('加载毕业设计数据失败') }
  finally { loading.value = false }
}

const openDetail = (row) => {
  detailData.value = row
  editForm.comment = row.comment || ''
  editForm.announcement = row.announcement || ''
  editForm.score = row.score ?? null
  editForm.status = row.status || ''
  detailVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateDesign(detailData.value.id, { ...editForm })
    ElMessage.success('保存成功')
    detailVisible.value = false
    loadData()
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}

const approve = async (status) => {
  approving.value = true
  try {
    await request.put(`/api/teacher/graduation-design/${detailData.value.id}/approve`, { status, comment: editForm.comment })
    ElMessage.success(status==='APPROVED'?'已通过':'已驳回')
    detailVisible.value = false
    loadData()
  } catch { ElMessage.error('操作失败') }
  finally { approving.value = false }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.teacher-graduation { padding: 20px; max-width: 1100px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
.empty-tip { text-align: center; padding: 40px 0; color: #c0c4cc; }
</style>
