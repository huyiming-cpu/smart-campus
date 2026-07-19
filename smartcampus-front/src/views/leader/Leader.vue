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
          <span class="logo-text">智慧校园服务</span>
        </div>
        <nav class="top-nav">
          <a href="#" @click.prevent="router.push('/leader')" :class="['nav-item', { active: route.path === '/leader' }]">首页</a>
          <a href="#" @click.prevent="router.push('/leader/announcement')" :class="['nav-item', { active: route.path.startsWith('/leader/announcement') }]">通知公告</a>
          <a href="#" @click.prevent="router.push('/leader/news')" :class="['nav-item', { active: route.path.startsWith('/leader/news') }]">学校新闻</a>
        </nav>
        <div class="user-area">
          <span class="user-greeting">👋</span>
          <div class="user-info">
            <span class="user-name">{{ userStore.userInfo?.name || '领导' }}</span>
            <span class="user-dept">{{ userStore.userInfo?.department || '' }}</span>
          </div>
          <div class="user-avatar">{{ (userStore.userInfo?.name || '领')[0] }}</div>
          <a href="#" @click.prevent="logout" class="logout-link">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/leader'">
      <div class="main-wrap">
        <div class="main-inner">
          <!-- ===== Banner 区 ===== -->
          <section class="hero-banner">
            <div class="hero-content">
              <h1 class="hero-title">{{ userStore.userInfo?.name || '领导' }}，欢迎回来</h1>
              <p class="hero-subtitle">智慧校园，数据驱动决策，管理更高效</p>
              <div class="hero-date-row">
                <el-icon :size="16" color="#6B7280"><Calendar /></el-icon>
                <span>{{ today }}</span>
              </div>
            </div>
            <div class="hero-illustration">
              <svg viewBox="0 0 260 180" fill="none" class="hero-svg">
                <rect x="30" y="15" width="200" height="18" rx="9" fill="#fff" opacity="0.7"/>
                <rect x="40" y="21" width="50" height="6" rx="3" fill="#5B9BD5" opacity="0.4"/>
                <rect x="100" y="21" width="80" height="6" rx="3" fill="#C4D9F0"/>
                <rect x="30" y="45" width="95" height="80" rx="10" fill="#fff" opacity="0.7"/>
                <rect x="42" y="56" width="70" height="30" rx="6" fill="#EBF3FC"/>
                <rect x="42" y="92" width="55" height="4" rx="2" fill="#C4D9F0"/>
                <rect x="42" y="100" width="45" height="4" rx="2" fill="#C4D9F0"/>
                <rect x="42" y="108" width="35" height="4" rx="2" fill="#C4D9F0"/>
                <rect x="135" y="45" width="95" height="80" rx="10" fill="#fff" opacity="0.7"/>
                <circle cx="182" cy="70" r="16" fill="#5B9BD5" opacity="0.1"/>
                <rect x="147" y="92" width="70" height="24" rx="5" fill="#EBF3FC"/>
                <rect x="30" y="140" width="200" height="25" rx="8" fill="#fff" opacity="0.5"/>
                <rect x="45" y="148" width="30" height="9" rx="4.5" fill="#10B981" opacity="0.2"/>
                <rect x="85" y="148" width="60" height="9" rx="4.5" fill="#5B9BD5" opacity="0.15"/>
                <rect x="155" y="148" width="50" height="9" rx="4.5" fill="#F59E0B" opacity="0.2"/>
              </svg>
            </div>
          </section>

          <!-- ===== 通知公告 + 学校新闻 双栏 ===== -->
          <div class="info-row">
            <div class="info-panel">
              <div class="panel-head">
                <h3 class="panel-title">📢 通知公告</h3>
                <a href="#" @click.prevent="router.push('/leader/announcement')" class="panel-more">更多 <el-icon :size="12"><ArrowRight /></el-icon></a>
              </div>
              <ul class="news-list" v-if="announcements.length">
                <li v-for="item in announcements.slice(0,6)" :key="item.id" @click="router.push('/leader/announcement')" class="news-item">
                  <span class="item-dot" :class="{urgent:item.isUrgent,top:item.isTop}"></span>
                  <span class="item-title">{{ item.title }}</span>
                  <span class="item-date">{{ item.publishTime?.slice(5,10) }}</span>
                </li>
              </ul>
              <div v-else class="empty-list">暂无公告</div>
            </div>
            <div class="info-panel">
              <div class="panel-head">
                <h3 class="panel-title">📰 学校新闻</h3>
                <a href="#" @click.prevent="router.push('/leader/news')" class="panel-more">更多 <el-icon :size="12"><ArrowRight /></el-icon></a>
              </div>
              <ul class="news-list" v-if="news.length">
                <li v-for="item in news.slice(0,6)" :key="item.id" @click="router.push('/leader/news')" class="news-item">
                  <span class="item-tag">{{ item.category||'综合' }}</span>
                  <span class="item-title">{{ item.title }}</span>
                  <span class="item-date">{{ item.publishTime?.slice(5,10) }}</span>
                </li>
              </ul>
              <div v-else class="empty-list">暂无新闻</div>
            </div>
          </div>

          <!-- ===== 核心管理功能（大卡片） ===== -->
          <section class="core-section">
            <div class="section-head">
              <h3 class="section-title">管理功能</h3>
              <span class="section-subtitle">高效管理工具，数据驱动决策</span>
            </div>
            <div class="core-grid">
              <div v-for="item in coreServices" :key="item.path" class="core-card" @click="router.push(item.path)">
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

          <!-- ===== 快捷入口 ===== -->
          <section class="quick-section">
            <div class="section-head">
              <h3 class="section-title">快捷入口</h3>
              <span class="section-subtitle">常用功能，快速访问</span>
            </div>
            <div class="quick-grid">
              <div v-for="item in quickServices" :key="item.path" class="quick-item" @click="router.push(item.path)">
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
              <span class="footer-logo-text">智慧校园信息门户</span>
            </div>
            <p class="footer-desc">智慧校园信息门户为各级管理人员提供数据驱动的决策支持，涵盖教务管理、科研统计、审批流程与团队协作，助力高校治理现代化。</p>
          </div>
          <div class="footer-col footer-links">
            <h4 class="footer-col-title">快速链接</h4>
            <a href="#" @click.prevent="router.push('/leader')">领导首页</a>
            <a href="#" @click.prevent="router.push('/leader/stats')">数据统计</a>
            <a href="#" @click.prevent="router.push('/leader/documents')">公文审批</a>
            <a href="#" @click.prevent="router.push('/leader/research')">科研总览</a>
            <a href="#" @click.prevent="router.push('/leader/announcement')">通知公告</a>
          </div>
          <div class="footer-col footer-contact">
            <h4 class="footer-col-title">关注我们</h4>
            <div class="footer-contact-items">
              <span>📧 campus@school.edu.cn</span>
              <span>📞 010-1234-5678</span>
              <span>📍 计算机学院信息中心</span>
            </div>
            <div class="footer-social">
              <span class="social-dot"></span><span class="social-dot"></span><span class="social-dot"></span><span class="social-dot"></span>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <span>© 2026 智慧校园服务平台 · 计算机学院</span>
          <span class="footer-divider">|</span>
          <span>让校园管理更智能</span>
        </div>
      </footer>
    </template>

    <div v-else class="sub-page">
      <div class="sub-nav">
        <a href="#" @click.prevent="router.push('/leader')" class="back-link"><el-icon :size="14"><ArrowLeft /></el-icon> 返回首页</a>
      </div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import { Calendar, ArrowRight, ArrowLeft, DataAnalysis, DocumentChecked, Tickets, Medal, Timer, Notebook, Phone, User } from '@element-plus/icons-vue'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const announcements = ref([]); const news = ref([])

const loadInfo = async () => {
  try { const r = await request.get('/api/notice/announcements'); announcements.value = r.data || [] } catch {}
  try { const r = await request.get('/api/notice/news'); news.value = r.data || [] } catch {}
}

const coreServices = [
  { label: '数据统计', desc: '全校数据概览、趋势分析与报表', path: '/leader/stats', icon: DataAnalysis, color: '#5B9BD5', bg: '#EBF3FC' },
  { label: '公文审批', desc: '待办公文处理、审批流程管理', path: '/leader/documents', icon: DocumentChecked, color: '#F59E0B', bg: '#FEF3E2' },
  { label: '奖助贷审批', desc: '奖学金、助学金、贷款审核', path: '/leader/applications', icon: Tickets, color: '#10B981', bg: '#ECFDF5' },
  { label: '科研总览', desc: '学院科研成果统计与趋势', path: '/leader/research', icon: Medal, color: '#8B5CF6', bg: '#F3F0FF' },
]

const quickServices = [
  { label: '工作计划', path: '/leader/work-plans', icon: Timer, color: '#5B9BD5', bg: '#EBF3FC' },
  { label: '通讯录', path: '/leader/directory', icon: Phone, color: '#6366F1', bg: '#EEF2FF' },
  { label: '通知公告', path: '/leader/announcement', icon: Notebook, color: '#F59E0B', bg: '#FFFBEB' },
  { label: '个人信息', path: '/leader/profile', icon: User, color: '#8B5CF6', bg: '#F3F0FF' },
]

const logout = () => { userStore.logout(); router.push('/login') }
onMounted(loadInfo)
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
.user-greeting { font-size:16px; }
.user-info { display:flex; flex-direction:column; line-height:1.3; }
.user-name { color:#1A1A2E; font-weight:600; font-size:13px; }
.user-dept { color:#9CA3AF; font-size:11px; }
.user-avatar { width:32px; height:32px; border-radius:50%; background:linear-gradient(135deg,#5B9BD5,#7EB8E8); color:#fff; font-size:13px; font-weight:600; display:flex; align-items:center; justify-content:center; flex-shrink:0; }
.logout-link { color:#9CA3AF; text-decoration:none; font-size:12px; padding:4px 8px; border-radius:4px; transition:all .15s; }
.logout-link:hover { color:#EF4444; background:#FEF2F2; }

/* Main */
.main-wrap { flex:1; }
.main-inner { max-width:1200px; margin:0 auto; padding:24px 24px 40px; }

/* Hero Banner */
.hero-banner { background:linear-gradient(135deg,#E3F0FD 0%,#F0F5FC 30%,#FFF9F0 100%); border-radius:18px; padding:40px 44px; margin-bottom:28px; display:flex; align-items:center; justify-content:space-between; gap:40px; position:relative; overflow:hidden; border:1px solid rgba(91,155,213,.08); }
.hero-banner::before { content:''; position:absolute; top:-60px; right:-40px; width:200px; height:200px; border-radius:50%; background:radial-gradient(circle, rgba(91,155,213,.06) 0%, transparent 70%); pointer-events:none; }
.hero-content { flex:1; z-index:1; }
.hero-title { font-size:28px; font-weight:700; color:#1A1A2E; margin-bottom:8px; letter-spacing:.3px; }
.hero-subtitle { font-size:15px; color:#6B7280; margin-bottom:12px; }
.hero-date-row { display:flex; align-items:center; gap:6px; font-size:13px; color:#6B7280; }
.hero-illustration { flex-shrink:0; z-index:1; }
.hero-svg { width:260px; height:180px; }

/* Info Panel Row */
.info-row { display:flex; gap:20px; margin-bottom:32px; }
.info-panel { flex:1; background:#fff; border-radius:14px; border:1px solid #EEF0F4; box-shadow:0 2px 8px rgba(0,0,0,.03); overflow:hidden; }
.panel-head { display:flex; justify-content:space-between; align-items:center; padding:16px 20px; border-bottom:1px solid #F3F4F6; }
.panel-title { font-size:15px; font-weight:600; color:#1A1A2E; }
.panel-more { font-size:12px; color:#5B9BD5; text-decoration:none; display:flex; align-items:center; gap:2px; }
.panel-more:hover { color:#4A8AD4; }
.news-list { list-style:none; }
.news-item { display:flex; align-items:center; gap:10px; padding:11px 20px; border-bottom:1px solid #F7F8FA; cursor:pointer; transition:background .15s; }
.news-item:hover { background:#F8FAFE; }
.news-item:last-child { border-bottom:none; }
.item-dot { width:6px; height:6px; border-radius:50%; background:#10B981; flex-shrink:0; }
.item-dot.urgent,.item-dot.top { background:#EF4444; width:8px; height:8px; }
.item-tag { font-size:11px; padding:2px 8px; border-radius:4px; background:#EBF3FC; color:#5B9BD5; flex-shrink:0; }
.item-title { flex:1; font-size:13px; color:#4B5563; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.item-date { font-size:11px; color:#C4C9D1; flex-shrink:0; }
.empty-list { padding:36px 20px; text-align:center; color:#D4D8DF; font-size:13px; }

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
  .hero-banner { flex-direction:column; padding:32px 28px; }
  .hero-illustration { display:none; }
  .info-row { flex-direction:column; }
  .core-grid { grid-template-columns:repeat(2, 1fr); }
  .footer-inner { grid-template-columns:1fr; gap:28px; }
}
@media (max-width:640px) {
  .header-inner { gap:16px; padding:0 14px; }
  .core-grid { grid-template-columns:1fr; }
  .hero-title { font-size:22px; }
}
</style>
