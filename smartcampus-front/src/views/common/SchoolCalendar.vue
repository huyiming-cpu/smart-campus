<template>
  <div class="calendar-page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);">
        <el-icon :size="20" color="#fff"><Calendar /></el-icon>
      </div>
      <div>
        <h2 class="page-title">校历查询</h2>
        <p class="page-desc">查看学期安排、节假日、考试周等重要日期</p>
      </div>
      <div class="page-actions">
        <el-select v-model="semester" @change="loadEvents" style="width:180px" placeholder="选择学期">
          <el-option v-for="s in semesters" :key="s" :label="formatSemester(s)" :value="s"/>
        </el-select>
      </div>
    </div>

    <div v-loading="loading" class="calendar-body">
      <div v-if="groupedEvents.length === 0 && !loading" class="empty-state">
        <el-empty description="该学期暂无校历数据" />
      </div>

      <div v-for="month in groupedEvents" :key="month.label" class="month-section">
        <div class="month-header">
          <span class="month-label">{{ month.label }}</span>
          <span class="month-count">{{ month.events.length }} 个事件</span>
        </div>
        <div class="event-list">
          <div v-for="evt in month.events" :key="evt.id" class="event-card">
            <div class="event-date-box">
              <span class="event-day">{{ dayOfMonth(evt.eventDate) }}</span>
              <span class="event-weekday">{{ weekdayName(evt.eventDate) }}</span>
            </div>
            <div class="event-line"></div>
            <div class="event-info">
              <div class="event-title-row">
                <span :class="['event-badge', typeClass(evt.eventType)]">{{ typeLabel(evt.eventType) }}</span>
                <span class="event-title">{{ evt.eventTitle }}</span>
              </div>
              <p class="event-desc" v-if="evt.description">{{ evt.description }}</p>
            </div>
            <div class="event-date-text">{{ evt.eventDate }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { Calendar } from '@element-plus/icons-vue'

const semester = ref('')
const semesters = ref([])
const events = ref([])
const loading = ref(false)

const formatSemester = (s) => {
  if (!s) return ''
  const parts = s.split('-')
  if (parts.length === 3) {
    const season = parts[2] === '1' ? '第一学期(秋)' : '第二学期(春)'
    return parts[0] + '-' + parts[1] + ' ' + season
  }
  return s
}

const dayOfMonth = (d) => {
  if (!d) return ''
  return String(new Date(d).getDate()).padStart(2, '0')
}

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
const weekdayName = (d) => {
  if (!d) return ''
  return weekdays[new Date(d).getDay()]
}

const typeMap = {
  SEMESTER_START: { label: '学期', cls: 'badge-blue' },
  SEMESTER_END: { label: '学期', cls: 'badge-blue' },
  HOLIDAY: { label: '假期', cls: 'badge-orange' },
  EXAM_WEEK: { label: '考试', cls: 'badge-red' },
  ACTIVITY: { label: '活动', cls: 'badge-green' },
  MAKEUP: { label: '补课', cls: 'badge-purple' },
  VACATION: { label: '寒暑假', cls: 'badge-teal' }
}

const typeLabel = (t) => typeMap[t]?.label || t
const typeClass = (t) => typeMap[t]?.cls || 'badge-blue'

const groupedEvents = computed(() => {
  const groups = {}
  events.value.forEach(e => {
    const d = new Date(e.eventDate)
    const key = d.getFullYear() + '年' + (d.getMonth() + 1) + '月'
    if (!groups[key]) groups[key] = []
    groups[key].push(e)
  })
  return Object.entries(groups).map(([label, evts]) => ({ label, events: evts }))
})

const loadEvents = async () => {
  loading.value = true
  try {
    const res = await request.get('/api/calendar/events', { params: { semester: semester.value } })
    events.value = res.data || []
  } catch {
    events.value = []
  } finally {
    loading.value = false
  }
}

onMounted(async () => {
  try {
    const r = await request.get('/api/calendar/current-semester')
    semester.value = r.data?.semester || '2025-2026-2'
    semesters.value = [semester.value]
    // add a few more semesters for selection
    const parts = semester.value.split('-')
    if (parts.length === 3) {
      const y1 = parseInt(parts[0]), y2 = parseInt(parts[1])
      semesters.value = [
        (y1 - 1) + '-' + y1 + '-2',
        y1 + '-' + y2 + '-1',
        semester.value,
        y1 + '-' + y2 + '-2',
        y2 + '-' + (y2 + 1) + '-1'
      ].filter((v, i, a) => a.indexOf(v) === i).sort()
    }
    await loadEvents()
  } catch {
    semester.value = '2025-2026-2'
    semesters.value = ['2025-2026-2']
    await loadEvents()
  }
})
</script>

<style scoped>
.calendar-page {
  padding: 20px 24px;
  max-width: 960px;
  margin: 0 auto;
  font-family: "Microsoft YaHei", "PingFang SC", "Helvetica Neue", system-ui, sans-serif;
}
.page-head {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 24px;
}
.page-icon {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(91, 155, 213, 0.25);
  flex-shrink: 0;
}
.page-title {
  font-size: 20px;
  font-weight: 700;
  color: #1A1A2E;
  margin: 0;
}
.page-desc {
  font-size: 13px;
  color: #9CA3AF;
  margin: 2px 0 0;
}
.page-actions {
  margin-left: auto;
}
.calendar-body {
  min-height: 400px;
}
.empty-state {
  padding: 60px 0;
}

/* Month section */
.month-section {
  margin-bottom: 24px;
}
.month-header {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #EEF0F4;
}
.month-label {
  font-size: 17px;
  font-weight: 700;
  color: #1A1A2E;
}
.month-count {
  font-size: 12px;
  color: #9CA3AF;
}

/* Event list */
.event-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.event-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border: 1px solid #EEF0F4;
  border-radius: 12px;
  padding: 16px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.02);
  transition: all 0.2s;
}
.event-card:hover {
  border-color: #D4E2F4;
  box-shadow: 0 4px 14px rgba(91, 155, 213, 0.08);
  transform: translateY(-1px);
}

.event-date-box {
  width: 52px;
  text-align: center;
  flex-shrink: 0;
}
.event-day {
  display: block;
  font-size: 22px;
  font-weight: 700;
  color: #5B9BD5;
  line-height: 1.1;
}
.event-weekday {
  font-size: 11px;
  color: #9CA3AF;
}

.event-line {
  width: 1px;
  height: 36px;
  background: #EEF0F4;
  flex-shrink: 0;
}

.event-info {
  flex: 1;
  min-width: 0;
}
.event-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}
.event-badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 500;
  white-space: nowrap;
  flex-shrink: 0;
}
.badge-blue { background: #EBF3FC; color: #5B9BD5; }
.badge-orange { background: #FEF3E2; color: #F59E0B; }
.badge-red { background: #FEF2F2; color: #EF4444; }
.badge-green { background: #ECFDF5; color: #10B981; }
.badge-purple { background: #F3F0FF; color: #8B5CF6; }
.badge-teal { background: #ECFEFF; color: #06B6D4; }

.event-title {
  font-size: 15px;
  font-weight: 600;
  color: #1A1A2E;
}
.event-desc {
  font-size: 13px;
  color: #6B7280;
  margin: 4px 0 0;
  line-height: 1.5;
}

.event-date-text {
  font-size: 12px;
  color: #C4C9D1;
  flex-shrink: 0;
  white-space: nowrap;
}

@media (max-width: 640px) {
  .calendar-page { padding: 12px 14px; }
  .event-card { flex-wrap: wrap; gap: 10px; padding: 14px; }
  .event-date-box { width: auto; display: flex; gap: 6px; align-items: baseline; }
  .event-day { font-size: 18px; }
  .event-line { display: none; }
  .event-date-text { display: none; }
}
</style>
