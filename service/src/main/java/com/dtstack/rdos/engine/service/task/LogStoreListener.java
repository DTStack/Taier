package com.dtstack.rdos.engine.service.task;

import com.dtstack.rdos.engine.execution.base.plugin.log.LogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class LogStoreListener implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(LogStoreListener.class);

    private MasterListener masterListener;

    public LogStoreListener(MasterListener masterListener){
        this.masterListener = masterListener;

    }

    @Override
    public void run() {

        while(true){

            try {
                Thread.sleep(30*60*1000);
                logger.info("LogStoreListener start again...");
                if(masterListener.isMaster()){
                    LogStoreFactory.getLogStore(null).clearJob();
                }
            } catch (Exception e) {
                logger.error("",e);
            }
        }
    }
}
