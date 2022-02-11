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

package com.dtstack.taiga.develop.controller.user;

import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.dao.domain.Tenant;
import com.dtstack.taiga.dao.domain.User;
import com.dtstack.taiga.develop.dto.user.DTToken;
import com.dtstack.taiga.develop.dto.user.DtUser;
import com.dtstack.taiga.develop.mapstruct.user.UserTransfer;
import com.dtstack.taiga.develop.service.console.TenantService;
import com.dtstack.taiga.develop.service.user.CookieService;
import com.dtstack.taiga.develop.service.user.LoginService;
import com.dtstack.taiga.develop.service.user.TokenService;
import com.dtstack.taiga.develop.service.user.UserService;
import com.dtstack.taiga.develop.vo.user.UserVO;
import com.dtstack.taiga.pluginapi.util.MD5Util;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author yuebai
 * @date 2021-08-02
 */
@RestController
@RequestMapping("/node/user")
@Api(value = "/node/user", tags = {"用户接口"})
public class UserController {

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

    @PostMapping(value = "/login")
    public R<String> login(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userName)) {
            throw new RdosDefineException("userName can not null");
        }
        if (StringUtils.isBlank(password)) {
            throw new RdosDefineException("password can not null");
        }

        User user = userService.getByUserName(userName.trim());
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
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
        return R.ok(dtUser.getUserName());
    }

    @RequestMapping(value = "/logout")
    public R<Boolean> logout(HttpServletRequest request, HttpServletResponse response) {
        cookieService.clean(request, response);
        return R.ok(true);
    }

    @PostMapping(value = "/switchTenant")
    public R<String> switchTenant(@RequestParam(value = "tenantId") Long tenantId, HttpServletRequest request, HttpServletResponse response) {
        String token = cookieService.token(request);
        if (StringUtils.isBlank(token)) {
            throw new RdosDefineException(ErrorCode.TOKEN_IS_NULL);
        }
        DTToken decryption = tokenService.decryption(token);
        Long userId = decryption.getUserId();
        User user = userService.getById(userId);
        if (null == user) {
            throw new RdosDefineException(ErrorCode.USER_IS_NULL);
        }
        Tenant tenant = tenantService.getTenantById(tenantId);
        if (null == tenant) {
            throw new RdosDefineException(ErrorCode.TENANT_IS_NULL);
        }
        DtUser dtUser = new DtUser();
        dtUser.setUserId(user.getId());
        dtUser.setUserName(user.getUserName());
        dtUser.setEmail(user.getEmail());
        dtUser.setPhone(user.getPhoneNumber());
        dtUser.setTenantId(tenantId);
        dtUser.setTenantName(tenant.getTenantName());
        loginService.onAuthenticationSuccess(request, response, dtUser);
        return R.ok(user.getUserName());
    }


    @RequestMapping(value = "/queryUser")
    public R<List<UserVO>> queryUser() {
        List<User> users = userService.listAll();
        List<UserVO> userVOS = UserTransfer.INSTANCE.toVo(users);
        return R.ok(userVOS);
    }
}
