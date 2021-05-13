package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/5/11 7:00 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class DtArgumentParamOrHeaderResolver implements HandlerMethodArgumentResolver {

    private final String COOKIE = "cookie";
    private final String USER_ID = "userId";


    @Autowired
    private SessionUtil sessionUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DtParamOrHeader.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        DtParamOrHeader requestParam = parameter.getParameterAnnotation(DtParamOrHeader.class);
        String paramName = requestParam.value();
        MultiReadHttpServletRequest servletRequest = webRequest.getNativeRequest(MultiReadHttpServletRequest.class);

        if(null == servletRequest){
            return null;
        }

        if (StringUtils.isNotBlank(paramName)) {
            JSONObject requestBody = (JSONObject) servletRequest.getRequest().getAttribute(DtRequestWrapperFilter.DT_REQUEST_BODY);

            if (requestBody != null) {
                Class<?> parameterType = parameter.getParameterType();
                String value = requestBody.getString(paramName);

                if (StringUtils.isNotBlank(value)) {
                    if (parameterType.equals(List.class)) {
                        try {
                            ParameterizedTypeImpl genericParameterType = (ParameterizedTypeImpl) parameter.getGenericParameterType();
                            Type[] actualTypeArguments = genericParameterType.getActualTypeArguments();
                            if (actualTypeArguments[0] != null) {
                                List<?> objects = JSON.parseArray(value, Class.forName(actualTypeArguments[0].getTypeName()));
                                if (CollectionUtils.isNotEmpty(objects)) {
                                    return objects;
                                }
                            }
                        } catch (Exception e) {
                            Object object = requestBody.getObject(paramName, parameterType);
                            if (object != null) {
                                return object;
                            }
                        }
                    }

                    Object object = requestBody.getObject(paramName, parameterType);
                    if (object != null) {
                        return object;
                    }
                }

            }
        }

        return header(parameter,requestParam,servletRequest);
    }

    private Object header(MethodParameter parameter,DtParamOrHeader requestParam, MultiReadHttpServletRequest servletRequest) {
        String paramName = requestParam.header();
        Class<?> parameterType = parameter.getParameterType();
        if (COOKIE.equals(paramName) && StringUtils.isNotBlank(requestParam.cookie())) {
            String header = servletRequest.getHeader(paramName);
            if (StringUtils.isBlank(header)) {
                return header;
            }

            Object value = paramToMap(header).get(requestParam.cookie());
            return TypeUtils.castToJavaBean(value, parameterType);
        }
        if (USER_ID.equals(paramName)) {
            String header = servletRequest.getHeader(COOKIE);
            Object userId = paramToMap(header).get("dt_user_id");
            if (userId == null) {
                Object dtToken = paramToMap(header).get("dt_token");

                if (dtToken != null) {
                    UserDTO user = sessionUtil.getUser(dtToken.toString(), UserDTO.class);

                    if (user != null) {
                        Long dtuicUserId = user.getDtuicUserId();
                        return TypeUtils.castToJavaBean(dtuicUserId, parameterType);
                    }
                }
            } else {
                return TypeUtils.castToJavaBean(userId, parameterType);
            }
        }
        return null;
    }

    private Map<String, Object> paramToMap(String header) {
        Map<String, Object> map = Maps.newHashMap();

        List<String> strings = Splitter.on(";").trimResults().splitToList(header);

        for (String param : strings) {
            String[] split1 = param.split("=");
            if (ArrayUtils.isNotEmpty(split1) && split1.length == 2) {
                map.put(split1[0],split1[1]);
            }
        }

        return map;
    }


}
