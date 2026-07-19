<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Calendar /></el-icon></div>
      <div><h2 class="page-title">任课信息查询</h2><p class="page-desc">查看本学期教学安排与班级名单</p></div>
    </div>
    <div class="toolbar"><el-select v-model="semester" @change="load" style="width:200px"><el-option v-for="s in semesters" :key="s" :label="s" :value="s"/></el-select></div>
    <div class="card" v-loading="loading">
      <el-empty v-if="!loading && list.length===0" description="本学期无教学任务"/>
      <el-table v-else :data="list" border stripe>
        <el-table-column label="周次" width="90"><template #default="{row}"><el-tag size="small" type="info">{{ wd(row.weekday) }}</el-tag></template></el-table-column>
        <el-table-column label="节次" width="100"><template #default="{row}">第{{ row.sectionStart }}-{{ row.sectionEnd }}节</template></el-table-column>
        <el-table-column prop="courseName" label="课程" min-width="160"/>
        <el-table-column prop="className" label="班级" width="110"/>
        <el-table-column prop="classroom" label="教室" width="130"/>
        <el-table-column prop="weeks" label="教学周" width="90"/>
        <el-table-column label="操作" width="90" fixed="right"><template #default="{row}"><el-button type="primary" size="small" plain @click="showStudents(row)">名单</el-button></template></el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dv" :title="'学生名单 · '+cur?.courseName" width="600px"><el-table :data="roster" v-loading="rLoading" border stripe max-height="420"><el-table-column type="index" label="#" width="50"/><el-table-column prop="studentName" label="姓名" width="90"/><el-table-column prop="studentNo" label="学号" width="140"/><el-table-column prop="className" label="班级" width="120"/><el-table-column prop="department" label="学院" width="140"/></el-table></el-dialog>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import request from '@/utils/request'
import { Calendar } from '@element-plus/icons-vue'
const semester=ref('2025-2026-2'), semesters=['2025-2026-2','2025-2026-1'], list=ref([]), loading=ref(false)
const dv=ref(false), roster=ref([]), rLoading=ref(false), cur=ref(null)
const wd=(d)=>['','周一','周二','周三','周四','周五','周六','周日'][d]||''
const load=async()=>{loading.value=true;try{const r=await request.get('/api/teacher/schedule',{params:{semester:semester.value}});list.value=r.data||[]}catch{list.value=[]}finally{loading.value=false}}
const showStudents=async(row)=>{cur.value=row;dv.value=true;rLoading.value=true;try{const r=await request.get(`/api/teacher/roster/${row.id}`);roster.value=r.data||[]}catch{roster.value=[]}finally{rLoading.value=false}}
load()
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
</style>
