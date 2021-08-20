package com.dtstack.engine.remote.node.strategy;

import java.util.List;
import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2021/8/4 4:28 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface NodeInfoStrategy {

    String CLUSTER_INFO_NAME = "remote.cluster.worker.identifiers";

    String CLUSTER_INFO_NODE_NAME_IP = "remote.cluster.worker.%s.nodes.ip";

    String CLUSTER_INFO_NODE_NAME_PORT = "remote.cluster.worker.%s.nodes.port";

    /**
     * 获得节点信息
     *
     * @return 节点信息集合 key:identifier(标识) values: array(address 节点地址)
     *
     */
    Map<String, List<String>> getNodeInfo();
}
