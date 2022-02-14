package com.dtstack.taier.develop.model.system.config;

import com.dtstack.taier.common.util.Strings;

/**
 * System config mapper exception.
 */
public class SystemConfigMapperException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Invalid system config: '{}'. {}";

    private final String configName;
    private final String message;

    public SystemConfigMapperException(String configName, String message) {
        this.configName = configName;
        this.message = message;
    }

    public SystemConfigMapperException(String configName, String message, Throwable cause) {
        this(configName, message);
        this.initCause(cause);
    }

    @Override
    public String getMessage() {
        return Strings.format(MESSAGE_TEMPLATE, this.configName, this.message);
    }

}
