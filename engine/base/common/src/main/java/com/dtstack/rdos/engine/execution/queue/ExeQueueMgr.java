package com.dtstack.rdos.engine.execution.queue;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 管理任务执行队列
 * 当groupExeQueue为空之后回收
 * Date: 2018/1/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class ExeQueueMgr {

    private static final Logger LOG = LoggerFactory.getLogger(ExeQueueMgr.class);

    private static final String DEFAULT_GROUP_NAME = "default";

    /**TODO 修改成外部可配置*/
    private static final int MAX_QUEUE_LENGTH = 5;

    private Map<String, GroupExeQueue> groupExeQueueMap = Maps.newConcurrentMap();

    /**按时间排序*/
    /**TODO 修改为线程安全的SkipList*/
    private Set<GroupExeQueue> groupExeQueueSet;

    private Map<String, Integer> groupMaxPriority = Maps.newHashMap();

    /**所有集群的队列信息*/
    private Map<String, Map<String, Integer>> clusterQueueInfo = Maps.newHashMap();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static ExeQueueMgr exeQueueMgr = new ExeQueueMgr();

    private ExeQueueMgr(){

        groupExeQueueSet = Sets.newTreeSet( (gq1, gq2) -> MathUtil.getIntegerVal(gq1.getMaxTime() - gq2.getMaxTime()));
        GroupExeQueue defaultQueue = new GroupExeQueue(DEFAULT_GROUP_NAME);
        groupExeQueueMap.put(defaultQueue.getGroupName(), defaultQueue);

        executorService.submit(new TimerClear());
    }

    public static ExeQueueMgr getInstance(){
        return exeQueueMgr;
    }

    public void add(JobClient jobClient){
        String groupName = jobClient.getGroupName();
        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);
        if(exeQueue == null){
            exeQueue = new GroupExeQueue(groupName);
            groupExeQueueMap.put(groupName, exeQueue);
        }

        exeQueue.addJobClient(jobClient);
        //重新更新下队列的排序
        groupExeQueueSet.add(exeQueue);
        groupMaxPriority.put(groupName, exeQueue.getMaxPriority());
    }

    public boolean remove(String groupName, String taskId){

        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);
        if(exeQueue == null){
            return false;
        }

        boolean result = exeQueue.remove(taskId);
        groupMaxPriority.put(groupName, exeQueue.getMaxPriority());
        return result;
    }

    public Set<GroupExeQueue> getGroupExeQueue(){
        return groupExeQueueSet;
    }

    /**
     * zk监听模块调用该接口--更新本地的集群队列信息缓存
     * @param clusterQueueInfo
     */
    public void updateZkGroupPriorityInfo(Map<String, Map<String, Integer>> clusterQueueInfo){
        this.clusterQueueInfo = clusterQueueInfo;
    }

    /**
     * 获取当前节点的队列信息
     */
    public Map<String, Integer> getZkGroupPriorityInfo(){
        return groupMaxPriority;
    }

    public boolean checkLocalPriorityIsMax(String groupName, String localAddress){

        Integer localPriority = groupMaxPriority.get(groupName);
        if(localPriority == null){
            //不可能发生的
            LOG.error("it is not impossible. groupMaxPriority don't have info of :{}", groupName);
            return true;
        }

        boolean result = true;

        for(Map.Entry<String, Map<String, Integer>> entry : clusterQueueInfo.entrySet()){

            String address = entry.getKey();
            Map<String, Integer> remoteQueueInfo = entry.getValue();

            if(localAddress.equals(address)){
                continue;
            }

            Integer priority = remoteQueueInfo.getOrDefault(groupName, 0);
            if(priority > localPriority){
                result = false;
                break;
            }
        }

        return result;
    }


    public boolean checkCanAddToWaitQueue(String engineType, String groupName){
        if(engineType == null){
            return false;
        }

        //TODO 先根据engineType获取map, 再根据groupName获取具体的队列

        groupName = groupName == null ? DEFAULT_GROUP_NAME : groupName;
        GroupExeQueue exeQueue = groupExeQueueMap.get(groupName);

        if(exeQueue == null){
            return true;
        }

        if(exeQueue.size() >= MAX_QUEUE_LENGTH){
            return false;
        }

        return true;
    }


    class TimerClear implements Runnable{

        /**连续3次检查队列为空则回收*/
        private static final int FAILURE_RATE = 3;

        /**5s 检查一次队列*/
        private static final long CHECK_INTERVAL = 5 * 1000;

        private Map<String, Integer> cache = Maps.newHashMap();

        @Override
        public void run() {

            LOG.info("timer clear start up...");

            while (true){

                try {
                    groupExeQueueMap.forEach((name, queue) ->{
                        int currVal = 0;
                        if(queue == null || queue.size() == 0){
                            currVal = cache.getOrDefault(name, 0);
                            currVal++;
                        }

                        cache.put(name, currVal);
                    });

                    //清理空的队列
                    Iterator<Map.Entry<String, Integer>> iterator = cache.entrySet().iterator();
                    for( ;iterator.hasNext(); ){
                        Map.Entry<String, Integer> entry = iterator.next();

                        if(entry.getValue() >= FAILURE_RATE){
                            String groupName = entry.getKey();
                            GroupExeQueue queue = groupExeQueueMap.remove(groupName);
                            groupExeQueueSet.remove(queue);
                            groupMaxPriority.remove(groupName);
                            iterator.remove();
                        }
                    }
                }catch (Throwable e){
                    LOG.error("", e);
                }finally {
                    try {
                        Thread.sleep(CHECK_INTERVAL);
                    } catch (InterruptedException e) {
                        LOG.error("", e);
                    }
                }
            }
        }
    }

}
