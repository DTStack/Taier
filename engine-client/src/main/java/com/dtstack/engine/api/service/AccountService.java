package com.dtstack.engine.api.service;


import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.Param;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface AccountService extends DtInsightServer {
    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    @RequestLine("POST /node/account/bindAccount")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> bindAccount(AccountVo accountVo);

    /**
     * 解绑数据库账号
     */
    @RequestLine("POST /node/account/unbindAccount")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> unbindAccount( AccountTenantVo accountTenantVo,  @Param("userId") Long userId);

    /**
     * 解绑数据库账号
     */
    @RequestLine("POST /node/account/updateBindAccount")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<Void> updateBindAccount( AccountTenantVo accountTenantVo,  @Param("userId") Long userId);

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
    ApiResponse<PageResult<List<AccountVo>>> pageQuery(@Param("dtuicTenantId") Long dtuicTenantId, @Param("username") String username, @Param("currentPage") Integer currentPage,
                                                       @Param("pageSize") Integer pageSize, @Param("engineType") Integer engineType);

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    @RequestLine("POST /node/account/getTenantUnBandList")
    ApiResponse<List<Map<String, Object>>> getTenantUnBandList(@Param("dtuicTenantId")Long dtuicTenantId, @Param("dtToken")String dtToken, @Param("userId") Long userId, @Param("engineType")Integer engineType);
}