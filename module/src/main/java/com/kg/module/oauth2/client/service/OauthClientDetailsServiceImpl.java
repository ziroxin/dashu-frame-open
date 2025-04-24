package com.kg.module.oauth2.client.service;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.TimeUtils;
import com.kg.module.oauth2.client.dto.OauthClientDetailsDTO;
import com.kg.module.oauth2.client.dto.convert.OauthClientDetailsConvert;
import com.kg.module.oauth2.client.entity.OauthClientDetails;
import com.kg.module.oauth2.client.excels.OauthClientDetailsExcelConstant;
import com.kg.module.oauth2.client.excels.OauthClientDetailsExcelOutDTO;
import com.kg.module.oauth2.client.mapper.OauthClientDetailsMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * Oauth2客户端信息表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-09-12
 */
@Service
public class OauthClientDetailsServiceImpl extends ServiceImpl<OauthClientDetailsMapper, OauthClientDetails> implements OauthClientDetailsService {

    @Resource
    private OauthClientDetailsConvert oauthClientDetailsConvert;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<OauthClientDetailsDTO> pagelist(Integer page, Integer limit, String params) {
        Page<OauthClientDetails> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<OauthClientDetails> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("clientId")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("clientId")), OauthClientDetails::getClientId, paramObj.getStr("clientId"));
            }
            if (paramObj.containsKey("resourceIds")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resourceIds")), OauthClientDetails::getResourceIds, paramObj.getStr("resourceIds"));
            }
            if (paramObj.containsKey("clientSecret")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("clientSecret")), OauthClientDetails::getClientSecret, paramObj.getStr("clientSecret"));
            }
            if (paramObj.containsKey("scope")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("scope")), OauthClientDetails::getScope, paramObj.getStr("scope"));
            }
            if (paramObj.containsKey("authorizedGrantTypes")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("authorizedGrantTypes")), OauthClientDetails::getAuthorizedGrantTypes, paramObj.getStr("authorizedGrantTypes"));
            }
            if (paramObj.containsKey("webServerRedirectUri")) {
                wrapper.lambda().like(StringUtils.hasText(paramObj.getStr("webServerRedirectUri")), OauthClientDetails::getWebServerRedirectUri, paramObj.getStr("webServerRedirectUri"));
            }
            if (paramObj.containsKey("authorities")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("authorities")), OauthClientDetails::getAuthorities, paramObj.getStr("authorities"));
            }
            if (paramObj.containsKey("accessTokenValidity")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("accessTokenValidity")), OauthClientDetails::getAccessTokenValidity, paramObj.getStr("accessTokenValidity"));
            }
            if (paramObj.containsKey("refreshTokenValidity")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refreshTokenValidity")), OauthClientDetails::getRefreshTokenValidity, paramObj.getStr("refreshTokenValidity"));
            }
            if (paramObj.containsKey("additionalInformation")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("additionalInformation")), OauthClientDetails::getAdditionalInformation, paramObj.getStr("additionalInformation"));
            }
            if (paramObj.containsKey("autoapprove")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("autoapprove")), OauthClientDetails::getAutoapprove, paramObj.getStr("autoapprove"));
            }
        }
        //返回数据
        Page<OauthClientDetails> pageEntity = page(pager, wrapper);
        Page<OauthClientDetailsDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> oauthClientDetailsConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param oauthClientDetailsDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(OauthClientDetailsDTO oauthClientDetailsDTO) {
        OauthClientDetails oauthClientDetails = oauthClientDetailsConvert.dtoToEntity(oauthClientDetailsDTO);
        if (!StringUtils.hasText(oauthClientDetails.getClientId())) {
            oauthClientDetails.setClientId(GuidUtils.getUuid());
        }
        // 加密
        oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
        save(oauthClientDetails);
    }

    /**
     * 修改
     *
     * @param oauthClientDetailsDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(OauthClientDetailsDTO oauthClientDetailsDTO) {
        OauthClientDetails oauthClientDetails = oauthClientDetailsConvert.dtoToEntity(oauthClientDetailsDTO);
        // 加密
        oauthClientDetails.setClientSecret(passwordEncoder.encode(oauthClientDetails.getClientSecret()));
        updateById(oauthClientDetails);
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

    /**
     * 导出Excel
     *
     * @param params 查询参数
     * @return 导出后的文件url
     */
    @Override
    public String exportExcel(String params) {
        try {
            // 拼接导出Excel的文件，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/"
                    + TimeUtils.now().toFormat("yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<OauthClientDetails> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("clientId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("clientId")), OauthClientDetails::getClientId, paramObj.getStr("clientId"));
                }
                if (paramObj.containsKey("resourceIds")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("resourceIds")), OauthClientDetails::getResourceIds, paramObj.getStr("resourceIds"));
                }
                if (paramObj.containsKey("clientSecret")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("clientSecret")), OauthClientDetails::getClientSecret, paramObj.getStr("clientSecret"));
                }
                if (paramObj.containsKey("scope")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("scope")), OauthClientDetails::getScope, paramObj.getStr("scope"));
                }
                if (paramObj.containsKey("authorizedGrantTypes")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("authorizedGrantTypes")), OauthClientDetails::getAuthorizedGrantTypes, paramObj.getStr("authorizedGrantTypes"));
                }
                if (paramObj.containsKey("webServerRedirectUri")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("webServerRedirectUri")), OauthClientDetails::getWebServerRedirectUri, paramObj.getStr("webServerRedirectUri"));
                }
                if (paramObj.containsKey("authorities")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("authorities")), OauthClientDetails::getAuthorities, paramObj.getStr("authorities"));
                }
                if (paramObj.containsKey("accessTokenValidity")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("accessTokenValidity")), OauthClientDetails::getAccessTokenValidity, paramObj.getStr("accessTokenValidity"));
                }
                if (paramObj.containsKey("refreshTokenValidity")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("refreshTokenValidity")), OauthClientDetails::getRefreshTokenValidity, paramObj.getStr("refreshTokenValidity"));
                }
                if (paramObj.containsKey("additionalInformation")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("additionalInformation")), OauthClientDetails::getAdditionalInformation, paramObj.getStr("additionalInformation"));
                }
                if (paramObj.containsKey("autoapprove")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("autoapprove")), OauthClientDetails::getAutoapprove, paramObj.getStr("autoapprove"));
                }
            }
            List<OauthClientDetails> list = list(wrapper);
            // 转换成导出excel实体
            List<OauthClientDetailsExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), OauthClientDetailsExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new OauthClientDetailsExcelOutDTO());
            }
            // 第一行标题
            String title = "Oauth2客户端信息表";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, OauthClientDetailsExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void importExcel(HttpServletRequest request) {
        // 读取导入数据
        List<OauthClientDetails> importData =
                ExcelReadUtils.read(request, 1, 2, OauthClientDetails.class, OauthClientDetailsExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<OauthClientDetails> saveData = importData.stream().map(o -> {
            if (!StringUtils.hasText(o.getClientId())) {
                o.setClientId(GuidUtils.getUuid());
            }
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
