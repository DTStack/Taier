package com.dtstack.rdos.engine.execution.mysql.executor;

import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.mysql.dao.PluginMysqlJobInfoDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * FIXME 是否把timeOutDeal的执行修改为插件只执行一次
 * 定时更新任务的执行修改时间和清理过期任务
 * Date: 2018/2/7
 * Company: www.dtstack.com
 * @author xuchao
 */

public class StatusUpdateDealer implements Runnable {
    
    private static final Logger LOG = LoggerFactory.getLogger(StatusUpdateDealer.class);

    private final int interval = 2 * 1000;

    /**30分钟对 保留记录做一次删除*/
    private final int clear_rate = 900;

    private PluginMysqlJobInfoDao jobInfoDao = new PluginMysqlJobInfoDao();

    private boolean isRun = true;

    private Map<String, JobClient> jobCache;

    public StatusUpdateDealer(Map<String, JobClient> jobCache){
        this.jobCache = jobCache;
    }

    @Override
    public void run() {

        LOG.warn("---mysql StatusUpdateDealer is start----");
        int i = 0;

        while (isRun){
            try{

                i++;
                //更新时间
                jobInfoDao.updateModifyTime(jobCache.keySet());
                //更新很久未有操作的任务---防止某台机器挂了,任务状态未被更新
                jobInfoDao.timeOutDeal();

                if(i%clear_rate == 0){
                    jobInfoDao.clearJob();
                    LOG.info("do clear db mysql_job_info where modify is 7 day ago.");
                }

                Thread.sleep(interval);
            }catch (Throwable e){
                LOG.error("", e);
            }
        }

        LOG.warn("---mysql StatusUpdateDealer is stop----");
    }

    public void stop(){
        isRun = false;
    }
}
