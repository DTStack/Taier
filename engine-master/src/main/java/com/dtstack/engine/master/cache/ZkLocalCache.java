package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.data.BrokerDataNode;
import com.dtstack.engine.master.data.BrokerDataShard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
@Component
public class ZkLocalCache {

    private volatile BrokerDataNode localDataCache = new BrokerDataNode(new ConcurrentHashMap<String,BrokerDataShard>(16));

    private volatile Map<String, Integer> zkDataSizeCache;

    private final ReentrantLock lock = new ReentrantLock();

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private WorkNode workNode;

    @Autowired
    private ZkShardManager zkShardManager;

    public void updateLocalMemTaskStatus(String zkTaskId, Integer status) {
        if (zkTaskId == null || status == null) {
            throw new UnsupportedOperationException();
        }
        //任务只有在提交成功后开始zk status轮询并同时checkShard一次
        if (RdosTaskStatus.SUBMITTED.getStatus().equals(status)){
            checkShard();
        }
        String shard = localDataCache.getShard(zkTaskId);
        Lock lock = zkShardManager.tryLock(shard);
        lock.lock();
        try {
            localDataCache.getShards().get(shard).put(zkTaskId, status.byteValue());
        } finally {
            lock.unlock();
        }
    }

    public BrokerDataNode getBrokerData() {
        return localDataCache;
    }


    public String getJobLocationAddr(String zkTaskId) {
        String addr = null;
        //先查本地
        String shard = localDataCache.getShard(zkTaskId);
        if (localDataCache.getShards().get(shard).containsKey(zkTaskId)) {
            addr = environmentContext.getLocalAddress();
        }
        //查数据库
        if (addr==null){
            String jobId = TaskIdUtil.getTaskId(zkTaskId);
            EngineJobCache jobCache = engineJobCacheDao.getOne(jobId);
            if (jobCache!=null){
                addr = jobCache.getNodeAddress();
            }
        }
        return addr;
    }

    public int getZkDataSize(String node){
        if (zkDataSizeCache!=null){
            return zkDataSizeCache.getOrDefault(node, 0);
        }
        return 0;
    }

    /**
     * 任务状态轮询的时候注意并发删除操作，CopyOnWrite
     */
    public Map<String, BrokerDataShard> cloneShardData() {
        return new HashMap<>(localDataCache.getShards());
    }

    public void checkShard() {
        final ReentrantLock createShardLock = this.lock;
        createShardLock.lock();
        try {
            int shardSize = localDataCache.getShards().size();
            int avg = localDataCache.getDataSize() / localDataCache.getShards().size();
            if (avg >= environmentContext.getShardSize()) {
                zkShardManager.createShardNode(shardSize);
            }
        } finally {
            createShardLock.unlock();
        }
    }

}
