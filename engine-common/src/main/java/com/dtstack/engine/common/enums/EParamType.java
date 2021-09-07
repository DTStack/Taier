package com.dtstack.engine.common.enums;

/**
 * 参数类型
 * Date: 2017/6/7
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public enum EParamType {

    SYS_TYPE(0), CUSTOMIZE_TYPE(1),COMPONENT(2);

    private Integer type;

    EParamType(Integer type){
        this.type = type;
    }

    public Integer getType(){
        return type;
    }

}
