# 架构分层规范

## 项目总览

大树快速开发平台是一个前后端分离的多模块 Maven 项目，包含后端（3 个模块）和前端（2 个版本）。

```
dashu-frame-open/
├── component/              # 组件库层 — 工具类、文件处理、JWT、验证码、OSS、短信、Swagger、MyBatis-Plus 基础
├── core/                   # 业务核心层 — 安全 (Spring Security+JWT)、权限/RBAC、DDoS、XSS、MyBatis-Plus 扩展、Quartz
├── module/                 # 应用层 — 入口、Controller、MyBatis XML 映射、配置，打包为可运行 app
├── web-vue3/               # Vue3 + Element Plus + TypeScript + Vite（推荐使用）
├── web-vue2/               # Vue2 + Element UI + JavaScript + Webpack（旧版）
└── ci/                     # Docker 持续集成部署
```

**依赖链：** `component` ← `core` ← `module`（module 依赖 core，core 依赖 component）

---

## 包结构

### 根包命名

```
com.kg.**  — 所有 Java 代码均位于此根包下
```

### 三模块分层

| 模块 | 根包 | 说明 |
|------|------|------|
| component | `com.kg.component.*` | 工具库、通用组件，不含业务逻辑 |
| core | `com.kg.core.*` | 核心业务框架（安全、权限、组织、日志等） |
| module | `com.kg.module.*` | 业务模块代码（Controller、Service、Entity、Mapper、DTO） |

### component 模块（12 个子包）

```
com.kg.component/
├── captcha/            # 验证码生成（EasyCaptcha 封装）
├── desensitized/       # 数据脱敏（Jackson 注解 @JsonDesensitized）
├── file/               # 文件上传/下载/分片/类型检测
├── generator/          # MyBatis-Plus 代码生成器（定制版）
├── jwt/                # JWT 工具（Hutool JWT 封装）
├── office/             # Word/Excel 读写操作
├── pdf/                # Word/Excel/图片转 PDF
├── redis/              # Redis 配置 + RedisUtils（SETNX 分布式锁）
├── sms/                # 阿里云短信发送
├── swagger/            # Swagger2 配置（Springfox 2.6.1）
├── utils/              # 12 个工具类（GuidUtils、IpUtils、MyRSAUtils 等）
└── wechat/             # 微信小程序登录/二维码
```

### core 模块（22 个子包）

```
com.kg.core/
├── annotation/         # 自定义注解：@AutoOperateLog / @IsResponseResult / @NoRepeatSubmit
├── aspect/             # AOP 实现：操作日志、防重复提交、API 扫描
├── base/               # 抽象基类：BaseController / BaseService / BaseServiceImpl / BaseDao / BaseDTO / BaseEntity / BaseConverter
├── common/constant/    # 缓存常量 (CacheConstant)、登录常量 (LoginConstant)
├── ddos/               # DDoS 防护（Redis 限流）
├── exception/          # 异常体系：BaseException / BaseErrorCode / GlobalExceptionHandler
├── mybatisplus/        # MyBatis-Plus 配置（分页插件，最大条数 100）
├── security/           # Spring Security + JWT（过滤器链、鉴权失败处理、当前用户工具）
├── web/                # 统一响应体 ResponseResult、全局响应包装、MVC 配置、错误控制器、图片缩略图拦截器
├── xss/                # XSS 过滤（Filter + RequestWrapper + 清理工具）
├── zapi/               # API 资源管理（@PreAuthorize 扫描、API 分组）
├── zapigroup/          # API 分组管理
├── zcaptcha/           # 验证码校验（Redis 存储）
├── zlog/               # 操作日志记录
├── zorg/               # 组织架构管理
├── zpermission/        # RBAC 权限核心（权限树、权限类型枚举、权限-API 关联）
├── zquartz/            # Quartz 定时任务管理
├── zrole/              # 角色管理（角色-权限关联）
├── zsafety/            # 密码安全策略
├── zuser/              # 用户管理（用户-角色关联）
├── zuserlock/          # 用户锁定
└── zuserpassword/      # 密码修改历史
```

### module 模块（17 个业务包 + 入口）

```
com.kg.module/
├── applet/wechat/              # 微信小程序登录
├── config/                     # 系统配置 CRUD
├── dictData/ / dictType/       # 字典管理
├── files/ / filesStatic/       # 文件管理
├── generator/                  # 表单生成器
├── message/ / messageTo/       # 消息管理
├── news/                       # 新闻 Demo
├── oauth2/client/ / user/      # OAuth2 客户端详情
├── redisCache/                 # Redis 缓存管理
├── sms/                        # 短信 Demo
├── test/                       # 测试 Controller
├── trade/ / tradeRefund/       # 交易 Demo
└── userTheme/                  # 用户主题

com.kg.                         # 入口与扩展
├── DashuApplication.java       # 启动入口
├── DashuApplicationRunner.java # 启动后初始化
├── core/                       # core 扩展（注册、登录、文件上传、FormGenerator）
├── component/                  # component 扩展（Spring 管理的 OAuth2 / 阿里云OSS / 支付 / RabbitMQ）
```

---

## 启动入口

```java
// module/src/main/java/com/kg/DashuApplication.java
@EnableTransactionManagement
@MapperScan("com.kg.**.mapper")
@SpringBootApplication(scanBasePackages = "com.kg.**")
public class DashuApplication {
    public static void main(String[] args) {
        SpringApplication.run(DashuApplication.class, args);
    }
}
```

- `scanBasePackages = "com.kg.**"`：自动扫描三模块所有 Spring Bean
- `@MapperScan("com.kg.**.mapper")`：自动发现所有 Mapper 接口
- `@EnableTransactionManagement`：启用声明式事务

---

## 分层职责

```
Controller (@RestController)
    ├── 接收请求、参数校验
    ├── @PreAuthorize 权限控制
    ├── @NoRepeatSubmit 防重复提交
    ├── AutoOperateLog 操作日志
    ├── 调用 Service + Convert 转换返回值
    └── 返回 DTO（由 ResponseResultBodyAdvice 自动包装为 ResponseResult）
        │
        ▼
Service (extends ServiceImpl<Mapper, Entity>)
    ├── @Transactional 事务管理
    ├── 业务逻辑、动态查询构建（JSON params 参数）
    ├── Convert 层 DTO ↔ Entity 转换
    └── Redis 缓存管理
        │
        ▼
Mapper (extends BaseMapper<Entity>)
    └── 仅负责数据库操作（MyBatis-Plus 基础 CRUD + 自定义 XML SQL）
        │
        ▼
Entity (@TableName) ←→ DTO (@ApiModel) ←→ Convert (MapStruct @Mapper)
```

---

## 安全架构

### 认证流程

```
1. 登录 → /login/login → AuthenticationManager.authenticate()
   → SecurityUserDetailServiceImpl.loadUserByUsername()
   → 查询 ZUser → 加载角色权限 → 返回 SecurityUserDetailEntity
   → JwtUtils.createToken(userId) → 生成 JWT + 存入 Redis
   → 返回 token + 用户信息

2. 后续请求 → JwtTokenAuthenticationFilter
   → 从 header/param 提取 JWT → parseToken() 解析用户 ID
   → 从 Redis 加载用户信息 → 设置 SecurityContextHolder
   → @PreAuthorize 注解鉴权
```

### 过滤器链

```
DecryptHttpServletRequestFilter (RSA 参数解密)
    → JwtTokenAuthenticationFilter (JWT 校验)
        → UsernamePasswordAuthenticationFilter (登录认证)
```

### RBAC 模型

```
User (z_user) ──[z_user_role]──→ Role (z_role)
                                     │
                            [z_role_permission]
                                     ▼
Permission (z_permission) ──[z_permission_api]──→ Api (z_api)
```

- 菜单/接口权限通过 `@PreAuthorize("hasAuthority('module:entity:operation')")` 控制
- 前端通过 `v-permission` 指令控制按钮级可见性

---

## 统一响应体

```java
// com.kg.core.web.ResponseResult
public class ResponseResult<T> {
    private String code;        // "200"=成功, "500"=错误
    private String message;     // 消息描述
    private LocalDateTime timestamp;
    private T data;             // 数据载荷
}
```

- Controller 返回普通对象/DTO，`ResponseResultBodyAdvice` 自动包装
- 通过 `@IsResponseResult(false)` 可跳过包装
- 异常由 `GlobalExceptionHandler` 统一捕获并返回 `ResponseResult`

---

## 部署架构

### 打包结构

```
module/target/package-jar/
├── app.jar          # 业务代码（不含配置文件）
├── config/          # application.yml、*.ignore、*.properties
└── lib/             # 所有依赖 JAR
```

- `maven-jar-plugin` 排除 `.yml`/`.properties`/`.ignore` 到 `config/` 目录
- Manifest 的 `Class-Path` 引用 `lib/` 和 `./config/`
- **优势：** 修改配置无需重新打包 JAR

### Docker Compose 架构

```
openresty (80:80)  →  springboot (8123:8123)  →  mariadb (3306)
                                               →  redis (6379)
```

- `dashu-net` 网络内通过 Docker DNS 通信（hostname: `mariadb` / `redis`）

---

## 前端架构

### Vue3（推荐，web-vue3/）

```
框架: Vue 3.5 + Composition API + TypeScript
UI: Element Plus 2.11 + UnoCSS + Less
状态: Pinia 3 + pinia-plugin-persistedstate
路由: Hash 模式，后端驱动动态路由
HTTP: Axios + RSA 加密 + Token 自动刷新
构建: Vite 6
布局: classic / topLeft / top / cutMenu 四种
权限: v-permission 指令 + 动态路由生成
```

### Vue2（旧版，web-vue2/）

```
框架: Vue 2.7 + Options API + JavaScript
UI: Element UI 2.15 + SCSS
状态: Vuex 3
路由: Hash 模式，后端驱动动态路由
HTTP: Axios + RSA 加密 + Token 自动刷新
构建: Vue CLI 4 + Webpack
布局: leftMenu / topMenu / topLeftMenu 三种
权限: v-permission 指令 + 动态路由生成
```

---

## 模块划分原则

- **业务模块**按业务域划分（`user`、`role`、`permission`、`dict`、`config` 等）
- **每个业务模块**有自己独立的 `entity`、`mapper`、`service`、`dto`、`convert`、`controller`、`excel` 包
- **Controller 统一前缀：** `@RequestMapping("/模块名/实体名")`
- **核心框架代码**放在 `core` 模块（安全、权限、日志、字典、组织）
- **通用工具/组件**放在 `component` 模块（JWT、文件、短信、验证码、Swagger）
