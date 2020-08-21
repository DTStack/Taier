package com.dtstack.engine.base.resource;

import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.internal.NodeMetricOperationsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class AbstractK8sResourceInfo implements EngineResourceInfo {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final static String PENDING_PHASE = "Pending";
    private final static String CPU = "cpu";
    private final static String MEMORY = "memory";

    private final static double RETAIN_MEM = Quantity.getAmountInBytes(new Quantity("512M")).doubleValue();
    private final static double RETAIN_CPU = Quantity.getAmountInBytes(new Quantity("1")).doubleValue();

    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public List<NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    protected double totalFreeCore = 0;
    protected double totalFreeMem = 0;
    protected double totalCore = 0;
    protected double totalMem = 0;
    protected double[] nmFreeCore = null;
    protected double[] nmFreeMem = null;

    protected JudgeResult judgeResource(List<InstanceInfo> instanceInfos) {
        if (totalFreeCore == 0 || totalFreeMem == 0) {
            logger.info("judgeResource, totalFreeCore={}, totalFreeMem={}", totalFreeCore, totalFreeMem);
            return JudgeResult.notOk(false, "totalFreeCore or totalFreeMem is 0");
        }
        double needTotalCore = 0;
        double needTotalMem = 0;
        for (InstanceInfo instanceInfo : instanceInfos) {
            needTotalCore += instanceInfo.instances * instanceInfo.coresPerInstance;
            needTotalMem += instanceInfo.instances * instanceInfo.memPerInstance;
        }
        if (needTotalCore == 0 || needTotalMem == 0) {
            throw new LimitResourceException("task resource configuration error，needTotalCore：" + 0 + ", needTotalMem：" + needTotalMem);
        }
        if (needTotalCore > totalCore) {
            logger.info("judgeResource, needTotalCore={}, totalCore={}", needTotalCore, totalCore);
            throw new LimitResourceException("The task required core resources are greater than the total resources");
        }
        if (needTotalMem > totalMem) {
            logger.info("judgeResource, needTotalMem={}, totalMem={}", needTotalMem, totalMem);
            throw new LimitResourceException("The task required memory resources are greater than the total resources");
        }
        for (InstanceInfo instanceInfo : instanceInfos) {
            JudgeResult judgeInstanceResource = judgeInstanceResource(instanceInfo.instances, instanceInfo.coresPerInstance, instanceInfo.memPerInstance);
            if (!judgeInstanceResource.getResult()) {
                logger.info("judgeResource, nmFreeCore={}, nmFreeMem={} instanceInfo={}", nmFreeCore, nmFreeMem, instanceInfo);
                return judgeInstanceResource;
            }
        }
        return JudgeResult.ok();
    }

    private JudgeResult judgeInstanceResource(int instances, double coresPerInstance, double memPerInstance) {
        if (instances == 0 || coresPerInstance == 0 || memPerInstance == 0) {
            throw new LimitResourceException("task resource configuration error，instance：" + instances + ", coresPerInstance：" + coresPerInstance + ", memPerInstance：" + memPerInstance);
        }
        if (!judgeCores(instances, coresPerInstance)) {
            return JudgeResult.notOk(false, "Insufficient cpu resources of kubernetes cluster");
        }
        if (!judgeMem(instances, memPerInstance)) {
            return JudgeResult.notOk(false, "Insufficient memory resources of kubernetes cluster");
        }
        return JudgeResult.ok();
    }

    private boolean judgeCores(int instances, double coresPerInstance) {
        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFreeCore, coresPerInstance)) {
                return false;
            }
        }
        return true;
    }

    private boolean judgeMem(int instances, double memPerInstance) {
        for (int i = 1; i <= instances; i++) {
            if (!allocateResource(nmFreeMem, memPerInstance)) {
                return false;
            }
        }
        return true;
    }

    private boolean allocateResource(double[] node, double toAllocate) {
        for (int i = 0; i < node.length; i++) {
            if (node[i] >= toAllocate) {
                node[i] -= toAllocate;
                return true;
            }
        }
        return false;
    }

    public void getResource(KubernetesClient kubernetesClient) {

        List<Node> nodes = kubernetesClient.nodes().list().getItems();
        Map<String, NodeStatus> nodeStatusMap = new HashMap<>(nodes.size());
        for (Node node : nodes) {
            nodeStatusMap.put(node.getMetadata().getName(), node.getStatus());
        }

        NodeMetricOperationsImpl nodeMetricOperations = kubernetesClient.top().nodes();
        List<NodeMetrics> nodeMetrics = nodeMetricOperations.metrics().getItems();
        for (NodeMetrics nodeMetric : nodeMetrics) {
            System.out.println(nodeMetric);

            String nodeName = nodeMetric.getMetadata().getName();
            NodeStatus nodeStatus = nodeStatusMap.get(nodeName);

            Map<String, Quantity> allocatable = nodeStatus.getAllocatable();
            Map<String, Quantity> usage = nodeMetric.getUsage();

            BigDecimal cpuAllocatable = Quantity.getAmountInBytes(allocatable.get(CPU));
            BigDecimal cpuUsage = Quantity.getAmountInBytes(usage.get(CPU));

            BigDecimal menAllocatable = Quantity.getAmountInBytes(allocatable.get(MEMORY));
            BigDecimal menUsage = Quantity.getAmountInBytes(usage.get(MEMORY));

            double freeCores = cpuAllocatable.subtract(cpuUsage).doubleValue();
            double freeMem = menAllocatable.subtract(menUsage).doubleValue();

            this.addNodeResource(new AbstractK8sResourceInfo.NodeResourceDetail(nodeName, cpuAllocatable.doubleValue(), cpuUsage.doubleValue(), freeCores, menAllocatable.doubleValue(), menUsage.doubleValue(), freeMem));
        }


        calc();
    }

    protected void calc() {
        nmFreeCore = new double[nodeResources.size()];
        nmFreeMem = new double[nodeResources.size()];

        int index = 0;
        //执行时，统一对每个node保留512M和1core
        for (AbstractK8sResourceInfo.NodeResourceDetail resourceDetail : nodeResources) {
            double nodeFreeMem = Math.max(resourceDetail.memoryFree - RETAIN_MEM, 0);
            double nodeMem = resourceDetail.memoryTotal - RETAIN_MEM;

            double nodeFreeCores = Math.max(resourceDetail.coresFree - RETAIN_CPU, 0);
            double nodeCores = resourceDetail.coresTotal - RETAIN_CPU;

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
        double coresPerInstance;
        double memPerInstance;

        public InstanceInfo(int instances, double coresPerInstance, double memPerInstance) {
            this.instances = instances;
            this.coresPerInstance = coresPerInstance;
            this.memPerInstance = memPerInstance;
        }

        @Override
        public String toString() {
            return String.format("InstanceInfo[instances=%s, coresPerInstance=%s, memPerInstance=%s]", instances, coresPerInstance, memPerInstance);
        }

        public static InstanceInfo newRecord(int instances, double coresPerInstance, double memPerInstance) {
            return new InstanceInfo(instances, coresPerInstance, memPerInstance);
        }
    }

    public static class NodeResourceDetail {
        private String nodeId;
        public double coresTotal;
        public double coresUsed;
        public double coresFree;
        public double memoryTotal;
        public double memoryUsed;
        public double memoryFree;

        public NodeResourceDetail(String nodeId, double coresTotal, double coresUsed, double coresFree, double memoryTotal, double memoryUsed, double memoryFree) {
            this.nodeId = nodeId;
            this.coresTotal = coresTotal;
            this.coresUsed = coresUsed;
            this.coresFree = coresFree;
            this.memoryTotal = memoryTotal;
            this.memoryUsed = memoryUsed;
            this.memoryFree = memoryFree;
        }
    }
}
