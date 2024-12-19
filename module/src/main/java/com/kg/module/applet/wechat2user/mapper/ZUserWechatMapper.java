package com.kg.module.applet.wechat2user.mapper;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kg.module.applet.wechat2user.dto.ZUserWechatDTO;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 用户-微信-绑定关系表 Mapper 接口
 * </p>
 *
 * @author ziro
 * @since 2024-12-17
 */
@Repository
public interface ZUserWechatMapper extends BaseMapper<ZUserWechat> {

    /**
     * 根据参数查询列表
     *
     * @param paramObj 参数对象
     * @return 列表
     */
    List<ZUserWechatDTO> list(JSONObject paramObj);

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
    void saveList(List<ZUserWechat> saveData);

}
