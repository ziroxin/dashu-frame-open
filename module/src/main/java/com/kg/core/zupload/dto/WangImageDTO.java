package com.kg.core.zupload.dto;

import cn.hutool.json.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * WangEditor上传图片的返回值
 *
 * @author ziro
 * @date 2023-02-20 09:07:36
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WangImageDTO {
    private int errno;
    private JSONObject data;
    private String message;

    /**
     * 上传成功
     *
     * @param url  图片src，必须
     * @param alt  图片描述文字，非必须
     * @param href 图片的链接，非必须
     * @return
     */
    public WangImageDTO success(String url, String alt, String href) {
        JSONObject upData = new JSONObject();
        upData.set("url", url);
        if (StringUtils.hasText(alt)) {
            upData.set("alt", alt);
        }
        if (StringUtils.hasText(href)) {
            upData.set("href", href);
        }
        return new WangImageDTO(0, upData, "上传成功！");
    }

    public WangImageDTO error(String errorMsg) {
        return new WangImageDTO(1, null, errorMsg);
    }

    /**
     * 上传成功返回值
     * {
     *     "errno": 0, // 注意：值是数字，不能是字符串
     *     "data": {
     *         "url": "xxx", // 图片 src ，必须
     *         "alt": "yyy", // 图片描述文字，非必须
     *         "href": "zzz" // 图片的链接，非必须
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
