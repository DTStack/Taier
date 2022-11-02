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

package com.dtstack.taier.develop.service.console;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.MultiEngineType;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.dao.domain.Cluster;
import com.dtstack.taier.dao.mapper.ClusterMapper;
import com.dtstack.taier.dao.mapper.ClusterTenantMapper;
import com.dtstack.taier.dao.mapper.ComponentMapper;
import com.dtstack.taier.develop.vo.console.ClusterEngineVO;
import com.dtstack.taier.develop.vo.console.ClusterVO;
import com.dtstack.taier.develop.vo.console.EngineVO;
import com.dtstack.taier.scheduler.service.ComponentService;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private ComponentService componentService;

    public Long addCluster(String clusterName) {
        if (clusterMapper.getByClusterName(clusterName) != null) {
            throw new TaierDefineException(ErrorCode.NAME_ALREADY_EXIST.getDescription());
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
            throw new TaierDefineException("Cluster cannot be empty");
        }
        Cluster cluster = clusterMapper.getOne(clusterId);
        if (null == cluster) {
            throw new TaierDefineException("Cluster does not exist");
        }
        if (DEFAULT_CLUSTER_ID.equals(clusterId)) {
            throw new TaierDefineException("The default cluster cannot be deleted");
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
        clusterVO.setComponentVOS(ComponentVO.toVOS(components));
        return clusterVO;
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


        return fillEngineInfo(clusterEngineMapping, cluster);
    }

    private ClusterEngineVO fillEngineInfo(Map<Long, Set<MultiEngineType>> clusterEngineMapping, Cluster cluster) {
        ClusterEngineVO vo = ClusterEngineVO.toVO(cluster);
        Set<MultiEngineType> engineList = clusterEngineMapping.get(vo.getClusterId());
        if (CollectionUtils.isNotEmpty(engineList)) {
            List<EngineVO> engineVOS = new ArrayList<>();
            for (MultiEngineType multiEngineType : engineList) {
                EngineVO engineVO = new EngineVO();
                engineVO.setEngineType(multiEngineType.getType());
                engineVO.setEngineName(multiEngineType.getName());
                engineVO.setClusterId(cluster.getId());
                engineVOS.add(engineVO);
            }
            engineVOS.sort(Comparator.comparingInt(EngineVO::getEngineType));
            vo.setEngines(engineVOS);
        }
        return vo;
    }

}
