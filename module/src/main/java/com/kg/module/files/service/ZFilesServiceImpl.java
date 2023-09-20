package com.kg.module.files.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.office.ExcelCommonUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.files.dto.ZFilesDTO;
import com.kg.module.files.dto.convert.ZFilesConvert;
import com.kg.module.files.entity.ZFiles;
import com.kg.module.files.excels.ZFilesExcelConstant;
import com.kg.module.files.excels.ZFilesExcelOutDTO;
import com.kg.module.files.mapper.ZFilesMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 文件记录表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2023-09-15
 */
@Service
public class ZFilesServiceImpl extends ServiceImpl<ZFilesMapper, ZFiles> implements ZFilesService {

    @Resource
    private ZFilesConvert zFilesConvert;

    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZFilesDTO> pagelist(Integer page, Integer limit, String params) {
        Page<ZFiles> pager = new Page<>(page, limit);
        // 根据条件查询
        QueryWrapper<ZFiles> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(params)) {
            JSONObject paramObj = JSONUtil.parseObj(params);
            if (paramObj.containsKey("fileId")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileId")), ZFiles::getFileId, paramObj.getStr("fileId"));
            }
            if (paramObj.containsKey("fileMd5")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileMd5")), ZFiles::getFileMd5, paramObj.getStr("fileMd5"));
            }
            if (paramObj.containsKey("fileUrl")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileUrl")), ZFiles::getFileUrl, paramObj.getStr("fileUrl"));
            }
            if (paramObj.containsKey("fileOldName")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileOldName")), ZFiles::getFileOldName, paramObj.getStr("fileOldName"));
            }
            if (paramObj.containsKey("fileName")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileName")), ZFiles::getFileName, paramObj.getStr("fileName"));
            }
            if (paramObj.containsKey("fileExtend")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileExtend")), ZFiles::getFileExtend, paramObj.getStr("fileExtend"));
            }
            if (paramObj.containsKey("fileSize")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileSize")), ZFiles::getFileSize, paramObj.getStr("fileSize"));
            }
            if (paramObj.containsKey("createTime")) {
                wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZFiles::getCreateTime, paramObj.getStr("createTime"));
            }
        }
        //返回数据
        Page<ZFiles> pageEntity = page(pager, wrapper);
        Page<ZFilesDTO> result = new Page<>();
        result.setRecords(
                pageEntity.getRecords().stream().map(m -> zFilesConvert.entityToDto(m)).collect(Collectors.toList()));
        result.setTotal(pageEntity.getTotal());
        return result;
    }

    /**
     * 新增
     *
     * @param zFilesDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZFilesDTO zFilesDTO) {
        ZFiles zFiles = zFilesConvert.dtoToEntity(zFilesDTO);
        zFiles.setFileId(GuidUtils.getUuid());
        zFiles.setCreateTime(LocalDateTime.now());
        save(zFiles);
    }

    /**
     * 修改
     *
     * @param zFilesDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZFilesDTO zFilesDTO) {
        ZFiles zFiles = zFilesConvert.dtoToEntity(zFilesDTO);
        updateById(zFiles);
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
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";

            // 查询待导出的数据
            QueryWrapper<ZFiles> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(params)) {
                JSONObject paramObj = JSONUtil.parseObj(params);
                if (paramObj.containsKey("fileId")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileId")), ZFiles::getFileId, paramObj.getStr("fileId"));
                }
                if (paramObj.containsKey("fileMd5")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileMd5")), ZFiles::getFileMd5, paramObj.getStr("fileMd5"));
                }
                if (paramObj.containsKey("fileUrl")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileUrl")), ZFiles::getFileUrl, paramObj.getStr("fileUrl"));
                }
                if (paramObj.containsKey("fileOldName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileOldName")), ZFiles::getFileOldName, paramObj.getStr("fileOldName"));
                }
                if (paramObj.containsKey("fileName")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileName")), ZFiles::getFileName, paramObj.getStr("fileName"));
                }
                if (paramObj.containsKey("fileExtend")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileExtend")), ZFiles::getFileExtend, paramObj.getStr("fileExtend"));
                }
                if (paramObj.containsKey("fileSize")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("fileSize")), ZFiles::getFileSize, paramObj.getStr("fileSize"));
                }
                if (paramObj.containsKey("createTime")) {
                    wrapper.lambda().eq(StringUtils.hasText(paramObj.getStr("createTime")), ZFiles::getCreateTime, paramObj.getStr("createTime"));
                }
            }
            List<ZFiles> list = list(wrapper);
            // 转换成导出excel实体
            List<ZFilesExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d), ZFilesExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZFilesExcelOutDTO());
            }
            // 第一行标题
            String title = "文件记录表";
            // 写入导出excel文件
            ExcelCommonUtils.write(path, title, dataList, ZFilesExcelConstant.EXPORT_EXCEL_COLUMN);
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
        List<ZFiles> importData =
                ExcelCommonUtils.read(request, 1, 2, ZFiles.class, ZFilesExcelConstant.IMPORT_EXCEL_COLUMN);
        // 处理数据
        List<ZFiles> saveData = importData.stream().map(o -> {
            o.setFileId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        // 保存
        saveBatch(saveData);
    }

}
