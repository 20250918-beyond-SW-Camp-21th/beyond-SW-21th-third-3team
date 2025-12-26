import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/HomeView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/SearchView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/ProfileView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/restaurant',
    name: 'Restaurant',
    component: () => import('@/views/RestaurantView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/recommend',
    name: 'Recommend',
    component: () => import('@/views/RecommendView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('@/views/ReviewsView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/bookmarks',
    name: 'Bookmarks',
    component: () => import('@/views/BookmarksView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@/views/StatisticsView.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 네비게이션 가드 (인증 체크)
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const isAuthenticated = userStore.isAuthenticated

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/')
  } else if (to.name === 'Login' && isAuthenticated) {
    next('/home')
  } else {
    next()
  }
})

export default router
