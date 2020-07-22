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

    /**
     * 解绑数据库账号
     */
    public void unbindAccount(AccountTenantVo accountTenantVo,  Long userId) throws Exception;

    /**
     * 解绑数据库账号
     */
    public void updateBindAccount(AccountTenantVo accountTenantVo,  Long userId) throws Exception;

    /**
     * 分页查询
     *
     * @param dtuicTenantId
     * @param username
     * @param currentPage
     * @param pageSize
     * @return
     */
    public PageResult<List<AccountVo>> pageQuery( Long dtuicTenantId,  String username,  Integer currentPage,
                                                  Integer pageSize,  Integer engineType);

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    public List<Map<String, Object>> getTenantUnBandList( Long dtuicTenantId,  String dtToken,  Long userId,Integer engineType);
}