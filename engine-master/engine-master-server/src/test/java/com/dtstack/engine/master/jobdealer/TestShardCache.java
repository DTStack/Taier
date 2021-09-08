package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 7:39 下午 2020/11/12
 */
public class TestShardCache extends AbstractTest {


    @Autowired
    private ShardCache shardCache;



    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRemoveIfPresent(){

        //测试jobId不存在的情况
        boolean flag = shardCache.removeIfPresent("falfja");
        Assert.assertFalse(flag);
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        //测试JobId存在的情况
        boolean b = shardCache.removeIfPresent(engineJobCache.getJobId());
        Assert.assertFalse(b);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testRemoveIfPresent2(){

        try {
            //测试JobId为空的情况
            boolean b = shardCache.removeIfPresent(null);
        } catch (Exception e) {
            Assert.assertEquals("jobId must not null.",e.getMessage());
        }
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateLocalMemTaskStatus(){

        //jobId不存在的情况
        boolean flag1 = shardCache.updateLocalMemTaskStatus("afaf", 1);
        Assert.assertFalse(flag1);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateLocalMemTaskStatus3(){

        //抛出异常的情况
        try {
            shardCache.updateLocalMemTaskStatus(null, 1);
        } catch (Exception e) {
            Assert.assertEquals("jobId or status must not null.",e.getMessage());
        }

    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testUpdateLocalMemTaskStatus2(){
        //jobId存在的情况
        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        boolean flag2 = shardCache.updateLocalMemTaskStatus(engineJobCache.getJobId(), 2);
        Assert.assertFalse(flag2);

    }

}
