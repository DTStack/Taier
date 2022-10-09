package com.dtstack.taier.develop.datasource.convert.enums;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.AdbForPgSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.AwsS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ClickHouseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.CspS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Db2SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.DmSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.DorisRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.DorisSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.EMQSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ES7SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ESSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.FtpSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.GBaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.GreatDbSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Greenplum6SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HiveSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.IcebergSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ImpalaSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InceptorSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InfluxDBSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KingbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KuduSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.LibraSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.MongoSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql8SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OceanBaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OdpsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OpenTSDBSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Phoenix5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.PhoenixSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.PostgresqlSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.PrestoSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RedisSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.S3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana2SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SocketSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SolrSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SparkSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SqlserverSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TDengineSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TiDBSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TrinoSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.VerticaSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.WebSocketSourceDTO;
import com.dtstack.taier.datasource.api.enums.RedisMode;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.datasource.convert.Consistent;
import com.dtstack.taier.develop.datasource.convert.dto.ConfigDTO;
import com.dtstack.taier.develop.datasource.convert.utils.DtMapUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 将 dataJson、kerberosConfig 转换为 ISourceDTO
 *
 * @author ：wangchuan
 * date：Created in 上午10:47 2021/7/5
 * company: www.dtstack.com
 */
public enum SourceDTOType {

    /**
     * mysql
     */
    MySQL(DataSourceType.MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * mysql pxc
     */
    MySQL_PXC(DataSourceType.MySQLPXC.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * MYSQL_SHARDING
     */
    MYSQL_SHARDING(DataSourceType.MYSQL_SHARDING.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * TDEngine
     */
    TDENGINE(DataSourceType.TDENGINE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(TDengineSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * RDBMS
     */
    RDBMS(DataSourceType.RDBMS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * mysql8
     */
    MySQL8(DataSourceType.MySQL8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql8SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * Polardb_For_MySQL
     */
    Polardb_For_MySQL(DataSourceType.Polardb_For_MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * oracle
     */
    Oracle(DataSourceType.Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(OracleSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * sqlserver
     */
    SQLServer(DataSourceType.SQLServer.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(SqlserverSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * sqlserver 2017
     */
    SQLSERVER_2017_LATER(DataSourceType.SQLSERVER_2017_LATER.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(SqlserverSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * postgresql
     */
    PostgreSQL(DataSourceType.PostgreSQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(PostgresqlSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * adb for pg
     */
    ADB_FOR_PG(DataSourceType.ADB_FOR_PG.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(AdbForPgSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * OceanBase
     */
    OceanBase(DataSourceType.OceanBase.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(OceanBaseSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * DB2数据源
     */
    DB2(DataSourceType.DB2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Db2SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * 达梦数据库
     */
    DMDB(DataSourceType.DMDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(DmSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * 达梦数据库 oracle
     */
    DMDB_For_Oracle(DataSourceType.DMDB_For_Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(DmSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * kingbase8
     */
    Kingbase(DataSourceType.KINGBASE8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(KingbaseSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * hive 1.x
     */
    HIVE1X(DataSourceType.HIVE1X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            Hive1SourceDTO sourceDTO = Hive1SourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * hive 2.x
     */
    HIVE2X(DataSourceType.HIVE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            HiveSourceDTO sourceDTO = HiveSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * hive 3.x
     */
    HIVE3X(DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            Hive3SourceDTO sourceDTO = Hive3SourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * carbonData
     */
    CarbonData(DataSourceType.CarbonData.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            HiveSourceDTO sourceDTO = HiveSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * sparkThrift
     */
    SparkThrift2_1(DataSourceType.SparkThrift2_1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            SparkSourceDTO sourceDTO = SparkSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * inceptor
     */
    INCEPTOR(DataSourceType.INCEPTOR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            InceptorSourceDTO sourceDTO = InceptorSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            sourceDTO.setMetaStoreUris(dataJson.getString(Consistent.META_STORE_URIS));
            return sourceDTO;
        }
    },

    /**
     * impala
     */
    IMPALA(DataSourceType.IMPALA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            ImpalaSourceDTO sourceDTO = ImpalaSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, dataJson, configDTO);
            sourceDTO.setDefaultFS(getDefaultFS(dataJson));
            sourceDTO.setConfig(dataJson.getString(Consistent.HADOOP_CONFIG));
            return sourceDTO;
        }
    },

    /**
     * odps(maxCompute)
     */
    MAXCOMPUTE(DataSourceType.MAXCOMPUTE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return OdpsSourceDTO.builder().config(dataJson.toJSONString()).build();
        }
    },

    /**
     * greenplum6
     */
    GREENPLUM6(DataSourceType.GREENPLUM6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Greenplum6SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * Libra
     */
    LIBRA(DataSourceType.LIBRA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(LibraSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * GBase
     */
    GBase_8a(DataSourceType.GBase_8a.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(GBaseSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * HDFS
     */
    HDFS(DataSourceType.HDFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return HdfsSourceDTO.builder()
                    .defaultFS(getDefaultFS(dataJson))
                    .config(dataJson.getString(Consistent.HADOOP_CONFIG))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .build();
        }
    },

    /**
     * HDFS3
     */
    HDFS3(DataSourceType.HDFS3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return Hdfs3SourceDTO.builder()
                    .defaultFS(getDefaultFS(dataJson))
                    .config(dataJson.getString(Consistent.HADOOP_CONFIG))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .build();
        }
    },

    /**
     * FTP or SFTP
     */
    FTP(DataSourceType.FTP.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return FtpSourceDTO.builder()
                    .auth(dataJson.getString(Consistent.AUTH))
                    .url(dataJson.getString(Consistent.HOST))
                    .hostPort(dataJson.getString(Consistent.PORT))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .protocol(dataJson.getString(Consistent.PROTOCOL))
                    .connectMode(dataJson.getString(Consistent.CONNECT_MODE))
                    .path(dataJson.getString(Consistent.RSA_PATH)).build();
        }
    },

    /**
     * clickHouse
     */
    ClickHouse(DataSourceType.Clickhouse.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(ClickHouseSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * tidb
     */
    TiDB(DataSourceType.TiDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(TiDBSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * Kudu
     */
    Kudu(DataSourceType.Kudu.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return KuduSourceDTO.builder()
                    .url(dataJson.getString(Consistent.HOST_PORTS))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .build();
        }
    },

    /**
     * Kylin
     */
    Kylin(DataSourceType.Kylin.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(KylinSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * KylinRestful
     */
    KylinRestful(DataSourceType.KylinRestful.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return KylinRestfulSourceDTO.builder()
                    .url(dataJson.getString(Consistent.KYLIN_RESTFUL_AUTH_URL))
                    .userName(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .project(dataJson.getString(Consistent.KYLIN_RESTFUL_PROJECT)).build();
        }
    },

    /**
     * dorisRestful
     */
    DorisRestful(DataSourceType.DorisRestful.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return DorisRestfulSourceDTO.builder()
                    .url(dataJson.getString(Consistent.URL))
                    .schema(configDTO.getSchema())
                    .userName(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * doris
     */
    Doris(DataSourceType.DORIS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(DorisSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * HBASE
     */
    HBASE(DataSourceType.HBASE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            // 替换 master/region kerberos principal
            if (MapUtils.isNotEmpty(configDTO.getKerberosConfig())) {
                DtMapUtils.putStringIgnoreBlank(configDTO.getKerberosConfig(), Consistent.HBASE_MASTER_PRINCIPAL, dataJson.getString(Consistent.HBASE_MASTER_PRINCIPAL));
                DtMapUtils.putStringIgnoreBlank(configDTO.getKerberosConfig(), Consistent.HBASE_REGION_PRINCIPAL, dataJson.getString(Consistent.HBASE_REGION_PRINCIPAL));
            }
            String url = StringUtils.isBlank(dataJson.getString(Consistent.HBASE_QUORUM)) ? dataJson.getString(Consistent.HBASE_ZK_QUORUM) : dataJson.getString(Consistent.HBASE_QUORUM);
            return HbaseSourceDTO.builder()
                    .url(url)
                    .path(dataJson.getString(Consistent.HBASE_PARENT))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .others(DtMapUtils.getStrFromJson(dataJson, Consistent.HBASE_OTHER)).build();
        }
    },

    /**
     * HBASE2
     */
    HBASE2(DataSourceType.HBASE2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            // 替换 master/region kerberos principal
            if (MapUtils.isNotEmpty(configDTO.getKerberosConfig())) {
                DtMapUtils.putStringIgnoreBlank(configDTO.getKerberosConfig(), Consistent.HBASE_MASTER_PRINCIPAL, dataJson.getString(Consistent.HBASE_MASTER_PRINCIPAL));
                DtMapUtils.putStringIgnoreBlank(configDTO.getKerberosConfig(), Consistent.HBASE_REGION_PRINCIPAL, dataJson.getString(Consistent.HBASE_REGION_PRINCIPAL));
            }
            String url = StringUtils.isBlank(dataJson.getString(Consistent.HBASE_QUORUM)) ? dataJson.getString(Consistent.HBASE_ZK_QUORUM) : dataJson.getString(Consistent.HBASE_QUORUM);
            return HbaseSourceDTO.builder()
                    .url(url)
                    .path(dataJson.getString(Consistent.HBASE_PARENT))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .others(DtMapUtils.getStrFromJson(dataJson, Consistent.HBASE_OTHER)).build();
        }
    },

    /**
     * Phoenix
     */
    Phoenix(DataSourceType.Phoenix.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(PhoenixSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * PHOENIX5
     */
    PHOENIX5(DataSourceType.PHOENIX5.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Phoenix5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * ES
     */
    ES(DataSourceType.ES.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return ESSourceDTO.builder()
                    .url(dataJson.getString(Consistent.ADDRESS))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD)).build();
        }
    },

    /**
     * Solr
     */
    SOLR(DataSourceType.SOLR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return SolrSourceDTO.builder()
                    .zkHost(dataJson.getString(Consistent.ZK_HOSt))
                    .schema(configDTO.getSchema())
                    .chroot(dataJson.getString(Consistent.CHOROT))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .build();
        }
    },

    /**
     * ES6
     */
    ES6(DataSourceType.ES6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return ESSourceDTO.builder()
                    .url(dataJson.getString(Consistent.ADDRESS))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD)).build();
        }
    },

    /**
     * ES7
     */
    ES7(DataSourceType.ES7.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return ES7SourceDTO.builder()
                    .url(dataJson.getString(Consistent.ADDRESS))
                    .kerberosConfig(configDTO.getKerberosConfig())
                    .sslConfig(configDTO.getSslConfig())
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * MONGODB
     */
    MONGODB(DataSourceType.MONGODB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return MongoSourceDTO.builder()
                    .hostPort(dataJson.getString(Consistent.HOST_PORTS))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .schema(StringUtils.isNotBlank(dataJson.getString(Consistent.DATABASE)) ? dataJson.getString(Consistent.DATABASE) : configDTO.getSchema())
                    .build();
        }
    },

    /**
     * REDIS
     */
    REDIS(DataSourceType.REDIS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            Integer redisModeInteger = dataJson.getInteger(Consistent.REDIS_TYPE);
            RedisMode redisMode = Objects.nonNull(redisModeInteger) ? RedisMode.getRedisModel(redisModeInteger) : RedisMode.Standalone;
            return RedisSourceDTO
                    .builder()
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .redisMode(redisMode)
                    .master(dataJson.getString(Consistent.MASTER_NAME))
                    .schema(StringUtils.isNotBlank(configDTO.getSchema()) ? configDTO.getSchema() : dataJson.getString(Consistent.DATABASE))
                    .hostPort(dataJson.getString(Consistent.HOST_PORT))
                    .build();
        }
    },

    /**
     * S3
     */
    S3(DataSourceType.S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return S3SourceDTO.builder()
                    .hostname(dataJson.getString(Consistent.HOST_NAME))
                    .username(dataJson.getString(Consistent.ACCESS_KEY))
                    .password(dataJson.getString(Consistent.SECRET_KEY))
                    .build();
        }
    },

    /**
     * aws S3
     */
    AWS_S3(DataSourceType.AWS_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return AwsS3SourceDTO.builder()
                    .region(dataJson.getString(Consistent.REGION))
                    .accessKey(dataJson.getString(Consistent.ACCESS_KEY))
                    .secretKey(dataJson.getString(Consistent.SECRET_KEY))
                    .build();
        }
    },

    /**
     * csp S3
     */
    CSP_S3(DataSourceType.CSP_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return CspS3SourceDTO.builder()
                    .region(dataJson.getString(Consistent.REGION))
                    .accessKey(dataJson.getString(Consistent.ACCESS_KEY))
                    .secretKey(dataJson.getString(Consistent.SECRET_KEY))
                    .build();
        }
    },

    /**
     * kafka 0.9
     */
    KAFKA_09(DataSourceType.KAFKA_09.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return buildKafkaSourceDTO(dataJson, configDTO);
        }
    },

    /**
     * kafka 0.10
     */
    KAFKA_10(DataSourceType.KAFKA_10.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return buildKafkaSourceDTO(dataJson, configDTO);
        }
    },

    /**
     * kafka 0.11
     */
    KAFKA_11(DataSourceType.KAFKA_11.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return buildKafkaSourceDTO(dataJson, configDTO);
        }
    },

    /**
     * kafka 1.x
     */
    KAFKA(DataSourceType.KAFKA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return buildKafkaSourceDTO(dataJson, configDTO);
        }
    },

    /**
     * kafka 2.x
     */
    KAFKA_2X(DataSourceType.KAFKA_2X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return buildKafkaSourceDTO(dataJson, configDTO);
        }
    },

    /**
     * EMQ
     */
    EMQ(DataSourceType.EMQ.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return EMQSourceDTO.builder()
                    .url(dataJson.getString(Consistent.ADDRESS))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * websocket
     */
    WEB_SOCKET(DataSourceType.WEB_SOCKET.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            JSONObject params = dataJson.getJSONObject(Consistent.WEB_SOCKET_PARAMS);
            Map<String, String> paramMap = new HashMap<>();
            if (MapUtils.isNotEmpty(params)) {
                for (String key : params.keySet()) {
                    paramMap.put(key, params.getString(key));
                }
            }
            return WebSocketSourceDTO
                    .builder()
                    .url(dataJson.getString(Consistent.URL))
                    .authParams(paramMap)
                    .build();
        }
    },

    /**
     * socket
     */
    SOCKET(DataSourceType.SOCKET.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return SocketSourceDTO
                    .builder()
                    .hostPort(dataJson.getString(Consistent.URL))
                    .build();
        }
    },

    /**
     * vertica
     */
    VERTICA(DataSourceType.VERTICA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(VerticaSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * vertica
     */
    VERTICA11(DataSourceType.VERTICA11.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(VerticaSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * ads
     */
    ADS(DataSourceType.ADS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * Presto
     */
    Presto(DataSourceType.Presto.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(PrestoSourceDTO.builder().build(), dataJson, configDTO);
        }
    },

    /**
     * RESTFUL
     */
    RESTFUL(DataSourceType.RESTFUL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return RestfulSourceDTO.builder()
                    .url(dataJson.getString(Consistent.URL))
                    .protocol(dataJson.getString(Consistent.PORTAL))
                    .headers(JSON.parseObject(dataJson.getString(Consistent.HEADER), HashMap.class))
                    .build();
        }
    },

    /**
     * OPENTSDB
     */
    OPENTSDB(DataSourceType.OPENTSDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return OpenTSDBSourceDTO.builder().url(dataJson.getString(Consistent.URL)).build();
        }
    },

    /**
     * InfluxDB
     */
    INFLUXDB(DataSourceType.INFLUXDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return InfluxDBSourceDTO.builder()
                    .url(dataJson.getString(Consistent.URL))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * Polardb_For_MySQL
     */
    TRINO(DataSourceType.TRINO.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return fillRdbmsSourceDTO(
                    TrinoSourceDTO.builder().sslConfig(configDTO.getSslConfig()).catalog(dataJson.getString(Consistent.CATALOG)).build(),
                    dataJson,
                    configDTO);
        }
    },

    /**
     * iceberg
     */
    ICEBERG(DataSourceType.ICEBERG.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return IcebergSourceDTO.builder()
                    .uri(dataJson.getString(Consistent.URI))
                    .clients(dataJson.getInteger(Consistent.CLIENTS))
                    .warehouse(dataJson.getString(Consistent.WAREHOUSE))
                    .sftpConf(configDTO.getSftpConf())
                    .confDir(MapUtils.getString(configDTO.getExpendConfig(), Consistent.CONF_DIR)).build();
        }
    },

    /**
     * SAP_HANA1
     */
    SAP_HANA1(DataSourceType.SAP_HANA1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return SapHana1SourceDTO
                    .builder()
                    .url(dataJson.getString(Consistent.JDBC_URL))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * SAP_HANA2
     */
    SAP_HANA2(DataSourceType.SAP_HANA2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return SapHana2SourceDTO
                    .builder()
                    .url(dataJson.getString(Consistent.JDBC_URL))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    },

    /**
     * GreatDb
     */
    GREAT_DB(DataSourceType.GREAT_DB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
            return GreatDbSourceDTO
                    .builder()
                    .url(dataJson.getString(Consistent.JDBC_URL))
                    .username(dataJson.getString(Consistent.USERNAME))
                    .password(dataJson.getString(Consistent.PASSWORD))
                    .build();
        }
    };

    SourceDTOType(Integer val) {
        this.val = val;
    }

    private final Integer val;

    public Integer getVal() {
        return val;
    }

    public abstract ISourceDTO getSourceDTO(JSONObject dataJson, ConfigDTO configDTO);

    /**
     * 填充 rdbms 数据源相关参数
     *
     * @param rdbmsSourceDTO 关系型数据源 DTO
     * @param dataJson       数据源信息
     * @param configDTO      相关配置
     * @return 数据源 sourceDTO
     */
    protected RdbmsSourceDTO fillRdbmsSourceDTO(RdbmsSourceDTO rdbmsSourceDTO, JSONObject dataJson, ConfigDTO configDTO) {
        rdbmsSourceDTO.setSchema(StringUtils.isNotBlank(configDTO.getSchema()) ? configDTO.getSchema() : dataJson.getString("schema"));
        rdbmsSourceDTO.setUrl(dataJson.getString(Consistent.JDBC_URL));
        rdbmsSourceDTO.setUsername(dataJson.getString(Consistent.USERNAME));
        rdbmsSourceDTO.setPassword(dataJson.getString(Consistent.PASSWORD));
        rdbmsSourceDTO.setKerberosConfig(configDTO.getKerberosConfig());
        return rdbmsSourceDTO;
    }

    /**
     * 填充 kafka sourceDTO
     *
     * @param dataJson  数据源信息
     * @param configDTO 相关配置
     */
    protected void fillKafkaSourceDTO(KafkaSourceDTO kafkaSourceDTO, JSONObject dataJson, ConfigDTO configDTO) {
        kafkaSourceDTO.setBrokerUrls(dataJson.getString(Consistent.BROKER_LIST));
        kafkaSourceDTO.setUrl(dataJson.getString(Consistent.ADDRESS));
        kafkaSourceDTO.setUsername(dataJson.getString(Consistent.USERNAME));
        kafkaSourceDTO.setPassword(dataJson.getString(Consistent.PASSWORD));
        kafkaSourceDTO.setKerberosConfig(configDTO.getKerberosConfig());
    }

    protected String getDefaultFS(JSONObject dataJson) {
        String defaultFs = dataJson.getString(Consistent.DEFAULT_FS);
        if (StringUtils.isNotBlank(defaultFs) && !defaultFs.matches(Consistent.DEFAULT_FS_REGEX)) {
            throw new DtCenterDefException("defaultFS格式不正确");
        }
        return defaultFs;
    }

    /**
     * 构建 kafka sourceDTO
     *
     * @param dataJson  数据源信息
     * @param configDTO 相关配置
     * @return kafkaSourceDTO
     */
    protected KafkaSourceDTO buildKafkaSourceDTO(JSONObject dataJson, ConfigDTO configDTO) {
        return KafkaSourceDTO.builder()
                .brokerUrls(dataJson.getString(Consistent.BROKER_LIST))
                .url(dataJson.getString(Consistent.ADDRESS))
                .username(dataJson.getString(Consistent.USERNAME))
                .password(dataJson.getString(Consistent.PASSWORD))
                .authentication(dataJson.getString(Consistent.KAFKA_AUTHENTICATION))
                .kerberosConfig(configDTO.getKerberosConfig()).build();
    }

    /**
     * 根据枚举值获取数据源类型
     *
     * @param sourceType 数据源类型
     * @return SourceDTOType
     */
    protected static SourceDTOType getSourceDTOType(Integer sourceType) {
        for (SourceDTOType sourceDTOType : values()) {
            if (sourceDTOType.getVal().equals(sourceType)) {
                return sourceDTOType;
            }
        }
        throw new DtCenterDefException(String.format("找不到对应的数据源类型, sourceType：%s", sourceType));
    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param data       数据源信息，json 格式
     * @param sourceType 数据源类型
     * @param configDTO  相关配置
     * @return datasourceX 需要的 ISourceDTO
     */
    public static ISourceDTO getSourceDTO(String data, Integer sourceType, ConfigDTO configDTO) {
        JSONObject dataJson = getDataSourceJson(data);
        SourceDTOType sourceDTOType = getSourceDTOType(sourceType);
        ISourceDTO sourceDTO = sourceDTOType.getSourceDTO(dataJson, configDTO);
        AbstractSourceDTO abstractSourceDTO = (AbstractSourceDTO) sourceDTO;
        // 多处理一步 kerberos
        if (Objects.isNull(abstractSourceDTO.getKerberosConfig()) && Objects.nonNull(configDTO.getKerberosConfig())) {
            abstractSourceDTO.setKerberosConfig(configDTO.getKerberosConfig());
        }
        // 添加 sftp 配置
        abstractSourceDTO.setSftpConf(configDTO.getSftpConf());
        return abstractSourceDTO;
    }

    /**
     * 解析 dataJson 参数
     *
     * @param dataJsonStr dataJson 字符串
     * @return dataJson jsonObject 格式
     */
    private static JSONObject getDataSourceJson(String dataJsonStr) {
        if (StringUtils.isBlank(dataJsonStr)) {
            throw new DtCenterDefException("数据源信息为空");
        }
        try {
            return JSONObject.parseObject(dataJsonStr);
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("转化json格式异常：%s", e.getMessage()), e);
        }
    }
}
