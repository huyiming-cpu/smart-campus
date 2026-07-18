<template>
  <div class="page">
    <h3>📞 教职工通讯录</h3>
    <el-row :gutter="12" style="margin-bottom:16px">
      <el-col :span="6"><el-input v-model="search" placeholder="搜索姓名/工号" clearable/></el-col>
      <el-col :span="6"><el-select v-model="dept" placeholder="按部门筛选" clearable @change="load" style="width:100%">
        <el-option v-for="d in departments" :key="d" :label="d" :value="d"/>
      </el-select></el-col>
    </el-row>
    <el-table :data="filtered" v-loading="loading" border stripe size="small">
      <el-table-column prop="name" label="姓名" width="80"/>
      <el-table-column prop="identityNumber" label="工号" width="120"/>
      <el-table-column prop="gender" label="性别" width="60"/>
      <el-table-column prop="department" label="部门" width="120"/>
      <el-table-column prop="title" label="职称" width="100"/>
      <el-table-column prop="phone" label="手机号" width="120"/>
      <el-table-column prop="email" label="邮箱" min-width="160"/>
    </el-table>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

const list = ref([]), loading = ref(false), search = ref(''), dept = ref('')
const departments = ref([])

const load = async () => {
  loading.value = true
  try {
    const params = {}
    if (dept.value) params.department = dept.value
    const r = await request.get('/api/teacher/directory', {params})
    list.value = r.data || []
    // 提取部门列表
    const deptSet = new Set(list.value.map(x => x.department).filter(Boolean))
    departments.value = [...deptSet].sort()
  } catch { list.value = [] }
  finally { loading.value = false }
}

const filtered = computed(() => {
  if (!search.value) return list.value
  const kw = search.value.toLowerCase()
  return list.value.filter(x => (x.name||'').includes(kw) || (x.identityNumber||'').includes(kw))
})

onMounted(load)
</script>
<style scoped>.page{padding:20px;max-width:1100px}h3{margin-bottom:16px}</style>
