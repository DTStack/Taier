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

package com.dtstack.taiga.develop.utils.develop.service;

import com.dtstack.taiga.common.enums.EComponentType;
import com.dtstack.taiga.common.enums.EScheduleJobType;

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
     * @param tenantId
     * @param userId
     * @return
     */
    Connection getConnection(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String dbName);


    /**
     * 通过租户和用户id获取连接 可以塞入任务参数 相当于set配置提前放入
     *
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param dbName
     * @param taskParam
     * @return
     */
    Connection getConnection(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String dbName, String taskParam);

    /**
     * 执行查询
     * @param tenantId
     * @param userId
     * @param schema
     * @param sql
     * @return
     * @throws Exception
     */
    List<List<Object>> executeQuery(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql);

    /**
     * 执行查询 加limit
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param limit
     * @return
     */
    List<List<Object>> executeQuery(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, Integer limit);

    /**
     * 执行查询，带前缀信息
     *
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param variables
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables);

    /**
     * 执行查询
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param variables
     * @param connection
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables, Connection connection);

    /**
     * 执行查询  传入taskParam
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param variables
     * @param taskParam
     * @return
     */
    List<List<Object>> executeQueryWithVariables(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, List<String> variables, String taskParam);

    /**
     * 执行查询 返回map结构
     * @param tenantId
     * @param userId
     * @param schema
     * @param sql
     * @return
     * @throws Exception
     */
    List<Map<String, Object>>  executeQueryMapResult(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql);

    /**
     *  执行sql 忽略查询结果
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @return
     */
    Boolean executeQueryWithoutResult(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql);

    /**
     * 执行查询
     * @param tenantId
     * @param userId
     * @param eScheduleJobType
     * @param schema
     * @param sql
     * @param connection
     * @return
     */
    Boolean executeQueryWithoutResult(Long tenantId, Long userId, EScheduleJobType eScheduleJobType, String schema, String sql, Connection connection);

    /**
     * 获取当前schema下面所有的表
     * @param tenantId
     * @param eScheduleJobType
     * @param schema
     * @return
     */
    List<String> getTableList(Long tenantId, EScheduleJobType eScheduleJobType, String schema);

    /**
     * 获取所有的databases
     *
     * @param clusterId      集群ID
     * @param eComponentType 组件类型
     * @param schema
     * @return
     */
    List<String> getAllDataBases(Long clusterId, EComponentType eComponentType, String schema);

    /**
     * 创建对应的db/schema
     *
     * @param clusterId      集群ID
     * @param eComponentType 组件类型
     * @param schema
     * @param comment
     */
    void createDatabase(Long clusterId, EComponentType eComponentType, String schema, String comment);

}
