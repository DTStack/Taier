package com.dtstack.rdos.engine.execution.flink150.enums;

import org.apache.commons.lang3.StringUtils;

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

    public static FlinkMode mode(String mode) {
        if (StringUtils.isBlank(mode) || !FlinkMode.NEW_MODE.mode.equals(mode)) {
            return LEGACY_MODE;
        }
        return NEW_MODE;
    }

}
