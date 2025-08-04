import type { ConfigEnv, UserConfig } from 'vite'
import { defineConfig, loadEnv } from 'vite'
import { resolve } from 'path'
import Vue from '@vitejs/plugin-vue'
import VueJsx from '@vitejs/plugin-vue-jsx'
import progress from 'vite-plugin-progress'
import EslintPlugin from 'vite-plugin-eslint'
import { ViteEjsPlugin } from 'vite-plugin-ejs'
import PurgeIcons from 'vite-plugin-purge-icons'
import ServerUrlCopy from 'vite-plugin-url-copy'
import VueI18nPlugin from '@intlify/unplugin-vue-i18n/vite'
import { createSvgIconsPlugin } from 'vite-plugin-svg-icons'
import { createStyleImportPlugin, ElementPlusResolve } from 'vite-plugin-style-import'
import UnoCSS from 'unocss/vite'
import { visualizer } from 'rollup-plugin-visualizer'
import AutoImport from 'unplugin-auto-import/vite'

// https://vitejs.dev/config/
const root = process.cwd()

export default defineConfig(({command, mode}: ConfigEnv): UserConfig => {
  let env = {} as any
  const isBuild = command === 'build'
  if (!isBuild) {
    env = loadEnv(process.argv[3] === '--mode' ? process.argv[4] : process.argv[3], root)
  } else {
    env = loadEnv(mode, root)
  }
  return {
    base: env.VITE_BASE_PATH,
    plugins: [
      AutoImport({
        // 自动引入组合式 API，如 ref, reactive, computed, vue-router 等
        imports: ['vue', 'vue-router'],
        // 生成的自动引入的类型声明文件的路径
        dts: 'src/unplugin-auto-imports.d.ts'
      }),
      Vue({
        script: {
          // 开启defineModel，允许在Vue组件中使用defineModel语法糖来定义模型
          defineModel: true
        }
      }),
      VueJsx(),
      ServerUrlCopy(),
      // 打包时显示打包进度
      progress(),
      // 是否全量引入element-plus样式
      env.VITE_USE_ALL_ELEMENT_PLUS_STYLE === 'true' ? undefined :
        // 按需引入
        createStyleImportPlugin({
          resolves: [ElementPlusResolve()],
          libs: [{
            libraryName: 'element-plus',
            esModule: true,
            resolveStyle: (name) => {
              return name === 'click-outside' ? '' : `element-plus/es/components/${name.replace(/^el-/, '')}/style/css`
            }
          }]
        }),
      // Eslint 代码检查
      EslintPlugin({
        cache: false,
        failOnWarning: false,
        failOnError: false,
        include: ['src/**/*.vue', 'src/**/*.ts', 'src/**/*.tsx'] // 检查的文件
      }),
      // 国际化配置
      VueI18nPlugin({
        runtimeOnly: true,
        compositionOnly: true,
        include: [resolve(__dirname, 'src/locales/**')]
      }),
      // svg图标
      createSvgIconsPlugin({
        iconDirs: [resolve(root, '.', 'src/assets/svgs')],
        symbolId: 'icon-[dir]-[name]',
        svgoOptions: true
      }),
      // 清除未使用的图标
      PurgeIcons(),
      // 使用环境变量设置页面标题
      ViteEjsPlugin({title: env.VITE_APP_TITLE}),
      // 使用UnoCSS进行样式处理
      UnoCSS()
    ],
    css: {
      preprocessorOptions: {
        less: {
          additionalData: '@import "./src/styles/variables.module.less";',
          javascriptEnabled: true
        }
      }
    },
    resolve: {
      extensions: ['.ts', '.js', '.mjs', '.jsx', '.tsx', '.json', '.less', '.scss', '.css'],
      alias: [{
        find: 'vue-i18n',
        replacement: 'vue-i18n/dist/vue-i18n.cjs.js'
      }, {
        find: /\@\//,
        replacement: `${resolve(root, '.', 'src')}/`
      }]
    },
    esbuild: {
      // 是否删除console.log
      pure: env.VITE_DROP_CONSOLE === 'true' ? ['console.log'] : undefined,
      // 是否删除debugger
      drop: env.VITE_DROP_DEBUGGER === 'true' ? ['debugger'] : undefined
    },
    build: {
      // es2015=es6
      target: 'es2015',
      // 输出目录
      outDir: env.VITE_OUT_DIR || 'dist',
      // 是否sourcemap
      sourcemap: env.VITE_SOURCEMAP === 'true',
      // brotliSize: false,
      rollupOptions: {
        // 是否包分析
        plugins: env.VITE_USE_BUNDLE_ANALYZER === 'true' ? [visualizer()] : undefined,
        // 拆包
        output: {
          manualChunks: {
            'vue-chunks': ['vue', 'vue-router', 'pinia', 'vue-i18n'],
            'element-plus': ['element-plus'],
            'wang-editor': ['@wangeditor/editor', '@wangeditor/editor-for-vue'],
            echarts: ['echarts', 'echarts-wordcloud']
          }
        }
      },
      // 是否切割css
      cssCodeSplit: !(env.VITE_USE_CSS_SPLIT === 'false'),
      cssTarget: ['chrome31']
    },
    server: {
      port: 4000,
      open: true,
      // 代理
      proxy: {
        '/dashuserver': {
          // target: 'http://localhost:8125',
          changeOrigin: true,
          rewrite: (path) => path.replace(/^\/dashuserver/, '')
        }
      },
      // 热加载
      hmr: {
        // 热加载出错时，是否显示一个覆盖层展示错误信息，false=不显示
        overlay: false
      },
      // 监听ip访问0.0.0.0代表localhost或ip地址，默认是localhost
      host: '0.0.0.0'
    },
    optimizeDeps: {
      include: [
        'vue',
        'vue-router',
        'vue-types',
        'element-plus/es/locale/lang/zh-cn',
        'element-plus/es/locale/lang/en',
        '@iconify/iconify',
        '@vueuse/core',
        'axios',
        'qs',
        'echarts',
        'echarts-wordcloud',
        'qrcode',
        '@wangeditor/editor ',
        '@wangeditor/editor-for-vue',
        'vue-json-pretty',
        '@zxcvbn-ts/core',
        'dayjs',
        'cropperjs'
      ]
    }
  }
})
