package com.dtstack.batch.enums;

/**
 * @author toutian
 * @date 2019/5/07
 */
public enum YarnAppLogType {

    /**
     * 数据输出
     */
    DTSTDOUT(1),

    /**
     * 数据错误
     */
    DTERROR(2),

    /**
     * 标准输出
     */
    STDOUT(3),

    /**
     * 错误文件
     */
    STDERR(4);

    private Integer type;

    YarnAppLogType(Integer type) {
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public static YarnAppLogType getType(String name) {
        for (YarnAppLogType type : YarnAppLogType.values()) {
            if (type.name().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
