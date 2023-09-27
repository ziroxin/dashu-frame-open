package com.kg.core.zupload.dto;

import com.kg.component.file.dto.FileChunkDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ziro
 * @date 2023/9/26 16:29
 */
@Getter
@Setter
public class FileSecondDTO extends FileChunkDTO {
    /**
     * 是否拷贝文件
     */
    private boolean isCopy;
    /**
     * 拷问后文件地址
     */
    private String copyPath;
}
