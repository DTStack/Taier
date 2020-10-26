package com.dtstack.engine.sql;

/**
 * 分区操作描述
 *
 * @author jiangbo
 */
public class PartCondition {

    /**
     * 分区字段
     */
    private String key;

    /**
     * 字段值
     */
    private String value;

    /**
     * 操作
     */
    private String operate;

    public PartCondition(String key, String operate, String value) {
        this.key = key;
        this.value = value;
        this.operate = operate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    @Override
    public String toString() {
        return "PartCondition{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", operate='" + operate + '\'' +
                '}';
    }
}
