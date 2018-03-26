package com.dtstack.rdos.engine.execution.flink140;

import org.apache.flink.client.program.ClusterClient;
import org.apache.flink.yarn.YarnClusterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用于检测 flink-application 切换的问题
 * Date: 2018/3/26
 * Company: www.dtstack.com
 * @author xuchao
 */

public class YarnAppStatusMonitor implements Runnable{

    private static final Logger LOG = LoggerFactory.getLogger(YarnAppStatusMonitor.class);

    private static final Integer CHECK_INTERVAL = 1 * 1000;

    private AtomicBoolean run = new AtomicBoolean(true);

    private ClusterClient client;

    private FlinkClient flinkClient;

    public YarnAppStatusMonitor(ClusterClient client, FlinkClient flinkClient){
        this.flinkClient = flinkClient;
        this.client = client;
    }

    @Override
    public void run() {

        LOG.warn("start flink monitor thread");
        while (run.get()){
            if(flinkClient.isClientOn()){
                try{
                    ((YarnClusterClient) client).getJobManagerAddress();
                }catch (Exception e){
                    LOG.error("-------Flink session is down----");
                    //限制任务提交---直到恢复
                    flinkClient.setClientOn(false);
                }
            }

            if(!flinkClient.isClientOn()){
                retry();
            }

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                LOG.error("", e);
            }
        }
    }


    private void retry(){
        //重试
        try{
            LOG.warn("--retry flink client with yarn session----");
            flinkClient.initYarnClusterClient();
        }catch (Exception e){
            LOG.error("", e);
        }
    }


    public void setRun(boolean run){
        this.run = new AtomicBoolean(run);
    }
}
