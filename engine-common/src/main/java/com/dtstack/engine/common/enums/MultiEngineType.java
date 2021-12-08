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

package com.dtstack.engine.common.enums;

import com.dtstack.engine.pluginapi.constrant.ComponentConstant;

/**
 *
 * 引擎类型
 * Date: 2019/5/28
 * Company: www.dtstack.com
 * @author xuchao
 */

public enum MultiEngineType {
    COMMON(-1,"Common"),
    HADOOP(1,"Hadoop"),
    LIBRA(2, "LibrA"),
    KYLIN(3, "Kylin"),
    TIDB(4,"TiDB"),
    ORACLE(5,"Oracle"),
    GREENPLUM(6, "Greenplum"),
    PRESTO(7, "Presto"),
    FLINK_ON_STANDALONE(8,"FlinkOnStandalone"),
    ANALYTICDB_FOR_PG(9, ComponentConstant.ANALYTICDB_FOR_PG_ENGINE),
    MYSQL(10, "Mysql"),
    SQL_SERVER(11, "SqlServer"),
    DB2(12, "DB2"),
    OCEANBASE(13, "OceanBase");

    private int type;

    private String name;

    public String getName() {
        return name;
    }

    MultiEngineType(int type, String name){
        this.type = type;
        this.name = name;
    }

    public int getType(){
        return this.type;
    }

    public static MultiEngineType getByName(String name){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getName().equalsIgnoreCase(name)){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + name);
    }

    public static MultiEngineType getByType(int type){
        for (MultiEngineType value : MultiEngineType.values()) {
            if(value.getType() == type){
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + type);
    }
}
