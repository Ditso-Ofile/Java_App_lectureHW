import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// npm run build  => writes the production build into Spring Boot's static folder,
// so it is packed into homework.jar and served at http://localhost:8080/react/
export default defineConfig({
  plugins: [react()],
  base: '/react/',
  build: {
    outDir: '../src/main/resources/static/react',
    emptyOutDir: true,
  },
  // npm run dev (http://localhost:5173/react/) => API calls go to the running Spring Boot app
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
