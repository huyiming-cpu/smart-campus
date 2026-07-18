<template>
  <el-card>
    <template #header><span>角色权限</span></template>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="roleCode" label="角色编码" width="140" />
      <el-table-column prop="roleName" label="角色名称" width="140" />
      <el-table-column prop="description" label="描述" min-width="300" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">{{ row.status === 'ACTIVE' ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { getAllRoles } from '@/api/admin'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)

const loadData = async () => {
  loading.value = true
  try { const res = await getAllRoles(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

onMounted(() => loadData())
</script>
