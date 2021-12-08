package com.dtstack.engine.master.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApiModel
public class EngineVO  extends BaseEntity {

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

    private Long clusterId;

    private String engineName;

    private int engineType;

    public boolean isSecurity() {
        return security;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }

    public int getEngineType() {
        return engineType;
    }

    public void setEngineType(int engineType) {
        this.engineType = engineType;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
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

