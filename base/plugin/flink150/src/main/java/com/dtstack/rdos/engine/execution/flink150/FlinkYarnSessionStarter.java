package com.dtstack.rdos.engine.execution.flink150;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.flink150.util.FLinkConfUtil;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.util.FlinkException;
import org.apache.flink.yarn.AbstractYarnClusterDescriptor;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/5/30
 */
public class FlinkYarnSessionStarter {

    private static final Logger logger = LoggerFactory.getLogger(FlinkClient.class);

    private AbstractYarnClusterDescriptor yarnSessionDescriptor;
    private ClusterSpecification yarnSessionSpecification;
    private ClusterClient<ApplicationId> clusterClient;

    public FlinkYarnSessionStarter(FlinkClientBuilder flinkClientBuilder, FlinkConfig flinkConfig, FlinkPrometheusGatewayConfig metricConfig) throws MalformedURLException {
        JobClient jobClient = new JobClient();
        jobClient.setTaskId("default");
        yarnSessionDescriptor = flinkClientBuilder.createClusterDescriptorByMode(flinkConfig, metricConfig, jobClient, false);
        yarnSessionDescriptor.setName(flinkConfig.getFlinkSessionName());
        yarnSessionSpecification = FLinkConfUtil.createYarnSessionSpecification(flinkClientBuilder.getFlinkConfiguration());
    }

    public void startFlinkYarnSession() {
        try {
            clusterClient = yarnSessionDescriptor.deploySessionCluster(yarnSessionSpecification);
            clusterClient.setDetached(true);
        } catch (FlinkException e) {
            logger.info("Couldn't deploy Yarn session cluster", e.getMessage());
            throw new RuntimeException("Couldn't deploy Yarn session cluster" + e.getMessage());
        }
    }

    public void stopFlinkYarnSession() {
        try {
            clusterClient.shutdown();
        } catch (Exception ex) {
            logger.info("Could not properly shutdown cluster client.", ex);
        }
    }

    public ClusterClient<ApplicationId> getClusterClient() {
        return clusterClient;
    }
}
