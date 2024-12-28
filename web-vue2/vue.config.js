'use strict'
const path = require('path')
const defaultSettings = require('./src/settings.js')

function resolve(dir) {
  return path.join(__dirname, dir)
}

const name = defaultSettings.title || '大树快速开发平台'

// 带端口运行命令：npm run dev --port = 9527
const port = process.env.port || process.env.npm_config_port || 9527 // dev port

// 配置说明：https://cli.vuejs.org/zh/config/index.html
module.exports = {
  // 静态资源路径，使用相对路径 @see:https://cli.vuejs.org/zh/config/index.html#publicpath
  publicPath: './',
  outputDir: 'dist',
  // 把静态资源都放入static
  assetsDir: 'static',
  lintOnSave: process.env.NODE_ENV === 'development',
  productionSourceMap: false,
  devServer: {
    port: port,
    open: true,
    overlay: {
      warnings: false,
      errors: true
    },
    proxy: {
      '/dashuserver': {
        target: 'http://localhost:8125',
        // target: 'https://yanshi.java119.cn/dashuserver',
        changeOrigin: true,
        pathRewrite: {"^/dashuserver": ""}
      }
    }
  },
  // 解决导入第三方包时，因为es语法导致的错误（让源码中的es6、es7的特性转换为es5）
  transpileDependencies: ['swagger-ui-dist'],
  configureWebpack: {
    // 打包时，给index.html设置title
    name: name,
    resolve: {
      alias: {
        '@': resolve('src')
      }
    }
  },
  chainWebpack(config) {
    // Preload： 用于标记页面加载后即将用到的资源，浏览器将在主体渲染前加载preload标记文件。
    // 预加载（建议开启，可提高运行速度）
    config.plugin('preload').tap(() => [{
      rel: 'preload',
      // 忽略 runtime.js @see:https://github.com/vuejs/vue-cli/blob/dev/packages/@vue/cli-service/lib/config/app.js#L171
      fileBlacklist: [/\.map$/, /hot-update\.js$/, /runtime\..*\.js$/],
      include: 'initial'
    }])

    // Prefetch: 用于标记浏览器在页面加载完成后，利用空闲时间预加载的内容。
    // 文件太多时，预读无意义，删除预读
    config.plugins.delete('prefetch')

    // svg-sprite-loader设置
    config.module.rule('svg').exclude.add(resolve('src/icons')).end()
    config.module.rule('icons').test(/\.svg$/).include.add(resolve('src/icons')).end()
      .use('svg-sprite-loader').loader('svg-sprite-loader').options({symbolId: 'icon-[name]'}).end()

    config
      .when(process.env.NODE_ENV !== 'development',
        config => {
          config
            .plugin('ScriptExtHtmlWebpackPlugin')
            .after('html')
            .use('script-ext-html-webpack-plugin', [{
              // `runtime` 和 runtimeChunk 相同。默认：`runtime`
              inline: /runtime\..*\.js$/
            }])
            .end()
          config
            .optimization.splitChunks({
            chunks: 'all',
            cacheGroups: {
              libs: {
                name: 'chunk-libs',
                test: /[\\/]node_modules[\\/]/,
                priority: 10,
                chunks: 'initial' // only package third parties that are initially dependent
              },
              elementUI: {
                name: 'chunk-elementUI', // split elementUI into a single package
                priority: 20, // the weight needs to be larger than libs and app or it will be packaged into libs or app
                test: /[\\/]node_modules[\\/]_?element-ui(.*)/ // in order to adapt to cnpm
              },
              commons: {
                name: 'chunk-commons',
                test: resolve('src/components'), // can customize your rules
                minChunks: 3, //  minimum common number
                priority: 5,
                reuseExistingChunk: true
              }
            }
          })
          // @see:https://webpack.js.org/configuration/optimization/#optimizationruntimechunk
          config.optimization.runtimeChunk('single')
        }
      )
  }
}
