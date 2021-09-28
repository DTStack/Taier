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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:30 下午 2020/11/25
 */
public enum  DataSourceTypeEnum {

    SPARK_THRIFT(6, "SparkThrift", "hiveConf"),
    LIBRA_SQL(8, "LibrA SQL", "libraConf"),
    IMPALA_SQL(11, "Impala SQL", "impalaSqlConf"),
    TIDB_SQL(12, "TiDB SQL", "tidbConf"),
    ORACLE_SQL(13, "Oracle SQL", "oracleConf"),
    GREENPLUM_SQL(14, "Greenplum SQL", "greenplumConf");

    private int typeCode;

    private String name;

    private String confName;

    DataSourceTypeEnum(int typeCode, String name, String confName) {
        this.typeCode = typeCode;
        this.name = name;
        this.confName = confName;
    }

    public static DataSourceTypeEnum getByCode(int code) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getTypeCode() == code) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with type code:" + code);
    }

    public static DataSourceTypeEnum getByName(String name) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with name:" + name);
    }

    public static DataSourceTypeEnum getByConfName(String ConfName) {
        for (DataSourceTypeEnum value : DataSourceTypeEnum.values()) {
            if (value.getConfName().equalsIgnoreCase(ConfName)) {
                return value;
            }
        }

        throw new IllegalArgumentException("No enum constant with conf name:" + ConfName);
    }

    public static List<Integer> getAllTypeCodes(){
        DataSourceTypeEnum[] values = DataSourceTypeEnum.values();
        return Arrays.stream(values).map(DataSourceTypeEnum::getTypeCode).collect(Collectors.toList());
    }

    public int getTypeCode() {
        return typeCode;
    }

    public String getName() {
        return name;
    }

    public String getConfName() {
        return confName;
    }
}
