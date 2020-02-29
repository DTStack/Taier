package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.hash.ShardData;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.resource.ComputeResourceType;
import com.dtstack.engine.master.taskdealer.TaskStatusDealer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
@Component
public class ShardCache implements ApplicationContextAware {

    private final ReentrantLock lock = new ReentrantLock();

    private static final ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(ComputeResourceType.values().length, new CustomThreadFactory(Class.class.getSimpleName()));

    private ApplicationContext applicationContext;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    private Map<String, ShardManager> jobResourceShardManager = new ConcurrentHashMap<>();

    private ShardManager getShardManager(String jobId) {
        EngineJobCache engineJobCache = engineJobCacheDao.getOne(jobId);
        if (engineJobCache == null) {
            return null;
        }
        return jobResourceShardManager.computeIfAbsent(engineJobCache.getJobResource(), jr -> {
            ShardManager shardManager = new ShardManager();
            TaskStatusDealer taskStatusDealer = new TaskStatusDealer();
            taskStatusDealer.setApplicationContext(applicationContext);
            taskStatusDealer.setShardManager(shardManager);
            taskStatusDealer.setShardCache(this);
            taskStatusDealer.setJobResource(engineJobCache.getJobResource());
            scheduledService.scheduleWithFixedDelay(
                    taskStatusDealer,
                    0,
                    TaskStatusDealer.INTERVAL,
                    TimeUnit.MILLISECONDS);
            return shardManager;
        });
    }

    public void updateLocalMemTaskStatus(String jobId, Integer status) {
        if (jobId == null || status == null) {
            throw new IllegalArgumentException("jobId or status not null.");
        }
        //任务只有在提交成功后开始task status轮询并同时checkShard一次
        if (RdosTaskStatus.SUBMITTED.getStatus().equals(status)) {
            checkShard(jobId);
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            String shardName = shardManager.getShardName(jobId);
            if (shardName == null) {
                return;
            }
            Lock lock = shardManager.tryLock(shardName);
            if (lock != null) {
                lock.lock();
                try {
                    ShardData shardData = shardManager.getShardData(jobId);
                    if (shardData != null) {
                        shardData.put(jobId, status);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void removeIfPresent(String jobId) {
        if (jobId == null) {
            throw new IllegalArgumentException("jobId not null.");
        }
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            String shardName = shardManager.getShardName(jobId);
            if (shardName == null) {
                return;
            }
            Lock lock = shardManager.tryLock(shardName);
            if (lock != null) {
                lock.lock();
                try {
                    ShardData shardData = shardManager.getShardData(jobId);
                    if (shardData != null) {
                        shardData.remove(jobId);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public String getJobNodeAddress(String jobId) {
        String nodeAddress = null;
        //先查本地
        ShardManager shardManager = getShardManager(jobId);
        if (shardManager != null) {
            ShardData shardData = shardManager.getShardData(jobId);
            if (shardData != null && shardData.containsKey(jobId)) {
                nodeAddress = environmentContext.getLocalAddress();
            }
            //查数据库
            if (nodeAddress == null) {
                EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
                if (jobCache != null) {
                    nodeAddress = jobCache.getNodeAddress();
                }
            }
        }
        return nodeAddress;
    }

    private void checkShard(String jobId) {
        final ReentrantLock createShardLock = this.lock;
        createShardLock.lock();
        try {
            ShardManager shardManager = getShardManager(jobId);
            if (shardManager != null) {
                int shardSize = shardManager.getShards().size();
                int avg = shardManager.getShardDataSize() / shardSize;
                if (avg >= environmentContext.getShardSize()) {
                    shardManager.createShardNode(shardSize);
                }
            }
        } finally {
            createShardLock.unlock();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
