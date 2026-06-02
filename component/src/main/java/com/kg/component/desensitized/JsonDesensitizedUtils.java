package com.kg.component.desensitized;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ziro
 * @date 2026-06-02 15:08:15
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JsonDesensitizedUtils extends JsonSerializer<String> implements ContextualSerializer {

    /**
     * 脱敏类型
     */
    private DesensitizedType type;

    /**
     * 脱敏配置（静态变量，由外部初始化）
     */
    private static DesensitizedConfig config;

    /**
     * 设置脱敏配置（由Spring配置类调用）
     */
    public static void setConfig(DesensitizedConfig desensitizedConfig) {
        config = desensitizedConfig;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        // 为空直接跳过
        if (beanProperty != null) {
            // 非 String 类直接跳过
            if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
                JsonDesensitized jsonDesensitized = beanProperty.getAnnotation(JsonDesensitized.class);
                if (jsonDesensitized == null) {
                    jsonDesensitized = beanProperty.getContextAnnotation(JsonDesensitized.class);
                }
                if (jsonDesensitized != null) {
                    // 如果能得到注解，就将注解的 value 传入 SensitiveSerialize
                    return new JsonDesensitizedUtils(jsonDesensitized.value());
                }
            }
            return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return serializerProvider.findNullValueSerializer(beanProperty);
    }

    @Override
    public void serialize(String str, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        switch (this.type) {
            case CHINESE_NAME:
                // 中文姓名脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getChineseName() : "0,1"));
                break;
            case ENGLISH_NAME:
                // 英文名脱敏： 1. 只有一段的名字，保留首字母; 2. 两段或者以上的名字，脱敏第一段.
                jsonGenerator.writeString(JsonDesensitizedUtils.englishName(str));
                break;
            case ID_CARD:
                // 身份证号脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getIdCard() : "6,4"));
                break;
            case MOBILE_PHONE:
                // 手机号脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getMobilePhone() : "3,4"));
                break;
            case FIXED_PHONE:
                // 座机号脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getFixedPhone() : "3,2"));
                break;
            case BANK_CARD:
                // 银行卡号脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getBankCard() : "4,4"));
                break;
            case CAR_LICENSE:
                // 车牌号脱敏：根据配置保留前N位和后M位
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getCarLicense() : "2,3"));
                break;
            case EMAIL:
                // 邮箱脱敏：保留 @ 后的内容
                jsonGenerator.writeString(JsonDesensitizedUtils.email(str));
                break;
            case ADDRESS:
                // 地址脱敏：地址脱敏: 长度大于 12 时，只显示前 6 位，不足 12 位显示不超过 50%，其他隐藏
                jsonGenerator.writeString(JsonDesensitizedUtils.address(str));
                break;
            case PASSWORD:
                // 密码脱敏：根据配置保留前N位和后M位，中间用*代替
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getPassword() : "1,1"));
                break;
            case OTHER:
                // 其他脱敏规则：根据配置保留前N位和后M位，中间用*代替
                jsonGenerator.writeString(desensitizeWithConfig(str, config != null ? config.getOther() : "2,2"));
                break;
            default:
                jsonGenerator.writeString(str);
        }
    }

    /**
     * 根据配置进行脱敏：保留前N位和后M位，中间用*代替
     *
     * @param str       原始字符串
     * @param configStr 配置格式："前保留位数,后保留位数"，如 "3,4"
     * @return 脱敏后的字符串
     */
    private String desensitizeWithConfig(String str, String configStr) {
        if (StrUtil.isBlank(str)) {
            return "";
        }

        // 解析配置
        String[] parts = configStr.split(",");
        if (parts.length != 2) {
            // 配置格式错误，返回原字符串
            return str;
        }

        try {
            int keepStart = Integer.parseInt(parts[0].trim());
            int keepEnd = Integer.parseInt(parts[1].trim());

            int length = str.length();

            // 如果字符串长度小于等于要保留的位数之和，返回原字符串
            if (length <= keepStart + keepEnd) {
                return str;
            }

            // 计算需要隐藏的起始和结束位置
            int hideStart = keepStart;
            int hideEnd = length - keepEnd;

            // 执行脱敏
            return StrUtil.hide(str, hideStart, hideEnd);
        } catch (NumberFormatException e) {
            // 配置解析失败，返回原字符串
            return str;
        }
    }

    /**
     * 英文名脱敏：
     * 1. 只有一段的名字，保留首字母
     * 2. 两段或者以上的名字，脱敏第一段
     */
    public static String englishName(String fullName) {
        if (StrUtil.isBlank(fullName)) {
            return "";
        } else {
            // 把英文名，根据大写字母，拆成List
            Matcher matcher = Pattern.compile("[A-Z]").matcher(fullName);
            List<String> nameList = new ArrayList<>();
            int i = 0;
            while (matcher.find()) {
                int position = matcher.start();
                if (position > 0) {
                    nameList.add(fullName.substring(i, position));
                    i = position;
                }
            }
            if (i < fullName.length()) {
                nameList.add(fullName.substring(i, fullName.length()));
            }
            // 开始脱敏处理
            if (nameList.size() > 1) {
                // 两段或者以上的名字，脱敏第一段。
                String result = "";
                for (int j = 0; j < nameList.size(); j++) {
                    if (j == 0) {
                        result += StrUtil.hide(nameList.get(j), 0, nameList.get(j).length());
                    } else {
                        result += nameList.get(j);
                    }
                }
                return result;
            } else {
                // 只有一段的名字，保留首字母，后面显示 *
                return StrUtil.hide(fullName, 1, fullName.length());
            }
        }
    }

    /**
     * 邮箱脱敏：保留 @ 后的内容
     */
    public static String email(String email) {
        if (StrUtil.isBlank(email)) {
            return "";
        } else {
            int index = StrUtil.indexOf(email, '@');
            return index <= 1 ? email : StrUtil.hide(email, 0, index);
        }
    }

    /**
     * 地址脱敏: 长度大于 12 时，只显示前 6 位，不足 12 位显示不超过 50%，其他隐藏
     */
    public static String address(String address) {
        if (StrUtil.isBlank(address)) {
            return "";
        } else {
            int length = address.length();
            if (length <= 12) {
                return StrUtil.hide(address, length / 2, length);
            } else {
                return StrUtil.hide(address, 6, length);
            }
        }
    }

    /**
     * 其他脱敏规则：显示前 1/3 和后 1/3 其他用*代替
     */
    public static String other(String other) {
        if (StrUtil.isBlank(other)) {
            return "";
        } else {
            int length = other.length();
            return StrUtil.hide(other, length / 3, length - length / 3);
        }
    }

    public static void main(String[] args) {
        String str = "11223456789";
        System.out.println(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 4, str.length() - 2));
        System.out.println(englishName("Dsdfsad Asdlfkj Osldkfz Klsdkfx"));
        System.out.println(email("abcdef@qq.com"));
        System.out.println(address("山东省"));
        System.out.println(address("山东省潍坊市"));
        System.out.println(address("山东省XX市XX县"));
        System.out.println(address("山东省XX市XX县XX大厦"));
        System.out.println(address("山东省XX市XX县XX大厦XXXX"));
        System.out.println("------------");
        System.out.println(other("abcdef"));
    }
}
