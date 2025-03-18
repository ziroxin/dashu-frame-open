package com.kg.module.aTableImages.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.aTableImages.dto.ATableImagesDTO;
import com.kg.module.aTableImages.entity.ATableImages;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 我的表a_table附件表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2025-03-18
 */
@Repository
public interface ATableImagesMapper extends BaseMapper<ATableImages> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<ATableImagesDTO> list(JSONObject paramObj);

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
    void saveList(List<ATableImages> saveData);

}
