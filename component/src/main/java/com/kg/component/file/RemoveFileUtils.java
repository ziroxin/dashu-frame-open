package com.kg.component.file;

import cn.hutool.core.io.FileUtil;

/**
 * 删除文件 - 工具类
 *
 * @author ziro
 * @date 2023-03-28 09:20:50
 */
public class RemoveFileUtils {

    /**
     * 根据访问路径删除文件
     *
     * @param fileUrl 文件访问地址
     * @return 是否删除成功
     */
    public static boolean remove(String fileUrl) {
        try {
            String fileRealPath = FilePathConfig.switchSavePath(fileUrl);
            if (FileUtil.isFile(fileRealPath)) {
                FileUtil.del(fileRealPath);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
