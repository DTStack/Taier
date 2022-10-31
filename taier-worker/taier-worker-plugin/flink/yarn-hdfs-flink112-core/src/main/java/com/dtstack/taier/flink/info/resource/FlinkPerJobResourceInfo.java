/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.taier.flink.info.resource;

import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.flink.constant.ConfigConstant;
import com.dtstack.taier.flink.util.FlinkUtil;
import com.dtstack.taier.base.resource.AbstractYarnResourceInfo;
import com.google.common.collect.Lists;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.MemorySize;
import org.apache.flink.configuration.TaskManagerOptions;
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

    public int jobManagerMemoryMb = ConfigConstant.MIN_JM_MEMORY;
    public int taskManagerMemoryMb = ConfigConstant.MIN_JM_MEMORY;
    public int numberTaskManagers = 1;
    public int slotsPerTaskManager = 1;

    private final YarnClient yarnClient;
    private final String queueName;
    private final int yarnAccepterTaskNumber;
    private final Properties envProperties;

    private FlinkPerJobResourceInfo(YarnClient yarnClient, String queueName, int yarnAccepterTaskNumber, Properties envProperties) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
        this.envProperties = envProperties;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return judgePerJobResource(jobClient);
    }

    private JudgeResult judgePerJobResource(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.available()) {
            return jr;
        }

        setTaskResourceInfo(jobClient);

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                //作为启动 am 和 jobManager
                InstanceInfo.newRecord(1, 1, jobManagerMemoryMb),
                InstanceInfo.newRecord(numberTaskManagers, slotsPerTaskManager, taskManagerMemoryMb));

        return judgeYarnResource(instanceInfos);
    }

    /**
     * set jobManagerMemoryMb、jobManagerMemoryMb,slotsPerTaskManager,numberTaskManagers from job properties.
     * if job properties is not set, use environment properties.
     */
    private void setTaskResourceInfo(JobClient jobClient) {
        Properties properties = jobClient.getConfProperties();
        if(properties != null){
            int parallelism = Math.max(FlinkUtil.getEnvParallelism(properties), FlinkUtil.getJobParallelism(properties));
            numberTaskManagers = parallelism % slotsPerTaskManager == 0 ?
                    parallelism / slotsPerTaskManager :
                    parallelism / slotsPerTaskManager + 1;

            if (properties.containsKey(TaskManagerOptions.NUM_TASK_SLOTS.key())) {
                slotsPerTaskManager = MathUtil.getIntegerVal(properties.get(TaskManagerOptions.NUM_TASK_SLOTS.key()));
            }else{
                slotsPerTaskManager = MathUtil.getIntegerVal(envProperties.get(TaskManagerOptions.NUM_TASK_SLOTS.key()));
            }

            if (properties.containsKey(JobManagerOptions.TOTAL_PROCESS_MEMORY.key())) {
                jobManagerMemoryMb = MemorySize.parse(properties.getProperty(JobManagerOptions.TOTAL_PROCESS_MEMORY.key())).getMebiBytes();
            }else{
                jobManagerMemoryMb = MemorySize.parse(envProperties.getProperty(JobManagerOptions.TOTAL_PROCESS_MEMORY.key())).getMebiBytes();
            }
            jobManagerMemoryMb = Math.max(jobManagerMemoryMb, ConfigConstant.MIN_JM_MEMORY);

            if (properties.containsKey(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key())) {
                taskManagerMemoryMb = MemorySize.parse(properties.getProperty(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key())).getMebiBytes();
            }else{
                taskManagerMemoryMb = MemorySize.parse(envProperties.getProperty(TaskManagerOptions.TOTAL_PROCESS_MEMORY.key())).getMebiBytes();
            }
            taskManagerMemoryMb = Math.max(taskManagerMemoryMb, ConfigConstant.MIN_TM_MEMORY);
        }
    }

    public static FlinkPerJobResourceInfoBuilder FlinkPerJobResourceInfoBuilder() {
        return new FlinkPerJobResourceInfoBuilder();
    }

    public static class FlinkPerJobResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;
        private Properties envProperties;

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

        public FlinkPerJobResourceInfoBuilder withProperties(Properties envProperties) {
            this.envProperties = envProperties;
            return this;
        }

        public FlinkPerJobResourceInfo build() {
            return new FlinkPerJobResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber, envProperties);
        }
    }

}
