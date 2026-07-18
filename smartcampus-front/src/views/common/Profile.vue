<template>
  <div class="profile-page">
    <!-- 头像区域 -->
    <el-card style="margin-bottom:20px;text-align:center">
      <div class="avatar-section">
        <div class="avatar-wrapper" @click="triggerUpload" title="点击更换头像">
          <img v-if="avatarUrl && !avatarError" :src="avatarUrl" class="avatar-img" @error="avatarError=true"/>
          <div v-else class="avatar-default">{{ profile?.name?.charAt(0)||'?' }}</div>
          <div class="avatar-overlay">更换</div>
        </div>
        <p style="margin-top:8px;color:#909399;font-size:13px">点击头像更换</p>
        <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleUpload"/>
      </div>
    </el-card>

    <el-card v-loading="loading">
      <template #header>
        <div class="card-header">
          <h2>个人信息</h2>
          <el-button v-if="!editing" type="primary" @click="startEdit">修改信息</el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="110px" v-if="profile">
        <el-row :gutter="20">
          <!-- 只读字段 -->
          <el-col :span="12">
            <el-form-item label="学号/工号">
              <el-input :model-value="profile.identityNumber" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级">
              <el-input :model-value="profile.grade" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学院">
              <el-input :model-value="profile.department" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="班级">
              <el-input :model-value="profile.className" disabled />
            </el-form-item>
          </el-col>

          <!-- 可改字段 -->
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="form.name" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="form.gender" :disabled="!editing" style="width:100%">
                <el-option label="男" value="M" />
                <el-option label="女" value="F" />
                <el-option label="未知" value="UNKNOWN" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业">
              <el-input v-model="form.major" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="职称">
              <el-input v-model="form.title" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="家庭住址">
              <el-input v-model="form.address" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人">
              <el-input v-model="form.emergencyContact" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系电话">
              <el-input v-model="form.emergencyPhone" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入学/入职日期">
              <el-input v-model="form.enrollmentDate" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计毕业日期">
              <el-input v-model="form.graduationDate" :disabled="!editing" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 修改密码 -->
    <el-card style="margin-top:20px">
      <template #header><span style="font-weight:bold">修改密码</span></template>
      <el-form :model="pwdForm" label-width="100px" style="max-width:400px">
        <el-form-item label="当前密码"><el-input v-model="pwdForm.oldPwd" type="password" show-password/></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPwd" type="password" show-password/></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="pwdForm.confirmPwd" type="password" show-password/></el-form-item>
        <el-form-item><el-button type="primary" @click="changePwd" :loading="changingPwd">修改密码</el-button></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile, changePassword } from '@/api/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const loading = ref(false), saving = ref(false), editing = ref(false), changingPwd = ref(false)
const pwdForm = reactive({ oldPwd: '', newPwd: '', confirmPwd: '' })
const fileInput = ref(null)
const avatarUrl = ref('')
const avatarError = ref(false)

const triggerUpload = () => { fileInput.value?.click() }

const handleUpload = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  const formData = new FormData()
  formData.append('file', file)
  try {
    const res = await request.post('/api/user/avatar', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
    avatarUrl.value = res.data
    avatarError.value = false
    ElMessage.success('头像更换成功')
  } catch { ElMessage.error('上传失败') }
}

const changePwd = async () => {
  if (!pwdForm.oldPwd || !pwdForm.newPwd) { ElMessage.warning('请填写密码'); return }
  if (pwdForm.newPwd !== pwdForm.confirmPwd) { ElMessage.warning('两次新密码不一致'); return }
  changingPwd.value = true
  try { await changePassword(pwdForm.oldPwd, pwdForm.newPwd); ElMessage.success('密码修改成功'); pwdForm.oldPwd=''; pwdForm.newPwd=''; pwdForm.confirmPwd='' }
  catch { ElMessage.error('修改失败，请检查当前密码') }
  finally { changingPwd.value = false }
}
const profile = ref(null)
const form = reactive({
  name: '', gender: '', phone: '', email: '',
  major: '', title: '', address: '',
  emergencyContact: '', emergencyPhone: '',
  enrollmentDate: '', graduationDate: ''
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProfile()
    profile.value = res.data
    if (res.data?.avatarUrl) avatarUrl.value = '/uploads/avatars/' + res.data.avatarUrl
    fillForm(res.data)
  } catch { ElMessage.error('加载失败') }
  finally { loading.value = false }
})

const fillForm = (data) => {
  Object.assign(form, {
    name: data.name || '',
    gender: data.gender || 'UNKNOWN',
    phone: data.phone || '',
    email: data.email || '',
    major: data.major || '',
    title: data.title || '',
    address: data.address || '',
    emergencyContact: data.emergencyContact || '',
    emergencyPhone: data.emergencyPhone || '',
    enrollmentDate: data.enrollmentDate || '',
    graduationDate: data.graduationDate || ''
  })
}

const startEdit = () => { editing.value = true }
const cancelEdit = () => {
  editing.value = false
  fillForm(profile.value)
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateProfile(form)
    profile.value = { ...profile.value, ...form }
    ElMessage.success('保存成功')
    editing.value = false
  } catch { ElMessage.error('保存失败') }
  finally { saving.value = false }
}
</script>

<style scoped>
.profile-page { padding: 20px; max-width: 1000px; margin: 0 auto; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.card-header h2 { margin: 0; }

.avatar-section { display:flex; flex-direction:column; align-items:center; }
.avatar-wrapper { position:relative; width:160px; height:160px; border-radius:8px; overflow:hidden; cursor:pointer; border:3px solid #e8e8e8; transition:border-color .2s; }
.avatar-wrapper:hover { border-color:#409eff; }
.avatar-wrapper:hover .avatar-overlay { opacity:1; }
.avatar-img { width:100%; height:100%; object-fit:cover; }
.avatar-default { width:100%; height:100%; display:flex; align-items:center; justify-content:center; background:linear-gradient(135deg,#409eff,#66b1ff); color:#fff; font-size:56px; font-weight:bold; }
.avatar-overlay { position:absolute; bottom:0; left:0; right:0; background:rgba(0,0,0,.5); color:#fff; font-size:12px; text-align:center; padding:4px 0; opacity:0; transition:opacity .2s; }
</style>
