package com.dtstack.taiga.develop.model.exception;

import com.dtstack.taiga.common.enums.EComponentType;

public class InvalidComponentException extends BaseComponentException {

    public InvalidComponentException(EComponentType type, String msg) {
        super(type, msg);
    }

    @Override
    public String getErrorMsg() {
        return super.getMessage();
    }
}
