package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSON;
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
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dao.*;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.enums.EComponentType;
import com.dtstack.engine.master.enums.MultiEngineType;
import com.dtstack.engine.common.env.EnvironmentContext;
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
        componentVersionMapping.put(EComponentType.SPARK_THRIFT.getName(), Lists.newArrayList(new Pair<>("1.X", "1.x"), new Pair<>("2.X", "2.x"),new Pair<>("2.1.1-cdh6.1.1","2.1.1-cdh6.1.1")));
        componentVersionMapping.put(EComponentType.HIVE_SERVER.getName(), Lists.newArrayList(new Pair<>("1.X", "1.x"), new Pair<>("2.X", "2.x"),new Pair<>("2.1.1-cdh6.1.1","2.1.1-cdh6.1.1")));
        //-1 为hadoopversion
        componentVersionMapping.put("hadoopVersion", Lists.newArrayList(new Pair<>("hadoop2", "hadoop2"),
                new Pair<>("hadoop3", "hadoop3"), new Pair<>("HW", "HW")));
    }


    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long dtUicTenantId, Integer engineType) {
        EngineTenant targetEngine = engineTenantDao.getByTenantIdAndEngineType(dtUicTenantId, engineType);

        if (targetEngine == null) {
            return new ArrayList<>();
        }
        List<Component> componentList = componentDao.listByEngineIds(Lists.newArrayList(targetEngine.getEngineId()));
        List<ComponentsConfigOfComponentsVO> componentsVOS = componentList.stream().map(c -> {
            ComponentsConfigOfComponentsVO componentsConfigOfComponentsVO = new ComponentsConfigOfComponentsVO();
            componentsConfigOfComponentsVO.setComponentTypeCode(c.getComponentTypeCode());
            componentsConfigOfComponentsVO.setComponentConfig(c.getComponentConfig());
            return componentsConfigOfComponentsVO;
        }).collect(Collectors.toList());
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

        if ( null != componentCode && EComponentType.noQueueComponents.contains(
                EComponentType.getByCode(componentCode))) {
            //tidb 和libra 没有queue
            List<EngineTenantVO> tenantVOS = engineTenantDao.listEngineTenant(engineId);
            if (CollectionUtils.isNotEmpty(tenantVOS)) {
                for (EngineTenantVO tenantVO : tenantVOS) {
                    if ( null != tenantVO &&  null != tenantVO.getTenantId()) {
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

    public List<Component> listComponent(List<Long> engineIds) {
        return componentDao.listByEngineIds(engineIds);
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
            if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
                throw new RdosDefineException("压缩包格式仅支持ZIP格式");
            }

            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
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
                    if (null != fileMap ) {
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
        return kerberosDao.getByComponentType(clusterId, componentType);
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
                                             Integer componentCode) {
        if (StringUtils.isBlank(componentConfig) && !EComponentType.KUBERNETES.getTypeCode().equals(componentCode)) {
            throw new RdosDefineException("组件信息不能为空");
        }
        if ( null == componentCode ) {
            throw new RdosDefineException("组件类型不能为空");
        }
        if ( null == clusterId ) {
            throw new RdosDefineException("集群Id不能为空");
        }
        ComponentDTO componentDTO = new ComponentDTO();
        componentDTO.setComponentConfig(componentConfig);
        componentDTO.setComponentTypeCode(componentCode);
        Cluster cluster = clusterDao.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        String clusterName = cluster.getClusterName();
        EComponentType componentType = EComponentType.getByCode(componentDTO.getComponentTypeCode());
        MultiEngineType engineType = EComponentType.getEngineTypeByComponent(componentType);
        Engine engine = engineDao.getByClusterIdAndEngineType(clusterId, engineType.getType());
        if ( null == engine ) {
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
        if (EComponentType.YARN.getTypeCode().equals(componentCode) || EComponentType.KUBERNETES.getTypeCode().equals(componentCode)) {
            Component resourceComponent = componentDao.getByClusterIdAndComponentType(clusterId,
                    EComponentType.YARN.getTypeCode().equals(componentCode) ? EComponentType.KUBERNETES.getTypeCode() : EComponentType.YARN.getTypeCode());
            if (null != resourceComponent ) {
                throw new RdosDefineException("资源组件只能选择单项");
            }
        }

        Component addComponent = new ComponentDTO();
        BeanUtils.copyProperties(componentDTO, addComponent);
        Component dbComponent = componentDao.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode());
        boolean isUpdate = false;
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (isOpenKerberos) {
            if (!resources.isEmpty() && !kerberosFileName.endsWith(ZIP_SUFFIX)) {
                throw new RdosDefineException("kerberos上传文件非zip格式");
            }
        }
        if (null != dbComponent ) {
            //更新
            isUpdate = true;
            if (!isOpenKerberos) {
                KerberosConfig componentKerberos = kerberosDao.getByComponentType(dbComponent.getId(), dbComponent.getComponentTypeCode());
                if (null != componentKerberos ) {
                    isOpenKerberos = true;
                }
            }
            addComponent = dbComponent;
        }
        if(EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode()) && CollectionUtils.isNotEmpty(resources)){
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

        // 获得sftp配置
        if (CollectionUtils.isNotEmpty(resources)) {
            Component sftpComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.SFTP.getTypeCode());
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpComponent,componentCode,componentConfig);
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent);
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

    public SftpConfig getSFTPConfig(Component sftpComponent, Integer componentCode,String componentConfig) {
        if (sftpComponent == null) {
            //  判断componentCode 是否是sftp的配置，如果是上传文件，如果不是 抛异常返回提交配置sftp服务器
            if ( EComponentType.SFTP.getTypeCode().equals(componentCode)) {
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
    public void closeKerberos( Long componentId) {
        kerberosDao.deleteByComponentId(componentId);
        Component updateComponent = new Component();
        updateComponent.setId(componentId);
        updateComponent.setKerberosFileName("");
        componentDao.update(updateComponent);
    }

    public ComponentsResultVO addOrCheckClusterWithName( String clusterName) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("集群名称不能为空");
        }
        clusterName = clusterName.trim();
        Cluster cluster = clusterDao.getByClusterName(clusterName);
        if ( null == cluster) {
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
    public List<Object> config( List<Resource> resources,  Integer componentType, Boolean autoDelete) {
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
            } else if(EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
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
            } else {
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
            }
        } finally {
            if (Objects.isNull(autoDelete) || autoDelete) {
                for (Resource resource : resources) {
                    try {
                        FileUtils.forceDelete(new File(resource.getUploadedFileName()));
                    } catch (IOException e) {
                        LOGGER.debug("delete config resource error {} ", resource.getUploadedFileName());
                    }
                }
            }

        }
        return datas;
    }


    public String buildSftpPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterDao.getOne(clusterId);
        if( null == one ){
            throw new RdosDefineException("集群不存在");
        }
        return AppType.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() ;
    }


    /**
     * 测试单个组件联通性
     */
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
        if ( null == componentTestResult ) {
            componentTestResult = new ComponentTestResult();
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg("测试联通性失败");
            return componentTestResult;
        }
        componentTestResult.setComponentTypeCode(componentType);
        if (componentTestResult.getResult() && null != engineId) {
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
                if(null != kerberosConfig){
                    replaceStr = env.getComponentJdbcToReplace();
                }
                jdbcUrl = jdbcUrl.replace("/%s", replaceStr);
            }

            dataInfo.put("jdbcUrl", jdbcUrl);
            dataInfo.put("username", dataInfo.getString("username"));
            dataInfo.put("password", dataInfo.getString("password"));
            if ( null != kerberosConfig ) {
                //开启了kerberos
                dataInfo.put("openKerberos", kerberosConfig.getOpenKerberos());
                dataInfo.put("remoteDir", kerberosConfig.getRemotePath());
                dataInfo.put("principalFile", kerberosConfig.getName());
                dataInfo.put("krbName", kerberosConfig.getKrbName());
                dataInfo.put("kerberosFileTimestamp",kerberosConfig.getGmtModified());
                //补充yarn参数
                putYarnConfig(clusterName, dataInfo);
            }
        } else if (EComponentType.YARN.getTypeCode() == componentType) {
            Map map = JSONObject.parseObject(componentConfig, Map.class);
            dataInfo.put(EComponentType.YARN.getConfName(), map);
        } else if (EComponentType.HDFS.getTypeCode() == componentType) {
            Map map = JSONObject.parseObject(componentConfig, Map.class);
            dataInfo.put(EComponentType.HDFS.getConfName(), map);
            //补充yarn参数
            putYarnConfig(clusterName, dataInfo);
        } else if (EComponentType.KUBERNETES.getTypeCode() == componentType) {
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
            Component yarnComponent = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
            if (null != yarnComponent) {
                Map yarnMap = JSONObject.parseObject(yarnComponent.getComponentConfig(), Map.class);
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
    public File downloadFile( Long componentId,  Integer downloadType,  Integer componentType,
                              String hadoopVersion,  String clusterName) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (null == componentId ) {
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(componentType, clusterName, hadoopVersion);
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
            if ( null == component ) {
                throw new RdosDefineException("组件不存在");
            }
            Long clusterId = componentDao.getClusterIdByComponentId(componentId);
            Component sftpComponent = componentDao.getByClusterIdAndComponentType(clusterId, EComponentType.SFTP.getTypeCode());
            if ( null == sftpComponent ) {
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
                if ( null == component.getUploadFileName()) {
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
            //将文件夹压缩成zip文件
            return zipFile(componentId, downloadType, componentType, file, zipFilename);
        } else {
            return new File(localDownLoadPath);
        }
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
    public List<ClientTemplate> loadTemplate( Integer componentType,  String clusterName,  String version) {

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
                        Map myData = new HashMap(16);
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
    public String convertComponentTypeToClient(String clusterName, Integer componentType, String version) {
        //普通rdb插件
        String pluginName = EComponentType.convertPluginNameByComponent(EComponentType.getByCode(componentType));
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
        //hive也需要version
        if (EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) || EComponentType.SPARK_THRIFT.getTypeCode().equals(componentType)) {
            pluginName = "hive";
            if (version.contains("cdh")) {
                //cdh为完整路径
                pluginName = pluginName + version;
            } else if (!version.equalsIgnoreCase("1.x")) {
                pluginName = pluginName + version.charAt(0);
            }
            return pluginName;
        }
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("集群名称不能为空");
        } else if (EComponentType.YARN.getTypeCode().equals(componentType)) {
            if (StringUtils.isBlank(version)) {
                throw new RdosDefineException("请选择集群版本");
            }
            //yarn是第一配置的
            ClusterVO cluster = clusterService.getClusterByName(clusterName);
            if ( null == cluster ) {
                //如果没有配置hdfs hdfs给默认值 和yarn保持一致
                return String.format("yarn%s-hdfs%s-hadoop%s", this.formatHadoopVersion(version),
                        this.formatHadoopVersion(version), this.formatHadoopVersion(version));
            }
            Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
            //hdfs  和 yarn 的版本要保持一致
            if ( null != hdfs ) {
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
        } else if (EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
            return "k8s-hdfs2-hadoop2";
        }

        ClusterVO cluster = clusterService.getClusterByName(clusterName);
        if ( null == cluster ) {
            throw new RdosDefineException("请先配置HDFS");
        }

        Component yarn = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode());
        Component kubernetes = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.KUBERNETES.getTypeCode());
        if ( null == yarn && null == kubernetes ) {
            throw new RdosDefineException("请先配置资源组件");
        }
        String resourceSign = null == yarn ? "k8s" : "yarn" + this.formatHadoopVersion(yarn.getHadoopVersion());
        if (EComponentType.HDFS.getTypeCode().equals(componentType)) {
            //hdfs  和 yarn 的版本要保持一致
            if(null != yarn ){
                if(!version.equalsIgnoreCase(yarn.getHadoopVersion())){
                    throw new RdosDefineException("hdfs 和 yarn 版本不一致");
                }
            }
            return String.format("%s-hdfs%s-hadoop%s", resourceSign, this.formatHadoopVersion(version), this.formatHadoopVersion(version));
        }
        Component hdfs = componentDao.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode());
        if (null == hdfs ) {
            throw new RdosDefineException("请先配置HDFS");
        }
        String storageSign = "hdfs" + this.formatHadoopVersion(hdfs.getHadoopVersion());
        //dtscript yarn2-hdfs2-dtscript
        if(EComponentType.DT_SCRIPT.getTypeCode().equals(componentType)){
            return String.format("%s-%s-dtscript",resourceSign,storageSign);
        }
        //learing yarn2-hdfs2-leanring
        if (EComponentType.LEARNING.getTypeCode().equals(componentType)) {
            return String.format("%s-%s-learning",resourceSign,storageSign);
        }
        //flink  需要根据yarn hdfs version 拼接 如yarn2-hdfs2-flink180;
        if (EComponentType.FLINK.getTypeCode().equals(componentType)) {
            //kubernetes 仅有110
            if( null != kubernetes ){
                version = "110";
            }
            return String.format("%s-%s-flink%s", resourceSign, storageSign, version);
        }
        if (EComponentType.SPARK.getTypeCode().equals(componentType)) {
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
    public void delete( List<Integer> componentIds) {
        if (CollectionUtils.isEmpty(componentIds)) {
            return;
        }
        try {
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
        } catch (Exception e) {
            throw new RdosDefineException("删除组件异常");
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
            if (!EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
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
                                component.getEngineId(), kerberosConfig, finalSftpMap);

                        if (refreshResult.getResult() && EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
                            engineService.updateResource(component.getEngineId(), refreshResult.getClusterResourceDescription());
                            queueService.updateQueue(component.getEngineId(), refreshResult.getClusterResourceDescription());
                        }

                    } catch (Exception e) {
                        refreshResult.setResult(false);
                        refreshResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                        LOGGER.error("refres {}  error ", component.getComponentConfig(), e);
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

        if(CollectionUtils.isEmpty(components)){
            return new ArrayList<>();
        }
        Map<String, String> sftpMap = getSftpMap(components);
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
                        if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode())) {
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

        if (null == cluster ) {
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
                .filter(c -> EComponentType.SFTP.getTypeCode().equals(c.getComponentTypeCode()))
                .findFirst();
        Map<String, String> sftpMap = null;
        try {
            if (componentOptional.isPresent()) {
                sftpMap = (Map) JSONObject.parseObject(componentOptional.get().getComponentConfig(), Map.class);
            }else{
                throw new RdosDefineException("缺少sftp组件");
            }
        } catch (Exception e) {
            LOGGER.error("getSftpMap error:{}",e.getMessage());
        }
        return sftpMap;
    }

}
