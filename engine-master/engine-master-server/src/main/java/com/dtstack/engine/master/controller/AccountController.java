package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.master.impl.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/account")
@Api(value = "/node/account", tags = {"账户接口"})
public class AccountController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(value="/bindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "绑定数据库账号 到对应数栈账号下的集群")
    public void bindAccount(@RequestBody AccountVo accountVo) throws Exception {
        accountService.bindAccount(accountVo);
    }

    @RequestMapping(value="/unbindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "解绑数据库账号")
    public void unbindAccount(@RequestBody AccountTenantVo accountTenantVo, @RequestParam("userId") Long userId) throws Exception {
        accountService.unbindAccount(accountTenantVo, userId);
    }

    @RequestMapping(value="/updateBindAccount", method = {RequestMethod.POST})
    @ApiOperation(value = "更改数据库账号")
    public void updateBindAccount(@RequestBody AccountTenantVo accountTenantVo, @RequestParam("userId") Long userId) throws Exception {
        accountService.updateBindAccount(accountTenantVo, userId);
    }

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询")
    public PageResult<List<AccountVo>> pageQuery(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("username") String username, @RequestParam("currentPage") Integer currentPage,
                                                 @RequestParam("pageSize") Integer pageSize, @RequestParam("engineType") Integer engineType) {
        return accountService.pageQuery(dtuicTenantId, username, currentPage, pageSize, engineType);
    }

    @RequestMapping(value="/getTenantUnBandList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户未绑定用户列表")
    public List<Map<String, Object>> getTenantUnBandList(@RequestParam("dtuicTenantId") Long dtuicTenantId, @RequestParam("dtToken") String dtToken, @RequestParam("userId") Long userId, @RequestParam("engineType")Integer engineType) {
        return accountService.getTenantUnBandList(dtuicTenantId, dtToken, userId, engineType);
    }

}
