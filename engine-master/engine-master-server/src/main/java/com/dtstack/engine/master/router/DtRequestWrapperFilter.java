package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtRequestWrapperFilter extends OncePerRequestFilter {

    private final static Logger LOGGER = LoggerFactory.getLogger(DtRequestWrapperFilter.class);

    public final static String DT_REQUEST_BODY = "DT_REQUEST_BODY";

    private static String[] excludeTargets = {"/node/download/component/downloadFile", "/node/upload/component/config", "/node/upload/component/addOrUpdateComponent"
            ,"/node/alert/edit","/node/alert/testAlert","/node/upload/component/parseKerberos", "/node/upload/component/uploadKerberos","/node/login/submit","/node/login/logout"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest(request);
        for (String exc: excludeTargets) {
            if (exc.equals(uri)) {
                LOGGER.info("Uri: " + uri + ", Params: " + getParameterString(requestWrapper));
                filterChain.doFilter(requestWrapper, response);
                return;
            }
        }

        try (BufferedReader reader = requestWrapper.getReader()) {
            StringBuilder builder = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            reader.close();
            String reqBody = builder.toString();
            if (StringUtils.isNotBlank(reqBody)) {
                try {
                    JSONObject bodyJson = JSONObject.parseObject(reqBody);
                    Cookie[] cookies = request.getCookies();
                    if (cookies != null && cookies.length > 0) {
                        for (Cookie cookie : cookies) {
                            bodyJson.putIfAbsent(cookie.getName(), cookie.getValue());
                        }
                    }
                    request.setAttribute(DT_REQUEST_BODY, bodyJson);
                } catch (Exception e) {
                    LOGGER.warn(ExceptionUtil.getErrorMessage(e));
                }
            }
            LOGGER.info("Uri: " + uri + ", Params: " + reqBody);
        }
        filterChain.doFilter(requestWrapper, response);
    }

    private String getParameterString(MultiReadHttpServletRequest requestWrapper) {
        StringBuilder infoBuilder = new StringBuilder();
        Map<String, String[]> map = requestWrapper.getParameterMap();
        for (String key: map.keySet()) {
            String[] params = map.get(key);
            if (params.length == 0) {
                infoBuilder.append(key + ":" + "null ");
            } else if (params.length == 1){
                infoBuilder.append(key + ":" + params[0] + " ");
            } else {
                infoBuilder.append(key + ":" + Arrays.toString(params) + " ");
            }
        }
        return infoBuilder.toString();
    }
}