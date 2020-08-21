package com.dtstack.engine.flink.resource;

import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.flink.util.FlinkUtil;
import com.dtstack.engine.base.resource.AbstractYarnResourceInfo;
import com.dtstack.engine.flink.constrant.ConfigConstrant;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.List;
import java.util.Properties;

/**
 * 用于存储从flink上获取的资源信息
 * Date: 2017/11/24
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class FlinkPerJobResourceInfo extends AbstractYarnResourceInfo {

    public int jobmanagerMemoryMb = ConfigConstrant.MIN_JM_MEMORY;
    public int taskmanagerMemoryMb = ConfigConstrant.MIN_JM_MEMORY;
    public int numberTaskManagers = 1;
    public int slotsPerTaskManager = 1;

    private YarnClient yarnClient;
    private String queueName;
    private int yarnAccepterTaskNumber;

    private FlinkPerJobResourceInfo(YarnClient yarnClient, String queueName, int yarnAccepterTaskNumber) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return judgePerjobResource(jobClient);
    }

    private JudgeResult judgePerjobResource(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.getResult()) {
            return jr;
        }

        setTaskResourceInfo(jobClient);

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                //作为启动 am 和 jobmanager
                InstanceInfo.newRecord(1, 1, jobmanagerMemoryMb),
                InstanceInfo.newRecord(numberTaskManagers, slotsPerTaskManager, taskmanagerMemoryMb));

        return judgeYarnResource(instanceInfos);
    }

    private void setTaskResourceInfo(JobClient jobClient) {
        Properties properties = jobClient.getConfProperties();

        if (properties != null && properties.containsKey(ConfigConstrant.SLOTS)) {
            slotsPerTaskManager = MathUtil.getIntegerVal(properties.get(ConfigConstrant.SLOTS));
        }

        Integer sqlParallelism = FlinkUtil.getEnvParallelism(jobClient.getConfProperties());
        Integer jobParallelism = FlinkUtil.getJobParallelism(jobClient.getConfProperties());
        int parallelism = Math.max(sqlParallelism, jobParallelism);
        if (properties != null && properties.containsKey(ConfigConstrant.CONTAINER)) {
            numberTaskManagers = MathUtil.getIntegerVal(properties.get(ConfigConstrant.CONTAINER));
        }
        numberTaskManagers = Math.max(numberTaskManagers, parallelism);

        if (properties != null && properties.containsKey(ConfigConstrant.JOBMANAGER_MEMORY_MB)) {
            jobmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(ConfigConstrant.JOBMANAGER_MEMORY_MB));
        }
        if (jobmanagerMemoryMb < ConfigConstrant.MIN_JM_MEMORY) {
            jobmanagerMemoryMb = ConfigConstrant.MIN_JM_MEMORY;
        }

        if (properties != null && properties.containsKey(ConfigConstrant.TASKMANAGER_MEMORY_MB)) {
            taskmanagerMemoryMb = MathUtil.getIntegerVal(properties.get(ConfigConstrant.TASKMANAGER_MEMORY_MB));
        }
        if (taskmanagerMemoryMb < ConfigConstrant.MIN_TM_MEMORY) {
            taskmanagerMemoryMb = ConfigConstrant.MIN_TM_MEMORY;
        }
    }

    public static FlinkPerJobResourceInfoBuilder FlinkPerJobResourceInfoBuilder() {
        return new FlinkPerJobResourceInfoBuilder();
    }

    public static class FlinkPerJobResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;

        public FlinkPerJobResourceInfoBuilder withYarnClient(YarnClient yarnClient) {
            this.yarnClient = yarnClient;
            return this;
        }

        public FlinkPerJobResourceInfoBuilder withQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public FlinkPerJobResourceInfoBuilder withYarnAccepterTaskNumber(Integer yarnAccepterTaskNumber) {
            this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
            return this;
        }

        public FlinkPerJobResourceInfo build() {
            return new FlinkPerJobResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber);
        }
    }

}
