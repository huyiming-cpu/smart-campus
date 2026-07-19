<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#6366F1,#4F46E5);">
        <el-icon :size="20" color="#fff"><DataAnalysis /></el-icon>
      </div>
      <div>
        <h2 class="page-title">科研总览</h2>
        <p class="page-desc">查看全校科研项目进度与经费概况</p>
      </div>
    </div>

    <div class="toolbar">
      <el-tabs v-model="tab" @tab-change="load">
        <el-tab-pane label="全部" name=""/>
        <el-tab-pane label="进行中" name="IN_PROGRESS"/>
        <el-tab-pane label="已完成" name="COMPLETED"/>
      </el-tabs>
    </div>

    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border @row-click="showDetail" highlight-current-row style="cursor:pointer">
        <el-table-column prop="projectName" label="项目名称" min-width="200"/>
        <el-table-column prop="teacherName" label="负责人" width="80"/>
        <el-table-column label="类型" width="80">
          <template #default="{row}"><el-tag size="small">{{ row.projectType }}</el-tag></template>
        </el-table-column>
        <el-table-column label="经费" width="100">
          <template #default="{row}">&yen;{{ row.fundAmount }}</template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始" width="110"/>
        <el-table-column label="状态" width="90">
          <template #default="{row}"><el-tag :type="row.status==='COMPLETED'?'success':'warning'" size="small">{{ row.status==='IN_PROGRESS'?'进行中':row.status==='COMPLETED'?'已完成':row.status }}</el-tag></template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dv" title="项目详情" width="600px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称" :span="2">{{ detail.projectName }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ detail.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="编号">{{ detail.projectCode }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ detail.projectType }}</el-descriptions-item>
          <el-descriptions-item label="经费">&yen;{{ detail.fundAmount }}</el-descriptions-item>
          <el-descriptions-item label="开始">{{ detail.startDate }}</el-descriptions-item>
          <el-descriptions-item label="结束">{{ detail.endDate }}</el-descriptions-item>
          <el-descriptions-item label="成员" :span="2">{{ detail.members||'无' }}</el-descriptions-item>
          <el-descriptions-item label="成果" :span="2">{{ detail.achievements||'暂无' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { DataAnalysis } from '@element-plus/icons-vue'
import request from '@/utils/request'

const tab=ref(''),list=ref([]),loading=ref(false),dv=ref(false),detail=ref(null)

const load=async()=>{
  loading.value=true
  try{const r=await request.get('/api/leader/research',{params:{status:tab.value||undefined}});list.value=r.data||[]}catch{}
  finally{loading.value=false}
}

const showDetail=(row)=>{detail.value=row;dv.value=true}

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
