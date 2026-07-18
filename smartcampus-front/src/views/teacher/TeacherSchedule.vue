<template>
  <div style="padding:20px">
    <h3>📅 任课信息查询</h3>
    <el-select v-model="semester" @change="load" style="width:200px;margin-bottom:16px">
      <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
    </el-select>
    <el-card v-loading="loading">
      <el-empty v-if="!loading && list.length===0" description="本学期无教学任务"/>
      <el-table v-else :data="list" border>
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
    <el-dialog v-model="dv" :title="'学生名单 - '+sc?.courseName" width="600px">
      <el-table :data="students" v-loading="sl" border max-height="400">
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
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const semesters=['2025-2026-2','2025-2026-1'], semester=ref('2025-2026-2'), list=ref([]), loading=ref(false)
const dv=ref(false), students=ref([]), sl=ref(false), sc=ref(null)
const load=async()=>{
  loading.value=true
  try{const r=await request.get('/api/teacher/schedule',{params:{semester:semester.value}});list.value=r.data||[]}catch{list.value=[]}
  finally{loading.value=false}
}
const showStudents=async(row)=>{
  sc.value=row;dv.value=true;sl.value=true
  try{const r=await request.get(`/api/teacher/roster/${row.id}`);students.value=r.data||[]}catch{students.value=[]}
  finally{sl.value=false}
}
onMounted(load)
</script>
