<template>
  <div class="page">
    <h3>📋 考试安排查询</h3>
    <el-select v-model="semester" @change="load" style="width:200px;margin-bottom:16px">
      <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
    </el-select>
    <el-table :data="list" v-loading="loading" empty-text="暂无考试安排">
      <el-table-column prop="examName" label="考试名称" min-width="140"/>
      <el-table-column prop="courseName" label="课程" min-width="120"/>
      <el-table-column prop="examDate" label="日期" width="110"/>
      <el-table-column label="时间" width="150">
        <template #default="{row}">{{ row.examTimeStart }}-{{ row.examTimeEnd }}</template>
      </el-table-column>
      <el-table-column prop="location" label="地点" width="120"/>
      <el-table-column prop="supervisorName" label="监考教师" width="100"/>
      <el-table-column label="状态" width="100">
        <template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'">{{ row.status }}</el-tag></template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getSupervisorExams } from '@/api/exam'
const list = ref([])
const loading = ref(false)
const semester = ref('2025-2026-2')
const semesters = ['2025-2026-1','2025-2026-2']
const load = async () => {
  loading.value = true
  try { const r = await getSupervisorExams(semester.value); list.value = r.data||[] } catch {}
  finally { loading.value = false }
}
onMounted(load)
</script>
<style scoped>.page{padding:20px} h3{margin-bottom:16px}</style>
