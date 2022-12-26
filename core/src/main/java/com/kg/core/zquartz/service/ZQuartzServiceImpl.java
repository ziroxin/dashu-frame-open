package com.kg.core.zquartz.service;

import com.kg.core.zquartz.entity.ZQuartz;
import com.kg.core.zquartz.mapper.ZQuartzMapper;
import com.kg.core.zquartz.service.ZQuartzService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 定时任务调度表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
@Service
public class ZQuartzServiceImpl extends ServiceImpl<ZQuartzMapper, ZQuartz> implements ZQuartzService {

}
