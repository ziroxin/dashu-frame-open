package com.kg.core.zuserlock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zuserlock.entity.ZUserLock;

import java.util.List;

/**
 * <p>
 * 用户锁定 服务类
 * </p>
 *
 * @author ziro
 * @since 2023-01-05
 */
public interface ZUserLockService extends IService<ZUserLock> {

    /**
     * 判断用户是否锁定
     *
     * @param userName 用户名
     * @return 锁定的用户信息（未锁定，返回null）
     */
    ZUserLock isLocking(String userName);

    /**
     * 记录错误锁定信息
     *
     * @param userName 用户名
     * @return 锁定提示信息（不锁定时返回空）
     */
    String loginError(String userName);

    /**
     * 获取缓存中的，锁定用户列表
     *
     * @return 锁定用户列表
     */
    List<ZUserLock> getCacheList();

    /**
     * 解除用户锁定
     */
    void unlock(String[] userNames);
}
