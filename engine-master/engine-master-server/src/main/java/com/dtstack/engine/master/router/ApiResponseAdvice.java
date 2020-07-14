package com.dtstack.engine.master.router;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ApiResponseAdvice {

    private final static Logger logger = LoggerFactory.getLogger(ApiResponseAdvice.class);

    /**
     * 切点
     */
    @Pointcut("execution(public * com.dtstack.engine.master.controller..*(..))")
    public void apiResponse() {
    }

    @Around(value = "apiResponse()")
    public ApiResult<Object> controller(ProceedingJoinPoint proceedingJoinPoint) {
        ApiResult<Object> apiResult = new ApiResult<>();
        try {
            //获取方法的执行结果
            Object proceed = proceedingJoinPoint.proceed();
            //如果方法的执行结果是ApiResult，则将该对象直接返回
            if (proceed instanceof ApiResult) {
                apiResult = (ApiResult) proceed;
            } else {
                apiResult.setData(proceed);
            }
            apiResult.setCode(ErrorCode.SUCCESS.getCode());
        } catch (Throwable throwable) {
            //如果出现了异常，调用异常处理方法将错误信息封装到 ApiResult 中并返回
            apiResult = handle(throwable);
        }
        return apiResult;
    }

    @AfterReturning(value = "apiResponse()", returning = "result")
    public ApiResult<Object> handlerController(Object result) {
        ApiResult<Object> apiResult = new ApiResult<>();
        //获取方法的执行结果
        Object proceed = result;
        //如果方法的执行结果是ApiResult，则将该对象直接返回
        if (proceed instanceof ApiResult) {
            apiResult = (ApiResult) proceed;
        } else {
            apiResult.setData(proceed);
        }
        apiResult.setCode(ErrorCode.SUCCESS.getCode());
        return apiResult;
    }

    @AfterThrowing(value = "apiResponse()", throwing = "e")
    public ApiResult<Object> handle(Throwable e) {
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