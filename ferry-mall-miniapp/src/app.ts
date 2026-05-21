import { createApp } from 'vue'
import { createPinia } from 'pinia'
import './app.css'
import { useUserStore } from '@/stores/user'

const App = createApp({})
App.use(createPinia())

App.config.globalProperties.$onLaunch = async () => {
  const user = useUserStore()
  await user.checkLogin()
}

export default App
