/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.api.domain;

import io.swagger.annotations.ApiModel;

import java.util.List;

@ApiModel
public class ClusterResourceDescription {
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
    }
}
