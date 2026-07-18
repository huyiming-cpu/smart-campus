<template>
  <div class="page"><h3>👥 班级名单</h3>
    <el-select v-model="sid" @change="load" style="width:380px;margin-bottom:16px" placeholder="选择课程">
      <el-option v-for="s in schedules" :key="s.id" :label="(s.courseName||'')+' - '+(s.className||'')+' ('+wd(s.weekday)+')'" :value="s.id"/>
    </el-select>
    <el-table :data="roster" v-loading="loading" empty-text="请选择课程">
      <el-table-column type="index" label="#" width="50"/>
      <el-table-column prop="studentName" label="姓名" width="100"/>
      <el-table-column prop="studentNo" label="学号" width="130"/>
      <el-table-column prop="className" label="班级" width="120"/>
      <el-table-column prop="department" label="学院" width="120"/>
      <el-table-column label="成绩" width="80"><template #default="{row}">{{ row.score||'未录入' }}</template></el-table-column>
      <el-table-column label="等级" width="70"><template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='A'?'success':row.gradeLevel==='F'?'danger':''">{{ row.gradeLevel }}</el-tag></template></el-table-column>
      <el-table-column prop="examType" label="考试类型" width="90"/>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getTeacherSchedule, getClassRoster } from '@/api/course'
const loading=ref(false), schedules=ref([]), sid=ref(null), roster=ref([])
const wd=(d)=>['','周一','周二','周三','周四','周五','周六','周日'][d]||''
onMounted(async()=>{const r=await getTeacherSchedule('2025-2026-2');schedules.value=r.data||[]})
const load=async()=>{if(!sid.value)return;loading.value=true;try{const r=await getClassRoster(sid.value);roster.value=r.data||[]}catch{}finally{loading.value=false}}
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
