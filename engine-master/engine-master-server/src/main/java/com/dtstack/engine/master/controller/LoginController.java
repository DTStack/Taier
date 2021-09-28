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

package com.dtstack.engine.master.controller;

import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.master.router.login.CookieService;
import com.dtstack.engine.master.router.login.LoginService;
import com.dtstack.engine.master.router.login.domain.DtUicUser;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @Value("${dtstack.username:admin@dtstack.com}")
    public String USERNAME;

    @Value("${dtstack.password:admin123}")
    public String PASSWORD;

    @Autowired
    private LoginService loginService;

    @Autowired
    private CookieService cookieService;

    @RequestMapping(value = "/submit", method = {RequestMethod.POST})
    public String submit(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userName)) {
            throw new RdosDefineException("userName can not null");
        }
        if (StringUtils.isBlank(password)) {
            throw new RdosDefineException("password can not null");
        }
        if (!USERNAME.equalsIgnoreCase(userName)) {
            throw new RdosDefineException("userName not exist");
        }
        if (!PASSWORD.equalsIgnoreCase(password)) {
            throw new RdosDefineException("password not exist");
        }
        DtUicUser uicUser = new DtUicUser();
        uicUser.setUserId(1L);
        uicUser.setUserName(USERNAME);
        uicUser.setTenantId(1L);
        uicUser.setTenantName("DAGScheduleX");
        loginService.onAuthenticationSuccess(request, response, uicUser);
        return uicUser.getUserName();
    }

    @RequestMapping(value = "/logout")
    public boolean logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.clean(request, response);
        return true;
    }
}
