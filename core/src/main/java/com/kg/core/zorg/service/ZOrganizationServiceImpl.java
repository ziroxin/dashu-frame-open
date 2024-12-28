package com.kg.core.zorg.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.dto.convert.ZOrganizationConvert;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.excels.ZOrganizationExcelConstant;
import com.kg.core.zorg.excels.ZOrganizationExcelOutDTO;
import com.kg.core.zorg.mapper.ZOrganizationMapper;
import com.kg.core.zuser.dto.ZUserAllDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 组织机构表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-01-11
 */
@Service
public class ZOrganizationServiceImpl extends ServiceImpl<ZOrganizationMapper, ZOrganization> implements ZOrganizationService {
    @Resource
    private ZOrganizationConvert organizationConvert;

    /** 组织机构最最大层级（-1表示无限制） */
    @Value("${com.kg.max-org-level:-1}")
    private Integer maxOrgLevel;

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
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<ZOrganization> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("orgId")) {
                    wrapper.lambda().eq(ZOrganization::getOrgId, paramObj.getStr("orgId"));
                }
                if (paramObj.containsKey("orgName")) {
                    wrapper.lambda().like(ZOrganization::getOrgName, paramObj.getStr("orgName"));
                }
                if (paramObj.containsKey("orgParentId")) {
                    wrapper.lambda().eq(ZOrganization::getOrgParentId, paramObj.getStr("orgParentId"));
                }
                if (paramObj.containsKey("orgPath")) {
                    wrapper.lambda().eq(ZOrganization::getOrgPath, paramObj.getStr("orgPath"));
                }
                if (paramObj.containsKey("orgLevel")) {
                    wrapper.lambda().eq(ZOrganization::getOrgLevel, paramObj.getStr("orgLevel"));
                }
                if (paramObj.containsKey("orderIndex")) {
                    wrapper.lambda().eq(ZOrganization::getOrderIndex, paramObj.getStr("orderIndex"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(ZOrganization::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(ZOrganization::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZOrganization> list = list(wrapper);
            // 转换成导出excel实体
            List<ZOrganizationExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZOrganizationExcelOutDTO.class))
                    .collect(Collectors.toList());
            // 第一行标题
            String title = "组织机构表";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZOrganizationExcelConstant.EXPORT_EXCEL_COLUMN);
            // 导出成功，返回导出地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

    @Override
    public List<ZOrganizationDTO> tree(String orgName, String parentId) {
        // 根据名称模糊查询
        QueryWrapper<ZOrganization> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(orgName)) {
            wrapper.lambda().like(ZOrganization::getOrgName, orgName);
        }
        // 排序
        wrapper.lambda().orderByAsc(ZOrganization::getOrgLevel).orderByAsc(ZOrganization::getOrderIndex);
        // 查询
        List<ZOrganization> list = list(wrapper);
        List<ZOrganizationDTO> result = new ArrayList<>();
        if (StringUtils.hasText(parentId)) {
            // 取当前节点及其子节点
            Optional<ZOrganization> first = list.stream()
                    .filter(org -> org.getOrgId().equals(parentId)).findFirst();
            if (first.isPresent()) {
                ZOrganizationDTO top = organizationConvert.entityToDto(first.get());
                if (list.stream().filter(org -> org.getOrgParentId() != null
                        && org.getOrgParentId().equals(parentId)).count() > 0) {
                    // 有子节点，迭代添加
                    top.setChildren(getOrgChildren(list, parentId));
                }
                result.add(top);
                return result;
            }
            return null;
        } else {
            if (list.size() > 0) {
                // 从list中level最小值（最顶级）开始迭代
                Integer topLevel = list.stream().map(o -> o.getOrgLevel()).distinct().sorted().findFirst().get();
                return list.stream()
                        .filter(org -> org.getOrgLevel().equals(topLevel))
                        .map(org -> {
                            ZOrganizationDTO dto = organizationConvert.entityToDto(org);
                            dto.setChildren(getOrgChildren(list, dto.getOrgId()));
                            return dto;
                        }).collect(Collectors.toList());
            }
            return null;
        }
    }

    /**
     * 迭代处理组织机构树
     *
     * @param list     待处理列表
     * @param parentId 父级id
     */
    private List<ZOrganizationDTO> getOrgChildren(List<ZOrganization> list, String parentId) {
        return list.stream()
                .filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId))
                .map(org -> {
                    ZOrganizationDTO dto = organizationConvert.entityToDto(org);
                    if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getOrgId())).count() > 0) {
                        // 有子节点，迭代添加
                        dto.setChildren(getOrgChildren(list, dto.getOrgId()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ZOrganizationTreeSelectDTO> treeForSelect() {
        ZUserAllDTO user = CurrentUserUtils.getCurrentUserAll();
        // 查询
        List<ZOrganization> list = lambdaQuery().orderByAsc(ZOrganization::getOrgLevel)
                .orderByAsc(ZOrganization::getOrderIndex).list();
        List<ZOrganizationTreeSelectDTO> result = new ArrayList<>();
        // 判断
        if (user == null || !StringUtils.hasText(user.getOrgId())
                || user.getOrgId().equals("-1") || user.getOrgLevel() <= 1) {
            // 未登录，或当前用户为总管理员（当前用户的orgId=-1或者当前用户的orgLevel<=1代表总管理员），获取全部组织机构
            return getOrgChildrenForTreeSelect(list, "-1");
        } else {
            // 登录，且非总管理员，获取当前部门及下级部门
            String parentId = user.getOrgId();
            Optional<ZOrganization> first = list.stream().filter(org -> org.getOrgId().equals(parentId)).findFirst();
            if (first.isPresent()) {
                ZOrganizationTreeSelectDTO cascaderDTO = new ZOrganizationTreeSelectDTO();
                cascaderDTO.setValue(first.get().getOrgId());
                cascaderDTO.setLabel(first.get().getOrgName());
                if (list.stream().filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId)).count() > 0) {
                    // 有子节点，迭代添加
                    cascaderDTO.setChildren(getOrgChildrenForTreeSelect(list, parentId));
                }
                result.add(cascaderDTO);
                return result;
            }
            return null;
        }
    }

    /**
     * 迭代处理组织机构树 for 级联
     *
     * @param list     待处理列表
     * @param parentId 父级id
     */
    private List<ZOrganizationTreeSelectDTO> getOrgChildrenForTreeSelect(List<ZOrganization> list, String parentId) {
        return list.stream()
                .filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId))
                .map(org -> {
                    ZOrganizationTreeSelectDTO dto = new ZOrganizationTreeSelectDTO();
                    dto.setValue(org.getOrgId());
                    dto.setLabel(org.getOrgName());
                    if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getValue())).count() > 0) {
                        // 有子节点，迭代添加
                        dto.setChildren(getOrgChildrenForTreeSelect(list, dto.getValue()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }


    /**
     * 查询父级组织机构树
     */
    @Override
    public List<ZOrganizationDTO> parentTree() {
        ZUserAllDTO user = CurrentUserUtils.getCurrentUserAll();
        if (user == null) {
            return null;
        }
        List<ZOrganization> allList = lambdaQuery().orderByAsc(ZOrganization::getOrgLevel)
                .orderByAsc(ZOrganization::getOrderIndex).list();
        if (user.getOrgId().equals("-1") || user.getOrgLevel() <= 1) {
            // 总管理员（当前用户的orgId=-1或者当前用户的orgLevel<=1代表总管理员），获取全部父级
            List<ZOrganizationDTO> result = parentTreeGetChildren(allList, "-1");
            ZOrganizationDTO top = new ZOrganizationDTO();
            top.setOrgId("-1");
            top.setOrgName("顶级");
            result.add(0, top);
            return result;
        } else {
            // 非总管理员，获取当前部门及下级部门
            List<ZOrganizationDTO> result = new ArrayList<>();
            String parentId = user.getOrgId();
            // 取当前节点
            Optional<ZOrganization> first = allList.stream().filter(org -> org.getOrgId().equals(parentId)).findFirst();
            if (first.isPresent()) {
                ZOrganizationDTO orgDto = organizationConvert.entityToDto(first.get());
                if (allList.stream().filter(org -> StringUtils.hasText(org.getOrgParentId())
                        && org.getOrgParentId().equals(parentId)).count() > 0) {
                    // 有子节点，迭代添加
                    if (maxOrgLevel == -1 || orgDto.getOrgLevel() < (maxOrgLevel - 1)) {
                        orgDto.setChildren(parentTreeGetChildren(allList, parentId));
                    }
                }
                result.add(orgDto);
                return result;
            }

        }
        return null;
    }

    private List<ZOrganizationDTO> parentTreeGetChildren(List<ZOrganization> list, String parentId) {
        return list.stream()
                .filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId))
                .map(org -> {
                    ZOrganizationDTO dto = organizationConvert.entityToDto(org);
                    if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getOrgId())).count() > 0) {
                        // 有子节点，迭代添加
                        if (maxOrgLevel == -1 || org.getOrgLevel() < (maxOrgLevel - 1)) {
                            dto.setChildren(parentTreeGetChildren(list, dto.getOrgId()));
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Integer getMaxLevel() {
        return maxOrgLevel;
    }

}
