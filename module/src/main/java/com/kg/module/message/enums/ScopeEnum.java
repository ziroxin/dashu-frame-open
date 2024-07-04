package com.kg.module.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ziro
 * @date 2024/7/3 15:47
 */
@Getter
@AllArgsConstructor
public enum ScopeEnum {
    ALL("all", "全部"),
    CHILDREN("children", "子节点"),
    SELF_AND_CHILDREN("selfAndChildren", "本节点及子节点");

    private String code;
    private String desc;
}
