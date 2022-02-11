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

package com.dtstack.taiga.develop.vo.console;

import com.dtstack.taiga.dao.domain.Cluster;
import com.dtstack.taiga.scheduler.vo.SchedulingVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.List;

@ApiModel
public class ClusterVO extends Cluster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClusterVO.class);

    private Long clusterId;


    /**
     * 组件类型
     */
    @ApiModelProperty(notes = "组件类型")
    private List<SchedulingVo> scheduling;

    private boolean canModifyMetadata = true;

    @ApiModelProperty(notes = "是否能修改metadata组件")
    public boolean isCanModifyMetadata() {
        return canModifyMetadata;
    }

    public void setCanModifyMetadata(boolean canModifyMetadata) {
        this.canModifyMetadata = canModifyMetadata;
    }

    public List<SchedulingVo> getScheduling() {
        return scheduling;
    }

    public void setScheduling(List<SchedulingVo> scheduling) {
        this.scheduling = scheduling;
    }

    public static ClusterVO toVO(Cluster cluster) {
        ClusterVO vo = new ClusterVO();
        try {
            BeanUtils.copyProperties(cluster, vo);
            vo.setClusterId(cluster.getId());
        } catch (Throwable e) {
            LOGGER.error("ClusterVO.toVO error:",e);
        }
        return vo;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }
}

