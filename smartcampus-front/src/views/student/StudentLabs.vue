<template>
  <div class="page">
    <h3>🔬 实验室开放信息</h3>
    <el-input v-model="search" placeholder="搜索实验室名称..." clearable style="width:300px;margin-bottom:16px"/>
    <div v-loading="loading">
      <el-empty v-if="!loading && filteredLabs.length===0" description="暂无开放实验室"/>
      <el-row :gutter="16" v-else>
        <el-col :span="8" v-for="item in filteredLabs" :key="item.id" style="margin-bottom:16px">
          <el-card shadow="hover">
            <template #header>
              <div class="lab-header">
                <span class="lab-name">{{ item.labName }}</span>
                <el-tag :type="item.labType==='TEACHING'?'success':item.labType==='TRAINING'?'warning':'info'" size="small">{{labTypeMap[item.labType]||item.labType}}</el-tag>
              </div>
            </template>
            <div class="lab-info">
              <div class="info-row"><span class="label">📍 位置</span><span>{{ item.location }}</span></div>
              <div class="info-row"><span class="label">🏫 学院</span><span>{{ item.department }}</span></div>
              <div class="info-row"><span class="label">👥 容量</span><span>{{ item.capacity }}人</span></div>
              <div class="info-row"><span class="label">📐 面积</span><span>{{ item.area }}㎡</span></div>
              <div class="info-row"><span class="label">🕐 开放时间</span><span class="open-time">{{ item.openTime }}</span></div>
              <div class="info-row"><span class="label">👤 负责人</span><span>{{ item.managerName||'—' }}</span></div>
              <div class="equip-desc" v-if="item.equipmentDesc">
                <div class="label">🔧 设备说明</div>
                <p>{{ item.equipmentDesc }}</p>
              </div>
              <div class="remark" v-if="item.remark">
                <div class="label">📝 备注</div>
                <p>{{ item.remark }}</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

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
.page{padding:20px;max-width:1200px}h3{margin-bottom:16px}
.lab-header{display:flex;justify-content:space-between;align-items:center}
.lab-name{font-weight:600;font-size:15px}
.lab-info{padding:4px 0}
.info-row{display:flex;gap:8px;padding:4px 0;font-size:13px}
.info-row .label{color:#909399;min-width:70px;flex-shrink:0}
.open-time{color:#409eff;font-weight:500}
.equip-desc,.remark{margin-top:10px;padding-top:10px;border-top:1px solid #f0f0f0}
.equip-desc .label,.remark .label{font-size:12px;color:#909399;margin-bottom:4px}
.equip-desc p,.remark p{font-size:12px;color:#666;margin:0;line-height:1.6}
</style>
