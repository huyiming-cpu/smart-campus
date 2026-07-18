<template>
  <div class="portal">
    <header class="header">
      <div class="header-inner">
        <div class="logo-area">
          <span class="logo-icon">⚙️</span>
          <span class="logo-text">智慧校园 · 管理后台</span>
        </div>
        <nav class="top-nav">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" @click.prevent="router.push('/admin/users')" class="nav-item">用户管理</a>
          <a href="#" @click.prevent="router.push('/admin/courses')" class="nav-item">课程管理</a>
          <a href="#" @click.prevent="router.push('/admin/config')" class="nav-item">系统配置</a>
        </nav>
        <div class="user-area">
          <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
          <a href="#" @click.prevent="logout" class="logout-link">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/admin'">
      <div class="main-wrap">
        <div class="main-inner">
          <!-- Hero + 统计 -->
          <div class="welcome-hero admin-hero">
            <div class="hero-content">
              <h1>{{ userStore.userInfo?.name || '管理员' }}，欢迎回来</h1>
              <p class="hero-date">{{ today }}</p>
            </div>
            <div class="hero-stats">
              <div class="hero-stat-item">
                <div class="hs-value">{{ dashboard.todayActiveUsers }}</div>
                <div class="hs-label">今日活跃</div>
              </div>
              <div class="hero-stat-item">
                <div class="hs-value">{{ dashboard.totalUsers }}</div>
                <div class="hs-label">用户总数</div>
              </div>
              <div class="hero-stat-item">
                <div class="hs-value" style="color:#f56c6c">{{ dashboard.pendingRegistrations }}</div>
                <div class="hs-label">待审核</div>
              </div>
              <div class="hero-stat-item">
                <div class="hs-value status-ok">●</div>
                <div class="hs-label">运行中</div>
              </div>
            </div>
          </div>

          <!-- 管理功能 -->
          <div class="service-section">
            <div class="section-head"><h3>管理功能</h3></div>
            <div class="service-grid">
              <div v-for="item in modules" :key="item.path" class="service-item" @click="router.push(item.path)">
                <span class="service-icon">{{ item.icon }}</span>
                <span class="service-label">{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <footer class="footer">
        <p>© 2026 智慧校园服务平台 · 管理后台</p>
      </footer>
    </template>

    <div v-else class="sub-page">
      <div class="sub-nav"><a href="#" @click.prevent="router.push('/admin')" class="back-link">← 返回首页</a></div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboard } from '@/api/system'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const dashboard = ref({ todayActiveUsers: 0, totalUsers: 0, pendingRegistrations: 0, serverStatus: '未知' })
const loadDashboard = async () => {
  try { const res = await getDashboard(); if (res.data) dashboard.value = res.data } catch {}
}
onMounted(() => loadDashboard())

const modules = [
  { icon:'📢', label:'内容管理', path:'/admin/announcements' },
  { icon:'👥', label:'用户与角色', path:'/admin/users' },
  { icon:'📚', label:'课程管理', path:'/admin/courses' },
  { icon:'📝', label:'考试管理', path:'/admin/exams' },
  { icon:'💳', label:'一卡通管理', path:'/admin/cards' },
  { icon:'💰', label:'缴费管理', path:'/admin/payments' },
  { icon:'🏢', label:'固定资产', path:'/admin/assets' },
  { icon:'🔬', label:'实验室', path:'/admin/labs' },
  { icon:'⚙️', label:'系统配置', path:'/admin/config' },
]

const logout = () => { userStore.logout(); router.push('/login') }
</script>

<style scoped>
* { margin:0; padding:0; box-sizing:border-box; }
.portal { min-height:100vh; display:flex; flex-direction:column; background:#f5f6f8; color:#333; font-family:"Microsoft YaHei","PingFang SC",sans-serif; }

.header { background:#fff; border-bottom:1px solid #e8e8e8; position:sticky; top:0; z-index:100; box-shadow:0 1px 3px rgba(0,0,0,.04); }
.header-inner { max-width:1200px; margin:0 auto; display:flex; align-items:center; height:56px; padding:0 24px; gap:32px; }
.logo-area { display:flex; align-items:center; gap:8px; flex-shrink:0; }
.logo-icon { font-size:22px; }
.logo-text { font-size:17px; font-weight:700; color:#6c757d; letter-spacing:1px; }
.top-nav { display:flex; gap:4px; }
.top-nav .nav-item { padding:6px 14px; border-radius:4px; font-size:14px; color:#555; text-decoration:none; transition:all .15s; }
.top-nav .nav-item:hover, .top-nav .nav-item.active { color:#6c757d; background:#f0f0f2; }
.user-area { margin-left:auto; display:flex; align-items:center; gap:12px; font-size:13px; color:#888; }
.user-name { color:#333; font-weight:500; }
.logout-link { color:#999; text-decoration:none; font-size:12px; }
.logout-link:hover { color:#e64242; }

.main-wrap { flex:1; }
.main-inner { max-width:1200px; margin:0 auto; padding:24px; }

.welcome-hero { background:linear-gradient(135deg,#5a6268 0%,#7a8288 50%,#495057 100%); border-radius:8px; padding:24px 32px; margin-bottom:20px; color:#fff; display:flex; justify-content:space-between; align-items:center; flex-wrap:wrap; gap:20px; }
.hero-content h1 { font-size:20px; font-weight:500; margin-bottom:4px; }
.hero-date { font-size:13px; opacity:.8; }
.hero-stats { display:flex; gap:24px; }
.hero-stat-item { text-align:center; min-width:60px; }
.hs-value { font-size:26px; font-weight:bold; }
.hs-label { font-size:11px; opacity:.75; }
.status-ok { color:#67c23a; }

.service-section { background:#fff; border-radius:8px; padding:20px 24px; box-shadow:0 1px 3px rgba(0,0,0,.04); }
.section-head h3 { font-size:15px; font-weight:600; color:#222; margin-bottom:16px; }
.service-grid { display:flex; gap:20px; flex-wrap:wrap; }
.service-item { display:flex; flex-direction:column; align-items:center; gap:6px; padding:14px 16px; border-radius:6px; cursor:pointer; transition:all .15s; min-width:72px; }
.service-item:hover { background:#f5f5f6; }
.service-icon { font-size:26px; }
.service-label { font-size:12px; color:#666; }

.footer { text-align:center; padding:16px; color:#bbb; font-size:12px; border-top:1px solid #eee; background:#fff; margin-top:auto; }
.sub-page { flex:1; max-width:1200px; margin:0 auto; width:100%; padding:16px 24px; }
.sub-nav { margin-bottom:8px; }
.back-link { font-size:13px; color:#6c757d; text-decoration:none; }
</style>
