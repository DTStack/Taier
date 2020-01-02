package com.dtstack.engine.common;

import com.dtstack.engine.common.enums.EDeployType;
import com.dtstack.engine.common.enums.EngineType;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * 执行引擎的部署方式.standalone, yarn
 * Date: 2017/11/13
 * Company: www.dtstack.com
 * @author xuchao
 */

public class EngineDeployInfo {

    private static final Logger logger = LoggerFactory.getLogger(EngineDeployInfo.class);

    private Map<String, Integer> deployMap = Maps.newHashMap();

    public EngineDeployInfo(List<Map<String, Object>> engineTypeList) {
        for(Map<String, Object> engineInfo : engineTypeList){
            String typeName = (String) engineInfo.get("typeName");
            String typeNameNoVersion = EngineType.getEngineTypeWithoutVersion(typeName);
            EDeployType deployType = null;

            if(EngineType.isFlink(typeName)){
                String deployMode = (String) engineInfo.get("clusterMode");
                deployType = EDeployType.getDeployType(deployMode);

            }else if(EngineType.isSpark(typeName)){
                deployType = engineInfo.containsKey("sparkYarnArchive") ? EDeployType.YARN : EDeployType.STANDALONE;

            }else if(EngineType.isLearning(typeName)){
                deployType = EDeployType.YARN;

            }else{
                deployType = EDeployType.STANDALONE;

            }

            deployMap.put(typeNameNoVersion, deployType.getType());
        }
    }

    public Map<String, Integer> getDeployMap() {
        return deployMap;
    }
}
