<template>
  <AppLayout>
    <div class="recommend-page">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-section">
        <MukkebiCharacter 
          size="large" 
          mood="thinking" 
          message="맛집을 찾고 있어요... 🔍"
        />
        <el-skeleton :rows="3" animated class="loading-skeleton" />
      </div>

      <!-- 선택 화면 -->
      <template v-else>
        <h1 class="page-title">🍽️ 메뉴 추천</h1>

        <!-- 식사 시간 선택 -->
        <section class="selection-section">
          <h2 class="section-title">🕐 언제 드실 건가요?</h2>
          <div class="meal-type-grid">
            <div 
              v-for="meal in mealTypes" 
              :key="meal.value"
              class="meal-card"
              :class="{ selected: selectedMealType === meal.value }"
              @click="selectMealType(meal.value)"
            >
              <span class="meal-icon">{{ meal.icon }}</span>
              <span class="meal-label">{{ meal.label }}</span>
            </div>
          </div>
        </section>

        <!-- 해시태그 선택 -->
        <section class="selection-section" v-if="selectedMealType">
          <h2 class="section-title">
            🍽️ 어떤 음식이 땡기세요? 
            <span class="hint">(복수 선택 가능)</span>
          </h2>
          <div v-if="isLoadingHashTags" class="hashtag-loading">
            <el-skeleton :rows="2" animated />
          </div>
          <div v-else class="hashtag-grid">
            <el-check-tag
              v-for="tag in hashTags"
              :key="tag.code"
              :checked="selectedHashTags.includes(tag.code)"
              @change="toggleHashTag(tag.code)"
              class="hashtag-chip"
            >
              {{ tag.icon }} {{ tag.label }}
            </el-check-tag>
          </div>
        </section>

        <!-- 추천 버튼 -->
        <div class="action-section">
          <el-button
            type="primary"
            size="large"
            class="recommend-btn"
            :disabled="!canSubmit"
            @click="requestRecommendation"
          >
            🔮 AI 추천 받기
          </el-button>
          <p v-if="!canSubmit" class="hint-text">
            식사 시간과 음식 종류를 선택해주세요
          </p>
        </div>
      </template>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'
import { restaurantApi } from '@/api/restaurantApi'

const route = useRoute()
const router = useRouter()

// 위치 정보 (SearchView에서 전달받음)
const latitude = ref(parseFloat(route.query.lat) || 37.5665)
const longitude = ref(parseFloat(route.query.lng) || 126.9780)
const radius = ref(parseInt(route.query.radius) || 250)

// 상태
const isLoading = ref(false)
const isLoadingHashTags = ref(false)
const selectedMealType = ref('')
const selectedHashTags = ref([])

// 식사 시간 옵션
const mealTypes = [
  { value: 'BREAKFAST', label: '아침', icon: '🌅' },
  { value: 'LUNCH', label: '점심', icon: '☀️' },
  { value: 'DINNER', label: '저녁', icon: '🌙' }
]

// 해시태그 아이콘 매핑
const hashTagIcons = {
  HANGOVER: '🍜',
  LIGHT: '🥗',
  BAKERY: '🥐',
  PORRIDGE: '🥣',
  QUICK: '⚡',
  SOLO: '🧑',
  COST_EFFECTIVE: '💰',
  HEARTY: '🍖',
  DIET: '🥬',
  SPICY: '🌶️',
  FLEX: '💎',
  DATE: '💕',
  ALCOHOL: '🍺',
  NOISY: '🎉',
  RAINY: '🌧️',
  MSG: '😋',
  YASIK: '🌙'
}

// DB에서 가져온 해시태그 목록
const hashTags = ref([])

// 식사 시간 선택 시 해시태그 API 호출
watch(selectedMealType, async (newMealType) => {
  if (!newMealType) {
    hashTags.value = []
    return
  }
  
  isLoadingHashTags.value = true
  try {
    const response = await restaurantApi.getHashTagsByMealType(newMealType)
    hashTags.value = response.data.data.map(tag => ({
      ...tag,
      icon: hashTagIcons[tag.code] || '🏷️'
    }))
  } catch (error) {
    console.error('해시태그 조회 실패:', error)
    hashTags.value = []
  } finally {
    isLoadingHashTags.value = false
  }
})

// 제출 가능 여부
const canSubmit = computed(() => {
  return selectedMealType.value && selectedHashTags.value.length > 0
})

// 식사 시간 선택
const selectMealType = (value) => {
  selectedMealType.value = value
  selectedHashTags.value = []
}

// 해시태그 토글
const toggleHashTag = (code) => {
  const index = selectedHashTags.value.indexOf(code)
  if (index === -1) {
    selectedHashTags.value.push(code)
  } else {
    selectedHashTags.value.splice(index, 1)
  }
}

// AI 추천 요청
const requestRecommendation = async () => {
  if (!canSubmit.value) return
  
  isLoading.value = true
  
  try {
    const response = await restaurantApi.getRecommendation({
      latitude: latitude.value,
      longitude: longitude.value,
      radius: radius.value,
      hashTagCodes: selectedHashTags.value,
      excludeRestaurantIds: []
    })
    
    const restaurant = response.data.data
    
    router.push({
      name: 'Restaurant',
      query: {
        id: restaurant.id,
        data: JSON.stringify(restaurant),
        lat: latitude.value,
        lng: longitude.value,
        radius: radius.value,
        hashTags: selectedHashTags.value.join(',')
      }
    })
    
  } catch (error) {
    console.error('추천 요청 실패:', error)
    ElMessage.error('추천을 받는데 실패했습니다. 다시 시도해주세요.')
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  if (!route.query.lat || !route.query.lng) {
    ElMessage.warning('먼저 위치를 선택해주세요.')
    router.push('/search')
  }
})
</script>

<style scoped>
.recommend-page {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: var(--font-size-4xl, 28px);
  font-weight: var(--font-weight-bold, 700);
  color: var(--text-primary, #333);
  margin: 0 0 32px 0;
  text-align: center;
}

/* 로딩 섹션 */
.loading-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  gap: 32px;
}

.loading-skeleton {
  width: 100%;
  max-width: 400px;
}

/* 선택 섹션 */
.selection-section {
  background: var(--bg-white, #fff);
  border-radius: var(--radius-lg, 16px);
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: var(--shadow-md, 0 2px 8px rgba(0, 0, 0, 0.06));
}

.section-title {
  font-size: var(--font-size-xl, 18px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
  margin: 0 0 20px 0;
}

.section-title .hint {
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-normal, 400);
  color: var(--text-light, #999);
}

/* 식사 시간 그리드 */
.meal-type-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.meal-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 24px 16px;
  background: var(--bg-hover, #f9f9f9);
  border: 2px solid transparent;
  border-radius: var(--radius-md, 12px);
  cursor: pointer;
  transition: all var(--transition-fast, 0.2s);
}

.meal-card:hover {
  background: var(--primary-bg, #FFF3E0);
  border-color: var(--primary-light, #FFCCBC);
}

.meal-card.selected {
  background: var(--primary-bg, #FFF3E0);
  border-color: var(--primary-color, #FF5722);
}

.meal-icon {
  font-size: 32px;
}

.meal-label {
  font-size: var(--font-size-lg, 16px);
  font-weight: var(--font-weight-semibold, 600);
  color: var(--text-primary, #333);
}

/* 해시태그 그리드 */
.hashtag-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.hashtag-loading {
  padding: 16px 0;
}

.hashtag-chip {
  padding: 10px 16px;
  font-size: var(--font-size-base, 14px);
  border-radius: var(--radius-xl, 20px);
  cursor: pointer;
  transition: all var(--transition-fast, 0.2s);
}

.hashtag-chip:hover {
  background: var(--primary-bg, #FFF3E0);
}

:deep(.el-check-tag.is-checked) {
  background: var(--primary-color, #FF5722);
  color: var(--bg-white, #fff);
}

/* 액션 섹션 */
.action-section {
  text-align: center;
  padding: 24px 0;
}

.recommend-btn {
  width: 100%;
  max-width: 400px;
  height: 56px;
  font-size: var(--font-size-xl, 18px);
  font-weight: var(--font-weight-semibold, 600);
  border-radius: var(--radius-md, 12px);
  background: var(--primary-color, #FF5722);
  border-color: var(--primary-color, #FF5722);
}

.recommend-btn:hover:not(:disabled) {
  background: var(--primary-dark, #E64A19);
  border-color: var(--primary-dark, #E64A19);
}

.recommend-btn:disabled {
  background: var(--divider-color, #ccc);
  border-color: var(--divider-color, #ccc);
}

.hint-text {
  margin-top: 12px;
  font-size: var(--font-size-base, 14px);
  color: var(--text-light, #999);
}

/* 반응형 */
@media (max-width: 600px) {
  .meal-type-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
  
  .meal-card {
    padding: 16px 12px;
  }
  
  .meal-icon {
    font-size: 24px;
  }
  
  .meal-label {
    font-size: var(--font-size-base, 14px);
  }
  
  .selection-section {
    padding: 20px 16px;
  }
  
  .page-title {
    font-size: var(--font-size-3xl, 24px);
  }
}
</style>
