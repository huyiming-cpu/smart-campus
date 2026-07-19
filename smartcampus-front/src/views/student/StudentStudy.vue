<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Reading /></el-icon></div>
      <div><h2 class="page-title">学习中心</h2><p class="page-desc">课表、成绩、评教、选课一站式管理</p></div>
    </div>
    <div class="card">
      <el-tabs v-model="tab" class="custom-tabs">
        <el-tab-pane label="我的课表" name="schedule"/>
        <el-tab-pane label="课程成绩" name="scores"/>
        <el-tab-pane label="网上评教" name="eval"/>
        <el-tab-pane label="选课中心" name="select"/>
      </el-tabs>

      <div v-if="tab==='schedule'">
        <div class="toolbar">
          <el-select v-model="sem" @change="loadSchedule" style="width:200px"><el-option v-for="s in sems" :key="s" :label="s" :value="s"/></el-select>
        </div>
        <div v-if="sList.length" class="timetable">
          <div class="tt-header"><div class="tt-time"></div><div v-for="d in 7" :key="d" class="tt-day">{{ wd[d-1] }}</div></div>
          <div v-for="p in [1,3,5,7,9]" :key="p" class="tt-row">
            <div class="tt-time">{{p}}-{{p+1}}节</div>
            <div v-for="d in 7" :key="d" class="tt-cell">
              <div v-for="s in sList.filter(x=>x.weekday===d&&x.sectionStart<p+2&&x.sectionEnd>=p)" :key="s.id" class="tt-item">{{s.courseName}}<br/><small>{{s.teacherName}} {{s.classroom}}</small></div>
            </div>
          </div>
        </div>
        <el-empty v-else description="暂无课表"/>
      </div>

      <div v-if="tab==='scores'">
        <el-row :gutter="16" style="margin-bottom:16px"><el-col :span="6" v-for="c in cards" :key="c.label">
          <div class="stat-card"><div class="stat-num" :style="{color:c.color}">{{ c.val }}</div><div class="stat-label">{{ c.label }}</div></div>
        </el-col></el-row>
        <div class="toolbar">
          <el-select v-model="sSem" @change="loadScores" style="width:200px"><el-option v-for="s in sems" :key="s" :label="s" :value="s"/><el-option label="全部学期" value=""/></el-select>
        </div>
        <el-table :data="scores" v-loading="sLoad" stripe size="small">
          <el-table-column prop="courseName" label="课程" min-width="150"/><el-table-column prop="courseCode" label="代码" width="90"/>
          <el-table-column label="教师" width="100"><template #default="{row}">{{row.teacherName||''}}</template></el-table-column>
          <el-table-column prop="className" label="班级" min-width="100"/>
          <el-table-column prop="score" label="成绩" width="70"/><el-table-column label="等级" width="60"><template #default="{row}"><el-tag size="small" :type="row.gradeLevel==='A'?'success':row.gradeLevel==='F'?'danger':''">{{row.gradeLevel}}</el-tag></template></el-table-column>
          <el-table-column prop="semester" label="学期" width="120"/>
        </el-table>
      </div>

      <div v-if="tab==='eval'">
        <el-table :data="evals" v-loading="eLoad" stripe size="small">
          <el-table-column prop="courseName" label="课程" min-width="150"/><el-table-column prop="teacherName" label="教师" width="100"/>
          <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.evaluated?'success':'warning'" size="small" border>{{row.evaluated?'已评教':'待评教'}}</el-tag></template></el-table-column>
          <el-table-column label="操作" width="120"><template #default="{row}"><el-button v-if="!row.evaluated" size="small" type="primary" @click="openEval(row)"><el-icon :size="14" style="margin-right:4px"><Star /></el-icon>去评教</el-button><span v-else style="color:#10B981;font-weight:600">{{row.overallScore||'-'}} 分</span></template></el-table-column>
        </el-table>
      </div>

      <div v-if="tab==='select'">
        <el-alert v-if="!selectOpen" title="选课暂未开放" description="请联系管理员开启选课功能" type="info" show-icon style="margin-bottom:12px"/>
        <div v-else>
          <el-input v-model="searchKey" placeholder="搜索课程/教师名称" clearable style="width:300px;margin-bottom:12px"/>
          <el-table :data="filteredCourses" v-loading="cLoad" stripe size="small">
          <el-table-column prop="courseName" label="课程" min-width="150"/><el-table-column label="代码" width="90"><template #default="{row}">{{row.courseCode||''}}</template></el-table-column>
          <el-table-column prop="teacherName" label="教师" width="100"/><el-table-column prop="className" label="班级" min-width="100"/>
          <el-table-column label="时间" min-width="130"><template #default="{row}">{{['','周一','周二','周三','周四','周五'][row.weekday]}} {{row.sectionStart}}-{{row.sectionEnd}}节</template></el-table-column>
          <el-table-column prop="classroom" label="教室" min-width="110"/>
          <el-table-column label="操作" width="130" fixed="right"><template #default="{row}"><el-button v-if="row.enrolled" size="small" type="success" @click="doDrop(row)"><el-icon :size="14" style="margin-right:4px"><Check /></el-icon>已选</el-button><el-button v-else size="small" type="primary" @click="doSelect(row)"><el-icon :size="14" style="margin-right:4px"><Plus /></el-icon>选课</el-button></template></el-table-column>
        </el-table>
        </div>
      </div>
    </div>

    <el-dialog v-model="eDv" title="教学评价" width="500px">
      <el-form :model="ef" label-width="80px" v-if="ec">
        <el-form-item label="课程"><strong>{{ec.courseName}}</strong></el-form-item>
        <el-form-item label="教师">{{ec.teacherName}}</el-form-item>
        <el-form-item v-for="d in dims" :key="d.k" :label="d.l"><el-rate v-model="ef[d.k]" :max="5" show-score/></el-form-item>
        <el-form-item label="评语"><el-input v-model="ef.comment" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="eDv=false">取消</el-button><el-button type="primary" @click="doEval" :loading="eSav">提交</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Reading, Star, Plus, Check } from '@element-plus/icons-vue'
const tab=ref('schedule'),sem=ref('2025-2026-2'),sSem=ref('2025-2026-2'),sems=['2025-2026-2','2025-2026-1'],wd=['周一','周二','周三','周四','周五','周六','周日']
const sList=ref([]),scores=ref([]),sLoad=ref(false),evals=ref([]),eLoad=ref(false)
const overview=ref({}),oLoad=ref(false)
const cards=ref([]),eDv=ref(false),eSav=ref(false),ec=ref(null)
const dims=[{k:'teachingAttitude',l:'教学态度'},{k:'teachingContent',l:'教学内容'},{k:'teachingMethod',l:'教学方法'},{k:'teachingEffect',l:'教学效果'}]
const ef=ref({teachingAttitude:5,teachingContent:5,teachingMethod:5,teachingEffect:5,comment:''})

const loadSchedule=async()=>{try{const r=await request.get('/api/student/schedule',{params:{semester:sem.value}});sList.value=r.data||[]}catch{}}
const loadOverview=async()=>{oLoad.value=true;try{const r=await request.get('/api/student/course-overview');const d=r.data||{};cards.value=[{label:'全部课程',val:d.totalCourses||0,color:'#303133'},{label:'已通过',val:d.passed||0,color:'#67c23a'},{label:'未通过',val:d.failed||0,color:'#f56c6c'},{label:'平均分',val:d.avgScore||0,color:'#409eff'}]}catch{}finally{oLoad.value=false}}
const loadScores=async()=>{sLoad.value=true;try{const r=await request.get('/api/student/scores',{params:{semester:sSem.value}});scores.value=r.data||[]}catch{}finally{sLoad.value=false}}
const loadEvals=async()=>{eLoad.value=true;try{const r=await request.get('/api/student/evaluations',{params:{semester:'2025-2026-2'}});evals.value=r.data||[]}catch{}finally{eLoad.value=false}}
const openEval=(row)=>{ec.value=row;ef.value={teachingAttitude:5,teachingContent:5,teachingMethod:5,teachingEffect:5,comment:''};eDv.value=true}
const doEval=async()=>{eSav.value=true;try{await request.post('/api/student/evaluations',{courseId:ec.value.courseId,teacherId:ec.value.teacherId,scores:ef.value,comment:ef.value.comment});ElMessage.success('评教成功');eDv.value=false;loadEvals()}catch{ElMessage.error('失败')}finally{eSav.value=false}}
const selectOpen=ref(false),availCourses=ref([]),cLoad=ref(false),enrolled=ref([])
const checkSelectOpen=async()=>{try{const r=await request.get('/api/course-select/status');selectOpen.value=r.data}catch{}}
onMounted(()=>{checkSelectOpen();loadSchedule();loadOverview();loadScores();loadEvals();loadCourses()})
const loadCourses=async()=>{
  cLoad.value=true
  try{
    const [sr,er]=await Promise.all([
      request.get('/api/admin/schedules',{params:{semester:'2025-2026-2'}}),
      request.get('/api/student/scores',{params:{semester:'2025-2026-2'}})
    ])
    const schedules=sr.data||[];enrolled.value=er.data||[]
    const enrolledIds=new Set(enrolled.value.filter(e=>e.status!=='DROPPED').map(e=>e.scheduleId))
    availCourses.value=schedules.map(s=>({...s,enrolled:enrolledIds.has(s.id)}))
  }catch{}
  finally{cLoad.value=false}
}
const userStore=useUserStore()
const doSelect=async(row)=>{try{const uid=userStore.userInfo?.userId||userStore.userInfo?.id;await request.post('/api/admin/enroll',{studentId:uid,scheduleId:row.id,semester:'2025-2026-2'});ElMessage.success('选课成功');loadCourses();loadSchedule()}catch(e){ElMessage.error(e.response?.data?.message||'选课失败')}}

const searchKey=ref('')
const filteredCourses=computed(()=>{
  if(!searchKey.value)return availCourses.value
  const kw=searchKey.value.toLowerCase()
  return availCourses.value.filter(c=>(c.courseName||'').toLowerCase().includes(kw)||(c.teacherName||'').toLowerCase().includes(kw))
})
const doDrop=async(row)=>{
  try{
    const enrolledItem=enrolled.value.find(e=>e.scheduleId===row.id)
    if(!enrolledItem){ElMessage.error('未找到选课记录');return}
    await ElMessageBox.confirm(`确定要退选「${row.courseName}」吗？`,'确认退课',{confirmButtonText:'确定退课',cancelButtonText:'取消',type:'warning'})
    await request.put(`/api/admin/student-courses/${enrolledItem.studentCourseId||enrolledItem.id}/drop`)
    ElMessage.success('退课成功')
    row.enrolled=false
    loadCourses();loadSchedule();loadScores()
  }catch(e){if(e!=='cancel')ElMessage.error('退课失败: '+(e.response?.data?.msg||e.message))}
}
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.custom-tabs :deep(.el-tabs__header){margin-bottom:16px}
.custom-tabs :deep(.el-tabs__item){font-size:14px;font-weight:500;color:#6B7280}
.custom-tabs :deep(.el-tabs__item.is-active){color:#5B9BD5;font-weight:600}
.custom-tabs :deep(.el-tabs__active-bar){background:#5B9BD5}
.stat-card{background:#fafbfc;border:1px solid #EEF0F4;border-radius:10px;padding:16px;text-align:center}
.stat-num{font-size:28px;font-weight:bold}
.stat-label{font-size:13px;color:#909399;margin-top:4px}
.timetable{border:1px solid #e4e7ed}.tt-header,.tt-row{display:flex}.tt-header{background:#f5f7fa;font-weight:bold}
.tt-time{width:60px;padding:8px;text-align:center;border-right:1px solid #e4e7ed;font-size:12px}
.tt-day{flex:1;padding:10px;text-align:center;border-right:1px solid #e4e7ed;font-size:13px}
.tt-cell{flex:1;min-height:70px;padding:3px;border-right:1px solid #e4e7ed;border-bottom:1px solid #e4e7ed}
.tt-item{background:#ecf5ff;border-left:3px solid #409eff;border-radius:3px;padding:3px 6px;margin:1px 0;font-size:11px;line-height:1.4}
</style>
