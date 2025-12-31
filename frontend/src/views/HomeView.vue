<template>
  <AppLayout title="Food Finder">
    <div class="home-view">
      <!-- 환영 메시지 -->
      <section class="welcome-section">
        <h2 class="welcome-title">
          안녕하세요{{ nickname ? `, ${nickname}님` : '' }}
        </h2>
        <p class="welcome-subtitle">오늘은 어떤 맛집을 찾아볼까요?</p>
      </section>

      <!-- 주요 기능 카드 -->
      <section class="feature-section">
        <h3 class="section-title">빠른 기능</h3>
        
        <div class="feature-grid">
          <!-- AI 추천 -->
          <div class="feature-card primary" @click="goTo('/recommend')">
            <div class="feature-icon-wrapper primary">
              <el-icon :size="20"><Promotion /></el-icon>
            </div>
            <div class="feature-content">
              <h4>AI 맛집 추천</h4>
              <p>오늘 기분에 맞는 맛집을 AI가 추천해드려요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 내 리뷰 -->
          <div class="feature-card" @click="goTo('/reviews')">
            <div class="feature-icon-wrapper">
              <el-icon :size="20"><Star /></el-icon>
            </div>
            <div class="feature-content">
              <h4>내 리뷰 관리</h4>
              <p>작성한 리뷰를 확인하고 관리하세요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>

          <!-- 통계 보기 -->
          <div class="feature-card" @click="goTo('/statistics')">
            <div class="feature-icon-wrapper">
              <el-icon :size="20"><DataAnalysis /></el-icon>
            </div>
            <div class="feature-content">
              <h4>활동 통계</h4>
              <p>나의 맛집 활동을 한눈에 확인하세요</p>
            </div>
            <el-icon class="feature-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </section>

      <!-- 통계 미리보기 -->
      <section class="stats-section">
        <h3 class="section-title">나의 활동</h3>
        <div class="stats-grid">
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.reviewCount }}</span>
            <span class="stat-label">리뷰 작성</span>
          </div>
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.visitCount }}</span>
            <span class="stat-label">방문 맛집</span>
          </div>
          <div class="stat-item" @click="goTo('/statistics')">
            <span class="stat-value">{{ stats.recommendCount }}</span>
            <span class="stat-label">AI 추천</span>
          </div>
        </div>
      </section>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Promotion, Star, DataAnalysis } from '@element-plus/icons-vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { useUserStore } from '@/stores/user'
import { statisticsApi } from '@/api/statistics'

const router = useRouter()
const userStore = useUserStore()

// 닉네임
const nickname = computed(() => userStore.nickname)

// 통계 데이터
const stats = ref({
  reviewCount: 0,
  visitCount: 0,
  recommendCount: 0
})

// 페이지 이동
const goTo = (path) => {
  router.push(path)
}

// 통계 로드
const loadStats = async () => {
  try {
    const response = await statisticsApi.getHomeStats()
    if (response) {
      stats.value = response.data
    }
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

// 마운트 시 초기화
onMounted(async () => {
  // 프로필이 없으면 가져오기
  if (!userStore.profile) {
    try {
      await userStore.fetchProfile()
    } catch (error) {
      console.error('프로필 로드 실패:', error)
    }
  }
  
  // 통계 로드
  loadStats()
})
</script>

<style scoped>
.home-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: var(--color-bg-grouped, #f2f2f7);
  min-height: 100%;
}

/* 환영 섹션 */
.welcome-section {
  padding: 8px 4px;
}

.welcome-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
}

.welcome-subtitle {
  font-size: 15px;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
}

/* 기능 섹션 */
.feature-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0 0 0 4px;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

.feature-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feature-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: var(--color-bg-primary, #ffffff);
  padding: 14px 16px;
  border-radius: 14px;
  box-shadow: var(--ios-card-shadow);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.feature-card:active {
  transform: scale(0.98);
}

.feature-card:hover {
  box-shadow: var(--ios-card-shadow-hover);
}

.feature-card.primary {
  background: var(--color-primary, #007AFF);
}

.feature-card.primary:hover {
  background: var(--color-primary-hover, #0056CC);
}

.feature-card.primary .feature-content h4,
.feature-card.primary .feature-content p {
  color: #ffffff;
}

.feature-card.primary .feature-arrow {
  color: rgba(255, 255, 255, 0.6);
}

.feature-card.primary .feature-icon-wrapper.primary {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.feature-icon-wrapper {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
  color: var(--color-primary, #007AFF);
}

.feature-icon-wrapper.primary {
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
}

.feature-content {
  flex: 1;
}

.feature-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 2px 0;
}

.feature-content p {
  font-size: 13px;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
  line-height: 1.4;
}

.feature-arrow {
  color: var(--color-text-tertiary, #8e8e93);
  font-size: 18px;
}

/* 통계 섹션 */
.stats-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.stat-item {
  background: var(--color-bg-primary, #ffffff);
  padding: 18px 12px;
  border-radius: 14px;
  box-shadow: var(--ios-card-shadow);
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-item:active {
  transform: scale(0.98);
}

.stat-item:hover {
  box-shadow: var(--ios-card-shadow-hover);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-primary, #007AFF);
  letter-spacing: -0.02em;
}

.stat-label {
  font-size: 12px;
  color: var(--color-text-tertiary, #8e8e93);
  font-weight: 500;
}
</style>
