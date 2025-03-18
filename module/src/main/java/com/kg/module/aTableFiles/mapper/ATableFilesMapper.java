package com.kg.module.aTableFiles.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.aTableFiles.dto.ATableFilesDTO;
import com.kg.module.aTableFiles.entity.ATableFiles;
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
public interface ATableFilesMapper extends BaseMapper<ATableFiles> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<ATableFilesDTO> list(JSONObject paramObj);

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
    void saveList(List<ATableFiles> saveData);

}
