<template>
  <header class="app-header">
    <div class="header-content">
      <!-- 로고 -->
      <router-link to="/home" class="logo">
        <span class="logo-emoji">🍽️</span>
        <span class="logo-text">Food Finder</span>
      </router-link>

      <!-- 데스크톱 네비게이션 메뉴 -->
      <nav class="nav-menu" v-if="isAuthenticated">
        <router-link 
          v-for="item in navItems" 
          :key="item.path"
          :to="item.path" 
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <component :is="item.icon" class="nav-icon" />
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </nav>

      <!-- 사용자 영역 -->
      <div class="user-area" v-if="isAuthenticated">
        <el-dropdown trigger="click" @command="handleCommand">
          <div class="user-info">
            <el-avatar :size="36" :src="userAvatar">
              <span>{{ userInitial }}</span>
            </el-avatar>
            <span class="user-name">{{ userName }}</span>
            <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>
                프로필
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>
                로그아웃
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>

      <!-- 모바일 메뉴 버튼 -->
      <button 
        v-if="isAuthenticated" 
        class="mobile-menu-btn"
        @click="toggleMobileMenu"
      >
        <el-icon v-if="!isMobileMenuOpen"><Menu /></el-icon>
        <el-icon v-else><Close /></el-icon>
      </button>
    </div>

    <!-- 모바일 네비게이션 -->
    <transition name="slide-down">
      <nav v-if="isMobileMenuOpen && isAuthenticated" class="mobile-nav">
        <router-link 
          v-for="item in navItems" 
          :key="item.path"
          :to="item.path" 
          class="mobile-nav-item"
          :class="{ active: isActive(item.path) }"
          @click="closeMobileMenu"
        >
          <component :is="item.icon" class="nav-icon" />
          <span>{{ item.label }}</span>
        </router-link>
        <div class="mobile-nav-divider"></div>
        <router-link to="/profile" class="mobile-nav-item" @click="closeMobileMenu">
          <el-icon><User /></el-icon>
          <span>프로필</span>
        </router-link>
        <button class="mobile-nav-item logout" @click="handleLogout">
          <el-icon><SwitchButton /></el-icon>
          <span>로그아웃</span>
        </button>
      </nav>
    </transition>
  </header>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  HomeFilled, 
  Search, 
  EditPen, 
  DataAnalysis, 
  User, 
  SwitchButton,
  ArrowDown,
  Menu,
  Close
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 모바일 메뉴 상태
const isMobileMenuOpen = ref(false)

// 네비게이션 아이템
const navItems = [
  { path: '/home', label: '홈', icon: HomeFilled },
  { path: '/search', label: '메뉴 추천', icon: Search },
  { path: '/reviews', label: '내 리뷰', icon: EditPen },
  { path: '/statistics', label: '통계', icon: DataAnalysis }
]

// 인증 상태
const isAuthenticated = computed(() => userStore.isAuthenticated)

// 사용자 정보
const userName = computed(() => userStore.user?.name || userStore.user?.login || '사용자')
const userAvatar = computed(() => userStore.user?.avatar_url || '')
const userInitial = computed(() => userName.value.charAt(0).toUpperCase())

// 현재 페이지 확인
const isActive = (path) => {
  if (path === '/home') {
    return route.path === '/home'
  }
  return route.path.startsWith(path)
}

// 모바일 메뉴 토글
const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value
}

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false
}

// 드롭다운 명령 처리
const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'logout') {
    handleLogout()
  }
}

// 로그아웃
const handleLogout = async () => {
  closeMobileMenu()
  await userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--header-height, 64px);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color, #eee);
  z-index: var(--z-sticky, 100);
}

.header-content {
  max-width: var(--max-width, 1200px);
  height: 100%;
  margin: 0 auto;
  padding: 0 var(--content-padding, 24px);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 로고 */
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-emoji {
  font-size: 24px;
}

.logo-text {
  font-size: var(--font-size-2xl, 20px);
  font-weight: var(--font-weight-extrabold, 800);
  color: var(--primary-color, #FF5722);
}

/* 데스크톱 네비게이션 */
.nav-menu {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: var(--radius-sm, 8px);
  text-decoration: none;
  color: var(--text-secondary, #666);
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-medium, 500);
  transition: all var(--transition-fast, 0.2s);
}

.nav-item:hover {
  background: var(--bg-gray, #f5f5f5);
  color: var(--text-primary, #333);
}

.nav-item.active {
  background: var(--primary-bg, #FFF3E0);
  color: var(--primary-color, #FF5722);
}

.nav-icon {
  width: 18px;
  height: 18px;
}

/* 사용자 영역 */
.user-area {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px 4px 4px;
  border-radius: var(--radius-sm, 8px);
  transition: background var(--transition-fast, 0.2s);
}

.user-info:hover {
  background: var(--bg-gray, #f5f5f5);
}

.user-name {
  font-size: var(--font-size-base, 14px);
  font-weight: var(--font-weight-medium, 500);
  color: var(--text-primary, #333);
}

.dropdown-arrow {
  font-size: 12px;
  color: var(--text-muted, #888);
}

/* 모바일 메뉴 버튼 */
.mobile-menu-btn {
  display: none;
  width: 40px;
  height: 40px;
  border: none;
  background: transparent;
  border-radius: var(--radius-sm, 8px);
  cursor: pointer;
  color: var(--text-secondary, #666);
  font-size: 20px;
  transition: all var(--transition-fast, 0.2s);
}

.mobile-menu-btn:hover {
  background: var(--bg-gray, #f5f5f5);
  color: var(--primary-color, #FF5722);
}

/* 모바일 네비게이션 */
.mobile-nav {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: var(--bg-white, #fff);
  border-bottom: 1px solid var(--border-color, #eee);
  padding: 8px 16px 16px;
  box-shadow: var(--shadow-lg, 0 8px 24px rgba(0, 0, 0, 0.08));
}

.mobile-nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 14px 16px;
  border: none;
  background: transparent;
  border-radius: var(--radius-md, 12px);
  text-decoration: none;
  color: var(--text-secondary, #666);
  font-size: var(--font-size-md, 15px);
  font-weight: var(--font-weight-medium, 500);
  cursor: pointer;
  transition: all var(--transition-fast, 0.2s);
  text-align: left;
}

.mobile-nav-item:hover {
  background: var(--bg-gray, #f5f5f5);
  color: var(--text-primary, #333);
}

.mobile-nav-item.active {
  background: var(--primary-bg, #FFF3E0);
  color: var(--primary-color, #FF5722);
}

.mobile-nav-item.logout {
  color: var(--error-color, #DC2626);
}

.mobile-nav-item.logout:hover {
  background: var(--error-bg, #FEE2E2);
}

.mobile-nav-divider {
  height: 1px;
  background: var(--border-color, #eee);
  margin: 8px 0;
}

/* 슬라이드 다운 애니메이션 */
.slide-down-enter-active,
.slide-down-leave-active {
  transition: all var(--transition-normal, 0.3s);
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 반응형 */
@media (max-width: 900px) {
  .nav-menu {
    display: none;
  }
  
  .user-area {
    display: none;
  }
  
  .mobile-menu-btn {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

@media (max-width: 600px) {
  .header-content {
    padding: 0 var(--content-padding-mobile, 16px);
  }
  
  .logo-text {
    font-size: var(--font-size-xl, 18px);
  }
}
</style>
