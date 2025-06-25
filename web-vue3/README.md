## 官网

https://element-plus-admin-doc.cn/



## 简介
vue-element-plus-admin 是一个基于 `element-plus` 免费开源的中后台模版。使用了最新的`vue3`，`vite`，`TypeScript`等主流技术开发，开箱即用的中后台前端解决方案，可以用来作为项目的启动模版，也可用于学习参考。并且时刻关注着最新技术动向，尽可能的第一时间更新。

vue-element-plus-admin 的定位是后台集成方案，不太适合当基础模板来进行二次开发。因为集成了很多你可能用不到的功能，会造成不少的代码冗余。如果你的项目不关注这方面的问题，也可以直接基于它进行二次开发。

如需要基础模版，请切换到 `mini` 分支，`mini` 只简单集成了一些如：布局、动态菜单等常用布局功能，更适合开发者进行二次开发。



## node、pnpm版本建议
1. node版本：v18.20.7+
2. pnpm版本：9.15.3+
3. 不推荐使用npm（未测试）



## 安装和使用
- 安装依赖
```bash
cd web-vue3
pnpm install
```
- 运行
```bash
pnpm run dev
```
- 打包
```bash
pnpm run build
```



## 目录结构

```
.
├── mock # 自定义 mock 数据及配置
├── public # 静态资源
├── src # 项目代码
│   ├── api # api接口管理
│   ├── assets # 静态资源
|   |── axios # axios配置
│   ├── components # 公用组件
│   ├── constants # 存放常量
│   ├── directives # 自定义指令
│   ├── hooks # 常用hooks
│   ├── layout # 布局组件
│   ├── locales # 语言文件
│   ├── plugins # 外部插件
│   ├── router # 路由配置
│   ├── store # 状态管理
│   ├── styles # 全局样式
│   ├── utils # 全局工具类
│   ├── views # 路由页面
│   ├── App.vue # 入口vue文件
│   ├── main.ts # 主入口文件
│   └── permission.ts # 路由拦截
├── types # 全局类型
├── .browserslistrc # 浏览器兼容配置
├── .env.development # 本地开发环境 环境变量配置
├── .env.production # 打包到生产环境 环境变量配置
├── .gitignore # git 跳过配置
├── .pnpm-debug.log # pnpm 调试日志
├── .stylelintignore # stylelint 跳过检测配置
├── commitlint.config.js # git commit 提交规范配置
├── eslint.config.mjs # eslint 配置
├── index.html # 入口页面
├── package.json # 项目依赖
├── postcss.config.cjs # postcss 配置
├── README.md
├── stylelint.config.js # stylelint 配置
├── tsconfig.json # typescript 配置
└── uno.config.ts # unocss 配置
├── vite.config.ts # vite 配置
```



## 技术栈
- [node](http://nodejs.org/) 和 [git](https://git-scm.com/) - 项目开发环境
- [Vite](https://vitejs.dev/) - 熟悉 vite 特性
- [Vue3](https://v3.vuejs.org/) - 熟悉 Vue 基础语法
- [TypeScript](https://www.typescriptlang.org/) - 熟悉 `TypeScript` 基本语法
- [Es6+](http://es6.ruanyifeng.com/) - 熟悉 es6 基本语法
- [Vue-Router-Next](https://next.router.vuejs.org/) - 熟悉 vue-router 基本使用
- [Element-Plus](https://element-plus.org/) - element-plus 基本使用



## 浏览器支持
本地开发推荐使用 `Chrome 80+` 浏览器

支持现代浏览器, 不支持 IE

| [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/archive/internet-explorer_9-11/internet-explorer_9-11_48x48.png" alt=" Edge" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>IE | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/edge/edge_48x48.png" alt=" Edge" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Edge | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/firefox/firefox_48x48.png" alt="Firefox" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Firefox | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/chrome/chrome_48x48.png" alt="Chrome" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Chrome | [<img src="https://raw.githubusercontent.com/alrra/browser-logos/master/src/safari/safari_48x48.png" alt="Safari" width="24px" height="24px" />](http://godban.github.io/browsers-support-badges/)</br>Safari |
| :-: | :-: | :-: | :-: | :-: |
| not support | last 2 versions | last 2 versions | last 2 versions | last 2 versions |
