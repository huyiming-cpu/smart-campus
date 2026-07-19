<template>
  <div class="profile-page" :style="themeVars">
    <!-- ===== 顶部身份卡片（玻璃质感） ===== -->
    <div class="identity-card">
      <div class="identity-bg"></div>
      <div class="identity-content">
        <div class="avatar-zone">
          <div class="avatar-ring" @click="triggerUpload" title="点击更换头像">
            <div class="avatar-inner">
              <img v-if="avatarUrl && !avatarError" :src="avatarUrl" class="avatar-img" @error="avatarError=true" />
              <span v-else class="avatar-text">{{ (profile?.name || '?')[0] }}</span>
            </div>
            <div class="avatar-shine"></div>
          </div>
          <span class="avatar-hint">📷 点击更换</span>
        </div>
        <div class="identity-info">
          <div class="identity-name-row">
            <span class="identity-name">{{ profile?.name || '—' }}</span>
            <span v-if="profile?.title" class="identity-badge">{{ profile.title }}</span>
          </div>
          <div class="identity-meta">
            <div class="meta-item">
              <el-icon :size="15"><School /></el-icon>
              <span>{{ roleLabel }}：{{ profile?.identityNumber || '—' }}</span>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-item">
              <el-icon :size="15"><OfficeBuilding /></el-icon>
              <span>{{ profile?.department || '—' }}</span>
            </div>
            <div class="meta-divider"></div>
            <div class="meta-item">
              <el-icon :size="15"><Message /></el-icon>
              <span>{{ profile?.email || '—' }}</span>
            </div>
          </div>
        </div>
        <div class="identity-actions">
          <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleUpload" />
        </div>
      </div>
    </div>

    <!-- ===== 信息卡片区（双栏） ===== -->
    <div class="info-grid">
      <div class="glass-card">
        <div class="glass-card-header">
          <div class="gc-icon" :style="{ background: theme.accentBg }"><el-icon :size="16" :color="theme.primary"><UserFilled /></el-icon></div>
          <span class="gc-title">基本信息</span>
          <div class="gc-actions">
            <template v-if="!editing">
              <el-button size="small" type="primary" plain @click="startEdit">编辑</el-button>
            </template>
            <template v-else>
              <el-button size="small" @click="cancelEdit">取消</el-button>
              <el-button size="small" type="primary" :loading="saving" @click="handleSave">保存</el-button>
            </template>
          </div>
        </div>
        <el-form :model="form" label-width="90px" label-position="top" class="info-form" v-if="profile">
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="姓名">
                <el-input v-model="form.name" :disabled="!editing" placeholder="姓名" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别">
                <el-select v-model="form.gender" :disabled="!editing" style="width:100%">
                  <el-option label="男" value="M" /><el-option label="女" value="F" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item :label="roleFieldLabel">
                <el-input v-model="form.title" :disabled="!editing" :placeholder="roleFieldPlaceholder" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item :label="roleLabel">
                <el-input :model-value="profile.identityNumber" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学院">
                <el-input :model-value="profile.department" disabled />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="专业">
                <el-input v-model="form.major" :disabled="!editing" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="glass-card">
        <div class="glass-card-header">
          <div class="gc-icon" style="background:#ECFDF5;"><el-icon :size="16" color="#10B981"><Phone /></el-icon></div>
          <span class="gc-title">联系方式</span>
        </div>
        <el-form :model="form" label-width="90px" label-position="top" class="info-form" v-if="profile">
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="手机号">
                <el-input v-model="form.phone" :disabled="!editing" placeholder="手机号" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="邮箱">
                <el-input v-model="form.email" :disabled="!editing" placeholder="邮箱" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="紧急联系人">
                <el-input v-model="form.emergencyContact" :disabled="!editing" placeholder="紧急联系人" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="紧急联系电话">
                <el-input v-model="form.emergencyPhone" :disabled="!editing" placeholder="紧急联系电话" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="家庭住址">
                <el-input v-model="form.address" :disabled="!editing" placeholder="家庭住址" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
    </div>

    <!-- ===== 学籍/入职信息 ===== -->
    <div class="glass-card" v-if="profile">
      <div class="glass-card-header">
        <div class="gc-icon" style="background:#FEF3E2;"><el-icon :size="16" color="#F59E0B"><Clock /></el-icon></div>
        <span class="gc-title">{{ role === 'STUDENT' ? '学籍信息' : '入职信息' }}</span>
      </div>
      <el-form :model="form" label-width="110px" label-position="top" class="info-form">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="年级/班级">
              <el-input :model-value="(profile.grade||'') + (profile.className ? ' · ' + profile.className : '')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="role === 'STUDENT' ? '入学日期' : '入职日期'">
              <el-input v-model="form.enrollmentDate" :disabled="!editing" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="预计毕业日期">
              <el-input v-model="form.graduationDate" :disabled="!editing" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- ===== 安全设置 ===== -->
    <div class="glass-card">
      <div class="glass-card-header">
        <div class="gc-icon" style="background:#EEF2FF;"><el-icon :size="16" color="#6366F1"><Lock /></el-icon></div>
        <span class="gc-title">安全设置</span>
        <span class="gc-hint">定期更换密码保障账户安全</span>
      </div>
      <el-form :model="pwdForm" label-width="100px" label-position="top" class="info-form">
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="当前密码">
              <el-input v-model="pwdForm.oldPwd" type="password" show-password placeholder="输入当前密码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="新密码">
              <el-input v-model="pwdForm.newPwd" type="password" show-password placeholder="输入新密码" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="确认新密码">
              <el-input v-model="pwdForm.confirmPwd" type="password" show-password placeholder="再次输入新密码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-button type="primary" @click="changePwd" :loading="changingPwd" style="margin-top:4px">确认修改密码</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getProfile, updateProfile, changePassword } from '@/api/user'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'
import { School, OfficeBuilding, Message, UserFilled, Phone, Clock, Lock } from '@element-plus/icons-vue'

const userStore = useUserStore()
const role = computed(() => userStore.role)

// ===== 角色主题 =====
const themes = {
  TEACHER:  { primary: '#5B9BD5', primaryDark: '#4A8AD4', primaryLight: '#6DB3E8', accentBg: '#EBF3FC', shadow: 'rgba(91,155,213,0.25)', tagBg: 'rgba(91,155,213,0.08)' },
  STUDENT:  { primary: '#FF6B6B', primaryDark: '#EE5A5A', primaryLight: '#FF8E72', accentBg: '#FFF0EE', shadow: 'rgba(255,107,107,0.25)', tagBg: 'rgba(255,107,107,0.08)' },
  LEADER:   { primary: '#6366F1', primaryDark: '#4F46E5', primaryLight: '#818CF8', accentBg: '#EEF2FF', shadow: 'rgba(99,102,241,0.25)', tagBg: 'rgba(99,102,241,0.08)' },
  ADMIN:    { primary: '#0EA5E9', primaryDark: '#0284C7', primaryLight: '#38BDF8', accentBg: '#EBF8FE', shadow: 'rgba(14,165,233,0.25)', tagBg: 'rgba(14,165,233,0.08)' },
}
const theme = computed(() => themes[role.value] || themes.TEACHER)

const themeVars = computed(() => ({
  '--pf-primary': theme.value.primary,
  '--pf-primary-dark': theme.value.primaryDark,
  '--pf-primary-light': theme.value.primaryLight,
  '--pf-accent-bg': theme.value.accentBg,
  '--pf-shadow': theme.value.shadow,
  '--pf-tag-bg': theme.value.tagBg,
}))

const roleLabel = computed(() => {
  const map = { TEACHER: '工号', STUDENT: '学号', LEADER: '工号', ADMIN: '工号' }
  return map[role.value] || '工号'
})
const roleFieldLabel = computed(() => role.value === 'STUDENT' ? '班级' : '职称')
const roleFieldPlaceholder = computed(() => role.value === 'STUDENT' ? '例如：软件工程2101' : '教授/副教授/讲师...')

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
.profile-page { padding: 20px; max-width: 1100px; margin: 0 auto; font-family: "Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif; }

/* ===== 顶部身份卡片（玻璃拟态） ===== */
.identity-card {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  margin-bottom: 24px;
  background: linear-gradient(135deg, var(--pf-primary) 0%, var(--pf-primary-dark) 40%, var(--pf-primary-light) 100%);
  box-shadow: 0 8px 32px var(--pf-shadow), inset 0 1px 0 rgba(255,255,255,0.2);
}
.identity-bg {
  position: absolute; inset: 0;
  background: radial-gradient(ellipse at 20% 50%, rgba(255,255,255,0.15) 0%, transparent 60%),
              radial-gradient(ellipse at 80% 50%, rgba(255,255,255,0.06) 0%, transparent 50%);
  pointer-events: none;
}
.identity-bg::after {
  content: '';
  position: absolute; top: -50%; right: -20%;
  width: 300px; height: 300px;
  border-radius: 50%;
  border: 1px solid rgba(255,255,255,0.08);
  pointer-events: none;
}
.identity-content {
  position: relative; z-index: 1;
  display: flex; align-items: center; gap: 28px;
  padding: 32px 36px;
}
.avatar-zone { display: flex; flex-direction: column; align-items: center; gap: 10px; flex-shrink: 0; }
.avatar-ring {
  width: 120px; height: 120px; border-radius: 50%;
  padding: 4px;
  background: linear-gradient(135deg, rgba(255,255,255,0.5), rgba(255,255,255,0.15));
  box-shadow: 0 4px 20px rgba(0,0,0,0.12), inset 0 1px 2px rgba(255,255,255,0.3);
  cursor: pointer; transition: transform .25s, box-shadow .25s;
  position: relative;
}
.avatar-ring:hover { transform: scale(1.05); box-shadow: 0 6px 28px rgba(0,0,0,0.18), inset 0 1px 2px rgba(255,255,255,0.4); }
.avatar-inner {
  width: 100%; height: 100%; border-radius: 50%; overflow: hidden;
  border: 2px solid rgba(255,255,255,0.35);
  background: rgba(255,255,255,0.12);
}
.avatar-img { width: 100%; height: 100%; object-fit: cover; }
.avatar-text { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 44px; font-weight: 700; color: #fff; }
.avatar-shine {
  position: absolute; top: 8px; left: 12px; width: 30px; height: 14px;
  background: linear-gradient(180deg, rgba(255,255,255,0.5), transparent);
  border-radius: 50%; pointer-events: none;
}
.avatar-hint { font-size: 12px; color: rgba(255,255,255,0.75); }

.identity-info { flex: 1; min-width: 0; color: #fff; }
.identity-name-row { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.identity-name { font-size: 28px; font-weight: 700; letter-spacing: .5px; text-shadow: 0 1px 4px rgba(0,0,0,0.1); }
.identity-badge {
  font-size: 12px; padding: 4px 14px; border-radius: 20px;
  background: rgba(255,255,255,0.22); backdrop-filter: blur(4px);
  color: #fff; font-weight: 500; letter-spacing: .5px;
  border: 1px solid rgba(255,255,255,0.25);
}
.identity-meta { display: flex; align-items: center; gap: 0; flex-wrap: wrap; }
.meta-item { display: flex; align-items: center; gap: 6px; font-size: 13px; color: rgba(255,255,255,0.85); }
.meta-divider { width: 1px; height: 14px; background: rgba(255,255,255,0.25); margin: 0 14px; }
.identity-actions { flex-shrink: 0; }

/* ===== 玻璃卡片通用 ===== */
.glass-card {
  background: #fff;
  border: 1px solid #EEF0F4;
  border-radius: 14px;
  padding: 24px 28px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.03);
  transition: box-shadow .2s;
}
.glass-card:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.05); }
.glass-card-header { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; padding-bottom: 14px; border-bottom: 1px solid #F0F2F5; }
.gc-icon { width: 32px; height: 32px; border-radius: 8px; display: flex; align-items: center; justify-content: center; flex-shrink: 0; }
.gc-title { font-size: 15px; font-weight: 600; color: #1A1A2E; }
.gc-hint { font-size: 12px; color: #9CA3AF; margin-left: 8px; }
.gc-actions { margin-left: auto; display: flex; gap: 8px; }

/* ===== 双栏 ===== */
.info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; margin-bottom: 4px; }

/* ===== 表单微调 ===== */
.info-form :deep(.el-form-item) { margin-bottom: 10px; }
.info-form :deep(.el-form-item__label) { font-size: 12px; color: #9CA3AF; font-weight: 500; padding-bottom: 2px; }
.info-form :deep(.el-input__wrapper) { background: #F8FAFE; border-radius: 8px; box-shadow: none !important; border: 1px solid #EEF0F4; transition: border-color .2s, background .2s; }
.info-form :deep(.el-input__wrapper:hover) { border-color: #D4D8DF; }
.info-form :deep(.el-input__wrapper.is-focus) { border-color: var(--pf-primary); background: #fff; }
.info-form :deep(.el-input.is-disabled .el-input__wrapper) { background: #F3F4F6; border-color: #EEF0F4; }
.info-form :deep(.el-input__inner) { font-size: 13px; color: #1A1A2E; }
.info-form :deep(.el-input__inner::placeholder) { color: #C4C9D1; }
.info-form :deep(.el-select .el-input__wrapper) { background: #F8FAFE; border-radius: 8px; box-shadow: none !important; border: 1px solid #EEF0F4; }

@media (max-width: 768px) {
  .identity-content { flex-direction: column; text-align: center; padding: 24px 20px; }
  .identity-meta { justify-content: center; }
  .identity-name-row { justify-content: center; flex-wrap: wrap; }
  .meta-divider { margin: 0 8px; }
  .info-grid { grid-template-columns: 1fr; }
}
</style>
