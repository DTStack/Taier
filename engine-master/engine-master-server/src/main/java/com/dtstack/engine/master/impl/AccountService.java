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

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.Account;
import com.dtstack.engine.domain.AccountTenant;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.engine.domain.User;
import com.dtstack.engine.dto.AccountDTO;
import com.dtstack.engine.common.pager.PageQuery;
import com.dtstack.engine.common.pager.PageResult;
import com.dtstack.engine.master.vo.AccountTenantVo;
import com.dtstack.engine.master.vo.AccountVo;
import com.dtstack.engine.master.vo.user.UserVO;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.dao.AccountDao;
import com.dtstack.engine.dao.AccountTenantDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.dao.UserDao;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.enums.AccountType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.common.enums.DataBaseType;
import com.dtstack.engine.common.enums.Deleted;
import com.dtstack.engine.common.enums.EntityStatus;
import com.dtstack.engine.common.enums.Sort;
import com.dtstack.engine.common.util.Base64Util;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author yuebai
 * @date 2020-02-14
 */
@Service
public class AccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountTenantDao accountTenantDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private UserService userService;

    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    public void bindAccount(AccountVo accountVo) throws Exception {
        if (null == accountVo) {
            throw new RdosDefineException("The binding parameter cannot be empty");
        }
        if (null == accountVo.getUserId() || null == accountVo.getUsername() || null == accountVo.getPassword()
                || null == accountVo.getBindTenantId() || null == accountVo.getBindUserId() || null == accountVo.getName()) {
            throw new RdosDefineException("Please fill in the necessary parameters");
        }
        //校验db账号测试连通性
        checkDataSourceConnect(accountVo);
        //绑定账号
        bindAccountTenant(accountVo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void bindAccountList(List<AccountVo> list) throws Exception {
        for (AccountVo accountVo : list) {
            bindAccount(accountVo);
        }
    }

    private void checkDataSourceConnect(AccountVo accountVo) throws SQLException {
        JSONObject jdbc = null;
        DataBaseType dataBaseType = null;
        if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.tiDBInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.TiDB;
        } else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.oracleInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.Oracle;
        } else if (MultiEngineType.GREENPLUM.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.greenplumInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.Greenplum6;
        } else if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.adbPostgrepsqlInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.adb_Postgrepsql;
        } else if (MultiEngineType.MYSQL.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.mysqlInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.MySql8;
        } else if (MultiEngineType.DB2.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.db2Info(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.DB2;
        } else if (MultiEngineType.SQL_SERVER.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.sqlServerInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.SQLServer;
        } else if (MultiEngineType.OCEANBASE.getType() == accountVo.getEngineType()) {
            jdbc = JSONObject.parseObject(clusterService.oceanBaseInfo(accountVo.getBindTenantId(), null,null));
            dataBaseType = DataBaseType.OCEANBASE;
        } else if (MultiEngineType.HADOOP.getType() == accountVo.getEngineType()) {
            //如果是HADOOP，则添加ldap,无需校验连通性
            return;
        }

        if (null == jdbc) {
            if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind TiDB components first");
            } else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind Oracle components first");
            } else if (MultiEngineType.GREENPLUM.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind the GREENPLUMe component first");
            } else if (MultiEngineType.ANALYTICDB_FOR_PG.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("Please bind the AnalyticDB for PostgreSQL component first");
            } else{
                throw new RdosDefineException("Please bind the corresponding components first");
            }
        }

        JSONObject pluginInfo = new JSONObject();
        pluginInfo.put("jdbcUrl", jdbc.getString("jdbcUrl"));
        pluginInfo.put("username", accountVo.getName());
        pluginInfo.put("password", accountVo.getPassword());
        pluginInfo.put(ConfigConstant.TYPE_NAME_KEY,dataBaseType.getTypeName().toLowerCase());
        try {
            workerOperator.testConnect(dataBaseType.getTypeName().toLowerCase(), pluginInfo.toJSONString());
        } catch (Exception e) {
            throw new RdosDefineException("Failed to test connectivity :" + ExceptionUtil.getErrorMessage(e));
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void bindAccountTenant(AccountVo accountVo) {

        try {
            checkAccountVo(accountVo);
            Account dbAccountByName = insertAccount(accountVo);
            //bindUserId 是从uic获取 需要转换下
            User dtUicUserId = userDao.getByDtUicUserId(accountVo.getBindUserId());
            //bindTenantId 需要转换为租户id
            Long tenantId = tenantDao.getIdByDtUicTenantId(accountVo.getBindTenantId());
            if ( null == tenantId ) {
                throw new RdosDefineException("租户不存在");
            }
            if ( null != dtUicUserId ) {
                AccountTenant dbAccountTenant = accountTenantDao.getByAccount(dtUicUserId.getId(), tenantId, dbAccountByName.getId(), Deleted.NORMAL.getStatus());
                if ( null != dbAccountTenant ) {
                    throw new RdosDefineException("该账号已绑定对应产品账号");
                }
            } else {
                this.addUser(accountVo.getUsername(), accountVo.getBindUserId(), accountVo.getPhone(), accountVo.getEmail());
                //添加新用户到user表
                dtUicUserId = userDao.getByDtUicUserId(accountVo.getBindUserId());
            }
            if (StringUtils.isNotBlank(accountVo.getModifyUserName())) {
                this.addUser(accountVo.getModifyUserName(), accountVo.getUserId(), "", accountVo.getModifyUserName());
            }
            insertAccountTenant(accountVo, dbAccountByName, dtUicUserId, tenantId);
        } catch (Exception e) {
            throw new RdosDefineException("绑定账户租户关系异常");
        }
    }

    /**
     * @author newman
     * @Description 插入账户租户关系对象
     * @Date 2020-12-22 11:56
     * @param accountVo:
     * @param dbAccountByName:
     * @param dtUicUserId:
     * @param tenantId:
     * @return: void
     **/
    private void insertAccountTenant(AccountVo accountVo, Account dbAccountByName, User dtUicUserId, Long tenantId) {
        AccountTenant accountTenant = new AccountTenant();
        accountTenant.setUserId(dtUicUserId.getId());
        accountTenant.setTenantId(tenantId);
        accountTenant.setAccountId(dbAccountByName.getId());
        accountTenant.setCreateUserId(accountVo.getUserId());
        accountTenant.setModifyUserId(accountVo.getUserId());
        accountTenant.setIsDeleted(Deleted.NORMAL.getStatus());
        accountTenantDao.insert(accountTenant);
        LOGGER.info("bind db account id [{}]username [{}] to user [{}] tenant {}  success ", accountTenant.getAccountId(), dbAccountByName.getName(),
                accountTenant.getUserId(), tenantId);
    }

    /**
     * @author newman
     * @Description 插入账号信息
     * @Date 2020-12-22 11:53
     * @param accountVo:
     * @return: com.dtstack.engine.domain.Account
     **/
    private Account insertAccount(AccountVo accountVo) {
        Account dbAccountByName = new Account();
        dbAccountByName.setName(accountVo.getName());
        dbAccountByName.setPassword(StringUtils.isBlank(accountVo.getPassword()) ? "" : Base64Util.baseEncode(accountVo.getPassword()));
        dbAccountByName.setType(getAccountTypeByMultiEngineType(accountVo.getEngineType()));
        dbAccountByName.setCreateUserId(accountVo.getUserId());
        dbAccountByName.setModifyUserId(accountVo.getUserId());
        accountDao.insert(dbAccountByName);
        LOGGER.info("add db account {} [{}] ", dbAccountByName.getName(), dbAccountByName.getId());
        return dbAccountByName;
    }


    private void addUser(String userName,Long dtuicUserId,String phoneNumber,String email){
        User dbUser = userDao.getByDtUicUserId(dtuicUserId);
        if(null != dbUser){
            return;
        }
        User addUser = new User();
        addUser.setDtuicUserId(dtuicUserId);
        addUser.setUserName(userName);
        addUser.setPhoneNumber(StringUtils.isBlank(phoneNumber) ? "" : phoneNumber);
        addUser.setEmail(StringUtils.isNotBlank(email) ? email : userName);
        addUser.setStatus(EntityStatus.normal.getStatus());
        userDao.insert(addUser);
    }

    /**
     * 把引擎类型转化为dataSource类型
     * @param multiEngineType
     * @return
     */
    private Integer getAccountTypeByMultiEngineType(Integer multiEngineType) {
        if (null == multiEngineType) {
            return null;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return AccountType.TiDB.getVal();
        } else if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return AccountType.Oracle.getVal();
        } else if (MultiEngineType.GREENPLUM.getType() == multiEngineType) {
            return AccountType.GREENPLUM6.getVal();
        } else if (MultiEngineType.HADOOP.getType() == multiEngineType) {
            return AccountType.LDAP.getVal();
        }
        return 0;
    }



    /**
     * 解绑数据库账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void unbindAccount(AccountTenantVo accountTenantVo) throws Exception {
        if (null == accountTenantVo || null == accountTenantVo.getId()) {
            throw new RdosDefineException("Parameter cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getName())) {
            throw new RdosDefineException("Unbind account information cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getPassword())){
            accountTenantVo.setPassword("");
        }
        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (null == dbAccountTenant) {
            throw new RdosDefineException("The account is not bound to the corresponding cluster");
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if (null == account) {
            throw new RdosDefineException("Unbind account does not exist");
        }
        if (!account.getName().equals(accountTenantVo.getName())) {
            throw new RdosDefineException("Unbinding failed, please use the database account entered during binding to unbind");
        }
        String oldPassWord = StringUtils.isBlank(account.getPassword()) ? "" : Base64Util.baseDecode(account.getPassword());
        if (!oldPassWord.equals(accountTenantVo.getPassword())) {
            throw new RdosDefineException("Unbind failed, unbind account password is wrong");
        }
        try {
            //标记为删除
            dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
            dbAccountTenant.setIsDeleted(Deleted.DELETED.getStatus());
            dbAccountTenant.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
            accountTenantDao.update(dbAccountTenant);

            account.setGmtModified(new Timestamp(System.currentTimeMillis()));
            account.setIsDeleted(Deleted.DELETED.getStatus());
            account.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
            this.addUser(accountTenantVo.getModifyUserName(),accountTenantVo.getModifyDtUicUserId(),"",accountTenantVo.getModifyUserName());
            accountDao.update(account);
            LOGGER.info("unbind db account id [{}] to user [{}] tenant {}  success ", dbAccountTenant.getAccountId(), dbAccountTenant.getUserId(), dbAccountTenant.getTenantId());
            List<Long> dtUicTenantIdByIds = tenantDao.listDtUicTenantIdByIds(Lists.newArrayList(dbAccountTenant.getTenantId()));
            //刷新缓存
            if (CollectionUtils.isNotEmpty(dtUicTenantIdByIds)) {
                User dbUser = userDao.getByUserId(dbAccountTenant.getUserId());
                if ( null != dbUser ) {
                    consoleCache.publishRemoveMessage(String.format("%s.%s", dtUicTenantIdByIds.get(0), dbUser.getDtuicUserId()));
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException("解绑异常",e);
        }
    }


    /**
     * 更改数据库账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBindAccount(AccountTenantVo accountTenantVo) throws Exception {
        if (null == accountTenantVo || null == accountTenantVo.getId()) {
            throw new RdosDefineException("Parameter cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getName())) {
            throw new RdosDefineException("Update account information cannot be empty");
        }
        if (StringUtils.isBlank(accountTenantVo.getPassword())){
            accountTenantVo.setPassword("");
        }
        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (null == dbAccountTenant) {
            throw new RdosDefineException("The account is not bound to the corresponding cluster");
        }
        AccountVo accountVO = new AccountVo();
        List<Long> dtUicTenantIdByIds = tenantDao.listDtUicTenantIdByIds(Lists.newArrayList(dbAccountTenant.getTenantId()));
        if (CollectionUtils.isEmpty(dtUicTenantIdByIds)) {
            throw new RdosDefineException("The account is not bound to the corresponding cluster");
        }
        accountVO.setBindTenantId(dtUicTenantIdByIds.get(0));
        accountVO.setName(accountTenantVo.getName());
        accountVO.setPassword(accountTenantVo.getPassword());
        accountVO.setEngineType(accountTenantVo.getEngineType());
        //校验db账号测试连通性
        checkDataSourceConnect(accountVO);
        Account oldAccount = new Account();
        //删除旧账号
        oldAccount.setId(dbAccountTenant.getAccountId());
        oldAccount.setGmtModified(new Timestamp(System.currentTimeMillis()));
        oldAccount.setIsDeleted(Deleted.DELETED.getStatus());
        oldAccount.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        accountDao.update(oldAccount);
        //添加新账号
        Account newAccount = new Account();
        newAccount.setName(accountVO.getName());
        newAccount.setPassword(Base64Util.baseEncode(accountVO.getPassword()));
        newAccount.setType(getAccountTypeByMultiEngineType(accountTenantVo.getEngineType()));
        newAccount.setCreateUserId(accountTenantVo.getModifyDtUicUserId());
        newAccount.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        accountDao.insert(newAccount);

        //更新关联关系
        dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
        dbAccountTenant.setAccountId(newAccount.getId());
        dbAccountTenant.setModifyUserId(accountTenantVo.getModifyDtUicUserId());
        this.addUser(accountTenantVo.getModifyUserName(),accountTenantVo.getModifyDtUicUserId(),"",accountTenantVo.getModifyUserName());
        accountTenantDao.update(dbAccountTenant);
        LOGGER.info("modify db account id [{}] old account [{}] new account [{}]  success ", dbAccountTenant.getId(), oldAccount.getId(), newAccount.getId());
        User dbUser = userDao.getByUserId(dbAccountTenant.getUserId());
        if (null != dbUser) {
            consoleCache.publishRemoveMessage(String.format("%s.%s", dtUicTenantIdByIds.get(0), dbUser.getDtuicUserId()));
        }
    }


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
                                                  Integer pageSize,  Integer engineType,Long dtuicUserId) {
        if (null == dtuicTenantId) {
            throw new RdosDefineException("The binding parameter cannot be empty");
        }
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtuicTenantId);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setTenantId(tenantId);
        accountDTO.setName(username);
        accountDTO.setDtuicUserId(dtuicUserId);
        accountDTO.setType(getAccountTypeByMultiEngineType(engineType));
        PageQuery<AccountDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(accountDTO);
        Integer count = accountTenantDao.generalCount(accountDTO);
        if (0 >= count) {
            return new PageResult<>(null, 0, pageQuery);
        }
        List<AccountDTO> accountDTOS = accountTenantDao.generalQuery(pageQuery);
        List<AccountVo> data = new ArrayList<>(accountDTOS.size());
        for (AccountDTO dto : accountDTOS) {
            data.add(new AccountVo(dto));
        }
        return new PageResult<>(data, count, pageQuery);
    }

    /**
     * 获取租户未绑定用户列表
     *
     * @param dtuicTenantId
     * @return
     */
    public List<UserVO> getTenantUnBandList(Long dtuicTenantId, Integer engineType) {
        if (null == dtuicTenantId) {
            throw new RdosDefineException("Please select the corresponding tenant");
        }
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtuicTenantId);
        if (null == tenantId) {
            throw new RdosDefineException("Please bind the tenant to the cluster first");
        }
        List<UserVO> uicUsers = userService.findAllUser();
        if (CollectionUtils.isEmpty(uicUsers)) {
            return new ArrayList<>(0);
        }
        List<AccountDTO> tenantUser = accountTenantDao.getTenantUser(tenantId,getAccountTypeByMultiEngineType(engineType));
        List<Long> userInIds;
        if (CollectionUtils.isNotEmpty(tenantUser)) {
            userInIds = tenantUser.stream().map(AccountDTO::getDtuicUserId).collect(Collectors.toList());
        } else {
            userInIds = new ArrayList<>();
        }
        //过滤租户下已绑定的用户
        return uicUsers.stream()
                .filter((uicUser) -> !userInIds.contains(uicUser.getDtuicUserId()))
                .collect(Collectors.toList());
    }

    private void checkAccountVo(AccountVo accountVo) {

        Integer accountType = getAccountTypeByMultiEngineType(accountVo.getEngineType());
        if (accountType != AccountType.LDAP.getVal()) {
            return;
        }
        //检查ldap 同一个租户下一个ldap name 只能被一个账号绑定
        Tenant tenant = tenantDao.getByDtUicTenantId(accountVo.getBindTenantId());
        if ( null == tenant ) {
            return;
        }
        User user = userDao.getByDtUicUserId(accountVo.getBindUserId());
        if ( null == user ) {
            return;
        }
        //检查同租户下用户是否已被绑定
        Account one = accountDao.getOne(tenant.getId(), user.getId(), accountType, null);
        if ( null != one ) {
            throw new RdosDefineException("User "+ user.getUserName() + "is bound");
        }
        //检查同租户下用户名是否被绑定
        Account exit = accountDao.getOne(tenant.getId(), null, accountType, accountVo.getName());
        if ( null != exit ) {
            throw new RdosDefineException("User "+ accountVo.getName() + "is bound");
        }

    }
}
