package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageQuery;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.KerberosConfigVO;
import com.dtstack.engine.api.vo.SchedulingVo;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.enums.EComponentScheduleType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.utils.PublicUtil;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.enums.Sort;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
import com.dtstack.schedule.common.util.Base64Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
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

    private static ObjectMapper objectMapper = new ObjectMapper();

    private final static String DEFAULT_HADOOP_VERSION = "hadoop2";

    private final static List<String> BASE_CONFIG = Lists.newArrayList(EComponentType.HDFS.getConfName(),
            EComponentType.YARN.getConfName(), EComponentType.SPARK_THRIFT.getConfName(), EComponentType.SFTP.getConfName());

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineService engineService;

    @Autowired
    private QueueService queueService;

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

    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public void addDefaultCluster() throws Exception {
        Cluster cluster = new Cluster();
        cluster.setId(DEFAULT_CLUSTER_ID);
        cluster.setClusterName(DEFAULT_CLUSTER_NAME);
        clusterDao.insertWithId(cluster);

        boolean updateQueue = true;
        JSONObject componentConfig = new JSONObject();
        componentConfig.put(EComponentType.HDFS.getConfName(), new JSONObject().toJSONString());
        componentConfig.put(EComponentType.YARN.getConfName(), new JSONObject().toJSONString());
        componentConfig.put(EComponentType.SPARK_THRIFT.getConfName(), new JSONObject().toJSONString());
        componentConfig.put(EComponentType.SFTP.getConfName(), new JSONObject().toJSONString());

        engineService.addEnginesByComponentConfig(componentConfig, cluster.getId(), updateQueue);

        if (!updateQueue) {
            Engine hadoopEngine = engineDao.getByClusterIdAndEngineType(cluster.getId(), MultiEngineType.HADOOP.getType());
            if (hadoopEngine != null) {
                queueService.addDefaultQueue(hadoopEngine.getId());
            }
        }
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
            if (name.length() >= 32) {
                throw new RdosDefineException("名称过长");
            }
        } else {
            throw new RdosDefineException("名称不能为空");
        }
    }



    @Forbidden
    public ClusterVO getClusterByName(String clusterName) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        return ClusterVO.toVO(cluster);
    }

    public PageResult<List<ClusterVO>> pageQuery(@Param("currentPage") int currentPage, @Param("pageSize") int pageSize) {
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
    public String clusterInfo(@Param("tenantId") Long tenantId) {
        ClusterVO cluster = getClusterByTenant(tenantId);
        if (cluster != null) {
            JSONObject config = buildClusterConfig(cluster);
            return config.toJSONString();
        }

        LOGGER.error("无法取得集群信息，默认集群信息没有配置！");
        return StringUtils.EMPTY;
    }

    public String clusterExtInfo(@Param("tenantId") Long uicTenantId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(uicTenantId);
        if (tenantId == null) {
            return StringUtils.EMPTY;
        }
        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return StringUtils.EMPTY;
        }
        Engine engine = engineDao.getOne(engineIds.get(0));
        ClusterVO cluster = getCluster(engine.getClusterId(), true,false);
        return JSONObject.toJSONString(cluster);
    }

    /**
     * 对外接口
     */
    public JSONObject pluginInfoJSON(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr, @Param("dtUicUserId")Long dtUicUserId) {
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
        JSONObject pluginJson = convertPluginInfo(clusterConfigJson, type, cluster);
        if (pluginJson == null) {
            throw new RdosDefineException(format("The cluster is not configured [%s] engine", engineTypeStr));
        }

        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        Queue queue = getQueue(tenantId, cluster.getClusterId());

        pluginJson.put(QUEUE, queue == null ? null : queue.getQueueName());
        pluginJson.put(CLUSTER, cluster.getClusterName());
        pluginJson.put(TENANT_ID, tenantId);
        setComponentSftpDir(cluster.getClusterId(), clusterConfigJson, pluginJson,type);
        return pluginJson;
    }

    public String pluginInfo(@Param("tenantId") Long dtUicTenantId, @Param("engineType") String engineTypeStr,@Param("userId") Long dtUicUserId) {
        return pluginInfoJSON(dtUicTenantId, engineTypeStr, dtUicUserId).toJSONString();
    }

    /**
     * 填充对应的组件信息
     * @param clusterId
     * @param clusterConfigJson
     * @param pluginJson
     * @param type
     */
    private void setComponentSftpDir(Long clusterId, JSONObject clusterConfigJson, JSONObject pluginJson,EngineTypeComponentType type) {
        //sftp Dir
        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        EComponentType componentType = type.getComponentType();
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        KerberosConfig kerberosConfig = kerberosDao.getByComponentId(component.getId());
        if (MapUtils.isNotEmpty(sftpConfig) && Objects.nonNull(kerberosConfig)) {
            Integer openKerberos = kerberosConfig.getOpenKerberos();
            String remotePath = kerberosConfig.getRemotePath();
            Preconditions.checkState(StringUtils.isNotEmpty(remotePath), "remotePath can not be null");
            pluginJson.fluentPut("openKerberos", Objects.nonNull(openKerberos) && openKerberos > 0)
                    .fluentPut("remoteDir", remotePath)
                    .fluentPut("principalFile", kerberosConfig.getName());
        }
    }

    /**
     * 获取集群在sftp上的路径
     *
     * @param tenantId
     * @return
     */
    public String clusterSftpDir(@Param("tenantId") Long tenantId) {
        Long clusterId = engineTenantDao.getClusterIdByTenantId(tenantId);
        if (clusterId != null) {
            Map<String, String> sftpConfig = componentService.getSFTPConfig(clusterId);
            if (sftpConfig != null) {
                String path = sftpConfig.get("path");
                path += "/" + componentService.getSftpClusterKey(clusterId);
                return path;
            }
        }
        return null;
    }

    private Queue getQueue(Long tenantId, Long clusterId) {
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

    /**
     * 对外接口
     * FIXME 这里获取的hiveConf其实是spark thrift server的连接信息，后面会统一做修改
     */
    public String hiveInfo(@Param("tenantId") Long dtUicTenantId, @Param("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.SPARK_THRIFT.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String hiveServerInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.HIVE_SERVER.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String hadoopInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.HDFS.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String carbonInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.CARBON_DATA.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String impalaInfo(@Param("tenantId") Long dtUicTenantId,@Param("fullKerberos") Boolean fullKerberos) {
        return getConfigByKey(dtUicTenantId, EComponentType.IMPALA_SQL.getConfName(),fullKerberos);
    }

    /**
     * 对外接口
     */
    public String sftpInfo(@Param("tenantId") Long dtUicTenantId) {
        return getConfigByKey(dtUicTenantId, EComponentType.SFTP.getConfName(),false);
    }

    @Forbidden
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

    private ClusterVO getClusterByTenant(Long dtUicTenantId) {
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null) {
            return getCluster(DEFAULT_CLUSTER_ID, true,false);
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return getCluster(DEFAULT_CLUSTER_ID, true,false);
        }

        Engine engine = engineDao.getOne(engineIds.get(0));
        return getCluster(engine.getClusterId(), true,false);
    }

    public String getConfigByKey(@Param("dtUicTenantId")Long dtUicTenantId, @Param("key") String key,@Param("fullKerberos") Boolean fullKerberos) {
        ClusterVO cluster = getClusterByTenant(dtUicTenantId);
        JSONObject config = buildClusterConfig(cluster);
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(key);
        Component component = componentDao.getByClusterIdAndComponentType(cluster.getId(),componentType.getTypeCode());
        KerberosConfig kerberosConfig = kerberosDao.getByComponentId(component.getId());
        JSONObject configObj = config.getJSONObject(key);
        if (configObj != null) {
            addKerberosConfigWithHdfs(key, cluster, kerberosConfig, configObj);
            if (Objects.nonNull(fullKerberos) && fullKerberos) {
                //将sftp中keytab配置转换为本地路径
                this.fullKerberosFilePath(dtUicTenantId, configObj,component);
            }
            return configObj.toJSONString();
        }
        return "{}";
    }

    private <T> T fullKerberosFilePath(Long dtUicTenantId, T data,Component component) {
        Map<String, String> sftp = JSONObject.parseObject(this.sftpInfo(dtUicTenantId),Map.class);
        if (MapUtils.isEmpty(sftp)) {
            return data;
        } else {
            JSONObject dataMap = this.getJsonObject(data);
            this.accordToKerberosFile(sftp, dataMap,component);
            data = this.convertJsonOverBack(data, dataMap);
            return data;
        }
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
    private void accordToKerberosFile(Map<String, String> sftp, JSONObject dataMap, Component component) {
        try {
            JSONObject configJsonObject = dataMap.getJSONObject("kerberosConfig");
            if (Objects.isNull(configJsonObject)) {
                return;
            }
            KerberosConfig kerberosConfig = PublicUtil.strToObject(configJsonObject.toString(), KerberosConfig.class);
            if (Objects.isNull(kerberosConfig)) {
                return;
            }
            if (kerberosConfig.getOpenKerberos() <= 0) {
                return;
            }
            Preconditions.checkState(Objects.nonNull(kerberosConfig.getClusterId()));
            Preconditions.checkState(Objects.nonNull(kerberosConfig.getOpenKerberos()));
            Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getPrincipal()));
            Preconditions.checkState(StringUtils.isNotEmpty(kerberosConfig.getRemotePath()));
            Preconditions.checkState(Objects.nonNull(kerberosConfig.getComponentId()));
            String remoteSftpKerberosPath = componentService.buildSftpPath(kerberosConfig.getClusterId(), component.getComponentTypeCode()) +
                   File.separator +  ComponentService.KERBEROS_PATH;
            String localKerberosPath = componentService.getLocalKerberosPath(kerberosConfig.getClusterId(), component.getComponentTypeCode());
            KerberosConfigVerify.downloadKerberosFromSftp(remoteSftpKerberosPath, localKerberosPath, sftp);
            File file = new File(localKerberosPath);
            Preconditions.checkState(file.exists() && file.isDirectory(), "console kerberos local path not exist");
            File keytabFile = Arrays.stream(file.listFiles()).filter((obj) -> obj.getName().endsWith("keytab"))
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
        return;
    }

    public Map<String, Object> getConfig(Long dtUicTenantId, String key) {
        ClusterVO cluster = getClusterByTenant(dtUicTenantId);
        JSONObject config = buildClusterConfig(cluster);
        KerberosConfig kerberosConfig = componentService.getKerberosConfig(cluster.getId());

        JSONObject configObj = config.getJSONObject(key);
        if (configObj != null) {
            addKerberosConfigWithHdfs(key, cluster, kerberosConfig, configObj);
            return configObj;
        }
        return null;
    }

    /**
     * 如果开启集群开启了kerberos认证，kerberosConfig中还需要包含hdfs配置
     *
     * @param key
     * @param cluster
     * @param kerberosConfig
     * @param configObj
     */
    private void addKerberosConfigWithHdfs(String key, ClusterVO cluster, KerberosConfig kerberosConfig, JSONObject configObj) {
        if (Objects.nonNull(kerberosConfig)) {
            KerberosConfigVO kerberosConfigVO = KerberosConfigVO.toVO(kerberosConfig);
            if (!Objects.equals(EComponentType.HDFS.getConfName(), key)) {
                Component hdfsComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
                if (Objects.isNull(hdfsComponent)) {
                    throw new RdosDefineException("开启kerberos后需要预先保存hdfs组件");
                }
                kerberosConfigVO.setHdfsConfig(JSONObject.parseObject(hdfsComponent.getComponentConfig()));
            }
            configObj.put("kerberosConfig", kerberosConfigVO);
        }
    }

    @Forbidden
    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, EngineTypeComponentType type, ClusterVO clusterVO) {
        JSONObject pluginInfo;
        if (EComponentType.HDFS == type.getComponentType()) {
            pluginInfo = new JSONObject();
            //hdfs yarn%s-hdfs%s-hadoop%s的版本
            JSONObject hadoopConf = clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName());
            String typeName = hadoopConf.getString(TYPE_NAME);
            pluginInfo.put("typeName", typeName);
            pluginInfo.put(EComponentType.HDFS.getConfName(), hadoopConf);
            pluginInfo.put(EComponentType.YARN.getConfName(), clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName()));

        } else if (EComponentType.LIBRA_SQL == type.getComponentType()) {
            JSONObject libraConf = clusterConfigJson.getJSONObject(EComponentType.LIBRA_SQL.getConfName());
            pluginInfo = new JSONObject();
            // fixme 特殊逻辑，libra用的是engine端的postgresql插件
            pluginInfo.putAll(libraConf);
            pluginInfo.put("dbUrl", libraConf.getString("jdbcUrl"));
            pluginInfo.remove("jdbcUrl");
            pluginInfo.put("userName", libraConf.getString("username"));
            pluginInfo.remove("username");
            pluginInfo.put("pwd", libraConf.getString("password"));
            pluginInfo.remove("password");
            pluginInfo.put("typeName", "postgresql");
        } else if (EComponentType.IMPALA_SQL == type.getComponentType()) {
            JSONObject impalaConf = clusterConfigJson.getJSONObject(EComponentType.IMPALA_SQL.getConfName());
            pluginInfo = new JSONObject();
            pluginInfo.putAll(impalaConf);
            pluginInfo.put("dbUrl", impalaConf.getString("jdbcUrl"));
            pluginInfo.remove("jdbcUrl");
            pluginInfo.put("userName", impalaConf.getString("username"));
            pluginInfo.remove("username");
            pluginInfo.put("pwd", impalaConf.getString("password"));
            pluginInfo.remove("password");
            pluginInfo.put("typeName", "impala");
        } else if (EComponentType.TIDB_SQL == type.getComponentType()) {
            JSONObject tiDBConf = JSONObject.parseObject(tiDBInfo(clusterVO.getDtUicTenantId(),clusterVO.getDtUicUserId()));
            pluginInfo = new JSONObject();
            if(Objects.nonNull(tiDBConf)){
                pluginInfo.putAll(tiDBConf);
            }
            pluginInfo.put("dbUrl", tiDBConf.getString("jdbcUrl"));
            pluginInfo.remove("jdbcUrl");
            pluginInfo.put("userName", tiDBConf.getString("username"));
            pluginInfo.remove("username");
            pluginInfo.put("pwd", tiDBConf.getString("password"));
            pluginInfo.remove("password");
            pluginInfo.put("typeName", "tidb");
        } else {
            pluginInfo = clusterConfigJson.getJSONObject(type.getComponentType().getConfName());
            if (pluginInfo == null) {
                return null;
            }

            for (Iterator<Map.Entry<String, Object>> it = clusterConfigJson.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                if (!BASE_CONFIG.contains(entry.getKey())) {
                    it.remove();
                    continue;
                }
                if (EComponentType.DT_SCRIPT == type.getComponentType() && EComponentType.SPARK_THRIFT.getConfName().equals(entry.getKey())) {
                    //dt-script  不需要hive-site配置
                    continue;
                }
                pluginInfo.put(entry.getKey(), entry.getValue());
            }
            if (EComponentType.HIVE_SERVER == type.getComponentType()) {
                String jdbcUrl = pluginInfo.getString("jdbcUrl");
                jdbcUrl = jdbcUrl.replace("/%s", "");
                pluginInfo.put("dbUrl", jdbcUrl);
                pluginInfo.remove("jdbcUrl");
                pluginInfo.put("pwd", pluginInfo.getString("password"));
                pluginInfo.remove("password");
                pluginInfo.put("typeName", "hive");
                pluginInfo.put("userName", pluginInfo.getString("username"));
                pluginInfo.remove("username");
            }
            pluginInfo.put(ConfigConstant.MD5_SUM_KEY, getZipFileMD5(clusterConfigJson));
            removeMd5FieldInHadoopConf(pluginInfo);
        }

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
        if (hadoopConf.containsKey(ConfigConstant.MD5_SUM_KEY)) {
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

    public String tiDBInfo(@Param("tenantId") Long dtUicTenantId, @Param("userId") Long dtUicUserId){
        //优先绑定账号
        String jdbcInfo = getConfigByKey(dtUicTenantId, EComponentType.TIDB_SQL.getConfName(),false);
        User dtUicUser = userDao.getByDtUicUserId(dtUicUserId);
        if(Objects.isNull(dtUicUser)){
            return jdbcInfo;
        }
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        AccountTenant dbAccountTenant = accountTenantDao.getByAccount(dtUicUser.getId(), tenantId, null, Deleted.NORMAL.getStatus());
        if(Objects.isNull(dbAccountTenant)){
            return jdbcInfo;
        }
        Account account = accountDao.getById(dbAccountTenant.getAccountId());
        if(Objects.isNull(account)){
            return jdbcInfo;
        }
        JSONObject data = JSONObject.parseObject(jdbcInfo);
        data.put("username",account.getName());
        data.put("password", Base64Util.baseDecode(account.getPassword()));
        return data.toJSONString();
    }


    /**
     * 删除集群
     * 判断该集群下是否有租户
     * @param clusterId
     */
    public void deleteCluster(@Param("clusterId")Long clusterId){
        if(Objects.isNull(clusterId)){
            throw new RdosDefineException("集群不能为空");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if(Objects.isNull(cluster)){
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
        if(Objects.nonNull(engineIds)){
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
    public ClusterVO getCluster(@Param("clusterId") Long clusterId, @Param("kerberosConfig") Boolean kerberosConfig,@Param("kerberosConfig") Boolean removeTypeName) {
        Cluster cluster = clusterDao.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        if (CollectionUtils.isEmpty(engines)) {
            return clusterVO;
        }
        List<Long> engineIds = engines.stream().map(Engine::getId).collect(Collectors.toList());
        List<Component> components = componentDao.listByEngineIds(engineIds);

        Map<EComponentScheduleType, List<Component>> scheduleType = null;
        if (CollectionUtils.isNotEmpty(components)) {
            scheduleType = components.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = new ArrayList<>();
        //为空也返回
        for (EComponentScheduleType value : EComponentScheduleType.values()) {
            SchedulingVo schedulingVo = new SchedulingVo();
            schedulingVo.setSchedulingCode(value.getType());
            schedulingVo.setSchedulingName(value.getName());
            schedulingVo.setComponents(new ArrayList<>());
            if (EComponentScheduleType.resourceScheduling.getType() == value.getType()) {
                //资源调度组件单选
                if (Objects.nonNull(scheduleType) && scheduleType.containsKey(value)) {
                    List<Component> resourceComponents = scheduleType.get(value);
                    if (CollectionUtils.isNotEmpty(resourceComponents)) {
                        for (Component resourceComponent : resourceComponents) {
                            if (resourceComponent.getId() >= 0) {
                                schedulingVo.setComponents(ComponentVO.toVOS(Lists.newArrayList(resourceComponent),Objects.isNull(removeTypeName) ? true : removeTypeName));
                                break;
                            }
                        }
                    }
                }

            } else if (Objects.nonNull(scheduleType) && scheduleType.containsKey(value)) {
                schedulingVo.setComponents(ComponentVO.toVOS(scheduleType.get(value),Objects.isNull(removeTypeName) ? true : removeTypeName));
            }
            schedulingVos.add(schedulingVo);
        }
        clusterVO.setScheduling(schedulingVos);
        return clusterVO;
    }
}

