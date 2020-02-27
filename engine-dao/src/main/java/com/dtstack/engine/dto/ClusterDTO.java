package com.dtstack.engine.dto;

import com.dtstack.engine.domain.Cluster;

import java.util.List;

public class ClusterDTO extends Cluster {

    private List<EngineDTO> engineList;

    public List<EngineDTO> getEngineList() {
        return engineList;
    }

    public void setEngineList(List<EngineDTO> engineList) {
        this.engineList = engineList;
    }
}

