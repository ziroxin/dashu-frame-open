package com.kg.module.atable.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.atable.dto.ATableDTO;
import com.kg.module.atable.entity.ATable;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 我的表a_table Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2025-02-11
 */
@Repository
public interface ATableMapper extends BaseMapper<ATable> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<ATableDTO> list(JSONObject paramObj);

    /**
     * 根据参数查询总数
     *
     * @param paramObj 参数对象
     * @return 总数
     */
    long count(JSONObject paramObj);

    /**
     * 批量保存
     *
     * @param saveData 批量保存数据
     */
    void saveList(List<ATable> saveData);

}
