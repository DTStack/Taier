package com.dtstack.engine.remote.message;

import java.io.Serializable;

/**
 * @Auther: dazhi
 * @Date: 2020/9/3 5:11 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class TargetInfo implements Serializable {
    private static final long serialVersionUID = 576062720439677529L;
    private String clazz;

    private String method;

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
