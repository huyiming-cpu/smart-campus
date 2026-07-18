<template>
  <el-card>
    <template #header><span>数据统计总览</span></template>
    <el-tabs v-model="activeTab" @tab-change="loadTabData">
      <el-tab-pane label="固定资产统计" name="assets" />
      <el-tab-pane label="缴费统计" name="payment" />
      <el-tab-pane label="在校生统计" name="studentCount" />
      <el-tab-pane label="评教统计" name="evaluation" />
    </el-tabs>

    <!-- 固定资产统计 -->
    <template v-if="activeTab === 'assets'">
      <div class="stat-summary">
        <el-card class="mini-stat"><div class="mini-value">{{ assetStats.totalCount }}</div><div class="mini-label">总件数</div></el-card>
        <el-card class="mini-stat"><div class="mini-value">{{ formatMoney(assetStats.totalValue) }}</div><div class="mini-label">总价值(元)</div></el-card>
      </div>
      <el-collapse v-model="assetActive" v-loading="loading">
        <el-collapse-item v-for="dept in assetStats.details" :key="dept.department" :name="dept.department">
          <template #title>
            <div class="dept-title">
              <span class="dept-name">{{ dept.department }}</span>
              <el-tag size="small">{{ dept.count }}件</el-tag>
              <span style="color:#409eff;font-weight:500">{{ formatMoney(dept.totalValue) }}元</span>
            </div>
          </template>
          <el-table :data="dept.items" size="small" border>
            <el-table-column prop="assetName" label="资产名称" min-width="160"/>
            <el-table-column prop="assetNo" label="编号" width="80"/>
            <el-table-column prop="assetType" label="类型" width="60"/>
            <el-table-column label="价值(元)" width="100"><template #default="{row}">{{ formatMoney(row.price) }}</template></el-table-column>
            <el-table-column prop="purchaseDate" label="购置日期" width="100"/>
            <el-table-column label="状态" width="70"><template #default="{row}"><el-tag size="small" :type="row.status==='NORMAL'?'success':'warning'">{{row.status==='NORMAL'?'正常':row.status}}</el-tag></template></el-table-column>
            <el-table-column prop="remark" label="备注" min-width="120"/>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </template>

    <!-- 缴费统计 -->
    <template v-if="activeTab === 'payment'">
      <div class="stat-summary">
        <el-card class="mini-stat"><div class="mini-value" style="color:#409eff">{{ formatMoney(paymentStats.totalAmount) }}</div><div class="mini-label">应缴总额(元)</div></el-card>
        <el-card class="mini-stat"><div class="mini-value" style="color:#67c23a">{{ formatMoney(paymentStats.paidAmount) }}</div><div class="mini-label">已缴(元)</div></el-card>
        <el-card class="mini-stat"><div class="mini-value" style="color:#f56c6c">{{ paymentStats.paidCount }}/{{ paymentStats.totalStudents }}</div><div class="mini-label">已缴/总人数</div></el-card>
      </div>
    </template>

    <!-- 在校生统计 -->
    <template v-if="activeTab === 'studentCount'">
      <el-collapse v-model="studentActive" v-loading="loading">
        <el-collapse-item v-for="dept in studentCountStats" :key="dept.department" :name="dept.department">
          <template #title>
            <div class="dept-title">
              <span class="dept-name">{{ dept.department }}</span>
              <el-tag size="small" type="info">{{ dept.totalCount }}人</el-tag>
            </div>
          </template>
          <el-table :data="dept.students" size="small" border>
            <el-table-column type="index" label="#" width="50"/>
            <el-table-column prop="name" label="姓名" width="80"/>
            <el-table-column prop="studentNo" label="学号" width="120"/>
            <el-table-column prop="grade" label="年级" width="80"/>
            <el-table-column prop="className" label="班级" width="110"/>
            <el-table-column prop="major" label="专业" min-width="130"/>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </template>

    <!-- 评教统计 -->
    <template v-if="activeTab === 'evaluation'">
      <el-row :gutter="12" style="margin-bottom:12px">
        <el-col :span="6">
          <el-select v-model="evalDept" placeholder="全部学院" clearable @change="filterEval" style="width:100%">
            <el-option v-for="d in evalDepts" :key="d" :label="d" :value="d"/>
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-input v-model="evalSearch" placeholder="搜索教师姓名" clearable @input="filterEval"/>
        </el-col>
      </el-row>
      <el-table :data="filteredEvals" v-loading="loading" stripe border>
        <el-table-column prop="teacherName" label="教师姓名" width="120" />
        <el-table-column prop="department" label="学院" width="140" />
        <el-table-column prop="avgScore" label="平均分" width="120">
          <template #default="{ row }">
            <el-tag :type="row.avgScore >= 90 ? 'success' : row.avgScore >= 75 ? 'warning' : 'danger'">{{ row.avgScore }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </el-card>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getAssetStats, getPaymentStats, getStudentCountStats } from '@/api/system'
import { getEvaluationStats } from '@/api/exam'
import { ElMessage } from 'element-plus'

const activeTab = ref('assets')
const loading = ref(false)

const assetStats = ref({ totalCount: 0, totalValue: 0, details: [] })
const assetActive = ref([])
const paymentStats = ref({ totalAmount: 0, paidAmount: 0, totalStudents: 0, paidCount: 0 })
const studentCountStats = ref([])
const studentActive = ref([])
const evaluationStats = ref([])

// 评教筛选
const evalDept = ref('')
const evalSearch = ref('')
const evalDepts = computed(() => [...new Set(evaluationStats.value.map(x=>x.department).filter(Boolean))].sort())
const filteredEvals = computed(() => {
  let list = evaluationStats.value
  if (evalDept.value) list = list.filter(x => x.department === evalDept.value)
  if (evalSearch.value) {
    const kw = evalSearch.value.toLowerCase()
    list = list.filter(x => (x.teacherName||'').toLowerCase().includes(kw))
  }
  return list
})
const filterEval = () => {} // trigger computed

const formatMoney = (v) => (v || 0).toLocaleString()

const loadTabData = async () => {
  loading.value = true
  try {
    switch (activeTab.value) {
      case 'assets': { const r = await getAssetStats(); assetStats.value = r.data || { totalCount:0,totalValue:0,details:[] }; break }
      case 'payment': { const r = await getPaymentStats(); paymentStats.value = r.data || { totalAmount:0,paidAmount:0,totalStudents:0,paidCount:0 }; break }
      case 'studentCount': { const r = await getStudentCountStats(); studentCountStats.value = r.data || []; break }
      case 'evaluation': { const r = await getEvaluationStats(); evaluationStats.value = r.data || []; evalDept.value='';evalSearch.value=''; break }
    }
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

onMounted(() => loadTabData())
</script>

<style scoped>
.stat-summary { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 16px; margin-bottom: 20px; }
.mini-stat { text-align: center; } .mini-value { font-size: 28px; font-weight: bold; color: #409eff; } .mini-label { font-size: 13px; color: #909399; margin-top: 6px; }
.dept-title{display:flex;align-items:center;gap:12px;width:100%}
.dept-name{font-weight:600;font-size:14px}
</style>
