<template>
  <AppLayout title="추천 결과" :show-back="true">
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-section">
      <div class="loading-content">
        <div class="loading-emoji">
          <el-icon :size="32" color="white"><Dish /></el-icon>
        </div>
        <el-icon class="loading-icon" :size="32">
          <Loading />
        </el-icon>
        <h3>AI가 맛집을 찾고 있어요</h3>
        <p>잠시만 기다려주세요</p>
      </div>
    </div>

    <!-- 메인 컨텐츠 -->
    <div v-else class="result-container">
      <!-- AI 추천 배지 -->
      <div class="ai-badge">
        <span class="badge-icon"><el-icon :size="14"><Promotion /></el-icon></span>
        <span class="badge-text">AI 추천</span>
      </div>

      <!-- 식당 이름 & 카테고리 -->
      <div class="restaurant-header">
        <h1 class="restaurant-name">{{ restaurant.name || '추천 음식점' }}</h1>
        <p class="restaurant-category">{{ restaurant.category || '맛집' }}</p>
      </div>

      <!-- 평점 & 거리 -->
      <div class="quick-info">
        <div class="info-chip rating">
          <span class="chip-icon"><el-icon><Star /></el-icon></span>
          <span class="chip-value">{{ formattedRating }}</span>
        </div>
        <div class="info-chip distance">
          <span class="chip-icon"><el-icon><Location /></el-icon></span>
          <span class="chip-value">{{ formattedDistance }}</span>
        </div>
        <div v-if="restaurant.reviewCount" class="info-chip reviews" @click="goToRestaurantReviews">
          <span class="chip-icon"><el-icon><ChatDotRound /></el-icon></span>
          <span class="chip-value">리뷰 {{ restaurant.reviewCount }}개</span>
        </div>
      </div>

      <!-- 카카오맵 영역 -->
      <div class="map-wrapper">
        <div class="map-section">
          <div v-if="isMapLoading" class="map-loading">
            <el-icon class="loading-icon" :size="24"><Loading /></el-icon>
          </div>
          <div ref="mapContainer" class="map-container"></div>
        </div>
        <div class="address-bar">
          <span class="address-icon"><el-icon><OfficeBuilding /></el-icon></span>
          <span class="address-text">{{ restaurant.roadAddress || restaurant.address || '주소 정보 없음' }}</span>
        </div>
      </div>

      <!-- AI 추천 이유 -->
      <div class="recommend-section">
        <div class="section-header">
          <span class="section-icon"><el-icon><InfoFilled /></el-icon></span>
          <span class="section-title">AI 추천 이유</span>
        </div>
        <p class="recommend-text">{{ restaurant.recommend || '선택하신 조건에 맞는 맛집이에요!' }}</p>
      </div>

      <!-- 연락처 -->
      <div v-if="restaurant.phone" class="contact-section">
        <a :href="'tel:' + restaurant.phone" class="contact-link">
          <span class="contact-icon"><el-icon><Phone /></el-icon></span>
          <span class="contact-text">{{ restaurant.phone }}</span>
        </a>
      </div>

      <!-- 리뷰 행 -->
      <div class="review-row" @click="openReviewModal">
        <span class="review-label">리뷰 보기</span>
        <div class="review-row-right">
          <span class="review-count-badge">{{ restaurant.reviewCount || 0 }}개</span>
          <span class="review-arrow">›</span>
        </div>
      </div>

      <!-- 버튼 영역 -->
      <div class="action-buttons">
        <button 
          class="btn-retry" 
          :disabled="isReLoading"
          @click="requestReRecommendation"
        >
          <span v-if="isReLoading" class="btn-loading"><el-icon><Loading /></el-icon></span>
          <span v-else class="btn-icon"><el-icon><Refresh /></el-icon></span>
          <span class="btn-text">다른 추천</span>
        </button>
        <button class="btn-review" @click="goToWriteReview">
          <span class="btn-icon"><el-icon><Edit /></el-icon></span>
          <span class="btn-text">리뷰 작성</span>
        </button>
      </div>
    </div>

    <!-- 리뷰 목록 모달 -->
    <el-dialog
      v-model="showReviewModal"
      :title="''"
      width="320px"
      :show-close="false"
      class="review-dialog"
    >
      <template #header>
        <div class="modal-header">
          <h3 class="modal-title">{{ restaurant.name }}</h3>
          <p class="modal-subtitle">리뷰 {{ restaurant.reviewCount || 0 }}개</p>
        </div>
      </template>
      
      <div class="review-modal-content">
        <!-- 로딩 -->
        <div v-if="reviewLoading" class="review-loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <!-- 리뷰 없음 -->
        <div v-else-if="reviews.length === 0" class="no-reviews">
          <div class="empty-icon">
            <el-icon :size="24"><Document /></el-icon>
          </div>
          <p>아직 리뷰가 없습니다</p>
        </div>
        
        <!-- 리뷰 목록 -->
        <div v-else class="review-list">
          <div v-for="review in reviews" :key="review.id" class="review-item">
            <div class="review-item-header">
              <div class="review-rating-badge">
                <span class="star">★</span>
                <span class="rating-num">{{ review.rating?.toFixed(1) || '0.0' }}</span>
              </div>
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
            <p class="review-text">{{ review.content }}</p>
          </div>
        </div>
      </div>
      
      <template #footer>
        <div class="modal-footer">
          <button class="modal-btn-secondary" @click="showReviewModal = false">닫기</button>
          <button class="modal-btn-primary" @click="goToWriteReview">리뷰 작성</button>
        </div>
      </template>
    </el-dialog>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading, Dish, Promotion, Star, Location, ChatDotRound, OfficeBuilding, InfoFilled, Phone, Refresh, Edit, Document } from '@element-plus/icons-vue'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import { useRecommendStore } from '@/stores/recommend'
import { recommendApi } from '@/api/recommend'
import { reviewApi } from '@/api/review'
import AppLayout from '@/components/layout/AppLayout.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const recommendStore = useRecommendStore()

// 상태
const mapContainer = ref(null)
const isLoading = ref(true)
const isReLoading = ref(false)
const isMapLoading = ref(true)

// 리뷰 모달 상태
const showReviewModal = ref(false)
const reviewLoading = ref(false)
const reviews = ref([])

// 음식점 데이터
const restaurant = ref({
  id: '',
  recommendId: '',
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
  rating: 0,
  reviewCount: 0
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
  isMapLoading.value = true
  
  try {
    await nextTick()
    
    if (!mapContainer.value) {
      console.warn('맵 컨테이너가 아직 준비되지 않았습니다.')
      return
    }
    
    const kakao = await loadKakaoMap()
    
    const options = {
      center: new kakao.maps.LatLng(restaurant.value.latitude, restaurant.value.longitude),
      level: 3,
      draggable: false,
      scrollwheel: false,
      disableDoubleClickZoom: true
    }
    
    map = new kakao.maps.Map(mapContainer.value, options)
    map.setZoomable(false)
    
    const markerPosition = new kakao.maps.LatLng(
      restaurant.value.latitude,
      restaurant.value.longitude
    )
    
    const imageSrc = 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/markerStar.png'
    const imageSize = new kakao.maps.Size(24, 35)
    const markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize)
    
    marker = new kakao.maps.Marker({
      position: markerPosition,
      map: map,
      image: markerImage
    })
    
    infowindow = new kakao.maps.InfoWindow({
      content: `<div style="padding:8px 12px;font-size:13px;font-weight:600;white-space:nowrap;">${restaurant.value.name}</div>`
    })
    infowindow.open(map, marker)
    
    isMapLoading.value = false
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
    isMapLoading.value = false
  }
}

// 음식점 데이터 로드
const loadRestaurantData = async () => {
  isLoading.value = true
  
  try {
    const stateResult = history.state?.recommendResult
    console.log('📥 history.state.recommendResult:', stateResult)
    
    if (stateResult) {
      restaurant.value = {
        id: stateResult.restaurantId || stateResult.id || 'restaurant_001',
        recommendId: stateResult.recommendId || '',
        name: stateResult.restaurantName || stateResult.name || '추천 음식점',
        category: stateResult.category || '',
        phone: stateResult.phone || '',
        address: stateResult.address || '',
        roadAddress: stateResult.roadAddress || '',
        latitude: stateResult.latitude || parseFloat(route.query.lat) || 37.5665,
        longitude: stateResult.longitude || parseFloat(route.query.lng) || 126.9780,
        distance: stateResult.distance || 0,
        placeUrl: stateResult.placeUrl || '',
        recommend: stateResult.reason || stateResult.recommend || '',
        rating: stateResult.rating || 0,
        reviewCount: stateResult.reviewCount || 0
      }
      console.log('📋 매핑된 restaurant 데이터:', restaurant.value)
    } else {
      restaurant.value = {
        id: 'restaurant_demo',
        recommendId: '',
        name: '맛있는 식당 (데모)',
        category: '한식 > 백반/가정식',
        phone: '02-1234-5678',
        address: '서울 서초구 서초동 1234-56',
        roadAddress: '서울 서초구 서초대로 123',
        latitude: parseFloat(route.query.lat) || 37.5665,
        longitude: parseFloat(route.query.lng) || 126.9780,
        distance: 150,
        placeUrl: '',
        recommend: '백엔드 API 연동 후 실제 AI 추천 결과가 표시됩니다.',
        rating: 4.5,
        reviewCount: 23
      }
    }
    
    isLoading.value = false
    await nextTick()
    await initMap()
    
  } catch (error) {
    console.error('음식점 데이터 로드 실패:', error)
    ElMessage.error('추천 정보를 불러오는데 실패했습니다.')
    isLoading.value = false
  }
}

// 재추천 요청
const requestReRecommendation = async () => {
  isReLoading.value = true
  
  try {
    // 컨텍스트가 없으면 현재 식당 정보로 재구성
    if (!recommendStore.hasContext) {
      const tags = route.query.tags ? route.query.tags.split(',') : []
      recommendStore.setContext({
        latitude: restaurant.value.latitude,
        longitude: restaurant.value.longitude,
        radius: parseInt(route.query.radius) || 250,
        hashTagCodes: tags
      })
    }
    
    // 현재 식당을 제외 목록에 추가
    if (restaurant.value.id) {
      recommendStore.addExcludedRestaurant(restaurant.value.id)
    }
    
    const requestData = recommendStore.getRecommendRequest
    console.log('📤 재추천 요청 데이터:', requestData)
    
    let response = null
    try {
      response = await recommendApi.getRecommendation(requestData)
      console.log('📥 재추천 응답:', response)
    } catch (apiError) {
      console.warn('API 호출 실패:', apiError)
      
      if (apiError.response?.status === 404 || apiError.response?.data?.message?.includes('없')) {
        ElMessage.warning('주변에 더 이상 추천할 식당이 없습니다.')
        return
      }
      throw apiError
    }
    
    if (response.restaurantId) {
      recommendStore.addExcludedRestaurant(response.restaurantId)
    }
    
    restaurant.value = {
      id: response.restaurantId || response.id || 'restaurant_new',
      recommendId: response.recommendId || '',
      name: response.restaurantName || response.name || '추천 음식점',
      category: response.category || '',
      phone: response.phone || '',
      address: response.address || '',
      roadAddress: response.roadAddress || '',
      latitude: response.latitude || restaurant.value.latitude,
      longitude: response.longitude || restaurant.value.longitude,
      distance: response.distance || 0,
      placeUrl: response.placeUrl || '',
      recommend: response.reason || response.recommend || '',
      rating: response.rating || 0,
      reviewCount: response.reviewCount || 0
    }
    
    await nextTick()
    await updateMapMarker()
    // 재추천 후 리뷰 정보 자동 로드
    await loadRestaurantReviews()
    
    ElMessage.success('새로운 맛집을 추천해드렸어요!')
    
  } catch (error) {
    console.error('재추천 요청 실패:', error)
    ElMessage.error('추천 요청에 실패했습니다.')
  } finally {
    isReLoading.value = false
  }
}

// 지도 마커 업데이트
const updateMapMarker = async () => {
  if (!map || !window.kakao) return
  
  try {
    const kakao = window.kakao
    const newPosition = new kakao.maps.LatLng(
      restaurant.value.latitude,
      restaurant.value.longitude
    )
    
    map.setCenter(newPosition)
    if (marker) marker.setPosition(newPosition)
    if (infowindow) {
      infowindow.setContent(`<div style="padding:8px 12px;font-size:13px;font-weight:600;white-space:nowrap;">${restaurant.value.name}</div>`)
    }
  } catch (error) {
    console.error('지도 마커 업데이트 실패:', error)
  }
}

// 리뷰 작성 페이지로 이동
const goToWriteReview = () => {
  console.log('📋 리뷰 작성 이동 - restaurant 데이터:', restaurant.value)
  
  if (!restaurant.value.recommendId) {
    ElMessage.warning('추천 정보가 없습니다. 추천을 다시 받아주세요.')
    return
  }
  
  showReviewModal.value = false
  router.push({
    name: 'PostReview',
    query: {
      recommendId: restaurant.value.recommendId,
      restaurantName: restaurant.value.name,
      category: restaurant.value.category
    }
  })
}

// 리뷰 모달 열기
const openReviewModal = async () => {
  showReviewModal.value = true
  await loadRestaurantReviews()
}

// 식당 리뷰 로드
const loadRestaurantReviews = async () => {
  if (!restaurant.value.id) return
  
  reviewLoading.value = true
  try {
    const response = await reviewApi.getRestaurantReviews(restaurant.value.id)
    reviews.value = response.reviews || []
    restaurant.value.reviewCount = response.totalCount || reviews.value.length
    // 평균 별점 반영
    if (response.averageRating !== undefined) {
      restaurant.value.rating = response.averageRating
    }
  } catch (error) {
    console.error('리뷰 로드 실패:', error)
    reviews.value = []
  } finally {
    reviewLoading.value = false
  }
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 음식점 리뷰 목록으로 이동 (모달로 표시)
const goToRestaurantReviews = () => {
  openReviewModal()
}

onMounted(async () => {
  await loadRestaurantData()
  // 페이지 로드 시 리뷰 정보 자동 로드 (별점, 리뷰 개수 업데이트)
  await loadRestaurantReviews()
})

onUnmounted(() => {
  if (infowindow) infowindow.close()
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
/* 로딩 */
.loading-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: var(--color-primary, #007AFF);
  min-height: 100%;
}

.loading-content {
  text-align: center;
  color: white;
}

.loading-emoji {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 16px;
}

.loading-icon {
  animation: rotate 1s linear infinite;
  color: white;
  margin-bottom: 16px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.loading-content h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0 0 8px 0;
}

.loading-content p {
  font-size: 14px;
  opacity: 0.9;
  margin: 0;
}

/* 결과 컨테이너 */
.result-container {
  padding: 20px;
  background: var(--color-bg-grouped, #f2f2f7);
  min-height: 100%;
}

/* AI 배지 */
.ai-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: var(--color-primary, #007AFF);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 12px;
}

.badge-icon {
  display: flex;
  align-items: center;
}

/* 식당 헤더 */
.restaurant-header {
  margin-bottom: 16px;
}

.restaurant-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
  line-height: 1.3;
  letter-spacing: -0.02em;
}

.restaurant-category {
  font-size: 14px;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

/* 빠른 정보 */
.quick-info {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 20px;
}

.info-chip {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: var(--color-bg-primary, #ffffff);
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 13px;
  box-shadow: var(--ios-card-shadow);
}

.info-chip.reviews {
  cursor: pointer;
  transition: all 0.2s;
}

.info-chip.reviews:active {
  transform: scale(0.98);
}

.chip-icon {
  color: var(--color-text-secondary, #3c3c43);
}

.chip-value {
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
}

/* 지도 래퍼 */
.map-wrapper {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 14px;
  overflow: hidden;
  box-shadow: var(--ios-card-shadow);
  margin-bottom: 16px;
}

.map-section {
  width: 100%;
  height: 180px;
  position: relative;
  background: var(--color-bg-secondary, #f2f2f7);
}

.map-container {
  width: 100%;
  height: 100%;
}

.map-loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.address-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: var(--color-bg-secondary, #f2f2f7);
  border-top: 0.5px solid var(--color-separator, #e5e5ea);
}

.address-icon {
  color: var(--color-text-secondary, #3c3c43);
}

.address-text {
  font-size: 13px;
  color: var(--color-text-secondary, #3c3c43);
  flex: 1;
}

/* 추천 이유 섹션 */
.recommend-section {
  background: var(--color-bg-primary, #ffffff);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 16px;
  box-shadow: var(--ios-card-shadow);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.section-icon {
  color: var(--color-primary, #007AFF);
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
}

.recommend-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

/* 연락처 섹션 */
.contact-section {
  margin-bottom: 16px;
}

.contact-link {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--color-bg-primary, #ffffff);
  padding: 14px 16px;
  border-radius: 14px;
  text-decoration: none;
  box-shadow: var(--ios-card-shadow);
  transition: all 0.2s;
}

.contact-link:active {
  transform: scale(0.98);
}

.contact-icon {
  color: var(--color-text-secondary, #3c3c43);
}

.contact-text {
  font-size: 15px;
  color: var(--color-text-primary, #1c1c1e);
  font-weight: 500;
}

/* 리뷰 행 */
.review-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--color-bg-primary, #ffffff);
  padding: 16px;
  border-radius: 14px;
  margin-bottom: 16px;
  box-shadow: var(--ios-card-shadow);
  cursor: pointer;
  transition: all 0.2s;
}

.review-row:active {
  transform: scale(0.98);
}

.review-label {
  font-size: 15px;
  font-weight: 500;
  color: var(--color-text-primary, #1c1c1e);
}

.review-row-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.review-count-badge {
  font-size: 13px;
  color: var(--color-text-secondary, #3c3c43);
  background: var(--color-bg-secondary, #f2f2f7);
  padding: 4px 10px;
  border-radius: 12px;
}

.review-arrow {
  font-size: 20px;
  color: var(--color-text-tertiary, #8e8e93);
  font-weight: 300;
}

/* 액션 버튼 */
.action-buttons {
  display: flex;
  gap: 12px;
  padding-top: 8px;
}

.btn-retry,
.btn-review {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 52px;
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-retry {
  background: var(--color-bg-primary, #ffffff);
  color: var(--color-text-primary, #1c1c1e);
  box-shadow: var(--ios-card-shadow);
}

.btn-retry:active:not(:disabled) {
  transform: scale(0.98);
}

.btn-retry:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-review {
  background: var(--color-primary, #007AFF);
  color: white;
}

.btn-review:active {
  transform: scale(0.98);
}

.btn-review:hover {
  background: var(--color-primary-hover, #0056CC);
}

.btn-icon {
  display: flex;
  align-items: center;
}

.btn-loading {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.btn-text {
  font-size: 15px;
}

/* 리뷰 모달 */
:deep(.review-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.review-dialog .el-dialog__header) {
  padding: 20px 16px 0;
  margin: 0;
}

:deep(.review-dialog .el-dialog__body) {
  padding: 16px;
}

:deep(.review-dialog .el-dialog__footer) {
  padding: 0 16px 20px;
}

.modal-header {
  text-align: center;
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary, #1c1c1e);
  margin: 0 0 4px 0;
}

.modal-subtitle {
  font-size: 13px;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
}

.review-modal-content {
  max-height: 300px;
  overflow-y: auto;
}

.review-loading {
  padding: 20px 0;
}

.no-reviews {
  text-align: center;
  padding: 40px 20px;
}

.no-reviews .empty-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
  margin: 0 auto 12px;
  color: var(--color-text-tertiary, #8e8e93);
}

.no-reviews p {
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
  font-size: 14px;
}

.review-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.review-item {
  padding: 14px;
  background: var(--color-bg-secondary, #f2f2f7);
  border-radius: 12px;
}

.review-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.review-rating-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  background: var(--color-bg-primary, #ffffff);
  padding: 4px 10px;
  border-radius: 12px;
}

.review-rating-badge .star {
  color: var(--color-warning, #ff9500);
  font-size: 12px;
}

.review-rating-badge .rating-num {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-primary, #1c1c1e);
}

.review-date {
  font-size: 12px;
  color: var(--color-text-tertiary, #8e8e93);
}

.review-text {
  font-size: 14px;
  line-height: 1.6;
  color: var(--color-text-secondary, #3c3c43);
  margin: 0;
}

.modal-footer {
  display: flex;
  gap: 10px;
}

.modal-btn-secondary,
.modal-btn-primary {
  flex: 1;
  height: 46px;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.modal-btn-secondary {
  background: var(--color-bg-secondary, #f2f2f7);
  color: var(--color-text-primary, #1c1c1e);
}

.modal-btn-secondary:hover {
  background: var(--color-bg-tertiary, #e5e5ea);
}

.modal-btn-primary {
  background: var(--color-primary, #007AFF);
  color: white;
}

.modal-btn-primary:hover {
  background: var(--color-primary-hover, #0056CC);
}
</style>
