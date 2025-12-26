import axios from 'axios'

const api = axios.create({
    baseURL: '/api/v1',
    timeout: 10000,
    withCredentials: true
})

export const reviewApi = {
    /**
     * 리뷰 작성
     * @param {number} restaurantId - 음식점 ID
     * @param {Object} data - 리뷰 데이터
     * @param {string} data.content - 리뷰 내용
     * @param {number} data.rating - 평점 (1-5)
     */
    createReview(restaurantId, data) {
        return api.post(`/reviews/${restaurantId}`, data)
    },

    /**
     * 리뷰 수정
     * @param {number} reviewId - 리뷰 ID
     * @param {Object} data - 수정할 리뷰 데이터
     * @param {string} data.content - 리뷰 내용
     * @param {number} data.rating - 평점 (1-5)
     */
    updateReview(reviewId, data) {
        return api.put(`/reviews/${reviewId}`, data)
    },

    /**
     * 리뷰 삭제
     * @param {number} reviewId - 리뷰 ID
     */
    deleteReview(reviewId) {
        return api.delete(`/reviews/${reviewId}`)
    },

    /**
     * 내 리뷰 조회
     */
    getMyReviews() {
        return api.get('/reviews/me')
    },

    /**
     * 음식점별 리뷰 조회
     * @param {number} restaurantId - 음식점 ID
     */
    getRestaurantReviews(restaurantId) {
        return api.get(`/reviews/restaurants/${restaurantId}`)
    }
}

export default reviewApi
