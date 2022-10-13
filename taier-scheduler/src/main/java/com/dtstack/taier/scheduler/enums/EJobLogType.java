package com.dtstack.taier.scheduler.enums;


public enum EJobLogType {
    //
    FINISH_LOG(0),
    //
    RETRY_LOG(1);

    Integer type;

    public Integer getType() {
        return type;
    }


    EJobLogType(Integer type) {
        this.type = type;
    }
}
