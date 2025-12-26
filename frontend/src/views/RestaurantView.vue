<template>
  <AppLayout>
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-section">
      <MukkebiCharacter size="medium" mood="thinking" message="정보를 불러오는 중..." />
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 메인 컨텐츠 -->
    <div v-else class="restaurant-page">
      <div class="restaurant-content">
        <!-- 맵 영역 -->
        <div class="map-section">
          <div ref="mapContainer" class="map-container"></div>
        </div>

        <!-- 정보 패널 -->
        <div class="info-panel">
          <!-- 기본 정보 카드 -->
          <div class="info-card">
            <div class="card-header">
              <h1 class="restaurant-name">{{ restaurant.name }}</h1>
              <div class="rating-badge">
                <el-icon><Star /></el-icon>
                <span>{{ formattedRating }}</span>
              </div>
            </div>

            <div class="info-list">
              <div class="info-item">
                <el-icon><Location /></el-icon>
                <span>{{ restaurant.category || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><Phone /></el-icon>
                <span>{{ restaurant.phone || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><MapLocation /></el-icon>
                <span>{{ restaurant.roadAddress || restaurant.address || '-' }}</span>
              </div>
              <div class="info-item">
                <el-icon><Odometer /></el-icon>
                <span>{{ formattedDistance }}</span>
              </div>
            </div>

            <a 
              v-if="restaurant.placeUrl" 
              :href="restaurant.placeUrl" 
              target="_blank" 
              class="place-link"
            >
              카카오맵에서 보기 →
            </a>
          </div>

          <!-- AI 추천 이유 카드 -->
          <div class="recommend-card">
            <div class="recommend-header">
              <span class="recommend-icon">💡</span>
              <h3 class="recommend-title">AI 추천 이유</h3>
            </div>
            <p class="recommend-content">
              {{ restaurant.recommend || '맛있는 음식점입니다!' }}
            </p>
          </div>

          <!-- 버튼 그룹 -->
          <div class="button-group">
            <el-button 
              size="large" 
              class="btn-secondary"
              :loading="isLoadingAnother"
              @click="requestAnotherRecommend"
            >
              🔄 다른 추천
            </el-button>
            <el-button 
              type="primary" 
              size="large" 
              class="btn-primary"
              @click="goToWriteReview"
            >
              ✏️ 리뷰 작성
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import AppLayout from '@/components/layout/AppLayout.vue'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { restaurantApi } from '@/api/restaurantApi'
import { Star, Location, Phone, MapLocation, Odometer } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

// 상태
const mapContainer = ref(null)
const isLoading = ref(true)
const isLoadingAnother = ref(false)

// 위치 정보
const latitude = ref(parseFloat(route.query.lat) || 37.5665)
const longitude = ref(parseFloat(route.query.lng) || 126.9780)
const radius = ref(parseInt(route.query.radius) || 250)
const hashTags = ref(route.query.hashTags?.split(',') || [])

// 제외할 음식점 ID 목록
const excludeIds = ref([])

// 음식점 데이터
const restaurant = ref({
  id: '',
  name: '',
  category: '',
  phone: '',
  address: '',
  roadAddress: '',
  latitude: 37.5665,
  longitude: 126.9780,
  distance: 0,
  placeUrl: '',
  recommend: '',
  rating: 0
})

// 카카오맵 관련
let map = null
let marker = null
let infowindow = null

// Computed
const formattedRating = computed(() => {
  const rating = restaurant.value.rating || 0
  return rating.toFixed(1)
})

const formattedDistance = computed(() => {
  const distance = restaurant.value.distance || 0
  if (distance >= 1000) {
    return `${(distance / 1000).toFixed(1)}km`
  }
  return `${Math.round(distance)}m`
})

// 카카오맵 초기화
const initMap = async () => {
  try {
    if (!mapContainer.value) return
    
    const kakao = await loadKakaoMap()
    
    const options = {
      center: new kakao.maps.LatLng(restaurant.value.latitude, restaurant.value.longitude),
      level: 3,
      draggable: true,
      scrollwheel: true
    }
    
    map = new kakao.maps.Map(mapContainer.value, options)
    
    // 음식점 위치 마커 생성
    const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png'
    const imageSize = new kakao.maps.Size(24, 35)
    const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
    
    marker = new kakao.maps.Marker({
      position: new kakao.maps.LatLng(restaurant.value.latitude, restaurant.value.longitude),
      map: map,
      image: markerImage
    })
    
    // 인포윈도우
    infowindow = new kakao.maps.InfoWindow({
      content: `<div style="padding:8px 12px;font-size:13px;font-weight:600;">${restaurant.value.name}</div>`
    })
    infowindow.open(map, marker)
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
  }
}

// 맵 위치 업데이트
const updateMapPosition = () => {
  if (!map || !marker) return
  
  const kakao = window.kakao
  const newPosition = new kakao.maps.LatLng(
    restaurant.value.latitude,
    restaurant.value.longitude
  )
  
  map.setCenter(newPosition)
  marker.setPosition(newPosition)
  
  // 인포윈도우 닫고 새 위치에서 다시 열기
  if (infowindow) {
    infowindow.close()
    infowindow.setContent(`<div style="padding:8px 12px;font-size:13px;font-weight:600;">${restaurant.value.name}</div>`)
    infowindow.open(map, marker)
  }
}

// 음식점 데이터 로드
const loadRestaurantData = async () => {
  isLoading.value = true
  
  try {
    if (route.query.data) {
      restaurant.value = JSON.parse(route.query.data)
      excludeIds.value.push(restaurant.value.id)
    }
    
    isLoading.value = false
    await nextTick()
    await initMap()
    
  } catch (error) {
    console.error('음식점 데이터 로드 실패:', error)
    ElMessage.error('음식점 정보를 불러오는데 실패했습니다.')
    isLoading.value = false
  }
}

// 다른 추천 요청
const requestAnotherRecommend = async () => {
  isLoadingAnother.value = true
  
  try {
    // excludeIds를 숫자 배열로 변환
    const excludeIdsAsNumbers = excludeIds.value.map(id => parseInt(id, 10))
    
    const response = await restaurantApi.getRecommendation({
      latitude: latitude.value,
      longitude: longitude.value,
      radius: radius.value,
      hashTagCodes: hashTags.value,
      excludeRestaurantIds: excludeIdsAsNumbers
    })
    
    const newRestaurant = response.data.data
    restaurant.value = newRestaurant
    excludeIds.value.push(newRestaurant.id)
    
    updateMapPosition()
    ElMessage.success('새로운 추천을 받았습니다!')
    
  } catch (error) {
    console.error('추천 요청 실패:', error)
    ElMessage.error('더 이상 추천할 음식점이 없습니다.')
  } finally {
    isLoadingAnother.value = false
  }
}

// 리뷰 작성 페이지로 이동
const goToWriteReview = () => {
  router.push({
    name: 'Reviews',
    query: {
      restaurantId: restaurant.value.id,
      restaurantName: restaurant.value.name
    }
  })
}

onMounted(() => {
  loadRestaurantData()
})

onUnmounted(() => {
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
.restaurant-page {
  height: calc(100vh - var(--header-height, 64px) - 48px);
}

/* 로딩 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 24px;
}

/* 컨텐츠 레이아웃 */
.restaurant-content {
  display: grid;
  grid-template-columns: 1fr 400px;
  gap: 24px;
  height: 100%;
}

/* 맵 영역 */
.map-section {
  background: var(--bg-gray, #f5f5f5);
  border-radius: var(--radius-lg, 16px);
  overflow: hidden;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.08));
}

.map-container {
  width: 100%;
  height: 100%;
  min-height: 400px;
}

/* 정보 패널 */
.info-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 정보 카드 */
.info-card {
  background: var(--bg-white, #fff);
  border-radius: var(--radius-lg, 16px);
  padding: 24px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.restaurant-name {
  font-size: var(--font-size-3xl, 24px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--text-primary, #333);
  margin: 0;
  flex: 1;
  padding-right: 16px;
}

.rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  background: var(--primary-bg, #FFF3E0);
  border-radius: var(--radius-xl, 20px);
  color: var(--primary-color, #FF5722);
  font-weight: var(--font-weight-semibold, 600);
  font-size: var(--font-size-base, 14px);
}

.info-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  font-size: var(--font-size-base, 14px);
  color: var(--text-secondary, #666);
}

.info-item .el-icon {
  color: var(--text-light, #999);
  margin-top: 2px;
  flex-shrink: 0;
}

.place-link {
  display: inline-block;
  margin-top: 16px;
  font-size: var(--font-size-base, 14px);
  color: var(--primary-color, #FF5722);
  text-decoration: none;
  transition: all var(--transition-fast, 0.2s);
}

.place-link:hover {
  text-decoration: underline;
}

/* 추천 이유 카드 */
.recommend-card {
  background: var(--primary-gradient, linear-gradient(135deg, #FFF3E0 0%, #FFCCBC 100%));
  border-radius: var(--radius-lg, 16px);
  padding: 20px;
}

.recommend-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.recommend-icon {
  font-size: 20px;
}

.recommend-title {
  font-size: var(--font-size-lg, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
  margin: 0;
}

.recommend-content {
  font-size: var(--font-size-base, 14px);
  line-height: 1.6;
  color: var(--text-secondary, #555);
  margin: 0;
}

/* 버튼 그룹 */
.button-group {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.btn-secondary,
.btn-primary {
  flex: 1;
  height: 48px;
  font-size: var(--font-size-md, 15px);
  font-weight: var(--font-weight-semibold, 600);
  border-radius: var(--radius-md, 12px);
}

.btn-secondary {
  background: var(--bg-white, #fff);
  border-color: var(--border-color, #ddd);
  color: var(--text-primary, #333);
}

.btn-secondary:hover {
  background: var(--bg-gray, #f5f5f5);
  border-color: var(--divider-color, #ccc);
}

.btn-primary {
  background: var(--primary-color, #FF5722);
  border-color: var(--primary-color, #FF5722);
}

.btn-primary:hover {
  background: var(--primary-dark, #E64A19);
  border-color: var(--primary-dark, #E64A19);
}

/* 반응형 */
@media (max-width: 900px) {
  .restaurant-page {
    height: auto;
  }
  
  .restaurant-content {
    grid-template-columns: 1fr;
    grid-template-rows: 300px auto;
  }
  
  .map-section {
    min-height: 300px;
  }
}
</style>
