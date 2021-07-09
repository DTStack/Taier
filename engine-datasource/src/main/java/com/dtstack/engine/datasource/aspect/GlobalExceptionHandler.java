package com.dtstack.engine.datasource.aspect;

import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.exception.ExceptionEnums;
import com.dtstack.dtcenter.loader.exception.DtLoaderException;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.sdk.core.common.SdkException;
import dt.insight.plat.lang.web.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = DtLoaderException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public R<Void> dtLoaderExceptionHandler(HttpServletRequest req, DtLoaderException e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        logger.error("uri:{}, params:{} dtLoaderException:", req.getRequestURI(), req.getQueryString(), e);
        return R.fail(errorCode.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = SdkException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public R<Void> sdkExceptionHandler(HttpServletRequest req, SdkException e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        logger.error("uri:{}, params:{} sdkException:", req.getRequestURI(), req.getQueryString(), e);
        return R.fail(errorCode.getCode(), e.getMessage());
    }

    @ExceptionHandler(value = DtCenterDefException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.PAYMENT_REQUIRED)
    public R<Void> dtCenterDefExceptionHandler(HttpServletRequest req, DtCenterDefException e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        logger.error("uri:{}, params:{} dtCenterDefException:", req.getRequestURI(), req.getQueryString(), e);
        return R.fail(errorCode.getCode(), e.getErrorMsg());
    }


}
