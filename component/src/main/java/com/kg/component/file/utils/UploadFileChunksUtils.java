package com.kg.component.file.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.kg.component.file.FilePathConfig;
import com.kg.component.file.dto.FileChunkDTO;
import com.kg.component.utils.GuidUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 文件分片上传 - 工具类
 *
 * @author ziro
 * @date 2023/9/15 14:40
 */
public class UploadFileChunksUtils {
    /**
     * 分片上传临时文件存储地址
     */
    private static final String FILE_CHUNK_TEMP_PATH =
            FilePathConfig.SAVE_PATH + "/chunks/temp/" + DateUtil.format(new Date(), "yyyyMMdd") + "/";

    /**
     * 文件上传处理
     *
     * @param request 请求体
     * @param dirName 自定义文件夹
     * @return 上传文件列表
     */
    public static FileChunkDTO upload(HttpServletRequest request, String dirName,
                                      String chunkNumber, String tempFileName, String totalChunks) throws IOException {
        FileChunkDTO result = new FileChunkDTO();
        // 获取分片文件
        MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile("file");
        // 上传分片文件
        String chunkFileName = FILE_CHUNK_TEMP_PATH + tempFileName + "_" + chunkNumber;
        FileUtil.mkParentDirs(chunkFileName);
        multipartFile.transferTo(new File(chunkFileName));

        // 是否已上传所有分片
        if (!isAllChunksUploaded(FILE_CHUNK_TEMP_PATH, tempFileName, totalChunks)) {
            // 返回分片信息
            result.setChunkNumber(chunkNumber);
            result.setFileOldName(tempFileName);// 原文件名
            result.setFileUrl(FilePathConfig.switchUrl(chunkFileName));// 分片临时文件-存储地址
            result.setFileMd5(FileMD5Utils.getFileMD5(chunkFileName));// 分片临时文件-MD5
            result.setMerged(false);
            return result;
        } else {
            // 合并，返回合并信息
            String extend = FileUtil.extName(tempFileName).toLowerCase();// 文件扩展名
            String saveFileName = GuidUtils.getUuid32() + "." + extend;
            String savePath = FilePathConfig.SAVE_PATH + "/" + dirName + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + saveFileName;
            FileUtil.mkParentDirs(savePath);
            // 调用合并文件方法
            mergeChunks(FILE_CHUNK_TEMP_PATH, tempFileName, totalChunks, savePath);
            // 合并完成，删除临时分片文件
            deleteChunks(FILE_CHUNK_TEMP_PATH, tempFileName, totalChunks);
            // 返回数据
            result.setFileMd5(FileMD5Utils.getFileMD5(savePath));
            result.setFileUrl(FilePathConfig.switchUrl(savePath));
            result.setFileOldName(tempFileName);
            result.setFileName(saveFileName);
            result.setFileExtend(extend);
            result.setFileSize(new File(savePath).length());
            result.setMerged(true);
            return result;
        }
    }

    /**
     * 检查是否已接收到所有分片
     *
     * @param chunkTempPath 上传地址
     * @param tempFileName  分片文件名
     * @param totalChunks   总分片数
     * @return 是否已接收所有分片
     */
    public static boolean isAllChunksUploaded(String chunkTempPath, String tempFileName, String totalChunks) {
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = chunkTempPath + tempFileName + "_" + i;
            File chunkFile = new File(chunkFileName);
            if (!chunkFile.exists()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 合并分片文件
     *
     * @param chunkTempPath 上传地址
     * @param tempFileName  分片文件名
     * @param totalChunks   总分片数
     * @param savePath      合并后保存文件
     */
    public static void mergeChunks(String chunkTempPath, String tempFileName, String totalChunks, String savePath) throws IOException {
        File mergedFile = new File(savePath);
        FileOutputStream fos = new FileOutputStream(mergedFile, true);
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = chunkTempPath + tempFileName + "_" + i;
            File chunkFile = new File(chunkFileName);
            FileInputStream fis = new FileInputStream(chunkFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fis.close();
            chunkFile.delete();
        }
        fos.close();
    }

    /**
     * 删除分片上传的临时文件
     *
     * @param chunkTempPath 上传路径
     * @param tempFileName  分片文件名
     * @param totalChunks   总分片数
     */
    public static void deleteChunks(String chunkTempPath, String tempFileName, String totalChunks) {
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = chunkTempPath + tempFileName + "_" + i;
            File chunkFile = new File(chunkFileName);
            chunkFile.delete();
        }
    }

    public static void copyChunkFile(String filePath, String tempFileName, String chunkNumber) {
        String chunkFileName = FILE_CHUNK_TEMP_PATH + tempFileName + "_" + chunkNumber;
        FileUtil.mkParentDirs(chunkFileName);
        FileUtil.copyFile(filePath, chunkFileName);
    }
}
