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
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.router.login.DtUicUserConnect;
import com.dtstack.schedule.common.enums.DataSourceType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.Sort;
import com.dtstack.schedule.common.util.Base64Util;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.*;
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
    private static final String MAILBOX_CUTTING = "@";

    @Autowired
    private ClusterDao clusterDao;

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
    private EnvironmentContext environmentContext;

    @Autowired
    private DtUicUserConnect dtUicUserConnect;

    @Autowired
    private ComponentConfigService componentConfigService;


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
        return ClusterVO.toVO(cluster);
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
        return StringUtils.EMPTY;
    }

    public ClusterVO clusterExtInfo(Long uicTenantId) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(uicTenantId);
        if(null == clusterId){
            return null;
        }
        return getCluster(clusterId,false);
    }

    /**
     * 内部使用
     */
    public JSONObject pluginInfoJSON( Long dtUicTenantId,  String engineTypeStr, Long dtUicUserId,Integer deployMode) {
        if (EngineType.Dummy.name().equalsIgnoreCase(engineTypeStr)) {
            JSONObject dummy = new JSONObject();
            dummy.put(TYPE_NAME_KEY, EngineType.Dummy.name().toLowerCase());
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
                    .fluentPut("principal", kerberosConfig.getPrincipal())
                    .fluentPut("krbName", kerberosConfig.getKrbName())
                    .fluentPut("kerberosFileTimestamp", kerberosConfig.getGmtModified())
                    .fluentPut(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
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
            Map<String, String> sftpConfig = componentService.getComponentByClusterId(clusterId,EComponentType.SFTP.getTypeCode(),false,Map.class);
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
        //先获取绑定的
        Long queueId = engineTenantDao.getQueueIdByTenantId(tenantId);
        Queue queue = queueDao.getOne(queueId);
        if (queue != null) {
            return queue;
        }

        Engine hadoopEngine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (hadoopEngine == null) {
            return null;
        }

        List<Queue> queues = queueDao.listByEngineIdWithLeaf(Lists.newArrayList(hadoopEngine.getId()));
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

            Long clusterId = engineTenantDao.getClusterIdByTenantId(tenantId);
            if (null == clusterId) {
                return null;
            }
            JSONObject componentConf = componentService.getComponentByClusterId(clusterId,type.getComponentType().getTypeCode(),false,JSONObject.class);
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
                        JSONObject componentConfig = componentService.getComponentByClusterId(cluster.getClusterId(), component.getComponentTypeCode(), false, JSONObject.class);
                        config.put(type.getConfName(), componentConfig);
                    }
                }
            }
        }
        config.put("clusterName", cluster.getClusterName());
        return config;
    }

    /**
     * 如果只用集群Id 不要使用此接口
     * 填充信息到组件 集群—>引擎->组件
     * @param dtUicTenantId
     * @return
     */
    public ClusterVO getClusterByTenant(Long dtUicTenantId) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(dtUicTenantId);
        if(Objects.isNull(clusterId)){
            return getCluster(DEFAULT_CLUSTER_ID,false);
        }
        return getCluster(clusterId,false);
    }

    /**
     * 只有集群信息
     * @param dtUicTenantId
     * @return
     */
    public Cluster getCluster(Long dtUicTenantId) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(dtUicTenantId);
        if(null == clusterId){
            return null;
        }
        return clusterDao.getOne(clusterId);
    }

    public String getConfigByKey(Long dtUicTenantId, String key, Boolean fullKerberos) {
        Long clusterId = Optional.ofNullable(engineTenantDao.getClusterIdByTenantId(dtUicTenantId)).orElse(DEFAULT_CLUSTER_ID);
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(key);
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        if (null == component) {
            return "{}";
        }
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, JSONObject.class);
        if (configObj != null) {
            KerberosConfig kerberosConfig = null;
            if (StringUtils.isNotBlank(component.getKerberosFileName())) {
                //开启kerberos的kerberosFileName不为空
                kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType.getTypeCode());
            }
            //返回版本
            configObj.put(ComponentService.VERSION, component.getHadoopVersion());
            // 添加组件的kerberos配置信息 应用层使用
            addKerberosConfigWithHdfs(key, clusterId, kerberosConfig, configObj);
            //填充sftp配置项
            Map sftpMap = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class);
            if (MapUtils.isNotEmpty(sftpMap)) {
                configObj.put(EComponentType.SFTP.getConfName(), sftpMap);
            }
            return configObj.toJSONString();
        }
        return "{}";
    }



    /**
     * 如果开启集群开启了kerberos认证，kerberosConfig中还需要包含hdfs配置
     *
     * @param key
     * @param clusterId
     * @param kerberosConfig
     * @param configObj
     */
    public void addKerberosConfigWithHdfs(String key, Long clusterId, KerberosConfig kerberosConfig, JSONObject configObj) {
        if (Objects.nonNull(kerberosConfig)) {
            KerberosConfigVO kerberosConfigVO = KerberosConfigVO.toVO(kerberosConfig);
            if (!Objects.equals(EComponentType.HDFS.getConfName(), key)) {
                Map hdfsComponent = componentService.getComponentByClusterId(clusterId, EComponentType.HDFS.getTypeCode(),false,Map.class);
                if (MapUtils.isEmpty(hdfsComponent)) {
                    throw new RdosDefineException("开启kerberos后需要预先保存hdfs组件");
                }
                kerberosConfigVO.setHdfsConfig(hdfsComponent);
            }
            kerberosConfigVO.setKerberosFileTimestamp(kerberosConfig.getGmtModified());
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
            pluginInfo = clusterConfigJson.getJSONObject(EComponentType.LIBRA_SQL.getConfName());
            pluginInfo.put(TYPE_NAME, "postgresql");
        } else if (EComponentType.IMPALA_SQL == type.getComponentType()) {
            pluginInfo = clusterConfigJson.getJSONObject(EComponentType.IMPALA_SQL.getConfName());
            pluginInfo.put(TYPE_NAME, "impala");
        } else if (EComponentType.TIDB_SQL == type.getComponentType()) {
            pluginInfo = JSONObject.parseObject(tiDBInfo(clusterVO.getDtUicTenantId(), clusterVO.getDtUicUserId()));
            pluginInfo.put(TYPE_NAME, "tidb");
        } else if (EComponentType.ORACLE_SQL == type.getComponentType()) {
            pluginInfo = JSONObject.parseObject(oracleInfo(clusterVO.getDtUicTenantId(), clusterVO.getDtUicUserId()));
            pluginInfo.put(TYPE_NAME, "oracle");
        } else if (EComponentType.GREENPLUM_SQL == type.getComponentType()) {
            pluginInfo = JSONObject.parseObject(greenplumInfo(clusterVO.getDtUicTenantId(),clusterVO.getDtUicUserId()));
            pluginInfo.put(TYPE_NAME, "greenplum");
        } else if (EComponentType.PRESTO_SQL == type.getComponentType()) {
            pluginInfo = JSONObject.parseObject(prestoInfo(clusterVO.getDtUicTenantId(),clusterVO.getDtUicUserId()));
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
            } else if (EComponentType.DT_SCRIPT == type.getComponentType() || EComponentType.SPARK == type.getComponentType()) {
                if (clusterVO.getDtUicUserId() != null && clusterVO.getDtUicTenantId() != null) {
                    String ldapUserName = this.getLdapUserName(clusterVO.getDtUicUserId());
                    LOGGER.info("dtUicUserId:{},dtUicTenantId:{},ldapUserName:{}",clusterVO.getDtUicUserId(),clusterVO.getDtUicTenantId() ,ldapUserName);
                    if (StringUtils.isNotBlank(ldapUserName) && ldapUserName.contains(MAILBOX_CUTTING)) {
                        ldapUserName = ldapUserName.substring(0, ldapUserName.indexOf(MAILBOX_CUTTING));
                    }
                    pluginInfo.put(LDAP_USER_NAME, ldapUserName);
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
            throw new RdosDefineException("hive component cannot be empty");
        }
        String jdbcUrl = pluginInfo.getString("jdbcUrl");
        //%s替换成默认的 供插件使用
        jdbcUrl = jdbcUrl.replace("/%s", environmentContext.getComponentJdbcToReplace());
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
        if(null == flinkConf || flinkConf.size() == 0){
            throw new RdosDefineException("Flink configuration information is empty");
        }
        pluginInfo = flinkConf.getJSONObject(deploy.getMode());
        if (Objects.isNull(pluginInfo)) {
            throw new RdosDefineException(String.format("Corresponding mode [%s] no information is configured", deploy.name()));
        }
        String typeName = flinkConf.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            pluginInfo.put(TYPE_NAME_KEY, typeName);
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



    Cache<Long, String> ldapCache = CacheBuilder
            .newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private String getLdapUserName(Long dtUicUserId) {
        if (StringUtils.isBlank(environmentContext.getUicToken()) || StringUtils.isBlank(environmentContext.getDtUicUrl())) {
            return null;
        }
        String ldapUserName = null;
        if (environmentContext.isOpenLdapCache()) {
            ldapUserName = ldapCache.getIfPresent(dtUicUserId);
            if (null != ldapUserName) {
                return ldapUserName;
            }
        }
        ldapUserName = dtUicUserConnect.getLdapUserName(dtUicUserId, environmentContext.getUicToken(), environmentContext.getDtUicUrl());
        ldapCache.put(dtUicUserId, ldapUserName);
        return ldapUserName;
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
        //查询未被删除的
        ClusterDTO clusterDTO = new ClusterDTO();
        clusterDTO.setIsDeleted(Deleted.NORMAL.getStatus());
        pageQuery.setModel(clusterDTO);
        List<Cluster> clusterVOS = clusterDao.generalQuery(pageQuery);
        if (CollectionUtils.isNotEmpty(clusterVOS)) {
            return ClusterVO.toVOs(clusterVOS);
        }
        return Lists.newArrayList();
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
            throw new RdosDefineException("Unsupported data source type");
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
            throw new RdosDefineException("Cluster cannot be empty");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException("Cluster does not exist");
        }
        if(DEFAULT_CLUSTER_ID.equals(clusterId)){
            throw new RdosDefineException("The default cluster cannot be deleted");
        }
        List<Long> engineIds = null;
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if(CollectionUtils.isNotEmpty(engines)){
            engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        }
        List<EngineTenant> engineTenants = null;
        if(CollectionUtils.isNotEmpty(engineIds)){
            engineTenants = engineTenantDao.listByEngineIds(engineIds);
        }
        if(CollectionUtils.isNotEmpty(engineTenants)){
            throw new RdosDefineException(String.format("Cluster %s has tenants and cannot be deleted",cluster.getClusterName()));
        }
        clusterDao.deleteCluster(clusterId);
    }

    public ClusterVO getCluster( Long clusterId, Boolean removeTypeName) {
        return getCluster(clusterId,removeTypeName,false);
    }

    /**
     * 获取集群信息详情 需要根据组件分组
     * @param clusterId
     * @return
     */
    public ClusterVO getCluster(Long clusterId, Boolean removeTypeName,boolean isFullPrincipal) {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if (CollectionUtils.isEmpty(engines)) {
            return clusterVO;
        }
        List<Long> engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        List<Component> components = componentDao.listByEngineIds(engineIds);
        List<ComponentVO> componentConfigs = componentConfigService.getComponentVoByComponent(components,
                null == removeTypeName || removeTypeName , clusterId,true);
        List<KerberosConfig> kerberosConfigs = null;
        if(isFullPrincipal){
            kerberosConfigs =  kerberosDao.getByClusters(clusterId);
        }

        Map<EComponentScheduleType, List<ComponentVO>> scheduleType = new HashMap<>();
        if (CollectionUtils.isNotEmpty(componentConfigs)) {
            scheduleType = componentConfigs.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = convertComponentToScheduling(kerberosConfigs, scheduleType);
        clusterVO.setScheduling(schedulingVos);
        return clusterVO;
    }

    private List<SchedulingVo> convertComponentToScheduling(List<KerberosConfig> kerberosConfigs, Map<EComponentScheduleType, List<ComponentVO>> scheduleType) {
        List<SchedulingVo> schedulingVos = new ArrayList<>();
        //为空也返回
        for (EComponentScheduleType value : EComponentScheduleType.values()) {
            SchedulingVo schedulingVo = new SchedulingVo();
            schedulingVo.setSchedulingCode(value.getType());
            schedulingVo.setSchedulingName(value.getName());
            schedulingVo.setComponents(scheduleType.getOrDefault(value,new ArrayList<>(0)));
            List<ComponentVO> componentVOS = scheduleType.get(value);
            if(CollectionUtils.isNotEmpty(componentVOS) && CollectionUtils.isNotEmpty(kerberosConfigs)){
                for (ComponentVO componentVO : componentVOS) {
                    for (KerberosConfig config : kerberosConfigs) {
                        if(componentVO.getComponentTypeCode().equals(config.getComponentType())){
                            componentVO.setPrincipal(config.getPrincipal());
                            componentVO.setPrincipals(config.getPrincipals());
                            componentVO.setMergeKrb5Content(config.getMergeKrbContent());
                        }
                    }
                }
            }
            schedulingVo.setComponents(componentVOS);
            schedulingVo.setComponents(scheduleType.getOrDefault(value,new ArrayList<>(0)));
            schedulingVos.add(schedulingVo);
        }
        return schedulingVos;
    }



    public List<ClusterEngineVO> getAllCluster() {
        List<ClusterEngineVO> result = new ArrayList<>();

        List<Cluster> clusters = clusterDao.listAll();
        List<Engine> engines = engineDao.listByEngineIds(new ArrayList<>());
        if (null == engines) {
            return new ArrayList<>();
        }
        Map<Long, List<Engine>> clusterEngineMapping = engines
                .stream()
                .collect(Collectors.groupingBy(Engine::getClusterId));
        List<Long> engineIds = engines.stream()
                .map(Engine::getId)
                .collect(Collectors.toList());

        List<Queue> queues = queueDao.listByEngineIdWithLeaf(engineIds);

        Map<Long, List<Queue>> engineQueueMapping = queues
                .stream()
                .collect(Collectors.groupingBy(Queue::getEngineId));


        for (Cluster cluster : clusters) {
            ClusterEngineVO vo = fillEngineQueueInfo(clusterEngineMapping, engineQueueMapping, cluster);
            result.add(vo);
        }

        return result;
    }

    private ClusterEngineVO fillEngineQueueInfo(Map<Long, List<Engine>> clusterEngineMapping, Map<Long, List<Queue>> engineQueueMapping, Cluster cluster) {
        ClusterEngineVO vo = ClusterEngineVO.toVO(cluster);
        List<Engine> engineList = clusterEngineMapping.get(vo.getClusterId());
        if (CollectionUtils.isNotEmpty(engineList)) {
            List<EngineVO> engineVOS = EngineVO.toVOs(engineList);
            for (EngineVO engineVO : engineVOS) {
                //页面接口不用queue信息 但是queue信息绑定租户要使用
                List<Queue> queueList = engineQueueMapping.get(engineVO.getEngineId());
                if (CollectionUtils.isNotEmpty(queueList)) {
                    engineVO.setQueues(QueueVO.toVOs(queueList));
                }
            }
            vo.setEngines(engineVOS);
        }
        return vo;
    }

    public String pluginInfoForType(Long dtUicTenantId, Boolean fullKerberos, Integer pluginType) {
        EComponentType type = EComponentType.getByCode(pluginType);
        return getConfigByKey(dtUicTenantId, type.getConfName(),fullKerberos);
    }

    public String dbInfo(Long dtUicTenantId, Long dtUicUserId, Integer type) {
        DataSourceType sourceType = DataSourceType.getSourceType(type);
        return accountInfo(dtUicTenantId,dtUicUserId,sourceType);
    }

    public Boolean isSameCluster(Long dtUicTenantId, List<Long> dtUicTenantIds) {
        if (dtUicTenantId ==null) {
            throw new RdosDefineException("The tenant id cannot be null");
        }

        if (CollectionUtils.isEmpty(dtUicTenantIds)) {
            return Boolean.FALSE;
        }

        Long clusterId = engineTenantDao.getClusterIdByTenantId(dtUicTenantId);

        if (clusterId == null) {
            throw new RdosDefineException("租户id:"+dtUicTenantId+"不存在!");
        }

        for (Long uicTenantId : dtUicTenantIds) {
            Long checkClusterId = engineTenantDao.getClusterIdByTenantId(uicTenantId);

            if (checkClusterId != null) {
                if (clusterId.equals(checkClusterId)) {
                    // dtUicTenantIds集合中存在和 dtUicTenantId相同的集群
                    return Boolean.TRUE;
                }
            }
        }

        return Boolean.FALSE;
    }
}

