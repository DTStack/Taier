package com.dtstack.rdos.engine.execution.queue;

import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

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

    /***需要改成根据时间排序的队列*/
    private Map<String, GroupExeQueue> groupExeQueueMap = Maps.newHashMap();

    private Set<GroupExeQueue> groupExeQueueSet;

    private Map<String, Integer> zkGroupMaxPriority = Maps.newHashMap();

    private static ExeQueueMgr exeQueueMgr = new ExeQueueMgr();

    private ExeQueueMgr(){

        groupExeQueueSet = Sets.newTreeSet( (gq1, gq2) -> MathUtil.getIntegerVal(gq1.getMaxTime() - gq2.getMaxTime()));
        GroupExeQueue defaultQueue = new GroupExeQueue(DEFAULT_GROUP_NAME);
        groupExeQueueMap.put(defaultQueue.getGroupName(), defaultQueue);
    }

    public static ExeQueueMgr getInstance(){
        return exeQueueMgr;
    }

    public void addJob(JobClient jobClient){
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
    }

    public void updateZkGroupPriorityInfo(){

    }

}
