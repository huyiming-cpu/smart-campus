<template>
  <div class="page"><h3>📰 新闻管理</h3>
    <el-button type="primary" @click="openAdd" style="margin-bottom:16px">新增新闻</el-button>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="title" label="标题" min-width="200"/>
      <el-table-column prop="category" label="分类" width="100"/>
      <el-table-column prop="source" label="来源" width="100"/>
      <el-table-column prop="viewCount" label="阅读" width="80"/>
      <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status }}</el-tag></template></el-table-column>
      <el-table-column prop="publishTime" label="发布时间" width="170"/>
      <el-table-column label="操作" width="160"><template #default="{row}"><el-button size="small" @click="openEdit(row)">编辑</el-button><el-button size="small" type="danger" @click="del(row)">删除</el-button></template></el-table-column>
    </el-table>
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
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
