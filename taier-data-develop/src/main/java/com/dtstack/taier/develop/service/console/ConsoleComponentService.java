/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.develop.service.console;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.dtstack.taier.common.enums.DictType;
import com.dtstack.taier.common.enums.DownloadType;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EFrontType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.common.thread.RdosThreadFactory;
import com.dtstack.taier.common.util.ComponentVersionUtil;
import com.dtstack.taier.common.util.Pair;
import com.dtstack.taier.common.util.Xml2JsonUtil;
import com.dtstack.taier.common.util.ZipUtil;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.Component;
import com.dtstack.taier.dao.domain.ComponentConfig;
import com.dtstack.taier.dao.domain.Dict;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.dto.Resource;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.dao.mapper.ConsoleKerberosMapper;
import com.dtstack.taier.develop.model.ClusterFactory;
import com.dtstack.taier.develop.model.Part;
import com.dtstack.taier.develop.model.PartCluster;
import com.dtstack.taier.develop.model.system.Context;
import com.dtstack.taier.develop.model.system.config.ComponentModel;
import com.dtstack.taier.develop.vo.console.ComponentModelVO;
import com.dtstack.taier.pluginapi.constrant.ConfigConstant;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.ComponentTestResult;
import com.dtstack.taier.pluginapi.sftp.SftpConfig;
import com.dtstack.taier.pluginapi.sftp.SftpFileManage;
import com.dtstack.taier.pluginapi.util.MD5Util;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.dtstack.taier.scheduler.executor.DatasourceOperator;
import com.dtstack.taier.scheduler.impl.pojo.ClientTemplate;
import com.dtstack.taier.scheduler.service.ComponentConfigService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.service.ScheduleDictService;
import com.dtstack.taier.scheduler.utils.ComponentConfigUtils;
import com.dtstack.taier.scheduler.utils.Krb5FileUtil;
import com.dtstack.taier.scheduler.utils.XmlFileUtil;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import com.google.common.collect.Lists;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.dtstack.taier.common.constant.CommonConstant.ZIP_SUFFIX;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.KERBEROS;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.KEYTAB_SUFFIX;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.KRB5_CONF;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.MD5_SUM_KEY;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.TYPE_NAME_KEY;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.USER_DIR_DOWNLOAD;
import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.USER_DIR_UNZIP;


@org.springframework.stereotype.Component
public class ConsoleComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;

    @Autowired
    private DatasourceOperator datasourceOperator;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private SftpFileManage sftpFileManageBean;

    @Autowired
    private ClusterFactory clusterFactory;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private Context context;

    /**
     * 组件配置文件映射
     */
    public static Map<Integer, List<String>> componentTypeConfigMapping = new HashMap<>(2);

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 10,
            100, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
            new RdosThreadFactory("test-connect"), new ThreadPoolExecutor.DiscardOldestPolicy());


    static {
        //hdfs core 需要合并
        componentTypeConfigMapping.put(EComponentType.HDFS.getTypeCode(), Lists.newArrayList("hdfs-site.xml", "core-site.xml"));
        componentTypeConfigMapping.put(EComponentType.YARN.getTypeCode(), Lists.newArrayList("yarn-site.xml", "core-site.xml"));
    }


    @Transactional(rollbackFor = Exception.class)
    public ComponentVO addOrUpdateComponent(Long clusterId, String componentConfig,
                                            List<Resource> resources, String versionName,
                                            String kerberosFileName,
                                            EComponentType componentType, Integer storeType,
                                            String principals, String principal, boolean isMetadata, Boolean isDefault, Integer deployType) {

        EComponentType storeComponent = null == storeType ? null : EComponentType.getByCode(storeType);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = partCluster.create(componentType, versionName, storeComponent, deployType);
        String versionValue = part.getVersionValue();
        String pluginName = part.getPluginName();
        List<ComponentConfig> templateConfig = part.loadTemplate();
        Component componentDTO = new Component();
        componentDTO.setComponentTypeCode(componentType.getTypeCode());
        Component addComponent = new Component();
        BeanUtils.copyProperties(componentDTO, addComponent);
        // 判断是否是更新组件, 需要校验组件版本
        Component dbComponent = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(),
                ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? versionValue : null, deployType);
        String dbHadoopVersion = "";
        boolean isUpdate = false;
        if (null != dbComponent) {
            //更新
            dbHadoopVersion = dbComponent.getVersionValue();
            addComponent = dbComponent;
            isUpdate = true;
        }

        addComponent.setStoreType(storeType);
        addComponent.setVersionValue(versionValue);
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setDeployType(deployType);
        addComponent.setDatasourceType(context.getDataSourceTypeByComponentAndVersion(componentType.getTypeCode(), versionName));

        addComponent.setVersionName(versionName);

        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }
        changeDefault(BooleanUtils.isTrue(isDefault), clusterId, componentType, addComponent);

        // md5zip 会作为 config 表的一个属性
        String md5Key = updateResource(clusterId, componentConfig, resources, kerberosFileName, componentType.getTypeCode(), principals, principal, addComponent, dbComponent);
        addComponent.setClusterId(clusterId);
        if (isUpdate) {
            componentMapper.updateById(addComponent);
            refreshVersion(componentType, clusterId, addComponent, dbHadoopVersion);
            clusterMapper.updateGmtModified(clusterId);
        } else {
            componentMapper.insert(addComponent);
        }

        List<ComponentConfig> componentConfigs = buildConfigs(componentType, componentConfig, md5Key, pluginName, templateConfig,
                addComponent.getId(), addComponent.getClusterId());
        componentConfigService.addOrUpdateComponentConfig(addComponent.getId(), addComponent.getClusterId(), componentType.getTypeCode(), componentConfigs);
        this.updateCache();
        return ComponentVO.toVO(addComponent);
    }


    private List<ComponentConfig> buildConfigs(EComponentType componentType, String componentString, String md5Key, String pluginName,
                                               List<ComponentConfig> templateConfig, Long componentId, Long clusterId) {
        List<ComponentConfig> configs = new ArrayList<>();
        JSONObject componentConfigJSON = JSONObject.parseObject(componentString);
        //添加typeName
        configs.add(ComponentConfigUtils.buildOthers(TYPE_NAME_KEY, pluginName, componentId, clusterId, componentType.getTypeCode()));
        if (!StringUtils.isBlank(md5Key)) {
            configs.add(ComponentConfigUtils.buildOthers(MD5_SUM_KEY, md5Key, componentId, clusterId, componentType.getTypeCode()));
        }
        if (EComponentType.noControlComponents.contains(componentType)) {
            //xml配置文件也转换为组件
            configs.addAll(componentConfigJSON.keySet().stream().map(key ->
                    ComponentConfigUtils.buildCustomConfig(key, componentConfigJSON.getString(key), EFrontType.CUSTOM_CONTROL.name(),
                            null, componentId, clusterId, componentType.getTypeCode())).collect(Collectors.toList()));
        } else {
            configs.addAll(ComponentConfigUtils.fillTemplateValue(componentConfigJSON, templateConfig, componentId, clusterId, componentType.getTypeCode()));
        }
        return configs;
    }


    /**
     * yarn组件版本变更之后  hdfs组件保存一致
     * 计算组件 如flink的typename也同步变更
     *
     * @param componentType
     * @param clusterId
     * @param addComponent
     * @param dbHadoopVersion 形如 3.1.1
     */
    public void refreshVersion(EComponentType componentType, Long clusterId, Component addComponent, String dbHadoopVersion) {
        if (!EComponentType.YARN.equals(componentType)) {
            return;
        }
        if (addComponent.getVersionValue().equals(dbHadoopVersion)) {
            return;
        }
        List<Component> components = componentMapper.listByClusterId(clusterId, null, false);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        for (Component component : components) {
            EComponentType eComponentType = EComponentType.getByCode(component.getComponentTypeCode());
            if (!EComponentType.typeComponentVersion.contains(eComponentType)) {
                continue;
            }
            EComponentType storeComponentType = EComponentType.getByCode(component.getStoreType());
            Part part = partCluster.create(eComponentType, component.getVersionName(), storeComponentType, component.getDeployType());
            String pluginName = part.getPluginName();
            ComponentConfig typeNameComponentConfig = componentConfigService.getComponentConfigByKey(component.getId(), TYPE_NAME_KEY);
            if (null != typeNameComponentConfig && StringUtils.isNotBlank(pluginName)) {
                LOGGER.info("refresh clusterId {} component {} typeName {} to {}", component.getClusterId(), component.getComponentName(), typeNameComponentConfig.getValue(), pluginName);
                typeNameComponentConfig.setValue(pluginName);
                componentConfigService.updateValueComponentConfig(typeNameComponentConfig);
            }
        }
    }

    /**
     * @param isDefault
     * @param clusterId
     * @param componentType
     */
    private int changeDefault(boolean isDefault, Long clusterId, EComponentType componentType, Component updateComponent) {
        if (!EComponentType.multiVersionComponents.contains(componentType)) {
            updateComponent.setIsDefault(true);
            return -1;
        }
        updateComponent.setIsDefault(isDefault);
        if (!isDefault) {
            List<Component> dbComponents = componentMapper.listByClusterId(clusterId, componentType.getTypeCode(), false);
            Set<Long> dbComponentId = dbComponents.stream().map(Component::getId).collect(Collectors.toSet());
            dbComponentId.remove(updateComponent.getId());
            if (dbComponentId.size() == 0) {
                // single component must be default
                updateComponent.setIsDefault(true);
            }
        }
        return componentMapper.updateDefault(clusterId, componentType.getTypeCode(), !isDefault);
    }


    private String updateResource(Long clusterId, String componentConfig, List<Resource> resources, String kerberosFileName, Integer componentCode, String principals, String principal, Component addComponent, Component dbComponent) {
        //上传资源依赖sftp组件
        String md5Key = "";
        if (CollectionUtils.isNotEmpty(resources)) {
            String sftpConfigStr = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = getSFTPConfig(sftpConfigStr, componentCode, componentConfig);
            md5Key = uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent, principals, principal);
        } else if (CollectionUtils.isEmpty(resources) && StringUtils.isNotBlank(principal)) {
            //直接更新认证信息
            KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, addComponent.getComponentTypeCode(),
                    ComponentVersionUtil.isMultiVersionComponent(addComponent.getComponentTypeCode()) ?
                            StringUtils.isNotBlank(addComponent.getVersionValue()) ? addComponent.getVersionValue() :
                                    componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentCode) : null);
            if (null != kerberosConfig) {
                kerberosConfig.setPrincipal(principal);
                kerberosConfig.setPrincipals(principals);
                consoleKerberosMapper.updateById(kerberosConfig);
            }
        }
        return md5Key;
    }


    private String uploadResourceToSftp(Long clusterId, List<Resource> resources, String kerberosFileName,
                                        SftpConfig sftpConfig, Component addComponent, Component dbComponent, String principals, String principal) {
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
                    LOGGER.info("start upload hadoop config file:{}", kerberosFileName);
                    this.updateComponentConfigFile(dbComponent, sftpFileManage, remoteDir, resource);
                    if (EComponentType.HDFS.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                        String xmlZipLocation = resource.getUploadedFileName();
                        md5sum = MD5Util.getFileMd5String(new File(xmlZipLocation));
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource, null, addComponent.getComponentTypeCode());
                    }
                    if (EComponentType.YARN.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                        List<ComponentConfig> clientTemplates = scheduleDictService
                                .loadExtraComponentConfig(addComponent.getVersionValue(), addComponent.getComponentTypeCode());
                        this.updateConfigToSftpPath(clusterId, sftpConfig, sftpFileManage, resource, clientTemplates, addComponent.getComponentTypeCode());
                    }
                }
            } catch (Exception e) {
                LOGGER.error("update component resource {}  error", resource.getUploadedFileName(), e);
                if (e instanceof TaierDefineException) {
                    throw (TaierDefineException) e;
                } else {
                    throw new TaierDefineException("Failed to update component");
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

    public SftpConfig getSFTPConfig(String sftpConfigStr, Integer componentCode, String componentTemplate) {
        if (StringUtils.isBlank(sftpConfigStr)) {
            //  判断componentCode 是否是sftp的配置，如果是上传文件，如果不是 抛异常返回提交配置sftp服务器
            if (EComponentType.SFTP.getTypeCode().equals(componentCode)) {
                // 是sftp的配置
                try {
                    Map<String, Object> configMap = ComponentConfigUtils.convertClientTemplateToMap(JSONArray.parseArray(componentTemplate, ClientTemplate.class));
                    return PublicUtil.mapToObject(configMap, SftpConfig.class);
                } catch (IOException e) {
                    throw new TaierDefineException("sftp配置信息不正确");
                }
            } else {
                throw new TaierDefineException("Please configure the sftp server to upload files!");
            }
        } else {
            return JSONObject.parseObject(sftpConfigStr, SftpConfig.class);
        }
    }

    /**
     * 上传四个xml到sftp 作为spark 作为confHdfsPath
     *
     * @param clusterId
     * @param resource
     */
    private void updateConfigToSftpPath(Long clusterId, SftpConfig sftpConfig, SftpFileManage sftpFileManage, Resource resource,
                                        List<ComponentConfig> templates, Integer componentType) {
        //上传xml到对应路径下 拼接confHdfsPath
        String confRemotePath = sftpConfig.getPath() + File.separator;
        String buildPath = File.separator + componentService.buildConfRemoteDir(clusterId);
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
        List<String> fileNames = componentTypeConfigMapping.get(componentType);
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
        String remoteDirKerberos = remoteDir + File.separator + KERBEROS;
        if (resource != null) {
            // kerberos认证文件 远程删除 kerberos下的文件
            LOGGER.info("updateComponentKerberosFile remote path:{}", remoteDirKerberos);
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
                throw new TaierDefineException("Hadoop-Kerberos file decompression error");
            }

            keyTabFile = files.stream().filter(f -> f.getName().endsWith(KEYTAB_SUFFIX)).findFirst().orElse(null);
            krb5ConfFile = files.stream().filter(f -> f.getName().equalsIgnoreCase(KRB5_CONF)).findFirst().orElse(null);
            if (keyTabFile == null) {
                throw new TaierDefineException("There must be a keytab file in the zip file of the uploaded Hadoop-Kerberos file, please add the keytab file");
            }
            LOGGER.info("fileKeyTab Unzip fileName:{}", keyTabFile.getAbsolutePath());
            if (krb5ConfFile == null) {
                throw new TaierDefineException("There must be a krb5.conf file in the zip file of the uploaded Hadoop-Kerberos file, please add the krb5.conf file");
            }
            LOGGER.info("conf Unzip fileName:{}", krb5ConfFile.getAbsolutePath());

            //获取principal
            List<PrincipalName> principalLists = this.getPrincipal(keyTabFile);
            principal = parsePrincipal(principal, principalLists);
            if (StringUtils.isEmpty(principals)) {
                List<String> principalNames = new ArrayList<>();
                for (PrincipalName principalName : principalLists) {
                    principalNames.add(principalName.getName());
                }
                principals = StringUtils.join(principalNames, ",");
            }

            //删除sftp原来kerberos 的文件夹
            sftpFileManage.deleteDir(remoteDirKerberos);
            //上传kerberos解压后的文件
            for (File file : files) {
                LOGGER.info("upload sftp file:{}", file.getAbsolutePath());
                sftpFileManage.uploadFile(remoteDirKerberos, file.getPath());
            }
        }
        String versionName = addComponent.getVersionName();
        String componentVersion = "";
        if (StringUtils.isNotBlank(versionName) && ComponentVersionUtil.isMultiVersionComponent(addComponent.getComponentTypeCode())) {
            //
            DictType dictType = null;
            if (EComponentType.SPARK.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                dictType = DictType.SPARK_VERSION;
            } else if (EComponentType.FLINK.getTypeCode().equals(addComponent.getComponentTypeCode())) {
                dictType = DictType.FLINK_VERSION;
            }
            if (null != dictType) {
                Dict dict = scheduleDictService.getByNameAndValue(dictType.getType(), versionName, null, null);
                if (null != dict) {
                    componentVersion = dict.getDictValue();
                }
            }
        }

        //更新数据库kerberos信息
        KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, addComponent.getComponentTypeCode(), componentVersion);
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
            consoleKerberosMapper.insert(kerberosConfig);
        } else {
            consoleKerberosMapper.updateById(kerberosConfig);
        }
        return remoteDirKerberos;
    }

    private String parsePrincipal(String principal, List<PrincipalName> principalLists) {
        if (CollectionUtils.isEmpty(principalLists)) {
            throw new TaierDefineException("The keytab file does not contain principal");
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
                throw new TaierDefineException(String.format("The uploaded Hadoop-Kerberos file does not contain the corresponding %s", principal));
            }
        }
        return principal;
    }

    /**
     * 获取本地kerberos配置地址
     *
     * @param clusterId
     * @param componentCode
     * @return
     */
    public String getLocalKerberosPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return env.getTempDir() + File.separator + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name() + File.separator + KERBEROS;
    }


    /**
     * 解析对应的kerberos的zip中principle
     *
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
                throw new TaierDefineException("Hadoop-Kerberos file decompression error");
            }

            File fileKeyTab = files
                    .stream()
                    .filter(f -> f.getName().endsWith(KEYTAB_SUFFIX))
                    .findFirst()
                    .orElse(null);
            if (fileKeyTab == null) {
                throw new TaierDefineException("There must be a keytab file in the zip file of the uploaded Hadoop-Kerberos file, please add the keytab file");
            }

            //获取principal
            List<PrincipalName> principal = this.getPrincipal(fileKeyTab);
            return principal
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

    public void updateCache() {
        componentConfigService.clearComponentCache();
    }

    private List<PrincipalName> getPrincipal(File file) {
        if (null != file) {
            Keytab keytab = null;
            try {
                keytab = Keytab.loadKeytab(file);
            } catch (IOException e) {
                LOGGER.error("Keytab loadKeytab error ", e);
                throw new TaierDefineException("Failed to parse keytab file");
            }
            return keytab.getPrincipals();
        }
        throw new TaierDefineException("The current keytab file does not contain principal information");
    }

    public String buildSftpPath(Long clusterId, Integer componentCode) {
        Cluster one = clusterMapper.getOne(clusterId);
        if (null == one) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        return ConfigConstant.CONSOLE + "_" + one.getClusterName() + File.separator + EComponentType.getByCode(componentCode).name();
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
        LOGGER.info("upload config file to sftp:{}", deletePath);
        if (Objects.nonNull(dbComponent)) {
            deletePath = deletePath + dbComponent.getUploadFileName();
            //删除原来的文件配置zip 如果dbComponent不为null ,删除文件。
            LOGGER.info("delete file :{}", deletePath);
            sftpFileManage.deleteFile(deletePath);
        }

        //更新为原名
        sftpFileManage.uploadFile(remoteDir, resource.getUploadedFileName());
        sftpFileManage.renamePath(remoteDir + File.separator + resource.getUploadedFileName().substring(resource.getUploadedFileName().lastIndexOf(File.separator) + 1), remoteDir + File.separator + resource.getFileName());
    }


    private File getFileWithSuffix(String dir, String suffix) {
        if (StringUtils.isBlank(suffix)) {
            throw new TaierDefineException("File suffix cannot be empty");
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

    @Transactional(rollbackFor = Exception.class)
    public String uploadKerberos(List<Resource> resources, Long clusterId, Integer componentCode, String versionName) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new TaierDefineException("Please upload a kerberos file!");
        }

        Resource resource = resources.get(0);
        String kerberosFileName = resource.getFileName();
        if (!kerberosFileName.endsWith(ZIP_SUFFIX)) {
            throw new TaierDefineException("Kerberos upload files are not in zip format");
        }
        String sftpComponent = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
        SftpConfig sftpConfig = getSFTPConfig(sftpComponent, componentCode, "");
        SftpFileManage sftpFileManage = sftpFileManageBean.retrieveSftpManager(sftpConfig);

        String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, componentCode);
        Component addComponent = new Component();
        addComponent.setComponentTypeCode(componentCode);
        addComponent.setVersionName(versionName);
        updateComponentKerberosFile(clusterId, addComponent, sftpFileManage, remoteDir, resource, null, null);

        List<KerberosConfig> kerberosConfigs = consoleKerberosMapper.listAll();
        return mergeKrb5(kerberosConfigs);
    }


    private synchronized String mergeKrb5(List<KerberosConfig> kerberosConfigs) {
        String mergeKrb5Content = "";
        if (CollectionUtils.isEmpty(kerberosConfigs)) {
            LOGGER.error("KerberosConfigs is null");
            return mergeKrb5Content;
        }

        String mergeDirPath = ConfigConstant.LOCAL_KRB5_MERGE_DIR_PARENT + ConfigConstant.SP + UUID.randomUUID();
        List<Long> clusterDownloadRecords = new ArrayList<>();
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
                    String sftpComponent = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class, null);
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
                consoleKerberosMapper.updateById(kerberosConfig);
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
            List<KerberosConfig> kerberosConfigs = consoleKerberosMapper.listAll();
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                String remotePath = kerberosConfig.getRemotePath();
                kerberosConfig.setMergeKrbContent(krb5Content);
                consoleKerberosMapper.updateById(kerberosConfig);
                LOGGER.info("Update krb5 remotePath {}", remotePath);
            }
        } catch (Exception e) {
            LOGGER.error("Update krb5 error! {}", e.getMessage());
            throw new TaierDefineException(e);
        }
    }

    /**
     * 移除kerberos配置
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void closeKerberos(Long componentId) {
        // 删除kerberos配置需要版本号
        Component component = componentMapper.selectById(componentId);
        if (Objects.isNull(component)) {
            return;
        }
        consoleKerberosMapper.deleteByComponentId(component.getId());
        Component updateComponent = new Component();
        updateComponent.setId(componentId);
        updateComponent.setKerberosFileName("");
        componentMapper.updateById(updateComponent);
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

    private List<Object> parseXmlFileConfig(List<Resource> resources, List<String> xmlName) {
        List<Object> datas = new ArrayList<>();
        Map<String, Map<String, Object>> xmlConfigMap = this.parseUploadFileToMap(resources);
        boolean isLostXmlFile = xmlConfigMap.keySet().containsAll(xmlName);
        if (!isLostXmlFile) {
            LOGGER.error("Missing necessary configuration file, maybe the Zip file corrupt, please retry zip files.");
            throw new TaierDefineException("Missing necessary configuration file, maybe the Zip file corrupt, please retry zip files.");
        }
        //多个配置文件合并为一个map
        if (MapUtils.isNotEmpty(xmlConfigMap)) {
            Map<String, Object> data = new HashMap<>();
            for (String key : xmlConfigMap.keySet()) {
                data.putAll(xmlConfigMap.get(key));
            }
            datas.add(data);
        }
        return datas;
    }


    /**
     * 加载各个组件的默认值
     * 解析yml文件转换为前端渲染格式
     *
     * @param componentType 组件类型
     * @param clusterId     集群ID
     * @param versionName   组件版本名 如Hadoop 2.x
     * @param storeType     存储组件type 如 HDFS
     * @return
     */
    public List<ClientTemplate> loadTemplate(Long clusterId, EComponentType componentType, String versionName, EComponentType storeType, Integer deployType) {
        PartCluster cluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = cluster.create(componentType, versionName, storeType, deployType);
        List<ComponentConfig> componentConfigs = part.loadTemplate();
        return componentConfigs.stream().map(c -> {
            ClientTemplate clientTemplate = new ClientTemplate();
            BeanUtils.copyProperties(c, clientTemplate);
            return clientTemplate;
        }).collect(Collectors.toList());
    }

    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
        if (null == cluster) {
            throw new TaierDefineException("Cluster does not exist");
        }
        List<Component> components = new ArrayList<>();
        Component hdfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(), null, null);
        if (null != hdfs) {
            components.add(hdfs);
        }
        return components;
    }

    public ComponentTestResult testConnect(Long clusterId, Integer componentType, String versionName) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            throw new TaierDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        Component testComponent = componentMapper.getByVersionName(cluster.getId(), componentType, versionName, null);
        if (null == testComponent) {
            throw new TaierDefineException(ErrorCode.COMPONENT_INVALID);
        }
        Map sftpMap = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class, null);
        return testComponentWithResult(cluster, sftpMap, testComponent);
    }

    /**
     * 测试所有组件连通性
     *
     * @return
     */
    public List<ComponentTestResult> testConnects(Long clusterId) {

        Cluster cluster = clusterMapper.getOne(clusterId);
        List<Component> components = getComponents(cluster);
        if (CollectionUtils.isEmpty(components)) {
            return new ArrayList<>();
        }

        Map sftpMap = componentService.getComponentByClusterId(cluster.getId(), EComponentType.SFTP.getTypeCode(), false, Map.class, null);

        Map<Component, CompletableFuture<ComponentTestResult>> completableFutureMap = components.stream()
                .collect(Collectors.toMap(component -> component,
                        c -> CompletableFuture.supplyAsync(() -> testComponentWithResult(cluster, sftpMap, c), executor)));

        CompletableFuture<Void> totalFuture = CompletableFuture.allOf(completableFutureMap.values().toArray(new CompletableFuture[0]));
        try {
            totalFuture.get(env.getTestConnectTimeout(), TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("get components testConnects time out", e);
        }

        return completableFutureMap.keySet().stream().map(component -> {
            ComponentTestResult testResult = new ComponentTestResult();
            try {
                testResult.setResult(false);
                testResult.setErrorMsg("connect timeout");
                return completableFutureMap.get(component).getNow(testResult);
            } catch (Exception e) {
                testResult.setResult(false);
                testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
                testResult.setVersionName(component.getVersionName());
                testResult.setComponentTypeCode(component.getComponentTypeCode());
                return testResult;
            }
        }).collect(Collectors.toList());

    }

    private ComponentTestResult testComponentWithResult(Cluster cluster, Map sftpMap, Component component) {
        ComponentTestResult testResult = new ComponentTestResult();
        try {
            KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(cluster.getId(), component.getComponentTypeCode(),
                    ComponentVersionUtil.isMultiVersionComponent(component.getComponentTypeCode()) ?
                            StringUtils.isNotBlank(component.getVersionValue()) ? component.getVersionValue() : componentMapper.getDefaultComponentVersionByClusterAndComponentType(cluster.getId(), component.getComponentTypeCode()) : null);
            String componentConfig = componentService.getComponentByClusterId(cluster.getId(), component.getComponentTypeCode(), false, String.class, null);
            testResult = this.testConnect(component.getComponentTypeCode(), componentConfig, component.getVersionName(), component.getClusterId(), kerberosConfig, sftpMap);
        } catch (Exception e) {
            testResult.setResult(false);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            LOGGER.error("test connect {}  error ", component.getId(), e);
        } finally {
            testResult.setVersionName(component.getVersionName());
            testResult.setComponentTypeCode(component.getComponentTypeCode());
        }
        return testResult;
    }

    private List<Component> getComponents(Cluster cluster) {

        if (null == cluster) {
            throw new TaierDefineException("Cluster does not exist");
        }

        List<Component> components = componentMapper.listByClusterId(cluster.getId(), null, false);
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyList();
        }
        return components;
    }


    private Map<String, Map<String, Object>> parseUploadFileToMap(List<Resource> resources) {

        if (CollectionUtils.isEmpty(resources)) {
            throw new TaierDefineException("The uploaded file cannot be empty");
        }

        Resource resource = resources.get(0);
        if (!resource.getFileName().endsWith(ZIP_SUFFIX)) {
            throw new TaierDefineException("The compressed package format only supports ZIP format");
        }

        String upzipLocation = USER_DIR_UNZIP + File.separator + resource.getFileName();
        try {
            Map<String, Map<String, Object>> confMap = new HashMap<>();
            //解压缩获得配置文件
            String xmlZipLocation = resource.getUploadedFileName();
            List<File> xmlFiles = XmlFileUtil.getFilesFromZip(xmlZipLocation, upzipLocation, null);
            if (CollectionUtils.isEmpty(xmlFiles)) {
                throw new TaierDefineException("The configuration file cannot be empty");
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
                } else if (file.getName().endsWith("json")) {
                    //json文件
                    String jsonStr = Xml2JsonUtil.readFile(file);
                    if (StringUtils.isBlank(jsonStr)) {
                        continue;
                    }
                    fileMap = (Map<String, Object>) JSONObject.parseObject(jsonStr, Map.class);
                }
                if (null != fileMap) {
                    confMap.put(file.getName(), fileMap);
                }
            }
            return confMap;
        } catch (Exception e) {
            LOGGER.error("parseAndUploadXmlFile file error ", e);
            throw new TaierDefineException(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (StringUtils.isNotBlank(upzipLocation)) {
                ZipUtil.deletefile(upzipLocation);
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
                throw new TaierDefineException("JSON file format error");
            }
        }
        return data;
    }


    /**
     * 测试单个组件联通性, 所有组件的测试连通性全部迁移到 datasource-plugin 中
     */
    public ComponentTestResult testConnect(Integer componentType, String componentConfig,
                                           String versionName, Long clusterId, KerberosConfig kerberosConfig,
                                           Map<String, String> sftpConfig) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        // 不需要测试连通性的组件,跳过 ^^
        if (EComponentType.isUnnecessaryCheckConnectComponents(componentType)) {
            componentTestResult.setResult(true);
            return componentTestResult;
        }

        try {
            JSONObject pluginInfo = componentService.wrapperConfig(componentType, componentConfig, sftpConfig, kerberosConfig);
            if (EComponentType.SFTP.getTypeCode().equals(componentType)) {
                componentTestResult = testSftp(pluginInfo);
            } else {
                Integer dataSourceType = context.getDataSourceTypeByComponentAndVersion(componentType, versionName);
                componentTestResult = datasourceOperator.testConnect(dataSourceType, pluginInfo.toJSONString());
                if (null == componentTestResult) {
                    componentTestResult = new ComponentTestResult();
                    componentTestResult.setResult(false);
                    componentTestResult.setErrorMsg("测试联通性失败");
                    return componentTestResult;
                }
            }

        } catch (Throwable e) {
            if (Objects.isNull(componentTestResult)) {
                componentTestResult = new ComponentTestResult();
            }
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        } finally {
            if (null != componentTestResult) {
                componentTestResult.setComponentTypeCode(componentType);
                componentTestResult.setVersionName(versionName);
            }
        }
        return componentTestResult;
    }

    private ComponentTestResult testSftp(JSONObject pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        componentTestResult.setComponentTypeCode(EComponentType.SFTP.getTypeCode());
        componentTestResult.setResult(true);
        try {
            SftpConfig sftp = JSONObject.toJavaObject(pluginInfo, SftpConfig.class);
            SftpFileManage sftpManager = SftpFileManage.getSftpManager(sftp);
            sftpManager.listFile(sftp.getPath());
        } catch (Exception e) {
            componentTestResult.setResult(false);
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return componentTestResult;
    }


    /**
     * 下载文件
     *
     * @param componentId
     * @param downloadType 0:kerberos配置文件 1:配置文件 2:模板文件
     * @return
     */
    public File downloadFile(Long componentId, Integer downloadType, Integer componentType,
                             String versionName, Long clusterId, Integer deployType) {
        String localDownLoadPath = "";
        String uploadFileName = "";
        if (null == componentId) {
            EComponentType type = EComponentType.getByCode(componentType);
            //解析模版中的信息 作为默认值 返回json
            List<ClientTemplate> clientTemplates = this.loadTemplate(clusterId, type, versionName, null, deployType);
            if (CollectionUtils.isNotEmpty(clientTemplates)) {
                Map<String, Object> fileMap = ComponentConfigUtils.convertClientTemplateToMap(clientTemplates);
                uploadFileName = EComponentType.getByCode(componentType).name() + ".json";
                localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + uploadFileName;
                try {
                    FileUtils.write(new File(localDownLoadPath), JSONObject.toJSONString(fileMap));
                } catch (Exception e) {
                    throw new TaierDefineException("file does not exist");
                }
            }
        } else {
            Component component = componentMapper.selectById(componentId);
            if (null == component) {
                throw new TaierDefineException("Component does not exist");
            }
            SftpConfig sftpConfig = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, SftpConfig.class, null);
            if (null == sftpConfig) {
                throw new TaierDefineException("sftp component does not exist");
            }

            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + component.getComponentName();
            String remoteDir = sftpConfig.getPath() + File.separator + this.buildSftpPath(clusterId, component.getComponentTypeCode());
            SftpFileManage sftpFileManage = null;
            if (DownloadType.Kerberos.getCode() == downloadType) {
                remoteDir = remoteDir + File.separator + KERBEROS;
                localDownLoadPath = localDownLoadPath + File.separator + KERBEROS;
                sftpFileManage = SftpFileManage.getSftpManager(sftpConfig);
                sftpFileManage.downloadDir(remoteDir, localDownLoadPath);
            } else {
                if (StringUtils.isBlank(component.getUploadFileName())) {
                    // 一种是  全部手动填写的 如flink
                    EComponentType type = EComponentType.getByCode(componentType);
                    String componentConfig = componentService.getComponentByClusterId(clusterId, type.getTypeCode(), true, String.class, component.getVersionValue());
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
            throw new TaierDefineException("file does not exist");
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
     */
    private String filterConfigMessage(String componentConfig) {
        if (StringUtils.isBlank(componentConfig)) {
            return "";
        }
        JSONObject configJsonObject = JSONObject.parseObject(componentConfig);
        configJsonObject.put("password", "");
        return configJsonObject.toJSONString();
    }


    private File zipFile(Long componentId, Integer downloadType, Integer componentType, File file, String zipFilename) {
        File[] files = file.listFiles();
        //压缩成zip包
        if (null != files) {
            if (DownloadType.Kerberos.getCode() == downloadType) {
                Long clusterId = componentMapper.getClusterIdByComponentId(componentId);
                KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(clusterId, componentType, ComponentVersionUtil.isMultiVersionComponent(componentType) ? componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType) : null);
                if (null != kerberosConfig) {
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
     * 删除组件
     *
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long componentId) {
        Component component = componentMapper.selectById(componentId);
        if (component == null) {
            throw new TaierDefineException(ErrorCode.DATA_NOT_FIND);
        }
        componentMapper.deleteById(componentId);
        consoleKerberosMapper.deleteByComponentId(component.getId());
        componentConfigService.deleteComponentConfig(componentId);
        try {
            this.updateCache();
        } catch (Exception e) {
            LOGGER.error("clear cache error {} ", componentId, e);
        }
    }


    public KerberosConfig getKerberosConfig(Long clusterId, Integer componentType, String componentVersion) {
        return consoleKerberosMapper.getByComponentType(clusterId, componentType, ComponentVersionUtil.formatMultiVersion(componentType, componentVersion));
    }

    /**
     * 根据组件类型转换对应的插件名称
     * 如果只配yarn 需要调用插件时候 hdfs给默认值
     *
     * @param clusterId
     * @param componentType
     * @param versionName
     * @return
     */
    public String convertComponentTypeToClient(Long clusterId, Integer componentType, String versionName, Integer storeType, Integer deployType) {
        EComponentType type = EComponentType.getByCode(componentType);
        EComponentType storeComponent = null == storeType ? null : EComponentType.getByCode(storeType);
        PartCluster partCluster = clusterFactory.newImmediatelyLoadCluster(clusterId);
        Part part = partCluster.create(type, versionName, storeComponent, deployType);
        return part.getPluginName();
    }


    /**
     * 获取可配置组件信息
     *
     * @return
     */
    public List<ComponentModelVO> getComponentModels() {
        List<Dict> dicts = scheduleDictService.listByDictType(DictType.COMPONENT_MODEL);
        if (CollectionUtils.isEmpty(dicts)) {
            return new ArrayList<>();
        }
        List<ComponentModelVO> modelVOS = new ArrayList<>(dicts.size());
        for (Dict dict : dicts) {
            ComponentModelVO componentModelVO = new ComponentModelVO();
            JSONObject componentModel = JSONObject.parseObject(dict.getDictValue());
            componentModelVO.setAllowCoexistence(componentModel.getBooleanValue(ComponentModel.ALLOW_COEXISTENCE_KEY));
            List<String> dependsOn = JSON.parseObject(componentModel.getString(ComponentModel.DEPENDS_ON_KEY), new TypeReference<List<String>>() {
            });
            if (CollectionUtils.isNotEmpty(dependsOn)) {
                List<Integer> dependsVal = dependsOn.stream().map(d -> EComponentScheduleType.valueOf(d).getType()).collect(Collectors.toList());
                componentModelVO.setDependOn(dependsVal);
            }
            EComponentScheduleType ownerComponentScheduleType = EComponentScheduleType.valueOf(componentModel.getString(ComponentModel.OWNER_KEY));
            componentModelVO.setOwner(ownerComponentScheduleType.getType());
            EComponentType componentType = EComponentType.valueOf(dict.getDictName());
            componentModelVO.setComponentCode(componentType.getTypeCode());
            componentModelVO.setName(componentType.getName());

            //version
            String versionDict = componentModel.getString(ComponentModel.VERSION_DICTIONARY_KEY);
            if (!StringUtils.isBlank(versionDict)) {
                List<Dict> versions = scheduleDictService.listByDictCode(versionDict.toLowerCase());
                if (!CollectionUtils.isEmpty(versions)) {
                    List<Pair<String, List<Pair>>> pairs = scheduleDictService.groupByDependName(versions);
                    componentModelVO.setVersionDictionary(pairs);
                }
            }

            //是否能配置kerberos
            componentModelVO.setAllowKerberos(componentModel.getBooleanValue(ComponentModel.ALLOW_KERBEROS));
            //上传文件控件
            componentModelVO.setUploadConfigType(componentModel.getIntValue(ComponentModel.UPLOAD_CONFIG_TYPE));

            modelVOS.add(componentModelVO);
        }
        return modelVOS;
    }

    public ComponentVO getComponentInfo(Long componentId) {
        Component component = componentMapper.selectById(componentId);
        if (null == component) {
            throw new TaierDefineException(ErrorCode.COMPONENT_INVALID);
        }
        ComponentVO componentVO = ComponentVO.toVO(component);
        List<ComponentConfig> componentConfigs = componentConfigService.listByComponentIds(Lists.newArrayList(componentId), false);
        context.populateTip(componentConfigs, component.getComponentTypeCode());
        Map<String, Object> configToMap = ComponentConfigUtils.convertComponentConfigToMap(componentConfigs);
        componentVO.setComponentConfig(JSONObject.toJSONString(configToMap));
        String version = ComponentVersionUtil.formatMultiVersion(component.getComponentTypeCode(), component.getVersionName());
        KerberosConfig kerberosConfig = consoleKerberosMapper.getByComponentType(component.getClusterId(), component.getComponentTypeCode(), version);
        if (null != kerberosConfig) {
            componentVO.setPrincipal(kerberosConfig.getPrincipal());
            componentVO.setPrincipals(kerberosConfig.getPrincipals());
            componentVO.setMergeKrb5Content(kerberosConfig.getMergeKrbContent());
            componentVO.setKerberosFileName(kerberosConfig.getName());
        }
        return componentVO;
    }

    public Component getByClusterIdAndComponentType(Long clusterId, Integer typeCode) {
        return componentMapper.getByClusterIdAndComponentType(clusterId, typeCode, null, null);
    }
}
