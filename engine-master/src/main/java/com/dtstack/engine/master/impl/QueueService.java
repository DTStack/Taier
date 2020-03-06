package com.dtstack.engine.master.impl;

import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.QueueDao;
import com.dtstack.engine.domain.ClusterResourceDescription;
import com.dtstack.engine.domain.Queue;
import com.dtstack.engine.master.component.YARNComponent;
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
    private final static String DEFAULT_QUEUE_NAME = "default";

    @Autowired
    private QueueDao queueDao;

    @Forbidden
    public void addDefaultQueue(Long engineId){
        Queue queue = new Queue();
        queue.setEngineId(engineId);
        queue.setQueueName(DEFAULT_QUEUE_NAME);
        queue.setCapacity("1.0");
        queue.setMaxCapacity("1.0");
        queue.setQueueState("RUNNING");
        queue.setParentQueueId(-1L);
        queue.setQueuePath(DEFAULT_QUEUE_NAME);

        queueDao.insert(queue);
    }

    @Forbidden
    public void updateQueue(Long engineId, ClusterResourceDescription description){
        List<Queue> queues = queueDao.listByEngineId(engineId);
        if(CollectionUtils.isEmpty(queues)){
            newAddQueue(engineId, ROOT_QUEUE_ID, description.getQueueDescriptions());
        } else {
            Map<String, Queue> existQueueMap = new HashMap<>(queues.size());
            for (Queue queue : queues) {
                existQueueMap.put(queue.getQueuePath(), queue);
            }

            updateAddQueue(existQueueMap, engineId, ROOT_QUEUE_ID, description.getQueueDescriptions());
            if (!existQueueMap.isEmpty()) {
                Integer delete = queueDao.deleteByIds(existQueueMap.values().stream().map(q -> q.getId()).collect(Collectors.toList()), engineId);
                if (delete != existQueueMap.size()) {
                    throw new RdosDefineException("操作失败");
                }
            }
        }
    }

    private void newAddQueue(Long engineId, Long parentQueueId, List<ClusterResourceDescription.QueueDescription> descriptions) {
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ClusterResourceDescription.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setEngineId(engineId);
                queue.setMaxCapacity(queueDescription.getMaximumCapacity());
                queue.setCapacity(queueDescription.getCapacity());
                queue.setQueueState(queueDescription.getQueueState());
                queue.setParentQueueId(parentQueueId);
                queue.setQueuePath(queueDescription.getQueuePath());
                Integer insert = queueDao.insert(queue);
                if (insert != 1) {
                    throw new RdosDefineException("操作失败");
                }
                newAddQueue(engineId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }

    private void updateAddQueue(Map<String, Queue> existQueueMap, Long engineId, Long parentQueueId, List<ClusterResourceDescription.QueueDescription> descriptions) {
        //不会有空队列的
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ClusterResourceDescription.QueueDescription queueDescription : descriptions) {
                Queue queue = new Queue();
                queue.setQueueName(queueDescription.getQueueName());
                queue.setEngineId(engineId);
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
                        queueDao.update(oldQueue);
                        existQueueMap.remove(queueDescription.getQueuePath());
                    }
                    queue.setId(oldQueue.getId());
                } else {
                    queue.setParentQueueId(parentQueueId);
                    Integer insert = queueDao.insert(queue);
                    if (insert != 1) {
                        throw new RdosDefineException("操作失败");
                    }
                }

                updateAddQueue(existQueueMap, engineId, queue.getId(), queueDescription.getChildQueues());
            }
        }
    }
}
