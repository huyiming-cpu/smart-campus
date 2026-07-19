<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><OfficeBuilding /></el-icon></div>
      <div><h2 class="page-title">固定资产管理</h2><p class="page-desc">管理校园固定资产信息，支持按学院筛选与资产录入</p></div>
    </div>
    <div class="toolbar">
      <el-select v-model="dept" @change="load" placeholder="全部学院" clearable style="width:200px">
        <el-option v-for="d in depts" :key="d" :label="d" :value="d"/>
      </el-select>
      <el-button type="primary" size="small" @click="openAdd" style="margin-left:8px">新增资产</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border size="small">
        <el-table-column prop="assetName" label="名称" min-width="140"/>
        <el-table-column prop="assetNo" label="编号" width="120"/>
        <el-table-column prop="assetType" label="类型" width="90"/>
        <el-table-column prop="model" label="型号" width="120"/>
        <el-table-column label="价格" width="90"><template #default="{row}">&yen;{{ row.price }}</template></el-table-column>
        <el-table-column prop="department" label="所属学院" width="100"/>
        <el-table-column prop="location" label="存放位置" width="120"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='NORMAL'?'success':'info'" size="small">{{ row.status==='NORMAL'?'正常':row.status }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button><el-button size="small" type="danger" plain @click="del(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>

    <el-dialog :title="isEdit?'编辑资产':'新增资产'" v-model="dv" width="550px">
      <el-form :model="f" label-width="80px">
        <el-form-item label="名称"><el-input v-model="f.assetName"/></el-form-item>
        <el-form-item label="编号"><el-input v-model="f.assetNo"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="f.assetType"><el-option v-for="t in types" :key="t" :label="t" :value="t"/></el-select></el-form-item>
        <el-form-item label="型号"><el-input v-model="f.model"/></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="f.price" :min="0"/></el-form-item>
        <el-form-item label="所属学院"><el-select v-model="f.department" filterable allow-create><el-option v-for="d in depts" :key="d" :label="d" :value="d"/></el-select></el-form-item>
        <el-form-item label="存放位置"><el-input v-model="f.location"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="f.status"><el-option label="正常" value="NORMAL"/><el-option label="报废" value="SCRAPPED"/></el-select></el-form-item>
        <el-form-item label="备注"><el-input v-model="f.remark" type="textarea" :rows="2"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { OfficeBuilding } from '@element-plus/icons-vue'
const list=ref([]),loading=ref(false),dept=ref(''),depts=['计算机学院','经管学院','理学院','校办'],types=['IT设备','教学设备','家具','电器','其他']
const dv=ref(false),isEdit=ref(false),eid=ref(null),f=ref({})
const load=async()=>{loading.value=true;try{const r=await request.get('/api/admin/assets',{params:{department:dept.value||undefined}});list.value=r.data||[]}catch{}finally{loading.value=false}}
const openAdd=()=>{isEdit.value=false;eid.value=null;f.value={assetName:'',assetNo:'',assetType:'IT设备',model:'',price:0,department:dept.value||'计算机学院',location:'',status:'NORMAL',remark:''};dv.value=true}
const openEdit=(row)=>{isEdit.value=true;eid.value=row.id;f.value={...row};dv.value=true}
const save=async()=>{try{if(isEdit.value)await request.put(`/api/admin/assets/${eid.value}`,f.value);else await request.post('/api/admin/assets',f.value);ElMessage.success('OK');dv.value=false;load()}catch{ElMessage.error('失败')}}
const del=async(id)=>{try{await request.delete(`/api/admin/assets/${id}`);ElMessage.success('已删除');load()}catch{}}
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
