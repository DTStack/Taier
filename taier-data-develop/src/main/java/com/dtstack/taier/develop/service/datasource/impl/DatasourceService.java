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

package com.dtstack.taier.develop.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.base.ClientCache;
import com.dtstack.taier.datasource.api.client.IClient;
import com.dtstack.taier.datasource.api.client.IKerberos;
import com.dtstack.taier.datasource.api.constant.KerberosConstants;
import com.dtstack.taier.datasource.api.dto.ColumnMetaDTO;
import com.dtstack.taier.datasource.api.dto.SqlQueryDTO;
import com.dtstack.taier.datasource.api.dto.Table;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.source.DataSourceType;
import com.dtstack.taier.datasource.api.utils.DBUtil;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.enums.DataSourceTypeEnum;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.PubSvcDefineException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.common.util.JsonUtils;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.common.util.SqlFormatUtil;
import com.dtstack.taier.common.util.Strings;
import com.dtstack.taier.dao.domain.DevelopDataSource;
import com.dtstack.taier.dao.domain.DsFormField;
import com.dtstack.taier.dao.domain.DsInfo;
import com.dtstack.taier.develop.datasource.convert.load.SourceLoaderService;
import com.dtstack.taier.develop.dto.devlop.DataSourceVO;
import com.dtstack.taier.develop.enums.develop.RDBMSSourceType;
import com.dtstack.taier.develop.enums.develop.TableLocationType;
import com.dtstack.taier.develop.enums.develop.TaskCreateModelType;
import com.dtstack.taier.develop.service.template.hive.HiveWriterFormat;
import com.dtstack.taier.develop.sql.formate.SqlFormatter;
import com.dtstack.taier.develop.utils.develop.sync.format.ColumnType;
import com.dtstack.taier.develop.utils.develop.sync.format.TypeFormat;
import com.dtstack.taier.develop.utils.develop.sync.format.writer.OracleWriterFormat;
import com.dtstack.taier.develop.utils.develop.sync.format.writer.PostgreSqlWriterFormat;
import com.dtstack.taier.develop.utils.develop.sync.job.PluginName;
import com.dtstack.taier.develop.utils.develop.sync.util.ADBForPGUtil;
import com.dtstack.taier.develop.utils.develop.sync.util.CreateTableSqlParseUtil;
import com.dtstack.taier.develop.utils.develop.sync.util.OracleSqlFormatUtil;
import com.dtstack.taier.pluginapi.util.DtStringUtil;
import com.dtstack.taier.scheduler.service.ClusterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.JAVA_SECURITY_KRB5_CONF;

/**
 * 有关数据源中心
 * <p>
 * description: 数据源相关操作
 *
 * @author liu
 * date: 2021/3/16
 */
@Service
public class DatasourceService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DatasourceService.class);

    @Autowired
    private KerberosService kerberosService;

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsFormFieldService formFieldService;

    @Autowired
    private DsTypeService typeService;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private SourceLoaderService sourceLoaderService;


    private static final String IS_OPEN_CDB = "select * from v$database";
    public static final String JDBC_URL = "jdbcUrl";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    public static final String JDBC_HOSTPORTS = "hostPorts";

    public static final String HDFS_DEFAULTFS = "defaultFS";

    public static final String HADOOP_CONFIG = "hadoopConfig";

    public static String HIVE_METASTORE_URIS = "hiveMetastoreUris";

    private static final String HBASE_CONFIG = "hbaseConfig";

    private static final String TYPE = "type";

    private static final List<String> MYSQL_NUMBERS = Lists.newArrayList("TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT", "INT UNSIGNED");

    private static final List<String> CLICKHOUSE_NUMBERS = Lists.newArrayList("UINT8", "UINT16", "UINT32", "UINT64", "INT8", "INT16", "INT32", "INT64");

    private static final List<String> ORACLE_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "NUMBER");

    private static final List<String> SQLSERVER_NUMBERS = Lists.newArrayList("INT", "INTEGER", "SMALLINT", "TINYINT", "BIGINT");

    private static final List<String> POSTGRESQL_NUMBERS = Lists.newArrayList("INT2", "INT4", "INT8", "SMALLINT", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL");

    private static final List<String> DB2_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> GBASE_NUMBERS = Lists.newArrayList("SMALLINT", "TINYINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "NUMERIC");

    private static final List<String> DMDB_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "BIGINT", "NUMBER");

    private static final List<String> GREENPLUM_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> KINGBASE_NUMBERS = Lists.newArrayList("BIGINT", "DOUBLE", "FLOAT", "INT4", "INT8", "FLOAT", "FLOAT8", "NUMERIC");

    private static final List<String> INFLUXDB_NUMBERS = Lists.newArrayList("INTEGER");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("NUMBER\\(\\d+\\)");

    private static final Pattern NUMBER_PATTERN2 = Pattern.compile("NUMBER\\((\\d+),([\\d-]+)\\)");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final TypeFormat TYPE_FORMAT = new HiveWriterFormat();

    public static final String IS_HADOOP_AUTHORIZATION = "hadoop.security.authorization";

    public static final String HADOOP_AUTH_TYPE = "hadoop.security.authentication";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    /**
     * ssl 认证文件路径
     */
    private static final String KEY_PATH = "keyPath";

    /**
     * ssl 认证文件路径
     */
    private static final String SSL_LOCAL_DIR = "sslLocalDir";

    // 底层是postgresql的类型
    private static final Set<Integer> CREATE_TABLE_TO_PG_TABLE = Sets.newHashSet(DataSourceType.LIBRA.getVal(), DataSourceType.GREENPLUM6.getVal(),
            DataSourceType.PostgreSQL.getVal());
    private static final List ORIGIN_TABLE_ALLOW_TYPES = new ArrayList();

    // 一键生成目标表，不同数据源表名处理正则，像"a"."b" 、 `a`.`b` 、 'a'.'b' 、 [a].[b]
    private static final String TABLE_FORMAT_REGEX = "`|'|\"|\\[|\\]";

    // 关系型数据库建表模版
    private static final String RDB_CREATE_TABLE_SQL_TEMPLATE = "create table %s ( %s );";

    private final String SEMICOLON = ";";

    static {
        ORIGIN_TABLE_ALLOW_TYPES.addAll(RDBMSSourceType.getRDBMS());
        ORIGIN_TABLE_ALLOW_TYPES.add(DataSourceType.MAXCOMPUTE.getVal());
    }

    // 支持一键建表的数据源类型
    private static final Set<DataSourceType> SUPPORT_CREATE_TABLE_DATASOURCES = Sets.newHashSet(DataSourceType.HIVE, DataSourceType.SparkThrift2_1,
            DataSourceType.LIBRA, DataSourceType.PostgreSQL, DataSourceType.HIVE1X, DataSourceType.HIVE3X, DataSourceType.IMPALA,
            DataSourceType.TiDB, DataSourceType.Oracle, DataSourceType.GREENPLUM6, DataSourceType.MySQL, DataSourceType.ADB_FOR_PG);

    // 支持一键建表的数据源类型名称
    private static final String SUPPORT_CREATE_TABLE_DATASOURCES_NAMES = SUPPORT_CREATE_TABLE_DATASOURCES.stream().map(DataSourceType::getName).collect(Collectors.joining("、"));

    /**
     * 解析 kerberos 文件获取 principal 列表
     *
     * @param source   数据源信息
     * @param resource 资源信息
     * @param userId   用户 id
     * @return principal 集合
     */
    public List<String> getPrincipalsWithConf(DataSourceVO source, Pair<String, String> resource, Long userId) {
        Map<String, Object> kerberosConfig = buildKerberosConfig(source, resource, userId);
        IKerberos kerberos = ClientCache.getKerberos(DataSourceType.KERBEROS.getVal());
        return kerberos.getPrincipals(kerberosConfig);
    }

    /**
     * 构建 kerberos config
     *
     * @param source   数据源信息
     * @param resource 上传的资源信息
     * @param userId   用户 id
     * @return kerberos config
     */
    public Map<String, Object> buildKerberosConfig(DataSourceVO source, Pair<String, String> resource, Long userId) {
        Map<String, Object> kerberosConfig;
        IKerberos kerberos = ClientCache.getKerberos(DataSourceType.KERBEROS.getVal());
        if (Objects.nonNull(resource)) {
            String localKerberosPath = kerberosService.getTempLocalKerberosConf(userId);
            try {
                // 解析Zip文件获取配置对象, 不设置 sftp 配置, 因为此时一定是本地路径, 不需要从 sftp 下载
                kerberosConfig = kerberos.parseKerberosFromUpload(resource.getRight(), localKerberosPath);
            } catch (IOException e) {
                throw new PubSvcDefineException(String.format("kerberos 配置解析异常: %s", e.getMessage()), e);
            }
        } else {
            kerberosConfig = ((AbstractSourceDTO) sourceLoaderService.buildSourceDTO(source.getId())).getKerberosConfig();
        }
        return kerberosConfig;
    }

    /**
     * 检测 kerberos 认证的数据源连通性
     *
     * @param source   数据源信息
     * @param resource 上传的资源信息
     * @param userId   用户 id
     * @return 连通性测试结果
     */
    public Boolean checkConnectionWithKerberos(DataSourceVO source, Pair<String, String> resource, Long userId) {
        Map<String, Object> kerberosConfig = buildKerberosConfig(source, resource, userId);
        try {
            source.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        } catch (Exception e) {
            LOGGER.error("check datasource error", e);
            throw new PubSvcDefineException("JSONObject 转化异常", e);
        }
        // 设置前台传入的principals
        setPrincipals(source.getDataJson(), kerberosConfig);
        return checkConnectionWithConf(source, kerberosConfig);
    }

    /**
     * 数据源连通性测试
     *
     * @param source         数据源配置
     * @param kerberosConfig kerberos 配置
     * @return 检测结果
     */
    public Boolean checkConnectionWithConf(DataSourceVO source, Map<String, Object> kerberosConfig) {
        ISourceDTO sourceDTOConn = sourceLoaderService.buildSourceDTO(source, kerberosConfig);
        return ClientCache.getClient(sourceDTOConn.getSourceType()).testCon(sourceDTOConn);
    }

    public Boolean checkConnectionWithConf(DataSourceVO source, Map<String, Object> kerberosConfig, Consumer<String> schemaConsumer) {
        ISourceDTO sourceDTOConn = sourceLoaderService.buildSourceDTO(source, kerberosConfig);
        IClient client = ClientCache.getClient(sourceDTOConn.getSourceType());
        Boolean testResult = client.testCon(sourceDTOConn);
        if (testResult && DataSourceType.GET_SCHEMA.contains(sourceDTOConn.getSourceType())) {
            String currentDatabase = client.getCurrentDatabase(sourceDTOConn);
            schemaConsumer.accept(currentDatabase);
        }
        return testResult;
    }

    /**
     * 设置前台传入的principals
     *
     * @param dataJson       data json
     * @param kerberosConfig kerberos config
     */
    public void setPrincipals(JSONObject dataJson, Map<String, Object> kerberosConfig) {
        if (Objects.isNull(kerberosConfig)) {
            return;
        }
        // principal
        String principal = dataJson.getString(FormNames.PRINCIPAL);
        if (Strings.isNotBlank(principal)) {
            kerberosConfig.put(KerberosConstants.PRINCIPAL, principal);
        }
        // Hbase master kerberos Principal
        String hbaseMasterPrincipal = dataJson.getString(FormNames.HBASE_MASTER_PRINCIPAL);
        if (Strings.isNotBlank(hbaseMasterPrincipal)) {
            kerberosConfig.put(KerberosConstants.HBASE_MASTER_PRINCIPAL, hbaseMasterPrincipal);
        }
        // Hbase region kerberos Principal
        String hbaseRegionServerPrincipal = dataJson.getString(FormNames.HBASE_REGION_PRINCIPAL);
        if (Strings.isNotBlank(hbaseRegionServerPrincipal)) {
            kerberosConfig.put(KerberosConstants.HBASE_REGION_PRINCIPAL, hbaseRegionServerPrincipal);
        }
    }

    /**
     * 上传Kerberos添加和修改数据源
     *
     * @param dataSourceVO 数据源信息
     * @param resource     资源信息
     * @param userId       用户 id
     * @param tenantId     租户 id
     * @return 添加后的数据源 id
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unchecked")
    public Long addOrUpdateSourceWithKerberos(DataSourceVO dataSourceVO, Pair<String, String> resource, Long userId, Long tenantId) {
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
        dataSourceVO.setDataJson(dataJson);
        List<Integer> list = JSON.parseObject(dataSourceVO.getAppTypeListString(), List.class);
        dataSourceVO.setAppTypeList(list);
        Map<String, Object> kerberosConfig = buildKerberosConfig(dataSourceVO, resource, userId);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            //设置openKerberos变量表示开启kerberos认证
            DataSourceUtils.setOpenKerberos(dataJson, true);
            DataSourceUtils.setKerberosFile(dataJson, resource.getRight());
        }

        // 设置前台传入的principals
        setPrincipals(dataJson, kerberosConfig);
        // 检查链接
        Boolean connValue = checkConnectionWithConf(dataSourceVO, kerberosConfig);
        if (BooleanUtils.isFalse(connValue)) {
            throw new PubSvcDefineException("不能添加连接失败的数据源", ErrorCode.CONFIG_ERROR);
        }
        Long dataSourceId = dataSourceVO.getId();
        String localKerberosDir = MapUtils.getString(kerberosConfig, KerberosConstants.LOCAL_KERBEROS_DIR);
        IKerberos kerberos = ClientCache.getKerberos(DataSourceType.KERBEROS.getVal());
        // 更改路径为相对路径, 用于保存数据库
        kerberos.changeToRelativePath(kerberosConfig);
        //有认证文件上传 进行上传至sftp操作
        Long id;
        try {
            if (Objects.nonNull(resource)) {
                if (dataSourceVO.getId() == 0L) {
                    // 没有保存过的数据源需要先保存, 获取自增id
                    dataSourceVO.setKerberosConfig(kerberosConfig);
                    dataSourceVO.setDataJson((JSONObject) dataJson.clone());
                    dataSourceId = addOrUpdate(dataSourceVO, userId);
                }
                Map<String, String> sftpMap = kerberosService.getSftpMap(tenantId);
                //目录转换 - 将临时目录根据数据源ID转移到新的kerberos文件目录
                File localKerberosConfDir = new File(localKerberosDir);
                File newConfDir = new File(kerberosService.getLocalKerberosPath(dataSourceId));
                //如果原来的目录存在 删除原来的文件
                try {
                    FileUtils.deleteDirectory(newConfDir);
                } catch (IOException e) {
                    LOGGER.error("delete old kerberos file error", e);
                }
                // 目录转换, temp路径转换成新路径
                localKerberosConfDir.renameTo(newConfDir);
                // 上传配置文件到sftp
                String dataSourceKey = kerberosService.getSourceKey(dataSourceId, null);
                KerberosService.uploadDirFinal(sftpMap, newConfDir.getPath(), dataSourceKey);
                kerberosConfig.put("kerberosDir", dataSourceKey);
            }
            dataSourceVO.setKerberosConfig(kerberosConfig);
            dataSourceVO.setDataJson(dataJson);
            dataSourceVO.setId(dataSourceId);
            id = addOrUpdate(dataSourceVO, userId);
        } catch (Exception e) {
            LOGGER.error("addOrUpdateSourceWithKerberos error", e);
            throw new PubSvcDefineException(e.getMessage());
        }
        return id;
    }

    /**
     * 添加和修改数据源
     *
     * @param dataSourceVO 数据源信息
     * @param userId       用户 id
     * @return 数据源 id
     */
    public Long addOrUpdateSource(DataSourceVO dataSourceVO, Long userId) {
        if (!checkConnectionWithConf(dataSourceVO, null, dataSourceVO::setSchemaName)) {
            throw new PubSvcDefineException("不能添加连接失败的数据源" + ErrorCode.CONFIG_ERROR);
        }
        return addOrUpdate(dataSourceVO, userId);
    }

    /**
     * 添加和修改数据源信息
     *
     * @param dataSourceVO 数据源信息
     * @param userId       用户 id
     * @return 添加或修改后数据源 id
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdate(DataSourceVO dataSourceVO, Long userId) {
        //字段转换
        colMap(dataSourceVO);
        dataSourceVO.setModifyUserId(userId);
        // 构造数据源元数据
        DsInfo dsInfo = buildDsInfo(dataSourceVO);
        if (dataSourceVO.getId() > 0) {
            // edit 不存在授权操作
            dsInfoService.getOneById(dataSourceVO.getId());
            dsInfo.setId(dataSourceVO.getId());
            dsInfo.setModifyUserId(dataSourceVO.getUserId());
            if (dsInfoService.checkDataNameDup(dsInfo)) {
                throw new PubSvcDefineException(ErrorCode.DATASOURCE_DUP_NAME);
            }
            dsInfo.setGmtCreate(DateTime.now().toDate());
            dsInfoService.updateById(dsInfo);
        } else {
            // add 存在授权产品操作
            dsInfo.setCreateUserId(dataSourceVO.getUserId());
            dsInfo.setModifyUserId(dataSourceVO.getUserId());
            if (dsInfoService.checkDataNameDup(dsInfo)) {
                throw new PubSvcDefineException(ErrorCode.DATASOURCE_DUP_NAME);
            }
            dsInfoService.save(dsInfo);
            // 保存数据源类型权重
            typeService.plusDataTypeWeight(dsInfo.getDataType(), 1);
        }
        return dsInfo.getId();
    }


    /**
     * 构建数据源元数据对象
     *
     * @param dataSourceVO 数据源信息
     * @return 书数据源数据对象
     */
    private DsInfo buildDsInfo(DataSourceVO dataSourceVO) {
        DsInfo dsInfo = new DsInfo();
        dsInfo.setDataType(dataSourceVO.getDataType());
        dsInfo.setDataVersion(dataSourceVO.getDataVersion());
        dsInfo.setDataName(dataSourceVO.getDataName());
        dsInfo.setDataDesc(dataSourceVO.getDataDesc());
        dsInfo.setStatus(1);
        dsInfo.setIsMeta(dataSourceVO.getIsMeta());
        dsInfo.setTenantId(dataSourceVO.getTenantId());
        dsInfo.setSchemaName(dataSourceVO.getSchemaName());
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        dsInfo.setDataTypeCode(typeEnum.getVal());
        // dataJson
        if (Objects.nonNull(dataSourceVO.getDataJson())) {
            JSONObject dataJson = dataSourceVO.getDataJson();
            if (dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())) {
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_ZK_QUORUM, dataJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT, dataJson.get(FormNames.HBASE_PARENT));
                dataJson.put("hbaseConfig", jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos = dataJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG)) != null) {
                dataJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP, kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP, System.currentTimeMillis()));
            }
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataJson, true));
            String linkInfo = getDataSourceLinkInfo(dataSourceVO.getDataType(), dataSourceVO.getDataVersion(), dataSourceVO.getDataJson());
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        } else if (Strings.isNotBlank(dataSourceVO.getDataJsonString())) {
            JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
            if (dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())) {
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_QUORUM, dataSourceJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT, dataSourceJson.get(FormNames.HBASE_PARENT));
                dataSourceJson.put("hbaseConfig", jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos = dataSourceJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG)) != null) {
                dataSourceJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP, kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP, System.currentTimeMillis()));
            }
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataSourceJson, true));
            //获取连接信息
            String linkInfo = getDataSourceLinkInfo(dataSourceVO.getDataType(), dataSourceVO.getDataVersion(), dataSourceJson);
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        } else {
            throw new PubSvcDefineException(ErrorCode.DATASOURCE_CONF_ERROR);
        }
        return dsInfo;
    }


    /**
     * 根据数据源版本获取对应的连接信息
     *
     * @param dataType    数据源类型
     * @param dataVersion 数据源版本
     * @param dataJson    data json
     * @return 数据源连接信息
     */
    public String getDataSourceLinkInfo(String dataType, String dataVersion, JSONObject dataJson) {
        List<DsFormField> linkFieldList = formFieldService.findLinkFieldByTypeVersion(dataType, dataVersion);
        if (CollectionUtils.isEmpty(linkFieldList)) {
            return null;
        }
        JSONObject linkJson = new JSONObject();
        for (DsFormField dsFormField : linkFieldList) {
            String value = JsonUtils.getStrFromJson(dataJson, dsFormField.getName());
            if (Strings.isNotBlank(value)) {
                linkJson.put(dsFormField.getName(), value);
            }
        }
        return linkJson.toJSONString();
    }

    /**
     * 替换字段, 此时 dataSourceVO 中的 dataJson 已经存在 kerberosConfig, 并且路径也已经是相对路径
     *
     * @param dataSourceVO datasource vo
     */
    private void colMap(DataSourceVO dataSourceVO) {
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(dataSourceVO, null);
        if (DataSourceType.getKafkaS().contains(sourceDTO.getSourceType())) {
            String brokersAddress;
            try {
                brokersAddress = ClientCache.getKafka(sourceDTO.getSourceType()).getAllBrokersAddress(sourceDTO);
            } catch (Exception e) {
                throw new PubSvcDefineException("获取 kafka brokersAddress 异常", e);
            }
            dataSourceVO.getDataJson().put("bootstrapServers", brokersAddress);
        }

        if (MapUtils.isNotEmpty(dataSourceVO.getKerberosConfig())) {
            dataSourceVO.getDataJson().put(FormNames.KERBEROS_CONFIG, dataSourceVO.getKerberosConfig());
        }
    }

    public String setJobDataSourceInfo(String jobStr, Long tenantId, Integer createModel) {
        JSONObject job = JSONObject.parseObject(jobStr);
        JSONObject jobContent = job.getJSONObject("job");
        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
        setPluginDataSourceInfo(content.getJSONObject("reader"), tenantId, createModel);
        setPluginDataSourceInfo(content.getJSONObject("writer"), tenantId, createModel);
        return job.toJSONString();
    }


    public void setPluginDataSourceInfo(JSONObject plugin, Long tenantId, Integer createModel) {
        String pluginName = plugin.getString("name");
        JSONObject param = plugin.getJSONObject("parameter");
        if (PluginName.MySQLD_R.equals(pluginName)) {
            JSONArray connections = param.getJSONArray("connection");
            for (int i = 0; i < connections.size(); i++) {
                JSONObject conn = connections.getJSONObject(i);
                if (!conn.containsKey("sourceId")) {
                    continue;
                }

                DevelopDataSource source = getOne(conn.getLong("sourceId"));
                JSONObject json = JSONObject.parseObject(source.getDataJson());
                replaceDataSourceInfoByCreateModel(conn, "username", JsonUtils.getStrFromJson(json, JDBC_USERNAME), createModel);
                replaceDataSourceInfoByCreateModel(conn, "password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD), createModel);
                replaceDataSourceInfoByCreateModel(conn, "jdbcUrl", Collections.singletonList(JsonUtils.getStrFromJson(json, JDBC_URL)), createModel);
            }
        } else {
            if (!param.containsKey("sourceIds")) {
                return;
            }

            List<Long> sourceIds = param.getJSONArray("sourceIds").toJavaList(Long.class);
            if (CollectionUtils.isEmpty(sourceIds)) {
                return;
            }

            DevelopDataSource source = getOne(sourceIds.get(0));

            JSONObject json = JSON.parseObject(source.getDataJson());
            Integer sourceType = source.getType();

            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                    && !DataSourceType.HIVE.getVal().equals(sourceType)
                    && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                    && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                    && !DataSourceType.IMPALA.getVal().equals(sourceType)
                    && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)
                    && !DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "username", JsonUtils.getStrFromJson(json, JDBC_USERNAME), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD), createModel);
                JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                if (conn.get("jdbcUrl") instanceof String) {
                    replaceDataSourceInfoByCreateModel(conn, "jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL), createModel);
                } else {
                    replaceDataSourceInfoByCreateModel(conn, "jdbcUrl", Arrays.asList(JsonUtils.getStrFromJson(json, JDBC_URL)), createModel);
                }
            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HDFS.getVal().equals(sourceType)
                    || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn, JDBC_URL, JsonUtils.getStrFromJson(json, JDBC_URL), createModel);
                    }
                }
                //非meta数据源从高可用配置中取hadoopConf
                if (0 == source.getIsDefault()) {
                    replaceDataSourceInfoByCreateModel(param, "defaultFS", JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS).trim(), createModel);
                    String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
                    if (StringUtils.isNotBlank(hadoopConfig)) {
                        replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(hadoopConfig), createModel);
                    }
                    setSftpConfig(source.getId(), json, tenantId, param, HADOOP_CONFIG);
                } else {
                    //meta数据源从console取配置
                    //拿取最新配置
                    String consoleHadoopConfig = this.getConsoleHadoopConfig(tenantId);
                    if (StringUtils.isNotBlank(consoleHadoopConfig)) {
                        //替换新path 页面运行fix
                        JSONArray connections = param.getJSONArray("connection");
                        if ((DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) && Objects.nonNull(connections)) {
                            JSONObject conn = connections.getJSONObject(0);
                            String hiveTable = conn.getJSONArray("table").get(0).toString();
                            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(source);
                            String hiveTablePath = ClientCache
                                    .getClient(sourceDTO.getSourceType())
                                    .getTable(sourceDTO, SqlQueryDTO.builder().tableName(hiveTable).build())
                                    .getPath();
                            if (StringUtils.isNotEmpty(hiveTablePath)) {
                                replaceDataSourceInfoByCreateModel(param, "path", hiveTablePath.trim(), createModel);
                            }
                        }
                        replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(consoleHadoopConfig), createModel);
                        JSONObject hadoopConfJson = JSONObject.parseObject(consoleHadoopConfig);
                        String defaultFs = JsonUtils.getStrFromJson(hadoopConfJson, "fs.defaultFS");
                        //替换defaultFs
                        replaceDataSourceInfoByCreateModel(param, "defaultFS", defaultFs.trim(), createModel);
                    } else {
                        String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
                        if (StringUtils.isNotBlank(hadoopConfig)) {
                            replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(hadoopConfig), createModel);
                        }
                    }
                    setDefaultHadoopSftpConfig(json, tenantId, param);
                }
            } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
                String jsonStr = json.getString(HBASE_CONFIG);
                Map jsonMap = new HashMap();
                if (StringUtils.isNotEmpty(jsonStr)) {
                    try {
                        jsonMap = objectMapper.readValue(jsonStr, Map.class);
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
                replaceDataSourceInfoByCreateModel(param, HBASE_CONFIG, jsonMap, createModel);
                if (TaskCreateModelType.GUIDE.getType().equals(createModel)) {
                    setSftpConfig(source.getId(), json, tenantId, param, HBASE_CONFIG);
                }
            } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
                if (json != null) {
                    json.entrySet().forEach(bean -> {
                        replaceDataSourceInfoByCreateModel(param, bean.getKey(), bean.getValue(), createModel);
                    });
                }
            } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "accessId", json.get("accessId"), createModel);
                replaceDataSourceInfoByCreateModel(param, "accessKey", json.get("accessKey"), createModel);
                replaceDataSourceInfoByCreateModel(param, "project", json.get("project"), createModel);
                replaceDataSourceInfoByCreateModel(param, "endPoint", json.get("endPoint"), createModel);
            } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
                replaceDataSourceInfoByCreateModel(param, "address", json.get("address"), createModel);
            } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "hostPort", JsonUtils.getStrFromJson(json, "hostPort"), createModel);
                replaceDataSourceInfoByCreateModel(param, "database", json.getIntValue("database"), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtils.getStrFromJson(json, "password"), createModel);
            } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, JDBC_HOSTPORTS, JsonUtils.getStrFromJson(json, JDBC_HOSTPORTS), createModel);
                replaceDataSourceInfoByCreateModel(param, "username", JsonUtils.getStrFromJson(json, "username"), createModel);
                replaceDataSourceInfoByCreateModel(param, "database", JsonUtils.getStrFromJson(json, "database"), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtils.getStrFromJson(json, "password"), createModel);
            } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "masterAddresses", JsonUtils.getStrFromJson(json, JDBC_HOSTPORTS), createModel);
                replaceDataSourceInfoByCreateModel(param, "others", JsonUtils.getStrFromJson(json, "others"), createModel);
            } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                String tableLocation = param.getString(TableLocationType.key());
                replaceDataSourceInfoByCreateModel(param, "dataSourceType", DataSourceType.IMPALA.getVal(), createModel);
                String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
                if (StringUtils.isNotBlank(hadoopConfig)) {
                    replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(hadoopConfig), createModel);
                }
                if (TableLocationType.HIVE.getValue().equals(tableLocation)) {
                    replaceDataSourceInfoByCreateModel(param, "username", JsonUtils.getStrFromJson(json, JDBC_USERNAME), createModel);
                    replaceDataSourceInfoByCreateModel(param, "password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD), createModel);
                    replaceDataSourceInfoByCreateModel(param, "defaultFS", JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS), createModel);
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn, "jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL), createModel);
                    }
                }
            } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceInceptorDataSource(param, json, createModel, source, tenantId);
            } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "username", JsonUtils.getStrFromJson(json, "username"), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtils.getStrFromJson(json, "password"), createModel);
                if (param.containsKey("connection")) {
                    JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                    String url = JsonUtils.getStrFromJson(json, "url");
                    replaceDataSourceInfoByCreateModel(conn, "url", Lists.newArrayList(url), createModel);
                    replaceDataSourceInfoByCreateModel(conn, "measurement", conn.getJSONArray("table"), createModel);
                    replaceDataSourceInfoByCreateModel(conn, "database", conn.getString("schema"), createModel);

                }
            }
        }
    }


    /**
     * 根据模式 判断是否要覆盖数据源信息
     * 脚本模式 空缺了再覆盖  向导模式 默认覆盖
     */
    private void replaceDataSourceInfoByCreateModel(JSONObject jdbcInfo, String key, Object values, Integer createModel) {
        Boolean isReplace = TaskCreateModelType.TEMPLATE.getType().equals(createModel) && jdbcInfo.containsKey(key);
        if (isReplace) {
            return;
        }
        jdbcInfo.put(key, values);
    }


    /**
     * 替换Inceptor 相关的数据源信息
     *
     * @param param
     * @param json
     * @param createModel
     * @param source
     * @param tenantId
     */
    public void replaceInceptorDataSource(JSONObject param, JSONObject json, Integer createModel, DevelopDataSource source,
                                          Long tenantId) {
        if (param.containsKey("connection")) {
            JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
            replaceDataSourceInfoByCreateModel(conn, "jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL), createModel);
        }

        replaceDataSourceInfoByCreateModel(param, HDFS_DEFAULTFS, JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS), createModel);
        replaceDataSourceInfoByCreateModel(param, HIVE_METASTORE_URIS, JsonUtils.getStrFromJson(json, HIVE_METASTORE_URIS), createModel);
        String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
        JSONObject hadoopConfigJson = new JSONObject();
        if (StringUtils.isNotBlank(hadoopConfig)) {
            hadoopConfigJson.putAll(JSONObject.parseObject(hadoopConfig));
        }
        hadoopConfigJson.put(HIVE_METASTORE_URIS, JsonUtils.getStrFromJson(json, HIVE_METASTORE_URIS));
        replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, hadoopConfigJson, createModel);

        // 替换表相关的信息
        JSONArray connections = param.getJSONArray("connection");
        JSONObject conn = connections.getJSONObject(0);
        String hiveTableName = conn.getJSONArray("table").get(0).toString();
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(source);
        Table tableInfo = ClientCache.getClient(sourceDTO.getSourceType())
                .getTable(sourceDTO, SqlQueryDTO.builder().tableName(hiveTableName).build());
        replaceDataSourceInfoByCreateModel(param, "path", tableInfo.getPath().trim(), createModel);
        replaceDataSourceInfoByCreateModel(param, "schema", tableInfo.getDb(), createModel);
        replaceDataSourceInfoByCreateModel(param, "table", hiveTableName, createModel);
        replaceDataSourceInfoByCreateModel(param, "isTransaction", tableInfo.getIsTransTable(), createModel);

        setSftpConfig(source.getId(), json, tenantId, param, HADOOP_CONFIG);
    }

    public void setDefaultHadoopSftpConfig(JSONObject json, Long tenantId, Map<String, Object> map) {
        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
        String remoteDir = "";

        JSONObject hdfs = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
        if (Objects.nonNull(hdfs.get(KERBEROS_CONFIG))) {
            kerberosConfig = JSON.parseObject(JSON.toJSONString(hdfs.get(KERBEROS_CONFIG)));
            remoteDir = kerberosConfig.getString("remotePath");
        }
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = getSftpMap(tenantId);
            JSONObject conf = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
            //flinkx参数
            conf.putAll(kerberosConfig);
            conf.put("sftpConf", sftpMap);
            //替换remotePath 就是ftp上kerberos的相对路径和principalFile
            if (StringUtils.isEmpty(remoteDir)) {
                remoteDir = sftpMap.get("path") + File.separator + kerberosConfig.getString("kerberosDir");
            }
            String principalFile = conf.getOrDefault("principalFile", "").toString();
            if (StringUtils.isNotEmpty(principalFile)) {
                conf.put("principalFile", getFileName(principalFile));
            }
            conf.put("remoteDir", remoteDir);
            map.put(HADOOP_CONFIG, conf);

            map.put(KERBEROS_CONFIG, kerberosConfig);

            String krb5Conf = conf.getOrDefault("java.security.krb5.conf", "").toString();
            if (StringUtils.isNotEmpty(krb5Conf)) {
                conf.put("java.security.krb5.conf", getFileName(krb5Conf));
            }
            // 开启kerberos认证需要的参数
            conf.put(IS_HADOOP_AUTHORIZATION, "true");
            conf.put(HADOOP_AUTH_TYPE, "kerberos");
        }
    }


    /**
     * 添加ftp地址
     *
     * @param sourceId
     * @param json
     * @param tenantId
     * @param map
     * @param confKey
     */
    private void setSftpConfig(Long sourceId, JSONObject json, Long tenantId, Map<String, Object> map, String confKey) {
        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = getSftpMap(tenantId);
            Map<String, Object> conf = null;
            Object confObj = map.get(confKey);
            if (confObj instanceof String) {
                conf = JSON.parseObject(confObj.toString());
            } else if (confObj instanceof Map) {
                conf = (Map<String, Object>) confObj;
            }
            conf = Optional.ofNullable(conf).orElse(new HashMap<>());
            //flinkx参数
            conf.putAll(kerberosConfig);
            conf.put("sftpConf", sftpMap);
            //替换remotePath 就是ftp上kerberos的相对路径和principalFile
            String remoteDir = sftpMap.get("path") + File.separator + kerberosConfig.getString("kerberosDir");
            String principalFile = conf.getOrDefault("principalFile", "").toString();
            ;
            if (StringUtils.isNotEmpty(principalFile)) {
                conf.put("principalFile", getFileName(principalFile));
            }
            conf.put("remoteDir", remoteDir);
            map.put(confKey, conf);
            String krb5Conf = conf.getOrDefault("java.security.krb5.conf", "").toString();
            if (StringUtils.isNotEmpty(krb5Conf)) {
                conf.put("java.security.krb5.conf", getFileName(krb5Conf));
            }
            // 开启kerberos认证需要的参数
            conf.put(IS_HADOOP_AUTHORIZATION, "true");
            conf.put(HADOOP_AUTH_TYPE, "kerberos");
        }
    }

    public Map<String, String> getSftpMap(Long tenantId) {
        JSONObject configByKey = clusterService.getConfigByKey(tenantId, EComponentType.SFTP.getConfName(), null);
        try {
            return PublicUtil.objectToObject(configByKey, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new PubSvcDefineException(ErrorCode.SFTP_NOT_FOUND);
    }


    private String getFileName(final String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }
        final String[] split = path.split(File.separator);
        return split[split.length - 1];
    }

    /**
     * 获取hadoopconfig最新配置
     *
     * @param tenantId
     * @return
     */
    private String getConsoleHadoopConfig(Long tenantId) {
        if (null == tenantId) {
            return null;
        }
        JSONObject configByKey = clusterService.getConfigByKey(tenantId, EComponentType.HDFS.getConfName(), null);
        return configByKey == null ? null : configByKey.toJSONString();
    }

    /**
     * 添加ftp地址
     *
     * @param json
     * @param dtuicTenantId
     * @param map
     * @param confKey
     */
    public void setSftpConfig(JSONObject json, Long dtuicTenantId, Map<String, Object> map, String confKey) {
        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = kerberosService.getSftpMap(dtuicTenantId);
            Map<String, Object> conf = null;
            Object confObj = map.get(confKey);
            if (confObj instanceof String) {
                conf = JSON.parseObject(confObj.toString());
            } else if (confObj instanceof Map) {
                conf = (Map<String, Object>) confObj;
            }
            conf = Optional.ofNullable(conf).orElse(new HashMap<>());
            //flinkx参数
            conf.putAll(kerberosConfig);
            conf.put("sftpConf", sftpMap);
            //替换remotePath 就是ftp上kerberos的相对路径和principalFile
            String remoteDir = sftpMap.get("path") + File.separator + kerberosConfig.getString("kerberosDir");
            String principalFile = conf.getOrDefault("principalFile", "").toString();
            ;
            if (StringUtils.isNotEmpty(principalFile)) {
                conf.put("principalFile", getFileName(principalFile));
            }
            conf.put("remoteDir", remoteDir);
            map.put(confKey, conf);

            String krb5Conf = conf.getOrDefault(JAVA_SECURITY_KRB5_CONF, "").toString();
            if (StringUtils.isNotEmpty(krb5Conf)) {
                conf.put(JAVA_SECURITY_KRB5_CONF, getFileName(krb5Conf));
            }
            // 开启kerberos认证需要的参数
            conf.put(IS_HADOOP_AUTHORIZATION, "true");
            conf.put(HADOOP_AUTH_TYPE, "kerberos");
        }
    }

    /**
     * 获取表所属字段 不包括分区字段
     *
     * @param source
     * @param tableName
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumn(DevelopDataSource source, String tableName, String schema) {
        try {
            return this.getTableColumnIncludePart(source, tableName, false, schema);
        } catch (final Exception e) {
            throw new RdosDefineException("获取表字段异常", e);
        }

    }

    /**
     * 查询表所属字段 可以选择是否需要分区字段
     *
     * @param source
     * @param tableName
     * @param part      是否需要分区字段
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumnIncludePart(DevelopDataSource source, String tableName, Boolean part, String schema) {
        try {
            if (source == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            if (part == null) {
                part = false;
            }
            JSONObject dataJson = JSONObject.parseObject(source.getDataJson());
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(source);
            IClient client = ClientCache.getClient(source.getType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .filterPartitionColumns(part)
                    .build();
            List<ColumnMetaDTO> columnMetaData = client.getColumnMetaData(sourceDTO, sqlQueryDTO);
            List<JSONObject> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(columnMetaData)) {
                for (ColumnMetaDTO columnMetaDTO : columnMetaData) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(columnMetaDTO));
                    jsonObject.put("isPart", columnMetaDTO.getPart());
                    list.add(jsonObject);
                }
            }
            return list;
        } catch (DtCenterDefException e) {
            throw e;
        } catch (Exception e) {
            throw new RdosDefineException(ErrorCode.GET_COLUMN_ERROR, e);
        }
    }

    public DevelopDataSource getOne(Long id) {
        DsInfo dsInfo = dsInfoService.getOneById(id);
        DevelopDataSource developDataSource = new DevelopDataSource();
        BeanUtils.copyProperties(dsInfo, developDataSource);
        developDataSource.setType(dsInfo.getDataTypeCode());
        developDataSource.setIsDefault(dsInfo.getIsMeta());
        return developDataSource;
    }

    /**
     * 数据同步-获得数据库中相关的表信息
     *
     * @param sourceId 数据源id
     * @param schema   查询的schema
     * @param name     模糊查询表名
     * @return 表集合
     */
    public List<String> tableList(Long sourceId, String schema, String name) {
        // 查询的db
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableNamePattern(name).limit(5000).build();
        sqlQueryDTO.setView(true);
        sqlQueryDTO.setSchema(schema);
        return client.getTableList(sourceDTO, sqlQueryDTO);
    }

    /**
     * 获取 kafka 的 topic 集合
     *
     * @param sourceId 数据源 id
     * @return topic list
     */
    public List<String> getKafkaTopics(Long sourceId) {
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
        return ClientCache
                .getKafka(sourceDTO.getSourceType())
                .getTopicList(sourceDTO);
    }

    /**
     * 判断 oracle 是否开启 cdb
     *
     * @param sourceId 数据源 id
     * @return 是否开启 cdb
     */
    public Boolean isOpenCdb(Long sourceId) {
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
        // 非 oracle 返回 false
        if (!DataSourceType.Oracle.getVal().equals(sourceDTO.getSourceType())) {
            return false;
        }
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        List<Map<String, Object>> result;
        try {
            result = client.executeQuery(sourceDTO, SqlQueryDTO.builder().sql(IS_OPEN_CDB).build());
        } catch (Exception e) {
            LOGGER.error("error in judging whether to open CDB...{}", e.getMessage(), e);
            return false;
        }
        if (CollectionUtils.isEmpty(result)) {
            return false;
        }
        Map<String, Object> cdbResult = result.get(0);
        return !MapUtils.isEmpty(cdbResult) &&
                cdbResult.containsKey("CDB") &&
                StringUtils.equalsIgnoreCase("YES", MapUtils.getString(cdbResult, "CDB"));
    }

    /**
     * 数据同步-获得表中字段与类型信息
     *
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return 字段信息
     */
    public List<JSONObject> tableColumn(Long sourceId, String tableName, Boolean isIncludePart, String schema) {
        DevelopDataSource source = this.getOne(sourceId);
        return getTableColumnIncludePart(source, handleTableName(source.getType(), tableName), isIncludePart, schema);
    }


    /**
     * 返回切分键需要的列名
     * <p>
     * 只支持关系型数据库 mysql\oracle\sqlserver\postgresql  的整型数据类型
     * 也不支持其他数据库。
     * 如果指定了不支持的类型，则忽略切分键功能，使用单通道进行同步。
     *
     * @param sourceId
     * @param tableName
     * @return
     */
    public Set<JSONObject> columnForSyncopate(Long sourceId, String tableName, String schema) {

        DevelopDataSource source = getOne(sourceId);
        if (Objects.isNull(RDBMSSourceType.getByDataSourceType(source.getType())) && !DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
            throw new RdosDefineException("切分键只支持关系型数据库");
        }
        if (StringUtils.isEmpty(tableName)) {
            return new HashSet<>();
        }
        StringBuilder newTableName = new StringBuilder();
        tableName = handleTableName(source.getType(), tableName);
        final List<JSONObject> tableColumn = this.getTableColumn(source, tableName, schema);
        if (CollectionUtils.isNotEmpty(tableColumn)) {
            List<String> numbers;
            if (DataSourceType.MySQL.getVal().equals(source.getType()) || DataSourceType.Polardb_For_MySQL.getVal().equals(source.getType()) || DataSourceType.TiDB.getVal().equals(source.getType())) {
                numbers = MYSQL_NUMBERS;
            } else if (DataSourceType.Oracle.getVal().equals(source.getType())) {
                numbers = ORACLE_NUMBERS;
            } else if (DataSourceType.SQLServer.getVal().equals(source.getType())) {
                numbers = SQLSERVER_NUMBERS;
            } else if (DataSourceType.PostgreSQL.getVal().equals(source.getType())
                    || DataSourceType.ADB_FOR_PG.getVal().equals(source.getType())) {
                numbers = POSTGRESQL_NUMBERS;
            } else if (DataSourceType.DB2.getVal().equals(source.getType())) {
                numbers = DB2_NUMBERS;
            } else if (DataSourceType.GBase_8a.getVal().equals(source.getType())) {
                numbers = GBASE_NUMBERS;
            } else if (DataSourceType.Clickhouse.getVal().equals(source.getType())) {
                numbers = CLICKHOUSE_NUMBERS;
            } else if (DataSourceType.DMDB.getVal().equals(source.getType())) {
                numbers = DMDB_NUMBERS;
            } else if (DataSourceType.GREENPLUM6.getVal().equals(source.getType())) {
                numbers = GREENPLUM_NUMBERS;
            } else if (DataSourceType.KINGBASE8.getVal().equals(source.getType())) {
                numbers = KINGBASE_NUMBERS;
            } else if (DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
                numbers = INFLUXDB_NUMBERS;
            } else {
                throw new RdosDefineException("切分键只支持关系型数据库");
            }
            Map<JSONObject, String> twinsMap = new LinkedHashMap<>(tableColumn.size() + 1);
            for (JSONObject twins : tableColumn) {
                twinsMap.put(twins, twins.getString(TYPE));
            }


            Iterator<Map.Entry<JSONObject, String>> iterator = twinsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                String type = getSimpleType(iterator.next().getValue());
                if (numbers.contains(type.toUpperCase())) {
                    continue;
                }
                if (source.getType().equals(DataSourceType.Oracle.getVal())) {
                    if ("number".equalsIgnoreCase(type)) {
                        continue;
                    }

                    Matcher numberMatcher1 = NUMBER_PATTERN.matcher(type);
                    Matcher numberMatcher2 = NUMBER_PATTERN2.matcher(type);
                    if (numberMatcher1.matches()) {
                        continue;
                    } else if (numberMatcher2.matches()) {
                        int floatLength = Integer.parseInt(numberMatcher2.group(2));
                        if (floatLength <= 0) {
                            continue;
                        }
                    }
                }
                iterator.remove();
            }
            //为oracle加上默认切分键
            if (source.getType().equals(DataSourceType.Oracle.getVal())) {
                JSONObject keySet = new JSONObject();
                keySet.put("type", "NUMBER(38,0)");
                keySet.put("key", "ROW_NUMBER()");
                keySet.put("comment", "");
                twinsMap.put(keySet, "NUMBER(38,0)");
            }
            return twinsMap.keySet();
        }
        return Sets.newHashSet();
    }

    private String getSimpleType(String type) {
        type = type.toUpperCase();
        String[] split = type.split(" ");
        if (split.length > 1) {
            //提取例如"INT UNSIGNED"情况下的字段类型
            type = split[0];
        }
        return type;
    }


    public Set<String> getHivePartitions(Long sourceId, String tableName) {
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
        IClient iClient = ClientCache.getClient(sourceDTO.getSourceType());
        List<ColumnMetaDTO> partitionColumn = iClient.getPartitionColumn(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());

        Set<String> partitionNameSet = Sets.newHashSet();
        //格式化分区信息 与hive保持一致
        if (CollectionUtils.isNotEmpty(partitionColumn)) {
            StringJoiner tempJoiner = new StringJoiner("=/", "", "=");
            for (ColumnMetaDTO column : partitionColumn) {
                tempJoiner.add(column.getKey());
            }
            partitionNameSet.add(tempJoiner.toString());
        }
        return partitionNameSet;
    }

    /**
     * 数据同步-获得预览数据，默认展示3条
     *
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return 数据预览结果
     */
    public JSONObject preview(Long sourceId, String tableName, String schema) {
        DevelopDataSource source = getOne(sourceId);
        tableName = handleTableName(source.getType(), tableName);
        //获取字段信息
        List<String> columnList = new ArrayList<>();
        //获取数据
        List<List<Object>> dataList;
        try {
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(source);
            List<JSONObject> columnJson = getTableColumn(source, tableName, schema);
            if (CollectionUtils.isNotEmpty(columnJson)) {
                for (JSONObject columnMetaDTO : columnJson) {
                    columnList.add(columnMetaDTO.getString("key"));
                }
            }
            IClient client = ClientCache.getClient(sourceDTO.getSourceType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().schema(schema).tableName(tableName).previewNum(3).build();
            dataList = client.getPreview(sourceDTO, sqlQueryDTO);
            if (DataSourceType.getRDBMS().contains(source.getType())) {
                //因为会把字段名也会返回 所以要去除第一行
                dataList = dataList.subList(1, dataList.size());
            }
        } catch (Exception e) {
            LOGGER.error("datasource preview end with error.", e);
            throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
        }

        JSONObject preview = new JSONObject(2);
        preview.put("columnList", columnList);
        preview.put("dataList", dataList);

        return preview;
    }

    private String handleTableName(Integer type, String tableName) {
        StringBuilder newTableName = new StringBuilder();
        if (DataSourceType.SQLServer.getVal().equals(type) && StringUtils.isNotBlank(tableName)) {
            if (!tableName.contains("[")) {
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0, newTableName.length() - 1);
            }
        }
        return tableName;
    }

    /**
     * 获取所有schema
     *
     *
     * @param sourceId 数据源id
     * @return 所有 schema
     */
    public List<String> getAllSchemas(Long sourceId, String schema) {
        ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId, schema);
        IClient client = ClientCache.getClient(sourceDTO.getSourceType());
        return client.getAllDatabases(sourceDTO, SqlQueryDTO.builder().schema(schema).build());
    }

    public String getCreateTargetTableSql(Long originSourceId,
                                          Long targetSourceId,
                                          String tableName,
                                          String partition,
                                          String sourceSchema,
                                          String targetSchema) {
        try {
            DevelopDataSource originSource = getOne(originSourceId);
            JSONObject reader = JSON.parseObject(originSource.getDataJson());
            if (!ORIGIN_TABLE_ALLOW_TYPES.contains(originSource.getType())) {
                throw new RdosDefineException("一键生成目标表，只支持关系型数据库、hive和maxCompute类型");
            }
            List<JSONObject> columnMetaData = new ArrayList<>();
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(originSourceId);
            IClient iClient = ClientCache.getClient(sourceDTO.getSourceType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().schema(sourceSchema).tableName(tableName).build();
            List<ColumnMetaDTO> columnMeta = iClient.getColumnMetaData(sourceDTO, sqlQueryDTO);
            if (CollectionUtils.isNotEmpty(columnMeta)) {
                for (ColumnMetaDTO columnMetaDTO : columnMeta) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(columnMetaDTO));
                    jsonObject.put("isPart", columnMetaDTO.getPart());
                    columnMetaData.add(jsonObject);
                }
            }
            String comment = iClient.getTableMetaComment(sourceDTO, sqlQueryDTO);
            List<String> partList = null;
            if (StringUtils.isNotBlank(partition)) {
                String[] parts = partition.split("/");
                partList = new ArrayList<>();
                for (String part : parts) {
                    String[] partDetail = part.split("=");
                    String partCol = partDetail[0];
                    if (!partCol.equals("pt")) {
                        partList.add(partCol);
                    }
                }
            }
            List<JSONObject> columns = null;
            DevelopDataSource targetDataSource = getOne(targetSourceId);

            String sql;
            //'CHARNT.'CUSTMERS_10_MIN' 需要做处理
            tableName = this.formatTableName(tableName);
            int sourceType = 0;
            if (targetDataSource != null) {
                sourceType = Objects.isNull(targetDataSource) ? DataSourceType.HIVE.getVal() : targetDataSource.getType();
            }
            if (CREATE_TABLE_TO_PG_TABLE.contains(sourceType)) {
                // 注意：ADB For PG不会在此处理，后面单独处理
                columns = convertWriterColumns(columnMetaData, new PostgreSqlWriterFormat());
                sql = generalLibraCreateSql(columns, tableName, targetSchema);
            } else if (sourceType == DataSourceType.TiDB.getVal() || sourceType == DataSourceType.MySQL.getVal()) {
                columns = convertTidbWriterColumns(columnMetaData, TYPE_FORMAT);
                sql = generalTidbCreateSql(columns, tableName, comment);
            } else if (sourceType == DataSourceType.Oracle.getVal()) {
                columns = convertOracleWriterColumns(columnMetaData, new OracleWriterFormat());
                sql = OracleSqlFormatUtil.generalCreateSql(targetSchema, tableName.toUpperCase(), columns, comment);
            } else if (sourceType == DataSourceType.ADB_FOR_PG.getVal()) {
                columns = ADBForPGUtil.convertADBForPGWriterColumns(columnMetaData);
                sql = ADBForPGUtil.generalCreateSql(targetSchema, tableName, columns, comment);
            } else {
                //默认走hive建表
                columns = convertWriterColumns(columnMetaData, TYPE_FORMAT);
                sql = generalCreateSql(columns, partList, tableName, comment);
            }
            return sqlFormat(sql);
        } catch (Exception e) {
            throw new RdosDefineException("一键生成目标表失败", e);
        }
    }

    private String formatTableName(String tableName) {
        try {
            if (StringUtils.isNotBlank(tableName)) {
                tableName = tableName.replaceAll(TABLE_FORMAT_REGEX, "");
                String[] split = tableName.split("\\.");
                if (split.length > 1) {
                    return split[split.length - 1];
                }
                return split[0];
            }
        } catch (Exception e) {
            LOGGER.error("tableName split error", e);
        }
        return tableName;
    }

    /**
     * 适配oracle建表字段
     *
     * @param dbColumns
     * @param format
     * @return
     */
    private List convertOracleWriterColumns(List<JSONObject> dbColumns, TypeFormat format) {
        if (Objects.isNull(format)) {
            return dbColumns;
        }
        List<JSONObject> oracleColumns = new ArrayList<>(dbColumns.size());
        for (int i = 0; i < dbColumns.size(); i++) {
            JSONObject oracleColumn = new JSONObject(4);
            JSONObject dbColumn = dbColumns.get(i);
            oracleColumn.put("key", dbColumn.getString("key"));
            oracleColumn.put("index", i);
            oracleColumn.put("comment", dbColumn.getString("comment"));
            String dbColumnType = dbColumn.getString("type");
            String type = format.formatToString(dbColumnType);

            if (dbColumn.getLong("precision") != null && type.equalsIgnoreCase(ColumnType.INT.name())
                    && dbColumn.getLong("precision") >= 11) {
                type = ColumnType.NUMBER.name();
            } else if (type.equalsIgnoreCase("unsigned")
                    || dbColumn.getString("type").toLowerCase().contains("unsigned")) {
                type = ColumnType.NUMBER.name();
            } else if (Lists.newArrayList("UINT8", "UINT16", "INT8", "INT16", "INT32").contains(type.toUpperCase())) {
                type = ColumnType.INTEGER.name();
            } else if (Lists.newArrayList("UINT32", "UINT64", "INT64").contains(type.toUpperCase())) {
                type = ColumnType.NUMBER.name();
            } else if (ColumnType.FLOAT32.name().equalsIgnoreCase(type)) {
                type = ColumnType.FLOAT.name();
            } else if (ColumnType.FLOAT64.name().equalsIgnoreCase(type)) {
                type = ColumnType.NUMBER.name();
            } else if (ColumnType.TEXT.name().equalsIgnoreCase(dbColumnType)) {
                type = ColumnType.VARCHAR2.name() + "(1000)";
            } else if (ColumnType.VARCHAR2.name().equalsIgnoreCase(type)) {
                type = ColumnType.VARCHAR2.name() + "(255)";
            }
            oracleColumn.put("type", type);
            oracleColumns.add(oracleColumn);
        }
        return oracleColumns;
    }


    private List convertWriterColumns(List<JSONObject> dbColumns, TypeFormat format) {
        if (null == format) {
            return dbColumns;
        }
        List<JSONObject> hiveColumns = new ArrayList<>(dbColumns.size());
        for (int i = 0; i < dbColumns.size(); i++) {
            JSONObject hiveColumn = new JSONObject(4);
            JSONObject dbColumn = dbColumns.get(i);
            hiveColumn.put("key", dbColumn.getString("key"));
            hiveColumn.put("index", i);
            hiveColumn.put("comment", dbColumn.getString("comment"));
            String type = format.formatToString(dbColumn.getString("type"));
            if (type.equalsIgnoreCase(ColumnType.DECIMAL.name())) {
                if (dbColumn.containsKey("precision") && dbColumn.containsKey("scale")) {
                    Integer precision = dbColumn.getInteger("precision");
                    Integer scale = dbColumn.getInteger("scale");
                    precision = precision == null ? 10 : precision;
                    scale = scale == null ? 0 : scale;
                    type = String.format("%s(%s,%s)", type, precision, scale);
                } else {
                    type = String.format("%s(%s,%s)", type, 15, 0);
                }
            } else if (type.equalsIgnoreCase(ColumnType.DOUBLE_PRECISION.name())) {
                // DOUBLE PRECISION
                type = type.replaceAll("_", " ");
            }
            if (dbColumn.getLong("precision") != null && type.equalsIgnoreCase(ColumnType.INT.name()) && dbColumn.getLong("precision") >= 11) {
                type = ColumnType.BIGINT.name();
            } else if (type.equalsIgnoreCase("unsigned") || dbColumn.getString("type").toLowerCase().contains("unsigned")) {
                type = ColumnType.BIGINT.name();
            } else if (Lists.newArrayList("UINT8", "UINT16", "INT8", "INT16", "INT32").contains(type.toUpperCase())) {
                type = ColumnType.INT.name();
            } else if (Lists.newArrayList("UINT32", "UINT64", "INT64").contains(type.toUpperCase())) {
                type = ColumnType.BIGINT.name();
            } else if (ColumnType.FLOAT32.name().equalsIgnoreCase(type)) {
                type = ColumnType.FLOAT.name();
            } else if (ColumnType.FLOAT64.name().equalsIgnoreCase(type)) {
                type = ColumnType.DOUBLE.name();
            }
            hiveColumn.put("type", type);
            hiveColumns.add(hiveColumn);
        }
        return hiveColumns;
    }

    /**
     * 生成pg建表sql
     *
     * @param columns
     * @param tableName
     * @return
     */
    private String generalLibraCreateSql(List<JSONObject> columns, String tableName, String targetSchema) {
        List<String> columnList = new ArrayList<>();
        for (JSONObject column : columns) {
            columnList.add(String.format("%s %s", column.getString("key"), column.getString("type")));
        }
        if (StringUtils.isNotBlank(targetSchema)) {
            tableName = targetSchema.trim() + "." + tableName;
        }
        String sql = String.format(RDB_CREATE_TABLE_SQL_TEMPLATE, tableName, StringUtils.join(columnList, ","));
        return SqlFormatUtil.formatSql(sql);
    }

    private List convertTidbWriterColumns(List<JSONObject> dbColumns, TypeFormat format) {
        if (null == format) {
            return dbColumns;
        }
        List<JSONObject> hiveColumns = new ArrayList<>(dbColumns.size());
        for (int i = 0; i < dbColumns.size(); i++) {
            JSONObject hiveColumn = new JSONObject(4);
            JSONObject dbColumn = dbColumns.get(i);
            hiveColumn.put("key", dbColumn.getString("key"));
            hiveColumn.put("index", i);
            hiveColumn.put("comment", dbColumn.getString("comment"));
            String type = format.formatToString(dbColumn.getString("type"));
            if (type.equalsIgnoreCase(ColumnType.DECIMAL.name())) {
                if (dbColumn.containsKey("precision") && dbColumn.containsKey("scale")) {
                    Integer precision = dbColumn.getInteger("precision");
                    Integer scale = dbColumn.getInteger("scale");
                    precision = precision == null ? 10 : precision;
                    scale = scale == null ? 0 : scale;
                    type = String.format("%s(%s,%s)", type, precision, scale);
                }
            } else if (type.equalsIgnoreCase(ColumnType.DOUBLE_PRECISION.name())) {
                // DOUBLE PRECISION
                type = type.replaceAll("_", " ");
            }
            if (dbColumn.getLong("precision") != null && type.equalsIgnoreCase(ColumnType.INT.name()) && dbColumn.getLong("precision") >= 11) {
                type = ColumnType.BIGINT.name();
            } else if (type.equalsIgnoreCase("unsigned") || dbColumn.getString("type").toLowerCase().contains("unsigned")) {
                type = ColumnType.BIGINT.name();
            } else if (Lists.newArrayList("UINT8", "UINT16", "INT8", "INT16", "INT32").contains(type.toUpperCase())) {
                type = ColumnType.INT.name();
            } else if (Lists.newArrayList("UINT32", "UINT64", "INT64").contains(type.toUpperCase())) {
                type = ColumnType.BIGINT.name();
            } else if (ColumnType.FLOAT32.name().equalsIgnoreCase(type)) {
                type = ColumnType.FLOAT.name();
            } else if (ColumnType.FLOAT64.name().equalsIgnoreCase(type)) {
                type = ColumnType.DOUBLE.name();
            } else if (ColumnType.STRING.name().equalsIgnoreCase(type)) {
                type = ColumnType.VARCHAR.name() + "(255)";
            }
            hiveColumn.put("type", type);
            hiveColumns.add(hiveColumn);
        }
        return hiveColumns;
    }

    private String generalTidbCreateSql(List<JSONObject> writerColumns, String tableName, String tableComment) {
        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ").append("`" + tableName + "`").append(" (");
        Iterator<JSONObject> it = writerColumns.iterator();
        while (true) {
            JSONObject writerColumn = it.next();
            createSql.append("`" + writerColumn.getString("key") + "`").append(" ").append(writerColumn.getString("type"))
                    .append(" COMMENT '" + (StringUtils.isNotEmpty(writerColumn.getString("comment")) ? writerColumn.getString("comment") : "") + "'");
            if (!it.hasNext()) {
                break;
            }
            createSql.append(",");
        }
        createSql.append(")").append(" ");
        createSql.append(String.format("comment '%s'", (StringUtils.isNotEmpty(tableComment) ? tableComment : "")));
        return createSql.toString();
    }

    public String generalCreateSql(List<JSONObject> writerColumns, List<String> partList, String
            tableName, String tableComment) {
        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ").append("`" + tableName + "`").append(" (");
        Iterator<JSONObject> it = writerColumns.iterator();
        while (true) {
            JSONObject writerColumn = it.next();
            createSql.append("`" + writerColumn.getString("key") + "`").append(" ").append(writerColumn.getString("type"))
                    .append(" COMMENT '" + (StringUtils.isNotEmpty(writerColumn.getString("comment")) ? writerColumn.getString("comment") : "") + "'");
            if (!it.hasNext()) {
                break;
            }
            createSql.append(",");
        }
        createSql.append(")").append(" ");
        createSql.append(String.format("comment '%s'", (StringUtils.isNotEmpty(tableComment) ? tableComment : "")));
        createSql.append(" partitioned by (pt STRING");
        if (CollectionUtils.isNotEmpty(partList)) {
            for (String part : partList) {
                createSql.append(",").append(part).append(" STRING");
            }
        }
        createSql.append(String.format(") stored as %s \n ", environmentContext.getCreateTableType()));
        return createSql.toString();
    }

    /**
     * 格式化sql
     */
    public String sqlFormat(String sql) {
        if (StringUtils.isNotBlank(sql)) {
            try {
                return SqlFormatter.format(sql);
            } catch (Exception e) {
                LOGGER.error("failure to format sql, e : {}", e);
            }
        }
        return sql;
    }

    /**
     * ddl建表
     *
     * @param sql      建表SQL
     * @param sourceId 数据源ID
     * @return
     */
    public String ddlCreateTable(String sql, Long sourceId) {
        if (StringUtils.isNotBlank(sql)) {
            sql = sql.trim();
        } else {
            throw new RdosDefineException("Sql不能为空");
        }
        DevelopDataSource developDataSource = datasourceService.getOne(sourceId);
        if (DataSourceType.Oracle.getVal().equals(developDataSource.getType())) {
            return dealOracleCreateSql(sourceId, sql);
        }
        onlyNeedOneSql(sql);
        if (!SqlFormatUtil.isCreateSql(sql)) {
            throw new RdosDefineException(ErrorCode.ONLY_EXECUTE_CREATE_TABLE_SQL);
        }
        sql = SqlFormatUtil.init(sql).removeEndChar().getSql();
        String tableName = CreateTableSqlParseUtil.parseTableName(sql);
        executeOnSpecifySourceWithOutResult(sourceId, Lists.newArrayList(sql));
        return tableName;
    }

    /**
     * 处理oracle类型建表sql
     *
     * @param sourceId
     * @param sql
     * @return
     */
    private String dealOracleCreateSql(Long sourceId, String sql) {
        if (!sql.endsWith(SEMICOLON)) {
            sql = sql + SEMICOLON;
        }
        List<String> sqlList = SqlFormatUtil.splitSqlText(sql);
        if (CollectionUtils.isNotEmpty(sqlList) && !SqlFormatUtil.isCreateSql(sqlList.get(0))) {
            throw new RdosDefineException(ErrorCode.ONLY_EXECUTE_CREATE_TABLE_SQL);
        }
        String tableName = CreateTableSqlParseUtil.parseTableName(sqlList.get(0));
        executeOnSpecifySourceWithOutResult(sourceId, sqlList);
        return tableName.toUpperCase(Locale.ROOT);
    }

    /**
     * @param sourceId 数据源id
     * @param sqlList  拼写sql
     */
    private void executeOnSpecifySourceWithOutResult(Long sourceId, List<String> sqlList) {
        DevelopDataSource source = getOne(sourceId);
        DataSourceType dataSourceType = DataSourceType.getSourceType(source.getType());
        if (!SUPPORT_CREATE_TABLE_DATASOURCES.contains(dataSourceType)) {
            throw new RdosDefineException(String.format("只支持创建%s数据源表", SUPPORT_CREATE_TABLE_DATASOURCES_NAMES));
        }
        try {
            ISourceDTO sourceDTO = sourceLoaderService.buildSourceDTO(sourceId);
            IClient client = ClientCache.getClient(sourceDTO.getSourceType());
            Connection con = client.getCon(sourceDTO);
            for (String s : sqlList) {
                if (StringUtils.isNotBlank(s)) {
                    DBUtil.executeSqlWithoutResultSet(con, s, false);
                }
            }
        } catch (Exception e) {
            throw new RdosDefineException(String.format("执行sql：%s 异常", StringUtils.join(sqlList, ",")), e);
        }
    }

    private void onlyNeedOneSql(String sql) {
        int unEmptySqlNum = 0;
        List<String> split = DtStringUtil.splitIgnoreQuota(sql, ';');
        for (String s : split) {
            if (StringUtils.isNotEmpty(s.trim())) {
                unEmptySqlNum++;
            }
        }
        if (unEmptySqlNum == 0) {
            throw new RdosDefineException("Sql不能为空");
        } else if (unEmptySqlNum > 1) {
            throw new RdosDefineException("仅支持执行一条sql语句");
        }
    }

    public Object support() {
        return null;
    }
}
