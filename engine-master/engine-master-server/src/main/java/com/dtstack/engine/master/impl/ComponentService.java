package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.domain.*;
import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.dto.ComponentDTO;
import com.dtstack.engine.api.dto.Resource;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.vo.ClusterVO;
import com.dtstack.engine.api.vo.ComponentVO;
import com.dtstack.engine.api.vo.EngineTenantVO;
import com.dtstack.engine.api.vo.Pair;
import com.dtstack.engine.api.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.api.vo.components.ComponentsResultVO;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.XmlFileUtil;
import com.dtstack.schedule.common.enums.AppType;
import com.dtstack.schedule.common.enums.Deleted;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.common.constrant.ConfigConstant.*;

@Service
public class ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

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

    public static final String TYPE_NAME = "typeName";
    public static final String VERSION = "version";

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = new HashMap<>(2);

    public static Map<String, List<Pair<String,String>>> componentVersionMapping = new HashMap<>(1);

    private static ThreadPoolExecutor connectPool =  new ThreadPoolExecutor(5, 5,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
            new CustomThreadFactory("connectPool"));

    static {
        //hdfs core 需要合并
        componentTypeConfigMapping.put(EComponentType.HDFS.getTypeCode(), Lists.newArrayList("hdfs-site.xml", "core-site.xml","hive-site.xml"));
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList("yarn-site.xml","core-site.xml"));

        componentVersionMapping.put(EComponentType.FLINK.getName(), Lists.newArrayList(new Pair<>("1.8", "180"), new Pair<>("1.10", "110")));
        componentVersionMapping.put(EComponentType.SPARK.getName(), Lists.newArrayList(new Pair<>("2.1.X", "210"), new Pair<>("2.4.X", "240")));
        componentVersionMapping.put(EComponentType.HDFS.getName(), Lists.newArrayList(new Pair<>("hadoop2", "hadoop2"), new Pair<>("hadoop3", "hadoop3")));
        componentVersionMapping.put(EComponentType.YARN.getName(), Lists.newArrayList(new Pair<>("hadoop2", "hadoop2"), new Pair<>("hadoop3", "hadoop3")));
        componentVersionMapping.put(EComponentType.SPARK_THRIFT.getName(), Lists.newArrayList(new Pair<>("1.X", "1.x"), new Pair<>("2.X", "2.x"),new Pair<>("2.1.1-cdh6.1.1","2.1.1-cdh6.1.1")));
        componentVersionMapping.put(EComponentType.HIVE_SERVER.getName(), Lists.newArrayList(new Pair<>("1.X", "1.x"), new Pair<>("2.X", "2.x"),new Pair<>("2.1.1-cdh6.1.1","2.1.1-cdh6.1.1")));
        //-1 为hadoopversion
        componentVersionMapping.put("hadoopVersion", Lists.newArrayList(new Pair<>("hadoop2", "hadoop2"),
                new Pair<>("hadoop3", "hadoop3"), new Pair<>("HW", "HW")));
    }

    /**
     * {
     * "1":{
     * "xx":"xx"
     * }
     * }
     */
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

        List<Engine> engines = engineDao.listByEngineIds(engineIds);
        if (CollectionUtils.isEmpty(engines)) {
            return componentsVOS;
        }

        Engine targetEngine = null;
        for (Engine engine : engines) {
            if (engine.getEngineType() == engineType) {
                targetEngine = engine;
                break;
            }
        }

        if (targetEngine == null) {
            return componentsVOS;
        }

        List<Component> componentList = componentDao.listByEngineId(targetEngine.getId());
        for (Component component : componentList) {
            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(component.getComponentTypeCode());
            componentsConfigOfComponentsVO.setComponentConfig(component.getComponentConfig());
            componentsVOS.add(componentsConfigOfComponentsVO);
        }
        return componentsVOS;
    }

    public Component getOne( Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException("组件不存在");
        }
        return component;
    }

    public String getSftpClusterKey(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if(Objects.isNull(one)){
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE.name() + "_" + one.getClusterName();
    }

    /**
     * 更新缓存
     */
    public void updateCache(Long engineId, Integer componentCode) {
        Set<Long> dtUicTenantIds = new HashSet<>();
        if (Objects.nonNull(componentCode) && EComponentType.sqlComponent.contains(EComponentType.getByCode(componentCode))) {
            //tidb 和libra 没有queue
            List<EngineTenantVO> tenantVOS = engineTenantDao.listEngineTenant(engineId);
            if (CollectionUtils.isNotEmpty(tenantVOS)) {
                for (EngineTenantVO tenantVO : tenantVOS) {
                    if (Objects.nonNull(tenantVO) && Objects.nonNull(tenantVO.getTenantId())) {
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

    public void addComponentWithConfig(Long engineId, String confName, JSONObject config) {
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


    private File getDeepFileWithSuffix(String dir, String suffix) {
        //不是单个文件夹
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (files.length > 0) {
                for (File file : files) {
                    File subFile = this.getFileWithSuffix(file.getPath(), suffix);
                    if (Objects.nonNull(subFile)) {
                        return subFile;
                    }
                }
            }
        }
        return null;
    }

    private String getPrincipal(File file) {
        if (Objects.nonNull(file)) {
            Keytab keytab = null;
            try {
                keytab = Keytab.loadKeytab(file);
            } catch (IOException e) {
                LOGGER.error("Keytab loadKeytab error ", e);
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

    private void unzipKeytab(String localKerberosConf, Resource resource) {
        try {
            KerberosConfigVerify.getFilesFromZip(resource.getUploadedFileName(), localKerberosConf);
        } catch (Exception e) {
            KerberosConfigVerify.delFile(new File(localKerberosConf));
            throw e;
        }
    }


    public KerberosConfig getKerberosConfig( Long clusterId,  Integer componentType) {
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType);
        return kerberosConfig;
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


    @Transactional(rollbackFor = Exception.class)
    public ComponentVO addOrUpdateComponent( Long clusterId,  String componentConfig,
                                             List<Resource> resources,  String hadoopVersion,
                                             String kerberosFileName,  String componentTemplate,
                                             Integer componentCode, Integer storeType) {
        if (StringUtils.isBlank(componentConfig) && !EComponentType.KUBERNETES.getTypeCode().equals(componentCode)) {
            throw new RdosDefineException("组件信息不能为空");
        }
        if (Objects.isNull(componentCode)) {
            throw new RdosDefineException("组件类型不能为空");
        }
        if (Objects.isNull(clusterId)) {
            throw new RdosDefineException("集群Id不能为空");
        }
        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentConfig(componentConfig);
        componentDTO.setComponentTypeCode(componentCode);


        String clusterName = clusterDao.getOne(clusterId).getClusterName();
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
        boolean isOpenKerberos = isOpenKerberos(resources, kerberosFileName, dbComponent);
        if (null != dbComponent) {
            //更新
            addComponent = dbComponent;
            isUpdate = true;
        }
        componentConfig = this.checkKubernetesConfig(componentConfig, resources, componentType);

        EComponentType storesComponent = this.checkStoresComponent(clusterId, storeType);
        addComponent.setStoreType(storesComponent.getTypeCode());
        addComponent.setHadoopVersion(Optional.ofNullable(hadoopVersion).orElse("hadoop2"));
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setEngineId(engine.getId());
        addComponent.setComponentTemplate(componentTemplate);
        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }

        String md5Key = "";

        //上传资源依赖sftp组件
        if (CollectionUtils.isNotEmpty(resources)) {
            Component sftpComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.SFTP.getTypeCode());
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpComponent,componentCode,componentConfig);
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent);
        }
        addComponent.setComponentConfig(this.wrapperConfig(componentType, componentConfig, isOpenKerberos, clusterName, hadoopVersion,md5Key,addComponent.getStoreType()));

        addComponent.setClusterId(clusterId);
        if (isUpdate) {
            componentDao.update(addComponent);
            clusterDao.updateGmtModified(clusterId);
        } else {
            componentDao.insert(addComponent);
        }
        ComponentVO componentVO = ComponentVO.toVO(addComponent, true);
        componentVO.setClusterName(clusterName);
        this.updateCache(engine.getId(),componentType.getTypeCode());
        return componentVO;
    }

    private String checkKubernetesConfig(String componentConfig, List<Resource> resources, EComponentType componentType) {
        if(EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode()) && CollectionUtils.isNotEmpty(resources)){
            //kubernetes 信息需要自己解析文件
            List<Object> config = this.config(resources, EComponentType.KUBERNETES.getTypeCode(),false);
            if(CollectionUtils.isNotEmpty(config)){
                componentConfig = (String)config.get(0);
            }
        }
        return componentConfig;
    }

    private boolean isOpenKerberos(List<Resource> resources, String kerberosFileName, Component dbComponent) {
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (isOpenKerberos) {
            if (!resources.isEmpty() && !kerberosFileName.endsWith(ZIP_SUFFIX)) {
                throw new RdosDefineException("kerberos上传文件非zip格式");
            }
        } else {
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

    public SftpConfig getSFTPConfig(Component sftpComponent, Integer componentCode,String componentConfig) {
        if (sftpComponent == null) {
            //  判断componentCode 是否是sftp的配置，如果是上传文件，如果不是 抛异常返回提交配置sftp服务器
            if (EComponentType.SFTP.getTypeCode().equals(componentCode)) {
                // 是sftp的配置
                return JSONObject.parseObject(componentConfig, SftpConfig.class);
            } else {
                throw new RdosDefineException("请先配置sftp服务器在上传文件!");
            }
        } else {
            return JSONObject.parseObject(sftpComponent.getComponentConfig(), SftpConfig.class);
        }
    }

    private String uploadResourceToSftp(Long clusterId,  List<Resource> resources,  String kerberosFileName,
                                        SftpConfig sftpConfig, Component addComponent, Component dbComponent) {
        //上传配置文件到sftp 供后续下载
        SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
        String md5sum = "";
        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, addComponent.getComponentTypeCode());
        for (Resource resource : resources) {
            if (!resource.getFileName().equalsIgnoreCase(kerberosFileName) || StringUtils.isBlank(kerberosFileName)) {
                addComponent.setUploadFileName(resource.getFileName());
            }
            try {
                if (resource.getFileName().equalsIgnoreCase(kerberosFileName)) {
                    // 更新Kerberos文件
                    LOGGER.info("start upload kerberosFile:{}",kerberosFileName);
                    this.updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource);
                } else {
                    LOGGER.info("start upload hadoop config file:{}",kerberosFileName);
                    this.updateComponentConfigFile(dbComponent, sftpFileManage, remoteDir, resource);
                    if(EComponentType.HDFS.getTypeCode().equals(addComponent.getComponentTypeCode())){
                        String xmlZipLocation = resource.getUploadedFileName();
                        md5sum = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource);
                    }
                    if(EComponentType.YARN.getTypeCode().equals(addComponent.getComponentTypeCode())){
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("update component resource {}  error", resource.getUploadedFileName(), e);
                if (e instanceof RdosDefineException) {
                    throw (RdosDefineException)e;
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
     * @param clusterId
     * @param resource
     */
    private void updateConfigToSftpPath( Long clusterId, SftpConfig sftpConfig, SftpFileManage sftpFileManage, Resource resource) {
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
            if (Objects.isNull(xmlFile)) {
                //包含文件夹目录
                File[] files = localFile.listFiles();
                if (Objects.nonNull(files) && files.length > 0 && files[0].isDirectory()) {
                    dirFiles = files[0];
                }
            } else {
                //直接是文件
                dirFiles = xmlFile.getParentFile();
            }
            if (Objects.nonNull(dirFiles) && null != dirFiles.listFiles()) {
                for (File file : dirFiles.listFiles()) {
                    if (file.getName().contains(".xml")) {
                        sftpFileManage.uploadFile(confRemotePath + buildPath, file.getPath());
                    }
                }
            }
        }

    }

    public String buildConfRemoteDir(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if(Objects.isNull(one)){
            throw new RdosDefineException("集群不存在");
        }
        return  "confPath" + File.separator + AppType.CONSOLE + "_" + one.getClusterName();
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
    private String wrapperConfig(EComponentType componentType, String componentString, boolean isOpenKerberos, String clusterName, String hadoopVersion,
                                 String md5Key,Integer storeType) {
        if (EComponentType.KUBERNETES.equals(componentType)) {
            JSONObject dataJSON = new JSONObject();
            dataJSON.put("kubernetes.context",componentString);
            return dataJSON.toJSONString();
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
            componentConfigJSON.put(TYPE_NAME, this.convertComponentTypeToClient(clusterName, componentType.getTypeCode(), hadoopVersion,storeType));
        }
        if(!StringUtils.isBlank(md5Key)){
            componentConfigJSON.put(MD5_SUM_KEY, md5Key);
        }
        return componentConfigJSON.toJSONString();
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
    private String updateComponentKerberosFile(Long clusterId, Component addComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource) {
        // kerberos认证文件 远程删除 kerberos下的文件
        String remoteDirKerberos = remoteDir + File.separator + KERBEROS_PATH;
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

        File fileKeyTab = files.stream().filter(f -> f.getName().endsWith(KEYTAB_SUFFIX)).findFirst().orElse(null);
        File fileConf = files.stream().filter(f -> f.getName().equalsIgnoreCase(KRB5_CONF)).findFirst().orElse(null);

        if (fileKeyTab==null) {
            throw new RdosDefineException("上传的Hadoop-Kerberos文件的zip文件中必须有keytab文件，请添加keytab文件");
        }
        LOGGER.info("fileKeyTab Unzip fileName:{}",fileKeyTab.getAbsolutePath());

        if (fileConf==null) {
            throw new RdosDefineException("上传的Hadoop-Kerberos文件的zip文件中必须有conf文件，请添加conf文件");
        }
        LOGGER.info("conf Unzip fileName:{}",fileConf.getAbsolutePath());

        //获取principal
        String principal = this.getPrincipal(fileKeyTab);
        //删除sftp原来kerberos 的文件夹
        sftpFileManage.deleteDir(remoteDirKerberos);
        //上传kerberos解压后的文件
        for (File file : files) {
            LOGGER.info("upload sftp file:{}",file.getAbsolutePath());
            sftpFileManage.uploadFile(remoteDirKerberos, file.getPath());
        }

        //更新数据库kerberos信息
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, addComponent.getComponentTypeCode());
        boolean isFirstOpenKerberos = false;
        if (Objects.isNull(kerberosConfig)) {
            kerberosConfig = new KerberosConfig();
            isFirstOpenKerberos = true;
        }
        kerberosConfig.setOpenKerberos(1);
        kerberosConfig.setPrincipal(principal);
        kerberosConfig.setName(fileKeyTab.getName());
        kerberosConfig.setRemotePath(remoteDirKerberos);
        kerberosConfig.setClusterId(clusterId);
        kerberosConfig.setComponentType(addComponent.getComponentTypeCode());
        kerberosConfig.setKrbName(fileConf.getName());
        if (isFirstOpenKerberos) {
            kerberosDao.insert(kerberosConfig);
        } else {
            kerberosDao.update(kerberosConfig);
        }
        return remoteDirKerberos;
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeKerberos(Long componentId) {
        kerberosDao.deleteByComponentId(componentId);
        Component updateComponent = new Component();
        updateComponent.setId(componentId);
        updateComponent.setKerberosFileName("");
        componentDao.update(updateComponent);
    }

    public ComponentsResultVO addOrCheckClusterWithName(String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("集群名称不能为空");
        }
        clusterName = clusterName.trim();
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if (Objects.isNull(cluster)) {
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
    public List<Object> config(List<Resource> resources,  Integer componentType, Boolean autoDelete) {

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
        if(Objects.isNull(one)){
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() ;
    }


    /**
     * 测试单个组件联通性
     */
    public ComponentTestResult testConnect(Integer componentType, String componentConfig, String clusterName,
                                            String hadoopVersion, Long engineId, KerberosConfig kerberosConfig, Map<String, String> sftpConfig,Integer storeType) {
        if (EComponentType.notCheckComponent.contains(EComponentType.getByCode(componentType))) {
            ComponentTestResult componentTestResult = new ComponentTestResult();
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

        ComponentTestResult componentTestResult = workerOperator.testConnect(pluginType,
                this.wrapperConfig(componentType, componentConfig, sftpConfig, kerberosConfig, clusterName));
        if (Objects.isNull(componentTestResult)) {
            componentTestResult = new ComponentTestResult();
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg("测试联通性失败");
            return componentTestResult;
        }
        componentTestResult.setComponentTypeCode(componentType);
        if (componentTestResult.getResult() && Objects.nonNull(engineId)) {
            updateCache(engineId, componentType);
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
    public String wrapperConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig,String clusterName) {
        JSONObject dataInfo = new JSONObject();
        dataInfo.put("componentName", EComponentType.getByCode(componentType).getName().toLowerCase());
        if (Objects.nonNull(kerberosConfig)) {
            dataInfo.put("kerberosFileTimestamp",kerberosConfig.getGmtModified());
            //开启了kerberos
            dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
            dataInfo.put("remoteDir", kerberosConfig.getRemotePath());
            dataInfo.put("principalFile", kerberosConfig.getName());
            dataInfo.put("krbName", kerberosConfig.getKrbName());
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
            Cluster cluster = clusterDao.getByClusterName(clusterName);
            if(Objects.nonNull(cluster)){
                Component yarnComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
                if(Objects.nonNull(yarnComponent)){
                    Map yarnMap = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);
                    dataInfo.put(EComponentType.YARN.getConfName(), yarnMap);
                }
            }
        } else if (EComponentType.KUBERNETES.getTypeCode() == componentType) {
            //
            dataInfo = new JSONObject();
            JSONObject confObj = new JSONObject();
            if(componentConfig.contains("kubernetes.context")){
                JSONObject contextConf = JSONObject.parseObject(componentConfig);
                componentConfig = contextConf.getString("kubernetes.context");
            }
            confObj.put(EComponentType.KUBERNETES.getConfName(),componentConfig);
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
        if (Objects.nonNull(kerberosConfig)) {
            //开启了kerberos
            dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
            dataInfo.put("remoteDir", kerberosConfig.getRemotePath());
            dataInfo.put("principalFile", kerberosConfig.getName());
            dataInfo.put("krbName", kerberosConfig.getKrbName());
            dataInfo.put("kerberosFileTimestamp", kerberosConfig.getGmtModified());
            //补充yarn参数
            Cluster cluster = clusterDao.getByClusterName(clusterName);
            if (Objects.nonNull(cluster)) {
                Component yarnComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
                if (Objects.nonNull(yarnComponent)) {
                    Map yarnMap = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);
                    dataInfo.put(EComponentType.YARN.getConfName(), yarnMap);
                }
            }
        }
        return dataInfo;
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
        if(Objects.isNull(one)){
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
        if (Objects.isNull(componentId)) {
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(componentType, clusterName, hadoopVersion,null);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                JSONObject fileJson = new JSONObject();
                fileJson = (JSONObject) this.convertTemplateToJson(clientTemplates, fileJson);
                uploadFileName = EComponentType.getByCode(componentType).name() + ".json";
                localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
                try {
                    FileUtils.write(new File(localDownLoadPath), fileJson.toString());
                } catch (Exception e) {
                    throw new RdosDefineException("文件不存在");
                }
            }
        } else {
            Component component = componentDao.getOne(componentId);
            if (Objects.isNull(component)) {
                throw new RdosDefineException("组件不存在");
            }
            Long clusterId = componentDao.getClusterIdByComponentId(componentId);
            Component sftpComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.SFTP.getTypeCode());
            if (Objects.isNull(sftpComponent)) {
                throw new RdosDefineException("sftp组件不存在");
            }

            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + component.getComponentName();

            SftpConfig sftpConfig = JSONObject.parseObject(sftpComponent.getComponentConfig(), SftpConfig.class);
            String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, component.getComponentTypeCode());
            SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
            if (DownloadType.Kerberos.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + KERBEROS_PATH;
                localDownLoadPath = localDownLoadPath + File.separator + KERBEROS_PATH;
                sftpFileManage.downloadDir(remoteDir, localDownLoadPath);
            } else {
                if (Objects.isNull(component.getUploadFileName())) {
                    // 一种是  全部手动填写的 如flink
                    try {
                        localDownLoadPath = localDownLoadPath + ".json";
                        FileUtils.write(new File(localDownLoadPath), component.getComponentConfig());
                    } catch (IOException e) {
                        LOGGER.error("write upload file {} error", component.getComponentConfig(), e);
                    }
                } else {
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
            File[] files = file.listFiles();
            //压缩成zip包
            if (Objects.nonNull(files)) {
                if (DownloadType.Kerberos.getCode() == downloadType) {
                    Long clusterId = componentDao.getClusterIdByComponentId(componentId);
                    KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType);
                    if (Objects.nonNull(kerberosConfig)) {
                        zipFilename = kerberosConfig.getName() + ZIP_SUFFIX;
                    }
                }
                ZipUtil.zipFile(USER_DIR_DOWNLOAD + File.separator + zipFilename, Arrays.stream(files).collect(Collectors.toList()));
            }
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                LOGGER.error("delete upload file {} error", file.getName(), e);
            }
            return new File(USER_DIR_DOWNLOAD + File.separator + zipFilename);
        } else {
            return new File(localDownLoadPath);
        }
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
        List<ClientTemplate> defaultPluginConfig = null;
        try {
            String typeName = null;
            if (EComponentType.HDFS.getTypeCode().equals(componentType)) {
                typeName = EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(version, component);
            } else if(EComponentType.NFS.getTypeCode().equals(componentType)){
                typeName = EComponentType.NFS.name().toLowerCase();
            } else {
                typeName = this.convertComponentTypeToClient(clusterName, componentType, version, storeType);
            }

            defaultPluginConfig = workerOperator.getDefaultPluginConfig(typeName, component.getName().toLowerCase());
        } catch (Exception e) {
            throw new RdosDefineException("不支持的插件类型");
        }
        if (CollectionUtils.isEmpty(defaultPluginConfig)) {
            return new ArrayList<>();
        }

        return defaultPluginConfig;
    }

    @SuppressWarnings("unchecked")
    private Object convertTemplateToJson(List<ClientTemplate> clientTemplates, Object data) {
        for (ClientTemplate clientTemplate : clientTemplates) {
            Object temp = data;
            if (StringUtils.isNotBlank(clientTemplate.getKey())) {
                if (data instanceof Map) {
                    if (EFrontType.CHECKBOX.name().equalsIgnoreCase(clientTemplate.getType())) {
                        List myData = new ArrayList();
                        ((Map) data).put(clientTemplate.getKey(), myData);
                        data = myData;
                    } else if(EFrontType.GROUP.name().equalsIgnoreCase(clientTemplate.getType())) {
                        Map myData = new HashMap();
                        ((Map) data).put(clientTemplate.getKey(), myData);
                        data = myData;
                    } else {
                        ((Map) data).put(clientTemplate.getKey(), clientTemplate.getValue());
                    }
                } else if (data instanceof List) {
                    if (EFrontType.CHECKBOX.name().equalsIgnoreCase(clientTemplate.getType())) {
                        List myData = new ArrayList();
                        ((List) data).add(myData);
                        data = myData;
                    } else if(EFrontType.GROUP.name().equalsIgnoreCase(clientTemplate.getType())) {
                        Map myData = new HashMap();
                        ((List) data).add(myData);
                        data = myData;
                    } else {
                        ((List)data).add(clientTemplate.getValue());
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(clientTemplate.getValues())) {
                //以第一个参数为准 作为默认值
                this.convertTemplateToJson(Lists.newArrayList(clientTemplate.getValues()), data);
            }
            data = temp;
        }
        return data;
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
            List<Pair<String, String>> defaultVersion = componentVersionMapping.get(EComponentType.getByCode(componentType).getName());
            if (CollectionUtils.isNotEmpty(defaultVersion)) {
                version = defaultVersion.get(0).getValue();
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
                    return EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(version, EComponentType.HDFS);
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
            //hdfs 和 yarn 为1位标识
            if (StringUtils.isBlank(hadoopVersion)) {
                return "2";
            }
            if (hadoopVersion.startsWith("hadoop")) {
                //hadoop2
                return hadoopVersion.toLowerCase().replace("hadoop", "").substring(0, 1);
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
    public void delete( List<Integer> componentIds) {
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
        }
    }


    /***
     * 获取对应的组件版本信息
     * @return
     */
    public Map getComponentVersion() {
        return componentVersionMapping;
    }

    public Component getComponentByClusterId(Long clusterId, Integer componentType) {
        return componentDao.getByClusterIdAndComponentType(clusterId, componentType);
    }

    /**
     * 刷新组件信息
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

        Map<String, String> sftpMap = getSftpMap(components);
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        for (Component component : components) {
            if (EComponentType.YARN.getTypeCode() != component.getComponentTypeCode()) {
                continue;
            }
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(
                    cluster.getId(), component.getComponentTypeCode());
            Map<String, String> finalSftpMap = sftpMap;
            try {
                CompletableFuture.runAsync(() -> {
                    ComponentTestResult refreshResult = new ComponentTestResult();
                    try {
                        refreshResult = this.testConnect(component.getComponentTypeCode(),
                                component.getComponentConfig(), clusterName, component.getHadoopVersion(),
                                component.getEngineId(), kerberosConfig, finalSftpMap,component.getStoreType());

                        if (refreshResult.getResult() && EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()) {
                            engineService.updateResource(component.getEngineId(), refreshResult.getClusterResourceDescription());
                            queueService.updateQueue(component.getEngineId(), refreshResult.getClusterResourceDescription());
                        }

                    } catch (Exception e) {
                        refreshResult.setResult(false);
                        refreshResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                        LOGGER.error("refresh {}  error ", component.getComponentConfig(), e);
                    } finally {
                        refreshResult.setComponentTypeCode(component.getComponentTypeCode());
                        refreshResults.add(refreshResult);
                        countDownLatch.countDown();
                    }
                }, connectPool).get(env.getTestConnectTimeout(),TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("refres {}  e ", component.getComponentConfig(), e);
            }
        }
        return refreshResults;
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

        Map<String, String> sftpMap = getSftpMap(components);

        List<ComponentTestResult> testResults = new ArrayList<>(components.size());
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        for (Component component : components) {
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), component.getComponentTypeCode());
            try {
                CompletableFuture.runAsync(() -> {
                    ComponentTestResult testResult = new ComponentTestResult();
                    try {
                        testResult = this.testConnect(component.getComponentTypeCode(), component.getComponentConfig(), clusterName, component.getHadoopVersion(),
                                component.getEngineId(), kerberosConfig, sftpMap,component.getStoreType());
                        //测试联通性
                        if (EComponentType.YARN.getTypeCode() == component.getComponentTypeCode()) {
                            if (testResult.getResult()) {
                                engineService.updateResource(component.getEngineId(), testResult.getClusterResourceDescription());
                                queueService.updateQueue(component.getEngineId(), testResult.getClusterResourceDescription());
                            }
                        }
                    } catch (Exception e) {
                        testResult.setResult(false);
                        testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                        LOGGER.error("test connect {}  error ", component.getComponentConfig(), e);
                    } finally {
                        testResult.setComponentTypeCode(component.getComponentTypeCode());
                        testResults.add(testResult);
                        countDownLatch.countDown();
                    }
                },connectPool).get(env.getTestConnectTimeout(),TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("test connect {}  e ", component.getComponentConfig(), e);
                countDownLatch.countDown();
                ComponentTestResult testResult = new ComponentTestResult();
                testResult.setResult(false);
                testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                testResult.setComponentTypeCode(component.getComponentTypeCode());
                testResults.add(testResult);
            }
        }
        try {
            countDownLatch.await(env.getTestConnectTimeout(),TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("test connect  await {}  error ", clusterName, e);
        }
        return testResults;
    }

    private List<Component> getComponents(Cluster cluster){

        if (Objects.isNull(cluster)) {
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

    private Map<String, String> getSftpMap(List<Component> components) {
        Optional<Component> componentOptional = components.stream()
                .filter(c -> EComponentType.SFTP.getTypeCode() == c.getComponentTypeCode())
                .findFirst();
        Map<String, String> sftpMap = null;
        try {
            if (componentOptional.isPresent()) {
                sftpMap = (Map) JSONObject.parseObject(componentOptional.get().getComponentConfig(), Map.class);
            }
        } catch (Exception e) {
        }
        return sftpMap;
    }

    public JSONObject getPluginInfoWithComponentType(Long dtuicTenantId,EComponentType componentType){
        ClusterVO cluster = clusterService.getClusterByTenant(dtuicTenantId);

        Component component = this.getComponentByClusterId(cluster.getId(), componentType.getTypeCode());
        JSONObject hdfsConfigJSON = JSONObject.parseObject(component.getComponentConfig());
        String typeName = hdfsConfigJSON.getString(ComponentService.TYPE_NAME);
        if (StringUtils.isBlank(typeName)) {
            //获取对应的插件名称
            component = this.getComponentByClusterId(cluster.getId(), componentType.getTypeCode());
            typeName = this.convertComponentTypeToClient(cluster.getClusterName(),
                    componentType.getTypeCode(), component.getHadoopVersion(),component.getStoreType());
        }
        //是否开启kerberos
        KerberosConfig kerberos = kerberosDao.getByComponentType(cluster.getId(), componentType.getTypeCode());
        Component sftpConfig = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.SFTP.getTypeCode());
        Map sftpMap = JSONObject.parseObject(sftpConfig.getComponentConfig(), Map.class);
        String pluginInfo = this.wrapperConfig(componentType.getTypeCode(), component.getComponentConfig(), sftpMap, kerberos, cluster.getClusterName());
        JSONObject pluginInfoObj = JSONObject.parseObject(pluginInfo);
        pluginInfoObj.put(TYPE_NAME,typeName);
        pluginInfoObj.put(VERSION,component.getHadoopVersion());
        return pluginInfoObj;
    }

    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if(null == cluster){
            throw new RdosDefineException("集群不存在");
        }
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
        Component nfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode());
        List<Component> components = new ArrayList<>();
        components.add(hdfs);
        components.add(nfs);
        return components;
    }
}
