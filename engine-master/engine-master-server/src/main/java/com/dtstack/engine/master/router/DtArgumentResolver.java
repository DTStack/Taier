package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.master.router.util.MultiReadHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;

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
        if (StringUtils.isBlank(paramName)) {
            paramName = methodParameter.getParameterName();
        }

        MultiReadHttpServletRequest servletRequest = webRequest.getNativeRequest(MultiReadHttpServletRequest.class);

        String paramJson = (String) servletRequest.getRequest().getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);

        if (StringUtils.isNotBlank(paramJson)) {
            JSONObject requestBody = JSONObject.parseObject(paramJson);

            Class<?> parameterType = methodParameter.getParameterType();

            if (parameterType.equals(List.class)) {
                try {
                    String value = requestBody.getString(paramName);
                    ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl)methodParameter.getGenericParameterType();
                    Type[] actualTypeArguments = genericParameterType.getActualTypeArguments();
                    if (actualTypeArguments[0] != null) {
                        return JSON.parseArray(value, Class.forName(actualTypeArguments[0].getTypeName()));
                    }
                } catch (Exception e) {
                    return null;
                }
            }

            return requestBody.getObject(paramName, parameterType);
        }

        return null;
    }
}