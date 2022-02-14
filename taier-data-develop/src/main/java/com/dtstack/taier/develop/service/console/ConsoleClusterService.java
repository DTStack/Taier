package com.dtstack.taier.develop.service.console;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EComponentScheduleType;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.MultiEngineType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.domain.ClusterTenant;
import com.dtstack.taier.dao.domain.KerberosConfig;
import com.dtstack.taier.dao.domain.Queue;
import com.dtstack.taier.dao.mapper.*;
import com.dtstack.taier.develop.vo.console.ClusterEngineVO;
import com.dtstack.taier.develop.vo.console.ClusterVO;
import com.dtstack.taier.develop.vo.console.EngineVO;
import com.dtstack.taier.develop.vo.console.QueueVO;
import com.dtstack.taier.scheduler.service.ComponentConfigService;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import com.dtstack.taier.scheduler.vo.IComponentVO;
import com.dtstack.taier.scheduler.vo.SchedulingVo;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.dtstack.taier.pluginapi.constrant.ConfigConstant.DEFAULT_CLUSTER_ID;

@Component
public class ConsoleClusterService {

    @Autowired
    private ClusterMapper clusterMapper;

    @Autowired
    private ClusterTenantMapper clusterTenantMapper;

    @Autowired
    private ComponentMapper componentMapper;

    @Autowired
    private ConsoleKerberosMapper consoleKerberosMapper;

    @Autowired
    private ComponentConfigService componentConfigService;

    @Autowired
    private ComponentService componentService;

    @Autowired
    private ConsoleQueueMapper consoleQueueMapper;

    public Long addCluster(String clusterName) {
        if (clusterMapper.getByClusterName(clusterName) != null) {
            throw new RdosDefineException(ErrorCode.NAME_ALREADY_EXIST.getDescription());
        }
        Cluster cluster = new Cluster();
        cluster.setClusterName(clusterName);
        clusterMapper.insert(cluster);
        return cluster.getId();
    }

    public IPage<Cluster> pageQuery(int currentPage, int pageSize) {
        Page<Cluster> page = new Page<>(currentPage, pageSize);
        return clusterMapper.selectPage(page, Wrappers.lambdaQuery(Cluster.class).eq(
                Cluster::getIsDeleted, Deleted.NORMAL.getStatus())
        );
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
        return clusterMapper.deleteById(clusterId) > 0;
    }

    /**
     * 获取集群信息详情 需要根据组件分组
     *
     * @param clusterId
     * @return
     */
    public ClusterVO getConsoleClusterInfo(Long clusterId) {
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            return new ClusterVO();
        }
        ClusterVO clusterVO = ClusterVO.toVO(cluster);
        // 查询默认版本或者多个版本
        List<com.dtstack.taier.dao.domain.Component> components = componentMapper.listByClusterId(clusterId, null, false);

        List<IComponentVO> componentConfigs = componentConfigService.getComponentVoByComponent(components, true, clusterId, true, true);
        Table<Integer, String, KerberosConfig> kerberosTable = null;
        // kerberos的配置
        kerberosTable = HashBasedTable.create();
        for (KerberosConfig kerberosConfig : consoleKerberosMapper.getByClusters(clusterId)) {
            kerberosTable.put(kerberosConfig.getComponentType(), StringUtils.isBlank(kerberosConfig.getComponentVersion()) ?
                    StringUtils.EMPTY : kerberosConfig.getComponentVersion(), kerberosConfig);
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

    private boolean checkMetadata(Long clusterId, List<com.dtstack.taier.dao.domain.Component> components) {
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
                                type == EComponentType.HIVE_SERVER) {
                            kerberosConfig = kerberosTable.get(type.getTypeCode(), StringUtils.EMPTY);
                        } else {
                            kerberosConfig = kerberosTable.get(componentVO.getComponentTypeCode(), StringUtils.isBlank(componentVO.getVersionValue()) ?
                                    StringUtils.EMPTY : componentVO.getVersionValue());
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


    public List<Cluster> getAllCluster() {
        return clusterMapper.selectList(Wrappers.lambdaQuery(Cluster.class)
                .eq(Cluster::getIsDeleted, Deleted.NORMAL.getStatus()));
    }

    public ClusterEngineVO getClusterEngine(Long clusterId) {
        Cluster cluster = clusterMapper.selectById(clusterId);
        List<com.dtstack.taier.dao.domain.Component> components = componentService.listAllComponents(clusterId);
        Map<Long, Set<MultiEngineType>> clusterEngineMapping = new HashMap<>();
        if (CollectionUtils.isNotEmpty(components)) {
            clusterEngineMapping = components.stream().filter(c -> {
                MultiEngineType multiEngineType = EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType());
                return null != multiEngineType && !MultiEngineType.COMMON.equals(multiEngineType);
            }).collect(Collectors.groupingBy(com.dtstack.taier.dao.domain.Component::getClusterId,
                    Collectors.mapping(c -> EComponentType.getEngineTypeByComponent(EComponentType.getByCode(c.getComponentTypeCode()), c.getDeployType()), Collectors.toSet())));
        }

        List<com.dtstack.taier.dao.domain.Queue> queues = consoleQueueMapper.listByClusterWithLeaf(clusterId);

        Map<Long, List<com.dtstack.taier.dao.domain.Queue>> engineQueueMapping = queues
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

    public Integer getMetaComponentByClusterId(Long clusterId) {
        return componentService.getMetaComponentByClusterId(clusterId);
    }
}
