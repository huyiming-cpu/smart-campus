<template>
  <div class="page">
    <h3>📊 选课情况 · 班级名单</h3>
    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-select v-model="semester" @change="load" style="width:100%">
        <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
      </el-select></el-col>
      <el-col :span="8"><el-select v-model="quickCourse" @change="onQuickSelect" style="width:100%" filterable clearable placeholder="快速定位课程...">
        <el-option v-for="item in list" :key="item.scheduleId" :label="item.courseName+' - '+item.className" :value="String(item.scheduleId)"/>
      </el-select></el-col>
      <el-col :span="4"><el-tag v-if="list.length" type="info">共 {{ list.length }} 门课</el-tag></el-col>
    </el-row>

    <el-collapse v-model="activeNames" v-loading="loading">
      <el-collapse-item v-for="item in list" :key="item.scheduleId" :name="String(item.scheduleId)">
        <template #title>
          <div class="collapse-title">
            <span class="course-name">{{ item.courseName }} <small style="color:#999">({{ item.courseCode }})</small></span>
            <span class="course-class">{{ item.className }} | {{ ['','周一','周二','周三','周四','周五'][item.weekday] }} {{ item.sectionStart}}-{{ item.sectionEnd }}节 | {{ item.classroom }}</span>
            <el-tag size="small" :type="item.enrolledCount >= (item.maxStudents||99) ? 'danger' : 'success'">{{ item.enrolledCount }}/{{ item.maxStudents||'不限' }}人</el-tag>
          </div>
        </template>
        <el-table :data="item.students" size="small" border v-if="item.students?.length">
          <el-table-column type="index" label="#" width="50"/>
          <el-table-column prop="studentName" label="姓名" width="80"/>
          <el-table-column prop="studentNo" label="学号" width="120"/>
          <el-table-column prop="className" label="班级" width="110"/>
          <el-table-column label="成绩" width="70"><template #default="{row}">{{ row.score??'未录入' }}</template></el-table-column>
          <el-table-column label="等级" width="60"><template #default="{row}"><el-tag v-if="row.gradeLevel" size="small" :type="row.gradeLevel==='F'?'danger':''">{{row.gradeLevel}}</el-tag></template></el-table-column>
        </el-table>
        <el-empty v-else description="暂无学生选课"/>
      </el-collapse-item>
    </el-collapse>
    <el-empty v-if="!loading && list.length===0" description="本学期无教学任务"/>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const semesters = ['2025-2026-2','2025-2026-1'], semester = ref('2025-2026-2')
const list = ref([]), loading = ref(false), activeNames = ref([]), quickCourse = ref('')

const load = async () => {
  loading.value = true; quickCourse.value = ''
  try { const r = await request.get('/api/teacher/enrollment', {params:{semester:semester.value}}); list.value = r.data || [] }
  catch { list.value = [] }
  finally { loading.value = false }
}

const onQuickSelect = (val) => { if (val) activeNames.value = [val] }

onMounted(load)
</script>
<style scoped>
.page{padding:20px;max-width:1100px}h3{margin-bottom:16px}
.collapse-title{display:flex;align-items:center;width:100%;gap:16px;flex-wrap:wrap}
.course-name{font-weight:600;min-width:140px}
.course-class{font-size:12px;color:#909399}
</style>
