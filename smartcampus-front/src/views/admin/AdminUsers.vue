<template>
  <div class="page"><h3>👥 用户与角色管理</h3>
    <el-tabs v-model="tab">
      <el-tab-pane label="用户管理" name="users"/>
      <el-tab-pane label="角色权限" name="roles"/>
    </el-tabs>
    <div v-if="tab==='users'">
      <el-table :data="users" v-loading="uLoad" border size="small">
        <el-table-column prop="id" label="ID" width="60"/><el-table-column prop="identityNumber" label="学号/工号" width="130"/>
        <el-table-column prop="name" label="姓名" width="80"/><el-table-column prop="gender" label="性别" width="60"/>
        <el-table-column prop="phone" label="电话" width="120"/><el-table-column prop="email" label="邮箱" min-width="160"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status==='NORMAL'?'success':'danger'" size="small">{{ row.status==='NORMAL'?'正常':row.status }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" plain @click="openEdit(row)">编辑</el-button>
            <el-button size="small" @click="toggleLock(row)">{{ row.status==='LOCKED'?'解冻':'冻结' }}</el-button>
            <el-button size="small" type="danger" plain @click="delUser(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div v-if="tab==='roles'">
      <el-table :data="roles" v-loading="rLoad" border size="small">
        <el-table-column prop="roleCode" label="编码" width="100"/><el-table-column prop="roleName" label="名称" width="80"/>
        <el-table-column prop="roleDesc" label="描述" min-width="200"/>
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'danger'" size="small">{{ row.status===1?'启用':'禁用' }}</el-tag></template></el-table-column>
      </el-table>
    </div>

    <el-dialog title="编辑用户" v-model="dv" width="700px" :close-on-click-modal="false">
      <el-tabs v-model="etab">
        <el-tab-pane label="基本信息" name="user"/>
        <el-tab-pane label="扩展信息" name="profile"/>
        <el-tab-pane label="角色" name="role"/>
      </el-tabs>
      <el-form :model="uf" label-width="100px" v-if="etab==='user'">
        <el-form-item label="学号/工号"><el-input v-model="uf.identityNumber"/></el-form-item>
        <el-form-item label="姓名"><el-input v-model="uf.name"/></el-form-item>
        <el-form-item label="性别"><el-select v-model="uf.gender"><el-option label="男" value="M"/><el-option label="女" value="F"/><el-option label="未知" value="UNKNOWN"/></el-select></el-form-item>
        <el-form-item label="电话"><el-input v-model="uf.phone"/></el-form-item>
        <el-form-item label="邮箱"><el-input v-model="uf.email"/></el-form-item>
        <el-form-item label="状态"><el-select v-model="uf.status"><el-option label="正常" value="NORMAL"/><el-option label="锁定" value="LOCKED"/></el-select></el-form-item>
      </el-form>
      <el-form :model="pf" label-width="100px" v-if="etab==='profile'">
        <el-form-item label="学院"><el-input v-model="pf.department"/></el-form-item>
        <el-form-item label="专业"><el-input v-model="pf.major"/></el-form-item>
        <el-form-item label="年级"><el-input v-model="pf.grade"/></el-form-item>
        <el-form-item label="班级"><el-input v-model="pf.className"/></el-form-item>
        <el-form-item label="职务/职称"><el-input v-model="pf.title"/></el-form-item>
        <el-form-item label="地址"><el-input v-model="pf.address"/></el-form-item>
        <el-form-item label="紧急联系人"><el-input v-model="pf.emergencyContact"/></el-form-item>
        <el-form-item label="紧急电话"><el-input v-model="pf.emergencyPhone"/></el-form-item>
      </el-form>
      <div v-if="etab==='role'" style="padding:10px">
        <p>当前角色：<el-tag v-for="r in userRoles" :key="r" style="margin:2px">{{ r }}</el-tag></p>
        <el-checkbox-group v-model="selectedRoles">
          <el-checkbox v-for="r in roles" :key="r.id" :label="r.roleCode" :value="r.roleCode">{{ r.roleName }}</el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="dv=false">取消</el-button>
        <el-button type="primary" @click="saveUser" :loading="saving">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdDv" title="重置密码" width="350px">
      <el-input v-model="newPwd"/>
      <template #footer><el-button @click="pwdDv=false">取消</el-button><el-button type="primary" @click="doResetPwd">确定</el-button></template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
const tab=ref('users'),users=ref([]),uLoad=ref(false),roles=ref([]),rLoad=ref(false)
const dv=ref(false),etab=ref('user'),saving=ref(false),eid=ref(null)
const uf=ref({}),pf=ref({}),userRoles=ref([]),selectedRoles=ref([])
const pwdDv=ref(false),newPwd=ref('123456'),curUser=ref(null)

const loadUsers=async()=>{uLoad.value=true;try{const r=await request.get('/api/admin/users');users.value=r.data||[]}catch{}finally{uLoad.value=false}}
const loadRoles=async()=>{rLoad.value=true;try{const r=await request.get('/api/admin/roles');roles.value=r.data||[]}catch{}finally{rLoad.value=false}}
const openEdit=async(row)=>{
  eid.value=row.id;uf.value={...row};etab.value='user'
  try{const r=await request.get(`/api/admin/user-profile/${row.id}`);if(r.data)pf.value={...r.data}}catch{}
  // Load user roles
  try{const r=await request.get('/api/admin/roles');const allRoles=r.data||[]
    userRoles.value=[];selectedRoles.value=[]
    // Find user's current roles from their data
    if(row.roleCode)userRoles.value=[row.roleCode]
  }catch{}
  dv.value=true
}
const saveUser=async()=>{
  saving.value=true
  try{
    await request.put('/api/user/profile',{...uf.value,...pf.value})
    ElMessage.success('保存成功');dv.value=false;loadUsers()
  }catch{ElMessage.error('保存失败')}
  finally{saving.value=false}
}
const toggleLock=async(r)=>{const s=r.status==='LOCKED'?'NORMAL':'LOCKED';try{await request.put(`/api/admin/users/${r.id}/status`,{status:s});loadUsers()}catch{}}
const delUser=async(id)=>{try{await ElMessageBox.confirm('确定删除？','提示',{type:'warning'});await request.delete(`/api/admin/users/${id}`);ElMessage.success('已删除');loadUsers()}catch{}}
onMounted(()=>{loadUsers();loadRoles()})
</script>
<style scoped>.page{padding:20px}h3{margin-bottom:16px}</style>
