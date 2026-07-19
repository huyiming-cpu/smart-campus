<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#F59E0B,#E5900B);"><el-icon :size="20" color="#fff"><Medal /></el-icon></div>
      <div><h2 class="page-title">奖助贷管理</h2><p class="page-desc">辅导员审核奖学金、助学金、贷款申请</p></div>
    </div>
    <div class="card">
      <el-tabs v-model="tab" @tab-change="load" class="custom-tabs">
        <el-tab-pane label="待审核" name="pending"/>
        <el-tab-pane label="已审核" name="reviewed"/>
      </el-tabs>
      <el-table :data="list" v-loading="loading" border stripe>
        <el-table-column prop="studentName" label="姓名" width="90"/>
        <el-table-column prop="studentNo" label="学号" width="140"/>
        <el-table-column prop="department" label="学院" width="110"/>
        <el-table-column prop="className" label="班级" width="110"/>
        <el-table-column label="类型" width="90"><template #default="{row}"><el-tag size="small" :type="row.applicationType==='SCHOLARSHIP'?'warning':'info'">{{ typeMap[row.applicationType]||row.applicationType }}</el-tag></template></el-table-column>
        <el-table-column prop="title" label="标题" min-width="160"/>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="160" fixed="right" v-if="tab==='pending'">
          <template #default="{row}">
            <el-button type="success" plain size="small" @click="review(row.id,'FIRST_APPROVED')">通过</el-button>
            <el-button type="danger" plain size="small" @click="review(row.id,'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Medal } from '@element-plus/icons-vue'
const tab=ref('pending'), list=ref([]), loading=ref(false)
const typeMap={SCHOLARSHIP:'奖学金',SUBSIDY:'助学金',LOAN:'贷款'}
const statusType=(s)=>s==='APPROVED'||s==='FIRST_APPROVED'?'success':s==='REJECTED'?'danger':'warning'
const statusText=(s)=>({PENDING:'待审',FIRST_APPROVED:'待院长批',APPROVED:'已通过',REJECTED:'已驳回'})[s]||s
const load=async()=>{loading.value=true;const url=tab.value==='pending'?'/api/teacher/applications/pending':'/api/teacher/applications/reviewed';try{const r=await request.get(url);list.value=r.data||[]}catch{}finally{loading.value=false}}
const review=async(id,status)=>{try{await request.put(`/api/teacher/applications/${id}/review`,{status,comment:status==='FIRST_APPROVED'?'辅导员审核通过，提交院长审批':'辅导员审核驳回',reviewerId:'2'});ElMessage.success('操作成功');load()}catch{ElMessage.error('操作失败')}}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(245,158,11,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.custom-tabs :deep(.el-tabs__header){margin-bottom:16px}
.custom-tabs :deep(.el-tabs__item){font-size:14px;font-weight:500;color:#6B7280}
.custom-tabs :deep(.el-tabs__item.is-active){color:#5B9BD5;font-weight:600}
.custom-tabs :deep(.el-tabs__active-bar){background:#5B9BD5}
</style>
