package com.kg.module.applet.wechat2user.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.utils.GuidUtils;
import com.kg.module.applet.wechat2user.dto.ZUserWechatDTO;
import com.kg.module.applet.wechat2user.dto.convert.ZUserWechatConvert;
import com.kg.module.applet.wechat2user.entity.ZUserWechat;
import com.kg.module.applet.wechat2user.mapper.ZUserWechatMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户-微信-绑定关系表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2024-12-17
 */
@Service
public class ZUserWechatServiceImpl extends ServiceImpl<ZUserWechatMapper, ZUserWechat> implements ZUserWechatService {

    @Resource
    private ZUserWechatConvert zUserWechatConvert;
    @Resource
    private ZUserWechatMapper zUserWechatMapper;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZUserWechatDTO> pagelist(Integer page, Integer limit, String params) {
        // 解析查询参数
        JSONObject paramObj = new JSONObject();
        if (StringUtils.hasText(params)) {
            paramObj = JSONUtil.parseObj(params, true);
        }
        // 计算分页偏移量
        Integer offset = (page - 1) * limit;
        paramObj.set("limit", limit);
        paramObj.set("offset", offset);
        // 查询列表
        List<ZUserWechatDTO> list = zUserWechatMapper.list(paramObj);
        Page<ZUserWechatDTO> result = new Page<>();
        result.setRecords(list);
        result.setTotal(zUserWechatMapper.count(paramObj));// 查询总数
        return result;
    }

    /**
     * 新增
     *
     * @param zUserWechatDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZUserWechatDTO zUserWechatDTO) {
        ZUserWechat zUserWechat = zUserWechatConvert.dtoToEntity(zUserWechatDTO);
        zUserWechat.setOpenid(GuidUtils.getUuid());
        save(zUserWechat);
    }

    /**
     * 修改
     *
     * @param zUserWechatDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZUserWechatDTO zUserWechatDTO) {
        ZUserWechat zUserWechat = zUserWechatConvert.dtoToEntity(zUserWechatDTO);
        updateById(zUserWechat);
    }

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) {
        removeBatchByIds(idlist);
    }

}
