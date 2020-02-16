package com.dtstack.engine.master.cache;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.WorkNode;
import com.dtstack.engine.master.env.EnvironmentContext;
import com.dtstack.engine.master.zk.ZkService;
import com.dtstack.engine.master.data.BrokerDataNode;
import com.dtstack.engine.master.data.BrokerDataShard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
public class ZkLocalCache implements Closeable {

    private volatile BrokerDataNode localDataCache;

    private String localAddress;
    private int distributeZkWeight;
    private int distributeQueueWeight;
    private int distributeDeviation;
    private int perShardSize;
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

    private ZkLocalCache() {
    }

    public void init(ZkService zkService) {
        localAddress = zkService.getLocalAddress();
        localDataCache = new BrokerDataNode(new ConcurrentHashMap<String,BrokerDataShard>(16));
        distributeQueueWeight = 1;
        distributeZkWeight = 1;
        distributeDeviation = 1;
        perShardSize = environmentContext.getShardSize();
    }

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
            addr = localAddress;
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

    /**
     * 选择节点间（队列负载+已提交任务 加权值）+ 误差 符合要求的node，做任务分发
     */
    public String getDistributeNode(String engineType, String groupName, List<String> excludeNodes) {
        return localAddress;
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
            if (avg >= perShardSize) {
                zkShardManager.createShardNode(shardSize);
            }
        } finally {
            createShardLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
    }

    public void setWorkNode(WorkNode workNode) {
        this.workNode = workNode;
    }
}
