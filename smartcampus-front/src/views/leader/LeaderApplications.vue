<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#6366F1,#4F46E5);">
        <el-icon :size="20" color="#fff"><DocumentChecked /></el-icon>
      </div>
      <div>
        <h2 class="page-title">奖助贷审批</h2>
        <p class="page-desc">管理奖学金、助学金与助学贷款审批事项</p>
      </div>
    </div>

    <div class="toolbar">
      <el-tabs v-model="tab" @tab-change="load">
        <el-tab-pane label="待审批" name="pending"/>
        <el-tab-pane label="已审批" name="reviewed"/>
      </el-tabs>
    </div>

    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border>
        <el-table-column prop="studentName" label="姓名" width="80"/>
        <el-table-column prop="studentNo" label="学号" width="120"/>
        <el-table-column prop="department" label="学院" width="100"/>
        <el-table-column prop="className" label="班级" width="100"/>
        <el-table-column label="类型" width="80">
          <template #default="{row}">
            <el-tag size="small">{{ row.applicationType==='SCHOLARSHIP'?'奖学金':row.applicationType==='SUBSIDY'?'助学金':row.applicationType==='LOAN'?'贷款':row.applicationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="150"/>
        <el-table-column label="状态" width="90">
          <template #default="{row}"><el-tag type="warning" size="small">待院长批</el-tag></template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{row}">
            <el-button type="success" size="small" @click="approve(row.id,'APPROVED')">通过</el-button>
            <el-button type="danger" size="small" @click="approve(row.id,'REJECTED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { DocumentChecked } from '@element-plus/icons-vue'
import request from '@/utils/request'

const tab=ref('pending'), list=ref([]), loading=ref(false)

const load=async()=>{
  loading.value=true
  const url=tab.value==='pending'?'/api/leader/applications/pending':'/api/leader/applications/reviewed'
  try{const r=await request.get(url);list.value=r.data||[]}catch{}
  finally{loading.value=false}
}

const approve=async(id,status)=>{
  try{await request.put(`/api/leader/applications/${id}/final-approve`,{status,comment:status==='APPROVED'?'院长审批通过':'院长审批驳回',reviewerId:'3'});ElMessage.success('操作成功');load()}
  catch{ElMessage.error('操作失败')}
}

onMounted(load)
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:16px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;margin:0;color:#303133}
.page-desc{font-size:13px;color:#909399;margin:4px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
