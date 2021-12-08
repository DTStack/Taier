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

package com.dtstack.engine.master.router.login;


import com.dtstack.engine.common.constrant.Cookies;
import com.dtstack.engine.dto.UserDTO;
import com.dtstack.engine.master.router.login.domain.DTToken;
import com.dtstack.engine.master.router.login.domain.DtUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

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


    public void login(DtUser dtUicUser, Consumer<UserDTO> resultHandler) {
        try {
            if (dtUicUser == null) {
                resultHandler.accept(null);
                return;
            }
            boolean isRootUser = Optional.ofNullable(dtUicUser.getRootOnly()).orElse(false);
            LOGGER.info("dtUic userId [{}] userName {} tenantId {} is Root {} login", dtUicUser.getUserId(), dtUicUser.getUserName(), dtUicUser.getTenantId(), isRootUser);
            UserDTO userDTO = new UserDTO();
            userDTO.setDtuicUserId(dtUicUser.getUserId());
            userDTO.setTenantId(dtUicUser.getTenantId());
            userDTO.setUserName(dtUicUser.getUserName());
            userDTO.setRootUser(isRootUser ? 1 : 0);
            resultHandler.accept(userDTO);
        } catch (Throwable e) {
            LOGGER.error("login fail:", e);
            throw e;
        }
    }


    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, DtUser dtUicUser) {
        String dtToken = cookieService.token(request);

        //若Token不存在,则生成Token
        if (Objects.isNull(dtToken)
        ) {
            cookie(request, response, dtUicUser);
        } else {
            DTToken token = tokenService.decryption(dtToken);
            boolean equalsUserId = dtUicUser.getUserId().equals(token.getUserId());
            boolean nonNullTenantId = Objects.nonNull(dtUicUser.getTenantId());
            if (nonNullTenantId && !dtUicUser.getTenantId().equals(token.getTenantId())) {
                cookie(request, response, dtUicUser);
            } else if (!equalsUserId) {
                cookie(request, response, dtUicUser);
            }
        }
    }

    private void cookie(HttpServletRequest request, HttpServletResponse response, DtUser user) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(response);
        Objects.requireNonNull(user);

        String token = tokenService.encryption(user.getUserId(), user.getUserName(), user.getTenantId());
        cookieService.addCookie(request, response, Cookies.DT_USER_ID, user.getUserId());
        cookieService.addCookie(request, response, Cookies.DT_USER_NAME, user.getUserName());
        cookieService.addCookie(request, response, Cookies.DT_TOKEN, token);

        cookieService.addCookie(request, response, Cookies.CREATE_USER_ID, user.getUserId());
        cookieService.addCookie(request, response, Cookies.MODIFY_USER_ID, user.getUserId());

        if (null != user.getTenantId()) {
            cookieService.addCookie(request, response, Cookies.DT_TENANT_ID, user.getTenantId());
            cookieService.addCookie(request, response, Cookies.DT_TENANT_NAME, user.getTenantName());
        }
    }

}
