package com.dtstack.engine.master.router;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.master.router.util.MultiReadHttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/7/16
 */
@Component
public class DtRequestWrapperFilter extends OncePerRequestFilter {

    public final static String DT_REQUEST_BODY = "DT_REQUEST_BODY";

    private static String[] excludeTargets = {"/node/download/component/downloadFile", "/node/upload/component/config"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        for (String exc: excludeTargets) {
            if (exc.equals(uri)) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        MultiReadHttpServletRequest requestWrapper = new MultiReadHttpServletRequest(request);
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
                request.setAttribute(DT_REQUEST_BODY, JSONObject.parse(reqBody));
            }
        }
        filterChain.doFilter(requestWrapper, response);
    }
}