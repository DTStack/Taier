package com.dtstack.engine.master.queue;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.listener.QueueListener;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * @Author: newman
 * Date: 2020/12/28 10:06 上午
 * Description: 测试jobPartitioner
 * @since 1.0.0
 */
public class TestJobPartitioner extends AbstractTest {

    @Autowired
    private JobPartitioner jobPartitioner;

    @MockBean
    private QueueListener queueListener;


    private Map<Integer, Map<String, QueueInfo>> getAllNodes(){
        Map<Integer, Map<String, QueueInfo>> allNodesJobQueueTypes = new HashMap<>();
        Map<String, QueueInfo> map = new HashMap<>();
        QueueInfo queueInfo = new QueueInfo();
        queueInfo.setSize(3);
        map.put("127.0.0.1:8090",queueInfo);
        allNodesJobQueueTypes.put(1,map);
        return allNodesJobQueueTypes;
    }

    private Map<String, Map<String, GroupInfo>> getAllNodesGroupQueue(){
        Map<String, Map<String, GroupInfo>> allNodesJobQueue = new HashMap<>();
        Map<String, GroupInfo> map = new HashMap<>();
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setPriority(1);
        groupInfo.setSize(3);
        map.put("127.0.0.1:8090",groupInfo);
        allNodesJobQueue.put("aa",map);
        return allNodesJobQueue;
    }

    @Test
    public void testGetDefaultStrategy(){

        List<String> aliveNodes = new ArrayList<>();
        aliveNodes.add("127.0.0.1:8090");
        aliveNodes.add("172.16.100.38:8090");
        Map<String, Integer> strategy = jobPartitioner.getDefaultStrategy(aliveNodes, 20);
        Assert.assertNotNull(strategy);
    }

    @Test
    public void testComputeBatchJobSize(){
        when(queueListener.getAllNodesJobQueueInfo()).thenReturn(getAllNodes());
        jobPartitioner.computeBatchJobSize(1,10);
    }

    @Test
    public void testComputeJobCacheSize(){

        when(queueListener.getAllNodesGroupQueueInfo()).thenReturn(getAllNodesGroupQueue());
        jobPartitioner.computeJobCacheSize("aa",10);
    }

    @Test
    public void testGetGroupInfoByJobResource(){

        when(queueListener.getAllNodesGroupQueueInfo()).thenReturn(getAllNodesGroupQueue());
        jobPartitioner.getGroupInfoByJobResource("aa");
    }
}
