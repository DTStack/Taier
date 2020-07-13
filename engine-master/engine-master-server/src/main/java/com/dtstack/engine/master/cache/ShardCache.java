package com.dtstack.engine.master.cache;

import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.master.jobdealer.JobStatusDealer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
@Component
public class ShardCache implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(ShardCache.class);

    private ApplicationContext applicationContext;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    private Map<String, ShardManager> jobResourceShardManager = new ConcurrentHashMap<>();

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
            jobStatusDealer.setShardCache(this);
            jobStatusDealer.setApplicationContext(applicationContext);
            jobStatusDealer.start();
            return shardManager;
        });
    }

    public boolean updateLocalMemTaskStatus(String jobId, Integer status) {
        if (jobId == null || status == null) {
            throw new IllegalArgumentException("jobId or status must not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            shardManager.putJob(jobId, status);
            return true;
        }
        return removeWithForeach(jobId);
    }

    public void removeIfPresent(String jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("jobId must not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            shardManager.removeJob(jobId);
        }
    }

    public boolean removeWithForeach(String jobId) {
        logger.warn("jobId:{} stackTrace:{}", jobId, ExceptionUtil.stackTrack());
        for (ShardManager shardManager : jobResourceShardManager.values()) {
            if (shardManager.getShard().remove(jobId) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
