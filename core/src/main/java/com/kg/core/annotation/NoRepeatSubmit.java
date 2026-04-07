package com.kg.core.annotation;

import java.lang.annotation.*;

/**
 * 防止重复提交
 *
 * @author ziro
 * @date 2023-01-07 11:05:29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface NoRepeatSubmit {
    /**
     * 防止重复锁定时间 默认2s不能重复提交
     */
    long lockSecond() default 2L;

    /**
     * 是否校验参数（默认：带参数校验）
     * <p>
     * 举例：@NoRepeatSubmit(useParamFingerprint = true)
     * POST /api/order {productId: 1} → key: repeat_submit:xxx:/api/order:POST:md5(params1)
     * POST /api/order {productId: 2} → key: repeat_submit:xxx:/api/order:POST:md5(params2)
     * 两者互不影响 ✅
     */
    boolean useParamFingerprint() default true;
}
