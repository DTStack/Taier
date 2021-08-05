package com.dtstack.engine.remote.node.strategy;

import com.dtstack.engine.remote.config.RemoteConfig;
import com.dtstack.engine.remote.constant.GlobalConstant;
import com.dtstack.engine.remote.exception.NoNodeException;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Auther: dazhi
 * @Date: 2021/8/4 4:33 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class ConfigurationNodeInfoStrategy implements NodeInfoStrategy {

    @Override
    public Map<String, List<String>> getNodeInfo() {
        String values = RemoteConfig.getValueByKey(CLUSTER_INFO_NAME);

        if (StringUtils.isBlank(values)) {
            throw new NoNodeException("Please configure node information in properties");
        }

        String[] split = values.split(",");

        if (split.length > 0) {
            Map<String, List<String>> nodeInfoMap = new ConcurrentHashMap<>();

            for (String identifier : split) {

                String nodes = RemoteConfig.getValueByKey(String.format(CLUSTER_INFO_NODE_NAME,identifier));

                if (StringUtils.isBlank(nodes)) {
                    String[] nodeArray = nodes.split(",");
                    List<String> nodeLists = Arrays.asList(nodeArray);
                    nodeInfoMap.put(identifier,nodeLists);
                }
            }

            return nodeInfoMap;
        } else {
            return null;
        }
    }
}
