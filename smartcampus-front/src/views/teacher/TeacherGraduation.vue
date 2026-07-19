<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#8B5CF6,#7C3AED);"><el-icon :size="20" color="#fff"><Document /></el-icon></div>
      <div><h2 class="page-title">毕业设计管理</h2><p class="page-desc">审阅与指导学生的毕业设计课题</p></div>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" border stripe highlight-current-row @row-click="openDetail" style="cursor:pointer">
        <el-table-column prop="studentName" label="学生姓名" width="110"/>
        <el-table-column prop="studentNo" label="学号" width="140"/>
        <el-table-column prop="topicName" label="课题名称" min-width="220" show-overflow-tooltip/>
        <el-table-column label="状态" width="120"><template #default="{row}"><el-tag size="small" :type="stType(row.status)">{{row.status}}</el-tag></template></el-table-column>
        <el-table-column label="提交时间" width="180"><template #default="{row}">{{row.submitTime||'-'}}</template></el-table-column>
        <el-table-column label="成绩" width="80"><template #default="{row}"><span :style="{color:scColor(row.score),fontWeight:'700'}">{{row.score??'-'}}</span></template></el-table-column>
      </el-table>
      <div v-if="!list.length && !loading" class="empty-tip">暂无毕业设计数据</div>
    </div>
    <el-dialog v-model="dv" title="毕业设计详情" width="700px">
      <template v-if="cur">
        <el-descriptions :column="2" border><el-descriptions-item label="学生姓名">{{cur.studentName}}</el-descriptions-item><el-descriptions-item label="学号">{{cur.studentNo}}</el-descriptions-item><el-descriptions-item label="课题名称" :span="2">{{cur.topicName}}</el-descriptions-item><el-descriptions-item label="课题描述" :span="2">{{cur.topicDescription||'-'}}</el-descriptions-item><el-descriptions-item label="提交时间">{{cur.submitTime||'-'}}</el-descriptions-item><el-descriptions-item label="状态"><el-tag :type="stType(cur.status)" size="small">{{cur.status}}</el-tag></el-descriptions-item></el-descriptions>
        <el-divider/>
        <el-form :model="form" label-width="80px">
          <el-form-item label="教师评语"><el-input v-model="form.teacherComment" type="textarea" :rows="3" placeholder="输入评语..."/></el-form-item>
          <el-form-item label="公告通知"><el-input v-model="form.announcement" type="textarea" :rows="2" placeholder="发布公告..."/></el-form-item>
          <el-row :gutter="16">
            <el-col :span="12"><el-form-item label="成绩"><el-input-number v-model="form.score" :min="0" :max="100" style="width:100%"/></el-form-item></el-col>
            <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option v-for="s in stOptions" :key="s" :label="s" :value="s"/></el-select></el-form-item></el-col>
          </el-row>
        </el-form>
      </template>
      <template #footer>
        <el-button v-if="cur?.status==='PENDING'" type="success" @click="approve('APPROVED')">通过</el-button>
        <el-button v-if="cur?.status==='PENDING'" type="danger" @click="approve('REJECTED')">驳回</el-button>
        <el-button @click="dv=false">关闭</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { listMyDesigns, updateDesign } from '@/api/teacher'
import request from '@/utils/request'
import { Document } from '@element-plus/icons-vue'
const list=ref([]), loading=ref(false), dv=ref(false), cur=ref(null), saving=ref(false)
const form=reactive({teacherComment:'',announcement:'',score:null,status:''})
const stOptions=['PENDING','APPROVED','REJECTED','IN_PROGRESS','COMPLETED']
const stType=(s)=>{const m={APPROVED:'success',REJECTED:'danger',COMPLETED:'success',IN_PROGRESS:'warning',PENDING:'info'};return m[s]||'info'}
const scColor=(v)=>{const n=parseFloat(v);if(isNaN(n))return'#909399';if(n>=90)return'#10B981';if(n>=70)return'#F59E0B';return'#EF4444'}
const load=async()=>{loading.value=true;try{const r=await listMyDesigns();list.value=r.data||[]}catch{}finally{loading.value=false}}
const openDetail=(row)=>{cur.value=row;Object.assign(form,{teacherComment:row.teacherComment||'',announcement:row.announcement||'',score:row.score,status:row.status});dv.value=true}
const approve=async(status)=>{try{await request.put(`/api/teacher/graduation-design/${cur.value.id}/approve`,{status});ElMessage.success(status==='APPROVED'?'已通过':'已驳回');dv.value=false;load()}catch{ElMessage.error('操作失败')}}
const handleSave=async()=>{saving.value=true;try{await updateDesign(cur.value.id,{...form});ElMessage.success('保存成功');dv.value=false;load()}catch{ElMessage.error('保存失败')}finally{saving.value=false}}
load()
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(139,92,246,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.empty-tip{text-align:center;padding:40px 0;color:#C4C9D1;font-size:14px}
</style>
