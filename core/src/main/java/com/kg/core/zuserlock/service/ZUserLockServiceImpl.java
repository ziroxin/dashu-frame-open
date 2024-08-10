package com.kg.core.zuserlock.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.common.constant.CacheConstant;
import com.kg.core.zsafety.entity.ZSafety;
import com.kg.core.zsafety.service.ZSafetyService;
import com.kg.core.zuserlock.entity.ZUserLock;
import com.kg.core.zuserlock.mapper.ZUserLockMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户锁定 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-01-05
 */
@Service
public class ZUserLockServiceImpl extends ServiceImpl<ZUserLockMapper, ZUserLock> implements ZUserLockService {
    @Resource
    private ZSafetyService safetyService;
    @Resource
    private RedisUtils redisUtils;

    /**
     * 判断用户是否锁定
     *
     * @param userName 用户名
     * @return 锁定的用户信息（未锁定，返回null）
     */
    @Override
    public ZUserLock isLocking(String userName) {
        // 检查锁定配置
        ZSafety safety = safetyService.getSafety();
        if (safety.getLoginFailedTimes() <= 0) {
            // 配置不锁定
            return null;
        }
        // 查询Redis缓存中，是否有锁定信息
        if (redisUtils.hasKey(CacheConstant.USER_LOCK_REDIS_PRE + userName)) {
            return JSONUtil.toBean(redisUtils.get(CacheConstant.USER_LOCK_REDIS_PRE + userName).toString(), ZUserLock.class);
        }
        // 查询数据库中是否有锁定信息
        Optional<ZUserLock> lockUser = lambdaQuery().eq(ZUserLock::getUserName, userName).last("LIMIT 1").oneOpt();
        if (lockUser.isPresent()) {
            return lockUser.get();
        }
        return null;
    }

    /**
     * 记录错误锁定信息
     *
     * @param userName 用户名
     * @return 锁定提示信息（不锁定时返回空）
     */
    @Override
    public String loginError(String userName) {
        // 检查锁定配置
        ZSafety safety = safetyService.getSafety();
        if (safety.getLoginFailedTimes() <= 0) {
            // 配置不锁定，返回空
            return "";
        }
        // 错误次数
        int count = 1;
        String errorUserKey = CacheConstant.LOGIN_ERROR_COUNT_REDIS_PRE + userName;
        if (redisUtils.hasKey(errorUserKey)) {
            // 最新错误次数
            count = Integer.parseInt(redisUtils.get(errorUserKey).toString()) + 1;
        }
        // 登录错误次数
        redisUtils.set(errorUserKey, count,
                // 只记录到当前的晚上
                DateUtil.parse(DateUtil.format(new Date(), "yyyy/MM/dd 23:59:59")));
        // 判断是否锁定
        if (count >= safety.getLoginFailedTimes()) {
            // 锁定
            ZUserLock lock = new ZUserLock();
            lock.setLockId(GuidUtils.getUuid());
            lock.setUserName(userName);
            lock.setCreateTime(LocalDateTime.now());
            // 存入锁定记录
            if (safety.getLockTime() > 0) {
                lock.setLockReason("该账号登录错误次数过多，已被锁定！！！锁定时间【" +
                        LocalDateTimeUtil.format(lock.getCreateTime(), "yyyy-MM-dd HH:mm:ss") + "】，锁定时长【" +
                        safety.getLockTime() + "】分钟");
                // 缓存定时锁定（到期自动解锁）
                String lockUser = CacheConstant.USER_LOCK_REDIS_PRE + userName;
                redisUtils.set(lockUser, lock.toString(), safety.getLockTime() * 60L);
                // 记录锁定用户列表
                if (redisUtils.hasKey(CacheConstant.USER_LOCKED_LIST_REDIS_KEY)) {
                    redisUtils.setNoTimeLimit(CacheConstant.USER_LOCKED_LIST_REDIS_KEY,
                            redisUtils.get(CacheConstant.USER_LOCKED_LIST_REDIS_KEY).toString() + "," + lockUser);
                } else {
                    redisUtils.setNoTimeLimit(CacheConstant.USER_LOCKED_LIST_REDIS_KEY, lockUser);
                }
            } else {
                lock.setLockReason("该账号登录错误次数过多，已被永久锁定！！！如需解锁，请联系管理员！");
                // 数据库永久锁定（需手动解锁）
                save(lock);
            }
            // 锁定后清空错误次数
            redisUtils.delete(errorUserKey);
            return lock.getLockReason();
        } else {
            // 锁定次数倒计时
            return "用户名或密码错误！！！注意：错误" + safety.getLoginFailedTimes() + "次将被锁定，您还有"
                    + (safety.getLoginFailedTimes() - count) + "次机会！";
        }
    }

    /**
     * 获取缓存中的，锁定用户列表
     *
     * @return 锁定用户列表
     */
    @Override
    public List<ZUserLock> getCacheList() {
        List<ZUserLock> result = new ArrayList<>();
        // 是否有锁定的用户
        if (redisUtils.hasKey(CacheConstant.USER_LOCKED_LIST_REDIS_KEY)) {
            String users = redisUtils.get(CacheConstant.USER_LOCKED_LIST_REDIS_KEY).toString();
            if (StringUtils.hasText(users)) {
                List<String> newUserList = new ArrayList<>();
                String[] userList = users.split(",");
                // 循环处理已锁定用户列表
                for (String userName : userList) {
                    // 有效的用户key
                    if (redisUtils.hasKey(userName)) {
                        // 转换成list
                        result.add(JSONUtil.toBean(redisUtils.get(userName).toString(), ZUserLock.class));
                        newUserList.add(userName);
                    }
                }
                // 把新列表存入redis
                redisUtils.setNoTimeLimit(CacheConstant.USER_LOCKED_LIST_REDIS_KEY, newUserList.stream().collect(Collectors.joining(",")));
            }
        }
        return result;
    }

    /**
     * 解除用户锁定
     */
    @Override
    public void unlock(String[] userNames) {
        for (String unlockUserName : userNames) {
            // 删除数据库锁定信息
            QueryWrapper<ZUserLock> wrapper = new QueryWrapper<>();
            wrapper.lambda().eq(ZUserLock::getUserName, unlockUserName);
            remove(wrapper);
            // 删除缓存锁定信息
            String unlockUserNameRedis = CacheConstant.USER_LOCK_REDIS_PRE + unlockUserName;
            redisUtils.delete(unlockUserNameRedis);
            // 从缓存列表中移除
            if (redisUtils.hasKey(CacheConstant.USER_LOCKED_LIST_REDIS_KEY)) {
                String users = redisUtils.get(CacheConstant.USER_LOCKED_LIST_REDIS_KEY).toString();
                String newUsers = Arrays.stream(users.split(","))
                        .filter(str -> redisUtils.hasKey(str) && !str.equals(unlockUserNameRedis))
                        .collect(Collectors.joining(","));
                redisUtils.set(CacheConstant.USER_LOCKED_LIST_REDIS_KEY, newUsers);
            }
        }
    }
}
