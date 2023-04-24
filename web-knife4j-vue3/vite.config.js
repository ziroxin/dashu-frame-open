import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vueJsx from '@vitejs/plugin-vue-jsx'
import Components from 'unplugin-vue-components/vite'
import {AntDesignVueResolver} from 'unplugin-vue-components/resolvers'
import viteCompression from 'vite-plugin-compression';
import {resolve} from 'path'

// https://vitejs.dev/config/
export default defineConfig({
    base: './',
    plugins: [
        vue(),
        vueJsx(),
        Components({
            resolvers: [AntDesignVueResolver()]
        }),
        viteCompression({
            deleteOriginFile: false, //删除源文件
            threshold: 10240, //压缩前最小文件大小
            algorithm: 'gzip', //压缩算法
            ext: '.gz', //文件类型
        }),
        // removeConsole()
    ],
    resolve: {
        alias: [
            {find: '@', replacement: resolve(__dirname, 'src')},
            {find: /^~/, replacement: ''},
        ]
    },
    // 开启less支持
    css: {
        preprocessorOptions: {
            less: {
                javascriptEnabled: true
            }
        }
    },
    server: {
        port: 9900,
        open: true,
        host: true,
        proxy: {
            '/api': {
                target: `http://localhost:8123/`,
                changeOrigin: true,
                rewrite: (path) => path.replace(/^\/api/, '')
            }
        }
    },
    build: {
        rollupOptions: {
            input: 'index.html',
            output: {
                chunkFileNames: 'webjars/js/[name]-[hash].js',
                entryFileNames: 'webjars/js/[name]-[hash].js',
                assetFileNames: 'webjars/[ext]/[name]-[hash].[ext]'
            }
        }
    }
})
