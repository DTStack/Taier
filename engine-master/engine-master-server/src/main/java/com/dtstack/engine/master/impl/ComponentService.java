package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
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
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.MultiEngineType;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.ComponentConfigUtils;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.router.cache.RdosSubscribe;
import com.dtstack.engine.master.router.cache.RdosTopic;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.utils.XmlFileUtil;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.util.Xml2JsonUtil;
import com.dtstack.schedule.common.util.ZipUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
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

    private static final String HADOOP3_SIGNAL = "hadoop3";

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

    public static final String VERSION = "version";

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = new HashMap<>(2);

    private static ThreadPoolExecutor connectPool = new ThreadPoolExecutor(5, 5,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
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
            }
        });
    }

    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId, Integer engineType) {

        List<ComponentsConfigOfComponentsVO> componentsVOS = Lists.newArrayList();
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null) {
            return componentsVOS;
        }
        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return componentsVOS;
        }
        Engine targetEngine = engineDao.getEngineByIdsAndType(engineIds,engineType);
        if(null == targetEngine){
            return componentsVOS;
        }
        List<Component> componentList = componentDao.listByEngineId(targetEngine.getId());
        for (Component component : componentList) {
            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(component.getComponentTypeCode());
            String componentConfig = getComponentByClusterId(targetEngine.getClusterId(), component.getComponentTypeCode(), false, String.class);

            componentsConfigOfComponentsVO.setComponentConfig(componentConfig);
            componentsVOS.add(componentsConfigOfComponentsVO);
        }
        return componentsVOS;
    }

    public Component getOne(Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException("组件不存在");
        }
        return component;
    }

    public String getSftpClusterKey(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE.name() + "_" + one.getClusterName();
    }


    /**
     * 更新缓存
     */
    public void updateCache(Long engineId, Integer componentCode) {
        clearComponentCache();
        Set<Long> dtUicTenantIds = new HashSet<>();
        if ( null != componentCode && EComponentType.sqlComponent.contains(EComponentType.getByCode(componentCode))) {
            //tidb 和libra 没有queue
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
        //缓存刷新
        if (!dtUicTenantIds.isEmpty()) {
            for (Long uicTenantId : dtUicTenantIds) {
                consoleCache.publishRemoveMessage(uicTenantId.toString());
            }
        }
    }

    public List<Component> listComponent(Long engineId) {
        return componentDao.listByEngineId(engineId);
    }

    private Map<String, Map<String,Object>> parseUploadFileToMap(List<Resource> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("上传的文件不能为空");
        }

        Resource resource = resources.get(0);
        if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("压缩包格式仅支持ZIP格式");
        }

        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            Map<String, Map<String,Object>> confMap = new HashMap<>();
            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            if(CollectionUtils.isEmpty(xmlFiles)){
                throw new RdosDefineException("配置文件不能为空");
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
            throw new RdosDefineException("文件后缀不能为空");
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
                throw new RdosDefineException("解析keytab文件失败");
            }
            return keytab.getPrincipals();
        }
        throw new RdosDefineException("当前keytab文件不包含principal信息");
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


    public KerberosConfig getKerberosConfig( Long clusterId,  Integer componentType) {
        return kerberosDao.getByComponentType(clusterId, componentType);
    }


    @Transactional(rollbackFor = Exception.class)
    public String uploadKerberos(List<Resource> resources, Long clusterId, Integer componentCode) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new RdosDefineException("请上传kerberos文件！");
        }

        Resource resource = resources.get(0);
        String kerberosFileName = resource.getFileName();
        if (!kerberosFileName.endsWith(ZIP_SUFFIX)) {
            throw new RdosDefineException("kerberos上传文件非zip格式");
        }

        String sftpComponent = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class);
        SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, componentCode);
        Component addComponent = new ComponentDTO();
        addComponent.setComponentTypeCode(componentCode);
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
                    String sftpComponent = getComponentByClusterId(clusterId,EComponentType.SFTP.getTypeCode(),false,String.class);
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
                                             String principals, String principal) {
        if (StringUtils.isBlank(componentConfig)) {
            componentConfig = new JSONObject().toJSONString();
        }
        if (null == componentCode) {
            throw new RdosDefineException("组件类型不能为空");
        }
        if (null == clusterId) {
            throw new RdosDefineException("集群Id不能为空");
        }
        if (CollectionUtils.isNotEmpty(resources) && resources.size() >= 2 && StringUtils.isBlank(kerberosFileName)) {
            //上传二份文件 需要kerberosFileName文件名字段
            throw new RdosDefineException("kerberosFileName不能为空");
        }
        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentTypeCode(componentCode);
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        String clusterName = cluster.getClusterName();
        //校验引擎是否添加
        EComponentType componentType = EComponentType.getByCode(componentDTO.getComponentTypeCode());
        MultiEngineType engineType = EComponentType.getEngineTypeByComponent(componentType);
        Engine engine = this.addEngineWithCheck(clusterId, engineType);
        if (null == engine) {
            throw new RdosDefineException("引擎不能为空");
        }
        this.checkSchedulesComponent(clusterId, componentCode);

        //判断是否是更新组件
        Component addComponent = new ComponentDTO();
        BeanUtils.copyProperties(componentDTO, addComponent);

        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        boolean isUpdate = false;
        boolean isOpenKerberos = isOpenKerberos(kerberosFileName, dbComponent);
        if (null != dbComponent) {
            //更新
            addComponent = dbComponent;
            isUpdate = true;
        }
        componentConfig = this.checkKubernetesConfig(componentConfig, resources, componentType);

        EComponentType storesComponent = this.checkStoresComponent(clusterId, storeType);
        addComponent.setStoreType(storesComponent.getTypeCode());
        addComponent.setHadoopVersion(convertHadoopVersionToValue(Optional.ofNullable(hadoopVersion).orElse("Hadoop 2.x")));
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setEngineId(engine.getId());

        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }

        String md5Key = "";
        //将componentTemplate的值放入componentConfig
        boolean removeSelfParams = EComponentType.HDFS.getTypeCode().equals(addComponent.getComponentTypeCode())
                || EComponentType.YARN.getTypeCode().equals(addComponent.getComponentTypeCode());
        if(componentTemplate!=null && removeSelfParams){
            JSONObject componentConfigJbj = JSONObject.parseObject(componentConfig);
            JSONArray jsonArray = JSONObject.parseArray(componentTemplate);
            for (Object o : jsonArray.toArray()) {
                String key = ((JSONObject) o).getString("key");
                String value = ((JSONObject) o).getString("value");
                componentConfigJbj.put(key,value);
            }
            componentConfig = JSON.toJSONString(componentConfigJbj);
        }

        md5Key = updateResource(clusterId, componentConfig, resources, kerberosFileName, componentCode, principals, principal, addComponent, dbComponent, md5Key);
        addComponent.setClusterId(clusterId);
        if (isUpdate) {
            componentDao.update(addComponent);
            clusterDao.updateGmtModified(clusterId);
        } else {
            componentDao.insert(addComponent);
        }
        List<ClientTemplate> clientTemplates = this.wrapperConfig(componentType, componentConfig, isOpenKerberos, clusterName, hadoopVersion, md5Key, componentTemplate,addComponent.getHadoopVersion(),addComponent.getStoreType());
        componentConfigService.addOrUpdateComponentConfig(clientTemplates, addComponent.getId(), addComponent.getClusterId(), componentCode);
        List<ComponentVO> componentVos = componentConfigService.getComponentVoByComponent(Lists.newArrayList(addComponent), true, clusterId,true);
        this.updateCache(engine.getId(), componentType.getTypeCode());
        if (CollectionUtils.isNotEmpty(componentVos)) {
            ComponentVO componentVO = componentVos.get(0);
            componentVO.setClusterName(clusterName);
            componentVO.setPrincipal(principal);
            componentVO.setPrincipals(principals);
            return componentVO;
        }
        return null;
    }

    /**
     *
     * @param hadoopVersion
     * @return
     */
    private String convertHadoopVersionToValue(String hadoopVersion) {
        ScheduleDict dict = scheduleDictService.getByNameAndValue(DictType.HADOOP_VERSION.type, hadoopVersion, null,null);
        if (null != dict) {
            return dict.getDictValue();
        }
        return hadoopVersion;
    }

    private String updateResource(Long clusterId, String componentConfig, List<Resource> resources, String kerberosFileName, Integer componentCode, String principals, String principal, Component addComponent, Component dbComponent, String md5Key) {
        //上传资源依赖sftp组件
        if (CollectionUtils.isNotEmpty(resources)) {
            String sftpConfigStr = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class);
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpConfigStr, componentCode, componentConfig);
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent, principals, principal);
        } else if (CollectionUtils.isEmpty(resources) && StringUtils.isNotBlank(principal)) {
            //直接更新认证信息
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode());
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
                KerberosConfig componentKerberos = kerberosDao.getByComponentType(dbComponent.getId(), dbComponent.getComponentTypeCode());
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
        Component storeComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        if(null == storeComponent){
            throw new RdosDefineException(String.format("请先配置对应%s组件",componentType.getName()));
        }
        return componentType;
    }

    private void checkSchedulesComponent(Long clusterId, Integer componentCode) {
        //yarn 和 Kubernetes 只能2选一
        if (EComponentType.YARN.getTypeCode().equals(componentCode) || EComponentType.KUBERNETES.getTypeCode().equals(componentCode)) {
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId,
                    EComponentType.YARN.getTypeCode().equals(componentCode) ? EComponentType.KUBERNETES.getTypeCode() : EComponentType.YARN.getTypeCode());
            if (Objects.nonNull(resourceComponent)) {
                throw new RdosDefineException("调度组件只能选择单项");
            }
        }
    }

    private Engine addEngineWithCheck(Long clusterId, MultiEngineType engineType) {
        if (null == engineType) {
            //如果是hdfs 组件 需要先确定调度组件为 yarn 还是k8s
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode());
            if (null == resourceComponent) {
                resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.KUBERNETES.getTypeCode());
            }
            if (null == resourceComponent) {
                throw new RdosDefineException("请先配置调度组件");
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
                throw new RdosDefineException("请先配置sftp服务器在上传文件!");
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
                    throw new RdosDefineException("更新组件失败");
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
            throw new RdosDefineException("集群不存在");
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
    private List<ClientTemplate> wrapperConfig(EComponentType componentType, String componentString, boolean isOpenKerberos, String clusterName, String hadoopVersion, String md5Key, String clientTemplates,String convertHadoopVersion,Integer storeType) {
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
            ClientTemplate typeNameClientTemplate = ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, this.convertComponentTypeToClient(clusterName, componentType.getTypeCode(), convertHadoopVersion,storeType));
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
                throw new RdosDefineException("Hadoop-Kerberos文件解压错误");
            }

            keyTabFile = files.stream().filter(f -> f.getName().endsWith(KEYTAB_SUFFIX)).findFirst().orElse(null);
            krb5ConfFile = files.stream().filter(f -> f.getName().equalsIgnoreCase(KRB5_CONF)).findFirst().orElse(null);
            if (keyTabFile == null) {
                throw new RdosDefineException("上传的Hadoop-Kerberos文件的zip文件中必须有keytab文件，请添加keytab文件");
            }
            LOGGER.info("fileKeyTab Unzip fileName:{}",keyTabFile.getAbsolutePath());
            if (krb5ConfFile == null) {
                throw new RdosDefineException("上传的Hadoop-Kerberos文件的zip文件中必须有krb5.conf文件，请添加krb5.conf文件");
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

        //更新数据库kerberos信息
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode());
        boolean isFirstOpenKerberos = false;
        if (Objects.isNull(kerberosConfig)) {
            kerberosConfig = new KerberosConfig();
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
            throw new RdosDefineException("keytab文件中不包含principal");
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
                throw new RdosDefineException(String.format("上传的Hadoop-Kerberos文件的不包含对应的 %s", principal));
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
            kerberosDao.deleteByComponentId(componentId);
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
                throw new RdosDefineException("名称过长");
            }
        } else {
            throw new RdosDefineException("集群名称不能为空");
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
        throw new RdosDefineException("集群名称已存在");
    }


    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    public List<Object> config(List<Resource> resources,  Integer componentType, Boolean autoDelete,String version) {

        try {
            //解析xml文件
            List<String> xmlName = componentTypeConfigMapping.get(componentType);
            if (CollectionUtils.isNotEmpty(xmlName)) {
                return parseXmlFileConfig(resources, xmlName);
            }

            //解析k8s组件
            if(EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
                return parseKubernetesData(resources);
            }

            List<Object> datas = new ArrayList<>();
            // 当作json来解析
            for (Resource resource : resources) {
                try {
                    String fileInfo = FileUtils.readFileToString(new File(resource.getUploadedFileName()));
                    datas.add(PublicUtil.strToMap(fileInfo));
                } catch (Exception e) {
                    LOGGER.error("parse json config resource error {} ", resource.getUploadedFileName());
                    throw new RdosDefineException("JSON文件格式错误");
                }
            }
            return datas;
        } finally {
            if (null == autoDelete || true == autoDelete) {
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

    private List<Object> parseXmlFileConfig(List<Resource> resources, List<String> xmlName) {
        List<Object> datas = new ArrayList<>();
        Map<String, Map<String,Object>> xmlConfigMap = this.parseUploadFileToMap(resources);
        boolean isLostXmlFile = xmlConfigMap.keySet().containsAll(xmlName);
        if(!isLostXmlFile){
            throw new RdosDefineException("缺少 必要 配置文件");
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
        if( null == one ){
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name();
    }


    /**
     * 测试单个组件联通性
     */
    public ComponentTestResult testConnect(Integer componentType, String componentConfig, String clusterName,
                                           String hadoopVersion, Long engineId, KerberosConfig kerberosConfig, Map<String, String> sftpConfig) {
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
                pluginType = this.convertComponentTypeToClient(clusterName, componentType, hadoopVersion,storeType);
            }

            componentTestResult = workerOperator.testConnect(pluginType,
                    this.wrapperConfig(componentType, componentConfig, sftpConfig, kerberosConfig, clusterName));
            if (null == componentTestResult) {
                componentTestResult = new ComponentTestResult();
                componentTestResult.setResult(false);
                componentTestResult.setErrorMsg("测试联通性失败");
                return componentTestResult;
            }

            if (componentTestResult.getResult() && null != engineId) {
                updateCache(engineId, componentType);
            }
        } finally {
            if (null != componentTestResult) {
                componentTestResult.setComponentTypeCode(componentType);
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
        }
        return dataInfo.toJSONString();
    }

    private JSONObject buildSQLComponentConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig, String clusterName) {
        JSONObject dataInfo;
        dataInfo = JSONObject.parseObject(componentConfig);
        dataInfo.put(EComponentType.SFTP.getConfName(), sftpConfig);
        String jdbcUrl = dataInfo.getString("jdbcUrl");
        if (StringUtils.isBlank(jdbcUrl)) {
            throw new RdosDefineException("jdbcUrl不能为空");
        }

        if (EComponentType.SPARK_THRIFT.getTypeCode() == componentType ||
                EComponentType.HIVE_SERVER.getTypeCode() == componentType) {
            //数据库连接不带%s
            String replaceStr = "/";
            if (null != kerberosConfig) {
                replaceStr = env.getComponentJdbcToReplace();
            }
            jdbcUrl = jdbcUrl.replace("/%s", replaceStr);
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
            Map yarnMap = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(), false, Map.class);
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
            throw new RdosDefineException("集群不存在");
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
                              String hadoopVersion,  String clusterName) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (null == componentId) {
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(componentType, clusterName, hadoopVersion,null);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                Map<String, Object> fileMap = ComponentConfigUtils.convertClientTemplateToMap(clientTemplates);
                uploadFileName = EComponentType.getByCode(componentType).name() + ".json";
                localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
                try {
                    FileUtils.write(new File(localDownLoadPath), JSONObject.toJSONString(fileMap));
                } catch (Exception e) {
                    throw new RdosDefineException("文件不存在");
                }
            }
        } else {
            Component component = componentDao.getOne(componentId);
            if (null == component) {
                throw new RdosDefineException("组件不存在");
            }
            Long clusterId = componentDao.getClusterIdByComponentId(componentId);
            SftpConfig sftpConfig = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),false,SftpConfig.class);
            if ( null == sftpConfig ) {
                throw new RdosDefineException("sftp组件不存在");
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
                    String componentConfig = getComponentByClusterId(clusterId,EComponentType.getByCode(componentType).getTypeCode(),true,String.class);
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
            throw new RdosDefineException("文件不存在");
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
                KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType);
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
     * @param componentType
     * @return
     */
    public List<ClientTemplate> loadTemplate(Integer componentType, String clusterName, String version, Integer storeType) {
        EComponentType component = EComponentType.getByCode(componentType);
        if(EComponentType.noControlComponents.contains(component)){
            return new ArrayList<>(0);
        }
        String typeName = convertComponentTypeToClient(clusterName, componentType, version,storeType);
        List<ComponentConfig> componentConfigs = componentConfigService.loadDefaultTemplate(typeName);

        ClusterVO clusterByName = clusterService.getClusterByName(clusterName);
        Component yarnComponent = componentDao.getByClusterIdAndComponentType(clusterByName.getClusterId(), EComponentType.YARN.getTypeCode());
        List<ComponentConfig> extraConfig = null;
        if (null != yarnComponent) {
            ComponentConfig originHadoopVersion = componentConfigService.getComponentConfigByKey(yarnComponent.getId(), HADOOP_VERSION);
            String yarnVersion = null == originHadoopVersion ? yarnComponent.getHadoopVersion() : originHadoopVersion.getValue();
            //根据版本添加对于的额外配置 需要根据yarn的版本来
            extraConfig = scheduleDictService.loadExtraComponentConfig(yarnVersion, componentType);
        }
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
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String version, Integer storeType) {
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
        //hive 特殊处理 version
        if (EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) || EComponentType.SPARK_THRIFT.getTypeCode().equals(componentType)) {
            pluginName = "hive";
            if (version.equalsIgnoreCase("1.x")) {

            } else if (version.equalsIgnoreCase("2.x")) {
                pluginName = pluginName + version.charAt(0);
            } else {
                //其他为完整路径
                pluginName = pluginName + version;
            }
            return pluginName;
        }

        //调度或存储单个组件
        if (EComponentType.NFS.equals(componentCode) || EComponentType.ResourceScheduling.contains(componentCode)) {
            return String.format("%s%s", componentCode.name().toLowerCase(), this.formatHadoopVersion(version, componentCode));
        }

        ClusterVO cluster = clusterService.getClusterByName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }

        //需要按照 调度-存储-计算 拼接的typeName
        String computeSign = EComponentType.convertPluginNameWithNeedVersion(componentCode);
        if (StringUtils.isBlank(computeSign)) {
            throw new RdosDefineException("不支持的组件");
        }

        Component yarn = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
        Component kubernetes = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.KUBERNETES.getTypeCode());
        if (null == yarn && null == kubernetes) {
            throw new RdosDefineException("请先配置调度组件");
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
                    Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
                    if (null == hdfs) {
                        throw new RdosDefineException("请先配置存储组件");
                    }
                    return EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(hdfs.getHadoopVersion(), EComponentType.HDFS);
                }
            }
        } else {
            //hdfs和nfs可以共存 hdfs为默认
            Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
            Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode());
            if (null == hdfs && null == nfs) {
                throw new RdosDefineException("请先配置存储组件");
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
        for (Integer componentId : componentIds) {
            Component component = componentDao.getOne(componentId.longValue());
            EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());

            if (EComponentType.requireComponent.contains(EComponentType.getByCode(component.getComponentTypeCode()))){
                throw new RdosDefineException(component.getComponentName() + " 是必选组件，不可删除");
            }
            component.setIsDeleted(Deleted.DELETED.getStatus());
            componentDao.deleteById(componentId.longValue());
            kerberosDao.deleteByComponentId(componentId.longValue());
            componentConfigService.deleteComponentConfig(componentId.longValue());
        }
    }


    /***
     * 获取对应的组件版本信息
     * @return
     */
    public Map getComponentVersion() {
        return scheduleDictService.getVersion();
    }

    public Component getComponentByClusterId(Long clusterId, Integer componentType) {
        return componentDao.getByClusterIdAndComponentType(clusterId, componentType);
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
    public <T> T getComponentByClusterId(Long clusterId, Integer componentType, boolean isFilter, Class<T> clazz) {
        Map<String, Object> configMap = getCacheComponentConfigMap(clusterId, componentType, isFilter);
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

    @Cacheable(cacheNames = "component")
    public Map<String, Object> getCacheComponentConfigMap(Long clusterId, Integer componentType, boolean isFilter) {
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, componentType);
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
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);

        List<Component> components = getComponents(cluster);
        if (CollectionUtils.isEmpty(components)) {
            return refreshResults;
        }

        Map<String, String> sftpMap = getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class);
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        for (Component component : components) {
            if (!EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
                continue;
            }
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(
                    cluster.getId(), component.getComponentTypeCode());
            try {
                CompletableFuture.runAsync(() -> {
                    ComponentTestResult refreshResult = new ComponentTestResult();
                    String componentConfig = getComponentByClusterId(cluster.getId(),component.getComponentTypeCode(),false,String.class);
                    try {
                        refreshResult = this.testConnect(component.getComponentTypeCode(),
                                componentConfig, clusterName, component.getHadoopVersion(),
                                component.getEngineId(), kerberosConfig, sftpMap,component.getStoreType());

                        if (refreshResult.getResult() && EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
                            engineService.updateResource(component.getEngineId(), refreshResult.getClusterResourceDescription());
                            queueService.updateQueue(component.getEngineId(), refreshResult.getClusterResourceDescription());
                        }

                    } catch (Exception e) {
                        refreshResult.setResult(false);
                        refreshResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                        LOGGER.error("refresh {}  error ", componentConfig, e);
                    } finally {
                        refreshResult.setComponentTypeCode(component.getComponentTypeCode());
                        refreshResults.add(refreshResult);
                        countDownLatch.countDown();
                    }
                }, connectPool).get(env.getTestConnectTimeout(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("refresh {}  e ", component.getId(), e);
            }
        }
        return refreshResults;
    }

    public ComponentTestResult testConnect(String clusterName, Integer componentType) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }
        Component testComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), componentType);
        if (null == testComponent) {
            throw new RdosDefineException("该组件不存在");
        }
        if (EComponentType.notCheckComponent.contains(EComponentType.getByCode(componentType))) {
            ComponentTestResult componentTestResult = new ComponentTestResult();
            componentTestResult.setComponentTypeCode(componentType);
            componentTestResult.setResult(true);
            return componentTestResult;
        }
        String componentConfig = getComponentByClusterId(cluster.getId(), componentType, false, String.class);
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), componentType);
        Map sftpMap = getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class);
        return testConnect(componentType, componentConfig, clusterName, testComponent.getHadoopVersion(), testComponent.getEngineId(), kerberosConfig, sftpMap,testComponent.getStoreType());
    }

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> testConnects(String clusterName) {

        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterDao.getByClusterName(clusterName);

        List<Component> components = getComponents(cluster);

        if(CollectionUtils.isEmpty(components)){
            return new ArrayList<>();
        }
        Map sftpMap = getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class);
        List<ComponentTestResult> testResults = new ArrayList<>(components.size());
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        for (Component component : components) {
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), component.getComponentTypeCode());
            try {
                CompletableFuture.runAsync(() -> {
                    String componentConfig = getComponentByClusterId(cluster.getId(), component.getComponentTypeCode(), false, String.class);
                    ComponentTestResult testResult = new ComponentTestResult();
                    try {
                        testResult = this.testConnect(component.getComponentTypeCode(), componentConfig, clusterName, component.getHadoopVersion(), component.getEngineId(), kerberosConfig, sftpMap,component.getStoreType());
                        //测试联通性
                        if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
                            if (testResult.getResult()) {
                                if (null != testResult.getClusterResourceDescription()) {
                                    engineService.updateResource(component.getEngineId(), testResult.getClusterResourceDescription());
                                    queueService.updateQueue(component.getEngineId(), testResult.getClusterResourceDescription());
                                } else {
                                    testResult.setResult(false);
                                    testResult.setErrorMsg(clusterName + "获取yarn信息为空");
                                }
                            }
                        }
                    } catch (Exception e) {
                        testResult.setResult(false);
                        testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                        LOGGER.error("test connect {}  error ", componentConfig, e);
                    } finally {
                        testResult.setComponentTypeCode(component.getComponentTypeCode());
                        testResults.add(testResult);
                        countDownLatch.countDown();
                    }
                }, connectPool).get(env.getTestConnectTimeout(), TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("test connect {}  e ",component.getId(), e);
                countDownLatch.countDown();
                ComponentTestResult testResult = new ComponentTestResult();
                testResult.setResult(false);
                testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                testResult.setComponentTypeCode(component.getComponentTypeCode());
                testResults.add(testResult);
            }
        }
        try {
            countDownLatch.await(env.getTestConnectTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("test connect  await {}  error ", clusterName, e);
        }
        return testResults;
    }

    private List<Component> getComponents(Cluster cluster) {

        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }
        List<Engine> engines = engineDao.listByClusterId(cluster.getId());
        if (CollectionUtils.isEmpty(engines)) {
            return new ArrayList<>(0);
        }
        List<Long> engineId = engines.stream().map(Engine::getId).collect(Collectors.toList());

        List<Component> components = componentDao.listByEngineIds(engineId);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        return components;
    }


    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }
        List<Component> components = new ArrayList<>();
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
        if (null != hdfs) {
            /*//将componentConfig中的componentTemplate内容过滤掉
            String componentTemplate = hdfs.getComponentTemplate();
            String componentConfig = hdfs.getComponentConfig();
            JSONObject configJbj = JSONObject.parseObject(componentConfig);
            if(null != componentTemplate){
                JSONArray jsonArray = JSONObject.parseArray(componentTemplate);
                for (Object o : jsonArray.toArray()) {
                    String key = ((JSONObject) o).getString("key");
                    String value = ((JSONObject) o).getString("value");
                    configJbj.remove(key,value);
                }
            }
            componentConfig = JSON.toJSONString(configJbj);
            hdfs.setComponentConfig(componentConfig);
            //将componentTemplate设为null
            hdfs.setComponentTemplate(null);*/
            components.add(hdfs);
        }
        Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode());
        if (null != nfs) {
            components.add(nfs);
        }
        return components;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long addOrUpdateNamespaces(Long clusterId, String namespace, Long queueId, Long dtUicTenantId) {
        if (StringUtils.isBlank(namespace)) {
            throw new RdosDefineException("namespace不能为空");
        }
        Cluster cluster = clusterDao.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException("集群为空");
        }
        Component component = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.KUBERNETES.getTypeCode());
        if (null == component) {
            throw new RdosDefineException("kubernetes 组件为空");
        }
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, MultiEngineType.HADOOP.getType());
        if (null == engine) {
            throw new RdosDefineException("引擎为空");
        }
        String clusterName = cluster.getClusterName();
        String pluginType = this.convertComponentTypeToClient(clusterName, EComponentType.KUBERNETES.getTypeCode(), "", null);
        Map sftpMap = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),false,Map.class);
        if (sftpMap == null) {
            throw new RdosDefineException("sftp配置为空");
        }
        String componentConfig = getComponentByClusterId(clusterId, EComponentType.KUBERNETES.getTypeCode(),false,String.class);
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
                throw new RdosDefineException("操作失败");
            }
            if (null == queueId) {
                Tenant tenant = tenantDao.getByDtUicTenantId(dtUicTenantId);
                if (null == tenant) {
                    throw new RdosDefineException("租户不存在");
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
        Component yarnComponent = consoleService.getYarnComponent(cluster.getId());

        if (yarnComponent == null) {
            return false;
        }
        if (!HADOOP3_SIGNAL.equals(yarnComponent.getHadoopVersion())) {
            return false;
        }
        JSONObject yarnConf = getComponentByClusterId(cluster.getId(), EComponentType.YARN.getTypeCode(),false,JSONObject.class);
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
                throw new RdosDefineException("Hadoop-Kerberos文件解压错误");
            }

            File fileKeyTab = files
                    .stream()
                    .filter(f -> f.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst()
                    .orElse(null);
            if (fileKeyTab == null) {
                throw new RdosDefineException("上传的Hadoop-Kerberos文件的zip文件中必须有keytab文件，请添加keytab文件");
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
}
