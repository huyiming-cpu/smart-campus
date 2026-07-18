<template>
  <div class="scores-page">
    <div class="page-header">
      <h2>成绩查询</h2>
      <el-select v-model="semester" placeholder="选择学期" @change="loadData" style="width: 200px">
        <el-option label="2025-2026-2" value="2025-2026-2" />
        <el-option label="2025-2026-1" value="2025-2026-1" />
        <el-option label="2024-2025-2" value="2024-2025-2" />
        <el-option label="2024-2025-1" value="2024-2025-1" />
      </el-select>
    </div>

    <div class="overview-cards" v-loading="overviewLoading">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-num">{{ overview.totalCourses }}</div>
        <div class="stat-label">全部课程</div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-num green">{{ overview.passed }}</div>
        <div class="stat-label">已通过</div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-num red">{{ overview.failed }}</div>
        <div class="stat-label">未通过</div>
      </el-card>
      <el-card shadow="hover" class="stat-card">
        <div class="stat-num blue">{{ overview.avgScore }}</div>
        <div class="stat-label">平均成绩</div>
      </el-card>
      <el-card shadow="hover" class="stat-card" v-if="overview.gpa !== undefined">
        <div class="stat-num purple">{{ overview.gpa }}</div>
        <div class="stat-label">GPA</div>
      </el-card>
    </div>

    <el-card shadow="never" style="margin-top: 20px">
      <el-table :data="scores" stripe v-loading="scoresLoading" empty-text="暂无成绩数据">
        <el-table-column prop="courseName" label="课程名称" min-width="180" />
        <el-table-column prop="score" label="成绩" width="100">
          <template #default="{ row }">
            <span :class="{ 'fail-score': row.score < 60 }">{{ row.score }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="gradeLevel" label="等级" width="80">
          <template #default="{ row }">
            <el-tag :type="gradeTagType(row.gradeLevel)" size="small">{{ row.gradeLevel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examType" label="考试类型" width="100" />
        <el-table-column label="补考/重修" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isRetake" type="warning" size="small">补考</el-tag>
            <el-tag v-else-if="row.isMakeup" type="info" size="small">重修</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="130" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyScores, getCourseOverview } from '@/api/course'

const semester = ref('2025-2026-2')
const scores = ref([])
const scoresLoading = ref(false)
const overviewLoading = ref(false)
const overview = reactive({
  totalCourses: 0, passed: 0, failed: 0, avgScore: 0, gpa: null
})

const gradeTagType = (level) => {
  const map = { A: 'success', B: '', C: 'info', D: 'warning', F: 'danger' }
  return map[level] || 'info'
}

const loadOverview = async () => {
  overviewLoading.value = true
  try {
    const res = await getCourseOverview()
    if (res.code === 200) {
      Object.assign(overview, res.data || {})
    }
  } catch { /* ignore */ }
  finally { overviewLoading.value = false }
}

const loadScores = async () => {
  scoresLoading.value = true
  try {
    const res = await getMyScores(semester.value)
    if (res.code === 200) {
      scores.value = res.data || []
    } else {
      ElMessage.error(res.message || '加载成绩失败')
    }
  } catch {
    ElMessage.error('加载成绩失败')
  } finally {
    scoresLoading.value = false
  }
}

const loadData = () => { loadOverview(); loadScores() }

onMounted(() => loadData())
</script>

<style scoped>
.scores-page { max-width: 1200px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
.overview-cards { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 16px; }
.stat-card { text-align: center; }
.stat-num { font-size: 32px; font-weight: bold; color: #303133; }
.stat-num.green { color: #67c23a; }
.stat-num.red { color: #f56c6c; }
.stat-num.blue { color: #409eff; }
.stat-num.purple { color: #b37feb; }
.stat-label { font-size: 14px; color: #909399; margin-top: 6px; }
.fail-score { color: #f56c6c; font-weight: bold; }
</style>
