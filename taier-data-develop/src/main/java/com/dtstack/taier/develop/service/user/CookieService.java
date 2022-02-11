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

package com.dtstack.taier.develop.service.user;

import com.dtstack.taier.common.constant.Cookies;
import com.dtstack.taier.common.util.AddressUtil;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

/**
 * @author yuebai
 * @date 2021-08-03
 */
@Configuration
public class CookieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieService.class);

    @Value("${server.session.cookie.domain:#{null}}")
    public String SESSION_COOKIE_DOMAIN;

    @Value("${dtstack.httpsEnable:false}")
    public Boolean httpsEnable;

    @Value("${server.session.timeout:259200}")
    public Integer SESSION_TIMEOUT;

    @Value("${server.session.cookie.host:#{null}}")
    public String SESSION_COOKIE_HOST;

    public String decisionCookieDomain(HttpServletRequest request) {
        Objects.requireNonNull(request);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("server :{}", request.getServerName());
            LOGGER.info("remote host:{}", request.getRemoteHost());
        }
        if (AddressUtil.ip(request.getServerName())) {
            if (Objects.nonNull(SESSION_COOKIE_HOST)) {
                return SESSION_COOKIE_HOST;
            }
        } else {
            if (Objects.nonNull(SESSION_COOKIE_DOMAIN)) {
                return SESSION_COOKIE_DOMAIN;
            }
        }
        return request.getServerName();
    }

    public String token(HttpServletRequest request) {
        Objects.requireNonNull(request);
        Map<String, String> hashMap = Maps.newHashMap();
        if (Objects.nonNull(request.getCookies())) {
            for (Cookie cookie : request.getCookies()) {
                hashMap.put(cookie.getName(), cookie.getValue());
            }
        }
        return hashMap.get(Cookies.TOKEN);
    }

    public void clean(HttpServletRequest request, HttpServletResponse response) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return;
        }
        for (Cookie cookie : cookies) {
            cookie.setDomain(decisionCookieDomain(request));
            cookie.setMaxAge(0);
            cookie.setValue(null);
            cookie.setPath("/");
            response.addCookie(cookie);
        }

    }

    public void addCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, Object cookieValue) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(cookieName);
        Objects.requireNonNull(cookieValue);

        try {
            ResponseCookie cookie;
            if (httpsEnable) {
                cookie = ResponseCookie.from(cookieName, URLEncoder.encode(cookieValue.toString(), "UTF-8"))
                        .httpOnly(false)
                        .secure(true)
                        .domain(decisionCookieDomain(request))
                        .path("/")
                        .maxAge(SESSION_TIMEOUT)
                        .sameSite("None")
                        .build();
            } else {
                cookie = ResponseCookie.from(cookieName, URLEncoder.encode(cookieValue.toString(), "UTF-8"))
                        .httpOnly(false)
                        .domain(decisionCookieDomain(request))
                        .path("/")
                        .maxAge(SESSION_TIMEOUT)
                        .build();
            }
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        } catch (UnsupportedEncodingException e) {
            if (LOGGER.isErrorEnabled()) {
                LOGGER.error("cookie store Error.", e);
            }
        }

    }
}
