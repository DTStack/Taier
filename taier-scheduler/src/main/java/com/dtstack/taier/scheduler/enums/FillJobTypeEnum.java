package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/9/16 3:02 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum FillJobTypeEnum {
    // 补数据类型 0 默认值 周期实例，立即运行等非补数据实例的默认值 1 可执行补数据实例 2 不可执行的补数据实例（例如黑名单，中间实例等）
    DEFAULT(0, "默认值"),
    RUN_JOB(1, "可执行补数据实例"),
    MIDDLE_JOB(2, "中间实例"),
    ;

    private final Integer type;

    private final String name;

    FillJobTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
