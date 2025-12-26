import axios from 'axios'

const api = axios.create({
    baseURL: '/api/v1',
    timeout: 10000,
    withCredentials: true
})

export const restaurantApi = {

    /**
     * 식사 시간대별 해시태그 목록 조회
     * @param {string} mealType - 식사 시간대 (BREAKFAST, LUNCH, DINNER)
     */
    getHashTagsByMealType(mealType) {
        return api.get('/hashtags', { params: { mealType } })
    },

    /**
     * 주변 음식점 검색
     * @param {number} latitude - 위도
     * @param {number} longitude - 경도
     * @param {number} radius - 검색 범위 (미터)
     */
    searchNearbyRestaurants(latitude, longitude, radius = 500) {
        return api.post('/restaurants/search', {
            latitude,
            longitude,
            radius
        })
    },

    /**
     * AI 음식점 추천 요청
     * @param {Object} params - 추천 요청 파라미터
     * @param {number} params.latitude - 위도
     * @param {number} params.longitude - 경도
     * @param {number} params.radius - 검색 범위 (미터, 100-500)
     * @param {string[]} params.hashTagCodes - 해시태그 코드 목록 (필수, 최소 1개)
     * @param {number[]} [params.excludeRestaurantIds] - 제외할 음식점 ID 목록
     */
    getRecommendation({ latitude, longitude, radius, hashTagCodes, excludeRestaurantIds = [] }) {
        return api.post('/ai/recommend', {
            latitude,
            longitude,
            radius,
            hashTagCodes,
            excludeRestaurantIds
        })
    },

    /**
     * 음식점 상세 정보 조회
     * @param {string} restaurantId - 음식점 ID
     */
    getRestaurantDetail(restaurantId) {
        return api.get(`/restaurants/${restaurantId}`)
    }
}

export default restaurantApi
