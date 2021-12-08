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

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.engine.common.enums.*;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.common.exception.EngineAssert;
import com.dtstack.engine.common.util.ComponentVersionUtil;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.domain.*;
import com.dtstack.engine.mapper.*;
import com.dtstack.engine.master.vo.*;
import com.dtstack.engine.pluginapi.constrant.ConfigConstant;
import com.dtstack.engine.pluginapi.enums.EDeployMode;
import com.dtstack.engine.pluginapi.exception.ErrorCode;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.dtstack.engine.pluginapi.constrant.ConfigConstant.*;
import static java.lang.String.format;

@Service
public class ClusterService implements com.dtstack.engine.api.ClusterService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterService.class);

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private QueueMapper queueMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private KerberosMapper kerberosMapper;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ComponentConfigService componentConfigService;

    public boolean addCluster(String clusterName) {
        if (clusterMapper.getByClusterName(clusterName) != null) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST.getDescription());
        }
        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterName);
        return clusterMapper.insert(cluster) > 0;
    }


    public ClusterVO getClusterByName(String clusterName) {
        Cluster cluster = clusterMapper.getByClusterName(clusterName);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        return ClusterVO.toVO(cluster);
    }

    public IPage<Cluster> pageQuery(int currentPage, int pageSize) {
        Page<Cluster> page = new Page(currentPage, pageSize);
        return clusterMapper.selectPage(page, null);
    }


    /**
     * 内部使用
     */
    public JSONObject pluginInfoJSON(Long tenantId, Integer taskType, Long dtUicUserId, Integer deployMode, Map<Integer, String> componentVersionMap) {
        EScheduleJobType engineJobType = EScheduleJobType.getEngineJobType(taskType);
        EComponentType componentType = engineJobType.getComponentType();
        if (componentType == null) {
            return null;
        }
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (null == clusterId) {
            clusterId = DEFAULT_CLUSTER_ID;
        }
        ClusterVO cluster = getCluster(clusterId, false, true);
        if (cluster == null) {
            String msg = format("The tenant [%s] is not bound to any cluster", tenantId);
            throw new RdosDefineException(msg);
        }
        JSONObject clusterConfigJson = buildClusterConfig(cluster, componentVersionMap);
        JSONObject pluginJson = convertPluginInfo(clusterConfigJson, componentType, cluster, deployMode, componentVersionMap);
        if (pluginJson == null) {
            throw new RdosDefineException(format("The cluster is not configured [%s] engine", componentType));
        }

        Queue queue = getQueue(tenantId, cluster.getClusterId());
        pluginJson.put(QUEUE, queue == null ? "" : queue.getQueueName());
        pluginJson.put(CLUSTER, cluster.getClusterName());
        setComponentSftpDir(cluster.getClusterId(), clusterConfigJson, pluginJson, componentType);
        return pluginJson;
    }

    /**
     * 填充对应的组件信息
     *
     * @param clusterId
     * @param clusterConfigJson
     * @param pluginJson
     * @param componentType
     */
    private void setComponentSftpDir(Long clusterId, JSONObject clusterConfigJson, JSONObject pluginJson, EComponentType componentType) {
        //sftp Dir
        JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
        if (null != sftpConfig) {
            pluginJson.put(EComponentType.SFTP.getConfName(), sftpConfig);
        }
        KerberosConfig kerberosConfig = kerberosMapper.getByComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType.getTypeCode()) : null);
        if (null != kerberosConfig) {
            Integer openKerberos = kerberosConfig.getOpenKerberos();
            String remotePath = kerberosConfig.getRemotePath();
            Preconditions.checkState(StringUtils.isNotEmpty(remotePath), "remotePath can not be null");
            pluginJson.fluentPut("openKerberos", null != openKerberos && openKerberos > 0)
                    .fluentPut("remoteDir", remotePath)
                    .fluentPut("principalFile", kerberosConfig.getName())
                    .fluentPut("principal", kerberosConfig.getPrincipal())
                    .fluentPut("krbName", kerberosConfig.getKrbName())
                    .fluentPut("kerberosFileTimestamp", kerberosConfig.getGmtModified())
                    .fluentPut(MERGE_KRB5_CONTENT_KEY, kerberosConfig.getMergeKrbContent());
            //如果 hiveSQL  impalaSQL中没有yarnConf 需要添加yarnConf做kerberos认证
            pluginJson.putIfAbsent(EComponentType.YARN.getConfName(), clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName()));
        }
    }

    /**
     * 获取集群在sftp上的路径
     * 开启kerberos 带上kerberos路径
     *
     * @param tenantId
     * @return
     */
    public String clusterSftpDir(Long tenantId, Integer componentType) {
        Long clusterId = clusterTenantMapper.getClusterIdByTenantId(tenantId);
        if (clusterId != null) {
            if (null == componentType) {
                componentType = EComponentType.SPARK_THRIFT.getTypeCode();
            }
            Map<String, String> sftpConfig = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
            if (sftpConfig != null) {
                KerberosConfig kerberosDaoByComponentType = kerberosMapper.getByComponentType(clusterId, componentType, ComponentVersionUtil.isMultiVersionComponent(componentType) ? componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType) : null);
                if (null != kerberosDaoByComponentType) {
                    return sftpConfig.get("path") + File.separator + componentService.buildSftpPath(clusterId, componentType) + File.separator +
                            ComponentService.KERBEROS_PATH;
                }
                return sftpConfig.get("path") + File.separator + componentService.buildSftpPath(clusterId, componentType);
            }
        }
        return null;
    }

    public Queue getQueue(Long tenantId, Long clusterId) {
        //先获取绑定的
        Long queueId = clusterTenantMapper.getQueueIdByTenantId(tenantId);
        Queue queue = queueMapper.selectById(queueId);
        if (queue != null) {
            return queue;
        }
        List<Queue> queues = queueMapper.listByClusterWithLeaf(clusterId);
        if (CollectionUtils.isEmpty(queues)) {
            return null;
        }

        // 没有绑定集群和队列时，返回第一个队列
        return queues.get(0);
    }


    public JSONObject buildClusterConfig(ClusterVO cluster, Map<Integer, String> componentVersionMap) {
        JSONObject config = new JSONObject();
        List<SchedulingVo> scheduling = cluster.getScheduling();
        if (CollectionUtils.isNotEmpty(scheduling)) {
            for (SchedulingVo schedulingVo : scheduling) {
                List<IComponentVO> components = schedulingVo.getComponents();
                if (CollectionUtils.isNotEmpty(components)) {
                    for (IComponentVO componentVO : components) {
                        String version = MapUtils.isEmpty(componentVersionMap) ? "" : componentVersionMap.getOrDefault(componentVO.getComponentTypeCode(), "");
                        EComponentType type = EComponentType.getByCode(componentVO.getComponentTypeCode());
                        //IComponentVO contains  flink on standalone and on yarn
                        ComponentVO component = componentVO.getComponent(version);
                        if (null == component) {
                            continue;
                        }
                        JSONObject componentConfig = componentService.getComponentByClusterId(component.getId(), false, JSONObject.class);
                        if (EComponentType.FLINK.equals(type) && EDeployType.STANDALONE.getType() == component.getDeployType()) {
                            config.put(FLINK_ON_STANDALONE_CONF, componentConfig);
                            continue;
                        }
                        config.put(type.getConfName(), componentConfig);
                    }
                }
            }
        }
        config.put("clusterName", cluster.getClusterName());
        return config;
    }


    public Cluster getCluster(Long clusterId) {
        return clusterMapper.selectById(clusterId);
    }

    public String getConfigByKey(Long tenantId, String componentConfName, Boolean fullKerberos, Map<Integer, String> componentVersionMap) {
        Long clusterId = Optional.ofNullable(clusterTenantMapper.getClusterIdByTenantId(tenantId)).orElse(DEFAULT_CLUSTER_ID);
        //根据组件区分kerberos
        EComponentType componentType = EComponentType.getByConfName(componentConfName);
        Component component = componentMapper.getByClusterIdAndComponentType(clusterId, componentType.getTypeCode(), ComponentVersionUtil.getComponentVersion(componentVersionMap, componentType), null);
        if (null == component) {
            return "{}";
        }
        JSONObject configObj = componentService.getComponentByClusterId(clusterId, component.getComponentTypeCode(), false, JSONObject.class, componentVersionMap);
        if (configObj != null) {
            KerberosConfig kerberosConfig = null;
            if (StringUtils.isNotBlank(component.getKerberosFileName())) {
                //开启kerberos的kerberosFileName不为空
                String componentVersion = ComponentVersionUtil.getComponentVersion(componentVersionMap, componentType.getTypeCode());
                kerberosConfig = kerberosMapper.getByComponentType(clusterId, componentType.getTypeCode(),
                        ComponentVersionUtil.isMultiVersionComponent(componentType.getTypeCode()) ? StringUtils.isNotBlank(componentVersion) ? componentVersion :
                                componentMapper.getDefaultComponentVersionByClusterAndComponentType(clusterId, componentType.getTypeCode()) : null);
            }
            //返回版本
            configObj.put(ConfigConstant.VERSION, component.getHadoopVersion());
            configObj.put(IS_METADATA, component.getIsMetadata());
            // 添加组件的kerberos配置信息 应用层使用
            configObj.put(ConfigConstant.KERBEROS_CONFIG,kerberosConfig);
            //填充sftp配置项
            Map sftpMap = componentService.getComponentByClusterId(clusterId, EComponentType.SFTP.getTypeCode(), false, Map.class, null);
            if (MapUtils.isNotEmpty(sftpMap)) {
                configObj.put(EComponentType.SFTP.getConfName(), sftpMap);
            }
            return configObj.toJSONString();
        }
        return "{}";
    }


    public JSONObject convertPluginInfo(JSONObject clusterConfigJson, EComponentType componentType, ClusterVO clusterVO, Integer deployMode, Map<Integer, String> componentVersionMap) {
        JSONObject pluginInfo = new JSONObject();
        if (EComponentType.HDFS == componentType) {
            //hdfs yarn%s-hdfs%s-hadoop%s的版本
            JSONObject hadoopConf = clusterConfigJson.getJSONObject(EComponentType.HDFS.getConfName());
            String typeName = hadoopConf.getString(TYPE_NAME);
            pluginInfo.put(TYPE_NAME, typeName);
            pluginInfo.put(EComponentType.HDFS.getConfName(), hadoopConf);
            pluginInfo.put(EComponentType.YARN.getConfName(), clusterConfigJson.getJSONObject(EComponentType.YARN.getConfName()));

        } else {
            //flink spark 需要区分任务类型
            if (EComponentType.FLINK.equals(componentType) || EComponentType.SPARK.equals(componentType)) {
                pluginInfo = this.buildDeployMode(clusterConfigJson, componentType, clusterVO, deployMode);
            } else if (EComponentType.DT_SCRIPT.equals(componentType)) {
                //DT_SCRIPT 需要将common配置放在外边
                JSONObject dtscriptConf = clusterConfigJson.getJSONObject(componentType.getConfName());
                JSONObject commonConf = dtscriptConf.getJSONObject("commonConf");
                dtscriptConf.remove("commonConf");
                pluginInfo = dtscriptConf;
                pluginInfo.putAll(commonConf);
            } else {
                pluginInfo = clusterConfigJson.getJSONObject(componentType.getConfName());
            }

            if (pluginInfo == null) {
                return null;
            }

            for (Iterator<Map.Entry<String, Object>> it = clusterConfigJson.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Object> entry = it.next();
                if (!EComponentType.BASE_CONFIG.contains(entry.getKey())) {
                    it.remove();
                    continue;
                }
                if (EComponentType.DT_SCRIPT == componentType && EComponentType.SPARK_THRIFT.getConfName().equals(entry.getKey())) {
                    //dt-script  不需要hive-site配置
                    continue;
                }

                pluginInfo.put(entry.getKey(), entry.getValue());
            }
            if (EComponentType.HIVE_SERVER == componentType) {
                this.buildHiveVersion(clusterVO, pluginInfo, componentVersionMap);
            }
            pluginInfo.put(ConfigConstant.MD5_SUM_KEY, getZipFileMD5(clusterConfigJson));
            removeMd5FieldInHadoopConf(pluginInfo);
        }

        return pluginInfo;
    }

    private void buildHiveVersion(ClusterVO clusterVO, JSONObject pluginInfo, Map<Integer, String> componentVersionMap) {
        Component hiveServer = componentMapper.getByClusterIdAndComponentType(clusterVO.getId(), EComponentType.HIVE_SERVER.getTypeCode(), ComponentVersionUtil.getComponentVersion(componentVersionMap, EComponentType.HIVE_SERVER), null);
        if (null == hiveServer) {
            throw new RdosDefineException("hive component cannot be empty");
        }
        String jdbcUrl = pluginInfo.getString("jdbcUrl");
        //%s替换成默认的 供插件使用
        jdbcUrl = jdbcUrl.replace("/%s", environmentContext.getComponentJdbcToReplace());
        pluginInfo.put("jdbcUrl", jdbcUrl);
        String typeName = componentService.convertComponentTypeToClient(clusterVO.getClusterName(),
                EComponentType.HIVE_SERVER.getTypeCode(), hiveServer.getHadoopVersion(), hiveServer.getStoreType(), componentVersionMap, null);
        pluginInfo.put(TYPE_NAME, typeName);
    }


    private JSONObject buildDeployMode(JSONObject clusterConfigJson, EComponentType componentType, ClusterVO clusterVO, Integer deployMode) {
        JSONObject pluginInfo;
        //默认为session
        EDeployMode deploy = EComponentType.FLINK.equals(componentType) ? EDeployMode.SESSION : EDeployMode.PERJOB;
        //spark 暂时全部为perjob
        if (Objects.nonNull(deployMode) && !EComponentType.SPARK.equals(componentType)) {
            deploy = EDeployMode.getByType(deployMode);
        }
        JSONObject confConfig = null;
        if (EComponentType.FLINK.equals(componentType) && EDeployMode.STANDALONE.getType().equals(deployMode)) {
            confConfig = clusterConfigJson.getJSONObject(FLINK_ON_STANDALONE_CONF);
            return confConfig;
        } else {
            confConfig = clusterConfigJson.getJSONObject(componentType.getConfName());
        }
        if (null == confConfig || confConfig.size() == 0) {
            throw new RdosDefineException("Flink configuration information is empty");
        }
        pluginInfo = confConfig.getJSONObject(deploy.getMode());
        if (Objects.isNull(pluginInfo)) {
            throw new RdosDefineException(String.format("Corresponding mode [%s] no information is configured", deploy.name()));
        }
        String typeName = confConfig.getString(TYPE_NAME);
        if (!StringUtils.isBlank(typeName)) {
            pluginInfo.put(TYPE_NAME_KEY, typeName);
        }
        if (EComponentType.SPARK.equals(componentType)) {
            JSONObject sftpConfig = clusterConfigJson.getJSONObject(EComponentType.SFTP.getConfName());
            if (Objects.nonNull(sftpConfig)) {
                String confHdfsPath = sftpConfig.getString("path") + File.separator + componentService.buildConfRemoteDir(clusterVO.getId());
                pluginInfo.put("confHdfsPath", confHdfsPath);
            }
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
        if (null != hadoopConf && hadoopConf.containsKey(ConfigConstant.MD5_SUM_KEY)) {
            return hadoopConf.getString(ConfigConstant.MD5_SUM_KEY);
        }
        return "";
    }



    /**
     * 删除集群
     * 判断该集群下是否有租户
     *
     * @param clusterId
     */
    public boolean deleteCluster(Long clusterId) {
        if (null == clusterId) {
            throw new RdosDefineException("Cluster cannot be empty");
        }
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            throw new RdosDefineException("Cluster does not exist");
        }
        if (DEFAULT_CLUSTER_ID.equals(clusterId)) {
            throw new RdosDefineException("The default cluster cannot be deleted");
        }
        return clusterMapper.deleteById(clusterId) > 0 ;
    }

    /**
     * 获取集群配置
     *
     * @param clusterId      集群id
     * @param removeTypeName
     * @param multiVersion   组件默认版本
     * @return
     */
    public ClusterVO getCluster(Long clusterId, Boolean removeTypeName, boolean multiVersion) {
        return getCluster(clusterId, removeTypeName, false, multiVersion);
    }

    /**
     * 获取集群信息详情 需要根据组件分组
     *
     * @param clusterId
     * @return
     */
    public ClusterVO getCluster(Long clusterId, Boolean removeTypeName,boolean isFullPrincipal,boolean multiVersion) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        EngineAssert.assertTrue(cluster != null, ErrorCode.DATA_NOT_FIND.getDescription());
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        // 查询默认版本或者多个版本
        List<Component> components = componentMapper.listByClusterId(clusterId,null,!multiVersion);

        List<IComponentVO> componentConfigs = componentConfigService.getComponentVoByComponent(components,
                null == removeTypeName || removeTypeName , clusterId,true, multiVersion);
        Table<Integer,String ,KerberosConfig> kerberosTable = null;
        // kerberos的配置
        if(isFullPrincipal){
            kerberosTable= HashBasedTable.create();
            for (KerberosConfig kerberosConfig : kerberosMapper.getByClusters(clusterId)) {
                kerberosTable.put(kerberosConfig.getComponentType(), StringUtils.isBlank(kerberosConfig.getComponentVersion())?
                        StringUtils.EMPTY:kerberosConfig.getComponentVersion(),kerberosConfig);
            }
        }

        Map<EComponentScheduleType, List<IComponentVO>> scheduleType = new HashMap<>(4);
        // 组件根据用途分组(计算,资源)
        if (CollectionUtils.isNotEmpty(componentConfigs)) {
            scheduleType = componentConfigs.stream().collect(Collectors.groupingBy(c -> EComponentType.getScheduleTypeByComponent(c.getComponentTypeCode())));
        }
        List<SchedulingVo> schedulingVos = convertComponentToScheduling(kerberosTable, scheduleType);
        clusterVO.setScheduling(schedulingVos);
        clusterVO.setCanModifyMetadata(checkMetadata(clusterId, components));
        return clusterVO;
    }

    private boolean checkMetadata(Long clusterId, List<Component> components) {
        if (components.stream().anyMatch(c -> EComponentType.metadataComponents.contains(EComponentType.getByCode(c.getComponentTypeCode())))) {
            List<ClusterTenant> clusterTenants = clusterTenantMapper.listByClusterId(clusterId);
            return CollectionUtils.isEmpty(clusterTenants);
        }
        return true;
    }

    private List<SchedulingVo> convertComponentToScheduling(Table<Integer, String, KerberosConfig> kerberosTable, Map<EComponentScheduleType, List<IComponentVO>> scheduleType) {
        List<SchedulingVo> schedulingVos = new ArrayList<>();
        //为空也返回
        for (EComponentScheduleType value : EComponentScheduleType.values()) {
            SchedulingVo schedulingVo = new SchedulingVo();
            schedulingVo.setSchedulingCode(value.getType());
            schedulingVo.setSchedulingName(value.getName());
            List<IComponentVO> componentVoList = scheduleType.getOrDefault(value, Collections.emptyList());
            if (Objects.nonNull(kerberosTable) && !kerberosTable.isEmpty() && CollectionUtils.isNotEmpty(componentVoList)) {
                componentVoList.forEach(component -> {
                    // 组件每个版本设置k8s参数
                    for (ComponentVO componentVO : component.loadComponents()) {
                        KerberosConfig kerberosConfig;
                        EComponentType type = EComponentType.getByCode(componentVO.getComponentTypeCode());
                        if (type == EComponentType.YARN || type == EComponentType.SPARK_THRIFT ||
                                type == EComponentType.DT_SCRIPT || type == EComponentType.HIVE_SERVER ||
                                type == EComponentType.IMPALA_SQL || type == EComponentType.LEARNING ||
                                type == EComponentType.INCEPTOR_SQL) {
                            kerberosConfig = kerberosTable.get(type.getTypeCode(), StringUtils.EMPTY);
                        } else {
                            kerberosConfig = kerberosTable.get(componentVO.getComponentTypeCode(), StringUtils.isBlank(componentVO.getHadoopVersion()) ?
                                    StringUtils.EMPTY : componentVO.getHadoopVersion());
                        }
                        if (Objects.nonNull(kerberosConfig)) {
                            componentVO.setPrincipal(kerberosConfig.getPrincipal());
                            componentVO.setPrincipals(kerberosConfig.getPrincipals());
                            componentVO.setMergeKrb5Content(kerberosConfig.getMergeKrbContent());
                        }
                    }
                });
            }
            schedulingVo.setComponents(componentVoList);
            schedulingVos.add(schedulingVo);
        }
        return schedulingVos;
    }


    @CacheEvict(cacheNames = "standalone", allEntries = true)
    public void clearStandaloneCache() {
        LOGGER.info("clear all standalone cache");
    }

    public String clusterInfo(Long dtuicTenantId) {
        return null;
    }

    public String pluginInfoForType(Long uicTenantId, boolean b, int typeCode) {
        return null;
    }

    public String dbInfo(Long uicTenantId, Long aLong, int typeCode) {
        return null;
    }

    public Boolean hasStandalone(Long dtUicTenantId, int typeCode) {
        return null;
    }

    public List<Cluster> getAllCluster() {
       return clusterMapper.selectList(null);
    }

    public ClusterEngineVO getClusterEngine(Long clusterId) {
        Cluster cluster = clusterMapper.selectById(clusterId);
        List<Component> components = componentMapper.listByClusterId(clusterId, null, false);
        Map<Long, Set<MultiEngineType>> clusterEngineMapping = new HashMap<>();
        if(CollectionUtils.isNotEmpty(components)){
            clusterEngineMapping = components.stream().filter(c -> {
                MultiEngineType multiEngineType = EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType());
                return null != multiEngineType && !MultiEngineType.COMMON.equals(multiEngineType);
            }).collect(Collectors.groupingBy(Component::getClusterId,
                    Collectors.mapping(c -> EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType()), Collectors.toSet())));
        }

        List<Queue> queues = queueMapper.listByClusterWithLeaf(clusterId);

        Map<Long, List<Queue>> engineQueueMapping = queues
                .stream()
                .collect(Collectors.groupingBy(Queue::getClusterId));

        return fillEngineQueueInfo(clusterEngineMapping, engineQueueMapping, cluster);
    }

    private ClusterEngineVO fillEngineQueueInfo(Map<Long, Set<MultiEngineType>> clusterEngineMapping, Map<Long, List<Queue>> engineQueueMapping, Cluster cluster) {
        ClusterEngineVO vo = ClusterEngineVO.toVO(cluster);
        Set<MultiEngineType> engineList = clusterEngineMapping.get(vo.getClusterId());
        if (CollectionUtils.isNotEmpty(engineList)) {
            List<EngineVO> engineVOS = new ArrayList<>();
            for (MultiEngineType multiEngineType : engineList) {
                EngineVO engineVO = new EngineVO();
                engineVO.setEngineType(multiEngineType.getType());
                engineVO.setEngineName(multiEngineType.getName());
                engineVO.setClusterId(cluster.getId());
                if (MultiEngineType.HADOOP.equals(multiEngineType)) {
                    engineVO.setQueues(QueueVO.toVOs(engineQueueMapping.get(cluster.getId())));
                }
                engineVOS.add(engineVO);
            }
            engineVOS.sort(Comparator.comparingInt(EngineVO::getEngineType));
            vo.setEngines(engineVOS);
        }
        return vo;
    }
}

