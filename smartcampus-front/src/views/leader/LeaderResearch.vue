<template>
  <div class="page"><h3>🔬 科研总览</h3>
    <el-tabs v-model="tab" @tab-change="load">
      <el-tab-pane label="全部" name=""/><el-tab-pane label="进行中" name="IN_PROGRESS"/><el-tab-pane label="已完成" name="COMPLETED"/>
    </el-tabs>
    <el-table :data="list" v-loading="loading" border @row-click="showDetail" highlight-current-row style="cursor:pointer">
      <el-table-column prop="projectName" label="项目名称" min-width="200"/>
      <el-table-column prop="teacherName" label="负责人" width="80"/>
      <el-table-column label="类型" width="80"><template #default="{row}"><el-tag size="small">{{ row.projectType }}</el-tag></template></el-table-column>
      <el-table-column label="经费" width="100"><template #default="{row}">¥{{ row.fundAmount }}</template></el-table-column>
      <el-table-column prop="startDate" label="开始" width="110"/>
      <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="row.status==='COMPLETED'?'success':'warning'" size="small">{{ row.status==='IN_PROGRESS'?'进行中':row.status==='COMPLETED'?'已完成':row.status }}</el-tag></template></el-table-column>
    </el-table>
    <el-dialog v-model="dv" title="项目详情" width="600px">
      <template v-if="detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称" :span="2">{{ detail.projectName }}</el-descriptions-item>
          <el-descriptions-item label="负责人">{{ detail.teacherName }}</el-descriptions-item>
          <el-descriptions-item label="编号">{{ detail.projectCode }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ detail.projectType }}</el-descriptions-item>
          <el-descriptions-item label="经费">¥{{ detail.fundAmount }}</el-descriptions-item>
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
import request from '@/utils/request'
const tab=ref(''),list=ref([]),loading=ref(false),dv=ref(false),detail=ref(null)
const load=async()=>{loading.value=true;try{const r=await request.get('/api/leader/research',{params:{status:tab.value||undefined}});list.value=r.data||[]}catch{}finally{loading.value=false}}
const showDetail=(row)=>{detail.value=row;dv.value=true}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
