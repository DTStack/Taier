/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.router;

import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionEnums;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;

@ControllerAdvice("com.dtstack.engine.master.controller")
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseAdvisor.class);

    @Override
    public boolean supports(@Nullable MethodParameter returnType, @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  @Nullable MethodParameter returnType,
                                  @Nullable MediaType selectedContentType, @Nullable Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  @Nullable ServerHttpResponse response) {
        String requestPath = request.getURI().getPath();
        if (!requestPath.startsWith("/node")) {
            return body;
        }
        ApiResult<Object> apiResult = new ApiResult<>();
        if (body instanceof ApiResult) {
            return body;
        } else {
            apiResult.setData(body);
        }
        apiResult.setCode(ErrorCode.SUCCESS.getCode());
        return apiResult;
    }

    /**
     * ValidationException
     */
    @ResponseBody
    @ExceptionHandler(BindException.class)
    public ApiResult handleValidationException(BindException e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringBuilder message = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            message.append(fieldError.getField()).append(":").append(fieldError.getDefaultMessage()).append(",");
        }

        return ApiResult.createErrorResult(message.toString(), errorCode.getCode());
    }

    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
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
                LOGGER.error("{}", e.getCause().getCause());
            }
        } else if (e instanceof RdosDefineException) {
            rdosDefineException = (RdosDefineException) e;
            if (rdosDefineException.getErrorCode() != null) {
                errorCode = rdosDefineException.getErrorCode();
            }
            errorMsg = rdosDefineException.getErrorMsg();
            if (e.getCause() != null) {
                LOGGER.error("{}", e.getCause());
            }
        } else {
            errorCode = ErrorCode.SERVER_EXCEPTION;
            errorMsg = ErrorCode.SERVER_EXCEPTION.getDescription();
            LOGGER.error("", e);
        }

        if (errorCode.equals(ErrorCode.PERMISSION_LIMIT)) {
            return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
        }

        return ApiResult.createErrorResult(errorMsg, errorCode.getCode());
    }

}