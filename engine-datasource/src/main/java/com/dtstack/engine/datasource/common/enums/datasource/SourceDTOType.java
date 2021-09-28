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

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.enums.RedisMode;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.datasource.common.constant.FormNames;
import com.dtstack.engine.datasource.common.constant.RegexMatch;
import com.dtstack.engine.datasource.common.exception.ErrorCode;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import com.dtstack.engine.datasource.common.utils.CommonUtils;
import com.dtstack.engine.datasource.common.utils.DataSourceUtils;
import com.dtstack.engine.datasource.common.utils.datakit.Asserts;
import dt.insight.plat.lang.base.Strings;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * 获取数据源对应的sourceDTO
 * @description:
 * @author: liuxx
 * @date: 2021/3/17
 */
public enum SourceDTOType {

    /**
     * mysql
     */
    MySQL(DataSourceType.MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMysql5(dataJson, confMap, schema);
        }
    },
    /**
     * mysql8
     */
    MySQL8(DataSourceType.MySQL8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMysql8(dataJson, confMap, schema);
        }
    },
    /**
     * Polardb_For_MySQL
     */
    Polardb_For_MySQL(DataSourceType.Polardb_For_MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMysql5(dataJson, confMap, schema);
        }
    },
    /**
     * oracle
     */
    Oracle(DataSourceType.Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildOracle(dataJson, confMap, schema);
        }
    },
    /**
     * sqlserver
     */
    SQLServer(DataSourceType.SQLServer.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.SQLServer.getVal());
        }
    },
    /**
     * sqlserver 2017
     */
    SQLSERVER_2017_LATER(DataSourceType.SQLSERVER_2017_LATER.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.SQLSERVER_2017_LATER.getVal());
        }
    },
    /**
     * postgresql
     */
    PostgreSQL(DataSourceType.PostgreSQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceWithSchema(dataJson, confMap, schema, DataSourceType.PostgreSQL.getVal());
        }
    },
    /**
     * DB2数据源
     */
    DB2(DataSourceType.DB2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceWithSchema(dataJson, confMap, schema, DataSourceType.DB2.getVal());
        }
    },
    /**
     * 达梦数据库
     */
    DMDB(DataSourceType.DMDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.DMDB.getVal());
        }
    },
    /**
     * RDBMS
     */
    RDBMS(DataSourceType.RDBMS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.RDBMS.getVal());
        }
    },
    /**
     * kingbaseES
     */
    Kingbase(DataSourceType.KINGBASE8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceWithSchema(dataJson, confMap, schema, DataSourceType.KINGBASE8.getVal());
        }
    },
    HIVE1X(DataSourceType.HIVE1X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHive1SourceDTO(dataJson, confMap, schema, DataSourceType.HIVE1X.getVal());
        }
    },
    HIVE2X(DataSourceType.HIVE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHiveSourceDTO(dataJson, confMap, schema, DataSourceType.HIVE.getVal());
        }
    },
    INCEPTOR(DataSourceType.INCEPTOR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildInceptorSourceDTO(dataJson, confMap, schema, DataSourceType.INCEPTOR.getVal());
        }
    },
    HIVE3X(DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHive3SourceDTO(dataJson, confMap, schema, DataSourceType.HIVE3X.getVal());
        }
    },
    SparkThrift2_1(DataSourceType.SparkThrift2_1.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildSparkSourceDTO(dataJson, confMap, schema, DataSourceType.SparkThrift2_1.getVal());
        }
    },
    MAXCOMPUTE(DataSourceType.MAXCOMPUTE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildOdpsSourceDTO(dataJson, confMap, schema, DataSourceType.MAXCOMPUTE.getVal());
        }
    },
    GREENPLUM6(DataSourceType.GREENPLUM6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceWithSchema(dataJson, confMap, schema, DataSourceType.GREENPLUM6.getVal());
        }
    },
    LIBRA(DataSourceType.LIBRA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceWithSchema(dataJson, confMap, schema, DataSourceType.LIBRA.getVal());
        }
    },
    GBase_8a(DataSourceType.GBase_8a.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.GBase_8a.getVal());
        }
    },
    HDFS(DataSourceType.HDFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHdfsSourceDTO(dataJson, confMap, schema, DataSourceType.HDFS.getVal());
        }
    },
    FTP(DataSourceType.FTP.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildFtpSourceDTO(dataJson, confMap, schema, DataSourceType.FTP.getVal());
        }
    },
    IMPALA(DataSourceType.IMPALA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildImpalaSourceDTO(dataJson, confMap, schema, DataSourceType.IMPALA.getVal());
        }
    },
    ClickHouse(DataSourceType.Clickhouse.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.Clickhouse.getVal());
        }
    },
    TiDB(DataSourceType.TiDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMysql5(dataJson, confMap, schema);
        }
    },
    CarbonData(DataSourceType.CarbonData.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHiveSourceDTO(dataJson, confMap, schema, DataSourceType.CarbonData.getVal());
        }
    },
    /**
     * Kudu
     */
    Kudu(DataSourceType.Kudu.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKudoDTO(dataJson, confMap, schema);
        }
    },
    /**
     * Kylin
     */
    Kylin(DataSourceType.KylinRestful.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKylinUrlDTO(dataJson, confMap, schema);
        }
    },
    /**
     * HBASE
     */
    HBASE(DataSourceType.HBASE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHbaseSourceDTO(dataJson, confMap, schema, DataSourceType.HBASE.getVal());
        }
    },
    /**
     * HBASE2
     */
    HBASE2(DataSourceType.HBASE2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildHbaseSourceDTO(dataJson, confMap, schema, DataSourceType.HBASE2.getVal());
        }
    },
    /**
     * Phoenix
     */
    Phoenix(DataSourceType.Phoenix.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.Phoenix.getVal());
        }
    },
    /**
     * PHOENIX5
     */
    PHOENIX5(DataSourceType.PHOENIX5.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson, confMap, schema, DataSourceType.PHOENIX5.getVal());
        }
    },
    /**
     * ES
     */
    ES(DataSourceType.ES.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildEsSourceDTO(dataJson, confMap, schema, DataSourceType.ES.getVal());
        }
    },
    /**
     * Solr
     */
    SOLR(DataSourceType.SOLR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildSolrSourceDTO(dataJson, confMap, schema, DataSourceType.SOLR.getVal());
        }
    },
    /**
     * ES6
     */
    ES6(DataSourceType.ES6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildEsSourceDTO(dataJson, confMap, schema, DataSourceType.ES6.getVal());
        }
    },
    /**
     * ES7
     */
    ES7(DataSourceType.ES7.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildEsSourceDTO(dataJson, confMap, schema, DataSourceType.ES7.getVal());
        }
    },
    /**
     * MONGODB
     */
    MONGODB(DataSourceType.MONGODB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMongoSourceDTO(dataJson, confMap, schema, DataSourceType.MONGODB.getVal());
        }
    },
    /**
     * REDIS
     */
    REDIS(DataSourceType.REDIS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRedisSourceDTO(dataJson, confMap, schema, DataSourceType.REDIS.getVal());
        }
    },
    /**
     * S3
     */
    S3(DataSourceType.S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildS3SourceDTO(dataJson, confMap, schema, DataSourceType.S3.getVal());
        }
    },
    /**
     * S3
     */
    AWS_S3(DataSourceType.AWS_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildAwsS3SourceDTO(dataJson, confMap, schema, DataSourceType.AWS_S3.getVal());
        }
    },
    /**
     * KAFKA_09
     */
    KAFKA_09(DataSourceType.KAFKA_09.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKafkaSourceDTO(dataJson, confMap, schema, DataSourceType.KAFKA_09.getVal());
        }
    },
    KAFKA_10(DataSourceType.KAFKA_10.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKafkaSourceDTO(dataJson, confMap, schema, DataSourceType.KAFKA_10.getVal());
        }
    },
    KAFKA_11(DataSourceType.KAFKA_11.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKafkaSourceDTO(dataJson, confMap, schema, DataSourceType.KAFKA_11.getVal());
        }
    },
    KAFKA(DataSourceType.KAFKA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKafkaSourceDTO(dataJson, confMap, schema, DataSourceType.KAFKA.getVal());
        }
    },
    KAFKA_2X(DataSourceType.KAFKA_2X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildKafkaSourceDTO(dataJson, confMap, schema, DataSourceType.KAFKA_2X.getVal());
        }
    },
    /**
     * EMQ
     */
    EMQ(DataSourceType.EMQ.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildEMQSourceDTO(dataJson, confMap, schema, DataSourceType.EMQ.getVal());
        }
    },
    /**
     * websocket
     */
    WEB_SOCKET(DataSourceType.WEB_SOCKET.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = CommonUtils.getStrFromJson(dataJson, FormNames.URL);
            return WebSocketSourceDTO
                    .builder()
                    .url(url)
                    .sourceType(DataSourceType.WEB_SOCKET.getVal())
                    .authParams(CommonUtils.getObjFromJson(dataJson, FormNames.WEB_SOCKET_PARAMS, Map.class))
                    .build();
        }
    },
    /**
     * socket
     */
    SOCKET(DataSourceType.SOCKET.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.getString("url");
            return SocketSourceDTO
                    .builder()
                    .hostPort(url)
                    .sourceType(DataSourceType.SOCKET.getVal())
                    .build();
        }
    },
    /**
     * vertica
     */
    VERTICA(DataSourceType.VERTICA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildVertica(dataJson, confMap, schema);
        }
    },
    /**
     * ads
     */
    ADS(DataSourceType.ADS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildMysql5(dataJson, confMap, schema);
        }
    },
    /**
     * Presto
     */
    Presto(DataSourceType.Presto.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildPrestoSourceDTO(dataJson, confMap, schema, DataSourceType.Presto.getVal());
        }
    },
    /**
     * InfluxDB
     */
    INFLUXDB(DataSourceType.INFLUXDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildInfluxDBSourceDTO(dataJson);
        }
    },
    /**
     * InfluxDB
     */
    OPENTSDB(DataSourceType.OPENTSDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildOpenTSDBSourceDTO(dataJson);
        }
    },


    DORISDB(DataSourceType.DORIS.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson,confMap,null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildDorisDBSourceDTO(dataJson,schema);
        }
    },
    KylinJdbc(DataSourceType.Kylin.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson,confMap,null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return buildRdbmsSourceDTO(dataJson,confMap,schema, DataSourceType.Kylin.getVal());
        }
    }

    ;

    SourceDTOType(Integer val) {
        this.val = val;
    }

    private Integer val;

    public Integer getVal() {
        return val;
    }

    public void setVal(Integer val) {
        this.val = val;
    }

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap);

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema);

    /**
     * 构造Mysql5DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public Mysql5SourceDTO buildMysql5(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        String jdbcUrl = dataJson.containsKey("jdbcUrl") ? dataJson.getString("jdbcUrl") : "";
        String username = dataJson.containsKey("username") ? dataJson.getString("username") : "";
        String password = dataJson.containsKey("password") ? dataJson.getString("password") : "";
        return Mysql5SourceDTO
                .builder()
                .url(jdbcUrl)
                .schema(schema)
                .username(username)
                .password(password)
                .sourceType(DataSourceType.MySQL.getVal())
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构造Mysql5DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public VerticaSourceDTO buildVertica(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        String jdbcUrl = dataJson.containsKey("jdbcUrl") ? dataJson.getString("jdbcUrl") : "";
        String username = dataJson.containsKey("username") ? dataJson.getString("username") : "";
        String password = dataJson.containsKey("password") ? dataJson.getString("password") : "";
        return VerticaSourceDTO
                .builder()
                .url(jdbcUrl)
                .schema(schema)
                .username(username)
                .password(password)
                .sourceType(DataSourceType.VERTICA.getVal())
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构造Mysql8DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public Mysql8SourceDTO buildMysql8(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        String jdbcUrl = dataJson.containsKey("jdbcUrl") ? dataJson.getString("jdbcUrl") : "";
        String username = dataJson.containsKey("username") ? dataJson.getString("username") : "";
        String password = dataJson.containsKey("password") ? dataJson.getString("password") : "";
        return Mysql8SourceDTO
                .builder()
                .url(jdbcUrl)
                .schema(schema)
                .username(username)
                .password(password)
                .sourceType(DataSourceType.MySQL8.getVal())
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构造OracleDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public OracleSourceDTO buildOracle(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        if (StringUtils.isBlank(schema)) {
            schema = dataJson.getString("schema");
        }
        String url = dataJson.containsKey("jdbcUrl") ? dataJson.getString("jdbcUrl") : "";
        String username = dataJson.containsKey("username") ? dataJson.getString("username") : "";
        String password = dataJson.containsKey("password") ? dataJson.getString("password") : "";
        return OracleSourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .sourceType(DataSourceType.Oracle.getVal())
                .schema(schema)
                .build();
    }

    /**
     * 构造KuduDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public KuduSourceDTO buildKudoDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        String hostPorts = CommonUtils.getStrFromJson(dataJson, FormNames.HOST_PORTS);
        if (StringUtils.isBlank(hostPorts)) {
            return KuduSourceDTO.builder().build();
        }
        return KuduSourceDTO
                .builder()
                .url(hostPorts)
                .schema(schema)
                .sourceType(DataSourceType.Kudu.getVal())
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构造KylinDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public KylinRestfulSourceDTO buildKylinUrlDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.AUTH_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String project = CommonUtils.getStrFromJson(dataJson, FormNames.PROJECT);
        return KylinRestfulSourceDTO.builder()
                .url(url).userName(username)
                .password(password)
                .project(project)
                .build();

    }

    /**
     * 构建关系型数据库DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public RdbmsSourceDTO buildRdbmsSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        RdbmsSourceDTO sourceDTO = getDataValObj(val);
        sourceDTO.setUrl(url);
        sourceDTO.setUsername(username);
        sourceDTO.setPassword(password);
        sourceDTO.setSourceType(val);
        sourceDTO.setSchema(schema);
        sourceDTO.setKerberosConfig(confMap);
        return sourceDTO;
    }

    public RdbmsSourceDTO getDataValObj(Integer val) {
        Objects.requireNonNull(val);
        RdbmsSourceDTO returnDTO = null;
        if (Objects.equals(DataSourceType.SQLServer.getVal(), val)) {
            returnDTO = SqlserverSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.SQLSERVER_2017_LATER.getVal(), val)) {
            returnDTO = Sqlserver2017SourceDTO.builder().build();
        }  else if (Objects.equals(DataSourceType.DMDB.getVal(), val)) {
            returnDTO = DmSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.RDBMS.getVal(), val)) {
            returnDTO = RdbmsSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.Clickhouse.getVal(), val)) {
            returnDTO = ClickHouseSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.Kylin.getVal(), val)) {
            returnDTO = KylinSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.Phoenix.getVal(), val)) {
            returnDTO = PhoenixSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.PHOENIX5.getVal(), val)) {
            returnDTO = Phoenix5SourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.PostgreSQL.getVal(), val)) {
            returnDTO = PostgresqlSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.DB2.getVal(), val)) {
            returnDTO = Db2SourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.KINGBASE8.getVal(), val)) {
            returnDTO = KingbaseSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.LIBRA.getVal(), val)) {
            returnDTO = LibraSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.GBase_8a.getVal(), val)) {
            returnDTO = GBaseSourceDTO.builder().build();
        } else if (Objects.equals(DataSourceType.GREENPLUM6.getVal(), val)) {
            returnDTO = Greenplum6SourceDTO.builder().build();
        } else {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
        }
        return returnDTO;
    }


    /**
     * 构建Presto数据库DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @return
     */
    public PrestoSourceDTO buildPrestoSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        return PrestoSourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .sourceType(val)
                .schema(schema)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建InfluxDB数据库DTO参数
     * @param dataJson
     * @return
     */
    public InfluxDBSourceDTO buildInfluxDBSourceDTO(JSONObject dataJson) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        return InfluxDBSourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    /**
     * 构建InfluxDB数据库DTO参数
     * @param dataJson
     * @return
     */
    public OpenTSDBSourceDTO buildOpenTSDBSourceDTO(JSONObject dataJson) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.URL);
        return OpenTSDBSourceDTO
                .builder()
                .url(url)
                .build();
    }

    /**
     * 构建带有schema的关系型数据库DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public RdbmsSourceDTO buildRdbmsSourceWithSchema(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        if (StringUtils.isBlank(schema)) {
            schema = dataJson.getString("schema");
        }
        return buildRdbmsSourceDTO(dataJson, confMap, schema, val);
    }

    /**
     * 构建Hive2.x DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public HiveSourceDTO buildHiveSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String defaultFS = null;
        String hadoopConfig = null;
        defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return  HiveSourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建Inceptor DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public InceptorSourceDTO buildInceptorSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String defaultFS = null;
        String hadoopConfig = null;
        defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return  InceptorSourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建Hive3.x DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public Hive3SourceDTO buildHive3SourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String defaultFS = null;
        String hadoopConfig = null;
        defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return  Hive3SourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建Hive1.x DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public Hive1SourceDTO buildHive1SourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String defaultFS = null;
        String hadoopConfig = null;
        defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return  Hive1SourceDTO
                .builder()
                .url(url)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建SparkDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public SparkSourceDTO buildSparkSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = dataJson.containsKey("jdbcUrl") ? dataJson.getString("jdbcUrl") : "";
        String username = dataJson.containsKey("username") ? dataJson.getString("username") : "";
        String password = dataJson.containsKey("password") ? dataJson.getString("password") : "";
        String defaultFS = null;
        String hadoopConfig = null;
        defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return SparkSourceDTO.builder()
                .url(url)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建OdpsDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public OdpsSourceDTO buildOdpsSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        return OdpsSourceDTO.builder()
                .config(dataJson.toJSONString())
                .build();
    }

    /**
     * 构建ImpalaDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public ImpalaSourceDTO buildImpalaSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String jdbcUrl = CommonUtils.getStrFromJson(dataJson, FormNames.JDBC_URL);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String hadoopConfig = null;
        String defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            // 不匹配HDFS正则
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG) && Strings.isNotBlank(dataJson.getString(FormNames.HADOOP_CONFIG))) {

            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return ImpalaSourceDTO
                .builder()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .schema(schema)
                .sourceType(val)
                .defaultFS(defaultFS)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建HdfsDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public HdfsSourceDTO buildHdfsSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String defaultFS = dataJson.getString(FormNames.DEFAULT_FS);
        if (!defaultFS.matches(RegexMatch.DEFAULT_FS_REGEX)) {
            throw new PubSvcDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
        }
        String hadoopConfig = null;
        if (dataJson.containsKey(FormNames.HADOOP_CONFIG)) {
            hadoopConfig = dataJson.getString(FormNames.HADOOP_CONFIG);
        }
        return HdfsSourceDTO
                .builder()
                .defaultFS(defaultFS)
                .schema(schema)
                .sourceType(val)
                .config(hadoopConfig)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建FTPDTP参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public FtpSourceDTO buildFtpSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String host = CommonUtils.getStrFromJson(dataJson, FormNames.HOST);
        String port = CommonUtils.getStrFromJson(dataJson, FormNames.PORT);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String protocol = CommonUtils.getStrFromJson(dataJson, FormNames.PROTOCOL);
        String connectMode = CommonUtils.getStrFromJson(dataJson, FormNames.CONNECT_MODE);
        String auth = CommonUtils.getStrFromJson(dataJson, FormNames.AUTH);
        String rsaPath = CommonUtils.getStrFromJson(dataJson, FormNames.RSA_PATH);

        return FtpSourceDTO.builder()
                .url(host)
                .hostPort(port)
                .username(username)
                .password(password)
                .protocol(protocol)
                .connectMode(connectMode)
                .auth(auth)
                .path(rsaPath)
                .build();
    }

    /**
     * 构建HbaseDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public HbaseSourceDTO buildHbaseSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String hbaseQuorum = CommonUtils.getStrFromJson(dataJson, FormNames.HBASE_QUORUM);
        String hbaseParent = CommonUtils.getStrFromJson(dataJson, FormNames.HBASE_PARENT);
        String hbaseOther = CommonUtils.getStrFromJson(dataJson, FormNames.HBASE_OTHER);

        return HbaseSourceDTO
                .builder()
                .url(hbaseQuorum)
                .path(hbaseParent)
                .schema(schema)
                .sourceType(val)
                .others(hbaseOther)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建EsDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public ESSourceDTO buildEsSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String address = CommonUtils.getStrFromJson(dataJson, FormNames.ADDRESS);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);

        return ESSourceDTO
                .builder()
                .url(address)
                .schema(schema)
                .username(username)
                .password(password)
                .sourceType(val)
                .build();
    }

    /**
     * 构建SolrDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public SolrSourceDTO buildSolrSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String zkHost = CommonUtils.getStrFromJson(dataJson, FormNames.ZK_HOSt);
        String chroot = CommonUtils.getStrFromJson(dataJson, FormNames.CHOROT);

        return SolrSourceDTO
                .builder()
                .zkHost(zkHost)
                .schema(schema)
                .chroot(chroot)
                .sourceType(val)
                .kerberosConfig(confMap)
                .build();
    }

    /**
     * 构建MongoDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public MongoSourceDTO buildMongoSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        if (StringUtils.isBlank(schema)) {
            schema = CommonUtils.getStrFromJson(dataJson, FormNames.DATABASE);
        }
        String hostPorts = CommonUtils.getStrFromJson(dataJson, FormNames.HOST_PORTS);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);

        return MongoSourceDTO
                .builder()
                .hostPort(hostPorts)
                .username(username)
                .password(password)
                .sourceType(val)
                .schema(schema)
                .build();
    }

    /**
     * 构建RedisDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public RedisSourceDTO buildRedisSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        if (StringUtils.isBlank(schema)) {
            schema = CommonUtils.getStrFromJson(dataJson, FormNames.DATABASE);
        }
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        Integer redisType = CommonUtils.getObjFromJson(dataJson, FormNames.REDIS_TYPE, Integer.class);
        RedisMode redisMode = null;
        if (redisType != null) {
            redisMode = RedisMode.getRedisModel(redisType);
        }
        String masterName = CommonUtils.getStrFromJson(dataJson, FormNames.MASTER_NAME);
        String hostPort = CommonUtils.getStrFromJson(dataJson, FormNames.HOST_PORT);
        return RedisSourceDTO
                .builder()
                .password(password)
                .sourceType(val)
                .redisMode(redisMode)
                .master(masterName)
                .schema(schema)
                .hostPort(hostPort)
                .build();
    }

    /**
     * 构建S3DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public S3SourceDTO buildS3SourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String hostname = CommonUtils.getStrFromJson(dataJson, FormNames.HOST_NAME);
        String accessKey = CommonUtils.getStrFromJson(dataJson, FormNames.ACCESS_KEY);
        String secretKey = CommonUtils.getStrFromJson(dataJson, FormNames.SECRET_KEY);

        return S3SourceDTO
                .builder()
                .hostname(hostname)
                .password(secretKey)
                .username(accessKey)
                .build();
    }

    /**
     * 构建AWSS3DTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public AwsS3SourceDTO buildAwsS3SourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String regine = CommonUtils.getStrFromJson(dataJson, FormNames.REGINE);
        String accessKey = CommonUtils.getStrFromJson(dataJson, FormNames.ACCESS_KEY);
        String secretKey = CommonUtils.getStrFromJson(dataJson, FormNames.SECRET_KEY);

        return AwsS3SourceDTO
                .builder()
                .region(regine)
                .accessKey(accessKey)
                .secretKey(secretKey)
                .build();

    }

    /**
     * 构造KafkaDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public KafkaSourceDTO buildKafkaSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.ADDRESS);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        String brokerUrls = dataJson.getString(FormNames.BROKER_LIST);
        return KafkaSourceDTO
                .builder()
                .brokerUrls(brokerUrls)
                .sourceType(val)
                .kerberosConfig(confMap)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    /**
     * 构建EMQDTO参数
     * @param dataJson
     * @param confMap
     * @param schema
     * @param val
     * @return
     */
    public EMQSourceDTO buildEMQSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Integer val) {
        String url = CommonUtils.getStrFromJson(dataJson, FormNames.ADDRESS);
        String username = CommonUtils.getStrFromJson(dataJson, FormNames.USERNAME);
        String password = CommonUtils.getStrFromJson(dataJson, FormNames.PASSWORD);
        return EMQSourceDTO.builder()
                .url(url)
                .username(username)
                .password(password)
                .sourceType(val)
                .build();
    }

    /**
     * 根据枚举值获取数据源类型
     * @param val
     * @return
     */
    public static SourceDTOType getSourceDTOType(Integer val) {
        Objects.requireNonNull(val);
        for (SourceDTOType sourceDTOType : values()) {
            if (sourceDTOType.getVal().equals(val)) {
                return sourceDTOType;
            }
        }
        throw new PubSvcDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param dataJson
     * @param sourceType
     * @return
     */
    public static ISourceDTO getSourceDTO(JSONObject dataJson, Integer sourceType) {
        JSONObject kerberosConfig = dataJson.getJSONObject(FormNames.KERBEROS_CONFIG);
        return getSourceDTO(dataJson, sourceType, kerberosConfig);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param data       未解码的数据信息
     * @param sourceType
     * @return
     */
    public static ISourceDTO getSourceDTO(String data, Integer sourceType) {
        Asserts.hasText(data, ErrorCode.DATASOURCE_CONF_ERROR);
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(data);
        JSONObject kerberosConfig = dataJson.getJSONObject(FormNames.KERBEROS_CONFIG);
        return getSourceDTO(dataJson, sourceType, kerberosConfig);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param dataJson
     * @param sourceType
     * @param confMap
     * @return
     */
    public static ISourceDTO getSourceDTO(JSONObject dataJson, Integer sourceType, Map<String, Object> confMap) {
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param dataJson
     * @param sourceType
     * @param confMap
     * @return
     */
    public static ISourceDTO getSourceDTO(JSONObject dataJson, Integer sourceType, Map<String, Object> confMap, String schema) {
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap, schema);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param data       未解码的数据信息
     * @param sourceType
     * @param confMap
     * @return
     */
    public static ISourceDTO getSourceDTO(String data, Integer sourceType, Map<String, Object> confMap) {
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(data);
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     * @param data
     * @param sourceType
     * @param confMap
     * @param schema
     * @return
     */
    public static ISourceDTO getSourceDTO(String data, Integer sourceType, Map<String, Object> confMap, String schema) {
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(data);
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap, schema);
    }

    public ISourceDTO buildDorisDBSourceDTO(JSONObject dataJson, String schema){
        Function<String ,String > getOrEmpty = (v)->
                StringUtils.isBlank(v)?StringUtils.EMPTY:v;
        return DorisSourceDTO.builder()
                .url(getOrEmpty.apply(dataJson.getString(FormNames.JDBC_URL)))
                .schema(schema)
                .username(getOrEmpty.apply(dataJson.getString(FormNames.USERNAME)))
                .password(getOrEmpty.apply(dataJson.getString(FormNames.PASSWORD)))
                .build();
    }

    public static void main(String[] args) {
        String httpUrl = "http://172.16.101.17:7070";
        String sss = "asdaada";
        URI uri = URI.create(httpUrl);
        System.out.println(uri.getHost());
    }


}
