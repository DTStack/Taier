package com.dtstack.taier.develop.model.exception;

import com.dtstack.taier.common.enums.EComponentType;

public class InvalidComponentException extends BaseComponentException {

    public InvalidComponentException(EComponentType type, String msg) {
        super(type, msg);
    }

    @Override
    public String getErrorMsg() {
        return super.getMessage();
    }
}
