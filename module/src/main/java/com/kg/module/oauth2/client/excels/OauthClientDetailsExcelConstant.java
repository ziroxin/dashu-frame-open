package com.kg.module.oauth2.client.excels;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Excel使用常量 - Oauth2客户端信息表
 *
 * @author ziro
 * @date 2023-09-12
 */
public class OauthClientDetailsExcelConstant {
    /**
     * 导出所需的字段信息
     */
    public static LinkedHashMap<String, String> EXPORT_EXCEL_COLUMN = new LinkedHashMap<>();
    /**
     * 导入所需字段信息
     */
    public static Map<String, String> IMPORT_EXCEL_COLUMN = new HashMap<>();

    static {
        // 初始化导出字段
        EXPORT_EXCEL_COLUMN.put("clientId", "客户端id");
        EXPORT_EXCEL_COLUMN.put("resourceIds", "资源ids");
        EXPORT_EXCEL_COLUMN.put("clientSecret", "客户端Secret");
        EXPORT_EXCEL_COLUMN.put("scope", "权限范围(read,write)");
        EXPORT_EXCEL_COLUMN.put("authorizedGrantTypes", "授权模式(authorization_code,password,refresh_token)");
        EXPORT_EXCEL_COLUMN.put("webServerRedirectUri", "客户端回调地址");
        EXPORT_EXCEL_COLUMN.put("authorities", "授权用户/角色");
        EXPORT_EXCEL_COLUMN.put("accessTokenValidity", "access_token有效期，单位秒");
        EXPORT_EXCEL_COLUMN.put("refreshTokenValidity", "refresh_token有效期，单位秒");
        EXPORT_EXCEL_COLUMN.put("additionalInformation", "备注信息，必须是JSON格式");
        EXPORT_EXCEL_COLUMN.put("autoapprove", "是否自动授权(true自动授权;false需要用户手动确认)");
        // 初始化导入字段
        IMPORT_EXCEL_COLUMN.put("客户端id", "clientId");
        IMPORT_EXCEL_COLUMN.put("资源ids", "resourceIds");
        IMPORT_EXCEL_COLUMN.put("客户端Secret", "clientSecret");
        IMPORT_EXCEL_COLUMN.put("权限范围(read,write)", "scope");
        IMPORT_EXCEL_COLUMN.put("授权模式(authorization_code,password,refresh_token)", "authorizedGrantTypes");
        IMPORT_EXCEL_COLUMN.put("客户端回调地址", "webServerRedirectUri");
        IMPORT_EXCEL_COLUMN.put("授权用户/角色", "authorities");
        IMPORT_EXCEL_COLUMN.put("access_token有效期，单位秒", "accessTokenValidity");
        IMPORT_EXCEL_COLUMN.put("refresh_token有效期，单位秒", "refreshTokenValidity");
        IMPORT_EXCEL_COLUMN.put("备注信息，必须是JSON格式", "additionalInformation");
        IMPORT_EXCEL_COLUMN.put("是否自动授权(true自动授权;false需要用户手动确认)", "autoapprove");
    }

}