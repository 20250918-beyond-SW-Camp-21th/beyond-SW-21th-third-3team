<template>
  <div class="app-layout">
    <!-- 헤더 -->
    <TopHeader :title="title" :show-back="showBack" @back="$emit('back')">
      <template #action>
        <slot name="header-action"></slot>
      </template>
    </TopHeader>

    <!-- 메인 컨텐츠 영역 -->
    <main class="app-content" :class="{ 
      'no-bottom-nav': hideBottomNav,
      'has-footer': hasFooter 
    }">
      <slot></slot>
    </main>

    <!-- 커스텀 Footer 영역 (BottomNav 위) -->
    <div v-if="hasFooter" class="app-footer">
      <slot name="footer"></slot>
    </div>

    <!-- 하단 네비게이션 -->
    <BottomNav v-if="!hideBottomNav" />
  </div>
</template>

<script setup>
import { useSlots, computed } from 'vue'
import TopHeader from './TopHeader.vue'
import BottomNav from './BottomNav.vue'

defineProps({
  title: {
    type: String,
    default: 'Food Finder',
  },
  showBack: {
    type: Boolean,
    default: false,
  },
  hideBottomNav: {
    type: Boolean,
    default: false,
  },
})

defineEmits(['back'])

const slots = useSlots()
const hasFooter = computed(() => !!slots.footer)
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--color-bg-grouped, #f2f2f7);
  max-width: 430px;
  margin: 0 auto;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.08);
}

/* 메인 컨텐츠 */
.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: 56px;
  padding-bottom: 64px;
  overflow-y: auto;
  background-color: var(--color-bg-grouped, #f2f2f7);
}

.app-content.no-bottom-nav {
  padding-bottom: 0;
}

.app-content.has-footer {
  padding-bottom: 0;
}

/* 커스텀 Footer */
.app-footer {
  background: var(--color-bg-primary, #ffffff);
  border-top: 0.5px solid var(--color-separator, #e5e5ea);
  margin-bottom: 64px;
}
</style>
