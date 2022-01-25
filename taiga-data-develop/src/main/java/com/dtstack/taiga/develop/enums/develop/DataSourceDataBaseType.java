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

package com.dtstack.taiga.develop.enums.develop;

import com.dtstack.dtcenter.loader.source.DataBaseType;
import com.dtstack.dtcenter.loader.source.DataSourceType;

import java.util.Objects;

/**
 * @author chener
 * @Classname DataSourceDataBaseType
 * @Description datasourceType和databaseBaseType复合枚举
 * @Date 2020/8/5 21:15
 * @Created chener@dtstack.com
 */
public enum DataSourceDataBaseType {
    /**
     * MySql
     */
    MySql(DataSourceType.MySQL, DataBaseType.MySql),
    /**
     * Oracle
     */
    Oracle(DataSourceType.Oracle, DataBaseType.Oracle),
    /**
     * SQLServer
     */
    SQLServer(DataSourceType.SQLServer, DataBaseType.SQLServer),
    /**
     * SQLSSERVER_2017_LATER
     */
    SQLSSERVER_2017_LATER(DataSourceType.SQLSERVER_2017_LATER, DataBaseType.SQLSSERVER_2017_LATER),
    /**
     * PostgreSQL
     */
    PostgreSQL(DataSourceType.PostgreSQL, DataBaseType.PostgreSQL),
    /**
     * RDBMS
     */
    RDBMS(DataSourceType.RDBMS, DataBaseType.RDBMS),
    /**
     * DB2
     */
    DB2(DataSourceType.DB2, DataBaseType.DB2),
    /**
     * HIVE
     */
    HIVE(DataSourceType.HIVE, DataBaseType.HIVE),
    /**
     * CarbonData
     */
    CarbonData(DataSourceType.CarbonData, DataBaseType.CarbonData),
    /**
     * ADS
     */
    ADS(DataSourceType.ADS, DataBaseType.ADS),
    /**
     * MaxCompute
     */
    MaxCompute(DataSourceType.MAXCOMPUTE, DataBaseType.MaxCompute),
    /**
     * LIBRA
     */
    LIBRA(DataSourceType.LIBRA, DataBaseType.LIBRA),
    /**
     * GBase8a
     */
    GBase8a(DataSourceType.GBase_8a, DataBaseType.GBase8a),
    /**
     * Kudu
     */
    Kudu(DataSourceType.Kudu, DataBaseType.Kudu),
    /**
     * Spark
     */
    Spark(DataSourceType.SparkThrift2_1, DataBaseType.Spark),
    /**
     * Impala
     */
    Impala(DataSourceType.IMPALA, DataBaseType.Impala),
    /**
     * Inceptor
     */
    Inceptor(DataSourceType.INCEPTOR, DataBaseType.INCEPTOR),
    /**
     * Clickhouse
     */
    Clickhouse(DataSourceType.Clickhouse, DataBaseType.Clickhouse),
    /**
     * HIVE1X
     */
    HIVE1X(DataSourceType.HIVE1X, DataBaseType.HIVE1X),
    /**
     * HIVE3X
     */
    HIVE3X(DataSourceType.HIVE3X, DataBaseType.HIVE3),
    /**
     * Polardb_For_MySQL
     */
    Polardb_For_MySQL(DataSourceType.Polardb_For_MySQL, DataBaseType.Polardb_For_MySQL),
    /**
     * Phoenix
     */
    Phoenix(DataSourceType.Phoenix, DataBaseType.Phoenix),
    /**
     * TiDB
     */
    TiDB(DataSourceType.TiDB, DataBaseType.TiDB),
    /**
     * MySql8
     */
    MySql8(DataSourceType.MySQL8, DataBaseType.MySql8),
    /**
     * DMDB
     */
    DMDB(DataSourceType.DMDB, DataBaseType.DMDB),
    /**
     * Greenplum6
     */
    Greenplum6(DataSourceType.GREENPLUM6, DataBaseType.Greenplum6),
    /**
     * Phoenix5
     */
    Phoenix5(DataSourceType.PHOENIX5, DataBaseType.Phoenix5),
    /**
     * KINGBASE8
     */
    KINGBASE8(DataSourceType.KINGBASE8, DataBaseType.KINGBASE8),
    /**
     * AnalyticDB PostgreSQL
     */
    ADB_FOR_PG(DataSourceType.ADB_FOR_PG, DataBaseType.ADB_FOR_PG);

    private DataSourceType dataSourceType;

    private DataBaseType dataBaseType;

    DataSourceDataBaseType(DataSourceType sourceType, DataBaseType baseType) {
        this.dataSourceType = sourceType;
        this.dataBaseType = baseType;
    }

    public DataSourceType getDataSourceType() {
        return dataSourceType;
    }

    public DataBaseType getDataBaseType() {
        return dataBaseType;
    }

    public static DataSourceDataBaseType getBySourceType(DataSourceType sourceType){
        for (DataSourceDataBaseType sbType:values()){
            if (sbType.getDataSourceType().equals(sourceType)){
                return sbType;
            }
        }
        return null;
    }

    public static DataSourceDataBaseType getBySourceType(Integer sourceType){
        for (DataSourceDataBaseType sbType:values()){
            if (sbType.getDataSourceType().getVal() == sourceType){
                return sbType;
            }
        }
        return null;
    }

    public static DataSourceDataBaseType getByBaseType(DataBaseType baseType){
        for (DataSourceDataBaseType sbType:values()){
            if (sbType.getDataBaseType().equals(baseType)){
                return sbType;
            }
        }
        return null;
    }

    public static DataSourceType getSourceTypeByBaseType(DataBaseType baseType){
        DataSourceDataBaseType byBaseType = getByBaseType(baseType);
        if (Objects.isNull(byBaseType)){
            return null;
        }
        return byBaseType.getDataSourceType();
    }

    public static DataBaseType getBaseTypeBySourceType(DataSourceType sourceType){
        DataSourceDataBaseType bySourceType = getBySourceType(sourceType);
        if (Objects.isNull(bySourceType)){
            return null;
        }
        return bySourceType.getDataBaseType();
    }

    public static DataBaseType getBaseTypeBySourceType(Integer sourceType){
        DataSourceDataBaseType bySourceType = getBySourceType(sourceType);
        if (Objects.isNull(bySourceType)){
            return null;
        }
        return bySourceType.getDataBaseType();
    }
}
