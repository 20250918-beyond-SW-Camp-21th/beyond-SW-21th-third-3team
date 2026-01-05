<template>
  <AppLayout title="활동 통계" :show-back="true">
    <div class="statistics-view">
      <!-- 요약 카드 -->
      <div class="summary-section">
        <div class="summary-card">
          <div class="summary-header">
            <h3>
              <span class="nav-btn" @click="prevMonth">&lt;</span>
              {{ currentMonth }}
              <span class="nav-btn" @click="nextMonth" :class="{ disabled: isCurrentMonth }">&gt;</span>
            </h3>
            <span class="period" style="visibility: hidden;">기간</span>
          </div>
          <div class="summary-stats">
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyVisits }}</span>
              <span class="summary-label">방문(승인)</span>
            </div>
            <div class="summary-item">
              <span class="summary-value">{{ stats.monthlyRecommends }}</span>
              <span class="summary-label">총 추천</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 카테고리별 추천 -->
      <div class="chart-section">
        <h3 class="section-title">카테고리별 추천</h3>
        <div class="chart-card">
          <div v-if="categoryStats.length > 0" class="bar-chart">
            <div 
              v-for="(item, index) in visibleCategoryStats" 
              :key="index"
              class="bar-item"
            >
              <span class="bar-label">{{ item.category }}</span>
              <div class="bar-track">
                <div 
                  class="bar-fill" 
                  :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                ></div>
              </div>
              <span class="bar-value">{{ item.count }}회</span>
            </div>
            
            <!-- 더보기 버튼 -->
            <div v-if="categoryStats.length > 5" class="more-btn-wrapper" @click="goToCategoryStats">
              <span class="more-text">더보기 ></span>
            </div>
          </div>
          <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 요일별 활동 (주간) -->
      <div class="chart-section">
        <h3 class="section-title">요일별 활동</h3>
        <div class="chart-card">
          <div v-if="weeklyStats.length > 0" class="col-chart">
            <div 
              v-for="item in weeklyStats" 
              :key="item.label"
              class="col-item"
            >
              <span class="col-value" v-if="item.count > 0">{{ item.count }}</span>
              <div class="col-track">
                <div 
                  class="col-fill" 
                  :style="{ height: item.percentage + '%' }"
                ></div>
              </div>
              <span class="col-label">{{ item.label }}</span>
            </div>
            </div>
          </div>
          
          <!-- 자세히 보기 버튼 -->
          <div v-if="weeklyStats.length > 0" class="more-btn-wrapper" @click="goToTimeStats">
            <span class="more-text">자세히 보기 ></span>
          </div>

           <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 차트 섹션: 추천 반응 분포 -->
      <div class="chart-section">
        <h3 class="section-title">추천 반응 분포</h3>
        <div class="chart-card">
          <div v-if="reactionStats.length > 0" class="rating-chart">
            <div 
              v-for="item in reactionStats" 
              :key="item.label"
              class="rating-item"
            >
              <span class="rating-stars">{{ item.label }}</span>
              <div class="rating-track">
                <div 
                  class="rating-fill" 
                  :style="{ width: item.percentage + '%', backgroundColor: item.color }"
                ></div>
              </div>
              <span class="rating-count">{{ item.count }}</span>
            </div>
          </div>
           <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 최근 활동 -->
      <div class="recent-section">
        <h3 class="section-title">최근 활동</h3>
        <div class="recent-list">
          <div 
            v-for="item in recentVisits" 
            :key="item.id"
            class="recent-item"
          >
            <div class="recent-info">
              <span class="recent-name">{{ item.name }}</span>
              <div class="recent-sub">
                <span class="recent-category">{{ item.category }}</span>
              </div>
            </div>
            <div class="recent-meta">
              <span :class="['recent-result', getResultClass(item.result)]">{{ formatResult(item.result) }}</span>
              <span class="recent-date">{{ item.visitDate }}</span>
            </div>
          </div>
          <div v-if="recentVisits.length === 0" class="empty-list">
            최근 활동 내역이 없습니다.
          </div>
        </div>
      </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { statisticsApi } from '@/api/statistics'

// 날짜 상태
const selectedDate = ref(new Date())

// 현재 월 표시
const currentMonth = computed(() => {
  const d = selectedDate.value
  return `${d.getFullYear()}년 ${d.getMonth() + 1}월`
})

// 다음 달이 현재(미래)인지 확인
const isCurrentMonth = computed(() => {
  const now = new Date()
  const d = selectedDate.value
  return now.getFullYear() === d.getFullYear() && now.getMonth() === d.getMonth()
})

const prevMonth = () => {
  const d = new Date(selectedDate.value)
  d.setMonth(d.getMonth() - 1)
  selectedDate.value = d
  loadStatistics()
}

const nextMonth = () => {
  if (isCurrentMonth.value) return
  const d = new Date(selectedDate.value)
  d.setMonth(d.getMonth() + 1)
  selectedDate.value = d
  loadStatistics()
}

// 통계 데이터
const stats = ref({
  monthlyVisits: 0,
  monthlyRecommends: 0
})

const categoryStats = ref([])
const visibleCategoryStats = computed(() => {
  return categoryStats.value.slice(0, 5)
})

import { useRouter } from 'vue-router'

const router = useRouter()

const goToCategoryStats = () => {
  const d = selectedDate.value
  router.push({
    path: '/statistics/categories',
    query: {
      year: d.getFullYear(),
      month: d.getMonth() + 1
    }
  })
}

const goToTimeStats = () => {
  const d = selectedDate.value
  router.push({
    path: '/statistics/time',
    query: {
      year: d.getFullYear(),
      month: d.getMonth() + 1
    }
  })
}

const weeklyStats = ref([])
const reactionStats = ref([])
const recentVisits = ref([])

// 색상 팔레트 & 설정
const categoryColors = ['#007AFF', '#3291FF', '#5E9EFF', '#8AB4FF', '#A6C8FF', '#C2DAFF', '#DEEAFF']
const reactionConfig = {
  ACCEPTED: { label: '승인', color: '#007AFF' },
  REJECTED: { label: '재요청', color: '#FF3B30' },
  PENDING: { label: '보류', color: '#FFCC00' }
}

// 데이터 로드
const loadStatistics = async () => {
  try {
    const d = selectedDate.value
    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const lastDay = new Date(d.getFullYear(), d.getMonth() + 1, 0).getDate()
    
    const from = `${year}-${month}-01`
    const to = `${year}-${month}-${lastDay}`
    
    const params = { from, to }

    // 병렬로 API 호출
    // 병렬로 API 호출 (allSettled로 변경하여 하나가 실패해도 나머지는 로드되도록 함)
    const results = await Promise.allSettled([
      statisticsApi.getMyCategoryStats(params),
      statisticsApi.getMyWeeklyStats(params),
      statisticsApi.getMyReactionStats(params),
      statisticsApi.getMyRecentStats()
    ])

    const categoriesRes = results[0].status === 'fulfilled' ? results[0].value : null
    const weeklyRes = results[1].status === 'fulfilled' ? results[1].value : null
    const reactionsRes = results[2].status === 'fulfilled' ? results[2].value : null
    const recentRes = results[3].status === 'fulfilled' ? results[3].value : null

    // 에러 로그 출력
    results.forEach((result, index) => {
      if (result.status === 'rejected') {
        const apiNames = ['Category', 'Weekly', 'Reaction', 'Recent']
        console.error(`${apiNames[index]} API failed:`, result.reason)
      }
    })

    // 카테고리 통계 처리
    if (categoriesRes && categoriesRes.data) {
      const total = categoriesRes.data.reduce((sum, item) => sum + item.value, 0)
      categoryStats.value = categoriesRes.data.map((item, index) => ({
        category: item.label,
        count: item.value,
        percentage: total > 0 ? Math.round((item.value / total) * 100) : 0,
        color: categoryColors[index % categoryColors.length]
      })).sort((a, b) => b.count - a.count)
    }

    // 주간(요일별) 통계 처리
    if (weeklyRes && weeklyRes.data) {
      const maxCount = Math.max(...weeklyRes.data.map(item => item.value), 0)
      
      // 요일 순서 고정 (일~토)
      const dayOrder = ['일', '월', '화', '수', '목', '금', '토']
      
      // 데이터 매핑 (없는 요일은 0으로 채움)
      weeklyStats.value = dayOrder.map(day => {
        const item = weeklyRes.data.find(d => d.label === day)
        const count = item ? item.value : 0
        // 최대값이 0이면 퍼센트 0, 아니면 비율 계산
        const percentage = maxCount > 0 ? (count / maxCount) * 100 : 0
        
        return {
          label: day,
          count: count,
          percentage: percentage
        }
      })
    }

    // 반응 통계 처리
    if (reactionsRes && reactionsRes.data) {
      const total = reactionsRes.data.reduce((sum, item) => sum + item.value, 0)
      
      // 요약 정보 업데이트
      stats.value.monthlyRecommends = total
      const acceptedItem = reactionsRes.data.find(item => item.label === 'ACCEPTED')
      stats.value.monthlyVisits = acceptedItem ? acceptedItem.value : 0

      // 차트 데이터 (보류 제외)
      const chartReactions = ['ACCEPTED', 'REJECTED']
      reactionStats.value = chartReactions.map(label => {
        const item = reactionsRes.data.find(r => r.label === label)
        const count = item ? item.value : 0
        const config = reactionConfig[label]
        
        return {
          label: config.label,
          count: count,
          percentage: total > 0 ? Math.round((count / total) * 100) : 0,
          color: config.color
        }
      })
    }

    // 최근 방문 처리
    if (recentRes && recentRes.data) {
      recentVisits.value = recentRes.data
    }

  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

const getResultClass = (result) => {
  if (result === 'ACCEPTED') return 'text-success'
  if (result === 'REJECTED') return 'text-danger'
  return 'text-warning'
}

const formatResult = (result) => {
  if (result === 'PENDING') return '보류'
  if (result === 'ACCEPTED') return '승인'
  if (result === 'REJECTED') return '재요청'
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 요약 섹션 */
.summary-section {
  margin-bottom: 8px;
}


.summary-card {
  background: var(--color-primary, #007AFF);
  border-radius: 16px;
  padding: 20px;
  color: white;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.summary-header h3 {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.nav-btn {
  cursor: pointer;
  opacity: 0.7;
  font-size: 24px;
  user-select: none;
}

.nav-btn:hover {
  opacity: 1;
}

.nav-btn.disabled {
  opacity: 0.2;
  cursor: not-allowed;
}

.period {
  font-size: 13px;
  opacity: 0.8;
}

.summary-stats {
  display: flex;
  justify-content: space-around;
}

.summary-item {
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.summary-value {
  font-size: 32px;
  font-weight: 700;
}

.summary-label {
  font-size: 12px;
  opacity: 0.8;
}

/* 차트 섹션 */
.chart-section {
  margin-bottom: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 4px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-state, .empty-list {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
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
  width: 48px;
  font-size: 13px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bar-track {
  flex: 1;
  height: 20px;
  background: #f5f5f5;
  border-radius: 10px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 10px;
  transition: width 0.5s ease;
}

.bar-value {
  width: 40px;
  font-size: 13px;
  font-weight: 500;
  color: #333;
  text-align: right;
}

/* 세로 바 차트 (요일별) */
.col-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 180px;
  padding: 10px 0;
}

.col-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.col-track {
  width: 12px;
  height: 120px;
  background: #f8f9fa;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
}

.col-fill {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-radius: 6px;
  transition: height 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to top, #007AFF, #5AC8FA);
}

.col-label {
  font-size: 12px;
  color: #666;
}

.col-value {
  font-size: 11px;
  color: #007AFF;
  font-weight: 600;
  margin-bottom: -4px;
}

/* 평점 차트 (재사용) */
.rating-chart {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.rating-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rating-stars {
  width: 50px;
  font-size: 13px;
  color: #666;
  white-space: nowrap;
}

/* 보류 박스 스타일 */
.pending-box {
  background: #FFF3CD;
  border: 1px solid #FFE69C;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.pending-box:active {
  transform: scale(0.98);
}

.pending-content {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pending-text {
  font-weight: 600;
  color: #856404;
}

.pending-count {
  background: #856404;
  color: white;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 11px;
  font-weight: 700;
}

.pending-sub {
  font-size: 12px;
  color: #856404;
  opacity: 0.8;
}

.rating-track {
  flex: 1;
  height: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}

.rating-fill {
  height: 100%;
  border-radius: 8px;
  transition: width 0.5s ease;
}

.rating-count {
  width: 24px;
  font-size: 13px;
  color: #666;
  text-align: right;
}

/* 최근 방문 */
.recent-section {
  margin-bottom: 8px;
}

.recent-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.recent-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f5f5f5;
}

.recent-item:last-child {
  border-bottom: none;
}

.recent-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.recent-name {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.recent-category {
  font-size: 12px;
  color: #999;
}

.recent-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.recent-result {
  font-size: 13px;
  font-weight: 500;
}

.text-success { color: #007AFF; }
.text-danger { color: #FF3B30; }
.text-warning { color: #FFCC00; }

.recent-date {
  font-size: 12px;
  color: #999;
}

/* 더보기 버튼 */
.more-btn-wrapper {
  display: flex;
  justify-content: center;
  padding-top: 8px;
  cursor: pointer;
}

.more-text {
  font-size: 12px;
  color: #999;
  padding: 4px 12px;
  border-radius: 12px;
  background-color: #f5f5f5;
  transition: all 0.2s ease;
}

.more-text:hover {
  background-color: #e0e0e0;
  color: #666;
}
</style>
