# 技术栈与版本

---

## 一、后端（Java）

### 基础框架

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | 源码和目标版本 |
| Spring Boot | 2.6.7 | 基础框架 |
| Spring Security | 5.6.x | 安全与认证（通过 Spring Boot starter 引入） |
| Spring Security OAuth2 | 2.5.2.RELEASE | OAuth2 授权服务器（单独依赖） |
| Spring AMQP / RabbitMQ | 跟随 Boot | 消息队列 |

### ORM 与数据库

| 组件 | 版本 | 说明 |
|------|------|------|
| MyBatis-Plus | 3.5.6 | ORM 框架 |
| MySQL Connector | 8.0.x | MySQL 驱动 |
| HikariCP | 跟随 Boot | 连接池（Spring Boot 默认） |

### 缓存

| 组件 | 版本 | 说明 |
|------|------|------|
| Spring Data Redis | 跟随 Boot | Redis 客户端 |
| Redis | 5+ | 服务器端版本 |

### 工具库

| 组件 | 版本 | 说明 |
|------|------|------|
| Hutool | 5.8.0.M3 | 核心工具集（JSON、加密、文件、压缩等） |
| Lombok | 1.18.x | 代码简化（@Getter/@Setter/@Slf4j 等） |
| FastJSON | 1.2.83 | JSON 序列化/反序列化 |
| MapStruct | 1.4.1.Final | DTO/Entity 转换（注解处理器） |
| Commons IO | 2.11.0 | IO 工具 |
| Commons FileUpload | 1.4 | 文件上传 |
| Guava | 31.1-jre | Google 基础工具 |
| Reflections | 0.10.2 | 类路径扫描（API 扫描用） |
| Thumbnailator | 0.4.21 | 图片缩略图处理 |
| jsoup | 1.14.3 | HTML 解析（XSS 清理用） |
| Aspose Words | 23.1 | Word 转 PDF |
| Aspose Cells | 23.1 | Excel 转 PDF |
| PDFBox | 2.0.24 | PDF 处理 |
| Apache POI | 4.1.2 | Office 文档读写 |

### 认证与安全

| 组件 | 版本 | 说明 |
|------|------|------|
| Hutool JWT | 5.8.0.M3 | JWT 令牌（基于 Hutool） |
| EasyCaptcha | 1.6.2 | 图形验证码 |
| jsencrypt | 3.3.2 | 前端 RSA 加密（前端依赖） |

### 第三方服务集成

| 组件 | 版本 | 说明 |
|------|------|------|
| 阿里云 OSS SDK | 3.17.4 | 阿里云对象存储 |
| 阿里云 SMS SDK | 2.0.24 | 阿里云短信服务 |
| 阿里云 dysmsapi | 2.0.24 | 短信 API |
| IJPay WxPay | 2.9.7 | 微信支付 |
| IJPay AliPay | 2.9.7 | 支付宝支付 |
| 百度 API Explorer SDK | 1.0.3.1 | 百度 OCR |

### API 文档

| 组件 | 版本 | 说明 |
|------|------|------|
| Springfox Swagger2 | 2.6.1 | Swagger API 文档 |

### 定时任务

| 组件 | 版本 | 说明 |
|------|------|------|
| Quartz | 2.3.x | 通过 Spring Boot starter 引入 |

### 开发工具

| 组件 | 版本 | 说明 |
|------|------|------|
| Maven | 3.6+ | 构建工具（wrapper 在项目中） |
| Directory Watcher | 0.18.0 | MyBatis XML 热加载（开发环境） |
| Freemarker | 2.6.x | 代码生成器模板引擎 |

### 项目版本

- **根 POM 版本：** `1.3.1`
- **子模块版本属性：** `${currentVersion}` = `1.3.10`
- **依赖关系：** `dashu-frame-component` ← `dashu-frame-core` ← `dashu-frame-module`

---

## 二、前端 Vue3（推荐，web-vue3/）

### 核心框架

| 组件 | 版本 | 说明 |
|------|------|------|
| Node.js | >= 18.20.7 | 运行环境 |
| pnpm | >= 9.15.3 | 包管理器 |
| Vue | 3.5.13 | 前端框架 |
| TypeScript | 5.7.3 | 类型系统 |
| Vite | 6.0.7 | 构建工具 |
| Element Plus | 2.11.2 | UI 组件库 |

### 状态管理与路由

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue Router | 4.5.0 | 路由（Hash 模式） |
| Pinia | 3.0.3 | 状态管理 |
| pinia-plugin-persistedstate | 4.2.0 | Pinia 持久化 |

### HTTP 与工具

| 组件 | 版本 | 说明 |
|------|------|------|
| Axios | 1.7.9 | HTTP 客户端 |
| js-cookie | 2.2.0 | Cookie 管理 |
| jsencrypt | 3.3.2 | RSA 加密 |
| dayjs | 1.11.13 | 日期处理 |
| lodash-es | 4.17.21 | 工具库 |
| qs | 6.13.1 | 查询字符串解析 |
| spark-md5 | 3.0.2 | MD5 计算 |

### 图表与富文本

| 组件 | 版本 | 说明 |
|------|------|------|
| ECharts | 5.6.0 | 图表 |
| wangEditor | 5.1.23 | 富文本编辑器 |
| VueUse | 12.3.0 | Composition API 工具集 |

### 国际化

| 组件 | 版本 | 说明 |
|------|------|------|
| vue-i18n | 11.0.1 | 国际化 |

### 其他 UI 组件

| 组件 | 版本 | 说明 |
|------|------|------|
| UnoCSS | 0.65.4 | 原子化 CSS |
| @iconify/vue | 4.3.0 | 图标库 |
| cropperjs | 1.6.2 | 图片裁剪 |
| driver.js | 1.3.1 | 引导页 |
| Monaco Editor | 0.52.2 | 代码编辑器 |
| clipboard | 2.0.4 | 剪贴板 |

### 开发工具

| 组件 | 版本 | 说明 |
|------|------|------|
| ESLint | 9.17.0 | 代码规范 |
| Stylelint | 16.12.0 | 样式规范 |
| vue-tsc | 2.2.0 | TypeScript 类型检查 |
| Less | 4.2.1 | CSS 预处理器 |
| Terser | 5.37.0 | JS 压缩（生产） |
| unplugin-auto-import | 19.3.0 | 自动导入 API |

### 环境变量

```ini
# .env.development
VITE_API_BASE_PATH=/dashuserver
VITE_APP_KEY_PREFIX=ds

# .env.production
VITE_API_BASE_PATH=/dashuserver
VITE_DROP_DEBUGGER=true
VITE_DROP_CONSOLE=true
```

### 构建命令

```bash
pnpm install
pnpm run dev      # 开发：端口 4000，代理 /dashuserver -> 后端
pnpm run build    # 构建
```

---

## 三、前端 Vue2（旧版，web-vue2/）

### 核心框架

| 组件 | 版本 | 说明 |
|------|------|------|
| Node.js | >= 8.9 | 运行环境 |
| npm | >= 3.0.0 | 包管理器 |
| Vue | 2.7.16 | 前端框架 |
| Vue CLI | 4.5.18 | 构建工具 |
| Element UI | 2.15.13 | UI 组件库 |

### 状态管理与路由

| 组件 | 版本 | 说明 |
|------|------|------|
| Vue Router | 3.6.5 | 路由（Hash 模式） |
| Vuex | 3.1.0 | 状态管理 |

### HTTP 与工具

| 组件 | 版本 | 说明 |
|------|------|------|
| Axios | 1.4.0 | HTTP 客户端 |
| js-cookie | 2.2.0 | Cookie 管理 |
| jsencrypt | 3.3.2 | RSA 加密 |
| lodash | 4.17.21 | 工具库 |
| qs | 6.11.0 | 查询字符串解析 |
| spark-md5 | 3.0.2 | MD5 计算 |

### 图表与富文本

| 组件 | 版本 | 说明 |
|------|------|------|
| ECharts | 5.4.3 | 图表 |
| wangEditor | 5.1.23 | 富文本编辑器 |

### 其他 UI 组件

| 组件 | 版本 | 说明 |
|------|------|------|
| clipboard | 2.0.4 | 剪贴板 |
| driver.js | 0.9.5 | 引导页 |
| screenfull | 4.2.0 | 全屏 |
| fuse.js | 3.4.4 | 模糊搜索 |

### 样式

| 组件 | 版本 | 说明 |
|------|------|------|
| SCSS/Sass | 1.26.2 | CSS 预处理器 |
| normalize.css | 7.0.0 | 样式重置 |

### 开发工具

| 组件 | 版本 | 说明 |
|------|------|------|
| ESLint | 6.8.0 | 代码规范 |
| Webpack | 4.x | 打包（Vue CLI 内置） |
| babel-eslint | 10.1.0 | ESLint 解析器 |
| svgo | 1.2.0 | SVG 优化 |

### 环境变量

```ini
# .env.development
VUE_APP_BASE_API=/dashuserver

# .env.production
VUE_APP_BASE_API=/dashuserver
```

### 构建命令

```bash
npm install
npm run dev           # 开发：端口 9527，代理 /dashuserver -> localhost:8125
npm run build:prod    # 构建
npm run lint          # ESLint 检查
npm run test:unit     # 单元测试（Jest）
```

---

## 四、基础设施

### 数据库

| 组件 | 版本 | 说明 |
|------|------|------|
| MySQL / MariaDB | 10.3+ | 关系数据库 |
| Redis | 5+ | 缓存 |

### 容器化部署

| 组件 | 版本 | 说明 |
|------|------|------|
| Docker | 20+ | 容器运行时 |
| Docker Compose | 2.x | 编排 |
| OpenResty | 1.29.2.4.1 | 带 WAF 的反向代理 |

### Docker 服务端口

| 服务 | 内部端口 | 外部端口 |
|------|----------|----------|
| OpenResty | 80 | 80 |
| Spring Boot | 8123 | 8123 |
| MariaDB | 3306 | 13306 |
| Redis | 6379 | 6379 |

---

## 五、持续集成部署

| 组件 | 版本 | 说明 |
|------|------|------|
| Node.js | 16+ | CI 脚本运行环境 |
| node-ssh | ^12 | SSH 部署库 |

### 部署流程

```bash
# 1. 初始部署
cd ci && npm i
node 1.ci-docker-install.js   # 安装 Docker 环境
node 2.ci-docker-start.js     # 启动 Docker 服务
node 3.ci-mysql-restart.js    # 导入数据库

# 2. 后端更新
mvn clean package -DskipTests -pl module
node ci-java.js               # 上传 JAR + lib/，重启容器

# 3. 前端 Vue3 更新
cd web-vue3 && pnpm run build
cd ../ci && node ci-vue3.js   # 上传 dist/，重启容器

# 4. 前端 Vue2 更新
cd web-vue2 && npm run build:prod
cd ../ci && node ci-vue2.js   # 上传 dist/，重启容器
```
