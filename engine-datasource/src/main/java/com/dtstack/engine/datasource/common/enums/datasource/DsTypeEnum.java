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

package com.dtstack.engine.datasource.common.enums.datasource;

/**
 * 数据源类型枚举类
 * @description:
 * @author: liuxx
 * @date: 2021/3/9
 */
public enum DsTypeEnum {
    MySQL(3L, "MySQL", 0.5),
    PolarDB(3L, "PolarDB for MySQL8", 0.0),
    Oracle(3L, "Oracle", 0.5),
    SQLServer(3L, "SQLServer", 0.0),
    PostgreSQL(3L, "PostgreSQL", 0.0),
    DB2(3L, "DB2", 0.0),
    DMDB(3L, "DMDB", 0.0),
    KINGBASE8(3L, "KingbaseES8", 0.0),
    HIVE(4L, "Hive", 0.5),
    SparkThrift(4L, "SparkThrift", 0.0),
    MaxCompute(4L, "Maxcompute", 0.0),
    Phoenix(4L, "Phoenix", 0.0),
    GREENPLUM6(5L, "Greenplum", 0.0),
    LIBRA(5L, "LibrA", 0.0),
    GBase_8a(5L, "GBase_8a", 0.0),
    HDFS(6L, "HDFS", 0.0),
    FTP(6L, "FTP", 0.0),
    S3(6L, "S3", 0.0),
    IMPALA(7L, "Impala", 0.0),
    ClickHouse(7L, "ClickHouse", 0.0),
    TiDB(7L, "TiDB", 0.0),
    Kudu(7L, "Kudu", 0.0),
    ADS(7L, "AnalyticDB", 0.0),
    CarbonData(7L, "CarbonData", 0.0),
    Kylin(7L, "Kylin", 0.0),
    HBASE(8L, "HBase", 0.0),
    ES(8L, "Elasticsearch", 0.0),
    MONGODB(8L, "MongoDB", 0.0),
    REDIS(8L, "Redis", 0.0),
    KAFKA(9L, "Kafka", 0.5),
    EMQ(9L, "EMQ", 0.0),
    WEB_SOCKET(10L, "WebSocket", 0.0),
    SOCKET(10L, "Socket", 0.0),
    Presto(1L, "Presto", 0.0)
    ;


    DsTypeEnum(Long classifyId, String dataType, Double weight) {
        this.classifyId = classifyId;
        this.dataType = dataType;
        this.weight = weight;
    }

    private Long classifyId;

    private String dataType;

    private Double weight;

    public Long getClassifyId() {
        return classifyId;
    }

    public void setClassifyId(Long classifyId) {
        this.classifyId = classifyId;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
