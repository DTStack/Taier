package com.dtstack.engine.master.router;

import com.dtstack.engine.api.dto.UserDTO;
import com.dtstack.engine.master.router.login.SessionUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtArgumentCookieResolver implements HandlerMethodArgumentResolver {

    private final String COOKIE = "cookie";
    private final String USER_ID = "userId";

    @Autowired
    private SessionUtil sessionUtil;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(DtHeader.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        DtHeader requestParam = methodParameter.getParameterAnnotation(DtHeader.class);
        if (null == requestParam) {
            return null;
        }
        String paramName = requestParam.value();
        if(StringUtils.isBlank(paramName)){
            return null;
        }
        MultiReadHttpServletRequest servletRequest = webRequest.getNativeRequest(MultiReadHttpServletRequest.class);
        if(null == servletRequest){
            return null;
        }
        String header = servletRequest.getHeader(paramName);

        if (COOKIE.equals(paramName) && StringUtils.isNotBlank(requestParam.cookie())) {
            if (StringUtils.isBlank(header)) {
                return header;
            }

            return paramToMap(header).get(requestParam.cookie());
        }
        if (USER_ID.equals(paramName) && StringUtils.isNotBlank(requestParam.cookie())) {
            String dtToken = paramToMap(header).get("dt_token");

            UserDTO user = sessionUtil.getUser(dtToken, UserDTO.class);

            if (user == null) {
                return null;
            } else {
                return user.getDtuicUserId();
            }

        } else {
            return header;
        }
    }

    private Map<String, String> paramToMap(String header) {
        Map<String, String> map = Maps.newHashMap();

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