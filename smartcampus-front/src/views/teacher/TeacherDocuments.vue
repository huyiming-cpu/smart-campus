<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><DocumentChecked /></el-icon></div>
      <div><h2 class="page-title">待办公文</h2><p class="page-desc">提交与管理个人公文申请与通知</p></div>
    </div>
    <div class="toolbar"><el-button type="primary" @click="openSubmitDialog()"><el-icon :size="16"><Plus /></el-icon> 提交新公文</el-button></div>
    <div class="card" v-loading="loading">
      <el-table :data="docList" border stripe>
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip/>
        <el-table-column label="类型" width="100"><template #default="{row}"><el-tag :type="row.documentType==='NOTICE'?'warning':''" size="small">{{ row.documentType==='NOTICE'?'通知':'申请' }}</el-tag></template></el-table-column>
        <el-table-column prop="department" label="部门" width="130"/>
        <el-table-column label="提交时间" width="180"><template #default="{row}">{{ row.createTime||'-' }}</template></el-table-column>
        <el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{row}">
            <el-button type="primary" plain size="small" @click="viewDetail(row)">查看</el-button>
            <el-button type="danger" plain size="small" @click="handleDelete(row)" :disabled="row.status==='APPROVED'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!docList.length && !loading" class="empty-tip">暂无公文</div>
    </div>
    <el-dialog v-model="submitVisible" title="提交新公文" width="560px" @close="resetSubmitForm">
      <el-form ref="submitFormRef" :model="submitForm" :rules="submitRules" label-width="80px">
        <el-form-item label="标题" prop="title"><el-input v-model="submitForm.title" placeholder="请输入公文标题"/></el-form-item>
        <el-form-item label="类型" prop="documentType"><el-select v-model="submitForm.documentType" style="width:100%"><el-option label="申请" value="APPLY"/><el-option label="通知" value="NOTICE"/></el-select></el-form-item>
        <el-form-item label="部门" prop="department"><el-input v-model="submitForm.department" placeholder="请输入部门名称"/></el-form-item>
        <el-form-item label="内容" prop="content"><el-input v-model="submitForm.content" type="textarea" :rows="5" placeholder="请输入公文内容"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="submitVisible=false">取消</el-button><el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button></template>
    </el-dialog>
    <el-dialog v-model="viewVisible" title="公文详情" width="560px"><template v-if="viewData"><el-descriptions :column="1" border><el-descriptions-item label="标题">{{viewData.title}}</el-descriptions-item><el-descriptions-item label="类型"><el-tag :type="viewData.documentType==='NOTICE'?'warning':''" size="small">{{viewData.documentType==='NOTICE'?'通知':'申请'}}</el-tag></el-descriptions-item><el-descriptions-item label="部门">{{viewData.department}}</el-descriptions-item><el-descriptions-item label="状态"><el-tag :type="statusTag(viewData.status)" size="small">{{statusText(viewData.status)}}</el-tag></el-descriptions-item><el-descriptions-item label="提交时间">{{viewData.createTime||'-'}}</el-descriptions-item><el-descriptions-item label="内容">{{viewData.content}}</el-descriptions-item></el-descriptions></template></el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listMyDocuments, submitDocument, deleteDocument } from '@/api/document'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentChecked, Plus } from '@element-plus/icons-vue'
const loading=ref(false), submitting=ref(false), docList=ref([]), submitVisible=ref(false), submitFormRef=ref(null), viewVisible=ref(false), viewData=ref(null)
const submitForm=reactive({title:'',content:'',documentType:'APPLY',department:''})
const submitRules={title:[{required:true,message:'请输入标题',trigger:'blur'}],content:[{required:true,message:'请输入内容',trigger:'blur'}],documentType:[{required:true,message:'请选择类型',trigger:'change'}],department:[{required:true,message:'请输入部门',trigger:'blur'}]}
const statusTag=(s)=>{if(s==='APPROVED')return'success';if(s==='REJECTED')return'danger';return'info'}
const statusText=(s)=>({PENDING:'待审批',APPROVED:'已通过',REJECTED:'已驳回'})[s]||s||'-'
const loadData=async()=>{loading.value=true;try{const res=await listMyDocuments();docList.value=res.data||[]}catch{ElMessage.error('加载公文列表失败')}finally{loading.value=false}}
const openSubmitDialog=()=>{submitVisible.value=true}
const resetSubmitForm=()=>{submitForm.title='';submitForm.content='';submitForm.documentType='APPLY';submitForm.department='';submitFormRef.value?.resetFields()}
const handleSubmit=async()=>{const valid=await submitFormRef.value.validate().catch(()=>false);if(!valid)return;submitting.value=true;try{await submitDocument({...submitForm});ElMessage.success('提交成功');submitVisible.value=false;loadData()}catch{ElMessage.error('提交失败')}finally{submitting.value=false}}
const viewDetail=(row)=>{viewData.value=row;viewVisible.value=true}
const handleDelete=async(row)=>{try{await ElMessageBox.confirm(`确定删除公文"${row.title}"吗？`,'删除确认',{confirmButtonText:'确定',cancelButtonText:'取消',type:'warning'});await deleteDocument(row.id);ElMessage.success('删除成功');loadData()}catch{}}
onMounted(()=>{loadData()})
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.empty-tip{text-align:center;padding:40px 0;color:#C4C9D1;font-size:14px}
</style>
