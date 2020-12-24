package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 10:07 上午 2020/11/28
 */
public class TestShardManager extends AbstractTest {



    @Test
    public void testGetResource(){

        ShardManager shardManager = new ShardManager("aa");
        String jobResource = shardManager.getJobResource();
        Assert.assertEquals("aa",jobResource);
    }

}
