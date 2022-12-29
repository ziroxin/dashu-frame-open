package com.kg.component.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import com.kg.component.utils.GuidUtils;
import org.springframework.util.Base64Utils;
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
 * 图片上传工具类
 *
 * @author ziro
 * @date 2022-07-02 17:44:07
 */
public class UploadImageUtils {

    /**
     * base64转存图片
     *
     * @param base64Data base64数据
     * @param dirName    自定义路径
     * @return 存储文件实体
     */
    public static FileDTO base64ToJpg(String base64Data, String dirName) {
        // 把base64文件，解析成bytes
        byte[] buffer = Base64Utils.decodeFromString(base64Data);
        // 然后保存
        return UploadFileUtils.bufferToFile(buffer, dirName, "jpg");
    }

    /**
     * 图片压缩上传
     *
     * @param request 请求体
     * @param dirName 自定义文件夹
     * @return 上传文件列表
     */
    public static List<FileDTO> upload(HttpServletRequest request, String dirName) throws IOException {
        return upload(request, dirName, true);
    }

    /**
     * 图片上传
     *
     * @param request    请求体
     * @param dirName    自定义文件夹
     * @param isCompress 是否进行图片压缩（为true时，图片文件自动压缩，非图片文件不处理）
     * @return 上传文件列表
     */
    public static List<FileDTO> upload(HttpServletRequest request, String dirName, boolean isCompress) throws IOException {
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

                // 判断文件扩展名
                String extend = FileType.getFileType(multipartFile.getBytes()).toLowerCase();
                if (FilePathConfig.UPLOAD_FILE_ALLOW_EXTEND.toLowerCase().indexOf(extend) < 0) {
                    throw new IOException("您上传的文件格式不正确！请检查");
                }
                file.setFileExtend(extend);

                // 判断是否图片格式
                if (FilePathConfig.DEFAULT_IMAGE_FILE_EXTEND.toLowerCase().indexOf(extend) < 0) {
                    throw new IOException("您上传的不是图片！请检查");
                }

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
                if (isCompress) {
                    // 压缩
                    Img.from(multipartFile.getInputStream())
                            .setQuality(0.6)// 压缩比率60%
                            .write(FileUtil.getOutputStream(saveFile));
                } else {
                    // 不压缩直接复制
                    FileCopyUtils.copy(multipartFile.getBytes(), saveFile);
                }
                // 文件访问地址
                file.setFileUrl(FilePathConfig.switchUrl(savePath));
                resultList.add(file);
            }
        }
        return resultList;
    }
}
