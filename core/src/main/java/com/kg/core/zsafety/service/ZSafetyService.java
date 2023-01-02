package com.kg.core.zsafety.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zsafety.entity.ZSafety;

/**
 * <p>
 * 密码安全等设置 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-12-30
 */
public interface ZSafetyService extends IService<ZSafety> {

    /**
     * 获取密码安全配置信息
     */
    ZSafety getSafety();

    /**
     * 清空安全配置缓存
     */
    void clearCache();
}
