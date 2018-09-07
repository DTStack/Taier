package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.engine.service.zk.ZkDistributed;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import com.dtstack.rdos.engine.service.zk.data.BrokerHeartNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by sishu.yss on 2017/3/14.
 */
public class DataMigrationListener implements Runnable{

    private MasterListener masterListener;

    private static long listener = 20000;

    private static ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    Logger logger = LoggerFactory.getLogger(DataMigrationListener.class);


    public DataMigrationListener(MasterListener masterListener){
        this.masterListener = masterListener;
    }

    @Override
    public void run() {
        try{
            while(true){
                Thread.sleep(listener);
                if(masterListener.isMaster()){
                    List<String> brokers =  zkDistributed.getBrokersChildren();
                    for(String node:brokers){
                        BrokerHeartNode brokerHeartNode = zkDistributed.getBrokerHeartNode(node);
                        if(brokerHeartNode!=null&&!brokerHeartNode.getAlive()) {
                            Map<String,BrokerDataShard> brokerDataNodeMap =  zkDistributed.getBrokerDataNode(node);
                            boolean data = false;
                            if (brokerDataNodeMap!=null){
                                for (Map.Entry<String,BrokerDataShard> entry:brokerDataNodeMap.entrySet()){
                                    if (entry.getValue().metaSize()>0){
                                        data = true;
                                        break;
                                    }
                                }
                            }
                            if (data) {
                                zkDistributed.dataMigration(node);
                            }
                        }
                    }
                }
                logger.warn("DataMigrationListener start again...");
            }
        }catch(Throwable e){
            logger.error("DataMigrationListener error:",e);
        }
    }
}
