<template>
  <div class="landing-page">
    <!-- 헤더 -->
    <header class="landing-header">
      <div class="header-inner">
        <div class="logo">🍽️ Food Finder</div>
        <nav class="nav-menu">
          <a href="#features">기능 소개</a>
          <a href="#how-it-works">이용 방법</a>
          <a href="#about">서비스 소개</a>
        </nav>
        <button class="login-btn" @click="showLoginModal = true">
          로그인
        </button>
      </div>
    </header>

    <!-- 히어로 섹션 -->
    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <div class="hero-badges">
          <span class="badge">🤖 AI 추천</span>
          <span class="badge">📍 위치 기반</span>
          <span class="badge hot">🔥 HOT</span>
        </div>
        <h1 class="hero-title">
          오늘 뭐 먹지?<br/>
          <span class="highlight">AI가 추천하는 맛집</span>
        </h1>
        <p class="hero-desc">
          취향을 분석해 나만의 맛집을 추천받으세요.<br/>
          Food Finder와 함께라면 매일 새로운 맛집을 발견할 수 있어요.
        </p>
        <div class="hero-actions">
          <button class="cta-btn primary" @click="showLoginModal = true">
            무료로 시작하기
          </button>
          <button class="cta-btn secondary" @click="scrollToFeatures">
            기능 둘러보기
          </button>
        </div>
      </div>
    </section>

    <!-- 기능 소개 섹션 -->
    <section id="features" class="features-section">
      <div class="section-inner">
        <h2 class="section-title">주요 기능</h2>
        <p class="section-desc">Food Finder만의 특별한 기능을 만나보세요</p>
        
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">📍</div>
            <h3>내 주변 맛집</h3>
            <p>현재 위치를 기반으로 가까운 맛집을 찾아드려요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🤖</div>
            <h3>AI 맞춤 추천</h3>
            <p>취향과 기분에 맞는 맛집을 AI가 추천해드려요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">⭐</div>
            <h3>리뷰 관리</h3>
            <p>방문한 맛집의 리뷰를 작성하고 관리하세요</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🔖</div>
            <h3>북마크</h3>
            <p>가고 싶은 맛집을 저장하고 나중에 확인하세요</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 이용 방법 섹션 -->
    <section id="how-it-works" class="howto-section">
      <div class="section-inner">
        <h2 class="section-title">이용 방법</h2>
        <p class="section-desc">3단계로 간편하게 맛집을 추천받으세요</p>
        
        <div class="steps">
          <div class="step">
            <div class="step-num">1</div>
            <h3>로그인</h3>
            <p>GitHub 계정으로 간편 로그인</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-num">2</div>
            <h3>취향 선택</h3>
            <p>오늘의 기분과 원하는 음식 선택</p>
          </div>
          <div class="step-arrow">→</div>
          <div class="step">
            <div class="step-num">3</div>
            <h3>추천 받기</h3>
            <p>AI가 분석한 맞춤 맛집 확인</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 서비스 소개 섹션 -->
    <section id="about" class="about-section">
      <div class="section-inner">
        <div class="about-content">
          <MukkebiCharacter size="medium" mood="happy" />
          <div class="about-text">
            <h2>먹깨비와 함께하는 맛집 탐험</h2>
            <p>
              Food Finder는 먹깨비둘 팀이 만든 AI 기반 맛집 추천 서비스입니다.
              매일 뭘 먹을지 고민하는 당신을 위해, AI가 취향에 맞는 맛집을 추천해드려요.
            </p>
            <button class="cta-btn primary" @click="showLoginModal = true">
              지금 시작하기
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- 푸터 -->
    <footer class="landing-footer">
      <p>© 2024 먹깨비둘 팀. All rights reserved.</p>
    </footer>

    <!-- 로그인 모달 -->
    <div v-if="showLoginModal" class="modal-overlay" @click.self="showLoginModal = false">
      <div class="login-modal">
        <button class="modal-close" @click="showLoginModal = false">×</button>
        
        <div class="modal-content">
          <MukkebiCharacter size="small" :mood="isLoading ? 'thinking' : 'happy'" />
          
          <h2>로그인</h2>
          <p class="modal-desc">{{ characterMessage }}</p>

          <button 
            class="github-login-btn"
            :disabled="isLoading"
            @click="handleGitHubLogin"
          >
            <svg v-if="!isLoading" viewBox="0 0 24 24" width="20" height="20">
              <path fill="currentColor" d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
            <span v-if="isLoading" class="spinner"></span>
            {{ isLoading ? '로그인 중...' : 'GitHub으로 계속하기' }}
          </button>

          <div v-if="errorMessage" class="error-msg">{{ errorMessage }}</div>
          
          <p class="modal-hint">GitHub 계정으로 간편하게 시작하세요</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import MukkebiCharacter from '@/components/common/MukkebiCharacter.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const showLoginModal = ref(false)
const isLoading = ref(false)
const errorMessage = ref('')

const characterMessage = computed(() => {
  if (isLoading.value) return '잠시만 기다려주세요!'
  if (errorMessage.value) return '다시 시도해주세요'
  return '맛집 추천받으러 가볼까요?'
})

const handleGitHubLogin = () => {
  isLoading.value = true
  errorMessage.value = ''
  window.location.href = '/oauth2/authorization/github'
}

const scrollToFeatures = () => {
  document.getElementById('features')?.scrollIntoView({ behavior: 'smooth' })
}

const checkAuth = async () => {
  if (route.query.error) {
    showLoginModal.value = true
    errorMessage.value = '로그인에 실패했습니다. 다시 시도해주세요.'
    return
  }
  try {
    await userStore.fetchProfile()
    if (userStore.isAuthenticated) {
      router.push('/home')
    }
  } catch (err) {
    // 미인증 상태 - 랜딩 페이지 유지
  }
}

onMounted(() => {
  checkAuth()
})
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: #fff;
}

/* 헤더 */
.landing-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255,255,255,0.95);
  backdrop-filter: blur(10px);
  z-index: 100;
  border-bottom: 1px solid #eee;
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: 800;
  color: #FF5722;
}

.nav-menu {
  display: flex;
  gap: 32px;
}

.nav-menu a {
  color: #555;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.2s;
}

.nav-menu a:hover {
  color: #FF5722;
}

.login-btn {
  background: #FF5722;
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.login-btn:hover {
  background: #E64A19;
}

/* 히어로 섹션 */
.hero-section {
  padding: 140px 24px 80px;
  position: relative;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(180deg, #FFF5F0 0%, #FFF 100%);
  z-index: -1;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.hero-badges {
  display: flex;
  gap: 12px;
  justify-content: center;
  margin-bottom: 24px;
}

.badge {
  background: #fff;
  border: 1px solid #eee;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  color: #555;
}

.badge.hot {
  background: #FF5722;
  color: #fff;
  border-color: #FF5722;
}

.hero-title {
  font-size: 48px;
  font-weight: 800;
  color: #1a1a1a;
  line-height: 1.3;
  margin: 0 0 20px 0;
}

.hero-title .highlight {
  color: #FF5722;
}

.hero-desc {
  font-size: 18px;
  color: #666;
  line-height: 1.7;
  margin: 0 0 40px 0;
}

.hero-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
}

.cta-btn {
  padding: 14px 32px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.cta-btn.primary {
  background: #FF5722;
  color: #fff;
}

.cta-btn.primary:hover {
  background: #E64A19;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255,87,34,0.3);
}

.cta-btn.secondary {
  background: #fff;
  color: #333;
  border: 1px solid #ddd;
}

.cta-btn.secondary:hover {
  border-color: #FF5722;
  color: #FF5722;
}

/* 섹션 공통 */
.section-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  text-align: center;
  margin: 0 0 12px 0;
}

.section-desc {
  font-size: 16px;
  color: #888;
  text-align: center;
  margin: 0 0 48px 0;
}

/* 기능 소개 */
.features-section {
  padding: 80px 24px;
  background: #FAFAFA;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.feature-card {
  background: #fff;
  padding: 32px 24px;
  border-radius: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: all 0.2s;
}

.feature-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
}

.feature-icon {
  font-size: 40px;
  margin-bottom: 16px;
}

.feature-card h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.feature-card p {
  font-size: 14px;
  color: #888;
  margin: 0;
  line-height: 1.5;
}

/* 이용 방법 */
.howto-section {
  padding: 80px 24px;
}

.steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
}

.step {
  text-align: center;
  flex: 1;
  max-width: 200px;
}

.step-num {
  width: 48px;
  height: 48px;
  background: #FF5722;
  color: #fff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  margin: 0 auto 16px;
}

.step h3 {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0 0 8px 0;
}

.step p {
  font-size: 14px;
  color: #888;
  margin: 0;
}

.step-arrow {
  font-size: 24px;
  color: #ddd;
}

/* 서비스 소개 */
.about-section {
  padding: 80px 24px;
  background: linear-gradient(180deg, #FFF5F0 0%, #FFF 100%);
}

.about-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 48px;
}

.about-text {
  max-width: 400px;
}

.about-text h2 {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 16px 0;
}

.about-text p {
  font-size: 15px;
  color: #666;
  line-height: 1.7;
  margin: 0 0 24px 0;
}

/* 푸터 */
.landing-footer {
  padding: 32px 24px;
  text-align: center;
  border-top: 1px solid #eee;
}

.landing-footer p {
  font-size: 13px;
  color: #999;
  margin: 0;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 24px;
}

.login-modal {
  background: #fff;
  border-radius: 20px;
  padding: 40px;
  width: 100%;
  max-width: 400px;
  position: relative;
  animation: modalIn 0.3s ease-out;
}

@keyframes modalIn {
  from {
    opacity: 0;
    transform: scale(0.95);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  width: 32px;
  height: 32px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #eee;
}

.modal-content {
  text-align: center;
}

.modal-content h2 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 16px 0 8px 0;
}

.modal-desc {
  font-size: 14px;
  color: #888;
  margin: 0 0 24px 0;
}

.github-login-btn {
  width: 100%;
  height: 52px;
  background: #24292e;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  transition: all 0.2s;
}

.github-login-btn:hover:not(:disabled) {
  background: #1a1d21;
}

.github-login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.spinner {
  width: 18px;
  height: 18px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-msg {
  margin-top: 16px;
  padding: 12px;
  background: #FEE2E2;
  color: #DC2626;
  border-radius: 8px;
  font-size: 14px;
}

.modal-hint {
  margin: 20px 0 0 0;
  font-size: 13px;
  color: #aaa;
}

/* 반응형 */
@media (max-width: 900px) {
  .nav-menu {
    display: none;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .steps {
    flex-direction: column;
  }
  
  .step-arrow {
    transform: rotate(90deg);
  }
  
  .about-content {
    flex-direction: column;
    text-align: center;
  }
}

@media (max-width: 600px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .hero-actions {
    flex-direction: column;
  }
  
  .cta-btn {
    width: 100%;
  }
}
</style>
