<template>
  <header class="app-header">
    <div class="logo">
      <img src="@/assets/logo.png" alt="Food Finder" class="logo-icon" />
      <span class="logo-text">Food Finder</span>
    </div>
  </header>
</template>

<script setup>
</script>

<style scoped>
.app-header {
  padding: 16px 20px;
  background: var(--color-bg-primary, #ffffff);
  border-bottom: 1px solid var(--color-border, #e5e7eb);
  flex-shrink: 0;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
}

.logo-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: var(--color-text-primary, #111827);
  letter-spacing: -0.02em;
}
</style>
