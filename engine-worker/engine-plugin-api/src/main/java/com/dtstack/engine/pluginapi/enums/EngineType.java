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

package com.dtstack.engine.pluginapi.enums;

import com.dtstack.engine.pluginapi.constrant.ComponentConstant;

/**
 * Company: www.dtstack.com
 * @author toutian
 */

public enum EngineType {
    Flink,
    Spark,
    Datax,
    Learning,
    DtScript,
    Mysql,
    Oracle,
    Sqlserver,
    Maxcompute,
    Hadoop,
    Hive,
    PostgreSQL,
    Kylin,
    Impala,
    TiDB,
    GreenPlum,
    Dummy,
    Presto,
    KingBase,
    InceptorSQL,
    DtScriptAgent,
    FlinkOnStandalone,
    AnalyticdbForPg;

    public static EngineType getEngineType(String type) {

        switch (type.toLowerCase()) {
            case "flink":
                return EngineType.Flink;
            case "spark":
                return EngineType.Spark;
            case "datax":
                return EngineType.Datax;
            case "learning":
                return EngineType.Learning;
            case "dtscript":
                return EngineType.DtScript;
            case "mysql":
                return EngineType.Mysql;
            case "tidb":
                return EngineType.TiDB;
            case "oracle":
                return EngineType.Oracle;
            case "sqlserver":
                return EngineType.Sqlserver;
            case "maxcompute":
                return EngineType.Maxcompute;
            case "hadoop":
                return EngineType.Hadoop;
            case "hive":
                return EngineType.Hive;
            case "postgresql":
                return EngineType.PostgreSQL;
            case "kylin":
                return EngineType.Kylin;
            case "impala":
                return EngineType.Impala;
            case "greenplum":
                return EngineType.GreenPlum;
            case "dummy":
                return EngineType.Dummy;
            case "presto":
                return EngineType.Presto;
            case "kingbase":
                return EngineType.KingBase;
            case "inceptor":
                return EngineType.InceptorSQL;
            case "dtscript-agent":
                return EngineType.DtScriptAgent;
            case ComponentConstant
                        .ANALYTICDB_FOR_PG_PLUGIN:
                return EngineType.AnalyticdbForPg;
            case "flink-on-standalone":
                return EngineType.FlinkOnStandalone;
            default:
                throw new UnsupportedOperationException("unsupported operation exception");
        }
    }

    public static boolean isFlink(String engineType) {
        engineType = engineType.toLowerCase();
        if (engineType.startsWith("flink")) {
            return true;
        }

        return false;
    }

}
