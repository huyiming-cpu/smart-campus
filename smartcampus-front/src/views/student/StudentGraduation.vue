<template>
  <div class="page">
    <h3>🎓 毕业设计</h3>
    <!-- 已有毕设 -->
    <el-card v-if="design && design.id" v-loading="loading" style="margin-bottom:16px">
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
    </el-card>

    <!-- 提交/修改毕设 -->
    <el-card v-if="!design || !design.id || showForm || design.status==='REJECTED'">
      <template #header><span style="font-weight:bold">{{ design?.id ? '修改毕业设计' : '提交毕业设计选题' }}</span></template>
      <el-form :model="form" label-width="100px" style="max-width:500px">
        <el-form-item label="选择指导老师">
          <el-select v-model="form.teacherId" placeholder="请选择老师" style="width:100%" filterable>
            <el-option v-for="t in teachers" :key="t.id" :label="t.name+' - '+t.title" :value="t.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="选题名称"><el-input v-model="form.topicName" placeholder="请输入毕业设计选题名称"/></el-form-item>
        <el-form-item label="选题描述"><el-input v-model="form.topicDescription" type="textarea" :rows="4" placeholder="请描述你的毕业设计内容"/></el-form-item>
        <el-form-item><el-button type="primary" @click="submit" :loading="submitting">提交</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyDesign } from '@/api/teacher'
import request from '@/utils/request'
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
<style scoped>.page{padding:20px;max-width:900px}h3{margin-bottom:16px}</style>
