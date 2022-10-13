package com.dtstack.taier.flink.base.enums;

/**
 * @description: session state (healthy、unhealthy)
 * @program: engine-all
 * @author: lany
 * @create: 2021/07/10 14:24
 */
public enum SessionState {
    // session is ok
    HEALTHY(true),

    // session is not ok
    UNHEALTHY(false);

    private final boolean state;

    SessionState(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return this.state;
    }
}
