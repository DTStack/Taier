package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.components.SlotNoAvailableJobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.EngineType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.loader.DtClassLoader;
import com.dtstack.rdos.engine.execution.queue.ExeQueueMgr;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
public class JobSubmitExecutor{

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitExecutor.class);

    /**循环任务等待队列的间隔时间：mills*/
    private static final int CHECK_INTERVAL = 2000;

    private int minPollSize = 5;

    private int maxPoolSize = 10;

    private ExecutorService jobExecutor;

    private ExecutorService queExecutor;

    private boolean hasInit = false;

    private SlotNoAvailableJobClient slotNoAvailableJobClients = new SlotNoAvailableJobClient();

    /**用于taskListener处理*/
    private LinkedBlockingQueue<JobClient> queueForTaskListener = new LinkedBlockingQueue<>();

    private ExeQueueMgr exeQueueMgr = ExeQueueMgr.getInstance();

    private ClientCache clientCache = ClientCache.getInstance();

    private static JobSubmitExecutor singleton = new JobSubmitExecutor();

    private JobSubmitExecutor(){
    }

    public static JobSubmitExecutor getInstance(){
        return singleton;
    }

    public void init() throws Exception{
        if(!hasInit){
            this.maxPoolSize = ConfigParse.getSlots();
            this.jobExecutor = new ThreadPoolExecutor(minPollSize, maxPoolSize,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(1), new CustomThreadFactory("jobExecutor"));

            this.queExecutor = new ThreadPoolExecutor(3, 3,
                    0L, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<>(2), new CustomThreadFactory("queExecutor"));

            clientCache.initLocalPlugin(ConfigParse.getEngineTypeList());
            executionJob();
            noAvailSlotsJobaddExecutionQueue();
            hasInit = true;
        }
    }


    private void executionJob(){
        queExecutor.submit(new Runnable() {
            @Override
            public void run() {

                while (true){
                    try{
                        exeQueueMgr.checkQueueAndSubmit(slotNoAvailableJobClients);
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
    
    private void noAvailSlotsJobaddExecutionQueue(){
    	queExecutor.submit(new Runnable(){
			@Override
			public void run() {
				for(;;){
					try {
						Thread.sleep(CHECK_INTERVAL);
						slotNoAvailableJobClients.noAvailSlotsJobAddExecutionQueue();
					} catch (InterruptedException e) {
						logger.error("", e);
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

        if(ExeQueueMgr.getInstance().remove(jobClient.getEngineType(), jobClient.getGroupName(), jobClient.getTaskId())
                || slotNoAvailableJobClients.remove(jobClient.getTaskId())){
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
    
    public void shutdown(){
        if(jobExecutor != null){
            jobExecutor.shutdown();
        }

        if(queExecutor != null){
        	queExecutor.shutdown();
        }
    }

    public LinkedBlockingQueue<JobClient> getQueueForTaskListener(){
        return queueForTaskListener;
    }

    public void addJobForTaskListenerQueue(JobClient jobClient){
        queueForTaskListener.offer(jobClient);
    }
}

