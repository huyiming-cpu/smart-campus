<template>
  <el-card>
    <template #header><span>系统参数配置</span></template>
    <el-table :data="list" v-loading="loading" stripe border>
      <el-table-column prop="configKey" label="配置键" width="200" />
      <el-table-column label="配置值" min-width="250">
        <template #default="{ row }">
          <template v-if="editId === row.id">
            <el-input v-model="editValue" style="width:200px" />
            <el-button type="primary" size="small" style="margin-left:8px" @click="handleSave(row)">保存</el-button>
            <el-button size="small" @click="editId = null">取消</el-button>
          </template>
          <template v-else>
            <span class="config-value" @click="startEdit(row)">{{ row.configValue }}</span>
            <el-button size="small" style="margin-left:8px" @click="startEdit(row)">编辑</el-button>
          </template>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" min-width="200" />
      <el-table-column prop="configType" label="类型" width="100" />
    </el-table>
  </el-card>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { listConfigs, updateConfig } from '@/api/system'
import { ElMessage } from 'element-plus'

const list = ref([])
const loading = ref(false)
const editId = ref(null)
const editValue = ref('')

const loadData = async () => {
  loading.value = true
  try { const res = await listConfigs(); list.value = res.data || [] }
  catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
}

const startEdit = (row) => {
  editId.value = row.id
  editValue.value = row.configValue
}

const handleSave = async (row) => {
  try {
    await updateConfig(row.id, editValue.value)
    ElMessage.success('保存成功')
    row.configValue = editValue.value
    editId.value = null
  } catch { ElMessage.error('保存失败') }
}

onMounted(() => loadData())
</script>
<style scoped>
.config-value { cursor: pointer; color: #409eff; text-decoration: underline; }
</style>
