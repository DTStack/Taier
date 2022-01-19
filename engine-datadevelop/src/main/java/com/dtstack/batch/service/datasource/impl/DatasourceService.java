package com.dtstack.batch.service.datasource.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.DataSourceTypeEnum;
import com.dtstack.engine.common.exception.PubSvcDefineException;
import com.dtstack.batch.common.template.Reader;
import com.dtstack.batch.common.template.Setting;
import com.dtstack.batch.common.template.Writer;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.hive.util.SparkThriftConnectionUtils;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.enums.*;
import com.dtstack.batch.mapping.ComponentTypeDataSourceTypeMapping;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.sync.format.TypeFormat;
import com.dtstack.batch.sync.format.writer.HiveWriterFormat;
import com.dtstack.batch.sync.handler.SyncBuilderFactory;
import com.dtstack.batch.sync.job.JobTemplate;
import com.dtstack.batch.sync.job.PluginName;
import com.dtstack.batch.sync.template.*;
import com.dtstack.batch.utils.Asserts;
import com.dtstack.batch.vo.DataSourceVO;
import com.dtstack.batch.vo.TaskResourceParam;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.client.IClient;
import com.dtstack.dtcenter.loader.client.IKerberos;
import com.dtstack.dtcenter.loader.dto.ColumnMetaDTO;
import com.dtstack.dtcenter.loader.dto.SqlQueryDTO;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.kerberos.HadoopConfTool;
import com.dtstack.dtcenter.loader.source.DataBaseType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.common.constrant.FormNames;
import com.dtstack.engine.common.engine.JdbcInfo;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.DtCenterDefException;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.DataSourceUtils;
import com.dtstack.engine.common.util.JsonUtils;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.Strings;
import com.dtstack.engine.domain.BatchDataSource;
import com.dtstack.engine.domain.DsFormField;
import com.dtstack.engine.domain.DsInfo;
import com.dtstack.engine.master.service.ClusterService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.jcraft.jsch.SftpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
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
    private BatchDataSourceTaskRefService dataSourceTaskRefService;

    @Autowired
    private SyncBuilderFactory syncBuilderFactory;

    @Autowired
    private DsTypeService dsTypeService;

    public static final String DSC_INFO_CHANGE_CHANNEL = "dscInfoChangeChannel";

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private ClusterService clusterService;

    /**
     * FIMXE 暂时将数据源读写权限设置在程序    里面
     */
    private static final Map<Integer, Integer> DATASOURCE_PERMISSION_MAP = Maps.newHashMap();

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

    private static final String KRB5_CONF = "java.security.krb5.conf";

    private static final String KRB5_FILE = "krb5.conf";

    private static final String PRINCIPLE_FILE = "principalFile";

    // 数据同步-模版导入 writer 不需要添加默认值的数据源类型
    private static final Set<Integer> notPutValueFoeWriterSourceTypeSet = Sets.newHashSet(DataSourceType.HIVE.getVal(), DataSourceType.HIVE3X.getVal(),
            DataSourceType.HIVE1X.getVal(), DataSourceType.CarbonData.getVal(), DataSourceType.INCEPTOR.getVal(), DataSourceType.SparkThrift2_1.getVal());


    static {
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.MySQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Oracle.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.SQLServer.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.PostgreSQL.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.RDBMS.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HDFS.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.DB2.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Clickhouse.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE1X.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.HIVE3X.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.Phoenix.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.PHOENIX5.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.TiDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.DMDB.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.GREENPLUM6.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.KINGBASE8.getVal(), EDataSourcePermission.READ_WRITE.getType());
        DatasourceService.DATASOURCE_PERMISSION_MAP.put(DataSourceType.INCEPTOR.getVal(), EDataSourcePermission.WRITE.getType());
    }

    /**
     * 解析kerberos文件获取principal列表
     * @param source
     * @param resource
     * @param dtuicTenantId
     * @param projectId
     * @param userId
     * @return
     */
    public List<String> getPrincipalsWithConf(DataSourceVO source, Pair<String, String> resource, Long userId) {
        String localKerberosPath;
        Map<String, Object> kerberosConfig;
        // 获取数据源类型，这里要做type version的改造
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(source.getDataType(),source.getDataVersion());
        IKerberos kerberos = ClientCache.getKerberos(typeEnum.getVal());
        if (Objects.nonNull(resource)) {
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId);
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
     * 测试联通性
     * @param id
     * @return
     */
    public Boolean checkConnectionById(Long id) {
        BatchDataSource dataSource = getOne(id);
        DataSourceVO dataSourceVO = new DataSourceVO();
        BeanUtils.copyProperties(dataSource, dataSourceVO);
        return checkConnectionWithConf(dataSourceVO, null, null);
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
            localKerberosPath = kerberosService.getTempLocalKerberosConf(userId);
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
     * @param userId
     * @param dtuicTenantId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateSourceWithKerberos(DataSourceVO dataSourceVO, Pair<String, String> resource, Long userId, Long dtuicTenantId) {
        Map<String, Object> confMap;
        JSONObject dataJson = DataSourceUtils.getDataSourceJson(dataSourceVO.getDataJsonString());
        dataSourceVO.setDataJson(dataJson);
        List<Integer> list = JSON.parseObject(dataSourceVO.getAppTypeListString(), List.class);
        dataSourceVO.setAppTypeList(list);
        String localKerberosConf;
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.typeVersionOf(dataSourceVO.getDataType(), dataSourceVO.getDataVersion());
        if (Objects.nonNull(resource)) {
            //resource不为空表示本地上传文件
            localKerberosConf = kerberosService.getTempLocalKerberosConf(userId);
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
            String value = JsonUtils.getStrFromJson(dataJson, dsFormField.getName());
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
                replaceDataSourceInfoByCreateModel(conn,"username",JsonUtils.getStrFromJson(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(conn,"password",JsonUtils.getStrFromJson(json, JDBC_PASSWORD),createModel);
                replaceDataSourceInfoByCreateModel(conn,"jdbcUrl", Collections.singletonList(JsonUtils.getStrFromJson(json, JDBC_URL)),createModel);
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
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtils.getStrFromJson(json, JDBC_USERNAME),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtils.getStrFromJson(json, JDBC_PASSWORD),createModel);
                JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                if (conn.get("jdbcUrl") instanceof String) {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtils.getStrFromJson(json, JDBC_URL),createModel);
                } else {
                    replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",Arrays.asList(JsonUtils.getStrFromJson(json, JDBC_URL)),createModel);
                }
            } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HDFS.getVal().equals(sourceType)
                    || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,JDBC_URL, JsonUtils.getStrFromJson(json, JDBC_URL),createModel);
                    }
                }
                //非meta数据源从高可用配置中取hadoopConf
                if (0 == source.getIsDefault()){
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS),createModel);
                    String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
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
                        String defaultFs = JsonUtils.getStrFromJson(hadoopConfJson, "fs.defaultFS");
                        //替换defaultFs
                        replaceDataSourceInfoByCreateModel(param,"defaultFS",defaultFs,createModel);
                    } else {
                        String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
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
                replaceDataSourceInfoByCreateModel(param,"hostPort", JsonUtils.getStrFromJson(json, "hostPort"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",json.getIntValue("database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtils.getStrFromJson(json, "password"),createModel);
            } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,JDBC_HOSTPORTS,JsonUtils.getStrFromJson(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"username",JsonUtils.getStrFromJson(json, "username"),createModel);
                replaceDataSourceInfoByCreateModel(param,"database",JsonUtils.getStrFromJson(json, "database"),createModel);
                replaceDataSourceInfoByCreateModel(param,"password",JsonUtils.getStrFromJson(json, "password"),createModel);
            } else if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                replaceDataSourceInfoByCreateModel(param,"masterAddresses",JsonUtils.getStrFromJson(json, JDBC_HOSTPORTS),createModel);
                replaceDataSourceInfoByCreateModel(param,"others",JsonUtils.getStrFromJson(json, "others"),createModel);
            } else if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                String tableLocation =  param.getString(TableLocationType.key());
                replaceDataSourceInfoByCreateModel(param,"dataSourceType", DataSourceType.IMPALA.getVal(),createModel);
                String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
                if (StringUtils.isNotBlank(hadoopConfig)) {
                    replaceDataSourceInfoByCreateModel(param,HADOOP_CONFIG,JSONObject.parse(hadoopConfig),createModel);
                }
                if (TableLocationType.HIVE.getValue().equals(tableLocation)) {
                    replaceDataSourceInfoByCreateModel(param,"username",JsonUtils.getStrFromJson(json, JDBC_USERNAME),createModel);
                    replaceDataSourceInfoByCreateModel(param,"password",JsonUtils.getStrFromJson(json, JDBC_PASSWORD),createModel);
                    replaceDataSourceInfoByCreateModel(param,"defaultFS",JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS),createModel);
                    if (param.containsKey("connection")) {
                        JSONObject conn = param.getJSONArray("connection").getJSONObject(0);
                        replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtils.getStrFromJson(json, JDBC_URL),createModel);
                    }
                }
            } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
                replaceInceptorDataSource(param, json, createModel, source, dtUicTenentId);
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
            replaceDataSourceInfoByCreateModel(conn,"jdbcUrl",JsonUtils.getStrFromJson(json, JDBC_URL),createModel);
        }

        replaceDataSourceInfoByCreateModel(param,HDFS_DEFAULTFS,JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS),createModel);
        replaceDataSourceInfoByCreateModel(param,HIVE_METASTORE_URIS,JsonUtils.getStrFromJson(json, HIVE_METASTORE_URIS),createModel);
        String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
        JSONObject hadoopConfigJson = new JSONObject();
        if (StringUtils.isNotBlank(hadoopConfig)) {
            hadoopConfigJson.putAll(JSONObject.parseObject(hadoopConfig));
        }
        hadoopConfigJson.put(HIVE_METASTORE_URIS, JsonUtils.getStrFromJson(json, HIVE_METASTORE_URIS));
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

    public Map<String, String> getSftpMap(Long tenantId) {
        JSONObject configByKey = clusterService.getConfigByKey(tenantId, EComponentType.SFTP.getConfName(), null);
        try {
            return PublicUtil.objectToObject(configByKey,Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new PubSvcDefineException(ErrorCode.SFTP_NOT_FOUND);
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

    /**
     * 配置或修改离线任务
     *
     * @param isFilter 获取数据同步脚本时候是否进行过滤用户名密码操作
     * @return
     * @throws IOException
     */
    public String getSyncSql(final TaskResourceParam param, boolean isFilter) {
        final Map<String, Object> sourceMap = param.getSourceMap();//来源集合
        final Map<String, Object> targetMap = param.getTargetMap();//目标集合
        final Map<String, Object> settingMap = param.getSettingMap();//流控、错误集合
        try {
            //清空资源和任务的关联关系
            this.dataSourceTaskRefService.removeRef(param.getId());

            this.setReaderJson(sourceMap, param.getId(), param.getTenantId(), isFilter);
            this.setWriterJson(targetMap, param.getId(), param.getTenantId(), isFilter);
            Reader reader = null;
            Writer writer = null;
            Setting setting = null;

            final Integer sourceType = Integer.parseInt(sourceMap.get("dataSourceType").toString());
            final Integer targetType = Integer.parseInt(targetMap.get("dataSourceType").toString());

            if (!this.checkDataSourcePermission(sourceType, EDataSourcePermission.READ.getType())) {
                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_INPUT);
            }

            if (!this.checkDataSourcePermission(targetType, EDataSourcePermission.WRITE.getType())) {
                throw new RdosDefineException(ErrorCode.SOURCE_CAN_NOT_AS_OUTPUT);
            }

            final List<Long> sourceIds = (List<Long>) sourceMap.get("sourceIds");
            final List<Long> targetIds = (List<Long>) targetMap.get("sourceIds");

            reader = this.syncReaderBuild(sourceType, sourceMap, sourceIds);
            writer = this.syncWriterBuild(targetType, targetIds, targetMap, reader);

            setting = PublicUtil.objectToObject(settingMap, DefaultSetting.class);

            //检查有效性
            if (writer instanceof HiveWriter) {
                final HiveWriter hiveWriter = (HiveWriter) writer;
                if (!hiveWriter.isValid()) {
                    throw new RdosDefineException(hiveWriter.getErrMsg());
                }
            }

            if (param.getCreateModel() == TaskCreateModelType.TEMPLATE.getType()) {  //脚本模式直接返回
                return this.getJobText(this.putDefaultEmptyValueForReader(sourceType, reader),
                        this.putDefaultEmptyValueForWriter(targetType, writer), this.putDefaultEmptyValueForSetting(setting));
            }

            //获得数据同步job.xml的配置
            final String jobXml = this.getJobText(reader, writer, setting);
            final String parserXml = this.getParserText(sourceMap, targetMap, settingMap);
            final JSONObject sql = new JSONObject(3);
            sql.put("job", jobXml);
            sql.put("parser", parserXml);
            sql.put("createModel", TaskCreateModelType.GUIDE.getType());

            this.batchTaskParamService.checkParams(this.batchTaskParamService.checkSyncJobParams(sql.toJSONString()), param.getTaskVariables());
            return sql.toJSONString();
        } catch (final Exception e) {
            log.error("{}", e);
            throw new RdosDefineException("解析同步任务失败: " + e.getMessage(), ErrorCode.SERVER_EXCEPTION);
        }
    }

    /**
     * 解析数据源连接信息
     *
     * @param map       不允许为空
     * @param taskId
     * @param tenantId
     * @param isFilter 是否过滤数据源账号密码信息
     */
    public void setReaderJson(Map<String, Object> map, Long taskId, Long tenantId, boolean isFilter) throws Exception {
        List<Long> sourceIds = new ArrayList<>();
        if (map == null){
            throw new RdosDefineException("传入信息有误");
        }

        if (map != null && !map.containsKey("sourceId")) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }
        Long dataSourceId = MapUtils.getLong(map, "sourceId", 0L);
        BatchDataSource source = getOne(dataSourceId);
        Integer sourceType = source.getType();
        map.put("type",sourceType);
        // 包含 sourceList 为分库分表读取,兼容原来的单表读取逻辑
        if ((DataSourceType.MySQL.getVal().equals(sourceType) || DataSourceType.TiDB.getVal().equals(sourceType)) && map.containsKey("sourceList")) {
            List<Object> sourceList = (List<Object>) map.get("sourceList");
            JSONArray connections = new JSONArray();
            for (Object dataSource : sourceList) {
                Map<String, Object> sourceMap = (Map<String, Object>) dataSource;
                Long sourceId = Long.parseLong(sourceMap.get("sourceId").toString());
                BatchDataSource batchDataSource = getOne(sourceId);

                JSONObject json = JSON.parseObject(batchDataSource.getDataJson());
                JSONObject conn = new JSONObject();
                if (!isFilter) {
                    conn.put("username", JsonUtils.getStrFromJson(json, JDBC_USERNAME));
                    conn.put("password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD));
                }
                conn.put("jdbcUrl", Collections.singletonList(JsonUtils.getStrFromJson(json, JDBC_URL)));

                if (sourceMap.get("tables") instanceof String) {
                    conn.put("table", Collections.singletonList(sourceMap.get("tables")));
                } else {
                    conn.put("table", sourceMap.get("tables"));
                }

                conn.put("type", batchDataSource.getType());
                conn.put("sourceId", sourceId);

                connections.add(conn);
                sourceIds.add(sourceId);

                sourceMap.put("name", batchDataSource.getDataName());
                if (map.get("source") == null) {
                    map.put("source", batchDataSource);
                }
                if (map.get("datasourceType") == null) {
                    map.put("dataSourceType", batchDataSource.getType());
                }
            }

            Map<String, Object> sourceMap = (Map<String, Object>) sourceList.get(0);
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("sourceId", sourceMap.get("sourceId"));
            map.put("name", sourceMap.get("name"));
            map.put("type", dataBaseType);
            map.put("connections", connections);
            processTable(map);
        } else {
            sourceIds.add(dataSourceId);
            Long sourceId = source.getId();
            map.put("source", source);
            map.put("dataSourceType", source.getType());
            JSONObject json = JSON.parseObject(source.getDataJson());
            // 根据jdbc信息 替换map中的信息
            replaceJdbcInfoByDataJsonToMap(map, sourceId, source, tenantId, json, sourceType);
            if (DataSourceType.Kudu.getVal().equals(sourceType)) {
                syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
                setSftpConfig(sourceId, json, tenantId, map, "hadoopConfig");
            }
            if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
                syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setReaderJson(map, json,fillKerberosConfig(sourceId));
                setSftpConfig(sourceId, json, tenantId, map, "hadoopConfig");
            }
        }

        // isFilter为true表示过滤数据源信息，移除相关属性
        if (isFilter) {
            map.remove("username");
            map.remove("password");

            //S3数据源不需要移除 accessKey
            if(!DataSourceType.AWS_S3.getVal().equals(sourceType)){
                map.remove("accessKey");
            }
        }

        map.put("sourceIds", sourceIds);

        if (taskId != null) {
            for (Long sourceId : sourceIds) {
                //插入资源和任务的关联关系
                dataSourceTaskRefService.addRef(sourceId, taskId, tenantId);
            }
        }
    }

    /**
     * 设置write属性
     *
     * @param map
     * @param taskId
     * @param tenantId
     * @param isFilter 是否过滤账号密码
     * @throws Exception
     */
    public void setWriterJson(Map<String, Object> map, Long taskId, Long tenantId, boolean isFilter) throws Exception {
        if (map.get("sourceId") == null) {
            throw new RdosDefineException(ErrorCode.DATA_SOURCE_NOT_SET);
        }

        Long sourceId = Long.parseLong(map.get("sourceId").toString());
        BatchDataSource source = getOne(sourceId);
        Map<String,Object> kerberos = fillKerberosConfig(sourceId);
        map.put("sourceIds", Arrays.asList(sourceId));
        map.put("source", source);

        JSONObject json = JSON.parseObject(source.getDataJson());
        map.put("dataSourceType", source.getType());
        Integer sourceType = source.getType();
        // 根据jdbc信息 替换map中的信息
        replaceJdbcInfoByDataJsonToMap(map, sourceId, source, tenantId, json, sourceType);

        if (DataSourceType.Kudu.getVal().equals(sourceType)) {
            syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).setWriterJson(map, json,kerberos);
            setSftpConfig(sourceId, json, tenantId, map, HADOOP_CONFIG);
        }

        if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
            syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).setWriterJson(map, json,kerberos);
            setSftpConfig(sourceId, json, tenantId, map, HADOOP_CONFIG);
        }

        if (isFilter) {
            map.remove("username");
            map.remove("password");

            //S3数据源不需要移除 accessKey
            if(!DataSourceType.AWS_S3.getVal().equals(sourceType)){
                map.remove("accessKey");
            }
        }

        if (taskId != null) {
            //插入资源和任务的关联关系
            dataSourceTaskRefService.addRef(sourceId, taskId, tenantId);
        }
    }

    /**
     * 根据dataJson 替换map中 jdbc信息
     *
     * @param map
     * @param sourceId
     * @param source
     * @param dtuicTenantId
     * @param json
     * @param sourceType
     * @throws Exception
     */
    private void replaceJdbcInfoByDataJsonToMap(Map<String, Object> map, Long sourceId, BatchDataSource source, Long dtuicTenantId, JSONObject json, Integer sourceType) throws Exception {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && !DataSourceType.HIVE.getVal().equals(sourceType)
                && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)
                && !DataSourceType.IMPALA.getVal().equals(sourceType)
                && !DataSourceType.CarbonData.getVal().equals(sourceType)
                && !DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
            map.put("type", sourceType);
            map.put("password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD));
            map.put("username", JsonUtils.getStrFromJson(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL));
            processTable(map);
        } else if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            map.put("isDefaultSource", source.getIsDefault() == 1 ? true : false);
            map.put("type", sourceType);
            map.put("password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD));
            map.put("username", JsonUtils.getStrFromJson(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL));
            map.put("partition", map.get(HIVE_PARTITION));
            map.put("defaultFS", JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS));
            this.checkLastHadoopConfig(map, json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, HADOOP_CONFIG);
        } else if (DataSourceType.HDFS.getVal().equals(sourceType)) {
            map.put("defaultFS", JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS));
            this.checkLastHadoopConfig(map,json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, HADOOP_CONFIG);
        } else if (DataSourceType.HBASE.getVal().equals(sourceType)) {
            String jsonStr = json.getString(HBASE_CONFIG);
            Map jsonMap = new HashMap();
            if (StringUtils.isNotEmpty(jsonStr)){
                jsonMap = objectMapper.readValue(jsonStr,Map.class);
            }
            map.put("hbaseConfig", jsonMap);
            setSftpConfig(sourceId, json, dtuicTenantId, map, "hbaseConfig");
        } else if (DataSourceType.FTP.getVal().equals(sourceType)) {
            map.putAll(json);
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
            map.put("accessId", json.get("accessId"));
            map.put("accessKey", json.get("accessKey"));
            map.put("project", json.get("project"));
            map.put("endPoint", json.get("endPoint"));
        } else if ((DataSourceType.ES.getVal().equals(sourceType))) {
            map.put("address", json.get("address"));
            map.put("username", JsonUtils.getStrFromJson(json, "username"));
            map.put("password", JsonUtils.getStrFromJson(json, "password"));
        } else if (DataSourceType.REDIS.getVal().equals(sourceType)) {
            map.put("type", "string");
            map.put("hostPort", JsonUtils.getStrFromJson(json, "hostPort"));
            map.put("database", json.getIntValue("database"));
            map.put("password", JsonUtils.getStrFromJson(json, "password"));
        } else if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
            map.put(JDBC_HOSTPORTS, JsonUtils.getStrFromJson(json, JDBC_HOSTPORTS));
            map.put("username", JsonUtils.getStrFromJson(json, "username"));
            map.put("database", JsonUtils.getStrFromJson(json, "database"));
            map.put("password", JsonUtils.getStrFromJson(json, "password"));
        } else if (DataSourceType.AWS_S3.getVal().equals(sourceType)) {
            map.put("accessKey", JsonUtils.getStrFromJson(json, "accessKey"));
            map.put("secretKey", JsonUtils.getStrFromJson(json, "secretKey"));
            map.put("region", JsonUtils.getStrFromJson(json, "region"));
        } else if (DataSourceType.INCEPTOR.getVal().equals(sourceType)) {
            DataBaseType dataBaseType = DataSourceDataBaseType.getBaseTypeBySourceType(sourceType);
            map.put("type", dataBaseType);
            map.put("password", JsonUtils.getStrFromJson(json, JDBC_PASSWORD));
            map.put("username", JsonUtils.getStrFromJson(json, JDBC_USERNAME));
            map.put("jdbcUrl", JsonUtils.getStrFromJson(json, JDBC_URL));
            map.put("partition", map.get(HIVE_PARTITION));
            map.put("defaultFS", JsonUtils.getStrFromJson(json, HDFS_DEFAULTFS));
            map.put("hiveMetastoreUris", JsonUtils.getStrFromJson(json, HIVE_METASTORE_URIS));
            checkLastHadoopConfig(map, json);
            setSftpConfig(sourceId, json, dtuicTenantId, map, "hadoopConfig");
        } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            map.put("username", JsonUtils.getStrFromJson(json, "username"));
            map.put("password", JsonUtils.getStrFromJson(json, "password"));
            map.put("url", JsonUtils.getStrFromJson(json, "url"));
        }
    }

    private void processTable(Map<String, Object> map) {
        Object table = map.get("table");
        List<String> tables = new ArrayList<>();
        if (table instanceof String) {
            tables.add(table.toString());
        } else {
            tables.addAll((List<String>) table);
        }

        map.put("table", tables);
    }

    /**
     * 获取最新的hadoopConfig 进行替换
     * @param map
     * @param json
     */
    private void checkLastHadoopConfig(Map<String, Object> map, JSONObject json) {
        //拿取最新配置
        String hadoopConfig = JsonUtils.getStrFromJson(json, HADOOP_CONFIG);
        if (StringUtils.isNotBlank(hadoopConfig)) {
            map.put(HADOOP_CONFIG, JSON.parse(hadoopConfig));
        }
    }

    /**
     * 校验数据源可以使用的场景---读写
     * 如果数据源没有添加到关系里面,默认为true
     * FIXME 暂时先把对应关系写在程序里面
     *
     * @return
     */
    private boolean checkDataSourcePermission(int dataSourceType, int targetType) {
        Integer permission = DATASOURCE_PERMISSION_MAP.get(dataSourceType);
        if (permission == null) {
            return true;
        }

        return (permission & targetType) == targetType;

    }

    private Reader syncReaderBuild(final Integer sourceType, final Map<String, Object> sourceMap, final List<Long> sourceIds) throws IOException {

        Reader reader = null;
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && !DataSourceType.HIVE.getVal().equals(sourceType)
                && !DataSourceType.HIVE1X.getVal().equals(sourceType)
                && !DataSourceType.HIVE3X.getVal().equals(sourceType)
                && !DataSourceType.CarbonData.getVal().equals(sourceType)
                && !DataSourceType.IMPALA.getVal().equals(sourceType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, RDBReader.class);
            ((RDBBase) reader).setSourceIds(sourceIds);
            return reader;
        }

        if (DataSourceType.HDFS.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HDFSReader.class);
        }

        if (DataSourceType.HIVE.getVal().equals(sourceType) || DataSourceType.HIVE3X.getVal().equals(sourceType) || DataSourceType.HIVE1X.getVal().equals(sourceType) || DataSourceType.SparkThrift2_1.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HiveReader.class);
        }

        if (DataSourceType.HBASE.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, HBaseReader.class);
        }

        if (DataSourceType.FTP.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, FtpReader.class);
            if (sourceMap.containsKey("isFirstLineHeader") && (Boolean) sourceMap.get("isFirstLineHeader")) {
                ((FtpReader) reader).setFirstLineHeader(true);
            } else {
                ((FtpReader) reader).setFirstLineHeader(false);
            }
            return reader;
        }

        if (DataSourceType.MAXCOMPUTE.getVal().equals(sourceType)) {
            reader = PublicUtil.objectToObject(sourceMap, OdpsReader.class);
            ((OdpsBase) reader).setSourceId(sourceIds.get(0));
            return reader;
        }

        if (DataSourceType.ES.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, EsReader.class);
        }

        if (DataSourceType.MONGODB.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, MongoDbReader.class);
        }

        if (DataSourceType.CarbonData.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, CarbonDataReader.class);
        }

        if (DataSourceType.Kudu.getVal().equals(sourceType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncReaderBuild(sourceMap, sourceIds);
        }

        if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, InfluxDBReader.class);
        }

        if (DataSourceType.IMPALA.getVal().equals(sourceType)) {
            //setSftpConf时，设置的hdfsConfig和sftpConf
            if (sourceMap.containsKey(HADOOP_CONFIG)){
                Object impalaConfig = sourceMap.get(HADOOP_CONFIG);
                if (impalaConfig instanceof Map){
                    sourceMap.put(HADOOP_CONFIG,impalaConfig);
                    sourceMap.put("sftpConf",((Map) impalaConfig).get("sftpConf"));
                }
            }
            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncReaderBuild(sourceMap, sourceIds);
        }

        if (DataSourceType.AWS_S3.getVal().equals(sourceType)) {
            return PublicUtil.objectToObject(sourceMap, AwsS3Reader.class);
        }

        throw new RdosDefineException("暂不支持" + DataSourceType.getSourceType(sourceType).name() +"作为数据同步的源");
    }

    private Writer syncWriterBuild(final Integer targetType, final List<Long> targetIds, final Map<String, Object> targetMap, final Reader reader) throws IOException {
        Writer writer = null;

        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
                && !DataSourceType.HIVE.getVal().equals(targetType)
                && !DataSourceType.HIVE1X.getVal().equals(targetType)
                && !DataSourceType.HIVE3X.getVal().equals(targetType)
                && !DataSourceType.IMPALA.getVal().equals(targetType)
                && !DataSourceType.CarbonData.getVal().equals(targetType)
                && !DataSourceType.SparkThrift2_1.getVal().equals(targetType)
                && !DataSourceType.INCEPTOR.getVal().equals(targetType)) {
            writer = PublicUtil.objectToObject(targetMap, RDBWriter.class);
            ((RDBBase) writer).setSourceIds(targetIds);
            return writer;
        }

        if (DataSourceType.HDFS.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, HDFSWriter.class);
        }

        if (DataSourceType.HIVE.getVal().equals(targetType) || DataSourceType.HIVE3X.getVal().equals(targetType) || DataSourceType.HIVE1X.getVal().equals(targetType) || DataSourceType.SparkThrift2_1.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, HiveWriter.class);
        }

        if (DataSourceType.FTP.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, FtpWriter.class);
        }

        if (DataSourceType.ES.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, EsWriter.class);
        }

        if (DataSourceType.HBASE.getVal().equals(targetType)) {
            targetMap.put("hbaseConfig",targetMap.get("hbaseConfig"));
            writer = PublicUtil.objectToObject(targetMap, HBaseWriter.class);
            HBaseWriter hbaseWriter = (HBaseWriter) writer;
            List<String> sourceColNames = new ArrayList<>();
            List<Map<String,String>> columnList = (List<Map<String, String>>) targetMap.get("column");
            for (Map<String,String> column : columnList){
                if (column.containsKey("key")){
                    sourceColNames.add(column.get("key"));
                }
            }
            hbaseWriter.setSrcColumns(sourceColNames);
            return writer;
        }

        if (DataSourceType.MAXCOMPUTE.getVal().equals(targetType)) {
            writer = PublicUtil.objectToObject(targetMap, OdpsWriter.class);
            ((OdpsBase) writer).setSourceId(targetIds.get(0));
            return writer;
        }

        if (DataSourceType.REDIS.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, RedisWriter.class);
        }

        if (DataSourceType.MONGODB.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, MongoDbWriter.class);
        }

        if (DataSourceType.CarbonData.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, CarbonDataWriter.class);
        }

        if (DataSourceType.Kudu.getVal().equals(targetType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.Kudu.getVal()).syncWriterBuild(targetIds, targetMap, reader);
        }

        if (DataSourceType.IMPALA.getVal().equals(targetType)) {
            return syncBuilderFactory.getSyncBuilder(DataSourceType.IMPALA.getVal()).syncWriterBuild(targetIds, targetMap, reader);
        }

        if (DataSourceType.AWS_S3.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, AwsS3Writer.class);
        }

        if (DataSourceType.INCEPTOR.getVal().equals(targetType)) {
            return PublicUtil.objectToObject(targetMap, InceptorWriter.class);
        }

        throw new RdosDefineException("暂不支持" + DataSourceType.getSourceType(targetType).name() +"作为数据同步的目标");
    }

    private void setSftpConfig(Long sourceId, JSONObject json, Long dtuicTenantId, Map<String, Object> map, String confKey) {
        setSftpConfig(sourceId, json, dtuicTenantId, map, confKey, true);
    }

    /**
     * @author toutian
     */
    private String getJobText(final Reader reader,
                              final Writer writer,
                              final Setting setting) {

        return new JobTemplate() {
            @Override
            public Reader newReader() {
                return reader;
            }

            @Override
            public Writer newWrite() {
                return writer;
            }

            @Override
            public Setting newSetting() {
                return setting;
            }
        }.toJobJsonString();
    }

    /**
     * 向导模式，填充reader的默认信息
     * @param sourceType
     * @param reader
     * @return
     */
    private Reader putDefaultEmptyValueForReader(int sourceType, Reader reader) {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(sourceType))
                && DataSourceType.HIVE.getVal() != sourceType
                && DataSourceType.HIVE1X.getVal() != sourceType
                && DataSourceType.HIVE3X.getVal() != sourceType
                && DataSourceType.SparkThrift2_1.getVal() != sourceType
                && DataSourceType.CarbonData.getVal() != sourceType) {
            RDBReader rdbReader = (RDBReader) reader;
            rdbReader.setWhere("");
            rdbReader.setSplitPK("");
            return rdbReader;
        } else if (DataSourceType.ES.getVal() == sourceType) {
            EsReader esReader = (EsReader) reader;
            JSONObject obj = new JSONObject();
            obj.put("col", "");
            JSONObject query = new JSONObject();
            query.put("match", obj);
            esReader.setQuery(query);
            JSONObject column = new JSONObject();
            column.put("key", "col1");
            column.put("type", "string");
            esReader.getColumn().add(column);
            return esReader;
        } else if (DataSourceType.FTP.getVal() == sourceType) {
            FtpReader ftpReader = (FtpReader) reader;
            ftpReader.setPath("/");
            return ftpReader;
        } else if (DataSourceType.INFLUXDB.getVal().equals(sourceType)) {
            InfluxDBReader influxDBReader = (InfluxDBReader) reader;
            influxDBReader.setWhere("");
            influxDBReader.setSplitPK("");
            return influxDBReader;
        }
        return reader;
    }

    private Writer putDefaultEmptyValueForWriter(int targetType, Writer writer) {
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(targetType))
                && !notPutValueFoeWriterSourceTypeSet.contains(targetType)){
            RDBWriter rdbWriter = (RDBWriter) writer;
            rdbWriter.setPostSql("");
            rdbWriter.setPostSql("");
            rdbWriter.setSession("");
            if (DataSourceType.GREENPLUM6.getVal() == targetType){
                rdbWriter.setWriteMode("insert");
            }else {
                rdbWriter.setWriteMode("replace");
            }
            return rdbWriter;
        } else if (DataSourceType.ES.getVal() == targetType) {
            EsWriter esWriter = (EsWriter) writer;
            esWriter.setType("");
            esWriter.setIndex("");
            JSONObject column = new JSONObject();
            column.put("key", "col1");
            column.put("type", "string");
            JSONObject idColumn = new JSONObject();
            idColumn.put("index", 0);
            idColumn.put("type", "int");
            esWriter.getIdColumn().add(idColumn);
            return esWriter;
        }
        return writer;
    }

    private Setting putDefaultEmptyValueForSetting(Setting setting) {
        DefaultSetting defaultSetting = (DefaultSetting) setting;
        defaultSetting.setSpeed(1.0);
        defaultSetting.setRecord(0);
        defaultSetting.setPercentage(0.0);
        return defaultSetting;
    }

    public String getParserText(final Map<String, Object> sourceMap,
                                final Map<String, Object> targetMap,
                                final Map<String, Object> settingMap) throws Exception {

        JSONObject parser = new JSONObject(4);
        parser.put("sourceMap", getSourceMap(sourceMap));
        parser.put("targetMap", getTargetMap(targetMap));
        parser.put("setting", settingMap);

        JSONObject keymap = new JSONObject(2);
        keymap.put("source", MapUtils.getObject(sourceMap, "column"));
        keymap.put("target", MapUtils.getObject(targetMap, "column"));
        parser.put("keymap", keymap);

        return parser.toJSONString();
    }

    private Map<String, Object> getSourceMap(Map<String, Object> sourceMap) {
        BatchDataSource source = (BatchDataSource) sourceMap.get("source");

        Map<String, Object> typeMap = new HashMap<>(6);
        typeMap.put("type", source.getType());

        Object obj = JSON.parse(JSON.toJSONString(MapUtils.getObject(sourceMap, "column")));
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(source.getType())) && !DataSourceType.IMPALA.getVal().equals(source.getType())) {
            if (DataSourceType.HIVE.getVal().equals(source.getType()) || DataSourceType.HIVE3X.getVal().equals(source.getType()) || DataSourceType.HIVE1X.getVal().equals(source.getType()) || DataSourceType.SparkThrift2_1.getVal().equals(source.getType())) {
                typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
            }

            if (!DataSourceType.HIVE.getVal().equals(source.getType()) && !DataSourceType.HIVE3X.getVal().equals(source.getType()) && !DataSourceType.HIVE1X.getVal().equals(source.getType())
                    && !DataSourceType.CarbonData.getVal().equals(source.getType()) && !DataSourceType.SparkThrift2_1.getVal().equals(source.getType())) {
                String table = ((List<String>) sourceMap.get("table")).get(0);
                JSONArray oriCols = (JSONArray) obj;
                List<JSONObject> dbCols = this.getTableColumn(source, table, Objects.isNull(sourceMap.get("schema")) ? null : sourceMap.get("schema").toString());

                if (oriCols.get(0) instanceof String) {//老版本存在字符串数组
                    obj = dbCols;
                } else {
                    Set<String> keys = new HashSet<>(oriCols.size());
                    for (int i = 0; i < oriCols.size(); i++) {
                        keys.add(oriCols.getJSONObject(i).getString("key"));
                    }

                    List<JSONObject> newCols = new ArrayList<>();
                    for (JSONObject dbCol : dbCols) {
                        JSONObject col = null;
                        for (Object oriCol : oriCols) {
                            if (((JSONObject) oriCol).getString("key").equals(dbCol.getString("key"))) {
                                col = (JSONObject) oriCol;
                                break;
                            }
                        }

                        if (col == null) {
                            col = dbCol;
                        }

                        newCols.add(col);
                    }

                    //加上常量字段信息
                    for (Object oriCol : oriCols) {
                        if ("string".equalsIgnoreCase(((JSONObject) oriCol).getString("type"))) {
                            //去重
                            if(!keys.contains(((JSONObject) oriCol).getString("key"))){
                                newCols.add((JSONObject) oriCol);
                            }
                        }
                    }
                    obj = newCols;
                }
            }

            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            typeMap.put("splitPK", MapUtils.getString(sourceMap, "splitPK"));
            typeMap.put("table", sourceMap.get("table"));
        } else if (DataSourceType.HDFS.getVal().equals(source.getType())) {
            typeMap.put("path", MapUtils.getString(sourceMap, "path"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("fileType", MapUtils.getString(sourceMap, "fileType"));
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
        } else if (DataSourceType.HBASE.getVal().equals(source.getType())) {
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put("startRowkey", MapUtils.getString(sourceMap, "startRowkey"));
            typeMap.put("endRowkey", MapUtils.getString(sourceMap, "endRowkey"));
            typeMap.put("isBinaryRowkey", MapUtils.getString(sourceMap, "isBinaryRowkey"));
            typeMap.put("scanCacheSize", MapUtils.getString(sourceMap, "scanCacheSize"));
            typeMap.put("scanBatchSize", MapUtils.getString(sourceMap, "scanBatchSize"));
        } else if (DataSourceType.FTP.getVal().equals(source.getType())) {
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("path", sourceMap.get("path"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("isFirstLineHeader", MapUtils.getBooleanValue(sourceMap, "isFirstLineHeader"));
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(source.getType())) {
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put("partition", MapUtils.getString(sourceMap, "partition"));
        } else if (DataSourceType.Kudu.getVal().equals(source.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(sourceMap, "table")), "表名不能为空");
            String table = MapUtils.getString(sourceMap, "table");
            typeMap.put("table", table);
            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            obj = this.getTableColumn(source, table, null);
        } else if (DataSourceType.IMPALA.getVal().equals(source.getType())) {
            typeMap.put("table", MapUtils.getString(sourceMap, "table"));
            typeMap.put(TableLocationType.key(), MapUtils.getString(sourceMap, TableLocationType.key()));
            Optional.ofNullable(MapUtils.getString(sourceMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
        } else if (DataSourceType.AWS_S3.getVal().equals(source.getType())) {
            typeMap.put("bucket", MapUtils.getString(sourceMap, "bucket"));
            typeMap.put("objects", MapUtils.getObject(sourceMap, "objects"));
            typeMap.put("fieldDelimiter", MapUtils.getString(sourceMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(sourceMap, "encoding"));
            typeMap.put("isFirstLineHeader", MapUtils.getBoolean(sourceMap, "isFirstLineHeader"));
        } else if (DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
            typeMap.put("customSql", MapUtils.getString(sourceMap, "customSql"));
            typeMap.put("format", MapUtils.getString(sourceMap, "format"));
            typeMap.put("where", MapUtils.getString(sourceMap, "where"));
            typeMap.put("splitPK", MapUtils.getString(sourceMap, "splitPK"));
            typeMap.put("table", MapUtils.getObject(sourceMap, "table"));
            typeMap.put("schema", MapUtils.getString(sourceMap, "schema"));
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("sourceId", source.getId());
        map.put("name", source.getDataName());
        map.put("column", obj);
        map.put("type", typeMap);
        map.put(EXTRAL_CONFIG, sourceMap.getOrDefault(EXTRAL_CONFIG, ""));

        if (sourceMap.containsKey("increColumn")) {
            map.put("increColumn", sourceMap.get("increColumn"));
        }

        if (sourceMap.containsKey("sourceList")) {
            map.put("sourceList", sourceMap.get("sourceList"));
        }
        if (sourceMap.containsKey("schema")) {
            map.put("schema", sourceMap.get("schema"));
        }
        return map;
    }

    private Map<String, Object> getTargetMap(Map<String, Object> targetMap) throws Exception {
        BatchDataSource target = (BatchDataSource) targetMap.get("source");

        Map<String, Object> typeMap = new HashMap<>(6);
        typeMap.put("type", target.getType());

        Object obj = null;
        if (Objects.nonNull(RDBMSSourceType.getByDataSourceType(target.getType())) && !DataSourceType.IMPALA.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            if (DataSourceType.HIVE.getVal().equals(target.getType()) || DataSourceType.HIVE3X.getVal().equals(target.getType())
                    || DataSourceType.HIVE1X.getVal().equals(target.getType()) || DataSourceType.SparkThrift2_1.getVal().equals(target.getType())
                    || DataSourceType.INCEPTOR.getVal().equals(target.getType())) {
                obj = MapUtils.getObject(targetMap, "column");
                typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
            } else if (DataSourceType.CarbonData.getVal().equals(target.getType())) {
                obj = MapUtils.getObject(targetMap, "column");
            } else {
                String schema = (targetMap.containsKey("schema") && targetMap.get("schema") != null) ? targetMap.get("schema").toString() : null;
                String table = ((List<String>) targetMap.get("table")).get(0);
                obj = this.getTableColumn(target, table, schema);
            }

            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("table", targetMap.get("table"));
            typeMap.put("preSql", MapUtils.getString(targetMap, "preSql"));
            typeMap.put("postSql", MapUtils.getString(targetMap, "postSql"));
        } else if (DataSourceType.HDFS.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("path", MapUtils.getString(targetMap, "path"));
            typeMap.put("fileName", MapUtils.getString(targetMap, "fileName"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("fileType", MapUtils.getString(targetMap, "fileType"));
        } else if (DataSourceType.HBASE.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put("nullMode", MapUtils.getString(targetMap, "nullMode"));
            typeMap.put("writeBufferSize", MapUtils.getString(targetMap, "writeBufferSize"));
            typeMap.put("rowkey", MapUtils.getString(targetMap, "rowkey"));
        } else if (DataSourceType.FTP.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
            typeMap.put("ftpFileName", MapUtils.getString(targetMap, "ftpFileName"));
            typeMap.put("path", MapUtils.getString(targetMap, "path"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
        } else if (DataSourceType.MAXCOMPUTE.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put("partition", MapUtils.getString(targetMap, "partition"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
        } else if (DataSourceType.Kudu.getVal().equals(target.getType())) {
            Assert.isTrue(StringUtils.isNotEmpty(MapUtils.getString(targetMap, "table")), "表名不能为空");
            String table = MapUtils.getString(targetMap, "table");
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("table", table);
            obj = this.getTableColumn(target, table, null);
        } else if (DataSourceType.IMPALA.getVal().equals(target.getType())) {
            typeMap.put("table", MapUtils.getString(targetMap, "table"));
            typeMap.put(TableLocationType.key(), MapUtils.getString(targetMap, TableLocationType.key()));
            Optional.ofNullable(MapUtils.getString(targetMap, "partition")).ifPresent(s -> typeMap.put("partition", s));
            Optional.ofNullable(MapUtils.getString(targetMap, "writeMode")).ifPresent(s -> typeMap.put("writeMode", s));
            obj = MapUtils.getObject(targetMap, "column");
        } else if (DataSourceType.AWS_S3.getVal().equals(target.getType())) {
            obj = MapUtils.getObject(targetMap, "column");
            typeMap.put("bucket", MapUtils.getString(targetMap, "bucket"));
            typeMap.put("object", MapUtils.getString(targetMap, "object"));
            typeMap.put("writeMode", MapUtils.getString(targetMap, "writeMode"));
            typeMap.put("fieldDelimiter", MapUtils.getString(targetMap, "fieldDelimiter"));
            typeMap.put("encoding", MapUtils.getString(targetMap, "encoding"));
        }

        Map<String, Object> map = new HashMap<>(4);
        map.put("sourceId", target.getId());
        map.put("name", target.getDataName());
        map.put("column", obj);
        map.put("type", typeMap);
        map.put(EXTRAL_CONFIG, targetMap.getOrDefault(EXTRAL_CONFIG, ""));
        if (targetMap.containsKey("schema")) {
            map.put("schema", targetMap.get("schema"));
        }
        map.put(EXTRAL_CONFIG, targetMap.getOrDefault(EXTRAL_CONFIG, ""));

        return map;
    }

    /**
     * 获取表所属字段 不包括分区字段
     * @param source
     * @param tableName
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumn(BatchDataSource source, String tableName, String schema) {
        try {
            return this.getTableColumnIncludePart(source,tableName,false, schema);
        } catch (final Exception e) {
            throw new RdosDefineException("获取表字段异常", e);
        }

    }

    /**
     * 查询表所属字段 可以选择是否需要分区字段
     * @param source
     * @param tableName
     * @param part 是否需要分区字段
     * @return
     * @throws Exception
     */
    private List<JSONObject> getTableColumnIncludePart(BatchDataSource source, String tableName, Boolean part, String schema)  {
        try {
            if (source == null) {
                throw new RdosDefineException(ErrorCode.CAN_NOT_FIND_DATA_SOURCE);
            }
            if (part ==null){
                part = false;
            }
            JSONObject dataJson = JSONObject.parseObject(source.getDataJson());
            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
            IClient iClient = ClientCache.getClient(source.getType());
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder()
                    .tableName(tableName)
                    .schema(schema)
                    .filterPartitionColumns(part)
                    .build();
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(dataJson, source.getType(), kerberosConfig);
            List<ColumnMetaDTO> columnMetaData = iClient.getColumnMetaData(iSourceDTO, sqlQueryDTO);
            List<JSONObject> list = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(columnMetaData)) {
                for (ColumnMetaDTO columnMetaDTO : columnMetaData) {
                    JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(columnMetaDTO));
                    jsonObject.put("isPart",columnMetaDTO.getPart());
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

    public void initDefaultSource(Long tenantId, String dataSourceName, String dataSourceDesc, Long userId) throws Exception {
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, userId, EJobType.SPARK_SQL);
        String jdbcUrl = jdbcInfo.getJdbcUrl();
        SparkThriftConnectionUtils.HiveVersion version = SparkThriftConnectionUtils.HiveVersion.getByVersion(jdbcInfo.getVersion());
        JSONObject dataJson = new JSONObject();
        dataJson.put("username",jdbcInfo.getUsername());
        dataJson.put("password",jdbcInfo.getPassword());
        if (!jdbcUrl.contains("%s")) {
            throw new RdosDefineException("控制台 HiveServer URL 不包含占位符 %s");
        }
        jdbcUrl = String.format(jdbcUrl, dataSourceName);
        dataJson.put("jdbcUrl", jdbcUrl);
        String defaultFs = HadoopConf.getDefaultFs(tenantId);
        if (org.apache.commons.lang3.StringUtils.isNotBlank(defaultFs)) {
            dataJson.put("defaultFS", defaultFs);
        }else {
            throw new RdosDefineException("默认数据源的defaultFs未找到");
        }

        JSONObject hdpConfig = createHadoopConfigObject(tenantId);
        if (!hdpConfig.isEmpty()) {
            dataJson.put("hadoopConfig", hdpConfig.toJSONString());
        }

        dataSourceName = dataSourceName + "_" + MultiEngineType.HADOOP.name();
        dataJson.put("hasHdfsConfig", true);
        DataSourceVO dataSourceVO = new DataSourceVO();
        dataSourceVO.setDataDesc(org.apache.commons.lang3.StringUtils.isNotEmpty(dataSourceDesc) ? dataSourceDesc : "");
        dataSourceVO.setDataJson(dataJson);
        dataSourceVO.setCreateUserId(userId);
        dataSourceVO.setActive(1);
        dataSourceVO.setDataName(dataSourceName);
        dataSourceVO.setTenantId(tenantId);
        dataSourceVO.setDataType(DataSourceTypeEnum.SparkThrift2_1.getDataType());
        dataSourceVO.setIsMeta(1);

        addOrUpdate(dataSourceVO, userId);
    }

    public JSONObject createHadoopConfigObject(Long tenantId) {
        JSONObject hadoop = new JSONObject();
        Map<String, Object> config = HadoopConf.getConfiguration(tenantId);
        String nameServices = config.getOrDefault("dfs.nameservices","").toString();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(nameServices)) {
            hadoop.put("dfs.nameservices", nameServices);
            String nameNodes = config.getOrDefault(String.format("dfs.ha.namenodes.%s", nameServices),"").toString();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(nameNodes)) {
                hadoop.put(String.format("dfs.ha.namenodes.%s", nameServices), nameNodes);
                for (String nameNode : nameNodes.split(",")) {
                    String key = String.format("dfs.namenode.rpc-address.%s.%s", nameServices, nameNode);
                    hadoop.put(key, config.get(key));
                }
            }
            String failoverKey = String.format("dfs.client.failover.proxy.provider.%s", nameServices);
            hadoop.put(failoverKey, config.get(failoverKey));
        }

        return hadoop;
    }

    public BatchDataSource getOne(Long id) {
        DsInfo dsInfo = dsInfoService.getOneById(id);
        BatchDataSource batchDataSource = new BatchDataSource();
        BeanUtils.copyProperties(dsInfo, batchDataSource);
        batchDataSource.setType(dsInfo.getDataTypeCode());
        return batchDataSource;
    }


    /**
     * 转换成datasourceVo
     * @param tenantId
     * @param userId
     * @param dscJson
     * @param dataName
     * @param dataSourceType
     * @param dbName
     * @return
     */
    private DataSourceVO convertParamToVO(Long tenantId, Long userId, String dscJson, String dataName, Integer dataSourceType, String dbName) {
        DataSourceVO dataSourceVO = new DataSourceVO();
        dataSourceVO.setUserId(userId);
        dataSourceVO.setTenantId(tenantId);
        dataSourceVO.setGmtCreate(new Date());
        dataSourceVO.setGmtModified(new Date());
        dataSourceVO.setDataName(dataName);
        dataSourceVO.setType(dataSourceType);
        dataSourceVO.setSchemaName(dbName);
        DataSourceTypeEnum typeEnum = DataSourceTypeEnum.valOf(dataSourceType);
        Asserts.notNull(typeEnum, ErrorCode.CAN_NOT_FITABLE_SOURCE_TYPE);
        dataSourceVO.setDataType(typeEnum.getDataType());
        dataSourceVO.setDataVersion(typeEnum.getDataVersion());
        if (Strings.isNotBlank(dscJson)) {
            dataSourceVO.setDataJson(DataSourceUtils.getDataSourceJson(dscJson));
        }
        dataSourceVO.setIsMeta(1);
        return dataSourceVO;
    }

    public void createMateDataSource(Long tenantId, Long userId, String dscJson, String dataName, Integer dataSourceType, String tenantDesc, String dbName) {

        DataSourceVO dataSourceVO = convertParamToVO(tenantId,userId,dscJson,dataName,dataSourceType,dbName);
        addOrUpdate(dataSourceVO, userId);

    }

    public Integer getEComponentTypeByDataSourceType(Integer val) {

        return ComponentTypeDataSourceTypeMapping.getEComponentType(val);
    }

    /**
     * 数据同步-获得数据库中相关的表信息
     *
     * @param sourceId  数据源id
     * @param tenantId 租户id
     * @param schema 查询的schema
     * @param name 模糊查询表名
     * @param isAll 是否获取所有表
     * @param isRead 是否读取类型
     * @return
     */
    public List<String> tablelist(Long sourceId,Long tenantId,String schema, String name, Boolean isAll, Boolean isRead) {
        List<String> tables = new ArrayList<>();
        BatchDataSource source = getOne(sourceId);
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        //查询的db
        String dataSource = schema;

        IClient client = ClientCache.getClient(source.getType());
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), fillKerberosConfig(source.getId()));
        SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().tableNamePattern(name).limit(5000).build();
        sqlQueryDTO.setView(true);
        sqlQueryDTO.setSchema(dataSource);
        //如果是hive类型的数据源  过滤脏数据表 和 临时表
        tables = client.getTableList(sourceDTO, sqlQueryDTO);
        return tables;
    }


    /**
     * 数据同步-获得表中字段与类型信息
     *
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return
     */
    public List<JSONObject> tablecolumn(Long projectId, Long userId, Long sourceId, String tableName, Boolean isIncludePart, String schema) {

        final BatchDataSource source = this.getOne(sourceId);
        final StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        return getTableColumnIncludePart(source, tableName,isIncludePart, schema);
    }


    /**
     * 返回切分键需要的列名
     * <p>
     * 只支持关系型数据库 mysql\oracle\sqlserver\postgresql  的整型数据类型
     * 也不支持其他数据库。
     * 如果指定了不支持的类型，则忽略切分键功能，使用单通道进行同步。
     *
     * @param userId
     * @param sourceId
     * @param tableName
     * @return
     */
    public Set<JSONObject> columnForSyncopate(Long userId, Long sourceId, String tableName, String schema) {

        BatchDataSource source = getOne(sourceId);
        if (Objects.isNull(RDBMSSourceType.getByDataSourceType(source.getType())) && !DataSourceType.INFLUXDB.getVal().equals(source.getType())) {
            log.error("切分键只支关系型数据库");
            throw new RdosDefineException("切分键只支持关系型数据库");
        }
        if (StringUtils.isEmpty(tableName)) {
            return new HashSet<>();
        }
        final StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        final List<JSONObject> tablecolumn = this.getTableColumn(source, tableName, schema);
        if (CollectionUtils.isNotEmpty(tablecolumn)) {
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
            Map<JSONObject, String> twinsMap = new LinkedHashMap<>(tablecolumn.size()+1);
            for (JSONObject twins : tablecolumn) {
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
        if (split != null && split.length > 1) {
            //提取例如"INT UNSIGNED"情况下的字段类型
            type = split[0];
        }
        return type;
    }


    public Set<String> getHivePartitions(Long sourceId, String tableName) {

        BatchDataSource source = getOne(sourceId);
        JSONObject json = JSON.parseObject(source.getDataJson());
        Map<String, Object> kerberosConfig = this.fillKerberosConfig(sourceId);

        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), kerberosConfig);
        IClient iClient = ClientCache.getClient(source.getType());
        List<ColumnMetaDTO> partitionColumn = iClient.getPartitionColumn(sourceDTO, SqlQueryDTO.builder().tableName(tableName).build());

        Set<String> partitionNameSet = Sets.newHashSet();
        //格式化分区信息 与hive保持一致
        if (CollectionUtils.isNotEmpty(partitionColumn)){
            StringJoiner tempJoiner = new StringJoiner("=/","","=");
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
     * @param userId    用户id
     * @param sourceId  数据源id
     * @param tableName 表名
     * @return
     * @author toutian
     */
    public JSONObject preview(Long userId, Long sourceId, String tableName, String partition,
                              Long tenantId, Long dtuicTenantId, Boolean isRoot, String schema) {

        BatchDataSource source = getOne(sourceId);
        StringBuffer newTableName = new StringBuffer();
        if (DataSourceType.SQLServer.getVal().equals(source.getType()) && StringUtils.isNotBlank(tableName)){
            if (tableName.indexOf("[") == -1){
                final String[] tableNames = tableName.split("\\.");
                for (final String name : tableNames) {
                    newTableName.append("[").append(name).append("]").append(".");
                }
                tableName = newTableName.substring(0,newTableName.length()-1);
            }
        }
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        //获取字段信息
        List<String> columnList = new ArrayList<String>();
        //获取数据
        List<List<String>> dataList = new ArrayList<List<String>>();
        try {
            Map<String, Object> kerberosConfig = fillKerberosConfig(source.getId());
            List<JSONObject> columnJson = getTableColumn(source, tableName, schema);
            if (CollectionUtils.isNotEmpty(columnJson)) {
                for (JSONObject columnMetaDTO : columnJson) {
                    columnList.add(columnMetaDTO.getString("key"));
                }
            }
            IClient iClient = ClientCache.getClient(source.getType());
            ISourceDTO iSourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), kerberosConfig);
            SqlQueryDTO sqlQueryDTO = SqlQueryDTO.builder().schema(schema).tableName(tableName).previewNum(3).build();
            dataList = iClient.getPreview(iSourceDTO, sqlQueryDTO);
            if (DataSourceType.getRDBMS().contains(source.getType())) {
                //因为会把字段名也会返回 所以要去除第一行
                dataList = dataList.subList(1, dataList.size());
            }
        } catch (Exception e) {
            log.error("datasource preview end with error.", e);
            throw new RdosDefineException(String.format("%s获取预览数据失败", source.getDataName()), e);
        }

        JSONObject preview = new JSONObject(2);
        preview.put("columnList", columnList);
        preview.put("dataList", dataList);

        return preview;
    }

    /**
     * 获取所有schema
     * @param sourceId 数据源id
     * @return
     */
    public List<String> getAllSchemas(Long sourceId, String schema) {
        BatchDataSource source = getOne(sourceId);
        String dataJson = source.getDataJson();
        JSONObject json = JSON.parseObject(dataJson);
        ISourceDTO sourceDTO = SourceDTOType.getSourceDTO(json, source.getType(), fillKerberosConfig(sourceId));
        IClient client = ClientCache.getClient(source.getType());
        return client.getAllDatabases(sourceDTO, SqlQueryDTO.builder().schema(schema).build());
    }

}
