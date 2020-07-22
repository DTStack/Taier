package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.annotation.Forbidden;
import com.dtstack.engine.api.annotation.Param;
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
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.EFrontType;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.exception.ErrorCode;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.MD5Util;
import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.router.cache.ConsoleCache;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.PublicUtil;
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

import static com.dtstack.engine.common.constrant.ConfigConstant.MD5_SUM_KEY;

@Service
public class ComponentService implements com.dtstack.engine.api.service.ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    private final static String ZIP_CONTENT_TYPE = "zip";

    private static String unzipLocation = System.getProperty("user.dir") + File.separator + "unzip";

    private static String downloadLocation = System.getProperty("user.dir") + File.separator + "download";

    public static final String KERBEROS_PATH = "kerberos";

    private static final String KERBEROS_CONFIG = "kerberosConfig";

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
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList("yarn-site.xml"));
        componentVersionMapping.put(EComponentType.FLINK.getName(), Lists.newArrayList(new Pair<>("1.8", "180"), new Pair<>("1.10", "110")));
        componentVersionMapping.put(EComponentType.SPARK.getName(), Lists.newArrayList(new Pair<>("2.1.X", "210"), new Pair<>("2.4.X", "240")));
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
    public String listConfigOfComponents(@Param("tenantId") Long dtUicTenantId, @Param("engineType") Integer engineType) {
        JSONObject result = new JSONObject();
        Long tenantId = tenantDao.getIdByDtUicTenantId(dtUicTenantId);
        if (tenantId == null) {
            return result.toJSONString();
        }

        List<Long> engineIds = engineTenantDao.listEngineIdByTenantId(tenantId);
        if (CollectionUtils.isEmpty(engineIds)) {
            return result.toJSONString();
        }

        List<Engine> engines = engineDao.listByEngineIds(engineIds);
        if (CollectionUtils.isEmpty(engines)) {
            return result.toJSONString();
        }

        Engine targetEngine = null;
        for (Engine engine : engines) {
            if (engine.getEngineType() == engineType) {
                targetEngine = engine;
                break;
            }
        }

        if (targetEngine == null) {
            return result.toJSONString();
        }

        List<Component> componentList = componentDao.listByEngineId(targetEngine.getId());
        for (Component component : componentList) {
            result.put(String.valueOf(component.getComponentTypeCode()), JSONObject.parseObject(component.getComponentConfig()));
        }

        return result.toJSONString();
    }

    public Component getOne(@Param("id") Long id) {
        Component component = componentDao.getOne(id);
        if (component == null) {
            throw new RdosDefineException("组件不存在");
        }
        return component;
    }

    @Forbidden
    public String getSftpClusterKey(Long clusterId) {
        Cluster one = clusterDao.getOne(clusterId);
        if(Objects.isNull(one)){
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE.name() + "_" + one.getClusterName();
    }

    @Forbidden
    public Map<String, Object> fillKerberosConfig(String allConfString, Long clusterId) {
        JSONObject allConf = JSONObject.parseObject(allConfString);
        allConf.putAll(KerberosConfigVerify.replaceFilePath(allConf, getClusterLocalKerberosDir(clusterId)));
        JSONObject kerberosConfig = allConf.getJSONObject(KERBEROS_CONFIG);
        if (kerberosConfig != null) {
            allConf.put(KERBEROS_CONFIG, KerberosConfigVerify.replaceFilePath(kerberosConfig, getClusterLocalKerberosDir(clusterId)));
        }
        return allConf;
    }

    /**
     * 更新缓存
     */
    @Forbidden
    public void updateCache(Long engineId, Integer componentCode) {
        Set<Long> dtUicTenantIds = new HashSet<>();
        if (Objects.nonNull(componentCode) && (
                EComponentType.TIDB_SQL.getTypeCode() == componentCode ||
                EComponentType.LIBRA_SQL.getTypeCode() == componentCode ||
                EComponentType.GREENPLUM_SQL.getTypeCode() == componentCode ||
                EComponentType.ORACLE_SQL.getTypeCode() == componentCode)) {

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
        clusterService.clearPluginInfoCache();
    }

    @Forbidden
    public List<Component> listComponent(Long engineId) {
        return componentDao.listByEngineId(engineId);
    }

    private Map<String, Object> parseUploadFileToMap(List<Resource> resources) {
        Map<String, Object> confMap = new HashMap<>();
        String upzipLocation = null;
        List<File> xmlFiles;
        try {
            if (CollectionUtils.isEmpty(resources)) {
                throw new RdosDefineException("上传的文件不能为空");
            }

            Resource resource = resources.get(0);
            if (!resource.getFileName().endsWith("." + ZIP_CONTENT_TYPE)) {
                throw new RdosDefineException("压缩包格式仅支持ZIP格式");
            }

            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            upzipLocation = unzipLocation + File.separator + resource.getFileName();
            try {
                xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            } catch (Exception e) {
                LOGGER.error("解压配置文件格式错误", e);
                throw new RdosDefineException("解压配置文件格式错误");
            }

            try {
                for (File file : xmlFiles) {
                    Map<String, Object> fileMap = null;
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                    if (file.getName().endsWith("xml")) {
                        //xml文件
                        fileMap = Xml2JsonUtil.xml2map(file);
                    } else {
                        if(file.getName().endsWith("json")){
                            //json文件
                            String jsonStr = Xml2JsonUtil.readFile(file);
                            if (StringUtils.isBlank(jsonStr)) {
                                continue;
                            }
                            fileMap = JSONObject.parseObject(jsonStr, Map.class);
                        }
                    }
                    if (Objects.nonNull(fileMap)) {
                        confMap.put(file.getName(), fileMap);
                    }
                }
            } catch (Exception e) {
                throw new RdosDefineException("解析配置文件出错:" + e.getMessage());
            }
            return confMap;
        } catch (Exception e) {
            LOGGER.error("parseAndUploadXmlFile file error ", e);
            if (e instanceof RdosDefineException) {
                RdosDefineException rdosDefineException = (RdosDefineException) e;
                throw new RdosDefineException(rdosDefineException.getErrorMessage());
            }
            throw new RdosDefineException(ErrorCode.SERVER_EXCEPTION.getDescription());
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
            }
        }
    }

    @Forbidden
    public String getClusterLocalKerberosDir(Long clusterId) {
        return env.getLocalKerberosDir() + File.separator + getSftpClusterKey(clusterId);
    }

    @Forbidden
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


    public KerberosConfig getKerberosConfig(@Param("clusterId") Long clusterId, @Param("componentType") Integer componentType) {
        KerberosConfig kerberosConfig = kerberosDao.getByComponentType(clusterId, componentType);
        return kerberosConfig;
    }

    @Forbidden
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
    public ComponentVO addOrUpdateComponent(@Param("clusterId") Long clusterId, @Param("componentConfig") String componentConfig,
                                            @Param("resources") List<Resource> resources, @Param("hadoopVersion") String hadoopVersion,
                                            @Param("kerberosFileName") String kerberosFileName, @Param("componentTemplate") String componentTemplate,
                                            @Param("componentCode") Integer componentCode) {
        if (StringUtils.isBlank(componentConfig) && EComponentType.KUBERNETES.getTypeCode() != componentCode) {
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

        Component sftpComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.SFTP.getTypeCode());;
        if (CollectionUtils.isNotEmpty(resources)) {
            //上传资源需要依赖sftp组件
            if (Objects.isNull(sftpComponent)) {
                throw new RdosDefineException("请先配置sftp组件");
            }
        }
        EComponentType componentType = EComponentType.getByCode(componentDTO.getComponentTypeCode());
        MultiEngineType engineType = EComponentType.getEngineTypeByComponent(componentType);
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType.getType());
        if (Objects.isNull(engine)) {
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
        //yarn 和 Kubernetes 只能2选一
        if (EComponentType.YARN.getTypeCode() == componentCode || EComponentType.KUBERNETES.getTypeCode() == componentCode) {
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId,
                    EComponentType.YARN.getTypeCode() == componentCode ? EComponentType.KUBERNETES.getTypeCode() : EComponentType.YARN.getTypeCode());
            if (Objects.nonNull(resourceComponent)) {
                throw new RdosDefineException("资源组件只能选择单项");
            }
        }

        Component addComponent = new ComponentDTO();
        BeanUtils.copyProperties(componentDTO, addComponent);
        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        boolean isUpdate = false;
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (isOpenKerberos) {
            if (!resources.isEmpty() && !kerberosFileName.endsWith("." + ZIP_CONTENT_TYPE)) {
                throw new RdosDefineException("kerberos上传文件非zip格式");
            }
        }
        if (Objects.nonNull(dbComponent)) {
            //更新
            isUpdate = true;
            if (!isOpenKerberos) {
                KerberosConfig componentKerberos = kerberosDao.getByComponentType(dbComponent.getId(), dbComponent.getComponentTypeCode());
                if (Objects.nonNull(componentKerberos)) {
                    isOpenKerberos = true;
                }
            }
            addComponent = dbComponent;
        }
        if(EComponentType.KUBERNETES.getTypeCode() == componentType.getTypeCode() && CollectionUtils.isNotEmpty(resources)){
            //kubernetes 信息需要自己解析文件
            List<Object> config = this.config(resources, EComponentType.KUBERNETES.getTypeCode(),false);
            if(CollectionUtils.isNotEmpty(config)){
                componentConfig = (String)config.get(0);
            }
        }

        addComponent.setHadoopVersion(Optional.ofNullable(hadoopVersion).orElse("hadoop2"));
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setEngineId(engine.getId());
        addComponent.setComponentTemplate(componentTemplate);
        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }

        String md5Key = "";
        Map<String, String> sftpMap = Objects.isNull(sftpComponent) ? new HashMap<>() : JSONObject.parseObject(sftpComponent.getComponentConfig(), Map.class);
        if (CollectionUtils.isNotEmpty(resources)) {
            //上传配置文件到sftp 供后续下载
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpMap, addComponent, dbComponent);
        }
        addComponent.setComponentConfig(this.wrapperConfig(componentType, componentConfig, isOpenKerberos, clusterName, hadoopVersion,md5Key));

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

    private String uploadResourceToSftp(@Param("clusterId") Long clusterId, @Param("resources") List<Resource> resources, @Param("kerberosFileName") String kerberosFileName,
                                      Map<String, String> sftpMap,
                                      Component addComponent, Component dbComponent) {
        //上传配置文件到sftp 供后续下载
        SFTPHandler instance = null;
        String md5sum = "";
        try {
            instance = SFTPHandler.getInstance(sftpMap);
        } catch (Exception e) {
            LOGGER.error("update component resource to sftp error", e);
            throw new RdosDefineException("请检查sftp配置");
        }
        String remoteDir = sftpMap.get("path") + File.separator + this.buildSftpPath(clusterId, addComponent.getComponentTypeCode());
        for (Resource resource : resources) {
            if (!resource.getFileName().equalsIgnoreCase(kerberosFileName) || StringUtils.isBlank(kerberosFileName)) {
                addComponent.setUploadFileName(resource.getFileName());
            }
            try {
                if (resource.getFileName().equalsIgnoreCase(kerberosFileName)) {
                    this.updateComponentKerberosFile(clusterId, addComponent, instance, remoteDir, resource, addComponent.getId());
                } else {
                    this.updateComponentConfigFile(dbComponent, instance, remoteDir, resource);
                    if(addComponent.getComponentTypeCode() == EComponentType.HDFS.getTypeCode()){
                        String xmlZipLocation = resource.getUploadedFileName();
                        md5sum = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        this.updateConfigToSftpPath(clusterId, sftpMap, instance, resource);
                    }
                    if(addComponent.getComponentTypeCode() == EComponentType.YARN.getTypeCode()){
                        this.updateConfigToSftpPath(clusterId, sftpMap, instance, resource);
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
     * @param sftpMap
     * @param instance
     * @param resource
     */
    private void updateConfigToSftpPath(@Param("clusterId") Long clusterId, Map<String, String> sftpMap, SFTPHandler instance, Resource resource) {
        //上传xml到对应路径下 拼接confHdfsPath
        String confRemotePath = sftpMap.get("path") + File.separator;
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
                        instance.upload(confRemotePath + buildPath, file.getPath(), true);
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
    private String wrapperConfig(EComponentType componentType, String componentString, boolean isOpenKerberos, String clusterName, String hadoopVersion,String md5Key) {
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
            componentConfigJSON.put(TYPE_NAME, this.convertComponentTypeToClient(clusterName, componentType.getTypeCode(), hadoopVersion));
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
     * @param instance
     * @param remoteDir
     * @param resource
     */
    private void updateComponentConfigFile(Component dbComponent, SFTPHandler instance, String remoteDir, Resource resource) {
        //原来配置
        String deletePath = remoteDir + File.separator;
        if (Objects.nonNull(dbComponent)) {
            deletePath = deletePath + dbComponent.getUploadFileName();
        }
        //删除原来的文件配置zip
        try {
            instance.deleteFile(deletePath);
        } catch (Exception e) {
            LOGGER.error("delete path  {} error ",deletePath,e);
        }
        //更新为原名
        instance.upload(remoteDir, resource.getUploadedFileName());
        instance.renamePath(remoteDir + File.separator + resource.getUploadedFileName().substring(resource.getUploadedFileName().lastIndexOf(File.separator) + 1),
                remoteDir + File.separator + resource.getFileName());
    }


    /**
     * 解压kerberos文件到本地 并上传至sftp
     * * @param clusterId
     *
     * @param addComponent
     * @param instance
     * @param remoteDir
     * @param resource
     * @return
     */
    private String updateComponentKerberosFile(Long clusterId, Component addComponent, SFTPHandler instance, String remoteDir, Resource resource, Long componentId) {
        //kerberos认证文件
        remoteDir = remoteDir + File.separator;
        //删除本地文件夹
        String kerberosPath = this.getLocalKerberosPath(clusterId, addComponent.getComponentTypeCode());
        try {
            FileUtils.deleteDirectory(new File(kerberosPath));
        } catch (IOException e) {
            LOGGER.error("delete old kerberos directory {} error", kerberosPath, e);
        }
        //解压到本地
        this.unzipKeytab(kerberosPath, resource);
        //获取principal
        boolean isDir = false;
        File keyTabPath = this.getFileWithSuffix(kerberosPath, ".keytab");
        if(Objects.isNull(keyTabPath)){
            isDir = true;
            keyTabPath = this.getDeepFileWithSuffix(kerberosPath, ".keytab");
        }
        if (Objects.isNull(keyTabPath)) {
            throw new RdosDefineException("keytab文件缺失");
        }
        String principal = this.getPrincipal(keyTabPath);
        //删除sftp原来kerberos 的文件夹
        instance.deleteDir(remoteDir);
        //上传kerberos解压后的文件
        File ktb5File;
        if (isDir) {
            for (File file : keyTabPath.getParentFile().listFiles()) {
                instance.upload(remoteDir + File.separator + KERBEROS_PATH, file.getPath(),true);
            }
            ktb5File = this.getDeepFileWithSuffix(kerberosPath, ".conf");
        } else {
            ktb5File = this.getFileWithSuffix(kerberosPath, ".conf");
            instance.uploadDir(remoteDir, kerberosPath);
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
        kerberosConfig.setName(keyTabPath.getName());
        kerberosConfig.setRemotePath(remoteDir + KERBEROS_PATH);
        kerberosConfig.setClusterId(clusterId);
        kerberosConfig.setComponentType(addComponent.getComponentTypeCode());

        kerberosConfig.setKrbName(Objects.nonNull(ktb5File) ? ktb5File.getName() : null);
        if (isFirstOpenKerberos) {
            kerberosDao.insert(kerberosConfig);
        } else {
            kerberosDao.update(kerberosConfig);
        }
        return remoteDir;
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeKerberos(@Param("componentId") Long componentId) {
        kerberosDao.deleteByComponentId(componentId);
        Component updateComponent = new Component();
        updateComponent.setId(componentId);
        updateComponent.setKerberosFileName("");
        componentDao.update(updateComponent);
    }

    public Map<String, Object> addOrCheckClusterWithName(@Param("clusterName") String clusterName) {
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
            Map<String, Object> result = new HashMap<>();
            Long clusterId = clusterVO.getClusterId();
            result.put("clusterId", clusterId);
            LOGGER.info("add cluster {} ", clusterId);
            return result;
        }
        throw new RdosDefineException("集群名称已存在");
    }

    private void checkJSON(String json){
        JSON.parse(json);
    }


    /**
     * parse zip中xml或者json
     *
     * @param resources
     * @return
     */
    public List<Object> config(@Param("resources") List<Resource> resources, @Param("componentType") Integer componentType,@Param("autoDelete") Boolean autoDelete) {
        List<Object> datas = new ArrayList<>();
        try {
            List<String> xmlName = componentTypeConfigMapping.get(componentType);
            if (CollectionUtils.isNotEmpty(xmlName)) {
                Map<String, Object> xmlConfigMap = this.parseUploadFileToMap(resources);
                //多个配置文件合并为一个map
                Map data = new HashMap();
                for (String xml : xmlName) {
                    Object xmlData = xmlConfigMap.get(xml);
                    if (Objects.isNull(xmlData)) {
                        throw new RdosDefineException(String.format("缺少 %s 配置文件", xml));
                    }
                    if (xmlData instanceof Map) {
                        data.putAll((Map) xmlData);
                    }
                }
                datas.add(data);
            } else if(EComponentType.KUBERNETES.getTypeCode() == componentType) {
                Resource resource = resources.get(0);
                //解压缩获得配置文件
                String xmlZipLocation = resource.getUploadedFileName();
                String upzipLocation = unzipLocation + File.separator + resource.getFileName();
                //解析zip 带换行符号
                List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
                if(CollectionUtils.isNotEmpty(xmlFiles)){
                    try {
                        datas.add(FileUtil.getContentFromFile(xmlFiles.get(0).getPath()));
                    } catch (FileNotFoundException e) {
                        LOGGER.error("parse Kubernetes resource error {} ", resource.getUploadedFileName());
                    }
                }
            } else {
                // 当作json来解析
                for (Resource resource : resources) {
                    try {
                        String fileInfo = FileUtils.readFileToString(new File(resource.getUploadedFileName()));
                        checkJSON(fileInfo);
                        datas.add(PublicUtil.strToMap(fileInfo));
                    } catch (Exception e) {
                        LOGGER.error("parse json config resource error {} ", resource.getUploadedFileName());
                        throw new RdosDefineException("JSON文件格式错误");
                    }
                }
            }
        } finally {
            if (Objects.isNull(autoDelete) || true == autoDelete) {
                for (Resource resource : resources) {
                    try {
                        FileUtils.forceDelete(new File(System.getProperty("user.dir") + File.separator +
                                resource.getUploadedFileName()));
                    } catch (IOException e) {
                        LOGGER.debug("delete config resource error {} ", resource.getUploadedFileName());
                    }
                }
            }

        }
        return datas;
    }


    @Forbidden
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
    @Forbidden
    public ComponentTestResult testConnect(Integer componentType, String componentConfig, String clusterName,
                                            String hadoopVersion, Long engineId, KerberosConfig kerberosConfig, Map<String, String> sftpConfig) {
        if (EComponentType.notCheckComponent.contains(EComponentType.getByCode(componentType))) {
            ComponentTestResult componentTestResult = new ComponentTestResult();
            componentTestResult.setResult(true);
            return componentTestResult;
        }

        String pluginType = this.convertComponentTypeToClient(clusterName, componentType, hadoopVersion);

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
    private String wrapperConfig(int componentType, String componentConfig, Map<String, String> sftpConfig, KerberosConfig kerberosConfig,String clusterName) {
        JSONObject dataInfo = new JSONObject();
        dataInfo.put("componentName", EComponentType.getByCode(componentType).getName().toLowerCase());
        if (Objects.nonNull(kerberosConfig)) {
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
            dataInfo = JSONObject.parseObject(componentConfig);
            dataInfo.put(EComponentType.SFTP.getConfName(), sftpConfig);
            String jdbcUrl = dataInfo.getString("jdbcUrl");
            if (StringUtils.isBlank(jdbcUrl)) {
                throw new RdosDefineException("jdbcUrl不能为空");
            }

            if (EComponentType.SPARK_THRIFT.getTypeCode() == componentType ||
                    EComponentType.HIVE_SERVER.getTypeCode() == componentType) {
                if (!jdbcUrl.contains(";principal=") && jdbcUrl.endsWith("%s")) {
                    //数据库连接不带%s
                    dataInfo.put("jdbcUrl", jdbcUrl.substring(0, jdbcUrl.lastIndexOf("/")));
                }
            }

            dataInfo.put("username", dataInfo.getString("username"));
            dataInfo.put("password", dataInfo.getString("password"));
            if (Objects.nonNull(kerberosConfig)) {
                JSONObject config = new JSONObject();
                //开启了kerberos
                dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
                config.put("openKerberos", kerberosConfig.getOpenKerberos());
                config.put("remoteDir", kerberosConfig.getRemotePath());
                config.put("principalFile", kerberosConfig.getName());
                config.put("krbName", kerberosConfig.getKrbName());
                //补充yarn参数
                Cluster cluster = clusterDao.getByClusterName(clusterName);
                if(Objects.nonNull(cluster)){
                    Component yarnComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
                    if(Objects.nonNull(yarnComponent)){
                        Map yarnMap = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);
                        config.put(EComponentType.YARN.getConfName(), yarnMap);
                    }
                }
                dataInfo.put("config",config);
            }
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
        }
        return dataInfo.toJSONString();
    }

    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    @Forbidden
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
    public File downloadFile(@Param("componentId") Long componentId, @Param("type") Integer downloadType, @Param("componentType") Integer componentType,
                             @Param("hadoopVersion") String hadoopVersion, @Param("clusterName") String clusterName) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (Objects.isNull(componentId)) {
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(componentType, clusterName, hadoopVersion);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                JSONObject fileJson = new JSONObject();
                fileJson = (JSONObject) this.convertTemplateToJson(clientTemplates, fileJson);
                uploadFileName = EComponentType.getByCode(componentType).name() + ".json";
                localDownLoadPath = downloadLocation + File.separator + uploadFileName;
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
            Map<String, String> map = JSONObject.parseObject(sftpComponent.getComponentConfig(), Map.class);
            SFTPHandler instance = SFTPHandler.getInstance(map);
            String remoteDir = map.get("path") + File.separator + this.buildSftpPath(clusterId, component.getComponentTypeCode());
            localDownLoadPath = downloadLocation + File.separator + component.getId();
            if (DownloadType.Kerberos.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + KERBEROS_PATH;
                localDownLoadPath = localDownLoadPath + File.separator + KERBEROS_PATH;
                instance.downloadDir(remoteDir, localDownLoadPath);
            } else {
                //一种是 上传配置文件的需要到sftp下载
                //一种是  全部手动填写的 如flink
                if (Objects.isNull(component.getUploadFileName())) {
                    try {
                        localDownLoadPath = localDownLoadPath + ".json";
                        FileUtils.write(new File(localDownLoadPath), component.getComponentConfig());
                    } catch (IOException e) {
                        LOGGER.error("write upload file {} error", component.getComponentConfig(), e);
                    }
                } else {
                    instance.downloadDir(remoteDir + File.separator + component.getUploadFileName(), localDownLoadPath);
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
                        zipFilename = kerberosConfig.getName() + "." + ZIP_CONTENT_TYPE;
                    }
                }
                ZipUtil.zipFile(downloadLocation + File.separator + zipFilename, Arrays.stream(files).collect(Collectors.toList()));
            }
            try {
                FileUtils.forceDelete(file);
            } catch (IOException e) {
                LOGGER.error("delete upload file {} error", file.getName(), e);
            }
            return new File(downloadLocation + File.separator + zipFilename);
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
    public List<ClientTemplate> loadTemplate(@Param("componentType") Integer componentType, @Param("clusterName") String clusterName, @Param("version") String version) {
        EComponentType component = EComponentType.getByCode(componentType);
        List<ClientTemplate> defaultPluginConfig = null;
        try {
            defaultPluginConfig = workerOperator.getDefaultPluginConfig(this.convertComponentTypeToClient(clusterName, componentType, version),
                    component.getName().toLowerCase());
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
    @Forbidden
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String version) {
        //普通rdb插件
        String pluginName = EComponentType.convertPluginNameByComponent(EComponentType.getByCode(componentType));
        if (StringUtils.isNotBlank(pluginName)) {
            return pluginName;
        }
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("集群名称不能为空");
        } else if (EComponentType.YARN.getTypeCode() == componentType) {
            if (StringUtils.isBlank(version)) {
                throw new RdosDefineException("请选择集群版本");
            }
            //yarn是第一配置的
            ClusterVO cluster = clusterService.getClusterByName(clusterName);
            if (Objects.isNull(cluster)) {
                //如果没有配置hdfs hdfs给默认值 和yarn保持一致
                return String.format("yarn%s-hdfs%s-hadoop%s", this.formatHadoopVersion(version),
                        this.formatHadoopVersion(version), this.formatHadoopVersion(version));
            }
            Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
            //hdfs  和 yarn 的版本要保持一致
            if (Objects.nonNull(hdfs)) {
                if (!version.equalsIgnoreCase(hdfs.getHadoopVersion())) {
                    throw new RdosDefineException("hdfs 和 yarn 版本不一致");
                }
            } else {
                return String.format("yarn%s-hdfs%s-hadoop%s", this.formatHadoopVersion(version), this.formatHadoopVersion(version),
                        this.formatHadoopVersion(version));
            }
            //yarn2-hdfs2-hadoop2
            return String.format("yarn%s-hdfs%s-hadoop%s", this.formatHadoopVersion(version),
                    this.formatHadoopVersion(hdfs.getHadoopVersion()), this.formatHadoopVersion(version));
        } else if (EComponentType.KUBERNETES.getTypeCode() == componentType) {
            return "k8s-hdfs2-hadoop2";
        }

        ClusterVO cluster = clusterService.getClusterByName(clusterName);
        if (Objects.isNull(cluster)) {
            throw new RdosDefineException("请先配置HDFS");
        }

        Component yarn = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
        Component kubernetes = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.KUBERNETES.getTypeCode());
        if (Objects.isNull(yarn) && Objects.isNull(kubernetes)) {
            throw new RdosDefineException("请先配置资源组件");
        }
        String resourceSign = Objects.isNull(yarn) ? "k8s" : "yarn" + this.formatHadoopVersion(yarn.getHadoopVersion());
        if (EComponentType.HDFS.getTypeCode() == componentType) {
            //hdfs  和 yarn 的版本要保持一致
            if(Objects.nonNull(yarn)){
                if(!version.equalsIgnoreCase(yarn.getHadoopVersion())){
                    throw new RdosDefineException("hdfs 和 yarn 版本不一致");
                }
            }
            return String.format("%s-hdfs%s-hadoop%s", resourceSign, this.formatHadoopVersion(version), this.formatHadoopVersion(version));
        }
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
        if (Objects.isNull(hdfs)) {
            throw new RdosDefineException("请先配置HDFS");
        }
        String storageSign = "hdfs" + this.formatHadoopVersion(hdfs.getHadoopVersion());
        //dtscript yarn2-hdfs2-dtscript
        if(EComponentType.DT_SCRIPT.getTypeCode() == componentType){
            return String.format("%s-%s-dtscript",resourceSign,storageSign);
        }
        //learing yarn2-hdfs2-leanring
        if (EComponentType.LEARNING.getTypeCode() == componentType) {
            return String.format("%s-%s-learning",resourceSign,storageSign);
        }
        //flink  需要根据yarn hdfs version 拼接 如yarn2-hdfs2-flink180;
        if (EComponentType.FLINK.getTypeCode() == componentType) {
            //kubernetes 仅有110
            if(Objects.nonNull(kubernetes)){
                version = "110";
            }
            return String.format("%s-%s-flink%s", resourceSign, storageSign, version);
        }
        if (EComponentType.SPARK.getTypeCode() == componentType) {
            return String.format("%s-%s-spark%s", resourceSign, storageSign, version);
        }
        throw new RdosDefineException("暂无对应组件默认配置");
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
    @Forbidden
    private String formatHadoopVersion(String hadoopVersion) {
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
    }


    /**
     * 删除组件
     *
     * @param componentIds
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(@Param("componentIds") List<Integer> componentIds) {
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

    @Forbidden
    public Component getComponentByClusterId(Long clusterId, Integer componentType) {
        return componentDao.getByClusterIdAndComponentType(clusterId, componentType);
    }

    /**
     * 测试所有组件连通性
     * @param clusterName
     * @return
     */
    public List<ComponentTestResult> testConnects(@Param("clusterName") String clusterName) {
        Cluster cluster = null;
        if (StringUtils.isNotBlank(clusterName)) {
            cluster = clusterDao.getByClusterName(clusterName);
        }
        if (Objects.isNull(cluster)) {
            throw new RdosDefineException("集群不存在");
        }
        List<ComponentTestResult> results = new ArrayList<>();
        List<Engine> engines = engineDao.listByClusterId(cluster.getId());
        if (CollectionUtils.isEmpty(engines)) {
            return results;
        }
        List<Long> engineId = engines.stream().map(Engine::getId).collect(Collectors.toList());

        List<Component> components = componentDao.listByEngineIds(engineId);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>(0);
        }
        Optional<Component> componentOptional = components.stream().filter(c -> EComponentType.SFTP.getTypeCode() == c.getComponentTypeCode()).findFirst();
        Map<String, String> sftpMap = null;
        try {
            if (componentOptional.isPresent()) {
                sftpMap = (Map) JSONObject.parseObject(componentOptional.get().getComponentConfig(), Map.class);
            }
        } catch (Exception e) {
        }
        List<ComponentTestResult> testResults = new ArrayList<>(components.size());
        CountDownLatch countDownLatch = new CountDownLatch(components.size());
        for (Component component : components) {
            KerberosConfig kerberosConfig = kerberosDao.getByComponentType(cluster.getId(), component.getComponentTypeCode());
            Map<String, String> finalSftpMap = sftpMap;
            try {
                CompletableFuture.runAsync(() -> {
                    ComponentTestResult testResult = new ComponentTestResult();
                    try {
                        testResult = this.testConnect(component.getComponentTypeCode(), component.getComponentConfig(), clusterName, component.getHadoopVersion(), component.getEngineId(), kerberosConfig, finalSftpMap);
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
            }
        }
        try {
            countDownLatch.await(env.getTestConnectTimeout(),TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.error("test connect  await {}  error ", clusterName, e);
        }
        return testResults;
    }

    @Forbidden
    public JSONObject getPluginInfoWithComponentType(Long dtuicTenantId,EComponentType componentType){
        ClusterVO cluster = clusterService.getClusterByTenant(dtuicTenantId);

        Component component = this.getComponentByClusterId(cluster.getId(), componentType.getTypeCode());
        JSONObject hdfsConfigJSON = JSONObject.parseObject(component.getComponentConfig());
        String typeName = hdfsConfigJSON.getString(ComponentService.TYPE_NAME);
        if (StringUtils.isBlank(typeName)) {
            //获取对应的插件名称
            component = this.getComponentByClusterId(cluster.getId(), componentType.getTypeCode());
            typeName = this.convertComponentTypeToClient(cluster.getClusterName(),
                    componentType.getTypeCode(), component.getHadoopVersion());
        }
        //是否开启kerberos
        KerberosConfig kerberos = kerberosDao.getByComponentType(cluster.getId(), componentType.getTypeCode());
        Component sftpConfig = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.SFTP.getTypeCode());
        Map sftpMap = JSONObject.parseObject(sftpConfig.getComponentConfig(), Map.class);
        String pluginInfo = this.wrapperConfig(componentType.getTypeCode(), component.getComponentConfig(), sftpMap, kerberos, cluster.getClusterName());
        JSONObject pluginInfoObj = JSONObject.parseObject(pluginInfo);
        pluginInfoObj.put(TYPE_NAME,typeName);
        return pluginInfoObj;
    }

}
