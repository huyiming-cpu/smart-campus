<template>
  <div class="page"><h3>📢 公告与新闻</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="系统公告" name="ann"/>
      <el-tab-pane label="学校新闻" name="news"/>
    </el-tabs>
    <div v-if="tab==='ann'">
      <el-table :data="announcements" v-loading="aLoad" @row-click="showAnn" highlight-current-row style="cursor:pointer">
        <el-table-column prop="title" label="标题" min-width="300">
          <template #default="{row}"><el-tag v-if="row.isTop" type="danger" size="small" effect="dark">置顶</el-tag> {{ row.title }}</template>
        </el-table-column>
        <el-table-column prop="createdTime" label="发布时间" width="170"/>
      </el-table>
    </div>
    <div v-if="tab==='news'">
      <el-select v-model="cat" @change="loadNews" placeholder="全部分类" clearable style="width:150px;margin-bottom:12px">
        <el-option v-for="c in cats" :key="c" :label="c" :value="c"/>
      </el-select>
      <el-table :data="news" v-loading="nLoad" @row-click="showNews" highlight-current-row style="cursor:pointer">
        <el-table-column prop="title" label="标题" min-width="250"/>
        <el-table-column prop="category" label="分类" width="100"/>
        <el-table-column prop="publishTime" label="时间" width="170"/>
      </el-table>
    </div>
    <el-dialog v-model="dv" :title="detail?.title" width="700px">
      <div v-if="detail" v-html="detail.content||'暂无内容'" style="line-height:1.8"></div>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const tab=ref('ann'),announcements=ref([]),aLoad=ref(false),news=ref([]),nLoad=ref(false),dv=ref(false),detail=ref(null)
const cat=ref(''),cats=['学校要闻','就业创业','校园生活']
const loadAnn=async()=>{aLoad.value=true;try{const r=await request.get('/api/notice/announcements');announcements.value=r.data||[]}catch{}finally{aLoad.value=false}}
const loadNews=async()=>{nLoad.value=true;try{const r=await request.get('/api/notice/news',{params:{category:cat.value||undefined}});news.value=r.data||[]}catch{}finally{nLoad.value=false}}
const showAnn=async(row)=>{try{const r=await request.get(`/api/notice/announcements/${row.id}`);detail.value=r.data;dv.value=true}catch{}}
const showNews=async(row)=>{try{const r=await request.get(`/api/notice/news/${row.id}`);detail.value=r.data;dv.value=true}catch{}}
onMounted(()=>{loadAnn();loadNews()})
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
