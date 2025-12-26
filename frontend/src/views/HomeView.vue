<template>
  <div class="home-page">
    <!-- 헤더 -->
    <header class="home-header">
      <div class="header-inner">
        <div class="logo">🍽️ Food Finder</div>
        <nav class="nav-menu">
          <router-link to="/search">맛집 검색</router-link>
          <router-link to="/recommend">AI 추천</router-link>
          <router-link to="/reviews">내 리뷰</router-link>
        </nav>
        <div class="header-right">
          <div class="user-info" @click="goToProfile">
            <el-avatar :size="32" :src="userAvatar">
              <span>{{ userInitial }}</span>
            </el-avatar>
            <span class="user-name">{{ userName }}</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="hero-greeting">
          <MukkebiCharacter size="medium" mood="happy" />
          <div class="greeting-text">
            <h1 class="hero-title">
              안녕하세요, {{ userName }}님! 👋
            </h1>
            <p class="hero-desc">
              오늘은 어떤 맛집을 찾아볼까요?<br/>
              AI가 당신의 취향에 맞는 맛집을 추천해드려요.
            </p>
          </div>
        </div>
        <div class="hero-actions">
          <button class="cta-btn primary" @click="goToRecommend">
            🤖 AI 추천 받기
          </button>
          <button class="cta-btn secondary" @click="goToSearch">
            📍 주변 맛집 찾기
          </button>
        </div>
      </div>
    </section>

    <!-- 퀵 메뉴 섹션 -->
    <section class="quick-menu-section">
      <div class="section-inner">
        <h2 class="section-title">빠른 메뉴</h2>
        
        <div class="menu-grid">
          <div class="menu-card" @click="goToSearch">
            <div class="menu-icon orange">📍</div>
            <h3>주변 맛집</h3>
            <p>현재 위치 기반 검색</p>
          </div>
          <div class="menu-card" @click="goToRecommend">
            <div class="menu-icon blue">🤖</div>
            <h3>AI 추천</h3>
            <p>취향 맞춤 추천</p>
          </div>
          <div class="menu-card" @click="goToReviews">
            <div class="menu-icon green">📝</div>
            <h3>내 리뷰</h3>
            <p>작성한 리뷰 관리</p>
          </div>
          <div class="menu-card" @click="goToBookmarks">
            <div class="menu-icon purple">🔖</div>
            <h3>북마크</h3>
            <p>저장한 맛집</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 통계 섹션 -->
    <section class="stats-section">
      <div class="section-inner">
        <h2 class="section-title">나의 활동</h2>
        
        <div class="stats-grid">
          <div class="stat-card" @click="goToReviews">
            <span class="stat-value">{{ stats.totalReviews }}</span>
            <span class="stat-label">작성한 리뷰</span>
          </div>
          <div class="stat-card" @click="goToBookmarks">
            <span class="stat-value">{{ stats.totalBookmarks }}</span>
            <span class="stat-label">저장한 맛집</span>
          </div>
          <div class="stat-card" @click="goToStatistics">
            <span class="stat-value">{{ stats.avgRating }}</span>
            <span class="stat-label">평균 평점</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 푸터 -->
    <footer class="home-footer">
      <p>© 2024 먹깨비둘 팀. All rights reserved.</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { reviewApi } from '@/api/reviewApi'

const router = useRouter()
const userStore = useUserStore()

// 사용자 정보
const userName = computed(() => userStore.userName || '사용자')
const userAvatar = computed(() => userStore.userAvatar || '')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

// 통계
const stats = ref({
  totalReviews: 0,
  totalBookmarks: 0,
  avgRating: '0.0'
})

// 통계 로드
const loadStats = async () => {
  try {
    const response = await reviewApi.getMyReviews()
    const reviews = response.data.data || []
    
    stats.value.totalReviews = reviews.length
    
    if (reviews.length > 0) {
      const totalRating = reviews.reduce((sum, r) => sum + (r.rating || 0), 0)
      stats.value.avgRating = (totalRating / reviews.length).toFixed(1)
    }
    
    // 북마크 카운트
    const bookmarks = JSON.parse(localStorage.getItem('foodfinder_bookmarks') || '[]')
    stats.value.totalBookmarks = bookmarks.length
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

// 네비게이션
const goToProfile = () => router.push('/profile')
const goToSearch = () => router.push('/search')
const goToRecommend = () => router.push('/recommend')
const goToReviews = () => router.push('/reviews')
const goToBookmarks = () => router.push('/bookmarks')
const goToStatistics = () => router.push('/statistics')

onMounted(async () => {
  if (!userStore.user) {
    try {
      await userStore.fetchProfile()
    } catch (err) {
      console.error('프로필 로드 실패:', err)
    }
  }
  loadStats()
})
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background: #fff;
}

/* 헤더 */
.home-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  z-index: 100;
  border-bottom: 1px solid #eee;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 800;
  color: #FF5722;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-menu a {
  color: #555;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-menu a:hover,
.nav-menu a.router-link-active {
  color: #FF5722;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 20px;
  transition: background 0.2s;
}

.user-info:hover {
  background: #f5f5f5;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

/* 히어로 섹션 */
.hero-section {
  padding: 120px 24px 60px;
  position: relative;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, #FFF5F0 0%, #FFF 100%);
  z-index: -1;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.hero-greeting {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
  margin-bottom: 32px;
}

.greeting-text {
  text-align: left;
}

.hero-title {
  font-size: 36px;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1.3;
  margin: 0 0 12px 0;
}

.hero-desc {
  font-size: 16px;
  color: #666;
  line-height: 1.7;
  margin: 0;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.cta-btn {
  padding: 14px 32px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.cta-btn.primary {
  background: #FF5722;
  color: #fff;
}

.cta-btn.primary:hover {
  background: #E64A19;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,87,34,0.3);
}

.cta-btn.secondary {
  background: #fff;
  color: #333;
  border: 1px solid #ddd;
}

.cta-btn.secondary:hover {
  border-color: #FF5722;
  color: #FF5722;
}

/* 섹션 공통 */
.section-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 24px 0;
}

/* 퀵 메뉴 */
.quick-menu-section {
  padding: 60px 24px;
  background: #FAFAFA;
}

.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.menu-card {
  background: #fff;
  padding: 32px 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  cursor: pointer;
  transition: all 0.2s;
}

.menu-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.menu-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin: 0 auto 16px;
}

.menu-icon.orange { background: #FFF3E0; }
.menu-icon.blue { background: #E3F2FD; }
.menu-icon.green { background: #E8F5E9; }
.menu-icon.purple { background: #F3E5F5; }

.menu-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.menu-card p {
  font-size: 14px;
  color: #888;
  margin: 0;
}

/* 통계 섹션 */
.stats-section {
  padding: 60px 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-card {
  background: linear-gradient(135deg, #FFF3E0 0%, #FFCCBC 100%);
  padding: 32px;
  border-radius: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(255,87,34,0.15);
}

.stat-value {
  display: block;
  font-size: 36px;
  font-weight: 800;
  color: #FF5722;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

/* 푸터 */
.home-footer {
  padding: 32px 24px;
  text-align: center;
  border-top: 1px solid #eee;
}

.home-footer p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 반응형 */
@media (max-width: 900px) {
  .nav-menu {
    display: none;
  }
  
  .hero-greeting {
    flex-direction: column;
    text-align: center;
  }
  
  .greeting-text {
    text-align: center;
  }
  
  .hero-title {
    font-size: 28px;
  }
  
  .menu-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .menu-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .cta-btn {
    width: 100%;
  }
  
  .user-name {
    display: none;
  }
}
</style>
