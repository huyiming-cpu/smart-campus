<template>
  <div class="page"><h3>📝 考试管理</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="考试安排" name="exams"/>
      <el-tab-pane label="等级考试" name="level"/>
    </el-tabs>
    <div v-if="tab==='exams'">
      <el-button type="primary" size="small" @click="openExam()" style="margin-bottom:12px">新增考试</el-button>
      <el-table :data="exams" v-loading="eLoad" border size="small">
        <el-table-column prop="examName" label="考试名称" width="140"/>
        <el-table-column prop="courseName" label="课程" width="140"/>
        <el-table-column prop="semester" label="学期" width="110"/>
        <el-table-column prop="examDate" label="日期" width="110"/>
        <el-table-column label="时间" width="130"><template #default="{row}">{{ row.examTimeStart }}-{{ row.examTimeEnd }}</template></el-table-column>
        <el-table-column prop="location" label="地点" width="120"/>
        <el-table-column prop="supervisorName" label="监考教师" width="90"/>
        <el-table-column prop="studentCount" label="人数" width="60"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" @click="openExam(row)">编辑</el-button><el-button size="small" type="danger" @click="delExam(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <div v-if="tab==='level'">
      <el-button type="primary" size="small" @click="openLevel()" style="margin-bottom:12px">新增等级考试</el-button>
      <el-table :data="levels" v-loading="lLoad" border size="small">
        <el-table-column prop="examName" label="名称" width="200"/><el-table-column prop="examCode" label="代码" width="100"/>
        <el-table-column prop="category" label="类别" width="80"/><el-table-column prop="level" label="等级" width="80"/>
        <el-table-column prop="examDate" label="日期" width="110"/><el-table-column label="费用" width="80"><template #default="{row}">&yen;{{ row.fee }}</template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}"><el-select v-model="row.status" size="small" @change="updateLevelStatus(row)" style="width:100px"><el-option label="报名中" value="REGISTERING"/><el-option label="已截止" value="CLOSED"/><el-option label="已结束" value="FINISHED"/></el-select></template></el-table-column>
        <el-table-column label="操作" width="80"><template #default="{row}"><el-button size="small" type="danger" @click="delLevel(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="eDv" :title="eEdit?'编辑考试':'新增考试'" width="550px">
      <el-form :model="ef" label-width="80px">
        <el-form-item label="名称"><el-input v-model="ef.examName"/></el-form-item>
        <el-form-item label="课程ID"><el-input-number v-model="ef.courseId"/></el-form-item>
        <el-form-item label="课程名"><el-input v-model="ef.courseName"/></el-form-item>
        <el-form-item label="学期"><el-input v-model="ef.semester"/></el-form-item>
        <el-form-item label="日期"><el-input v-model="ef.examDate" type="date"/></el-form-item>
        <el-form-item label="开始时间"><el-input v-model="ef.examTimeStart"/></el-form-item>
        <el-form-item label="结束时间"><el-input v-model="ef.examTimeEnd"/></el-form-item>
        <el-form-item label="地点"><el-input v-model="ef.location"/></el-form-item>
        <el-form-item label="监考教师ID"><el-input-number v-model="ef.supervisorId"/></el-form-item>
        <el-form-item label="监考教师名"><el-input v-model="ef.supervisorName"/></el-form-item>
        <el-form-item label="人数"><el-input-number v-model="ef.studentCount"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="ef.status"><el-option label="已发布" value="PUBLISHED"/><el-option label="草稿" value="DRAFT"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="eDv=false">取消</el-button><el-button type="primary" @click="saveExam">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="lDv" title="新增等级考试" width="500px">
      <el-form :model="lf" label-width="80px">
        <el-form-item label="名称"><el-input v-model="lf.examName"/></el-form-item>
        <el-form-item label="代码"><el-input v-model="lf.examCode"/></el-form-item>
        <el-form-item label="类别"><el-input v-model="lf.category"/></el-form-item>
        <el-form-item label="等级"><el-input v-model="lf.level"/></el-form-item>
        <el-form-item label="日期"><el-input v-model="lf.examDate" type="date"/></el-form-item>
        <el-form-item label="费用"><el-input-number v-model="lf.fee" :min="0"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="lf.status"><el-option label="报名中" value="REGISTERING"/><el-option label="已截止" value="CLOSED"/></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="lDv=false">取消</el-button><el-button type="primary" @click="saveLevel">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
const tab=ref('exams'),exams=ref([]),eLoad=ref(false),levels=ref([]),lLoad=ref(false)
const eDv=ref(false),eEdit=ref(false),eEid=ref(null),ef=ref({})
const lDv=ref(false),lf=ref({})
const load=async()=>{
  eLoad.value=true;lLoad.value=true
  try{const r=await request.get('/api/student/exams',{params:{semester:'2025-2026-2'}});exams.value=r.data||[]}catch{}
  try{const r=await request.get('/api/student/level-exams');levels.value=r.data||[]}catch{}
  eLoad.value=false;lLoad.value=false
}
const openExam=(row)=>{if(row){eEdit.value=true;eEid.value=row.id;ef.value={...row}}else{eEdit.value=false;eEid.value=null;ef.value={examName:'',courseId:'',courseName:'',semester:'2025-2026-2',examDate:'',examTimeStart:'09:00',examTimeEnd:'11:00',location:'',supervisorId:'',supervisorName:'',studentCount:0,status:'PUBLISHED'}};eDv.value=true}
const saveExam=async()=>{try{if(eEdit.value)await request.put(`/api/admin/exams/${eEid.value}`,ef.value);else await request.post('/api/admin/exams',ef.value);ElMessage.success('OK');eDv.value=false;load()}catch{ElMessage.error('失败')}}
const delExam=async(id)=>{try{await request.delete(`/api/admin/exams/${id}`);ElMessage.success('已删除');load()}catch{}}
const openLevel=()=>{lf.value={examName:'',examCode:'',category:'',level:'',examDate:'',fee:0,status:'REGISTERING'};lDv.value=true}
const saveLevel=async()=>{try{await request.post('/api/admin/level-exams',lf.value);ElMessage.success('OK');lDv.value=false;load()}catch{ElMessage.error('失败')}}
const updateLevelStatus=async(row)=>{try{await request.put(`/api/admin/level-exams/${row.id}`,row)}catch{}}
const delLevel=async(id)=>{try{await request.delete(`/api/admin/level-exams/${id}`);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
