package com.kg.core.zorg.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.component.utils.StrTypeCheckUtils;
import com.kg.core.exception.BaseException;
import com.kg.core.security.util.CurrentUserUtils;
import com.kg.core.zorg.dto.ZOrganizationDTO;
import com.kg.core.zorg.dto.ZOrganizationTreeSelectDTO;
import com.kg.core.zorg.dto.convert.ZOrganizationConvert;
import com.kg.core.zorg.entity.ZOrganization;
import com.kg.core.zorg.excels.ZOrganizationExcelConstant;
import com.kg.core.zorg.excels.ZOrganizationExcelImportDTO;
import com.kg.core.zorg.excels.ZOrganizationExcelOutDTO;
import com.kg.core.zorg.mapper.ZOrganizationMapper;
import com.kg.core.zuser.dto.ZUserAllDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
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
     * 新增组织机构
     *
     * @param zOrganizationDTO 组织机构信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZOrganizationDTO zOrganizationDTO) throws BaseException {
        ZOrganization zOrganization = organizationConvert.dtoToEntity(zOrganizationDTO);
        zOrganization.setOrgId(GuidUtils.getUuid32());
        if (StringUtils.hasText(zOrganization.getOrgParentId()) && !"-1".equals(zOrganization.getOrgParentId())) {
            // 有父级，取父级路径
            Optional<ZOrganization> oneOpt = lambdaQuery().eq(ZOrganization::getOrgId, zOrganization.getOrgParentId()).oneOpt();
            if (oneOpt.isPresent()) {
                ZOrganization parent = oneOpt.get();
                String parentPath = StringUtils.hasText(parent.getOrgPath()) ? parent.getOrgPath() + "." : "";
                zOrganization.setOrgPath(parentPath + zOrganization.getOrgId());
            } else {
                throw new BaseException("新增失败！您选择的上级部门不正确，请重试");
            }
        } else {
            zOrganization.setOrgParentId("-1");
            zOrganization.setOrgPath(zOrganization.getOrgId());
        }
        int pathLevel = StringUtils.hasText(zOrganization.getOrgPath()) ? zOrganization.getOrgPath().split("\\.").length : 0;
        if (maxOrgLevel > 0 && pathLevel > maxOrgLevel) {
            throw new BaseException("新增失败！超过最大层级限制，请重试");
        }
        zOrganization.setOrgLevel(pathLevel);
        zOrganization.setCreateTime(LocalDateTime.now());
        save(zOrganization);
    }

    /**
     * 修改组织机构
     *
     * @param zOrganizationDTO 组织机构信息
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZOrganizationDTO zOrganizationDTO) throws BaseException {
        ZOrganization zOrganization = organizationConvert.dtoToEntity(zOrganizationDTO);
        if (StringUtils.hasText(zOrganization.getOrgParentId()) && !"-1".equals(zOrganization.getOrgParentId())) {
            // 有父级，取父级路径
            Optional<ZOrganization> oneOpt = lambdaQuery().eq(ZOrganization::getOrgId, zOrganization.getOrgParentId()).oneOpt();
            if (oneOpt.isPresent()) {
                ZOrganization parent = oneOpt.get();
                String parentPath = StringUtils.hasText(parent.getOrgPath()) ? parent.getOrgPath() + "." : "";
                zOrganization.setOrgPath(parentPath + zOrganization.getOrgId());
            } else {
                throw new BaseException("保存失败！您选择的上级部门不正确，请重试");
            }
        } else {
            zOrganization.setOrgParentId("-1");
            zOrganization.setOrgPath(zOrganization.getOrgId());
        }
        int pathLevel = StringUtils.hasText(zOrganization.getOrgPath()) ? zOrganization.getOrgPath().split("\\.").length : 0;
        if (maxOrgLevel > 0 && pathLevel > maxOrgLevel) {
            throw new BaseException("保存失败！超过最大层级限制，请重试");
        }
        zOrganization.setOrgLevel(pathLevel);
        zOrganization.setUpdateTime(LocalDateTime.now());
        updateById(zOrganization);
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
            String path = FilePathConfig.SAVE_PATH + "/exportTemp/excel/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<ZOrganization> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
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
            List<ZOrganizationExcelOutDTO> dataList = list.stream().map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZOrganizationExcelOutDTO.class)).collect(Collectors.toList());
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
            Optional<ZOrganization> first = list.stream().filter(org -> org.getOrgId().equals(parentId)).findFirst();
            if (first.isPresent()) {
                ZOrganizationDTO top = organizationConvert.entityToDto(first.get());
                if (list.stream().filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId)).count() > 0) {
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
                return list.stream().filter(org -> org.getOrgLevel().equals(topLevel)).map(org -> {
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
        return list.stream().filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId)).map(org -> {
            ZOrganizationDTO dto = organizationConvert.entityToDto(org);
            if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getOrgId())).count() > 0) {
                // 有子节点，迭代添加
                dto.setChildren(getOrgChildren(list, dto.getOrgId()));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ZOrganizationTreeSelectDTO> treeForSelect() {
        ZUserAllDTO user = CurrentUserUtils.getCurrentUserAll();
        // 查询
        List<ZOrganization> list = lambdaQuery().orderByAsc(ZOrganization::getOrgLevel).orderByAsc(ZOrganization::getOrderIndex).list();
        List<ZOrganizationTreeSelectDTO> result = new ArrayList<>();
        // 判断
        if (user == null || !StringUtils.hasText(user.getOrgId()) || user.getOrgId().equals("-1") || user.getOrgLevel() <= 1) {
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
        return list.stream().filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId)).map(org -> {
            ZOrganizationTreeSelectDTO dto = new ZOrganizationTreeSelectDTO();
            dto.setValue(org.getOrgId());
            dto.setLabel(org.getOrgName());
            if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getValue())).count() > 0) {
                // 有子节点，迭代添加
                dto.setChildren(getOrgChildrenForTreeSelect(list, dto.getValue()));
            }
            return dto;
        }).collect(Collectors.toList());
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
        List<ZOrganization> allList = lambdaQuery().orderByAsc(ZOrganization::getOrgLevel).orderByAsc(ZOrganization::getOrderIndex).list();
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
                if (allList.stream().filter(org -> StringUtils.hasText(org.getOrgParentId()) && org.getOrgParentId().equals(parentId)).count() > 0) {
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
        return list.stream().filter(org -> org.getOrgParentId() != null && org.getOrgParentId().equals(parentId)).map(org -> {
            ZOrganizationDTO dto = organizationConvert.entityToDto(org);
            if (list.stream().filter(o -> o.getOrgParentId() != null && o.getOrgParentId().equals(dto.getOrgId())).count() > 0) {
                // 有子节点，迭代添加
                if (maxOrgLevel == -1 || org.getOrgLevel() < (maxOrgLevel - 1)) {
                    dto.setChildren(parentTreeGetChildren(list, dto.getOrgId()));
                }
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Integer getMaxLevel() {
        return maxOrgLevel;
    }

    /**
     * 导入Excel
     *
     * @param request 请求文件
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public String importExcel(HttpServletRequest request) {
        // 读取导入数据
        int startRowIdx = 2;
        List<ZOrganizationExcelImportDTO> importData = ExcelReadUtils.read(request, 1, startRowIdx, ZOrganizationExcelImportDTO.class, ZOrganizationExcelConstant.IMPORT_EXCEL_COLUMN);
        if (importData == null || importData.isEmpty()) {
            return "Excel文件中没有数据！";
        }

        // 2. 必填字段校验
        String errorMsg = "";
        int currentRowIdx = startRowIdx;
        if (ZOrganizationExcelConstant.IMPORT_REQUIRED_COLUMN.size() > 0) {
            for (ZOrganizationExcelImportDTO entity : importData) {
                currentRowIdx++;
                JSONObject rowData = JSONUtil.parseObj(entity);
                List<String> emptyColName = new ArrayList<>();
                for (Map.Entry<String, String> col : ZOrganizationExcelConstant.IMPORT_REQUIRED_COLUMN.entrySet()) {
                    if (!StringUtils.hasText(rowData.getStr(col.getKey()))) {
                        emptyColName.add(col.getValue());
                    }
                }
                if (emptyColName.size() > 0) {
                    errorMsg += "第" + currentRowIdx + "行，必填字段[" + String.join(",", emptyColName) + "]不能为空！<br/>";
                }
                // 检测数字格式
                if (StringUtils.hasText(entity.getOrderIndex()) && !StrTypeCheckUtils.isNumeric(entity.getOrderIndex())) {
                    errorMsg += "第" + currentRowIdx + "行，顺序必须是数字！<br/>";
                }
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }

        // 3. Excel表内查重
        Map<String, Long> orgCountMap = importData.stream().map(ZOrganizationExcelImportDTO::getOrgName).collect(Collectors.groupingBy(e -> e, Collectors.counting()));
        currentRowIdx = startRowIdx;
        for (ZOrganizationExcelImportDTO entity : importData) {
            currentRowIdx++;
            if (orgCountMap.get(entity.getOrgName()) != null && orgCountMap.get(entity.getOrgName()) > 1) {
                errorMsg += "第" + currentRowIdx + "行，组织机构[" + entity.getOrgName() + "]在Excel表内重复！<br/>";
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }

        // 4. 数据库查重
        List<ZOrganization> list = lambdaQuery().orderByAsc(ZOrganization::getOrgLevel).orderByAsc(ZOrganization::getOrderIndex).list();
        currentRowIdx = startRowIdx;
        for (ZOrganizationExcelImportDTO entity : importData) {
            currentRowIdx++;
            if (list.stream().anyMatch(d -> d.getOrgName().equals(entity.getOrgName()))) {
                errorMsg += "第" + currentRowIdx + "行，组织机构[" + entity.getOrgName() + "]已存在！<br/>";
            }
            // 判断上级部门是否存在
            if (entity.getOrgParentId().equals("顶级")) {
                entity.setOrgId(GuidUtils.getUuid32());
                entity.setOrgParentId("-1");
                entity.setOrgPath(entity.getOrgId());
            } else {
                Optional<ZOrganization> parent = list.stream()
                        .filter(d -> d.getOrgName().equals(entity.getOrgParentId())).findFirst();
                if (!parent.isPresent()) {
                    errorMsg += "第" + currentRowIdx + "行，上级部门[" + entity.getOrgParentId() + "]不存在(最高级请填写“顶级”)！<br/>";
                } else {
                    entity.setOrgId(GuidUtils.getUuid32());
                    ZOrganization porg = parent.get();
                    entity.setOrgParentId(porg.getOrgId());
                    String parentPath = StringUtils.hasText(porg.getOrgPath()) ? porg.getOrgPath() + "." : "";
                    entity.setOrgPath(parentPath + entity.getOrgId());
                }
            }
            if (StringUtils.hasText(entity.getOrgId())) {
                // 父级正确，判断是否超过最大层级限制
                int pathLevel = StringUtils.hasText(entity.getOrgPath()) ? entity.getOrgPath().split("\\.").length : 0;
                if (maxOrgLevel > 0 && pathLevel > maxOrgLevel) {
                    errorMsg += "第" + currentRowIdx + "行，组织机构[" + entity.getOrgName() + "]超过最大层级限制！<br/>";
                } else {
                    entity.setOrgLevel(pathLevel);
                }
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }

        // 5. 保存数据
        List<ZOrganization> saveData = importData.stream().map(o -> {
            ZOrganization entity = JSONUtil.toBean(JSONUtil.parseObj(JSONUtil.toJsonStr(o)), ZOrganization.class);
            entity.setCreateTime(LocalDateTime.now());
            return entity;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
        return "";
    }

    /**
     * 下载导入模板
     *
     * @return 模板文件url
     */
    @Override
    public String downloadTemplate() {
        try {
            // 拼接下载Excel模板，保存的临时路径
            String path = FilePathConfig.SAVE_PATH + "/importTemp/excel/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";
            // 第一行标题
            String title = "组织机构-导入模板";
            // 写入模板字段行
            ExcelWriteUtils.writeTemplate(path, title, ZOrganizationExcelConstant.IMPORT_EXCEL_COLUMN);
            // 生成模板成功，返回模板地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
