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

package com.dtstack.taier.sparkyarn.sparkyarn;

import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.MathUtil;
import com.dtstack.taier.pluginapi.util.UnitConvertUtil;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.base.resource.AbstractYarnResourceInfo;
import com.google.common.collect.Lists;
import org.apache.hadoop.yarn.client.api.YarnClient;

import java.util.List;
import java.util.Properties;

/**
 * spark yarn 资源相关
 * Date: 2017/11/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public class SparkYarnResourceInfo extends AbstractYarnResourceInfo {

    private final static String DRIVER_CORE_KEY = "driver.cores";

    private final static String DRIVER_MEM_KEY = "driver.memory";

    private final static String DRIVER_MEM_OVERHEAD_KEY = "yarn.driver.memoryOverhead";

    private final static String EXECUTOR_INSTANCES_KEY = "executor.instances";

    private final static String EXECUTOR_MEM_KEY = "executor.memory";

    private final static String EXECUTOR_CORES_KEY = "executor.cores";

    private final static String EXECUTOR_MEM_OVERHEAD_KEY = "yarn.executor.memoryOverhead";

    public final static int DEFAULT_CORES = 1;

    public final static int DEFAULT_INSTANCES = 1;

    public final static int DEFAULT_MEM = 512;

    public final static int DEFAULT_MEM_OVERHEAD = 384;

    public static final String SPARK_CLEAR_RESOURCED_RATE = "spark.clear.resource.rate";

    public static final String SPARK_RESOURCES_DIR = "spark.resources.dir";

    public static final String DEFAULT_SPARK_RESOURCES_DIR = "hdfs:///dtInsight/spark";

    private YarnClient yarnClient;
    private String queueName;
    private Integer yarnAccepterTaskNumber;

    public SparkYarnResourceInfo(YarnClient yarnClient, String queueName, Integer yarnAccepterTaskNumber) {
        this.yarnClient = yarnClient;
        this.queueName = queueName;
        this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {

        JudgeResult jr = getYarnSlots(yarnClient, queueName, yarnAccepterTaskNumber);
        if (!jr.available()) {
            return jr;
        }

        Properties properties = jobClient.getConfProperties();
        int driverCores = DEFAULT_CORES;
        if(properties != null && properties.containsKey(DRIVER_CORE_KEY)){
            driverCores = MathUtil.getIntegerVal(properties.get(DRIVER_CORE_KEY));
        }
        int driverMem = DEFAULT_MEM;
        if(properties != null && properties.containsKey(DRIVER_MEM_KEY)){
            String setMemStr = properties.getProperty(DRIVER_MEM_KEY);
            driverMem = UnitConvertUtil.convert2Mb(setMemStr);
        }
        int driverMemOverhead = DEFAULT_MEM_OVERHEAD;
        if(properties != null && properties.containsKey(DRIVER_MEM_OVERHEAD_KEY)){
            String setMemStr = properties.getProperty(DRIVER_MEM_OVERHEAD_KEY);
            driverMemOverhead = UnitConvertUtil.convert2Mb(setMemStr);
        }
        driverMem += driverMemOverhead;

        int executorNum = DEFAULT_INSTANCES;
        if(properties != null && properties.containsKey(EXECUTOR_INSTANCES_KEY)){
            executorNum = MathUtil.getIntegerVal(properties.get(EXECUTOR_INSTANCES_KEY));
        }
        int executorCores = DEFAULT_CORES;
        if(properties != null && properties.containsKey(EXECUTOR_CORES_KEY)){
            executorCores = MathUtil.getIntegerVal(properties.get(EXECUTOR_CORES_KEY));
        }

        int executorMem = DEFAULT_MEM;
        if(properties != null && properties.containsKey(EXECUTOR_MEM_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_KEY);
            executorMem = UnitConvertUtil.convert2Mb(setMemStr);
        }
        int executorMemOverhead = DEFAULT_MEM_OVERHEAD;
        if(properties != null && properties.containsKey(EXECUTOR_MEM_OVERHEAD_KEY)){
            String setMemStr = properties.getProperty(EXECUTOR_MEM_OVERHEAD_KEY);
            executorMemOverhead = UnitConvertUtil.convert2Mb(setMemStr);
        }
        executorMem += executorMemOverhead;

        List<InstanceInfo> instanceInfos = Lists.newArrayList(
                InstanceInfo.newRecord(1, driverCores, driverMem),
                InstanceInfo.newRecord(executorNum, executorCores, executorMem));
        return judgeYarnResource(instanceInfos);
    }


    public static SparkYarnResourceInfoBuilder SparkYarnResourceInfoBuilder() {
        return new SparkYarnResourceInfoBuilder();
    }

    public static class SparkYarnResourceInfoBuilder {
        private YarnClient yarnClient;
        private String queueName;
        private Integer yarnAccepterTaskNumber;

        public SparkYarnResourceInfoBuilder withYarnClient(YarnClient yarnClient) {
            this.yarnClient = yarnClient;
            return this;
        }

        public SparkYarnResourceInfoBuilder withQueueName(String queueName) {
            this.queueName = queueName;
            return this;
        }

        public SparkYarnResourceInfoBuilder withYarnAccepterTaskNumber(Integer yarnAccepterTaskNumber) {
            this.yarnAccepterTaskNumber = yarnAccepterTaskNumber;
            return this;
        }

        public SparkYarnResourceInfo build() {
            return new SparkYarnResourceInfo(yarnClient, queueName, yarnAccepterTaskNumber);
        }
    }

}
