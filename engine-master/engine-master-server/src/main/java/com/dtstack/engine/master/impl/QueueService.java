package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.BaseEntity;
import com.dtstack.engine.api.domain.Queue;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.QueueDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final static long ROOT_QUEUE_ID = -1L;

    private static final long DEFAULT_KUBERNETES_PARENT_NODE = -2L;

    @Autowired
    private QueueDao queueDao;

    public void updateQueue(Long engineId, ComponentTestResult.ClusterResourceDescription description){
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
                Integer delete = queueDao.deleteByIds(existQueueMap.values().stream().map(BaseEntity::getId).collect(Collectors.toList()), engineId);
                if (delete != existQueueMap.size()) {
                    throw new RdosDefineException("操作失败");
                }
            }
        }
    }

    private void newAddQueue(Long engineId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
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

    private void updateAddQueue(Map<String, Queue> existQueueMap, Long engineId, Long parentQueueId, List<ComponentTestResult.QueueDescription> descriptions) {
        //不会有空队列的
        if (CollectionUtils.isNotEmpty(descriptions)) {
            for (ComponentTestResult.QueueDescription queueDescription : descriptions) {
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

    /**
     * 添加k8s的namespace
     * @param engineId
     * @param namespace
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNamespaces(Long engineId, String namespace) {
        if(StringUtils.isBlank(namespace)){
            throw new RdosDefineException("namespace不能为空");
        }

        //校验namespace的是否存在
        List<Queue> namespaces = queueDao.listByEngineId(engineId);
        if (CollectionUtils.isNotEmpty(namespaces)) {
            List<Long> namespaceIds = namespaces.stream().map(BaseEntity::getId).collect(Collectors.toList());
            Integer delete = queueDao.deleteByIds(namespaceIds, engineId);
            if (delete != namespaces.size()) {
                throw new RdosDefineException("操作失败");
            }
        }
        Queue queue = new Queue();
        queue.setQueueName(namespace);
        queue.setEngineId(engineId);
        queue.setMaxCapacity("0");
        queue.setCapacity("0");
        queue.setQueueState("ACTIVE");
        queue.setParentQueueId(DEFAULT_KUBERNETES_PARENT_NODE);
        queue.setQueuePath(namespace);
        Integer insert = queueDao.insert(queue);
        if (insert != 1) {
            throw new RdosDefineException("操作失败");
        }
    }
}
