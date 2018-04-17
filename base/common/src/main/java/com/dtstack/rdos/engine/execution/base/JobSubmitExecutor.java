package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.restart.RestartStrategyUtil;
import com.dtstack.rdos.engine.execution.base.queue.ExeQueueMgr;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 任务提交执行容器
 * 单独起线程执行
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */
public class JobSubmitExecutor implements Closeable{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    /**循环任务等待队列的间隔时间：mills*/
    private static final int CHECK_INTERVAL = 2000;

    private int minPollSize = 5;

    private int maxPoolSize = 10;

    private ExecutorService jobExecutor;

    private ExecutorService queExecutor;

    private boolean hasInit = false;

    /**用于taskListener处理*/
    private LinkedBlockingQueue<JobClient> queueForTaskListener = new LinkedBlockingQueue<>();

    private ExeQueueMgr exeQueueMgr = ExeQueueMgr.getInstance();

    private ClientCache clientCache = ClientCache.getInstance();

    private static JobSubmitExecutor singleton = null;

    private JobSubmitExecutor(){
        init();
    }

    public static JobSubmitExecutor getInstance(){
        if(singleton == null){
            synchronized (JobSubmitExecutor.class){
                if(singleton == null){
                    singleton = new JobSubmitExecutor();
                }
            }
        }
        return singleton;
    }

    public void init(){
        try{
            if(!hasInit){
                this.maxPoolSize = ConfigParse.getSlots();
                this.jobExecutor = new ThreadPoolExecutor(minPollSize, maxPoolSize,
                        0L, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(1), new CustomThreadFactory("jobExecutor"));

                this.queExecutor = new ThreadPoolExecutor(3, 3,
                        0L, TimeUnit.MILLISECONDS,
                        new ArrayBlockingQueue<>(2), new CustomThreadFactory("queExecutor"));

                clientCache.initLocalPlugin(ConfigParse.getEngineTypeList());
                RestartStrategyUtil.getInstance();
                executionJob();
                hasInit = true;
                logger.warn("init JobSubmitExecutor success...");
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }


    private void executionJob(){
        queExecutor.submit(new Runnable() {
            @Override
            public void run() {

                while (true){
                    try{
                        exeQueueMgr.checkQueueAndSubmit();
                    }catch (Throwable e){
                        //防止退出循环
                        logger.error("----提交任务返回异常----", e);
                    }finally {
                        try {
                            Thread.sleep(CHECK_INTERVAL);
                        } catch (InterruptedException e) {
                            logger.error("", e);
                        }
                    }
                }
            }
        });
    }


    public void submitJob(JobClient jobClient) throws Exception{
        ExeQueueMgr.getInstance().add(jobClient);
    }

    public void addJobToProcessor(JobSubmitProcessor processor){
        jobExecutor.submit(processor);
    }


    public JobResult stopJob(JobClient jobClient) throws Exception {

        if(ExeQueueMgr.getInstance().remove(jobClient.getEngineType(), jobClient.getGroupName(), jobClient.getTaskId())){
            //直接移除
            Map<String, Integer> jobStatus = Maps.newHashMap();
            jobStatus.put(JobClientCallBack.JOB_STATUS, RdosTaskStatus.CANCELED.getStatus());
            jobClient.doJobClientCallBack(jobStatus);
        }

        if(jobClient.getEngineTaskId() == null){
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }

        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.cancelJob(jobClient.getEngineTaskId());
    }

    public LinkedBlockingQueue<JobClient> getQueueForTaskListener(){
        return queueForTaskListener;
    }

    public void addJobIntoTaskListenerQueue(JobClient jobClient){
        queueForTaskListener.offer(jobClient);
    }

    @Override
    public void close() throws IOException {
        if(jobExecutor != null){
            jobExecutor.shutdown();
        }

        if(queExecutor != null){
            queExecutor.shutdown();
        }
    }
}

