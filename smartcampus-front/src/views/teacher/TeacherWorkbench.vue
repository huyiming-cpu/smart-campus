<template>
  <div class="workbench">
    <h3>📖 教学工作台</h3>
    <el-tabs v-model="tab" @tab-change="onTabChange">
      <el-tab-pane label="任课信息" name="schedule">
        <el-select v-model="semester" @change="loadSchedule" style="width:200px;margin-bottom:16px">
          <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
        </el-select>
        <el-card v-loading="sLoading">
          <el-empty v-if="!sLoading && scheduleList.length===0" description="本学期无教学任务"/>
          <el-table v-else :data="scheduleList" border>
            <el-table-column label="周次" width="80">
              <template #default="{row}">{{ ['','周一','周二','周三','周四','周五','周六','周日'][row.weekday] }}</template>
            </el-table-column>
            <el-table-column label="节次" width="100">
              <template #default="{row}">第{{ row.sectionStart }}-{{ row.sectionEnd }}节</template>
            </el-table-column>
            <el-table-column prop="courseName" label="课程" min-width="150"/>
            <el-table-column prop="className" label="班级" width="100"/>
            <el-table-column prop="classroom" label="教室" width="120"/>
            <el-table-column prop="weeks" label="教学周" width="80"/>
            <el-table-column label="操作" width="80">
              <template #default="{row}"><el-button type="primary" size="small" @click="showStudents(row)">名单</el-button></template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="成绩录入" name="scores">
        <el-empty v-if="!scLoading && courses.length===0" description="本学期无教学任务"/>
        <template v-else>
          <el-card style="margin-bottom:16px">
            <span style="margin-right:8px">选择课程：</span>
            <el-select v-model="selectedCourseId" @change="loadScoreStudents" placeholder="请选择课程" style="width:380px">
              <el-option v-for="c in courses" :key="c.id" :label="c.courseName+' - '+c.className+' ('+['','周一','周二','周三','周四','周五'][c.weekday]+')'" :value="c.id"/>
            </el-select>
          </el-card>
          <el-card v-if="scoreStudents.length>0" v-loading="stuLoading">
            <template #header><span>学生列表（共{{ scoreStudents.length }}人）</span></template>
            <el-table :data="scoreStudents" border stripe>
              <el-table-column type="index" label="#" width="50"/>
              <el-table-column prop="studentName" label="姓名" width="80"/>
              <el-table-column prop="studentNo" label="学号" width="120"/>
              <el-table-column prop="className" label="班级" width="100"/>
              <el-table-column label="成绩" width="130">
                <template #default="{row}">
                  <el-input-number v-model="scores[row.studentCourseId||row.id]" :min="0" :max="100" :precision="1" size="small" placeholder="0-100" style="width:100px"/>
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
            <el-button type="primary" style="margin-top:16px" @click="saveAll" :loading="saving">批量保存成绩</el-button>
          </el-card>
        </template>
      </el-tab-pane>

      <el-tab-pane label="评教结果" name="evaluation">
        <el-select v-model="evalSemester" @change="loadEvals" style="width:200px;margin-bottom:16px">
          <el-option v-for="s in evalSemesters" :key="s" :label="s" :value="s"/>
        </el-select>
        <el-card class="summary-card" v-loading="evLoading">
          <template #header><span style="font-weight:bold">评分汇总</span></template>
          <el-row :gutter="20">
            <el-col :span="6" v-for="dim in dimensions" :key="dim.key">
              <div class="stat-item"><div class="stat-label">{{ dim.label }}</div><div class="stat-value" :style="{ color: scoreColor(dim.avg) }">{{ dim.avg }}</div></div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item"><div class="stat-label">综合评分</div><div class="stat-value overall" :style="{ color: scoreColor(overallAvg) }">{{ overallAvg }}</div></div>
            </el-col>
          </el-row>
        </el-card>
        <el-card v-loading="evLoading">
          <template #header><span style="font-weight:bold">评教详情</span></template>
          <el-table :data="evalList" border stripe>
            <el-table-column prop="courseName" label="课程" min-width="140"/>
            <el-table-column prop="semester" label="学期" width="120"/>
            <el-table-column label="教学态度" width="90"><template #default="{row}"><span :style="{color:scoreColor(row.teachingAttitude)}">{{row.teachingAttitude??'-'}}</span></template></el-table-column>
            <el-table-column label="教学内容" width="90"><template #default="{row}"><span :style="{color:scoreColor(row.teachingContent)}">{{row.teachingContent??'-'}}</span></template></el-table-column>
            <el-table-column label="教学方法" width="90"><template #default="{row}"><span :style="{color:scoreColor(row.teachingMethod)}">{{row.teachingMethod??'-'}}</span></template></el-table-column>
            <el-table-column label="教学效果" width="90"><template #default="{row}"><span :style="{color:scoreColor(row.teachingEffect)}">{{row.teachingEffect??'-'}}</span></template></el-table-column>
            <el-table-column label="综合" width="80"><template #default="{row}"><span :style="{color:scoreColor(row.overallScore),fontWeight:'bold'}">{{row.overallScore??'-'}}</span></template></el-table-column>
            <el-table-column prop="comment" label="评语" min-width="160" show-overflow-tooltip/>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="考试安排" name="exams">
        <el-select v-model="examSemester" @change="loadExams" style="width:200px;margin-bottom:16px">
          <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
        </el-select>
        <el-table :data="examList" v-loading="exLoading" empty-text="暂无考试安排">
          <el-table-column prop="examName" label="考试名称" min-width="140"/>
          <el-table-column prop="courseName" label="课程" min-width="120"/>
          <el-table-column prop="examDate" label="日期" width="110"/>
          <el-table-column label="时间" width="150"><template #default="{row}">{{ row.examTimeStart }}-{{ row.examTimeEnd }}</template></el-table-column>
          <el-table-column prop="location" label="地点" width="120"/>
          <el-table-column prop="supervisorName" label="监考" width="80"/>
          <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'">{{row.status}}</el-tag></template></el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="选课情况" name="enrollment">
        <el-select v-model="enrollSemester" @change="loadEnrollment" style="width:200px;margin-bottom:16px">
          <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
        </el-select>
        <el-collapse v-model="enrollActiveNames" v-loading="enLoading">
          <el-collapse-item v-for="item in enrollList" :key="item.scheduleId" :name="String(item.scheduleId)">
            <template #title>
              <div class="enroll-title">
                <span class="enroll-course">{{ item.courseName }} <small style="color:#999">({{ item.courseCode }})</small></span>
                <span class="enroll-meta">{{ item.className }} | {{ ['','周一','周二','周三','周四','周五'][item.weekday] }} {{ item.sectionStart}}-{{ item.sectionEnd }}节 | {{ item.classroom }}</span>
                <el-tag size="small" :type="item.enrolledCount >= (item.maxStudents||99) ? 'danger' : 'success'">{{ item.enrolledCount }}/{{ item.maxStudents||'不限' }}人</el-tag>
              </div>
            </template>
            <el-table :data="item.students" size="small" border v-if="item.students?.length">
              <el-table-column type="index" label="#" width="50"/>
              <el-table-column prop="studentName" label="姓名" width="80"/>
              <el-table-column prop="studentNo" label="学号" width="120"/>
              <el-table-column prop="className" label="班级" width="110"/>
              <el-table-column label="成绩" width="70"><template #default="{row}">{{ row.score??'未录入' }}</template></el-table-column>
              <el-table-column label="等级" width="60"><template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='F'?'danger':''">{{row.gradeLevel}}</el-tag></template></el-table-column>
              <el-table-column label="操作" width="70"><template #default="{row}"><el-button size="small" type="danger" @click="dropStudent(row,item)">退课</el-button></template></el-table-column>
            </el-table>
            <el-empty v-else description="暂无学生选课"/>
          </el-collapse-item>
        </el-collapse>
        <el-empty v-if="!enLoading && enrollList.length===0" description="本学期无教学任务"/>
      </el-tab-pane>
    </el-tabs>

    <!-- 学生名单弹窗 -->
    <el-dialog v-model="rosterDv" :title="'学生名单 - '+selectedCourse?.courseName" width="600px">
      <el-table :data="rosterStudents" v-loading="rosterLoading" border max-height="400">
        <el-table-column type="index" label="#" width="50"/>
        <el-table-column prop="studentName" label="姓名" width="80"/>
        <el-table-column prop="studentNo" label="学号" width="120"/>
        <el-table-column prop="className" label="班级" width="100"/>
        <el-table-column prop="department" label="学院" width="100"/>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  if (n >= 90) return '#67c23a'; if (n >= 70) return '#e6a23c'; return '#f56c6c'
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

// 首次加载默认tab
loadSchedule()
</script>

<style scoped>
.workbench { padding: 20px; max-width: 1100px; }
h3 { margin-bottom: 16px; }
.summary-card { margin-bottom: 20px; }
.stat-item { text-align: center; padding: 16px 0; }
.stat-label { font-size: 13px; color: #909399; margin-bottom: 8px; }
.stat-value { font-size: 28px; font-weight: bold; }
.stat-value.overall { font-size: 32px; }
.enroll-title{display:flex;align-items:center;width:100%;gap:16px;flex-wrap:wrap}
.enroll-course{font-weight:600;min-width:140px}
.enroll-meta{font-size:12px;color:#909399}
</style>
