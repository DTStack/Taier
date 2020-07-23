package com.dtstack.engine.api.service;

import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface AccountService extends DtInsightServer {
    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    @RequestLine("POST /node/account/bindAccount")
    void bindAccount(AccountVo accountVo) throws Exception;

    /**
     * 解绑数据库账号
     */
    @RequestLine("POST /node/account/unbindAccount")
    void unbindAccount(AccountTenantVo accountTenantVo,  Long userId) throws Exception;

    /**
     * 解绑数据库账号
     */
    @RequestLine("POST /node/account/updateBindAccount")
    void updateBindAccount(AccountTenantVo accountTenantVo,  Long userId) throws Exception;

    /**
     * 分页查询
     *
     * @param dtuicTenantId
     * @param username
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestLine("POST /node/account/pageQuery")
    PageResult<List<AccountVo>> pageQuery( Long dtuicTenantId,  String username,  Integer currentPage,
                                           Integer pageSize,  Integer engineType);

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/account/getTenantUnBandList")
    List<Map<String, Object>> getTenantUnBandList( Long dtuicTenantId,  String dtToken,  Long userId,Integer engineType);
}