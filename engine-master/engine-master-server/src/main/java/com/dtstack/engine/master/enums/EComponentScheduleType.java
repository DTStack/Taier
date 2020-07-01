package com.dtstack.engine.master.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public enum  EComponentScheduleType {
    COMMON(0,"公共组件"),
    RESOURCE(1,"资源调度组件"),
    STORAGE(2,"存储组件"),
    COMPUTE(3,"计算组件");

    private int type;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

     EComponentScheduleType(int type,String name) {
        this.type = type;
        this.name = name;
    }
}
