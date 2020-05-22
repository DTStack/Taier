package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Account;
import com.dtstack.engine.api.domain.AccountTenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.dto.AccountDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.AccountTenantVo;
import com.dtstack.engine.api.vo.AccountVo;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.AccountDao;
import com.dtstack.engine.dao.AccountTenantDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.engine.dao.UserDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.schedule.common.enums.*;
import com.dtstack.schedule.common.util.Base64Util;
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

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

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

    /**
     * 绑定数据库账号 到对应数栈账号下的集群
     */
    public void bindAccount(AccountVo accountVo) throws Exception {
        if (Objects.isNull(accountVo)) {
            throw new RdosDefineException("绑定参数不能为空");
        }
        if (Objects.isNull(accountVo.getUserId()) || Objects.isNull(accountVo.getUsername()) || Objects.isNull(accountVo.getPassword())
                || Objects.isNull(accountVo.getBindTenantId()) || Objects.isNull(accountVo.getBindUserId()) || Objects.isNull(accountVo.getName())) {
            throw new RdosDefineException("请填写必要参数");
        }
        //校验db账号测试连通性
        checkDataSourceConnect(accountVo);
        //绑定账号
        bindAccountTenant(accountVo);
    }

    private void checkDataSourceConnect(AccountVo accountVo) throws SQLException {
        JSONObject jdbc = null;
        DataBaseType dataBaseType = null;
        if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()){
            jdbc = JSONObject.parseObject(clusterService.tiDBInfo(accountVo.getBindTenantId(), null));
            dataBaseType = DataBaseType.TiDB;
        }else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()){
            jdbc = JSONObject.parseObject(clusterService.oracleInfo(accountVo.getBindTenantId(), null));
            dataBaseType = DataBaseType.Oracle;
        }
        if (Objects.isNull(jdbc)) {
            if (MultiEngineType.TIDB.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("请先绑定TiDB组件");
            } else if (MultiEngineType.ORACLE.getType() == accountVo.getEngineType()) {
                throw new RdosDefineException("请先绑定Oracle组件");
            }
        }
        JSONObject pluginInfo = new JSONObject();
        pluginInfo.put("dbUrl", jdbc.getString("jdbcUrl"));
        pluginInfo.put("userName", accountVo.getName());
        pluginInfo.put("pwd", accountVo.getPassword());
        pluginInfo.put("driverClassName", dataBaseType.getDriverClassName());
        try {
            workerOperator.executeQuery(DataBaseType.TiDB.getTypeName().toLowerCase(), pluginInfo.toJSONString(), "show databases", "");
        } catch (Exception e) {
            throw new RdosDefineException("测试联通性失败 :" + ExceptionUtil.getErrorMessage(e));
        }
    }


    @Forbidden
    @Transactional
    public void bindAccountTenant(AccountVo accountVo) {
        Account dbAccountByName = new Account();
        dbAccountByName.setName(accountVo.getName());
        dbAccountByName.setPassword(Base64Util.baseEncode(accountVo.getPassword()));
        dbAccountByName.setType(getDataSourceTypeByMultiEngineType(accountVo.getEngineType()));
        dbAccountByName.setCreateUserId(accountVo.getUserId());
        dbAccountByName.setModifyUserId(accountVo.getUserId());
        accountDao.insert(dbAccountByName);
        log.info("add db account {} [{}] ", dbAccountByName.getName(), dbAccountByName.getId());
        //bindUserId 是从uic获取 需要转换下
        User dtUicUserId = userDao.getByDtUicUserId(accountVo.getBindUserId());
        //bindTenantId 需要转换为租户id
        Long tenantId = tenantDao.getIdByDtUicTenantId(accountVo.getBindTenantId());
        if (Objects.isNull(tenantId)) {
            throw new RdosDefineException("租户不存在");
        }
        if (Objects.nonNull(dtUicUserId)) {
            AccountTenant dbAccountTenant = accountTenantDao.getByAccount(dtUicUserId.getId(), tenantId, dbAccountByName.getId(), Deleted.NORMAL.getStatus());
            if (Objects.nonNull(dbAccountTenant)) {
                throw new RdosDefineException("该账号已绑定对应产品账号");
            }
        } else {
            dtUicUserId = new User();
            //添加新用户到user表
            dtUicUserId.setDtuicUserId(accountVo.getBindUserId());
            dtUicUserId.setUserName(accountVo.getUsername());
            dtUicUserId.setPhoneNumber(accountVo.getPhone());
            dtUicUserId.setEmail(StringUtils.isNotBlank(accountVo.getEmail()) ? accountVo.getEmail() : accountVo.getUsername());
            dtUicUserId.setStatus(EntityStatus.normal.getStatus());
            userDao.insert(dtUicUserId);
            dtUicUserId = userDao.getByDtUicUserId(accountVo.getBindUserId());
        }
        AccountTenant accountTenant = new AccountTenant();
        accountTenant.setUserId(dtUicUserId.getId());
        accountTenant.setTenantId(tenantId);
        accountTenant.setAccountId(dbAccountByName.getId());
        accountTenant.setCreateUserId(accountVo.getUserId());
        accountTenant.setModifyUserId(accountVo.getUserId());
        accountTenant.setIsDeleted(Deleted.NORMAL.getStatus());
        accountTenantDao.insert(accountTenant);
        log.info("bind db account id [{}]username [{}] to user [{}] tenant {}  success ", accountTenant.getAccountId(), dbAccountByName.getName(),
                accountTenant.getUserId(), tenantId);
    }

    /**
     * 把引擎类型转化为dataSource类型
     * @param multiEngineType
     * @return
     */
    private Integer getDataSourceTypeByMultiEngineType(Integer multiEngineType) {
        if (null == multiEngineType) {
            return null;
        }
        if (MultiEngineType.TIDB.getType() == multiEngineType) {
            return DataSourceType.TiDB.getVal();
        } else if (MultiEngineType.ORACLE.getType() == multiEngineType) {
            return DataSourceType.Oracle.getVal();
        }
        return 0;
    }



    /**
     * 解绑数据库账号
     */
    @Transactional
    public void unbindAccount(AccountTenantVo accountTenantVo, @Param("userId") Long userId) throws Exception {
        if (Objects.isNull(accountTenantVo) || Objects.isNull(accountTenantVo.getId())) {
            throw new RdosDefineException("参数不能为空");
        }
        if (StringUtils.isBlank(accountTenantVo.getName()) || StringUtils.isBlank(accountTenantVo.getPassword())) {
            throw new RdosDefineException("解绑账号信息不能为空");
        }

        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (Objects.isNull(dbAccountTenant)) {
            throw new RdosDefineException("该账号未绑定对应集群");
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if (Objects.isNull(account)) {
            throw new RdosDefineException("解绑账号不存在");
        }
        if (!account.getName().equals(accountTenantVo.getName())) {
            throw new RdosDefineException("解绑失败，请使用绑定时输入的数据库账号进行解绑");
        }
        String oldPassWord = Base64Util.baseDecode(account.getPassword());
        if (!oldPassWord.equals(accountTenantVo.getPassword())) {
            throw new RdosDefineException("解绑失败,解绑账号密码错误");
        }
        //标记为删除
        dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
        dbAccountTenant.setIsDeleted(Deleted.DELETED.getStatus());
        dbAccountTenant.setModifyUserId(userId);
        accountTenantDao.update(dbAccountTenant);

        account.setGmtModified(new Timestamp(System.currentTimeMillis()));
        account.setIsDeleted(Deleted.DELETED.getStatus());
        account.setModifyUserId(userId);
        accountDao.update(account);
        log.info("unbind db account id [{}] to user [{}] tenant {}  success ", dbAccountTenant.getAccountId(), dbAccountTenant.getUserId(), dbAccountTenant.getTenantId());
        List<Long> dtUicTenantIdByIds = tenantDao.listDtUicTenantIdByIds(Lists.newArrayList(dbAccountTenant.getTenantId()));
        //刷新缓存
        if (CollectionUtils.isNotEmpty(dtUicTenantIdByIds)) {
            User dbUser = userDao.getByUserId(dbAccountTenant.getUserId());
            if (Objects.nonNull(dbUser)) {
                consoleCache.publishRemoveMessage(String.format("%s.%s", dtUicTenantIdByIds.get(0), dbUser.getDtuicUserId()));
            }
        }
    }


    /**
     * 解绑数据库账号
     */
    @Transactional
    public void updateBindAccount(AccountTenantVo accountTenantVo, @Param("userId") Long userId) throws Exception {
        if (Objects.isNull(accountTenantVo) || Objects.isNull(accountTenantVo.getId())) {
            throw new RdosDefineException("参数不能为空");
        }
        if (StringUtils.isBlank(accountTenantVo.getName()) || StringUtils.isBlank(accountTenantVo.getPassword())) {
            throw new RdosDefineException("更新账号信息不能为空");
        }

        AccountTenant dbAccountTenant = accountTenantDao.getById(accountTenantVo.getId());
        if (Objects.isNull(dbAccountTenant)) {
            throw new RdosDefineException("该账号未绑定对应集群");
        }
        AccountVo accountVO = new AccountVo();
        List<Long> dtUicTenantIdByIds = tenantDao.listDtUicTenantIdByIds(Lists.newArrayList(dbAccountTenant.getTenantId()));
        if (CollectionUtils.isEmpty(dtUicTenantIdByIds)) {
            throw new RdosDefineException("该账号未绑定对应集群");
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
        oldAccount.setModifyUserId(userId);
        accountDao.update(oldAccount);
        //添加新账号
        Account newAccount = new Account();
        newAccount.setName(accountVO.getName());
        newAccount.setPassword(Base64Util.baseEncode(accountVO.getPassword()));
        newAccount.setType(getDataSourceTypeByMultiEngineType(accountTenantVo.getEngineType()));
        newAccount.setCreateUserId(userId);
        newAccount.setModifyUserId(userId);
        accountDao.insert(newAccount);

        //更新关联关系
        dbAccountTenant.setGmtModified(new Timestamp(System.currentTimeMillis()));
        dbAccountTenant.setAccountId(newAccount.getId());
        dbAccountTenant.setModifyUserId(userId);
        accountTenantDao.update(dbAccountTenant);
        log.info("modify db account id [{}] old account [{}] new account [{}]  success ", dbAccountTenant.getId(), oldAccount.getId(), newAccount.getId());
        User dbUser = userDao.getByUserId(dbAccountTenant.getUserId());
        if (Objects.nonNull(dbUser)) {
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
    public PageResult<List<AccountVo>> pageQuery(@Param("dtuicTenantId") Long dtuicTenantId, @Param("username") String username, @Param("currentPage") Integer currentPage,
                                                 @Param("pageSize") Integer pageSize, @Param("engineType") Integer engineType) {
        if (Objects.isNull(dtuicTenantId)) {
            throw new RdosDefineException("绑定参数不能为空");
        }
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtuicTenantId);
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setTenantId(tenantId);
        accountDTO.setName(username);
        accountDTO.setType(getDataSourceTypeByMultiEngineType(engineType));
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
    public List<Map<String, Object>> getTenantUnBandList(@Param("dtuicTenantId") Long dtuicTenantId, @Param("dtToken") String dtToken, @Param("userId") Long userId) {
        if (Objects.isNull(dtuicTenantId)) {
            throw new RdosDefineException("请选择对应租户");
        }
        //获取uic下该租户所有用户
        List<Map<String, Object>> uicUsers = DtUicUserConnect.getAllUicUsers(environmentContext.getDtUicUrl(), "RDOS", dtuicTenantId, dtToken);
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtuicTenantId);
        if (Objects.isNull(tenantId)) {
            throw new RdosDefineException("请先绑定租户到集群");
        }
        //添加超级管理员
        User rootUser = userDao.getByUserId(userId);
        if (Objects.nonNull(rootUser)) {
            Map<String, Object> rootMap = new HashMap<>(5);
            rootMap.put("userName", rootUser.getUserName());
            rootMap.put("userId", rootUser.getDtuicUserId());
            rootMap.put("active", true);
            rootMap.put("createTime", rootUser.getGmtCreate());
            uicUsers.add(rootMap);
        }
        if (CollectionUtils.isEmpty(uicUsers)) {
            return new ArrayList(0);
        }
        List<AccountDTO> tenantUser = accountTenantDao.getTenantUser(tenantId);
        List<Long> userInIds;
        if (CollectionUtils.isNotEmpty(tenantUser)) {
            userInIds = tenantUser.stream().map(AccountDTO::getDtuicUserId).collect(Collectors.toList());
        } else {
            userInIds = new ArrayList<>();
        }
        //过滤租户下已绑定的用户
        return uicUsers.stream()
                .filter((uicUser) -> !userInIds.contains(Long.valueOf(uicUser.get("userId").toString())))
                .collect(Collectors.toList());

    }
}
