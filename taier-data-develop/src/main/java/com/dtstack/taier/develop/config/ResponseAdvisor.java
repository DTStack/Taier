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

package com.dtstack.taier.develop.config;

import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.ExceptionEnums;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.validation.ConstraintViolationException;
import javax.xml.bind.ValidationException;
import java.util.List;
import java.util.Optional;

@RestControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

    private final static Logger LOGGER = LoggerFactory.getLogger(ResponseAdvisor.class);

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

        return R.fail(errorCode.getCode(), message.toString());
    }

    /**
     * ValidationException
     */
    @ResponseBody
    @ExceptionHandler(ValidationException.class)
    public R handleValidationException(ValidationException e) {
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(), e.getMessage());
    }

    /**
     * ConstraintViolationException
     */
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    public R handleConstraintViolationException(ConstraintViolationException e) {
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(), e.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(), fieldError.getDefaultMessage());
        }
        return R.fail(ErrorCode.INVALID_PARAMETERS.getCode(), ex.getMessage());
    }


    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public R exceptionHandler(Throwable e) {
        ExceptionEnums errorCode = ErrorCode.UNKNOWN_ERROR;
        TaierDefineException taierDefineException = null;
        if (e.getCause() instanceof DtCenterDefException) {
            taierDefineException = (TaierDefineException) e.getCause();
            if (taierDefineException.getErrorCode() != null) {
                errorCode = taierDefineException.getErrorCode();
            }
        } else if (e instanceof TaierDefineException) {
            taierDefineException = (TaierDefineException) e;
            if (taierDefineException.getErrorCode() != null) {
                errorCode = taierDefineException.getErrorCode();
            }
        }
        LOGGER.error("", e.getCause());
        return R.fail(errorCode.getCode(), Optional.ofNullable(e.getMessage()).orElse(ExceptionUtils.getMessage(e.getCause())));
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        String requestPath = request.getURI().getPath();
        if (!requestPath.startsWith(ConfigConstant.REQUEST_PREFIX)) {
            return body;
        }
        R<Object> resultBody = new R<>();
        if (body instanceof R) {
            return body;
        } else {
            resultBody.setData(body);
        }
        resultBody.setCode(ErrorCode.SUCCESS.getCode());
        return resultBody;
    }
}
