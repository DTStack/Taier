package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.master.router.util.MultiReadHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DtRequestParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        DtRequestParam requestParam = methodParameter.getParameterAnnotation(DtRequestParam.class);
        String paramName = requestParam.name();
        if (StringUtils.isNotBlank(paramName)) {
            paramName = methodParameter.getParameterName();
        }

        MultiReadHttpServletRequest servletRequest = webRequest.getNativeRequest(MultiReadHttpServletRequest.class);

        JSONObject requestBody = (JSONObject) servletRequest.getRequest().getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);
        if (requestBody != null) {
            Class clazz = methodParameter.getParameterType();
            return requestBody.getObject(paramName, clazz);
        }

        return null;
    }
}