<template>
  <AppLayout>
    <div class="reviews-page">
      <h1 class="page-title">
        {{ isWriteMode ? '✏️ 리뷰 작성' : '📝 내 리뷰' }}
      </h1>

      <div class="reviews-content">
        <!-- 리뷰 작성 폼 (음식점 ID가 있을 때) -->
        <div v-if="isWriteMode" class="form-section">
          <div class="form-card">
            <h3 class="form-title">{{ restaurantName }}</h3>
            
            <div class="form-group">
              <label class="form-label">평점</label>
              <el-rate 
                v-model="reviewForm.rating" 
                :colors="['#FF5722', '#FF5722', '#FF5722']"
                size="large"
              />
            </div>

            <div class="form-group">
              <label class="form-label">리뷰 내용</label>
              <el-input
                v-model="reviewForm.content"
                type="textarea"
                :rows="5"
                placeholder="음식점에 대한 솔직한 리뷰를 작성해주세요"
                maxlength="500"
                show-word-limit
              />
            </div>

            <el-button
              type="primary"
              size="large"
              class="submit-btn"
              :loading="isSubmitting"
              :disabled="!canSubmit"
              @click="submitReview"
            >
              {{ editingReviewId ? '리뷰 수정' : '리뷰 등록' }}
            </el-button>
          </div>
        </div>

        <!-- 리뷰 목록 -->
        <div class="list-section">
          <div class="list-header">
            <h3 class="list-title">
              {{ isWriteMode ? '이 음식점의 리뷰' : '작성한 리뷰' }}
              <span class="review-count">({{ reviews.length }}개)</span>
            </h3>
          </div>

          <!-- 로딩 -->
          <div v-if="isLoading" class="loading-state">
            <el-skeleton :rows="3" animated />
          </div>

          <!-- 빈 상태 -->
          <div v-else-if="reviews.length === 0" class="empty-state">
            <MukkebiCharacter size="medium" mood="sad" message="아직 리뷰가 없어요 😢" />
            <p class="empty-text">첫 번째 리뷰를 작성해보세요!</p>
          </div>

          <!-- 리뷰 리스트 -->
          <div v-else class="review-list">
            <div 
              v-for="review in reviews" 
              :key="review.id || review.restaurantId"
              class="review-item"
            >
              <div class="review-header">
                <div class="reviewer-info">
                  <el-avatar :size="40" class="reviewer-avatar">
                    {{ review.userName?.charAt(0) || 'U' }}
                  </el-avatar>
                  <div class="reviewer-detail">
                    <span class="reviewer-name">{{ review.userName || '사용자' }}</span>
                    <span class="review-date">{{ formatDate(review.createdAt) }}</span>
                  </div>
                </div>
                <div class="review-rating">
                  <el-rate 
                    :model-value="review.rating" 
                    disabled 
                    :colors="['#FF5722', '#FF5722', '#FF5722']"
                  />
                </div>
              </div>
              
              <p class="review-content">{{ review.content }}</p>
              
              <!-- 내 리뷰인 경우 수정/삭제 버튼 -->
              <div v-if="isMyReview(review)" class="review-actions">
                <el-button text size="small" @click="editReview(review)">
                  수정
                </el-button>
                <el-button text size="small" type="danger" @click="deleteReview(review)">
                  삭제
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import AppLayout from '@/components/layout/AppLayout.vue'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { reviewApi } from '@/api/reviewApi'

const route = useRoute()
const userStore = useUserStore()

// 상태
const isLoading = ref(false)
const isSubmitting = ref(false)
const reviews = ref([])
const editingReviewId = ref(null)

// 음식점 정보 (쿼리에서)
const restaurantId = computed(() => route.query.restaurantId)
const restaurantName = computed(() => route.query.restaurantName || '음식점')
const isWriteMode = computed(() => !!restaurantId.value)

// 리뷰 폼
const reviewForm = ref({
  rating: 5,
  content: ''
})

// 제출 가능 여부
const canSubmit = computed(() => {
  return reviewForm.value.rating > 0 && reviewForm.value.content.trim().length > 0
})

// 내 리뷰인지 확인
const isMyReview = (review) => {
  return review.userId === userStore.user?.id
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 리뷰 목록 로드
const loadReviews = async () => {
  isLoading.value = true
  
  try {
    let response
    if (restaurantId.value) {
      response = await reviewApi.getRestaurantReviews(restaurantId.value)
    } else {
      response = await reviewApi.getMyReviews()
    }
    reviews.value = response.data.data || []
  } catch (error) {
    console.error('리뷰 로드 실패:', error)
    ElMessage.error('리뷰를 불러오는데 실패했습니다.')
  } finally {
    isLoading.value = false
  }
}

// 리뷰 제출
const submitReview = async () => {
  if (!canSubmit.value) return
  
  isSubmitting.value = true
  
  try {
    if (editingReviewId.value) {
      await reviewApi.updateReview(editingReviewId.value, {
        content: reviewForm.value.content,
        rating: reviewForm.value.rating
      })
      ElMessage.success('리뷰가 수정되었습니다.')
    } else {
      await reviewApi.createReview(restaurantId.value, {
        content: reviewForm.value.content,
        rating: reviewForm.value.rating
      })
      ElMessage.success('리뷰가 등록되었습니다.')
    }
    
    reviewForm.value = { rating: 5, content: '' }
    editingReviewId.value = null
    await loadReviews()
    
  } catch (error) {
    console.error('리뷰 제출 실패:', error)
    ElMessage.error('리뷰 등록에 실패했습니다.')
  } finally {
    isSubmitting.value = false
  }
}

// 리뷰 수정
const editReview = (review) => {
  editingReviewId.value = review.id
  reviewForm.value = {
    rating: review.rating,
    content: review.content
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 리뷰 삭제
const deleteReview = async (review) => {
  try {
    await ElMessageBox.confirm(
      '정말 이 리뷰를 삭제하시겠습니까?',
      '리뷰 삭제',
      {
        confirmButtonText: '삭제',
        cancelButtonText: '취소',
        type: 'warning'
      }
    )
    
    await reviewApi.deleteReview(review.id)
    ElMessage.success('리뷰가 삭제되었습니다.')
    await loadReviews()
    
  } catch (error) {
    if (error !== 'cancel') {
      console.error('리뷰 삭제 실패:', error)
      ElMessage.error('리뷰 삭제에 실패했습니다.')
    }
  }
}

onMounted(() => {
  loadReviews()
})
</script>

<style scoped>
.reviews-page {
  max-width: 1000px;
  margin: 0 auto;
}

.page-title {
  font-size: var(--font-size-4xl, 28px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--text-primary, #333);
  margin: 0 0 24px 0;
}

.reviews-content {
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 24px;
}

/* 폼 섹션 */
.form-section {
  position: sticky;
  top: calc(var(--header-height, 64px) + 24px);
  height: fit-content;
}

.form-card {
  background: var(--bg-white, #fff);
  border-radius: var(--radius-lg, 16px);
  padding: 24px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.form-title {
  font-size: var(--font-size-xl, 18px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
  margin: 0 0 20px 0;
  padding-bottom: 16px;
  border-bottom: 1px solid var(--border-color, #eee);
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--text-secondary, #666);
  margin-bottom: 8px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: var(--font-size-lg, 16px);
  font-weight: var(--font-weight-semibold, 600);
  border-radius: var(--radius-md, 12px);
  background: var(--primary-color, #FF5722);
  border-color: var(--primary-color, #FF5722);
}

.submit-btn:hover:not(:disabled) {
  background: var(--primary-dark, #E64A19);
  border-color: var(--primary-dark, #E64A19);
}

/* 리스트 섹션 */
.list-section {
  min-height: 400px;
}

.list-header {
  margin-bottom: 16px;
}

.list-title {
  font-size: var(--font-size-xl, 18px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
  margin: 0;
}

.review-count {
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-normal, 400);
  color: var(--text-light, #999);
}

/* 로딩/빈 상태 */
.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  background: var(--bg-white, #fff);
  border-radius: var(--radius-lg, 16px);
  padding: 40px;
}

.empty-text {
  margin-top: 16px;
  font-size: var(--font-size-base, 14px);
  color: var(--text-muted, #888);
}

/* 리뷰 리스트 */
.review-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.review-item {
  background: var(--bg-white, #fff);
  border-radius: var(--radius-md, 12px);
  padding: 20px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
  transition: all var(--transition-fast, 0.2s);
}

.review-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg, 0 8px 24px rgba(0, 0, 0, 0.08));
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-avatar {
  background: var(--primary-bg, #FFF3E0);
  color: var(--primary-color, #FF5722);
  font-weight: var(--font-weight-semibold, 600);
}

.reviewer-detail {
  display: flex;
  flex-direction: column;
}

.reviewer-name {
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
}

.review-date {
  font-size: var(--font-size-xs, 12px);
  color: var(--text-light, #999);
}

.review-rating {
  flex-shrink: 0;
}

.review-content {
  font-size: var(--font-size-base, 14px);
  line-height: 1.6;
  color: var(--text-secondary, #555);
  margin: 0;
}

.review-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid var(--border-light, #f0f0f0);
}

/* 반응형 */
@media (max-width: 900px) {
  .reviews-content {
    grid-template-columns: 1fr;
  }
  
  .form-section {
    position: static;
  }
}
</style>
