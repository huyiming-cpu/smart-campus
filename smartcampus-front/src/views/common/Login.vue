<template>
  <div class="login-container">
    <div class="login-card">
      <div class="header">
        <h1>智慧校园服务平台</h1>
        <p>Smart Campus Service Platform</p>
      </div>

      <el-tabs v-model="activeTab" class="tabs">
        <!-- ========== 登录 ========== -->
        <el-tab-pane label="登录" name="login">
          <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
            <el-form-item prop="identityNumber">
              <el-input v-model="loginForm.identityNumber" placeholder="学号 / 工号" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" :prefix-icon="Lock"
                show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submit-btn" :loading="loginLoading" @click="handleLogin">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <!-- ========== 注册 ========== -->
        <el-tab-pane label="注册" name="register">
          <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
            <el-form-item prop="identityNumber">
              <el-input v-model="registerForm.identityNumber" placeholder="学号 / 工号" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="name">
              <el-input v-model="registerForm.name" placeholder="真实姓名" :prefix-icon="Edit" />
            </el-form-item>
            <el-form-item prop="idCardLast6">
              <el-input v-model="registerForm.idCardLast6" placeholder="身份证后6位" :prefix-icon="Key" />
            </el-form-item>
            <el-form-item prop="phone">
              <el-input v-model="registerForm.phone" placeholder="手机号" :prefix-icon="Phone" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="设置密码" :prefix-icon="Lock"
                show-password />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input v-model="registerForm.confirmPassword" type="password" placeholder="确认密码"
                :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="success" class="submit-btn" :loading="registerLoading" @click="handleRegister">
                注 册
              </el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
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

// ========== 登录 ==========
const loginLoading = ref(false)
const loginFormRef = ref(null)
const loginForm = reactive({
  identityNumber: '',
  password: ''
})
const loginRules = {
  identityNumber: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
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

// ========== 注册 ==========
const registerLoading = ref(false)
const registerFormRef = ref(null)
const registerForm = reactive({
  identityNumber: '',
  name: '',
  idCardLast6: '',
  phone: '',
  password: '',
  confirmPassword: ''
})
const registerRules = {
  identityNumber: [{ required: true, message: '请输入学号/工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  idCardLast6: [{ required: true, message: '请输入身份证后6位', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  password: [{ required: true, message: '请设置密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: (rule, value, cb) => value === registerForm.password ? cb() : cb(new Error('两次密码不一致')), trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value.validate().catch(() => false)
  if (!valid) return
  registerLoading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    registerFormRef.value.resetFields()
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败')
  } finally {
    registerLoading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, .3);

  .header {
    text-align: center;
    margin-bottom: 30px;

    h1 {
      font-size: 24px;
      color: #303133;
      margin: 0 0 8px;
    }

    p {
      font-size: 13px;
      color: #909399;
      margin: 0;
    }
  }

  .submit-btn {
    width: 100%;
  }
}
</style>
