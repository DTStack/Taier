package com.dtstack.taier.develop.datasource.convert.enums;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.AdbForPgSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Greenplum6SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3CDPSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HiveSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ImpalaSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InceptorSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.LibraSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SparkSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SqlserverSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TiDBSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TrinoSourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.develop.datasource.convert.dto.PluginInfoDTO;
import com.dtstack.taier.develop.datasource.convert.load.ConsoleLoaderService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供根据dtUicTenantId、dtUicUserId、engine类型获取对应的SourceDTO
 *
 * @author ：wangchuan
 * date：Created in 下午9:12 2020/11/4
 * company: www.dtstack.com
 */
public enum Engine2DTOService {

    /**
     * spark 不支持hive1.x
     */
    SPARK(DataSourceType.Spark.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.Spark.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    SPARK_THRIFT2_1(DataSourceType.SparkThrift2_1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.SparkThrift2_1.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    HIVE(DataSourceType.HIVE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            ISourceDTO sourceDTO = HiveSourceDTO.builder()
                    .sourceType(DataSourceType.HIVE.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
            return sourceDTO;
        }
    },

    HIVE3(DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return Hive3SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    HIVE3_CDP(DataSourceType.HIVE3_CDP.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            JSONObject map = JSONObject.parseObject(pluginInfo.getHadoopConfig());
            JSONObject sslClient = map.getJSONObject("sslClient");
            SSLConfig sslConfig = null;
            if (sslClient != null && !sslClient.isEmpty()) {
                try {
                    sslConfig = SSLConfig.builder()
                            .remoteSSLDir(sslClient.getString("remoteSSLDir"))
                            .sslClientConf(sslClient.getString("sslClientConf"))
                            .sslFileTimestamp((Timestamp) sslClient.getTimestamp("sslFileTimestamp")).build();
                } catch (Exception e) {
                    throw new DtCenterDefException(String.format("获取SSL证书异常，原因是：%s", e.getMessage()), e);
                }
            }
            return Hive3CDPSourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3_CDP.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .sslConfig(sslConfig)
                    .build();
        }
    },


    /**
     * hive 1.x版本
     */
    HIVE1(DataSourceType.HIVE1X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return Hive1SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    /**
     * HDFS
     */
    HDFS(DataSourceType.HDFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return HdfsSourceDTO.builder()
                    .sourceType(DataSourceType.HDFS.getVal())
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    ORACLE(DataSourceType.Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return OracleSourceDTO.builder()
                    .sourceType(DataSourceType.Oracle.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    IMPALA(DataSourceType.IMPALA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return ImpalaSourceDTO.builder()
                    .sourceType(DataSourceType.IMPALA.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .build();
        }
    },

    TIDB(DataSourceType.TiDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return TiDBSourceDTO.builder()
                    .sourceType(DataSourceType.TiDB.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    GREENPLUM(DataSourceType.GREENPLUM6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return Greenplum6SourceDTO.builder()
                    .sourceType(DataSourceType.GREENPLUM6.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    /**
     * Inceptor数据源
     */
    INCEPTOR(DataSourceType.INCEPTOR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return InceptorSourceDTO.builder()
                    .sourceType(DataSourceType.INCEPTOR.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .schema(dbName)
                    .kerberosConfig(pluginInfo.getKerberosConfig())
                    .defaultFS(pluginInfo.getDefaultFs())
                    .config(pluginInfo.getHadoopConfig())
                    .build();
        }
    },

    LIBRA(DataSourceType.LIBRA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.isNotBlank(dbName)) {
                params.put("currentSchema", dbName);
            }
            String jdbcUrl = buildJdbcURLWithParam(pluginInfo.getJdbcUrl(), params);
            return LibraSourceDTO.builder()
                    .sourceType(DataSourceType.LIBRA.getVal())
                    .url(jdbcUrl)
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    ADB_FOR_PG(DataSourceType.ADB_FOR_PG.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.isNotBlank(dbName)) {
                params.put("currentSchema", dbName);
            }
            String jdbcUrl = buildJdbcURLWithParam(pluginInfo.getJdbcUrl(), params);
            return AdbForPgSourceDTO.builder()
                    .sourceType(DataSourceType.ADB_FOR_PG.getVal())
                    .url(jdbcUrl)
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    MySQL(DataSourceType.MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return Mysql5SourceDTO.builder()
                    .sourceType(DataSourceType.MySQL.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    SQLServer(DataSourceType.SQLServer.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return SqlserverSourceDTO.builder()
                    .sourceType(DataSourceType.SQLServer.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    },

    TRINO(DataSourceType.TRINO.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            Map<String, String> sftp = null;
            SSLConfig sslConfig = null;
            JSONObject sslClient = pluginInfo.getSslClient();
            if (sslClient != null && !sslClient.isEmpty()) {
                sslConfig = SSLConfig.builder()
                        .remoteSSLDir(sslClient.getString("remoteSSLDir"))
                        .sslClientConf(sslClient.getString("sslClientConf"))
                        .sslFileTimestamp((Timestamp) sslClient.getTimestamp("sslFileTimestamp"))
                        .build();
            }
            return TrinoSourceDTO
                    .builder()
                    .sourceType(DataSourceType.TRINO.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .sslConfig(sslConfig)
                    .sftpConf(sftp)
                    .build();
        }
    },


    SAP_HANA1(DataSourceType.SAP_HANA1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName) {
            return SapHana1SourceDTO.builder()
                    .sourceType(DataSourceType.SAP_HANA1.getVal())
                    .url(buildUrlWithDb(pluginInfo.getJdbcUrl(), dbName))
                    .schema(dbName)
                    .username(pluginInfo.getUsername())
                    .password(pluginInfo.getPassword())
                    .build();
        }
    };

    /**
     * 处理 JDBC 参数
     *
     * @param jdbcUrl jdbc url
     * @param params  参数
     * @return jdbc url
     */
    public static String buildJdbcURLWithParam(String jdbcUrl, Map<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            return jdbcUrl;
        }

        boolean needFlag = needSplicingSymbol(jdbcUrl);
        String splicingSymbol = needFlag ? "?" : "";
        jdbcUrl = jdbcUrl + splicingSymbol;
        StringBuilder paramBuilder = new StringBuilder();

        params.forEach((key, value) -> {
            paramBuilder.append("&")
                    .append(key)
                    .append("=")
                    .append(value);
        });

        String paramStr = paramBuilder.toString();
        if (needFlag) {
            paramStr = paramStr.replaceFirst("&", "");
        }
        jdbcUrl += paramStr;

        return jdbcUrl;
    }

    /**
     * 是否需要jdbc url 的参数拼接符 "?"
     *
     * @param jdbcURL jdbc url
     * @return true or false
     */
    private static boolean needSplicingSymbol(String jdbcURL) {
        return !jdbcURL.contains("?");
    }

    private final Integer val;

    Engine2DTOService(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    public abstract ISourceDTO getSourceDTO(PluginInfoDTO pluginInfo, Long dtUicTenantId, String dbName);

    /**
     * 根据枚举值获取对应的枚举类
     *
     * @param val 引擎类型
     * @return {@link Engine2DTOService}
     */
    public static Engine2DTOService getSourceDTOType(Integer val) {
        for (Engine2DTOService engine2DTOEnum : values()) {
            if (engine2DTOEnum.val.equals(val)) {
                return engine2DTOEnum;
            }
        }
        throw new DtCenterDefException("暂时不支持该引擎类型");
    }

    /**
     * 根据db构建url
     *
     * @param jdbcUrl url
     * @param dbName  数据库
     * @return 构建后的url
     */
    public static String buildUrlWithDb(String jdbcUrl, String dbName) {

        dbName = StringUtils.isNotBlank(dbName) ? dbName.trim() : "";

        if (StringUtils.isNotBlank(jdbcUrl) && jdbcUrl.trim().contains("%s")) {
            return String.format(jdbcUrl, dbName);
        }
        return jdbcUrl;
    }

    /**
     * jobType 转化为 对应的dataSourceType
     * 如果没有对应的数据源类型 抛出异常
     *
     * @return DataSourceType
     */
    public static DataSourceType jobTypeTransitionDataSourceType(EComponentType componentType, String version) {
        if (EComponentType.HIVE_SERVER.equals(componentType)) {
            if (HiveVersion.HIVE_1x.getVersion().equals(version)) {
                return DataSourceType.HIVE1X;
            } else if (HiveVersion.HIVE_3x.getVersion().equals(version)) {
                return DataSourceType.HIVE3X;
            } else if (HiveVersion.HIVE_3x_CDP.getVersion().equals(version)) {
                return DataSourceType.HIVE3_CDP;
            } else if (HiveVersion.HIVE_3x_APACHE.getVersion().equals(version)) {
                return DataSourceType.HIVE3X;
            } else {
                return DataSourceType.HIVE;
            }
        } else if (EComponentType.SPARK_THRIFT.equals(componentType)) {
            return DataSourceType.SparkThrift2_1;
        } else {
            throw new DtCenterDefException("jobType not transition dataSourceType");
        }
    }
}
