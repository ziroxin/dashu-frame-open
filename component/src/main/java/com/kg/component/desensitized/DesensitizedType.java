package com.kg.component.desensitized;

/**
 * 支持的脱敏类型枚举
 *
 * @author ziro
 * @date 2023-02-16 08:52:27
 */
public enum DesensitizedType {
    /**
     * 中文名
     */
    CHINESE_NAME,
    /**
     * 英文名
     */
    ENGLISH_NAME,
    /**
     * 身份证号
     */
    ID_CARD,
    /**
     * 手机号
     */
    MOBILE_PHONE,
    /**
     * 座机号
     */
    FIXED_PHONE,
    /**
     * 银行卡
     */
    BANK_CARD,
    /**
     * 中国大陆车牌，包含普通车辆、新能源车辆
     */
    CAR_LICENSE,
    /**
     * 电子邮件
     */
    EMAIL,
    /**
     * 地址
     */
    ADDRESS,
    /**
     * 密码
     */
    PASSWORD,
    /**
     * 其他
     */
    OTHER
}
