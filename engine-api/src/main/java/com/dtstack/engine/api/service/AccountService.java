package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;

import java.util.List;
import java.util.Map;

public interface AccountService {
    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    public void bindAccount(AccountVo accountVo) throws Exception;

    @Forbidden
    public void bindAccountTenant(AccountVo accountVo);

    /**
     * 解绑数据库账号
     */
    public void unbindAccount(AccountTenantVo accountTenantVo, @Param("userId") Long userId) throws Exception;

    /**
     * 解绑数据库账号
     */
    public void updateBindAccount(AccountTenantVo accountTenantVo, @Param("userId") Long userId) throws Exception;

    /**
     * 分页查询
     *
     * @param dtuicTenantId
     * @param username
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResult<List<AccountVo>> pageQuery(@Param("dtuicTenantId") Long dtuicTenantId, @Param("username") String username, @Param("currentPage") Integer currentPage,
                                                 @Param("pageSize") Integer pageSize, @Param("engineType") Integer engineType);

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    public List<Map<String, Object>> getTenantUnBandList(@Param("dtuicTenantId") Long dtuicTenantId, @Param("dtToken") String dtToken, @Param("userId") Long userId);
}
