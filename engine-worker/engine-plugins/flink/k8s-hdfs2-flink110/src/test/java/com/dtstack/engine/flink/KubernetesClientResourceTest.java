package com.dtstack.engine.flink;

import com.dtstack.engine.base.resource.AbstractK8sResourceInfo;
import com.google.common.collect.Lists;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.internal.NodeMetricOperationsImpl;
import org.apache.flink.kubernetes.utils.KubernetesUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/4/10
 */
public class KubernetesClientResourceTest {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final static String PENDING_PHASE = "Pending";
    private final static String CPU = "cpu";
    private final static String MEMORY = "memory";


    protected List<AbstractK8sResourceInfo.NodeResourceDetail> nodeResources = Lists.newArrayList();

    public List<AbstractK8sResourceInfo.NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(AbstractK8sResourceInfo.NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    private Config config;

    @Before
    public void setUp() {

        try {
            config = Config.fromKubeconfig(KubernetesUtils.getContentFromFile("/Users/zhaozhangwan/.kube/config"));
        } catch (IOException e) {
            throw new KubernetesClientException("Load kubernetes config failed.", e);
        }
    }

    @Test
    public void getResource() {

        int allowPendingPodSize = 0;
        KubernetesClient kubernetesClient = new DefaultKubernetesClient(config);
        if (allowPendingPodSize > 0) {
            List<Pod> pods = kubernetesClient.pods().list().getItems();
            List<Pod> pendingPods = pods.stream().filter(p -> PENDING_PHASE.equals(p.getStatus().getPhase())).collect(Collectors.toList());
            if (pendingPods.size() > allowPendingPodSize) {
                logger.info("pendingPods-size:{} allowPendingPodSize:{}", pendingPods.size(), allowPendingPodSize);
                return;
            }
        }

        List<Node> nodes = kubernetesClient.nodes().list().getItems();
        Map<String, NodeStatus> nodeStatusMap = new HashMap<>(nodes.size());
        for (Node node : nodes) {
            nodeStatusMap.put(node.getMetadata().getName(), node.getStatus());
        }

        NodeMetricOperationsImpl nodeMetricOperations = kubernetesClient.top().nodes();
        List<NodeMetrics> nodeMetrics = nodeMetricOperations.metrics().getItems();
        for (NodeMetrics nodeMetric : nodeMetrics) {
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

    protected double totalFreeCore = 0;
    protected double totalFreeMem = 0;
    protected double totalCore = 0;
    protected double totalMem = 0;
    protected double[] nmFreeCore = null;
    protected double[] nmFreeMem = null;
    private final static double retainMem = Quantity.getAmountInBytes(new Quantity("512M")).doubleValue();
    private final static double retainCpu = Quantity.getAmountInBytes(new Quantity("1")).doubleValue();

    protected void calc() {
        nmFreeCore = new double[nodeResources.size()];
        nmFreeMem = new double[nodeResources.size()];

        int index = 0;
        //执行时，统一对每个node保留512M和1core
        for (AbstractK8sResourceInfo.NodeResourceDetail resourceDetail : nodeResources) {
            double nodeFreeMem = Math.max(resourceDetail.memoryFree - retainMem, 0);
            double nodeMem = resourceDetail.memoryTotal - retainMem;

            double nodeFreeCores = Math.max(resourceDetail.coresFree - retainCpu, 0);
            double nodeCores = resourceDetail.coresTotal - retainCpu;

            totalFreeMem += nodeFreeMem;
            totalFreeCore += nodeFreeCores;
            totalCore += nodeCores;
            totalMem += nodeMem;

            nmFreeMem[index] = nodeFreeMem;
            nmFreeCore[index] = nodeFreeCores;
            index++;
        }
    }

    @Test
    public void nodeUsage() {
        KubernetesClient client = new DefaultKubernetesClient(config);
        NodeMetricOperationsImpl nodeMetricOperations = client.top().nodes();
        List<NodeMetrics> nodeMetrics = nodeMetricOperations.metrics().getItems();
        for (NodeMetrics nodeMetric : nodeMetrics) {
            System.out.println(nodeMetric.getUsage());
        }
    }


    @Test
    public void nodes() {
        KubernetesClient client = new DefaultKubernetesClient(config);
        List<Node> nodes = client.nodes().list().getItems();
        for (Node node : nodes) {
            System.out.println(node.getMetadata());

            NodeStatus nodeStatus = node.getStatus();
            System.out.println(nodeStatus.getNodeInfo());
            Map<String, Quantity> capacity = nodeStatus.getCapacity();
            Map<String, Quantity> allocatable = nodeStatus.getAllocatable();
            System.out.println(capacity.get("cpu"));
            System.out.println(allocatable.get("cpu"));
            System.out.println(capacity.get("memory"));
            System.out.println(allocatable.get("memory"));
        }
    }


}
