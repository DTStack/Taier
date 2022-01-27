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

package com.dtstack.taiga.develop.service.user;


import com.dtstack.taiga.common.constant.Cookies;
import com.dtstack.taiga.develop.dto.user.DTToken;
import com.dtstack.taiga.develop.dto.user.DtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author toutian
 */
@Service
public class LoginService {

    private static Logger LOGGER = LoggerFactory.getLogger(LoginService.class);


    @Autowired
    private CookieService cookieService;

    @Autowired
    private TokenService tokenService;


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, DtUser dtUser) {
        String dtToken = cookieService.token(request);

        //若Token不存在,则生成Token
        if (Objects.isNull(dtToken)
        ) {
            cookie(request, response, dtUser);
        } else {
            DTToken token = tokenService.decryption(dtToken);
            boolean equalsUserId = dtUser.getUserId().equals(token.getUserId());
            boolean nonNullTenantId = Objects.nonNull(dtUser.getTenantId());
            if (nonNullTenantId && !dtUser.getTenantId().equals(token.getTenantId())) {
                cookie(request, response, dtUser);
            } else if (!equalsUserId) {
                cookie(request, response, dtUser);
            }
        }
    }

    private void cookie(HttpServletRequest request, HttpServletResponse response, DtUser user) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(user);

        String token = tokenService.encryption(user.getUserId(), user.getUserName(), user.getTenantId());
        cookieService.addCookie(request, response, Cookies.USER_ID, user.getUserId());
        cookieService.addCookie(request, response, Cookies.USER_NAME, user.getUserName());
        cookieService.addCookie(request, response, Cookies.TOKEN, token);
        if (null != user.getTenantId()) {
            cookieService.addCookie(request, response, Cookies.TENANT_ID, user.getTenantId());
            cookieService.addCookie(request, response, Cookies.TENANT_NAME, user.getTenantName());
        }
    }

}
