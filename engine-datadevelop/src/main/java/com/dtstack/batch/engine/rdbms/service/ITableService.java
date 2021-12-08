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

package com.dtstack.batch.engine.rdbms.service;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.engine.pluginapi.pojo.Column;

import java.util.List;
import java.util.Map;

/**
 * @author jiangbo
 * @date 2019/6/15
 */
public interface ITableService {

    String showCreateTable(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType, String tableName);

    void createDatabase(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType, String comment);

    Boolean isDatabaseExist(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType);

    Boolean isTableExistInDatabase(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType, String tableName);

    Boolean isPartitionTable(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType, String tableName);

//    Map<String, List<Column>> getTablesColumns(Long dtuicTenantId, Long dtuicUserId, ETableType tableType, List<Table> tables);

    List<Column> getColumns(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName);

    List<Column> getPartitionColumns(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName);

    List<String> showPartitions(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String table);

    void alterTableParams(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName, Map<String, String> params);

    void dropTable(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName);

    void renameTable(Long dtuicTenantId, Long dtUicUserId, String dbName, ETableType tableType, String tableName, String newTableName);

//    Table getTableInfo(Long dtuicTenantId, Long dtuicUserId, String dbName, ETableType tableType, String tableName);

    Boolean isPartitionExist(Long dtuicTenantId, Long dtUicUserId,  String partitionVal,  String db, ETableType tableType,  String tableName);

    Boolean isView(Long dtuicTenantId, Long dtUicUserId, String db, ETableType tableType,  String tableName);

    /**
     * 计算表大小
     *
     * @param dtUicTenantId
     * @param dtUicUserId
     * @param db
     * @param tableType
     * @param tableName
     * @return
     */
    Long getTableSize(Long dtUicTenantId, Long dtUicUserId, String db, ETableType tableType,  String tableName);
}
