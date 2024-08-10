package com.kg.core.zupload.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileChunkDTO;
import com.kg.component.file.utils.UploadFileChunksUtils;
import com.kg.component.redis.RedisUtils;
import com.kg.component.utils.GuidUtils;
import com.kg.core.base.controller.BaseController;
import com.kg.core.exception.BaseException;
import com.kg.core.zupload.dto.FileSecondDTO;
import com.kg.module.files.entity.ZFiles;
import com.kg.module.files.service.ZFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * 文件秒传 - 控制器
 *
 * @author ziro
 * @date 2022-09-20 11:20:05
 */
@Api(tags = "ZUploadSecondController", value = "文件秒传控制器", description = "文件秒传控制器")
@RestController
@RequestMapping("upload")
public class ZUploadSecondController implements BaseController {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ZFilesService zFilesService;

    @ApiOperation(value = "upload/second/md5", notes = "文件秒传校验", httpMethod = "GET")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "secondMd5", value = "秒传文件MD5", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isCopy", value = "是否复制", paramType = "query", required = true, dataType = "Boolean")
    })
    @GetMapping("second/md5")
    public FileSecondDTO secondMd5(@RequestParam(value = "secondMd5", required = true) String secondMd5,
                                   @RequestParam(value = "path", required = false) String path,
                                   @RequestParam(value = "isCopy", required = true, defaultValue = "false") Boolean isCopy) {
        String dirName = StringUtils.hasText(path) ? path : "seconds";
        Optional<ZFiles> files = zFilesService.lambdaQuery().eq(ZFiles::getFileMd5, secondMd5).last("LIMIT 1").oneOpt();
        if (files.isPresent()) {
            try {
                // 文件已存在，跳过上传
                FileSecondDTO result = JSONUtil.toBean(JSONUtil.parseObj(files.get()), FileSecondDTO.class);
                // 处理文件是否拷贝
                return saveZFiles(result, dirName, isCopy, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 其他情况，返回null（表示文件不存在，需要重新上传）
        return null;
    }

    @ApiOperation(value = "upload/second/chunks", notes = "分片上传 - 断点续传", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "request", value = "请求", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "path", value = "路径", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "chunkMD5", value = "分片MD5", paramType = "query", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isCopy", value = "是否复制", paramType = "query", required = true, dataType = "Boolean")
    })
    @PostMapping("second/chunks")
    public FileSecondDTO secondChunks(HttpServletRequest request, String path, String chunkMD5,
                                      @RequestParam(value = "isCopy", required = true, defaultValue = "false") Boolean isCopy) throws BaseException {
        try {
            // 获取分片信息
            String chunkNumber = request.getParameter("chunk");
            String tempFileName = request.getParameter("name");
            String uploadId = request.getParameter("uploadId");// 本次上传的唯一id
            String totalChunks = request.getParameter("chunks");
            String dirName = StringUtils.hasText(path) ? path : "seconds";
            if (StringUtils.hasText(chunkMD5)) {
                // 有md5参数，则检测是否已上传
                String md5 = chunkMD5.toUpperCase();
                if (redisUtils.hasKey(md5)) {
                    FileSecondDTO result = JSONUtil.toBean(JSONUtil.parseObj(redisUtils.get(md5)), FileSecondDTO.class);
                    // 拷贝redis中已缓存的分片
                    String tempOldFilePath = FilePathConfig.switchSavePath(result.getFileUrl());
                    UploadFileChunksUtils.copyChunkFile(tempOldFilePath, tempFileName, uploadId, chunkNumber);
                    // 判断是否需要合并
                    if (UploadFileChunksUtils.isAllChunksUploaded(tempFileName, uploadId, totalChunks)) {
                        // 全部上传完成，合并，并清理redis
                        if (redisUtils.hasKey(uploadId)) {
                            for (String md5Key : redisUtils.get(uploadId).toString().split(",")) {
                                redisUtils.delete(md5Key);
                            }
                            redisUtils.delete(uploadId);
                        }
                        FileChunkDTO fileChunkDTO = UploadFileChunksUtils.mergeChunks(dirName, tempFileName, uploadId, totalChunks);
                        FileSecondDTO secondDTO = JSONUtil.toBean(JSONUtil.parseObj(fileChunkDTO), FileSecondDTO.class);
                        // 保存【文件秒传表】，返回结果
                        return saveZFiles(secondDTO, dirName, isCopy, true);
                    } else {
                        // 未上传完成所有上传，返回分片上传结果
                        result.setChunkNumber(chunkNumber);
                        result.setFileOldName(tempFileName);
                        result.setMerged(false);
                        return result;
                    }
                } else {
                    // redis中无该md5，返回null，前端需上传新文件
                    return null;
                }
            } else {
                // 无md5，则上传分片文件
                System.out.println("新上传");
                FileChunkDTO fileChunkDTO = UploadFileChunksUtils.upload(request, dirName, chunkNumber, tempFileName, uploadId, totalChunks);
                FileSecondDTO result = JSONUtil.toBean(JSONUtil.parseObj(fileChunkDTO), FileSecondDTO.class);
                if (fileChunkDTO.isMerged()) {
                    // 已合并完成，清理redis缓存
                    if (redisUtils.hasKey(uploadId)) {
                        for (String md5Key : redisUtils.get(uploadId).toString().split(",")) {
                            redisUtils.delete(md5Key);
                        }
                        redisUtils.delete(uploadId);
                    }
                    // 保存【文件秒传表】，返回结果
                    return saveZFiles(result, dirName, isCopy, true);
                } else {
                    // 未合并，缓存分片文件信息（保存1天）
                    redisUtils.set(result.getFileMd5(), result, 24 * 60 * 60L);
                    String md5s = redisUtils.hasKey(uploadId) ? redisUtils.get(uploadId).toString() + "," : "";
                    redisUtils.set(uploadId, md5s + result.getFileMd5(), 24 * 60 * 60L);
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "分片上传文件失败！请重试");
        }
    }

    /**
     * 保存【文件秒传表】，返回结果
     *
     * @param result  文件信息
     * @param dirName 保存的文件夹
     * @param isCopy  是否拷贝业务文件
     * @param isSave  是否保存【文件秒传表】
     */
    private FileSecondDTO saveZFiles(FileSecondDTO result, String dirName, Boolean isCopy, Boolean isSave) throws IOException {
        result.setFileName(GuidUtils.getUuid32() + "." + result.getFileExtend());
        // 是否保存【文件秒传表】
        if (isSave) {
            ZFiles saveFile = JSONUtil.toBean(JSONUtil.toJsonStr(result), ZFiles.class);
            saveFile.setFileId(GuidUtils.getUuid());
            saveFile.setCreateTime(LocalDateTime.now());
            zFilesService.save(saveFile);
        }
        // 是否拷贝业务文件
        result.setCopy(isCopy);
        if (isCopy) {
            String copyPath = FilePathConfig.SAVE_PATH
                    + "/" + dirName
                    + "/" + DateUtil.format(new Date(), "yyyyMMdd")
                    + "/" + result.getFileName();
            copyPath = copyPath.replaceAll("//", "/");
            File saveFile = new File(copyPath);
            FileUtil.mkParentDirs(saveFile);
            FileCopyUtils.copy(new File(FilePathConfig.switchSavePath(result.getFileUrl())), saveFile);
            result.setCopyPath(FilePathConfig.switchUrl(copyPath));
            return result;
        }
        return result;
    }
}
