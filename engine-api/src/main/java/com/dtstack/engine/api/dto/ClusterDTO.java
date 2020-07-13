package com.dtstack.engine.api.dto;

import com.dtstack.engine.api.domain.Cluster;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel
public class ClusterDTO extends Cluster {

    private List<EngineDTO> engineList;

    private Long clusterId;

    private List<ComponentDTO> components;

    public List<ComponentDTO> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentDTO> components) {
        this.components = components;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public List<EngineDTO> getEngineList() {
        return engineList;
    }

    public void setEngineList(List<EngineDTO> engineList) {
        this.engineList = engineList;
    }
}

