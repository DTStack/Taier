package com.dtstack.engine.alert.param;

/**
 * @Auther: dazhi
 * @Date: 2020/12/7 10:08 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class CustomizeAlertParam extends AlertParam {

    /**
     * 自定义告警数据
     *
     */
    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
