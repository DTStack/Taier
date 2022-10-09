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

package com.dtstack.taier.develop.enums.develop;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author chener
 * @Classname RDBMSSourceType
 * @Description TODO
 * @Date 2020/8/5 18:57
 * @Created chener@dtstack
 */
public enum RDBMSSourceType {
    /**
     * MySQL
     */
    MySQL(DataSourceType.MySQL),
    /**
     * MySQL8
     */
    MySQL8(DataSourceType.MySQL8),
    /**
     * Oracle
     */
    Oracle(DataSourceType.Oracle),
    /**
     * SQLServer
     */
    SQLServer(DataSourceType.SQLServer),
    /**
     * SQLSERVER_2017_LATER
     */
    SQLSERVER_2017_LATER(DataSourceType.SQLSERVER_2017_LATER),
    /**
     * PostgreSQL
     */
    PostgreSQL(DataSourceType.PostgreSQL),
    /**
     * HIVE
     */
    HIVE(DataSourceType.HIVE),
    /**
     * hive3
     */
    HIVE3X(DataSourceType.HIVE3X),
    /**
     * HIVE1X
     */
    HIVE1X(DataSourceType.HIVE1X),
    /**
     * DB2
     */
    DB2(DataSourceType.DB2),
    /**
     * ADS
     */
    ADS(DataSourceType.ADS),
    /**
     * CarbonData
     */
    CarbonData(DataSourceType.CarbonData),
    /**
     * RDBMS
     */
    RDBMS(DataSourceType.RDBMS),
    /**
     * GBase_8a
     */
    GBase_8a(DataSourceType.GBase_8a),
    /**
     * LIBRA
     */
    LIBRA(DataSourceType.LIBRA),
    /**
     * Clickhouse
     */
    Clickhouse(DataSourceType.Clickhouse),
    /**
     * Polardb_For_MySQL
     */
    Polardb_For_MySQL(DataSourceType.Polardb_For_MySQL),
    /**
     * IMPALA
     */
    IMPALA(DataSourceType.IMPALA),
    /**
     * Phoenix
     */
    Phoenix(DataSourceType.Phoenix),
    /**
     * TiDB
     */
    TiDB(DataSourceType.TiDB),
    /**
     * DMDB
     */
    DMDB(DataSourceType.DMDB),
    /**
     * GREENPLUM6
     */
    GREENPLUM6(DataSourceType.GREENPLUM6),
    /**
     * PHOENIX5
     */
    PHOENIX5(DataSourceType.PHOENIX5),
    /**
     * KINGBASE8
     */
    KINGBASE8(DataSourceType.KINGBASE8),
    /**
     * SPARK
     */
    SPARK(DataSourceType.SparkThrift2_1),

    /**
     * INCEPTOR
     */
    INCEPTOR(DataSourceType.INCEPTOR),

    /**
     * ADB_FOR_PG
     */
    ADB_FOR_PG(DataSourceType.ADB_FOR_PG);

    private DataSourceType dataSourceType;

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    RDBMSSourceType(DataSourceType sourceType) {
        this.dataSourceType = sourceType;
    }

    public static RDBMSSourceType getByDataSourceType(DataSourceType sourceType){
        for (RDBMSSourceType dataSourceType:values()){
            if (dataSourceType.getDataSourceType().equals(sourceType)){
                return dataSourceType;
            }
        }
        return null;
    }

    public static RDBMSSourceType getByDataSourceType(Integer sourceType){
        for (RDBMSSourceType dataSourceType:values()){
            if (dataSourceType.getDataSourceType().getVal().equals(sourceType)){
                return dataSourceType;
            }
        }
        return null;
    }

    public static Set<Integer> getRDBMS(){
        Set<Integer> set = Sets.newHashSet();
        for (RDBMSSourceType sourceType:values()){
            set.add(sourceType.getDataSourceType().getVal());
        }
        return set;
    }
}
