package com.dtstack.engine.api.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 10:19 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum MailType {

    SIMPLE(1), MIME(2);
    private int type;

    MailType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
