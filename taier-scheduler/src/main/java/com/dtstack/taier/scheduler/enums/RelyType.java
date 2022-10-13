package com.dtstack.taier.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 3:48 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RelyType {
    SELF_RELIANCE(1, "自依赖"), UPSTREAM(2, "上游实例"), UPSTREAM_NEXT_JOB(3, "上游任务的下一个周期key");

    private final Integer type;

    private final String msg;

    RelyType(Integer type, String msg) {
        this.type = type;
        this.msg = msg;
    }

    public Integer getType() {
        return type;
    }

    public String getMsg() {
        return msg;
    }
}
