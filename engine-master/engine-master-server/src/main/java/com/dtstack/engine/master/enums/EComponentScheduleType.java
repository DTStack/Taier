package com.dtstack.engine.master.enums;

/**
 * @author yuebai
 * @date 2020-05-11
 */
public enum  EComponentScheduleType {
    commonScheduling(0,"公共组件"),
    resourceScheduling(1,"资源调度组件"),
    storageScheduling(2,"存储组件"),
    computeScheduling(3,"计算组件");

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
