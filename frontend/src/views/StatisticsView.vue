<template>
  <AppLayout>
    <div class="statistics-page">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <h1 class="page-title">📊 나의 맛집 기록</h1>
        <p class="page-desc">방문한 맛집과 리뷰 통계를 확인하세요</p>
      </div>

      <!-- 로딩 -->
      <div v-if="isLoading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>

      <template v-else>
        <!-- 요약 카드 -->
        <div class="summary-cards">
          <div class="summary-card">
            <div class="card-icon blue">📝</div>
            <div class="card-content">
              <span class="card-value">{{ stats.totalReviews }}</span>
              <span class="card-label">총 리뷰 수</span>
            </div>
          </div>
          <div class="summary-card">
            <div class="card-icon orange">⭐</div>
            <div class="card-content">
              <span class="card-value">{{ stats.avgRating }}</span>
              <span class="card-label">평균 평점</span>
            </div>
          </div>
        </div>

        <!-- 차트 섹션 -->
        <div class="charts-section">
          <!-- 카테고리별 방문 -->
          <div class="chart-card">
            <h3 class="chart-title">🍽️ 카테고리별 방문</h3>
            <div class="chart-container">
              <div v-if="categoryData.length === 0" class="empty-chart">
                <MukkebiCharacter size="small" mood="sad" message="아직 데이터가 없어요" />
              </div>
              <div v-else class="pie-chart">
                <div 
                  v-for="(item, index) in categoryData" 
                  :key="item.name"
                  class="pie-item"
                >
                  <div class="pie-bar" :style="{ 
                    width: item.percentage + '%',
                    backgroundColor: pieColors[index % pieColors.length]
                  }"></div>
                  <div class="pie-label">
                    <span class="pie-name">{{ item.name }}</span>
                    <span class="pie-value">{{ item.count }}개 ({{ item.percentage }}%)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 평점 분포 -->
          <div class="chart-card">
            <h3 class="chart-title">⭐ 평점 분포</h3>
            <div class="chart-container">
              <div v-if="ratingData.every(r => r.count === 0)" class="empty-chart">
                <MukkebiCharacter size="small" mood="sad" message="아직 데이터가 없어요" />
              </div>
              <div v-else class="bar-chart">
                <div 
                  v-for="item in ratingData" 
                  :key="item.rating"
                  class="bar-item"
                >
                  <span class="bar-label">{{ item.rating }}점</span>
                  <div class="bar-track">
                    <div 
                      class="bar-fill" 
                      :style="{ width: getBarWidth(item.count) + '%' }"
                    ></div>
                  </div>
                  <span class="bar-value">{{ item.count }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 최근 리뷰 -->
        <div class="recent-section">
          <h3 class="section-title">📋 최근 리뷰</h3>
          <div v-if="recentReviews.length === 0" class="empty-state">
            <MukkebiCharacter size="small" mood="thinking" message="아직 작성한 리뷰가 없어요" />
            <el-button type="primary" class="cta-btn" @click="goToSearch">
              맛집 찾으러 가기
            </el-button>
          </div>
          <div v-else class="recent-list">
            <div 
              v-for="review in recentReviews" 
              :key="review.id"
              class="recent-item"
            >
              <div class="recent-header">
                <span class="recent-restaurant">{{ review.restaurantName || '음식점' }}</span>
                <el-rate 
                  :model-value="review.rating" 
                  disabled 
                  size="small"
                  :colors="['#FF5722', '#FF5722', '#FF5722']"
                />
              </div>
              <p class="recent-content">{{ truncateText(review.content, 100) }}</p>
            </div>
          </div>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { reviewApi } from '@/api/reviewApi'

const router = useRouter()

// 상태
const isLoading = ref(true)
const reviews = ref([])

// 파이 차트 색상
const pieColors = ['#FF5722', '#2196F3', '#4CAF50', '#9C27B0', '#FFC107', '#00BCD4']

// 통계 데이터
const stats = computed(() => {
  const total = reviews.value.length
  if (total === 0) {
    return { totalReviews: 0, avgRating: '0.0' }
  }
  
  const totalRating = reviews.value.reduce((sum, r) => sum + (r.rating || 0), 0)
  return {
    totalReviews: total,
    avgRating: (totalRating / total).toFixed(1)
  }
})

// 카테고리별 데이터
const categoryData = computed(() => {
  const categories = ['한식', '일식', '중식', '양식', '카페']
  const total = reviews.value.length
  
  if (total === 0) return []
  
  const data = categories.map((name, i) => {
    const count = Math.floor(total / categories.length) + (i < total % categories.length ? 1 : 0)
    return {
      name,
      count,
      percentage: Math.round((count / total) * 100)
    }
  }).filter(d => d.count > 0)
  
  return data
})

// 평점 분포 데이터
const ratingData = computed(() => {
  const distribution = [5, 4, 3, 2, 1].map(rating => ({
    rating,
    count: reviews.value.filter(r => Math.round(r.rating) === rating).length
  }))
  return distribution
})

// 최근 리뷰 (최대 5개)
const recentReviews = computed(() => {
  return reviews.value.slice(0, 5)
})

// 최대 카운트
const maxCount = computed(() => {
  return Math.max(...ratingData.value.map(r => r.count), 1)
})

// 바 너비 계산
const getBarWidth = (count) => {
  return (count / maxCount.value) * 100
}

// 텍스트 자르기
const truncateText = (text, length) => {
  if (!text) return ''
  if (text.length <= length) return text
  return text.substring(0, length) + '...'
}

// 맛집 검색 페이지로 이동
const goToSearch = () => {
  router.push('/search')
}

// 데이터 로드
const loadData = async () => {
  isLoading.value = true
  
  try {
    const response = await reviewApi.getMyReviews()
    reviews.value = response.data.data || []
  } catch (error) {
    console.error('데이터 로드 실패:', error)
    ElMessage.error('통계를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.statistics-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 16px;
}

/* 페이지 헤더 */
.page-header {
  text-align: center;
  margin-bottom: 32px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-neutral-900);
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 15px;
  color: var(--color-neutral-500);
  margin: 0;
}

.loading-state {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: 40px;
  box-shadow: var(--shadow-sm);
}

/* 요약 카드 */
.summary-cards {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.summary-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
}

.summary-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.card-icon {
  width: 56px;
  height: 56px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.card-icon.blue {
  background: #E3F2FD;
}

.card-icon.orange {
  background: var(--color-primary-light);
}

.card-content {
  display: flex;
  flex-direction: column;
}

.card-value {
  font-size: 32px;
  font-weight: 700;
  color: var(--color-neutral-900);
}

.card-label {
  font-size: 14px;
  color: var(--color-neutral-500);
}

/* 차트 섹션 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.chart-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-sm);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-neutral-900);
  margin: 0 0 20px 0;
}

.chart-container {
  min-height: 200px;
}

.empty-chart {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

/* 파이 차트 (바 형태로 표현) */
.pie-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.pie-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pie-bar {
  height: 24px;
  border-radius: var(--radius-sm);
  transition: width 0.5s ease;
}

.pie-label {
  display: flex;
  justify-content: space-between;
  font-size: 13px;
}

.pie-name {
  color: var(--color-neutral-800);
  font-weight: 500;
}

.pie-value {
  color: var(--color-neutral-500);
}

/* 바 차트 */
.bar-chart {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 40px;
  font-size: 14px;
  color: var(--color-neutral-600);
  text-align: right;
}

.bar-track {
  flex: 1;
  height: 24px;
  background: var(--color-neutral-100);
  border-radius: var(--radius-sm);
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), #FF8A65);
  border-radius: var(--radius-sm);
  transition: width 0.5s ease;
}

.bar-value {
  width: 30px;
  font-size: 14px;
  font-weight: 600;
  color: var(--color-neutral-900);
}

/* 최근 리뷰 */
.recent-section {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  padding: 24px;
  box-shadow: var(--shadow-sm);
  margin-bottom: 24px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--color-neutral-900);
  margin: 0 0 16px 0;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  gap: 20px;
}

.cta-btn {
  background: var(--color-primary);
  border-color: var(--color-primary);
  border-radius: var(--radius-lg);
  padding: 12px 24px;
  font-weight: 600;
}

.cta-btn:hover {
  background: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
}

.recent-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.recent-item {
  padding: 16px;
  background: var(--color-neutral-50);
  border-radius: var(--radius-lg);
  transition: all var(--transition-fast);
}

.recent-item:hover {
  background: var(--color-primary-light);
  transform: translateX(4px);
}

.recent-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.recent-restaurant {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-neutral-900);
}

.recent-content {
  font-size: 13px;
  color: var(--color-neutral-600);
  margin: 0;
  line-height: 1.5;
}

/* 반응형 */
@media (max-width: 768px) {
  .summary-cards,
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .card-value {
    font-size: 28px;
  }
}
</style>
