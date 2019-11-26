package com.dtstack.engine.rdbs.common.executor;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.plugin.log.LogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.engine.common.plugin.log.LogStore;
import java.util.Map;

/**
 * FIXME 是否把timeOutDeal的执行修改为插件只执行一次
 * 定时更新任务的执行修改时间和清理过期任务
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author
 */

public class StatusUpdateDealer implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(StatusUpdateDealer.class);

    private final int interval = 2 * 1000;

    private LogStore logstore = LogStoreFactory.getLogStore(null);

    private boolean isRun = true;

    private Map<String, JobClient> jobCache;

    public StatusUpdateDealer(Map<String, JobClient> jobCache){
        this.jobCache = jobCache;
    }

    @Override
    public void run() {

        LOG.warn("---StatusUpdateDealer is start----");
        int i = 0;

        while (isRun){
            try{

                i++;
                //更新时间
                logstore.updateModifyTime(jobCache.keySet());
                //更新很久未有操作的任务---防止某台机器挂了,任务状态未被更新
                logstore.timeOutDeal();
                Thread.sleep(interval);
            }catch (Throwable e){
                LOG.error("", e);
            }
        }
        LOG.warn("---StatusUpdateDealer is stop----");
    }

    public void stop(){
        isRun = false;
    }
}
