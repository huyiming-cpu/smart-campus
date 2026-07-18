<template>
  <div class="schedule-page">
    <div class="page-header">
      <h2>📅 我的课表</h2>
      <el-select v-model="semester" @change="loadSchedule" style="width:200px">
        <el-option v-for="s in semesters" :key="s" :label="s" :value="s"/>
      </el-select>
    </div>
    <el-card shadow="never" v-loading="loading">
      <div class="timetable" v-if="scheduleList.length">
        <div class="timetable-header">
          <div class="period-col"></div>
          <div v-for="d in 7" :key="d" class="day-col">{{ weekdays[d-1] }}</div>
        </div>
        <div class="timetable-body">
          <div v-for="p in [1,3,5,7,9]" :key="p" class="timetable-row">
            <div class="period-label">{{ p }}-{{ p+1 }}节</div>
            <div v-for="d in 7" :key="d" class="cell">
              <template v-for="item in getCellItems(d, p)" :key="item.id">
                <el-tag :color="getColor(item.courseName)" effect="dark" class="course-tag" size="small">
                  <div class="tag-content">
                    <strong>{{ item.courseName || '未知课程' }}</strong>
                    <span>{{ item.teacherName || '' }}</span>
                    <span>{{ item.classroom || '' }}</span>
                  </div>
                </el-tag>
              </template>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-else description="本学暂无课表数据"/>
    </el-card>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getStudentSchedule } from '@/api/course'
const semester = ref('2025-2026-2')
const scheduleList = ref([])
const loading = ref(false)
const semesters = ['2025-2026-2','2025-2026-1']
const weekdays = ['周一','周二','周三','周四','周五','周六','周日']
const colors = {}
const pool = ['#409eff','#67c23a','#e6a23c','#f56c6c','#909399','#b37feb','#ff7875','#36cfc9']
const getColor = (name) => { if(!colors[name]) colors[name]=pool[Object.keys(colors).length%pool.length]; return colors[name] }
const getCellItems = (day, p) => scheduleList.value.filter(s => s.weekday===day && s.sectionStart < p+2 && s.sectionEnd >= p)
const loadSchedule = async () => {
  loading.value = true
  try { const r = await getStudentSchedule(semester.value); scheduleList.value = r.data||[] }
  catch { ElMessage.error('加载课表失败') }
  finally { loading.value = false }
}
onMounted(loadSchedule)
</script>
<style scoped>
.schedule-page{max-width:1200px}.page-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}.page-header h2{margin:0;font-size:20px}
.timetable{overflow-x:auto}.timetable-header{display:flex;border-bottom:2px solid #ebeef5}
.period-col{width:60px;flex-shrink:0}.day-col{flex:1;min-width:140px;text-align:center;padding:10px 4px;font-weight:bold;background:#f5f7fa;border-right:1px solid #ebeef5}
.timetable-row{display:flex;min-height:100px;border-bottom:1px solid #ebeef5}
.period-label{width:70px;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:12px;color:#909399;background:#fafafa;border-right:1px solid #ebeef5}
.cell{flex:1;min-width:140px;padding:4px;border-right:1px solid #ebeef5;display:flex;flex-direction:column;gap:2px}
.course-tag{cursor:pointer;width:100%;border:none;white-space:normal;height:auto;min-height:28px}.tag-content{display:flex;flex-direction:column;gap:1px;line-height:1.3}.tag-content strong{font-size:11px}.tag-content span{font-size:10px;opacity:.85}
</style>
