package com.dtstack.taier.develop.enums.develop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.enums.RedisMode;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.enums.HadoopConfig;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;


/**
 * shixi
 *
 * @Description：获取数据源对应的sourceDTO
 */
public enum SourceDTOType {

    /**
     * clickhouse 数据源
     */
    Clickhouse(DataSourceType.Clickhouse.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
     * es6
     */
    ES6(DataSourceType.ES6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String address = dataJson.containsKey("address") ? dataJson.getString("address") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            ESSourceDTO esSourceDTO = ESSourceDTO
                    .builder()
                    .url(address)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .sourceType(DataSourceType.ES6.getVal())
                    .build();
            return esSourceDTO;
        }
    },

    /**
     * es7
     */
    ES7(DataSourceType.ES7.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String address = dataJson.containsKey("address") ? dataJson.getString("address") : "";
            String username = dataJson.containsKey(JDBC_USERNAME) ? dataJson.getString(JDBC_USERNAME) : "";
            String password = dataJson.containsKey(JDBC_PASSWORD) ? dataJson.getString(JDBC_PASSWORD) : "";
            ES7SourceDTO esSourceDTO = ES7SourceDTO
                    .builder()
                    .url(address)
                    .schema(schema)
                    .username(username)
                    .password(password)
                    .keyPath((String)expandConfig.get(SSL_LOCAL_DIR))
                    .sourceType(DataSourceType.ES7.getVal())
                    .build();
            return esSourceDTO;
        }
    },

    /**
     * hbase
     */
    HBASE(DataSourceType.HBASE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String hbaseQuorum = dataJson.containsKey("hbase_quorum") ? dataJson.getString("hbase_quorum") : "";
            String hbaseParent = dataJson.containsKey("hbase_parent") ? dataJson.getString("hbase_parent") : "";
            String hbaseOther = dataJson.containsKey("hbase_other") ? dataJson.getString("hbase_other") : "";
            HbaseSourceDTO hbaseSourceDTO = HbaseSourceDTO
                    .builder()
                    .url(hbaseQuorum)
                    .path(hbaseParent)
                    .schema(schema)
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
    HIVE3(DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            if (null == dataJson || StringUtils.isBlank(dataJson.getString("hostPorts"))) {
                return KuduSourceDTO.builder().build();
            }
            KuduSourceDTO kuduSourceDTO = KuduSourceDTO
                    .builder()
                    .url(dataJson.getString("hostPorts"))
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            return MySQL.getSourceDTO(dataJson, confMap, schema, expandConfig);
        }
    },

    /**
     * mysql8
     */
    MySQL8(DataSourceType.MySQL8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            Map<String, String> properties = JSONObject.parseObject(dataJson.toString(), Map.class);
            return OdpsSourceDTO
                    .builder()
                    .config(JSON.toJSONString(properties))
                    .sourceType(DataSourceType.MAXCOMPUTE.getVal())
                    .kerberosConfig(confMap)
                    .schema(schema)
                    .build();
        }
    },

    /**
     * phoennix
     */
    Phoenix(DataSourceType.Phoenix.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
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
    },

    /**
     * Open_TSDB
     */
    OPEN_TSDB(DataSourceType.OPENTSDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig) {
            return getSourceDTO(dataJson, confMap, null, expandConfig);
        }

        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
            String jdbcUrl = dataJson.containsKey(URL) ? dataJson.getString(URL) : "";
            OpenTSDBSourceDTO sourceDTO = OpenTSDBSourceDTO
                    .builder()
                    .url(jdbcUrl)
                    .build();
            return sourceDTO;
        }
    },
    ;
    public static final String JDBC_URL = "jdbcUrl";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    // ssl 认证文件路径
    public static final String SSL_LOCAL_DIR = "sslLocalDir";
    public static final String URL = "url";
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

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, Map<String, Object> expandConfig);

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig);

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
        throw new RdosDefineException("数据源类型不存在");
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param dataJson
     * @param sourceType
     * @param confMap
     * @return
     */
    public static ISourceDTO getSourceDTO(JSONObject dataJson, Integer sourceType, Map<String, Object> confMap, Map<String, Object> expandConfig) {
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap, expandConfig);
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param dataJson
     * @param sourceType
     * @param confMap
     * @return
     */
    public static ISourceDTO getSourceDTO(JSONObject dataJson, Integer sourceType, Map<String, Object> confMap, String schema, Map<String, Object> expandConfig) {
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        return sourceDTOType.getSourceDTO(dataJson, confMap, schema,expandConfig);
    }
}
