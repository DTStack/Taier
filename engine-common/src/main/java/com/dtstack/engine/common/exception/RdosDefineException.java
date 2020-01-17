package com.dtstack.engine.common.exception;


import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;

/**
 * @author sishu.yss
 */
public class RdosDefineException extends DtCenterDefException {


    public RdosDefineException(String message) {
        super(message);
    }

    public RdosDefineException(String message, Throwable cause) {
        super(message, cause);
    }

    public RdosDefineException(ExceptionEnums errorCode) {
        super(errorCode);
    }

    public RdosDefineException(String message, ExceptionEnums errorCode) {
        super(message, errorCode);
    }

    public RdosDefineException(ExceptionEnums errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public RdosDefineException(String message, ExceptionEnums errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }
}
