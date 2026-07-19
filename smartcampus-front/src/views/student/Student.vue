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
          <a href="#" @click.prevent="router.push('/student')" :class="['nav-item', { active: route.path === '/student' }]">首页</a>
          <a href="#" @click.prevent="router.push('/student/notice')" :class="['nav-item', { active: route.path.startsWith('/student/notice') }]">通知公告</a>
          <a href="#" @click.prevent="router.push('/student/news')" :class="['nav-item', { active: route.path.startsWith('/student/news') }]">学校新闻</a>
        </nav>
        <div class="user-area">
          <span class="user-greeting">👋</span>
          <div class="user-info">
            <span class="user-name">{{ userStore.userInfo?.name || '同学' }}</span>
            <span class="user-dept">{{ userStore.userInfo?.department || '' }}</span>
          </div>
          <div class="user-avatar">{{ (userStore.userInfo?.name || '学')[0] }}</div>
          <a href="#" @click.prevent="logout" class="logout-link">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/student'">
      <div class="main-wrap">
        <div class="main-inner">
          <!-- ===== Banner 区 ===== -->
          <section class="hero-banner">
            <div class="hero-content">
              <h1 class="hero-title">{{ userStore.userInfo?.name || '同学' }}，{{ greeting }}</h1>
              <p class="hero-subtitle">智慧校园，让学习生活更便捷、更精彩</p>
              <div class="hero-search">
                <el-icon class="search-icon"><Search /></el-icon>
                <input v-model="searchKeyword" type="text" placeholder="搜索课程、通知、成绩..." class="search-input" @keyup.enter="onSearch" />
                <button class="search-btn" @click="onSearch">搜索</button>
              </div>
              <div class="hero-tags">
                <span class="hero-tag" @click="router.push('/student/study')">📖 课程学习</span>
                <span class="hero-tag" @click="router.push('/student/scores')">📝 成绩查询</span>
                <span class="hero-tag" @click="router.push('/student/wallet')">💳 一卡通</span>
                <span class="hero-tag" @click="router.push('/student/exams')">📋 考试中心</span>
                <span class="hero-tag" @click="router.push('/student/schedule')">📅 校历查询</span>
              </div>
              <div class="hero-date-row">
                <el-icon :size="16" color="#6B7280"><Calendar /></el-icon>
                <span>{{ today }}</span>
              </div>
              <div class="hero-stats">
                <div class="hero-stat"><strong>{{ announcements.length }}</strong><span>条公告</span></div>
                <div class="hero-stat"><strong>{{ news.length }}</strong><span>条新闻</span></div>
              </div>
            </div>
            <div class="hero-illustration">
              <svg viewBox="0 0 260 180" fill="none" class="hero-svg">
                <rect x="20" y="20" width="100" height="70" rx="14" fill="#fff" opacity="0.7"/>
                <rect x="30" y="30" width="35" height="4" rx="2" fill="#5B9BD5" opacity="0.5"/>
                <rect x="30" y="40" width="60" height="3" rx="1.5" fill="#C4D9F0"/>
                <rect x="30" y="48" width="50" height="3" rx="1.5" fill="#C4D9F0"/>
                <rect x="140" y="40" width="100" height="70" rx="14" fill="#fff" opacity="0.7"/>
                <rect x="155" y="48" width="70" height="4" rx="2" fill="#5B9BD5" opacity="0.4"/>
                <rect x="155" y="58" width="55" height="3" rx="1.5" fill="#C4D9F0"/>
                <rect x="155" y="66" width="45" height="3" rx="1.5" fill="#C4D9F0"/>
                <rect x="80" y="108" width="100" height="55" rx="14" fill="#fff" opacity="0.6"/>
                <circle cx="130" cy="135" r="18" fill="#10B981" opacity="0.12"/>
                <rect x="95" y="118" width="70" height="4" rx="2" fill="#10B981" opacity="0.4"/>
              </svg>
            </div>
          </section>

          <!-- ===== 学校新闻轮播 ===== -->
          <section class="news-carousel" v-if="news.length" @mouseenter="pauseCarousel" @mouseleave="resumeCarousel">
            <div class="carousel-viewport">
              <div class="carousel-track" :style="{ transform: `translateX(-${carouselIndex * 100}%)`, transition: carouselPaused ? 'none' : 'transform 0.5s ease-in-out' }">
                <div v-for="(item, i) in news" :key="i" class="carousel-slide" @click="router.push('/student/news')">
                  <div class="slide-card">
                    <div class="slide-image-box">
                      <img v-if="item.coverImage" :src="'/uploads/news/' + item.coverImage" :alt="item.title" class="slide-cover-img"/>
                      <el-icon v-else :size="28" color="#fff"><Notebook /></el-icon>
                    </div>
                    <div class="slide-body">
                      <div class="slide-meta">
                        <span class="slide-tag">{{ item.category || '校园新闻' }}</span>
                        <span class="slide-date">{{ item.publishTime?.slice(0, 10) || '' }}</span>
                      </div>
                      <h3 class="slide-title">{{ item.title }}</h3>
                      <p class="slide-summary">{{ item.summary || item.title }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="carousel-dots">
              <span v-for="(_, i) in news" :key="i" class="carousel-dot" :class="{ active: i === carouselIndex }" @click="carouselIndex = i"></span>
            </div>
            <button class="carousel-arrow prev" @click="prevSlide"><el-icon :size="18"><ArrowLeft /></el-icon></button>
            <button class="carousel-arrow next" @click="nextSlide"><el-icon :size="18"><ArrowRight /></el-icon></button>
          </section>

          <!-- ===== 核心服务 ===== -->
          <section class="core-section">
            <div class="section-head">
              <h3 class="section-title">学习服务</h3>
              <span class="section-subtitle">一站式学习管理，助力学业成长</span>
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

          <!-- ===== 快捷服务 ===== -->
          <section class="quick-section">
            <div class="section-head">
              <h3 class="section-title">校园服务</h3>
              <span class="section-subtitle">高频服务，一键直达</span>
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
            <p class="footer-desc">智慧校园信息门户是面向全校师生的综合服务平台，集教务管理、学习支持、校园生活于一体，致力于为同学们提供便捷高效的学习体验。</p>
          </div>
          <div class="footer-col footer-links">
            <h4 class="footer-col-title">快速链接</h4>
            <a href="#" @click.prevent="router.push('/student')">学生首页</a>
            <a href="#" @click.prevent="router.push('/student/study')">学习中心</a>
            <a href="#" @click.prevent="router.push('/student/exams')">考试中心</a>
            <a href="#" @click.prevent="router.push('/student/wallet')">一卡通</a>
            <a href="#" @click.prevent="router.push('/student/notice')">通知公告</a>
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
          <span>让校园生活更简单</span>
        </div>
      </footer>
    </template>

    <div v-else class="sub-page">
      <div class="sub-nav">
        <a href="#" @click.prevent="router.push('/student')" class="back-link"><el-icon :size="14"><ArrowLeft /></el-icon> 返回首页</a>
      </div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import { Search, Calendar, ArrowRight, ArrowLeft, Notebook, School, Timer, Wallet, Document, Tickets, Reading, Phone } from '@element-plus/icons-vue'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const searchKeyword = ref('')

const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 8) return '早上好'; if (h < 12) return '上午好'
  if (h < 14) return '中午好'; if (h < 18) return '下午好'
  return '晚上好'
})

const announcements = ref([]); const news = ref([])
const carouselIndex = ref(0); const carouselPaused = ref(false)
let carouselTimer = null

const nextSlide = () => { carouselIndex.value = (carouselIndex.value + 1) % news.value.length }
const prevSlide = () => { carouselIndex.value = (carouselIndex.value - 1 + news.value.length) % news.value.length }

const startCarousel = () => { stopCarousel(); if (news.value.length <= 1) return; carouselTimer = setInterval(() => { if (!carouselPaused.value) nextSlide() }, 3500) }
const stopCarousel = () => { if (carouselTimer) { clearInterval(carouselTimer); carouselTimer = null } }
const pauseCarousel = () => { carouselPaused.value = true }
const resumeCarousel = () => { carouselPaused.value = false }

const loadInfo = async () => {
  try { const r = await request.get('/api/notice/announcements'); announcements.value = r.data || [] } catch {}
  try { const r = await request.get('/api/notice/news'); news.value = r.data || []; if (news.value.length) startCarousel() } catch {}
}

const coreServices = [
  { label: '学习中心', desc: '课程学习、成绩查询、课表查看', path: '/student/study', icon: Reading, color: '#5B9BD5', bg: '#EBF3FC' },
  { label: '考试中心', desc: '考试安排、等级考试报名', path: '/student/exams', icon: Timer, color: '#F59E0B', bg: '#FEF3E2' },
  { label: '一卡通缴费', desc: '校园卡管理、在线缴费', path: '/student/wallet', icon: Wallet, color: '#10B981', bg: '#ECFDF5' },
  { label: '毕业设计', desc: '选题申报、论文提交、答辩', path: '/student/graduation', icon: Document, color: '#8B5CF6', bg: '#F3F0FF' },
]

const quickServices = [
  { label: '实验室', path: '/student/labs', icon: School, color: '#5B9BD5', bg: '#EBF3FC' },
  { label: '校园服务', path: '/student/applications', icon: Tickets, color: '#6366F1', bg: '#EEF2FF' },
  { label: '通知公告', path: '/student/notice', icon: Notebook, color: '#EC4899', bg: '#FDF2F8' },
  { label: '个人信息', path: '/student/profile', icon: Phone, color: '#8B5CF6', bg: '#F3F0FF' },
]

const onSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push(`/student/notice?keyword=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const logout = () => { userStore.logout(); router.push('/login') }
onMounted(loadInfo)
onBeforeUnmount(stopCarousel)
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
.hero-date-row { display:flex; align-items:center; gap:6px; font-size:13px; color:#6B7280; margin-bottom:16px; }
.hero-stats { display:flex; gap:16px; }
.hero-stat { padding:8px 18px; border-radius:10px; background:#fff; border:1px solid #EEF0F4; box-shadow:0 1px 4px rgba(0,0,0,.02); }
.hero-stat strong { color:#5B9BD5; font-size:20px; margin-right:4px; }
.hero-stat span { color:#9CA3AF; font-size:12px; }

/* 搜索框 */
.hero-search{display:flex;align-items:center;background:#fff;border-radius:12px;padding:4px 4px 4px 16px;border:1px solid #E5E7EB;box-shadow:0 2px 10px rgba(0,0,0,.04);max-width:480px;transition:border-color .2s,box-shadow .2s;margin-bottom:12px;}
.hero-search:focus-within{border-color:#5B9BD5;box-shadow:0 2px 16px rgba(91,155,213,.12);}
.search-icon{color:#9CA3AF;flex-shrink:0;}
.search-input{flex:1;border:none;outline:none;font-size:14px;padding:10px 10px;color:#1A1A2E;background:transparent;font-family:inherit;}
.search-input::placeholder{color:#C4C9D1;}
.search-btn{padding:10px 22px;background:linear-gradient(135deg,#5B9BD5,#4A8AD4);color:#fff;border:none;border-radius:10px;font-size:14px;font-weight:500;cursor:pointer;transition:all .2s;white-space:nowrap;font-family:inherit;}
.search-btn:hover{background:linear-gradient(135deg,#4A8AD4,#3A7AC4);box-shadow:0 4px 12px rgba(91,155,213,.3);}

/* 热门标签 */
.hero-tags{display:flex;gap:10px;margin-top:12px;flex-wrap:wrap;}
.hero-tag{font-size:12px;padding:4px 12px;border-radius:20px;background:rgba(91,155,213,.08);color:#5B9BD5;cursor:pointer;transition:all .2s;border:1px solid transparent;}
.hero-tag:hover{background:rgba(91,155,213,.16);border-color:rgba(91,155,213,.25);}

.hero-illustration { flex-shrink:0; z-index:1; }
.hero-svg { width:260px; height:180px; }

/* Section */
.section-head { display:flex; align-items:baseline; gap:14px; margin-bottom:18px; }
.section-title { font-size:18px; font-weight:700; color:#1A1A2E; letter-spacing:.3px; }
.section-subtitle { font-size:13px; color:#9CA3AF; font-weight:400; }

/* News Carousel */
.news-carousel { position:relative; margin-bottom:28px; background:#fff; border-radius:14px; border:1px solid #EEF0F4; box-shadow:0 2px 10px rgba(0,0,0,.03); overflow:hidden; }
.carousel-viewport { overflow:hidden; }
.carousel-track { display:flex; will-change:transform; }
.carousel-slide { flex:0 0 100%; cursor:pointer; }
.slide-card { display:flex; align-items:center; gap:28px; padding:28px 36px; background:linear-gradient(135deg,#F8FAFE 0%,#F0F5FC 100%); min-height:160px; }
.slide-image-box { width:240px; height:140px; border-radius:12px; background:linear-gradient(135deg,#5B9BD5,#7EB8E8); display:flex; align-items:center; justify-content:center; flex-shrink:0; overflow:hidden; }
.slide-cover-img { width:100%; height:100%; object-fit:cover; display:block; }
.slide-body { flex:1; min-width:0; }
.slide-meta { display:flex; align-items:center; gap:12px; margin-bottom:8px; }
.slide-tag { font-size:11px; padding:2px 10px; border-radius:4px; background:#EBF3FC; color:#5B9BD5; font-weight:500; }
.slide-date { font-size:12px; color:#9CA3AF; }
.slide-title { font-size:16px; font-weight:600; color:#1A1A2E; margin-bottom:6px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.slide-summary { font-size:13px; color:#6B7280; line-height:1.5; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp:2; -webkit-box-orient:vertical; }
.carousel-dots { display:flex; justify-content:center; gap:8px; padding:14px 0; }
.carousel-dot { width:8px; height:8px; border-radius:50%; background:#D4D8DF; cursor:pointer; transition:all .3s; }
.carousel-dot.active { width:22px; border-radius:4px; background:#5B9BD5; }
.carousel-arrow { position:absolute; top:50%; transform:translateY(-50%); width:36px; height:36px; border-radius:50%; background:rgba(255,255,255,.85); border:1px solid #E5E7EB; display:flex; align-items:center; justify-content:center; cursor:pointer; color:#6B7280; transition:all .2s; z-index:2; box-shadow:0 2px 6px rgba(0,0,0,.06); }
.carousel-arrow:hover { background:#fff; color:#5B9BD5; border-color:#5B9BD5; box-shadow:0 4px 12px rgba(91,155,213,.15); }
.carousel-arrow.prev { left:14px; } .carousel-arrow.next { right:14px; }

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

/* Sub page */
.sub-page { flex:1; max-width:1200px; margin:0 auto; width:100%; padding:16px 24px; }
.sub-nav { margin-bottom:12px; }
.back-link { color:#5B9BD5; text-decoration:none; font-size:13px; display:inline-flex; align-items:center; gap:4px; transition:color .2s; }
.back-link:hover { color:#4A8AD4; }

@media (max-width:1024px) {
  .hero-banner { flex-direction:column; padding:32px 28px; }
  .hero-illustration { display:none; }
  .core-grid { grid-template-columns:repeat(2, 1fr); }
  .footer-inner { grid-template-columns:1fr; gap:28px; }
}
@media (max-width:640px) {
  .header-inner { gap:16px; padding:0 14px; }
  .top-nav .nav-item { padding:6px 10px; font-size:13px; }
  .core-grid { grid-template-columns:1fr; }
  .hero-title { font-size:22px; }
  .main-inner { padding:16px 14px 32px; }
}
</style>
