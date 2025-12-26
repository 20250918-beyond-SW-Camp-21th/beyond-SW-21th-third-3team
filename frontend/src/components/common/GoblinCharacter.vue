<template>
  <div class="goblin-wrapper" :class="size">
    <div class="goblin-character" :class="mood">
      <!-- 귀 (디즈니풍 둥근 귀) -->
      <div class="ears">
        <div class="ear left"></div>
        <div class="ear right"></div>
      </div>
      
      <!-- 몸체 -->
      <div class="body">
        <!-- 얼굴 -->
        <div class="face">
          <!-- 눈썹 -->
          <div class="eyebrows">
            <div class="eyebrow left"></div>
            <div class="eyebrow right"></div>
          </div>
          
          <!-- 눈 (디즈니풍 큰 눈) -->
          <div class="eyes">
            <div class="eye left">
              <div class="eye-white">
                <div class="iris">
                  <div class="pupil"></div>
                </div>
                <div class="eye-shine"></div>
                <div class="eye-shine small"></div>
              </div>
            </div>
            <div class="eye right">
              <div class="eye-white">
                <div class="iris">
                  <div class="pupil"></div>
                </div>
                <div class="eye-shine"></div>
                <div class="eye-shine small"></div>
              </div>
            </div>
          </div>
          
          <!-- 코 -->
          <div class="nose"></div>
          
          <!-- 볼 -->
          <div class="cheeks">
            <div class="cheek left"></div>
            <div class="cheek right"></div>
          </div>
          
          <!-- 입 -->
          <div class="mouth">
            <div class="tongue"></div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 말풍선 -->
    <div v-if="message" class="speech-bubble">
      <span>{{ message }}</span>
      <div class="bubble-tail"></div>
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
    validator: (value) => ['happy', 'thinking', 'excited', 'sad'].includes(value)
  },
  message: {
    type: String,
    default: ''
  }
})
</script>

<style scoped>
.goblin-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

/* 사이즈 */
.goblin-wrapper.small .goblin-character {
  transform: scale(0.6);
}

.goblin-wrapper.medium .goblin-character {
  transform: scale(1);
}

.goblin-wrapper.large .goblin-character {
  transform: scale(1.5);
}

.goblin-character {
  position: relative;
  width: 120px;
  height: 150px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

/* 귀 (디즈니풍 둥근 귀) */
.ears {
  position: absolute;
  top: 8px;
  left: 50%;
  transform: translateX(-50%);
  width: 110px;
  display: flex;
  justify-content: space-between;
  z-index: 0;
}

.ear {
  width: 32px;
  height: 32px;
  background: linear-gradient(145deg, #A5D6A7 0%, #81C784 50%, #66BB6A 100%);
  border-radius: 50%;
  box-shadow: 
    inset -2px -2px 4px rgba(0, 0, 0, 0.1),
    inset 2px 2px 4px rgba(255, 255, 255, 0.3);
}

.ear.left {
  transform: rotate(-20deg);
}

.ear.right {
  transform: rotate(20deg);
}

/* 몸체 */
.body {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 105px;
  height: 110px;
  background: linear-gradient(180deg, #A5D6A7 0%, #81C784 40%, #66BB6A 100%);
  border-radius: 50% 50% 48% 48%;
  box-shadow: 
    0 8px 20px rgba(0, 0, 0, 0.15),
    inset 0 -10px 20px rgba(0, 0, 0, 0.05),
    inset 0 10px 20px rgba(255, 255, 255, 0.2);
  z-index: 1;
}

/* 얼굴 */
.face {
  position: relative;
  width: 100%;
  height: 100%;
}

/* 눈썹 */
.eyebrows {
  position: absolute;
  top: 22%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 30px;
}

.eyebrow {
  width: 14px;
  height: 4px;
  background: #558B2F;
  border-radius: 4px;
}

.eyebrow.left {
  transform: rotate(-8deg);
}

.eyebrow.right {
  transform: rotate(8deg);
}

/* 눈 (디즈니풍 큰 눈) */
.eyes {
  position: absolute;
  top: 28%;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
}

.eye {
  position: relative;
}

.eye-white {
  width: 28px;
  height: 32px;
  background: linear-gradient(180deg, #fff 0%, #f5f5f5 100%);
  border-radius: 50%;
  position: relative;
  box-shadow: 
    inset 0 2px 4px rgba(0, 0, 0, 0.08),
    0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.iris {
  position: absolute;
  bottom: 4px;
  left: 50%;
  transform: translateX(-50%);
  width: 18px;
  height: 20px;
  background: linear-gradient(180deg, #5D4037 0%, #4E342E 100%);
  border-radius: 50%;
}

.pupil {
  position: absolute;
  bottom: 2px;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 12px;
  background: #1a1a1a;
  border-radius: 50%;
}

.eye-shine {
  position: absolute;
  top: 6px;
  left: 6px;
  width: 8px;
  height: 8px;
  background: #fff;
  border-radius: 50%;
  z-index: 2;
}

.eye-shine.small {
  top: 12px;
  left: 14px;
  width: 4px;
  height: 4px;
}

/* 코 */
.nose {
  position: absolute;
  top: 52%;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 8px;
  background: #558B2F;
  border-radius: 50%;
}

/* 볼 */
.cheeks {
  position: absolute;
  top: 55%;
  left: 50%;
  transform: translateX(-50%);
  width: 85px;
  display: flex;
  justify-content: space-between;
}

.cheek {
  width: 18px;
  height: 12px;
  background: radial-gradient(ellipse, rgba(255, 138, 101, 0.5) 0%, transparent 70%);
  border-radius: 50%;
}

/* 입 */
.mouth {
  position: absolute;
  bottom: 22%;
  left: 50%;
  transform: translateX(-50%);
  width: 22px;
  height: 12px;
  background: #D32F2F;
  border-radius: 0 0 50% 50%;
  overflow: hidden;
}

.tongue {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 10px;
  height: 6px;
  background: #E57373;
  border-radius: 50% 50% 0 0;
}

/* 기분별 스타일 */

/* Happy */
.goblin-character.happy .mouth {
  width: 26px;
  height: 14px;
}

.goblin-character.happy .eyebrow.left {
  transform: rotate(-12deg) translateY(-2px);
}

.goblin-character.happy .eyebrow.right {
  transform: rotate(12deg) translateY(-2px);
}

/* Excited */
.goblin-character.excited .mouth {
  width: 30px;
  height: 18px;
}

.goblin-character.excited .eye-white {
  height: 36px;
}

.goblin-character.excited .iris {
  animation: excitedLook 0.8s ease-in-out infinite;
}

.goblin-character.excited .ear {
  animation: earWiggle 0.5s ease-in-out infinite alternate;
}

@keyframes excitedLook {
  0%, 100% { transform: translateX(-50%) scale(1); }
  50% { transform: translateX(-50%) scale(1.1); }
}

@keyframes earWiggle {
  from { transform: rotate(-25deg); }
  to { transform: rotate(-15deg); }
}

.goblin-character.excited .ear.right {
  animation: earWiggleRight 0.5s ease-in-out infinite alternate;
}

@keyframes earWiggleRight {
  from { transform: rotate(25deg); }
  to { transform: rotate(15deg); }
}

/* Thinking */
.goblin-character.thinking .eye-white {
  height: 26px;
}

.goblin-character.thinking .iris {
  animation: lookAround 2.5s ease-in-out infinite;
}

.goblin-character.thinking .mouth {
  width: 14px;
  height: 14px;
  background: none;
  border: 3px solid #D32F2F;
  border-radius: 50%;
}

.goblin-character.thinking .tongue {
  display: none;
}

.goblin-character.thinking .eyebrow.left {
  transform: rotate(-5deg) translateY(2px);
}

.goblin-character.thinking .eyebrow.right {
  transform: rotate(15deg) translateY(-3px);
}

@keyframes lookAround {
  0%, 100% { transform: translateX(-50%); }
  25% { transform: translateX(-70%); }
  75% { transform: translateX(-30%); }
}

/* Sad */
.goblin-character.sad .eyebrow.left {
  transform: rotate(15deg) translateY(2px);
}

.goblin-character.sad .eyebrow.right {
  transform: rotate(-15deg) translateY(2px);
}

.goblin-character.sad .mouth {
  width: 18px;
  height: 8px;
  background: none;
  border-top: 3px solid #D32F2F;
  border-radius: 50% 50% 0 0;
}

.goblin-character.sad .tongue {
  display: none;
}

.goblin-character.sad .eye-shine {
  opacity: 0.5;
}

/* 말풍선 */
.speech-bubble {
  position: relative;
  background: #fff;
  padding: 12px 20px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  max-width: 280px;
  text-align: center;
  border: 2px solid #E8F5E9;
}

.speech-bubble span {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
}

.bubble-tail {
  position: absolute;
  top: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 12px solid #fff;
  filter: drop-shadow(0 -2px 2px rgba(0, 0, 0, 0.05));
}

/* 사이즈별 말풍선 */
.goblin-wrapper.small .speech-bubble {
  padding: 8px 12px;
  max-width: 180px;
}

.goblin-wrapper.small .speech-bubble span {
  font-size: 12px;
}

.goblin-wrapper.large .speech-bubble {
  padding: 16px 24px;
  max-width: 360px;
}

.goblin-wrapper.large .speech-bubble span {
  font-size: 16px;
}

/* 사이즈별 float 애니메이션 조정 */
.goblin-wrapper.small .goblin-character {
  animation: floatSmall 3s ease-in-out infinite;
}

@keyframes floatSmall {
  0%, 100% { transform: scale(0.6) translateY(0); }
  50% { transform: scale(0.6) translateY(-3px); }
}

.goblin-wrapper.large .goblin-character {
  animation: floatLarge 3s ease-in-out infinite;
}

@keyframes floatLarge {
  0%, 100% { transform: scale(1.5) translateY(0); }
  50% { transform: scale(1.5) translateY(-8px); }
}
</style>
