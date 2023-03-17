package com.kg.component.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.kg.component.utils.GuidUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 文件上传 - 工具类
 *
 * @author ziro
 * @date 2022-07-02 14:54:24
 */
public class UploadFileUtils {
    /**
     * 文件上传处理
     *
     * @param request 请求体
     * @param dirName 自定义文件夹
     * @return 上传文件列表
     */
    public static List<FileDTO> upload(HttpServletRequest request, String dirName) throws IOException {
        List<FileDTO> resultList = new ArrayList<>();
        // 上传文件请求处理
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        multipartRequest.setCharacterEncoding("UTF-8");
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> fileInput : fileMap.entrySet()) {
            // 循环处理待上传列表
            String fileInputName = fileInput.getKey();
            List<MultipartFile> fileList = multipartRequest.getFiles(fileInputName);
            for (MultipartFile multipartFile : fileList) {
                // 上传文件实体
                FileDTO file = new FileDTO();
                String oldFileName = FilePathConfig.removeSpec(multipartFile.getOriginalFilename());
                if (!StringUtils.hasText(oldFileName)) {
                    throw new IOException("上传文件名不正确！请检查");
                }
                // 旧文件名
                file.setFileOldName(oldFileName);

                // 获取文件扩展名
                String extend = FileUtil.extName(oldFileName).toLowerCase();
                // 根据头判断格式是否正确
                String extendStr = FileType.getFileType(multipartFile.getBytes());
                if (StringUtils.hasText(extendStr) && extendStr.indexOf(extend) < 0) {
                    throw new IOException("您上传的文件格式与扩展名不符！请检查");
                }
                if (FilePathConfig.UPLOAD_FILE_ALLOW_EXTEND.toLowerCase().indexOf(extend) < 0) {
                    throw new IOException("您上传的文件格式不被允许！请检查");
                }
                file.setFileExtend(extend);

                // 新文件名
                file.setFileName(GuidUtils.getUuid32() + "." + extend);
                // 文件大小
                file.setFileSize(multipartFile.getSize());

                // 准备保存文件
                String savePath = FilePathConfig.SAVE_PATH
                        + "/" + dirName
                        + "/" + DateUtil.format(new Date(), "yyyyMMdd")
                        + "/" + file.getFileName();
                File saveFile = new File(savePath.replaceAll("//", "/"));
                FileUtil.mkParentDirs(saveFile);
                FileCopyUtils.copy(multipartFile.getBytes(), saveFile);
                // 文件访问地址
                file.setFileUrl(FilePathConfig.switchUrl(savePath));
                resultList.add(file);
            }
        }
        return resultList;
    }

    /**
     * byte[]转存文件
     *
     * @param buffer  源文件byte[]
     * @param dirName 自定义路径
     * @param extend  文件扩展名
     * @return 存储文件实体
     */
    public static FileDTO bufferToFile(byte[] buffer, String dirName, String extend) {
        try {
            // 上传文件实体
            FileDTO file = new FileDTO();
            file.setFileExtend(extend);
            // 处理文件名、路径
            file.setFileName(GuidUtils.getUuid32() + "." + extend);

            // 准备保存文件
            String savePath = FilePathConfig.SAVE_PATH
                    + "/" + dirName
                    + "/" + DateUtil.format(new Date(), "yyyyMMdd")
                    + "/" + file.getFileName();
            File saveFile = new File(savePath.replaceAll("//", "/"));
            FileUtil.mkParentDirs(saveFile);
            FileCopyUtils.copy(buffer, saveFile);
            file.setFileSize(saveFile.length());
            file.setFileUrl(FilePathConfig.switchUrl(savePath));
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
