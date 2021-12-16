package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.AppType;
import com.dtstack.engine.common.enums.EComponentType;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.Xml2JsonUtil;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.domain.Component;
import com.dtstack.engine.domain.ComponentConfig;
import com.dtstack.engine.domain.KerberosConfig;
import com.dtstack.engine.dto.Resource;
import com.dtstack.engine.mapper.ClusterMapper;
import com.dtstack.engine.mapper.KerberosMapper;
import com.dtstack.engine.master.utils.ComponentConfigUtils;
import com.dtstack.engine.master.utils.FileUtil;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.utils.ResourceUtil;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.sftp.SftpConfig;
import com.dtstack.engine.pluginapi.sftp.SftpFileManage;
import com.dtstack.engine.pluginapi.util.MD5Util;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.*;
import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.KRB5_CONF;

@Service
public class ComponentFileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentFileService.class);

    @Autowired
    private EnvironmentContext env;
    @Autowired
    private ClusterMapper clusterMapper;
    @Autowired
    private KerberosMapper kerberosMapper;
    @Autowired
    private ScheduleDictService scheduleDictService;
    @Autowired
    private ComponentConfigService componentConfigService;
    @Autowired
    private SftpFileManage sftpFileManageBean;

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = Maps.newHashMap();

    private static final String HDFS_SITE_XML = "hdfs-site.xml";
    private static final String CORE_SITE_XML = "core-site.xml";
    private static final String HIVE_SITE_XML = "hive-site.xml";
    private static final String YARN_SITE_XML = "yarn-site.xml";

    static {
        //hdfs core 需要合并
        componentTypeConfigMapping.put(EComponentType.HDFS.getTypeCode(), Lists.newArrayList(HDFS_SITE_XML, CORE_SITE_XML, HIVE_SITE_XML));
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList(YARN_SITE_XML, CORE_SITE_XML));
    }

    /**
     *
     * @param clusterId
     * @param resources
     * @param kerberosFileName 本次上传的 kerberosFileName
     * @param sftpConfig sftp 配置
     * @param addComponent 当前新增的组件
     * @param dbComponent 当前组件对应的 DB 中的记录
     * @param principals
     * @param principal
     * @return
     */
    public String uploadResourceToSftp(Long clusterId,  List<Resource> resources,  String kerberosFileName,
                                       SftpConfig sftpConfig, Component addComponent, Component dbComponent,
                                       String principals,String principal) {
        //上传配置文件到sftp 供后续下载
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
        String md5sum = StringUtils.EMPTY;
        Integer componentTypeCode = addComponent.getComponentTypeCode();
        // sftpPath/CONSOLE_clusterName/EComponentName
        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, componentTypeCode);
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
                    // 上传配置文件到 sftp
                    this.updateComponentConfigFile(dbComponent, sftpFileManage, remoteDir, resource);
                    // confPath/CONSOLE_clusterName
                    String remoteConfPath = this.buildConfRemoteDir(clusterId);
                    if(EComponentType.HDFS.getTypeCode().equals(componentTypeCode)){
                        String xmlZipLocation = resource.getUploadedFileName();
                        md5sum = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        // 上传配置文件到远端 confPath/CONSOLE_clusterName
                        this.updateConfigToSftpPath(sftpConfig, resource,
                                null, componentTypeCode,
                                remoteConfPath);
                    }
                    if(EComponentType.YARN.getTypeCode().equals(componentTypeCode)){
                        List<ComponentConfig> clientTemplates = scheduleDictService
                                .loadExtraComponentConfig(addComponent.getHadoopVersion(), componentTypeCode);
                        this.updateConfigToSftpPath(sftpConfig,
                                resource,clientTemplates, componentTypeCode,
                                remoteConfPath);
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
     * 生成远程文件路径，形如 confPath/CONSOLE_clusterName
     *
     * @param clusterId
     * @return
     */
    public String buildConfRemoteDir(Long clusterId) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return USER_CONF_PATH + File.separator + AppType.CONSOLE + SPLIT + one.getClusterName();
    }

    /**
     * 生成组件对应的文件夹，形如 CONSOLE_clusterName/EComponentName
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String buildSftpPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return AppType.CONSOLE + SPLIT + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name();
    }

    /**
     * 获取 SftpFileManage
     *
     * @param sftpConfigStr
     * @param componentCode
     * @param componentTemplate
     * @return
     */
    public SftpFileManage getSftpFileManage(String sftpConfigStr, Integer componentCode, String componentTemplate) {
        SftpConfig sftpConfig = ComponentConfigUtils.getSFTPConfig(sftpConfigStr, componentCode, componentTemplate);
        return sftpFileManageBean.retrieveSftpManager(sftpConfig);
    }

    /**
     * 上传配置文件到 sftp
     *
     * @param dbComponent
     * @param sftpFileManage
     * @param remoteDir
     * @param resource
     */
    public void updateComponentConfigFile(Component dbComponent, SftpFileManage sftpFileManage, String remoteDir, Resource resource) {
        //原来配置
        String deletePath = remoteDir + File.separator;
        LOGGER.info("upload config file to sftp:{}",deletePath);
        if (Objects.nonNull(dbComponent) && StringUtils.isNotEmpty(dbComponent.getUploadFileName())) {
            deletePath = deletePath + dbComponent.getUploadFileName();
            //删除远端原来的文件配置zip
            LOGGER.info("delete file :{}",deletePath);
            sftpFileManage.deleteFile(deletePath);
        }
        // 本地文件上传到远端
        sftpFileManage.uploadFile(remoteDir, resource.getUploadedFileName());
        String oldPath = remoteDir + File.separator
                + resource.getUploadedFileName().substring(resource.getUploadedFileName().lastIndexOf(File.separator) + 1);
        String newPath = remoteDir + File.separator + resource.getFileName();
        //更新为原名
        sftpFileManage.renamePath(oldPath, newPath);
    }

    /**
     * 上传本地的四个 xml 到 sftp，作为 spark，作为confHdfs
     */
    public void updateConfigToSftpPath(SftpConfig sftpConfig, Resource resource,
                                         List<ComponentConfig> templates, Integer componentType,
                                        String remotePath) {
        //上传xml到对应路径下 拼接confHdfsPath
        String confRemotePath = sftpConfig.getPath() + File.separator;
        String buildPath = File.separator + remotePath;
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
                SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);
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
     * 解析 zip 中 xml 或者 json
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
                return ResourceUtil.parseXmlFileConfig(resources, xmlName);
            } else if (EComponentType.KUBERNETES.getTypeCode().equals(componentType)) {
                //解析k8s组件
                return ResourceUtil.parseKubernetesData(resources);
            } else {
                //解析上传的json文件
                return ResourceUtil.parseJsonFile(resources);
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

    @Transactional(rollbackFor = Exception.class)
    public String uploadKerberos(Resource resource, Long clusterId, Integer componentCode,String componentVersion, String sftpComponent) {
        if (StringUtils.isEmpty(sftpComponent)) {
            throw new RdosDefineException("sftpComponent should not be empty");
        }
        SftpConfig sftpConfig = ComponentConfigUtils.getSFTPConfig(sftpComponent, componentCode, StringUtils.EMPTY);
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        String remoteDir = sftpConfig.getPath() + File.separator + buildSftpPath(clusterId, componentCode);
        Component addComponent = new Component();
        addComponent.setComponentTypeCode(componentCode);
        addComponent.setHadoopVersion(componentVersion);
        updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, null, null);

        List<KerberosConfig> kerberosConfigs = kerberosMapper.listAll();
        return mergeKrb5(kerberosConfigs);
    }

    /**
     * 解压kerberos文件到本地 并上传至sftp
     * @param clusterId
     * @param addComponent
     * @param sftpFileManage
     * @param remoteDir
     * @param resource
     * @param principals
     * @param principal
     * @return
     */
    public String updateComponentKerberosFile(Long clusterId, Component addComponent, SftpFileManage sftpFileManage,
                                              String remoteDir, Resource resource,
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

            // 获取 principal
            List<PrincipalName> principalLists = FileUtil.getPrincipal(keyTabFile);
            principal = ComponentService.parsePrincipal(principal, principalLists);
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
        String componentVersion = addComponent.getHadoopVersion();
        // 更新数据库 kerberos 信息
        this.updateKerberos(clusterId, addComponent, principals, principal, keyTabFile, krb5ConfFile, remoteDirKerberos, componentVersion);
        return remoteDirKerberos;
    }

    /**
     * 获取本地 kerberos 配置地址，如 /kerberosUploadTempDir/CONSOLE_clusterName/EComponentTypeName/kerberos
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new RdosDefineException("Cluster does not exist");
        }
        return env.getLocalKerberosDir() + File.separator + AppType.CONSOLE + SPLIT
                + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() + File.separator + KERBEROS_PATH;
    }

    private synchronized String mergeKrb5(List<KerberosConfig> kerberosConfigs) {
        String mergeKrb5Content = "";
        if (CollectionUtils.isEmpty(kerberosConfigs)) {
            LOGGER.error("KerberosConfigs is null");
            return mergeKrb5Content;
        }

        String mergeDirPath = ConfigConstant.LOCAL_KRB5_MERGE_DIR_PARENT + ConfigConstant.SP + UUID.randomUUID();
        List<Long> clusterDownloadRecords = new ArrayList();
        String oldMergeKrb5Content = null;
        String mergeKrb5Path = mergeDirPath + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
        List<Long> clusterIds = kerberosConfigs.stream().map(KerberosConfig::getClusterId).collect(Collectors.toList());
        Map<Long, String> clusterIdToSftpConfig = querySftpConfig(clusterIds);
        try {
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
                    String sftpComponent = clusterIdToSftpConfig.get(clusterId);
                    SftpFileManage sftpFileManage = getSftpFileManage(sftpComponent, componentCode, StringUtils.EMPTY);

                    if (clusterDownloadRecords.contains(clusterId)) {
                        // 打包的问题
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
                kerberosMapper.updateById(kerberosConfig);
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

    private Map<Long, String> querySftpConfig(List<Long> clusterIds) {
        if (CollectionUtils.isEmpty(clusterIds)) {
            return Collections.emptyMap();
        }
        Map<Long, String> clusterIdToSftpConfig = Maps.newHashMapWithExpectedSize(clusterIds.size());
        for (Long clusterId : clusterIds) {
            Map<String, Object> configMap = componentConfigService.getCacheComponentConfigMap(clusterId,
                    EComponentType.SFTP.getTypeCode(), false,null,null);
            if (MapUtils.isEmpty(configMap)) {
                continue;
            }
            String configStr = JSONObject.toJSONString(configMap);
            clusterIdToSftpConfig.put(clusterId, configStr);
        }
        return clusterIdToSftpConfig;
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

    private void updateKerberos(Long clusterId, Component addComponent, String principals, String principal, File keyTabFile, File krb5ConfFile, String remoteDirKerberos, String componentVersion) {
        KerberosConfig kerberosConfig = kerberosMapper.getByComponentType(clusterId, addComponent.getComponentTypeCode(),
                ComponentVersionUtil.formatMultiVersion(addComponent.getComponentTypeCode(), componentVersion));
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
            kerberosMapper.insert(kerberosConfig);
        } else {
            kerberosMapper.updateById(kerberosConfig);
        }
    }
}
