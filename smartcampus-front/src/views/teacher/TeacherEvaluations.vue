<template>
  <div class="teacher-evaluations">
    <div class="page-header">
      <h2>评教结果查看</h2>
      <el-select v-model="semester" @change="loadData" style="width:200px">
        <el-option label="2025-2026-2" value="2025-2026-2" />
        <el-option label="2025-2026-1" value="2025-2026-1" />
        <el-option label="2024-2025-2" value="2024-2025-2" />
        <el-option label="2024-2025-1" value="2024-2025-1" />
      </el-select>
    </div>

    <!-- 评分汇总 -->
    <el-card class="summary-card" v-loading="loading">
      <template #header><span style="font-weight:bold">评分汇总</span></template>
      <el-row :gutter="20">
        <el-col :span="6" v-for="dim in dimensions" :key="dim.key">
          <div class="stat-item">
            <div class="stat-label">{{ dim.label }}</div>
            <div class="stat-value" :style="{ color: scoreColor(dim.avg) }">{{ dim.avg }}</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-label">综合评分</div>
            <div class="stat-value overall" :style="{ color: scoreColor(overallAvg) }">{{ overallAvg }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 详细记录 -->
    <el-card v-loading="loading">
      <template #header><span style="font-weight:bold">评教详情</span></template>
      <el-table :data="evalList" border stripe>
        <el-table-column prop="courseName" label="课程名称" min-width="140" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="教学态度" width="90">
          <template #default="{ row }">
            <span :style="{ color: scoreColor(row.teachingAttitude) }">{{ row.teachingAttitude ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教学内容" width="90">
          <template #default="{ row }">
            <span :style="{ color: scoreColor(row.teachingContent) }">{{ row.teachingContent ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教学方法" width="90">
          <template #default="{ row }">
            <span :style="{ color: scoreColor(row.teachingMethod) }">{{ row.teachingMethod ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="教学效果" width="90">
          <template #default="{ row }">
            <span :style="{ color: scoreColor(row.teachingEffect) }">{{ row.teachingEffect ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="综合评分" width="100">
          <template #default="{ row }">
            <span class="overall-cell" :style="{ color: scoreColor(row.overallScore) }">{{ row.overallScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="评语" min-width="160" show-overflow-tooltip>
          <template #default="{ row }">
            <span v-if="row.comment">{{ row.comment }}</span>
            <span v-else style="color:#c0c4cc">匿名评价</span>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!evalList.length && !loading" class="empty-tip">暂无评教数据</div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getTeacherEvaluations } from '@/api/exam'
import { ElMessage } from 'element-plus'

const semester = ref('2025-2026-2')
const loading = ref(false)
const evalList = ref([])

const dimensions = computed(() => {
  const keys = ['teachingAttitude', 'teachingContent', 'teachingMethod', 'teachingEffect']
  const labels = ['教学态度', '教学内容', '教学方法', '教学效果']
  if (!evalList.value.length) return labels.map((l, i) => ({ key: keys[i], label: l, avg: '-' }))
  return keys.map((k, i) => {
    const sum = evalList.value.reduce((s, e) => s + (Number(e[k]) || 0), 0)
    return { key: k, label: labels[i], avg: (sum / evalList.value.length).toFixed(1) }
  })
})

const overallAvg = computed(() => {
  if (!evalList.value.length) return '-'
  const sum = evalList.value.reduce((s, e) => s + (Number(e.overallScore) || 0), 0)
  return (sum / evalList.value.length).toFixed(1)
})

const scoreColor = (val) => {
  const n = parseFloat(val)
  if (isNaN(n)) return '#909399'
  if (n >= 90) return '#67c23a'
  if (n >= 70) return '#e6a23c'
  return '#f56c6c'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTeacherEvaluations(semester.value)
    evalList.value = res.data || []
  } catch { ElMessage.error('加载评教数据失败') }
  finally { loading.value = false }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.teacher-evaluations { padding: 20px; max-width: 1100px; margin: 0 auto; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-header h2 { margin: 0; font-size: 20px; }
.summary-card { margin-bottom: 20px; }
.stat-item { text-align: center; padding: 16px 0; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-value.overall { font-size: 32px; }
.overall-cell { font-weight: bold; }
.empty-tip { text-align: center; padding: 40px 0; color: #c0c4cc; }
</style>
