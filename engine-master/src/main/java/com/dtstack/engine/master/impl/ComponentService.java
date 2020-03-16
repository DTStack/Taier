package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.cache.ConsoleCache;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.hadoop.HadoopConfTool;
import com.dtstack.dtcenter.common.hadoop.IDownload;
import com.dtstack.dtcenter.common.hadoop.TemplateFileDownload;
import com.dtstack.dtcenter.common.kerberos.KerberosConfigVerify;
import com.dtstack.dtcenter.common.sftp.SFTPHandler;
import com.dtstack.dtcenter.common.sftp.SftpPath;
import com.dtstack.dtcenter.common.util.MD5Util;
import com.dtstack.dtcenter.common.util.ZipUtil;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.dto.Resource;
import com.dtstack.engine.master.component.ComponentFactory;
import com.dtstack.engine.master.component.ComponentImpl;
import com.dtstack.engine.master.component.SparkComponent;
import com.dtstack.engine.master.component.YARNComponent;
import com.dtstack.engine.master.enums.ComponentTempFile;
import com.dtstack.engine.master.enums.ComponentTypeNameNeedVersion;
import com.dtstack.engine.master.enums.KerberosKey;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.utils.EngineUtil;
import com.dtstack.engine.master.utils.HadoopConf;
import com.dtstack.engine.master.utils.XmlFileUtil;
import com.dtstack.engine.vo.TestConnectionVO;
import com.google.common.collect.Lists;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import com.dtstack.engine.domain.Queue;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    public static final String EMPTY_JSON_STRING = new JSONObject().toJSONString();

    private final static String ZIP_CONTENT_TYPE = "zip";

    private static String unzipLocation = System.getProperty("user.dir") + File.separator + "unzip";

    private static final Long DEFAULT_CLUSTER_ID = -1L;

    private static final String KERBEROS_FILE_SUF = "%sKerberosFile";

    private static final String KERBEROS_FILE = "kerberosFile";

    private static final String OPEN_KERBEROS = "openKerberos";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

    private static final String LOCAL_KERBEROS_PATH = "localKerberosPath";

    public static final String CLUSTER_KEY = "clusterKey";

    public static final String SFTP_HADOOP_CONFIG_PATH = "%s%s/%s";


    private static final String SEPARATE = "/";

    public static final Map<Integer, String> COMPONENT_NAME_MAP = new HashMap(){{
        put(EComponentType.FLINK.getTypeCode(), "flink");
        put(EComponentType.HIVE_SERVER.getTypeCode(), "hive");
        put(EComponentType.HDFS.getTypeCode(), "hdfs");
        put(EComponentType.SPARK.getTypeCode(), "spark");
        put(EComponentType.YARN.getTypeCode(), "yarn");
    }};


    @Autowired
    private ComponentDao componentDao;

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private EngineDao engineDao;

    @Autowired
    private QueueDao queueDao;

    @Autowired
    private QueueService queueService;

    @Autowired
    private EngineService engineService;

    @Autowired
    private EngineTenantDao engineTenantDao;

    @Autowired
    private TenantDao tenantDao;

    @Autowired
    private ConsoleCache consoleCache;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KerberosDao kerberosDao;

    public static final String TYPE_NAME = "typeName";

    /**
     * {
     *     "1":{
     *         "xx":"xx"
     *     }
     * }
     */
    public String listConfigOfComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType){
        JSONObject result = new JSONObject();
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null){
            return result.toJSONString();
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if(CollectionUtils.isEmpty(engineIds)){
            return result.toJSONString();
        }

        List<Engine> engines = engineDao.listByEngineIds(engineIds);
        if(CollectionUtils.isEmpty(engines)){
            return result.toJSONString();
        }

        Engine targetEngine = null;
        for (Engine engine : engines) {
            if(engine.getEngineType() == engineType){
                targetEngine = engine;
                break;
            }
        }

        if(targetEngine == null){
            return result.toJSONString();
        }

        List<Component> componentList = componentDao.listByEngineId(targetEngine.getId());
        for (Component component : componentList) {
            result.put(String.valueOf(component.getComponentTypeCode()), JSONObject.parseObject(component.getComponentConfig()));
        }

        return result.toJSONString();
    }

    public List<Component> addComponent(@Param("engineId") Long engineId, @Param("componentTypeCodeList") List<Integer> componentTypeCodeList){
        EngineAssert.assertTrue(CollectionUtils.isNotEmpty(componentTypeCodeList), ErrorCode.INVALID_PARAMETERS.getDescription());
        checkComponentIsRepeat(engineId, componentTypeCodeList);

        List<Component> componentList = new ArrayList<>();
        for (Integer integer : componentTypeCodeList) {
            EComponentType componentType = EComponentType.getByCode(integer);

            Component component = new Component();
            component.setEngineId(engineId);
            component.setComponentName(componentType.getName());
            component.setComponentTypeCode(componentType.getTypeCode());
            component.setComponentConfig(EMPTY_JSON_STRING);

            componentDao.insert(component);
            componentList.add(component);
        }

        return componentList;
    }

    private void checkComponentIsRepeat(Long engineId, List<Integer> componentTypeCodeList){
        List<Component> componentList = listComponent(engineId);
        if (CollectionUtils.isEmpty(componentList)){
            return;
        }

        for (Component component : componentList) {
            if(componentTypeCodeList.contains(component.getComponentTypeCode())){
                throw new RdosDefineException("组件:" + component.getComponentName() + " 已经存在，不能重复添加");
            }
        }
    }

    public void delete(@Param("componentId") Long componentId){
        Component component = componentDao.getOne(componentId);
        EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());

        if(EngineUtil.isRequiredComponent(component.getComponentTypeCode())){
            throw new RdosDefineException(component.getComponentName() + " 是必选组件，不可删除");
        }

        component.setIsDeleted(Deleted.DELETED.getStatus());
        componentDao.update(component);
        rmEmptyEngine(component.getEngineId());
    }

    private void rmEmptyEngine(Long engineId) {
        List<Component> componentList = listComponent(engineId);
        if (CollectionUtils.isEmpty(componentList)) {
            engineDao.delete(engineId);
        }
    }

    public Component getOne(Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException("组件不存在");
        }
        return component;
    }

    public void updateWithKerberos(@Param("componentId") Long componentId, @Param("configString") String configString, @Param("resources") List<Resource> resources) throws Exception {
        Component component = getOne(componentId);
        Long clusterId = componentDao.getClusterIdByComponentId(componentId);
        String sftpClusterKey = getSftpClusterKey(clusterId);
        String localKerberosConfDir = env.getLocalKerberosDir() + SEPARATE + sftpClusterKey;

        JSONObject config = JSONObject.parseObject(configString);

        //1.下载解析xml
        Map kerberosConfigMap;
        if (CollectionUtils.isNotEmpty(resources)) {
            kerberosConfigMap = getKerberosConfigStr(resources, localKerberosConfDir);
            setKerberosFile(resources.get(0), config);
        } else {
            kerberosConfigMap = config.getJSONObject(KERBEROS_CONFIG);
            //下载
            KerberosConfigVerify.downloadKerberosFromSftp(sftpClusterKey, localKerberosConfDir, getSFTPConfig(clusterId));
        }
        config.put(KERBEROS_CONFIG, kerberosConfigMap);

        //2.更新配置，校验连通性
        update(componentId, config.toString(), clusterId);

        //3.上传kerberos配置
        if (CollectionUtils.isNotEmpty(resources)) {
            uploadSftpDir(clusterId, localKerberosConfDir);
        }
    }

    private void setKerberosFile(Resource resource, JSONObject dataJson) {
        Map<String,String> kerberosFile = new HashMap<>();
        kerberosFile.put("name", resource.getFileName());
        kerberosFile.put("modifyTime", Timestamp.valueOf(LocalDateTime.now()).toString());
        dataJson.put("kerberosFile", kerberosFile);
        dataJson.put("openKerberos", true);
    }

    public String getSftpClusterKey(Long clusterId) {
        return AppType.CONSOLE.name() + "_" + clusterId;
    }

    private Map<String, String> getKerberosConfigStr(List<Resource> resources, String localKerberosConf) throws Exception {
        if (CollectionUtils.isNotEmpty(resources)) {
            return parseKerberosConfig(resources.get(0), localKerberosConf);
        }
        throw new RdosDefineException("缺少xml配置文件");
    }

    private Map<String, String> parseKerberosConfig(Resource resource, String localKerberosConf) throws Exception {
        Map<String, Map<String, String>> confMap = KerberosConfigVerify.parseKerberosFromUpload(resource.getUploadedFileName(), localKerberosConf);
        if (MapUtils.isNotEmpty(confMap)) {
            Map<String, String> map = new HashMap<>();
            for (String key : confMap.keySet()) {
                map.putAll(confMap.get(key));
            }
            return map;
        }
        throw new RdosDefineException("缺少xml配置文件");
    }

    public void update(@Param("componentId") Long componentId, @Param("configString") String configString, @Param("clusterId") Long clusterId) {
        Component component = componentDao.getOne(componentId);
        EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());

        EComponentType componentType = EComponentType.getByCode(component.getComponentTypeCode());
        Map<String, Object> config = JSONObject.parseObject(configString, Map.class);
        Cluster cluster = clusterService.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        updateTypeNameVersion(clusterId, component, config,cluster);
        addDefaultProperties(componentType, config);

        checkKerberosConfig(config,clusterId,componentType,cluster);
        ComponentImpl componentImpl = ComponentFactory.getComponent(config, componentType);

        addExtraConfig(componentType, componentImpl, component);

        component.setComponentConfig(componentImpl.getJsonString());
        componentDao.update(component);

        addClusterKerberosConfig(config, component);
        componentImpl = ComponentFactory.getComponent(config, componentType);
        testConnection(componentImpl, component);
    }

    private void addDefaultProperties(EComponentType componentType, Map<String, Object> config) {
        if (EComponentType.SFTP.equals(componentType)) {
            String authType = MapUtils.getString(config, SFTPHandler.KEY_AUTHENTICATION);
            String rsaPath = MapUtils.getString(config, SFTPHandler.KEY_RSA);
            String username = MapUtils.getString(config, SFTPHandler.KEY_USERNAME);
            if (SftpAuthType.RSA.getType().toString().equals(authType) && StringUtils.isBlank(rsaPath) && StringUtils.isNotBlank(username)) {
                rsaPath = String.format(SFTPHandler.DEFAULT_RSA_PATH_TEMPLATE, username);
                config.put(SFTPHandler.KEY_RSA, rsaPath);
            }
        }
    }

    /**
     * 更新组件typeName的hadoopVersion后缀
     *
     * @param clusterId
     * @param component
     * @param config
     */
    private void updateTypeNameVersion(Long clusterId, Component component, Map<String, Object> config,Cluster cluster) {
        for (ComponentTypeNameNeedVersion type : ComponentTypeNameNeedVersion.listByCode(component.getComponentTypeCode())) {
            String typeName = (String) config.get(TYPE_NAME);
            if (StringUtils.isNotEmpty(typeName)) {
                if (typeName.contains(type.getTypeName())) {
                    config.put(TYPE_NAME, type.getTypeName() + "-" + cluster.getHadoopVersion());
                }
            }
        }
    }

    /**
     * 保持原有的kerberos
     *
     * @param originalComponentConfig
     * @param config
     */
    private void keepOriginalKerberos(String originalComponentConfig, Map<String, Object> config) {
        JSONObject configObj = JSONObject.parseObject(originalComponentConfig);
        JSONObject kerberosConfig = configObj.getJSONObject(KERBEROS_CONFIG);

        JSONObject kerberosParam = (JSONObject) config.get(KERBEROS_CONFIG);
        if (kerberosParam == null && kerberosConfig != null) {
            config.put(KERBEROS_CONFIG, kerberosConfig);
        } else if (kerberosParam == null && kerberosConfig == null) {
            config.put(OPEN_KERBEROS, false);
        }
    }

    public Map<String, Object> fillKerberosConfig(JSONObject allConf, Long clusterId) {
        JSONObject kerberosConfig = allConf.getJSONObject(KERBEROS_CONFIG);
        allConf.putAll(KerberosConfigVerify.replaceFilePath(allConf, getClusterLocalKerberosDir(clusterId)));
        allConf.put(KERBEROS_CONFIG, KerberosConfigVerify.replaceFilePath(kerberosConfig, getClusterLocalKerberosDir(clusterId)));
        return allConf;
    }

    public Map<String, Object> fillKerberosConfig(String allConfString, Long clusterId) {
        JSONObject allConf = JSONObject.parseObject(allConfString);
        allConf.putAll(KerberosConfigVerify.replaceFilePath(allConf, getClusterLocalKerberosDir(clusterId)));
        JSONObject kerberosConfig = allConf.getJSONObject(KERBEROS_CONFIG);
        if (kerberosConfig != null) {
            allConf.put(KERBEROS_CONFIG, KerberosConfigVerify.replaceFilePath(kerberosConfig, getClusterLocalKerberosDir(clusterId)));
        }
        return allConf;
    }

    private boolean setOpenKerberos(Map<String, Object> config) {
        boolean openKerberos = config.get(KERBEROS_CONFIG) != null;
        config.put(OPEN_KERBEROS, openKerberos);
        return openKerberos;
    }

    private void addExtraConfig(EComponentType componentType, ComponentImpl componentImpl, Component component){
        if (EComponentType.SPARK == componentType) {
            Engine engine = engineDao.getOne(component.getEngineId());
            Cluster cluster = clusterDao.getOne(engine.getClusterId());
            ((SparkComponent) componentImpl).addExtraConfig(env.getHadoopConfigField(),
                    getHadoopConfigPath(cluster.getId()));
        }
    }

    private void testConnection(ComponentImpl componentImpl, Component component){
        componentImpl.checkConfig();

        if(EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()){
            try {
                ((YARNComponent)componentImpl).initClusterResource(true);
                ClusterResourceDescription description = ((YARNComponent)componentImpl).getResourceDescription();

                engineService.updateResource(component.getEngineId(), description);
                queueService.updateQueue(component.getEngineId(), description);
            } catch (Exception e){
                LOGGER.error("更新队列信息失败: ", e);
                throw new RdosDefineException("更新队列信息失败");
            }
        } else {
            try {
                componentImpl.testConnection();
            } catch (Exception e){
                throw new RdosDefineException(ErrorCode.TEST_CONN_FAIL.getDescription());
            }
        }

        updateCache(component.getEngineId());
    }

    private void addClusterKerberosConfig(Map<String, Object> config, Component component) {
        Engine engine = engineService.getOne(component.getEngineId());
        KerberosConfig kerberosConfig = kerberosDao.getByClusterId(engine.getClusterId());
        if (Objects.nonNull(kerberosConfig)) {
            Map<String, String> sftpConfig = getSFTPConfig(engine.getClusterId());
            try {
                String clusterLocalKerberosDir = getClusterLocalKerberosDir(engine.getClusterId());
                KerberosConfigVerify.downloadKerberosFromSftp(getSftpClusterKey(engine.getClusterId()), getClusterLocalKerberosDir(engine.getClusterId()), sftpConfig);
                config.putIfAbsent(KerberosKey.KEYTAB.getKey(), clusterLocalKerberosDir + SEPARATE + kerberosConfig.getName());
            } catch (SftpException e) {
                LOGGER.error("downloadKerberosFromSftp error {}", e);
            }
            config.putIfAbsent(KerberosKey.PRINCIPAL.getKey(), kerberosConfig.getPrincipal());
            if (!Objects.equals(EComponentType.HDFS.getTypeCode(), component.getComponentTypeCode())) {
                Component hdfsComponent = componentDao.getByEngineIdAndComponentType(component.getEngineId(), EComponentType.HDFS.getTypeCode());
                if (Objects.isNull(hdfsComponent)) {
                    throw new RdosDefineException("开启kerberos后需要预先保存hdfs组件");
                }
                config.putIfAbsent(KerberosKey.HDFS_CONFIG.getKey(), hdfsComponent.getComponentConfig());
            }
        }
    }

    /**
     * 更新缓存
     */
    public void updateCache(Long engineId){
        List<Queue> refreshQueues = queueDao.listByEngineId(engineId);
        if(CollectionUtils.isEmpty(refreshQueues)){
            return;
        }

        List<Long> queueIds = refreshQueues.stream().map(BaseEntity::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(queueIds)) {
            return;
        }

        List<Long> tenantIds = engineTenantDao.listTenantIdByQueueIds(queueIds);
        List<Long> dtUicTenantIds = tenantDao.listDtUicTenantIdByIds(tenantIds);
        if (!dtUicTenantIds.isEmpty()) {
            for (Long uicTenantId : dtUicTenantIds) {
                consoleCache.publishRemoveMessage(uicTenantId.toString());
            }
        }
    }

    public List<Component> listComponent(Long engineId){
        return componentDao.listByEngineId(engineId);
    }

    public TestConnectionVO testConnections(@Param("componentConfigs") String componentConfigs, @Param("clusterId") Long clusterId, @Param("resources") List<Resource> resources) throws Exception {
        Map<String, Resource> resourceMap = convertToMap(resources);
        JSONObject configs = JSONObject.parseObject(componentConfigs);
        if(configs == null || configs.isEmpty()){
            return TestConnectionVO.EMPTY_RESULT;
        }

        ClusterResourceDescription description = null;
        List<TestConnectionVO.ComponentTestResult> testResults = new ArrayList<>();
        for (String key : configs.keySet()) {
            EComponentType type;
            try {
                type = EComponentType.getByConfName(key);
            } catch (Exception e){
                continue;
            }

            Map configMap = configs.getObject(key, Map.class);
            setKerberosConfig(clusterId, configMap, resourceMap, key);
            Map<String, Object> kerberosConfig = fillKerberosConfig(JSONObject.toJSONString(configMap), clusterId);
            ComponentImpl component = ComponentFactory.getComponent(kerberosConfig, type);

            TestConnectionVO.ComponentTestResult result = new TestConnectionVO.ComponentTestResult();
            result.setComponentTypeCode(type.getTypeCode());
            try {
                component.checkConfig();
                if(EComponentType.YARN == type){
                    ((YARNComponent)component).initClusterResource(true);
                    description = ((YARNComponent)component).getResourceDescription();
                } else {
                    component.testConnection();
                }

                result.setResult(true);
            } catch (Exception e){
                result.setResult(false);
                result.setErrorMsg(e.getMessage());
            }

            testResults.add(result);
        }

        TestConnectionVO vo = new TestConnectionVO();
        vo.setTestResults(testResults);
        vo.setDescription(description);
        return vo;
    }

    private void setKerberosConfig(Long clusterId, Map configMap, Map<String, Resource> resourceMap, String key) throws Exception {
        EComponentType type = EComponentType.getByConfName(key);
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, type.getTypeCode());
        if (component == null) {
            return;
        }
        JSONObject config = JSONObject.parseObject(component.getComponentConfig());

        String clusterKey = getSftpClusterKey(clusterId);
        String localKerberosConf = env.getLocalKerberosDir() + SEPARATE + clusterKey;
        Boolean openKerberos = MapUtils.getBoolean(configMap, OPEN_KERBEROS, false);
        if (EComponentType.HDFS.equals(type)) {
            if (MapUtils.getString(configMap, HadoopConfTool.DFS_NAMENODE_KERBEROS_PRINCIPAL) != null) {
                KerberosConfigVerify.downloadKerberosFromSftp(clusterKey, localKerberosConf, getSFTPConfig(clusterId));
                configMap.put(OPEN_KERBEROS, true);
            }
        } else if (openKerberos) {
            Resource resource = resourceMap.get(String.format(KERBEROS_FILE_SUF, key));
            if (resource == null) {
                JSONObject kerberosConfig = config.getJSONObject(KERBEROS_CONFIG);
                if (kerberosConfig == null) {
                    throw new RdosDefineException("kerberos配置错误");
                }
                KerberosConfigVerify.downloadKerberosFromSftp(clusterKey, localKerberosConf, getSFTPConfig(clusterId));
                configMap.put(KERBEROS_CONFIG, kerberosConfig);
            } else {
                //test路径
                Map<String, String> map = parseKerberosConfig(resource, localKerberosConf);
                configMap.put(KERBEROS_CONFIG, map);
            }
        }
    }

    private JSONObject convertTOJSON(Map<String, String> map) {
        JSONObject json = new JSONObject();
        for (String key : map.keySet()) {
            json.put(key, map.get(key));
        }
        return json;
    }

    private Map<String, Object> convertTOObj(Map<String, String> map) {
        Map<String, Object> json = new HashMap<>();
        for (String key : map.keySet()) {
            json.put(key, map.get(key));
        }
        return json;
    }

    private Map<String, Resource> convertToMap(List<Resource> resources) {
        Map<String, Resource> resourceMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(resources)) {
            for (Resource resource : resources) {
                resourceMap.put(resource.getKey(), resource);
            }
        }
        return resourceMap;
    }

    public JSONObject config(@Param("clusterId") Long clusterId,@Param("resources") List<Resource> resources){
        Cluster cluster = clusterDao.getOne(clusterId);
        if (cluster == null){
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND.getDescription());
        }

        Engine hadoopEngine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if(hadoopEngine == null){
            throw new RdosDefineException("该集群没有配置[HADOOP]引擎");
        }
        //处理并强制校验
        JSONObject componentConfig = new JSONObject();
        List<Component> componentList = componentDao.listByEngineId(hadoopEngine.getId());
        //判断是否勾选hive-server选项
        boolean bool = componentList.stream().anyMatch(component -> EComponentType.HIVE_SERVER.getTypeCode() == component.getComponentTypeCode());
        //存在勾选操作 设置校验文件
        Map<String, Object> confMap = parseAndUploadXmlFile(cluster, resources, bool? Lists.newArrayList("hive-site.xml"):null);
        for (Component component : componentList) {
            if(EComponentType.HDFS.getTypeCode() == component.getComponentTypeCode()){
                componentConfig.put(EComponentType.HDFS.getName(), HadoopConf.getHadoopConf(confMap));
            } else if(EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()){
                componentConfig.put(EComponentType.YARN.getName(), HadoopConf.getYarnConf(confMap));
            }
        }
        if(confMap!= null && confMap.containsKey(ConfigConstant.MD5_SUM_KEY)){
            componentConfig.put(ConfigConstant.MD5_SUM_KEY, confMap.get(ConfigConstant.MD5_SUM_KEY));
        }
        JSONObject result = new JSONObject();
        result.put("componentConfig",componentConfig);

        boolean authorization = MapUtils.getBoolean(confMap, HadoopConfTool.IS_HADOOP_AUTHORIZATION,false);
        result.put("security",authorization);
        return result;
    }

    private Map<String, Object> parseAndUploadXmlFile(Cluster cluster, List<Resource> resources,List<String> validXml){
        Map<String, Object> confMap;
        String upzipLocation = null;
        List<File> xmlFiles;
        try {
            if (CollectionUtils.isEmpty(resources)) {
                throw new RdosDefineException("上传的文件不能为空");
            }

            Resource resource = resources.get(0);
            if (!resource.getContentType().contains(ZIP_CONTENT_TYPE)) {
                throw new RdosDefineException("压缩包格式仅支持ZIP格式");
            }

            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            String md5sum = MD5Util.getFileMD5String(new File(xmlZipLocation));
            upzipLocation = unzipLocation + File.separator + resource.getFileName();
            try {
                xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation,validXml);
            } catch (Exception e) {
                LOGGER.error("解压配置文件格式错误", e);
                throw new RdosDefineException("解压配置文件格式错误");
            }

            try {
                confMap = XmlFileUtil.parseAndRead(xmlFiles);
                confMap.put(ConfigConstant.MD5_SUM_KEY, md5sum);
            } catch (Exception e){
                throw new RdosDefineException("解析配置文件出错:" + e.getMessage());
            }

            //上传xml文件到sftp
            uploadToSftp(cluster, xmlFiles);
            return confMap;
        } catch (Exception e){
            throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION.getDescription());
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
            }
        }
    }

    public String getHadoopConfigPath(Long clusterId) {
        Cluster cluster = clusterService.getOne(clusterId);
        Map<String, String> sftpConfig = getSFTPConfig(clusterId);
        String path = sftpConfig.get("path");
        if (StringUtils.isBlank(path)) {
            throw new RdosDefineException("sftp组件路径配置不能为空");
        }

        return String.format(SFTP_HADOOP_CONFIG_PATH, path, SftpPath.CONSOLE_HADOOP_CONFIG,  cluster.getClusterName());
    }

    private void uploadToSftp(Cluster cluster, List<File> xmlFiles) {
        Map<String, String> sftpConfig = getSFTPConfig(cluster.getId());
        String path = sftpConfig.get("path");
        if (StringUtils.isBlank(path)) {
            throw new RdosDefineException("sftp组件路径配置不能为空");
        }

        String sftpDir = String.format(SFTP_HADOOP_CONFIG_PATH, path, SftpPath.CONSOLE_HADOOP_CONFIG,  cluster.getClusterName());
        XmlFileUtil.uploadConfig2SFTP(sftpDir, xmlFiles, sftpConfig);
    }

    public String getClusterLocalKerberosDir(Long clusterId) {
        return env.getLocalKerberosDir() + SEPARATE + getSftpClusterKey(clusterId);
    }

    @Forbidden
    public void addComponentWithConfig(Long engineId, String confName, JSONObject config, boolean updateQueue){
        EComponentType type = EComponentType.getByConfName(confName);

        if (Objects.isNull(config)) {
            config = new JSONObject();
        }
        Component component = componentDao.getByEngineIdAndComponentType(engineId, type.getTypeCode());
        if (component == null) {
            component = new Component();
            component.setEngineId(engineId);
            component.setComponentName(type.getName());
            component.setComponentTypeCode(type.getTypeCode());
            component.setComponentConfig(config.toJSONString());

            componentDao.insert(component);
        } else {
            component.setComponentConfig(config.toJSONString());
            componentDao.update(component);
        }

        if (EComponentType.YARN == type) {
            engineService.updateResource(engineId, config, updateQueue);
        }
    }

    /**
     * 更新集群的kerberos配置
     *
     * @param clusterId
     * @param resources
     */
    @Transactional(rollbackFor = Exception.class)
    public void hadoopKerberosConfig(@Param("clusterId") Long clusterId, @Param("resources") List<Resource> resources) {
        String localKerberosConf = getClusterLocalKerberosDir(clusterId);
        if (CollectionUtils.isNotEmpty(resources)) {
            //上传覆盖之前的文件
            deleteOldFile(localKerberosConf);
            Resource resource = resources.get(0);
            //1.解压keytab文件，可能包括krb5.conf文件
            unzipKeytab(localKerberosConf, resource);
            //2.解析获取principal
            String principal = getPrincipal(localKerberosConf);
            //删除原来sftp下的keyTab文件
            deleteOldSftpFile(clusterId);
            //3.上传至sftp
            String remotePath = uploadSftpDir(clusterId, localKerberosConf);
            //4.更新db信息
            updateKerberosConfig(clusterId, resource, remotePath, principal);
        } else {
            throw new RdosDefineException("文件缺失");
        }
    }

    /**
     * 更新hdfs yarn 组件信息
     * 开启 kerberos hdfs 添加dfs.namenode.kerberos.principal.pattern
     * yarn 添加 yarn.resourcemanager.principal.pattern
     * spark thrift 开启kerberos 需要手动添加hive-site里面的配置信息
     */
    private void checkKerberosConfig(Map<String, Object> config,Long clusterId,EComponentType componentType,Cluster cluster) {
        KerberosConfig kerberosConfig = kerberosDao.getByClusterId(clusterId);
        if(Objects.isNull(kerberosConfig)){
            return;
        }
        if(Objects.isNull(config) || Objects.isNull(componentType) || Objects.isNull(cluster)){
            return;
        }
        if(EComponentType.HDFS.equals(componentType)){
            config.put("dfs.namenode.kerberos.principal.pattern", "*");
        }

        if (EComponentType.YARN.equals(componentType)) {
            config.put("yarn.resourcemanager.principal.pattern", "*");
        }

        if (EComponentType.SPARK_THRIFT.equals(componentType)) {
            String localConsolePath = env.getLocalKerberosDir() + File.separator
                    + "CONSOLE_" + cluster.getId();
            LOGGER.info("add  SparkThrift hadoopKerberosConfig path {} ", localConsolePath);
            try {
                File hiveFile = new File(localConsolePath + File.separator + "hive-site.xml");
                if (!hiveFile.exists()) {
                    //本地没有下载sftp路径下的配置
                    if (downloadClusterSftpPath(cluster, localConsolePath)) {
                        return;
                    }
                }
                hiveFile = new File(localConsolePath + File.separator + "hive-site.xml");
                if (hiveFile.exists()) {
                    config.putAll(XmlFileUtil.parseAndRead(Lists.newArrayList(hiveFile)));
                }
            } catch (Exception e) {
                LOGGER.error("add  SparkThrift hadoopKerberosConfig file error {}", localConsolePath, e);
            }
        }
    }

    public boolean downloadClusterSftpPath(Cluster cluster, String localConsolePath) {
        if (Objects.isNull(cluster) || StringUtils.isBlank(localConsolePath)) {
            return false;
        }
        Engine hadoopEngine = getEngineByClusterId(cluster.getId());
        Component sftpComponent = componentDao.getByEngineIdAndComponentType(hadoopEngine.getId(), EComponentType.SFTP.getTypeCode());
        if (Objects.isNull(sftpComponent)) {
            return true;
        }
        Map<String, String> sftpMap = convertToMap(sftpComponent.getComponentConfig());
        if (Objects.isNull(sftpMap.get("path"))) {
            return true;
        }
        String hadoopConfigPath = String.format(SFTP_HADOOP_CONFIG_PATH, sftpMap.get("path"), SftpPath.CONSOLE_HADOOP_CONFIG, cluster.getClusterName());
        SFTPHandler handler = null;
        try {
            handler = SFTPHandler.getInstance(sftpMap);
            handler.downloadDir(hadoopConfigPath, localConsolePath);
        } catch (Exception e) {
            LOGGER.error("downloadSftpPath file error {}", localConsolePath, e);
        } finally {
            if (Objects.nonNull(handler)) {
                handler.close();
            }
        }
        return false;
    }


    /**
     * 删除之前的文件
     * @param localKerberosConf
     */
    private void deleteOldFile(String localKerberosConf) {
        File file = new File(localKerberosConf);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (Objects.nonNull(files)) {
                for (File oldFile : files) {
                    if (oldFile.getName().endsWith(KerberosKey.KEYTAB.getKey()) && oldFile.isFile()) {
                        LOGGER.info("delete hadoopKerberosConfig path {}  file name {} error ", oldFile.getPath(), oldFile.getName());
                        try {
                            oldFile.delete();
                        } catch (Exception e) {
                            LOGGER.error("delete hadoopKerberosConfig path {}  file name {} error ", oldFile.getPath(), oldFile.getName(), e);
                        }
                    }
                }
            }
        }
    }

    private void unzipKeytab(String localKerberosConf, Resource resource) {
        try {
            KerberosConfigVerify.getFilesFromZip(resource.getUploadedFileName(), localKerberosConf);
        } catch (Exception e) {
            KerberosConfigVerify.delFile(new File(localKerberosConf));
            throw e;
        }
    }

    private String getPrincipal(String dir) {
        File file = null;
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (files.length > 0) {
                file = Arrays.stream(files).filter(f -> f.getName().endsWith(".keytab")).findFirst().orElseThrow(() -> new RdosDefineException("压缩包中不包含keytab文件"));
            }
        }
        if (Objects.nonNull(file)) {
            Keytab keytab = null;
            try {
                keytab = Keytab.loadKeytab(file);
            } catch (IOException e) {
                LOGGER.error("Keytab loadKeytab error {}", e);
                throw new RdosDefineException("解析keytab文件失败");
            }
            List<PrincipalName> names = keytab.getPrincipals();
            if (CollectionUtils.isNotEmpty(names)) {
                PrincipalName principalName = names.get(0);
                if (Objects.nonNull(principalName)) {
                    return principalName.getName();
                }
            }
        }
        throw new RdosDefineException("当前keytab文件不包含principal信息");
    }

    public KerberosConfig getKerberosConfig(@Param("clusterId") Long clusterId) {
        KerberosConfig kerberosConfig = kerberosDao.getByClusterId(clusterId);
        return kerberosConfig;
    }

    private void updateKerberosConfig(Long clusterId, Resource resource, String remotePath, String principal) {
        KerberosConfig kerberosConfig = kerberosDao.getByClusterId(clusterId);
        kerberosConfig = Optional.ofNullable(kerberosConfig).orElse(new KerberosConfig());
        //文件以zip结尾 实际上是解压了的 需要移除zip
        if (resource.getFileName().endsWith(ZIP_CONTENT_TYPE)) {
            kerberosConfig.setName(resource.getFileName().replace(String.format(".%s", ZIP_CONTENT_TYPE),""));
        } else {
            kerberosConfig.setName(resource.getFileName());
        }
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setRemotePath(remotePath);
        kerberosConfig.setPrincipal(principal);
        kerberosConfig.setClusterId(clusterId);
        if (kerberosConfig.getId() != null && kerberosConfig.getId() > 0 ) {
            kerberosDao.update(kerberosConfig);
        } else {
            kerberosDao.insert(kerberosConfig);
        }

        //刷新缓存
        List<Engine> engines = engineDao.listByClusterId(clusterId);
        engines.stream().forEach(engine -> updateCache(engine.getId()));
    }

    private File renameFile(String localKerberosConf, Resource resource) {
        File file = null;
        File realNameFile = null;

        try {
            if (Objects.nonNull(resource)) {
                file = new File(resource.getUploadedFileName());
                realNameFile = new File(localKerberosConf + SEPARATE + resource.getFileName());
                FileUtils.copyFile(file, realNameFile);
            }
        }catch (Exception e) {
            LOGGER.error("", e);
            throw new RdosDefineException("重命名文件失败");
        } finally {
            if (Objects.nonNull(file) && file.exists()) {
                file.delete();
            }
        }
        return realNameFile;
    }


    public void deleteOldSftpFile(Long clusterId){
        Map<String, String> confMap = getSFTPConfig(clusterId);
        String path = confMap.get("path");
        if (StringUtils.isBlank(path)) {
            throw new RdosDefineException("SFTP组件的path配置不能为空");
        }
        StringBuilder destDir = new StringBuilder().append(path).append(SEPARATE).append(getSftpClusterKey(clusterId));
        SFTPHandler handler = SFTPHandler.getInstance(confMap);
        try {
            if(handler.isFileExist(destDir.toString())){
                Vector vector = handler.listFile(destDir.toString());
                for (Iterator<ChannelSftp.LsEntry> iterator = vector.iterator(); iterator.hasNext(); ) {
                    ChannelSftp.LsEntry str = iterator.next();
                    String filename = str.getFilename();
                    if (".".equals(filename) || "..".equals(filename)) {
                        continue;
                    }
                    if (StringUtils.isNotBlank(filename) && filename.endsWith(KerberosKey.KEYTAB.getKey())) {
                        LOGGER.info("delete hadoopKerberosConfig sftp path {}  file name {} error ", destDir.toString(), filename);
                        try {
                            handler.deleteDir(destDir.toString() + File.separator + filename);
                        } catch (Exception e) {
                            LOGGER.error("delete hadoopKerberosConfig sftp path {}  file name {} error ", destDir.toString(), filename, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            if (e instanceof RdosDefineException) {
                throw (RdosDefineException) e;
            } else {
                throw new RdosDefineException("文件上传sftp失败");
            }
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    public String uploadSftpDir(Long clusterId, String srcDir) {
        Map<String, String> confMap = getSFTPConfig(clusterId);
        String path = confMap.get("path");
        if (StringUtils.isBlank(path)) {
            throw new RdosDefineException("SFTP组件的path配置不能为空");
        }
        StringBuilder destDir = new StringBuilder().append(path).append(SEPARATE).append(getSftpClusterKey(clusterId));
        SFTPHandler handler = SFTPHandler.getInstance(confMap);
        try {
            KerberosConfigVerify.uploadLockFile(srcDir, destDir.toString(), handler);
            handler.uploadDir(path, srcDir);
            if (!handler.isFileExist(destDir.toString())) {
                throw new RdosDefineException("文件上传sftp失败");
            }
        } catch (Exception e) {
            LOGGER.error("", e);
            if (e instanceof RdosDefineException) {
                throw (RdosDefineException) e;
            } else {
                throw new RdosDefineException("文件上传sftp失败");
            }
        } finally {
            if (handler != null) {
                handler.close();
            }
        }

        return destDir.toString();
    }

    public Map<String, String> getSFTPConfig(Long clusterId) {
        Engine hadoopEngine = getEngineByClusterId(clusterId);
        Component sftpComponent = componentDao.getByEngineIdAndComponentType(hadoopEngine.getId(), EComponentType.SFTP.getTypeCode());
        if (sftpComponent == null) {
            throw new RdosDefineException("需要提前配置SFTP组件");
        }
        return convertToMap(sftpComponent.getComponentConfig());
    }

    private Map<String, String> convertToMap(String str) {
        JSONObject sftpObj = JSONObject.parseObject(str);
        Map<String, String> confMap = new HashMap<>();
        for (String key : sftpObj.keySet()) {
            confMap.put(key, sftpObj.getString(key));
        }
        return confMap;
    }


    private Engine getEngineByClusterId(Long clusterId) {
        Cluster cluster = clusterDao.getOne(clusterId);
        if (cluster == null) {
            throw new RdosDefineException(ErrorCode.DATA_NOT_FIND.getDescription());
        }

        Engine hadoopEngine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (hadoopEngine == null) {
            throw new RdosDefineException("该集群没有配置[HADOOP]引擎");
        }
        return hadoopEngine;
    }

    public IDownload downloadKerberosXML(@Param("componentType") Integer componentType) throws Exception {
        return buildFileDownLoad(componentType);
    }

    private IDownload buildFileDownLoad(Integer componentType) throws Exception {
        Preconditions.checkNotNull(componentType, "componentType 不能为空");

        String fileName = ComponentTempFile.getFileName(componentType);
        Preconditions.checkNotNull(fileName, "该数据源类型不存在kerberos配置文件, componentType:" + componentType);
        TemplateFileDownload download = new TemplateFileDownload(env.getKerberosTemplatepath() + "/" + fileName);
        download.configure();
        return download;
    }

    public JSONObject getHadoopKerberosFile(@Param("clusterId") Long clusterId) {
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (engine != null) {
            Component hdfsComponent = componentDao.getByEngineIdAndComponentType(engine.getId(), EComponentType.HDFS.getTypeCode());
            if(Objects.isNull(hdfsComponent)){
                return new JSONObject();
            }
            String componentConfig = hdfsComponent.getComponentConfig();
            if (StringUtils.isNotEmpty(componentConfig)) {
                JSONObject config = JSONObject.parseObject(componentConfig);
                JSONObject kerberosFile = config.getJSONObject("kerberosFile");
                if (kerberosFile != null) {
                    return kerberosFile;
                }
            }
        }
        return new JSONObject();
    }

    /**
     * 移除kerberos配置
     *
     * @param clusterId
     * @param type
     */
    public void rmKerberosConfig(@Param("clusterId") Long clusterId, @Param("componentType") Integer type) {
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        //默认为hdfs
        type = Optional.ofNullable(type).orElse(EComponentType.HDFS.getTypeCode());
        EComponentType componentType = EComponentType.getByCode(type);
        Component component = componentDao.getByEngineIdAndComponentType(engine.getId(), componentType.getTypeCode());
        String componentConfig = component.getComponentConfig();
        if (StringUtils.isNotEmpty(componentConfig)) {
            JSONObject config = JSONObject.parseObject(componentConfig);
            config.remove(KERBEROS_CONFIG);
            config.remove(OPEN_KERBEROS);
            config.remove(KERBEROS_FILE);

            component.setComponentConfig(config.toString());
            componentDao.update(component);
        }
    }
}
