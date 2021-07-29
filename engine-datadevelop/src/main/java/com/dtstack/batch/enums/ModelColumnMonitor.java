package com.dtstack.batch.enums;

import java.util.List;

/**
 * @author sanyue
 */
public enum ModelColumnMonitor {

    /**
     * 字段名称不合理
     */
    BAD_NAME(1, "字段名称不合理"),

    /**
     * 字段类型不合理
     */
    BAD_DATA_TYPE(2, "字段类型不合理"),

    /**
     * 字段描述不合理
     */
    BAD_DESC(3, "字段描述不合理");

    private int type;

    private String value;

    ModelColumnMonitor(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public static String concatString(List<Integer> types) {
        StringBuilder str = new StringBuilder();
        for (ModelColumnMonitor modelColumnMonitor : ModelColumnMonitor.values()) {
            if (types.contains(modelColumnMonitor.getType())) {
                str.append(modelColumnMonitor.getValue()).append(" ");
            }
        }
        return str.toString();
    }

    public String getValue() {
        return value;
    }
}
