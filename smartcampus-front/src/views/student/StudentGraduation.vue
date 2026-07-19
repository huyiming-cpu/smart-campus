<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><School /></el-icon></div>
      <div><h2 class="page-title">毕业设计</h2><p class="page-desc">毕设选题、指导老师分配与进度跟踪</p></div>
    </div>

    <div class="card" v-loading="loading" v-if="design && design.id && design.status!=='REJECTED' && !showForm" style="margin-bottom:16px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="状态"><el-tag :type="design.status==='APPROVED'||design.status==='IN_PROGRESS'?'success':design.status==='PENDING'?'warning':design.status==='REJECTED'?'danger':'info'">{{ design.status||'未知' }}</el-tag></el-descriptions-item>
        <el-descriptions-item label="指导老师">{{ design.teacherName||'未分配' }}</el-descriptions-item>
        <el-descriptions-item label="选题名称" :span="2">{{ design.topicName||'待提交' }}</el-descriptions-item>
        <el-descriptions-item label="选题描述" :span="2">{{ design.topicDescription||'无' }}</el-descriptions-item>
        <el-descriptions-item label="教师评语" :span="2">{{ design.teacherComment||'暂无' }}</el-descriptions-item>
        <el-descriptions-item label="成绩">{{ design.score||'未评定' }}</el-descriptions-item>
        <el-descriptions-item label="答辩时间">{{ design.defenseTime||'未安排' }}</el-descriptions-item>
        <el-descriptions-item label="公告" :span="2" v-if="design.announcement"><div style="white-space:pre-wrap">{{ design.announcement }}</div></el-descriptions-item>
      </el-descriptions>
      <el-button v-if="design.status==='REJECTED'" type="primary" style="margin-top:12px" @click="showForm=true">重新提交</el-button>
    </div>

    <div class="card" v-if="!design || !design.id || showForm || design.status==='REJECTED'">
      <div class="section-title">{{ design?.id ? '修改毕业设计' : '提交毕业设计选题' }}</div>
      <el-form :model="form" label-width="100px" style="max-width:500px;margin-top:16px">
        <el-form-item label="选择指导老师">
          <el-select v-model="form.teacherId" placeholder="请选择老师" style="width:100%" filterable>
            <el-option v-for="t in teachers" :key="t.id" :label="t.name+' - '+t.title" :value="t.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="选题名称"><el-input v-model="form.topicName" placeholder="请输入毕业设计选题名称"/></el-form-item>
        <el-form-item label="选题描述"><el-input v-model="form.topicDescription" type="textarea" :rows="4" placeholder="请描述你的毕业设计内容"/></el-form-item>
        <el-form-item><el-button type="primary" @click="submit" :loading="submitting">提交</el-button></el-form-item>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyDesign } from '@/api/teacher'
import request from '@/utils/request'
import { School } from '@element-plus/icons-vue'
const loading=ref(false), design=ref(null), showForm=ref(false), submitting=ref(false), teachers=ref([])
const form=reactive({teacherId:'', topicName:'', topicDescription:''})

const load=async()=>{
  loading.value=true
  try{const r=await getMyDesign();design.value=r.data;if(r.data){form.teacherId=r.data.teacherId||'';form.topicName=r.data.topicName||'';form.topicDescription=r.data.topicDescription||''}}
  catch{}
  finally{loading.value=false}
}
const loadTeachers=async()=>{
  try{const r=await request.get('/api/student/teachers');teachers.value=r.data||[]}
  catch{}
}
const submit=async()=>{
  if(!form.teacherId||!form.topicName){ElMessage.warning('请填写完整信息');return}
  submitting.value=true
  try{await request.post('/api/student/graduation-design',form);ElMessage.success('提交成功');showForm.value=false;load()}
  catch(e){ElMessage.error(e.response?.data?.message||'提交失败')}
  finally{submitting.value=false}
}
onMounted(()=>{load();loadTeachers()})
</script>
<style scoped>
.page{padding:20px 24px;max-width:900px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.section-title{font-weight:700;font-size:15px;color:#1A1A2E;padding-bottom:12px;border-bottom:1px solid #EEF0F4;margin-bottom:4px}
</style>
