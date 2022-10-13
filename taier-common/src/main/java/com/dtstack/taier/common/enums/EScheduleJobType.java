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

package com.dtstack.taier.common.enums;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.pluginapi.enums.EJobType;

import java.util.ArrayList;
import java.util.List;

public enum EScheduleJobType {

    /**
     * 虚节点
     */
    VIRTUAL(-1, "虚节点", -1, 0, null, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * SparkSQL
     */
    SPARK_SQL(0, "SparkSQL", EJobType.SQL.getType(), 1, EComponentType.SPARK, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * 数据同步
     */
    SYNC(2, "数据同步", EJobType.SYNC.getType(), 3, EComponentType.FLINK, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * FlinkSQL
     */
    FLINK_SQL(5, "FlinkSQL", EJobType.SQL.getType(), 5, EComponentType.FLINK, EComputeType.STREAM, EJobClientType.ENGINE_PLUGIN),

    /**
     * 实时采集
     */
    DATA_ACQUISITION(6, "实时采集", EJobType.SYNC.getType(), 4, EComponentType.FLINK, EComputeType.STREAM, EJobClientType.ENGINE_PLUGIN),

    /**
     * HiveSQL
     */
    HIVE_SQL(7, "HiveSQL", EJobType.SQL.getType(), 4, EComponentType.HIVE_SERVER, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * OceanBaseSQL
     */
    OCEANBASE_SQL(8, "OceanBaseSQL", EJobType.SQL.getType(), 4, EComponentType.OCEAN_BASE, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * 工作流
     */
    WORK_FLOW(10, "工作流", -1, 9, null, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * Flink
     */
    FLINK_MR(11, "Flink", EJobType.MR.getType(), 11, EComponentType.FLINK, EComputeType.STREAM, EJobClientType.ENGINE_PLUGIN),

    /**
     * Python
     */
    PYTHON(12, "Python", EJobType.PYTHON.getType(), 12, EComponentType.SCRIPT, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * Shell
     */
    SHELL(13, "Shell", EJobType.PYTHON.getType(), 13, EComponentType.SCRIPT, EComputeType.BATCH, EJobClientType.ENGINE_PLUGIN),

    /**
     * TiDBSQL
     */
    TIDB_SQL(14, "TiDBSQL", EJobType.SQL.getType(), 4, EComponentType.TIDB, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * OracleSQL
     */
    ORACLE_SQL(15, "OracleSQL", EJobType.SQL.getType(), 4, EComponentType.ORACLE, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * GreenplumSQL
     */
    GREENPLUM_SQL(16, "GreenplumSQL", EJobType.SQL.getType(), 4, EComponentType.GREENPLUM, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * InceptorSQL
     */
    INCEPTOR_SQL(18, "InceptorSQL", EJobType.SQL.getType(), 4, EComponentType.INCEPTOR, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * MySQL
     */
    MYSQL(19, "MySQL", EJobType.SQL.getType(), 4, EComponentType.MYSQL, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * SQLServerSQL
     */
    SQL_SERVER_SQL(20, "SQLServerSQL", EJobType.SQL.getType(), 4, EComponentType.SQLSERVER, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * DB2SQL
     */
    DB2_SQL(21, "DB2SQL", EJobType.SQL.getType(), 4, EComponentType.DB2, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * TrinoSQL
     */
    TRINO_SQL(22, "TrinoSQL", EJobType.SQL.getType(), 4, EComponentType.TRINO, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * HanaSQL
     */
    HANA_SQL(23, "HanaSQL", EJobType.SQL.getType(), 4, EComponentType.HANA, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * GaussDBSQL
     */
    GaussDB_SQL(24, "GaussDBSQL", EJobType.SQL.getType(), 4, EComponentType.GaussDB, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * ImpalaSQL
     */
    IMPALA_SQL(25, "ImpalaSQL", EJobType.SQL.getType(), 4, EComponentType.IMPALA, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * ClickHouseSQL
     */
    CLICK_HOUSE_SQL(26, "ClickHouseSQL", EJobType.SQL.getType(), 14, EComponentType.CLICK_HOUSE, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN),

    /**
     * DorisSQL
     */
    DORIS_SQL(27, "DorisSQL", EJobType.SQL.getType(), 15, EComponentType.DORIS, EComputeType.BATCH, EJobClientType.DATASOURCE_PLUGIN);

    private final Integer type;

    private final String name;

    /**
     * 引擎能够接受的jobType
     * SQL              0
     * MR               1
     * SYNC             2
     * PYTHON           3
     * 不接受的任务类型    -1
     */
    private final Integer engineJobType;

    private final Integer sort;

    private final EComponentType componentType;

    /**
     * 任务所属类型
     */
    private final EComputeType computeType;

    /**
     * job client 类型 {@link EJobClientType}
     */
    private final EJobClientType jobClientType;

    public static final List<Integer> STREAM_JOB_TYPES = new ArrayList<>();
    public static final List<Integer> BATCH_JOB_TYPES = new ArrayList<>();

    static {
        for (EScheduleJobType value : EScheduleJobType.values()) {
            if (EComputeType.STREAM == value.getComputeType()) {
                STREAM_JOB_TYPES.add(value.getValue());
            }
            if (EComputeType.BATCH == value.getComputeType()) {
                BATCH_JOB_TYPES.add(value.getValue());
            }
        }
    }

    EScheduleJobType(Integer type, String name, Integer engineJobType, Integer sort, EComponentType componentType, EComputeType computeType, EJobClientType jobClientType) {
        this.type = type;
        this.name = name;
        this.engineJobType = engineJobType;
        this.sort = sort;
        this.componentType = componentType;
        this.computeType = computeType;
        this.jobClientType = jobClientType;
    }

    public static EScheduleJobType getByTaskType(int type) {
        EScheduleJobType[] eJobTypes = EScheduleJobType.values();
        for (EScheduleJobType eJobType : eJobTypes) {
            if (eJobType.type == type) {
                return eJobType;
            }
        }
        throw new RdosDefineException("不支持的任务类型");
    }

    public Integer getValue() {
        return type;
    }

    public Integer getVal() {
        return type;
    }

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Integer getEngineJobType() {
        return engineJobType;
    }

    public Integer getSort() {
        return sort;
    }

    public EComputeType getComputeType() {
        return computeType;
    }

    public EComponentType getComponentType() {
        return componentType;
    }

    public EJobClientType getJobClientType() {
        return jobClientType;
    }
}