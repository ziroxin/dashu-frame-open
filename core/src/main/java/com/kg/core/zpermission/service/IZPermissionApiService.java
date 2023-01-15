package com.kg.core.zpermission.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kg.core.zpermission.dto.ZPermissionApiDTO;
import com.kg.core.zpermission.entity.ZPermissionApi;

/**
 * <p>
 * 资源API关联表 服务类
 * </p>
 *
 * @author ziro
 * @since 2022-05-21
 */
public interface IZPermissionApiService extends IService<ZPermissionApi> {

    /**
     * 保存资源和API关联关系
     */
    void savePermissionApi(ZPermissionApiDTO permissionApiDTO);
}
