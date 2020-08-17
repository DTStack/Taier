package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/8/17 10:03 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum  JobRunStatus {

    CREATE(0,"创建"),JOIN_THE_TEAM(1,"入队"),LEAVE_THE_TEAM(2,"出队");

    private Integer code;

    private String msg;

    JobRunStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
