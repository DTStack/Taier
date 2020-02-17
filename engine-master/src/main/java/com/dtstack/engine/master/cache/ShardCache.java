package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.common.hash.ShardData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
@Component
public class ShardCache {

    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private WorkNode workNode;

    @Autowired
    private ShardManager shardManager;

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        //任务只有在提交成功后开始task status轮询并同时checkShard一次
        if (RdosTaskStatus.SUBMITTED.getStatus().equals(status)) {
            checkShard();
        }
        String shard = shardManager.getShard().getShard(zkTaskId);
        Lock lock = shardManager.tryLock(shard);
        lock.lock();
        try {
            shardManager.getShard().getShards().get(shard).put(zkTaskId, status);
        } finally {
            lock.unlock();
        }
    }

    public String getJobLocationAddr(String jobId) {
        String addr = null;
        //先查本地
        String shard = shardManager.getShard().getShard(jobId);
        if (shardManager.getShard().getShards().get(shard).containsKey(jobId)) {
            addr = environmentContext.getLocalAddress();
        }
        //查数据库
        if (addr == null) {
            EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
            if (jobCache != null) {
                addr = jobCache.getNodeAddress();
            }
        }
        return addr;
    }


    /**
     * 任务状态轮询的时候注意并发删除操作，CopyOnWrite
     */
    public Map<String, ShardData> cloneShardData() {
        return new HashMap<>(shardManager.getShard().getShards());
    }

    public void checkShard() {
        final ReentrantLock createShardLock = this.lock;
        createShardLock.lock();
        try {
            int shardSize = shardManager.getShard().getShards().size();
            int avg = shardManager.getShard().getDataSize() / shardManager.getShard().getShards().size();
            if (avg >= environmentContext.getShardSize()) {
                shardManager.createShardNode(shardSize);
            }
        } finally {
            createShardLock.unlock();
        }
    }

}
