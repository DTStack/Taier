package com.dtstack.batch.engine.rdbms.service.impl;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.HiveVersion;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.dtcenter.common.engine.ConsoleSend;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.engine.JdbcUrlPropertiesValue;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.cache.pool.config.PoolConfig;
import com.dtstack.dtcenter.loader.dto.source.*;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.vo.engine.EngineSupportVO;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    SPARK(DataSourceType.Spark.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.Spark.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    SPARK_THRIFT2_1(DataSourceType.SparkThrift2_1.getVal() ){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            return SparkSourceDTO.builder()
                    .sourceType(DataSourceType.SparkThrift2_1.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    HIVE(DataSourceType.HIVE.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = HiveSourceDTO.builder()
                        .sourceType(DataSourceType.HIVE.getVal())
                        .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                        .username(jdbcInfo.getUsername())
                        .password(jdbcInfo.getPassword())
                        .kerberosConfig(jdbcInfo.getKerberosConfig())
                        .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                        .config(config)
                        .poolConfig(buildPoolConfig())
                        .build();
            return sourceDTO;
        }
    },

    HIVE3(DataSourceType.HIVE3X.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = Hive3SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE3X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    },

    /**
     * hive 1.x版本
     */
    HIVE1(DataSourceType.HIVE1X.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = Hive1SourceDTO.builder()
                    .sourceType(DataSourceType.HIVE1X.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    },

    ORACLE(DataSourceType.Oracle.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            return OracleSourceDTO.builder()
                    .sourceType(DataSourceType.Oracle.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    IMPALA(DataSourceType.IMPALA.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            return ImpalaSourceDTO.builder()
                    .sourceType(DataSourceType.IMPALA.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    TIDB(DataSourceType.TiDB.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            return Mysql5SourceDTO.builder()
                    .sourceType(DataSourceType.TiDB.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    GREENPLUM(DataSourceType.GREENPLUM6.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            return Greenplum6SourceDTO.builder()
                    .sourceType(DataSourceType.GREENPLUM6.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    /**
     * Inceptor数据源
     */
    INCEPTOR(DataSourceType.INCEPTOR.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            String config = buildHadoopConfig(dtUicTenantId);
            ISourceDTO sourceDTO = InceptorSourceDTO.builder()
                    .sourceType(DataSourceType.INCEPTOR.getVal())
                    .url(buildUrlWithDb(jdbcInfo.getJdbcUrl(), dbName))
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .kerberosConfig(jdbcInfo.getKerberosConfig())
                    .defaultFS(HadoopConf.getDefaultFs(dtUicTenantId))
                    .config(config)
                    .poolConfig(buildPoolConfig())
                    .build();
            return sourceDTO;
        }
    },

    LIBRA(DataSourceType.LIBRA.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.isNotBlank(dbName)) {
                params.put("currentSchema", dbName);
            }
            String jdbcUrl = buildJdbcURLWithParam(jdbcInfo.getJdbcUrl(), params);
            return LibraSourceDTO.builder()
                    .sourceType(DataSourceType.LIBRA.getVal())
                    .url(jdbcUrl)
                    .schema(dbName)
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    },

    ADB_FOR_PG(DataSourceType.ADB_FOR_PG.getVal()){
        @Override
        public ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName) {
            Map<String, String> params = new HashMap<>();
            if (StringUtils.isNotBlank(dbName)) {
                params.put("currentSchema", dbName);
            }
            String jdbcUrl = buildJdbcURLWithParam(jdbcInfo.getJdbcUrl(), params);
            return AdbForPgSourceDTO.builder()
                    .sourceType(DataSourceType.ADB_FOR_PG.getVal())
                    .url(jdbcUrl)
                    .schema(dbName)
                    .username(jdbcInfo.getUsername())
                    .password(jdbcInfo.getPassword())
                    .poolConfig(buildPoolConfig())
                    .build();
        }
    };

    /**
     * 处理 JDBC 参数
     * @param jdbcUrl
     * @param params
     * @return
     */
    public static String buildJdbcURLWithParam(String jdbcUrl, Map<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            return jdbcUrl;
        }

        String splicingSymbol = needSplicingSymbol(jdbcUrl) ? "?" : "";
        jdbcUrl = jdbcUrl + splicingSymbol;
        StringBuilder paramBuilder = new StringBuilder();

        params.forEach((key, value) -> {
            paramBuilder.append("&")
                    .append(key)
                    .append("=")
                    .append(value);
        });

        String paramStr = paramBuilder.toString();
        paramStr = paramStr.replaceFirst("&", "");
        jdbcUrl += paramStr;

        return jdbcUrl;
    }

    /**
     * 是否需要jdbc url 的参数拼接符 "?"
     *
     * @param jdbcURL
     * @return
     */
    private static boolean needSplicingSymbol(String jdbcURL) {
        return !jdbcURL.contains("?");
    }


    public static ConsoleSend consoleSend;

    public static EnvironmentContext environmentContext;

    public static void setConsoleSend(ConsoleSend consoleSend, EnvironmentContext environmentContext) {
        Engine2DTOService.consoleSend = consoleSend;
        Engine2DTOService.environmentContext = environmentContext;

    }

    private Integer val;

    Engine2DTOService(Integer val) {
        this.val = val;
    }

    public Integer getVal() {
        return val;
    }

    protected abstract ISourceDTO getSourceDTO(JdbcInfo jdbcInfo, Long dtUicTenantId, Long dtUicUserId, String dbName);

    /**
     * 根据dtUicTenantId、dtUicUserId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param tableType 表类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, ETableType tableType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, tableType);
        DataSourceType dataSourceType = tableTypeTransitionDataSourceType(tableType, jdbcInfo.getVersion(), dtUicTenantId);
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }

    /**
     * 根据dtUicTenantId、dtUicUserId、tableType、dbName获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param jobType  任务类型
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, EJobType jobType, String dbName) {
        JdbcInfo jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, jobType);
        DataSourceType dataSourceType = jobTypeTransitionDataSourceType(jobType, jdbcInfo.getVersion());
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType.getVal());
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }


    /**
     * 根据dtUicTenantId、dtUicUserId、engineType、dbName、jdbcinfo获取对应的sourceDTO，供外部调用
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param dataSourceType 引擎类型 {@link DataSourceType}
     * @return 对应的sourceDTO
     */
    public static ISourceDTO get(Long dtUicTenantId, Long dtUicUserId, Integer dataSourceType, String dbName, JdbcInfo jdbcInfo) {
        Engine2DTOService engine2DTOEnum = getSourceDTOType(dataSourceType);
        return engine2DTOEnum.getSourceDTO(jdbcInfo, dtUicTenantId, dtUicUserId, dbName);
    }

    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param eTableType 表类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long dtUicTenantId, Long dtUicUserId, ETableType eTableType) {
        JdbcInfo jdbcInfo = null;
        if (consoleSend != null && dtUicTenantId != null) {
            if (ETableType.TIDB.equals(eTableType)) {
                jdbcInfo = consoleSend.getTiDBJDBC(dtUicTenantId, dtUicUserId);
            } else if (ETableType.GREENPLUM.equals(eTableType)) {
                jdbcInfo = consoleSend.getGreenplumJDBC(dtUicTenantId, dtUicUserId);
            } else if (ETableType.ORACLE.equals(eTableType)) {
                jdbcInfo = consoleSend.getOracleJDBC(dtUicTenantId, dtUicUserId);
            } else if (ETableType.IMPALA.equals(eTableType)) {
                jdbcInfo = consoleSend.getImpalaJDBC(dtUicTenantId);
            } else if (ETableType.LIBRA.equals(eTableType)) {
                jdbcInfo = consoleSend.getLibraJDBC(dtUicTenantId);
            } else if (ETableType.HIVE.equals(eTableType)) {
                EJobType eJobType = getJobTypeByHadoopMetaType(dtUicTenantId);
                jdbcInfo = getJdbcInfo(dtUicTenantId, dtUicUserId, eJobType);
            } else if (ETableType.ADB_FOR_PG.equals(eTableType)) {
                jdbcInfo = consoleSend.getADBForPGJDBC(dtUicTenantId, dtUicUserId);
            }
        }
        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get jdbc conf from console");
        }
        JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
        return jdbcInfo;
    }


    /**
     * 获取引擎对应的jdbcInfo
     *
     * @param dtUicTenantId uic租户id
     * @param dtUicUserId uic用户id
     * @param eJobType 任务类型
     * @return 数据源连接信息
     */
    public static JdbcInfo getJdbcInfo (Long dtUicTenantId, Long dtUicUserId, EJobType eJobType) {
        JdbcInfo jdbcInfo = null;
        if (consoleSend != null && dtUicTenantId != null) {
            if (EJobType.TIDB_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getTiDBJDBC(dtUicTenantId, dtUicUserId);
            } else if (EJobType.GREENPLUM_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getGreenplumJDBC(dtUicTenantId, dtUicUserId);
            } else if (EJobType.ORACLE_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getOracleJDBC(dtUicTenantId, dtUicUserId);
            } else if (EJobType.IMPALA_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getImpalaJDBC(dtUicTenantId);
            } else if (EJobType.LIBRA_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getLibraJDBC(dtUicTenantId);
            } else if (EJobType.SPARK_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getSparkThrift(dtUicTenantId);
            } else if (EJobType.HIVE_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getHiveServer(dtUicTenantId);
            } else if (EJobType.INCEPTOR_SQL.equals(eJobType)) {
                jdbcInfo = consoleSend.getInceptorSqlJDBC(dtUicTenantId);
            } else if (EJobType.ANALYTICDB_FOR_PG.equals(eJobType)) {
                jdbcInfo = consoleSend.getADBForPGJDBC(dtUicTenantId, dtUicUserId);
            }
        }
        if (jdbcInfo == null) {
            throw new DtCenterDefException("can't get jdbc conf from console");
        }
        JdbcUrlPropertiesValue.setNullPropertiesToDefaultValue(jdbcInfo);
        return jdbcInfo;
    }

    /**
     * 根据枚举值获取对应的枚举类
     * @param val 引擎类型
     * @return {@link Engine2DTOService}
     */
    private static Engine2DTOService getSourceDTOType(Integer val) {
        for (Engine2DTOService engine2DTOEnum : values()) {
            if (engine2DTOEnum.val.equals(val)) {
                return engine2DTOEnum;
            }
        }
        throw new DtCenterDefException("暂时不支持该引擎类型");
    }

    /**
     * 构建连接池 TODO 后续使用common-loader中的连接池、ide中不再维护连接池
     *
     * @return
     */
    public static PoolConfig buildPoolConfig () {

        // 构建连接池
        return PoolConfig.builder()
                .build();
    }

    /**
     * 构建hadoop配置参数
     *
     * @param dtUicUserId
     * @return
     */
    protected String buildHadoopConfig(Long dtUicUserId){
        String config;
        try {
            config = PublicUtil.objectToStr(HadoopConf.getConfiguration(dtUicUserId));
        } catch (IOException e) {
            throw new DtCenterDefException(String.format("hadoop配置转换异常，原因是：%s", e.getMessage()), e);
        }
        return config;
    }

    /**
     * 根据db构建url
     * @param jdbcUrl url
     * @param dbName 数据库
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
     * tableType 转化为datasourceType
     *
     * @param eTableType
     * @param version 小版本信息
     * @param dtUicTenantId
     * @return
     */
    public static DataSourceType tableTypeTransitionDataSourceType(ETableType eTableType, String version, Long dtUicTenantId){
        if (ETableType.HIVE.equals(eTableType)) {
            EJobType eJobType = getJobTypeByHadoopMetaType(dtUicTenantId);
            return jobTypeTransitionDataSourceType(eJobType, version);
        } else if (ETableType.IMPALA.equals(eTableType)) {
            return DataSourceType.IMPALA;
        } else if (ETableType.GREENPLUM.equals(eTableType)) {
            return DataSourceType.GREENPLUM6;
        } else if (ETableType.ORACLE.equals(eTableType)) {
            return DataSourceType.Oracle;
        } else if (ETableType.LIBRA.equals(eTableType)) {
            return DataSourceType.LIBRA;
        } else if (ETableType.TIDB.equals(eTableType)) {
            return DataSourceType.TiDB;
        } else if (ETableType.ADB_FOR_PG.equals(eTableType)) {
            return DataSourceType.ADB_FOR_PG;
        } else {
            throw new RdosDefineException("tableType not transition dataSourceType");
        }
    }

    /**
     * jobType 转化为 对应的dataSourceType
     * 如果没有对应的数据源类型 抛出异常
     *
     * @return
     */
    public static DataSourceType jobTypeTransitionDataSourceType(EJobType eJobType, String version) {
        if (EJobType.HIVE_SQL.equals(eJobType)) {
            if (HiveVersion.HIVE_1x.getVersion().equals(version)) {
                return DataSourceType.HIVE1X;
            } else if (HiveVersion.HIVE_3x.getVersion().equals(version)) {
                return DataSourceType.HIVE3X;
            } else {
                return DataSourceType.HIVE;
            }
        } else if (EJobType.SPARK_SQL.equals(eJobType)) {
            return DataSourceType.SparkThrift2_1;
        } else if (EJobType.IMPALA_SQL.equals(eJobType)) {
            return DataSourceType.IMPALA;
        } else if (EJobType.GREENPLUM_SQL.equals(eJobType)) {
            return DataSourceType.GREENPLUM6;
        } else if (EJobType.ORACLE_SQL.equals(eJobType)) {
            return DataSourceType.Oracle;
        } else if (EJobType.LIBRA_SQL.equals(eJobType)) {
            return DataSourceType.LIBRA;
        } else if (EJobType.TIDB_SQL.equals(eJobType)) {
            return DataSourceType.TiDB;
        } else if (EJobType.INCEPTOR_SQL.equals(eJobType)) {
            return DataSourceType.INCEPTOR;
        } else if (EJobType.ANALYTICDB_FOR_PG.equals(eJobType)) {
            return DataSourceType.ADB_FOR_PG;
        } else {
            throw new RdosDefineException("jobType not transition dataSourceType");
        }
    }

    /**
     * 根据当前租户绑定集群的元数据方式 获取 对应的 JobType
     *
     * @param dtuicTenantId
     * @return
     */
    public static EJobType getJobTypeByHadoopMetaType(Long dtuicTenantId) {
        List<EngineSupportVO> engineSupportVOS = consoleSend.listSupportEngine(dtuicTenantId);
        for (EngineSupportVO engineSupportVO : engineSupportVOS) {
            if (MultiEngineType.HADOOP.getType() == engineSupportVO.getEngineType()) {
                if (EComponentType.HIVE_SERVER.getTypeCode() == engineSupportVO.getMetadataComponent()) {
                    return EJobType.HIVE_SQL;
                }
                if (EComponentType.SPARK_THRIFT.getTypeCode() == engineSupportVO.getMetadataComponent()) {
                    return EJobType.SPARK_SQL;
                }
                if (EComponentType.IMPALA_SQL.getTypeCode() == engineSupportVO.getMetadataComponent()) {
                    return EJobType.IMPALA_SQL;
                }
            }
        }
        throw new RdosDefineException("not find 'Hadoop' meta DataSource!");
    }
}
