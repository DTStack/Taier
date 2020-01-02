package com.dtstack.engine.common.enums;

/**
 * Reason:
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum  EPluginType {
    DEFAULT(0), DYNAMIC(1);

    int type;

    EPluginType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
