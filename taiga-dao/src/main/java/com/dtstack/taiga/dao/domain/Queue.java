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

package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableName;


@TableName("console_queue")
public class Queue extends BaseEntity {

    private String queueName;

    private String capacity;
    private String maxCapacity;

    private String queueState;

    private Long parentQueueId;
    private String queuePath;

    private Long clusterId;

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(String maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public String getQueueState() {
        return queueState;
    }

    public void setQueueState(String queueState) {
        this.queueState = queueState;
    }

    public Long getParentQueueId() {
        return parentQueueId;
    }

    public void setParentQueueId(Long parentQueueId) {
        this.parentQueueId = parentQueueId;
    }

    public String getQueuePath() {
        return queuePath;
    }

    public void setQueuePath(String queuePath) {
        this.queuePath = queuePath;
    }

    public Long getClusterId() {
        return clusterId;
    }

    public void setClusterId(Long clusterId) {
        this.clusterId = clusterId;
    }

    public boolean baseEquals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Queue queue = (Queue) o;

        if (queueName != null ? !queueName.equals(queue.queueName) : queue.queueName != null) {
            return false;
        }
        if (capacity != null ? !capacity.equals(queue.capacity) : queue.capacity != null) {
            return false;
        }
        if (maxCapacity != null ? !maxCapacity.equals(queue.maxCapacity) : queue.maxCapacity != null) {
            return false;
        }
        if (queueState != null ? !queueState.equals(queue.queueState) : queue.queueState != null) {
            return false;
        }
        return queuePath != null ? queuePath.equals(queue.queuePath) : queue.queuePath == null;
    }
}
