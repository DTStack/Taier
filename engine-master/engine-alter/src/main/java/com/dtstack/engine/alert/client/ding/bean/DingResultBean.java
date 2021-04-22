package com.dtstack.engine.alert.client.ding.bean;

/**
 * @Auther: dazhi
 * @Date: 2021/1/19 1:42 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class DingResultBean {

    private String errcode;

    private String errmsg;


    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
