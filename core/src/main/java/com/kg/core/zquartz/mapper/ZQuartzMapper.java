package com.kg.core.zquartz.mapper;

import com.kg.core.zquartz.entity.ZQuartz;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 定时任务调度表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2022-12-26
 */
@Repository
public interface ZQuartzMapper extends BaseMapper<ZQuartz> {

}
