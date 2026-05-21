import path from 'node:path'
import { defineConfig } from '@tarojs/cli'

export default defineConfig(async () => ({
  projectName: 'ferry-mall-miniapp',
  date: '2026-05-20',
  designWidth: 750,
  deviceRatio: { 640: 2.34 / 2, 750: 1, 828: 1.81 / 2 },
  sourceRoot: 'src',
  outputRoot: 'dist',
  framework: 'vue3',
  compiler: 'vite',
  alias: {
    '@': path.resolve(__dirname, '..', 'src')
  },
  plugins: ['@tarojs/plugin-html'],
  mini: {},
  h5: { publicPath: '/', staticDirectory: 'static' }
}))
