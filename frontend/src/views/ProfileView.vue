<template>
  <AppLayout>
    <div class="profile-page">
      <!-- 로딩 -->
      <div v-if="isLoading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 프로필 카드 -->
      <div v-else class="profile-card">
        <!-- 프로필 헤더 (그라데이션 배경) -->
        <div class="profile-header">
          <div class="avatar-wrapper">
            <el-avatar :size="100" :src="userAvatar">
              <span class="avatar-text">{{ userInitial }}</span>
            </el-avatar>
          </div>
          <h1 class="user-name">{{ userName }}</h1>
          <p class="user-email">{{ userEmail }}</p>
        </div>

        <!-- 통계 섹션 -->
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-icon orange">📝</div>
            <div class="stat-content">
              <span class="stat-value">{{ stats.totalReviews }}</span>
              <span class="stat-label">작성한 리뷰</span>
            </div>
          </div>
          <div class="stat-item">
            <div class="stat-icon yellow">⭐</div>
            <div class="stat-content">
              <span class="stat-value">{{ stats.avgRating }}</span>
              <span class="stat-label">평균 평점</span>
            </div>
          </div>
        </div>

        <!-- 메뉴 섹션 -->
        <div class="menu-section">
          <div class="menu-item" @click="goToReviews">
            <div class="menu-icon blue">
              <el-icon><EditPen /></el-icon>
            </div>
            <span class="menu-text">내 리뷰 보기</span>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>
          <div class="menu-item" @click="goToStatistics">
            <div class="menu-icon green">
              <el-icon><DataAnalysis /></el-icon>
            </div>
            <span class="menu-text">통계 보기</span>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>
          <div class="menu-item" @click="goToBookmarks">
            <div class="menu-icon purple">
              <el-icon><Star /></el-icon>
            </div>
            <span class="menu-text">북마크</span>
            <el-icon class="menu-arrow"><ArrowRight /></el-icon>
          </div>
        </div>

        <!-- 로그아웃 버튼 -->
        <button class="logout-btn" @click="handleLogout">
          🚪 로그아웃
        </button>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AppLayout from '@/components/layout/AppLayout.vue'
import { EditPen, DataAnalysis, ArrowRight, Star } from '@element-plus/icons-vue'
import { reviewApi } from '@/api/reviewApi'

const router = useRouter()
const userStore = useUserStore()

// 상태
const isLoading = ref(true)
const stats = ref({
  totalReviews: 0,
  avgRating: '0.0'
})

// 사용자 정보
const userName = computed(() => userStore.user?.name || userStore.user?.login || '사용자')
const userEmail = computed(() => userStore.user?.email || '')
const userAvatar = computed(() => userStore.user?.avatar_url || '')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

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
  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

// 프로필 로드
const loadProfile = async () => {
  isLoading.value = true
  
  try {
    if (!userStore.user) {
      await userStore.fetchProfile()
    }
    await loadStats()
  } catch (error) {
    console.error('프로필 로드 실패:', error)
    ElMessage.error('프로필을 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 내 리뷰 페이지로 이동
const goToReviews = () => {
  router.push('/reviews')
}

// 통계 페이지로 이동
const goToStatistics = () => {
  router.push('/statistics')
}

// 북마크 페이지로 이동
const goToBookmarks = () => {
  router.push('/bookmarks')
}

// 로그아웃
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm(
      '정말 로그아웃 하시겠습니까?',
      '로그아웃',
      {
        confirmButtonText: '로그아웃',
        cancelButtonText: '취소',
        type: 'warning'
      }
    )
    
    await userStore.logout()
    // logout() 내부에서 window.location.href = '/' 처리됨
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('로그아웃 실패:', error)
    }
  }
}

onMounted(() => {
  loadProfile()
})
</script>

<style scoped>
.profile-page {
  display: flex;
  justify-content: center;
  padding: 24px 16px;
  min-height: calc(100vh - 64px);
}

.loading-state {
  width: 100%;
  max-width: 480px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: 40px;
  box-shadow: var(--shadow-md);
}

.profile-card {
  width: 100%;
  max-width: 480px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-md);
}

/* 프로필 헤더 */
.profile-header {
  background: linear-gradient(135deg, var(--color-primary-light) 0%, #FFCCBC 50%, #FFE0B2 100%);
  padding: 40px 24px;
  text-align: center;
}

.avatar-wrapper {
  margin-bottom: 16px;
}

.avatar-wrapper :deep(.el-avatar) {
  border: 4px solid var(--color-white);
  box-shadow: var(--shadow-md);
}

.avatar-text {
  font-size: 36px;
  font-weight: 600;
  color: var(--color-primary);
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-neutral-900);
  margin: 0 0 4px 0;
}

.user-email {
  font-size: 14px;
  color: var(--color-neutral-600);
  margin: 0;
}

/* 통계 섹션 */
.stats-section {
  display: flex;
  justify-content: center;
  gap: 32px;
  padding: 24px;
  border-bottom: 1px solid var(--color-neutral-200);
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-icon.orange {
  background: var(--color-primary-light);
}

.stat-icon.yellow {
  background: #FFF8E1;
}

.stat-content {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-neutral-900);
}

.stat-label {
  font-size: 12px;
  color: var(--color-neutral-500);
}

/* 메뉴 섹션 */
.menu-section {
  padding: 16px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: var(--radius-lg);
  cursor: pointer;
  transition: all var(--transition-fast);
}

.menu-item:hover {
  background: var(--color-neutral-100);
  transform: translateX(4px);
}

.menu-icon {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.menu-icon.blue {
  background: #E3F2FD;
  color: #2196F3;
}

.menu-icon.green {
  background: #E8F5E9;
  color: #4CAF50;
}

.menu-icon.purple {
  background: #F3E5F5;
  color: #9C27B0;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  font-weight: 500;
  color: var(--color-neutral-800);
}

.menu-arrow {
  color: var(--color-neutral-400);
  transition: transform var(--transition-fast);
}

.menu-item:hover .menu-arrow {
  transform: translateX(4px);
  color: var(--color-primary);
}

/* 로그아웃 버튼 */
.logout-btn {
  width: calc(100% - 32px);
  margin: 8px 16px 24px;
  height: 52px;
  background: var(--color-white);
  color: #DC2626;
  border: 1px solid #FEE2E2;
  border-radius: var(--radius-lg);
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.logout-btn:hover {
  background: #FEE2E2;
  border-color: #FECACA;
}

/* 반응형 */
@media (max-width: 600px) {
  .profile-page {
    padding: 16px;
  }
  
  .profile-header {
    padding: 32px 20px;
  }
  
  .stats-section {
    gap: 24px;
    padding: 20px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .stat-value {
    font-size: 20px;
  }
}
</style>
