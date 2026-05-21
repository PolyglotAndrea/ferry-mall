package com.ferry.framework.web.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DataScopeEnum {

    ALL(1, "全部数据"),
    DEPT_ONLY(2, "本部门数据"),
    DEPT_AND_CHILD(3, "本部门及以下数据"),
    SELF_ONLY(4, "仅本人数据"),
    CUSTOM(5, "自定义数据");

    private final int scope;
    private final String description;

    public static DataScopeEnum of(Integer scope) {
        if (scope == null) {
            return ALL;
        }
        for (DataScopeEnum item : values()) {
            if (item.scope == scope) {
                return item;
            }
        }
        return ALL;
    }
}
