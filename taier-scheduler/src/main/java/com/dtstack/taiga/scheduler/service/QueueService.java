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

package com.dtstack.taiga.scheduler.service;

import com.dtstack.taiga.common.exception.RdosDefineException;
import com.dtstack.taiga.dao.domain.BaseEntity;
import com.dtstack.taiga.dao.domain.Queue;
import com.dtstack.taiga.dao.mapper.QueueMapper;
import com.dtstack.taiga.pluginapi.pojo.ComponentTestResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final static long ROOT_QUEUE_ID = -1L;

    @Autowired
    private QueueMapper queueMapper;

    public void updateQueue(Long clusterId, ComponentTestResult.ClusterResourceDescription description){
        List<Queue> queues = queueMapper.listByClusterId(clusterId);
        if(CollectionUtils.isEmpty(queues)){
            newAddQueue(clusterId, ROOT_QUEUE_ID, description.getQueueDescriptions());
        } else {
            Map<String, Queue> existQueueMap = new HashMap<>(queues.size());
            for (Queue queue : queues) {
                existQueueMap.put(queue.getQueuePath(), queue);
            }

            updateAddQueue(existQueueMap, clusterId, ROOT_QUEUE_ID, description.getQueueDescriptions());
            if (!existQueueMap.isEmpty()) {
                Integer delete = queueMapper.deleteByIds(existQueueMap.values().stream().map(BaseEntity::getId).collect(Collectors.toList()), clusterId);
                if (delete != existQueueMap.size()) {
                    throw new RdosDefineException("operation failed");
                }
            }
        }
    }

    private void newAddQueue(Long clusterId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setClusterId(clusterId);
                queue.setMaxCapacity(queueDescription.getMaximumCapacity());
                queue.setCapacity(queueDescription.getCapacity());
                queue.setQueueState(queueDescription.getQueueState());
                queue.setParentQueueId(parentQueueId);
                queue.setQueuePath(queueDescription.getQueuePath());
                Integer insert = queueMapper.insert(queue);
                if (insert != 1) {
                    throw new RdosDefineException("operation failed");
                }
                newAddQueue(clusterId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }

    private void updateAddQueue(Map<String, Queue> existQueueMap, Long clusterId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        //不会有空队列的
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setClusterId(clusterId);
                queue.setMaxCapacity(queueDescription.getMaximumCapacity());
                queue.setCapacity(queueDescription.getCapacity());
                queue.setQueueState(queueDescription.getQueueState());
                queue.setQueuePath(queueDescription.getQueuePath());

                Queue oldQueue = existQueueMap.get(queueDescription.getQueuePath());
                if (oldQueue != null) {
                    if (oldQueue.baseEquals(queue)) {
                        existQueueMap.remove(queueDescription.getQueuePath());
                    } else if (queue.getQueueName().equals(oldQueue.getQueueName())) {
                        oldQueue.setQueueState(queue.getQueueState());
                        oldQueue.setCapacity(queue.getCapacity());
                        oldQueue.setMaxCapacity(queue.getMaxCapacity());
                        queueMapper.updateById(oldQueue);
                        existQueueMap.remove(queueDescription.getQueuePath());
                    }
                    queue.setId(oldQueue.getId());
                } else {
                    queue.setParentQueueId(parentQueueId);
                    Integer insert = queueMapper.insert(queue);
                    if (insert != 1) {
                        throw new RdosDefineException("operation failed");
                    }
                }
                // todo 递归调用，当没有子队列后就停止递归
                updateAddQueue(existQueueMap, clusterId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }

}
