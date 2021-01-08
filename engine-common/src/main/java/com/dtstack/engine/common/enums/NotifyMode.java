package com.dtstack.engine.common.enums;

/**
 * @Auther: dazhi
 * @Date: 2020/10/10 10:59 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public enum NotifyMode {
    NORMAL(1), UNREAD(2), READ(3);

    private int mode;

    NotifyMode(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }
}
