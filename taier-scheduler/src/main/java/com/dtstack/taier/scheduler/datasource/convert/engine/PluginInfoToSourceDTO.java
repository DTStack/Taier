/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.scheduler.datasource.convert.engine;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.AdbForPgSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.AwsS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ClickHouseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.CspS3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Db2SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.DorisRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Greenplum6SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hdfs3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HdfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3CDPSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Hive3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.HiveSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ImpalaSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.InceptorSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KingbaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KubernetesSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KylinRestfulSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.LibraSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql5SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Mysql8SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.NfsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OceanBaseSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OdpsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.OracleSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.PostgresqlSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.PrestoSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.RdbmsSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana1SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SapHana2SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SparkSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.SqlserverSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TiDBSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.TrinoSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Yarn2SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.Yarn3SourceDTO;
import com.dtstack.taier.datasource.api.dto.source.YarnSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.dtstack.taier.scheduler.datasource.convert.util.DtMapUtils;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

/**
 * pluginInfo to SourceDTO, 用于 engine 使用
 *
 * @author ：wangchuan
 * date：Created in 下午6:00 2022/3/22
 * company: www.dtstack.com
 */
public enum PluginInfoToSourceDTO {

    /**
     * mysql
     */
    MySQL(DataSourceType.MySQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(Mysql5SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * mysql8
     */
    MySQL8(DataSourceType.MySQL8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(Mysql8SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * oracle
     */
    Oracle(DataSourceType.Oracle.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(OracleSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * sqlserver
     */
    SQLServer(DataSourceType.SQLServer.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(SqlserverSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * postgresql
     */
    PostgreSQL(DataSourceType.PostgreSQL.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(PostgresqlSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * adb for pg
     */
    ADB_FOR_PG(DataSourceType.ADB_FOR_PG.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(AdbForPgSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * OceanBase
     */
    OceanBase(DataSourceType.OceanBase.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(OceanBaseSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * DB2
     */
    DB2(DataSourceType.DB2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(Db2SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * kingbase8
     */
    Kingbase(DataSourceType.KINGBASE8.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(KingbaseSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * hive 1.x
     */
    HIVE1X(DataSourceType.HIVE1X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            Hive1SourceDTO sourceDTO = Hive1SourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * hive 2.x
     */
    HIVE2X(DataSourceType.HIVE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            HiveSourceDTO sourceDTO = HiveSourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();


            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * hive 3.x
     */
    HIVE3X(DataSourceType.HIVE3X.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            Hive3SourceDTO sourceDTO = Hive3SourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * hive 3.x cdp
     */
    HIVE3_CDP(DataSourceType.HIVE3_CDP.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            Hive3CDPSourceDTO sourceDTO = Hive3CDPSourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * inceptor
     */
    INCEPTOR(DataSourceType.INCEPTOR.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            InceptorSourceDTO sourceDTO = InceptorSourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * impala
     */
    IMPALA(DataSourceType.IMPALA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            ImpalaSourceDTO sourceDTO = ImpalaSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * odps(maxCompute)
     */
    MAXCOMPUTE(DataSourceType.MAXCOMPUTE.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return OdpsSourceDTO.builder().config(pluginInfo.toJSONString()).build();
        }
    },

    /**
     * greenplum6
     */
    GREENPLUM6(DataSourceType.GREENPLUM6.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(Greenplum6SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * libra
     */
    LIBRA(DataSourceType.LIBRA.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(LibraSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * HDFS
     */
    HDFS(DataSourceType.HDFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return HdfsSourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
        }
    },

    /**
     * HDFS3
     */
    HDFS3(DataSourceType.HDFS3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return Hdfs3SourceDTO.builder()
                    .defaultFS(getDefaultFS(pluginInfo))
                    .config(getHadoopConfig(pluginInfo)).build();
        }
    },

    /**
     * yarn2
     */
    YARN2(DataSourceType.YARN2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillYarnSourceDTO(Yarn2SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * yarn3
     */
    YARN3(DataSourceType.YARN3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillYarnSourceDTO(Yarn3SourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * yarn3
     */
    KUBERNETES(DataSourceType.KUBERNETES.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return KubernetesSourceDTO.builder()
                    .namespace(pluginInfo.getString(SourceConstant.NAMESPACE))
                    .kubernetesConf(pluginInfo.getJSONObject(SourceConstant.KUBERNETES_CONF).getString(SourceConstant.KUBERNETES_CONF)).build();
        }
    },

    /**
     * tidb
     */
    TiDB(DataSourceType.TiDB.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(TiDBSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * KylinRestful
     */
    KylinRestful(DataSourceType.KylinRestful.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return KylinRestfulSourceDTO.builder()
                    .url(pluginInfo.getString(SourceConstant.KYLIN_RESTFUL_AUTH_URL))
                    .userName(pluginInfo.getString(SourceConstant.USERNAME))
                    .password(pluginInfo.getString(SourceConstant.PASSWORD))
                    .project(pluginInfo.getString(SourceConstant.KYLIN_RESTFUL_PROJECT)).build();
        }
    },

    /**
     * csp S3
     */
    CSP_S3(DataSourceType.CSP_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return CspS3SourceDTO.builder()
                    .accessKey(pluginInfo.getString(SourceConstant.ACCESS_KEY))
                    .secretKey(pluginInfo.getString(SourceConstant.SECRET_KEY))
                    .endPoint(pluginInfo.getString(SourceConstant.ENDPOINT))
                    .build();
        }
    },

    /**
     * aws S3
     */
    AWS_S3(DataSourceType.AWS_S3.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return AwsS3SourceDTO.builder()
                    .accessKey(pluginInfo.getString(SourceConstant.ACCESS_KEY))
                    .secretKey(pluginInfo.getString(SourceConstant.SECRET_KEY))
                    .endPoint(pluginInfo.getString(SourceConstant.ENDPOINT))
                    .build();
        }
    },

    /**
     * nfs
     */
    NFS(DataSourceType.NFS.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return NfsSourceDTO.builder()
                    .server(pluginInfo.getString(SourceConstant.SERVER))
                    .path(pluginInfo.getString(SourceConstant.PATH))
                    .build();
        }
    },

    /**
     * Presto
     */
    Presto(DataSourceType.Presto.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(PrestoSourceDTO.builder().build(), pluginInfo);
        }
    },

    /**
     * Polardb_For_MySQL
     */
    TRINO(DataSourceType.TRINO.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return fillRdbmsSourceDTO(TrinoSourceDTO.builder().build(), pluginInfo);
        }
    },


    /**
     * Spark
     */
    SPARK(DataSourceType.Spark.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            SparkSourceDTO sourceDTO = SparkSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * Spark_thrift
     */
    SPARK_THRIFT(DataSourceType.SparkThrift2_1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            SparkSourceDTO sourceDTO = SparkSourceDTO.builder().build();
            fillRdbmsSourceDTO(sourceDTO, pluginInfo);
            return sourceDTO;
        }
    },

    /**
     * SAP_HANA1
     */
    SAP_HANA1(DataSourceType.SAP_HANA1.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return SapHana1SourceDTO
                    .builder()
                    .url(pluginInfo.getString(SourceConstant.JDBC_URL))
                    .username(pluginInfo.getString(SourceConstant.USERNAME))
                    .password(pluginInfo.getString(SourceConstant.PASSWORD))
                    .build();
        }
    },

    /**
     * SAP_HANA2
     */
    SAP_HANA2(DataSourceType.SAP_HANA2.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return SapHana2SourceDTO
                    .builder()
                    .url(pluginInfo.getString(SourceConstant.JDBC_URL))
                    .username(pluginInfo.getString(SourceConstant.USERNAME))
                    .password(pluginInfo.getString(SourceConstant.PASSWORD))
                    .build();
        }
    },

    CLICK_HOUSE(DataSourceType.Clickhouse.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return ClickHouseSourceDTO
                    .builder()
                    .url(pluginInfo.getString(SourceConstant.JDBC_URL))
                    .username(pluginInfo.getString(SourceConstant.USERNAME))
                    .password(pluginInfo.getString(SourceConstant.PASSWORD))
                    .build();
        }
    },

    DORIS(DataSourceType.DorisRestful.getVal()) {
        @Override
        public ISourceDTO getSourceDTO(JSONObject pluginInfo) {
            return DorisRestfulSourceDTO
                    .builder()
                    .url(pluginInfo.getString(SourceConstant.URL))
                    .userName(pluginInfo.getString(SourceConstant.USERNAME))
                    .password(pluginInfo.getString(SourceConstant.PASSWORD))
                    .build();
        }
    };

    PluginInfoToSourceDTO(Integer val) {
        this.val = val;
    }

    private final Integer val;

    public Integer getVal() {
        return val;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginInfoToSourceDTO.class);

    public abstract ISourceDTO getSourceDTO(JSONObject pluginInfo);

    /**
     * 填充 rdbms 数据源相关参数
     *
     * @param rdbmsSourceDTO 关系型数据源 DTO
     * @param pluginInfo     数据源信息
     * @return 数据源 sourceDTO
     */
    protected RdbmsSourceDTO fillRdbmsSourceDTO(RdbmsSourceDTO rdbmsSourceDTO, JSONObject pluginInfo) {
        rdbmsSourceDTO.setSchema(pluginInfo.getString(SourceConstant.SCHEMA));
        rdbmsSourceDTO.setUrl(pluginInfo.getString(SourceConstant.JDBC_URL));
        String proxyUsername = pluginInfo.getString(SourceConstant.DT_PROXY_USERNAME);
        if (StringUtils.isNotBlank(proxyUsername)) {
            rdbmsSourceDTO.setUsername(proxyUsername);
            rdbmsSourceDTO.setPassword(pluginInfo.getString(SourceConstant.DT_PROXY_PASSWORD));
            // 设置 hive.server2.proxy.user
            setHiveProxyUserName(pluginInfo, rdbmsSourceDTO, proxyUsername);
        } else {
            rdbmsSourceDTO.setUsername(pluginInfo.getString(SourceConstant.USERNAME));
            rdbmsSourceDTO.setPassword(pluginInfo.getString(SourceConstant.PASSWORD));
        }
        setHiveQueue(pluginInfo, rdbmsSourceDTO);
        return rdbmsSourceDTO;
    }

    /**
     * 填充 yarn 数据源相关参数
     *
     * @param yarnSourceDTO yarn数据源 DTO
     * @param pluginInfo    数据源信息
     * @return 数据源 sourceDTO
     */
    protected YarnSourceDTO fillYarnSourceDTO(YarnSourceDTO yarnSourceDTO, JSONObject pluginInfo) {
        yarnSourceDTO.setYarnConf(pluginInfo.getJSONObject(SourceConstant.YARN_CONF_KEY));
        yarnSourceDTO.setHadoopConf(pluginInfo.getJSONObject(SourceConstant.HADOOP_CONF_KEY));
        return yarnSourceDTO;
    }

    /**
     * 获取 defaultFs
     *
     * @param pluginInfo pluginInfo
     * @return defaultFs
     */
    protected String getDefaultFS(JSONObject pluginInfo) {
        JSONObject hadoopConfigJson = getHadoopConfigJson(pluginInfo);
        String defaultFs = hadoopConfigJson.getString(SourceConstant.FS_DEFAULT_FS);
        return StringUtils.isBlank(defaultFs) ? hadoopConfigJson.getString(SourceConstant.DEFAULT_FS) : defaultFs;
    }

    /**
     * 设置 hive queue
     *
     * @param pluginInfo pluginInfo
     * @param sourceDTO  数据源信息
     */
    protected void setHiveQueue(JSONObject pluginInfo, ISourceDTO sourceDTO) {
        String queue = pluginInfo.getString(SourceConstant.QUEUE);
        if (StringUtils.isBlank(queue)) {
            return;
        }
        try {
            Method setQueueMethod = sourceDTO.getClass().getDeclaredMethod("setQueue", String.class);
            setQueueMethod.invoke(sourceDTO, queue);
        } catch (NoSuchMethodException e) {
            LOGGER.debug("no such method: setQueue");
            // ignore error
        } catch (Exception e) {
            LOGGER.error("set hive queue error.", e);
        }
    }


    /**
     * 设置 hive.server2.proxy.user
     *
     * @param rdbmsSourceDTO 数据源信息
     * @param proxyUsername  代理用户名
     */
    private void setHiveProxyUserName(JSONObject pluginInfo, RdbmsSourceDTO rdbmsSourceDTO, String proxyUsername) {
        String jdbcUrl = rdbmsSourceDTO.getUrl();
        boolean hiveProxyEnable = BooleanUtils.toBoolean(pluginInfo.getBoolean(SourceConstant.HIVE_PROXY_ENABLE));
        if (hiveProxyEnable) {
            if (StringUtils.isNotBlank(jdbcUrl) && StringUtils.isNotBlank(proxyUsername)) {
                if (jdbcUrl.endsWith(SourceConstant.SEMICOLON)) {
                    rdbmsSourceDTO.setUrl(jdbcUrl + String.format(SourceConstant.PROXY_USER_FORMAT, proxyUsername));
                } else {
                    rdbmsSourceDTO.setUrl(jdbcUrl + SourceConstant.SEMICOLON + String.format(SourceConstant.PROXY_USER_FORMAT, proxyUsername));
                }
            }

        }

    }

    /**
     * 根据数据源获取对应的sourceDTO，供外调用
     *
     * @param data 数据源信息，json 格式
     * @return datasourceX 需要的 ISourceDTO
     */
    public static ISourceDTO getSourceDTO(String data) {
        JSONObject pluginInfo = getDataSourceJson(data);
        Integer sourceType = pluginInfo.getInteger(SourceConstant.DATASOURCE_TYPE_KEY);
        AssertUtils.notNull(sourceType, "pluginInfo does not contain dataSourceType.");
        PluginInfoToSourceDTO sourceDTOType = getSourceDTOType(sourceType);
        ISourceDTO sourceDTO = sourceDTOType.getSourceDTO(pluginInfo);
        // 填充 kerberos 配置、ssl 配置、sftp 配置
        fillOtherConfig(sourceDTO, pluginInfo);
        return sourceDTO;
    }

    /**
     * 填充其他参数
     *
     * @param sourceDTO  数据源信息
     * @param pluginInfo 配置信息
     */
    private static void fillOtherConfig(ISourceDTO sourceDTO, JSONObject pluginInfo) {
        AbstractSourceDTO abstractSourceDTO = (AbstractSourceDTO) sourceDTO;
        abstractSourceDTO.setSftpConf(DtMapUtils.toStringMap(pluginInfo.getJSONObject(SourceConstant.SFTP_CONF)));
        // 开启 kerberos
        if (Objects.equals(1, pluginInfo.getInteger(SourceConstant.OPEN_KERBEROS))) {
            Map<String, Object> kerberosConf = Maps.newHashMap();
            kerberosConf.put(SourceConstant.PRINCIPAL, pluginInfo.getString(SourceConstant.PRINCIPAL));
            kerberosConf.put(SourceConstant.PRINCIPAL_FILE, pluginInfo.getString(SourceConstant.PRINCIPAL_FILE));
            kerberosConf.put(SourceConstant.KRB5_CONF, pluginInfo.getString(SourceConstant.KRB_NAME));
            kerberosConf.put(SourceConstant.KERBEROS_FILE_TIMESTAMP, pluginInfo.getTimestamp(SourceConstant.KERBEROS_FILE_TIMESTAMP));
            kerberosConf.put(SourceConstant.KERBEROS_REMOTE_PATH, pluginInfo.getString(SourceConstant.KERBEROS_REMOTE_PATH));
            kerberosConf.put(SourceConstant.SFTP_CONF, DtMapUtils.toStringMap(pluginInfo.getJSONObject(SourceConstant.SFTP_CONF)));
            abstractSourceDTO.setKerberosConfig(kerberosConf);
        }
        // ssl 认证传递，优先级 sslClient --> sslConfig
        JSONObject sslInfo = null;
        if (org.apache.commons.collections.MapUtils.isNotEmpty(pluginInfo.getJSONObject(SourceConstant.SSL_CLIENT))) {
            sslInfo = pluginInfo.getJSONObject(SourceConstant.SSL_CLIENT);
        } else if (org.apache.commons.collections.MapUtils.isNotEmpty(pluginInfo.getJSONObject(SourceConstant.SSL_CONFIG))) {
            // 数据源中心的 ssl 认证信息
            sslInfo = pluginInfo.getJSONObject(SourceConstant.SSL_CONFIG);
        }
        if (sslInfo != null) {
            SSLConfig sslConfig = SSLConfig.builder()
                    .sslFileTimestamp((Timestamp) sslInfo.getTimestamp(SourceConstant.SSL_FILE_TIMESTAMP))
                    .remoteSSLDir(sslInfo.getString(SourceConstant.REMOTE_SSL_DIR))
                    .sslClientConf(sslInfo.getString(SourceConstant.SSL_CLIENT_CONF)).build();
            abstractSourceDTO.setSslConfig(sslConfig);
        }
    }

    /**
     * 解析 pluginInfo 参数
     *
     * @param pluginInfoStr pluginInfo 字符串
     * @return pluginInfo jsonObject 格式
     */
    private static JSONObject getDataSourceJson(String pluginInfoStr) {
        if (StringUtils.isBlank(pluginInfoStr)) {
            throw new SourceException("数据源信息为空");
        }
        try {
            return JSONObject.parseObject(pluginInfoStr);
        } catch (Exception e) {
            throw new SourceException(String.format("转化json格式异常：%s", e.getMessage()), e);
        }
    }

    /**
     * 根据枚举值获取数据源类型
     *
     * @param sourceType 数据源类型
     * @return SourceDTOType
     */
    private static PluginInfoToSourceDTO getSourceDTOType(Integer sourceType) {
        for (PluginInfoToSourceDTO pluginInfoToSourceDTO : values()) {
            if (pluginInfoToSourceDTO.getVal().equals(sourceType)) {
                return pluginInfoToSourceDTO;
            }
        }
        throw new SourceException(String.format("找不到对应的数据源类型, sourceType：%s", sourceType));
    }

    /**
     * 获得pluginInfo的xml配置, 合并 yarnConf 和 hdfsConf
     */
    private static String getHadoopConfig(JSONObject pluginInfo) {
        return getHadoopConfigJson(pluginInfo).toJSONString();
    }

    /**
     * 获得pluginInfo的xml配置, 合并 yarnConf 和 hdfsConf
     *
     * @param pluginInfo 配置信息
     * @return hdfs & yarn conf
     */
    private static JSONObject getHadoopConfigJson(JSONObject pluginInfo) {
        JSONObject allConf = new JSONObject();
        JSONObject yarnConf = pluginInfo.getJSONObject(SourceConstant.YARN_CONF_KEY);
        if (yarnConf != null) {
            allConf.putAll(yarnConf);
        }
        JSONObject hadoopConf = pluginInfo.getJSONObject(SourceConstant.HADOOP_CONF_KEY);
        if (hadoopConf != null) {
            allConf.putAll(hadoopConf);
        }
        allConf.putAll(pluginInfo);
        return allConf;
    }
}