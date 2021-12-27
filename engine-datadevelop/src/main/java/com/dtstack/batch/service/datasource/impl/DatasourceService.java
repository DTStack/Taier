package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.enums.DataSourceTypeEnum;
import com.dtstack.batch.common.exception.PubSvcDefineException;
import com.dtstack.batch.common.util.JsonUtil;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.enums.RDBMSSourceType;
import com.dtstack.batch.enums.SourceDTOType;
import com.dtstack.batch.enums.TableLocationType;
import com.dtstack.batch.enums.TaskCreateModelType;
import com.dtstack.batch.sync.format.TypeFormat;
import com.dtstack.batch.sync.format.writer.HiveWriterFormat;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.kerberos.HadoopConfTool;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.constrant.FormNames;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.CommonUtils;
import com.dtstack.engine.common.util.DataSourceUtils;
import com.dtstack.engine.common.util.Strings;
import com.dtstack.engine.domain.BatchDataSource;
import com.dtstack.engine.domain.datasource.DsFormField;
import com.dtstack.engine.domain.datasource.DsInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 有关数据源中心
 * @description:
 * @author: liuxx
 * @date: 2021/3/16
 */
@Slf4j
@Service
public class DatasourceService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private KerberosService kerberosService;

    @Autowired
    private DsInfoService dsInfoService;

    @Autowired
    private DsFormFieldService formFieldService;

    @Autowired
    private DsTypeService typeService;


    @Autowired
    private DsTypeService dsTypeService;

    public static final String DSC_INFO_CHANGE_CHANNEL = "dscInfoChangeChannel";

    public static final String JDBC_URL = "jdbcUrl";
    public static final String JDBC_USERNAME = "username";
    public static final String JDBC_PASSWORD = "password";
    public static final String JDBC_HOSTPORTS = "hostPorts";
    public static final String SECRET_KEY = "secretKey";

    public static final String HDFS_DEFAULTFS = "defaultFS";

    public static final String HADOOP_CONFIG = "hadoopConfig";

    public static String HIVE_METASTORE_URIS = "hiveMetastoreUris";

    private static final String HBASE_CONFIG = "hbaseConfig";

    public static final String HIVE_PARTITION = "partition";

    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";

    public static final String TEMP_TABLE_PREFIX_FROM_DQ = "temp_data_";

    private static final String KEY = "key";

    private static final String TYPE = "type";

    private static final String COLUMN = "column";

    private static final String EXTRAL_CONFIG = "extralConfig";

    private static final List<String> MYSQL_NUMBERS = Lists.newArrayList("TINYINT", "SMALLINT", "MEDIUMINT", "INT", "BIGINT", "INT UNSIGNED");

    private static final List<String> CLICKHOUSE_NUMBERS = Lists.newArrayList("UINT8", "UINT16", "UINT32", "UINT64", "INT8", "INT16", "INT32", "INT64");

    private static final List<String> ORACLE_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "NUMBER");

    private static final List<String> SQLSERVER_NUMBERS = Lists.newArrayList("INT", "INTEGER", "SMALLINT", "TINYINT", "BIGINT");

    private static final List<String> POSTGRESQL_NUMBERS = Lists.newArrayList("INT2", "INT4", "INT8", "SMALLINT", "INTEGER", "BIGINT", "SMALLSERIAL", "SERIAL", "BIGSERIAL");

    private static final List<String> DB2_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> GBASE_NUMBERS = Lists.newArrayList("SMALLINT", "TINYINT", "INT", "BIGINT", "FLOAT", "DOUBLE", "DECIMAL", "NUMERIC");

    private static final List<String> DMDB_NUMBERS = Lists.newArrayList("INT", "SMALLINT", "BIGINT","NUMBER");

    private static final List<String> GREENPLUM_NUMBERS = Lists.newArrayList("SMALLINT", "INTEGER", "BIGINT");

    private static final List<String> KINGBASE_NUMBERS = Lists.newArrayList("BIGINT", "DOUBLE", "FLOAT", "INT4", "INT8", "FLOAT", "FLOAT8", "NUMERIC");

    private static final List<String> INFLUXDB_NUMBERS = Lists.newArrayList("INTEGER");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("NUMBER\\(\\d+\\)");

    private static final Pattern NUMBER_PATTERN2 = Pattern.compile("NUMBER\\((\\d+),([\\d-]+)\\)");

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final TypeFormat TYPE_FORMAT = new HiveWriterFormat();

    private static final String NO_PERMISSION = "NO PERMISSION";

    private static final String hdfsCustomConfig = "hdfsCustomConfig";

    private static final String KERBEROS_CONFIG = "kerberosConfig";


    /**
     * 解析kerberos文件获取principal列表
     * @param source
     * @param resource
     * @param dtuicTenantId
     * @param projectId
     * @param userId
     * @return
     */
    public List<String> getPrincipalsWithConf(DataSourceVO source, Pair<String, String> resource, Long dtuicTenantId, Long projectId, Long userId) {
        String localKerberosPath;
        Map<String, Object> kerberosConfig;
        // 获取数据源类型，这里要做type version的改造
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(),source.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        if (Objects.nonNull(resource)) {
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                // 解析Zip文件获取配置对象
                kerberosConfig = kerberos.parseKerberosFromUpload(resource.getRight(), localKerberosPath);
            } catch (IOException e) {
                log.error("解析principals， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
            // 连接 Kerberos 前的准备工作
            kerberos.prepareKerberosForConnect(kerberosConfig, localKerberosPath);
        } else {
            kerberosConfig = kerberosConnectPrepare(source.getId());
        }
        return kerberos.getPrincipals(kerberosConfig);
    }

    /**
     * kerberos认证前预处理 ：对kerberos参数替换相对路径为绝对路径等操作
     * @param sourceId
     * @return
     */
    public Map<String, Object> kerberosConnectPrepare(Long sourceId) {
        DsInfo dataSource = dsInfoService.getOneById(sourceId);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSource.getDataType(), dataSource.getDataVersion());
        if (Objects.isNull(typeEnum)) {
            throw new PubSvcDefineException(ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        }
        Map<String, Object> kerberosConfig = fillKerberosConfig(dataSource.getId());
        HashMap<String, Object> tmpKerberosConfig = new HashMap<>(kerberosConfig);
        // kerberos获取表操作预处理
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            String localKerberosPath = kerberosService.getLocalKerberosPath(sourceId);
            IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
            try {
                kerberos.prepareKerberosForConnect(tmpKerberosConfig, localKerberosPath);
            } catch (Exception e) {
                log.error("kerberos连接预处理失败！{}", e.getMessage(), e);
                throw new DtCenterDefException(String.format("kerberos连接预处理失败,Caused by: %s", e.getMessage()), e);
            }
        }
        return tmpKerberosConfig;
    }

    /**
     * 根据已有数据源主键填充confMap
     * @param sourceId
     * @return
     */
    public Map<String, Object> fillKerberosConfig(Long sourceId) {
        DsInfo dataSource = dsInfoService.getOneById(sourceId);
        Long tenantId = dataSource.getTenantId();
        // 获取Kerberos客户端
        JSONObject kerberosConfig = DataSourceUtils.getOriginKerberosConfig(dataSource.getDataJson(), false);

        if (MapUtils.isEmpty(kerberosConfig)) {
            return Collections.emptyMap();
        }

        try {
            // 获取kerberos本地路径
            String localKerberosConf = kerberosService.getLocalKerberosPath(sourceId);
            kerberosService.downloadKerberosFromSftp(dataSource.getIsMeta(), sourceId, DataSourceUtils.getDataSourceJson(dataSource.getDataJson()), localKerberosConf, tenantId);
        } catch (SftpException e) {
            throw new DtCenterDefException(String.format("获取kerberos认证文件失败,Caused by: %s", e.getMessage()), e);
        }
        return kerberosConfig;
    }

    /**
     * 测试联通性
     * @param source
     * @return
     */
    public Boolean checkConnection(DataSourceVO source) {
        return checkConnectionWithConf(source, null, null);
    }
    /**
     * 检测kerberos认证的数据源连通性
     * @param source
     * @param resource
     * @param projectId
     * @param userId
     * @return
     */
    public Boolean checkConnectionWithKerberos(DataSourceVO source, Pair<String, String> resource, Long projectId, Long userId) {
        Map<String, Object> kerberosConfig;
        String localKerberosPath;
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        if (Objects.nonNull(resource)) {
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                kerberosConfig = kerberos.parseKerberosFromUpload(resource.getRight(), localKerberosPath);
            } catch (IOException e) {
                log.error("检测连通性， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
        } else {
            localKerberosPath = kerberosService.getLocalKerberosPath(source.getId());
            kerberosConfig = fillKerberosConfig(source.getId());
        }
        try {
            source.setDataJson(DataSourceUtils.getDataSourceJson(source.getDataJsonString()));
        } catch (Exception e) {
            log.error("检查数据源连接，DataJsonString 转化异常", e);
            throw new PubSvcDefineException("JSONObject 转化异常", e);
        }
        // 设置前台传入的principals
        setPrincipals(source.getDataJson(), kerberosConfig);
        return checkConnectionWithConf(source, kerberosConfig, localKerberosPath);
    }

    /**
     * 数据源连通性测试
     * @param source
     * @param confMap
     * @param localKerberosPath
     * @return
     */
    public Boolean checkConnectionWithConf(DataSourceVO source, Map<String, Object> confMap, String localKerberosPath) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(), source.getDataVersion());
        if (MapUtils.isEmpty(confMap) && source.getId() > 0L) {
            confMap = fillKerberosConfig(source.getId());
            localKerberosPath = kerberosService.getLocalKerberosPath(source.getId());
        }
        if(DataSourceTypeEnum.ADB_PostgreSQL == typeEnum){
            typeEnum = DataSourceTypeEnum.PostgreSQL;
        }
        // 替换相对绝对路径
        Map<String, Object> tempConfMap = null;
        if (MapUtils.isNotEmpty(confMap)) {
            tempConfMap = Maps.newHashMap(confMap);
            IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
            kerberos.prepareKerberosForConnect(tempConfMap, localKerberosPath);
        }
        // 测试连通性
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(source.getDataJson(), typeEnum.getVal(), tempConfMap);
        return ClientCache.getClient(typeEnum.getVal()).testCon(sourceDTO);
    }


    /**
     * 设置前台传入的principals
     * @param dataJson
     * @param confMap
     */
    public void setPrincipals(JSONObject dataJson, Map<String, Object> confMap) {
        //principal 键
        String principal = dataJson.getString(FormNames.PRINCIPAL);
        if (Strings.isNotBlank(principal)) {
            confMap.put(HadoopConfTool.PRINCIPAL, principal);
        }
        //Hbase master kerberos Principal
        String hbaseMasterPrincipal = dataJson.getString(FormNames.HBASE_MASTER_PRINCIPAL);
        if (Strings.isNotBlank(hbaseMasterPrincipal)) {
            confMap.put(HadoopConfTool.HBASE_MASTER_PRINCIPAL, hbaseMasterPrincipal);
        }
        //Hbase region kerberos Principal
        String hbaseRegionserverPrincipal = dataJson.getString(FormNames.HBASE_REGION_PRINCIPAL);
        if (Strings.isNotBlank(hbaseRegionserverPrincipal)) {
            confMap.put(HadoopConfTool.HBASE_REGION_PRINCIPAL, hbaseRegionserverPrincipal);
        }
    }

    /**
     * 上传Kerberos添加和修改数据源
     * @param dataSourceVO
     * @param resource
     * @param projectId
     * @param userId
     * @param dtuicTenantId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateSourceWithKerberos(DataSourceVO dataSourceVO, Pair<String, String> resource, Long projectId, Long userId, Long dtuicTenantId) {
        Map<String, Object> confMap;
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
        dataSourceVO.setDataJson(dataJson);
        List<Integer> list = JSON.parseObject(dataSourceVO.getAppTypeListString(), List.class);
        dataSourceVO.setAppTypeList(list);
        String localKerberosConf;
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        if (Objects.nonNull(resource)) {
            //resource不为空表示本地上传文件
            localKerberosConf = kerberosService.getTempLocalKerberosConf(userId, projectId);
            try {
                confMap = ClientCache.getKerberos(typeEnum.getVal()).parseKerberosFromUpload(resource.getRight(), localKerberosConf);
            } catch (IOException e) {
                log.error("添加数据源， kerberos config 解析异常,{}", e.getMessage(), e);
                throw new PubSvcDefineException(String.format("kerberos config 解析异常,Caused by: %s", e.getMessage()), e);
            }
            //设置openKerberos变量表示开启kerberos认证
            DataSourceUtils.setOpenKerberos(dataJson, true);
            DataSourceUtils.setKerberosFile(dataJson, resource.getRight());
        } else {
            DsInfo originSource = dsInfoService.getOneById(dataSourceVO.getId());
            DataSourceUtils.getOriginKerberosConfig(dataJson, originSource.getDataJson());
            localKerberosConf = kerberosService.getLocalKerberosPath(dataSourceVO.getId());
            confMap = fillKerberosConfig(dataSourceVO.getId());
        }
        // 设置前台传入的principals
        setPrincipals(dataJson, confMap);
        //检查链接
        Boolean connValue = checkConnectionWithConf(dataSourceVO, confMap, localKerberosConf);
        if (!connValue) {
            throw new PubSvcDefineException("不能添加连接失败的数据源", ErrorCode.CONFIG_ERROR);
        }
        Long dataSourceId = dataSourceVO.getId();
        //有认证文件上传 进行上传至sftp操作
        Long id = null;
        try {
            if (Objects.nonNull(resource)) {
                if (dataSourceVO.getId() == 0L) {
                    // 没有保存过的数据源需要先保存, 获取自增id
                    dataSourceVO.setKerberosConfig(confMap);
                    dataSourceVO.setDataJson((JSONObject) dataJson.clone());
                    dataSourceVO.setLocalKerberosConf(localKerberosConf);
                    dataSourceId = addOrUpdate(dataSourceVO, userId);
                }
                Map<String, String> sftpMap = kerberosService.getSftpMap(dtuicTenantId);
                //目录转换 - 将临时目录根据数据源ID转移到新的kerberos文件目录
                File localKerberosConfDir = new File(localKerberosConf);
                File newConfDir = new File(kerberosService.getLocalKerberosPath(dataSourceId));
                //如果原来的目录存在 删除原来的文件
                try {
                    FileUtils.deleteDirectory(newConfDir);
                } catch (IOException e) {
                    log.error("删除历史的kerberos文件失败", e);
                }
                //目录转换, temp路径转换成新路径
                localKerberosConfDir.renameTo(newConfDir);
                //上传配置文件到sftp
                String dataSourceKey = kerberosService.getSourceKey(dataSourceId, null);
                KerberosService.uploadDirFinal(sftpMap, newConfDir.getPath(), dataSourceKey);
                confMap.put("kerberosDir", dataSourceKey);
            }
            dataSourceVO.setKerberosConfig(confMap);
            dataSourceVO.setLocalKerberosConf(localKerberosConf);
            dataSourceVO.setDataJson(dataJson);
            dataSourceVO.setId(dataSourceId);
            id = addOrUpdate(dataSourceVO, userId);
        } catch (Exception e) {
            log.error("addOrUpdateSourceWithKerberos error",e);
            throw new PubSvcDefineException(e.getMessage());
        }
        return id;
    }



    /**
     * 添加和修改数据源
     * @param dataSourceVO
     * @param userId
     * @return
     */
    public Long addOrUpdateSource(DataSourceVO dataSourceVO, Long userId) {
        if (!checkConnectionWithConf(dataSourceVO, null, null)) {
            throw new PubSvcDefineException("不能添加连接失败的数据源" + ErrorCode.CONFIG_ERROR);
        }
        return addOrUpdate(dataSourceVO, userId);
    }


    /**
     * 添加和修改数据源信息
     * @param dataSourceVO
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdate(DataSourceVO dataSourceVO, Long userId) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        JSONObject json = dataSourceVO.getDataJson();
        //字段转换
        colMap(json, typeEnum.getVal(), dataSourceVO.getKerberosConfig());
        dataSourceVO.setDataJson(json);
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
     * @param dataSourceVO
     * @return
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
        dsInfo.setTenantId(dataSourceVO.getTenantId());
        dsInfo.setSchemaName(dataSourceVO.getSchemaName());
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        dsInfo.setDataTypeCode(typeEnum.getVal());
        // dataJson
        if (Objects.nonNull(dataSourceVO.getDataJson())) {
            JSONObject dataJson = dataSourceVO.getDataJson();
            if(dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())){
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_ZK_QUORUM,dataJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT,dataJson.get(FormNames.HBASE_PARENT));
                dataJson.put("hbaseConfig",jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos=dataJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG))!=null){
                dataJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,System.currentTimeMillis()));
            }
            dsInfo.setDataJson(DataSourceUtils.getEncodeDataSource(dataJson, true));
            String linkInfo = getDataSourceLinkInfo(dataSourceVO.getDataType(), dataSourceVO.getDataVersion(), dataSourceVO.getDataJson());
            dsInfo.setLinkJson(DataSourceUtils.getEncodeDataSource(linkInfo, true));
        } else if(Strings.isNotBlank(dataSourceVO.getDataJsonString())) {
            JSONObject dataSourceJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
            if(dataSourceVO.getDataType().equals(DataSourceTypeEnum.HBASE2.getDataType())){
                //Hbase需要特殊处理
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormNames.HBASE_QUORUM,dataSourceJson.get(FormNames.HBASE_QUORUM));
                jsonObject.put(FormNames.HBASE_ZK_PARENT,dataSourceJson.get(FormNames.HBASE_PARENT));
                dataSourceJson.put("hbaseConfig",jsonObject);
            }
            JSONObject kerberos;
            if ((kerberos=dataSourceJson.getJSONObject(DataSourceUtils.KERBEROS_CONFIG))!=null){
                dataSourceJson.put(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,kerberos.getOrDefault(DataSourceUtils.KERBEROS_FILE_TIMESTAMP,System.currentTimeMillis()));
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
     * @param dataType
     * @param dataVersion
     * @param dataJson
     * @return
     */
    public String getDataSourceLinkInfo(String dataType, String dataVersion, JSONObject dataJson) {
        List<DsFormField> linkFieldList = formFieldService.findLinkFieldByTypeVersion(dataType, dataVersion);
        if (CollectionUtils.isEmpty(linkFieldList)) {
            return null;
        }
        JSONObject linkJson = new JSONObject();
        for (DsFormField dsFormField : linkFieldList) {
            String value = CommonUtils.getStrFromJson(dataJson, dsFormField.getName());
            if (Strings.isNotBlank(value)) {
                linkJson.put(dsFormField.getName(), value);
            }
        }
        return linkJson.toJSONString();
    }

    /**
     * 解析字段
     * @param json
     * @param type
     * @param kerberosConfig
     * @return
     */
    private void colMap(JSONObject json, Integer type, Map<String, Object> kerberosConfig) {
        if (DataSourceType.getKafkaS().contains(type)) {
            ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, type);
            String brokersAddress = null;

            try {
                brokersAddress = ClientCache.getKafka(type).getAllBrokersAddress(sourceDTO);
            } catch (Exception e) {
                log.error("获取kafka brokersAddress 异常!", e);
                throw new PubSvcDefineException("获取kafka brokersAddress 异常!", e);
            }
            json.put("bootstrapServers", brokersAddress);
        }

        if (kerberosConfig != null) {
            json.put(FormNames.KERBEROS_CONFIG, kerberosConfig);
        }
    }


    /**
     * 通过数据源信息判断数据源的联通性
     * @param dsInfo
     * @return
     */
    public Boolean checkConnectByDsInfo(DsInfo dsInfo) {
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dsInfo.getDataType(), dsInfo.getDataVersion());
        // 测试连通性
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dsInfo.getDataJson(), typeEnum.getVal(), null);
        return ClientCache.getClient(typeEnum.getVal()).testCon(sourceDTO);
    }

    public DataSourceType getHadoopDefaultDataSourceByTenantId(Long tenantId) {
        return DataSourceType.SparkThrift2_1;
    }

    public String setJobDataSourceInfo(String jobStr, Long dtUicTenentId, Integer createModel) {
        JSONObject job = JSONObject.parseObject(jobStr);
        JSONObject jobContent = job.getJSONObject("job");
        JSONObject content = jobContent.getJSONArray("content").getJSONObject(0);
        setPluginDataSourceInfo(content.getJSONObject("reader"), dtUicTenentId, createModel);
        setPluginDataSourceInfo(content.getJSONObject("writer"), dtUicTenentId, createModel);
        return job.toJSONString();
    }


    private void setPluginDataSourceInfo(JSONObject plugin, Long dtUicTenentId, Integer createModel) {
        String pluginName = plugin.getString("name");
        JSONObject param = plugin.getJSONObject("parameter");
        if (PluginName.MySQLD_R.equals(pluginName)) {
            JSONArray connections = param.getJSONArray("connection");
            for (int i = 0; i < connections.size(); i++) {
                JSONObject conn = connections.getJSONObject(i);
                if (!conn.containsKey("sourceId")) {
                    continue;
                }

                BatchDataSource source = getOne(conn.getLong("sourceId"));
                JSONObject json = JSONObject.parseObject(source.getDataJson());
                replaceDataSourceInfoByCreateModel(conn,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(conn,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                replaceDataSourceInfoByCreateModel(conn,"jdbcUrl", Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
            }
        } else {
            if (!param.containsKey("sourceIds")) {
                return;
            }

            List<Long> sourceIds = param.getJSONArray("sourceIds").toJavaList(Long.class);
            if (CollectionUtils.isEmpty(sourceIds)) {
                return;
            }

            BatchDataSource source = getOne(sourceIds.get(0));

            JSONObject json = JSON.parseObject(source.getDataJson());
            Integer sourceType = source.getType();

            if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                    && !DataSourceType.HIVE.getVal().equals(sourceType)
                    && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                    && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                    && !DataSourceType.IMPALA.getVal().equals(sourceType)
                    && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)
                    && !DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                if (conn.get("jdbcUrl") instanceof String) {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                } else {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtil.getStringDefaultEmpty(json, JDBC_URL)),createModel);
                }
            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HDFS.getVal().equals(sourceType)
                    || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,JDBC_URL, JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                    }
                }
                //非meta数据源从高可用配置中取hadoopConf
                if (0 == source.getIsDefault()){
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
                    String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                    if (StringUtils.isNotBlank(hadoopConfig)) {
                        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(hadoopConfig),createModel);
                    }
                }else {
                    //meta数据源从console取配置
                    //拿取最新配置
                    String consoleHadoopConfig = this.getConsoleHadoopConfig(dtUicTenentId);
                    if (StringUtils.isNotBlank(consoleHadoopConfig)) {
                        //替换新path 页面运行fix
                        JSONArray connections = param.getJSONArray("connection");
                        if ((DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) && Objects.nonNull(connections)){
                            JSONObject conn = connections.getJSONObject(0);
                            String hiveTable = conn.getJSONArray("table").get(0).toString();
                            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
                            String hiveTablePath = getHiveTablePath(sourceType, hiveTable, json, kerberosConfig);
                            if (StringUtils.isNotEmpty(hiveTablePath)){
                                replaceDataSourceInfoByCreateModel(param,"path", hiveTablePath.trim(), createModel);
                            }
                        }
                        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(consoleHadoopConfig),createModel);
                        JSONObject hadoopConfJson = JSONObject.parseObject(consoleHadoopConfig);
                        String defaultFs = JsonUtil.getStringDefaultEmpty(hadoopConfJson, "fs.defaultFS");
                        //替换defaultFs
                        replaceDataSourceInfoByCreateModel(param,"defaultFS",defaultFs,createModel);
                    } else {
                        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                        if (StringUtils.isNotBlank(hadoopConfig)) {
                            replaceDataSourceInfoByCreateModel(param, HADOOP_CONFIG, JSONObject.parse(hadoopConfig), createModel);
                        }
                    }
                }
                setSftpConfig(source.getId(), json, dtUicTenentId, param, HADOOP_CONFIG, false);
            } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
                String jsonStr = json.getString(HBASE_CONFIG);
                Map jsonMap = new HashMap();
                if (StringUtils.isNotEmpty(jsonStr)){
                    try {
                        jsonMap = objectMapper.readValue(jsonStr,Map.class);
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
                replaceDataSourceInfoByCreateModel(param,HBASE_CONFIG,jsonMap,createModel);
                if (TaskCreateModelType.GUIDE.getType().equals(createModel)) {
                    setSftpConfig(source.getId(), json, dtUicTenentId, param, HBASE_CONFIG, false);
                }
            } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
                if (json != null){
                    json.entrySet().forEach(bean->{
                        replaceDataSourceInfoByCreateModel(param,bean.getKey(),bean.getValue(),createModel);
                    });
                }
            } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"accessId",json.get("accessId"),createModel);
                replaceDataSourceInfoByCreateModel(param,"accessKey",json.get("accessKey"),createModel);
                replaceDataSourceInfoByCreateModel(param,"project",json.get("project"),createModel);
                replaceDataSourceInfoByCreateModel(param,"endPoint",json.get("endPoint"),createModel);
            } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
                replaceDataSourceInfoByCreateModel(param,"address",json.get("address"),createModel);
            } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"hostPort", JsonUtil.getStringDefaultEmpty(json, "hostPort"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",json.getIntValue("database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
            } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,JDBC_HOSTPORTS,JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, "username"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",JsonUtil.getStringDefaultEmpty(json, "database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, "password"),createModel);
            } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"masterAddresses",JsonUtil.getStringDefaultEmpty(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"others",JsonUtil.getStringDefaultEmpty(json, "others"),createModel);
            } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                String tableLocation =  param.getString(TableLocationType.key());
                replaceDataSourceInfoByCreateModel(param,"dataSourceType", DataSourceType.IMPALA.getVal(),createModel);
                String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
                if (StringUtils.isNotBlank(hadoopConfig)) {
                    replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(hadoopConfig),createModel);
                }
                if (TableLocationType.HIVE.getValue().equals(tableLocation)) {
                    replaceDataSourceInfoByCreateModel(param,"username",JsonUtil.getStringDefaultEmpty(json, JDBC_USERNAME),createModel);
                    replaceDataSourceInfoByCreateModel(param,"password",JsonUtil.getStringDefaultEmpty(json, JDBC_PASSWORD),createModel);
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
                    }
                }
            } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceInceptorDataSource(param, json, createModel, source, dtUicTenentId);
            } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param, "username", JsonUtil.getStringDefaultEmpty(json, "username"), createModel);
                replaceDataSourceInfoByCreateModel(param, "password", JsonUtil.getStringDefaultEmpty(json, "password"), createModel);
                if (param.containsKey("connection")) {
                    JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                    String url = JsonUtil.getStringDefaultEmpty(json, "url");
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
    private void replaceDataSourceInfoByCreateModel(JSONObject jdbcInfo, String key, Object values, Integer createModel){
        Boolean isReplace = TaskCreateModelType.TEMPLATE.getType().equals(createModel) && jdbcInfo.containsKey(key);
        if (isReplace) {
            return;
        }
        jdbcInfo.put(key,values);
    }


    /**
     * 替换Inceptor 相关的数据源信息
     *
     * @param param
     * @param json
     * @param createModel
     * @param source
     * @param dtUicTenentId
     */
    public void replaceInceptorDataSource(JSONObject param, JSONObject json, Integer createModel, BatchDataSource source,
                                          Long dtUicTenentId){
        if (param.containsKey("connection")) {
            JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
            replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtil.getStringDefaultEmpty(json, JDBC_URL),createModel);
        }

        replaceDataSourceInfoByCreateModel(param,HDFS_DEFAULTFS,JsonUtil.getStringDefaultEmpty(json, HDFS_DEFAULTFS),createModel);
        replaceDataSourceInfoByCreateModel(param,HIVE_METASTORE_URIS,JsonUtil.getStringDefaultEmpty(json, HIVE_METASTORE_URIS),createModel);
        String hadoopConfig = JsonUtil.getStringDefaultEmpty(json, HADOOP_CONFIG);
        JSONObject hadoopConfigJson = new JSONObject();
        if (StringUtils.isNotBlank(hadoopConfig)) {
            hadoopConfigJson.putAll(JSONObject.parseObject(hadoopConfig));
        }
        hadoopConfigJson.put(HIVE_METASTORE_URIS, JsonUtil.getStringDefaultEmpty(json, HIVE_METASTORE_URIS));
        replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG, hadoopConfigJson, createModel);

        // 替换表相关的信息
        JSONArray connections = param.getJSONArray("connection");
        JSONObject conn = connections.getJSONObject(0);
        String hiveTableName = conn.getJSONArray("table").get(0).toString();
        Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
        com.dtstack.dtcenter.loader.dto.Table tableInfo = getTableInfo(DataSourceType.INCEPTOR.getVal(), hiveTableName, json, kerberosConfig);

        replaceDataSourceInfoByCreateModel(param,"path", tableInfo.getPath().trim(), createModel);
        replaceDataSourceInfoByCreateModel(param,"schema", tableInfo.getDb(), createModel);
        replaceDataSourceInfoByCreateModel(param,"table", hiveTableName, createModel);
        replaceDataSourceInfoByCreateModel(param,"isTransaction", tableInfo.getIsTransTable(), createModel);

        setSftpConfig(source.getId(), json, dtUicTenentId, param, HADOOP_CONFIG, false);
    }


    /**
     * 添加ftp地址
     * @param sourceId
     * @param json
     * @param dtuicTenantId
     * @param map
     * @param confKey
     */
    private void setSftpConfig(Long sourceId, JSONObject json, Long dtuicTenantId, Map<String, Object> map, String confKey, boolean downloadKerberos) {
        JSONObject kerberosConfig = json.getJSONObject(KERBEROS_CONFIG);
        if (MapUtils.isNotEmpty(kerberosConfig)) {
            Map<String, String> sftpMap = getSftpMap(dtuicTenantId);
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
            String principalFile = conf.getOrDefault("principalFile", "").toString();;
            if (StringUtils.isNotEmpty(principalFile)){
                conf.put("principalFile", getFileName(principalFile));
            }
            conf.put("remoteDir", remoteDir);
            map.put(confKey, conf);

            if (downloadKerberos) {
                //hiveBase中连接数据库需要kerberosConfig
                Map<String, Object> kerberosConfigReplaced = fillKerberosConfig(sourceId);
                map.put("kerberosConfig", kerberosConfigReplaced);
            }

            String krb5Conf = conf.getOrDefault("java.security.krb5.conf", "").toString();
            if (StringUtils.isNotEmpty(krb5Conf)){
                conf.put("java.security.krb5.conf", getFileName(krb5Conf));
            }
            // 开启kerberos认证需要的参数
            conf.put(com.dtstack.batch.engine.rdbms.common.HadoopConfTool.IS_HADOOP_AUTHORIZATION, "true");
            conf.put(com.dtstack.batch.engine.rdbms.common.HadoopConfTool.HADOOP_AUTH_TYPE, "kerberos");
        }
    }

    public Map<String, String> getSftpMap(Long dtuicTenantId) {
        Map<String, String> map = new HashMap<>();
        String cluster = clusterServic.clusterInfo(dtuicTenantId);
        JSONObject clusterObj = JSON.parseObject(cluster);
        JSONObject sftpConfig = clusterObj.getJSONObject(EComponentType.SFTP.getConfName());
        if (Objects.isNull(sftpConfig)) {
            throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_SFTP);
        } else {
            for (String key : sftpConfig.keySet()) {
                map.put(key, sftpConfig.getString(key));
            }
        }
        return map;
    }


    private String getFileName(final String path){
        if (StringUtils.isEmpty(path)){
            return path;
        }
        final String[] split = path.split(File.separator);
        return split[split.length-1];
    }

    /**
     * 获取表信息
     *
     * @param sourceType
     * @param table
     * @param dataJson
     * @param kerberosConfig
     * @return
     */
    private  com.dtstack.dtcenter.loader.dto.Table  getTableInfo(Integer sourceType, String table, JSONObject dataJson, Map<String, Object> kerberosConfig){
        IClient client = ClientCache.getClient(sourceType);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(dataJson, sourceType, kerberosConfig);

        com.dtstack.dtcenter.loader.dto.Table tableInfo = client.getTable(sourceDTO, SqlQueryDTO.builder().tableName(table).build());
        return tableInfo;
    }

    /**
     * 获取hadoopconfig最新配置
     * @param dtUicTenantId
     * @return
     */
    private String getConsoleHadoopConfig(Long dtUicTenantId){
        if(null == dtUicTenantId){
            return null;
        }
        String enginePluginInfo = Engine2DTOService.getEnginePluginInfo(dtUicTenantId, MultiEngineType.HADOOP.getType());
        if(StringUtils.isBlank(enginePluginInfo)){
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(enginePluginInfo);
        return jsonObject.getString(EComponentType.HDFS.getTypeCode() + "");
    }


    /**
     * 获取table location
     *
     * @param sourceType
     * @param table
     * @param dataJson
     * @param kerberosConfig
     * @return
     */
    private String getHiveTablePath(Integer sourceType, String table, JSONObject dataJson, Map<String, Object> kerberosConfig) {
        com.dtstack.dtcenter.loader.dto.Table tableInfo = getTableInfo(sourceType, table, dataJson, kerberosConfig);
        return tableInfo.getPath();
    }

    public BatchDataSource getOne(Long valueOf) {
        return null;
    }

    public void createMateDataSource(Long tenantId, Long userId, String toJSONString, String dataSourceName, Integer dataSourceType, String tenantDesc, String dbName) {
    }
}
