package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/3/11 1:49 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum TaskRuleEnum {

    NO_RULE(0,"无规则"), WEAK_RULE(1,"弱规则"),STRONG_RULE(2,"强规则");

    private Integer code;

    private String msg;

    TaskRuleEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
