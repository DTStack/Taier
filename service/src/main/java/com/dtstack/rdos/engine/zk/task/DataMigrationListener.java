package com.dtstack.rdos.engine.zk.task;

import com.dtstack.rdos.engine.zk.ZkDistributed;
import com.dtstack.rdos.engine.zk.data.BrokerDataNode;
import com.dtstack.rdos.engine.zk.data.BrokerHeartNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Created by sishu.yss on 2017/3/14.
 */
public class DataMigrationListener implements Runnable{

    private MasterListener masterListener;

    private static long listener = 20000;

    private static ZkDistributed zkDistributed = ZkDistributed.getZkDistributed();

    Logger logger = LoggerFactory.getLogger(TaskMemStatusListener.class);


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
                        BrokerDataNode brokerDataNode =  zkDistributed.getBrokerDataNode(node);
                        BrokerHeartNode brokerHeartNode = zkDistributed.getBrokerHeartNode(node);
                        if(brokerHeartNode!=null&&!brokerHeartNode.getAlive()&&brokerDataNode!=null&&brokerDataNode.getMetas()!=null&&brokerDataNode.getMetas().size()>0){
                            zkDistributed.dataMigration(node);
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
