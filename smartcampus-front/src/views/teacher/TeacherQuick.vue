<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Menu /></el-icon></div>
      <div><h2 class="page-title">快速入口</h2><p class="page-desc">常用功能一键直达</p></div>
    </div>
    <div class="quick-grid">
      <div v-for="item in links" :key="item.path" class="quick-card" @click="router.push(item.path)">
        <div class="qc-icon" :style="{background:item.bg}"><el-icon :size="26" :color="item.color"><component :is="item.icon"/></el-icon></div>
        <div class="qc-body"><h4 class="qc-title">{{item.title}}</h4><p class="qc-desc">{{item.desc}}</p></div>
        <el-icon :size="14" color="#C4C9D1" class="qc-arrow"><ArrowRight /></el-icon>
      </div>
    </div>
  </div>
</template>
<script setup>
import { useRouter } from 'vue-router'
import { Menu, ArrowRight, EditPen, Calendar, UserFilled, Document, DataAnalysis, Medal } from '@element-plus/icons-vue'
const router=useRouter()
const links=[
  {title:'成绩录入',desc:'录入学生成绩与等级',path:'/teacher/scores',icon:EditPen,color:'#5B9BD5',bg:'#EBF3FC'},
  {title:'任课信息',desc:'查看课表与班级安排',path:'/teacher/schedule',icon:Calendar,color:'#F59E0B',bg:'#FEF3E2'},
  {title:'班级名单',desc:'查看学生名册与成绩',path:'/teacher/students',icon:UserFilled,color:'#10B981',bg:'#ECFDF5'},
  {title:'考试安排',desc:'监考与考试信息查询',path:'/teacher/exams',icon:Document,color:'#8B5CF6',bg:'#F3F0FF'},
  {title:'科研管理',desc:'科研项目管理',path:'/teacher/research',icon:DataAnalysis,color:'#6366F1',bg:'#EEF2FF'},
  {title:'奖助贷管理',desc:'奖学金助学金审核',path:'/teacher/awards',icon:Medal,color:'#EC4899',bg:'#FDF2F8'},
]
</script>
<style scoped>
.page{padding:20px 24px;max-width:1000px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:24px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.quick-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:16px}
.quick-card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:24px 20px;display:flex;align-items:center;gap:16px;cursor:pointer;transition:all .25s;box-shadow:0 2px 8px rgba(0,0,0,.02)}
.quick-card:hover{transform:translateY(-3px);box-shadow:0 8px 24px rgba(0,0,0,.06);border-color:#D4E2F4}
.qc-icon{width:52px;height:52px;border-radius:14px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.qc-body{flex:1;min-width:0}
.qc-title{font-size:15px;font-weight:600;color:#1A1A2E;margin:0 0 4px}
.qc-desc{font-size:12px;color:#9CA3AF;margin:0;line-height:1.4}
.qc-arrow{flex-shrink:0;transition:color .2s}
.quick-card:hover .qc-arrow{color:#5B9BD5}
@media(max-width:768px){.quick-grid{grid-template-columns:repeat(2,1fr)}}
@media(max-width:480px){.quick-grid{grid-template-columns:1fr}}
</style>
