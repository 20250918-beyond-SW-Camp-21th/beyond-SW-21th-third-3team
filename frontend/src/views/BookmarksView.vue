<template>
  <AppLayout>
    <div class="bookmarks-page">
      <!-- 페이지 헤더 -->
      <div class="page-header">
        <h1 class="page-title">🔖 북마크</h1>
        <p class="page-desc">저장한 맛집을 확인하세요</p>
      </div>

      <!-- 로딩 -->
      <div v-if="isLoading" class="loading-state">
        <el-skeleton :rows="3" animated />
      </div>

      <!-- 빈 상태 -->
      <div v-else-if="bookmarks.length === 0" class="empty-state">
        <MukkebiCharacter size="medium" mood="thinking" message="아직 저장한 맛집이 없어요" />
        <p class="empty-text">마음에 드는 맛집을 북마크해보세요!</p>
        <el-button type="primary" class="cta-btn" @click="goToSearch">
          맛집 찾으러 가기
        </el-button>
      </div>

      <!-- 북마크 목록 -->
      <template v-else>
        <div class="bookmarks-count">
          총 <strong>{{ bookmarks.length }}</strong>개의 맛집
        </div>
        
        <div class="bookmarks-grid">
          <div 
            v-for="bookmark in bookmarks" 
            :key="bookmark.id"
            class="bookmark-card"
            @click="goToRestaurant(bookmark)"
          >
            <!-- 카드 상단 컬러 바 -->
            <div class="card-color-bar" :style="{ background: getCategoryColor(bookmark.category) }"></div>
            
            <div class="card-content">
              <!-- 음식점 정보 -->
              <div class="restaurant-info">
                <h3 class="restaurant-name">{{ bookmark.name }}</h3>
                <span class="restaurant-category">{{ bookmark.category || '음식점' }}</span>
              </div>
              
              <!-- 주소 -->
              <p class="restaurant-address">
                <el-icon><Location /></el-icon>
                {{ bookmark.address || '주소 정보 없음' }}
              </p>
              
              <!-- 평점 (있는 경우) -->
              <div v-if="bookmark.rating" class="restaurant-rating">
                <el-rate 
                  :model-value="bookmark.rating" 
                  disabled 
                  size="small"
                  :colors="['#FF5722', '#FF5722', '#FF5722']"
                />
                <span class="rating-value">{{ bookmark.rating.toFixed(1) }}</span>
              </div>
              
              <!-- 저장 날짜 -->
              <div class="bookmark-date">
                {{ formatDate(bookmark.savedAt) }} 저장
              </div>
            </div>
            
            <!-- 삭제 버튼 -->
            <button 
              class="delete-btn" 
              @click.stop="removeBookmark(bookmark.id)"
              title="북마크 삭제"
            >
              <el-icon><Delete /></el-icon>
            </button>
          </div>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { Location, Delete } from '@element-plus/icons-vue'

const router = useRouter()

// 상태
const isLoading = ref(true)
const bookmarks = ref([])

// 카테고리별 색상
const categoryColors = {
  '한식': '#FF5722',
  '일식': '#2196F3',
  '중식': '#F44336',
  '양식': '#4CAF50',
  '카페': '#9C27B0',
  '분식': '#FF9800',
  '치킨': '#FFC107',
  '피자': '#E91E63',
  '패스트푸드': '#00BCD4',
  'default': '#607D8B'
}

// 카테고리 색상 가져오기
const getCategoryColor = (category) => {
  return categoryColors[category] || categoryColors.default
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')}`
}

// 북마크 로드
const loadBookmarks = () => {
  isLoading.value = true
  
  try {
    const saved = localStorage.getItem('foodfinder_bookmarks')
    bookmarks.value = saved ? JSON.parse(saved) : []
  } catch (error) {
    console.error('북마크 로드 실패:', error)
    bookmarks.value = []
  } finally {
    isLoading.value = false
  }
}

// 북마크 삭제
const removeBookmark = (id) => {
  ElMessageBox.confirm(
    '이 맛집을 북마크에서 삭제하시겠습니까?',
    '북마크 삭제',
    {
      confirmButtonText: '삭제',
      cancelButtonText: '취소',
      type: 'warning'
    }
  ).then(() => {
    bookmarks.value = bookmarks.value.filter(b => b.id !== id)
    localStorage.setItem('foodfinder_bookmarks', JSON.stringify(bookmarks.value))
    ElMessage.success('북마크가 삭제되었습니다.')
  }).catch(() => {
    // 취소
  })
}

// 맛집 검색 페이지로 이동
const goToSearch = () => {
  router.push('/search')
}

// 음식점 상세 페이지로 이동
const goToRestaurant = (bookmark) => {
  router.push({
    path: '/restaurant',
    query: { id: bookmark.id }
  })
}

onMounted(() => {
  loadBookmarks()
})
</script>

<style scoped>
.bookmarks-page {
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

/* 빈 상태 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 20px;
  background: var(--color-white);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-sm);
  text-align: center;
}

.empty-text {
  font-size: 15px;
  color: var(--color-neutral-500);
  margin: 16px 0 24px;
}

.cta-btn {
  background: var(--color-primary);
  border-color: var(--color-primary);
  border-radius: var(--radius-lg);
  padding: 12px 32px;
  font-weight: 600;
  font-size: 15px;
}

.cta-btn:hover {
  background: var(--color-primary-dark);
  border-color: var(--color-primary-dark);
}

/* 북마크 카운트 */
.bookmarks-count {
  font-size: 14px;
  color: var(--color-neutral-600);
  margin-bottom: 16px;
}

.bookmarks-count strong {
  color: var(--color-primary);
}

/* 북마크 그리드 */
.bookmarks-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.bookmark-card {
  background: var(--color-white);
  border-radius: var(--radius-xl);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
  transition: all var(--transition-fast);
  position: relative;
}

.bookmark-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.card-color-bar {
  height: 4px;
}

.card-content {
  padding: 20px;
}

/* 음식점 정보 */
.restaurant-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.restaurant-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--color-neutral-900);
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.restaurant-category {
  font-size: 12px;
  color: var(--color-neutral-500);
  background: var(--color-neutral-100);
  padding: 4px 8px;
  border-radius: var(--radius-sm);
  flex-shrink: 0;
}

.restaurant-address {
  font-size: 13px;
  color: var(--color-neutral-600);
  margin: 0 0 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.restaurant-address .el-icon {
  color: var(--color-neutral-400);
  flex-shrink: 0;
}

/* 평점 */
.restaurant-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.rating-value {
  font-size: 14px;
  font-weight: 600;
  color: var(--color-primary);
}

/* 저장 날짜 */
.bookmark-date {
  font-size: 12px;
  color: var(--color-neutral-400);
}

/* 삭제 버튼 */
.delete-btn {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: var(--color-white);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-neutral-400);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-fast);
  opacity: 0;
}

.bookmark-card:hover .delete-btn {
  opacity: 1;
}

.delete-btn:hover {
  background: #FEE2E2;
  color: #DC2626;
}

/* 반응형 */
@media (max-width: 768px) {
  .bookmarks-grid {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .delete-btn {
    opacity: 1;
  }
}
</style>
