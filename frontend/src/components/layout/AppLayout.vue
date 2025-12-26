<template>
  <div class="app-layout">
    <!-- 헤더 -->
    <AppHeader />

    <!-- 메인 컨텐츠 영역 -->
    <main class="app-content">
      <div class="content-wrapper">
        <slot></slot>
      </div>
    </main>

    <!-- 하단 영역 (필요 시 사용) -->
    <footer v-if="$slots.footer" class="app-footer">
      <div class="footer-wrapper">
        <slot name="footer"></slot>
      </div>
    </footer>
  </div>
</template>

<script setup>
import AppHeader from './AppHeader.vue'
</script>

<style scoped>
.app-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: var(--bg-gray, #f5f5f5);
}

/* 메인 컨텐츠 */
.app-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding-top: calc(var(--header-height, 64px) + 24px);
  padding-bottom: 24px;
}

.content-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-width: var(--max-width, 1200px);
  width: 100%;
  margin: 0 auto;
  padding: 0 var(--content-padding, 24px);
}

/* 하단 영역 */
.app-footer {
  flex-shrink: 0;
  background: var(--bg-white, #fff);
  border-top: 1px solid var(--border-color, #eee);
  padding: 16px 0;
}

.footer-wrapper {
  max-width: var(--max-width, 1200px);
  width: 100%;
  margin: 0 auto;
  padding: 0 var(--content-padding, 24px);
}

/* 반응형 */
@media (max-width: 768px) {
  .content-wrapper,
  .footer-wrapper {
    padding: 0 var(--content-padding-mobile, 16px);
  }
  
  .app-content {
    padding-top: calc(var(--header-height, 64px) + 16px);
    padding-bottom: 16px;
  }
}
</style>
