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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.enums.RedisMode;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taiga.common.constant.FormNames;
import com.dtstack.taiga.common.enums.HadoopConfig;
import com.dtstack.taiga.common.exception.DtCenterDefException;
import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.common.util.DataSourceUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


/**
 * shixi
 * @Description：获取数据源对应的sourceDTO
 */
public enum SourceDTOType {

    /**
     * clickhouse 数据源
     */
    Clickhouse(DataSourceType.Clickhouse.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            ClickHouseSourceDTO sourceDTO = ClickHouseSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.Clickhouse.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
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
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("schema");
            }
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            Db2SourceDTO sourceDTO = Db2SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.DB2.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
            return sourceDTO;
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
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            DmSourceDTO sourceDTO = DmSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.DMDB.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * CarbonData
     */
    CarbonData(DataSourceType.CarbonData.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            HiveSourceDTO hiveSourceDTO = HiveSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.HIVE.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return hiveSourceDTO;
        }
    },

    /**
     * ftp
     */
    FTP(DataSourceType.FTP.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String host = dataJson.containsKey("host") ? dataJson.getString("host") : "";
            String port = dataJson.containsKey("port") ? dataJson.getString("port") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String protocol = dataJson.containsKey("protocol") ? dataJson.getString("protocol") : "";
            String connectMode = dataJson.containsKey("connectMode") ? dataJson.getString("connectMode") : "";
            String auth = dataJson.containsKey("auth") ? dataJson.getString("auth") : "";
            String rasPath = dataJson.containsKey("rasPath") ? dataJson.getString("rasPath") : "";
            FtpSourceDTO sourceDTO = FtpSourceDTO
                    .builder()
                    .url(host)
                    .hostPort(port)
                    .username(username)
                    .password(password)
                    .protocol(protocol)
                    .connectMode(connectMode)
                    .auth(auth)
                    .path(rasPath)
                    .sourceType(DataSourceType.FTP.getVal())
                    .build();
            return sourceDTO;
        }
    },

    /**
     * gbase
     */
    GBase_8a(DataSourceType.GBase_8a.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            GBaseSourceDTO sourceDTO = GBaseSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.GBase_8a.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * greenplum
     */
    GREENPLUM6(DataSourceType.GREENPLUM6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            Greenplum6SourceDTO sourceDTO = Greenplum6SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.GREENPLUM6.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * es
     */
    ES(DataSourceType.ES.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String address = dataJson.containsKey("address") ? dataJson.getString("address") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            ESSourceDTO esSourceDTO = ESSourceDTO
                    .builder()
                    .url(address)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.ES.getVal())
                    .build();
            return esSourceDTO;
        }
    },

    /**
     * hbase
     */
    HBASE(DataSourceType.HBASE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String hbaseQuorum = dataJson.containsKey("hbase_quorum") ? dataJson.getString("hbase_quorum") : "";
            String hbaseParent = dataJson.containsKey("hbase_parent") ? dataJson.getString("hbase_parent") : "";
            String hbaseOther = dataJson.containsKey("hbase_other") ? dataJson.getString("hbase_other") : "";
            String hbaseConfig = dataJson.containsKey("hbaseConfig") ? dataJson.getString("hbaseConfig") : "";
            HbaseSourceDTO hbaseSourceDTO = HbaseSourceDTO
                    .builder()
                    .url(hbaseQuorum)
                    .path(hbaseParent)
                    .schema(schema)
                    .config(hbaseConfig)
                    .sourceType(DataSourceType.HBASE.getVal())
                    .others(hbaseOther)
                    .kerberosConfig(confMap)
                    .build();
            return hbaseSourceDTO;

        }
    },

    /**
     * SparkThrift2_1
     */
    SparkThrift2_1(DataSourceType.SparkThrift2_1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            SparkSourceDTO sparkSourceDTO = SparkSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.HIVE.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return sparkSourceDTO;
        }
    },

    /**
     * hdfs
     */
    HDFS(DataSourceType.HDFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
            if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
            }
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal())) {
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }

            HdfsSourceDTO hdfsSourceDTO = HdfsSourceDTO
                    .builder()
                    .defaultFS(defaultFS)
                    .schema(schema)
                    .sourceType(DataSourceType.HDFS.getVal())
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();

            return hdfsSourceDTO;
        }
    },

    /**
     * hive
     */
    HIVE3 (DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            Hive3SourceDTO hiveSourceDTO = Hive3SourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return hiveSourceDTO;
        }
    },

    /**
     * hive
     */
    HIVE(DataSourceType.HIVE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            HiveSourceDTO hiveSourceDTO = HiveSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.HIVE.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return hiveSourceDTO;
        }
    },

    /**
     * hive 1.x
     */
    HIVE1X(DataSourceType.HIVE1X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            Hive1SourceDTO hiveSourceDTO = Hive1SourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return hiveSourceDTO;
        }
    },

    /**
     * impala
     */
    IMPALA(DataSourceType.IMPALA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            ImpalaSourceDTO sourceDTO = ImpalaSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.IMPALA.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * kudu
     */
    Kudu(DataSourceType.Kudu.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String hostPorts = dataJson.getString("hostPorts");
            if (null == dataJson || StringUtils.isBlank(hostPorts)) {
                return KuduSourceDTO.builder().build();
            }
            KuduSourceDTO kuduSourceDTO = KuduSourceDTO
                    .builder()
                    .url(hostPorts)
                    .schema(schema)
                    .sourceType(DataSourceType.Kudu.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return kuduSourceDTO;
        }
    },

    /**
     * AWSS3
     */
    AWSS3(DataSourceType.AWS_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String accessKey = dataJson.containsKey("accessKey") ? dataJson.getString("accessKey") : "";
            String region = dataJson.containsKey("region") ? dataJson.getString("region") : "";
            String secretKey = dataJson.containsKey("secretKey") ? dataJson.getString("secretKey") : "";
            AwsS3SourceDTO kuduSourceDTO = AwsS3SourceDTO
                    .builder()
                    .accessKey(accessKey)
                    .region(region)
                    .secretKey(secretKey)
                    .build();
            return kuduSourceDTO;
        }
    },

    /**
     * kylin
     */
    Kylin(DataSourceType.Kylin.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            KylinSourceDTO sourceDTO = KylinSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.Kylin.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * libra
     */
    LIBRA(DataSourceType.LIBRA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            LibraSourceDTO sourceDTO = LibraSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.LIBRA.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * mongodb
     */
    MONGODB(DataSourceType.MONGODB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("database");
            }
            String hostPorts = dataJson.containsKey("hostPorts") ? dataJson.getString("hostPorts") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            MongoSourceDTO mongoSourceDTO = MongoSourceDTO
                    .builder()
                    .hostPort(hostPorts)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.MONGODB.getVal())
                    .schema(schema)
                    .build();
            return mongoSourceDTO;
        }
    },

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
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            Mysql5SourceDTO sourceDTO = Mysql5SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.MySQL.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * tidb
     */
    TiDB(DataSourceType.TiDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            return MySQL.getSourceDTO(dataJson, confMap, schema);
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
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            Mysql8SourceDTO sourceDTO = Mysql8SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.MySQL8.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * maxcompute
     */
    MAXCOMPUTE(DataSourceType.MAXCOMPUTE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            Map<String, String> properties = JSONObject.parseObject(dataJson.toString(), Map.class);
            OdpsSourceDTO
                    .builder()
                    .config(JSON.toJSONString(properties))
                    .sourceType(DataSourceType.MAXCOMPUTE.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
            return null;
        }
    },

    /**
     * phoennix
     */
    Phoenix(DataSourceType.Phoenix.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            PhoenixSourceDTO sourceDTO = PhoenixSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.Phoenix.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * phoennix5
     */
    Phoenix5X(DataSourceType.PHOENIX5.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            Phoenix5SourceDTO sourceDTO = Phoenix5SourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.PHOENIX5.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
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
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("schema");
            }
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            PostgresqlSourceDTO sourceDTO = PostgresqlSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.PostgreSQL.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
            return sourceDTO;
        }
    },

    /**
     * redis
     */
    REDIS(DataSourceType.REDIS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("database");
            }
            String password = dataJson.getString(JDBC_PASSWORD);
            Integer redisType = dataJson.getInteger("redisType");
            RedisMode redisMode = null;
            if (redisType != null) {
                redisMode = RedisMode.getRedisModel(redisType);
            }
            String masterName = dataJson.getString("masterName");
            String hostPort = dataJson.getString("hostPort");
            RedisSourceDTO redisSourceDTO = RedisSourceDTO
                    .builder()
                    .password(password)
                    .sourceType(DataSourceType.REDIS.getVal())
                    .redisMode(redisMode)
                    .master(masterName)
                    .schema(schema)
                    .hostPort(hostPort).build();
            return redisSourceDTO;
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
            String jdbcUrl = dataJson.getString(JDBC_URL);
            String username = dataJson.getString(JDBC_USERNAME);
            String password = dataJson.getString(JDBC_PASSWORD);
            SqlserverSourceDTO sourceDTO = SqlserverSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.SQLServer.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
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
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            SqlserverSourceDTO sourceDTO = SqlserverSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.SQLServer.getVal())
                    .kerberosConfig(confMap)
                    .build();
            return sourceDTO;
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
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("schema");
            }
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            OracleSourceDTO oracleSourceDTO = OracleSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.Oracle.getVal())
                    .schema(schema)
                    .build();
            return oracleSourceDTO;
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
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("schema");
            }
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            KingbaseSourceDTO kingbaseSourceDTO = KingbaseSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .sourceType(DataSourceType.KINGBASE8.getVal())
                    .build();
            return kingbaseSourceDTO;
        }
    },

    /**
     * Inceptor
     */
    Inceptor(DataSourceType.INCEPTOR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String hiveMetastoreUris = dataJson.containsKey("hive.metastore.uris") ? dataJson.getString("hive.metastore.uris") : "";
            String defaultFS = null;
            String hadoopConfig = null;
            if (dataJson.containsKey(HadoopConfig.HADOOP_CONFIG.getVal()) && StringUtils.isNotBlank(dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal()))) {
                defaultFS = dataJson.getString(HadoopConfig.HDFS_DEFAULTFS.getVal());
                if (!defaultFS.matches(HadoopConfig.DEFAULT_FS_REGEX.getVal())) {
                    throw new RdosDefineException(ErrorCode.ERROR_DEFAULT_FS_FORMAT);
                }
                hadoopConfig = dataJson.getString(HadoopConfig.HADOOP_CONFIG.getVal());
            }
            InceptorSourceDTO hiveSourceDTO = InceptorSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .schema(schema)
                    .metaStoreUris(hiveMetastoreUris)
                    .sourceType(DataSourceType.INCEPTOR.getVal())
                    .defaultFS(defaultFS)
                    .config(hadoopConfig)
                    .kerberosConfig(confMap)
                    .build();
            return hiveSourceDTO;
        }
    },

    /**
     * InfluxDB
     */
    InfluxDB(DataSourceType.INFLUXDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            String url = dataJson.containsKey("url") ? dataJson.getString("url") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            String retentionPolicy = dataJson.containsKey("retentionPolicy") ? dataJson.getString("retentionPolicy") : "";
            InfluxDBSourceDTO influxDBSourceDTO = InfluxDBSourceDTO
                    .builder()
                    .url(url)
                    .username(username)
                    .password(password)
                    .database(schema)
                    .retentionPolicy(retentionPolicy)
                    .build();
            return influxDBSourceDTO;
        }
    },

    /**
     * AnalyticDB PostgreSQL
     */
    ADB_FOR_PG(DataSourceType.ADB_FOR_PG.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap) {
            return getSourceDTO(dataJson, confMap, null);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema) {
            if (StringUtils.isBlank(schema)) {
                schema = dataJson.getString("schema");
            }
            String jdbcUrl = dataJson.containsKey(JDBC_URL) ? dataJson.getString(JDBC_URL) : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            AdbForPgSourceDTO sourceDTO = AdbForPgSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.ADB_FOR_PG.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
            return sourceDTO;
        }
    }
    ;
    public static String JDBC_URL = "jdbcUrl";
    public static String JDBC_USERNAME = "username";
    public static String JDBC_PASSWORD = "password";
    /**
     * 数据源类型的值
     */
    private Integer val;

    public Integer getVal() {
        return val;
    }

    SourceDTOType(Integer val) {
        this.val = val;
    }

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap);

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema);


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
     * 根据枚举值获取数据源类型
     *
     * @param val
     * @return
     */
    public static SourceDTOType getSourceDTOType(Integer val) {
        for (SourceDTOType sourceDTOType : values()) {
            if (sourceDTOType.val.equals(val)) {
                return sourceDTOType;
            }
        }
        throw new DtCenterDefException("数据源类型不存在");
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
}
