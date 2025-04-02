package com.kg.module.filesStatic.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.utils.RemoveFileUtils;
import com.kg.component.office.ExcelReadUtils;
import com.kg.component.office.ExcelWriteUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.module.filesStatic.dto.ZFilesStaticDTO;
import com.kg.module.filesStatic.dto.convert.ZFilesStaticConvert;
import com.kg.module.filesStatic.entity.ZFilesStatic;
import com.kg.module.filesStatic.excels.ZFilesStaticExcelConstant;
import com.kg.module.filesStatic.excels.ZFilesStaticExcelImportDTO;
import com.kg.module.filesStatic.excels.ZFilesStaticExcelOutDTO;
import com.kg.module.filesStatic.mapper.ZFilesStaticMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 静态资源文件表 服务实现类
 * </p>
 *
 * @author ziro
 * @since 2025-01-24
 */
@Service
public class ZFilesStaticServiceImpl extends ServiceImpl<ZFilesStaticMapper, ZFilesStatic> implements ZFilesStaticService {

    @Resource
    private ZFilesStaticConvert zFilesStaticConvert;
    @Resource
    private ZFilesStaticMapper zFilesStaticMapper;


    /**
     * 分页列表
     *
     * @param page   页码
     * @param limit  条数
     * @param params 查询条件
     * @return 分页列表
     */
    @Override
    public Page<ZFilesStaticDTO> pagelist(Integer page, Integer limit, String params) {
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
        List<ZFilesStaticDTO> list = zFilesStaticMapper.list(paramObj);
        list.forEach(d -> d.setAutoUnzip(false));// 设置不自动解压
        if (paramObj.containsKey("hasChildren") && paramObj.getStr("hasChildren").equals("true")) {
            // 查询文件夹下级资源
            List<ZFilesStatic> childrenList = lambdaQuery().eq(ZFilesStatic::getFileType, "1").list();
            list.forEach(d -> {
                List<ZFilesStatic> children = childrenList.stream()
                        .filter(c -> c.getParentId().equals(d.getFileId())).collect(Collectors.toList());
                if (children.size() > 0) {
                    d.setChildren(children.stream().map(c -> zFilesStaticConvert.entityToDto(c)).collect(Collectors.toList()));
                }
            });
        }
        Page<ZFilesStaticDTO> result = new Page<>();
        result.setRecords(list);
        result.setTotal(zFilesStaticMapper.count(paramObj));// 查询总数
        return result;
    }

    /**
     * 新增
     *
     * @param zFilesStaticDTO 新增实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void add(ZFilesStaticDTO zFilesStaticDTO) {
        ZFilesStatic zFilesStatic = zFilesStaticConvert.dtoToEntity(zFilesStaticDTO);
        zFilesStatic.setFileId(GuidUtils.getUuid());
        zFilesStatic.setCreateTime(LocalDateTime.now());
        if (zFilesStatic.getFileType().equals("0")) {
            zFilesStatic.setFileUrl("/upload/" + zFilesStatic.getFileOldName() + "/");
        }
        save(zFilesStatic);
    }

    /**
     * 修改
     *
     * @param zFilesStaticDTO 编辑实体
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void update(ZFilesStaticDTO zFilesStaticDTO) {
        ZFilesStatic zFilesStatic = zFilesStaticConvert.dtoToEntity(zFilesStaticDTO);
        zFilesStatic.setUpdateTime(LocalDateTime.now());
        if (zFilesStatic.getFileType().equals("0")) {
            String newFolderUrl = "/upload/" + zFilesStatic.getFileOldName() + "/";
            // 判断文件夹内是否有文件
            List<ZFilesStatic> fileList = lambdaQuery().eq(ZFilesStatic::getParentId, zFilesStatic.getFileId()).list();
            if (fileList.size() > 0) {
                ZFilesStatic oldEntity = getById(zFilesStatic.getFileId());
                String oldFolderUrl = oldEntity.getFileUrl();
                // 若文件夹内有文件，则物理修改文件夹名，并更新所有文件url
                FileUtil.rename(new File(FilePathConfig.switchSavePath(oldFolderUrl)),
                        zFilesStatic.getFileOldName(), true);
                fileList.forEach(f -> {
                    String oldUrl = f.getFileUrl();
                    String newUrl = oldUrl.replace(oldFolderUrl, newFolderUrl);
                    f.setFileUrl(newUrl);
                });
                updateBatchById(fileList);
            }
            // 更新文件夹名
            zFilesStatic.setFileUrl(newFolderUrl);
        }
        updateById(zFilesStatic);
    }

    /**
     * 删除
     *
     * @param idlist 删除id列表
     */
    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void delete(List<String> idlist) {
        List<ZFilesStatic> list = lambdaQuery().in(ZFilesStatic::getFileId, idlist).list();
        for (ZFilesStatic file : list) {
            if (file.getFileType().equals("1")) {
                // 删除文件时，物理删除文件
                RemoveFileUtils.remove(file.getFileUrl());
            }
        }
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
            JSONObject paramObj = new JSONObject();
            if (StringUtils.hasText(params)) {
                paramObj = JSONUtil.parseObj(params, true);
            }

            List<ZFilesStaticDTO> list = zFilesStaticMapper.list(paramObj);
            // 转换成导出excel实体
            List<ZFilesStaticExcelOutDTO> dataList = list.stream()
                    .map(d -> JSONUtil.toBean(JSONUtil.parseObj(d, true), ZFilesStaticExcelOutDTO.class))
                    .collect(Collectors.toList());
            if (dataList == null || dataList.size() <= 0) {
                // 未查到数据时，模拟一行空数据
                dataList.add(new ZFilesStaticExcelOutDTO());
            }
            // 第一行标题
            String title = "静态资源文件表";
            // 写入导出excel文件
            ExcelWriteUtils.write(path, title, dataList, ZFilesStaticExcelConstant.EXPORT_EXCEL_COLUMN);
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
    public String importExcel(HttpServletRequest request) {
        // 1. 读取导入数据
        int startRowIdx = 2;
        List<ZFilesStaticExcelImportDTO> importData =
                ExcelReadUtils.read(request, 1, startRowIdx, ZFilesStaticExcelImportDTO.class, ZFilesStaticExcelConstant.IMPORT_EXCEL_COLUMN);
        if (importData == null || importData.isEmpty()) {
            return "Excel文件中没有数据！";
        }
        // 2. 必填字段校验
        String errorMsg = "";
        int currentRowIdx = startRowIdx;
        if (ZFilesStaticExcelConstant.IMPORT_REQUIRED_COLUMN.size() > 0) {
            for (ZFilesStaticExcelImportDTO entity : importData) {
                currentRowIdx++;
                JSONObject rowData = JSONUtil.parseObj(entity, true);
                List<String> emptyColName = new ArrayList<>();
                for (Map.Entry<String, String> col : ZFilesStaticExcelConstant.IMPORT_REQUIRED_COLUMN.entrySet()) {
                    if (!StringUtils.hasText(rowData.getStr(col.getKey()))) {
                        emptyColName.add(col.getValue());
                    }
                }
                if (emptyColName.size() > 0) {
                    errorMsg += "第" + currentRowIdx + "行，必填字段[" + String.join(",", emptyColName) + "]不能为空！<br/>";
                }
            }
        }
        if (StringUtils.hasText(errorMsg)) {
            return errorMsg;
        }
        // 3. 保存
        List<ZFilesStatic> saveData = importData.stream().map(obj -> {
            ZFilesStatic o = JSONUtil.toBean(JSONUtil.parseObj(obj, true), ZFilesStatic.class);
            o.setFileId(GuidUtils.getUuid());
            o.setCreateTime(LocalDateTime.now());
            return o;
        }).collect(Collectors.toList());
        zFilesStaticMapper.saveList(saveData);
        return "";// 导入成功，返回空字符串
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
            String path = FilePathConfig.SAVE_PATH + "/importTemp/excel/"
                    + DateUtil.format(new Date(), "yyyyMMdd") + "/" + GuidUtils.getUuid32() + ".xlsx";
            // 第一行标题
            String title = "静态资源文件表-导入模板";
            // 写入模板字段行
            ExcelWriteUtils.writeTemplate(path, title, ZFilesStaticExcelConstant.IMPORT_EXCEL_COLUMN);
            // 生成模板成功，返回模板地址
            return FilePathConfig.switchUrl(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }

}
