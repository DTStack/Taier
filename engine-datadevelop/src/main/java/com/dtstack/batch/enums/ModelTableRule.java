package com.dtstack.batch.enums;

import java.util.List;

/**
 * @author sanyue
 */
public enum ModelTableRule {
    /**
     * 模型层级
     */
    GRADE(1,"层级"),
    /**
     * 主题域
     */
    SUBJECT(2, "主题域"),
    /**
     * 刷新频率
     */
    REFRESH_RATE(3, "刷新频率"),
    /**
     * 增量方式
     */
    INCRE_TYPE(4, "增量"),
    /**
     * 自定义
     */
    CUSTOM(5, "自定义");

    private int key;
    private String value;
    ModelTableRule(int key, String value){
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int key){
        for (ModelTableRule rule : ModelTableRule.values()){
            if (rule.getKey() == key){
                return rule.getValue();
            }
        }
        return null;
    }

    public static String concatString(List<Integer> result){
        StringBuilder str = new StringBuilder();
        for (ModelTableRule rule : ModelTableRule.values()){
            if (result.contains(rule.getKey())){
                str.append(rule.getValue()).append("不匹配 ");
            }
        }
        return str.toString();
    }

    public static ModelTableRule getByKey(int key) {
        for (ModelTableRule rule : ModelTableRule.values()){
            if (rule.getKey() == key){
                return rule;
            }
        }
        return null;
    }
}
