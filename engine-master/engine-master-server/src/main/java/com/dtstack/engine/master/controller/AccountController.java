package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.api.vo.AccountVoLists;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.master.impl.AccountService;
import com.dtstack.engine.master.router.DtRequestParam;
import com.dtstack.engine.master.router.util.CookieUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public PageResult<List<AccountVo>> pageQuery(@DtRequestParam("dtuicTenantId") Long dtuicTenantId, @DtRequestParam("username") String username, @DtRequestParam("currentPage") Integer currentPage,
                                                 @DtRequestParam("pageSize") Integer pageSize, @DtRequestParam("engineType") Integer engineType) {
        return accountService.pageQuery(dtuicTenantId, username, currentPage, pageSize, engineType);
    }

    @RequestMapping(value="/getTenantUnBandList", method = {RequestMethod.POST})
    @ApiOperation(value = "获取租户未绑定用户列表")
    public List<Map<String, Object>> getTenantUnBandList(@DtRequestParam("dtuicTenantId") Long dtuicTenantId, @DtRequestParam("dtToken") String dtToken, HttpServletRequest request, @DtRequestParam("engineType")Integer engineType) {
        return accountService.getTenantUnBandList(dtuicTenantId, dtToken, CookieUtil.getUserId(request.getCookies()), engineType);
    }

}
