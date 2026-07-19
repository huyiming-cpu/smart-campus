<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><UserFilled /></el-icon></div>
      <div><h2 class="page-title">班级名单</h2><p class="page-desc">查看所选课程的学生名单与成绩概况</p></div>
    </div>
    <div class="toolbar">
      <span class="toolbar-label">选择课程：</span>
      <el-select v-model="sid" @change="load" style="width:400px" placeholder="请选择课程" filterable>
        <el-option v-for="s in schedules" :key="s.id" :label="(s.courseName||'')+' - '+(s.className||'')+' ('+wd(s.weekday)+')'" :value="s.id"/>
      </el-select>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="roster" border stripe empty-text="请选择课程">
        <el-table-column type="index" label="#" width="50"/>
        <el-table-column prop="studentName" label="姓名" width="100"/>
        <el-table-column prop="studentNo" label="学号" width="140"/>
        <el-table-column prop="className" label="班级" width="130"/>
        <el-table-column prop="department" label="学院" width="130"/>
        <el-table-column label="成绩" width="90"><template #default="{row}"><span :class="{nas:!row.score}">{{ row.score||'未录入' }}</span></template></el-table-column>
        <el-table-column label="等级" width="80"><template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='A'?'success':row.gradeLevel==='F'?'danger':''">{{ row.gradeLevel }}</el-tag></template></el-table-column>
        <el-table-column prop="examType" label="考试类型" width="100"><template #default="{row}">{{ row.examType==='FINAL'?'期末':row.examType==='MAKEUP'?'补考':row.examType==='RETAKE'?'重修':row.examType||'-' }}</template></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getTeacherSchedule, getClassRoster } from '@/api/course'
import { UserFilled } from '@element-plus/icons-vue'
const loading=ref(false), schedules=ref([]), sid=ref(null), roster=ref([])
const wd=(d)=>['','周一','周二','周三','周四','周五','周六','周日'][d]||''
onMounted(async()=>{const r=await getTeacherSchedule('2025-2026-2');schedules.value=r.data||[]})
const load=async()=>{if(!sid.value)return;loading.value=true;try{const r=await getClassRoster(sid.value);roster.value=r.data||[]}catch{}finally{loading.value=false}}
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
.nas{color:#C4C9D1}
</style>
