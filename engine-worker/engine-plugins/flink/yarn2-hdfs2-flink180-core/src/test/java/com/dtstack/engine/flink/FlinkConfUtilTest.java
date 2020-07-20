package com.dtstack.engine.flink;

import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.dtstack.engine.flink.util.FlinkConfUtil;
import org.apache.flink.client.deployment.ClusterSpecification;
import org.apache.flink.configuration.ConfigConstants;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * Date: 2020/7/10
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class FlinkConfUtilTest {

    @Test
    public void testCreateClusterSpecification() {
        Properties props = new Properties();
        props.setProperty(ConfigConstrant.JOBMANAGER_MEMORY_MB, "2048");
        props.setProperty(ConfigConstrant.TASKMANAGER_MEMORY_MB, "2048");
        props.setProperty(ConfigConstrant.SLOTS, "1");

        ClusterSpecification clusterSpecification =
                FlinkConfUtil.createClusterSpecification(null, 0, props);
        Assert.assertNotNull(clusterSpecification);

        Configuration configuration = new Configuration();
        configuration.setInteger(JobManagerOptions.JOB_MANAGER_HEAP_MEMORY_MB, 1024);
        configuration.setInteger(TaskManagerOptions.TASK_MANAGER_HEAP_MEMORY_MB, 1024);
        configuration.setInteger(ConfigConstants.TASK_MANAGER_NUM_TASK_SLOTS, 1);
        configuration.setInteger(ConfigConstants.DEFAULT_PARALLELISM_KEY, 1);
        ClusterSpecification defaultClusterSpecification =
                FlinkConfUtil.createClusterSpecification(configuration, 0, null);
        Assert.assertNotNull(defaultClusterSpecification);
    }
}
