<template>
  <div class="workbench">
    <!-- 顶部标题栏 -->
    <div class="wb-header">
      <div class="wb-header-left">
        <div class="wb-icon-box">
          <el-icon :size="22" color="#fff"><Reading /></el-icon>
        </div>
        <div>
          <h2 class="wb-title">教学工作台</h2>
          <p class="wb-subtitle">课程管理 · 成绩录入 · 教学评估</p>
        </div>
      </div>
    </div>

    <!-- 标签页 -->
    <div class="wb-tabs">
      <el-tabs v-model="tab" @tab-change="onTabChange" class="custom-tabs">
        <el-tab-pane name="schedule">
          <template #label>
            <span class="tab-label"><el-icon :size="16"><Calendar /></el-icon> 任课信息</span>
          </template>

          <div class="tab-toolbar">
            <el-select v-model="semester" @change="loadSchedule" style="width:200px" size="default">
              <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
            </el-select>
          </div>

          <div class="glass-card" v-loading="sLoading">
            <el-empty v-if="!sLoading && scheduleList.length===0" description="本学期无教学任务" />
            <el-table v-else :data="scheduleList" border stripe>
              <el-table-column label="周次" width="90">
                <template #default="{row}">
                  <el-tag size="small" type="info">{{ ['','周一','周二','周三','周四','周五','周六','周日'][row.weekday] }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column label="节次" width="100">
                <template #default="{row}">第{{ row.sectionStart }}-{{ row.sectionEnd }}节</template>
              </el-table-column>
              <el-table-column prop="courseName" label="课程" min-width="160" />
              <el-table-column prop="className" label="班级" width="110" />
              <el-table-column prop="classroom" label="教室" width="130" />
              <el-table-column prop="weeks" label="教学周" width="90" />
              <el-table-column label="操作" width="90" fixed="right">
                <template #default="{row}">
                  <el-button type="primary" size="small" plain @click="showStudents(row)">名单</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane name="scores">
          <template #label>
            <span class="tab-label"><el-icon :size="16"><EditPen /></el-icon> 成绩录入</span>
          </template>

          <div class="tab-toolbar">
            <span class="toolbar-hint">选择课程：</span>
            <el-select v-model="selectedCourseId" @change="loadScoreStudents" placeholder="请选择课程" style="width:420px" size="default">
              <el-option v-for="c in courses" :key="c.id" :label="c.courseName+' — '+c.className+' ('+['','周一','周二','周三','周四','周五'][c.weekday]+')'" :value="c.id"/>
            </el-select>
          </div>

          <div class="glass-card" v-if="scoreStudents.length>0" v-loading="stuLoading">
            <div class="card-head">
              <span>学生列表 · 共 <b>{{ scoreStudents.length }}</b> 人</span>
            </div>
            <el-table :data="scoreStudents" border stripe>
              <el-table-column type="index" label="#" width="50" />
              <el-table-column prop="studentName" label="姓名" width="90" />
              <el-table-column prop="studentNo" label="学号" width="130" />
              <el-table-column prop="className" label="班级" width="110" />
              <el-table-column label="成绩" width="140">
                <template #default="{row}">
                  <el-input-number v-model="scores[row.studentCourseId||row.id]" :min="0" :max="100" :precision="1" size="small" placeholder="0-100" style="width:110px"/>
                </template>
              </el-table-column>
              <el-table-column label="等级" width="100">
                <template #default="{row}">
                  <el-select v-model="grades[row.studentCourseId||row.id]" size="small" style="width:80px">
                    <el-option v-for="g in ['A','B','C','D','F']" :key="g" :label="g" :value="g"/>
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column label="考试类型" width="120">
                <template #default="{row}">
                  <el-select v-model="types[row.studentCourseId||row.id]" size="small" style="width:100px">
                    <el-option label="期末" value="FINAL"/><el-option label="补考" value="MAKEUP"/><el-option label="重修" value="RETAKE"/>
                  </el-select>
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" class="submit-btn" @click="saveAll" :loading="saving">
              <el-icon :size="16"><Upload /></el-icon> 批量保存成绩
            </el-button>
          </div>
          <el-empty v-else-if="!scLoading && courses.length===0" description="本学期无教学任务" />
        </el-tab-pane>

        <el-tab-pane name="evaluation">
          <template #label>
            <span class="tab-label"><el-icon :size="16"><Star /></el-icon> 评教结果</span>
          </template>

          <div class="tab-toolbar">
            <el-select v-model="evalSemester" @change="loadEvals" style="width:200px" size="default">
              <el-option v-for="s in evalSemesters" :key="s" :label="s" :value="s"/>
            </el-select>
          </div>

          <!-- 评分统计卡片 -->
          <div class="eval-stats" v-loading="evLoading">
            <div v-for="dim in dimensions" :key="dim.key" class="eval-stat-card">
              <div class="esc-icon" :style="{ background: dim.bg }">
                <el-icon :size="18" :color="dim.color"><component :is="dim.icon" /></el-icon>
              </div>
              <div class="esc-body">
                <div class="esc-label">{{ dim.label }}</div>
                <div class="esc-value" :style="{ color: scoreColor(dim.avg) }">{{ dim.avg }}</div>
              </div>
            </div>
            <div class="eval-stat-card overall">
              <div class="esc-icon" style="background: linear-gradient(135deg, #5B9BD5, #4A8AD4);">
                <el-icon :size="18" color="#fff"><Trophy /></el-icon>
              </div>
              <div class="esc-body">
                <div class="esc-label">综合评分</div>
                <div class="esc-value big" :style="{ color: scoreColor(overallAvg) }">{{ overallAvg }}</div>
              </div>
            </div>
          </div>

          <div class="glass-card" v-loading="evLoading">
            <div class="card-head"><span>评教详情</span></div>
            <el-table :data="evalList" border stripe>
              <el-table-column prop="courseName" label="课程" min-width="150" />
              <el-table-column prop="semester" label="学期" width="130" />
              <el-table-column label="教学态度" width="100">
                <template #default="{row}"><span class="score-cell" :style="{color:scoreColor(row.teachingAttitude)}">{{row.teachingAttitude??'-'}}</span></template>
              </el-table-column>
              <el-table-column label="教学内容" width="100">
                <template #default="{row}"><span class="score-cell" :style="{color:scoreColor(row.teachingContent)}">{{row.teachingContent??'-'}}</span></template>
              </el-table-column>
              <el-table-column label="教学方法" width="100">
                <template #default="{row}"><span class="score-cell" :style="{color:scoreColor(row.teachingMethod)}">{{row.teachingMethod??'-'}}</span></template>
              </el-table-column>
              <el-table-column label="教学效果" width="100">
                <template #default="{row}"><span class="score-cell" :style="{color:scoreColor(row.teachingEffect)}">{{row.teachingEffect??'-'}}</span></template>
              </el-table-column>
              <el-table-column label="综合" width="90">
                <template #default="{row}"><span class="score-cell fw" :style="{color:scoreColor(row.overallScore)}">{{row.overallScore??'-'}}</span></template>
              </el-table-column>
              <el-table-column prop="comment" label="评语" min-width="180" show-overflow-tooltip />
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane name="exams">
          <template #label>
            <span class="tab-label"><el-icon :size="16"><Document /></el-icon> 考试安排</span>
          </template>

          <div class="tab-toolbar">
            <el-select v-model="examSemester" @change="loadExams" style="width:200px" size="default">
              <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
            </el-select>
          </div>

          <div class="glass-card" v-loading="exLoading">
            <el-table :data="examList" border stripe empty-text="暂无考试安排">
              <el-table-column prop="examName" label="考试名称" min-width="160" />
              <el-table-column prop="courseName" label="课程" min-width="140" />
              <el-table-column prop="examDate" label="日期" width="120" />
              <el-table-column label="时间" width="160">
                <template #default="{row}">{{ row.examTimeStart }} — {{ row.examTimeEnd }}</template>
              </el-table-column>
              <el-table-column prop="location" label="地点" width="140" />
              <el-table-column prop="supervisorName" label="监考" width="90" />
              <el-table-column label="状态" width="100">
                <template #default="{row}">
                  <el-tag :type="row.status==='PUBLISHED'?'success':''" size="small">{{ row.status==='PUBLISHED'?'已发布':'草稿' }}</el-tag>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>

        <el-tab-pane name="enrollment">
          <template #label>
            <span class="tab-label"><el-icon :size="16"><UserFilled /></el-icon> 选课情况</span>
          </template>

          <div class="tab-toolbar">
            <el-select v-model="enrollSemester" @change="loadEnrollment" style="width:200px" size="default">
              <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
            </el-select>
          </div>

          <div class="glass-card" v-loading="enLoading" v-if="enrollList.length>0">
            <el-collapse v-model="enrollActiveNames">
              <el-collapse-item v-for="item in enrollList" :key="item.scheduleId" :name="String(item.scheduleId)">
                <template #title>
                  <div class="enroll-title">
                    <span class="enroll-course">{{ item.courseName }} <small>{{ item.courseCode }}</small></span>
                    <span class="enroll-meta">{{ item.className }} · {{ ['','周一','周二','周三','周四','周五'][item.weekday] }} {{ item.sectionStart}}-{{ item.sectionEnd }}节 · {{ item.classroom }}</span>
                    <el-tag size="small" :type="(item.enrolledCount>=(item.maxStudents||99))?'danger':'success'">
                      {{ item.enrolledCount }}/{{ item.maxStudents||'不限' }}人
                    </el-tag>
                  </div>
                </template>
                <el-table :data="item.students" size="small" border stripe v-if="item.students?.length">
                  <el-table-column type="index" label="#" width="50" />
                  <el-table-column prop="studentName" label="姓名" width="90" />
                  <el-table-column prop="studentNo" label="学号" width="130" />
                  <el-table-column prop="className" label="班级" width="120" />
                  <el-table-column label="成绩" width="80"><template #default="{row}">{{ row.score??'未录入' }}</template></el-table-column>
                  <el-table-column label="等级" width="70">
                    <template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='F'?'danger':''">{{row.gradeLevel}}</el-tag></template>
                  </el-table-column>
                  <el-table-column label="操作" width="90" fixed="right">
                    <template #default="{row}"><el-button size="small" type="danger" plain @click="dropStudent(row,item)">退课</el-button></template>
                  </el-table-column>
                </el-table>
                <el-empty v-else description="暂无学生选课" :image-size="60" />
              </el-collapse-item>
            </el-collapse>
          </div>
          <el-empty v-if="!enLoading && enrollList.length===0" description="本学期无教学任务" />
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 学生名单弹窗 -->
    <el-dialog v-model="rosterDv" :title="'学生名单 · '+selectedCourse?.courseName" width="600px">
      <el-table :data="rosterStudents" v-loading="rosterLoading" border stripe max-height="420">
        <el-table-column type="index" label="#" width="50" />
        <el-table-column prop="studentName" label="姓名" width="90" />
        <el-table-column prop="studentNo" label="学号" width="140" />
        <el-table-column prop="className" label="班级" width="120" />
        <el-table-column prop="department" label="学院" width="140" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Reading, Calendar, EditPen, Star, Document, UserFilled, Upload, Trophy, Medal, DataAnalysis, Notebook, ChatDotRound } from '@element-plus/icons-vue'

const tab = ref('schedule')
const semesters = ['2025-2026-2', '2025-2026-1']

// ========== 任课信息 ==========
const semester = ref('2025-2026-2'), scheduleList = ref([]), sLoading = ref(false)
const rosterDv = ref(false), rosterStudents = ref([]), rosterLoading = ref(false), selectedCourse = ref(null)

const loadSchedule = async () => {
  sLoading.value = true
  try { const r = await request.get('/api/teacher/schedule', { params: { semester: semester.value } }); scheduleList.value = r.data || [] }
  catch { scheduleList.value = [] }
  finally { sLoading.value = false }
}

const showStudents = async (row) => {
  selectedCourse.value = row; rosterDv.value = true; rosterLoading.value = true
  try { const r = await request.get(`/api/teacher/roster/${row.id}`); rosterStudents.value = r.data || [] }
  catch { rosterStudents.value = [] }
  finally { rosterLoading.value = false }
}

// ========== 成绩录入 ==========
const scLoading = ref(false), courses = ref([]), selectedCourseId = ref(null)
const scoreStudents = ref([]), stuLoading = ref(false), saving = ref(false)
const scores = reactive({}), grades = reactive({}), types = reactive({})

const loadCourses = async () => {
  scLoading.value = true
  try { const r = await request.get('/api/teacher/schedule', { params: { semester: '2025-2026-2' } }); courses.value = r.data || [] }
  catch { }
  finally { scLoading.value = false }
}

const loadScoreStudents = async () => {
  if (!selectedCourseId.value) return; stuLoading.value = true
  try {
    const r = await request.get(`/api/teacher/roster/${selectedCourseId.value}`)
    scoreStudents.value = (r.data || []).map(s => { const k = s.studentCourseId || s.id; scores[k] = s.score; grades[k] = s.gradeLevel || 'A'; types[k] = s.examType || 'FINAL'; return s })
  } catch { }
  finally { stuLoading.value = false }
}

const saveAll = async () => {
  saving.value = true; let ok = 0
  for (const s of scoreStudents.value) {
    const k = s.studentCourseId || s.id
    if (scores[k] === null || scores[k] === undefined) continue
    try { await request.put(`/api/teacher/scores/${k}`, { score: String(scores[k]), gradeLevel: grades[k] || 'A', examType: types[k] || 'FINAL', operatorName: '教师' }); ok++ } catch { }
  }
  ElMessage.success(`保存完成：${ok}/${scoreStudents.value.length}`)
  saving.value = false
}

// ========== 评教结果 ==========
const evalSemester = ref('2025-2026-2'), evalSemesters = ['2025-2026-2', '2025-2026-1', '2024-2025-2', '2024-2025-1']
const evLoading = ref(false), evalList = ref([])

const dimDefs = [
  { key: 'teachingAttitude', label: '教学态度', icon: Medal, color: '#5B9BD5', bg: '#EBF3FC' },
  { key: 'teachingContent', label: '教学内容', icon: Notebook, color: '#F59E0B', bg: '#FEF3E2' },
  { key: 'teachingMethod', label: '教学方法', icon: ChatDotRound, color: '#10B981', bg: '#ECFDF5' },
  { key: 'teachingEffect', label: '教学效果', icon: DataAnalysis, color: '#8B5CF6', bg: '#F3F0FF' },
]

const dimensions = computed(() => {
  if (!evalList.value.length) return dimDefs.map(d => ({ ...d, avg: '-' }))
  return dimDefs.map(d => {
    const sum = evalList.value.reduce((s, e) => s + (Number(e[d.key]) || 0), 0)
    return { ...d, avg: (sum / evalList.value.length).toFixed(1) }
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
  if (n >= 90) return '#10B981'; if (n >= 75) return '#F59E0B'; if (n >= 60) return '#F97316'; return '#EF4444'
}

const loadEvals = async () => {
  evLoading.value = true
  try { const r = await request.get('/api/teacher/evaluations', { params: { semester: evalSemester.value } }); evalList.value = r.data || [] }
  catch { ElMessage.error('加载评教数据失败') }
  finally { evLoading.value = false }
}

// ========== 考试安排 ==========
const examSemester = ref('2025-2026-2'), examList = ref([]), exLoading = ref(false)

const loadExams = async () => {
  exLoading.value = true
  try { const r = await request.get('/api/teacher/exams', { params: { semester: examSemester.value } }); examList.value = r.data || [] }
  catch { }
  finally { exLoading.value = false }
}

// ========== 选课情况 ==========
const enrollSemester = ref('2025-2026-2'), enrollList = ref([]), enLoading = ref(false)
const enrollActiveNames = ref([])

const loadEnrollment = async () => {
  enLoading.value = true
  try { const r = await request.get('/api/teacher/enrollment', {params:{semester:enrollSemester.value}}); enrollList.value = r.data || [] }
  catch { enrollList.value = [] }
  finally { enLoading.value = false }
}

const dropStudent = async (student, courseItem) => {
  try {
    await ElMessageBox.confirm(`确定要帮「${student.studentName}」退选「${courseItem.courseName}」吗？`,'教师退课',{confirmButtonText:'确定退课',cancelButtonText:'取消',type:'warning'})
    await request.put(`/api/teacher/student-courses/${student.studentCourseId}/drop`)
    ElMessage.success('退课成功')
    loadEnrollment()
  } catch(e){ if(e!=='cancel') ElMessage.error('退课失败') }
}

// ========== 初始化 ==========
const onTabChange = (name) => {
  if (name === 'schedule' && scheduleList.value.length === 0) loadSchedule()
  else if (name === 'scores' && courses.value.length === 0) loadCourses()
  else if (name === 'evaluation' && evalList.value.length === 0) loadEvals()
  else if (name === 'exams' && examList.value.length === 0) loadExams()
  else if (name === 'enrollment' && enrollList.value.length === 0) loadEnrollment()
}

loadSchedule()
</script>

<style scoped>
.workbench { padding: 20px 24px; max-width: 1200px; margin: 0 auto; font-family: "Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif; }

/* ===== 头部 ===== */
.wb-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 20px; }
.wb-header-left { display: flex; align-items: center; gap: 14px; }
.wb-icon-box { width: 42px; height: 42px; border-radius: 12px; background: linear-gradient(135deg, #5B9BD5, #4A8AD4); display: flex; align-items: center; justify-content: center; box-shadow: 0 4px 12px rgba(91,155,213,0.25); }
.wb-title { font-size: 20px; font-weight: 700; color: #1A1A2E; margin: 0; letter-spacing: .3px; }
.wb-subtitle { font-size: 13px; color: #9CA3AF; margin: 2px 0 0; }

/* ===== 标签页 ===== */
.wb-tabs { background: #fff; border-radius: 14px; border: 1px solid #EEF0F4; box-shadow: 0 2px 12px rgba(0,0,0,0.02); padding: 4px 20px 20px; }
.custom-tabs :deep(.el-tabs__header) { margin-bottom: 16px; }
.custom-tabs :deep(.el-tabs__item) { font-size: 14px; font-weight: 500; color: #6B7280; padding: 0 20px; height: 44px; line-height: 44px; }
.custom-tabs :deep(.el-tabs__item.is-active) { color: #5B9BD5; font-weight: 600; }
.custom-tabs :deep(.el-tabs__active-bar) { background: #5B9BD5; height: 3px; border-radius: 2px; }
.custom-tabs :deep(.el-tabs__nav-wrap::after) { height: 1px; background: #F0F2F5; }
.tab-label { display: flex; align-items: center; gap: 6px; }

/* ===== 工具栏 ===== */
.tab-toolbar { margin-bottom: 16px; display: flex; align-items: center; gap: 10px; }
.toolbar-hint { font-size: 13px; color: #6B7280; }

/* ===== 卡片 ===== */
.glass-card { background: #fff; border: 1px solid #F0F2F5; border-radius: 12px; padding: 20px; }
.card-head { margin-bottom: 14px; font-size: 14px; color: #4B5563; padding-bottom: 12px; border-bottom: 1px solid #F5F6F8; }
.submit-btn { margin-top: 16px; }

/* ===== 评教统计卡片 ===== */
.eval-stats { display: grid; grid-template-columns: repeat(5, 1fr); gap: 14px; margin-bottom: 20px; }
.eval-stat-card { background: #fff; border: 1px solid #EEF0F4; border-radius: 14px; padding: 18px 16px; display: flex; align-items: center; gap: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.02); transition: all .2s; }
.eval-stat-card:hover { transform: translateY(-2px); box-shadow: 0 6px 18px rgba(0,0,0,0.05); }
.eval-stat-card.overall { border-color: #D4E2F4; background: linear-gradient(135deg, #FAFCFE, #F0F5FC); }
.esc-icon { width: 42px; height: 42px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.esc-body { min-width: 0; }
.esc-label { font-size: 12px; color: #9CA3AF; margin-bottom: 4px; }
.esc-value { font-size: 26px; font-weight: 700; letter-spacing: .5px; }
.esc-value.big { font-size: 30px; }

/* ===== 表格内评分 ===== */
.score-cell { font-weight: 500; }
.score-cell.fw { font-weight: 700; }

/* ===== 选课标题 ===== */
.enroll-title { display: flex; align-items: center; width: 100%; gap: 16px; flex-wrap: wrap; }
.enroll-course { font-weight: 600; min-width: 150px; color: #1A1A2E; }
.enroll-course small { color: #9CA3AF; font-weight: 400; margin-left: 4px; }
.enroll-meta { font-size: 12px; color: #9CA3AF; }

@media (max-width: 900px) {
  .eval-stats { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 600px) {
  .eval-stats { grid-template-columns: repeat(2, 1fr); }
  .wb-tabs { padding: 4px 12px 12px; }
}
</style>
