<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Monitor /></el-icon></div>
      <div><h2 class="page-title">实验室开放信息</h2><p class="page-desc">查看各实验室开放状态与设备详情</p></div>
    </div>
    <div class="toolbar">
      <el-input v-model="search" placeholder="搜索实验室名称或学院..." clearable style="width:300px" :prefix-icon="Search"/>
    </div>
    <div class="card" v-loading="loading">
      <el-empty v-if="!loading && filteredLabs.length===0" description="暂无开放实验室"/>
      <el-row :gutter="16" v-else>
        <el-col :span="8" v-for="item in filteredLabs" :key="item.id" style="margin-bottom:16px">
          <div class="lab-card">
            <div class="lab-card-header">
              <span class="lab-name">{{ item.labName }}</span>
              <el-tag :type="item.labType==='TEACHING'?'success':item.labType==='TRAINING'?'warning':'info'" size="small">{{labTypeMap[item.labType]||item.labType}}</el-tag>
            </div>
            <div class="lab-info">
              <div class="info-row"><el-icon :size="14" color="#909399"><Location /></el-icon><span>{{ item.location }}</span></div>
              <div class="info-row"><el-icon :size="14" color="#909399"><School /></el-icon><span>{{ item.department }}</span></div>
              <div class="info-row"><el-icon :size="14" color="#909399"><User /></el-icon><span>{{ item.capacity }}人</span></div>
              <div class="info-row"><el-icon :size="14" color="#909399"><FullScreen /></el-icon><span>{{ item.area }}㎡</span></div>
              <div class="info-row"><el-icon :size="14" color="#409eff"><Clock /></el-icon><span class="open-time">{{ item.openTime }}</span></div>
              <div class="info-row"><el-icon :size="14" color="#909399"><UserFilled /></el-icon><span>{{ item.managerName||'—' }}</span></div>
              <div class="equip-desc" v-if="item.equipmentDesc">
                <div class="label"><el-icon :size="13"><Setting /></el-icon> 设备说明</div>
                <p>{{ item.equipmentDesc }}</p>
              </div>
              <div class="remark" v-if="item.remark">
                <div class="label"><el-icon :size="13"><EditPen /></el-icon> 备注</div>
                <p>{{ item.remark }}</p>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { Monitor, Search, Location, School, User, FullScreen, Clock, UserFilled, Setting, EditPen } from '@element-plus/icons-vue'

const list = ref([]), loading = ref(false), search = ref('')
const labTypeMap = { TEACHING:'教学', TRAINING:'实训', RESEARCH:'科研' }

const filteredLabs = computed(() => {
  if (!search.value) return list.value
  const kw = search.value.toLowerCase()
  return list.value.filter(l => (l.labName||'').toLowerCase().includes(kw) || (l.department||'').toLowerCase().includes(kw))
})

const load = async () => {
  loading.value = true
  try { const r = await request.get('/api/student/labs'); list.value = r.data || [] }
  catch { list.value = [] }
  finally { loading.value = false }
}

onMounted(load)
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.lab-card{background:#fafbfc;border:1px solid #EEF0F4;border-radius:10px;padding:16px 20px;transition:box-shadow .2s}
.lab-card:hover{box-shadow:0 4px 16px rgba(0,0,0,.06)}
.lab-card-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:12px}
.lab-name{font-weight:600;font-size:15px;color:#1A1A2E}
.lab-info{padding:4px 0}
.info-row{display:flex;align-items:center;gap:6px;padding:4px 0;font-size:13px;color:#606266}
.info-row .open-time{color:#409eff;font-weight:500}
.equip-desc,.remark{margin-top:10px;padding-top:10px;border-top:1px solid #f0f0f0}
.equip-desc .label,.remark .label{font-size:12px;color:#909399;margin-bottom:4px;display:flex;align-items:center;gap:4px}
.equip-desc p,.remark p{font-size:12px;color:#666;margin:0;line-height:1.6}
</style>
