/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.enums;


import com.dtstack.engine.api.enums.ScheduleEngineType;

public enum EngineTypeComponentType {

    FLINK(ScheduleEngineType.Flink, EComponentType.FLINK),
    SPARK(ScheduleEngineType.Spark, EComponentType.SPARK),
    LEARNING(ScheduleEngineType.Learning, EComponentType.LEARNING),
    DT_SCRIPT(ScheduleEngineType.DtScript, EComponentType.DT_SCRIPT),
    HDFS(ScheduleEngineType.Hadoop, EComponentType.HDFS),
    CARBON_DATA(ScheduleEngineType.Carbon, EComponentType.CARBON_DATA),
    LIBRA_SQL(ScheduleEngineType.Libra, EComponentType.LIBRA_SQL),
    HIVE(ScheduleEngineType.HIVE,EComponentType.HIVE_SERVER),
    IMPALA_SQL(ScheduleEngineType.Hadoop, EComponentType.IMPALA_SQL),
    TIDB_SQL(ScheduleEngineType.TIDB, EComponentType.TIDB_SQL),
    ORACLE_SQL(ScheduleEngineType.ORACLE, EComponentType.ORACLE_SQL),
    KUBERNETES(ScheduleEngineType.KUBERNETES, EComponentType.KUBERNETES),
    GREENPLUM_SQL(ScheduleEngineType.GREENPLUM, EComponentType.GREENPLUM_SQL),
    PRESTO_SQL(ScheduleEngineType.Presto, EComponentType.PRESTO_SQL);

    private ScheduleEngineType scheduleEngineType;

    private EComponentType componentType;

    EngineTypeComponentType(ScheduleEngineType scheduleEngineType, EComponentType componentType) {
        this.scheduleEngineType = scheduleEngineType;
        this.componentType = componentType;
    }

    public ScheduleEngineType getScheduleEngineType() {
        return scheduleEngineType;
    }

    public EComponentType getComponentType() {
        return componentType;
    }

    public static EngineTypeComponentType getByEngineName(String engineName){
        switch (engineName.toLowerCase()) {

            case "flink":
                return EngineTypeComponentType.FLINK;

            case "spark":
                return EngineTypeComponentType.SPARK;

            case "learning":
                return EngineTypeComponentType.LEARNING;

            case "dtscript":
                return EngineTypeComponentType.DT_SCRIPT;

            case "hadoop":
                return EngineTypeComponentType.HDFS;

            case "carbon":
                return EngineTypeComponentType.CARBON_DATA;

            case "librasql":
            case "postgresql":
                return EngineTypeComponentType.LIBRA_SQL;
            case "hive":
                return EngineTypeComponentType.HIVE;
            case "mysql":
            case "maxcompute":
            case "sqlserver":
            case "kylin":
            case "dummy":
                return null;
            case "impala":
                return EngineTypeComponentType.IMPALA_SQL;
            case "tidb":
                return EngineTypeComponentType.TIDB_SQL;
            case "oracle":
                return EngineTypeComponentType.ORACLE_SQL;
            case "kubernetes":
                return EngineTypeComponentType.KUBERNETES;
            case "greenplum":
                return EngineTypeComponentType.GREENPLUM_SQL;
            case "presto":
                return EngineTypeComponentType.PRESTO_SQL;
            default:
                throw new UnsupportedOperationException("未知引擎类型:" + engineName);
        }
    }
}

