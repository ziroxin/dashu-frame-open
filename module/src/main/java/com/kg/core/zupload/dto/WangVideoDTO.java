package com.kg.core.zupload.dto;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * WangEditor上传视频的返回值
 *
 * @author ziro
 * @date 2023-02-20 09:07:36
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WangVideoDTO {
    private int errno;
    private JSONObject data;
    private String message;

    /**
     * 上传成功
     *
     * @param url    视频src，必须
     * @param poster 视频封面图片url，非必须
     * @return
     */
    public WangVideoDTO success(String url, String poster) {
        JSONObject upData = new JSONObject();
        upData.set("url", url);
        if (StringUtils.hasText(poster)) {
            upData.set("alt", poster);
        }
        return new WangVideoDTO(0, upData, "上传成功！");
    }

    public WangVideoDTO error(String errorMsg) {
        return new WangVideoDTO(1, null, errorMsg);
    }

    /**
     * 上传成功返回值
     * {
     *     "errno": 0, // 注意：值是数字，不能是字符串
     *     "data": {
     *         "url": "xxx", // 视频 src ，必须
     *         "poster": "xxx.png" // 视频封面图片 url ，可选
     *     }
     * }
     */

    /**
     * 上传失败返回值
     * {
     *     "errno": 1, // 只要不等于 0 就行
     *     "message": "失败信息"
     * }
     */
}
