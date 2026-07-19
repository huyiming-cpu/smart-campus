<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#0EA5E9,#0284C7);"><el-icon :size="20" color="#fff"><Notification /></el-icon></div>
      <div><h2 class="page-title">内容管理</h2><p class="page-desc">管理校园公告与新闻内容，支持发布、编辑与置顶</p></div>
    </div>
    <div class="toolbar">
      <el-tabs v-model="tab">
        <el-tab-pane label="公告管理" name="ann"/>
        <el-tab-pane label="新闻管理" name="news"/>
      </el-tabs>
    </div>

    <!-- 公告 -->
    <div class="card" v-if="tab==='ann'" v-loading="aLoad">
      <el-button type="primary" size="small" @click="openAnn()" style="margin-bottom:12px">新增公告</el-button>
      <el-table :data="alist" stripe border size="small">
        <el-table-column prop="title" label="标题" min-width="200"/>
        <el-table-column label="置顶" width="70"><template #default="{row}"><el-switch :model-value="row.isTop===1" @change="toggleTop(row)" size="small"/></template></el-table-column>
        <el-table-column label="状态" width="70"><template #default="{row}"><el-tag :type="row.status==='PUBLISHED'?'success':'info'" size="small">{{ row.status==='PUBLISHED'?'已发':'草稿' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openAnn(row)">编辑</el-button><el-button size="small" type="danger" plain @click="delAnn(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>

    <!-- 新闻 -->
    <div class="card" v-if="tab==='news'" v-loading="nLoad">
      <el-button type="primary" size="small" @click="openNew()" style="margin-bottom:12px">新增新闻</el-button>
      <el-table :data="nlist" stripe border size="small">
        <el-table-column prop="title" label="标题" min-width="200"/>
        <el-table-column prop="category" label="分类" width="90"/>
        <el-table-column prop="viewCount" label="阅读" width="60"/>
        <el-table-column prop="publishTime" label="发布时间" width="150"/>
        <el-table-column label="操作" width="140"><template #default="{row}"><el-button size="small" type="primary" plain @click="openNew(row)">编辑</el-button><el-button size="small" type="danger" plain @click="delNew(row.id)">删除</el-button></template></el-table-column>
      </el-table>
    </div>

    <!-- 公告弹窗 -->
    <el-dialog :title="aEdit?'编辑公告':'新增公告'" v-model="aDv" width="700px" :close-on-click-modal="false">
      <el-form :model="af" label-width="80px" v-if="aDv" :key="'ann-'+aEid">
        <el-form-item label="标题"><el-input v-model="af.title"/></el-form-item>
        <el-form-item label="内容"><el-input v-model="af.content" type="textarea" :rows="10"/></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="目标角色"><el-input v-model="af.targetRoles" placeholder="STUDENT,TEACHER"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="置顶"><el-switch v-model="af.isTop"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="状态"><el-select v-model="af.status" style="width:100%"><el-option label="发布" value="PUBLISHED"/><el-option label="草稿" value="DRAFT"/></el-select></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="aDv=false">取消</el-button><el-button type="primary" @click="saveAnn" :loading="aSav">保存</el-button></template>
    </el-dialog>

    <!-- 新闻弹窗 -->
    <el-dialog :title="nEdit?'编辑新闻':'新增新闻'" v-model="nDv" width="700px" :close-on-click-modal="false">
      <el-form :model="nf" label-width="80px" v-if="nDv" :key="'news-'+nEid">
        <el-form-item label="标题"><el-input v-model="nf.title"/></el-form-item>
        <el-form-item label="摘要"><el-input v-model="nf.summary" type="textarea" :rows="2"/></el-form-item>
        <el-form-item label="内容"><el-input v-model="nf.content" type="textarea" :rows="10"/></el-form-item>
        <el-row :gutter="16">
          <el-col :span="8"><el-form-item label="分类"><el-select v-model="nf.category" style="width:100%"><el-option v-for="c in cats" :key="c" :label="c" :value="c"/></el-select></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="来源"><el-input v-model="nf.source"/></el-form-item></el-col>
          <el-col :span="8"><el-form-item label="封面图"><el-input v-model="nf.coverImage" placeholder="URL"/></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer><el-button @click="nDv=false">取消</el-button><el-button type="primary" @click="saveNew" :loading="nSav">保存</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Notification } from '@element-plus/icons-vue'
const tab=ref('ann'),cats=['学校要闻','就业创业','校园生活']
// 公告
const alist=ref([]),aLoad=ref(false),aDv=ref(false),aEdit=ref(false),aSav=ref(false),aEid=ref(null)
const af=ref({title:'',content:'',targetRoles:'',isTop:false,isUrgent:false,status:'PUBLISHED'})
const loadAnn=async()=>{aLoad.value=true;try{const r=await request.get('/api/admin/announcements');alist.value=r.data||[]}catch{}finally{aLoad.value=false}}
const stripHtml=(html)=>{if(!html)return'';const d=document.createElement('div');d.innerHTML=html;return d.textContent||d.innerText||''}
const openAnn=async(row)=>{if(row){aEdit.value=true;aEid.value=row.id;aDv.value=true;try{const r=await request.get(`/api/notice/announcements/${row.id}`);if(r.data)af.value={...r.data,content:stripHtml(r.data.content),isTop:r.data.isTop===1,isUrgent:r.data.isUrgent===1};else af.value={...row,content:stripHtml(row.content),isTop:row.isTop===1,isUrgent:row.isUrgent===1}}catch{af.value={...row,content:stripHtml(row.content),isTop:row.isTop===1,isUrgent:row.isUrgent===1}}}else{aEdit.value=false;aEid.value=null;af.value={title:'',content:'',targetRoles:'',isTop:false,isUrgent:false,status:'PUBLISHED'};aDv.value=true}}
const saveAnn=async()=>{aSav.value=true;const d={...af.value,isTop:af.value.isTop?1:0,isUrgent:af.value.isUrgent?1:0,publisherId:4,publisherName:'管理员'};try{if(aEdit.value)await request.put(`/api/admin/announcements/${aEid.value}`,d);else await request.post('/api/admin/announcements',d);ElMessage.success('保存成功');aDv.value=false;loadAnn()}catch{ElMessage.error('保存失败')}finally{aSav.value=false}}
const toggleTop=async(r)=>{try{await request.put(`/api/admin/announcements/${r.id}/top`);loadAnn()}catch{}}
const delAnn=async(id)=>{try{await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});await request.delete(`/api/admin/announcements/${id}`);loadAnn()}catch{}}
// 新闻
const nlist=ref([]),nLoad=ref(false),nDv=ref(false),nEdit=ref(false),nSav=ref(false),nEid=ref(null)
const nf=ref({title:'',summary:'',content:'',category:'学校要闻',source:'',coverImage:'',publishTime:''})
const loadNews=async()=>{nLoad.value=true;try{const r=await request.get('/api/notice/news');nlist.value=r.data||[]}catch{}finally{nLoad.value=false}}
const openNew=async(row)=>{if(row){nEdit.value=true;nEid.value=row.id;nDv.value=true;try{const r=await request.get(`/api/notice/news/${row.id}`);if(r.data)nf.value={...r.data,content:stripHtml(r.data.content)};else nf.value={...row,content:stripHtml(row.content)}}catch{nf.value={...row,content:stripHtml(row.content)}}}else{nEdit.value=false;nEid.value=null;nf.value={title:'',summary:'',content:'',category:'学校要闻',source:'',coverImage:'',publishTime:new Date().toISOString().slice(0,19)};nDv.value=true}}
const saveNew=async()=>{nSav.value=true;const d={...nf.value,publisherId:4,publisherName:'管理员'};try{if(nEdit.value)await request.put(`/api/admin/news/${nEid.value}`,d);else await request.post('/api/admin/news',d);ElMessage.success('保存成功');nDv.value=false;loadNews()}catch{ElMessage.error('保存失败')}finally{nSav.value=false}}
const delNew=async(id)=>{try{await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});await request.delete(`/api/admin/news/${id}`);loadNews()}catch{}}

onMounted(()=>{loadAnn();loadNews()})
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
