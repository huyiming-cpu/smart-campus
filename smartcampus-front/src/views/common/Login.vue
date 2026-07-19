<template>
  <div class="login-page">
    <!-- 装饰光斑 -->
    <div class="glow-orb glow-1"></div>
    <div class="glow-orb glow-2"></div>

    <div class="split-layout">
      <!-- ===== 左侧品牌区 ===== -->
      <div class="brand-panel">
        <div class="brand-content">
          <div class="brand-icon">
            <svg viewBox="0 0 48 48" fill="none">
              <rect width="48" height="48" rx="14" fill="currentColor" opacity="0.12"/>
              <path d="M14 32V16l10 8-10 8Z" fill="currentColor"/>
              <path d="M24 32V16l10 8-10 8Z" fill="currentColor" opacity="0.6"/>
            </svg>
          </div>
          <h1 class="brand-title">智慧校园</h1>
          <p class="brand-subtitle">Smart Campus Service Platform</p>
          <div class="brand-divider"></div>
          <p class="brand-desc">简洁 · 高效 · 智能<br/>一站式校园服务平台</p>
        </div>
      </div>

      <!-- ===== 右侧表单区 ===== -->
      <div class="form-panel">
        <div class="form-card">
          <div class="card-header">
            <h2 class="card-title">{{ activeTab === 'login' ? '欢迎回来' : '创建账号' }}</h2>
            <p class="card-sub">{{ activeTab === 'login' ? '登录你的校园账号' : '注册新的校园账号' }}</p>
          </div>

          <!-- Tab 切换 -->
          <div class="tab-row">
            <button
              :class="['tab-btn', { active: activeTab === 'login' }]"
              @click="activeTab = 'login'"
            >登录</button>
            <button
              :class="['tab-btn', { active: activeTab === 'register' }]"
              @click="activeTab = 'register'"
            >注册</button>
          </div>

          <!-- ===== 登录表单 ===== -->
          <form v-show="activeTab === 'login'" @submit.prevent="handleLogin">
            <div class="field-group">
              <label class="field-label">学号 / 工号</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><User /></el-icon>
                <input
                  v-model="loginForm.identityNumber"
                  class="text-input"
                  placeholder="请输入学号或工号"
                  autocomplete="username"
                />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">密码</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><Lock /></el-icon>
                <input
                  v-model="loginForm.password"
                  class="text-input"
                  type="password"
                  placeholder="请输入密码"
                  autocomplete="current-password"
                  @keyup.enter="handleLogin"
                />
              </div>
            </div>
            <button type="submit" class="submit-btn primary" :disabled="loginLoading">
              <span v-if="loginLoading" class="btn-spinner"></span>
              <span v-else>登 录</span>
            </button>
          </form>

          <!-- ===== 注册表单 ===== -->
          <form v-show="activeTab === 'register'" @submit.prevent="handleRegister">
            <div class="field-group">
              <label class="field-label">学号 / 工号</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><User /></el-icon>
                <input
                  v-model="registerForm.identityNumber"
                  class="text-input"
                  placeholder="请输入学号或工号"
                />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">真实姓名</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><Edit /></el-icon>
                <input
                  v-model="registerForm.name"
                  class="text-input"
                  placeholder="请输入真实姓名"
                />
              </div>
            </div>
            <div class="field-row">
              <div class="field-group half">
                <label class="field-label">身份证后6位</label>
                <div class="input-wrap">
                  <el-icon class="input-icon"><Key /></el-icon>
                  <input
                    v-model="registerForm.idCardLast6"
                    class="text-input"
                    placeholder="后6位"
                    maxlength="6"
                  />
                </div>
              </div>
              <div class="field-group half">
                <label class="field-label">手机号</label>
                <div class="input-wrap">
                  <el-icon class="input-icon"><Phone /></el-icon>
                  <input
                    v-model="registerForm.phone"
                    class="text-input"
                    placeholder="手机号"
                  />
                </div>
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">设置密码</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><Lock /></el-icon>
                <input
                  v-model="registerForm.password"
                  class="text-input"
                  type="password"
                  placeholder="请设置密码"
                />
              </div>
            </div>
            <div class="field-group">
              <label class="field-label">确认密码</label>
              <div class="input-wrap">
                <el-icon class="input-icon"><Lock /></el-icon>
                <input
                  v-model="registerForm.confirmPassword"
                  class="text-input"
                  type="password"
                  placeholder="请再次输入密码"
                />
              </div>
            </div>
            <button type="submit" class="submit-btn primary" :disabled="registerLoading">
              <span v-if="registerLoading" class="btn-spinner"></span>
              <span v-else>注 册</span>
            </button>
          </form>

          <p class="form-footer">
            {{ activeTab === 'login' ? '首次使用？请先' : '已有账号？去' }}
            <a href="#" @click.prevent="activeTab = activeTab === 'login' ? 'register' : 'login'">
              {{ activeTab === 'login' ? '注册账号' : '登录' }}
            </a>
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { login, register } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock, Edit, Key, Phone } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')

// ===== 登录 =====
const loginLoading = ref(false)
const loginForm = reactive({
  identityNumber: '',
  password: ''
})

const handleLogin = async () => {
  if (!loginForm.identityNumber) { ElMessage.warning('请输入学号/工号'); return }
  if (!loginForm.password) { ElMessage.warning('请输入密码'); return }
  loginLoading.value = true
  try {
    const res = await login(loginForm)
    const data = res.data
    userStore.setToken(data.token)
    userStore.setUser({
      userId: data.userId,
      identityNumber: data.identityNumber,
      name: data.name,
      roleName: data.roleName
    }, data.roleCode)
    ElMessage.success('登录成功')
    router.push(`/${data.roleCode.toLowerCase()}`)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败')
  } finally {
    loginLoading.value = false
  }
}

// ===== 注册 =====
const registerLoading = ref(false)
const registerForm = reactive({
  identityNumber: '',
  name: '',
  idCardLast6: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const handleRegister = async () => {
  if (!registerForm.identityNumber) { ElMessage.warning('请输入学号/工号'); return }
  if (!registerForm.name) { ElMessage.warning('请输入真实姓名'); return }
  if (!registerForm.idCardLast6) { ElMessage.warning('请输入身份证后6位'); return }
  if (!registerForm.phone) { ElMessage.warning('请输入手机号'); return }
  if (!registerForm.password) { ElMessage.warning('请设置密码'); return }
  if (registerForm.password !== registerForm.confirmPassword) {
    ElMessage.warning('两次密码不一致'); return
  }
  registerLoading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    Object.assign(registerForm, {
      identityNumber: '', name: '', idCardLast6: '',
      phone: '', password: '', confirmPassword: ''
    })
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
/* ==================== 基础变量 ==================== */
$bg: #F4F5FA;
$card-bg: #FFFFFF;
$text-primary: #1A1A2E;
$text-secondary: #6B7280;
$text-muted: #9CA3AF;
$accent: #6C63FF;
$accent-hover: #5A52E0;
$border: #E5E7EB;
$input-bg: #F5F6FA;
$radius-lg: 20px;
$radius-md: 12px;
$radius-sm: 8px;

/* ==================== 页面容器 ==================== */
.login-page {
  min-height: 100vh;
  background: $bg;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* ==================== 装饰光斑 ==================== */
.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  z-index: 0;
}
.glow-1 {
  width: 500px;
  height: 500px;
  background: rgba($accent, 0.08);
  top: -200px;
  right: -150px;
  animation: drift 8s ease-in-out infinite alternate;
}
.glow-2 {
  width: 400px;
  height: 400px;
  background: rgba(#FFC0BC, 0.10);
  bottom: -150px;
  left: -100px;
  animation: drift 10s ease-in-out 2s infinite alternate;
}
@keyframes drift {
  0% { transform: translate(0, 0) scale(1); }
  100% { transform: translate(30px, -20px) scale(1.08); }
}

/* ==================== 左右分栏 ==================== */
.split-layout {
  display: flex;
  width: 960px;
  min-height: 620px;
  background: $card-bg;
  border-radius: $radius-lg;
  box-shadow: 0 2px 24px rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  position: relative;
  z-index: 1;
}

/* ==================== 左侧品牌面板 ==================== */
.brand-panel {
  flex: 1;
  background: linear-gradient(155deg, #1E1B4B 0%, #312E81 40%, #4338CA 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at 30% 20%, rgba(255,255,255,0.06) 0%, transparent 60%),
                radial-gradient(ellipse at 70% 80%, rgba($accent, 0.15) 0%, transparent 50%);
  }
}

.brand-content {
  text-align: center;
  position: relative;
  z-index: 1;
  color: #fff;
}

.brand-icon {
  width: 56px;
  height: 56px;
  margin: 0 auto 24px;
  color: rgba(255, 255, 255, 0.9);
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  margin: 0 0 8px;
}

.brand-subtitle {
  font-size: 13px;
  opacity: 0.55;
  margin: 0 0 28px;
  letter-spacing: 1px;
  text-transform: uppercase;
}

.brand-divider {
  width: 48px;
  height: 2px;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent);
  margin: 0 auto 28px;
}

.brand-desc {
  font-size: 14px;
  opacity: 0.6;
  line-height: 1.8;
  margin: 0;
}

/* ==================== 右侧表单面板 ==================== */
.form-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.form-card {
  width: 100%;
  max-width: 360px;
}

.card-header {
  margin-bottom: 28px;

  .card-title {
    font-size: 22px;
    font-weight: 700;
    color: $text-primary;
    margin: 0 0 6px;
  }

  .card-sub {
    font-size: 14px;
    color: $text-secondary;
    margin: 0;
  }
}

/* ==================== Tab 切换 ==================== */
.tab-row {
  display: flex;
  gap: 0;
  margin-bottom: 28px;
  background: $input-bg;
  border-radius: $radius-sm;
  padding: 4px;
}

.tab-btn {
  flex: 1;
  padding: 8px 0;
  border: none;
  background: transparent;
  font-size: 14px;
  font-weight: 500;
  color: $text-secondary;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.25s;

  &.active {
    background: #fff;
    color: $text-primary;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  }

  &:hover:not(.active) {
    color: $text-primary;
  }
}

/* ==================== 表单字段 ==================== */
.field-group {
  margin-bottom: 18px;
}

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: $text-primary;
  margin-bottom: 6px;
}

.input-wrap {
  position: relative;
  display: flex;
  align-items: center;

  .input-icon {
    position: absolute;
    left: 14px;
    color: $text-muted;
    font-size: 16px;
    z-index: 1;
    pointer-events: none;
  }
}

.text-input {
  width: 100%;
  height: 44px;
  padding: 0 14px 0 40px;
  background: $input-bg;
  border: 1.5px solid transparent;
  border-radius: $radius-sm;
  font-size: 14px;
  color: $text-primary;
  outline: none;
  transition: all 0.25s;
  box-sizing: border-box;
  font-family: inherit;

  &::placeholder {
    color: $text-muted;
  }

  &:focus {
    background: #fff;
    border-color: $accent;
    box-shadow: 0 0 0 3px rgba($accent, 0.08);
  }
}

.field-row {
  display: flex;
  gap: 12px;

  .half {
    flex: 1;
  }
}

/* ==================== 提交按钮 ==================== */
.submit-btn {
  width: 100%;
  height: 46px;
  border: none;
  border-radius: $radius-sm;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 6px;
  font-family: inherit;
  letter-spacing: 2px;

  &.primary {
    background: $text-primary;
    color: #fff;

    &:hover:not(:disabled) {
      background: $accent;
      transform: translateY(-1px);
      box-shadow: 0 6px 20px rgba($accent, 0.3);
    }

    &:active:not(:disabled) {
      transform: translateY(0);
    }

    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
  }
}

.btn-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* ==================== 底部链接 ==================== */
.form-footer {
  text-align: center;
  margin-top: 24px;
  font-size: 13px;
  color: $text-secondary;

  a {
    color: $accent;
    text-decoration: none;
    font-weight: 500;

    &:hover {
      text-decoration: underline;
    }
  }
}

/* ==================== 暗色模式 ==================== */
@media (prefers-color-scheme: dark) {
  .login-page {
    background: #0C0C12;
  }

  .split-layout {
    background: #16161E;
    box-shadow: 0 2px 24px rgba(0, 0, 0, 0.4), 0 0 0 1px rgba(255, 255, 255, 0.06);
  }

  .brand-panel {
    background: linear-gradient(155deg, #0F0D2E 0%, #1E1B4B 40%, #2D2870 100%);
  }

  .card-header .card-title { color: #F1F1F6; }
  .card-header .card-sub { color: #8E8E9A; }
  .field-label { color: #E4E4EC; }
  .text-input {
    background: #1E1E28;
    color: #F1F1F6;
    border-color: transparent;

    &::placeholder { color: #6B6B78; }
    &:focus {
      background: #22222E;
      border-color: $accent;
    }
  }
  .tab-row { background: #1E1E28; }
  .tab-btn {
    color: #8E8E9A;
    &.active {
      background: #2A2A38;
      color: #F1F1F6;
    }
    &:hover:not(.active) { color: #F1F1F6; }
  }
  .form-footer { color: #8E8E9A; }
  .submit-btn.primary { background: #E4E4EC; color: #0C0C12;
    &:hover:not(:disabled) { background: $accent; color: #fff; }
  }
}
</style>
