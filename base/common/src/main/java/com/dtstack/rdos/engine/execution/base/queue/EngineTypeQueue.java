package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.constrant.ConfigConstant;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 指定引擎类型对应的执行等待队列
 * Date: 2018/1/15
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class EngineTypeQueue {

    private static final Logger LOG = LoggerFactory.getLogger(EngineTypeQueue.class);

    private static int MAX_QUEUE_LENGTH = 1;

    private String engineType;

    private Map<String, GroupExeQueue> groupExeQueueMap = Maps.newConcurrentMap();

    /**
     * 按时间排序
     */

    private Map<String, Integer> groupMaxPriority = Maps.newHashMap();

    public EngineTypeQueue(String engineType) {
        this.engineType = engineType;
        GroupExeQueue defaultQueue = new GroupExeQueue(ConfigConstant.DEFAULT_GROUP_NAME);
        groupExeQueueMap.put(defaultQueue.getGroupName(), defaultQueue);
        MAX_QUEUE_LENGTH = ConfigParse.getExeQueueSize();

        LOG.info("instance {} with queue size:{}", engineType, MAX_QUEUE_LENGTH);
    }

    public void add(JobClient jobClient) {
        final String groupName = jobClient.getGroupName() == null ? ConfigConstant.DEFAULT_GROUP_NAME : jobClient.getGroupName();
        GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);
        exeQueue.addJobClient(jobClient);
        //重新更新下队列的排序
        groupMaxPriority.put(groupName, exeQueue.getMaxPriority());
    }

    public boolean remove(String groupName, String taskId) {

        groupName = groupName == null ? ConfigConstant.DEFAULT_GROUP_NAME : groupName;
        GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);
        if (exeQueue == null) {
            return false;
        }

        boolean result = exeQueue.remove(taskId);
        groupMaxPriority.put(groupName, exeQueue.getMaxPriority());
        return result;
    }

    public boolean checkLocalPriorityIsMax(String groupName, String localAddress,
                                           ClusterQueueInfo.EngineTypeQueueInfo zkInfo) {

        Integer localPriority = groupMaxPriority.get(groupName);
        localPriority = localPriority == null ? 0 : localPriority;

        boolean result = true;
        for (Map.Entry<String, ClusterQueueInfo.GroupQueueInfo> zkInfoEntry : zkInfo.getGroupQueueInfoMap().entrySet()) {
            String address = zkInfoEntry.getKey();
            ClusterQueueInfo.GroupQueueInfo groupQueueZkInfo = zkInfoEntry.getValue();

            if (localAddress.equals(address)) {
                continue;
            }

            Map<String, GroupInfo> remoteQueueInfo = groupQueueZkInfo.getGroupInfo();
            GroupInfo groupInfo = remoteQueueInfo.getOrDefault(groupName, new GroupInfo());
            if (groupInfo.getPriority() > localPriority) {
                result = false;
                break;
            }
        }
        //fixme  去掉提交队列
        if (!result){
            GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);
            exeQueue.incrementPriority();
            groupMaxPriority.put(groupName,exeQueue.getMaxPriority());
        }
        return result;
    }

    public boolean checkCanAddToWaitQueue(String groupName) {
        final String groupExeName = groupName == null ? ConfigConstant.DEFAULT_GROUP_NAME : groupName;
        GroupExeQueue exeQueue = groupExeQueueMap.putIfAbsent(groupExeName, new GroupExeQueue(groupExeName));
        if (exeQueue == null) {
            return true;
        }
        if (exeQueue.size() >= MAX_QUEUE_LENGTH) {
            return false;
        }
        return true;
    }

    public Map<String, GroupExeQueue> getGroupExeQueueMap() {
        return groupExeQueueMap;
    }

    public GroupExeQueue remove(String groupName) {
        GroupExeQueue groupExeQueue = groupExeQueueMap.remove(groupName);
        groupMaxPriority.remove(groupName);
        return groupExeQueue;
    }

    public Map<String, Integer> getGroupPriorityInfo() {
        return groupMaxPriority;
    }

    public String getEngineType() {
        return engineType;
    }

    @Override
    public String toString() {
        return "EngineTypeQueue{" +
                "engineType='" + engineType + '\'' +
                ", groupExeQueueMap=" + groupExeQueueMap +
                ", groupMaxPriority=" + groupMaxPriority +
                '}';
    }
}
