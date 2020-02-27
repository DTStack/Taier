package com.dtstack.engine.common.exception;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;

public class EngineDefineException extends DtCenterDefException {

    public EngineDefineException(String message) {
        super(message);
    }

    public EngineDefineException(ExceptionEnums errorCode) {
        super(errorCode);
    }

    public EngineDefineException(String message, ExceptionEnums errorCode) {
        super(message, errorCode);
    }

    public EngineDefineException(ExceptionEnums errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public EngineDefineException(String message, ExceptionEnums errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
