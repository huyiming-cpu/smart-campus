<template>
  <div class="page">
    <div class="page-head">
      <div class="page-icon" style="background:linear-gradient(135deg,#6366F1,#4F46E5);"><el-icon :size="20" color="#fff"><Phone /></el-icon></div>
      <div><h2 class="page-title">教职工通讯录</h2><p class="page-desc">查看全校教职工联系方式与部门信息</p></div>
    </div>

    <div class="toolbar">
      <el-input v-model="search" placeholder="搜索姓名/工号" clearable style="width:220px" :prefix-icon="Search"/>
      <el-select v-model="dept" @change="load" placeholder="全部部门" clearable style="width:180px">
        <el-option v-for="d in depts" :key="d" :label="d" :value="d"/>
      </el-select>
      <el-tag effect="plain" type="info" v-if="filtered.length">共 {{ filtered.length }} 人</el-tag>
    </div>

    <div class="card" v-loading="loading">
      <el-table :data="filtered" stripe border empty-text="暂无教职工数据">
        <el-table-column prop="name" label="姓名" width="90"/>
        <el-table-column prop="identityNumber" label="工号" width="130"/>
        <el-table-column prop="gender" label="性别" width="70"/>
        <el-table-column prop="department" label="部门" width="130"/>
        <el-table-column prop="title" label="职务/职称" min-width="120"/>
        <el-table-column prop="phone" label="联系电话" width="140"/>
        <el-table-column prop="email" label="邮箱" min-width="180"/>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { Phone, Search } from '@element-plus/icons-vue'
import request from '@/utils/request'

const list=ref([]), loading=ref(false), dept=ref(''), search=ref('')
const depts=['计算机学院','经管学院','理学院','校办']

const filtered=computed(()=>{
  let r=list.value
  if(search.value){const kw=search.value.toLowerCase();r=r.filter(x=>(x.name||'').includes(kw)||(x.identityNumber||'').includes(kw))}
  return r
})

const load=async()=>{
  loading.value=true
  try{const r=await request.get('/api/leader/directory',{params:{department:dept.value||undefined}});list.value=r.data||[]}catch{}
  finally{loading.value=false}
}

onMounted(load)
</script>

<style scoped>
.page{padding:20px 24px;max-width:1200px;margin:0 auto;font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif}
.page-head{display:flex;align-items:center;gap:14px;margin-bottom:20px}
.page-icon{width:42px;height:42px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0}
.page-title{font-size:20px;font-weight:700;color:#1A1A2E;margin:0}
.page-desc{font-size:13px;color:#9CA3AF;margin:2px 0 0}
.toolbar{display:flex;align-items:center;gap:12px;margin-bottom:16px}
.card{border:1px solid #EEF0F4;border-radius:14px;padding:20px;box-shadow:0 2px 12px rgba(0,0,0,.02);background:#fff}
</style>
