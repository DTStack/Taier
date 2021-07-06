package com.dtstack.batch.enums;

/**
 * 原子指标中的指标类型
 *
 * @author sanyue
 */
public enum ModelColumnType {
    /**
     * 原子指标
     */
    ATOM(1, "原子指标"),
    /**
     * 修饰词
     */
    qualify(2, "修饰词");

    private int type;
    private String value;

    ModelColumnType(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }


    public String getValue() {
        return value;
    }

    public static ModelColumnType getByType(int type) {
        for (ModelColumnType rule : ModelColumnType.values()) {
            if (rule.getType() == type) {
                return rule;
            }
        }
        return null;
    }
}
