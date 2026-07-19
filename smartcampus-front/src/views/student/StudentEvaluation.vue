<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><StarFilled /></el-icon></div>
      <div><h2 class="page-title">网上评教</h2><p class="page-desc">对授课教师进行教学评价</p></div>
    </div>
    <div class="toolbar">
      <el-select v-model="semester" @change="loadData" style="width: 200px">
        <el-option label="2025-2026-2" value="2025-2026-2" />
        <el-option label="2025-2026-1" value="2025-2026-1" />
      </el-select>
    </div>
    <div class="card" v-loading="loading">
      <el-empty v-if="courses.length === 0" description="暂无待评教课程" />
      <div v-else class="eval-list">
        <div
          v-for="course in courses" :key="course.id"
          class="eval-card-item"
        >
          <div class="eval-card-header">
            <div>
              <strong>{{ course.courseName }}</strong>
              <span class="teacher-name">授课教师：{{ course.teacherName }}</span>
            </div>
            <el-tag v-if="course.evaluated" type="success" size="small">已评教</el-tag>
            <el-button v-else type="primary" size="small" @click="openDialog(course)"><el-icon :size="14" style="margin-right:4px"><EditPen /></el-icon>去评教</el-button>
          </div>
          <div v-if="course.evaluated" class="eval-scores">
            <div v-for="(score, dim) in course.scores" :key="dim" class="score-item">
              <span>{{ dim }}</span>
              <el-rate v-model="course.scores[dim]" disabled :max="5" size="small" />
            </div>
            <div class="eval-comment" v-if="course.comment">
              <span>评语：</span>{{ course.comment }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="教学评价" width="550px" :close-on-click-modal="false">
      <el-form :model="form" label-width="100px" v-if="currentCourse">
        <el-form-item label="课程">
          <strong>{{ currentCourse.courseName }}</strong>
        </el-form-item>
        <el-form-item label="教师">
          <span>{{ currentCourse.teacherName }}</span>
        </el-form-item>
        <el-form-item v-for="dim in dimensions" :key="dim.key" :label="dim.label">
          <el-rate v-model="form.scores[dim.key]" :max="5" show-score />
        </el-form-item>
        <el-form-item label="评语">
          <el-input v-model="form.comment" type="textarea" :rows="3" placeholder="请输入您的评语（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting"><el-icon :size="14" style="margin-right:4px"><Upload /></el-icon>提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyEvaluations, submitEvaluation } from '@/api/exam'
import { StarFilled, EditPen, Upload } from '@element-plus/icons-vue'

const semester = ref('2025-2026-2')
const courses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const submitting = ref(false)
const currentCourse = ref(null)

const dimensions = [
  { key: '教学态度', label: '教学态度' },
  { key: '教学内容', label: '教学内容' },
  { key: '教学方法', label: '教学方法' },
  { key: '教学效果', label: '教学效果' }
]

const form = reactive({
  scores: { '教学态度': 0, '教学内容': 0, '教学方法': 0, '教学效果': 0 },
  comment: ''
})

const openDialog = (course) => {
  currentCourse.value = course
  form.scores = { '教学态度': 5, '教学内容': 5, '教学方法': 5, '教学效果': 5 }
  form.comment = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (Object.values(form.scores).some(v => v === 0)) {
    ElMessage.warning('请完成所有维度的评分')
    return
  }
  submitting.value = true
  try {
    const res = await submitEvaluation({
      courseId: currentCourse.value.courseId || currentCourse.value.id,
      teacherId: currentCourse.value.teacherId,
      scores: form.scores,
      comment: form.comment
    })
    if (res.code === 200) {
      ElMessage.success('评教提交成功')
      dialogVisible.value = false
      await loadData()
    } else {
      ElMessage.error(res.message || '提交失败')
    }
  } catch {
    ElMessage.error('提交失败')
  } finally {
    submitting.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getMyEvaluations(semester.value)
    if (res.code === 200) {
      courses.value = (res.data || []).map(c => ({
        ...c,
        scores: c.scores || {}
      }))
    } else {
      ElMessage.error(res.message || '加载评教列表失败')
    }
  } catch {
    ElMessage.error('加载评教列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => loadData())
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.eval-list{display:flex;flex-direction:column;gap:16px}
.eval-card-item{background:#fafbfc;border:1px solid #EEF0F4;border-radius:10px;padding:16px 20px}
.eval-card-header{display:flex;justify-content:space-between;align-items:center}
.teacher-name{color:#909399;margin-left:16px;font-size:14px}
.eval-scores{margin-top:12px;padding-top:12px;border-top:1px solid #ebeef5}
.score-item{display:flex;align-items:center;gap:10px;margin-bottom:8px}
.score-item>span{width:80px;color:#606266}
.eval-comment{margin-top:8px;color:#606266;font-size:13px}
.eval-comment span{color:#909399}
</style>
