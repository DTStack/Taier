/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.common.util.ZipUtil;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.dto.Resource;
import com.dtstack.engine.mapper.*;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.enums.DictType;
import com.dtstack.engine.master.enums.DownloadType;
import com.dtstack.engine.master.impl.pojo.ClientTemplate;
import com.dtstack.engine.master.impl.pojo.ComponentMultiTestResult;
import com.dtstack.engine.master.utils.ComponentConfigUtils;
import com.dtstack.engine.master.utils.Krb5FileUtil;
import com.dtstack.engine.master.vo.ClusterVO;
import com.dtstack.engine.master.vo.ComponentVO;
import com.dtstack.engine.master.vo.IComponentVO;
import com.dtstack.engine.master.vo.components.ComponentsConfigOfComponentsVO;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.pluginapi.sftp.SftpConfig;
import com.dtstack.engine.pluginapi.sftp.SftpFileManage;
import com.dtstack.engine.pluginapi.util.MathUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.*;

@Service
public class ComponentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentService.class);

    public static final String KERBEROS_PATH = "kerberos";
    
    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ClusterMapper clusterMapper;
    
    @Autowired
    private QueueMapper queueMapper;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private EnvironmentContext env;

    @Autowired
    private ClusterService clusterService;

    @Autowired
    private KerberosMapper kerberosMapper;

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ScheduleDictService scheduleDictService;

    @Autowired
    private ComponentFileService componentFileService;

    private static ThreadPoolExecutor connectPool =  new ThreadPoolExecutor(5, 10,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(20),
            new CustomThreadFactory("connectPool"));

    public void updateCache(Long clusterId,Integer componentCode) {
        componentConfigService.clearComponentCache();
        clusterService.clearStandaloneCache();
        List<Long> tenantIds = new ArrayList<>();
        if ( null != componentCode && EComponentType.sqlComponent.contains(EComponentType.getByCode(componentCode))) {
            //tidb 和libra 没有queue
            List<ClusterTenant> tenantVOS = clusterTenantMapper.listByClusterId(clusterId);
            if (CollectionUtils.isNotEmpty(tenantVOS)) {
                for (ClusterTenant tenantVO : tenantVOS) {
                    if (null != tenantVO && null != tenantVO.getTenantId()) {
                        tenantIds.add(tenantVO.getTenantId());
                    }
                }
            }
        } else {
            List<Queue> refreshQueues = queueMapper.listByClusterId(clusterId);
            if (CollectionUtils.isEmpty(refreshQueues)) {
                return;
            }
            List<Long> queueIds = refreshQueues.stream().map(BaseEntity::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(queueIds)) {
                return;
            }
            tenantIds = clusterTenantMapper.listTenantIdByQueueIds(queueIds);
        }
        //缓存刷新
        if (!tenantIds.isEmpty()) {
            for (Long tenantId : tenantIds) {
//                consoleCache.publishRemoveMessage(tenantId.toString());
            }
        }
    }

    public KerberosConfig getKerberosConfig(Long clusterId, Integer componentType, String componentVersion) {
        return kerberosMapper.getByComponentType(clusterId, componentType, ComponentVersionUtil.formatMultiVersion(componentType, componentVersion));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateKrb5Conf(String krb5Content) {
        try {
            Krb5FileUtil.checkKrb5Content(krb5Content);
            List<KerberosConfig> kerberosConfigs = kerberosMapper.listAll();
            for (KerberosConfig kerberosConfig : kerberosConfigs) {
                String remotePath = kerberosConfig.getRemotePath();
                kerberosConfig.setMergeKrbContent(krb5Content);
                kerberosMapper.updateById(kerberosConfig);
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
                                             EComponentType componentType, Integer storeType,
                                             String principals, String principal,boolean isMetadata,Boolean isDefault,Integer deployType) {
        Component componentDTO = new Component();
        componentDTO.setComponentTypeCode(componentType.getTypeCode());
        Cluster cluster = clusterMapper.getOne(clusterId);
        if(null == cluster){
            throw new RdosDefineException(ErrorCode.CANT_NOT_FIND_CLUSTER);
        }
        String clusterName = cluster.getClusterName();

        Component addComponent = new Component();
        BeanUtils.copyProperties(componentDTO, addComponent);
        // 判断是否是更新组件, 需要校验组件版本
        Component dbComponent = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? hadoopVersion : null, deployType);
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
        addComponent.setHadoopVersion(convertHadoopVersionToValue(hadoopVersion,componentType.getTypeCode(),clusterId));
        addComponent.setComponentName(componentType.getName());
        addComponent.setComponentTypeCode(componentType.getTypeCode());
        addComponent.setClusterId(clusterId);
        addComponent.setDeployType(deployType);

        if (StringUtils.isNotBlank(kerberosFileName)) {
            addComponent.setKerberosFileName(kerberosFileName);
        }

        changeDefault(BooleanUtils.isTrue(isDefault),clusterId,componentType,addComponent);
        String md5Key = updateResource(clusterId, componentConfig, resources, kerberosFileName, componentType.getTypeCode(), principals, principal, addComponent, dbComponent);
        addComponent.setClusterId(clusterId);
        if (isUpdate) {
            componentMapper.updateById(addComponent);
            refreshVersion(componentType, clusterId, addComponent, dbHadoopVersion,hadoopVersion);
            clusterMapper.updateGmtModified(clusterId);
        } else {
            componentMapper.insert(addComponent);
        }

        changeMetadata(componentType.getTypeCode(),isMetadata,clusterId,addComponent.getIsMetadata());
        List<ClientTemplate> clientTemplates = this.wrapperConfig(componentType, componentConfig, isOpenKerberos, clusterName, hadoopVersion, md5Key, componentTemplate,addComponent.getHadoopVersion(),addComponent.getStoreType(),deployType);
        componentConfigService.addOrUpdateComponentConfig(clientTemplates, addComponent.getId(), addComponent.getClusterId(), componentType.getTypeCode());
        // 此时不需要查询默认版本
        List<IComponentVO> componentVos = componentConfigService.getComponentVoByComponent(Lists.newArrayList(addComponent), true, clusterId,true,false);
        this.updateCache(clusterId, componentType.getTypeCode());
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
     * @param clusterId
     * @param componentType
     */
    private int changeDefault(boolean isDefault, Long clusterId, EComponentType componentType,Component updateComponent) {
        if(!EComponentType.multiVersionComponents.contains(componentType)) {
            updateComponent.setIsDefault(true);
            return -1;
        }
        updateComponent.setIsDefault(isDefault);
        if(!isDefault){
            List<Component> dbComponents = componentMapper.listByClusterId(clusterId, componentType.getTypeCode(), false);
            Set<Long> dbComponentId = dbComponents.stream().map(Component::getId).collect(Collectors.toSet());
            dbComponentId.remove(updateComponent.getId());
            if(dbComponentId.size() == 0){
                // single component must be default
                updateComponent.setIsDefault(true);
            }
        }
        return componentMapper.updateDefault(clusterId,componentType.getTypeCode(),!isDefault);
    }

    /**
     * yarn组件版本变更之后  hdfs组件保存一致
     * 计算组件 如flink的typename也同步变更
     *
     * @param componentType
     * @param clusterId
     * @param addComponent
     * @param dbHadoopVersion
     */
    public void refreshVersion(EComponentType componentType, Long clusterId, Component addComponent,String dbHadoopVersion, String hadoopVersion) {
        if (!EComponentType.YARN.equals(componentType)) {
            return;
        }
        List<Component> hdfsComponents = componentMapper.listByClusterId(clusterId, EComponentType.HDFS.getTypeCode(), false);
        if (CollectionUtils.isEmpty(hdfsComponents)) {
            return;
        }
        Component hdfsComponent = hdfsComponents.get(0);
        String oldVersion = formatHadoopVersion(dbHadoopVersion, componentType);
        String newVersion = formatHadoopVersion(addComponent.getHadoopVersion(), componentType);
        String hdfsVersion = formatHadoopVersion(hdfsComponent.getHadoopVersion(), EComponentType.HDFS);
        if (newVersion.equalsIgnoreCase(oldVersion) && newVersion.equalsIgnoreCase(hdfsVersion)) {
            return;
        }
        //1. 同步hdfs组件版本
        hdfsComponent.setHadoopVersion(addComponent.getHadoopVersion());
        componentMapper.updateById(hdfsComponent);
        ComponentConfig hadoopVersionConfig = componentConfigService.getComponentConfigByKey(hdfsComponent.getId(), HADOOP_VERSION);
        if (null != hadoopVersionConfig) {
            hadoopVersionConfig.setValue(hadoopVersion);
            componentConfigService.updateValueComponentConfig(hadoopVersionConfig);
        }

        //2. 版本切换 影响计算组件typeName
        List<Component> components = componentMapper.listByClusterId(clusterId,null,false);
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
                    LOGGER.info("refresh clusterId {} component {} typeName {} to {}", component.getClusterId(), component.getComponentName(), oldValue, newValue);
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
            Component yarnComponent = componentMapper.getByClusterIdAndComponentType(clusterId, EComponentType.YARN.getTypeCode(),null,null);
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
        //上传资源依赖 sftp 组件
        if (CollectionUtils.isNotEmpty(resources)) {
            String sftpConfigStr = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, String.class,null);
            // 上传配置文件到sftp 供后续下载
            SftpConfig sftpConfig = ComponentConfigUtils.getSFTPConfig(sftpConfigStr, componentCode, componentConfig);
            String md5Key = componentFileService.uploadResourceToSftp(clusterId, resources, kerberosFileName, sftpConfig, addComponent, dbComponent, principals, principal);
            return md5Key;
        }
        if (StringUtils.isNotBlank(principal)) {
            //直接更新认证信息
            KerberosConfig kerberosConfig = kerberosMapper.getByComponentType(clusterId, addComponent.getComponentTypeCode(),ComponentVersionUtil.isMultiVersionComponent(addComponent.getComponentTypeCode())?StringUtils.isNotBlank(addComponent.getHadoopVersion())?addComponent.getHadoopVersion():componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId,componentCode):null);
            if (null != kerberosConfig) {
                kerberosConfig.setPrincipal(principal);
                kerberosConfig.setPrincipals(principals);
                kerberosMapper.updateById(kerberosConfig);
            }
        }
        return StringUtils.EMPTY;
    }

    private String checkKubernetesConfig(String componentConfig, List<Resource> resources, EComponentType componentType) {
        if (CollectionUtils.isEmpty(resources)) {
            return componentConfig;
        }
        if (!EComponentType.KUBERNETES.getTypeCode().equals(componentType.getTypeCode())) {
            return componentConfig;
        }
        //kubernetes 信息需要自己解析文件
        List<Object> config = componentFileService.config(resources, EComponentType.KUBERNETES.getTypeCode(),false,null);
        if(CollectionUtils.isNotEmpty(config)){
            componentConfig = (String)config.get(0);
        }
        return componentConfig;
    }

    private boolean isOpenKerberos(String kerberosFileName, Component dbComponent) {
        boolean isOpenKerberos = StringUtils.isNotBlank(kerberosFileName);
        if (!isOpenKerberos) {
            if (null != dbComponent) {
                KerberosConfig componentKerberos = kerberosMapper.getByComponentType(dbComponent.getClusterId(), dbComponent.getComponentTypeCode(),ComponentVersionUtil.formatMultiVersion(dbComponent.getComponentTypeCode(),dbComponent.getHadoopVersion()));
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
        Component storeComponent = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(),null,null);
        if(null == storeComponent){
            throw new RdosDefineException(String.format("Please configure the corresponding %s component first",componentType.getName()));
        }
        return componentType;
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

    public static String parsePrincipal(String principal, List<PrincipalName> principalLists) {
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
    public void closeKerberos(Long componentId) {
        // 删除kerberos配置需要版本号
        Component component = componentMapper.selectById(componentId);
        if (Objects.isNull(component)) {
            return;
        }
        kerberosMapper.deleteByComponentId(component.getId());
        Component updateComponent = new Component();
        updateComponent.setId(componentId);
        updateComponent.setKerberosFileName("");
        componentMapper.updateById(updateComponent);
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
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
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
        Cluster one = clusterMapper.getOne(clusterId);
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
            Component component = componentMapper.selectById(componentId);
            if (null == component) {
                throw new RdosDefineException("Component does not exist");
            }
            Long clusterId = componentMapper.getClusterIdByComponentId(componentId);
            SftpConfig sftpConfig = getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(),false,SftpConfig.class,null);
            if ( null == sftpConfig ) {
                throw new RdosDefineException("sftp component does not exist");
            }

            localDownLoadPath = USER_DIR_DOWNLOAD + File.separator + component.getComponentName();
            String remoteDir = sftpConfig.getPath() + File.separator + componentFileService.buildSftpPath(clusterId, component.getComponentTypeCode());
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
                Long clusterId = componentMapper.getClusterIdByComponentId(componentId);
                KerberosConfig kerberosConfig = kerberosMapper.getByComponentType(clusterId, componentType,ComponentVersionUtil.isMultiVersionComponent(componentType)?componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId,componentType):null);
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
            Component yarnComponent = componentMapper.getByClusterIdAndComponentType(clusterByName.getClusterId(), EComponentType.YARN.getTypeCode(),null,null);
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

        if (componentCode == EComponentType.MYSQL) {
            String simpleVer = "8";
            if (version.startsWith("5.")) {
                simpleVer = "";
            }
            return String.format("mysql%s", simpleVer);
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
        Component yarn = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.YARN.getTypeCode(), ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.YARN),null);
        Component kubernetes = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.KUBERNETES.getTypeCode(),ComponentVersionUtil.getComponentVersion(componentVersionMap,EComponentType.KUBERNETES),null);
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
                    Component hdfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
                    if (null == hdfs) {
                        throw new RdosDefineException("Please configure storage components first");
                    }
                    return EComponentType.HDFS.name().toLowerCase() + this.formatHadoopVersion(hdfs.getHadoopVersion(), EComponentType.HDFS);
                }
            }
        } else {
            //hdfs和nfs可以共存 hdfs为默认
            Component hdfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
            Component nfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode(),null,null);
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
     * @param componentId
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long componentId) {
        Component component = componentMapper.selectById(componentId);
        EngineAssert.assertTrue(component != null, ErrorCode.DATA_NOT_FIND.getDescription());

        if (EComponentType.requireComponent.contains(EComponentType.getByCode(component.getComponentTypeCode()))){
            throw new RdosDefineException(String.format("%s is a required component and cannot be deleted",component.getComponentName()));
        }
        componentMapper.deleteById(componentId);
        kerberosMapper.deleteByComponentId(component.getId());
        componentConfigService.deleteComponentConfig(componentId);
        try {
            this.updateCache(component.getClusterId(),component.getComponentTypeCode());
        } catch (Exception e) {
            LOGGER.error("clear cache error {} ", componentId, e);
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
        return componentMapper.getByClusterIdAndComponentType(clusterId, componentType,componentVersion,null);
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
        Map<String, Object> configMap = componentConfigService.getCacheComponentConfigMap(clusterId, componentType, isFilter,componentVersionMap,componentId);
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

    public ComponentTestResult testConnect(String clusterName, Integer componentType, Map<Integer,String> componentVersionMap) {
        if (StringUtils.isBlank(clusterName)) {
            throw new RdosDefineException("clusterName is null");
        }
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("集群不存在");
        }
        Component testComponent = componentMapper.getByClusterIdAndComponentType(cluster.getId(), componentType,ComponentVersionUtil.getComponentVersion(componentVersionMap,componentType),null);
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
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
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
            KerberosConfig kerberosConfig = kerberosMapper.getByComponentType(cluster.getId(), component.getComponentTypeCode(),ComponentVersionUtil.isMultiVersionComponent(component.getComponentTypeCode())?StringUtils.isNotBlank(component.getHadoopVersion())?component.getHadoopVersion():componentMapper.getDefaultComponentVersionByClusterAndComponentType(cluster.getId(),component.getComponentTypeCode()):null);
            String componentConfig = getComponentByClusterId(cluster.getId(), component.getComponentTypeCode(), false, String.class,null);
            testResult = this.testConnect(component.getComponentTypeCode(), componentConfig, clusterName, component.getHadoopVersion(), component.getClusterId(), kerberosConfig, sftpMap,component.getStoreType(),null,component.getDeployType());
            //测试联通性
            if (EComponentType.YARN.getTypeCode().equals(component.getComponentTypeCode()) && testResult.getResult()) {
                if (null != testResult.getClusterResourceDescription()) {
                    queueService.updateQueue(cluster.getId(), testResult.getClusterResourceDescription());
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

        List<Component> components = componentMapper.listByClusterId(cluster.getId(),null,false);
        if (CollectionUtils.isEmpty(components)) {
            return Collections.emptyList();
        }
        return components;
    }

    public List<Component> getComponentStore(String clusterName, Integer componentType) {
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
        if (null == cluster) {
            throw new RdosDefineException("Cluster does not exist");
        }
        List<Component> components = new ArrayList<>();
        Component hdfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.HDFS.getTypeCode(),null,null);
        if (null != hdfs) {
            components.add(hdfs);
        }
        Component nfs = componentMapper.getByClusterIdAndComponentType(cluster.getId(), EComponentType.NFS.getTypeCode(),null,null);
        if (null != nfs) {
            components.add(nfs);
        }
        return components;
    }

    /**
     * 更新metadata的元数据组件
     * 如果集群只有单个metadata组件 默认勾选
     * 如果集群多个metadata组件 绑定租户之后 无法切换
     *
     * @param clusterId
     * @param componentType
     * @param isMetadata
     * @return
     */
    public boolean changeMetadata(Integer componentType, boolean isMetadata, Long clusterId, Integer oldMetadata) {
        if (!EComponentType.metadataComponents.contains(EComponentType.getByCode(componentType))) {
            return false;
        }
        Integer revertComponentType = EComponentType.HIVE_SERVER.getTypeCode().equals(componentType) ? EComponentType.SPARK_THRIFT.getTypeCode() : EComponentType.HIVE_SERVER.getTypeCode();
        List<Component> components = componentMapper.listByClusterId(clusterId, revertComponentType, false);
        Component revertComponent = CollectionUtils.isEmpty(components) ? null : components.get(0);
        if (null == revertComponent) {
            //单个组件默认勾选
            componentMapper.updateMetadata(clusterId, componentType, 1);
            return true;
        }
        if (null != oldMetadata && !BooleanUtils.toIntegerObject(isMetadata, 1, 0).equals(oldMetadata)) {
            //如果集群已经绑定过租户 不允许修改
            if (CollectionUtils.isNotEmpty(clusterTenantMapper.listByClusterId(clusterId))) {
                throw new RdosDefineException("cluster has bind tenant can not change metadata component");
            }
        }
        LOGGER.info("change metadata clusterId {} component {} to {} ", clusterId, componentType, isMetadata);
        componentMapper.updateMetadata(clusterId, componentType, isMetadata ? 1 : 0);
        componentMapper.updateMetadata(clusterId, revertComponentType, isMetadata ? 0 : 1);
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

    public List<Component> getComponentVersionByEngineType(Long tenantId, Integer  taskType) {
        EScheduleJobType scheduleJobType = EScheduleJobType.getEngineJobType(taskType);
        EComponentType componentType = scheduleJobType.getComponentType();
        List<Component > componentVersionList = componentMapper.getComponentVersionByEngineType(tenantId,componentType.getTypeCode());
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

    public Component getMetadataComponent(Long clusterId){
        return componentMapper.getMetadataComponent(clusterId);
    }

    public List<Component> listComponents(Long tenantId, Integer engineType) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        return componentMapper.listByClusterId(clusterId,null,true);
    }

    public List<ComponentsConfigOfComponentsVO> listConfigOfComponents(Long uicTenantId, int type, Object o) {
        return null;
    }
}
