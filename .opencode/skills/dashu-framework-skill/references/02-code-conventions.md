# 代码编写规范

## 命名规则

| 类型 | 规则 | 示例 |
|------|------|------|
| 类名 | 大驼峰 | `ZDictTypeController`, `ZUserService`, `BaseException` |
| 方法/变量 | 小驼峰 | `getUserById()`, `pagelist()`, `entityToDto()` |
| 常量 | 全大写下划线 | `MAX_RETRY_COUNT`, `ROLE_API_REDIS_KEY` |
| 枚举 | 大驼峰 | `ARITHMETIC`, `CHINESE_GIF` |
| 数据库表 | 下划线小写 | `z_dict_type`, `z_user_role` |
| 数据库字段 | 下划线小写 | `type_id`, `user_name` |

> 注意：项目中 Entity 字段名使用小驼峰（如 `typeId`），通过 `@TableField("type_id")` 映射到数据库。

---

## 类名后缀强制要求

| 后缀 | 说明 |
|------|------|
| `XxxController` | REST 控制器 |
| `XxxService` | Service 接口 |
| `XxxServiceImpl` | Service 实现 |
| `XxxMapper` | MyBatis-Plus Mapper 接口 |
| `XxxEntity` | 已废弃，新代码直接使用 `Xxx` 命名 Entity |
| `XxxDTO` | 数据传输对象 |
| `XxxConvert` | MapStruct 转换器 |
| `XxxExcelDTO` / `XxxExcelConstant` | Excel 导入导出 |

---

## 注解规范

### Controller 层

```java
@RestController
@RequestMapping("/dictType/zDictType")
@Api(tags = "ZDictTypeController", value = "字典类型", description = "字典类型")
@PreAuthorize("hasAuthority('dictType:zDictType:list')")  // 可选，类级别默认权限
```

### Service 层

```java
@Service
@Transactional(rollbackFor = RuntimeException.class)  // 事务注解
```

### 数据映射

```java
@TableName("z_dict_type")                    // 表名
@TableId(value = "type_id", type = IdType.ASSIGN_UUID)  // 主键（UUID）
@TableField("type_name")                     // 字段映射
@TableField(value = "create_time", fill = FieldFill.INSERT)  // 自动填充
```

### 字段校验

```java
@NotBlank(message = "字典名称不能为空")
@Size(max = 50, message = "字典名称长度不能超过50")
@ApiModelProperty("字典名称")
```

---

## Controller 标准 CRUD 模式

```java
@RestController
@RequestMapping("/module/entityName")
@Api(tags = "XxxController")
public class XxxController {

    @Resource private XxxService xxxService;
    @Resource private XxxConvert xxxConvert;

    @GetMapping("/getById")
    @PreAuthorize("hasAuthority('module:entity:getById')")
    public XxxDTO getById(String id) {
        return xxxConvert.entityToDto(xxxService.getById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('module:entity:list')")
    public Page<XxxDTO> list(@RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer limit,
                             @RequestParam(required = false) String params) {
        return xxxService.pagelist(page, limit, params);
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('module:entity:add')")
    @NoRepeatSubmit
    public void add(@RequestBody XxxDTO dto) throws BaseException {
        xxxService.add(dto);
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('module:entity:update')")
    @NoRepeatSubmit
    public void update(@RequestBody XxxDTO dto) throws BaseException {
        xxxService.update(dto);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('module:entity:delete')")
    @NoRepeatSubmit
    public void delete(@RequestBody String[] ids) throws BaseException {
        xxxService.delete(Arrays.asList(ids));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('module:entity:export:excel')")
    public String exportExcel(@RequestParam(required = false) String params) throws BaseException {
        return xxxService.exportExcel(params);
    }

    @PostMapping("/import/excel")
    @PreAuthorize("hasAuthority('module:entity:import:excel')")
    @NoRepeatSubmit
    public void importExcel(HttpServletRequest request) throws BaseException {
        xxxService.importExcel(request);
    }
}
```

---

## Service 标准 CRUD 模式

```java
public interface XxxService extends IService<XxxEntity> {
    Page<XxxDTO> pagelist(Integer page, Integer limit, String params);
    void add(XxxDTO dto) throws BaseException;
    void update(XxxDTO dto) throws BaseException;
    void delete(List<String> ids) throws BaseException;
    String exportExcel(String params);
    void importExcel(HttpServletRequest request);
}
```

```java
@Service
public class XxxServiceImpl extends ServiceImpl<XxxMapper, XxxEntity> implements XxxService {

    @Resource private XxxConvert xxxConvert;

    public Page<XxxDTO> pagelist(Integer page, Integer limit, String params) {
        Page<XxxEntity> pager = new Page<>(page, limit);
        QueryWrapper<XxxEntity> wrapper = new QueryWrapper<>();
        // 解析 JSON params 构建动态查询条件
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            // 按字段拼接条件...
        }
        Page<XxxEntity> pageEntity = page(pager, wrapper);
        // 转换为 DTO 分页
        Page<XxxDTO> result = new Page<>();
        result.setRecords(pageEntity.getRecords().stream()
            .map(e -> xxxConvert.entityToDto(e)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }
}
```

---

## MapStruct DTO/Entity 转换

```java
// BaseConverter 定义（core 模块）
@MapperConfig(componentModel = "spring", injectionStrategy = ConstructorInjectionStrategy.CONSTRUCTOR)
public interface BaseConverterConfig {}

public interface BaseConverter<E extends BaseEntity, DTO extends BaseDTO> {
    DTO entityToDto(E entity);
    E dtoToEntity(DTO dto);
    E updateEntityFromDto(DTO dto, @MappingTarget E entity);
    Page<DTO> pageableToDto(Page<E> page);
    IPage<DTO> mybatisPageToDto(IPage<E> page);
}
```

```java
// 实际使用
@Component
@Mapper(config = BaseConverterConfig.class)
public interface ZDictTypeConvert extends BaseConverter<ZDictType, ZDictTypeDTO> {
    // 继承所有方法，无需额外代码
}
```

---

## 统一返回体

```java
// com.kg.core.web.ResponseResult
ResponseResult.success()              // code="200", message="成功"
ResponseResult.success(data)          // code="200", data=data
ResponseResult.error()                // code="500", message="错误"
ResponseResult.error("自定义消息")     // code="500", message="自定义消息"
```

Controller 直接返回 DTO（不用手动包装）：
```java
@GetMapping("/getById")
public ZDictTypeDTO getById(String typeId) {
    return xxxConvert.entityToDto(xxxService.getById(typeId));
}
```

`ResponseResultBodyAdvice` 自动包装返回值为 `ResponseResult.success(body)`。

若需跳过包装，使用 `@IsResponseResult(false)`。

---

## 异常处理

### 业务异常

```java
throw new BaseException("字典Code已存在");             // 自定义消息，code=500
throw new BaseException(BaseErrorCode.LOGIN_ERROR_TOKEN_INVALID);  // 预定义错误码
```

### 错误码枚举（BaseErrorCode）

| Code | 含义 |
|------|------|
| `200` | 成功 |
| `500` | 服务器端错误 |
| `40001` | 用户名或者密码错误 |
| `40002` | 您未登录或者登录已过期！请重新登录 |
| `40003` | 用户未登录 |
| `40004` | 用户已禁用 |
| `401` | 无权限访问该资源 |

### 全局异常处理器（GlobalExceptionHandler）

统一捕获 `BaseException`、`BadCredentialsException`、`MaxUploadSizeExceededException`、参数校验异常等，返回 `ResponseResult`（HTTP 200 + 错误码）。

> 数据库异常会进行安全过滤（检测 SQL 语法错误、约束违反等关键字），防止敏感信息泄露。

---

## 权限控制

### 后端（方法级别）

```java
@PreAuthorize("hasAuthority('dictType:zDictType:add')")
// 格式: @PreAuthorize("hasAuthority('模块名:实体名:操作')")
```

### 后端（API 自动扫描）

- `ZApiServiceImpl.scanApiList()` 使用 `org.reflections` 库扫描所有 `@PreAuthorize` 注解
- 自动注册到 `z_api` 表，通过 `z_permission_api` 关联到权限

### 前端 Vue3 / Vue2

```html
<!-- v-permission 指令控制元素可见性 -->
<el-button v-permission="'user-add'" type="primary">添加用户</el-button>
```

---

## 防重复提交

```java
@PostMapping("/add")
@NoRepeatSubmit(lockSecond = 2, useParamFingerprint = true)  // 2秒内防重复
public void add(@RequestBody XxxDTO dto) throws BaseException { ... }
```

基于 Redis 的 SETNX 实现，Key 格式：`repeat_submit:<用户标识>:<路径>:<方法>:<参数指纹>`。

---

## 操作日志

```java
@PostMapping("/update")
@AutoOperateLog(logMethod = "system:user:update", logMsg = "修改用户信息")
public void update(@RequestBody XxxDTO dto) throws BaseException { ... }
```

自动记录：请求用户、IP、参数、请求体、时间。

---

## 实体/数据库规范

### 主键策略

```java
@TableId(value = "xxx_id", type = IdType.ASSIGN_UUID)
private String xxxId;
```

所有主键使用 32 位 UUID（`GuidUtils.getUuid32()`），type 字段统一使用 `ASSIGN_UUID`。

### 时间字段

```java
@TableField(value = "create_time", fill = FieldFill.INSERT)
private LocalDateTime createTime;

@TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
private LocalDateTime updateTime;
```

### 动态查询

Controller 接收 `params` 参数（JSON 字符串），Service 中用 Hutool `JSONUtil` 解析：

```java
if (StringUtils.hasText(params)) {
    JSONObject paramObj = JSONUtil.parseObj(params, true);
    wrapper.like(StringUtils.hasText(paramObj.getStr("typeName")),
                 ZDictType::getTypeName, paramObj.getStr("typeName"));
    wrapper.eq(StringUtils.hasText(paramObj.getStr("status")),
               ZDictType::getStatus, paramObj.getStr("status"));
}
```

### MyBatis XML

XML 文件位于 `module/src/main/resources/mapper/` 目录，通过 `classpath*:/mapper/**/*.xml` 加载。
大部分模块使用 MyBatis-Plus 基础 CRUD，XML 中只有 namespace 声明（或为空）。

---

## Excel 导入/导出

```java
// 导出
public String exportExcel(String params) {
    List<XxxDTO> list = ...;
    return ExcelWriteUtils.exportXlsx(response, "文件名", "标题", XxxExcelDTO.class, list);
}

// 导入
public void importExcel(HttpServletRequest request) {
    List<XxxDTO> list = ExcelReadUtils.importExcel(request, XxxExcelDTO.class);
    // 逐一处理业务逻辑
}
```

---

## 日志规范

```java
@Slf4j  // 必须使用 Lombok @Slf4j

// 关键入口打印
log.info("请求参数：{}", params);

// 异常处打印
log.error("业务异常：", e);
```

---

## 前端 Vue3 规范

### 代码风格

- 推荐使用 Composition API + `<script setup lang="ts">`
- API 调用直接使用 `request()` 工具函数
- 状态管理使用 Pinia + 持久化插件
- 路由为 Hash 模式，动态路由由后端生成

### 目录结构规范

```
src/views/moduleName/
├── index.vue           # CRUD 主页面
├── components/         # 子组件
│   ├── AddDialog.vue
│   └── EditDialog.vue
└── api.ts              # API 定义（可选，可直接在 .vue 中 request）
```

### 权限控制

```html
<el-button v-permission="'user-add'" type="primary">新增</el-button>
```

### API 调用

```typescript
import request from '@/utils/request'

// GET 请求
request({ url: '/user/list', method: 'get', params: { page: 1, limit: 10 } })

// POST 请求
request({ url: '/user/add', method: 'post', data: formData })
```

---

## 前端 Vue2 规范

- 使用 Options API（`export default { data(), methods: {} }`）
- 状态管理使用 Vuex（`namespaced: true`）
- API 定义集中在 `src/api/` 目录
- 字典使用 `vue-data-dict` 组件 + `VueDataDict` 插件

---

## SQL 规范

- 禁止使用 `SELECT *`，字段列表必须显式写出
- 动态 SQL 优先使用 MyBatis-Plus `QueryWrapper` / `LambdaQueryWrapper`
- 复杂查询使用 XML 的 `<if>`、`<foreach>` 等标签

---

## 代码生成规范

本项目的代码生成器是**开发的基础标准**，所有业务模块的 CRUD 代码应当基于代码生成器产出，再在此基础上进行二次开发。

### 模板文件一览

代码生成器 FTL 模板位于 `component/src/main/resources/templates/`，共 16 个模板文件，覆盖后端 Java 全部 8 层 + 前端 Vue2/Vue3 + 权限 SQL：

| 模板文件 | 生成内容 | 生成包/路径 |
|----------|----------|-------------|
| `entity.java.ftl` | 数据库实体类 | `{module}/src/main/java/{basePackage}/entity/` |
| `dto.java.ftl` | 数据传输对象 | `{module}/src/main/java/{basePackage}/dto/` |
| `dtoconvert.java.ftl` | MapStruct 转换器 | `{module}/src/main/java/{basePackage}/dto/convert/` |
| `mapper.java.ftl` | MyBatis-Plus Mapper 接口 | `{module}/src/main/java/{basePackage}/mapper/` |
| `mapper.xml.ftl` | Mapper XML 映射文件 | `{module}/src/main/resources/mapper/` |
| `service.java.ftl` | Service 接口 | `{module}/src/main/java/{basePackage}/service/` |
| `serviceImpl.java.ftl` | Service 实现类 | `{module}/src/main/java/{basePackage}/service/impl/` |
| `controller.java.ftl` | REST Controller | `{module}/src/main/java/{basePackage}/controller/` |
| `excelconstant.java.ftl` | Excel 导出导入字段常量 | `{module}/src/main/java/{basePackage}/excels/` |
| `excelout.java.ftl` | Excel 导出 DTO | `{module}/src/main/java/{basePackage}/excels/` |
| `excelimport.java.ftl` | Excel 导入 DTO | `{module}/src/main/java/{basePackage}/excels/` |
| `index.vue.ftl` | Vue2 列表页面 | `{vue2Folder}/src/views/{viewPath}/` |
| `deleteLogs.vue.ftl` | Vue2 删除日志页面 | `{vue2Folder}/src/views/{viewPath}/deleteLogs/` |
| `vue3Index.vue.ftl` | Vue3 列表页面 | `{vue3Folder}/src/views/{viewPath}/` |
| `vue3DeleteLogs.vue.ftl` | Vue3 删除日志页面 | `{vue3Folder}/src/views/{viewPath}/deleteLogs/` |
| `permission.sql.ftl` | 权限预填 SQL | `{basePath}/sql/` |

### 代码生成器入口

#### 方式一：测试类生成（批量离线生成）

**位置：** `module/src/test/java/com/kg/generator/MybatisPlusGenerator.java`

用于快速为多张表生成完整 CRUD 代码。通过 `@SpringBootTest` 连接数据库，配置表名、ID 类型、包名、前端视图路径等参数即可运行。

```java
@SpringBootTest
public class MybatisPlusGenerator {
    @Resource
    private GeneratorCodeUtils generatorCodeUtils;

    @Test
    public void generator() {
        LinkedList<String> tableNames = new LinkedList<>();
        LinkedList<IdType> idTypes = new LinkedList<>();
        LinkedList<String> packages = new LinkedList<>();
        LinkedList<String> viewPaths = new LinkedList<>();
        LinkedList<Boolean> hasDeleteLogs = new LinkedList<>();

        // 配置业务表（支持批量生成多个表）
        tableNames.add("z_xxx");                    // 表名
        idTypes.add(IdType.ASSIGN_UUID);            // 主键类型（统一 UUID）
        packages.add("module.xxx");                 // 包名（com.kg.module.xxx）
        viewPaths.add("xxx");                       // 前端视图路径（src/views/xxx/）
        hasDeleteLogs.add(false);                   // 是否生成删除日志表

        // 可选：配置子表（附件表）
        // childTableMap.put("z_xxx", ["z_xxx_file"]);

        generatorCodeUtils.start(
            "E:/generate/code",                     // 代码输出根路径
            "module",                               // Maven 模块名
            "com.kg",                               // 基础包名
            "ziro",                                 // 作者
            "web-vue2", "web-vue3",                 // Vue2/Vue3 目录名
            tableNames, idTypes, packages, viewPaths,
            null, null, hasDeleteLogs
        );
    }
}
```

#### 方式二：在线表单生成（Web 界面生成）

**位置：** `module/src/main/java/com/kg/core/formGenerator/controller/FormGeneratorController.java`

提供 REST API，通过前端界面在线配置表结构和字段，一键生成代码并打包为 ZIP 下载：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/generator/code/tableList` | GET | 查询数据库所有表列表 |
| `/generator/code/tableInfo` | GET | 查询指定表的字段信息 |
| `/generator/code/hasTables` | GET | 检查表是否存在 |
| `/generator/code/byform` | POST | 生成代码（可选择覆盖表结构） |

POST 请求体（`TableDTO`）核心配置：

```json
{
  "tableName": "z_xxx",
  "tableDecription": "业务表描述",
  "basePackage": "com.kg",
  "author": "ziro",
  "tablePackage": "module.xxx",
  "viewPath": "xxx",
  "isDeleteLogs": false,
  "isCoverTable": false,
  "searchFields": ["field1", "field2"],
  "listFields": ["field1", "field2"],
  "importFields": ["field1", "field2"],
  "exportFields": ["field1", "field2"],
  "fields": [
    { "name": "field_name", "type": "varchar", "length": 50, "required": true, "key": false, "title": "字段说明" }
  ]
}
```

### 生成器核心引擎

**位置：** `module/src/main/java/com/kg/core/formGenerator/utils/GeneratorCodeUtils.java`

核心方法 `start()` 遍历表配置列表，对每个表依次执行：

1. **构建 FastAutoGenerator**（封装自 `com.kg.component.generator`）配置数据源、全局策略、包名、模板路径
2. **配置 Entity 策略**：继承 `BaseEntity`、使用 `@TableName`、`IdType.ASSIGN_UUID`、自动填充 `createTime`/`updateTime`
3. **配置 DTO 策略**：实现 `BaseDTO`、Swagger 注解、`@JsonFormat`
4. **配置 Convert 策略**：继承 `BaseConverter<Entity, DTO>`、MapStruct `@Mapper(config = BaseConverterConfig.class)`
5. **配置 Service 策略**：继承 `IService<Entity>` + `ServiceImpl<Mapper, Entity>`，生成标准 CRUD 方法
6. **配置 Controller 策略**：`@RestController` + `@RequestMapping` + `@PreAuthorize` + CRUD 方法
7. **配置 Vue 策略**：生成搜索面板 + 表格 + 分页 + 新增/编辑/删除对话框
8. **配置 Excel 策略**：导出常量 + 导出 DTO + 导入 DTO
9. **生成权限 SQL**：菜单/按钮/API 权限预填脚本

### 生成的代码文件架构

以业务表 `z_xxx`、包名 `com.kg.module.xxx` 为例，生成的文件组织如下：

```
module/src/main/java/com/kg/module/xxx/
├── controller/
│   └── ZXxxController.java              # REST API
├── service/
│   ├── ZXxxService.java                 # Service 接口
│   └── impl/
│       └── ZXxxServiceImpl.java         # Service 实现
├── mapper/
│   └── ZXxxMapper.java                  # MyBatis-Plus Mapper
├── entity/
│   └── ZXxx.java                        # 数据库实体
├── dto/
│   ├── ZXxxDTO.java                     # 数据传输对象
│   └── convert/
│       └── ZXxxConvert.java             # MapStruct 转换器
└── excels/
    ├── ZXxxExcelConstant.java           # Excel 字段常量
    ├── ZXxxExcelOutDTO.java             # 导出 DTO
    └── ZXxxExcelImportDTO.java          # 导入 DTO

module/src/main/resources/mapper/
└── ZXxxMapper.xml                       # MyBatis XML

web-vue3/src/views/xxx/
├── index.vue                            # Vue3 列表页面
└── deleteLogs/
    └── index.vue                        # Vue3 删除日志页面（可选）

web-vue2/src/views/xxx/
├── index.vue                            # Vue2 列表页面
└── deleteLogs/
    └── index.vue                        # Vue2 删除日志页面（可选）

sql/
└── ZXxx-permission.sql                  # 权限预填 SQL
```

### 代码生成标准约定

生成器产出的代码遵循本项目**统一的标准架构**，所有业务模块应基于此产出进行开发：

1. **Controller** — `@RestController` + `@RequestMapping("/模块名/实体名")` + `@PreAuthorize` + 标准 6 接口（getById/list/add/update/delete/exportExcel/importExcel/downloadTemplate）
2. **Service** — 继承 `ServiceImpl<Mapper, Entity>`，JSON 参数动态查询，MapStruct 转换，`@Transactional`
3. **Mapper** — 继承 `BaseMapper<Entity>`，提供 `list(paramObj)` / `count(paramObj)` / `saveList()` 三个自定义方法
4. **Entity** — `@TableName` + `@TableId(type = IdType.ASSIGN_UUID)` + `@TableField` + 实现 `BaseEntity`
5. **DTO** — `@ApiModel` + `@ApiModelProperty` + `@JsonFormat` + 实现 `BaseDTO`
6. **Convert** — MapStruct `@Mapper(config = BaseConverterConfig.class)` + 继承 `BaseConverter<E, DTO>`
7. **Vue 页面** — 搜索面板 + 数据表格 + 分页 + 新增/编辑对话框（弹窗模式）+ 删除确认
8. **Excel** — 导出常量定义字段映射 + 导出 DTO + 导入 DTO + 必填校验 + 下载模板
9. **权限 SQL** — 按模块生成菜单、按钮、API 三级权限预填脚本

> 所有新业务功能应优先使用代码生成器生成基础 CRUD，然后在此基础上增加业务逻辑。生成器的 FTL 模板定义了代码的标准骨架，可在 `component/src/main/resources/templates/` 中按需修改以适配业务变化。
