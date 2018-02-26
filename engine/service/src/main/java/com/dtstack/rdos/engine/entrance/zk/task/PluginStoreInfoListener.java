package com.dtstack.rdos.engine.entrance.zk.task;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.pluginlog.PluginJobInfoComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sishu.yss on 2018/2/26.
 */
public class PluginStoreInfoListener implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(PluginStoreInfoListener.class);

    private MasterListener masterListener;

    public PluginStoreInfoListener(MasterListener masterListener){
        this.masterListener = masterListener;

    }

    @Override
    public void run() {

        while(true){

            try {
                Thread.sleep(30*60*1000);
                logger.info("PluginStoreInfoListener start again...");
                if(masterListener.isMaster()){
                    PluginJobInfoComponent.getPluginJobInfoComponent().clearJob();
                }
            } catch (Exception e) {
                logger.error("",e);
            }
        }
    }
}
