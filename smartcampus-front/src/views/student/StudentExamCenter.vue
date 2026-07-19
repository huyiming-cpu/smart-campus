<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Tickets /></el-icon></div>
      <div><h2 class="page-title">考试中心</h2><p class="page-desc">期末考试安排、补考重修申请与等级考试报名</p></div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-item">
        <div class="stat-icon" style="background:#EBF3FC;"><el-icon :size="18" color="#5B9BD5"><Document /></el-icon></div>
        <div class="stat-body"><div class="stat-num">{{ exams.length }}</div><div class="stat-lbl">考试安排</div></div>
      </div>
      <div class="stat-item">
        <div class="stat-icon" style="background:#FEF3E2;"><el-icon :size="18" color="#F59E0B"><WarningFilled /></el-icon></div>
        <div class="stat-body"><div class="stat-num">{{ retakes.filter(r=>!r.status).length }}</div><div class="stat-lbl">待申请补考</div></div>
      </div>
      <div class="stat-item">
        <div class="stat-icon" style="background:#ECFDF5;"><el-icon :size="18" color="#10B981"><Medal /></el-icon></div>
        <div class="stat-body"><div class="stat-num">{{ levelExams.filter(e=>!e.registered).length }}</div><div class="stat-lbl">可报名等级考试</div></div>
      </div>
    </div>

    <div class="card">
      <el-tabs v-model="tab" class="custom-tabs">
        <el-tab-pane name="exams">
          <template #label><span class="tab-label"><el-icon :size="16"><Document /></el-icon> 考试安排</span></template>
        </el-tab-pane>
        <el-tab-pane name="retake">
          <template #label><span class="tab-label"><el-icon :size="16"><WarningFilled /></el-icon> 补考重修</span></template>
        </el-tab-pane>
        <el-tab-pane name="level">
          <template #label><span class="tab-label"><el-icon :size="16"><Medal /></el-icon> 等级考试</span></template>
        </el-tab-pane>
      </el-tabs>

      <!-- 考试安排 -->
      <div v-if="tab==='exams'">
        <div class="toolbar">
          <el-select v-model="semester" @change="loadExams" style="width:200px">
            <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
          </el-select>
        </div>
        <el-table :data="exams" v-loading="eLoad" stripe border empty-text="暂无考试安排">
          <el-table-column prop="examName" label="考试名称" min-width="150" />
          <el-table-column prop="courseName" label="课程" min-width="140" />
          <el-table-column prop="examDate" label="日期" width="120" />
          <el-table-column label="时间" width="150"><template #default="{row}">{{ row.examTimeStart }} — {{ row.examTimeEnd }}</template></el-table-column>
          <el-table-column prop="location" label="地点" min-width="130" />
          <el-table-column prop="supervisorName" label="监考教师" width="100" />
        </el-table>
      </div>

      <!-- 补考重修 -->
      <div v-if="tab==='retake'">
        <el-table :data="retakes" v-loading="rLoad" stripe border empty-text="暂无补考重修记录">
          <el-table-column prop="courseName" label="课程" min-width="160" />
          <el-table-column prop="originalScore" label="原成绩" width="90" />
          <el-table-column label="类型" width="90">
            <template #default="{row}"><el-tag size="small" :type="row.type==='MAKEUP'?'warning':'info'">{{ row.type==='MAKEUP'?'补考':'重修' }}</el-tag></template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{row}">
              <el-tag v-if="row.status" size="small" :type="row.status==='已通过'?'success':row.status==='已驳回'?'danger':'info'">{{ row.status }}</el-tag>
              <el-tag v-else size="small" type="warning">未申请</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{row}">
              <el-button v-if="!row.status" size="small" type="primary" @click="applyRetake(row)"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>申请补考</el-button>
              <span v-else style="color:#9CA3AF;font-size:13px">—</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 等级考试 -->
      <div v-if="tab==='level'">
        <el-table :data="levelExams" v-loading="lLoad" stripe border empty-text="暂无等级考试">
          <el-table-column prop="examName" label="考试名称" min-width="220" />
          <el-table-column prop="examDate" label="考试日期" width="120" />
          <el-table-column label="费用" width="90"><template #default="{row}">&yen;{{ row.fee }}</template></el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="{row}">
              <el-tag v-if="row.registered" type="success" size="small">已报名</el-tag>
              <el-tag v-else size="small" type="info">待报名</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="110" fixed="right">
            <template #default="{row}">
              <el-button v-if="!row.registered" size="small" type="primary" @click="regLevel(row)"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>报名</el-button>
              <el-button v-else size="small" type="success" plain disabled>已报名</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Tickets, Document, WarningFilled, Medal, Plus } from '@element-plus/icons-vue'

const tab = ref('exams')
const semester = ref('2025-2026-2')
const semesters = ['2025-2026-2', '2025-2026-1']
const exams = ref([]), eLoad = ref(false)
const retakes = ref([]), rLoad = ref(false)
const levelExams = ref([]), lLoad = ref(false)

const loadExams = async () => {
  eLoad.value = true
  try { const r = await request.get('/api/student/exams', { params: { semester: semester.value } }); exams.value = r.data || [] } catch {}
  finally { eLoad.value = false }
}

const loadAll = async () => {
  eLoad.value = true; rLoad.value = true; lLoad.value = true
  try { const r = await request.get('/api/student/exams', { params: { semester: '2025-2026-2' } }); exams.value = r.data || [] } catch {}
  try { const r = await request.get('/api/student/retakes'); retakes.value = r.data || [] } catch {}
  try {
    const [r1, r2] = await Promise.all([request.get('/api/student/level-exams'), request.get('/api/student/level-exams/my')])
    const regged = new Set((r2.data || []).map(x => x.examId))
    levelExams.value = (r1.data || []).map(e => ({ ...e, registered: regged.has(e.id) }))
  } catch {}
  eLoad.value = false; rLoad.value = false; lLoad.value = false
}

const applyRetake = async (row) => {
  try { await request.post('/api/student/retakes', { courseId: row.courseId, semester: '2025-2026-2', type: 'MAKEUP', originalScore: row.originalScore || 0 }); ElMessage.success('申请成功'); loadAll() }
  catch { ElMessage.error('申请失败') }
}

const regLevel = async (row) => {
  try { await request.post('/api/student/level-exams/register', { examId: row.id }); ElMessage.success('报名成功'); loadAll() }
  catch { ElMessage.error('报名失败') }
}

onMounted(loadAll)
</script>

<style scoped>
.page { padding: 20px 24px; max-width: 1200px; margin: 0 auto; font-family: "Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif; }
.page-head { display: flex; align-items: center; gap: 14px; margin-bottom: 20px; }
.page-icon { width: 42px; height: 42px; border-radius: 12px; display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(91,155,213,.25); }
.page-title { font-size: 20px; font-weight: 700; color: #1A1A2E; margin: 0; }
.page-desc { font-size: 13px; color: #9CA3AF; margin: 2px 0 0; }

.stats-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 14px; margin-bottom: 20px; }
.stat-item { background: #fff; border: 1px solid #EEF0F4; border-radius: 14px; padding: 16px 20px; display: flex; align-items: center; gap: 14px; box-shadow: 0 2px 8px rgba(0,0,0,.02); }
.stat-icon { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.stat-body { min-width: 0; }
.stat-num { font-size: 24px; font-weight: 700; color: #1A1A2E; line-height: 1.2; }
.stat-lbl { font-size: 12px; color: #9CA3AF; }

.toolbar { margin-bottom: 16px; display: flex; align-items: center; gap: 10px; }
.card { background: #fff; border: 1px solid #EEF0F4; border-radius: 14px; padding: 20px; box-shadow: 0 2px 12px rgba(0,0,0,.02); }
.custom-tabs :deep(.el-tabs__header) { margin-bottom: 16px; }
.custom-tabs :deep(.el-tabs__item) { font-size: 14px; font-weight: 500; color: #6B7280; }
.custom-tabs :deep(.el-tabs__item.is-active) { color: #5B9BD5; font-weight: 600; }
.custom-tabs :deep(.el-tabs__active-bar) { background: #5B9BD5; }
.tab-label { display: flex; align-items: center; gap: 6px; }

@media (max-width: 768px) { .stats-row { grid-template-columns: 1fr; } }
</style>
