# Dashu Frame (大树快速开发平台) — Agent Guide 

## Project structure

Multi-module Maven monorepo (`pom.xml` at root). Three Maven modules, two frontend sub-projects, and a CI/deploy module.

| Path | Role |
|------|------|
| `component/` | Library module — utilities, file handling, JWT, captcha, OSS, SMS, Swagger, MyBatis-Plus base. Artifact: `dashu-frame-component` |
| `core/` | Business core — security (Spring Security + JWT), permission/RBAC, DDoS, XSS, MyBatis-Plus extensions, Quartz. Artifact: `dashu-frame-core` |
| `module/` | Deployable Spring Boot app — entrypoint, controllers, MyBatis XML mappers, config. Produces `app.jar` + `lib/` + `config/` |
| `web-vue3/` | Modern frontend — Vue 3 + Element Plus + TypeScript + Vite. Package manager: **pnpm** |
| `web-vue2/` | Legacy frontend — Vue 2 + Element UI + JavaScript + Webpack. Package manager: **npm** |
| `ci/` | Docker deployment (docker-compose with openresty/springboot/mariadb/redis) + Node.js SSH deploy scripts |
| `sql/` | Permission-prefill SQL (gitignored) |

Dependency chain: `component` ← `core` ← `module` (app).

## Quick commands

### Backend (Maven + Java 8)

```bash
# Build deployable app (module only):
mvn clean package -DskipTests -pl module

# Full build all modules:
mvn clean install -DskipTests

# Run single test:
mvn test -pl module -Dtest=RabbitmqTest

# Run app locally:
#   config: module/src/main/resources/application.yml (port 8125, MySQL 39.107.157.91:13306)
#   entry: com.kg.DashuApplication
```

### Frontend

```bash
# Vue3 (pnpm required, node >= 18.20.7):
cd web-vue3 && pnpm install && pnpm run dev

# Vue2 (npm):
cd web-vue2 && npm install && npm run dev
npm run build:prod    # production build
```

### Docker deploy

```bash
# Initial setup (one-time):
cd ci && npm i
node 1.ci-docker-install.js
node 2.ci-docker-start.js
node 3.ci-mysql-restart.js

# Update backend:
mvn clean package -DskipTests -pl module
node ci-java.js

# Update Vue3 frontend:
cd web-vue3 && pnpm run build
cd ../ci && node ci-vue3.js
```

## Architecture & conventions

- **Entrypoint**: `module/src/main/java/com/kg/DashuApplication.java` — `@SpringBootApplication(scanBasePackages = "com.kg.**")` + `@MapperScan("com.kg.**.mapper")`
- **Java target**: 1.8 (source + target)
- **Version**: `currentVersion` property in root POM (currently 1.3.10). All inter-module deps use this property.
- **Package naming**: `com.kg.module.*`, `com.kg.core.*`, `com.kg.component.*` — but boot scan starts at `com.kg.**`, so placement matters.
- **Mapper XML**: loaded from `classpath*:/mapper/**/*.xml`; XML files live under `module/src/main/java/` (not resources), filtered into the jar at build time.
- **MyBatis XML hot reload**: enabled in dev (`application.yml`) via `directory-watcher`; disable in production.
- **MapStruct** (1.4.1.Final) used for DTO mapping in core module (annotation processor).
- **JWT + Spring Security**: custom security layer in `core/src/main/java/com/kg/core/security/` + `zuser/` + `zpermission/`
- **Swagger**: bundled via component module (springfox 2.6.1, not springdoc).
- **Config files**: `module/src/main/resources/application.yml` (dev), `ci/docker/springboot/config/application.yml` (docker). Also: `xss.ignore`, `security.ignore`, `fileTypeMap.properties`.
- **Quartz scheduler** + **RabbitMQ** (delayed messages) integrated in module.
- **Package layout**: `maven-jar-plugin` excludes `.yml`/`.properties`/`.ignore` from `app.jar`; these go to `config/` alongside `lib/` (dependencies). Manifest references `lib/` classpath + `./config/`. This means **config changes don't require rebuilding the jar**.

## Testing quirks

- **Test framework**: JUnit 5 (`@SpringBootTest`) — no Mockito used.
- **Style**: Integration tests only, with `System.out` output for manual validation (no assertions). Do not expect automated test verification.
- **Test location**: `module/src/test/java/` only.
- **Code generator**: `module/src/test/java/com/kg/generator/MybatisPlusGenerator.java` generates CRUD code + permission SQL from database tables.

## CI tips

- SSH deploy scripts in `ci/` use `node-ssh`. Configure `ci/config.js` before use.
- Docker Compose in `ci/docker/docker-compose.yml` uses a `dashu-net` network. The `springboot` service references `mariadb` and `redis` as hostnames (Docker DNS).
- Maven upload to public repo requires `settings.xml` mirror exclusion: `<mirrorOf>*,!sonatype-nexus-releases</mirrorOf>` — otherwise the sonatype repo is shadowed.

## Git

- **pnpm-lock.yaml** and **node_modules/** are gitignored.
- **sql/** (generated permission scripts) are gitignored.
- Branch strategy: `master` (full source) vs `quicker-*` (scaffold with core/component as JAR deps). Work on `master`.
