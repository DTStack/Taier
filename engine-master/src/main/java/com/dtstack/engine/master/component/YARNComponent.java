package com.dtstack.engine.master.component;

import com.dtstack.dtcenter.common.hadoop.HadoopConfTool;
import com.dtstack.engine.api.domain.ClusterResourceDescription;
import com.dtstack.engine.master.enums.KerberosKey;
import com.dtstack.engine.master.utils.HadoopConf;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YARNComponent extends BaseComponent {

    private List<NodeDescription> clusterNodes = new ArrayList<>();

    private ClusterResourceDescription resourceDescription;

    private YarnClient yarnClient;

    public YARNComponent(Map<String, Object> allConfig) {
        super(allConfig);
    }

    @Override
    public void testConnection() throws Exception {

    }

    public void initClusterResource(boolean closeYarnClient) throws Exception{
        try {
            initYarnClient();

            List<NodeReport> nodes = yarnClient.getNodeReports(NodeState.RUNNING);
            for (NodeReport rep : nodes) {
                NodeDescription node = new NodeDescription();
                node.setMemory(rep.getCapability().getMemory());
                node.setUsedMemory(rep.getUsed().getMemory());
                node.setUsedVirtualCores(rep.getUsed().getVirtualCores());
                node.setVirtualCores(rep.getCapability().getVirtualCores());

                clusterNodes.add(node);
            }

            initResourceDescription(yarnClient);
        } finally {
            if(closeYarnClient){
                closeYarnClient();
            }
        }
    }

    private void initResourceDescription(YarnClient yarnClient) throws Exception{
        int totalMemory = 0;
        int totalCores = 0;
        for (NodeDescription clusterNode : clusterNodes) {
            totalMemory += clusterNode.getMemory();
            totalCores += clusterNode.getVirtualCores();
        }

        List<ClusterResourceDescription.QueueDescription> descriptions = getQueueDescription(null, yarnClient.getRootQueueInfos());
        resourceDescription = new ClusterResourceDescription(clusterNodes.size(), totalMemory,totalCores,descriptions);
    }

    private List<ClusterResourceDescription.QueueDescription> getQueueDescription(String parentPath, List<QueueInfo> queueInfos) {
        List<ClusterResourceDescription.QueueDescription> descriptions = new ArrayList<>(queueInfos.size());
        parentPath = StringUtils.isBlank(parentPath) ? "" : parentPath + ".";
        for (QueueInfo queueInfo : queueInfos) {
            String queuePath = parentPath + queueInfo.getQueueName();
            ClusterResourceDescription.QueueDescription queueDescription = new ClusterResourceDescription.QueueDescription();
            queueDescription.setQueueName(queueInfo.getQueueName());
            queueDescription.setCapacity(String.valueOf(queueInfo.getCapacity()));
            queueDescription.setMaximumCapacity(String.valueOf(queueInfo.getMaximumCapacity()));
            queueDescription.setQueueState(queueInfo.getQueueState().name());
            queueDescription.setQueuePath(queuePath);
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                List<ClusterResourceDescription.QueueDescription> childQueues = getQueueDescription(queueInfo.getQueueName(), queueInfo.getChildQueues());
                queueDescription.setChildQueues(childQueues);
            }
            descriptions.add(queueDescription);
        }
        return descriptions;
    }

    public YarnClient getYarnClient() {
        if(yarnClient == null){
            initYarnClient();
        }

        return yarnClient;
    }

    public void initYarnClient(){
        HadoopConf hadoopConf = new HadoopConf();
        hadoopConf.initYarnConf(allConfig);

        String principal = MapUtils.getString(allConfig, KerberosKey.PRINCIPAL.getKey());
        String keytab = MapUtils.getString(allConfig, KerberosKey.KEYTAB.getKey());
        String krb5Conf = MapUtils.getString(allConfig, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);

        loginKerberos(hadoopConf.getYarnConfiguration(), principal, keytab, krb5Conf);
        yarnClient = YarnClient.createYarnClient();
        yarnClient.init(hadoopConf.getYarnConfiguration());
        yarnClient.start();
    }

    public void closeYarnClient(){
        if (yarnClient != null){
            try {
                yarnClient.close();
                yarnClient = null;
            } catch (Exception e) {
                LOG.warn("An exception occurred while closing the yarnClient: ", e);
            }
        }
    }

    public void setYarnClient(YarnClient yarnClient) {
        this.yarnClient = yarnClient;
    }

    public List<NodeDescription> getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(List<NodeDescription> clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public ClusterResourceDescription getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(ClusterResourceDescription resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public class NodeDescription {
        private int memory;
        private int virtualCores;
        private int usedMemory;
        private int usedVirtualCores;

        public int getMemory() {
            return memory;
        }

        public void setMemory(int memory) {
            this.memory = memory;
        }

        public int getVirtualCores() {
            return virtualCores;
        }

        public void setVirtualCores(int virtualCores) {
            this.virtualCores = virtualCores;
        }

        public int getUsedMemory() {
            return usedMemory;
        }

        public void setUsedMemory(int usedMemory) {
            this.usedMemory = usedMemory;
        }

        public int getUsedVirtualCores() {
            return usedVirtualCores;
        }

        public void setUsedVirtualCores(int usedVirtualCores) {
            this.usedVirtualCores = usedVirtualCores;
        }
    }

    /*public static class ClusterResourceDescription {
        private final int totalNode;
        private final int totalMemory;
        private final int totalCores;
        private final List<QueueDescription> queueDescriptions;

        public ClusterResourceDescription(int totalNode, int totalMemory, int totalCores, List<QueueDescription> descriptions) {
            this.totalNode = totalNode;
            this.totalMemory = totalMemory;
            this.totalCores = totalCores;
            this.queueDescriptions = descriptions;
        }

        public int getTotalNode() {
            return totalNode;
        }

        public int getTotalMemory() {
            return totalMemory;
        }

        public int getTotalCores() {
            return totalCores;
        }

        public List<QueueDescription> getQueueDescriptions() {
            return queueDescriptions;
        }
    }

    public static class QueueDescription {
        private String queueName;
        private String queuePath;
        private String capacity;
        private String maximumCapacity;
        private String queueState;
        private List<QueueDescription> childQueues;

        public String getQueueName() {
            return queueName;
        }

        public void setQueueName(String queueName) {
            this.queueName = queueName;
        }

        public String getQueuePath() {
            return queuePath;
        }

        public void setQueuePath(String queuePath) {
            this.queuePath = queuePath;
        }

        public String getCapacity() {
            return capacity;
        }

        public void setCapacity(String capacity) {
            this.capacity = capacity;
        }

        public String getMaximumCapacity() {
            return maximumCapacity;
        }

        public void setMaximumCapacity(String maximumCapacity) {
            this.maximumCapacity = maximumCapacity;
        }

        public String getQueueState() {
            return queueState;
        }

        public void setQueueState(String queueState) {
            this.queueState = queueState;
        }

        public List<QueueDescription> getChildQueues() {
            return childQueues;
        }

        public void setChildQueues(List<QueueDescription> childQueues) {
            this.childQueues = childQueues;
        }
    }*/
}
