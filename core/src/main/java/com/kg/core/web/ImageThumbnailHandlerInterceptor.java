package com.kg.core.web;

import com.kg.component.file.FilePathConfig;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 图像缩略图处理程序拦截器（支持jpg、jpeg、png、gif格式）
 * 使用举例： https://图片地址?width=500&height=400
 *
 * @author ziro
 * @date 2025/10/24 10:32
 */
public class ImageThumbnailHandlerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String queryString = request.getQueryString();
        // 检查请求路径是否是图片并且带有查询参数
        String extend = requestURI.substring(requestURI.lastIndexOf(".") + 1).toLowerCase();
        MediaType mediaType = getMediaTypeForExtension(extend);
        if (queryString != null && mediaType != null) {
            String[] params = queryString.split("&");
            int width = 0;
            int height = 0;
            for (String param : params) {
                if (param.startsWith("width=")) {
                    width = Integer.parseInt(param.substring("width=".length()));
                } else if (param.startsWith("height=")) {
                    height = Integer.parseInt(param.substring("height=".length()));
                }
            }
            // 如果width和height都被设置，则生成缩略图
            if (width > 0 && height > 0) {
                String parentUrl = requestURI.substring(0, requestURI.lastIndexOf("/")) + "/";
                String parentSavePath = FilePathConfig.switchSavePath(parentUrl) + "/";
                String fileName = requestURI.substring(requestURI.lastIndexOf("/") + 1);
                String baseFileName = fileName.substring(0, fileName.lastIndexOf("."));
                String thumbnailFileName = baseFileName + "_" + width + "x" + height + "." + extend;
                String thumbnailFilePath = parentSavePath + thumbnailFileName;
                File thumbnailFile = new File(thumbnailFilePath);
                if (thumbnailFile.exists()) {
                    // 如果缩略图文件存在，则重定向到该文件的URL地址
                    String thumbnailUrl = parentUrl + thumbnailFileName;
                    response.sendRedirect(thumbnailUrl);
                    return false; // 中止后续处理
                } else {
                    // 如果缩略图文件不存在，则生成缩略图并保存为新文件
                    String originalFilePath = parentSavePath + fileName;
                    File originalFile = new File(originalFilePath);
                    if (originalFile.exists()) {
                        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                            // 生成缩略图
                            Thumbnails.of(originalFile).size(width, height).outputFormat(extend).toOutputStream(outputStream);
                            byte[] thumbnailBytes = outputStream.toByteArray();
                            // 保存缩略图文件
                            try (FileOutputStream fileOutputStream = new FileOutputStream(thumbnailFile)) {
                                fileOutputStream.write(thumbnailBytes);
                            }
                            // 重定向到生成的缩略图的URL地址
                            String thumbnailUrl = parentUrl + thumbnailFileName;
                            response.sendRedirect(thumbnailUrl);
                            return false; // 中止后续处理
                        } catch (IOException e) {
                            e.printStackTrace();
                            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to generate thumbnail");
                            return false;
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                        return false;
                    }
                }
            }
        }
        return true; // 继续处理请求
    }

    private MediaType getMediaTypeForExtension(String extension) {
        switch (extension) {
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            // 添加其他支持的图片格式
            default:
                return null;
        }
    }
}
