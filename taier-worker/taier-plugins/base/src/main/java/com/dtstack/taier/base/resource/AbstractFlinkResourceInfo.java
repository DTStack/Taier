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

package com.dtstack.taier.base.resource;

import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/11/1
 */
public abstract class AbstractFlinkResourceInfo implements EngineResourceInfo {

    protected List<NodeResourceDetail> nodeResources = Lists.newArrayList();

    public List<NodeResourceDetail> getNodeResources() {
        return nodeResources;
    }

    public void addNodeResource(NodeResourceDetail nodeResourceDetail) {
        nodeResources.add(nodeResourceDetail);
    }

    protected JudgeResult judgeFlinkSessionResource(int sqlEnvParallel, int mrParallel) {
        if (sqlEnvParallel == 0 && mrParallel == 0) {
            return JudgeResult.limitError("Flink task resource configuration error，sqlEnvParallel：" + sqlEnvParallel + ", mrParallel：" + mrParallel);
        }
        int availableSlots = 0;
        int totalSlots = 0;
        for (NodeResourceDetail resourceDetail : nodeResources) {
            availableSlots += resourceDetail.freeSlots;
            totalSlots += resourceDetail.slotsNumber;
        }
        //没有资源直接返回false
        if (availableSlots == 0) {
            return JudgeResult.notOk( "Available resources available is 0");
        }
        int maxParallel = Math.max(sqlEnvParallel, mrParallel);
        if (totalSlots < maxParallel) {
            return JudgeResult.limitError("Flink task allocation resource exceeds the maximum resource of the cluster, totalSlots:" + totalSlots + ",maxParallel:" + maxParallel);
        }
        Boolean rs = availableSlots >= maxParallel;
        if (!rs) {
            return JudgeResult.notOk( "Available resources are greater than task request resources");
        }
        return JudgeResult.ok();
    }


    public static class NodeResourceDetail {
        private String nodeId;
        private int freeSlots;
        private int slotsNumber;

        public NodeResourceDetail(String nodeId, int freeSlots, int slotsNumber) {
            this.nodeId = nodeId;
            this.freeSlots = freeSlots;
            this.slotsNumber = slotsNumber;
        }
    }

}
