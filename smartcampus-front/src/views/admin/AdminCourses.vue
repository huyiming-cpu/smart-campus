<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Reading /></el-icon></div>
      <div><h2 class="page-title">课程综合管理</h2><p class="page-desc">管理课程信息、排课分配与学生选课</p></div>
    </div>
    <div class="toolbar">
      <div style="margin-bottom:12px">选课开关：<el-switch v-model="selectOpen" @change="onToggleSelect" active-text="开启" inactive-text="关闭"/></div>
      <el-tabs v-model="tab">
        <el-tab-pane label="课程列表" name="courses"/>
        <el-tab-pane label="排课分配" name="schedules"/>
        <el-tab-pane label="学生选课" name="enrollment"/>
      </el-tabs>
    </div>
    <!-- 课程 -->
    <div class="card" v-if="tab==='courses'" v-loading="cLoading">
      <el-button type="primary" size="small" @click="openCourse()" style="margin-bottom:12px">新增课程</el-button>
      <el-table :data="courses" stripe border size="small">
        <el-table-column prop="courseCode" label="代码" width="80"/><el-table-column prop="courseName" label="名称" min-width="150"/>
        <el-table-column prop="credit" label="学分" width="60"/><el-table-column prop="hours" label="学时" width="60"/>
        <el-table-column prop="courseType" label="类型" width="70"/><el-table-column prop="department" label="学院" width="100"/>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openCourse(row)">编辑</el-button><el-button size="small" type="danger" plain @click="delCourse(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <!-- 排课 -->
    <div class="card" v-if="tab==='schedules'" v-loading="sLoading">
      <el-button type="primary" size="small" @click="openSchedule()" style="margin-bottom:12px">新增排课</el-button>
      <el-table :data="schedules" stripe border size="small">
        <el-table-column label="课程" min-width="180"><template #default="{row}">{{ row.courseName||getCourseName(row.courseId) }}<br/><small style="color:#999">{{ getCourseCode(row.courseId) }}</small></template></el-table-column>
        <el-table-column label="教师" width="100"><template #default="{row}">{{ row.teacherName||getTeacherName(row.teacherId) }}</template></el-table-column>
        <el-table-column prop="semester" label="学期" width="120"/><el-table-column prop="className" label="班级" min-width="120"/>
        <el-table-column label="时间" min-width="140"><template #default="{row}">{{ ['','周一','周二','周三','周四','周五'][row.weekday] }} {{ row.sectionStart }}-{{ row.sectionEnd }}节</template></el-table-column>
        <el-table-column prop="classroom" label="教室" min-width="120"/>
        <el-table-column label="操作" width="100" fixed="right"><template #default="{row}"><el-button size="small" type="danger" plain @click="delSchedule(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <!-- 选课 -->
    <div class="card" v-if="tab==='enrollment'" v-loading="eLoading">
      <el-button type="primary" size="small" @click="enrollDv=true" style="margin-bottom:12px">为学生选课</el-button>
      <el-table :data="enrollments" stripe border size="small">
        <el-table-column label="学生" min-width="120"><template #default="{row}">{{ getName(row.studentId) }}</template></el-table-column>
        <el-table-column prop="scheduleId" label="排课ID" width="100"/><el-table-column prop="semester" label="学期" min-width="130"/>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag size="small">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column label="成绩" width="80"><template #default="{row}">{{ row.score ?? '-' }}</template></el-table-column>
        <el-table-column label="操作" width="100" fixed="right"><template #default="{row}"><el-button v-if="row.status==='ENROLLED'" size="small" type="danger" plain @click="drop(row.id)">退课</el-button></template></el-table-column>
      </el-table>
    </div>

    <!-- 课程弹窗 -->
    <el-dialog v-model="cDv" :title="cEdit?'编辑课程':'新增课程'" width="500px">
      <el-form :model="cf" label-width="80px">
        <el-form-item label="代码"><el-input v-model="cf.courseCode"/></el-form-item>
        <el-form-item label="名称"><el-input v-model="cf.courseName"/></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="cf.credit" :min="0" :step="0.5"/></el-form-item>
        <el-form-item label="学时"><el-input-number v-model="cf.hours" :min="0"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="cf.courseType" style="width:100%"><el-option label="必修" value="REQUIRED"/><el-option label="选修" value="ELECTIVE"/></el-select></el-form-item>
        <el-form-item label="学院"><el-input v-model="cf.department"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="cDv=false">取消</el-button><el-button type="primary" @click="saveCourse">保存</el-button></template>
    </el-dialog>

    <!-- 排课弹窗（下拉版） -->
    <el-dialog v-model="sDv" title="新增排课" width="550px">
      <el-form :model="sf" label-width="80px">
        <el-form-item label="课程"><el-select v-model="sf.courseId" style="width:100%" filterable><el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id"/></el-select></el-form-item>
        <el-form-item label="教师"><el-select v-model="sf.teacherId" style="width:100%" filterable><el-option v-for="t in teachers" :key="t.id" :label="t.name+' ('+(t.department||'')+')'" :value="t.id"/></el-select></el-form-item>
        <el-form-item label="学期"><el-input v-model="sf.semester"/></el-form-item>
        <el-form-item label="班级"><el-select v-model="sf.className" style="width:100%" filterable allow-create><el-option v-for="c in classes" :key="c" :label="c" :value="c"/></el-select></el-form-item>
        <el-form-item label="周次"><el-select v-model="sf.weekday" style="width:100%"><el-option v-for="i in 7" :key="i" :label="['','周一','周二','周三','周四','周五','周六','周日'][i]" :value="i"/></el-select></el-form-item>
        <el-form-item label="开始节"><el-select v-model="sf.sectionStart" style="width:100%"><el-option v-for="i in 12" :key="i" :label="'第'+i+'节'" :value="i"/></el-select></el-form-item>
        <el-form-item label="结束节"><el-select v-model="sf.sectionEnd" style="width:100%"><el-option v-for="i in 12" :key="i" :label="'第'+i+'节'" :value="i"/></el-select></el-form-item>
        <el-form-item label="教室"><el-select v-model="sf.classroom" style="width:100%" filterable><el-option v-for="r in rooms" :key="r.roomName" :label="r.building+' '+r.roomName" :value="r.roomName"/></el-select></el-form-item>
        <el-form-item label="校区"><el-input v-model="sf.campus"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="sDv=false">取消</el-button><el-button type="primary" @click="saveSchedule">保存</el-button></template>
    </el-dialog>

    <!-- 选课弹窗 -->
    <el-dialog v-model="enrollDv" title="为学生选课" width="350px">
      <el-form :model="ef" label-width="80px">
        <el-form-item label="学生ID"><el-input-number v-model="ef.studentId"/></el-form-item>
        <el-form-item label="排课ID"><el-input-number v-model="ef.scheduleId"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="ef.semester"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="enrollDv=false">取消</el-button><el-button type="primary" @click="doEnroll">确认选课</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Reading } from '@element-plus/icons-vue'
const tab=ref('courses'),courses=ref([]),cLoading=ref(false),schedules=ref([]),sLoading=ref(false),enrollments=ref([]),eLoading=ref(false)
const users=ref([]),teachers=ref([]),classes=ref([]),rooms=ref([])
const getName=(id)=>{const u=users.value.find(x=>x.id===id);return u?u.name:id}
const getCourseName=(id)=>{const c=courses.value.find(x=>x.id===id);return c?c.courseName:id}
const getCourseCode=(id)=>{const c=courses.value.find(x=>x.id===id);return c?c.courseCode:''}
const getTeacherName=(id)=>{const t=teachers.value.find(x=>x.id===id);return t?t.name:id}
const cDv=ref(false),cEdit=ref(false),cEid=ref(null),cf=ref({courseCode:'',courseName:'',credit:3,hours:48,courseType:'REQUIRED',department:'计算机学院'})
const sDv=ref(false),sf=ref({courseId:'',teacherId:'',semester:'2025-2026-2',className:'',weekday:1,sectionStart:1,sectionEnd:2,classroom:'',campus:'主校区',weeks:'1-16',maxStudents:60,currentStudents:0,status:'OPEN'})
const enrollDv=ref(false),ef=ref({studentId:'',scheduleId:'',semester:'2025-2026-2'})

const selectOpen=ref(true)
const loadSelectStatus=async()=>{try{const r=await request.get('/api/course-select/status');selectOpen.value=r.data}catch{}}
const onToggleSelect=(val)=>{request.put('/api/course-select/toggle',{open:val}).catch(()=>{})}

const loadAll=async()=>{
  await loadSelectStatus()
  try{const r=await request.get('/api/admin/users');users.value=r.data||[]}catch{}
  try{const r=await request.get('/api/admin/teachers');teachers.value=r.data||[]}catch{}
  try{const r=await request.get('/api/admin/classes');classes.value=r.data||[]}catch{}
  try{const r=await request.get('/api/admin/classrooms');rooms.value=r.data||[]}catch{}
  loadCourses();loadSchedules();loadEnrollments()
}
const loadCourses=async()=>{cLoading.value=true;try{const r=await request.get('/api/admin/courses');courses.value=r.data||[]}catch{}finally{cLoading.value=false}}
const loadSchedules=async()=>{sLoading.value=true;try{const r=await request.get('/api/admin/schedules');schedules.value=r.data||[]}catch{}finally{sLoading.value=false}}
const loadEnrollments=async()=>{eLoading.value=true;try{const r=await request.get('/api/admin/student-courses');enrollments.value=r.data||[]}catch{}finally{eLoading.value=false}}
const openCourse=(row)=>{if(row){cEdit.value=true;cEid.value=row.id;cf.value={...row}}else{cEdit.value=false;cEid.value=null;cf.value={courseCode:'',courseName:'',credit:3,hours:48,courseType:'REQUIRED',department:'计算机学院'}};cDv.value=true}
const saveCourse=async()=>{try{if(cEdit.value)await request.put(`/api/admin/courses/${cEid.value}`,cf.value);else await request.post('/api/admin/courses',cf.value);ElMessage.success('OK');cDv.value=false;loadCourses()}catch{ElMessage.error('失败')}}
const delCourse=async(id)=>{try{await request.delete(`/api/admin/courses/${id}`);ElMessage.success('已删除');loadCourses()}catch{}}
const openSchedule=()=>{sf.value={courseId:'',teacherId:'',semester:'2025-2026-2',className:'',weekday:1,sectionStart:1,sectionEnd:2,classroom:'',campus:'主校区',weeks:'1-16',maxStudents:60,currentStudents:0,status:'OPEN'};sDv.value=true}
const saveSchedule=async()=>{try{await request.post('/api/admin/schedules',sf.value);ElMessage.success('OK');sDv.value=false;loadSchedules()}catch{ElMessage.error('失败')}}
const delSchedule=async(id)=>{try{await request.delete(`/api/admin/schedules/${id}`);ElMessage.success('已删除');loadSchedules()}catch{}}
const doEnroll=async()=>{try{await request.post('/api/admin/enroll',ef.value);ElMessage.success('OK');enrollDv.value=false;loadEnrollments()}catch{ElMessage.error('失败')}}
const drop=async(id)=>{try{await request.put(`/api/admin/student-courses/${id}/drop`);ElMessage.success('已退课');loadEnrollments()}catch{}}
onMounted(loadAll)
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
