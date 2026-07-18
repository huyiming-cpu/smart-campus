<template>
  <div class="portal">
    <header class="header">
      <div class="header-inner">
        <div class="logo">
          <svg class="logo-svg" viewBox="0 0 40 40"><rect width="40" height="40" rx="8" fill="url(#lg)"/><text x="20" y="27" text-anchor="middle" fill="#fff" font-size="20" font-weight="700">智</text><defs><linearGradient id="lg" x1="0" y1="0" x2="40" y2="40"><stop stop-color="#4facfe"/><stop offset="1" stop-color="#00f2fe"/></linearGradient></defs></svg>
          <span class="logo-text">智慧校园</span>
        </div>
        <nav class="nav">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" @click.prevent="router.push('/student/notice')" class="nav-item">通知公告</a>
          <a href="#" @click.prevent="router.push('/student/news')" class="nav-item">学校新闻</a>
        </nav>
        <div class="user">
          <div class="user-avatar">{{ (userStore.userInfo?.name||'?')[0] }}</div>
          <span class="user-name">{{ userStore.userInfo?.name }}</span>
          <a href="#" @click.prevent="logout" class="logout">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/student'">
      <section class="hero">
        <div class="hero-particles"></div>
        <div class="hero-content">
          <p class="hero-greeting">{{ greeting }}，{{ userStore.userInfo?.name || '同学' }}</p>
          <h1 class="hero-title">智慧校园服务平台</h1>
          <p class="hero-sub">{{ today }}</p>
          <div class="hero-quick">
            <div class="quick-stat"><strong>{{ announcements.length }}</strong><span>条公告</span></div>
            <div class="quick-stat"><strong>{{ news.length }}</strong><span>条新闻</span></div>
          </div>
        </div>
      </section>

      <div class="main">
        <!-- 学校新闻轮播 -->
        <section class="section-block" v-if="news.length">
          <div class="section-header">
            <h2 class="section-title"><span class="title-bar"></span>学校新闻</h2>
            <a href="#" @click.prevent="router.push('/student/news')" class="section-more">查看全部 →</a>
          </div>
          <div class="carousel">
            <div class="carousel-viewport">
              <div class="carousel-track" :style="{transform: 'translateX(-' + (currentSlide * 100) + '%)'}">
                <div v-for="item in news" :key="item.id" class="carousel-slide" @click="router.push('/student/news')">
                  <div class="slide-cover">
                    <img v-if="item.coverImage" :src="'/uploads/news/' + item.coverImage" alt="" />
                    <div v-else class="slide-cover-fallback"></div>
                  </div>
                  <div class="slide-overlay">
                    <span class="slide-category">{{ item.category||'综合' }}</span>
                    <h3 class="slide-title">{{ item.title }}</h3>
                    <span class="slide-date">{{ item.publishTime?.slice(0,10) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="carousel-dots">
              <span v-for="(_,i) in news" :key="i" class="dot" :class="{active:i===currentSlide}" @click="currentSlide=i"></span>
            </div>
          </div>
        </section>

        <!-- 快捷服务 -->
        <section class="section-block">
          <div class="section-header">
            <h2 class="section-title"><span class="title-bar"></span>快捷服务</h2>
          </div>
          <div class="service-grid">
            <div v-for="(item, idx) in services" :key="item.path" class="service-card" :style="{background: gradients[idx % gradients.length]}" @click="router.push(item.path)">
              <div class="sc-name">{{ item.label }}</div>
            </div>
          </div>
        </section>
      </div>

      <footer class="footer">
        <p>智慧校园服务平台 &copy; 2026</p>
      </footer>
    </template>

    <div v-else class="sub-page">
      <div class="sub-nav"><a href="#" @click.prevent="router.push('/student')">← 返回首页</a></div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()

const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 8) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const announcements = ref([])
const news = ref([])
const currentSlide = ref(0)

let timer = null
const startCarousel = () => {
  if (news.value.length <= 1) return
  timer = setInterval(() => { currentSlide.value = (currentSlide.value + 1) % news.value.length }, 4000)
}
onUnmounted(() => { if (timer) clearInterval(timer) })

const loadInfo = async () => {
  try { const r = await request.get('/api/notice/announcements'); announcements.value = r.data || [] } catch {}
  try { const r = await request.get('/api/notice/news'); news.value = r.data || [] } catch {}
  if (news.value.length) setTimeout(startCarousel, 100)
}

const gradients = [
  'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
  'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
  'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
  'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
  'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
  'linear-gradient(135deg, #a18cd1 0%, #fbc2eb 100%)',
  'linear-gradient(135deg, #fccb90 0%, #d57eeb 100%)',
]

const services = [
  { label:'学习中心', path:'/student/study' },
  { label:'考试中心', path:'/student/exams' },
  { label:'一卡通', path:'/student/wallet' },
  { label:'个人信息', path:'/student/profile' },
  { label:'实验室', path:'/student/labs' },
  { label:'毕业设计', path:'/student/graduation' },
  { label:'校园服务', path:'/student/applications' },
]

const logout = () => { userStore.logout(); router.push('/login') }
onMounted(loadInfo)
</script>

<style scoped>
* { margin:0; padding:0; box-sizing:border-box; }

.portal {
  min-height:100vh; display:flex; flex-direction:column;
  background: #f0f2f8;
  color: #2c3e50;
  font-family: "Microsoft YaHei","PingFang SC",-apple-system,sans-serif;
}

/* Header */
.header {
  background: rgba(15,23,42,.95);
  backdrop-filter: blur(10px);
  position: sticky; top:0; z-index:100;
  border-bottom: 1px solid rgba(255,255,255,.06);
}
.header-inner { max-width:1280px; margin:0 auto; padding:0 28px; height:60px; display:flex; align-items:center; gap:40px; }
.logo { display:flex; align-items:center; gap:10px; }
.logo-svg { width:36px; height:36px; }
.logo-text { font-size:18px; font-weight:700; color:#e2e8f0; letter-spacing:2px; }
.nav { display:flex; gap:4px; }
.nav-item { padding:8px 18px; border-radius:6px; font-size:14px; color:#94a3b8; text-decoration:none; transition:all .2s; }
.nav-item:hover { color:#e2e8f0; background:rgba(255,255,255,.06); }
.nav-item.active { color:#fff; background:rgba(255,255,255,.12); font-weight:500; }
.user { display:flex; align-items:center; gap:10px; margin-left:auto; }
.user-avatar { width:32px; height:32px; border-radius:50%; background:linear-gradient(135deg,#667eea,#764ba2); color:#fff; display:flex; align-items:center; justify-content:center; font-size:14px; font-weight:600; }
.user-name { color:#cbd5e1; font-size:13px; }
.logout { color:#64748b; text-decoration:none; font-size:12px; }
.logout:hover { color:#94a3b8; }

/* Hero */
.hero { position:relative; background:linear-gradient(135deg,#0f172a 0%,#1e293b 40%,#0f172a 100%); padding:60px 0; overflow:hidden; }
.hero-particles { position:absolute; inset:0; background: radial-gradient(circle at 20% 50%, rgba(102,126,234,.15) 0%, transparent 50%), radial-gradient(circle at 80% 50%, rgba(240,147,251,.1) 0%, transparent 50%), radial-gradient(circle at 50% 80%, rgba(79,172,254,.08) 0%, transparent 50%); }
.hero-content { position:relative; max-width:1280px; margin:0 auto; padding:0 28px; }
.hero-greeting { color:#8b9dc3; font-size:15px; margin-bottom:8px; }
.hero-title { font-size:36px; font-weight:700; background:linear-gradient(135deg,#667eea 0%,#f093fb 50%,#4facfe 100%); -webkit-background-clip:text; -webkit-text-fill-color:transparent; background-clip:text; margin-bottom:8px; }
.hero-sub { color:#64748b; font-size:14px; margin-bottom:24px; }
.hero-quick { display:flex; gap:24px; }
.quick-stat { padding:10px 24px; border-radius:8px; background:rgba(255,255,255,.05); border:1px solid rgba(255,255,255,.08); }
.quick-stat strong { color:#e2e8f0; font-size:22px; margin-right:6px; }
.quick-stat span { color:#64748b; font-size:12px; }

/* Main */
.main { flex:1; max-width:1280px; margin:0 auto; width:100%; padding:32px 28px; }
.section-block { margin-bottom:36px; }
.section-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:18px; }
.section-title { font-size:17px; font-weight:600; color:#1e293b; display:flex; align-items:center; gap:10px; }
.title-bar { display:inline-block; width:3px; height:18px; background:linear-gradient(180deg,#667eea,#764ba2); border-radius:2px; }
.section-more { font-size:12px; color:#94a3b8; text-decoration:none; }
.section-more:hover { color:#667eea; }

/* Carousel */
.carousel { max-width:860px; margin:0 auto; }
.carousel-viewport { border-radius:16px; overflow:hidden; }
.carousel-track { display:flex; transition:transform .5s cubic-bezier(.4,0,.2,1); }
.carousel-slide { min-width:100%; height:200px; cursor:pointer; position:relative; display:flex; align-items:flex-end; }
.slide-cover { position:absolute; inset:0; }
.slide-cover img { width:100%; height:100%; object-fit:cover; }
.slide-cover-fallback { width:100%; height:100%; background:linear-gradient(135deg,#1e293b,#334155); }
.slide-overlay { position:relative; z-index:1; width:100%; padding:20px 28px; color:#fff; background:linear-gradient(to top, rgba(0,0,0,.75) 0%, transparent 60%); }
.slide-category { display:inline-block; padding:2px 8px; border-radius:4px; background:rgba(255,255,255,.15); font-size:11px; margin-bottom:6px; }
.slide-title { font-size:18px; font-weight:600; margin-bottom:4px; }
.slide-date { font-size:12px; opacity:.6; }
.carousel-dots { display:flex; justify-content:center; gap:8px; margin-top:14px; }
.dot { width:8px; height:8px; border-radius:50%; background:#cbd5e1; cursor:pointer; transition:all .2s; }
.dot.active { background:#667eea; width:24px; border-radius:4px; }

/* Service Cards */
.service-grid { display:grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap:14px; }
.service-card { padding:28px 16px; border-radius:14px; color:#fff; cursor:pointer; display:flex; align-items:center; justify-content:center; min-height:80px; transition:transform .2s, box-shadow .2s; }
.service-card:hover { transform:translateY(-4px); box-shadow:0 14px 36px rgba(0,0,0,.2); }
.sc-name { font-size:18px; font-weight:600; letter-spacing:3px; font-family:"PingFang SC","Microsoft YaHei","Helvetica Neue",sans-serif; }

/* Footer */
.footer { text-align:center; padding:20px; color:#94a3b8; font-size:12px; border-top:1px solid #e2e8f0; }

/* Sub page */
.sub-page { flex:1; max-width:1280px; margin:0 auto; width:100%; padding:16px 28px; }
.sub-nav { margin-bottom:12px; }
.sub-nav a { font-size:13px; color:#667eea; text-decoration:none; }
.sub-nav a:hover { text-decoration:underline; }

@media (max-width:768px) {
  .hero-title { font-size:26px; }
  .service-grid { grid-template-columns: repeat(3, 1fr); }
  .carousel-slide { height:160px; }
  .slide-overlay { padding:16px 20px; }
  .slide-title { font-size:15px; }
}
</style>
