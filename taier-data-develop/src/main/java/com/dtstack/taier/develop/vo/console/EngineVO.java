package com.dtstack.taier.develop.vo.console;

import com.dtstack.taier.dao.domain.BaseEntity;
import com.dtstack.taier.scheduler.vo.ComponentVO;
import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel
public class EngineVO extends BaseEntity {

    private List<ComponentVO> components;

    private Long clusterId;

    private String engineName;

    private int engineType;


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

    public List<ComponentVO> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentVO> components) {
        this.components = components;
    }
}

