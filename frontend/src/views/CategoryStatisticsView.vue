<template>
  <AppLayout title="카테고리별 추천" :show-back="true">
    <div class="statistics-view">
      <div class="chart-section">
        <h3 class="section-title">전체 카테고리 통계</h3>
        <div class="chart-card">
          <div v-if="chartData" class="donut-chart-container">
            <div class="chart-wrapper">
              <Doughnut :data="chartData" :options="chartOptions" />
              <!-- 중앙 텍스트 (총 횟수) -->
              <div class="center-text">
                <span class="center-label">총 추천</span>
                <span class="center-value">{{ totalCount }}회</span>
              </div>
            </div>
            
            <!-- 커스텀 범례 (리스트) -->
            <div class="custom-legend">
              <div 
                v-for="(item, index) in categoryStats" 
                :key="index"
                class="legend-item"
              >
                <div class="legend-info">
                  <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
                  <span class="legend-label">{{ item.category }}</span>
                </div>
                <div class="legend-values">
                  <span class="legend-percent">{{ item.percentage }}%</span>
                  <span class="legend-count">{{ item.count }}회</span>
                </div>
              </div>
            </div>

          </div>
          <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>
    </div>
  </AppLayout>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import AppLayout from '@/components/layout/AppLayout.vue'
import { statisticsApi } from '@/api/statistics'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'

// Chart.js 등록
ChartJS.register(ArcElement, Tooltip, Legend)

const categoryStats = ref([])
const totalCount = ref(0)
const chartData = ref(null)

// 색상 팔레트 (파란색 테마)
const categoryColors = ['#007AFF', '#3291FF', '#5E9EFF', '#8AB4FF', '#A6C8FF', '#C2DAFF', '#DEEAFF']

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '70%', // 도넛 두께 조절
  plugins: {
    legend: {
      display: false // 커스텀 범례 사용을 위해 숨김
    },
    tooltip: {
      callbacks: {
        label: function(context) {
          return ` ${context.label}: ${context.raw}회`
        }
      }
    }
  }
}

import { useRoute } from 'vue-router'

const route = useRoute()

// 데이터 로드
const loadStatistics = async () => {
  try {
    const queryYear = route.query.year
    const queryMonth = route.query.month
    
    let targetDate = new Date()
    if (queryYear && queryMonth) {
      targetDate = new Date(queryYear, queryMonth - 1, 1)
    }

    const year = targetDate.getFullYear()
    const month = String(targetDate.getMonth() + 1).padStart(2, '0')
    const lastDay = new Date(year, targetDate.getMonth() + 1, 0).getDate()
    
    const from = `${year}-${month}-01`
    const to = `${year}-${month}-${lastDay}`
    
    const params = { from, to }

    // API 호출
    const categoriesRes = await statisticsApi.getMyCategoryStats(params)

    // 카테고리 통계 처리
    if (categoriesRes && categoriesRes.data) {
      const total = categoriesRes.data.reduce((sum, item) => sum + item.value, 0)
      totalCount.value = total

      // 데이터 정렬
      const sortedData = categoriesRes.data.sort((a, b) => b.value - a.value)

      categoryStats.value = sortedData.map((item, index) => ({
        category: item.label,
        count: item.value,
        percentage: total > 0 ? Math.round((item.value / total) * 100) : 0,
        color: categoryColors[index % categoryColors.length]
      }))

      // 차트 데이터 설정
      chartData.value = {
        labels: sortedData.map(d => d.label),
        datasets: [
          {
            backgroundColor: sortedData.map((_, i) => categoryColors[i % categoryColors.length]),
            data: sortedData.map(d => d.value),
            borderWidth: 0,
            hoverOffset: 4
          }
        ]
      }
    }

  } catch (error) {
    console.error('통계 로드 실패:', error)
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.statistics-view {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chart-section {
  margin-bottom: 8px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 4px;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-state {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
}

/* 도넛 차트 컨테이너 */
.donut-chart-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 24px;
}

.chart-wrapper {
  position: relative;
  width: 200px;
  height: 200px;
}

/* 중앙 텍스트 */
.center-text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  pointer-events: none; /* 차트 호버링 방해 안 하게 */
}

.center-label {
  font-size: 12px;
  color: #8e8e93;
}

.center-value {
  font-size: 20px;
  font-weight: 700;
  color: #1c1c1e;
}

/* 커스텀 범례 */
.custom-legend {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.legend-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 8px;
  border-bottom: 1px solid #f5f5f5;
}

.legend-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.legend-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
}

.legend-label {
  font-size: 14px;
  color: #333;
}

.legend-values {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-percent {
  font-size: 13px;
  font-weight: 600;
  color: #333;
}

.legend-count {
  font-size: 12px;
  color: #999;
}
</style>
