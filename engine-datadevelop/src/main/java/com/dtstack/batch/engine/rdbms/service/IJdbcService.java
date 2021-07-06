package com.dtstack.batch.engine.rdbms.service;

import com.dtstack.dtcenter.common.enums.EJobType;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname IJdbcService
 * @Description TODO
 * @Date 2020/5/20 14:33
 * @Created chener@dtstack.com
 */
public interface IJdbcService {

    /**
     * 通过租户和用户id获取连接
     * @param dtuicTenantId
     * @param dtuicUserId
     * @return
     */
    Connection getConnection(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String dbName);


    /**
     * 通过租户和用户id获取连接 可以塞入任务参数 相当于set配置提前放入
     *
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param dbName
     * @param taskParam
     * @return
     */
    Connection getConnection(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String dbName, String taskParam);

    /**
     * 执行查询
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param schema
     * @param sql
     * @return
     * @throws Exception
     */
    List<List<Object>> executeQuery(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql);

    /**
     * 执行查询 加limit
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @param limit
     * @return
     */
    List<List<Object>> executeQuery(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql, Integer limit);

    /**
     * 执行查询，带前缀信息
     *
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @param variables
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql, List<String> variables);

    /**
     * 执行查询
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @param variables
     * @param connection
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql, List<String> variables, Connection connection);

    /**
     * 执行查询  传入taskParam
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @param variables
     * @param taskParam
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql, List<String> variables, String taskParam);

    /**
     * 执行查询 返回map结构
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param schema
     * @param sql
     * @return
     * @throws Exception
     */
    List<Map<String, Object>>  executeQueryMapResult(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql);

    /**
     *  执行sql 忽略查询结果
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @return
     */
    Boolean executeQueryWithoutResult(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql);

    /**
     * 执行查询
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @param sql
     * @param connection
     * @return
     */
    Boolean executeQueryWithoutResult(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema, String sql, Connection connection);

    /**
     * 获取当前schema下面所有的表
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @return
     */
    List<String> getTableList(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema);

    /**
     * 获取所有的databases
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eJobType
     * @param schema
     * @return
     */
    List<String> getAllDataBases(Long dtuicTenantId, Long dtuicUserId, EJobType eJobType, String schema);

}
