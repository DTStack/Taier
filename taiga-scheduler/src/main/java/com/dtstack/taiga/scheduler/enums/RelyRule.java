package com.dtstack.taiga.scheduler.enums;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 3:52 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum RelyRule {
    RUN_FINISH(1,"父实例运行完成，可以运行"),RUN_SUCCESS(2,"父实例运行成功，可以运行");

    private final Integer type;

    private final String msg;

    RelyRule(Integer type, String msg) {
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
