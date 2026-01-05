<template>
  <AppLayout title="시간 통계" :show-back="true">
    <div class="statistics-view">
      
      <!-- 요일별 활동 (주간) -->
      <div class="chart-section">
        <h3 class="section-title">요일별 활동</h3>
        <div class="chart-card">
          <div v-if="weeklyStats.length > 0" class="col-chart">
            <div 
              v-for="item in weeklyStats" 
              :key="item.label"
              class="col-item"
            >
              <span class="col-value" v-if="item.count > 0">{{ item.count }}</span>
              <div class="col-track">
                <div 
                  class="col-fill" 
                  :style="{ height: item.percentage + '%' }"
                ></div>
              </div>
              <span class="col-label">{{ item.label }}</span>
            </div>
          </div>
           <div v-else class="empty-state">
            데이터가 없습니다.
          </div>
        </div>
      </div>

      <!-- 시간대별 활동 -->
      <div class="chart-section">
        <h3 class="section-title">시간대별 활동</h3>
        <div class="chart-card">
          <div v-if="chartData" class="donut-chart-container">
            <div class="chart-wrapper">
              <Doughnut :data="chartData" :options="chartOptions" />
              <!-- 중앙 텍스트 (총 횟수) -->
              <div class="center-text">
                <span class="center-label" :style="{ color: centerInfo.color !== '#1c1c1e' ? centerInfo.color : '#8e8e93' }">{{ centerInfo.label }}</span>
                <span class="center-value" :style="{ color: centerInfo.color }">{{ centerInfo.value }}</span>
              </div>
            </div>
            
            <!-- 커스텀 범례 (리스트) -->
            <div class="custom-legend">
              <div 
                v-for="(item, index) in hourlyStats" 
                :key="index"
                class="legend-item"
              >
                <div class="legend-info">
                  <span class="legend-color" :style="{ backgroundColor: item.color }"></span>
                  <span class="legend-label">{{ item.label }}</span>
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
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import AppLayout from '@/components/layout/AppLayout.vue'
import { statisticsApi } from '@/api/statistics'
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js'
import { Doughnut } from 'vue-chartjs'

// Chart.js 등록
ChartJS.register(ArcElement, Tooltip, Legend)

const weeklyStats = ref([])
const hourlyStats = ref([])
const totalCount = ref(0)
const chartData = ref(null)

// 중앙 텍스트 상태
const centerInfo = ref({ label: '총 추천', value: '0회', color: '#1c1c1e' })

// 색상 팔레트
const timeColors = ['#007AFF', '#3291FF', '#5E9EFF', '#8AB4FF', '#A6C8FF', '#C2DAFF', '#DEEAFF']

const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  cutout: '70%',
  plugins: {
    legend: { display: false },
    tooltip: { enabled: false } // 툴팁 비활성화 (중앙 텍스트로 대체)
  },
  onHover: (event, elements) => {
    if (elements && elements.length > 0) {
      const index = elements[0].index
      // chartData의 datasets[0].data[index] 접근
      const label = chartData.value.labels[index]
      const value = chartData.value.datasets[0].data[index]
      const color = chartData.value.datasets[0].backgroundColor[index]
      
      centerInfo.value = {
        label: label,
        value: `${value}회`,
        color: color
      }
    } else {
      // 호버 해제 시 총 추천으로 복귀
      centerInfo.value = {
        label: '총 추천',
        value: `${totalCount.value}회`,
        color: '#1c1c1e'
      }
    }
  }
}

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

    // 병렬로 API 호출
    const results = await Promise.allSettled([
      statisticsApi.getMyWeeklyStats(params),
      statisticsApi.getMyHourlyStats(params)
    ])

    const weeklyRes = results[0].status === 'fulfilled' ? results[0].value : null
    const hourlyRes = results[1].status === 'fulfilled' ? results[1].value : null

    // 주간(요일별) 통계 처리
    if (weeklyRes && weeklyRes.data) {
      const maxCount = Math.max(...weeklyRes.data.map(item => item.value), 0)
      const dayOrder = ['일', '월', '화', '수', '목', '금', '토']
      
      weeklyStats.value = dayOrder.map(day => {
        const item = weeklyRes.data.find(d => d.label === day)
        const count = item ? item.value : 0
        const percentage = maxCount > 0 ? (count / maxCount) * 100 : 0
        
        return {
          label: day,
          count: count,
          percentage: percentage
        }
      })
    }

    // 시간대별 통계 처리 (도넛 차트)
    if (hourlyRes && hourlyRes.data) {
      const total = hourlyRes.data.reduce((sum, item) => sum + item.value, 0)
      totalCount.value = total
      
      // 초기 중앙 텍스트 설정
      centerInfo.value = { label: '총 추천', value: `${total}회`, color: '#1c1c1e' }

      // 데이터 정렬 (많은 순)
      const sortedData = hourlyRes.data.sort((a, b) => b.value - a.value)

      hourlyStats.value = sortedData.map((item, index) => ({
        label: formatTimeLabel(item.label),
        count: item.value,
        percentage: total > 0 ? Math.round((item.value / total) * 100) : 0,
        color: timeColors[index % timeColors.length]
      }))

      // 차트 데이터 설정
      chartData.value = {
        labels: sortedData.map(d => formatTimeLabel(d.label)),
        datasets: [
          {
            backgroundColor: sortedData.map((_, i) => timeColors[i % timeColors.length]),
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

const formatTimeLabel = (hour) => {
  return `${hour}`
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
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.empty-state {
  text-align: center;
  color: #999;
  font-size: 13px;
  padding: 20px 0;
}

/* 세로 바 차트 (요일별) */
.col-chart {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 180px;
  padding: 10px 0;
}

.col-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.col-track {
  width: 12px;
  height: 120px;
  background: #f8f9fa;
  border-radius: 6px;
  position: relative;
  overflow: hidden;
}

.col-fill {
  position: absolute;
  bottom: 0;
  width: 100%;
  border-radius: 6px;
  transition: height 0.8s cubic-bezier(0.4, 0, 0.2, 1);
  background: linear-gradient(to top, #007AFF, #5AC8FA);
}

.col-label {
  font-size: 12px;
  color: #666;
}

.col-value {
  font-size: 11px;
  color: #007AFF;
  font-weight: 600;
  margin-bottom: -4px;
}

/* 가로 바 차트 (시간대별) - CategoryStatsView 스타일 재사용 */
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
  pointer-events: none;
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
