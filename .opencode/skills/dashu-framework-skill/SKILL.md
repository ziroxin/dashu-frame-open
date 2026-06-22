---
name: dashu-framework-skill
description: >-
  Comprehensive reference for the Dashu Framework (大树快速开发平台) — a Java Spring Boot multi-module (component/core/module) monorepo with Vue 3 / Vue 2 frontends.
  Use this skill whenever the user asks about: project architecture or module layout; code conventions (naming, CRUD patterns, annotations, exception handling);
  tech stack versions and dependencies; MyBatis-Plus ORM patterns; MapStruct DTO/Entity conversion; Spring Security + JWT authentication;
  RBAC permission model; uniform response body (ResponseResult); Quartz scheduling; RabbitMQ; MyBatis-Plus code generation workflows;
  adding new business modules; Docker deployment; or writing any Java/Vue code that must conform to framework conventions.
  Also trigger when the user references "dashu", "大树", "dashu-frame", "大树平台", or the project's code generator (MybatisPlusGenerator).
---

# Dashu Framework Master

This skill encodes the architecture, code conventions, tech stack, and code generation standards of the **Dashu Framework (大树快速开发平台)**.

## Project Overview

- **Backend**: Java 8, Spring Boot 2.6.7, multi-module Maven (component → core → module)
- **Frontend**: Vue 3 + Element Plus + TypeScript + Vite (recommended); Vue 2 + Element UI (legacy)
- **Deploy**: Docker Compose (OpenResty → Spring Boot → MariaDB + Redis)
- **Entry**: `com.kg.DashuApplication`, `scanBasePackages = "com.kg.**"`
- **Version**: `currentVersion = 1.3.10` in root POM

## Reference Documents

Read these for detailed guidance. Read only the section(s) relevant to the task at hand.

| File | Content | Read when... |
|------|---------|-------------|
| `references/01-architecture.md` | Module structure (component/core/module), 22+ sub-packages, layer responsibilities, auth flow, RBAC, deployment | user asks about architecture, module layout, where to place code, security flow, deployment |
| `references/02-code-conventions.md` | Naming rules, annotation specs, CRUD templates (Controller/Service), MapStruct converters, ResponseResult, exceptions, permissions, code generator | writing or reviewing code, adding new module, generating code |
| `references/03-tech-stack.md` | Full version table for all deps (backend 30+, Vue3/Vue2, Docker, CI) | user asks "what version of X", dependency questions |

## Code Generator

Two entry points (relative paths, same across all project scaffolds):

| Entry | Relative Path | Usage |
|-------|--------------|-------|
| **Batch offline** | `module/src/test/java/com/kg/generator/MybatisPlusGenerator.java` | Configure table list → run test → generates all files |
| **Online form** | `module/src/main/java/com/kg/core/formGenerator/controller/FormGeneratorController.java` | REST API (`POST /generator/code/byform`) → ZIP download |

## FTL Templates

This skill bundles copies of all 16 FreeMarker templates in the `templates/` directory. Use these when you need to match the exact generated code shape — Entity, DTO, Convert, Mapper, Service, Controller, Excel, Vue pages, permission SQL. In scaffold projects the source FTLs are bundled inside JARs and are not directly accessible as files.

## 10 Key Principles (Must Follow ALWAYS)

1. **Code generator first** — new modules start from generated code, then add custom logic
2. **Return DTOs, never Entities** — controllers return DTOs, auto-wrapped by `ResponseResultBodyAdvice`
3. **@NoRepeatSubmit on writes** — every add/update/delete/import endpoint must have it
4. **@PreAuthorize on every endpoint** — permission string format: `module:entity:operation`
5. **UUID primary keys** — `@TableId(type = IdType.ASSIGN_UUID)`, 32-char string
6. **Dynamic query via JSON `params`** — `JSONUtil.parseObj(params)` in Service layer
7. **MapStruct for DTO↔Entity** — `BaseConverter<E, DTO>` + `@Mapper(config = BaseConverterConfig.class)`
8. **Vue3 is default** — Composition API + `<script setup lang="ts">` + Pinia
9. **No SELECT \*** — always list columns explicitly in SQL
10. **Transactional on Service writes** — `@Transactional(rollbackFor = RuntimeException.class)`
