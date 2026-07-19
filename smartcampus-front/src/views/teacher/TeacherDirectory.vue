<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#5B9BD5,#4A8AD4);"><el-icon :size="20" color="#fff"><Phone /></el-icon></div>
      <div><h2 class="page-title">教职工通讯录</h2><p class="page-desc">快速查找同事联系方式</p></div>
    </div>
    <div class="toolbar">
      <el-input v-model="search" placeholder="搜索姓名/工号" clearable style="width:240px" :prefix-icon="Search"/>
      <el-select v-model="dept" placeholder="按部门筛选" clearable @change="load" style="width:200px"><el-option v-for="d in departments" :key="d" :label="d" :value="d"/></el-select>
    </div>
    <div class="card" v-loading="loading">
      <el-table :data="filtered" border stripe>
        <el-table-column prop="name" label="姓名" width="90"/>
        <el-table-column prop="identityNumber" label="工号" width="130"/>
        <el-table-column prop="gender" label="性别" width="70"/>
        <el-table-column prop="department" label="部门" width="130"/>
        <el-table-column prop="title" label="职称" width="110"/>
        <el-table-column prop="phone" label="手机号" width="130"/>
        <el-table-column prop="email" label="邮箱" min-width="180"/>
      </el-table>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { Phone, Search } from '@element-plus/icons-vue'
const list=ref([]), loading=ref(false), search=ref(''), dept=ref(''), departments=ref([])
const load=async()=>{loading.value=true;try{const params={};if(dept.value)params.department=dept.value;const r=await request.get('/api/teacher/directory',{params});list.value=r.data||[];const deptSet=new Set(list.value.map(x=>x.department).filter(Boolean));departments.value=[...deptSet].sort()}catch{list.value=[]}finally{loading.value=false}}
const filtered=computed(()=>{if(!search.value)return list.value;const kw=search.value.toLowerCase();return list.value.filter(x=>(x.name||'').includes(kw)||(x.identityNumber||'').includes(kw))})
onMounted(load)
</script>
<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;box-shadow:0 4px 12px rgba(91,155,213,.25)}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{margin-bottom:16px;display:flex;align-items:center;gap:12px}
.card{background:#fff;border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02)}
</style>
