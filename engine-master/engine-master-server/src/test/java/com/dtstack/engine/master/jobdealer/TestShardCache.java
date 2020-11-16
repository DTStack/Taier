package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 7:39 下午 2020/11/12
 */
public class TestShardCache extends AbstractTest {


    @Autowired
    private ShardCache shardCache;



    @Test
    public void testRemoveIfPresent(){

        //测试jobId不存在的情况
        boolean flag = shardCache.removeIfPresent("falfja");
        Assert.assertFalse(flag);
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        //测试JobId存在的情况
        boolean b = shardCache.removeIfPresent(engineJobCache.getJobId());
        Assert.assertTrue(b);
    }


    @Test
    public void testUpdateLocalMemTaskStatus(){

        //jobId不存在的情况
        boolean flag1 = shardCache.updateLocalMemTaskStatus("afaf", 1);
        Assert.assertFalse(flag1);

    }


    @Test
    public void testUpdateLocalMemTaskStatus2(){
        //jobId存在的情况
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        boolean flag2 = shardCache.updateLocalMemTaskStatus(engineJobCache.getJobId(), 2);
        Assert.assertTrue(flag2);

    }

}
