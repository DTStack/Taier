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

package com.dtstack.taier.develop.vo.console;

import com.dtstack.taier.dao.domain.Cluster;
import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ClusterEngineVO extends Cluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterEngineVO.class);

    private Long clusterId;

    private List<EngineVO> engines;

    private Long tenantId;

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public static ClusterEngineVO toVO(Cluster cluster) {
        ClusterEngineVO vo = new ClusterEngineVO();
        try {
            BeanUtils.copyProperties(cluster, vo);
            vo.setClusterId(cluster.getId());
        } catch (Throwable e) {
            LOGGER.error("ClusterEngineVO.toVO error:", e);
        }
        return vo;
    }

    public static List<ClusterEngineVO> toVOs(List<Cluster> clusterList) {
        List<ClusterEngineVO> vos = new ArrayList<>();
        for (Cluster cluster : clusterList) {
            vos.add(toVO(cluster));
        }
        return vos;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<EngineVO> getEngines() {
        return engines;
    }

    public void setEngines(List<EngineVO> engines) {
        this.engines = engines;
    }
}

