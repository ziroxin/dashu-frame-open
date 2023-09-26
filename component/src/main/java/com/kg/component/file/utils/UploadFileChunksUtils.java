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
     * @param request      请求体
     * @param dirName      自定义文件夹
     * @param chunkNumber  分片编号
     * @param tempFileName 原文件名
     * @param uploadId     本次上传唯一id
     * @param totalChunks  总分片数
     * @return 上传文件列表
     */
    public static FileChunkDTO upload(HttpServletRequest request, String dirName, String chunkNumber,
                                      String tempFileName, String uploadId, String totalChunks) throws IOException {
        FileChunkDTO result = new FileChunkDTO();
        // 获取分片文件
        MultipartFile multipartFile = ((MultipartHttpServletRequest) request).getFile("file");
        // 上传分片文件
        String chunkFileName = getFileChunkTempPath(tempFileName, uploadId, chunkNumber);
        FileUtil.mkParentDirs(chunkFileName);
        multipartFile.transferTo(new File(chunkFileName));

        // 是否已上传所有分片
        if (!isAllChunksUploaded(tempFileName, uploadId, totalChunks)) {
            // 返回分片信息
            result.setChunkNumber(chunkNumber);
            result.setFileOldName(tempFileName);// 原文件名
            result.setFileUrl(FilePathConfig.switchUrl(chunkFileName));// 分片临时文件-存储地址
            result.setFileMd5(FileMD5Utils.getFileMD5(chunkFileName));// 分片临时文件-MD5
            result.setMerged(false);
            return result;
        } else {
            // 合并文件
            return mergeChunks(dirName, tempFileName, uploadId, totalChunks);
        }
    }

    /**
     * 合并文件
     *
     * @param dirName      存储目录
     * @param tempFileName 原文件名
     * @param uploadId     本次上传唯一id
     * @param totalChunks  总分片数
     */
    public static FileChunkDTO mergeChunks(String dirName, String tempFileName, String uploadId, String totalChunks) throws IOException {
        // 合并，返回合并信息
        String extend = FileUtil.extName(tempFileName).toLowerCase();// 文件扩展名
        String saveFileName = GuidUtils.getUuid32() + "." + extend;
        String savePath = FilePathConfig.SAVE_PATH + "/" + dirName + "/" + DateUtil.format(new Date(), "yyyyMMdd") + "/" + saveFileName;
        FileUtil.mkParentDirs(savePath);
        // 调用合并文件方法
        mergeChunksSave(tempFileName, uploadId, totalChunks, savePath);
        // 合并完成，删除临时分片文件
        deleteChunks(tempFileName, uploadId, totalChunks);
        // 返回数据
        FileChunkDTO result = new FileChunkDTO();
        result.setFileMd5(FileMD5Utils.getFileMD5(savePath));
        result.setFileUrl(FilePathConfig.switchUrl(savePath));
        result.setFileOldName(tempFileName);
        result.setFileName(saveFileName);
        result.setFileExtend(extend);
        result.setFileSize(new File(savePath).length());
        result.setMerged(true);
        return result;
    }

    /**
     * 检查是否已接收到所有分片
     *
     * @param tempFileName 分片文件名
     * @param uploadId     本次上传唯一id
     * @param totalChunks  总分片数
     * @return 是否已接收所有分片
     */
    public static boolean isAllChunksUploaded(String tempFileName, String uploadId, String totalChunks) {
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = getFileChunkTempPath(tempFileName, uploadId, i + "");
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
     * @param tempFileName 分片文件名
     * @param uploadId     本次上传唯一id
     * @param totalChunks  总分片数
     * @param savePath     合并后保存文件
     */
    public static void mergeChunksSave(String tempFileName, String uploadId, String totalChunks, String savePath) throws IOException {
        File mergedFile = new File(savePath);
        FileOutputStream fos = new FileOutputStream(mergedFile, true);
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = getFileChunkTempPath(tempFileName, uploadId, i + "");
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
     * @param tempFileName 分片文件名
     * @param uploadId     本次上传唯一id
     * @param totalChunks  总分片数
     */
    public static void deleteChunks(String tempFileName, String uploadId, String totalChunks) {
        for (int i = 0; i < Integer.parseInt(totalChunks); i++) {
            String chunkFileName = getFileChunkTempPath(tempFileName, uploadId, i + "");
            File chunkFile = new File(chunkFileName);
            chunkFile.delete();
        }
    }

    /**
     * 拷贝分片文件
     *
     * @param oldFilePath  原文件路径
     * @param tempFileName 文件名
     * @param uploadId     本次上传唯一id
     * @param chunkNumber  分片编号
     */
    public static void copyChunkFile(String oldFilePath, String tempFileName, String uploadId, String chunkNumber) {
        String chunkFileName = getFileChunkTempPath(tempFileName, uploadId, chunkNumber);
        FileUtil.mkParentDirs(chunkFileName);
        FileUtil.copyFile(oldFilePath, chunkFileName);
    }

    /**
     * 分片文件临时存储地址
     *
     * @param tempFileName 原文件名
     * @param uploadId     本次上传唯一id
     * @param chunkNumber  分片编号
     * @return 分片文件临时存储地址
     */
    private static String getFileChunkTempPath(String tempFileName, String uploadId, String chunkNumber) {
        return FILE_CHUNK_TEMP_PATH + uploadId + "_" + tempFileName + "_" + chunkNumber;
    }

    /**
     * 删除 - 分片文件临时存储文件
     *
     * @param folder 文件夹地址
     */
    public static void deleteChunksByQuartz(String folder) {
        String tempFolder = FilePathConfig.SAVE_PATH + "/chunks/temp/" + folder;
        if (FileUtil.exist(tempFolder)) {
            FileUtil.del(tempFolder);
        }
    }
}
