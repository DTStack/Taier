package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.exception.LimitResourceException;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class AbstractFlinkResourceInfo implements EngineResourceInfo {

    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public List<NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    protected boolean judgeFlinkSessionResource(int sqlEnvParallel, int mrParallel) {
        if (sqlEnvParallel == 0 && mrParallel == 0) {
            throw new LimitResourceException("Flink task resource configuration error，sqlEnvParallel：" + sqlEnvParallel + ", mrParallel：" + mrParallel);
        }
        int availableSlots = 0;
        int totalSlots = 0;
        for (NodeResourceDetail resourceDetail : nodeResources) {
            availableSlots += resourceDetail.freeSlots;
            totalSlots += resourceDetail.slotsNumber;
        }
        //没有资源直接返回false
        if (availableSlots == 0) {
            return false;
        }
        int maxParallel = Math.max(sqlEnvParallel, mrParallel);
        if (totalSlots < maxParallel) {
            throw new LimitResourceException("Flink task allocation resource exceeds the maximum resource of the cluster, totalSlots:" + totalSlots + ",maxParallel:" + maxParallel);
        }
        return availableSlots >= maxParallel;
    }


    public static class NodeResourceDetail {
        private String nodeId;
        private int freeSlots;
        private int slotsNumber;

        public NodeResourceDetail(String nodeId, int freeSlots, int slotsNumber) {
            this.nodeId = nodeId;
            this.freeSlots = freeSlots;
            this.slotsNumber = slotsNumber;
        }
    }

}
