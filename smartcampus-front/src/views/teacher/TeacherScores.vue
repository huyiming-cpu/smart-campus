<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><EditPen /></el-icon></div>
      <div><h2 class="page-title">考试成绩录入</h2><p class="page-desc">录入与批量保存学生成绩、等级及考试类型</p></div>
    </div>
    <el-empty v-if="!loading && courses.length===0" description="本学期无教学任务，无需录入成绩"/>
    <template v-else>
      <div class="toolbar">
        <span class="toolbar-label">选择课程：</span>
        <el-select v-model="selectedCourseId" @change="loadStudents" placeholder="请选择课程" style="width:420px">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName+' - '+c.className+' ('+wd(c.weekday)+')'" :value="c.id"/>
        </el-select>
      </div>
      <div class="card" v-if="students.length>0" v-loading="sLoading">
        <div class="card-head">学生列表 · 共 <b>{{ students.length }}</b> 人</div>
        <el-table :data="students" border stripe>
          <el-table-column type="index" label="#" width="50"/>
          <el-table-column prop="studentName" label="姓名" width="90"/>
          <el-table-column prop="studentNo" label="学号" width="140"/>
          <el-table-column prop="className" label="班级" width="110"/>
          <el-table-column label="成绩" width="140"><template #default="{row}"><el-input-number v-model="scores[row.studentCourseId||row.id]" :min="0" :max="100" :precision="1" size="small" placeholder="0-100" style="width:110px"/></template></el-table-column>
          <el-table-column label="等级" width="110"><template #default="{row}"><el-select v-model="grades[row.studentCourseId||row.id]" size="small" style="width:85px"><el-option v-for="g in ['A','B','C','D','F']" :key="g" :label="g" :value="g"/></el-select></template></el-table-column>
          <el-table-column label="考试类型" width="130"><template #default="{row}"><el-select v-model="types[row.studentCourseId||row.id]" size="small" style="width:105px"><el-option label="期末" value="FINAL"/><el-option label="补考" value="MAKEUP"/><el-option label="重修" value="RETAKE"/></el-select></template></el-table-column>
        </el-table>
        <el-button type="primary" class="submit-btn" @click="saveAll" :loading="saving"><el-icon :size="16"><Upload /></el-icon> 批量保存成绩</el-button>
      </div>
    </template>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { EditPen, Upload } from '@element-plus/icons-vue'
const loading=ref(false), courses=ref([]), selectedCourseId=ref(null), students=ref([]), sLoading=ref(false), saving=ref(false)
const scores=reactive({}), grades=reactive({}), types=reactive({})
const wd=(d)=>['','周一','周二','周三','周四','周五','周六','周日'][d]||''
const loadCourses=async()=>{loading.value=true;try{const r=await request.get('/api/teacher/schedule',{params:{semester:'2025-2026-2'}});courses.value=r.data||[]}catch{}finally{loading.value=false}}
const loadStudents=async()=>{if(!selectedCourseId.value)return;sLoading.value=true;try{const r=await request.get(`/api/teacher/roster/${selectedCourseId.value}`);students.value=(r.data||[]).map(s=>{const k=s.studentCourseId||s.id;scores[k]=s.score;grades[k]=s.gradeLevel||'A';types[k]=s.examType||'FINAL';return s})}catch{}finally{sLoading.value=false}}
const saveAll=async()=>{saving.value=true;let ok=0;for(const s of students.value){const k=s.studentCourseId||s.id;if(scores[k]===null||scores[k]===undefined)continue;try{await request.put(`/api/teacher/scores/${k}`,{score:String(scores[k]),gradeLevel:grades[k]||'A',examType:types[k]||'FINAL',operatorName:'教师'});ok++}catch{}}ElMessage.success(`保存完成：${ok}/${students.value.length}`);saving.value=false}
loadCourses()
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;align-items:center;gap:10px}
.toolbar-label{font-size:13px;color:#6B7280}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.card-head{margin-bottom:14px;font-size:14px;color:#4B5563;padding-bottom:12px;border-bottom:1px solid #F5F6F8}
.submit-btn{margin-top:16px}
</style>
