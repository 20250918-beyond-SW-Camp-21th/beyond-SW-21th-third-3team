import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// Design System CSS Variables
import './styles/variables.css'

// Axios 기본 설정
axios.defaults.baseURL = '/api'
axios.defaults.withCredentials = true

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
