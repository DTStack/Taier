package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.YarnException;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class EngineResourceInfo {

    public final static String LIMIT_RESOURCE_ERROR = "LIMIT RESOURCE ERROR:";

    /**
     * 弹性容量, 默认不开启
     */
    protected boolean elasticCapacity = false;
    protected float capacity = 1;
    protected float queueCapacity = 1;
    protected int totalFreeCore = 0;
    protected int totalFreeMem = 0;
    protected int totalCore = 0;
    protected int totalMem = 0;
    protected int[] nmFreeCore= null;
    protected int[] nmFreeMem = null;
    protected int containerCoreMax;
    protected int containerMemoryMax;

    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public abstract boolean judgeSlots(JobClient jobClient);

    protected boolean judgeFlinkSessionResource(int sqlEnvParallel, int mrParallel) {
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

    protected boolean judgeYarnResource(int instances, int coresPerInstance, int memPerInstance) {
        if (instances == 0 || coresPerInstance == 0 || memPerInstance == 0) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "Yarn task resource configuration error，instance：" + instances + ", coresPerInstance：" + coresPerInstance + ", memPerInstance：" + memPerInstance);
        }
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            return false;
        }

        if (!judgeCores(instances, coresPerInstance)) {
            return false;
        }
        if (!judgeMem(instances, memPerInstance)) {
            return false;
        }
        return true;
    }

    protected void calc() {
        nmFreeCore = new int[nodeResources.size()];
        nmFreeMem = new int[nodeResources.size()];
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

            nmFreeCore[index++] = nodeFreeCores;
            nmFreeMem[index++] = nodeFreeMem;
        }
    }

    private boolean judgeCores(int instances, int coresPerInstance) {
        int needCores = instances * coresPerInstance;
        if (needCores > (totalCore * queueCapacity)) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "The Yarn task is set to a core larger than the maximum allocated core");
        }
        if (needCores > (totalMem * capacity)) {
            return false;
        }
        if (instances * coresPerInstance > containerCoreMax) {
            return false;
        }
        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFreeCore, coresPerInstance)) {
                return false;
            }
        }
        return true;
    }

    private boolean judgeMem(int instances, int memPerInstance) {
        int needMems = instances * memPerInstance;
        if (needMems > (totalMem * queueCapacity)) {
            throw new RdosException(LIMIT_RESOURCE_ERROR + "The Yarn task is set to MEM larger than the maximum for the cluster");
        }
        if (needMems > (totalMem * capacity)) {
            return false;
        }
        if (instances * memPerInstance > containerMemoryMax) {
            return false;
        }
        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFreeMem, memPerInstance)) {
                return false;
            }
        }
        return true;
    }

    private boolean allocateResource(int[] nodeManagers, int toAllocate) {
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

    public void getYarnSlots(YarnClient yarnClient, String queueName, int yarnAccepterTaskNumber) throws IOException, YarnException {
        EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
        enumSet.add(YarnApplicationState.ACCEPTED);
        List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet).stream().
                filter(report -> report.getQueue().endsWith(queueName)).collect(Collectors.toList());
        if (acceptedApps.size() > yarnAccepterTaskNumber) {
            return;
        }

        List<NodeReport> nodeReports = yarnClient.getNodeReports(NodeState.RUNNING);
        if (!elasticCapacity) {
            getQueueRemainCapacity(1, queueName, yarnClient.getRootQueueInfos());
        }
        for (NodeReport report : nodeReports) {
            Resource capability = report.getCapability();
            Resource used = report.getUsed();
            int totalMem = capability.getMemory();
            int totalCores = capability.getVirtualCores();

            int usedMem = used.getMemory();
            int usedCores = used.getVirtualCores();

            int freeCores = totalCores - usedCores;
            int freeMem = totalMem - usedMem;

            if (freeCores > containerCoreMax) {
                containerCoreMax = freeCores;
            }
            if (freeMem > containerMemoryMax) {
                containerMemoryMax = freeMem;
            }
            this.addNodeResource(new EngineResourceInfo.NodeResourceDetail(report.getNodeId().toString(), totalCores, usedCores, freeCores, totalMem, usedMem, freeMem));
        }

        calc();
    }

    private float getQueueRemainCapacity(float coefficient, String queueName, List<QueueInfo> queueInfos) {
        for (QueueInfo queueInfo : queueInfos) {
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queueName, queueInfo.getChildQueues());
            }
            if (queueInfo.getQueueName().equalsIgnoreCase(queueName)) {
                queueCapacity = coefficient * queueInfo.getCapacity();
                capacity = queueCapacity * (1 - queueInfo.getCurrentCapacity());
            }
            if (capacity > 0) {
                return capacity;
            }
        }
        return capacity;
    }

    public static class NodeResourceDetail {
        private String nodeId;
        private int coresTotal;
        private int coresUsed;
        private int coresFree;
        private int memoryTotal;
        private int memoryUsed;
        private int memoryFree;
        private int freeSlots;
        private int slotsNumber;

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

    public void setElasticCapacity(boolean elasticCapacity) {
        this.elasticCapacity = elasticCapacity;
    }

    public boolean getElasticCapacity() {
        return elasticCapacity;
    }

    public float getCapacity() {
        return capacity;
    }

    public float getQueueCapacity() {
        return queueCapacity;
    }

    public int getContainerCoreMax() {
        return containerCoreMax;
    }

    public int getContainerMemoryMax() {
        return containerMemoryMax;
    }
}
