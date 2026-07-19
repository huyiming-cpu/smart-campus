<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Star /></el-icon></div>
      <div><h2 class="page-title">评教结果查看</h2><p class="page-desc">查看学生对教学质量的评价反馈</p></div>
    </div>
    <div class="toolbar"><el-select v-model="semester" @change="loadData" style="width:200px"><el-option v-for="s in semesters" :key="s" :label="s" :value="s"/></el-select></div>
    <div class="eval-stats" v-loading="loading">
      <div v-for="dim in dimensions" :key="dim.key" class="eval-stat-card">
        <div class="esc-icon" :style="{background:dim.bg}"><el-icon :size="18" :color="dim.color"><component :is="dim.icon"/></el-icon></div>
        <div class="esc-body"><div class="esc-label">{{dim.label}}</div><div class="esc-value" :style="{color:scColor(dim.avg)}">{{dim.avg}}</div></div>
      </div>
      <div class="eval-stat-card overall">
        <div class="esc-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="18" color="#fff"><Trophy /></el-icon></div>
        <div class="esc-body"><div class="esc-label">综合评分</div><div class="esc-value big" :style="{color:scColor(overallAvg)}">{{overallAvg}}</div></div>
      </div>
    </div>
    <div class="card" v-loading="loading">
      <div class="card-head">评教详情</div>
      <el-table :data="evalList" border stripe>
        <el-table-column prop="courseName" label="课程" min-width="160"/>
        <el-table-column prop="semester" label="学期" width="130"/>
        <el-table-column label="教学态度" width="100"><template #default="{row}"><span class="sc" :style="{color:scColor(row.teachingAttitude)}">{{row.teachingAttitude??'-'}}</span></template></el-table-column>
        <el-table-column label="教学内容" width="100"><template #default="{row}"><span class="sc" :style="{color:scColor(row.teachingContent)}">{{row.teachingContent??'-'}}</span></template></el-table-column>
        <el-table-column label="教学方法" width="100"><template #default="{row}"><span class="sc" :style="{color:scColor(row.teachingMethod)}">{{row.teachingMethod??'-'}}</span></template></el-table-column>
        <el-table-column label="教学效果" width="100"><template #default="{row}"><span class="sc" :style="{color:scColor(row.teachingEffect)}">{{row.teachingEffect??'-'}}</span></template></el-table-column>
        <el-table-column label="综合" width="90"><template #default="{row}"><span class="sc fw" :style="{color:scColor(row.overallScore)}">{{row.overallScore??'-'}}</span></template></el-table-column>
        <el-table-column prop="comment" label="评语" min-width="180" show-overflow-tooltip><template #default="{row}">{{row.comment||'匿名评价'}}</template></el-table-column>
      </el-table>
    </div>
    <div v-if="!loading && evalList.length===0" class="empty-tip">暂无评教数据</div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import { getTeacherEvaluations } from '@/api/exam'
import { ElMessage } from 'element-plus'
import { Star, Trophy, Medal, Notebook, ChatDotRound, DataAnalysis } from '@element-plus/icons-vue'
const semester=ref('2025-2026-2'), semesters=['2025-2026-2','2025-2026-1','2024-2025-2','2024-2025-1']
const loading=ref(false), evalList=ref([])
const dimDefs=[{key:'teachingAttitude',label:'教学态度',icon:Medal,color:'#5B9BD5',bg:'#EBF3FC'},{key:'teachingContent',label:'教学内容',icon:Notebook,color:'#F59E0B',bg:'#FEF3E2'},{key:'teachingMethod',label:'教学方法',icon:ChatDotRound,color:'#10B981',bg:'#ECFDF5'},{key:'teachingEffect',label:'教学效果',icon:DataAnalysis,color:'#8B5CF6',bg:'#F3F0FF'}]
const dimensions=computed(()=>{if(!evalList.value.length)return dimDefs.map(d=>({...d,avg:'-'}));return dimDefs.map(d=>{const sum=evalList.value.reduce((s,e)=>s+(Number(e[d.key])||0),0);return{...d,avg:(sum/evalList.value.length).toFixed(1)}})})
const overallAvg=computed(()=>{if(!evalList.value.length)return'-';const sum=evalList.value.reduce((s,e)=>s+(Number(e.overallScore)||0),0);return(sum/evalList.value.length).toFixed(1)})
const scColor=(v)=>{const n=parseFloat(v);if(isNaN(n))return'#909399';if(n>=90)return'#10B981';if(n>=75)return'#F59E0B';if(n>=60)return'#F97316';return'#EF4444'}
const loadData=async()=>{loading.value=true;try{const r=await getTeacherEvaluations(semester.value);evalList.value=r.data||[]}catch{ElMessage.error('加载失败')}finally{loading.value=false}}
loadData()
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.eval-stats{display:grid;grid-template-columns:repeat(5,1fr);gap:14px;margin-bottom:20px}
.eval-stat-card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:18px 16px;display:flex;align-items:center;gap:12px;box-shadow:0 2px 8px rgba(0,0,0,.02);transition:all .2s}
.eval-stat-card:hover{transform:translateY(-2px);box-shadow:0 6px 18px rgba(0,0,0,.05)}
.eval-stat-card.overall{border-color:#D4E2F4;background:linear-gradient(135deg,#FAFCFE,#F0F5FC)}
.esc-icon{width:42px;height:42px;border-radius:10px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.esc-body{min-width:0}
.esc-label{font-size:12px;color:#9CA3AF;margin-bottom:4px}
.esc-value{font-size:26px;font-weight:700;letter-spacing:.5px}
.esc-value.big{font-size:30px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.card-head{margin-bottom:14px;font-size:14px;color:#4B5563;padding-bottom:12px;border-bottom:1px solid #F5F6F8}
.sc{font-weight:500}.sc.fw{font-weight:700}
.empty-tip{text-align:center;padding:40px 0;color:#C4C9D1;font-size:14px}
@media(max-width:900px){.eval-stats{grid-template-columns:repeat(3,1fr)}}
@media(max-width:600px){.eval-stats{grid-template-columns:repeat(2,1fr)}}
</style>
