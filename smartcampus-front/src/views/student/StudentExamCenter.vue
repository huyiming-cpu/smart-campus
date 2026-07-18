<template>
  <div class="page"><h3>📝 考试中心</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="考试安排" name="exams"/>
      <el-tab-pane label="补考重修" name="retake"/>
      <el-tab-pane label="等级考试" name="level"/>
    </el-tabs>
    <div v-if="tab==='exams'">
      <el-table :data="exams" v-loading="eLoad" size="small">
        <el-table-column prop="examName" label="考试" width="140"/><el-table-column prop="courseName" label="课程" width="140"/>
        <el-table-column prop="examDate" label="日期" width="110"/><el-table-column label="时间" width="130"><template #default="{row}">{{row.examTimeStart}}-{{row.examTimeEnd}}</template></el-table-column>
        <el-table-column prop="location" label="地点" width="120"/><el-table-column prop="supervisorName" label="监考" width="80"/>
      </el-table>
    </div>
    <div v-if="tab==='retake'">
      <el-table :data="retakes" v-loading="rLoad" size="small">
        <el-table-column prop="courseName" label="课程" width="150"/><el-table-column prop="originalScore" label="原成绩" width="80"/>
        <el-table-column prop="type" label="类型" width="80"/><el-table-column label="状态" width="90"><template #default="{row}"><el-tag size="small">{{row.status||'未申请'}}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{row}"><el-button v-if="!row.status" size="small" type="primary" @click="applyRetake(row)">申请补考</el-button></template></el-table-column>
      </el-table>
    </div>
    <div v-if="tab==='level'">
      <el-table :data="levelExams" v-loading="lLoad" size="small">
        <el-table-column prop="examName" label="考试名称" width="200"/><el-table-column prop="examDate" label="日期" width="110"/>
        <el-table-column label="费用" width="80"><template #default="{row}">&yen;{{row.fee}}</template></el-table-column>
        <el-table-column label="操作" width="100"><template #default="{row}"><el-button v-if="!row.registered" size="small" type="primary" @click="regLevel(row)">报名</el-button><el-tag v-else type="success" size="small">已报名</el-tag></template></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const tab=ref('exams'),exams=ref([]),eLoad=ref(false),retakes=ref([]),rLoad=ref(false),levelExams=ref([]),lLoad=ref(false)
const load=async()=>{
  try{const r=await request.get('/api/student/exams',{params:{semester:'2025-2026-2'}});exams.value=r.data||[]}catch{}
  try{const r=await request.get('/api/student/retakes');retakes.value=r.data||[]}catch{}
  try{const r1=await request.get('/api/student/level-exams');const r2=await request.get('/api/student/level-exams/my');const regged=new Set((r2.data||[]).map(x=>x.examId));levelExams.value=(r1.data||[]).map(e=>({...e,registered:regged.has(e.id)}))}catch{}
}
const applyRetake=async(row)=>{try{await request.post('/api/student/retakes',{courseId:row.courseId,semester:'2025-2026-2',type:'MAKEUP',originalScore:row.originalScore||0});ElMessage.success('申请成功');load()}catch{ElMessage.error('申请失败')}}
const regLevel=async(row)=>{try{await request.post('/api/student/level-exams/register',{examId:row.id});ElMessage.success('报名成功');load()}catch{ElMessage.error('报名失败')}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
