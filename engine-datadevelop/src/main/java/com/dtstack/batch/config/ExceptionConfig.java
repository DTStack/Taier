package com.dtstack.batch.config;

import com.dtstack.engine.common.lang.web.R;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionEnums;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;
import java.util.List;

@Configuration
public class ExceptionConfig {

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionConfig.class);

    /**
     * ValidationException
     */
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public R handleValidationException(BindException e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            message.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(",");
        }

        return R.fail(errorCode.getCode(),message.toString());
    }

    /**
     * ValidationException
     */
    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public R handleValidationException(ValidationException e) {
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(),e.getMessage());
    }

    /**
     * ConstraintViolationException
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e) {
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(),e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(),fieldError.getDefaultMessage());
        }
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(),ex.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public R exceptionHandler(Throwable e) {
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
                LOGGER.error("", e.getCause().getCause());
            }
        } else if (e instanceof RdosDefineException) {
            rdosDefineException = (RdosDefineException) e;
            if (rdosDefineException.getErrorCode() != null) {
                errorCode = rdosDefineException.getErrorCode();
            }
            errorMsg = rdosDefineException.getErrorMsg();
            if (e.getCause() != null) {
                LOGGER.error("", e.getCause());
            }
        } else {
            errorCode = ErrorCode.SERVER_EXCEPTION;
            errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
            LOGGER.error("", e);
        }

        if (errorCode.equals(ErrorCode.PERMISSION_LIMIT)) {
            return R.fail(errorCode.getCode(), errorMsg);
        }
        return R.fail(errorCode.getCode(), errorMsg);
    }
}
