package com.dtstack.engine.master.task;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.RdosEngineJobDAO;
import com.dtstack.engine.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.dao.RdosPluginInfoDAO;
import com.dtstack.engine.domain.RdosEngineJob;
import com.dtstack.engine.common.util.TaskIdUtil;
import com.dtstack.engine.master.bo.FailedTaskInfo;
import com.dtstack.engine.master.cache.ZkLocalCache;
import com.dtstack.engine.master.data.BrokerDataShard;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author sishu.yss
 *
 */
public class TaskStatusListener implements Runnable{

	private static Logger logger = LoggerFactory.getLogger(TaskStatusListener.class);

	/**最大允许查询不到任务信息的次数--超过这个次数任务会被设置为CANCELED*/
    public final static int NOT_FOUND_LIMIT_TIMES = 300;

    /**最大允许查询不到的任务信息最久时间*/
    public final static int NOT_FOUND_LIMIT_INTERVAL = 3 * 60 * 1000;

    private static final long LISTENER_INTERVAL = 2000;

    private static final int JOB_FAILOVER_CONFIG = 50;

	private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

	/**记录job 连续某个状态的频次*/
	private Map<String, TaskStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

	private RdosEngineJobDAO rdosBatchEngineJobDAO = new RdosEngineJobDAO();

	private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

	private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

	/**失败任务的额外处理：当前只是对(失败任务 or 取消任务)继续更新日志或者更新checkpoint*/
    private Map<String, FailedTaskInfo> failedJobCache = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("taskStatusListener"));

    //  failover log
    private Cache<String, Long> failoverTimestampCache = CacheBuilder.newBuilder().maximumSize(JOB_FAILOVER_CONFIG).build();

    private CheckpointListener checkpointListener;

    public TaskStatusListener() {
        init();
    }

    public void init() {
        checkpointListener = new CheckpointListener();
    }

    @Override
	public void run() {
	  	int index = 0;
	  	while(true){
	  		try{
		  		++index;
                Thread.sleep(LISTENER_INTERVAL);
		  		if(PublicUtil.count(index, 5)){
		  		    logger.warn("\n\t\t  \t\tThread.sleep({});TaskStatusListener start again...", LISTENER_INTERVAL);
		  		}
		  		updateTaskStatus();
                dealFailedJob();
			}catch(Throwable e){
				logger.error("TaskStatusTaskListener run error:{}",ExceptionUtil.getErrorMessage(e));
            }
        }
	}

	public void dealFailedJob(){
        try{
            for(Map.Entry<String, FailedTaskInfo> failedTaskEntry : failedJobCache.entrySet()){
                FailedTaskInfo failedTaskInfo = failedTaskEntry.getValue();
                String key = failedTaskEntry.getKey();
                updateJobEngineLog(failedTaskInfo.getJobId(), failedTaskInfo.getJobIdentifier(),
                        failedTaskInfo.getEngineType(), failedTaskInfo.getComputeType() , failedTaskInfo.getPluginInfo());

                boolean streamAndopenCheckpoint = isFlinkStreamTask(failedTaskInfo) && checkpointListener.checkOpenCheckPoint(failedTaskInfo.getJobId());
                if(streamAndopenCheckpoint) {
                    //更新checkpoint
                    checkpointListener.updateStreamJobCheckpoints(failedTaskInfo.getJobIdentifier(), failedTaskInfo.getEngineType(), failedTaskInfo.getPluginInfo());
                } else if(isSyncTask(failedTaskInfo)){
                    checkpointListener.updateBatchTaskCheckpoint(failedTaskInfo.getPluginInfo(),failedTaskInfo.getJobIdentifier());
                }

                failedTaskInfo.waitClean();
                if(!failedTaskInfo.allowClean()){
                    // filter batch task
                    if(streamAndopenCheckpoint){
                        checkpointListener.dealStreamCheckpoint(failedTaskInfo);
                    }
                    failedJobCache.remove(key);
                }
            }
        }catch (Exception e){
            logger.error("dealFailed job run error:{}",ExceptionUtil.getErrorMessage(e));
        }
    }

    private boolean isSyncTask(FailedTaskInfo failedTaskInfo) {
        return failedTaskInfo.getComputeType() == ComputeType.BATCH.getType()
                && EngineType.isFlink(failedTaskInfo.getEngineType());
    }

    public boolean isFlinkStreamTask(FailedTaskInfo failedTaskInfo ) {
        return failedTaskInfo.getComputeType() == ComputeType.STREAM.getType()
                && EngineType.isFlink(failedTaskInfo.getEngineType());
    }

    public void addFailedJob(FailedTaskInfo failedTaskInfo){
        if(!failedJobCache.containsKey(failedTaskInfo.getJobId())){
            failedJobCache.put(failedTaskInfo.getJobId(), failedTaskInfo);
        }
    }

	private void updateTaskStatus(){
        try {
            Map<String, BrokerDataShard> shards = zkLocalCache.cloneShardData();
            CountDownLatch ctl = new CountDownLatch(shards.size());
            for (Map.Entry<String,BrokerDataShard> shardEntry: shards.entrySet()) {
                taskStatusPool.submit(()->{
                    try {
                        for (Map.Entry<String, Byte> entry : shardEntry.getValue().getView().entrySet()) {
                            try {
                                if (!RdosTaskStatus.needClean(entry.getValue().intValue())) {
                                    String zkTaskId = entry.getKey();
                                    int computeType = TaskIdUtil.getComputeType(zkTaskId);
                                    String engineTypeName = TaskIdUtil.getEngineType(zkTaskId);
                                    String taskId = TaskIdUtil.getTaskId(zkTaskId);

                                    //todo : 测试日志，便于排查问题
                                    logger.info("jobId:{} status:{}", taskId, entry.getValue().intValue());

                                    dealBatchJob(taskId, engineTypeName, zkTaskId, computeType);
                                }
                            } catch (Throwable e) {
                                logger.error("", e);
                            }
                        }
                    } catch (Throwable e) {
                        logger.error("{}", e);
                    } finally {
                        ctl.countDown();
                    }
                });
            }
            ctl.await();
        } catch (Throwable e) {
            logger.error("{}", e);
        }
	}

    private void dealBatchJob(String taskId, String engineTypeName, String zkTaskId, int computeType) throws Exception {
        RdosEngineJob rdosBatchJob  = rdosBatchEngineJobDAO.getRdosTaskByTaskId(taskId);

        if(rdosBatchJob != null){
            String engineTaskId = rdosBatchJob.getEngineJobId();
            String appId = rdosBatchJob.getApplicationId();
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineTaskId, appId, taskId);

            if(StringUtils.isNotBlank(engineTaskId)){
                String pluginInfoStr = "";
                if(rdosBatchJob.getPluginInfoId() > 0 ){
                    pluginInfoStr = pluginInfoDao.getPluginInfo(rdosBatchJob.getPluginInfoId());
                }

                RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, pluginInfoStr, jobIdentifier);

                if(rdosTaskStatus != null){

                    updateJobEngineLog(taskId, jobIdentifier, engineTypeName, computeType, pluginInfoStr);

                    rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, taskId);

                    Integer status = rdosTaskStatus.getStatus();
                    // 重试状态 先不更新状态
                    boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, appId, engineTypeName, computeType, pluginInfoStr);
                    if(isRestart){
                        return;
                    }

                    if(checkpointListener.isSyncTaskAndOpenCheckpoint(taskId, engineTypeName, computeType)){
                        checkpointListener.dealSyncTaskCheckpoint(status, jobIdentifier, pluginInfoStr);
                    }

                    zkLocalCache.updateLocalMemTaskStatus(zkTaskId, status);
                    //数据的更新顺序，先更新job_cache，再更新engine_batch_job

                    if (computeType == ComputeType.STREAM.getType()){
                        dealStreamAfterGetStatus(status, taskId, engineTypeName, jobIdentifier, pluginInfoStr);
                    } else {
                        dealBatchJobAfterGetStatus(status, taskId);
                    }

                    rdosBatchEngineJobDAO.updateJobStatusAndExecTime(taskId, status);
                }

                if(RdosTaskStatus.FAILED.equals(rdosTaskStatus)){
                    FailedTaskInfo failedTaskInfo = new FailedTaskInfo(rdosBatchJob.getJobId(), jobIdentifier,
                            engineTypeName, computeType, pluginInfoStr);
                    addFailedJob(failedTaskInfo);
                }
            }
        } else {
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.FAILED.getStatus());
            rdosEngineJobCacheDao.deleteJob(taskId);
        }
    }

	private void updateJobEngineLog(String jobId, JobIdentifier jobIdentifier, String engineType, int computeType, String pluginInfo){
        try {
            //从engine获取log
            String jobLog = JobClient.getEngineLog(engineType, pluginInfo, jobIdentifier);
            if (jobLog != null){
                updateJobEngineLog(jobId, jobLog, computeType);
            }
        } catch (Throwable e){
            String errorLog = ExceptionUtil.getErrorMessage(e);
            logger.error("update JobEngine Log error jobid {} ,error info {}..", jobId, errorLog);
            updateJobEngineLog(jobId, errorLog, computeType);
        }
    }

    private void updateJobEngineLog(String jobId, String jobLog, Integer computeType){

        //写入db
        rdosBatchEngineJobDAO.updateEngineLog(jobId, jobLog);
    }

    private RdosTaskStatus checkNotFoundStatus(RdosTaskStatus taskStatus, String jobId){
        TaskStatusFrequency statusPair = updateJobStatusFrequency(jobId, taskStatus.getStatus());
        if(statusPair.getStatus() == RdosTaskStatus.NOTFOUND.getStatus().intValue()){
            if(statusPair.getNum() >= NOT_FOUND_LIMIT_TIMES ||
                    System.currentTimeMillis() - statusPair.getCreateTime() >= NOT_FOUND_LIMIT_INTERVAL){
                return RdosTaskStatus.FAILED;
            }
        }
        return taskStatus;
    }

    /**
     * stream 获取任务状态--的处理
     * @param status
     * @param jobId
     */
    private void dealStreamAfterGetStatus(Integer status, String jobId, String engineTypeName,
                                          JobIdentifier jobIdentifier, String pluginInfo) throws ExecutionException {

        String engineTaskId = jobIdentifier.getEngineJobId();

        boolean openCheckPoint = checkpointListener.checkOpenCheckPoint(jobId);

        if(RdosTaskStatus.getStoppedStatus().contains(status)){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);

            if(Strings.isNullOrEmpty(engineTaskId)){
                return;
            }

            if (openCheckPoint) {
                checkpointListener.updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }
        }

        if (!openCheckPoint) {
            return;
        }

        if(RdosTaskStatus.RUNNING.getStatus().equals(status)){
            //运行中的stream任务需要更新checkpoint 并且 控制频率
            Integer checkpointCallNum = checkpointListener.getCheckpointCallNum(engineTaskId);
            if(checkpointCallNum % CheckpointListener.CHECKPOINT_GET_RATE == 0){
                checkpointListener.updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }
        }

    }

    private void dealBatchJobAfterGetStatus(Integer status, String jobId) throws ExecutionException{
        if(RdosTaskStatus.getStoppedStatus().contains(status)){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
        }
    }

    /**
     * 更新任务状态频次
     * @param jobId
     * @param status
     * @return
     */
    private TaskStatusFrequency updateJobStatusFrequency(String jobId, Integer status){

        TaskStatusFrequency statusFrequency = jobStatusFrequency.get(jobId);
        statusFrequency = statusFrequency == null ? new TaskStatusFrequency(status) : statusFrequency;
        if(statusFrequency.getStatus() == status.intValue()){
            statusFrequency.setNum(statusFrequency.getNum() + 1);
        }else{
            statusFrequency = new TaskStatusFrequency(status);
        }

        jobStatusFrequency.put(jobId, statusFrequency);
        return statusFrequency;
    }

}