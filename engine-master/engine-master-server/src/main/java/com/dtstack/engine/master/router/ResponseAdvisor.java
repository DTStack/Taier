package com.dtstack.engine.master.router;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionEnums;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

//@RestControllerAdvice("com.dtstack.engine.master.controller")
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

    private final static Logger logger = LoggerFactory.getLogger(ResponseAdvisor.class);

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        ApiResult<Object> apiResult = new ApiResult<Object>();
        apiResult.setData(body);
        apiResult.setCode(ErrorCode.SUCCESS.getCode());
        apiResult.setSpace(0);
        return apiResult;
    }



//    @ExceptionHandler(Exception.class)
//    @ResponseBody
//    public Object handle(Exception e) {
//        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
//        String errorMsg = null;
//        RdosDefineException rdosDefineException = null;
//        if (e.getCause() instanceof RdosDefineException) {
//            rdosDefineException = (RdosDefineException) e.getCause();
//            if (rdosDefineException.getErrorCode() != null) {
//                errorCode = rdosDefineException.getErrorCode();
//            }
//            errorMsg = rdosDefineException.getErrorMsg();
//            if (e.getCause().getCause() != null) {
//                logger.error("{}", e.getCause().getCause());
//            }
//        } else if (e instanceof RdosDefineException) {
//            rdosDefineException = (RdosDefineException) e;
//            if (rdosDefineException.getErrorCode() != null) {
//                errorCode = rdosDefineException.getErrorCode();
//            }
//            errorMsg = rdosDefineException.getErrorMsg();
//            if (e.getCause() != null) {
//                logger.error("{}", e.getCause());
//            }
//        } else {
//            errorCode = ErrorCode.SERVER_EXCEPTION;
//            errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
//            logger.error("", e);
//        }
//
//        if (errorCode.equals(ErrorCode.PERMISSION_LIMIT)) {
//            return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
//        }
//
//        return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
//    }
}