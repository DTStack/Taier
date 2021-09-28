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

import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.AccountTenantVo;
import com.dtstack.engine.master.vo.AccountVo;
import com.dtstack.engine.master.vo.AccountVoLists;
import com.dtstack.engine.master.vo.user.UserVO;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.master.impl.AccountService;
import org.springframework.web.bind.annotation.RequestParam;
import com.dtstack.engine.master.router.util.CookieUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/node/account")
@Api(value = "/node/account", tags = {"账户接口"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value="/bindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "绑定数据库账号 到对应数栈账号下的集群")
    public void bindAccount(@RequestBody AccountVo accountVo,HttpServletRequest request) throws Exception {
        long userId = CookieUtil.getUserId(request.getCookies());
        String dtUserName = CookieUtil.getDtUserName(request.getCookies());
        accountVo.setModifyUserName(dtUserName);
        accountVo.setUserId(userId);
        accountService.bindAccount(accountVo);
    }

    @RequestMapping(value = "/bindAccountList", method = {RequestMethod.POST})
    @ApiOperation(value = "绑定数据库账号列表 到对应数栈账号下")
    public void bindAccountList(@RequestBody AccountVoLists accountVoLists,HttpServletRequest request) throws Exception {
        long userId = CookieUtil.getUserId(request.getCookies());
        String dtUserName = CookieUtil.getDtUserName(request.getCookies());
        if (null == accountVoLists || CollectionUtils.isEmpty(accountVoLists.getAccountList())) {
            throw new RdosDefineException("绑定账号不能为空");
        }
        for (AccountVo accountVo : accountVoLists.getAccountList()) {
            accountVo.setModifyUserName(dtUserName);
            accountVo.setUserId(userId);
        }
        accountService.bindAccountList(accountVoLists.getAccountList());
    }

    @RequestMapping(value="/unbindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "解绑数据库账号")
    public void unbindAccount(@RequestBody AccountTenantVo accountTenantVo, HttpServletRequest request) throws Exception {
        accountTenantVo.setModifyUserName(CookieUtil.getDtUserName(request.getCookies()));
        accountTenantVo.setModifyDtUicUserId(CookieUtil.getUserId(request.getCookies()));
        accountService.unbindAccount(accountTenantVo);
    }

    @RequestMapping(value="/updateBindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "更改数据库账号")
    public void updateBindAccount(@RequestBody AccountTenantVo accountTenantVo, HttpServletRequest request) throws Exception {
        accountTenantVo.setModifyUserName(CookieUtil.getDtUserName(request.getCookies()));
        accountTenantVo.setModifyDtUicUserId(CookieUtil.getUserId(request.getCookies()));
        accountService.updateBindAccount(accountTenantVo);
    }

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询")
    public PageResult<List<AccountVo>> pageQuery(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("username") String username, @RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize, @RequestParam("engineType") Integer engineType, @RequestParam("dtuicUserId") Long dtuicUserId) {
        return accountService.pageQuery(dtuicTenantId, username, currentPage, pageSize, engineType,dtuicUserId);
    }

    @RequestMapping(value="/getTenantUnBandList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户未绑定用户列表")
    public List<UserVO> getTenantUnBandList(@RequestParam("dtuicTenantId") Long dtuicTenantId,  @RequestParam("engineType")Integer engineType) {
        return accountService.getTenantUnBandList(dtuicTenantId, engineType);
    }



}
