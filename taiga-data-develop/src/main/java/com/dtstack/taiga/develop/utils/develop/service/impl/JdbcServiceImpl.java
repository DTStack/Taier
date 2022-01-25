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

package com.dtstack.taiga.develop.utils.develop.service.impl;

import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.dtcenter.loader.utils.DBUtil;
import com.dtstack.taiga.common.engine.JdbcInfo;
import com.dtstack.taiga.common.enums.EScheduleJobType;
import com.dtstack.taiga.develop.utils.develop.service.IJdbcService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author chener
 * @Classname JdbcServiceImpl
 * @Description Jdbc 实现类
 * @Date 2020/5/20 14:31
 * @Created chener@dtstack.com
 */
@Service
@Slf4j
public class JdbcServiceImpl implements IJdbcService {

    @Override
    public Connection getConnection(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String dbName) {
        return getConnection(dtuicTenantId, dtuicUserId, eScheduleJobType, dbName,null);
    }

    @Override
    public Connection getConnection(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String dbName, String taskParam) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, eScheduleJobType, dbName);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        return client.getCon(iSourceDTO, taskParam);
    }

    @Override
    public List<List<Object>> executeQuery(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql) {
        return executeQueryWithVariables(dtuicTenantId, dtuicUserId, eScheduleJobType, schema, sql, Collections.emptyList());
    }

    @Override
    public List<List<Object>> executeQuery(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql, Integer limit) {
        return executeQueryWithVariables(dtuicTenantId, dtuicUserId, eScheduleJobType, schema, sql, null, limit, null);
    }

    @Override
    public List<List<Object>> executeQueryWithVariables(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables) {
        return executeQueryWithVariables(dtuicTenantId, dtuicUserId, eScheduleJobType, schema, sql, variables, null, null);
    }


    /**
     * 执行查询
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param variables
     * @param connection
     * @return
     */
    @Override
    public List<List<Object>> executeQueryWithVariables(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables, Connection connection) {
        List<List<Object>> returnList = new ArrayList<>();
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, dtuicUserId, eScheduleJobType);
        DataSourceType dataSourceType = Engine2DTOService.jobTypeTransitionDataSourceType(eScheduleJobType, jdbcInfo.getVersion());
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, dataSourceType.getVal(), schema, jdbcInfo);

        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        List<Map<String, Object>> list = null;
        iSourceDTO.setConnection(connection);
        // 处理 variables SQL
        if (CollectionUtils.isNotEmpty(variables)) {
            variables.forEach(variable ->
                    client.executeSqlWithoutResultSet(iSourceDTO, SqlQueryDTO.builder().sql(variable).limit(jdbcInfo.getMaxRows())
                            .queryTimeout(jdbcInfo.getQueryTimeout()).build())
            );

            list = client.executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(jdbcInfo.getMaxRows())
                    .queryTimeout(jdbcInfo.getQueryTimeout()).build());
        } else {
            list = client.executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(jdbcInfo.getMaxRows())
                    .queryTimeout(jdbcInfo.getQueryTimeout()).build());
        }
        log.info("集群执行SQL查询，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}，sql:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema, sql);
        //数据源插件化 查询出值不符合要求  进行转化
        if (CollectionUtils.isNotEmpty(list)) {
            List<Object> column = new ArrayList<>();
            list.get(0).keySet().stream().forEach(bean->{column.add(bean);});
            returnList.add(column);
            for (Map<String, Object> result : list) {
                List<Object> value = new ArrayList<>();
                result.values().forEach(bean->{value.add(bean);});
                returnList.add(value);
            }
        }
        return returnList;
    }

    @Override
    public List<List<Object>> executeQueryWithVariables(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables, String taskParam) {
        return executeQueryWithVariables(tenantId, userId, eScheduleJobType, schema, sql, variables, null, taskParam);
    }

    /**
     * 执行查询
     * @param dtuicTenantId
     * @param dtuicUserId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param connection
     * @return
     */
    @Override
    public Boolean executeQueryWithoutResult(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql, Connection connection) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, eScheduleJobType, schema);
        iSourceDTO.setConnection(connection);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        client.executeSqlWithoutResultSet(iSourceDTO, SqlQueryDTO.builder().sql(sql).build());
        log.info("集群执行SQL，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}，sql:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema, sql);
        return Boolean.TRUE;
    }


    @Override
    public List<Map<String, Object>> executeQueryMapResult(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql) {
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtuicTenantId, dtuicUserId, eScheduleJobType);
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, Engine2DTOService.jobTypeTransitionDataSourceType(eScheduleJobType, jdbcInfo.getVersion()).getVal(), schema, jdbcInfo);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        log.info("集群执行SQL查询，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}，sql:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema, sql);
        List<Map<String, Object>> list = client.executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(jdbcInfo.getMaxRows())
                .queryTimeout(jdbcInfo.getQueryTimeout()).build());
        return list;
    }

    @Override
    public Boolean executeQueryWithoutResult(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema, String sql) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, eScheduleJobType, schema);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        client.executeSqlWithoutResultSet(iSourceDTO, SqlQueryDTO.builder().sql(sql).build());
        log.info("集群执行SQL，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}，sql:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema, sql);
        return Boolean.TRUE;
    }


    @Override
    public List<String> getTableList(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, eScheduleJobType, schema);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        List<String> tableList = client.getTableList(iSourceDTO, SqlQueryDTO.builder().build());
        log.info("集群查询底层获取所有表名称，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema);
        return tableList;
    }

    @Override
    public List<String> getAllDataBases(Long dtuicTenantId, Long dtuicUserId, EScheduleJobType eScheduleJobType, String schema) {
        ISourceDTO iSourceDTO = Engine2DTOService.get(dtuicTenantId, dtuicUserId, eScheduleJobType, schema);
        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        List<String> allDatabases = client.getAllDatabases(iSourceDTO, SqlQueryDTO.builder().build());
        log.info("集群查询底层获取所有数据库名称，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}", dtuicTenantId, dtuicUserId, eScheduleJobType.getType(), schema);
        return allDatabases;
    }


    public List<List<Object>> executeQueryWithVariables(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables, Integer limit, String taskParam) {
        List<List<Object>> returnList = new ArrayList<>();
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, userId, eScheduleJobType);
        Integer maxRows = limit == null || limit == 0 ? jdbcInfo.getMaxRows() : limit;
        ISourceDTO iSourceDTO = Engine2DTOService.get(tenantId, userId, Engine2DTOService.jobTypeTransitionDataSourceType(eScheduleJobType, jdbcInfo.getVersion()).getVal(), schema, jdbcInfo);

        IClient client = ClientCache.getClient(iSourceDTO.getSourceType());
        // 率先获取Con，复用，为什么不使用try with resource，因为关闭捕获的异常太大了
        Connection con = client.getCon(iSourceDTO, taskParam);
        // 处理 variables SQL
        try {
            iSourceDTO.setConnection(con);
            List<Map<String, Object>> list;
            if (CollectionUtils.isNotEmpty(variables)) {

                variables.forEach(variable ->
                        client.executeSqlWithoutResultSet(iSourceDTO, SqlQueryDTO.builder().sql(variable).limit(jdbcInfo.getMaxRows())
                                .queryTimeout(jdbcInfo.getQueryTimeout()).build())
                );

                list = client.executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(maxRows)
                        .queryTimeout(jdbcInfo.getQueryTimeout()).build());

            } else {
                list = client.executeQuery(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(maxRows)
                        .queryTimeout(jdbcInfo.getQueryTimeout()).build());
            }
            log.info("集群执行SQL查询，dtUicTenantId:{}，dtUicUserId:{}，jobType:{}，schema:{}，sql:{}", tenantId, userId, eScheduleJobType.getType(), schema, sql);

            List<ColumnMetaDTO> columnMetaDataWithSql = client.getColumnMetaDataWithSql(iSourceDTO, SqlQueryDTO.builder().sql(sql).limit(0)
                    .queryTimeout(jdbcInfo.getQueryTimeout()).build());
            if (CollectionUtils.isNotEmpty(columnMetaDataWithSql)){
                List<Object> column = new ArrayList<>();
                columnMetaDataWithSql.stream().forEach(bean -> {
                    column.add(bean.getKey());
                });
                returnList.add(column);
            }
            //数据源插件化 查询出值不符合要求  进行转化
            if (CollectionUtils.isNotEmpty(list)) {
                for (Map<String, Object> result : list) {
                    List<Object> value = new ArrayList<>();
                    result.values().forEach(bean -> {
                        value.add(bean);
                    });
                    returnList.add(value);
                }
            }
        } finally {
            iSourceDTO.setConnection(null);
            DBUtil.closeDBResources(null, null, con);
        }

        return returnList;
    }

}
