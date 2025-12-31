<template>
  <div class="recommend-result">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading-state">
      <div class="loading-content">
        <el-icon class="loading-icon" :size="48">
          <Loading />
        </el-icon>
        <h3>AI가 맛집을 찾고 있어요...</h3>
        <p>잠시만 기다려주세요</p>
      </div>
    </div>

    <!-- 결과 없음 -->
    <div v-else-if="!restaurant" class="empty-state">
      <div class="empty-content">
        <div class="empty-icon">
          <el-icon :size="24"><Search /></el-icon>
        </div>
        <h3>추천 결과가 없습니다</h3>
        <p>조건을 변경하여 다시 시도해보세요</p>
      </div>
    </div>

    <!-- 추천 결과 -->
    <div v-else class="result-content">
      <!-- 음식점 정보 카드 -->
      <div class="restaurant-card">
        <div class="restaurant-header">
          <div class="restaurant-badge">AI 추천</div>
          <div v-if="restaurant.rating" class="restaurant-rating">
            <el-rate
              :model-value="restaurant.rating"
              disabled
              show-score
              score-template="{value}"
            />
          </div>
        </div>

        <h2 class="restaurant-name">{{ restaurant.name }}</h2>
        <p class="restaurant-category">{{ restaurant.category }}</p>

        <div class="restaurant-info">
          <div class="info-item">
            <el-icon><Location /></el-icon>
            <span>{{ restaurant.roadAddress || restaurant.address }}</span>
          </div>
          <div v-if="restaurant.phone" class="info-item">
            <el-icon><Phone /></el-icon>
            <span>{{ restaurant.phone }}</span>
          </div>
          <div class="info-item">
            <el-icon><Position /></el-icon>
            <span>{{ formatDistance(restaurant.distance) }}</span>
          </div>
        </div>

        <!-- AI 추천 이유 -->
        <div v-if="restaurant.recommend" class="recommend-reason">
          <h4>AI 추천 이유</h4>
          <p>{{ restaurant.recommend }}</p>
        </div>
      </div>

      <!-- 지도 -->
      <div class="map-section">
        <h4 class="map-title">위치</h4>
        <div ref="mapContainer" class="map-container"></div>
      </div>

      <!-- 액션 버튼 -->
      <div class="action-buttons">
        <el-button
          type="primary"
          size="large"
          class="action-btn"
          @click="handleViewDetail"
        >
          상세 보기
        </el-button>
        <el-button
          v-if="restaurant.placeUrl"
          size="large"
          class="action-btn"
          @click="handleOpenKakao"
        >
          카카오맵에서 보기
        </el-button>
        <el-button
          size="large"
          class="action-btn"
          @click="$emit('retry')"
        >
          다른 추천 받기
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Loading, Location, Phone, Position, Search } from '@element-plus/icons-vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'

const props = defineProps({
  restaurant: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['retry', 'view-detail'])

const router = useRouter()
const mapContainer = ref(null)
let map = null
let marker = null

// 거리 포맷팅
const formatDistance = (distance) => {
  if (!distance) return '거리 정보 없음'
  if (distance < 1000) {
    return `${Math.round(distance)}m`
  }
  return `${(distance / 1000).toFixed(1)}km`
}

// 지도 초기화
const initMap = async () => {
  if (!props.restaurant || !mapContainer.value) return

  try {
    const kakao = await loadKakaoMap()
    
    const { latitude, longitude } = props.restaurant
    const position = new kakao.maps.LatLng(latitude, longitude)
    
    // 지도 생성
    map = new kakao.maps.Map(mapContainer.value, {
      center: position,
      level: 3,
    })

    // 마커 생성
    marker = new kakao.maps.Marker({
      position: position,
      map: map,
    })

    // 인포윈도우
    const infowindow = new kakao.maps.InfoWindow({
      content: `<div style="padding:8px 12px;font-size:13px;font-weight:500;">${props.restaurant.name}</div>`,
    })
    infowindow.open(map, marker)

  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
  }
}

// 상세 보기
const handleViewDetail = () => {
  if (props.restaurant?.id) {
    router.push(`/restaurant/${props.restaurant.id}`)
  }
}

// 카카오맵 열기
const handleOpenKakao = () => {
  if (props.restaurant?.placeUrl) {
    window.open(props.restaurant.placeUrl, '_blank')
  }
}

// restaurant 변경 시 지도 업데이트
watch(() => props.restaurant, async (newVal) => {
  if (newVal) {
    await nextTick()
    initMap()
  }
}, { immediate: true })
</script>

<style scoped>
.recommend-result {
  width: 100%;
}

/* 로딩 상태 */
.loading-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.loading-content {
  text-align: center;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: var(--color-primary, #007AFF);
  margin-bottom: 16px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-content h3 {
  font-size: 18px;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 8px 0;
}

.loading-content p {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

/* 빈 상태 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
}

.empty-content {
  text-align: center;
}

.empty-icon {
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 14px;
  margin: 0 auto 16px;
  color: var(--color-text-tertiary, #8e8e93);
}

.empty-content h3 {
  font-size: 18px;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 8px 0;
}

.empty-content p {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

/* 결과 콘텐츠 */
.result-content {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 음식점 카드 */
.restaurant-card {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 14px;
  padding: 20px;
  box-shadow: var(--ios-card-shadow);
}

.restaurant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.restaurant-badge {
  background: var(--color-primary, #007AFF);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.restaurant-name {
  font-size: 22px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
  letter-spacing: -0.02em;
}

.restaurant-category {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0 0 16px 0;
}

.restaurant-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--color-text-secondary, #3c3c43);
}

.info-item .el-icon {
  color: var(--color-primary, #007AFF);
}

/* AI 추천 이유 */
.recommend-reason {
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
  padding: 16px;
}

.recommend-reason h4 {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 8px 0;
}

.recommend-reason p {
  font-size: 13px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
  line-height: 1.6;
}

/* 지도 섹션 */
.map-section {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 14px;
  padding: 16px;
  box-shadow: var(--ios-card-shadow);
}

.map-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 12px 0;
}

.map-container {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  width: 100%;
  height: 48px;
  font-size: 15px;
  font-weight: 500;
  border-radius: 14px;
}
</style>
