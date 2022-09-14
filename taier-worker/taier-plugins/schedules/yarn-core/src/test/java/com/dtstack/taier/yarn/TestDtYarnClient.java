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

package com.dtstack.taier.yarn;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class TestDtYarnClient {

    private static final Logger LOG = LoggerFactory.getLogger(DtYarnClient.class);

    public static void main(String[] args) throws Exception {

        DtYarnClient client = new DtYarnClient();
        JSONObject jsonObject = JSONObject.parseObject("{\"scheduler\":{\"schedulerInfo\":{\"type\":\"fairScheduler\",\"rootQueue\":{\"maxApps\":2147483647,\"minResources\":{\"memory\":0,\"vCores\":0},\"maxResources\":{\"memory\":12288,\"vCores\":8},\"usedResources\":{\"memory\":0,\"vCores\":0},\"amUsedResources\":{\"memory\":0,\"vCores\":0},\"amMaxResources\":{\"memory\":0,\"vCores\":0},\"demandResources\":{\"memory\":0,\"vCores\":0},\"steadyFairResources\":{\"memory\":12288,\"vCores\":8},\"fairResources\":{\"memory\":12288,\"vCores\":8},\"clusterResources\":{\"memory\":12288,\"vCores\":8},\"reservedResources\":{\"memory\":0,\"vCores\":0},\"pendingContainers\":0,\"allocatedContainers\":0,\"reservedContainers\":0,\"queueName\":\"root\",\"schedulingPolicy\":\"DRF\",\"childQueues\":{\"queue\":[{\"type\":\"fairSchedulerLeafQueueInfo\",\"maxApps\":2147483647,\"minResources\":{\"memory\":0,\"vCores\":0},\"maxResources\":{\"memory\":12288,\"vCores\":8},\"usedResources\":{\"memory\":0,\"vCores\":0},\"amUsedResources\":{\"memory\":0,\"vCores\":0},\"amMaxResources\":{\"memory\":0,\"vCores\":0},\"demandResources\":{\"memory\":0,\"vCores\":0},\"steadyFairResources\":{\"memory\":6144,\"vCores\":4},\"fairResources\":{\"memory\":0,\"vCores\":0},\"clusterResources\":{\"memory\":12288,\"vCores\":8},\"reservedResources\":{\"memory\":0,\"vCores\":0},\"pendingContainers\":0,\"allocatedContainers\":0,\"reservedContainers\":0,\"queueName\":\"root.default\",\"schedulingPolicy\":\"DRF\",\"preemptable\":true,\"numPendingApps\":0,\"numActiveApps\":0},{\"maxApps\":2147483647,\"minResources\":{\"memory\":0,\"vCores\":0},\"maxResources\":{\"memory\":12288,\"vCores\":8},\"usedResources\":{\"memory\":0,\"vCores\":0},\"amUsedResources\":{\"memory\":0,\"vCores\":0},\"amMaxResources\":{\"memory\":0,\"vCores\":0},\"demandResources\":{\"memory\":0,\"vCores\":0},\"steadyFairResources\":{\"memory\":6144,\"vCores\":4},\"fairResources\":{\"memory\":0,\"vCores\":0},\"clusterResources\":{\"memory\":12288,\"vCores\":8},\"reservedResources\":{\"memory\":0,\"vCores\":0},\"pendingContainers\":0,\"allocatedContainers\":0,\"reservedContainers\":0,\"queueName\":\"root.users\",\"schedulingPolicy\":\"DRF\",\"childQueues\":{\"queue\":[{\"type\":\"fairSchedulerLeafQueueInfo\",\"maxApps\":2147483647,\"minResources\":{\"memory\":0,\"vCores\":0},\"maxResources\":{\"memory\":12288,\"vCores\":8},\"usedResources\":{\"memory\":0,\"vCores\":0},\"amUsedResources\":{\"memory\":0,\"vCores\":0},\"amMaxResources\":{\"memory\":6144,\"vCores\":4},\"demandResources\":{\"memory\":0,\"vCores\":0},\"steadyFairResources\":{\"memory\":6144,\"vCores\":4},\"fairResources\":{\"memory\":0,\"vCores\":0},\"clusterResources\":{\"memory\":12288,\"vCores\":8},\"reservedResources\":{\"memory\":0,\"vCores\":0},\"pendingContainers\":0,\"allocatedContainers\":0,\"reservedContainers\":0,\"queueName\":\"root.users.hadoop\",\"schedulingPolicy\":\"fair\",\"preemptable\":true,\"numPendingApps\":0,\"numActiveApps\":0}]},\"preemptable\":true}]},\"preemptable\":true}}}}");
        List<JSONObject> queueResource = client.getQueueResource(jsonObject);
        System.out.println(queueResource);

    }
}
