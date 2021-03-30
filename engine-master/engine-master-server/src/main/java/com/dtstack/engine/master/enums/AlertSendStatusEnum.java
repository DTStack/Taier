package com.dtstack.engine.master.enums;

/**
 * @Auther: dazhi
 * @Date: 2021/1/13 3:31 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum AlertSendStatusEnum {
    NO_SEND(0,"未发送"),SEND_SUCCESS(1,"发送成功"),SEND_FAILURE(2,"发送失败");

    private int type;

    private String name;

    AlertSendStatusEnum(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
