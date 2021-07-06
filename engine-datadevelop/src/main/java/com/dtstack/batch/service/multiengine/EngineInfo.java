package com.dtstack.batch.service.multiengine;


import com.dtstack.dtcenter.common.enums.MultiEngineType;

import java.util.Map;

/**
 * Reason:
 * Date: 2019/4/25
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class EngineInfo {

    private MultiEngineType engineTypeEnum;

    private String engineType;

    public EngineInfo(MultiEngineType engineTypeEnum){
        this.engineTypeEnum = engineTypeEnum;
        this.engineType = engineTypeEnum.getName();
    }

    public void init(Map<String, String> conf){
    }

    public MultiEngineType getEngineTypeEnum(){
        return engineTypeEnum;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public String getEngineType() {
        return engineType;
    }

}
