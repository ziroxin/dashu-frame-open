# db-query 模块维护文档

> 面向后续维护者，说明「数据库查询工具」的用途、开关、接口、安全设计、前后端行为与常见坑。
> 最后更新：2026-11-11（写模式 + 每库历史 + CSV 导出 + 前端模式预校验 完成后）

---

## 1. 用途与边界

- **场景**：开发/运维在 **VPN + 堡垒机** 环境下，本地无法直连生产 MySQL 时，通过已部署的后端应用远程做**只读排障查询**；开发管理员可在受控前提下做**少量写操作**（INSERT/UPDATE/DELETE）。
- **不是**通用数据库管理面板。查询默认只读、限行数、限超时；写操作强门控 + 预览 + 二次确认 + 审计。
- **明确否决的技术方案**：不引入 PHP 工具、不做 SSH 隧道转发——一切经由本应用自身的 `/db/query/**` 接口。
- **双前端版本**：`web-vue3`（Vue3 + Element Plus + TS）与 `web-vue2`（Vue2 + Element UI + JS）功能等价，**任何改动必须两端同步**。

---

## 2. 文件地图

### 后端（`module/src/main/java/com/kg/module/dbQuery/`）

| 路径 | 职责 |
|------|------|
| `config/DbQueryProperties.java` | 配置属性（前缀 `com.kg.db-query`） |
| `config/DbQueryEnabled.java` | 条件注解 `@DbQueryEnabled` |
| `config/DbQueryEnabledCondition.java` | 启动期条件：读 `com.kg.db-query.enabled`，仅显式 `false` 才禁用 |
| `controller/DbQueryController.java` | 全部 REST 接口（类上带 `@DbQueryEnabled`） |
| `service/DbQueryService(Impl)` | 只读查询、库/表/结构、CSV 导出、状态探测、只读校验 |
| `service/DbWriteService(Impl)` | 写预览 / 写执行 / 快照 token / 审计 / 门控 |
| `service/DbWriteSqlParser.java` | 基于 JSqlParser 的写 SQL 解析与白/黑名单校验 |
| `entity/DbWriteAudit.java` | 写操作审计实体（表 `z_db_write_audit`） |
| `mapper/DbWriteAuditMapper.java` | 审计 Mapper |
| `dto/*` | 请求/响应 DTO（含 `DbWriteSnapshot` 快照、`DbWriteSqlInfo` 解析结果） |

### 前端

| 版本 | 页面 | API |
|------|------|-----|
| Vue3 | `web-vue3/src/views/dbQuery/index.vue` | `web-vue3/src/api/dbQuery/index.ts` |
| Vue2 | `web-vue2/src/views/dbQuery/index.vue` | `web-vue2/src/api/dbQuery.js` |

菜单/路由由数据库菜单表驱动（非前端静态路由），页面权限与写权限 SQL 预填脚本在 `sql/`（**已 gitignore**，不在仓库内）。

---

## 3. 开关与生效方式（重要）

| 配置键（`com.kg.db-query.*`） | 作用 | 生效时机 |
|------|------|----------|
| `enabled` | **模块总开关**。`false` → 整个控制器/服务 Bean 不注册，`/db/query/**` 全部**不存在** | **启动期**，改动需**重启后端** |
| `write-enabled` | 是否开放写模式 | 启动期 |
| `read-only` | 查询接口是否强制只读（白名单 + 危险词 + 禁堆叠） | 启动期 |
| `com.kg.developer-user-ids`（独立配置，非本前缀） | 开发管理员用户 ID 列表，写模式人员白名单 | 启动期 |

- 条件判定结果缓存在 `DbQueryEnabledCondition` 的 `static` 字段里，**不是热开关**。
- `DbQueryStatusDTO.enabled` 恒为 `true`——因为能进 `getStatus()` 就说明 Bean 已注册。**「关闭」完全靠 404 表达**（见 §7.1）。

---

## 4. 配置参考（`module/src/main/resources/application.yml`）

```yaml
com:
  db-query:
    enabled: false            # 总开关：false 时接口全 404
    write-enabled: true       # 是否允许写操作
    url: jdbc:mysql://<host>:<port>/<db>?...&allowMultiQueries=false
    username: <只读/可写账号>
    password: <见 application.yml，勿写入文档/提交>
    read-only: true           # 查询接口只读约束
    default-database: <库名>
    default-max-rows: 1000
    default-timeout: 30
    max-affected-rows: 5000   # 写执行允许的最大影响行数，超过回滚
    snapshot-ttl-seconds: 300 # 预览快照有效期，二次确认须在此时间内完成
```

> 安全提示：`allowMultiQueries=false` 是防堆叠注入的连接层兜底，**不要改成 true**。

---

## 5. 接口一览（前缀 `/db/query`）

| 方法 | 路径 | 权限门控 | 说明 |
|------|------|----------|------|
| GET | `/status` | 登录 | 前端探测：`enabled`/`writeEnabled`/`readOnly`/`developer` |
| GET | `/databases` | 登录 | 库列表 + 每库表数量 |
| GET | `/tables` | 登录 | 表列表（参数 `databaseName` 可选） |
| GET | `/table/structure` | 登录 | 单表结构（字段/注释，供「查询所有字段」「快捷填充」用） |
| POST | `/execute` | 登录 | 执行只读查询（受 `read-only` 校验） |
| POST | `/export/csv` | 登录 | 结果为 CSV（`responseType=blob`，含 UTF-8 BOM） |
| POST | `/write/preview` | `hasAuthority('db-query-write-preview')` | 解析校验 + 影响行数/原始行预览 + 一次性 token（存 Redis） |
| POST | `/write/execute` | `hasAuthority('db-query-write-execute')` | 校验 token 一致性后事务执行，做行数保护与审计 |

写接口额外受 `@AutoOperateLog` 操作日志注解覆盖。人员是否可用写模式 = **权限 + `write-enabled` + developer 白名单** 三者共同决定。

---

## 6. 安全设计

### 6.1 连接层（两个 Service 的 `getConnection` 一致）

- JDBC URL **只来自配置**，绝不用用户输入拼接 → 杜绝 `autoDeserialize`/`queryInterceptors` 等 URL 参数注入。
- 切库统一走 `conn.setCatalog(name)`，库名先过标识符白名单 `^[A-Za-z0-9_$.-]+$` 且禁止 `..`，非法直接拒绝。
- 查询连接 `setReadOnly(true)`（当 `read-only=true`）。

### 6.2 只读查询校验（`checkDangerousSql`）

1. 语句首词白名单：`SELECT / SHOW / DESC / DESCRIBE / EXPLAIN`。
2. 禁止堆叠：去尾分号后中间仍有 `;` 即拒绝。
3. 危险词黑名单：`DROP/TRUNCATE/ALTER/CREATE/GRANT/REVOKE/LOCK/UNLOCK/SHUTDOWN/SET GLOBAL/OUTFILE/DUMPFILE/LOAD_FILE/LOAD DATA/EXECUTE/SLEEP(/BENCHMARK(`。

### 6.3 写 SQL 校验（`DbWriteSqlParser`，JSqlParser AST）

- 只放行**单条** `INSERT/UPDATE/DELETE`；`Select` → 提示走查询接口；其他类型拒绝。
- 禁止堆叠、通用危险词黑名单（含 `RENAME`）。
- `UPDATE/DELETE` **必须带 WHERE**；拒绝 `JOIN` / 多表（SET 前有逗号）/ `USING`。
- `INSERT` 拒绝含 `SELECT`（`INSERT...SELECT` / 子查询，行数不可预览）。
- 预览 `countSql`/`backupSql` 用**反引号重建**的限定表名拼接，表名/别名经白名单校验。

### 6.4 写执行防护（`DbWriteServiceImpl.executeWrite`）

- 门控 `requireDeveloper()`：`write-enabled=true` 且当前用户在 developer 白名单。
- 一次性 token 存 Redis（key `db-write:snapshot:<token>`，TTL=`snapshot-ttl-seconds`），绑定**用户 + SQL 的 MD5**。
- 执行时：token 不存在→过期；用户不符 / SQL hash 不符→拒绝；再解析一次（纵深防御）。
- 事务执行 + 行数保护：`actual > maxAffectedRows` 或 `actual > 预览行数` → **回滚**（数据在预览后已变化时不盲写）。
- 成功即删除 token，防重放。
- 全程写审计（成功/失败/回滚都记）。

### 6.5 写流程时序

```
写模式编辑器 →[前端模式预校验]→ POST /write/preview → 解析校验 + 只读统计/备份 + 发 token(Redis)
      → 前端预览弹窗（影响行数 + 原始行）
      → 用户点“执行” → ElMessageBox 二次确认 → POST /write/execute {token,sql}
      → 校验 token/用户/SQL 一致性 → 事务执行 → 行数保护 → 审计 → 删 token
```

---

## 7. 前端行为要点

### 7.1 功能探测与关闭态
`loadStatus()`（两端）拉 `/status`：
- 正常 200：按 `enabled/writeEnabled/developer` 决定写模式是否可用（`canWrite = !featureDisabled && writeEnabled && developer`）。
- **404（模块关闭）**：走 `catch` → `featureDisabled=true` → 页面显示「功能已关闭」提示。→ 这是**设计如此**，不是 bug；代价是 Network 面板会留一条 404。如需消除，可把 `/status` 拆到不带 `@DbQueryEnabled` 的轻量控制器返回 `enabled:false`（会永久暴露一个状态接口，权衡后当前**保持 404 方案**）。

### 7.2 模式与校验
- 查询/写操作单选一档（`query`/`write`），选中态颜色区分（查询蓝 / 写橙）。
- `runMain` 执行前调用 `validateByMode(sql, mode)` 做**前端预校验**（镜像后端白名单）：明显模式不符（如在查询模式写 UPDATE、写模式跑 SELECT、UPDATE/DELETE 缺 WHERE、堆叠语句）直接 `warning` 拦截、不发请求。**非权威**——危险词、JOIN/多表、AST 解析仍由后端判定。
- 切换查询/写操作**不清空**输入框（便于把 SELECT 改写成 DELETE 后切模式；模式不符会被预校验拦下）。

### 7.3 其它交互
- 最近查询历史：按库分别存 localStorage（key `db-query:history`，`historyMap[db]=[{sql,ts,success}]`，去重、上限 50，可模糊筛选、可「清空全部」并二次确认）。列表带从 1 开始的索引。
- 结果导出 CSV 放在「查询结果 · N 行 · Nms」同行右侧；导出走 blob，两端 request 拦截器对 `responseType==='blob'` **直接返回 Blob**，调用方不能再包一层 `new Blob([res.data])`。
- 常用按钮第二行（表名 + 字段名 token）默认折叠一行；**仅当内容真溢出多行时才显示「展开/收起」按钮**（`measureTokenLine` 用 `scrollHeight` 判断）。
- 左侧树：只显示表名（超长省略号），tooltip 换行显示「表名 + 注释」，`placement="right"`；`.tree-node` 用 `align-self:stretch` 撑满整行高度，消除行上下无法触发 tooltip 的死区。
- SQL 输入框右上角的「清空」是**手写覆盖层图标**（`type=textarea` 的 `clearable` 在 Element Plus / Element UI **均不生效**，清除图标只在非 textarea 分支渲染），点击置空 `sqlText`。

### 7.4 组件差异（移植时注意）
| 能力 | Vue3 | Vue2 |
|------|------|------|
| 单选按钮值 | `el-radio-button value=` | `el-radio-button label=` |
| 多选选中态 CSS 类 | `.el-radio-button__original-radio:checked` | `.el-radio-button__orig-radio:checked` |
| 树节点插槽 | `#default="{ data }"` | `slot-scope="{ data }"` |
| tooltip 内容 | `<template #content>` | `<div slot="content">` |
| scoped 深度选择器 | `:deep()` | `::v-deep` |
| 图标 | `<my-icon icon="el-icon-xxx"/>`（本地 svg 精灵） | `<i class="el-icon-xxx">`（字体图标） |
| el-tag 原生点击 | `@click` | `@click.native` |
| 键盘监听原生 | `@keydown.ctrl.enter` | `@keydown.ctrl.enter.native` |

> Vue3 的 `my-icon` 走本地 svg 精灵（`src/assets/svgs`，symbolId `icon-[name]`，含 `el-icon-*` 系列）；**未安装** `@element-plus/icons-vue`，不要 import 它。

---

## 8. 审计表 `z_db_write_audit`

| 字段 | 说明 |
|------|------|
| `audit_id` | 主键（UUID） |
| `user_id` / `username` | 操作用户 |
| `database_name` | 目标库 |
| `sql_type` | INSERT/UPDATE/DELETE |
| `sql_text` | 执行 SQL（截断 5000） |
| `preview_rows` / `affected_rows` | 预览行数 / 实际影响行数 |
| `success` / `message` | 是否成功 / 结果或错误 |
| `snapshot_token` | 关联预览 token |
| `create_time` | 时间 |

建表 SQL 在 `sql/`（gitignore），部署新环境时需单独执行。

---

## 9. 权限

- 后端 `@PreAuthorize` 硬门控写接口：`db-query-write-preview`、`db-query-write-execute`。
- 仅「有权限」**不够**——运行时还要 `write-enabled=true` + 用户在 `com.kg.developer-user-ids` 白名单（双重门控）。
- 菜单/页面权限与上述权限码的授予关系由 `sql/` 预填脚本维护（gitignore）。

---

## 10. 维护者速查（常见坑）

1. **改配置不生效** → 本模块全部是**启动期**条件，必须重启后端。
2. **关闭后 status 404** → 预期行为，前端已降级为「功能已关闭」页。
3. **只改了一端前端** → 必须 Vue3 / Vue2 **成对修改**并各自 lint：
   `cd web-vue3 && npx eslint src/views/dbQuery/index.vue`
   `cd web-vue2 && npx eslint --ext .vue src/views/dbQuery/index.vue`
4. **CSV 下载得到 `undefined`** → 别对 blob 响应再 `new Blob([res.data])`；拦截器已返回 Blob。后端导出只用 `getOutputStream()`，**不要混用 `getWriter()`**（会 `IllegalStateException`）。
5. **textarea 加了 `clearable` 没反应** → Element 两端 textarea 都不支持，用覆盖层方案。
6. **写执行被回滚** → 正常保护：影响行数超 `max-affected-rows`，或实际行数 > 预览行数（预览后数据变了）。细化 WHERE 或调大配置（慎用）。
7. **新环境写接口报权限错** → 检查 `z_db_write_audit` 是否建表、`sql/` 权限码是否已授予、developer 白名单是否包含该用户。

---

## 11. 扩展指引

- 新增只读接口：加在 `DbQueryController`（类已在 `@DbQueryEnabled` 下），走 `DbQueryService`；前端 `api/dbQuery`（两端）+ 页面各加一个方法。
- 新增写能力：一律经 `DbWriteSqlParser` 校验 + `previewWrite`→token→`executeWrite` 流程 + 审计，**不要绕过 token 一致性校验**。
- 放宽只读白名单/黑名单时，同步更新前端 `validateByMode`（两端）的关键词，保持体验一致。
