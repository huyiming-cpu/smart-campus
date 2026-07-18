<template>
  <div class="page"><h3>📒 通讯录</h3>
    <el-select v-model="dept" @change="load" placeholder="全部部门" clearable style="width:200px;margin-bottom:16px">
      <el-option v-for="d in depts" :key="d" :label="d" :value="d"/>
    </el-select>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="name" label="姓名" width="80"/>
      <el-table-column prop="identityNumber" label="工号/学号" width="120"/>
      <el-table-column prop="department" label="部门" width="100"/>
      <el-table-column prop="title" label="职务" width="100"/>
      <el-table-column prop="phone" label="电话" width="130"/>
      <el-table-column prop="email" label="邮箱" min-width="160"/>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
const list=ref([]),loading=ref(false),dept=ref(''),depts=['计算机学院','经管学院','理学院','校办']
const load=async()=>{loading.value=true;try{const r=await request.get('/api/leader/directory',{params:{department:dept.value||undefined}});list.value=r.data||[]}catch{}finally{loading.value=false}}
onMounted(load)
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
