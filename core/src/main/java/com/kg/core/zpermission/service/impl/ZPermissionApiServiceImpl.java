package com.kg.core.zpermission.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.redis.RedisUtils;
import com.kg.core.common.constant.LoginConstant;
import com.kg.core.zpermission.dto.ZPermissionApiDTO;
import com.kg.core.zpermission.entity.ZPermissionApi;
import com.kg.core.zpermission.mapper.ZPermissionApiMapper;
import com.kg.core.zpermission.service.IZPermissionApiService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 资源API关联表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
@Service
public class ZPermissionApiServiceImpl extends ServiceImpl<ZPermissionApiMapper, ZPermissionApi> implements IZPermissionApiService {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 保存资源和API关联关系
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void savePermissionApi(ZPermissionApiDTO permissionApiDTO) {
        // 删除旧关联
        lambdaUpdate().eq(ZPermissionApi::getPermissionId, permissionApiDTO.getPermissionId()).remove();
        // 保存新关联
        List<ZPermissionApi> collect = Arrays.stream(permissionApiDTO.getApiIds()).map(apiId -> {
            ZPermissionApi permissionApi = new ZPermissionApi();
            permissionApi.setPermissionId(permissionApiDTO.getPermissionId());
            permissionApi.setApiId(apiId);
            return permissionApi;
        }).collect(Collectors.toList());
        saveBatch(collect);
        // 清除角色权限关联关系缓存
        redisUtils.delete(LoginConstant.ROLE_API_REDIS_KEY);
    }
}
