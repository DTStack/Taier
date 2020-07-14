package com.dtstack.engine.master.router;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerAdvice
public class ExceptionAdvisor {

    private final static Logger logger = LoggerFactory.getLogger(ExceptionAdvisor.class);
 
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    @ResponseStatus
    public ApiResult exceptionHandler(Throwable e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        String errorMsg = null;
        RdosDefineException rdosDefineException = null;
        if (e.getCause() instanceof RdosDefineException) {
            rdosDefineException = (RdosDefineException) e.getCause();
            if (rdosDefineException.getErrorCode() != null) {
                errorCode = rdosDefineException.getErrorCode();
            }
            errorMsg = rdosDefineException.getErrorMsg();
            if (e.getCause().getCause() != null) {
                logger.error("{}", e.getCause().getCause());
            }
        } else if (e instanceof RdosDefineException) {
            rdosDefineException = (RdosDefineException) e;
            if (rdosDefineException.getErrorCode() != null) {
                errorCode = rdosDefineException.getErrorCode();
            }
            errorMsg = rdosDefineException.getErrorMsg();
            if (e.getCause() != null) {
                logger.error("{}", e.getCause());
            }
        } else {
            errorCode = ErrorCode.SERVER_EXCEPTION;
            errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
            logger.error("", e);
        }

        if (errorCode.equals(ErrorCode.PERMISSION_LIMIT)) {
            return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
        }

        return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
    }

 
}