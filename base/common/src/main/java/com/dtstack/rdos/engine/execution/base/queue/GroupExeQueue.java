package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.engine.execution.base.JobClient;

/**
 * 包含组信息的任务执行队列
 * Date: 2018/1/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class GroupExeQueue {

    private OrderLinkedBlockingQueue<JobClient> exeQueue = new OrderLinkedBlockingQueue<>();

    private String groupName;

    private Integer maxPriority = 0;

    public GroupExeQueue(String groupName){
        this.groupName = groupName;
    }


    public void addJobClient(JobClient jobClient) {
        try {
            exeQueue.put(jobClient);
            if(jobClient.getPriority() > maxPriority){
                maxPriority = jobClient.getPriority();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JobClient remove(){
        JobClient result = exeQueue.poll();
        updateExtInfo();
        return result;
    }

    /**
     * 只是返回第一个元素并不移除
     * @return
     */
    public JobClient getTop(){
        return exeQueue.getTop();
    }


    public boolean remove(String taskId){
        boolean result = exeQueue.remove(taskId);
        updateExtInfo();
        return result;
    }

    private void updateExtInfo(){
        maxPriority = 0;
        exeQueue.forEach(jobClient -> {
            if(jobClient.getPriority() > maxPriority){
                maxPriority = jobClient.getPriority();
            }
        });
    }

    public void incrementPriority(){
        for(JobClient e: exeQueue){
            int currPriority = e.getPriority();
            e.setPriority(currPriority + 1);
        }
        updateExtInfo();
    }

    public int size(){
        return exeQueue.size();
    }

    public OrderLinkedBlockingQueue<JobClient> getExeQueue() {
        return exeQueue;
    }

    public String getGroupName() {
        return groupName;
    }

    public Integer getMaxPriority() {
        return maxPriority;
    }
}
