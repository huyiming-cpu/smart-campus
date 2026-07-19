<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Monitor /></el-icon></div>
      <div><h2 class="page-title">实验室管理</h2><p class="page-desc">管理校园实验室资源，支持多学院筛选与设备关联</p></div>
    </div>
    <div class="toolbar">
      <el-select v-model="dept" @change="load" placeholder="全部学院" clearable style="width:200px">
        <el-option v-for="d in depts" :key="d" :label="d" :value="d"/>
      </el-select>
      <el-button type="primary" size="small" @click="openAdd" style="margin-left:8px">新增实验室</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border size="small">
        <el-table-column prop="labName" label="名称" min-width="150"/><el-table-column prop="labCode" label="代码" width="80"/>
        <el-table-column prop="labType" label="类型" width="80"/><el-table-column prop="department" label="学院" width="100"/>
        <el-table-column prop="location" label="位置" width="100"/><el-table-column prop="capacity" label="容量" width="60"/>
        <el-table-column label="主要设备" min-width="200"><template #default="{row}">{{ getAssets(row.id) }}</template></el-table-column>
        <el-table-column prop="managerName" label="负责人" width="80"/>
        <el-table-column label="状态" width="70"><template #default="{row}"><el-tag :type="row.status==='OPEN'?'success':'warning'" size="small">{{ row.status==='OPEN'?'开放':'维护' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button><el-button size="small" type="danger" plain @click="del(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <el-dialog :title="isEdit?'编辑实验室':'新增实验室'" v-model="dv" width="550px">
      <el-form :model="f" label-width="80px">
        <el-form-item label="名称"><el-input v-model="f.labName"/></el-form-item>
        <el-form-item label="代码"><el-input v-model="f.labCode"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="f.labType"><el-option label="计算机" value="COMPUTER"/><el-option label="物理" value="PHYSICS"/><el-option label="商科" value="BUSINESS"/></el-select></el-form-item>
        <el-form-item label="学院"><el-select v-model="f.department" filterable allow-create><el-option v-for="d in depts" :key="d" :label="d" :value="d"/></el-select></el-form-item>
        <el-form-item label="位置"><el-input v-model="f.location"/></el-form-item><el-form-item label="容量"><el-input-number v-model="f.capacity"/></el-form-item>
        <el-form-item label="面积"><el-input-number v-model="f.area"/></el-form-item><el-form-item label="负责人"><el-input v-model="f.managerName"/></el-form-item>
        <el-form-item label="开放时间"><el-input v-model="f.openTime"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="f.status"><el-option label="开放" value="OPEN"/><el-option label="维护" value="MAINTENANCE"/></el-select></el-form-item>
        <el-form-item label="设备描述"><el-input v-model="f.equipmentDesc" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { Monitor } from '@element-plus/icons-vue'
const list=ref([]),loading=ref(false),assets=ref([]),dept=ref(''),depts=['计算机学院','经管学院','理学院','校办']
const dv=ref(false),isEdit=ref(false),eid=ref(null),f=ref({})
const getAssets=(labId)=>{const items=assets.value.filter(a=>a.labId===labId);return items.length?items.map(a=>a.assetName).join('、'):'无'}
const load=async()=>{loading.value=true;try{const r=await request.get('/api/admin/labs',{params:{department:dept.value||undefined}});list.value=r.data||[]}catch{};try{const r=await request.get('/api/admin/assets');assets.value=r.data||[]}catch{};loading.value=false}
const openAdd=()=>{isEdit.value=false;f.value={labName:'',labCode:'',labType:'COMPUTER',department:dept.value||'计算机学院',location:'',capacity:40,area:80,managerId:'',managerName:'',openTime:'周一至周五 8:00-22:00',status:'OPEN',equipmentDesc:''};dv.value=true}
const openEdit=(row)=>{isEdit.value=true;eid.value=row.id;f.value={...row};dv.value=true}
const save=async()=>{try{if(isEdit.value)await request.put(`/api/admin/labs/${eid.value}`,f.value);else await request.post('/api/admin/labs',f.value);ElMessage.success('OK');dv.value=false;load()}catch{ElMessage.error('失败')}}
const del=async(id)=>{try{await request.delete(`/api/admin/labs/${id}`);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;margin:0;line-height:1.3;color:#1a1a2e}
.page-desc{font-size:13px;color:#8c8c8c;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
