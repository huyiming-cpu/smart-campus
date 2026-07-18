<template>
  <div class="portal">
    <!-- 顶部导航 -->
    <header class="header">
      <div class="header-inner">
        <div class="logo-area">
          <span class="logo-icon">🏫</span>
          <span class="logo-text">智慧校园信息门户</span>
        </div>
        <nav class="top-nav">
          <a href="#" class="nav-item active">首页</a>
          <a href="#" @click.prevent="router.push('/teacher/announcement')" class="nav-item">通知公告</a>
          <a href="#" @click.prevent="router.push('/teacher/news')" class="nav-item">学校新闻</a>
        </nav>
        <div class="user-area">
          <span class="user-name">{{ userStore.userInfo?.name || '老师' }}</span>
          <span class="user-dept">{{ userStore.userInfo?.department||'' }}</span>
          <a href="#" @click.prevent="logout" class="logout-link">退出</a>
        </div>
      </div>
    </header>

    <template v-if="route.path === '/teacher'">
      <!-- 欢迎区 -->
      <div class="main-wrap">
        <div class="main-inner">
          <div class="welcome-hero teacher-hero">
            <div class="hero-content">
              <h1>{{ userStore.userInfo?.name || '老师' }}，欢迎回来</h1>
              <p class="hero-date">{{ today }}</p>
            </div>
          </div>

          <!-- 信息发布区：两栏布局 -->
          <div class="info-row">
            <div class="info-panel">
              <div class="panel-head">
                <h3 class="panel-title">📢 通知公告</h3>
                <a href="#" @click.prevent="router.push('/teacher/announcement')" class="panel-more">更多 →</a>
              </div>
              <ul class="news-list" v-if="announcements.length">
                <li v-for="item in announcements.slice(0,5)" :key="item.id" @click="router.push('/teacher/announcement')" class="news-item">
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
                <a href="#" @click.prevent="router.push('/teacher/news')" class="panel-more">更多 →</a>
              </div>
              <ul class="news-list" v-if="news.length">
                <li v-for="item in news.slice(0,5)" :key="item.id" @click="router.push('/teacher/news')" class="news-item">
                  <span class="item-tag">{{ item.category||'综合' }}</span>
                  <span class="item-title">{{ item.title }}</span>
                  <span class="item-date">{{ item.publishTime?.slice(5,10) }}</span>
                </li>
              </ul>
              <div v-else class="empty-list">暂无新闻</div>
            </div>
          </div>

          <!-- 常用服务 -->
          <div class="service-section">
            <div class="section-head"><h3>常用服务</h3></div>
            <div class="service-grid">
              <div v-for="item in services" :key="item.path" class="service-item" @click="router.push(item.path)">
                <span class="service-icon">{{ item.icon }}</span>
                <span class="service-label">{{ item.label }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <footer class="footer">
        <p>© 2026 智慧校园服务平台 · 计算机学院</p>
      </footer>
    </template>

    <!-- 子页面 -->
    <div v-else class="sub-page">
      <div class="sub-nav"><a href="#" @click.prevent="router.push('/teacher')" class="back-link">← 返回首页</a></div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'

const route = useRoute(); const router = useRouter(); const userStore = useUserStore()
const today = computed(() => {
  const d = new Date()
  const weekdays = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
  return `${d.getFullYear()}年${d.getMonth()+1}月${d.getDate()}日 ${weekdays[d.getDay()]}`
})

const announcements = ref([])
const news = ref([])
const loadInfo = async () => {
  try { const r = await request.get('/notice/announcements'); announcements.value = r.data || [] } catch {}
  try { const r = await request.get('/notice/news'); news.value = r.data || [] } catch {}
}

// 辅导员判断（后续改为后端接口）
const isCounselor = computed(() => {
  const id = userStore.userInfo?.identityNumber
  return id === '2023112618' // 赵美婷-辅导员
})

const services = computed(() => {
  const list = [
    { icon:'📖', label:'教学工作台', path:'/teacher/workbench' },
    { icon:'🗓️', label:'工作计划',   path:'/teacher/work-plans' },
    { icon:'🔬', label:'科研管理',   path:'/teacher/research' },
    { icon:'🎓', label:'毕业设计',   path:'/teacher/graduation' },
    { icon:'💳', label:'一卡通',     path:'/teacher/card' },
    { icon:'📞', label:'通讯录',     path:'/teacher/directory' },
    { icon:'📄', label:'待办公文',   path:'/teacher/documents' },
    { icon:'👤', label:'个人信息',   path:'/teacher/profile' },
  ]
  if (isCounselor.value) {
    list.push({ icon:'🏆', label:'奖助贷管理', path:'/teacher/awards' })
  }
  list.push({ icon:'🚀', label:'快速入口',   path:'/teacher/quick' })
  return list
})

const logout = () => { userStore.logout(); router.push('/login') }
onMounted(loadInfo)
</script>

<style scoped>
* { margin:0; padding:0; box-sizing:border-box; }
.portal { min-height:100vh; display:flex; flex-direction:column; background:#f5f6f8; color:#333; font-family:"Microsoft YaHei","PingFang SC",sans-serif; }

.header { background:#fff; border-bottom:1px solid #e8e8e8; position:sticky; top:0; z-index:100; box-shadow:0 1px 3px rgba(0,0,0,.04); }
.header-inner { max-width:1200px; margin:0 auto; display:flex; align-items:center; height:56px; padding:0 24px; gap:32px; }
.logo-area { display:flex; align-items:center; gap:8px; flex-shrink:0; }
.logo-icon { font-size:24px; }
.logo-text { font-size:17px; font-weight:700; color:#2c7a47; letter-spacing:1px; }
.top-nav { display:flex; gap:4px; }
.top-nav .nav-item { padding:6px 14px; border-radius:4px; font-size:14px; color:#555; text-decoration:none; transition:all .15s; }
.top-nav .nav-item:hover, .top-nav .nav-item.active { color:#2c7a47; background:#edf7f0; }
.user-area { margin-left:auto; display:flex; align-items:center; gap:12px; font-size:13px; color:#888; }
.user-name { color:#333; font-weight:500; }
.logout-link { color:#999; text-decoration:none; font-size:12px; }
.logout-link:hover { color:#e64242; }

.main-wrap { flex:1; }
.main-inner { max-width:1200px; margin:0 auto; padding:24px; }

.welcome-hero { background:linear-gradient(135deg,#2c7a47 0%,#3da05d 50%,#1a5c30 100%); border-radius:8px; padding:28px 32px; margin-bottom:20px; color:#fff; }
.welcome-hero.teacher-hero { background:linear-gradient(135deg,#2c7a47 0%,#3da05d 50%,#1a5c30 100%); }
.hero-content h1 { font-size:22px; font-weight:500; margin-bottom:6px; }
.hero-date { font-size:14px; opacity:.85; }

.info-row { display:flex; gap:20px; margin-bottom:20px; }
.info-panel { flex:1; background:#fff; border-radius:8px; overflow:hidden; box-shadow:0 1px 3px rgba(0,0,0,.04); }
.panel-head { display:flex; justify-content:space-between; align-items:center; padding:14px 20px; border-bottom:1px solid #f0f0f0; }
.panel-title { font-size:15px; font-weight:600; color:#222; }
.panel-more { font-size:12px; color:#2c7a47; text-decoration:none; }
.news-list { list-style:none; }
.news-item { display:flex; align-items:center; gap:10px; padding:11px 20px; border-bottom:1px solid #f7f7f7; cursor:pointer; transition:background .15s; }
.news-item:hover { background:#f9fafb; }
.news-item:last-child { border-bottom:none; }
.item-dot { width:6px; height:6px; border-radius:50%; background:#67c23a; flex-shrink:0; }
.item-dot.urgent { background:#f56c6c; }
.item-dot.top { background:#e64242; width:8px; height:8px; }
.item-tag { font-size:11px; padding:1px 6px; border-radius:2px; background:#ecf5ff; color:#409eff; flex-shrink:0; }
.item-title { flex:1; font-size:13px; color:#444; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.item-date { font-size:11px; color:#bbb; flex-shrink:0; }
.empty-list { padding:32px 20px; text-align:center; color:#ccc; font-size:13px; }

.service-section { background:#fff; border-radius:8px; padding:20px 24px; box-shadow:0 1px 3px rgba(0,0,0,.04); }
.section-head h3 { font-size:15px; font-weight:600; color:#222; margin-bottom:16px; }
.service-grid { display:flex; gap:24px; flex-wrap:wrap; }
.service-item { display:flex; flex-direction:column; align-items:center; gap:6px; padding:14px 18px; border-radius:6px; cursor:pointer; transition:all .15s; min-width:72px; }
.service-item:hover { background:#f2f9f4; }
.service-icon { font-size:26px; }
.service-label { font-size:12px; color:#666; }

.footer { text-align:center; padding:16px; color:#bbb; font-size:12px; border-top:1px solid #eee; background:#fff; margin-top:auto; }
.sub-page { flex:1; max-width:1200px; margin:0 auto; width:100%; padding:16px 24px; }
</style>
