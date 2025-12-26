<template>
  <AppLayout>
    <div class="search-page">
      <!-- 페이지 헤더 -->
      <section class="page-header">
        <div class="header-content">
          <div class="header-text">
            <h1 class="page-title">📍 위치 선택</h1>
            <p class="page-subtitle">맛집을 찾을 위치를 선택해주세요</p>
          </div>
        </div>
      </section>
      
      <div class="search-content">
        <!-- 맵 영역 -->
        <div class="map-section">
          <div ref="mapContainer" class="map-container"></div>
          
          <!-- 현재 위치 버튼 -->
          <button class="current-location-btn" @click="moveToCurrentLocation">
            <el-icon><Location /></el-icon>
            현재 위치
          </button>
        </div>

        <!-- 컨트롤 패널 -->
        <div class="control-panel">
          <div class="panel-card">
            <div class="card-icon-wrapper orange">
              <span>📍</span>
            </div>
            <h3 class="panel-title">현재 위치</h3>
            <p class="location-text">{{ currentAddress || '위치를 불러오는 중...' }}</p>
          </div>

          <div class="panel-card">
            <div class="card-icon-wrapper blue">
              <span>🎯</span>
            </div>
            <h3 class="panel-title">검색 반경</h3>
            <div class="radius-display">
              <span class="radius-value">{{ radius }}</span>
              <span class="radius-unit">m</span>
            </div>
            <div class="slider-container">
              <span class="slider-label">100m</span>
              <el-slider
                v-model="radius"
                :min="100"
                :max="500"
                :step="50"
                :show-tooltip="false"
                @input="updateCircle"
              />
              <span class="slider-label">500m</span>
            </div>
          </div>

          <el-button
            type="primary"
            size="large"
            class="recommend-btn"
            :loading="isLoading"
            @click="goToRecommend"
          >
            🔮 메뉴 추천 받기
          </el-button>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { loadKakaoMap } from '@/utils/kakaoMapLoader'
import AppLayout from '@/components/layout/AppLayout.vue'
import { Location } from '@element-plus/icons-vue'

const router = useRouter()

// 상태
const mapContainer = ref(null)
const radius = ref(250)
const isLoading = ref(false)
const currentAddress = ref('')

// 카카오맵 관련
let map = null
let circle = null
let marker = null
const currentPosition = ref({
  latitude: 37.5665,
  longitude: 126.9780
})

const FIXED_ZOOM_LEVEL = 5

// 카카오맵 초기화
const initMap = async () => {
  try {
    if (!mapContainer.value) {
      console.error('맵 컨테이너가 없습니다!')
      return
    }
    
    const kakao = await loadKakaoMap()
    
    const options = {
      center: new kakao.maps.LatLng(currentPosition.value.latitude, currentPosition.value.longitude),
      level: FIXED_ZOOM_LEVEL,
      draggable: true,
      scrollwheel: true
    }
    
    map = new kakao.maps.Map(mapContainer.value, options)
    
    // 현재 위치 마커 생성
    marker = new kakao.maps.Marker({
      position: map.getCenter(),
      map: map
    })
    
    // 범위 원 생성
    circle = new kakao.maps.Circle({
      center: map.getCenter(),
      radius: radius.value,
      strokeWeight: 2,
      strokeColor: '#FF5722',
      strokeOpacity: 0.8,
      strokeStyle: 'solid',
      fillColor: '#FFCCBC',
      fillOpacity: 0.4
    })
    circle.setMap(map)
    
    // 맵 클릭 이벤트 - 위치 변경
    kakao.maps.event.addListener(map, 'click', (mouseEvent) => {
      const latlng = mouseEvent.latLng
      currentPosition.value = {
        latitude: latlng.getLat(),
        longitude: latlng.getLng()
      }
      marker.setPosition(latlng)
      circle.setPosition(latlng)
      getAddressFromCoords(latlng)
    })
    
    getCurrentLocation()
    
  } catch (error) {
    console.error('카카오맵 초기화 실패:', error)
    ElMessage.error('지도를 불러오는데 실패했습니다.')
  }
}

// 현재 위치 가져오기
const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    console.warn('Geolocation 미지원')
    return
  }
  
  navigator.geolocation.getCurrentPosition(
    (position) => {
      currentPosition.value = {
        latitude: position.coords.latitude,
        longitude: position.coords.longitude
      }
      updateMapCenter()
    },
    (error) => {
      console.error('위치 정보 실패:', error)
      ElMessage.warning('현재 위치를 가져올 수 없습니다. 지도에서 위치를 선택해주세요.')
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 0
    }
  )
}

// 맵 중심 업데이트
const updateMapCenter = () => {
  if (!map) return
  
  const kakao = window.kakao
  const newCenter = new kakao.maps.LatLng(
    currentPosition.value.latitude,
    currentPosition.value.longitude
  )
  
  map.setCenter(newCenter)
  if (marker) marker.setPosition(newCenter)
  if (circle) circle.setPosition(newCenter)
  
  getAddressFromCoords(newCenter)
}

// 좌표로 주소 가져오기
const getAddressFromCoords = (latlng) => {
  const kakao = window.kakao
  const geocoder = new kakao.maps.services.Geocoder()
  
  geocoder.coord2Address(latlng.getLng(), latlng.getLat(), (result, status) => {
    if (status === kakao.maps.services.Status.OK) {
      const address = result[0].road_address 
        ? result[0].road_address.address_name 
        : result[0].address.address_name
      currentAddress.value = address
    }
  })
}

// 원 크기 업데이트
const updateCircle = () => {
  if (!circle) return
  circle.setRadius(radius.value)
}

// 현재 위치로 이동
const moveToCurrentLocation = () => {
  getCurrentLocation()
}

// 추천 페이지로 이동
const goToRecommend = () => {
  router.push({
    name: 'Recommend',
    query: {
      lat: currentPosition.value.latitude,
      lng: currentPosition.value.longitude,
      radius: radius.value
    }
  })
}

onMounted(async () => {
  await nextTick()
  await initMap()
})

onUnmounted(() => {
  if (circle) circle.setMap(null)
  if (marker) marker.setMap(null)
})
</script>

<style scoped>
.search-page {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 페이지 헤더 */
.page-header {
  background: var(--primary-gradient, linear-gradient(135deg, #FFF3E0 0%, #FFCCBC 50%, #FFE0B2 100%));
  border-radius: var(--radius-lg, 16px);
  padding: 24px 32px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.page-title {
  font-size: var(--font-size-4xl, 28px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--text-primary, #333);
  margin: 0 0 4px 0;
}

.page-subtitle {
  font-size: var(--font-size-md, 15px);
  color: var(--text-secondary, #666);
  margin: 0;
}

.search-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;
  min-height: 500px;
}

/* 맵 영역 */
.map-section {
  position: relative;
  background: var(--bg-gray, #f5f5f5);
  border-radius: var(--radius-lg, 16px);
  overflow: hidden;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.08));
}

.map-container {
  width: 100%;
  height: 100%;
  min-height: 500px;
}

.current-location-btn {
  position: absolute;
  bottom: 20px;
  left: 20px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 18px;
  background: var(--bg-white, #fff);
  border: 1px solid var(--border-color, #ddd);
  border-radius: var(--radius-sm, 8px);
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--text-primary, #333);
  cursor: pointer;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.1));
  transition: all var(--transition-fast, 0.2s);
}

.current-location-btn:hover {
  background: var(--bg-gray, #f5f5f5);
  border-color: var(--primary-color, #FF5722);
  color: var(--primary-color, #FF5722);
  transform: translateY(-2px);
}

/* 컨트롤 패널 */
.control-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.panel-card {
  background: var(--bg-white, #fff);
  border-radius: var(--radius-lg, 16px);
  padding: 20px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
  transition: all var(--transition-fast, 0.2s);
}

.panel-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg, 0 8px 24px rgba(0, 0, 0, 0.08));
}

.card-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md, 12px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 12px;
}

.card-icon-wrapper.orange {
  background: var(--primary-bg, #FFF3E0);
}

.card-icon-wrapper.blue {
  background: var(--accent-blue-light, #E3F2FD);
}

.panel-title {
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-secondary, #666);
  margin: 0 0 8px 0;
}

.location-text {
  font-size: var(--font-size-md, 15px);
  color: var(--text-primary, #333);
  margin: 0;
  line-height: 1.5;
}

.radius-display {
  text-align: center;
  margin-bottom: 16px;
}

.radius-value {
  font-size: var(--font-size-6xl, 36px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--primary-color, #FF5722);
}

.radius-unit {
  font-size: var(--font-size-xl, 18px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--text-muted, #888);
  margin-left: 4px;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.slider-container .el-slider {
  flex: 1;
}

.slider-label {
  font-size: var(--font-size-xs, 12px);
  color: var(--text-light, #999);
  white-space: nowrap;
}

.recommend-btn {
  width: 100%;
  height: 56px;
  font-size: var(--font-size-lg, 16px);
  font-weight: var(--font-weight-semibold, 600);
  border-radius: var(--radius-md, 12px);
  background: var(--primary-color, #FF5722);
  border-color: var(--primary-color, #FF5722);
  margin-top: auto;
}

.recommend-btn:hover {
  background: var(--primary-dark, #E64A19);
  border-color: var(--primary-dark, #E64A19);
  transform: translateY(-2px);
  box-shadow: var(--shadow-primary, 0 4px 12px rgba(255, 87, 34, 0.3));
}

/* Element Plus 슬라이더 커스텀 */
:deep(.el-slider__runway) {
  background-color: #e0e0e0;
}

:deep(.el-slider__bar) {
  background-color: var(--primary-color, #FF5722);
}

:deep(.el-slider__button) {
  border-color: var(--primary-color, #FF5722);
}

/* 반응형 */
@media (max-width: 900px) {
  .search-content {
    grid-template-columns: 1fr;
  }
  
  .map-container {
    min-height: 350px;
  }
  
  .control-panel {
    flex-direction: row;
    flex-wrap: wrap;
  }
  
  .panel-card {
    flex: 1;
    min-width: 200px;
  }
  
  .recommend-btn {
    width: 100%;
    flex-basis: 100%;
  }
}

@media (max-width: 600px) {
  .page-header {
    padding: 20px;
  }
  
  .page-title {
    font-size: var(--font-size-3xl, 24px);
  }
  
  .control-panel {
    flex-direction: column;
  }
  
  .panel-card {
    min-width: auto;
  }
}
</style>
