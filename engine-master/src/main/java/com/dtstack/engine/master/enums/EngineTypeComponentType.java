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

import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.EngineType;

public enum EngineTypeComponentType {

    FLINK(EngineType.Flink, EComponentType.FLINK),
    SPARK(EngineType.Spark, EComponentType.SPARK),
    LEARNING(EngineType.Learning, EComponentType.LEARNING),
    DT_SCRIPT(EngineType.DtScript, EComponentType.DT_SCRIPT),
    HDFS(EngineType.Hadoop, EComponentType.HDFS),
    CARBON_DATA(EngineType.Carbon, EComponentType.CARBON_DATA),
    LIBRA_SQL(EngineType.Libra, EComponentType.LIBRA_SQL),
    HIVE(EngineType.HIVE,EComponentType.HIVE_SERVER),
    IMPALA_SQL(EngineType.Hadoop, EComponentType.IMPALA_SQL)
    ;

    private EngineType engineType;

    private EComponentType componentType;

    EngineTypeComponentType(EngineType engineType, EComponentType componentType) {
        this.engineType = engineType;
        this.componentType = componentType;
    }

    public EngineType getEngineType() {
        return engineType;
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
                return EngineTypeComponentType.LIBRA_SQL;
            case "hive":
                return EngineTypeComponentType.HIVE;
            case "mysql":
                return null;
            case "oracle":
                return null;
            case "maxcompute":
                return null;
            case"sqlserver":
                return null;
            case "impala":
                return EngineTypeComponentType.IMPALA_SQL;

            default:
                throw new UnsupportedOperationException("未知引擎类型:" + engineName);
        }
    }
}

