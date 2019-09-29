package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class EngineResourceInfo {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public final static String LIMIT_RESOURCE_ERROR = "LIMIT RESOURCE ERROR:";
    protected float capacity = 1;
    protected float queueCapacity = 1;
    protected int totalFreeCore = 0;
    protected int totalFreeMem = 0;
    protected int totalCore = 0;
    protected int totalMem = 0;
    protected int[] nmFree = null;


    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public abstract boolean judgeSlots(JobClient jobClient);

    public boolean judgeFlinkResource(int sqlEnvParallel, int mrParallel) {
        if (sqlEnvParallel == 0 && mrParallel == 0) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "Flink task resource configuration error，sqlEnvParallel：" + sqlEnvParallel + ", mrParallel：" + mrParallel);
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
            throw new RdosException(LIMIT_RESOURCE_ERROR + "Flink task allocation resource exceeds the maximum resource of the cluster, totalSlots:" + totalSlots + ",maxParallel:" + maxParallel);
        }
        return availableSlots >= maxParallel;
    }

    /**
     *  离线任务yarnsession模式运行，资源判断
     * @return
     */
    public boolean judgeSessionResource() {
        int availableSlots = 0;
        for (NodeResourceDetail resourceDetail : nodeResources) {
            availableSlots += resourceDetail.freeSlots;
        }
        //没有资源直接返回false
        if (availableSlots == 0) {
            return false;
        }

        return true;
    }

    public boolean judgeYarnResource(int instances, int coresPerInstance, int memPerInstance) {
        if (instances == 0 || coresPerInstance == 0 || memPerInstance == 0) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "Yarn task resource configuration error，instance：" + instances + ", coresPerInstance：" + coresPerInstance + ", memPerInstance：" + memPerInstance);
        }
        calc();
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            logger.info("judgeYarnResource totalFreeCore={}, totalFreeMem={}", totalFreeCore, totalFreeMem);
            return false;
        }

        if (!judgeCores(instances, coresPerInstance, totalFreeCore, totalCore)) {
            logger.info("judgeYarnResource totalCore={}, totalFreeCore={}, totalFreeMem={}, coresPerInstance={}, instances={}, capacity={}", totalCore, totalFreeCore, totalFreeMem, coresPerInstance, instances, capacity);
            return false;
        }
        if (!judgeMem(instances, memPerInstance, totalFreeMem, totalMem)) {
            logger.info("judgeYarnResource totalMem={}, totalFreeCore={}, totalFreeMem={}, nmFree={}, coresPerInstance={}, instances={}, capacity={}", totalMem, totalFreeCore, totalFreeMem, nmFree, coresPerInstance, instances, capacity);
            return false;
        }
        return true;
    }

    protected void calc() {
        nmFree = new int[nodeResources.size()];
        int index = 0;
        //yarn 方式执行时，统一对每个node保留512M和1core
        for (NodeResourceDetail resourceDetail : nodeResources) {
            int nodeFreeMem = Math.max(resourceDetail.memoryFree - 512, 0);
            int nodeFreeCores = Math.max(resourceDetail.coresFree - 1, 0);
            int nodeCores = resourceDetail.coresTotal - 1;
            int nodeMem = resourceDetail.memoryTotal - 512;

            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;
            totalCore += nodeCores;
            totalMem += nodeMem;

            nmFree[index++] = nodeFreeMem;
        }
    }

    protected boolean judgeCores(int instances, int coresPerInstance, int freeCore, int totalCore) {
        int needCores = instances * coresPerInstance;
        if (needCores > (totalCore * capacity)) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "The Yarn task is set to a core larger than the maximum allocated core");
        }
        return needCores <= (freeCore * capacity);
    }

    protected boolean judgeMem(int instances, int memPerInstance, int freeMem, int totalMem) {
        int needTotal = instances * memPerInstance;
        if (needTotal > (totalMem * capacity)) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "The Yarn task is set to MEM larger than the maximum for the cluster");
        }
        if (needTotal > (freeMem * capacity)) {
            return false;
        }

        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFree, memPerInstance)) {
                return false;
            }
        }
        return true;
    }

    protected boolean allocateResource(int[] nodeManagers, int toAllocate) {
        for (int i = 0; i < nodeManagers.length; i++) {
            if (nodeManagers[i] >= toAllocate) {
                nodeManagers[i] -= toAllocate;
                return true;
            }
        }
        return false;
    }

    public List<NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    public static class NodeResourceDetail {
        public String nodeId;
        public int coresTotal;
        public int coresUsed;
        public int coresFree;
        public int memoryTotal;
        public int memoryUsed;
        public int memoryFree;
        public int freeSlots;
        public int slotsNumber;

        public NodeResourceDetail(String nodeId, int coresTotal, int coresUsed, int coresFree, int memoryTotal, int memoryUsed, int memoryFree) {
            this.nodeId = nodeId;
            this.coresTotal = coresTotal;
            this.coresUsed = coresUsed;
            this.coresFree = coresFree;
            this.memoryTotal = memoryTotal;
            this.memoryUsed = memoryUsed;
            this.memoryFree = memoryFree;
        }

        public NodeResourceDetail(String nodeId, int freeSlots, int slotsNumber) {
            this.nodeId = nodeId;
            this.freeSlots = freeSlots;
            this.slotsNumber = slotsNumber;
        }
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public void setQueueCapacity(float queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
