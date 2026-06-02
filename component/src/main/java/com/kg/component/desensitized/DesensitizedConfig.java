package com.kg.component.desensitized;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 脱敏规则配置文件
 *
 * @author ziro
 * @date 2026-06-02 15:08:05
 */
@Getter
@Setter
@Configuration
public class DesensitizedConfig {
    /** 中文名：默认保留前0位和后1位 */
    @Value("${com.kg.desensitized.chineseName:0,1}")
    private String chineseName;

    /** 身份证号：默认保留前6位和后4位 */
    @Value("${com.kg.desensitized.idCard:6,4}")
    private String idCard;

    /** 手机号：默认保留前3位和后4位 */
    @Value("${com.kg.desensitized.mobilePhone:3,4}")
    private String mobilePhone;

    /** 座机号：默认保留前3位和后2位 */
    @Value("${com.kg.desensitized.fixedPhone:3,2}")
    private String fixedPhone;

    /** 银行卡号：默认保留前4位和后4位 */
    @Value("${com.kg.desensitized.bankCard:4,4}")
    private String bankCard;

    /** 车牌号：默认保留前2位和后3位 */
    @Value("${com.kg.desensitized.carLicense:2,3}")
    private String carLicense;

    /** 密码：默认显示前1位和后1位 */
    @Value("${com.kg.desensitized.password:1,1}")
    private String password;

    /** 其他：默认显示前2位和后2位 */
    @Value("${com.kg.desensitized.other:2,2}")
    private String other;
}
