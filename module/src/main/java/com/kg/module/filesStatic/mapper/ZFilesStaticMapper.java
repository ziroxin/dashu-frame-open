package com.kg.module.filesStatic.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.filesStatic.dto.ZFilesStaticDTO;
import com.kg.module.filesStatic.entity.ZFilesStatic;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 静态资源文件表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2025-01-24
 */
@Repository
public interface ZFilesStaticMapper extends BaseMapper<ZFilesStatic> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<ZFilesStaticDTO> list(JSONObject paramObj);

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
    void saveList(List<ZFilesStatic> saveData);

}
