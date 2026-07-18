<template>
  <div class="page"><h3>📄 公文审批</h3>
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane label="待审批" name="pending"/>
      <el-tab-pane label="已审批" name="approved"/>
    </el-tabs>
    <el-table :data="list" v-loading="loading" border @row-click="showDetail" highlight-current-row style="cursor:pointer">
      <el-table-column prop="title" label="标题" min-width="180"/>
      <el-table-column prop="submitterName" label="提交人" width="80"/>
      <el-table-column prop="department" label="部门" width="100"/>
      <el-table-column label="类型" width="80"><template #default="{row}"><el-tag size="small">{{ row.documentType==='APPLY'?'申请':'通知' }}</el-tag></template></el-table-column>
      <el-table-column prop="submitTime" label="提交时间" width="170"/>
      <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.status==='APPROVED'?'success':row.status==='REJECTED'?'danger':'warning'" size="small">{{ row.status==='PENDING'?'待审':row.status==='APPROVED'?'已通过':'已驳回' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160" v-if="tab==='pending'">
        <template #default="{row}"><el-button type="success" size="small" @click.stop="approve(row,'APPROVED')">通过</el-button><el-button type="danger" size="small" @click.stop="approve(row,'REJECTED')">驳回</el-button></template>
      </el-table-column>
    </el-table>
    <el-dialog v-model="dv" title="公文详情" width="600px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题" :span="2">{{ detail.title }}</el-descriptions-item>
          <el-descriptions-item label="提交人">{{ detail.submitterName }}</el-descriptions-item>
          <el-descriptions-item label="部门">{{ detail.department }}</el-descriptions-item>
          <el-descriptions-item label="提交时间">{{ detail.submitTime }}</el-descriptions-item>
          <el-descriptions-item label="状态">{{ detail.status }}</el-descriptions-item>
          <el-descriptions-item label="审批意见" :span="2">{{ detail.approvalComment||'暂无' }}</el-descriptions-item>
          <el-descriptions-item label="内容" :span="2"><div style="white-space:pre-wrap">{{ detail.content }}</div></el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
const tab=ref('pending'),list=ref([]),loading=ref(false),dv=ref(false),detail=ref(null)
const load=async()=>{loading.value=true;try{const r=await request.get(`/api/leader/documents/${tab.value}`);list.value=r.data||[]}catch{}finally{loading.value=false}}
const showDetail=(row)=>{detail.value=row;dv.value=true}
const approve=async(row,status)=>{try{await request.put(`/api/leader/documents/${row.id}/approve`,{status,approverName:'徐娅',comment:status==='APPROVED'?'同意':'驳回'});ElMessage.success('操作成功');load()}catch{ElMessage.error('操作失败')}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
