<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Bell /></el-icon></div>
      <div><h2 class="page-title">公告与新闻</h2><p class="page-desc">查看系统公告与学校最新动态</p></div>
    </div>
    <div class="card">
      <el-tabs v-model="tab" class="custom-tabs">
        <el-tab-pane label="系统公告" name="ann"/>
        <el-tab-pane label="学校新闻" name="news"/>
      </el-tabs>
      <div v-if="tab==='ann'">
        <el-table :data="announcements" v-loading="aLoad" stripe @row-click="showAnn" highlight-current-row style="cursor:pointer">
          <el-table-column prop="title" label="标题" min-width="300">
            <template #default="{row}"><el-tag v-if="row.isTop" type="danger" size="small" effect="dark">置顶</el-tag> {{ row.title }}</template>
          </el-table-column>
          <el-table-column prop="createdTime" label="发布时间" width="170"/>
        </el-table>
      </div>
      <div v-if="tab==='news'">
        <div class="toolbar">
          <el-select v-model="cat" @change="loadNews" placeholder="全部分类" clearable style="width:150px">
            <el-option v-for="c in cats" :key="c" :label="c" :value="c"/>
          </el-select>
        </div>
        <el-table :data="news" v-loading="nLoad" stripe @row-click="showNews" highlight-current-row style="cursor:pointer">
          <el-table-column prop="title" label="标题" min-width="250"/>
          <el-table-column prop="category" label="分类" width="100"/>
          <el-table-column prop="publishTime" label="时间" width="170"/>
        </el-table>
      </div>
    </div>
    <el-dialog v-model="dv" :title="detail?.title" width="700px">
      <div v-if="detail" v-html="detail.content||'暂无内容'" style="line-height:1.8"></div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { Bell } from '@element-plus/icons-vue'
const tab=ref('ann'),announcements=ref([]),aLoad=ref(false),news=ref([]),nLoad=ref(false),dv=ref(false),detail=ref(null)
const cat=ref(''),cats=['学校要闻','就业创业','校园生活']
const loadAnn=async()=>{aLoad.value=true;try{const r=await request.get('/api/notice/announcements');announcements.value=r.data||[]}catch{}finally{aLoad.value=false}}
const loadNews=async()=>{nLoad.value=true;try{const r=await request.get('/api/notice/news',{params:{category:cat.value||undefined}});news.value=r.data||[]}catch{}finally{nLoad.value=false}}
const showAnn=async(row)=>{try{const r=await request.get(`/api/notice/announcements/${row.id}`);detail.value=r.data;dv.value=true}catch{}}
const showNews=async(row)=>{try{const r=await request.get(`/api/notice/news/${row.id}`);detail.value=r.data;dv.value=true}catch{}}
onMounted(()=>{loadAnn();loadNews()})
</script>
<style scoped>
.page{padding:20px 24px;max-width:1100px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;justify-content:flex-end}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
.custom-tabs :deep(.el-tabs__header){margin-bottom:16px}
.custom-tabs :deep(.el-tabs__item){font-size:14px;font-weight:500;color:#6B7280}
.custom-tabs :deep(.el-tabs__item.is-active){color:#5B9BD5;font-weight:600}
.custom-tabs :deep(.el-tabs__active-bar){background:#5B9BD5}
</style>
