import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { transformAssetUrls } from '@quasar/vite-plugin'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue({
      template: {transformAssetUrls}
    })],

    resolve: {
      alias: {
        '@': '/src' 
      }
    }
})
