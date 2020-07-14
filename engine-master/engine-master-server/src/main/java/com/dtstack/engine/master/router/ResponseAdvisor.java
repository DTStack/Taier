package com.dtstack.engine.master.router;

import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.master.router.callback.ApiResult;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {
 
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
}