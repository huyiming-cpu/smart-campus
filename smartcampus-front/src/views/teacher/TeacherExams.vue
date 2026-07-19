<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Document /></el-icon></div>
      <div><h2 class="page-title">考试安排查询</h2><p class="page-desc">查看本人监考及教学相关考试信息</p></div>
    </div>
    <div class="toolbar"><el-select v-model="semester" @change="load" style="width:200px"><el-option v-for="s in semesters" :key="s" :label="s" :value="s"/></el-select></div>
    <div class="card" v-loading="loading">
      <el-table :data="list" border stripe empty-text="暂无考试安排">
        <el-table-column prop="examName" label="考试名称" min-width="160"/>
        <el-table-column prop="courseName" label="课程" min-width="140"/>
        <el-table-column prop="examDate" label="日期" width="120"/>
        <el-table-column label="时间" width="160"><template #default="{row}">{{ row.examTimeStart }} — {{ row.examTimeEnd }}</template></el-table-column>
        <el-table-column prop="location" label="地点" width="140"/>
        <el-table-column prop="supervisorName" label="监考" width="90"/>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':''" size="small">{{ row.status==='PUBLISHED'?'已发布':'草稿' }}</el-tag></template></el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { getSupervisorExams } from '@/api/exam'
import { Document } from '@element-plus/icons-vue'
const semester=ref('2025-2026-2'), semesters=['2025-2026-2','2025-2026-1'], list=ref([]), loading=ref(false)
const load=async()=>{loading.value=true;try{const r=await getSupervisorExams(semester.value);list.value=r.data||[]}catch{list.value=[]}finally{loading.value=false}}
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
