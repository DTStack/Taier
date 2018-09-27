package com.dtstack.rdos.engine.execution.base.queue;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.queue.OrderLinkedBlockingQueue;

/**
 * 包含组信息的任务执行队列
 * Date: 2018/1/11
 * Company: www.dtstack.com
 * @author xuchao
 */

public class GroupExeQueue {

    private OrderLinkedBlockingQueue<JobClient> orderList = new OrderLinkedBlockingQueue<>();

    private String groupName;

    private long maxTime = 0;

    private Integer maxPriority = 0;

    public GroupExeQueue(String groupName){
        this.groupName = groupName;
    }


    public void addJobClient(JobClient jobClient) {
        try {
            orderList.put(jobClient);
            if(jobClient.getGenerateTime() > maxTime){
                maxTime = jobClient.getGenerateTime();
            }
            //使用原始的任务优先级
            if(jobClient.getPriorityLevel() > maxPriority){
                maxPriority = jobClient.getPriorityLevel();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JobClient remove(){
        JobClient result = orderList.poll();
        updateExtInfo();
        return result;
    }

    /**
     * 只是返回第一个元素并不移除
     * @return
     */
    public JobClient getTop(){
        return orderList.getTop();
    }


    public boolean remove(String taskId){
        boolean result = orderList.remove(taskId);
        updateExtInfo();
        return result;
    }

    private void updateExtInfo(){
        //更新时间
        maxTime = 0;
        maxPriority = 0;

        orderList.forEach(jobClient -> {
            if(jobClient.getGenerateTime() > maxTime){
                maxTime = jobClient.getGenerateTime();
            }

            if(jobClient.getPriorityLevel() > maxPriority){
                maxPriority = jobClient.getPriorityLevel();
            }
        });
    }

    public int size(){
        return orderList.size();
    }

    public String getGroupName() {
        return groupName;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public Integer getMaxPriority() {
        return maxPriority;
    }
}
