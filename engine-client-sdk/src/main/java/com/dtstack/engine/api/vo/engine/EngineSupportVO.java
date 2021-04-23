package com.dtstack.engine.api.vo.engine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/7/29 5:01 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class EngineSupportVO {

    private Integer engineType;

    private List<Integer> supportComponent;

    /**
     * 元数据组件
     */
    private Integer metadataComponent;

    public Integer getMetadataComponent() {
        return metadataComponent;
    }

    public void setMetadataComponent(Integer metadataComponent) {
        this.metadataComponent = metadataComponent;
    }

    public Integer getEngineType() {
        return engineType;
    }

    public void setEngineType(Integer engineType) {
        this.engineType = engineType;
    }

    public List<Integer> getSupportComponent() {
        return supportComponent;
    }

    public void setSupportComponent(List<Integer> supportComponent) {
        this.supportComponent = supportComponent;
    }
}
