package com.kg.module.userTheme.service;

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
import com.kg.core.zuser.entity.ZUser;
import com.kg.core.zuser.service.IZUserService;
import com.kg.module.userTheme.dto.ZUserThemeDTO;
import com.kg.module.userTheme.dto.convert.ZUserThemeConvert;
import com.kg.module.userTheme.entity.ZUserTheme;
import com.kg.module.userTheme.excels.ZUserThemeExcelConstant;
import com.kg.module.userTheme.excels.ZUserThemeExcelOutDTO;
import com.kg.module.userTheme.mapper.ZUserThemeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户主题配置 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-11-04
 */
@Service
public class ZUserThemeServiceImpl extends ServiceImpl<ZUserThemeMapper, ZUserTheme> implements ZUserThemeService {

    @Resource
    private ZUserThemeConvert zUserThemeConvert;
    @Resource
    private IZUserService userService;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZUserThemeDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZUserTheme> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZUserTheme> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params, true);
            if (paramObj.containsKey("themeId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("themeId")), ZUserTheme::getThemeId, paramObj.getStr("themeId"));
            }
            if (paramObj.containsKey("userId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), ZUserTheme::getUserId, paramObj.getStr("userId"));
            }
            if (paramObj.containsKey("userName")) {
                List<ZUser> userList = userService.lambdaQuery()
                        .like(ZUser::getUserName, paramObj.getStr("userName")).list();
                if (userList != null && userList.size() > 0) {
                    wrapper.lambda().in(ZUserTheme::getUserId,
                            userList.stream().map(ZUser::getUserId).collect(Collectors.toList()));
                } else {
                    wrapper.lambda().eq(ZUserTheme::getUserId, "-0-");
                }
            }
            if (paramObj.containsKey("themeJson")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("themeJson")), ZUserTheme::getThemeJson, paramObj.getStr("themeJson"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZUserTheme::getCreateTime, paramObj.getStr("createTime"));
            }
            if (paramObj.containsKey("updateTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZUserTheme::getUpdateTime, paramObj.getStr("updateTime"));
            }
        }
        //返回数据
        Page<ZUserTheme> pageEntity = page(pager, wrapper);
        Page<ZUserThemeDTO> result = new Page<>();
        if (pageEntity.getRecords() != null && pageEntity.getRecords().size() > 0) {
            List<ZUser> userList = userService.lambdaQuery()
                    .in(ZUser::getUserId, pageEntity.getRecords().stream().map(ZUserTheme::getUserId).collect(Collectors.toList()))
                    .list();
            result.setRecords(
                    pageEntity.getRecords().stream().map(m -> {
                        ZUserThemeDTO dto = zUserThemeConvert.entityToDto(m);
                        Optional<ZUser> first = userList.stream().filter(u -> u.getUserId().equals(m.getUserId())).findFirst();
                        if (first.isPresent()) {
                            dto.setUserName(first.get().getUserName());
                            dto.setName(first.get().getName());
                            dto.setPhone(first.get().getPhone());
                            dto.setNickName(first.get().getNickName());
                        }
                        return dto;
                    }).collect(Collectors.toList()));
        }
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zUserThemeDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZUserThemeDTO zUserThemeDTO) {
        ZUserTheme zUserTheme = zUserThemeConvert.dtoToEntity(zUserThemeDTO);
        zUserTheme.setThemeId(StringUtils.hasText(zUserTheme.getThemeId()) ? zUserTheme.getThemeId() : GuidUtils.getUuid());
        zUserTheme.setCreateTime(LocalDateTime.now());
        save(zUserTheme);
    }

    /**
     * 修改
     *
     * @param zUserThemeDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZUserThemeDTO zUserThemeDTO) {
        ZUserTheme zUserTheme = zUserThemeConvert.dtoToEntity(zUserThemeDTO);
        zUserTheme.setUpdateTime(LocalDateTime.now());
        updateById(zUserTheme);
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
            QueryWrapper<ZUserTheme> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params, true);
                if (paramObj.containsKey("themeId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("themeId")), ZUserTheme::getThemeId, paramObj.getStr("themeId"));
                }
                if (paramObj.containsKey("userId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("userId")), ZUserTheme::getUserId, paramObj.getStr("userId"));
                }
                if (paramObj.containsKey("themeJson")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("themeJson")), ZUserTheme::getThemeJson, paramObj.getStr("themeJson"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZUserTheme::getCreateTime, paramObj.getStr("createTime"));
                }
                if (paramObj.containsKey("updateTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("updateTime")), ZUserTheme::getUpdateTime, paramObj.getStr("updateTime"));
                }
            }
            List<ZUserTheme> list = list(wrapper);
            // 转换成导出excel实体
            List<ZUserThemeExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), ZUserThemeExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZUserThemeExcelOutDTO());
            }
            // 第一行标题
            String title = "用户主题配置";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZUserThemeExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZUserTheme> importData =
                ExcelReadUtils.read(request, 1, 2, ZUserTheme.class, ZUserThemeExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZUserTheme> saveData = importData.stream().map(o -> {
            o.setThemeId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
