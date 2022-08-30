package com.kg.core.zapigroup.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.utils.GuidUtils;
import com.kg.core.zapi.entity.ZApi;
import com.kg.core.zapi.mapper.ZApiMapper;
import com.kg.core.zapigroup.dto.ZApiGroupDTO;
import com.kg.core.zapigroup.entity.ZApiGroup;
import com.kg.core.zapigroup.mapper.ZApiGroupMapper;
import com.kg.core.zapigroup.service.IZApiGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * API分组信息表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-05-17
 */
@Service
public class ZApiGroupServiceImpl extends ServiceImpl<ZApiGroupMapper, ZApiGroup> implements IZApiGroupService {
    @Autowired
    private ZApiMapper apiMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(ZApiGroupDTO zApiGroupDTO) {
        ZApiGroup group = new ZApiGroup();
        group.setApiGroupId(GuidUtils.getUuid());
        // 更新api分组信息
        QueryWrapper<ZApi> apiQueryWrapper = new QueryWrapper<>();
        apiQueryWrapper.lambda().in(ZApi::getApiId, zApiGroupDTO.getApiIds());
        ZApi updateApi = new ZApi();
        updateApi.setApiGroupId(group.getApiGroupId());
        apiMapper.update(updateApi, apiQueryWrapper);
        // 保存分组信息
        group.setGroupName(zApiGroupDTO.getGroupName());
        group.setGroupOrder(zApiGroupDTO.getGroupOrder());
        group.setCreateTime(LocalDateTime.now());
        save(group);
    }
}
