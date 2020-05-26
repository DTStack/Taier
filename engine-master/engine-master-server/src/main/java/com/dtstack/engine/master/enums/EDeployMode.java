package com.dtstack.engine.master.enums;

import com.dtstack.engine.common.exception.RdosDefineException;

public enum EDeployMode{
    PERJOB("perjob", 1),
    SESSION("session", 2),
    STANDALONE("standalone", 3);
    private String mode;
    private Integer type;

    public String getMode() {
        return mode;
    }

    public Integer getType() {
        return type;
    }

    EDeployMode(String mode, Integer type) {
        this.mode = mode;
        this.type = type;
    }

    public static EDeployMode getByType(Integer type) {
        for (EDeployMode value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        throw new RdosDefineException("不支持的模式");
    }
}