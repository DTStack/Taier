package com.dtstack.rdos.engine.service.zk.cache;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/6
 */
public class ZkLocalCacheSyncListener implements Runnable {

    private static long listener = 5000;

    private static final Logger logger = LoggerFactory.getLogger(ZkLocalCacheSyncListener.class);

    private ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();
    private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();


    @Override
    public void run() {
        int index = 0;

        while (true) {
            try {
                Thread.sleep(listener);
                ++index;
                if (PublicUtil.count(index, 5)) {
                    logger.warn("ZkLocalCacheSyncListener start again");
                }
                syncLocalCache();
            } catch (Throwable e) {
                logger.error("AllTaskStatusListener error:{}", ExceptionUtil.getErrorMessage(e));
            }
        }
    }

    private void syncLocalCache() {
        String localAddress = zkDistributed.getLocalAddress();
        BrokerDataNode localDataNode = zkLocalCache.cloneData().get(localAddress);
        for (Map.Entry<String, BrokerDataShard> entry : localDataNode.getShards().entrySet()) {
            if (entry.getValue().getVersion() == entry.getValue().getNewVersion().longValue()) {
                continue;
            }
            zkDistributed.synchronizedBrokerDataShard(localAddress,entry.getKey(), entry.getValue(), true);
        }
    }

}
