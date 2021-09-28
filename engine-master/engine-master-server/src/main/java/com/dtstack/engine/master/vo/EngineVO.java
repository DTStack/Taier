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

package com.dtstack.engine.master.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.Engine;
import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class EngineVO extends Engine {

    private static final Logger LOGGER = LoggerFactory.getLogger(EngineVO.class);

    private Long engineId;

    private List<QueueVO> queues;

    private boolean security;

    private List<ComponentVO> components;

    private JSONObject resource;

    /**
     * yarn 还是k8s调度
     */
    private String resourceType;

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public static EngineVO toVO(Engine engine) {
        EngineVO vo = new EngineVO();
        try {
            BeanUtils.copyProperties(engine, vo);
            vo.setEngineId(engine.getId());

            JSONObject resource = new JSONObject();
            resource.put("totalCore", engine.getTotalCore());
            resource.put("totalMemory", engine.getTotalMemory());
            resource.put("totalNode", engine.getTotalNode());
            vo.setResource(resource);

        } catch (Throwable e) {
            LOGGER.error("EngineVO.toVO error:",e);
        }
        return vo;
    }

    public static List<EngineVO> toVOs(List<Engine> engines) {
        List<EngineVO> vos = new ArrayList<>();
        for (Engine engine : engines) {
            vos.add(toVO(engine));
        }
        return vos;
    }

    public List<ComponentVO> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentVO> components) {
        this.components = components;
    }

    public JSONObject getResource() {
        return resource;
    }

    public void setResource(JSONObject resource) {
        this.resource = resource;
    }

    public boolean getSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public List<QueueVO> getQueues() {
        return queues;
    }

    public void setQueues(List<QueueVO> queues) {
        this.queues = queues;
    }
}

