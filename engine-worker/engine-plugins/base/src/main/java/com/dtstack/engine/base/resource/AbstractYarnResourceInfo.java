package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.pojo.JudgeResult;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class AbstractYarnResourceInfo implements EngineResourceInfo {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public List<NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    /**
     * 弹性容量, 默认不开启
     */
    protected boolean elasticCapacity = true;
    protected float capacity = 1;
    protected float queueCapacity = 1;
    protected int totalFreeCore = 0;
    protected int totalFreeMem = 0;
    protected int totalCore = 0;
    protected int totalMem = 0;
    protected int[] nmFreeCore = null;
    protected int[] nmFreeMem = null;
    protected int containerCoreMax;
    protected int containerMemoryMax;

    protected JudgeResult judgeYarnResource(List<InstanceInfo> instanceInfos) {
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            logger.info("judgeYarnResource, totalFreeCore={}, totalFreeMem={}", totalFreeCore, totalFreeMem);
            return JudgeResult.notOk(false, "totalFreeCore or totalFreeMem is 0");
        }
        int needTotalCore = 0;
        int needTotalMem = 0;
        for (InstanceInfo instanceInfo : instanceInfos) {
            if (instanceInfo.coresPerInstance > containerCoreMax) {
                logger.info("judgeYarnResource, containerCoreMax={}, coresPerInstance={}", containerCoreMax, instanceInfo.coresPerInstance);
                return JudgeResult.notOk(false, "the instance's per core larger than then maximum containerCore");
            }
            if (instanceInfo.memPerInstance > containerMemoryMax) {
                logger.info("judgeYarnResource, containerMemoryMax={}, memPerInstance={}", containerMemoryMax, instanceInfo.memPerInstance);
                return JudgeResult.notOk(false, "the instance's per memory larger than then maximum containerMemory");
            }
            needTotalCore += instanceInfo.instances * instanceInfo.coresPerInstance;
            needTotalMem += instanceInfo.instances * instanceInfo.memPerInstance;
        }
        if (needTotalCore == 0 || needTotalMem == 0) {
            throw new LimitResourceException("Yarn task resource configuration error，needTotalCore：" + 0 + ", needTotalMem：" + needTotalMem);
        }
        if (needTotalCore > (totalCore * queueCapacity)) {
            throw new LimitResourceException("The Yarn task is set to a core larger than the maximum allocated core");
        }
        if (needTotalMem > (totalMem * queueCapacity)) {
            throw new LimitResourceException("The Yarn task is set to a mem larger than the maximum allocated mem");
        }
        if (needTotalCore > (totalCore * capacity)) {
            logger.info("judgeYarnResource, needTotalCore={}, totalCore={}, capacity={}", needTotalCore, totalCore, capacity);
            return JudgeResult.notOk(false, "The task required core resources are greater than the total queue resources");
        }
        if (needTotalMem > (totalMem * capacity)) {
            logger.info("judgeYarnResource, needTotalMem={}, totalMem={}, capacity={}", needTotalMem, totalMem, capacity);
            return JudgeResult.notOk(false, "The task required memory resources are greater than the total queue resources");
        }
        for (InstanceInfo instanceInfo : instanceInfos) {
            JudgeResult judgeInstanceResource = judgeInstanceResource(instanceInfo.instances, instanceInfo.coresPerInstance, instanceInfo.memPerInstance);
            if (!judgeInstanceResource.getResult()) {
                logger.info("judgeYarnResource, nmFreeCore={}, nmFreeMem={} instanceInfo={}", nmFreeCore, nmFreeMem, instanceInfo);
                return judgeInstanceResource;
            }
        }
        return JudgeResult.ok();
    }

    private JudgeResult judgeInstanceResource(int instances, int coresPerInstance, int memPerInstance) {
        if (instances == 0 || coresPerInstance == 0 || memPerInstance == 0) {
            throw new LimitResourceException("Yarn task resource configuration error，instance：" + instances + ", coresPerInstance：" + coresPerInstance + ", memPerInstance：" + memPerInstance);
        }
        if (!judgeCores(instances, coresPerInstance)) {
            return JudgeResult.notOk(false, "Insufficient cpu resources of yarn cluster");
        }
        if (!judgeMem(instances, memPerInstance)) {
            return JudgeResult.notOk(false, "Insufficient memory resources of yarn cluster");
        }
        return JudgeResult.ok();
    }

    private boolean judgeCores(int instances, int coresPerInstance) {
        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFreeCore, coresPerInstance)) {
                return false;
            }
        }
        return true;
    }

    private boolean judgeMem(int instances, int memPerInstance) {
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

    public JudgeResult getYarnSlots(YarnClient yarnClient, String queueName, int yarnAccepterTaskNumber) {
        try {
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet).stream().
                    filter(report -> report.getQueue().endsWith(queueName)).collect(Collectors.toList());
            if (acceptedApps.size() > yarnAccepterTaskNumber) {
                logger.info("queueName:{} acceptedApps:{} >= yarnAccepterTaskNumber:{}", queueName, acceptedApps.size(), yarnAccepterTaskNumber);
                return JudgeResult.notOk(false, "queueName:" + queueName + " acceptedApps:" + acceptedApps.size() + " >= yarnAccepterTaskNumber:" + yarnAccepterTaskNumber);
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
                this.addNodeResource(new NodeResourceDetail(report.getNodeId().toString(), totalCores, usedCores, freeCores, totalMem, usedMem, freeMem));
            }

            calc();
            return JudgeResult.ok();
        } catch (IOException | YarnException e) {
            throw new RuntimeException(e);
        }
    }

    private float getQueueRemainCapacity(float coefficient, String queueName, List<QueueInfo> queueInfos) {
        float capacity = 0;
        for (QueueInfo queueInfo : queueInfos) {
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queueName, queueInfo.getChildQueues());
            }
            if (queueInfo.getQueueName().equalsIgnoreCase(queueName)) {
                this.queueCapacity = coefficient * queueInfo.getCapacity();
                capacity = queueCapacity * (1 - queueInfo.getCurrentCapacity());
                this.capacity = capacity;
            }
            if (capacity > 0) {
                return capacity;
            }
        }
        return capacity;
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

            nmFreeMem[index] = nodeFreeMem;
            nmFreeCore[index] = nodeFreeCores;
            index++;
        }
    }

    public static class InstanceInfo {
        int instances;
        int coresPerInstance;
        int memPerInstance;

        public InstanceInfo(int instances, int coresPerInstance, int memPerInstance) {
            this.instances = instances;
            this.coresPerInstance = coresPerInstance;
            this.memPerInstance = memPerInstance;
        }

        @Override
        public String toString() {
            return String.format("InstanceInfo[instances=%s, coresPerInstance=%s, memPerInstance=%s]", instances, coresPerInstance, memPerInstance);
        }

        public static InstanceInfo newRecord(int instances, int coresPerInstance, int memPerInstance) {
            return new InstanceInfo(instances, coresPerInstance, memPerInstance);
        }
    }

    public static class NodeResourceDetail {
        private String nodeId;
        private int coresTotal;
        private int coresUsed;
        private int coresFree;
        private int memoryTotal;
        private int memoryUsed;
        private int memoryFree;

        public NodeResourceDetail(String nodeId, int coresTotal, int coresUsed, int coresFree, int memoryTotal, int memoryUsed, int memoryFree) {
            this.nodeId = nodeId;
            this.coresTotal = coresTotal;
            this.coresUsed = coresUsed;
            this.coresFree = coresFree;
            this.memoryTotal = memoryTotal;
            this.memoryUsed = memoryUsed;
            this.memoryFree = memoryFree;
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
