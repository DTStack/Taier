package com.dtstack.rdos.engine.execution.queue;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.components.OrderLinkedBlockingQueue;

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

    public GroupExeQueue(String groupName){
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void addJobClient(JobClient jobClient){
        orderList.add(jobClient);
        if(jobClient.getGenerateTime() > maxTime){
            maxTime = jobClient.getGenerateTime();
        }
    }

    public JobClient removeTop(){
        JobClient result = orderList.remove();
        //更新时间
        maxTime = 0;
        orderList.forEach(jobClient -> {
            if(jobClient.getGenerateTime() > maxTime){
                maxTime = jobClient.getGenerateTime();
            }
        });

        return result;
    }
}
