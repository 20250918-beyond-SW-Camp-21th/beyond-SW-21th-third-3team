<template>
  <div class="hashtag-selector">
    <!-- 식사 시간대 선택 -->
    <div class="meal-type-section">
      <h4 class="section-label">식사 시간</h4>
      <div class="meal-types">
        <el-radio-group v-model="selectedMealType" size="large">
          <el-radio-button
            v-for="meal in MEAL_TYPES"
            :key="meal.code"
            :value="meal.code"
          >
            {{ meal.label }}
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 해시태그 선택 -->
    <div class="hashtag-section">
      <div
        v-for="(tags, category) in filteredHashtagsByCategory"
        :key="category"
        class="category-group"
      >
        <h4 class="section-label">
          {{ category }}
        </h4>
        <div class="hashtag-tags">
          <el-tag
            v-for="tag in tags"
            :key="tag.code"
            :type="isSelected(tag.code) ? 'primary' : 'info'"
            :effect="isSelected(tag.code) ? 'dark' : 'plain'"
            class="hashtag-tag"
            size="large"
            @click="toggleHashtag(tag.code)"
          >
            {{ tag.label }}
          </el-tag>
        </div>
      </div>
    </div>

    <!-- 선택된 태그 표시 -->
    <div v-if="selectedTags.length > 0" class="selected-section">
      <div class="selected-header">
        <span class="selected-label">선택된 태그 ({{ selectedTags.length }})</span>
        <el-button link size="small" @click="clearSelection">
          전체 해제
        </el-button>
      </div>
      <div class="selected-tags">
        <el-tag
          v-for="code in selectedTags"
          :key="code"
          closable
          effect="dark"
          @close="toggleHashtag(code)"
        >
          {{ getTagLabel(code) }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { 
  MEAL_TYPES, 
  HASHTAGS, 
  getHashtagsByMealType,
  getHashtagByCode 
} from '@/constants/hashtags'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => [],
  },
  mealType: {
    type: String,
    default: null,
  },
})

const emit = defineEmits(['update:modelValue', 'update:mealType'])

// 현재 시간 기반으로 기본 식사 시간 설정
const getDefaultMealType = () => {
  const hour = new Date().getHours()
  if (hour >= 5 && hour < 10) return 'BREAKFAST'
  if (hour >= 10 && hour < 15) return 'LUNCH'
  return 'DINNER'
}

// 선택된 식사 시간대
const selectedMealType = ref(props.mealType || getDefaultMealType())

// 선택된 태그들
const selectedTags = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value),
})

// 필터링된 해시태그 (식사 시간대 기준)
const filteredHashtagsByCategory = computed(() => {
  const filtered = getHashtagsByMealType(selectedMealType.value)
  return filtered.reduce((acc, item) => {
    if (!acc[item.category]) {
      acc[item.category] = []
    }
    acc[item.category].push(item)
    return acc
  }, {})
})

// 태그 선택 여부
const isSelected = (code) => {
  return selectedTags.value.includes(code)
}

// 태그 토글
const toggleHashtag = (code) => {
  const newTags = [...selectedTags.value]
  const index = newTags.indexOf(code)
  
  if (index === -1) {
    newTags.push(code)
  } else {
    newTags.splice(index, 1)
  }
  
  selectedTags.value = newTags
}

// 태그 라벨 가져오기
const getTagLabel = (code) => {
  const tag = getHashtagByCode(code)
  return tag ? tag.label : code
}

// 선택 초기화
const clearSelection = () => {
  selectedTags.value = []
}

// 식사 시간대 변경 시 emit
watch(selectedMealType, (newValue) => {
  emit('update:mealType', newValue)
  
  // 새 시간대에 맞지 않는 태그 제거
  const validCodes = getHashtagsByMealType(newValue).map(t => t.code)
  const filtered = selectedTags.value.filter(code => validCodes.includes(code))
  if (filtered.length !== selectedTags.value.length) {
    selectedTags.value = filtered
  }
})
</script>

<style scoped>
.hashtag-selector {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 섹션 스타일 */
.meal-type-section,
.hashtag-section,
.selected-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.section-label {
  font-size: 13px;
  font-weight: 600;
  color: var(--color-text-tertiary, #8e8e93);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.02em;
}

/* 식사 시간대 */
.meal-types {
  display: flex;
  justify-content: center;
}

.meal-types :deep(.el-radio-button__inner) {
  padding: 12px 24px;
  border-color: var(--color-separator, #e5e5ea);
  background: var(--color-bg-primary, #ffffff);
  border-radius: 10px;
}

.meal-types :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: var(--color-primary, #007AFF);
  border-color: var(--color-primary, #007AFF);
  color: #ffffff;
  box-shadow: none;
}

.meal-types :deep(.el-radio-button__inner:hover) {
  color: var(--color-primary, #007AFF);
}

/* 해시태그 카테고리 */
.category-group {
  margin-bottom: 8px;
}

.hashtag-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.hashtag-tag {
  cursor: pointer;
  transition: all 0.2s;
  padding: 8px 16px;
  font-size: 13px;
  border-radius: 20px !important;
}

.hashtag-tag:active {
  transform: scale(0.96);
}

/* Element Plus 태그 오버라이드 - 미선택 상태 */
:deep(.el-tag.el-tag--info.el-tag--plain) {
  background: var(--color-bg-primary, #ffffff) !important;
  border-color: var(--color-separator, #e5e5ea) !important;
  color: var(--color-text-secondary, #3c3c43) !important;
  box-shadow: var(--ios-card-shadow) !important;
}

:deep(.el-tag.el-tag--info.el-tag--plain:hover) {
  background: var(--color-bg-secondary, #f2f2f7) !important;
  border-color: var(--color-primary, #007AFF) !important;
  color: var(--color-primary, #007AFF) !important;
}

/* Element Plus 태그 오버라이드 - 선택 상태 */
:deep(.el-tag.el-tag--primary.el-tag--dark) {
  background: var(--color-primary, #007AFF) !important;
  border-color: var(--color-primary, #007AFF) !important;
  color: #ffffff !important;
}

/* 선택된 태그 */
.selected-section {
  background: var(--color-bg-primary, #ffffff);
  padding: 16px;
  border-radius: 14px;
  box-shadow: var(--ios-card-shadow);
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.selected-label {
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-secondary, #3c3c43);
}

.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}
</style>
