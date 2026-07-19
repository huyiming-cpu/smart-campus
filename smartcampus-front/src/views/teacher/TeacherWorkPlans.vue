<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Notebook /></el-icon></div>
      <div><h2 class="page-title">个人工作计划</h2><p class="page-desc">管理教学、科研及个人工作计划</p></div>
    </div>
    <div class="toolbar"><el-button type="primary" @click="openAdd"><el-icon :size="16"><Plus /></el-icon> 新增计划</el-button></div>
    <div class="card" v-loading="loading">
      <el-table :data="list" border stripe>
        <el-table-column prop="title" label="标题" min-width="180"/>
        <el-table-column prop="planDate" label="计划日期" width="120"/>
        <el-table-column label="类型" width="90"><template #default="{row}"><el-tag size="small">{{ typeMap[row.planType]||row.planType }}</el-tag></template></el-table-column>
        <el-table-column label="优先级" width="90"><template #default="{row}"><el-tag size="small" :type="row.priority==='HIGH'?'danger':row.priority==='MEDIUM'?'warning':'info'">{{ priMap[row.priority]||row.priority }}</el-tag></template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag size="small" :type="row.status==='COMPLETED'?'success':row.status==='IN_PROGRESS'?'warning':'info'">{{ stMap[row.status]||row.status }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{row}">
            <el-button type="primary" plain size="small" @click="openEdit(row)"><el-icon :size="14"><Edit /></el-icon></el-button>
            <el-button type="danger" plain size="small" @click="handleDelete(row)"><el-icon :size="14"><Delete /></el-icon></el-button>
            <el-button v-if="row.status!=='COMPLETED'" type="success" size="small" @click="markDone(row)"><el-icon :size="14" style="margin-right:3px"><Check /></el-icon>完成</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog v-model="dv" :title="editing?'编辑计划':'新增计划'" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="form.title" placeholder="计划标题"/></el-form-item>
        <el-form-item label="计划日期" prop="planDate"><el-date-picker v-model="form.planDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD"/></el-form-item>
        <el-form-item label="类型"><el-select v-model="form.planType" style="width:100%"><el-option label="教学" value="TEACHING"/><el-option label="科研" value="RESEARCH"/><el-option label="行政" value="ADMIN"/><el-option label="个人" value="PERSONAL"/></el-select></el-form-item>
        <el-form-item label="优先级"><el-select v-model="form.priority" style="width:100%"><el-option label="高" value="HIGH"/><el-option label="中" value="MEDIUM"/><el-option label="低" value="LOW"/></el-select></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="form.content" type="textarea" :rows="4" placeholder="计划详细内容"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" :loading="saving" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { Notebook, Plus, Edit, Check, Delete } from '@element-plus/icons-vue'
const list=ref([]), loading=ref(false), dv=ref(false), editing=ref(false), saving=ref(false), formRef=ref(null), editId=ref(null)
const typeMap={TEACHING:'教学',RESEARCH:'科研',ADMIN:'行政',PERSONAL:'个人'}
const priMap={HIGH:'高',MEDIUM:'中',LOW:'低'}
const stMap={COMPLETED:'已完成',IN_PROGRESS:'进行中',PENDING:'待开始'}
const form=reactive({title:'',content:'',planDate:'',planType:'TEACHING',priority:'MEDIUM'})
const rules={title:[{required:true,message:'请输入标题',trigger:'blur'}],content:[{required:true,message:'请输入内容',trigger:'blur'}],planDate:[{required:true,message:'请选择日期',trigger:'change'}]}
const load=async()=>{loading.value=true;try{const r=await request.get('/api/teacher/work-plans');list.value=r.data||[]}catch{}finally{loading.value=false}}
const openAdd=()=>{editing.value=false;editId.value=null;form.planDate=new Date().toISOString().slice(0,10);dv.value=true}
const openEdit=(row)=>{editing.value=true;editId.value=row.id;Object.assign(form,{title:row.title,content:row.content,planDate:row.planDate,planType:row.planType,priority:row.priority});dv.value=true}
const resetForm=()=>{formRef.value?.resetFields();Object.assign(form,{title:'',content:'',planDate:'',planType:'TEACHING',priority:'MEDIUM'})}
const handleSave=async()=>{const valid=await formRef.value.validate().catch(()=>false);if(!valid)return;saving.value=true;try{if(editing.value){await request.put(`/api/teacher/work-plans/${editId.value}`,{...form})}else{await request.post('/api/teacher/work-plans',{...form})}ElMessage.success(editing.value?'修改成功':'新增成功');dv.value=false;load()}catch{ElMessage.error('保存失败')}finally{saving.value=false}}
const markDone=async(row)=>{try{await request.put(`/api/teacher/work-plans/${row.id}`,{...row,status:'COMPLETED'});ElMessage.success('已标记为完成');load()}catch{ElMessage.error('操作失败')}}
const handleDelete=async(row)=>{try{await ElMessageBox.confirm(`确定删除"${row.title}"？`,'删除确认',{type:'warning'});await request.delete(`/api/teacher/work-plans/${row.id}`);ElMessage.success('已删除');load()}catch{}}
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
</style>
