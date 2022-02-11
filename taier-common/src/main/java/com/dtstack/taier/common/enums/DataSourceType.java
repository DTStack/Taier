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
import com.google.common.collect.Lists;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/10
 * 值 1000 以上表示未启用，后续标号
 */
public enum DataSourceType {
    MySQL(1),
    MySQL8(1001),
    MySQLPXC(98),
    Polardb_For_MySQL(28),
    Oracle(2),
    SQLServer(3),
    SQLSERVER_2017_LATER(32),
    PostgreSQL(4),
    DB2(19),
    DMDB(35),
    RDBMS(5),
    KINGBASE8(40),
    HIVE(7),
    HIVE1X(27),
    HIVE3(50),
    MAXCOMPUTE(10),
    GREENPLUM6(36),
    LIBRA(21),
    GBase_8a(22),
    DORIS(57),
    HDFS(6),
    FTP(9),
    S3(41),
    AWS_S3(51),
    SPARKTHRIFT2_1(45),
    IMPALA(29),
    Clickhouse(25),
    TiDB(31),
    CarbonData(20),
    Kudu(24),
    ADS(15),
    ADB_POSTGREPSQL(54),
    Kylin(23),
    Presto(48),
    OceanBase(49),
    INCEPTOR_SQL(52),
    HBASE(8),
    HBASE2(39),
    Phoenix(30),
    PHOENIX5(38),
    ES(11),
    ES6(33),
    ES7(46),
    MONGODB(13),
    REDIS(12),
    SOLR(53),
    HBASE_GATEWAY(99),
    KAFKA_2X(37),
    KAFKA(26),
    KAFKA_11(14),
    KAFKA_10(17),
    KAFKA_09(18),
    EMQ(34),
    WEB_SOCKET(42),
    SOCKET(44),
    RESTFUL(47),
    VERTICA(43),
    INFLUXDB(55),
    OPENTSDB(56),
    BEATS(16),
    /**
     * spark thrift
     */
    Spark(1002),
    KylinRestful(58),

    /**
     * 因为数据资产新增自定义数据集，将该类型与之做对应
     */
    CUSTOM(1000),
    /**
     * 未知数据源，即类型暂时不确定，后续可能会修改为正确类型的数据源
     */
    UNKNOWN(3000);

    private int val;

    DataSourceType(int val) {
        this.val = val;
    }

    public int getVal() {
        return val;
    }


    public static DataSourceType getSourceType(int value) {
        for (DataSourceType type : DataSourceType.values()) {
            if (type.val == value) {
                return type;
            }
        }

        throw new RdosDefineException("不支持数据源类型");
    }



    public static List<Integer> hadoopDirtyDataSource = Lists.newArrayList(
            DataSourceType.HIVE1X.getVal(),
            DataSourceType.HIVE.getVal(),
            DataSourceType.HIVE3.getVal(),
            DataSourceType.SPARKTHRIFT2_1.getVal());

}
