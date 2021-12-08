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

package com.dtstack.batch.controller.login;

import com.dtstack.batch.service.console.TenantService;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.master.impl.UserService;
import com.dtstack.engine.master.router.login.CookieService;
import com.dtstack.engine.master.router.login.LoginService;
import com.dtstack.engine.master.router.login.TokenService;
import com.dtstack.engine.master.router.login.domain.DTToken;
import com.dtstack.engine.master.router.login.domain.DtUser;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.util.MD5Util;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yuebai
 * @date 2021-08-02
 */
@RestController
@RequestMapping("/node/login")
@Api(value = "/node/login", tags = {"登录接口"})
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private CookieService cookieService;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TenantService tenantService;

    @PostMapping(value = "/submit")
    public String submit(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userName)) {
            throw new RdosDefineException("userName can not null");
        }
        if (StringUtils.isBlank(password)) {
            throw new RdosDefineException("password can not null");
        }

        User user = userService.getByUserName(userName.trim());
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_NOT_FIND);
        }
        String md5Password = MD5Util.getMd5String(password);
        if (!md5Password.equalsIgnoreCase(user.getPassword())) {
            throw new RdosDefineException("password not correct");
        }
        DtUser dtUser = new DtUser();
        dtUser.setUserId(user.getId());
        dtUser.setUserName(user.getUserName());
        dtUser.setEmail(user.getEmail());
        dtUser.setPhone(user.getPhoneNumber());
        loginService.onAuthenticationSuccess(request, response, dtUser);
        return dtUser.getUserName();
    }

    @RequestMapping(value = "/logout")
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.clean(request, response);
        return true;
    }

    @PostMapping(value = "/switchTenant")
    public String switchTenant(@RequestParam(value = "tenantId") Long tenantId, HttpServletRequest request, HttpServletResponse response) {
        String token = cookieService.token(request);
        if (StringUtils.isBlank(token)) {
            throw new RdosDefineException(ErrorCode.TOKEN_IS_NULL);
        }
        DTToken decryption = tokenService.decryption(token);
        Long userId = decryption.getUserId();
        User user = userService.getById(userId);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_NOT_FIND);
        }
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (null == tenant) {
            throw new RdosDefineException(ErrorCode.TENANT_CAN_NOT_FIND);
        }
        DtUser dtUser = new DtUser();
        dtUser.setUserId(user.getId());
        dtUser.setUserName(user.getUserName());
        dtUser.setEmail(user.getEmail());
        dtUser.setPhone(user.getPhoneNumber());
        dtUser.setTenantId(tenantId);
        dtUser.setTenantName(tenant.getTenantName());
        loginService.onAuthenticationSuccess(request, response, dtUser);
        return user.getUserName();
    }
}
