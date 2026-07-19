<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#6366F1,#4F46E5);"><el-icon :size="20" color="#fff"><DataLine /></el-icon></div>
      <div><h2 class="page-title">数据统计</h2><p class="page-desc">固定资产、学生缴费、在校生及评教数据统计总览</p></div>
    </div>

    <div class="toolbar">
      <el-tabs v-model="activeTab" @tab-change="loadTabData">
        <el-tab-pane label="固定资产统计" name="assets"/>
        <el-tab-pane label="缴费统计" name="payment"/>
        <el-tab-pane label="在校生统计" name="studentCount"/>
        <el-tab-pane label="评教统计" name="evaluation"/>
      </el-tabs>
    </div>

    <div class="card" v-loading="loading">
      <!-- 固定资产统计 -->
      <template v-if="activeTab==='assets'">
        <div class="stat-row">
          <div class="stat-card"><div class="sc-icon" style="background:#EEF2FF;"><el-icon :size="18" color="#6366F1"><OfficeBuilding /></el-icon></div><div class="sc-body"><div class="sc-val">{{ assetStats.totalCount }}</div><div class="sc-lbl">总件数</div></div></div>
          <div class="stat-card"><div class="sc-icon" style="background:#FEF3E2;"><el-icon :size="18" color="#F59E0B"><Money /></el-icon></div><div class="sc-body"><div class="sc-val">&yen;{{ formatMoney(assetStats.totalValue) }}</div><div class="sc-lbl">总价值</div></div></div>
        </div>
        <el-collapse v-model="assetActive">
          <el-collapse-item v-for="dept in assetStats.details" :key="dept.department" :name="dept.department">
            <template #title>
              <div class="dept-title">
                <span class="dept-name">{{ dept.department }}</span>
                <el-tag size="small">{{ dept.count }}件</el-tag>
                <span class="dept-value">¥{{ formatMoney(dept.totalValue) }}</span>
              </div>
            </template>
            <el-table :data="dept.items" stripe border empty-text="暂无资产记录">
              <el-table-column prop="assetName" label="资产名称" min-width="170"/>
              <el-table-column prop="assetNo" label="编号" width="90"/>
              <el-table-column label="类型" width="80"><template #default="{row}"><el-tag size="small" type="info">{{ row.assetType }}</el-tag></template></el-table-column>
              <el-table-column label="价值" width="120"><template #default="{row}"><span class="price">¥{{ formatMoney(row.price) }}</span></template></el-table-column>
              <el-table-column prop="purchaseDate" label="购置日期" width="120"/>
              <el-table-column label="状态" width="80"><template #default="{row}"><el-tag size="small" :type="row.status==='NORMAL'?'success':'warning'">{{ row.status==='NORMAL'?'正常':row.status }}</el-tag></template></el-table-column>
              <el-table-column prop="remark" label="备注" min-width="130"/>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </template>

      <!-- 缴费统计 -->
      <template v-if="activeTab==='payment'">
        <div class="stat-row three">
          <div class="stat-card"><div class="sc-icon" style="background:#FEF3E2;"><el-icon :size="18" color="#F59E0B"><Money /></el-icon></div><div class="sc-body"><div class="sc-val" style="color:#F59E0B">¥{{ formatMoney(paymentStats.totalAmount) }}</div><div class="sc-lbl">应缴总额</div></div></div>
          <div class="stat-card"><div class="sc-icon" style="background:#ECFDF5;"><el-icon :size="18" color="#10B981"><CircleCheckFilled /></el-icon></div><div class="sc-body"><div class="sc-val" style="color:#10B981">¥{{ formatMoney(paymentStats.paidAmount) }}</div><div class="sc-lbl">已缴总额</div></div></div>
          <div class="stat-card"><div class="sc-icon" style="background:#EEF2FF;"><el-icon :size="18" color="#6366F1"><UserFilled /></el-icon></div><div class="sc-body"><div class="sc-val">{{ paymentStats.paidCount }}/{{ paymentStats.totalStudents }}</div><div class="sc-lbl">已缴/总人数</div></div></div>
        </div>
      </template>

      <!-- 在校生统计 -->
      <template v-if="activeTab==='studentCount'">
        <el-collapse v-model="studentActive">
          <el-collapse-item v-for="dept in studentCountStats" :key="dept.department" :name="dept.department">
            <template #title>
              <div class="dept-title">
                <span class="dept-name">{{ dept.department }}</span>
                <el-tag size="small" type="info">{{ dept.totalCount }}人</el-tag>
              </div>
            </template>
            <el-table :data="dept.students" stripe border empty-text="暂无学生数据">
              <el-table-column type="index" label="#" width="50"/>
              <el-table-column prop="name" label="姓名" width="90"/>
              <el-table-column prop="studentNo" label="学号" width="130"/>
              <el-table-column prop="grade" label="年级" width="90"/>
              <el-table-column prop="className" label="班级" width="120"/>
              <el-table-column prop="major" label="专业" min-width="140"/>
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </template>

      <!-- 评教统计 -->
      <template v-if="activeTab==='evaluation'">
        <div class="eval-toolbar">
          <el-select v-model="evalDept" placeholder="全部学院" clearable @change="filterEval" style="width:200px"><el-option v-for="d in evalDepts" :key="d" :label="d" :value="d"/></el-select>
          <el-input v-model="evalSearch" placeholder="搜索教师姓名" clearable @input="filterEval" style="width:220px" :prefix-icon="Search"/>
        </div>
        <el-table :data="filteredEvals" stripe border empty-text="暂无评教数据">
          <el-table-column prop="teacherName" label="教师姓名" width="130"/>
          <el-table-column prop="department" label="学院" min-width="150"/>
          <el-table-column label="平均分" width="120">
            <template #default="{row}">
              <el-tag :type="row.avgScore>=90?'success':row.avgScore>=75?'warning':'danger'" size="small">{{ row.avgScore }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="totalEvaluations" label="评教人次" width="110"/>
        </el-table>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { DataLine, OfficeBuilding, Money, CircleCheckFilled, UserFilled, Search } from '@element-plus/icons-vue'
import { getAssetStats, getPaymentStats, getStudentCountStats } from '@/api/system'
import { getEvaluationStats } from '@/api/exam'
import { ElMessage } from 'element-plus'

const activeTab=ref('assets'), loading=ref(false)
const assetStats=ref({totalCount:0,totalValue:0,details:[]}), assetActive=ref([])
const paymentStats=ref({totalAmount:0,paidAmount:0,totalStudents:0,paidCount:0})
const studentCountStats=ref([]), studentActive=ref([])
const evaluationStats=ref([]), evalDept=ref(''), evalSearch=ref('')
const evalDepts=computed(()=>[...new Set(evaluationStats.value.map(x=>x.department).filter(Boolean))].sort())
const filteredEvals=computed(()=>{
  let list=evaluationStats.value
  if(evalDept.value)list=list.filter(x=>x.department===evalDept.value)
  if(evalSearch.value){const kw=evalSearch.value.toLowerCase();list=list.filter(x=>(x.teacherName||'').toLowerCase().includes(kw))}
  return list
})
const filterEval=()=>{}
const formatMoney=(v)=>(v||0).toLocaleString()

const loadTabData=async()=>{
  loading.value=true
  try{
    switch(activeTab.value){
      case'assets':{const r=await getAssetStats();assetStats.value=r.data||{totalCount:0,totalValue:0,details:[]};break}
      case'payment':{const r=await getPaymentStats();paymentStats.value=r.data||{totalAmount:0,paidAmount:0,totalStudents:0,paidCount:0};break}
      case'studentCount':{const r=await getStudentCountStats();studentCountStats.value=r.data||[];break}
      case'evaluation':{const r=await getEvaluationStats();evaluationStats.value=r.data||[];evalDept.value='';evalSearch.value='';break}
    }
  }catch{ElMessage.error('加载失败')}
  finally{loading.value=false}
}

onMounted(()=>loadTabData())
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}

/* 统计卡片 */
.stat-row{display:grid;grid-template-columns:repeat(2,1fr);gap:16px;margin-bottom:20px}
.stat-row.three{grid-template-columns:repeat(3,1fr)}
.stat-card{background:#fff;border:1px solid #EEF0F4;border-radius:12px;padding:18px 20px;display:flex;align-items:center;gap:14px;transition:all .2s}
.stat-card:hover{box-shadow:0 4px 16px rgba(0,0,0,.04)}
.sc-icon{width:44px;height:44px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.sc-body{min-width:0}
.sc-val{font-size:24px;font-weight:700;color:#1A1A2E;letter-spacing:.3px}
.sc-lbl{font-size:12px;color:#9CA3AF;margin-top:2px}

/* 部门标题 */
.dept-title{display:flex;align-items:center;gap:12px;width:100%}
.dept-name{font-weight:600;font-size:14px;color:#1A1A2E}
.dept-value{color:#6366F1;font-weight:500;margin-left:auto}

/* 表格内辅助 */
.price{font-weight:600;color:#6366F1}

/* 评教 */
.eval-toolbar{display:flex;align-items:center;gap:12px;margin-bottom:16px}

@media(max-width:768px){.stat-row,.stat-row.three{grid-template-columns:1fr}}
</style>
