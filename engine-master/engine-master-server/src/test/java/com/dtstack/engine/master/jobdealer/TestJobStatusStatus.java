package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.jobdealer.cache.ShardCache;
import com.dtstack.engine.master.jobdealer.cache.ShardManager;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 5:04 下午 2020/11/14
 */
public class TestJobStatusStatus  extends AbstractTest {


    @Autowired
    private ShardCache shardCache;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    private Map<String, ShardManager> jobResourceShardManager = new ConcurrentHashMap<>();

    private JobStatusDealer jobStatusDealer = new JobStatusDealer();


    @Test
    public void testRun(){

        EngineJobCache engineJobCache = DataCollection.getData().getEngineJobCache();
        ShardManager shardManager = getShardManager(engineJobCache.getJobId());
        shardManager.putJob(engineJobCache.getJobId(), RdosTaskStatus.RUNNING.getStatus());
        jobStatusDealer.setShardManager(shardManager);
        new Thread(jobStatusDealer).start();
    }


    private ShardManager getShardManager(String jobId) {
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        return jobResourceShardManager.computeIfAbsent(engineJobCache.getJobResource(), jr -> {
            ShardManager shardManager = new ShardManager(engineJobCache.getJobResource());
            JobStatusDealer jobStatusDealer = new JobStatusDealer();
            jobStatusDealer.setJobResource(engineJobCache.getJobResource());
            jobStatusDealer.setShardManager(shardManager);
            jobStatusDealer.setShardCache(shardCache);
            jobStatusDealer.setApplicationContext(shardCache.applicationContext);
            jobStatusDealer.start();
            return shardManager;
        });
    }
}
