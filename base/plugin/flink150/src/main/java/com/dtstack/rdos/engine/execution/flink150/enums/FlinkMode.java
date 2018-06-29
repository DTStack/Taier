package com.dtstack.rdos.engine.execution.flink150.enums;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/6/29
 */
public enum FlinkMode {

    NEW_MODE("new"), LEGACY_MODE("legacy");

    private String mode;

    FlinkMode(String mode) {
        this.mode = mode;
    }
}
