---
name: quartz-job-setup
description: >-
  Step-by-step workflow for adding new Quartz scheduled jobs in the dashu framework.
  Covers: Job class creation, z_quartz table seed SQL, cron expression configuration,
  QuartzConfig auto-registration, and @DataScope null-safety guard.
  Use when: user asks to add a new timed task, scheduled job, cron job, or periodic task.
  Also trigger on: "定时任务", "定时调度", "Quartz", "cron", "周期任务", "自动处理".
---

# Quartz Job Setup Workflow

Step-by-step guide for adding new Quartz scheduled jobs in this project.

## How It Works

1. `z_quartz` table stores all job configurations (job name, class, cron, status)
2. `QuartzConfig.java` reads all `z_quartz` rows at startup, registers jobs with status=1
3. `ZQuartzController` provides CRUD + refresh endpoints for runtime management
4. `QuartzManagerUtils` (from component) handles the actual Quartz Scheduler registration

## Steps to Add a New Job

### Step 1 — Create the Job class

**Location:** `module/src/main/java/com/kg/core/quartz/job/YourJobName.java`

```java
package com.kg.core.quartz.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * <任务描述>
 * <p>
 * z_quartz 表中配置 cron 表达式后，由 Quartz 调度执行。
 * <执行逻辑简述>
 *
 * @author ziro
 * @date YYYY/MM/dd
 */
public class YourJobName implements Job {

    private static final Logger log = LoggerFactory.getLogger(YourJobName.class);

    @Resource
    private YourService yourService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("定时任务开始执行: YourJobName");
        try {
            // 业务逻辑
        } catch (Exception e) {
            log.error("定时任务执行异常", e);
        }
    }
}
```

**Key conventions:**
- Class implements `org.quartz.Job` (not Spring's `@Scheduled`)
- `@Resource` injection works because Quartz instantiates via `Class.forName()` but Spring's `QuartzManagerUtils.addJob()` registers the class, and Spring handles the instantiation
- Always wrap body in try-catch — uncaught exceptions kill the scheduler thread
- Always log entry and errors
- Use `LIMIT` on queries to prevent memory exhaustion on large tables
- Use `last("LIMIT 500")` or similar in MyBatis-Plus lambda queries

### Step 2 — Seed SQL for z_quartz table

**Location:** `sql/YourJobName-quartz.sql`

```sql
-- 定时任务: <任务名称>
-- Cron: <cron表达式说明>
-- 默认 status=0 (禁用), 部署后在前端启用
INSERT INTO `z_quartz` VALUES (
    '<UUID>',                      -- quartzId (用 GuidUtils.getUuid() 生成)
    '<任务名称>',                    -- jobName (显示名称)
    'com.kg.core.quartz.job.YourJobName',  -- jobClass (完整类名)
    '0 */10 * * * ?',              -- jobTimeCron (Quartz 6位 cron)
    '<任务描述>',                    -- description
    '0',                           -- status: 0=禁用, 1=启用
    NOW(),                         -- createTime
    NOW()                          -- updateTime
);
```

**Common cron patterns:**

| Pattern | Meaning |
|---------|---------|
| `0 */10 * * * ?` | Every 10 minutes |
| `0 */30 * * * ?` | Every 30 minutes |
| `0 0/1 * * * ?` | Every minute |
| `0 0 0 * * ?` | Daily at midnight |
| `0 0 1 * * ?` | Daily at 01:00 |
| `0 30 0 * * ?` | Daily at 00:30 |
| `0 0 */6 * * ?` | Every 6 hours |

**Important:** Quartz uses 6-field cron (with seconds). The 6th field is seconds.

### Step 3 — Verify QuartzConfig auto-registration

`QuartzConfig.java` at `core/src/main/java/com/kg/core/zquartz/config/QuartzConfig.java` automatically:
- Reads all `z_quartz` rows on startup
- Registers jobs with `status=1` via `QuartzManagerUtils.addJob()`
- Removes jobs when status changes to `0`

**No code changes needed in QuartzConfig** — just insert the row into `z_quartz`.

### Step 4 — Runtime management

After deployment, use the frontend or API to enable/disable:
- `GET /zquartz/zQuartz/refresh` — refresh all job states
- `POST /zquartz/zQuartz/update` — change cron or status
- `GET /zquartz/zQuartz/list` — list all jobs

## @DataScope Null-Safety Guard

If the Job class calls a Service whose class has `@DataScope` annotation, the `DataScopeAspect` will try to read `SecurityContextHolder.getContext().getAuthentication()`, which returns null in Quartz threads (no authenticated user).

**Required fix** — add null-safety in `CurrentUserUtils.java`:

```java
public static SecurityUserDetailEntity getSecurityUserDetailEntity() {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        Object principal = authentication.getPrincipal();
        if (principal == null) return null;
        if (principal instanceof SecurityUserDetailEntity) {
            return (SecurityUserDetailEntity) principal;
        }
        return null;
    } catch (Exception e) {
        return null;
    }
}
```

`DataScopeAspect` already handles `user == null` by skipping injection — this is correct behavior for scheduled tasks that need to query all data.

## Existing Jobs Reference

| Job Class | Cron | Status | Description |
|-----------|------|--------|-------------|
| `Job1` | `*/5 * * * * ?` | 0 | Demo job (every 5s) |
| `DeleteFileChunkTempJob` | `0 0 1 * * ?` | 0 | Delete expired file chunks daily at 1:00 |
| `DeleteOperateLogJob` | `0 30 0 * * ?` | 1 | Delete operate logs daily at 0:30 (180 days) |
| `OrderReceiptTimeoutJob` | `0 */10 * * * ?` | 1 | Auto-confirm receipt for overdue orders (every 10min) |
| `OrderReviewTimeoutJob` | `0 */30 * * * ?` | 1 | Auto-review completed orders (every 30min) |
| `AfterSalesTimeoutJob` | `0 */10 * * * ?` | 1 | Auto-handle overdue after-sales (every 10min) |
| `CardExpiryJob` | `0 0 0 * * ?` | 1 | Expire unused cards daily at midnight |
| `RefundStatusPollingJob` | `0 0/1 * * * ?` | 1 | Poll WeChat refund status (every 1min) |
| `GoodsOrderScoreJob` | (check z_quartz) | — | Goods order score calculation |
