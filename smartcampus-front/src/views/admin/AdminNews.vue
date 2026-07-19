<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Document /></el-icon></div>
      <div><h2 class="page-title">新闻管理</h2><p class="page-desc">管理校园新闻内容，支持分类发布与编辑</p></div>
    </div>
    <div class="toolbar">
      <el-button type="primary" @click="openAdd"><el-icon style="margin-right:4px"><Plus /></el-icon>新增新闻</el-button>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="list" stripe border>
        <el-table-column prop="title" label="标题" min-width="200"/>
        <el-table-column prop="category" label="分类" width="100"/>
        <el-table-column prop="source" label="来源" width="100"/>
        <el-table-column prop="viewCount" label="阅读" width="80"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status }}</el-tag></template></el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="170"/>
        <el-table-column label="操作" width="160"><template #default="{row}"><el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button><el-button size="small" type="danger" plain @click="del(row)">删除</el-button></template></el-table-column>
      </el-table>
    </div>
    <el-dialog :title="isEdit?'编辑新闻':'新增新闻'" v-model="dv" width="700px">
      <el-form :model="f" label-width="80px">
        <el-form-item label="标题"><el-input v-model="f.title"/></el-form-item>
        <el-form-item label="摘要"><el-input v-model="f.summary" type="textarea" :rows="2"/></el-form-item>
        <el-form-item label="内容"><el-input v-model="f.content" type="textarea" :rows="8"/></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="分类"><el-select v-model="f.category" style="width:100%"><el-option v-for="c in cats" :key="c" :label="c" :value="c"/></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="来源"><el-input v-model="f.source"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="状态"><el-select v-model="f.status" style="width:100%"><el-option label="发布" value="PUBLISHED"/><el-option label="草稿" value="DRAFT"/></el-select></el-form-item></el-col>
        </el-row>
        <el-form-item label="封面图URL"><el-input v-model="f.coverImage"/></el-form-item>
      </el-form>
      <template #footer><el-button @click="dv=false">取消</el-button><el-button type="primary" @click="save" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus } from '@element-plus/icons-vue'
const list=ref([]),loading=ref(false),dv=ref(false),isEdit=ref(false),saving=ref(false),eid=ref(null)
const cats=['学校要闻','就业创业','校园生活']
const f=ref({title:'',summary:'',content:'',category:'学校要闻',source:'',coverImage:'',status:'PUBLISHED',publishTime:''})
const load=async()=>{loading.value=true;try{const r=await request.get('/api/notice/news');list.value=r.data||[]}catch{}finally{loading.value=false}}
const openAdd=()=>{isEdit.value=false;eid.value=null;f.value={title:'',summary:'',content:'',category:'学校要闻',source:'',coverImage:'',status:'PUBLISHED',publishTime:new Date().toISOString().slice(0,19)};dv.value=true}
const openEdit=(row)=>{isEdit.value=true;eid.value=row.id;f.value={...row};dv.value=true}
const save=async()=>{
  saving.value=true
  const data={...f.value,publisherId:4,publisherName:'管理员'}
  try{if(isEdit.value)await request.put(`/api/admin/news/${eid.value}`,data);else await request.post('/api/admin/news',data);ElMessage.success(isEdit.value?'更新成功':'创建成功');dv.value=false;load()}
  catch{ElMessage.error('保存失败')}
  finally{saving.value=false}
}
const del=async(row)=>{try{await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});await request.delete(`/api/admin/news/${row.id}`);ElMessage.success('已删除');load()}catch{}}
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
