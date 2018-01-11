package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 引擎的资源信息
 * Date: 2017/11/27
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class EngineResourceInfo {

    protected Map<String, NodeResourceInfo> nodeResourceMap = Maps.newHashMap();

    public Map<String, NodeResourceInfo> getNodeResourceMap() {
        return nodeResourceMap;
    }

    public void addNodeResource(String nodeId, Map<String, Object> prop){

        NodeResourceInfo resourceInfo = new NodeResourceInfo(nodeId, prop);
        nodeResourceMap.put(nodeId, resourceInfo);
    }

    /**
     * FIXME 注意是否会有多线程问题
     * 默认返回true, 需要子类自定义
     * @param jobClient
     * @return
     *
     */
    public boolean judgeSlots(JobClient jobClient){
        return true;
    }


    /***
     * 节点的资源信息
     */
    protected class NodeResourceInfo {

        private String nodeId;

        private Map<String, Object> nodeDetail = Maps.newHashMap();

        public NodeResourceInfo(String nodeId, Map<String, Object> nodeDetail){
            this.nodeId = nodeId;
            this.nodeDetail = nodeDetail;
        }

        public String getNodeId() {
            return nodeId;
        }

        public void setNodeId(String nodeId) {
            this.nodeId = nodeId;
        }

        public Object addProp(String key, Object val){
            return nodeDetail.put(key, val);
        }

        public Object getProp(String key){
            return nodeDetail.get(key);
        }

        public Map<String, Object> getNodeDetail() {
            return nodeDetail;
        }

        public void setNodeDetail(Map<String, Object> nodeDetail) {
            this.nodeDetail = nodeDetail;
        }
    }
}
