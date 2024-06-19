package com.kg.module.message.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.message.dto.ZMessageDTO;
import com.kg.module.message.entity.ZMessage;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 消息中心 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2024-06-18
 */
@Repository
public interface ZMessageMapper extends BaseMapper<ZMessage> {

    /**
     * 获取消息列表
     *
     * @param offset 起始位置
     * @param limit  数量
     * @param params 查询参数
     * @return 消息列表
     */
    List<ZMessageDTO> messageList(@Param("offset") Integer offset, @Param("limit") Integer limit,
                                  @Param("params") HashMap<String, Object> params);

    /**
     * 获取消息列表总数
     *
     * @param params 查询参数
     * @return 消息列表总数
     */
    Integer messageListCount(@Param("params") HashMap<String, Object> params);
}
