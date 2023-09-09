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
 * @date 2023-02-15 15:08:15
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
                // 中文姓名脱敏：保留最后 1 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 0, str.length() - 1));
                break;
            case ENGLISH_NAME:
                // 英文名脱敏： 1. 只有一段的名字，保留首字母; 2. 两段或者以上的名字，脱敏第一段.
                jsonGenerator.writeString(JsonDesensitizedUtils.englishName(str));
                break;
            case ID_CARD:
                // 身份证号脱敏：保留前 1 位和后 1 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 1, str.length() - 1));
                break;
            case MOBILE_PHONE:
                // 手机号脱敏：保留前 3 位和后 2 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 3, str.length() - 2));
                break;
            case FIXED_PHONE:
                // 座机号脱敏：保留前 4 位和后 2 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 4, str.length() - 2));
                break;
            case BANK_CARD:
                // 银行卡号脱敏：保留后 4 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 0, str.length() - 4));
                break;
            case CAR_LICENSE:
                // 车牌号脱敏：保留前 2 位（地区信息）和后 3 位
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.hide(str, 2, str.length() - 3));
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
                // 密码脱敏：全部*代替
                jsonGenerator.writeString(StrUtil.isBlank(str) ? "" : StrUtil.repeat('*', str.length()));
                break;
            case OTHER:
                // 其他脱敏规则：显示前 1/3 和后 1/3 其他用*代替
                jsonGenerator.writeString(JsonDesensitizedUtils.other(str));
                break;
            default:
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
