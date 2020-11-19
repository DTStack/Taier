package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.enums.*;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.Sort;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
import com.dtstack.schedule.common.util.Base64Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.*;
import static com.dtstack.engine.master.impl.ComponentService.TYPE_NAME;
import static java.lang.String.format;

@Service
public class ClusterService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    private static final Long DEFAULT_CLUSTER_ID = -1L;

    private static final String DEFAULT_CLUSTER_NAME = "default";
    private final static String CLUSTER = "cluster";
    private final static String QUEUE = "queue";
    private final static String TENANT_ID = "tenantId";
    private static final String DEPLOY_MODEL = "deployMode";
    private static final String NAMESPACE = "namespace";

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineService engineService;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private KerberosDao kerberosDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private AccountTenantDao accountTenantDao;

    @Autowired
    private AccountDao accountDao;

    @Autowired
    private AccountService accountService;

    @Autowired
    private EnvironmentContext environmentContext;


    @Override
    public void afterPropertiesSet() throws Exception {
        if (isDefaultClusterExist()) {
            return;
        }

        try {
            addDefaultCluster();
        } catch (Exception e) {
            LOGGER.error(" ", e);
        }
    }

    private boolean isDefaultClusterExist() {
        Cluster cluster = clusterDao.getOne(DEFAULT_CLUSTER_ID);
        if (cluster == null) {
            cluster = clusterDao.getByClusterName(DEFAULT_CLUSTER_NAME);
            return cluster != null;
        }

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addDefaultCluster() throws Exception {
        Cluster cluster = new Cluster();
        cluster.setId(DEFAULT_CLUSTER_ID);
        cluster.setClusterName(DEFAULT_CLUSTER_NAME);
        cluster.setHadoopVersion("");
        clusterDao.insertWithId(cluster);
    }

    @Transactional(rollbackFor = Exception.class)
    public ClusterVO addCluster(ClusterDTO clusterDTO) {
        EngineAssert.assertTrue(StringUtils.isNotEmpty(clusterDTO.getClusterName()), ErrorCode.INVALID_PARAMETERS.getDescription());
        checkName(clusterDTO.getClusterName());

        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterDTO.getClusterName());
        cluster.setHadoopVersion("");
        Cluster byClusterName = clusterDao.getByClusterName(clusterDTO.getClusterName());
        if (byClusterName != null) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST.getDescription());
        }
        clusterDao.insert(cluster);
        clusterDTO.setId(cluster.getId());
        return getCluster(cluster.getId(),true,true);
    }

    private void checkName(String name) {
        if (StringUtils.isNotBlank(name)) {
            if (name.length() > 24) {
                throw new RdosDefineException("名称过长");
            }
        } else {
            throw new RdosDefineException("名称不能为空");
        }
    }



    public ClusterVO getClusterByName(String clusterName) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        return ClusterVO.toVO(cluster);
    }

    public PageResult<List<ClusterVO>> pageQuery( int currentPage,  int pageSize) {
        PageQuery<ClusterDTO> pageQuery = new PageQuery<>(currentPage, pageSize, "gmt_modified", Sort.DESC.name());
        ClusterDTO model = new ClusterDTO();
        model.setIsDeleted(Deleted.NORMAL.getStatus());
        int count = clusterDao.generalCount(model);
        List<ClusterVO> clusterVOS = new ArrayList<>();
        if (count > 0) {
            pageQuery.setModel(model);
            List<Cluster> clusterList = clusterDao.generalQuery(pageQuery);
            clusterVOS.addAll(ClusterVO.toVOs(clusterList));
        }

        return new PageResult<>(clusterVOS, count, pageQuery);
    }

    /**
     * 对外接口
     */
    public String clusterInfo( Long tenantId) {
        ClusterVO cluster = getClusterByTenant(tenantId);
        if (cluster != null) {
            JSONObject config = buildClusterConfig(cluster);
            return config.toJSONString();
        }

        LOGGER.error("无法取得集群信息，默认集群信息没有配置！");
        return StringUtils.EMPTY;
    }

    public ClusterVO clusterExtInfo( Long uicTenantId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(uicTenantId);
        if (tenantId == null) {
            return null;
        }
        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return null;
        }
        Engine engine = engineDao.getOne(engineIds.get(0));
        return getCluster(engine.getClusterId(), true,false);
    }

    /**
     * 内部使用
     */
    public JSONObject pluginInfoJSON( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode) {
        if (EngineType.Dummy.name().equalsIgnoreCase(engineTypeStr)) {
            JSONObject dummy = new JSONObject();
            dummy.put(TYPE_NAME, EngineType.Dummy.name().toLowerCase());
            return dummy;
        }
        EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineTypeStr);
        if (type == null) {
            return null;
        }

        ClusterVO cluster = getClusterByTenant(dtUicTenantId);
        if (cluster == null) {
            String msg = format("The tenant [%s] is not bound to any cluster", dtUicTenantId);
            throw new RdosDefineException(msg);
        }
        cluster.setDtUicTenantId(dtUicTenantId);
        cluster.setDtUicUserId(dtUicUserId);

        JSONObject clusterConfigJson = buildClusterConfig(cluster);
        JSONObject pluginJson = convertPluginInfo(clusterConfigJson, type, cluster, deployMode);
        if (pluginJson == null) {
            throw new RdosDefineException(format("The cluster is not configured [%s] engine", engineTypeStr));
        }

        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        Queue queue = getQueue(tenantId, cluster.getClusterId());

        pluginJson.put(QUEUE, queue == null ? "" : queue.getQueueName());
        pluginJson.put(NAMESPACE, queue == null ? "" : queue.getQueueName());
        pluginJson.put(CLUSTER, cluster.getClusterName());
        pluginJson.put(TENANT_ID, tenantId);
        setComponentSftpDir(cluster.getClusterId(), clusterConfigJson, pluginJson,type);
        return pluginJson;
    }

    public String pluginInfo( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode) {
        return "{}";
    }

    /**
     * 填充对应的组件信息
     * @param clusterId
     * @param clusterConfigJson
     * @param pluginJson
     * @param type
     */
    private void setComponentSftpDir(Long clusterId, JSONObject clusterConfigJson, JSONObject pluginJson, EngineTypeComponentType type) {
        //sftp Dir
        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        if (null != sftpConfig) {
            pluginJson.put(EComponentType.SFTP.getConfName(), sftpConfig);
        }
        EComponentType componentType = type.getComponentType();
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType.getTypeCode());
        if (null != kerberosConfig) {
            Integer openKerberos = kerberosConfig.getOpenKerberos();
            String remotePath = kerberosConfig.getRemotePath();
            Preconditions.checkState(StringUtils.isNotEmpty(remotePath), "remotePath can not be null");
            pluginJson.fluentPut("openKerberos", null != openKerberos && openKerberos > 0)
                    .fluentPut("remoteDir", remotePath)
                    .fluentPut("principalFile", kerberosConfig.getName())
                    .fluentPut("krbName", kerberosConfig.getKrbName())
                    .fluentPut("kerberosFileTimestamp", kerberosConfig.getGmtModified());
            //如果 hiveSQL  impalaSQL中没有yarnConf 需要添加yarnConf做kerberos认证
            pluginJson.putIfAbsent(EComponentType.YARN.getConfName(),clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName()));
        }
    }

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     * @param tenantId
     * @return
     */
    public String clusterSftpDir( Long tenantId,  Integer componentType) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(tenantId);
        if (clusterId != null) {
            if(null == componentType){
                componentType = EComponentType.SPARK_THRIFT.getTypeCode();
            }
            Map<String, String> sftpConfig = componentService.getSFTPConfig(clusterId);
            if (sftpConfig != null) {
                KerberosConfig kerberosDaoByComponentType = kerberosDao.getByComponentType(clusterId, componentType);
                if(null != kerberosDaoByComponentType){
                    return sftpConfig.get("path") + File.separator + componentService.buildSftpPath(clusterId, componentType) + File.separator +
                            ComponentService.KERBEROS_PATH;
                }
                return sftpConfig.get("path") + File.separator + componentService.buildSftpPath(clusterId, componentType);
            }
        }
        return null;
    }

    public Queue getQueue(Long tenantId, Long clusterId) {
        Long queueId = engineTenantDao.getQueueIdByTenantId(tenantId);
        Queue queue = queueDao.getOne(queueId);
        if (queue != null) {
            return queue;
        }

        Engine hadoopEngine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (hadoopEngine == null) {
            return null;
        }

        List<Queue> queues = queueDao.listByEngineIdWithLeaf(hadoopEngine.getId());
        if (CollectionUtils.isEmpty(queues)) {
            return null;
        }

        // 没有绑定集群和队列时，返回第一个队列
        return queues.get(0);
    }

    public String getNamespace(ParamAction action, Long tenantId, String engineName, ComputeType computeType) {

        try {
            Map actionParam = PublicUtil.objectToMap(action);
            Integer deployMode = MapUtils.getInteger(actionParam, DEPLOY_MODEL);
            EngineTypeComponentType type = EngineTypeComponentType.getByEngineName(engineName);

            if (type == null) {
                return null;
            }

            EDeployMode deploy = EDeployMode.PERJOB;
            if (ComputeType.BATCH == computeType && EngineTypeComponentType.FLINK.equals(type)) {
                deploy = EDeployMode.SESSION;
            }
            if (null != deployMode) {
                deploy = EDeployMode.getByType(deployMode);
            }

            ClusterVO cluster = getClusterByTenant(tenantId);
            if (null == cluster) {
                return null;
            }

            JSONObject clusterConfigJson = buildClusterConfig(cluster);
            JSONObject componentConf = clusterConfigJson.getJSONObject(type.getComponentType().getConfName());
            if (null == componentConf) {
                return null;
            }
            JSONObject pluginInfo = componentConf.getJSONObject(deploy.getMode());
            if (null == pluginInfo) {
                return null;
            }
            return pluginInfo.getString(NAMESPACE);
        } catch (IOException e) {
            LOGGER.error("Get namespace error " + e.getMessage());
        }
        return null;
    }


    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    public String hiveInfo( Long dtUicTenantId,  Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.SPARK_THRIFT.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String hiveServerInfo( Long dtUicTenantId, Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.HIVE_SERVER.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String hadoopInfo( Long dtUicTenantId, Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.HDFS.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String carbonInfo( Long dtUicTenantId, Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.CARBON_DATA.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String impalaInfo( Long dtUicTenantId, Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.IMPALA_SQL.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     * @param dtUicTenantId
     * @param fullKerberos
     * @return
     */
    public String prestoInfo(Long dtUicTenantId, Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.PRESTO_SQL.getConfName(), fullKerberos);
    }

    /**
     * 对外接口
     */
    public String sftpInfo( Long dtUicTenantId) {
        return getConfigByKey(dtUicTenantId, EComponentType.SFTP.getConfName(),false);
    }

    public JSONObject buildClusterConfig(ClusterVO cluster) {
        JSONObject config = new JSONObject();
        List<SchedulingVo> scheduling = cluster.getScheduling();
        if (CollectionUtils.isNotEmpty(scheduling)) {
            for (SchedulingVo schedulingVo : scheduling) {
                List<ComponentVO> components = schedulingVo.getComponents();
                if (CollectionUtils.isNotEmpty(components)) {
                    for (Component component : components) {
                        EComponentType type = EComponentType.getByCode(component.getComponentTypeCode());
                        config.put(type.getConfName(), JSONObject.parseObject(component.getComponentConfig()));
                    }
                }
            }
        }
        config.put("clusterName", cluster.getClusterName());
        return config;
    }

    public ClusterVO getClusterByTenant(Long dtUicTenantId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null) {
            return getCluster(DEFAULT_CLUSTER_ID, true,false);
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return getCluster(DEFAULT_CLUSTER_ID, true,false);
        }

        Engine engine = engineDao.getOne(engineIds.get(0));
        if(null == engine){
            return getCluster(DEFAULT_CLUSTER_ID, true,false);
        }
        return getCluster(engine.getClusterId(), true,false);
    }

    public String getConfigByKey(Long dtUicTenantId,  String key, Boolean fullKerberos) {
        ClusterVO cluster = getClusterByTenant(dtUicTenantId);
        JSONObject config = buildClusterConfig(cluster);
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(key);
        Component component = componentDao.getByClusterIdAndComponentType(cluster.getId(),componentType.getTypeCode());
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(),componentType.getTypeCode());
        JSONObject configObj = config.getJSONObject(key);
        if (configObj != null) {
            //返回版本
            configObj.put(ComponentService.VERSION, component.getHadoopVersion());
            // TODO 维持各个应用原来数据接口
            addKerberosConfigWithHdfs(key, cluster, kerberosConfig, configObj);
            if (null != fullKerberos && fullKerberos) {
                //将sftp中keytab配置转换为本地路径
                this.fullKerberosFilePath(dtUicTenantId, configObj,component);
            }

            if(BooleanUtils.isTrue(fullKerberos)){
                Component sftpComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.SFTP.getTypeCode());
                Map sftpMap = null;
                try {
                    sftpMap = PublicUtil.strToObject(sftpComponent.getComponentConfig(), Map.class);
                } catch (Exception e) {
                    throw new RdosDefineException("sftp 配置不能为空");
                }
                //填充信息
                JSONObject componentInfo = JSONObject.parseObject(componentService.wrapperConfig(componentType.getTypeCode(), component.getComponentConfig(),
                        sftpMap, kerberosConfig, cluster.getClusterName()));
                configObj.putAll(componentInfo);
            }
            return configObj.toJSONString();
        }
        return "{}";
    }

    private <T> T fullKerberosFilePath(Long dtUicTenantId, T data,Component component) {
        SftpConfig sftpConfig = JSONObject.parseObject(this.sftpInfo(dtUicTenantId), SftpConfig.class);
        if (StringUtils.isNotBlank(sftpConfig.getHost())) {
            JSONObject dataMap = this.getJsonObject(data);
            this.accordToKerberosFile(sftpConfig, dataMap,component);
            data = this.convertJsonOverBack(data, dataMap);
        }
        return data;
    }

    private <T> T convertJsonOverBack(T data, JSONObject dataMap) {
        if (data instanceof String) {
            data = (T) dataMap.toString();
        } else {
            try {
                data = objectMapper.readValue(dataMap.toString(), (Class<T>) data.getClass());
            } catch (IOException e) {
                LOGGER.error("", e);
            }
        }
        return data;
    }


    private <T> JSONObject getJsonObject(T data) {
        JSONObject dataMap = null;
        if (data instanceof String) {
            dataMap = JSONObject.parseObject((String)data);
        } else {
            dataMap = (JSONObject)JSONObject.toJSON(data);
        }

        return dataMap;
    }


    /**
     * 添加kerberos的配置文件地址为本地路径
     * @param sftp
     * @param dataMap
     */
    private void accordToKerberosFile(SftpConfig sftpConfig, JSONObject dataMap, Component component) {
        try {
            JSONObject configJsonObject = dataMap.getJSONObject("kerberosConfig");
            if (null == configJsonObject) {
                return;
            }
            KerberosConfig kerberosConfig = PublicUtil.strToObject(configJsonObject.toString(), KerberosConfig.class);
            if (null == kerberosConfig) {
                return;
            }
            if (kerberosConfig.getOpenKerberos() <= 0) {
                return;
            }
            Preconditions.checkState(null != kerberosConfig.getClusterId());
            Preconditions.checkState(null != kerberosConfig.getOpenKerberos());
            Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getPrincipal()));
            Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getRemotePath()));
            Preconditions.checkState(null != kerberosConfig.getComponentType());
            String remoteSftpKerberosPath = componentService.buildSftpPath(kerberosConfig.getClusterId(), component.getComponentTypeCode()) +
                   File.separator +  ComponentService.KERBEROS_PATH;
            Preconditions.checkState(Objects.nonNull(kerberosConfig.getComponentType()));
            String remoteSftpKerberosPath = componentService.buildSftpPath(kerberosConfig.getClusterId(), component.getComponentTypeCode()) + File.separator +  KERBEROS_PATH;
            String localKerberosPath = componentService.getLocalKerberosPath(kerberosConfig.getClusterId(), component.getComponentTypeCode());
            KerberosConfigVerify.downloadKerberosFromSftp(remoteSftpKerberosPath, localKerberosPath, sftpConfig);
            File file = new File(localKerberosPath);
            Preconditions.checkState(file.exists() && file.isDirectory(), "console kerberos local path not exist");
            File keytabFile = Arrays.stream(file.listFiles()).filter((obj) -> obj.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst().orElseThrow(() -> new RdosDefineException("keytab文件不存在"));
            //获取本地的kerberos本地路径
            configJsonObject.put("keytabPath", keytabFile.getPath());
            configJsonObject.put("principalFile", keytabFile.getName());
            configJsonObject.putAll(Optional.ofNullable(configJsonObject.getJSONObject("hdfsConfig")).orElse(new JSONObject()));
            configJsonObject.remove("hdfsConfig");
            dataMap.put("kerberosConfig", configJsonObject);
        } catch (Exception e) {
            LOGGER.error("accordToKerberosFile error {}", dataMap, e);
            throw new RdosDefineException("下载kerberos文件失败");
        }
    }

    /**
     * 如果开启集群开启了kerberos认证，kerberosConfig中还需要包含hdfs配置
     *
     * @param key
     * @param cluster
     * @param kerberosConfig
     * @param configObj
     */
    public void addKerberosConfigWithHdfs(String key, ClusterVO cluster, KerberosConfig kerberosConfig, JSONObject configObj) {
        if (null != kerberosConfig) {
            KerberosConfigVO kerberosConfigVO = KerberosConfigVO.toVO(kerberosConfig);
            if (!Objects.equals(EComponentType.HDFS.getConfName(), key)) {
                Component hdfsComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
                if (null == hdfsComponent) {
                    throw new RdosDefineException("开启kerberos后需要预先保存hdfs组件");
                }
                kerberosConfigVO.setHdfsConfig(JSONObject.parseObject(hdfsComponent.getComponentConfig()));
            }
            configObj.put("kerberosConfig", kerberosConfigVO);
        }
    }

    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, EngineTypeComponentType type, ClusterVO clusterVO,Integer deployMode) {
        JSONObject pluginInfo = new JSONObject();
        if (EComponentType.HDFS == type.getComponentType()) {
            //hdfs yarn%s-hdfs%s-hadoop%s的版本
            JSONObject hadoopConf = clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName());
            String typeName = hadoopConf.getString(TYPE_NAME);
            pluginInfo.put(TYPE_NAME, typeName);
            pluginInfo.put(EComponentType.HDFS.getConfName(), hadoopConf);
            pluginInfo.put(EComponentType.YARN.getConfName(), clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName()));

        } else if (EComponentType.LIBRA_SQL == type.getComponentType()) {
            JSONObject libraConf = clusterConfigJson.getJSONObject(EComponentType.LIBRA_SQL.getConfName());
            pluginInfo = this.convertSQLComponent(libraConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "postgresql");
        } else if (EComponentType.IMPALA_SQL == type.getComponentType()) {
            JSONObject impalaConf = clusterConfigJson.getJSONObject(EComponentType.IMPALA_SQL.getConfName());
            pluginInfo = this.convertSQLComponent(impalaConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "impala");
        } else if (EComponentType.TIDB_SQL == type.getComponentType()) {
            JSONObject tiDBConf = JSONObject.parseObject(tiDBInfo(clusterVO.getDtUicTenantId(), clusterVO.getDtUicUserId()));
            pluginInfo = this.convertSQLComponent(tiDBConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "tidb");
        } else if (EComponentType.ORACLE_SQL == type.getComponentType()) {
            JSONObject oracleConf = JSONObject.parseObject(oracleInfo(clusterVO.getDtUicTenantId(), clusterVO.getDtUicUserId()));
            pluginInfo = this.convertSQLComponent(oracleConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "oracle");
        } else if (EComponentType.GREENPLUM_SQL == type.getComponentType()) {
            JSONObject greenplumConf = JSONObject.parseObject(greenplumInfo(clusterVO.getDtUicTenantId(),clusterVO.getDtUicUserId()));
            pluginInfo = this.convertSQLComponent(greenplumConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "greenplum");
        } else if (EComponentType.PRESTO_SQL == type.getComponentType()) {
            JSONObject prestoConf = JSONObject.parseObject(prestoInfo(clusterVO.getDtUicTenantId(),clusterVO.getDtUicUserId()));
            pluginInfo = this.convertSQLComponent(prestoConf, pluginInfo);
            pluginInfo.put(TYPE_NAME, "presto");
        } else {
            //flink spark 需要区分任务类型
            if (EComponentType.FLINK.equals(type.getComponentType()) || EComponentType.SPARK.equals(type.getComponentType())) {
                pluginInfo = this.buildDeployMode(clusterConfigJson, type, clusterVO, deployMode);
            } else if (EComponentType.DT_SCRIPT.equals(type.getComponentType())) {
                //DT_SCRIPT 需要将common配置放在外边
                JSONObject dtscriptConf = clusterConfigJson.getJSONObject(type.getComponentType().getConfName());
                JSONObject commonConf = dtscriptConf.getJSONObject("commonConf");
                dtscriptConf.remove("commonConf");
                pluginInfo = dtscriptConf;
                pluginInfo.putAll(commonConf);
            } else {
                pluginInfo = clusterConfigJson.getJSONObject(type.getComponentType().getConfName());
            }

            if (pluginInfo == null) {
                return null;
            }

            for (Iterator<Map.Entry<String, Object>> it = clusterConfigJson.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                if (!EComponentType.BASE_CONFIG.contains(entry.getKey())) {
                    it.remove();
                    continue;
                }
                if (EComponentType.DT_SCRIPT == type.getComponentType() && EComponentType.SPARK_THRIFT.getConfName().equals(entry.getKey())) {
                    //dt-script  不需要hive-site配置
                    continue;
                }

                if (EComponentType.KUBERNETES.getConfName().equals(entry.getKey())){
                    //kubernetes 需要添加配置文件名称 供下载
                    this.buildKubernetesConfig(clusterConfigJson, clusterVO, pluginInfo);
                    continue;
                }
                pluginInfo.put(entry.getKey(), entry.getValue());
            }
            if (EComponentType.HIVE_SERVER == type.getComponentType()) {
                this.buildHiveVersion(clusterVO, pluginInfo);
            } else if (EComponentType.DT_SCRIPT == type.getComponentType() || EComponentType.SPARK==type.getComponentType()) {
                if (clusterVO.getDtUicUserId() != null && clusterVO.getDtUicTenantId() != null) {
                    AccountVo accountVo = accountService.getAccountVo(clusterVO.getDtUicTenantId(), clusterVO.getDtUicUserId(), AccountType.LDAP.getVal());
                    String ldapUserName = StringUtils.isBlank(accountVo.getName()) ? "" : accountVo.getName();
                    pluginInfo.put("dtProxyUserName", ldapUserName);
                }
            }
            pluginInfo.put(ConfigConstant.MD5_SUM_KEY, getZipFileMD5(clusterConfigJson));
            removeMd5FieldInHadoopConf(pluginInfo);
        }

        return pluginInfo;
    }

    private void buildHiveVersion(ClusterVO clusterVO, JSONObject pluginInfo) {
        Component hiveServer = componentDao.getByClusterIdAndComponentType(clusterVO.getId(), EComponentType.HIVE_SERVER.getTypeCode());
        if (null == hiveServer) {
            throw new RdosDefineException("hive组件不能为空");
        }
        String jdbcUrl = pluginInfo.getString("jdbcUrl");
        jdbcUrl = jdbcUrl.replace("/%s", "");
        pluginInfo.put("jdbcUrl", jdbcUrl);
        String typeName = componentService.convertComponentTypeToClient(clusterVO.getClusterName(),
                EComponentType.HIVE_SERVER.getTypeCode(), hiveServer.getHadoopVersion(),hiveServer.getStoreType());
        pluginInfo.put(TYPE_NAME,typeName);
    }

    private void buildKubernetesConfig(JSONObject clusterConfigJson, ClusterVO clusterVO, JSONObject pluginInfo) {
        Component kubernetes = componentDao.getByClusterIdAndComponentType(clusterVO.getId(), EComponentType.KUBERNETES.getTypeCode());
        if(Objects.nonNull(kubernetes)){
            pluginInfo.put("kubernetesConfigName",kubernetes.getUploadFileName());
            JSONObject sftpConf = clusterConfigJson.getJSONObject("sftpConf");
            if(Objects.nonNull(sftpConf)){
                String path = sftpConf.getString("path") + File.separator + componentService.buildSftpPath(clusterVO.getId(), EComponentType.KUBERNETES.getTypeCode());
                pluginInfo.put("remoteDir",path);
            }
        }
    }

    private JSONObject buildDeployMode(JSONObject clusterConfigJson, EngineTypeComponentType type, ClusterVO clusterVO, Integer deployMode) {
        JSONObject pluginInfo;
        //默认为session
        EDeployMode deploy = EComponentType.FLINK.equals(type.getComponentType()) ? EDeployMode.SESSION : EDeployMode.PERJOB;
        //spark 暂时全部为perjob
        if (Objects.nonNull(deployMode) && !EComponentType.SPARK.equals(type.getComponentType())) {
            deploy = EDeployMode.getByType(deployMode);
        }
        JSONObject flinkConf = clusterConfigJson.getJSONObject(type.getComponentType().getConfName());
        pluginInfo = flinkConf.getJSONObject(deploy.getMode());
        if (Objects.isNull(pluginInfo)) {
            throw new RdosDefineException(String.format("对应模式【%s】未配置信息", deploy.name()));
        }
        String typeName = flinkConf.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            pluginInfo.put(TYPE_NAME, typeName);
        }
        if (EComponentType.SPARK.equals(type.getComponentType())) {
            JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
            if (Objects.nonNull(sftpConfig)) {
                String confHdfsPath = sftpConfig.getString("path") + File.separator + componentService.buildConfRemoteDir(clusterVO.getId());
                pluginInfo.put("confHdfsPath", confHdfsPath);
            }
        }
        return pluginInfo;
    }


    public JSONObject convertSQLComponent(JSONObject jdbcInfo, JSONObject pluginInfo) {
        pluginInfo = new JSONObject();
        if (Objects.isNull(jdbcInfo)) {
            return pluginInfo;
        }
        pluginInfo.put("jdbcUrl", jdbcInfo.getString("jdbcUrl"));
        pluginInfo.put("username", jdbcInfo.getString("username"));
        pluginInfo.put("password", jdbcInfo.getString("password"));
        return pluginInfo;
    }

    private void removeMd5FieldInHadoopConf(JSONObject pluginInfo) {
        if (!pluginInfo.containsKey(EComponentType.HDFS.getConfName())) {
            return;
        }
        JSONObject hadoopConf = pluginInfo.getJSONObject(EComponentType.HDFS.getConfName());
        hadoopConf.remove(ConfigConstant.MD5_SUM_KEY);
        pluginInfo.put(EComponentType.HDFS.getConfName(), hadoopConf);
    }

    private String getZipFileMD5(JSONObject clusterConfigJson) {
        JSONObject hadoopConf = clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName());
        if (null != hadoopConf && hadoopConf.containsKey(ConfigConstant.MD5_SUM_KEY)) {
            return hadoopConf.getString(ConfigConstant.MD5_SUM_KEY);
        }
        return "";
    }

    /**
     * 集群下拉列表
     */
    public List<ClusterVO> clusters() {
        PageQuery<ClusterDTO> pageQuery = new PageQuery<>(1, 1000, "gmt_modified", Sort.DESC.name());
        pageQuery.setModel(new ClusterDTO());
        List<Cluster> clusterVOS = clusterDao.generalQuery(pageQuery);
        if (CollectionUtils.isNotEmpty(clusterVOS)) {
            return ClusterVO.toVOs(clusterVOS);
        }
        return Lists.newArrayList();
    }

    public Cluster getOne(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if (one == null) {
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER.getDescription());
        }
        return one;

    }

    public String tiDBInfo(Long dtUicTenantId, Long dtUicUserId){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.TiDB);
    }

    public String oracleInfo(Long dtUicTenantId, Long dtUicUserId){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.Oracle);
    }


    public String greenplumInfo(Long dtUicTenantId, Long dtUicUserId){
        return accountInfo(dtUicTenantId,dtUicUserId,DataSourceType.GREENPLUM6);
    }

    public String prestoInfo(Long dtUicTenantId, Long dtUicUserId) {
        return accountInfo(dtUicTenantId, dtUicUserId, DataSourceType.Presto);
    }


    private String accountInfo(Long dtUicTenantId, Long dtUicUserId, DataSourceType dataSourceType) {
        EComponentType componentType = null;
        if (DataSourceType.Oracle.equals(dataSourceType)) {
            componentType = EComponentType.ORACLE_SQL;
        } else if (DataSourceType.TiDB.equals(dataSourceType)) {
            componentType = EComponentType.TIDB_SQL;
        } else if (DataSourceType.GREENPLUM6.equals(dataSourceType)) {
            componentType = EComponentType.GREENPLUM_SQL;
        } else if (DataSourceType.Presto.equals(dataSourceType)) {
            componentType = EComponentType.PRESTO_SQL;
        }
        if (componentType == null) {
            throw new RdosDefineException("不支持的数据源类型");
        }
        //优先绑定账号
        String jdbcInfo = getConfigByKey(dtUicTenantId, componentType.getConfName(), false);
        User dtUicUser = userDao.getByDtUicUserId(dtUicUserId);
        if (null == dtUicUser) {
            return jdbcInfo;
        }
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        AccountTenant dbAccountTenant = accountTenantDao.getByUserIdAndTenantIdAndEngineType(dtUicUser.getId(), tenantId, dataSourceType.getVal());
        if (null == dbAccountTenant) {
            return jdbcInfo;
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if(null == account){
            return jdbcInfo;
        }
        JSONObject data = JSONObject.parseObject(jdbcInfo);
        data.put("username", account.getName());
        data.put("password", Base64Util.baseDecode(account.getPassword()));
        return data.toJSONString();
    }


    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    public void deleteCluster(Long clusterId){
        if(null == clusterId){
            throw new RdosDefineException("集群不能为空");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException("集群不存在");
        }
        if(DEFAULT_CLUSTER_ID.equals(clusterId)){
            throw new RdosDefineException("默认集群不能删除");
        }
        List<Long> engineIds = null;
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if(CollectionUtils.isNotEmpty(engines)){
            engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        }
        List<EngineTenant> engineTenants = null;
        if(null != engineIds){
            engineTenants = engineTenantDao.listByEngineIds(engineIds);
        }
        if(CollectionUtils.isNotEmpty(engineTenants)){
            throw new RdosDefineException(String.format("集群下%s有租户，无法删除",cluster.getClusterName()));
        }
        clusterDao.deleteCluster(clusterId);
    }

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    public ClusterVO getCluster( Long clusterId,  Boolean kerberosConfig, Boolean removeTypeName) {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if (CollectionUtils.isEmpty(engines)) {
            return clusterVO;
        }
        List<Long> engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        List<Component> components = componentDao.listByEngineIds(engineIds);

        Map<EComponentScheduleType, List<Component>> scheduleType = new HashMap<>();
        if (CollectionUtils.isNotEmpty(components)) {
            scheduleType = components.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = new ArrayList<>();
        //为空也返回
        for (EComponentScheduleType value : EComponentScheduleType.values()) {
            SchedulingVo schedulingVo = new SchedulingVo();
            schedulingVo.setSchedulingCode(value.getType());
            schedulingVo.setSchedulingName(value.getName());
            schedulingVo.setComponents(ComponentVO.toVOS(scheduleType.get(value), null == removeTypeName || removeTypeName));
            schedulingVos.add(schedulingVo);
        }
        clusterVO.setScheduling(schedulingVos);
        return clusterVO;
    }


    public List<ClusterEngineVO> getAllCluster() {
        List<ClusterEngineVO> result = new ArrayList<>();

        List<Cluster> clusters = clusterDao.listAll();
        for (Cluster cluster : clusters) {
            ClusterEngineVO vo = ClusterEngineVO.toVO(cluster);
            vo.setEngines(engineService.listClusterEngines(cluster.getId(), true));
            result.add(vo);
        }

        return result;
    }

    public String pluginInfoForType(Long dtUicTenantId, Boolean fullKerberos, Integer pluginType) {
        EComponentType type = EComponentType.getByCode(pluginType);
        return getConfigByKey(dtUicTenantId, type.getConfName(),fullKerberos);
    }

    public String dbInfo(Long dtUicTenantId, Long dtUicUserId, Integer type) {
        DataSourceType sourceType = DataSourceType.getSourceType(type);
        return accountInfo(dtUicTenantId,dtUicUserId,sourceType);
    }
}

