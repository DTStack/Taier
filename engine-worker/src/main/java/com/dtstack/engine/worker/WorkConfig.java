package com.dtstack.engine.worker;

import com.dtstack.engine.common.config.AbstractYamlConfig;
import com.dtstack.engine.common.exception.EngineAgumentsException;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/1/2
 */
public class WorkConfig extends AbstractYamlConfig {

    @Override
    public String getConfigFilePath() {
        return System.getProperty("user.dir") + "/conf/node.yml";
    }

    @Override
    public void checkEngineArguments(Map<String, Object> nodeConfig) throws EngineAgumentsException {
        super.checkEngineArguments(nodeConfig);
        String nodeZkAddress = (String) nodeConfig.get("nodeZkAddress");
        if (StringUtils.isBlank(nodeZkAddress)) {
            throw new EngineAgumentsException("nodeZkAddress");
        }
    }
}
