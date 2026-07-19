<template>
  <div class="portal">
    <!-- ===== 顶部导航栏 ===== -->
    <header class="header">
      <div class="header-inner">
        <div class="logo-area">
          <div class="logo-icon-box">
            <svg class="logo-svg" viewBox="0 0 40 40" fill="none">
              <rect width="40" height="40" rx="10" fill="url(#logo-grad)"/>
              <path d="M12 28V16l8-5 8 5v12l-8 5-8-5z" stroke="#fff" stroke-width="1.8" fill="none"/>
              <circle cx="20" cy="20" r="3" fill="#fff" opacity="0.9"/>
              <defs><linearGradient id="logo-grad" x1="0" y1="0" x2="40" y2="40"><stop stop-color="#5B9BD5"/><stop offset="1" stop-color="#4A8AD4"/></linearGradient></defs>
            </svg>
          </div>
          <span class="logo-text">智慧校园 · 管理后台</span>
        </div>
        <nav class="top-nav">
          <a href="#" @click.prevent="router.push('/admin')" :class="['nav-item', { active: route.path === '/admin' }]">首页</a>
          <a href="#" @click.prevent="router.push('/admin/users')" :class="['nav-item', { active: route.path.startsWith('/admin/users') }]">用户管理</a>
          <a href="#" @click.prevent="router.push('/admin/courses')" :class="['nav-item', { active: route.path.startsWith('/admin/courses') }]">课程管理</a>
        </nav>
        <div class="user-area">
          <div class="user-info">
            <span class="user-name">{{ userStore.userInfo?.name || '管理员' }}</span>
            <span class="user-role">系统管理员</span>
          </div>
          <div class="user-avatar">{{ (userStore.userInfo?.name || '管')[0] }}</div>
          <a href="#" @click.prevent="logout" class="logout-link">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/admin'">
      <div class="main-wrap">
        <div class="main-inner">
          <!-- ===== Banner + 数据概览 ===== -->
          <section class="hero-banner">
            <div class="hero-content">
              <h1 class="hero-title">{{ userStore.userInfo?.name || '管理员' }}，欢迎回来</h1>
              <p class="hero-subtitle">系统运行状态良好，各项服务正常</p>
              <div class="hero-stats">
                <div class="hero-stat">
                  <div class="hs-icon" style="background:#EBF3FC;"><el-icon :size="18" color="#5B9BD5"><UserFilled /></el-icon></div>
                  <div class="hs-info">
                    <div class="hs-value">{{ dashboard.todayActiveUsers || 0 }}</div>
                    <div class="hs-label">今日活跃</div>
                  </div>
                </div>
                <div class="hero-stat">
                  <div class="hs-icon" style="background:#ECFDF5;"><el-icon :size="18" color="#10B981"><DataAnalysis /></el-icon></div>
                  <div class="hs-info">
                    <div class="hs-value">{{ dashboard.totalUsers || 0 }}</div>
                    <div class="hs-label">用户总数</div>
                  </div>
                </div>
                <div class="hero-stat">
                  <div class="hs-icon" style="background:#FEF3E2;"><el-icon :size="18" color="#F59E0B"><Clock /></el-icon></div>
                  <div class="hs-info">
                    <div class="hs-value warn">{{ dashboard.pendingRegistrations || 0 }}</div>
                    <div class="hs-label">待审核</div>
                  </div>
                </div>
                <div class="hero-stat">
                  <div class="hs-icon" style="background:#ECFDF5;"><el-icon :size="18" color="#10B981"><CircleCheckFilled /></el-icon></div>
                  <div class="hs-info">
                    <div class="hs-value ok">●</div>
                    <div class="hs-label">运行中</div>
                  </div>
                </div>
              </div>
            </div>
          </section>

          <!-- ===== 核心管理功能（大卡片） ===== -->
          <section class="core-section">
            <div class="section-head">
              <h3 class="section-title">核心管理</h3>
              <span class="section-subtitle">平台运行核心，日常管理必备</span>
            </div>
            <div class="core-grid">
              <div v-for="item in coreModules" :key="item.path" class="core-card" @click="router.push(item.path)">
                <div class="core-card-icon" :style="{ background: item.bg }">
                  <el-icon :size="22" :color="item.color"><component :is="item.icon" /></el-icon>
                </div>
                <div class="core-card-body">
                  <h4 class="core-card-title">{{ item.label }}</h4>
                  <p class="core-card-desc">{{ item.desc }}</p>
                </div>
                <el-icon class="core-card-arrow" :size="14"><ArrowRight /></el-icon>
              </div>
            </div>
          </section>

          <!-- ===== 扩展管理（快捷入口） ===== -->
          <section class="quick-section">
            <div class="section-head">
              <h3 class="section-title">扩展管理</h3>
              <span class="section-subtitle">更多管理功能入口</span>
            </div>
            <div class="quick-grid">
              <div v-for="item in extModules" :key="item.path" class="quick-item" @click="router.push(item.path)">
                <div class="quick-icon-circle" :style="{ background: item.bg }">
                  <el-icon :size="20" :color="item.color"><component :is="item.icon" /></el-icon>
                </div>
                <span class="quick-label">{{ item.label }}</span>
              </div>
            </div>
          </section>
        </div>
      </div>

      <!-- ===== 底部页脚 ===== -->
      <footer class="footer">
        <div class="footer-inner">
          <div class="footer-col footer-intro">
            <div class="footer-logo">
              <div class="logo-icon-box small">
                <svg class="logo-svg" viewBox="0 0 40 40" fill="none">
                  <rect width="40" height="40" rx="10" fill="url(#logo-grad)"/>
                  <path d="M12 28V16l8-5 8 5v12l-8 5-8-5z" stroke="#fff" stroke-width="1.8" fill="none"/>
                  <circle cx="20" cy="20" r="3" fill="#fff" opacity="0.9"/>
                </svg>
              </div>
              <span class="footer-logo-text">智慧校园 · 管理后台</span>
            </div>
            <p class="footer-desc">智慧校园管理后台为系统管理员提供全面的平台运维能力，涵盖用户管理、课程管理、考试管理、一卡通、固定资产、实验室等核心模块的配置与监控。</p>
          </div>
          <div class="footer-col footer-links">
            <h4 class="footer-col-title">快速导航</h4>
            <a href="#" @click.prevent="router.push('/admin/users')">用户与角色</a>
            <a href="#" @click.prevent="router.push('/admin/courses')">课程管理</a>
            <a href="#" @click.prevent="router.push('/admin/exams')">考试管理</a>
            <a href="#" @click.prevent="router.push('/admin/cards')">一卡通管理</a>
          </div>
          <div class="footer-col footer-contact">
            <h4 class="footer-col-title">系统信息</h4>
            <div class="footer-contact-items">
              <span>🖥️ Spring Boot 4.1 + Vue 3.5</span>
              <span>🗄️ MySQL 8.0 + JPA</span>
              <span>🤖 DeepSeek AI 助手</span>
            </div>
            <div class="footer-social">
              <span class="social-dot"></span><span class="social-dot"></span><span class="social-dot"></span><span class="social-dot"></span>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <span>© 2026 智慧校园服务平台 · 管理后台</span>
          <span class="footer-divider">|</span>
          <span>系统版本 v1.0</span>
        </div>
      </footer>
    </template>

    <div v-else class="sub-page">
      <div class="sub-nav">
        <a href="#" @click.prevent="router.push('/admin')" class="back-link"><el-icon :size="14"><ArrowLeft /></el-icon> 返回首页</a>
      </div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { getDashboard } from '@/api/system'
import { ArrowRight, ArrowLeft, UserFilled, DataAnalysis, Clock, CircleCheckFilled, School, Document, Notebook, Tickets, Wallet, OfficeBuilding, Monitor } from '@element-plus/icons-vue'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const dashboard = ref({ todayActiveUsers: 0, totalUsers: 0, pendingRegistrations: 0 })

const loadDashboard = async () => {
  try { const res = await getDashboard(); if (res.data) dashboard.value = res.data } catch {}
}

const coreModules = [
  { label: '用户与角色', desc: '用户管理、角色权限分配', path: '/admin/users', icon: UserFilled, color: '#5B9BD5', bg: '#EBF3FC' },
  { label: '课程管理', desc: '课程维护、排课分配、选课管理', path: '/admin/courses', icon: School, color: '#F59E0B', bg: '#FEF3E2' },
  { label: '内容管理', desc: '新闻发布、公告管理、轮播图配置', path: '/admin/announcements', icon: Notebook, color: '#10B981', bg: '#ECFDF5' },
  { label: '考试管理', desc: '考试安排、成绩管理', path: '/admin/exams', icon: Document, color: '#8B5CF6', bg: '#F3F0FF' },
]

const extModules = [
  { label: '一卡通', path: '/admin/cards', icon: Wallet, color: '#F59E0B', bg: '#FFFBEB' },
  { label: '缴费管理', path: '/admin/payments', icon: Tickets, color: '#6366F1', bg: '#EEF2FF' },
  { label: '固定资产', path: '/admin/assets', icon: OfficeBuilding, color: '#10B981', bg: '#ECFDF5' },
  { label: '实验室', path: '/admin/labs', icon: Monitor, color: '#EC4899', bg: '#FDF2F8' },
  { label: '注册审核', path: '/admin/register-audit', icon: DataAnalysis, color: '#8B5CF6', bg: '#F3F0FF' },
]

const logout = () => { userStore.logout(); router.push('/login') }
onMounted(loadDashboard)
</script>

<style scoped>
* { margin:0; padding:0; box-sizing:border-box; }
.portal { min-height:100vh; display:flex; flex-direction:column; background:#F8FAFE; color:#1A1A2E; font-family:"Microsoft YaHei","PingFang SC","Helvetica Neue",system-ui,sans-serif; -webkit-font-smoothing:antialiased; }

/* Header */
.header { background:#fff; border-bottom:1px solid #EEF0F4; position:sticky; top:0; z-index:100; box-shadow:0 1px 8px rgba(0,0,0,.04); }
.header-inner { max-width:1200px; margin:0 auto; display:flex; align-items:center; height:60px; padding:0 24px; gap:32px; }
.logo-area { display:flex; align-items:center; gap:10px; flex-shrink:0; }
.logo-icon-box { width:36px; height:36px; border-radius:10px; overflow:hidden; flex-shrink:0; }
.logo-icon-box.small { width:32px; height:32px; border-radius:8px; }
.logo-svg { width:100%; height:100%; display:block; }
.logo-text { font-size:17px; font-weight:700; color:#5B9BD5; letter-spacing:.5px; white-space:nowrap; }
.top-nav { display:flex; gap:2px; }
.top-nav .nav-item { padding:8px 16px; border-radius:8px; font-size:14px; color:#4B5563; text-decoration:none; transition:all .2s; white-space:nowrap; font-weight:450; }
.top-nav .nav-item:hover { color:#5B9BD5; background:#F5F9FD; }
.top-nav .nav-item.active { color:#5B9BD5; background:#EBF3FC; font-weight:600; }
.user-area { margin-left:auto; display:flex; align-items:center; gap:10px; font-size:13px; flex-shrink:0; }
.user-info { display:flex; flex-direction:column; line-height:1.3; }
.user-name { color:#1A1A2E; font-weight:600; font-size:13px; }
.user-role { color:#9CA3AF; font-size:11px; }
.user-avatar { width:32px; height:32px; border-radius:50%; background:linear-gradient(135deg,#5B9BD5,#7EB8E8); color:#fff; font-size:13px; font-weight:600; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.logout-link { color:#9CA3AF; text-decoration:none; font-size:12px; padding:4px 8px; border-radius:4px; transition:all .15s; }
.logout-link:hover { color:#EF4444; background:#FEF2F2; }

/* Main */
.main-wrap { flex:1; }
.main-inner { max-width:1200px; margin:0 auto; padding:24px 24px 40px; }

/* Hero Banner */
.hero-banner { background:linear-gradient(135deg,#E3F0FD 0%,#F0F5FC 30%,#FFF9F0 100%); border-radius:18px; padding:36px 44px; margin-bottom:28px; position:relative; overflow:hidden; border:1px solid rgba(91,155,213,.08); }
.hero-banner::before { content:''; position:absolute; top:-60px; right:-40px; width:200px; height:200px; border-radius:50%; background:radial-gradient(circle, rgba(91,155,213,.06) 0%, transparent 70%); pointer-events:none; }
.hero-content { z-index:1; }
.hero-title { font-size:24px; font-weight:700; color:#1A1A2E; margin-bottom:6px; letter-spacing:.3px; }
.hero-subtitle { font-size:14px; color:#6B7280; margin-bottom:20px; }
.hero-stats { display:flex; gap:16px; }
.hero-stat { display:flex; align-items:center; gap:12px; padding:12px 20px; border-radius:12px; background:#fff; border:1px solid #EEF0F4; box-shadow:0 1px 4px rgba(0,0,0,.02); }
.hs-icon { width:40px; height:40px; border-radius:10px; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.hs-info { line-height:1.3; }
.hs-value { font-size:22px; font-weight:700; color:#1A1A2E; }
.hs-value.warn { color:#F59E0B; }
.hs-value.ok { color:#10B981; font-size:14px; }
.hs-label { font-size:11px; color:#9CA3AF; }

/* Section */
.section-head { display:flex; align-items:baseline; gap:14px; margin-bottom:18px; }
.section-title { font-size:18px; font-weight:700; color:#1A1A2E; letter-spacing:.3px; }
.section-subtitle { font-size:13px; color:#9CA3AF; font-weight:400; }

/* Core Cards */
.core-section { margin-bottom:32px; }
.core-grid { display:grid; grid-template-columns:repeat(4, 1fr); gap:18px; }
.core-card { background:#fff; border-radius:14px; padding:22px 20px; border:1px solid #EEF0F4; box-shadow:0 2px 8px rgba(0,0,0,.03); cursor:pointer; transition:all .25s; display:flex; align-items:flex-start; gap:14px; position:relative; }
.core-card:hover { transform:translateY(-3px); box-shadow:0 8px 24px rgba(0,0,0,.06); border-color:#D4E2F4; }
.core-card-icon { width:44px; height:44px; border-radius:12px; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.core-card-body { flex:1; min-width:0; }
.core-card-title { font-size:15px; font-weight:600; color:#1A1A2E; margin-bottom:4px; }
.core-card-desc { font-size:12px; color:#9CA3AF; line-height:1.4; }
.core-card-arrow { color:#C4C9D1; flex-shrink:0; margin-top:14px; transition:color .2s; }
.core-card:hover .core-card-arrow { color:#5B9BD5; }

/* Quick */
.quick-section { margin-bottom:32px; }
.quick-grid { display:flex; gap:14px; flex-wrap:wrap; }
.quick-item { display:flex; flex-direction:column; align-items:center; gap:8px; padding:16px 20px; border-radius:14px; cursor:pointer; transition:all .2s; min-width:90px; background:#fff; border:1px solid #EEF0F4; box-shadow:0 2px 8px rgba(0,0,0,.02); }
.quick-item:hover { transform:translateY(-2px); box-shadow:0 6px 18px rgba(0,0,0,.05); border-color:#D4E2F4; }
.quick-icon-circle { width:44px; height:44px; border-radius:50%; display:flex; align-items:center; justify-content:center; }
.quick-label { font-size:12px; color:#4B5563; font-weight:450; white-space:nowrap; }

/* Footer */
.footer { background:#1A2535; color:#B0B8C1; margin-top:auto; }
.footer-inner { max-width:1200px; margin:0 auto; padding:44px 24px 30px; display:grid; grid-template-columns:1.4fr .8fr .8fr; gap:40px; }
.footer-logo { display:flex; align-items:center; gap:10px; margin-bottom:14px; }
.footer-logo-text { font-size:16px; font-weight:700; color:#E5E7EB; }
.footer-desc { font-size:13px; color:#8896A6; line-height:1.7; max-width:360px; }
.footer-col-title { font-size:14px; font-weight:600; color:#E5E7EB; margin-bottom:14px; }
.footer-links { display:flex; flex-direction:column; gap:10px; }
.footer-links a { font-size:13px; color:#8896A6; text-decoration:none; transition:color .2s; }
.footer-links a:hover { color:#5B9BD5; }
.footer-contact-items { display:flex; flex-direction:column; gap:10px; font-size:13px; color:#8896A6; margin-bottom:16px; }
.footer-social { display:flex; gap:10px; }
.social-dot { width:32px; height:32px; border-radius:50%; background:rgba(255,255,255,.06); border:1px solid rgba(255,255,255,.08); transition:all .2s; cursor:pointer; }
.social-dot:hover { background:rgba(91,155,213,.2); border-color:rgba(91,155,213,.3); }
.footer-bottom { border-top:1px solid rgba(255,255,255,.06); padding:16px 24px; text-align:center; font-size:12px; color:#6B7A8A; }
.footer-divider { margin:0 10px; opacity:.4; }

/* Sub */
.sub-page { flex:1; max-width:1200px; margin:0 auto; width:100%; padding:16px 24px; }
.sub-nav { margin-bottom:12px; }
.back-link { color:#5B9BD5; text-decoration:none; font-size:13px; display:inline-flex; align-items:center; gap:4px; transition:color .2s; }
.back-link:hover { color:#4A8AD4; }

@media (max-width:1024px) {
  .hero-stats { flex-wrap:wrap; }
  .core-grid { grid-template-columns:repeat(2, 1fr); }
  .footer-inner { grid-template-columns:1fr; gap:28px; }
}
@media (max-width:640px) {
  .header-inner { gap:16px; padding:0 14px; }
  .core-grid { grid-template-columns:1fr; }
  .hero-title { font-size:20px; }
  .hero-stat { padding:10px 14px; }
}
</style>
