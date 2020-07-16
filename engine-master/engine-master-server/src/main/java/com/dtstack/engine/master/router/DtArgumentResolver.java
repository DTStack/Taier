package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
        String paramName = requestParam.name();
        if (StringUtils.isNotBlank(paramName)) {
            paramName = methodParameter.getParameterName();
        }

        ContentCachingRequestWrapper servletRequest = webRequest.getNativeRequest(ContentCachingRequestWrapper.class);

        JSONObject requestBody = (JSONObject) servletRequest.getRequest().getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);
        if (requestBody != null) {
            Class clazz = methodParameter.getParameterType();
            return requestBody.getObject(paramName, clazz);
        }

        return null;
    }
}