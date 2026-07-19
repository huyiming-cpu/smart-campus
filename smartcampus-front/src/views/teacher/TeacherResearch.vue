<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><DataAnalysis /></el-icon></div>
      <div><h2 class="page-title">科研情况管理</h2><p class="page-desc">管理个人科研项目、成果与经费信息</p></div>
    </div>
    <div class="toolbar"><el-button type="primary" @click="openDialog()"><el-icon :size="16"><Plus /></el-icon> 新增科研项目</el-button></div>
    <div class="card" v-loading="loading">
      <el-table :data="list" border stripe>
        <el-table-column prop="projectName" label="项目名称" min-width="190" show-overflow-tooltip/>
        <el-table-column prop="projectCode" label="项目编号" width="150"/>
        <el-table-column prop="projectType" label="项目类型" width="110"/>
        <el-table-column label="经费(万)" width="110"><template #default="{row}">{{ row.fund }}</template></el-table-column>
        <el-table-column label="开始" width="110"><template #default="{row}">{{ row.startDate?.slice(0,10) }}</template></el-table-column>
        <el-table-column label="结束" width="110"><template #default="{row}">{{ row.endDate?.slice(0,10) }}</template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag size="small" :type="row.status==='已完成'||row.status==='已结题'?'success':row.status==='暂停'?'warning':''">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="members" label="成员" min-width="150" show-overflow-tooltip/>
        <el-table-column prop="achievements" label="成果" min-width="170" show-overflow-tooltip/>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{row}"><el-button type="primary" plain size="small" @click="openDialog(row)">编辑</el-button><el-button type="danger" plain size="small" @click="handleDelete(row)">删除</el-button></template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dv" :title="editing?'编辑科研项目':'新增科研项目'" width="640px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="项目名称" prop="projectName"><el-input v-model="form.projectName" placeholder="项目名称"/></el-form-item>
        <el-form-item label="项目编号" prop="projectCode"><el-input v-model="form.projectCode" placeholder="项目编号"/></el-form-item>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="项目类型"><el-select v-model="form.projectType" style="width:100%"><el-option v-for="t in types" :key="t" :label="t" :value="t"/></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="经费(万元)"><el-input-number v-model="form.fund" :min="0" :precision="2" style="width:100%"/></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="开始日期"><el-date-picker v-model="form.startDate" type="date" placeholder="开始日期" style="width:100%" value-format="YYYY-MM-DD"/></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="结束日期"><el-date-picker v-model="form.endDate" type="date" placeholder="结束日期" style="width:100%" value-format="YYYY-MM-DD"/></el-form-item></el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="状态"><el-select v-model="form.status" style="width:100%"><el-option v-for="s in statuses" :key="s" :label="s" :value="s"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="成员"><el-input v-model="form.members" type="textarea" :rows="2" placeholder="项目成员，逗号分隔"/></el-form-item>
        <el-form-item label="成果"><el-input v-model="form.achievements" type="textarea" :rows="2" placeholder="项目成果描述"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" :loading="saving" @click="handleSave">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listMyResearch, saveResearch, updateResearch, deleteResearch } from '@/api/teacher'
import { DataAnalysis, Plus } from '@element-plus/icons-vue'
const list=ref([]), loading=ref(false), dv=ref(false), editing=ref(false), saving=ref(false), formRef=ref(null), editId=ref(null)
const types=['国家级','省部级','市厅级','校级','横向课题','其他']
const statuses=['进行中','已完成','已结题','暂停']
const form=reactive({projectName:'',projectCode:'',projectType:'',fund:null,startDate:'',endDate:'',status:'',members:'',achievements:''})
const rules={projectName:[{required:true,message:'请输入项目名称',trigger:'blur'}],projectCode:[{required:true,message:'请输入项目编号',trigger:'blur'}]}
const load=async()=>{loading.value=true;try{const r=await listMyResearch();list.value=r.data||[]}catch{}finally{loading.value=false}}
const openDialog=(row)=>{if(row){editing.value=true;editId.value=row.id;Object.assign(form,{...row})}else{editing.value=false;editId.value=null;Object.assign(form,{projectName:'',projectCode:'',projectType:'',fund:null,startDate:'',endDate:'',status:'',members:'',achievements:''})}dv.value=true}
const resetForm=()=>{formRef.value?.resetFields()}
const handleSave=async()=>{const valid=await formRef.value.validate().catch(()=>false);if(!valid)return;saving.value=true;try{if(editing.value){await updateResearch(editId.value,{...form})}else{await saveResearch({...form})}ElMessage.success(editing.value?'修改成功':'新增成功');dv.value=false;load()}catch{ElMessage.error('保存失败')}finally{saving.value=false}}
const handleDelete=async(row)=>{try{await ElMessageBox.confirm(`确定删除"${row.projectName}"？`,'删除确认',{type:'warning'});await deleteResearch(row.id);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1300px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
</style>
