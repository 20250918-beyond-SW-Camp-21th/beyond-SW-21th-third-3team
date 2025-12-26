<template>
  <div class="mukkebi-container" :class="[`size-${size}`, `mood-${mood}`]">
    <div class="mukkebi">
      <!-- 귀 -->
      <div class="ears">
        <div class="ear left">
          <div class="ear-inner"></div>
        </div>
        <div class="ear right">
          <div class="ear-inner"></div>
        </div>
      </div>
      
      <!-- 얼굴 -->
      <div class="face">
        <!-- 앞머리 -->
        <div class="bangs">
          <div class="bang"></div>
          <div class="bang"></div>
          <div class="bang"></div>
        </div>
        
        <!-- 눈 -->
        <div class="eyes">
          <div class="eye left">
            <div class="pupil"></div>
            <div class="sparkle"></div>
          </div>
          <div class="eye right">
            <div class="pupil"></div>
            <div class="sparkle"></div>
          </div>
        </div>
        
        <!-- 볼터치 -->
        <div class="cheeks">
          <div class="cheek left"></div>
          <div class="cheek right"></div>
        </div>
        
        <!-- 코 -->
        <div class="nose"></div>
        
        <!-- 입 -->
        <div class="mouth"></div>
      </div>
      
      <!-- 몸통 -->
      <div class="body">
        <!-- 음식 들고 있는 손 -->
        <div class="hands">
          <div class="hand left"></div>
          <div class="food-item">🍙</div>
          <div class="hand right"></div>
        </div>
      </div>
    </div>
    
    <!-- 말풍선 -->
    <div v-if="message" class="speech-bubble">
      <p>{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
defineProps({
  size: {
    type: String,
    default: 'medium',
    validator: (value) => ['small', 'medium', 'large'].includes(value)
  },
  mood: {
    type: String,
    default: 'happy',
    validator: (value) => ['happy', 'thinking', 'excited', 'eating'].includes(value)
  },
  message: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.mukkebi-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

/* 사이즈 */
.size-small .mukkebi { transform: scale(0.6); }
.size-medium .mukkebi { transform: scale(0.85); }
.size-large .mukkebi { transform: scale(1.1); }

.mukkebi {
  position: relative;
  width: 140px;
  height: 180px;
}

/* 귀 */
.ears {
  position: absolute;
  top: -50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 30px;
}

.ear {
  width: 28px;
  height: 70px;
  background: linear-gradient(135deg, #FFF5E6 0%, #FFE4C4 100%);
  border-radius: 50% 50% 40% 40%;
  position: relative;
  box-shadow: inset -2px 0 4px rgba(0,0,0,0.05);
}

.ear.left { transform: rotate(-15deg); }
.ear.right { transform: rotate(15deg); }

.ear-inner {
  position: absolute;
  top: 15px;
  left: 50%;
  transform: translateX(-50%);
  width: 14px;
  height: 40px;
  background: linear-gradient(180deg, #FFB6A3 0%, #FFC4B5 100%);
  border-radius: 50%;
}

/* 얼굴 */
.face {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 110px;
  height: 100px;
  background: linear-gradient(180deg, #FFF8F0 0%, #FFE8D6 100%);
  border-radius: 50% 50% 45% 45%;
  box-shadow: 
    0 4px 12px rgba(0,0,0,0.08),
    inset 0 -5px 15px rgba(255,200,150,0.2);
}

/* 앞머리 */
.bangs {
  position: absolute;
  top: -5px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 2px;
}

.bang {
  width: 20px;
  height: 30px;
  background: linear-gradient(180deg, #F5DEB3 0%, #DEB887 100%);
  border-radius: 0 0 50% 50%;
}

.bang:nth-child(1) { transform: rotate(-10deg); }
.bang:nth-child(3) { transform: rotate(10deg); }

/* 눈 */
.eyes {
  position: absolute;
  top: 35px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 25px;
}

.eye {
  width: 22px;
  height: 26px;
  background: #2C1810;
  border-radius: 50%;
  position: relative;
  animation: blink 4s infinite;
}

.pupil {
  position: absolute;
  top: 8px;
  left: 6px;
  width: 10px;
  height: 12px;
  background: #000;
  border-radius: 50%;
}

.sparkle {
  position: absolute;
  top: 6px;
  left: 12px;
  width: 6px;
  height: 6px;
  background: #fff;
  border-radius: 50%;
}

@keyframes blink {
  0%, 96%, 100% { transform: scaleY(1); }
  98% { transform: scaleY(0.1); }
}

/* 볼터치 */
.cheeks {
  position: absolute;
  top: 50px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 55px;
}

.cheek {
  width: 18px;
  height: 10px;
  background: rgba(255, 150, 150, 0.5);
  border-radius: 50%;
}

/* 코 */
.nose {
  position: absolute;
  top: 58px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 8px;
  background: #D4A574;
  border-radius: 50%;
}

/* 입 */
.mouth {
  position: absolute;
  top: 70px;
  left: 50%;
  transform: translateX(-50%);
  width: 15px;
  height: 8px;
  border: 2px solid #C4956A;
  border-top: none;
  border-radius: 0 0 50% 50%;
  background: transparent;
}

/* 몸통 */
.body {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 90px;
  height: 70px;
  background: linear-gradient(180deg, #FFF0E0 0%, #FFE0C8 100%);
  border-radius: 45% 45% 50% 50%;
  box-shadow: 0 4px 8px rgba(0,0,0,0.06);
}

/* 손 */
.hands {
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 5px;
}

.hand {
  width: 22px;
  height: 22px;
  background: linear-gradient(135deg, #FFF5E6 0%, #FFE4C4 100%);
  border-radius: 50%;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}

.food-item {
  font-size: 28px;
  animation: bounce 2s ease-in-out infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

/* 말풍선 */
.speech-bubble {
  background: #fff;
  border-radius: 16px;
  padding: 12px 18px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
  position: relative;
  max-width: 220px;
  text-align: center;
}

.speech-bubble::before {
  content: '';
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 12px solid #fff;
}

.speech-bubble p {
  margin: 0;
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-line;
}

/* 기분별 스타일 */
.mood-happy .mouth {
  height: 12px;
  border-radius: 0 0 50% 50%;
}

.mood-thinking .eye {
  animation: none;
}

.mood-thinking .pupil {
  animation: lookAround 2s ease-in-out infinite;
}

@keyframes lookAround {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-3px); }
  75% { transform: translateX(3px); }
}

.mood-thinking .food-item {
  content: '🤔';
}

.mood-excited .mukkebi {
  animation: wiggle 0.5s ease-in-out infinite;
}

@keyframes wiggle {
  0%, 100% { transform: rotate(-2deg); }
  50% { transform: rotate(2deg); }
}

.mood-eating .mouth {
  width: 20px;
  height: 15px;
  background: #C4956A;
  border: none;
  border-radius: 50%;
}

.mood-eating .food-item {
  animation: eating 0.3s ease-in-out infinite;
}

@keyframes eating {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-3px) scale(0.95); }
}

/* 사이즈별 말풍선 */
.size-small .speech-bubble {
  max-width: 160px;
  padding: 8px 12px;
}

.size-small .speech-bubble p {
  font-size: 12px;
}

.size-large .speech-bubble {
  max-width: 280px;
  padding: 16px 24px;
}

.size-large .speech-bubble p {
  font-size: 16px;
}
</style>
