package com.dtstack.schedule.common.enums;

/**
 * 参数类型
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public enum EParamType {

    SYS_TYPE(0), CUSTOMIZE_TYPE(1);

    private int type;

    EParamType(int type){
        this.type = type;
    }

    public int getType(){
        return type;
    }

}
