package com.dtstack.taier.common.enums;

/**
 * @author yuebai
 * @date 2019-10-23
 */
public enum ResourceRefType {
    //作为运行主体
    MAIN_RES(1),
    //作为依赖资源
    DEPENDENCY_RES(2);

    private int type;

    ResourceRefType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }
}

