<template>
  <div class="page">
    <h3>📝 考试成绩录入</h3>
    <el-empty v-if="!loading && courses.length===0" description="本学期无教学任务，无需录入成绩"/>
    <template v-else>
      <el-card style="margin-bottom:16px">
        <span style="margin-right:8px">选择课程：</span>
        <el-select v-model="sid" @change="loadStudents" placeholder="请选择课程" style="width:380px">
          <el-option v-for="c in courses" :key="c.id" :label="c.courseName+' - '+c.className+' ('+['','周一','周二','周三','周四','周五'][c.weekday]+')'" :value="c.id"/>
        </el-select>
      </el-card>
      <el-card v-if="students.length>0" v-loading="stuLoading">
        <template #header><span>学生列表（共{{ students.length }}人）</span></template>
        <el-table :data="students" border stripe>
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
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const loading=ref(false), courses=ref([]), sid=ref(null), students=ref([]), stuLoading=ref(false), saving=ref(false)
const scores=reactive({}), grades=reactive({}), types=reactive({})

const loadCourses=async()=>{
  loading.value=true
  try{const r=await request.get('/api/teacher/schedule',{params:{semester:'2025-2026-2'}});courses.value=r.data||[]}
  catch{}
  finally{loading.value=false}
}
const loadStudents=async()=>{
  if(!sid.value)return;stuLoading.value=true
  try{
    const r=await request.get(`/api/teacher/roster/${sid.value}`)
    students.value=(r.data||[]).map(s=>{const k=s.studentCourseId||s.id;scores[k]=s.score;grades[k]=s.gradeLevel||'A';types[k]=s.examType||'FINAL';return s})
  }catch{}
  finally{stuLoading.value=false}
}
const saveAll=async()=>{
  saving.value=true;let ok=0
  for(const s of students.value){
    const k=s.studentCourseId||s.id
    if(scores[k]===null||scores[k]===undefined)continue
    try{await request.put(`/api/teacher/scores/${k}`,{score:String(scores[k]),gradeLevel:grades[k]||'A',examType:types[k]||'FINAL',operatorName:'教师'});ok++}catch{}
  }
  ElMessage.success(`保存完成：${ok}/${students.value.length}`)
  saving.value=false
}
onMounted(loadCourses)
</script>
<style scoped>.page{padding:20px;max-width:1000px}h3{margin-bottom:16px}</style>
