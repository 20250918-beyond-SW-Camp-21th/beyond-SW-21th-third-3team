import axios from 'axios'

// 별도 인스턴스 (api prefix 없는 엔드포인트용)
const rootApi = axios.create({
    baseURL: '',
    timeout: 10000,
    withCredentials: true
})

export const userApi = {
    /**
     * 로그인 성공 확인
     * 백엔드 엔드포인트: /api/users/login-success (v1 없음)
     */
    checkLoginSuccess() {
        return rootApi.get('/api/users/login-success')
    },

    /**
     * 사용자 프로필 조회 (GitHub OAuth)
     * 백엔드 엔드포인트: /github-profile (api prefix 없음)
     */
    getProfile() {
        return rootApi.get('/github-profile')
    },

    /**
     * 로그아웃
     * Spring Security 로그아웃 엔드포인트: /logout (api prefix 없음)
     */
    logout() {
        return rootApi.post('/logout')
    }
}

export default userApi
