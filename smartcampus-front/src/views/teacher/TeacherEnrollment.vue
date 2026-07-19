<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Grid /></el-icon></div>
      <div><h2 class="page-title">选课情况</h2><p class="page-desc">查看各课程学生选课名单</p></div>
    </div>
    <div class="toolbar">
      <el-select v-model="semester" @change="load" style="width:200px"><el-option v-for="s in semesters" :key="s" :label="s" :value="s"/></el-select>
      <el-select v-model="quickSearch" filterable placeholder="快速定位课程..." style="width:300px" @change="jumpTo" clearable>
        <el-option v-for="item in enrollList" :key="item.scheduleId" :label="item.courseName+' - '+item.className" :value="item.scheduleId"/>
      </el-select>
      <el-tag effect="plain" type="info">共 {{ enrollList.length }} 门课程</el-tag>
    </div>
    <div class="card" v-loading="loading" v-if="enrollList.length>0">
      <el-collapse v-model="activeNames">
        <el-collapse-item v-for="item in enrollList" :key="item.scheduleId" :name="String(item.scheduleId)">
          <template #title>
            <div class="enroll-title">
              <span class="enroll-course">{{ item.courseName }} <small>{{ item.courseCode }}</small></span>
              <span class="enroll-meta">{{ item.className }} · {{ wd(item.weekday) }} {{ item.sectionStart }}-{{ item.sectionEnd }}节 · {{ item.classroom }}</span>
              <el-tag size="small" :type="(item.enrolledCount>=(item.maxStudents||99))?'danger':'success'">{{ item.enrolledCount }}/{{ item.maxStudents||'不限' }}人</el-tag>
            </div>
          </template>
          <el-table :data="item.students" size="small" border stripe v-if="item.students?.length">
            <el-table-column type="index" label="#" width="50"/>
            <el-table-column prop="studentName" label="姓名" width="90"/>
            <el-table-column prop="studentNo" label="学号" width="140"/>
            <el-table-column prop="className" label="班级" width="120"/>
            <el-table-column label="成绩" width="80"><template #default="{row}">{{ row.score??'未录入' }}</template></el-table-column>
            <el-table-column label="等级" width="70"><template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='F'?'danger':''">{{ row.gradeLevel }}</el-tag></template></el-table-column>
          </el-table>
          <el-empty v-else description="暂无学生选课" :image-size="60"/>
        </el-collapse-item>
      </el-collapse>
    </div>
    <el-empty v-if="!loading && enrollList.length===0" description="本学期无教学任务"/>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import request from '@/utils/request'
import { Grid } from '@element-plus/icons-vue'
const semester=ref('2025-2026-2'), semesters=['2025-2026-2','2025-2026-1'], enrollList=ref([]), loading=ref(false)
const activeNames=ref([]), quickSearch=ref(null)
const wd=(d)=>['','周一','周二','周三','周四','周五','周六','周日'][d]||''
const load=async()=>{loading.value=true;try{const r=await request.get('/api/teacher/enrollment',{params:{semester:semester.value}});enrollList.value=r.data||[]}catch{enrollList.value=[]}finally{loading.value=false}}
const jumpTo=(id)=>{if(id)activeNames.value=[String(id)]}
load()
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;align-items:center;gap:12px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.enroll-title{display:flex;align-items:center;width:100%;gap:16px;flex-wrap:wrap}
.enroll-course{font-weight:600;min-width:150px;color:#1A1A2E}
.enroll-course small{color:#9CA3AF;font-weight:400;margin-left:4px}
.enroll-meta{font-size:12px;color:#9CA3AF}
</style>
