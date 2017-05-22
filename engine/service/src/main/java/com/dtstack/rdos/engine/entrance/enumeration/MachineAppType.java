package com.dtstack.rdos.engine.entrance.enumeration;

/**
 * Created by sishu.yss on 2017/5/22.
 */
public enum MachineAppType {
    ENGINE("engine"),WEB("web");

    private String type;

    MachineAppType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }
}

