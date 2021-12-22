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

package com.dtstack.batch.enums;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.engine.common.enums.MultiEngineType;

/**
 * @author chener
 * @Classname TableEngineType
 * @Description TODO
 * @Date 2020/7/16 11:29
 * @Created chener@dtstack.com
 */
public enum TableEngineType {
    /**
     * hive
     */
    HIVE(ETableType.HIVE, MultiEngineType.HADOOP),

    /**
     * Libra
     */
    LIBRA(ETableType.LIBRA, MultiEngineType.LIBRA),

    /**
     * TiDB
     */
    TIDB(ETableType.TIDB, MultiEngineType.TIDB),

    /**
     * Oracle
     */
    ORACLE(ETableType.ORACLE, MultiEngineType.ORACLE),

    /**
     * Greenplum
     */
    GREENPLUM(ETableType.GREENPLUM, MultiEngineType.GREENPLUM),

    /**
     * IMPALA
     */
    IMPALA(ETableType.IMPALA, MultiEngineType.HADOOP);

    private ETableType tableType;
    private MultiEngineType engineType;

    public ETableType getTableType() {
        return tableType;
    }

    public MultiEngineType getEngineType() {
        return engineType;
    }

    TableEngineType(ETableType tableType, MultiEngineType engineType) {
        this.tableType = tableType;
        this.engineType = engineType;
    }

    public static MultiEngineType getEngineTypeByTableType(ETableType tableType){
        for (TableEngineType type:values()){
            if (type.getTableType().equals(tableType)){
                return type.getEngineType();
            }
        }
        return null;
    }

    public static MultiEngineType getEngineTypeByTableTypeInt(Integer tableType){
        for (TableEngineType type:values()){
            if (type.getTableType().getType() == tableType){
                return type.getEngineType();
            }
        }
        return null;
    }
}
