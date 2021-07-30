package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ComponentDTO;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.DtScriptAgentLabel;
import com.dtstack.engine.api.pojo.lineage.ComponentMultiTestResult;
import com.dtstack.engine.api.vo.*;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.*;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.cache.DictCache;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EngineTypeComponentType;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.cache.RdosSubscribe;
import com.dtstack.engine.master.router.cache.RdosTopic;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.utils.XmlFileUtil;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.util.Base64Util;
import com.dtstack.schedule.common.util.Xml2JsonUtil;
import com.dtstack.schedule.common.util.ZipUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import io.swagger.models.auth.In;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.*;

@Service
@DependsOn("rdosSubscribe")
public class ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    public static final String KERBEROS_PATH = "kerberos";

    private static final String HADOOP3_SIGNAL = "Hadoop3";

    private static final String GPU_EXEC_SIGNAL = "yarn.nodemanager.resource-plugins.gpu.path-to-discovery-executables";

    private static final String GPU_RESOURCE_PLUGINS_SIGNAL = "yarn.nodemanager.resource-plugins";

    private static final String GPU_ALLOWED_SIGNAL = "yarn.nodemanager.resource-plugins.gpu.allowed-gpu-devices";
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

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private RdosSubscribe rdosSubscribe;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ConsoleService consoleService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SftpFileManage sftpFileManageBean;

    @Autowired
    private DataSourceService dataSourceService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Autowired
    private DictCache dictCache;

    @Autowired
    private ComponentUserDao componentUserDao;

    public static final String VERSION = "version";

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = new HashMap<>(2);

    private static ThreadPoolExecutor connectPool =  new ThreadPoolExecutor(5, 10,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(20),
            new CustomThreadFactory("connectPool"));

    static {
        //hdfs core 需要合并
        componentTypeConfigMapping.put(EComponentType.HDFS.getTypeCode(), Lists.newArrayList("hdfs-site.xml", "core-site.xml","hive-site.xml"));
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList("yarn-site.xml","core-site.xml"));
    }

    @PostConstruct
    public void init() {
        rdosSubscribe.setCallBack((pair) -> {
            if (RdosTopic.CONSOLE.equalsIgnoreCase(pair.getKey())) {
                clearComponentCache();
                clusterService.clearStandaloneCache();
            }
        });
    }

    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId, Integer engineType,Map<Integer,String > componentVersionMap) {

        List<ComponentsConfigOfComponentsVO> componentsVOS = Lists.newArrayList();
        EngineTenant targetEngine = engineTenantDao.getByTenantIdAndEngineType(dtUicTenantId, engineType);
        if (targetEngine == null) {
            return componentsVOS;
        }
        Engine engine  = engineDao.getOne(targetEngine.getEngineId());
        // 目前只取租户下集群组件默认版本，如果需要取出特定版本，需要从componentVersionMap中取出指定版本
        List<Component> componentList = componentDao.listDefaultByEngineIds(Lists.newArrayList(targetEngine.getEngineId()));
        for (Component component : componentList) {
            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(component.getComponentTypeCode());
            String componentConfig = getComponentByClusterId(engine.getClusterId(), component.getComponentTypeCode(), false, String.class,componentVersionMap);

            componentsConfigOfComponentsVO.setComponentConfig(componentConfig);
            componentsVOS.add(componentsConfigOfComponentsVO);
        }
        return componentsVOS;
    }

    public Component getOne(Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException("Component does not exist");
        }
        return component;
    }

    public String getSftpClusterKey(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return AppType.CONSOLE.name() + "_" + one.getClusterName();
    }


    /**
     * 更新缓存
     */
    public void updateCache(Long clusterId,Long engineId, Integer componentCode) {
        clearComponentCache();
        clusterService.clearStandaloneCache();
        Set<Long> dtUicTenantIds = new HashSet<>();
        if ( null != componentCode && EComponentType.sqlComponent.contains(EComponentType.getByCode(componentCode))) {
            List<EngineTenantVO> tenantVOS = engineTenantDao.listEngineTenant(engineId);
            if (CollectionUtils.isNotEmpty(tenantVOS)) {
                for (EngineTenantVO tenantVO : tenantVOS) {
                    if (null != tenantVO && null != tenantVO.getTenantId()) {
                        dtUicTenantIds.add(tenantVO.getTenantId());
                    }
                }
            }
        } else {
            List<Queue> refreshQueues = queueDao.listByEngineId(engineId);
            if (CollectionUtils.isEmpty(refreshQueues)) {
                return;
            }
            List<Long> queueIds = refreshQueues.stream().map(BaseEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(queueIds)) {
                return;
            }
            List<Long> tenantIds = engineTenantDao.listTenantIdByQueueIds(queueIds);
            dtUicTenantIds = new HashSet<>(tenantDao.listDtUicTenantIdByIds(tenantIds));
        }
        dataSourceService.publishSqlComponent(clusterId,engineId,componentCode,dtUicTenantIds);
        //缓存刷新
        if (!dtUicTenantIds.isEmpty()) {
            for (Long uicTenantId : dtUicTenantIds) {
                consoleCache.publishRemoveMessage(uicTenantId.toString());
            }
        }
    }

    public List<Component> listComponent(List<Long> engineIds) {
        return componentDao.listByEngineIds(engineIds,null);
    }

    private Map<String, Map<String,Object>> parseUploadFileToMap(List<Resource> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("The uploaded file cannot be empty");
        }

        Resource resource = resources.get(0);
        if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("The compressed package format only supports ZIP format");
        }

        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            Map<String, Map<String,Object>> confMap = new HashMap<>();
            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            if(CollectionUtils.isEmpty(xmlFiles)){
                throw new RdosDefineException("The configuration file cannot be empty");
            }
            for (File file : xmlFiles) {
                Map<String, Object> fileMap = null;
                if (file.getName().startsWith(".")) {
                    //.开头过滤
                    continue;
                }
                if (file.getName().endsWith("xml")) {
                    //xml文件
                    fileMap = Xml2JsonUtil.xml2map(file);
                } else if(file.getName().endsWith("json")){
                    //json文件
                    String jsonStr = Xml2JsonUtil.readFile(file);
                    if (StringUtils.isBlank(jsonStr)) {
                        continue;
                    }
                    fileMap = (Map<String, Object>) JSONObject.parseObject(jsonStr,Map.class);
                }
                if (null != fileMap) {
                    confMap.put(file.getName(), fileMap);
                }
            }
            return confMap;
        } catch (Exception e) {
            LOGGER.error("parseAndUploadXmlFile file error ", e);
            throw new RdosDefineException(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
            }
        }
    }

    public String getClusterLocalKerberosDir(Long clusterId) {
        return env.getLocalKerberosDir() + File.separator + getSftpClusterKey(clusterId);
    }

    private File getFileWithSuffix(String dir, String suffix) {
        if (StringUtils.isBlank(suffix)) {
            throw new RdosDefineException("File suffix cannot be empty");
        }
        File file = null;
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (files.length > 0) {
                file = Arrays.stream(files).filter(f -> f.getName().endsWith(suffix)).findFirst().orElse(null);
            }
        }
        return file;
    }


    private List<PrincipalName> getPrincipal(File file) {
        if (null != file) {
            Keytab keytab = null;
            try {
                keytab = Keytab.loadKeytab(file);
            } catch (IOException e) {
                LOGGER.error("Keytab loadKeytab error ", e);
                throw new RdosDefineException("Failed to parse keytab file");
            }
            return keytab.getPrincipals();
        }
        throw new RdosDefineException("The current keytab file does not contain principal information");
    }

    private void unzipKeytab(String localKerberosConf, Resource resource) {
        try {
            ZipUtil.upzipFile(resource.getUploadedFileName(), localKerberosConf);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(new File(localKerberosConf));
            } catch (IOException ioException) {
                LOGGER.error("delete zip directory {} error ", localKerberosConf);
            }
        }
    }


    public KerberosConfig getKerberosConfig( Long clusterId,  Integer componentType,String componentVersion) {
        return kerberosDao.getByComponentType(clusterId, componentType,ComponentVersionUtil.formatMultiVersion(componentType,componentVersion));
    }


    @Transactional(rollbackFor = Exception.class)
    public String uploadKerberos(List<Resource> resources, Long clusterId, Integer componentCode,String componentVersion) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("Please upload a kerberos file!");
        }

        Resource resource = resources.get(0);
        String kerberosFileName = resource.getFileName();
        if (!kerberosFileName.endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("Kerberos upload files are not in zip format");
        }
        String sftpComponent = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class,null);
        SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, componentCode);
        Component addComponent = new ComponentDTO();
        addComponent.setComponentTypeCode(componentCode);
        addComponent.setHadoopVersion(ComponentVersionUtil.getComponentVersion(componentVersion));
        updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, null, null);

        List<KerberosConfig> kerberosConfigs = kerberosDao.listAll();
        return mergeKrb5(kerberosConfigs);
    }

    private synchronized String mergeKrb5(List<KerberosConfig> kerberosConfigs) {
        String mergeKrb5Content = "";
        if (CollectionUtils.isEmpty(kerberosConfigs)) {
            LOGGER.error("KerberosConfigs is null");
            return mergeKrb5Content;
        }

        String mergeDirPath = ConfigConstant.LOCAL_KRB5_MERGE_DIR_PARENT + ConfigConstant.SP + UUID.randomUUID();
        List<Long> clusterDownloadRecords = new ArrayList();
        try {
            String oldMergeKrb5Content = null;
            String mergeKrb5Path = mergeDirPath + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                String krb5Name = kerberosConfig.getKrbName();
                String remotePath = kerberosConfig.getRemotePath();
                Long clusterId = kerberosConfig.getClusterId();
                Integer componentCode = kerberosConfig.getComponentType();

                if (StringUtils.isNotEmpty(kerberosConfig.getMergeKrbContent()) && StringUtils.isEmpty(oldMergeKrb5Content)) {
                    oldMergeKrb5Content = kerberosConfig.getMergeKrbContent();
                }

                String remoteKrb5Path = remotePath + ConfigConstant.SP + krb5Name;
                String localKrb5Path = mergeDirPath + remoteKrb5Path;
                try {
                    String sftpComponent = getComponentByClusterId(clusterId,EComponentType.SFTP.getTypeCode(),false,String.class,null);
                    SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
                    SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
                    if (clusterDownloadRecords.contains(clusterId)) {
                        continue;
                    }
                    boolean downRes = sftpFileManage.downloadFile(remoteKrb5Path, localKrb5Path);
                    LOGGER.info("download remoteKrb5Path[{}] result {}", remoteKrb5Path, downRes);
                    if (downRes) {
                        clusterDownloadRecords.add(clusterId);
                        if (!new File(mergeKrb5Path).exists()) {
                            FileUtils.copyFile(new File(localKrb5Path), new File(mergeKrb5Path));
                            mergeKrb5Content = Krb5FileUtil.convertMapToString(Krb5FileUtil.readKrb5ByPath(mergeKrb5Path));
                            continue;
                        }
                        mergeKrb5Content = Krb5FileUtil.mergeKrb5ContentByPath(mergeKrb5Path, localKrb5Path);
                    }
                } catch (Exception e) {
                    LOGGER.error("merge krb5.conf[{}] error : {}", localKrb5Path, e.getMessage());
                }
            }
            if (StringUtils.isNotEmpty(oldMergeKrb5Content)) {
                mergeKrb5Content = Krb5FileUtil.resetMergeKrb5Content(oldMergeKrb5Content, mergeKrb5Content);
            }
            LOGGER.info("mergeKrb5Content is {}", mergeKrb5Content);
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                kerberosConfig.setMergeKrbContent(mergeKrb5Content);
                kerberosDao.update(kerberosConfig);
                LOGGER.info("Krb5[{}/krb5.conf] merge successed!", kerberosConfig.getRemotePath());
            }
        } catch (Exception e) {
            LOGGER.error("Merge krb5 error! {}", e.getMessage());
        } finally {
            try {
                File mergeDir = new File(mergeDirPath);
                FileUtils.deleteDirectory(mergeDir);
            } catch (Exception e) {
            }
        }
        return mergeKrb5Content;
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKrb5Conf(String krb5Content) {
        try {
            Krb5FileUtil.checkKrb5Content(krb5Content);
            List<KerberosConfig> kerberosConfigs = kerberosDao.listAll();
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                String remotePath = kerberosConfig.getRemotePath();
                kerberosConfig.setMergeKrbContent(krb5Content);
                kerberosDao.update(kerberosConfig);
                LOGGER.info("Update krb5 remotePath {}", remotePath);
            }
        } catch (Exception e) {
            LOGGER.error("Update krb5 error! {}", e.getMessage());
            throw new RdosDefineException(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public ComponentVO addOrUpdateComponent(Long clusterId,  String componentConfig,
                                             List<Resource> resources,  String hadoopVersion,
                                             String kerberosFileName,  String componentTemplate,
                                             Integer componentCode, Integer storeType,
                                             String principals, String principal,boolean isMetadata,Boolean isDefault,Integer deployType) {
        if (StringUtils.isBlank(componentConfig)) {
            componentConfig = new JSONObject().toJSONString();
        }
        if (null == componentCode) {
            throw new RdosDefineException("Component type cannot be empty");
        }
        if (null == clusterId) {
            throw new RdosDefineException("Cluster Id cannot be empty");
        }
        if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
            //上传二份文件 需要kerberosFileName文件名字段
            throw new RdosDefineException("kerberosFileName不能为空");
        }
        // 不涉及DB操作校验首先进行
        this.checkSchedulesComponent(clusterId, componentCode);

        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentTypeCode(componentCode);
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        String clusterName = cluster.getClusterName();
        //校验引擎是否添加
        EComponentType componentType = EComponentType.getByCode(componentDTO.getComponentTypeCode());
        if(EComponentType.deployTypeComponents.contains(componentType) && null == deployType){
            throw new RdosDefineException("deploy type cannot be empty");
        }
        MultiEngineType engineType = EComponentType.getEngineTypeByComponent(componentType,deployType);
        // 检验组件的此版本是否已经添加, 只校验了 yarn 和 k8s 此组件没有版本
        Engine engine = this.addEngineWithCheck(clusterId, engineType,null);
        if (null == engine) {
            throw new RdosDefineException("Engine cannot be empty");
        }
        Component addComponent = new ComponentDTO();
        BeanUtils.copyProperties(componentDTO, addComponent);
        // 判断是否是更新组件, 需要校验组件版本
        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(),ComponentVersionUtil.isMultiVersionComponent(componentCode)?hadoopVersion:null,deployType);
        String dbHadoopVersion = "";
        boolean isUpdate = false;
        boolean isOpenKerberos = isOpenKerberos(kerberosFileName, dbComponent);
        if (null != dbComponent) {
            //更新
            dbHadoopVersion = dbComponent.getHadoopVersion();
            addComponent = dbComponent;
            isUpdate = true;
        }
        componentConfig = this.checkKubernetesConfig(componentConfig, resources, componentType);

        EComponentType storesComponent = this.checkStoresComponent(clusterId, storeType);
        addComponent.setStoreType(storesComponent.getTypeCode());
        addComponent.setHadoopVersion(convertHadoopVersionToValue(hadoopVersion,componentCode,clusterId));
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setEngineId(engine.getId());
        addComponent.setDeployType(deployType);

        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }

        changeDefault(BooleanUtils.isTrue(isDefault),engine.getId(),componentType,addComponent);

        String md5Key = updateResource(clusterId, componentConfig, resources, kerberosFileName, componentCode, principals, principal, addComponent, dbComponent);
        addComponent.setClusterId(clusterId);
        if (isUpdate) {
            componentDao.update(addComponent);
            refreshVersion(componentType, engine.getId(), addComponent, dbHadoopVersion,hadoopVersion);
            clusterDao.updateGmtModified(clusterId);
        } else {
            componentDao.insert(addComponent);
        }

        changeMetadata(componentType.getTypeCode(),isMetadata,engine.getId(),addComponent.getIsMetadata());
        List<ClientTemplate> clientTemplates = this.wrapperConfig(componentType, componentConfig, isOpenKerberos, clusterName, hadoopVersion, md5Key, componentTemplate,addComponent.getHadoopVersion(),addComponent.getStoreType(),deployType);
        componentConfigService.addOrUpdateComponentConfig(clientTemplates, addComponent.getId(), addComponent.getClusterId(), componentCode);
        // 此时不需要查询默认版本
        List<IComponentVO> componentVos = componentConfigService.getComponentVoByComponent(Lists.newArrayList(addComponent), true, clusterId,true,false);
        this.updateCache(clusterId,engine.getId(), componentType.getTypeCode());
        if (CollectionUtils.isNotEmpty(componentVos)) {
            ComponentVO componentVO = (ComponentVO) componentVos.get(0);
            componentVO.setClusterName(clusterName);
            componentVO.setPrincipal(principal);
            componentVO.setPrincipals(principals);
            componentVO.setDeployType(deployType);
            componentVO.setIsMetadata(BooleanUtils.toInteger(isMetadata));
            return componentVO;
        }
        return null;
    }

    /**
     *
     * @param isDefault
     * @param engineId
     * @param componentType
     */
    private int changeDefault(boolean isDefault, Long engineId, EComponentType componentType,Component updateComponent) {
        if(!EComponentType.multiVersionComponents.contains(componentType)) {
            updateComponent.setIsDefault(true);
            return -1;
        }
        updateComponent.setIsDefault(isDefault);
        if(!isDefault){
            List<Component> dbComponents = componentDao.listByEngineIds(Lists.newArrayList(engineId), componentType.getTypeCode());
            Set<Long> dbComponentId = dbComponents.stream().map(Component::getId).collect(Collectors.toSet());
            dbComponentId.remove(updateComponent.getId());
            if(dbComponentId.size() == 0){
                // single component must be default
                updateComponent.setIsDefault(true);
            }
        }
        return componentDao.updateDefault(engineId,componentType.getTypeCode(),!isDefault);
    }

    /**
     * yarn组件版本变更之后  hdfs组件保存一致
     * 计算组件 如flink的typename也同步变更
     *
     * @param componentType
     * @param engineId
     * @param addComponent
     * @param dbHadoopVersion
     */
    public void refreshVersion(EComponentType componentType, Long engineId, Component addComponent, String dbHadoopVersion, String hadoopVersion) {
        if (!EComponentType.YARN.equals(componentType)) {
            return;
        }
        Component hdfsComponent = componentDao.getByEngineIdAndComponentType(engineId, EComponentType.HDFS.getTypeCode());
        if (null == hdfsComponent) {
            return;
        }
        String oldVersion = formatHadoopVersion(dbHadoopVersion, componentType);
        String newVersion = formatHadoopVersion(addComponent.getHadoopVersion(), componentType);
        String hdfsVersion = formatHadoopVersion(hdfsComponent.getHadoopVersion(), EComponentType.HDFS);
        if (newVersion.equalsIgnoreCase(oldVersion) && newVersion.equalsIgnoreCase(hdfsVersion)) {
            return;
        }
        //1. 同步hdfs组件版本
        hdfsComponent.setHadoopVersion(addComponent.getHadoopVersion());
        componentDao.update(hdfsComponent);
        ComponentConfig hadoopVersionConfig = componentConfigService.getComponentConfigByKey(hdfsComponent.getId(), HADOOP_VERSION);
        if (null != hadoopVersionConfig) {
            hadoopVersionConfig.setValue(hadoopVersion);
            componentConfigService.updateValueComponentConfig(hadoopVersionConfig);
        }

        //2. 版本切换 影响计算组件typeName
        List<Component> components = componentDao.listByEngineIds(Lists.newArrayList(engineId),null);
        if (CollectionUtils.isEmpty(components)) {
            return;
        }
        String newTypeNamePrefix = String.format("%s-%s-", EComponentType.YARN.name().toLowerCase() + newVersion, EComponentType.HDFS.name().toLowerCase() + newVersion);
        String oldTypeNamePrefix = String.format("%s-%s-", EComponentType.YARN.name().toLowerCase() + oldVersion, EComponentType.HDFS.name().toLowerCase() + oldVersion);
        for (Component component : components) {
            if (EComponentType.typeComponentVersion.contains(EComponentType.getByCode(component.getComponentTypeCode()))) {
                ComponentConfig typeNameComponentConfig = componentConfigService.getComponentConfigByKey(component.getId(), TYPE_NAME_KEY);
                if (null != typeNameComponentConfig) {
                    String newValue;
                    String oldValue = typeNameComponentConfig.getValue();
                    if (EComponentType.HDFS.getTypeCode().equals(component.getComponentTypeCode())) {
                        newValue = EComponentType.HDFS.name().toLowerCase() + newVersion;
                    } else {
                        newValue = oldValue.replace(oldTypeNamePrefix, newTypeNamePrefix);
                    }
                    typeNameComponentConfig.setValue(newValue);
                    LOGGER.info("refresh engineId {} component {} typeName {} to {}", engineId, component.getComponentName(), oldValue, newValue);
                    componentConfigService.updateValueComponentConfig(typeNameComponentConfig);
                }
            }
        }
    }

    /**
     * 处理hdfs 和yarn的自定义参数
     *
     * @param componentType
     * @param componentTemplate
     * @return
     */
    private List<ClientTemplate> dealXmlCustomControl(EComponentType componentType, String componentTemplate) {
        List<ClientTemplate> extraClient = new ArrayList<>(0);
        if (StringUtils.isBlank(componentTemplate)) {
            return extraClient;
        }
        if (EComponentType.HDFS.getTypeCode().equals(componentType.getTypeCode()) || EComponentType.YARN.getTypeCode().equals(componentType.getTypeCode())) {
            JSONArray keyValues = JSONObject.parseArray(componentTemplate);
            for (int i = 0; i < keyValues.size(); i++) {
                ClientTemplate clientTemplate = ComponentConfigUtils.buildCustom(
                        keyValues.getJSONObject(i).getString("key"),
                        keyValues.getJSONObject(i).getString("value"),
                        EFrontType.CUSTOM_CONTROL.name());
                extraClient.add(clientTemplate);
            }
        }
        return extraClient;
    }

    /**
     * 将选择的hadoop版本 转换为对应的值
     *
     * @param hadoopVersion
     * @return
     */
    private String convertHadoopVersionToValue(String hadoopVersion, Integer componentTypeCode, Long clusterId) {
        if (EComponentType.HDFS.getTypeCode().equals(componentTypeCode)) {
            //hdfs的组件和yarn组件的版本保持强一致 如果是k8s-hdfs2-则不作限制
            Component yarnComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode(),null,null);
            if (null != yarnComponent) {
                return yarnComponent.getHadoopVersion();
            }
        }

        ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.HADOOP_VERSION.type, Optional.ofNullable(hadoopVersion).orElse("Hadoop 2.x"), null, null);
        if (null != dict) {
            return dict.getDictValue();
        }
        return hadoopVersion;
    }

    private String updateResource(Long clusterId, String componentConfig, List<Resource> resources, String kerberosFileName, Integer componentCode, String principals, String principal, Component addComponent, Component dbComponent) {
        //上传资源依赖sftp组件
        String md5Key = "";
        if (CollectionUtils.isNotEmpty(resources)) {
            String sftpConfigStr = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class,null);
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpConfigStr, componentCode, componentConfig);
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent, principals, principal);
        } else if (CollectionUtils.isEmpty(resources) && StringUtils.isNotBlank(principal)) {
            //直接更新认证信息
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode(),ComponentVersionUtil.isMultiVersionComponent(addComponent.getComponentTypeCode())?StringUtils.isNotBlank(addComponent.getHadoopVersion())?addComponent.getHadoopVersion():componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId,componentCode):null);
            if (null != kerberosConfig) {
                kerberosConfig.setPrincipal(principal);
                kerberosConfig.setPrincipals(principals);
                kerberosDao.update(kerberosConfig);
            }
        }
        return md5Key;
    }

    private String checkKubernetesConfig(String componentConfig, List<Resource> resources, EComponentType componentType) {
        if(EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode()) && CollectionUtils.isNotEmpty(resources)){
            //kubernetes 信息需要自己解析文件
            List<Object> config = this.config(resources, EComponentType.KUBERNETES.getTypeCode(),false,null);
            if(CollectionUtils.isNotEmpty(config)){
                componentConfig = (String)config.get(0);
            }
        }
        return componentConfig;
    }

    private boolean isOpenKerberos(String kerberosFileName, Component dbComponent) {
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (!isOpenKerberos) {
            if (null != dbComponent) {
                KerberosConfig componentKerberos = kerberosDao.getByComponentType(dbComponent.getClusterId(), dbComponent.getComponentTypeCode(),ComponentVersionUtil.formatMultiVersion(dbComponent.getComponentTypeCode(),dbComponent.getHadoopVersion()));
                if (componentKerberos != null) {
                    isOpenKerberos = true;
                }
            }
        }
        return isOpenKerberos;
    }

    private EComponentType checkStoresComponent(Long clusterId, Integer storeType) {
        //默认为hdfs
        if(null == storeType){
            return EComponentType.HDFS;
        }
        EComponentType componentType = EComponentType.getByCode(MathUtil.getIntegerVal(storeType));
        Component storeComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(),null,null);
        if(null == storeComponent){
            throw new RdosDefineException(String.format("Please configure the corresponding %s component first",componentType.getName()));
        }
        return componentType;
    }

    private void checkSchedulesComponent(Long clusterId, Integer componentCode) {
        //yarn 和 Kubernetes 只能2选一
        if (EComponentType.YARN.getTypeCode().equals(componentCode) || EComponentType.KUBERNETES.getTypeCode().equals(componentCode)) {
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId,
                    EComponentType.YARN.getTypeCode().equals(componentCode) ? EComponentType.KUBERNETES.getTypeCode() : EComponentType.YARN.getTypeCode(),null,null);
            if (Objects.nonNull(resourceComponent)) {
                throw new RdosDefineException("The scheduling component can only select a single item");
            }
        }
    }

    private Engine addEngineWithCheck(Long clusterId, MultiEngineType engineType,Map<Integer,String> componentVersionMap) {
        if (null == engineType) {
            //如果是hdfs 组件 需要先确定调度组件为 yarn 还是k8s
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode(),ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.YARN),null);
            if (null == resourceComponent) {
                resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.KUBERNETES.getTypeCode(),ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.KUBERNETES),null);
            }
            if (null == resourceComponent) {
                throw new RdosDefineException("Please configure the scheduling component first");
            } else {
                return engineDao.getOne(resourceComponent.getEngineId());
            }
        }

        //如果是可以确定引擎类型的 需要确定引擎是否添加
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType.getType());
        if (null == engine) {
            //创建引擎
            engine = new Engine();
            engine.setClusterId(clusterId);
            engine.setEngineType(engineType.getType());
            engine.setEngineName(engineType.getName());
            engineDao.insert(engine);
            //同步租户引擎对应关系
            engineService.updateEngineTenant(clusterId,engine.getId());
            LOGGER.info("cluster {} add engine  {} ", clusterId, engine.getId());
        }
        return engine;
    }

    public SftpConfig getSFTPConfig(String sftpConfigStr, Integer componentCode, String componentTemplate) {
        if (StringUtils.isBlank(sftpConfigStr)) {
            //  判断componentCode 是否是sftp的配置，如果是上传文件，如果不是 抛异常返回提交配置sftp服务器
            if (EComponentType.SFTP.getTypeCode().equals(componentCode)) {
                // 是sftp的配置
                try {
                    Map<String, Object> configMap = ComponentConfigUtils.convertClientTemplateToMap(JSONArray.parseArray(componentTemplate, ClientTemplate.class));
                    return PublicUtil.mapToObject(configMap, SftpConfig.class);
                } catch (IOException e) {
                    throw new RdosDefineException("sftp配置信息不正确");
                }
            } else {
                throw new RdosDefineException("Please configure the sftp server to upload files!");
            }
        } else {
            return JSONObject.parseObject(sftpConfigStr, SftpConfig.class);
        }
    }

    private String uploadResourceToSftp(Long clusterId,  List<Resource> resources,  String kerberosFileName,
                                        SftpConfig sftpConfig, Component addComponent, Component dbComponent,String principals,String principal) {
        //上传配置文件到sftp 供后续下载
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
        String md5sum = "";
        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, addComponent.getComponentTypeCode());
        for (Resource resource : resources) {
            if (!resource.getFileName().equalsIgnoreCase(kerberosFileName) || StringUtils.isBlank(kerberosFileName)) {
                addComponent.setUploadFileName(resource.getFileName());
            }
            try {
                if (resource.getFileName().equalsIgnoreCase(kerberosFileName)) {
                    // 更新Kerberos信息
                    this.updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, principals, principal);
                } else {
                    LOGGER.info("start upload hadoop config file:{}",kerberosFileName);
                    this.updateComponentConfigFile(dbComponent, sftpFileManage, remoteDir, resource);
                    if(EComponentType.HDFS.getTypeCode().equals(addComponent.getComponentTypeCode())){
                        String xmlZipLocation = resource.getUploadedFileName();
                        md5sum = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource,null,addComponent.getComponentTypeCode());
                    }
                    if(EComponentType.YARN.getTypeCode().equals(addComponent.getComponentTypeCode())){
                        List<ComponentConfig> clientTemplates = scheduleDictService
                                .loadExtraComponentConfig(addComponent.getHadoopVersion(), addComponent.getComponentTypeCode());
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource,clientTemplates,addComponent.getComponentTypeCode());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("update component resource {}  error", resource.getUploadedFileName(), e);
                if (e instanceof RdosDefineException) {
                    throw (RdosDefineException) e;
                } else {
                    throw new RdosDefineException("Failed to update component");
                }
            } finally {
                try {
                    FileUtils.forceDelete(new File(resource.getUploadedFileName()));
                } catch (IOException e) {
                    LOGGER.error("delete upload file {} error", resource.getUploadedFileName(), e);
                }
            }
        }
        return md5sum;
    }

    /**
     * 上传四个xml到sftp 作为spark 作为confHdfsPath
     *
     * @param clusterId
     * @param resource
     */
    private void updateConfigToSftpPath( Long clusterId, SftpConfig sftpConfig, SftpFileManage sftpFileManage, Resource resource,
                                         List<ComponentConfig> templates, Integer componentType) {
        //上传xml到对应路径下 拼接confHdfsPath
        String confRemotePath = sftpConfig.getPath() + File.separator;
        String buildPath = File.separator + buildConfRemoteDir(clusterId);
        String confPath = System.getProperty("user.dir") + buildPath;
        File localFile = new File(confPath);
        try {
            //删除本地目录
            FileUtils.forceDelete(localFile);
        } catch (IOException e) {
            LOGGER.info("delete  local path  {} error ", localFile, e);
        }
        //解压到本地
        this.unzipKeytab(confPath, resource);
        if (localFile.isDirectory()) {
            File xmlFile = this.getFileWithSuffix(localFile.getPath(), ".xml");
            File dirFiles = null;
            if (null == xmlFile) {
                //包含文件夹目录
                File[] files = localFile.listFiles();
                if (null != files && files.length > 0 && files[0].isDirectory()) {
                    dirFiles = files[0];
                }
            } else {
                //直接是文件
                dirFiles = xmlFile.getParentFile();
            }
            if (null != dirFiles) {
                File[] files = dirFiles.listFiles();
                if (null == files) {
                    return;
                }
                for (File file : files) {
                    if (file.getName().contains(".xml")) {
                        beforeUploadAddExtraConfig(file, templates, componentType);
                        sftpFileManage.uploadFile(confRemotePath + buildPath, file.getPath());
                    }
                }
            }
        }

    }

    /**
     * 在上传到sftp的文件中判断是否需要添加自定义的配置参数
     *
     * @param file
     * @param configs
     * @param componentType
     */
    private void beforeUploadAddExtraConfig(File file, List<ComponentConfig> configs, Integer componentType) {
        if (CollectionUtils.isEmpty(configs) || null == file || null == componentType) {
            return;
        }
        EComponentType eComponentType = EComponentType.getByCode(componentType);
        List<String> fileNames = componentTypeConfigMapping.get(eComponentType);
        if (CollectionUtils.isEmpty(fileNames)) {
            return;
        }
        if (file.getName().contains(fileNames.get(0))) {
            Map<String, Object> configMap = ComponentConfigUtils.convertComponentConfigToMap(configs);
            try {
                Xml2JsonUtil.addInfoIntoXml(file, configMap, false);
            } catch (Exception e) {
                LOGGER.info("file path {} add extra config {} info error ", file.getPath(), configMap, e);
            }
        }
    }

    public String buildConfRemoteDir(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return "confPath" + File.separator + AppType.CONSOLE + "_" + one.getClusterName();
    }

    /**
     * 如果开启Kerberos 则添加一个必加配置项
     * 开启 kerberos hdfs 添加dfs.namenode.kerberos.principal.pattern
     * yarn 添加 yarn.resourcemanager.principal.pattern
     * 必要组件添加typename字段
     *
     * @param componentType
     * @param componentString
     * @return
     */
    private List<ClientTemplate> wrapperConfig(EComponentType componentType, String componentString, boolean isOpenKerberos, String clusterName, String hadoopVersion, String md5Key, String clientTemplates,String convertHadoopVersion,Integer storeType,Integer deployType) {
        List<ClientTemplate> templates = new ArrayList<>();
        if (EComponentType.KUBERNETES.equals(componentType)) {
            ClientTemplate kubernetesClientTemplate = ComponentConfigUtils.buildOthers("kubernetes.context", componentString);
            templates.add(kubernetesClientTemplate);
            return templates;
        }
        JSONObject componentConfigJSON = JSONObject.parseObject(componentString);
        if (isOpenKerberos) {
            if (EComponentType.HDFS.equals(componentType)) {
                componentConfigJSON.put("dfs.namenode.kerberos.principal.pattern", "*");
            }

            if (EComponentType.YARN.equals(componentType)) {
                componentConfigJSON.put("yarn.resourcemanager.principal.pattern", "*");
            }
        }
        if (EComponentType.typeComponentVersion.contains(componentType)) {
            //添加typeName
            ClientTemplate typeNameClientTemplate = ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, this.convertComponentTypeToClient(clusterName, componentType.getTypeCode(), convertHadoopVersion,storeType,null,deployType));
            templates.add(typeNameClientTemplate);
        }
        if (!StringUtils.isBlank(md5Key)) {
            ClientTemplate md5ClientTemplate = ComponentConfigUtils.buildOthers(MD5_SUM_KEY, md5Key);
            templates.add(md5ClientTemplate);
        }
        if (EComponentType.noControlComponents.contains(componentType)) {
            //xml配置文件也转换为组件
            List<ClientTemplate> xmlTemplates = ComponentConfigUtils.convertXMLConfigToComponentConfig(componentConfigJSON.toJSONString());
            //yarn 和hdfs 需要存入原来的hadoopVersion 如 CDH 5.1.x
            templates.add(ComponentConfigUtils.buildOthers(HADOOP_VERSION,hadoopVersion));
            templates.addAll(xmlTemplates);
            //yarn 和hdfs的自定义参数
            templates.addAll(dealXmlCustomControl(componentType,clientTemplates));
        } else {
            List<ClientTemplate> controlTemplate = JSONObject.parseArray(clientTemplates, ClientTemplate.class);
            templates.addAll(controlTemplate);
        }
        return templates;
    }

    /**
     * 上传配置文件到sftp
     *
     * @param dbComponent
     * @param remoteDir
     * @param resource
     */
    private void updateComponentConfigFile(Component dbComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource) {
        //原来配置
        String deletePath = remoteDir + File.separator;
        LOGGER.info("upload config file to sftp:{}",deletePath);
        if (Objects.nonNull(dbComponent)) {
            deletePath = deletePath + dbComponent.getUploadFileName();
            //删除原来的文件配置zip 如果dbComponent不为null ,删除文件。
            LOGGER.info("delete file :{}",deletePath);
            sftpFileManage.deleteFile(deletePath);
        }

        //更新为原名
        sftpFileManage.uploadFile(remoteDir, resource.getUploadedFileName());
        sftpFileManage.renamePath(remoteDir + File.separator + resource.getUploadedFileName().substring(resource.getUploadedFileName().lastIndexOf(File.separator) + 1), remoteDir + File.separator + resource.getFileName());
    }


    /**
     * 解压kerberos文件到本地 并上传至sftp
     * * @param clusterId
     *
     * @param addComponent
     * @param remoteDir
     * @param resource
     * @return
     */
    private String updateComponentKerberosFile(Long clusterId, Component addComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource,
                                               String principals, String principal) {

        File keyTabFile = null;
        File krb5ConfFile = null;
        String remoteDirKerberos = remoteDir + File.separator + KERBEROS_PATH;
        if (resource != null) {
            // kerberos认证文件 远程删除 kerberos下的文件
            LOGGER.info("updateComponentKerberosFile remote path:{}",remoteDirKerberos);
            //删除本地文件夹
            String kerberosPath = this.getLocalKerberosPath(clusterId, addComponent.getComponentTypeCode());
            try {
                FileUtils.deleteDirectory(new File(kerberosPath));
            } catch (IOException e) {
                LOGGER.error("delete old kerberos directory {} error", kerberosPath, e);
            }
            //解压到本地
            List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), kerberosPath);
            if (CollectionUtils.isEmpty(files)) {
                throw new RdosDefineException("Hadoop-Kerberos file decompression error");
            }

            keyTabFile = files.stream().filter(f -> f.getName().endsWith(KEYTAB_SUFFIX)).findFirst().orElse(null);
            krb5ConfFile = files.stream().filter(f -> f.getName().equalsIgnoreCase(KRB5_CONF)).findFirst().orElse(null);
            if (keyTabFile == null) {
                throw new RdosDefineException("There must be a keytab file in the zip file of the uploaded Hadoop-Kerberos file, please add the keytab file");
            }
            LOGGER.info("fileKeyTab Unzip fileName:{}",keyTabFile.getAbsolutePath());
            if (krb5ConfFile == null) {
                throw new RdosDefineException("There must be a krb5.conf file in the zip file of the uploaded Hadoop-Kerberos file, please add the krb5.conf file");
            }
            LOGGER.info("conf Unzip fileName:{}",krb5ConfFile.getAbsolutePath());

            //获取principal
            List<PrincipalName> principalLists = this.getPrincipal(keyTabFile);
            principal = parsePrincipal(principal, principalLists);
            if (StringUtils.isEmpty(principals)) {
                List<String> principalNames = new ArrayList<>();
                for(PrincipalName principalName : principalLists) {
                    principalNames.add(principalName.getName());
                }
                principals = StringUtils.join(principalNames, ",");
            }

            //删除sftp原来kerberos 的文件夹
            sftpFileManage.deleteDir(remoteDirKerberos);
            //上传kerberos解压后的文件
            for (File file : files) {
                LOGGER.info("upload sftp file:{}",file.getAbsolutePath());
                sftpFileManage.uploadFile(remoteDirKerberos, file.getPath());
            }
        }
        String componentVersion = ComponentVersionUtil.getComponentVersion(addComponent.getHadoopVersion());
        //更新数据库kerberos信息
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode(),
                ComponentVersionUtil.formatMultiVersion(addComponent.getComponentTypeCode(),componentVersion));
        boolean isFirstOpenKerberos = false;
        if (Objects.isNull(kerberosConfig)) {
            kerberosConfig = new KerberosConfig();
            kerberosConfig.setComponentVersion(componentVersion);
            isFirstOpenKerberos = true;
        }
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setRemotePath(remoteDirKerberos);
        kerberosConfig.setClusterId(clusterId);
        kerberosConfig.setComponentType(addComponent.getComponentTypeCode());
        if (keyTabFile != null) {
            kerberosConfig.setName(keyTabFile.getName());
        }
        if (krb5ConfFile != null) {
            kerberosConfig.setKrbName(krb5ConfFile.getName());
        }

        if (StringUtils.isNotEmpty(principal)) {
            kerberosConfig.setPrincipal(principal);
        }
        if (StringUtils.isNotEmpty(principals)) {
            kerberosConfig.setPrincipals(principals);
        }

        if (isFirstOpenKerberos) {
            kerberosDao.insert(kerberosConfig);
        } else {
            kerberosDao.update(kerberosConfig);
        }
        return remoteDirKerberos;
    }

    private String parsePrincipal(String principal, List<PrincipalName> principalLists) {
        if(CollectionUtils.isEmpty(principalLists)){
            throw new RdosDefineException("The keytab file does not contain principal");
        }
        if (StringUtils.isBlank(principal)) {
            //不传默认取第一个
            principal = principalLists.get(0).getName();
        } else {
            String finalPrincipal = principal;
            boolean isContainsPrincipal = principalLists
                    .stream()
                    .anyMatch(p -> p.getName().equalsIgnoreCase(finalPrincipal));
            if (!isContainsPrincipal) {
                throw new RdosDefineException(String.format("The uploaded Hadoop-Kerberos file does not contain the corresponding %s", principal));
            }
        }
        return principal;
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeKerberos( Long componentId) {
        try {
            // 删除kerberos配置需要版本号
            Component component = componentDao.getOne(componentId);
            if (Objects.isNull(component)){
                return;
            }
            kerberosDao.deleteByComponent(component.getEngineId(),component.getComponentTypeCode(),component.getHadoopVersion());
            Component updateComponent = new Component();
            updateComponent.setId(componentId);
            updateComponent.setKerberosFileName("");
            componentDao.update(updateComponent);
        } catch (Exception e) {
            throw new  RdosDefineException("移除kerberos配置异常");
        }
    }

    public ComponentsResultVO addOrCheckClusterWithName(String clusterName) {
        if (StringUtils.isNotBlank(clusterName)) {
            if (clusterName.length() > 24) {
                throw new RdosDefineException("The name is too long");
            }
        } else {
            throw new RdosDefineException("The cluster name cannot be empty");
        }
        clusterName = clusterName.trim();
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            //创建集群
            ClusterDTO clusterDTO = new ClusterDTO();
            clusterDTO.setClusterName(clusterName);
            ClusterVO clusterVO = clusterService.addCluster(clusterDTO);
            ComponentsResultVO componentsResultVO = new ComponentsResultVO();
            Long clusterId = clusterVO.getClusterId();
            componentsResultVO.setClusterId(clusterId);
            LOGGER.info("add cluster {} ", clusterId);
            return componentsResultVO;
        }
        throw new RdosDefineException("Cluster name already exists");
    }


    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    @SuppressWarnings("all")
    public List<Object> config(List<Resource> resources, Integer componentType, Boolean autoDelete, String version) {

        try {
            if (componentTypeConfigMapping.keySet().contains(componentType)) {
                //解析xml文件
                List<String> xmlName = componentTypeConfigMapping.get(componentType);
                return parseXmlFileConfig(resources, xmlName);
            } else if (EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
                //解析k8s组件
                return parseKubernetesData(resources);
            } else {
                //解析上传的json文件
                return parseJsonFile(resources);
            }
        } finally {
            if (null == autoDelete || autoDelete) {
                for (Resource resource : resources) {
                    try {
                        FileUtils.forceDelete(new File(resource.getUploadedFileName()));
                    } catch (IOException e) {
                        LOGGER.debug("delete config resource error {} ", resource.getUploadedFileName());
                    }
                }
            }

        }
    }

    private List<Object> parseJsonFile(List<Resource> resources) {
        List<Object> data = new ArrayList<>();
        // 当作json来解析
        for (Resource resource : resources) {
            try {
                String fileInfo = FileUtils.readFileToString(new File(resource.getUploadedFileName()));
                data.add(PublicUtil.strToMap(fileInfo));
            } catch (Exception e) {
                LOGGER.error("parse json config resource error {} ", resource.getUploadedFileName());
                throw new RdosDefineException("JSON file format error");
            }
        }
        return data;
    }

    private List<Object> parseXmlFileConfig(List<Resource> resources, List<String> xmlName) {
        List<Object> datas = new ArrayList<>();
        Map<String, Map<String,Object>> xmlConfigMap = this.parseUploadFileToMap(resources);
        boolean isLostXmlFile = xmlConfigMap.keySet().containsAll(xmlName);
        if(!isLostXmlFile){
            throw new RdosDefineException("Missing necessary configuration file");
        }
        //多个配置文件合并为一个map
        if(MapUtils.isNotEmpty(xmlConfigMap)){
            Map<String,Object> data = new HashMap<>();
            for (String key : xmlConfigMap.keySet()) {
                data.putAll(xmlConfigMap.get(key));
            }
            datas.add(data);
        }
        return datas;
    }

    private List<Object> parseKubernetesData(List<Resource> resources) {
        List<Object> datas = new ArrayList<>();
        Resource resource = resources.get(0);
        //解压缩获得配置文件
        String xmlZipLocation = resource.getUploadedFileName();
        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        //解析zip 带换行符号
        List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
        if(CollectionUtils.isNotEmpty(xmlFiles)){
            try {
                datas.add(FileUtil.getContentFromFile(xmlFiles.get(0).getPath()));
            } catch (FileNotFoundException e) {
                LOGGER.error("parse Kubernetes resource error {} ", resource.getUploadedFileName());
            }
        }
        return datas;
    }


    public String buildSftpPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name();
    }


    /**
     * 测试单个组件联通性
     */
    public ComponentTestResult testConnect(Integer componentType, String componentConfig, String clusterName,
                                           String hadoopVersion, Long engineId, KerberosConfig kerberosConfig, Map<String, String> sftpConfig,Integer storeType,Map<Integer,String > componentVersionMap,Integer deployType) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            if (EComponentType.notCheckComponent.contains(EComponentType.getByCode(componentType))) {
                componentTestResult.setResult(true);
                return componentTestResult;
            }

            String pluginType = null;
            if (EComponentType.HDFS.getTypeCode().equals(componentType)) {
                //HDFS 测试连通性走hdfs2 其他走yarn2-hdfs2-hadoop
                pluginType = EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(hadoopVersion, EComponentType.HDFS);
            } else {
                pluginType = this.convertComponentTypeToClient(clusterName, componentType, hadoopVersion,storeType,componentVersionMap,deployType);
            }

            componentTestResult = workerOperator.testConnect(pluginType,
                    this.wrapperConfig(componentType, componentConfig, sftpConfig, kerberosConfig, clusterName));
            if (null == componentTestResult) {
                componentTestResult = new ComponentTestResult();
                componentTestResult.setResult(false);
                componentTestResult.setErrorMsg("测试联通性失败");
                return componentTestResult;
            }
            // 单组件连通性测试回写yarn的队列信息
            if (EComponentType.YARN.getTypeCode().equals(componentType)
                    && componentTestResult.getResult()
                    && Objects.nonNull(componentTestResult.getClusterResourceDescription())) {
                    engineService.updateResource(engineId, componentTestResult.getClusterResourceDescription());
                    queueService.updateQueue(engineId, componentTestResult.getClusterResourceDescription());
            }

        }catch (Throwable e){
            if (Objects.isNull(componentTestResult)){
                componentTestResult = new ComponentTestResult();
            }
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (null != componentTestResult) {
                componentTestResult.setComponentTypeCode(componentType);
                componentTestResult.setComponentVersion(hadoopVersion);
            }
        }
        return componentTestResult;
    }


    /**
     * 将页面配置参数转换为插件需要的参数
     *
     * @param componentType
     * @param componentConfig
     * @return
     */
    public String wrapperConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig, String clusterName) {
        JSONObject dataInfo = new JSONObject();
        dataInfo.put("componentName", EComponentType.getByCode(componentType).getName().toLowerCase());
        if (null != kerberosConfig) {
            dataInfo.put("kerberosFileTimestamp", kerberosConfig.getGmtModified());
            //开启了kerberos
            dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
            dataInfo.put("remoteDir", kerberosConfig.getRemotePath());
            dataInfo.put("principalFile", kerberosConfig.getName());
            dataInfo.put("krbName", kerberosConfig.getKrbName());
            dataInfo.put("principal", kerberosConfig.getPrincipal());
            dataInfo.put(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
        }
        dataInfo.put(EComponentType.SFTP.getConfName(), sftpConfig);
        if (EComponentType.SFTP.getTypeCode() == componentType) {
            dataInfo = JSONObject.parseObject(componentConfig);
            dataInfo.put("componentType", EComponentType.SFTP.getName());
        } else if (EComponentType.sqlComponent.contains(EComponentType.getByCode(componentType))) {
            dataInfo = buildSQLComponentConfig(componentType, componentConfig, sftpConfig, kerberosConfig, clusterName);
        } else if (EComponentType.YARN.getTypeCode() == componentType) {
            Map map = JSONObject.parseObject(componentConfig, Map.class);
            dataInfo.put(EComponentType.YARN.getConfName(), map);
        } else if (EComponentType.HDFS.getTypeCode() == componentType) {
            Map map = JSONObject.parseObject(componentConfig, Map.class);
            dataInfo.put(EComponentType.HDFS.getConfName(), map);
            //补充yarn参数
            putYarnConfig(clusterName, dataInfo);
        } else if (EComponentType.KUBERNETES.getTypeCode() == componentType) {
            //
            dataInfo = new JSONObject();
            JSONObject confObj = new JSONObject();
            if (null != componentConfig && componentConfig.contains("kubernetes.context")) {
                JSONObject contextConf = JSONObject.parseObject(componentConfig);
                componentConfig = contextConf.getString("kubernetes.context");
            }
            confObj.put(EComponentType.KUBERNETES.getConfName(), componentConfig);
            dataInfo.put(EComponentType.KUBERNETES.getConfName(), confObj);
            dataInfo.put("componentName", EComponentType.KUBERNETES.getName());
        } else if (EComponentType.NFS.getTypeCode() == componentType){
            dataInfo = JSONObject.parseObject(componentConfig);
            dataInfo.put("componentType", EComponentType.NFS.getName());
        }else if (EComponentType.DTSCRIPT_AGENT.getTypeCode() == componentType){
            return componentConfig;
        }
        return dataInfo.toJSONString();
    }

    private JSONObject buildSQLComponentConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig, String clusterName) {
        JSONObject dataInfo;
        dataInfo = JSONObject.parseObject(componentConfig);
        dataInfo.put(EComponentType.SFTP.getConfName(), sftpConfig);
        String jdbcUrl = dataInfo.getString("jdbcUrl");
        if (StringUtils.isBlank(jdbcUrl)) {
            throw new RdosDefineException("jdbcUrl cannot be empty");
        }

        if (EComponentType.SPARK_THRIFT.getTypeCode() == componentType ||
                EComponentType.HIVE_SERVER.getTypeCode() == componentType ||
                EComponentType.TIDB_SQL.getTypeCode() == componentType) {
            //数据库连接不带%s
            String replaceStr = "/";
            if (null != kerberosConfig) {
                replaceStr = env.getComponentJdbcToReplace();
            }
            jdbcUrl = jdbcUrl.replace("/%s", replaceStr);
            if (EComponentType.TIDB_SQL.getTypeCode() == componentType && !jdbcUrl.endsWith("/")) {
                //tidb 需要以/结尾
                jdbcUrl = jdbcUrl + "/";
            }
        }

        dataInfo.put("jdbcUrl", jdbcUrl);
        dataInfo.put("username", dataInfo.getString("username"));
        dataInfo.put("password", dataInfo.getString("password"));
        if (null != kerberosConfig ) {
            //开启了kerberos
            dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
            dataInfo.put("remoteDir", kerberosConfig.getRemotePath());
            dataInfo.put("principalFile", kerberosConfig.getName());
            dataInfo.put("principal", kerberosConfig.getPrincipal());
            dataInfo.put("krbName", kerberosConfig.getKrbName());
            dataInfo.put("kerberosFileTimestamp", kerberosConfig.getGmtModified());
            dataInfo.put(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
            //补充yarn参数
            putYarnConfig(clusterName, dataInfo);
        }
        return dataInfo;
    }

    /**
     * @author newman
     * @Description 设置yarn配置
     * @Date 2020-12-22 11:40
     * @param clusterName:
     * @param dataInfo:
     * @return: void
     **/
    private void putYarnConfig(String clusterName, JSONObject dataInfo) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null != cluster) {
            Map yarnMap = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(), false, Map.class,null);
            if (null != yarnMap) {
                dataInfo.put(EComponentType.YARN.getConfName(), yarnMap);
            }
        }
    }

    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return env.getLocalKerberosDir() + File.separator + AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() + File.separator + KERBEROS_PATH;
    }

    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件
     * @return
     */
    public File downloadFile(Long componentId,  Integer downloadType,  Integer componentType,
                              String componentVersion,  String clusterName,Integer deployType) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (null == componentId) {
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(componentType, clusterName, componentVersion,null,null,deployType);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                Map<String, Object> fileMap = ComponentConfigUtils.convertClientTemplateToMap(clientTemplates);
                uploadFileName = EComponentType.getByCode(componentType).name() + ".json";
                localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
                try {
                    FileUtils.write(new File(localDownLoadPath), JSONObject.toJSONString(fileMap));
                } catch (Exception e) {
                    throw new RdosDefineException("file does not exist");
                }
            }
        } else {
            Component component = componentDao.getOne(componentId);
            if (null == component) {
                throw new RdosDefineException("Component does not exist");
            }
            Long clusterId = componentDao.getClusterIdByComponentId(componentId);
            SftpConfig sftpConfig = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),false,SftpConfig.class,null);
            if ( null == sftpConfig ) {
                throw new RdosDefineException("sftp component does not exist");
            }

            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + component.getComponentName();
            String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, component.getComponentTypeCode());
            SftpFileManage sftpFileManage = null;
            if (DownloadType.Kerberos.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + KERBEROS_PATH;
                localDownLoadPath = localDownLoadPath + File.separator + KERBEROS_PATH;
                sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                sftpFileManage.downloadDir(remoteDir, localDownLoadPath);
            } else {
                if (StringUtils.isBlank(component.getUploadFileName())) {
                    // 一种是  全部手动填写的 如flink
                    EComponentType type = EComponentType.getByCode(componentType);
                    String componentConfig = getComponentByClusterId(clusterId,type.getTypeCode(),true,String.class,Collections.singletonMap(type.getTypeCode(),componentVersion));
                    try {
                        localDownLoadPath = localDownLoadPath + ".json";
                        FileUtils.write(new File(localDownLoadPath), filterConfigMessage(componentConfig));
                    } catch (IOException e) {
                        LOGGER.error("write upload file {} error", componentConfig, e);
                    }
                } else {
                    sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                    // 一种是 上传配置文件的需要到sftp下载
                    sftpFileManage.downloadDir(remoteDir + File.separator + component.getUploadFileName(), localDownLoadPath);
                }
            }
            uploadFileName = component.getUploadFileName();
        }

        File file = new File(localDownLoadPath);
        if (!file.exists()) {
            throw new RdosDefineException("file does not exist");
        }
        String zipFilename = StringUtils.isBlank(uploadFileName) ? "download.zip" : uploadFileName;
        if (file.isDirectory()) {
            //将文件夹压缩成zip文件
            return zipFile(componentId, downloadType, componentType, file, zipFilename);
        } else {
            return new File(localDownLoadPath);
        }
    }

    /**
     * 移除配置信息中的密码信息
     *
     */
    private String filterConfigMessage(String componentConfig) {
        if (StringUtils.isBlank(componentConfig)) {
            return "";
        }
        JSONObject configJsonObject = JSONObject.parseObject(componentConfig);
        configJsonObject.put("password","");
        return configJsonObject.toJSONString();
    }


    private File zipFile(Long componentId, Integer downloadType, Integer componentType, File file, String zipFilename) {
        File[] files = file.listFiles();
        //压缩成zip包
        if (null != files ) {
            if (DownloadType.Kerberos.getCode() == downloadType) {
                Long clusterId = componentDao.getClusterIdByComponentId(componentId);
                KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType,ComponentVersionUtil.isMultiVersionComponent(componentType)?componentDao.getDefaultComponentVersionByClusterAndComponentType(clusterId,componentType):null);
                if ( null != kerberosConfig ) {
                    zipFilename = kerberosConfig.getName() + ZIP_SUFFIX;
                }
            }
            ZipUtil.zipFile(USER_DIR_DOWNLOAD + File.separator + zipFilename, Arrays.asList(files));
        }
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            LOGGER.error("delete upload file {} error", file.getName(), e);
        }
        return new File(USER_DIR_DOWNLOAD + File.separator + zipFilename);
    }

    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType 组件类型
     * @param clusterName   集群名称
     * @param componentVersion       组件版本值 如2.7.3
     * @param storeType     存储组件type 如 HDFS
     * @param originVersion 组件版本名称 如CDH 7.1.x
     * @return
     */
    public List<ClientTemplate> loadTemplate(Integer componentType, String clusterName, String componentVersion, Integer storeType, String originVersion,Integer deployType) {
        EComponentType component = EComponentType.getByCode(componentType);
        List<ComponentConfig> componentConfigs = new ArrayList<>();
        String yarnVersion = EComponentType.YARN.getTypeCode().equals(componentType) ? originVersion : null;
        if (!EComponentType.noControlComponents.contains(component)) {
            String typeName = convertComponentTypeToClient(clusterName, componentType, componentVersion, storeType,null,deployType);
            componentConfigs = componentConfigService.loadDefaultTemplate(typeName);
            ClusterVO clusterByName = clusterService.getClusterByName(clusterName);
            Component yarnComponent = componentDao.getByClusterIdAndComponentType(clusterByName.getClusterId(), EComponentType.YARN.getTypeCode(),null,null);
            if (null != yarnComponent) {
                ComponentConfig originHadoopVersion = componentConfigService.getComponentConfigByKey(yarnComponent.getId(), HADOOP_VERSION);
                yarnVersion = null == originHadoopVersion ? yarnComponent.getHadoopVersion() : originHadoopVersion.getValue();
            }
        }
        //根据yarn的版本添加额外配置
        List<ComponentConfig> extraConfig = scheduleDictService.loadExtraComponentConfig(yarnVersion, componentType);
        if (CollectionUtils.isNotEmpty(extraConfig)) {
            componentConfigs.addAll(extraConfig);
        }
        return ComponentConfigUtils.buildDBDataToClientTemplate(componentConfigs);
    }


    /**
     * 根据组件类型转换对应的插件名称
     * 如果只配yarn 需要调用插件时候 hdfs给默认值
     *
     * @param clusterName
     * @param componentType
     * @param version
     * @return
     */
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String version, Integer storeType,Map<Integer,String> componentVersionMap,Integer deployType) {
        //普通rdb插件
        EComponentType componentCode = EComponentType.getByCode(componentType);
        String pluginName = EComponentType.convertPluginNameByComponent(componentCode);
        if (StringUtils.isNotBlank(pluginName)) {
            return pluginName;
        }
        //如果没传 给默认插件 version
        if (StringUtils.isEmpty(version)) {
            Integer dictType = DictType.getByEComponentType(EComponentType.getByCode(componentType));
            if (null != dictType) {
                ScheduleDict defaultVersion = scheduleDictService.getTypeDefaultValue(dictType);
                if (null != defaultVersion) {
                    return defaultVersion.getDictValue();
                }
            }

        }
        //flink on standalone处理
        if(EComponentType.FLINK.getTypeCode().equals(componentType) && EDeployType.STANDALONE.getType() == deployType){
            return String.format("%s%s",String.format("%s%s",EComponentType.FLINK.name().toLowerCase(),version),"-standalone");
        }
        //hive 特殊处理 version
        if (EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) || EComponentType.SPARK_THRIFT.getTypeCode().equals(componentType)) {
            pluginName = "hive";
            if (!version.equalsIgnoreCase("1.x")) {
                pluginName = pluginName + version.charAt(0);
            }
            return pluginName;
        }

        //调度或存储单个组件
        if (EComponentType.YARN.equals(componentCode)) {
            return String.format("%s%s", componentCode.name().toLowerCase(), this.formatHadoopVersion(version, componentCode));
        }

        ClusterVO cluster = clusterService.getClusterByName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("Cluster does not exist");
        }

        //需要按照 调度-存储-计算 拼接的typeName
        String computeSign = EComponentType.convertPluginNameWithNeedVersion(componentCode);
        if (StringUtils.isBlank(computeSign)) {
            throw new RdosDefineException("Unsupported components");
        }
        Component yarn = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode(), ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.YARN),null);
        Component kubernetes = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.KUBERNETES.getTypeCode(),ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.KUBERNETES),null);
        if (null == yarn && null == kubernetes) {
            throw new RdosDefineException("Please configure the scheduling component first");
        }
        String resourceSign = null == yarn ? "k8s" : EComponentType.YARN.name().toLowerCase() + this.formatHadoopVersion(yarn.getHadoopVersion(), EComponentType.YARN);

        String storageSign = this.buildStoreSign(cluster, storeType,version,componentType);

        computeSign = computeSign + this.formatHadoopVersion(version, componentCode);
        return String.format("%s-%s-%s", resourceSign, storageSign, computeSign);
    }

    private String buildStoreSign(ClusterVO cluster, Integer storeType, String version, Integer componentType) {
        String storageSign = "";
        //如果组件配置了对应的存储组件 以配置为准
        if (null != storeType) {
            EComponentType storeComponent = EComponentType.getByCode(storeType);
            if (EComponentType.NFS.equals(storeComponent)) {
                return EComponentType.NFS.name().toLowerCase();
            } else {
                if (EComponentType.HDFS.getTypeCode().equals(componentType)) {
                    //当前更新组件为hdfs
                    return EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(version, EComponentType.HDFS);
                } else {
                    Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
                    if (null == hdfs) {
                        throw new RdosDefineException("Please configure storage components first");
                    }
                    return EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(hdfs.getHadoopVersion(), EComponentType.HDFS);
                }
            }
        } else {
            //hdfs和nfs可以共存 hdfs为默认
            Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
            Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode(),null,null);
            if (null == hdfs && null == nfs) {
                throw new RdosDefineException("Please configure storage components first");
            }
            storageSign = null == hdfs ? EComponentType.NFS.name().toLowerCase() :
                    EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(hdfs.getHadoopVersion(), EComponentType.HDFS);
        }
        return storageSign;
    }

    /**
     * version 默认为2
     * <p>
     * hadoop2  返回为2
     * hadoopHW 返回hw
     *
     * @param hadoopVersion
     * @return
     */
    public String formatHadoopVersion(String hadoopVersion, EComponentType componentType) {
        if (EComponentType.HDFS == componentType || EComponentType.YARN == componentType) {
            if (StringUtils.isBlank(hadoopVersion)) {
                return "2";
            }
            if (hadoopVersion.startsWith("hadoop")) {
                //hadoop2
                return hadoopVersion.toLowerCase().replace("hadoop", "").substring(0, 1);
            } else if (hadoopVersion.startsWith("2.") || hadoopVersion.startsWith("3.")) {
                //2.x
                return hadoopVersion.substring(0, 1);
            } else {
                //hw
                return hadoopVersion.substring(0, 2);
            }
        } else if (EComponentType.FLINK == componentType || EComponentType.SPARK == componentType) {
            //flink spark 为 三位版本标识
            return hadoopVersion;
        }
        return "";
    }


    /**
     * 删除组件
     *
     * @param componentIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(List<Integer> componentIds) {
        if (CollectionUtils.isEmpty(componentIds)) {
            return;
        }
        List<Long> engineIds = new ArrayList<>(componentIds.size());
        for (Integer componentId : componentIds) {
            Component component = componentDao.getOne(componentId.longValue()),nextDefaultComponent;
            EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());

            if(!canDeleteComponent(component.getId(),component.getComponentTypeCode(),component.getHadoopVersion())){
                throw new RdosDefineException("can not delete component because have task submit to schedule");
            }

            if (EComponentType.requireComponent.contains(EComponentType.getByCode(component.getComponentTypeCode()))){
                throw new RdosDefineException(String.format("%s is a required component and cannot be deleted",component.getComponentName()));
            }
            if (component.getIsDefault() && Objects.nonNull(nextDefaultComponent = componentDao.getNextDefaultComponent(
                    component.getEngineId(),component.getComponentTypeCode(),component.getId())) && ! nextDefaultComponent.getIsDefault()){
                nextDefaultComponent.setIsDefault(true);
                componentDao.update(nextDefaultComponent);
            }
            componentDao.deleteById(componentId.longValue());
            kerberosDao.deleteByComponent(component.getEngineId(),component.getComponentTypeCode(),ComponentVersionUtil.formatMultiVersion(component.getComponentTypeCode(),component.getHadoopVersion()));
            componentConfigService.deleteComponentConfig(componentId.longValue());
            engineIds.add(component.getEngineId());
            try {
                Engine engine = engineDao.getOne(component.getEngineId());
                if (null != engine) {
                    this.updateCache(engine.getClusterId(), engine.getId(), component.getComponentTypeCode());
                }
            } catch (Exception e) {
                LOGGER.error("clear cache error {} ", componentIds, e);
            }
        }
    }


    /***
     * 获取对应的组件版本信息
     * @return
     */
    public Map getComponentVersion() {
        return scheduleDictService.getVersion();
    }

    public Component getComponentByClusterId(Long clusterId, Integer componentType,String componentVersion) {
        return componentDao.getByClusterIdAndComponentType(clusterId, componentType,componentVersion,null);
    }

    /**
     * 获取对应组件的配置信息
     *
     * @param clusterId
     * @param componentType
     * @param isFilter      是否移除typeName 等配置信息
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz,Map<Integer,String > componentVersionMap,Long componentId) {
        Map<String, Object> configMap = getCacheComponentConfigMap(clusterId, componentType, isFilter,componentVersionMap,componentId);
        if(MapUtils.isEmpty(configMap)){
            return null;
        }
        if (clazz.isInstance(Map.class)) {
            return (T) configMap;
        }
        String configStr = JSONObject.toJSONString(configMap);
        if (clazz.isInstance(String.class)) {
            return (T) configStr;
        }
        return JSONObject.parseObject(configStr, clazz);
    }

    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz,Map<Integer,String > componentVersionMap) {
        return getComponentByClusterId(clusterId,componentType,isFilter,clazz,componentVersionMap,null);
    }

    public <T> T getComponentByClusterId(Long componentId,boolean isFilter, Class<T> clazz) {
        return getComponentByClusterId(null,null,isFilter,clazz,null,componentId);
    }

    @Cacheable(cacheNames = "component")
    public Map<String, Object> getCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter, Map<Integer, String> componentVersionMap, Long componentId) {
        if (null != componentId) {
            return componentConfigService.convertComponentConfigToMap(componentId, isFilter);
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType, ComponentVersionUtil.getComponentVersion(componentVersionMap, componentType),null);
        if (null == component) {
            return null;
        }
        return componentConfigService.convertComponentConfigToMap(component.getId(), isFilter);
    }

    @CacheEvict(cacheNames = "component", allEntries = true)
    public void clearComponentCache() {
        LOGGER.info(" clear all component cache ");
    }

    /**
     * 刷新组件信息
     *
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> refresh(String clusterName) {
        List<ComponentTestResult> refreshResults = new ArrayList<>();
        ComponentTestResult componentTestResult = testConnect(clusterName, EComponentType.YARN.getTypeCode(),null);
        refreshResults.add(componentTestResult);
        return refreshResults;
    }

    public ComponentTestResult testConnect(String clusterName, Integer componentType, Map<Integer,String> componentVersionMap) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }
        Component testComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), componentType,ComponentVersionUtil.getComponentVersion(componentVersionMap,componentType),null);
        if (null == testComponent) {
            throw new RdosDefineException("该组件不存在");
        }
        if (EComponentType.notCheckComponent.contains(EComponentType.getByCode(componentType))) {
            ComponentTestResult componentTestResult = new ComponentTestResult();
            componentTestResult.setComponentTypeCode(componentType);
            componentTestResult.setResult(true);
            componentTestResult.setComponentVersion(testComponent.getHadoopVersion());
            return componentTestResult;
        }
        Map sftpMap = getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class,null);
        return testComponentWithResult(clusterName,cluster,sftpMap,testComponent);
    }

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    public List<ComponentMultiTestResult> testConnects(String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        List<Component> components = getComponents(cluster);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>();
        }

        Map sftpMap = getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class,null);
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        Table<Integer,String , Future<ComponentTestResult>> completableFutures = HashBasedTable.create();
        for (Component component : components) {
            Future<ComponentTestResult> testResultFuture = connectPool.submit(() -> {
                try {
                    return testComponentWithResult(clusterName, cluster, sftpMap, component);
                } finally {
                    countDownLatch.countDown();
                }
            });
            completableFutures.put(component.getComponentTypeCode(),StringUtils.isBlank(component.getHadoopVersion())?StringUtils.EMPTY:component.getHadoopVersion(),testResultFuture);
        }
        try {
            countDownLatch.await(env.getTestConnectTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("test connect await {} error ", clusterName, e);
        }

        Map<Integer,ComponentMultiTestResult> multiComponent=new HashMap<>(completableFutures.size());
        for (Table.Cell<Integer, String, Future<ComponentTestResult>> cell : completableFutures.cellSet()) {
            Future<ComponentTestResult> completableFuture = cell.getValue();
            ComponentTestResult testResult = new ComponentTestResult();
            testResult.setResult(false);
            try {
                if (completableFuture.isDone()) {
                    testResult = completableFuture.get();
                } else {
                    testResult.setErrorMsg("test connect time out");
                    completableFuture.cancel(true);
                }
            } catch (Exception e) {
                testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            } finally {
                testResult.setComponentTypeCode(cell.getRowKey());
                ComponentMultiTestResult multiTestResult = multiComponent.computeIfAbsent(cell.getRowKey(), k -> new ComponentMultiTestResult(cell.getRowKey()));
                buildComponentMultiTest(multiTestResult,testResult,cell.getColumnKey());
            }
        }
        return new ArrayList<>(multiComponent.values());
    }

    private ComponentTestResult testComponentWithResult(String clusterName, Cluster cluster, Map sftpMap,Component component) {
        ComponentTestResult testResult = new ComponentTestResult();
        try {
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), component.getComponentTypeCode(),ComponentVersionUtil.isMultiVersionComponent(component.getComponentTypeCode())?StringUtils.isNotBlank(component.getHadoopVersion())?component.getHadoopVersion():componentDao.getDefaultComponentVersionByClusterAndComponentType(cluster.getId(),component.getComponentTypeCode()):null);
            String componentConfig = getComponentByClusterId(cluster.getId(), component.getComponentTypeCode(), false, String.class,null);
            testResult = this.testConnect(component.getComponentTypeCode(), componentConfig, clusterName, component.getHadoopVersion(), component.getEngineId(), kerberosConfig, sftpMap,component.getStoreType(),null,component.getDeployType());
            //测试联通性
            if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode()) && testResult.getResult()) {
                if (null != testResult.getClusterResourceDescription()) {
                    engineService.updateResource(component.getEngineId(), testResult.getClusterResourceDescription());
                    queueService.updateQueue(component.getEngineId(), testResult.getClusterResourceDescription());
                } else {
                    testResult.setResult(false);
                    testResult.setErrorMsg(clusterName + "获取yarn信息为空");
                }
            }
        } catch (Exception e) {
            testResult.setResult(false);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            LOGGER.error("test connect {}  error ", component.getId(), e);
        } finally {
            testResult.setComponentVersion(component.getHadoopVersion());
            testResult.setComponentTypeCode(component.getComponentTypeCode());
        }
        return testResult;
    }

    private List<Component> getComponents(Cluster cluster) {

        if (null == cluster) {
            throw new RdosDefineException("Cluster does not exist");
        }
        List<Engine> engines = engineDao.listByClusterId(cluster.getId());
        if (CollectionUtils.isEmpty(engines)) {
            return new ArrayList<>(0);
        }
        List<Long> engineId = engines.stream().map(Engine::getId).collect(Collectors.toList());

        List<Component> components = componentDao.listByEngineIds(engineId,null);
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyList();
        }
        return components;
    }


    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("Cluster does not exist");
        }
        List<Component> components = new ArrayList<>();
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
        if (null != hdfs) {
            components.add(hdfs);
        }
        Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode(),null,null);
        if (null != nfs) {
            components.add(nfs);
        }
        return components;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
        if (StringUtils.isBlank(namespace)) {
            throw new RdosDefineException("namespace cannot be empty");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException("Cluster is empty");
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.KUBERNETES.getTypeCode(),null,null);
        if (null == component) {
            throw new RdosDefineException("kubernetes Component is empty");
        }
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (null == engine) {
            throw new RdosDefineException("Engine is empty");
        }
        String clusterName = cluster.getClusterName();
        String pluginType = this.convertComponentTypeToClient(clusterName, EComponentType.KUBERNETES.getTypeCode(), "", null,null,null);
        Map sftpMap = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),false,Map.class,null);
        if (sftpMap == null) {
            throw new RdosDefineException("sftp配置为空");
        }
        String componentConfig = getComponentByClusterId(clusterId, EComponentType.KUBERNETES.getTypeCode(),false,String.class,null);
        //测试namespace 的权限
        String pluginInfo = this.wrapperConfig(EComponentType.KUBERNETES.getTypeCode(), componentConfig, sftpMap, null, clusterName);
        JSONObject infoObject = JSONObject.parseObject(pluginInfo);
        infoObject.put("namespace", namespace);
        ComponentTestResult componentTestResult = workerOperator.testConnect(pluginType, infoObject.toJSONString());
        if (null != componentTestResult && StringUtils.isNotBlank(componentTestResult.getErrorMsg())) {
            throw new RdosDefineException(componentTestResult.getErrorMsg());
        }
        List<Queue> namespaces = queueDao.listByIds(Lists.newArrayList(queueId));
        if (CollectionUtils.isNotEmpty(namespaces)) {
            Queue dbQueue = namespaces.get(0);
            dbQueue.setQueueName(namespace);
            dbQueue.setQueuePath(namespace);
            dbQueue.setIsDeleted(Deleted.NORMAL.getStatus());
            queueDao.update(dbQueue);
            return dbQueue.getId();
        } else {
            Queue queue = new Queue();
            queue.setQueueName(namespace);
            queue.setEngineId(engine.getId());
            queue.setMaxCapacity("0");
            queue.setCapacity("0");
            queue.setQueueState("ACTIVE");
            queue.setParentQueueId(DEFAULT_KUBERNETES_PARENT_NODE);
            queue.setQueuePath(namespace);
            Integer insert = queueDao.insert(queue);
            if (insert != 1) {
                throw new RdosDefineException("operation failed");
            }
            if (null == queueId) {
                Tenant tenant = tenantDao.getByDtUicTenantId(dtUicTenantId);
                if (null == tenant) {
                    throw new RdosDefineException("Tenant does not exist");
                }
                Long dbQueue = engineTenantDao.getQueueIdByTenantId(tenant.getId());
                if (null == dbQueue) {
                    //兼容4.0 queueId为空的数据 需要重新绑定
                    tenantService.updateTenantQueue(tenant.getId(), dtUicTenantId, engine.getId(), queue.getId());
                }
            }
            return queue.getId();
        }
    }

    public Boolean isYarnSupportGpus(String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        Component yarnComponent = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(),null);

        if (yarnComponent == null) {
            return false;
        }
        if (!HADOOP3_SIGNAL.equals(yarnComponent.getHadoopVersion())) {
            return false;
        }
        JSONObject yarnConf = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(),false,JSONObject.class,null);
        if(null == yarnConf){
            return false;
        }
        if (!"yarn.io/gpu".equals(yarnConf.getString(GPU_RESOURCE_PLUGINS_SIGNAL))) {
            return false;
        }
        if (StringUtils.isBlank(yarnConf.getString(GPU_EXEC_SIGNAL))) {
            return false;
        }
        if (!"true".equals(yarnConf.getString(GPU_ALLOWED_SIGNAL)) && !"auto".equals(yarnConf.getString(GPU_ALLOWED_SIGNAL))) {
            return false;
        }


        ClusterResource resource = consoleService.getResources(yarnComponent, cluster,yarnConf);
        List<JSONObject> queues = resource.getQueues();
        if (queues != null && queues.size() > 0) {
            for (JSONObject object: queues) {
                try {
                    JSONArray resources = object.getJSONObject("resources").getJSONArray("resourceUsagesByPartition");
                    for (int i = 0; i < resources.size(); ++i) {
                        JSONObject ele = resources.getJSONObject(i);
                        if (ele.containsKey("used")) {
                            JSONArray info = ele.getJSONObject("used").getJSONObject("resourceInformations")
                                    .getJSONArray("resourceInformation");
                            for (int j = 0; j < info.size(); ++j) {
                                JSONObject jsonEle = info.getJSONObject(j);
                                if ("yarn.io/gpu".equals(jsonEle.getString("name"))) {
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return false;
    }


    /**
     * 解析对应的kerberos的zip中principle
     * @param resourcesFromFiles
     * @return
     */
    public List<String> parseKerberos(List<Resource> resourcesFromFiles) {
        if (CollectionUtils.isEmpty(resourcesFromFiles)) {
            return null;
        }
        Resource resource = resourcesFromFiles.get(0);
        String unzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            //解压到本地
            List<File> files = ZipUtil.upzipFile(resource.getUploadedFileName(), unzipLocation);

            if (CollectionUtils.isEmpty(files)) {
                throw new RdosDefineException("Hadoop-Kerberos file decompression error");
            }

            File fileKeyTab = files
                    .stream()
                    .filter(f -> f.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst()
                    .orElse(null);
            if (fileKeyTab == null) {
                throw new RdosDefineException("There must be a keytab file in the zip file of the uploaded Hadoop-Kerberos file, please add the keytab file");
            }

            //获取principal
            List<PrincipalName> principal = this.getPrincipal(fileKeyTab);
            return  principal
                    .stream()
                    .map(PrincipalName::getName)
                    .collect(Collectors.toList());
        } finally {
            try {
                FileUtils.deleteDirectory(new File(unzipLocation));
            } catch (IOException e) {
                LOGGER.error("delete update file {} error", unzipLocation);
            }
        }
    }

    /**
     * 更新metadata的元数据组件
     * 如果集群只有单个metadata组件 默认勾选
     * 如果集群多个metadata组件 绑定租户之后 无法切换
     *
     * @param engineId
     * @param componentType
     * @param isMetadata
     * @return
     */
    public boolean changeMetadata(Integer componentType, boolean isMetadata, Long engineId, Integer oldMetadata) {
        if (!EComponentType.metadataComponents.contains(EComponentType.getByCode(componentType))) {
            return false;
        }
        Integer revertComponentType = EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) ? EComponentType.SPARK_THRIFT.getTypeCode() : EComponentType.HIVE_SERVER.getTypeCode();
        Component revertComponent = componentDao.getByEngineIdAndComponentType(engineId, revertComponentType);
        if (null == revertComponent) {
            //单个组件默认勾选
            componentDao.updateMetadata(engineId, componentType, 1);
            return true;
        }
        if (null != oldMetadata && !BooleanUtils.toIntegerObject(isMetadata, 1, 0).equals(oldMetadata)) {
            //如果集群已经绑定过租户 不允许修改
            if (CollectionUtils.isNotEmpty(engineTenantDao.listEngineTenant(engineId))) {
                throw new RdosDefineException("cluster has bind tenant can not change metadata component");
            }
        }
        LOGGER.info("change metadata engine {} component {} to {} ", engineId, componentType, isMetadata);
        componentDao.updateMetadata(engineId, componentType, isMetadata ? 1 : 0);
        componentDao.updateMetadata(engineId, revertComponentType, isMetadata ? 0 : 1);
        return true;
    }

    /**
     * 构建组件多版本测试结果
     * @param multiTestResult 多版本
     * @param componentTestResult 单个版本
     * @param componentVersion 版本,可能为 ""
     */
    private void buildComponentMultiTest(ComponentMultiTestResult multiTestResult,ComponentTestResult componentTestResult,String componentVersion){
        if (!componentTestResult.getResult()){
            if (multiTestResult.getResult()){
                multiTestResult.setResult(false);
                multiTestResult.setErrorMsg(new ArrayList<>(2));
            }
            multiTestResult.getErrorMsg().add(new ComponentMultiTestResult.MultiErrorMsg(componentVersion,componentTestResult.getErrorMsg()));
        }
        multiTestResult.getMultiVersion().add(componentTestResult);

    }

    public List<DtScriptAgentLabel> getDtScriptAgentLabel(String agentAddress) {
        try {
            String pluginInfo = new JSONObject(1).fluentPut("agentAddress",agentAddress).toJSONString();
            // 不需要集群信息,dtScriptAgent属于普通rdb,直接获取即可
            String engineType = EComponentType.convertPluginNameByComponent(EComponentType.DTSCRIPT_AGENT);
            List<DtScriptAgentLabel> dtScriptAgentLabelList = workerOperator.getDtScriptAgentLabel(engineType, pluginInfo);
            Map<String, List<DtScriptAgentLabel>> labelGroup = dtScriptAgentLabelList.stream().collect(Collectors.groupingBy(DtScriptAgentLabel::getLabel));
            List<DtScriptAgentLabel> resultList = new ArrayList<>(labelGroup.size());
            for (Map.Entry<String, List<DtScriptAgentLabel>> entry : labelGroup.entrySet()) {
                String ip = entry.getValue().stream().map(localIp -> localIp.getLocalIp()+":22").collect(Collectors.joining(","));
                DtScriptAgentLabel dtScriptAgentLabel = new DtScriptAgentLabel();
                dtScriptAgentLabel.setLabel(entry.getKey());
                dtScriptAgentLabel.setLocalIp(ip);
                resultList.add(dtScriptAgentLabel);
            }
            return resultList;
        }catch (Exception e){
            LOGGER.error("find dtScript Agent label error",e);
        }
        return Collections.emptyList();
    }

    public List<Component> getComponentVersionByEngineType(Long uicTenantId, String  engineType) {
        EComponentType componentType = EngineTypeComponentType.getByEngineName(engineType).getComponentType();
        List<Component > componentVersionList = componentDao.getComponentVersionByEngineType(uicTenantId,componentType.getTypeCode());
        if (CollectionUtils.isEmpty(componentVersionList)){
            return Collections.emptyList();
        }
        Set<String> distinct = new HashSet<>(2);
        List<Component> components =new ArrayList<>(2);
        for (Component component : componentVersionList) {
            if (distinct.add(component.getHadoopVersion())){
                components.add(component);
            }
        }
        return components;
    }

    private boolean canDeleteComponent(Long componentId,Integer componentTypeCode,String componentVersion){

        if (!ComponentVersionUtil.isMultiVersionComponent(componentTypeCode)
                || StringUtils.isBlank(componentVersion)){
            return true;
        }
        List<Long> useUicTenantList = componentDao.allUseUicTenant(componentId);
        if (CollectionUtils.isEmpty(useUicTenantList)){
            return true;
        }

        if (Objects.nonNull(scheduleTaskShadeDao.hasTaskSubmit(useUicTenantList,componentVersion))){
            return false;
        }
        return true;


    }

    public Component getMetadataComponent(Long clusterId){
        return componentDao.getMetadataComponent(clusterId);
    }

    @Transactional
    public void addOrUpdateComponentUser(List<ComponentUserVO> componentUserList) {
        if (CollectionUtils.isEmpty(componentUserList)){
            return ;
        }
        ComponentUserVO componentUserVO = componentUserList.get(0);

        // 删除之前保存的数据
        componentUserDao.deleteByComponentAndCluster(componentUserVO.getClusterId(),componentUserVO.getComponentTypeCode());
        List<ComponentUser> addComponentUserList =  new ArrayList<>(componentUserList.size());
        // 构建实例
        for (ComponentUserVO userVO : componentUserList) {
            if(CollectionUtils.isEmpty(userVO.getComponentUserInfoList())
                    && Boolean.TRUE.equals(userVO.getIsDefault())){
                ComponentUser emptyUser = new ComponentUser();
                emptyUser.setPassword(StringUtils.EMPTY);
                emptyUser.setUserName(StringUtils.EMPTY);
                emptyUser.setLabel(userVO.getLabel());
                emptyUser.setLabelIp(userVO.getLabelIp());
                emptyUser.setIsDefault(true);
                emptyUser.setClusterId(userVO.getClusterId());
                emptyUser.setComponentTypeCode(userVO.getComponentTypeCode());
                addComponentUserList.add(emptyUser);
            }
            if (CollectionUtils.isEmpty(userVO.getComponentUserInfoList())){
                continue;
            }
            for (ComponentUserVO.ComponentUserInfo userInfo : userVO.getComponentUserInfoList()) {
                ComponentUser componentUser = new ComponentUser();
                componentUser.setClusterId(userVO.getClusterId());
                componentUser.setComponentTypeCode(userVO.getComponentTypeCode());
                componentUser.setIsDefault(userVO.getIsDefault());
                componentUser.setLabel(userVO.getLabel());
                componentUser.setLabelIp(userVO.getLabelIp());
                componentUser.setUserName(userInfo.getUserName());
                componentUser.setPassword(Base64Util.baseEncode(userInfo.getPassword()));
                addComponentUserList.add(componentUser);
            }
        }
        if (CollectionUtils.isNotEmpty(addComponentUserList)){
            componentUserDao.batchInsert(addComponentUserList);
        }

    }

    public List<ComponentUserVO> getClusterComponentUser(Long clusterId, Integer componentTypeCode,
                                                         Boolean needRefresh,String agentAddress,boolean uic) {
        clusterId = uic?clusterService.getCluster(clusterId).getId():clusterId;
        List<ComponentUser> componentUserList = componentUserDao.getComponentUserByCluster(clusterId,componentTypeCode);
        // 只取数据库数据
        if (!Boolean.TRUE.equals(needRefresh)){
            return groupComponentByLabel(componentUserList);
        }
        // 刷新数据必须地址
        if (StringUtils.isBlank(agentAddress)){
            throw new RdosDefineException("refresh label need address");
        }
        List<DtScriptAgentLabel> dtScriptAgentLabel = getDtScriptAgentLabel(agentAddress);
        if (CollectionUtils.isEmpty(componentUserList)){
            return setDefaultComponentLabel(notDbComponentUser(dtScriptAgentLabel, clusterId, componentTypeCode));
        }
        // 以最新label数据为主
        Map<String,DtScriptAgentLabel> labelMap = dtScriptAgentLabel.stream().collect(Collectors.toMap(DtScriptAgentLabel::getLabel,label->label));
        List<ComponentUserVO> filterList = groupComponentByLabel(componentUserList.stream()
                .filter(componentUser -> labelMap.containsKey(componentUser.getLabel())).collect(Collectors.toList()));
        filterList.forEach(user->user.setLabelIp(labelMap.get(user.getLabel()).getLocalIp()));
        if (labelMap.size() == filterList.size()){
            return filterList;
        }
        Set<String> dbLabel = componentUserList.stream().map(ComponentUser::getLabel).collect(Collectors.toSet());
        List<DtScriptAgentLabel> lastLabelList = dtScriptAgentLabel.stream().filter(label -> !dbLabel.contains(label.getLabel())).collect(Collectors.toList());
        filterList.addAll(notDbComponentUser(lastLabelList,clusterId,componentTypeCode));
        return setDefaultComponentLabel(filterList);
    }

    private List<ComponentUserVO> groupComponentByLabel(List<ComponentUser> componentUserList) {
        Map<String, List<ComponentUser>> labelMap =
                componentUserList.stream().collect(Collectors.groupingBy(ComponentUser::getLabel));
        List<ComponentUserVO> componentUserVOList = new ArrayList<>(labelMap.size());
        for (Map.Entry<String, List<ComponentUser>> entry : labelMap.entrySet()) {
            ComponentUserVO componentUserVO = new ComponentUserVO();
            componentUserVO.setLabel(entry.getKey());
            List<ComponentUser> componentUsers = entry.getValue();
            ComponentUser componentUser = componentUsers.get(0);
            componentUserVO.setLabelIp(componentUser.getLabelIp());
            componentUserVO.setComponentTypeCode(componentUser.getComponentTypeCode());
            componentUserVO.setClusterId(componentUser.getClusterId());
            componentUserVO.setIsDefault(componentUser.getIsDefault());
            List<ComponentUserVO.ComponentUserInfo> componentUserInfoList = new ArrayList<>(componentUsers.size());
            componentUsers.forEach(user -> {
                if (StringUtils.isNoneBlank(user.getUserName(),user.getPassword())) {
                    componentUserInfoList.add(new ComponentUserVO.ComponentUserInfo(user.getUserName(),Base64Util.baseDecode(user.getPassword())));
                }
            });
            componentUserVO.setComponentUserInfoList(CollectionUtils.isEmpty(componentUserInfoList)?null:componentUserInfoList);
            componentUserVOList.add(componentUserVO);
        }
        return componentUserVOList;
    }


    public ComponentUser getComponentUser(Long dtUicId,Integer componentTypeCode,String label,String userName){
        Cluster cluster = clusterService.getCluster(dtUicId);
        return componentUserDao.getComponentUser(cluster.getId(),componentTypeCode,label,userName);
    }


    private List<ComponentUserVO> notDbComponentUser(List<DtScriptAgentLabel> dtScriptAgentLabel,Long clusterId,Integer componentTypeCode){
        List<ComponentUserVO> componentUserVOList = new ArrayList<>(dtScriptAgentLabel.size());
        for (DtScriptAgentLabel agentLabel : dtScriptAgentLabel) {
            ComponentUserVO componentUserVO = new ComponentUserVO();
            componentUserVO.setLabel(agentLabel.getLabel());
            componentUserVO.setLabelIp(agentLabel.getLocalIp());
            componentUserVO.setClusterId(clusterId);
            componentUserVO.setComponentTypeCode(componentTypeCode);
            componentUserVOList.add(componentUserVO);
        }
        return componentUserVOList;
    }

    private List<ComponentUserVO> setDefaultComponentLabel(List<ComponentUserVO> componentUserVOList){
        // 存在默认
        boolean hasDefault = componentUserVOList.stream().anyMatch(label->Boolean.TRUE.equals(label.getIsDefault()));
        for (int i = 0; i < componentUserVOList.size(); i++) {
            // 存在默认，其他设置为非默认
            if (hasDefault && Objects.isNull(componentUserVOList.get(i).getIsDefault())){
                componentUserVOList.get(i).setIsDefault(false);
            }
            // 不存在默认，第一个设置默认
            else if (!hasDefault && i==0){
                componentUserVOList.get(0).setIsDefault(true);
            } else if (Objects.isNull(componentUserVOList.get(i).getIsDefault())){
                componentUserVOList.get(i).setIsDefault(false);
            }
        }
        return componentUserVOList;
    }

    public List<Component> listComponents(Long dtUicTenantId, Integer engineType) {
        Tenant tenant = tenantDao.getByDtUicTenantId(dtUicTenantId);
        if (null == tenant) {
            return new ArrayList<>(0);
        }
        if (null != engineType) {
            List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenant.getId());
            if(CollectionUtils.isEmpty(engineIds)){
                return new ArrayList<>(0);
            }
            Engine engine = engineDao.getEngineByIdsAndType(engineIds, engineType);
            if (null == engine) {
                return new ArrayList<>(0);
            }
            return componentDao.listByEngineIds(Lists.newArrayList(engine.getId()), null);
        } else {
            return componentDao.listByTenantId(tenant.getId());

        }
    }
}
