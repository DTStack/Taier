package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.MathUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.enums.ComputeType;
import com.dtstack.rdos.engine.execution.base.enums.EngineType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.task.RestartDealer;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
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

    public final static String SYS_CANCLED_LOG = "{\"root-exception\":\"The system could not query the task state, so it is set to cancel state actively\"}";

    public final static String FLINK_CP_HISTORY_KEY = "history";

    public final static String TRIGGER_TIMESTAMP_KEY = "trigger_timestamp";

    public final static String CHECKPOINT_ID_KEY = "id";

    public final static String CHECKPOINT_SAVEPATH_KEY = "external_path";

    private static final long LISTENER_INTERVAL = 2000;

    /** 已经插入到db的checkpoint，其id缓存数量*/
    private static final long CHECKPOINT_INSERTED_RECORD = 2000;

    private static final char SEPARATOR = '_';

	private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

	/**记录job 连续某个状态的频次*/
	private Map<String, TaskStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

    private RdosEngineStreamJobDAO rdosStreamTaskDAO = new RdosEngineStreamJobDAO();

	private RdosEngineBatchJobDAO rdosBatchEngineJobDAO = new RdosEngineBatchJobDAO();

	private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

	private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

	private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

	/**失败任务的额外处理：当前只是对(失败任务 or 取消任务)继续更新日志或者更新checkpoint*/
    private Map<String, FailedTaskInfo> failedJobCache = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1,Integer.MAX_VALUE, 60L,TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("taskStatusListener"));

    private Cache<String, Integer> checkpointGetTotalNumCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 60, TimeUnit.SECONDS).build();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    //每隔5次状态获取之后更新一次checkpoint 信息 ===>checkpoint信息没必要那么频繁更新
    private int checkpointGetRate = 10;

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

                //flink任务在失败或者取消情况下多次更新checkpoint
                if(failedTaskInfo.getComputeType() == ComputeType.STREAM.getType()
                        && EngineType.isFlink(failedTaskInfo.getEngineType())){
                    //更新checkpoint
                    updateStreamJobCheckpoints(failedTaskInfo.getJobIdentifier(), failedTaskInfo.getEngineType(), failedTaskInfo.getPluginInfo());
                }

                failedTaskInfo.tryLog();

                if(!failedTaskInfo.canTryLogAgain()){
                    failedJobCache.remove(key);
                }
            }
        }catch (Exception e){
            logger.error("dealFailed job run error:{}",ExceptionUtil.getErrorMessage(e));
        }
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

                                    if (computeType == ComputeType.STREAM.getType()) {
                                        dealStreamJob(taskId, engineTypeName, zkTaskId, computeType);
                                    } else if (computeType == ComputeType.BATCH.getType()) {
                                        dealBatchJob(taskId, engineTypeName, zkTaskId, computeType);
                                    }
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

	private void dealStreamJob(String taskId, String engineTypeName, String zkTaskId, int computeType) throws Exception {
        RdosEngineStreamJob rdosTask = rdosStreamTaskDAO.getRdosTaskByTaskId(taskId);

        if(rdosTask != null){
            String engineTaskId = rdosTask.getEngineTaskId();
            String appId = rdosTask.getApplicationId();
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineTaskId, appId, taskId);

            if(StringUtils.isNotBlank(engineTaskId)){
                String pluginInfoStr = "";
                if(rdosTask.getPluginInfoId() > 0 ){
                    pluginInfoStr = pluginInfoDao.getPluginInfo(rdosTask.getPluginInfoId());
                }

                RdosTaskStatus rdosTaskStatus = JobClient.getStatus(engineTypeName, pluginInfoStr, jobIdentifier);

                if(rdosTaskStatus != null){

                    updateJobEngineLog(taskId, jobIdentifier, engineTypeName, computeType, pluginInfoStr);

                    rdosTaskStatus = checkNotFoundStatus(rdosTaskStatus, taskId);

                    Integer status = rdosTaskStatus.getStatus();
                    boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);
                    if(isRestart){
                        return;
                    }

                    zkLocalCache.updateLocalMemTaskStatus(zkTaskId, status);
                    //数据的更新顺序，先更新job_cache，再更新engine_stream_job
                    dealStreamAfterGetStatus(status, taskId, engineTypeName, jobIdentifier, pluginInfoStr);

                    rdosStreamTaskDAO.updateTaskStatus(taskId, status);
                }

                if(RdosTaskStatus.FAILED.equals(rdosTaskStatus)
                        || RdosTaskStatus.CANCELED.equals(rdosTaskStatus)
                        || RdosTaskStatus.KILLED.equals(rdosTaskStatus)){
                    FailedTaskInfo failedTaskInfo = new FailedTaskInfo(taskId, jobIdentifier,
                            engineTypeName, computeType, pluginInfoStr);
                    addFailedJob(failedTaskInfo);
                }
            }
        } else {
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.FAILED.getStatus());
            rdosEngineJobCacheDao.deleteJob(taskId);
        }
    }

    private void dealBatchJob(String taskId, String engineTypeName, String zkTaskId, int computeType) throws Exception {
        RdosEngineBatchJob rdosBatchJob  = rdosBatchEngineJobDAO.getRdosTaskByTaskId(taskId);

        if(rdosBatchJob != null){
            String engineTaskId = rdosBatchJob.getEngineJobId();
            String appId = rdosBatchJob.getApplicationId();
            JobIdentifier jobIdentifier = JobIdentifier.createInstance(engineTaskId, appId, null);

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
                    boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, engineTypeName, computeType, pluginInfoStr);
                    if(isRestart){
                        return;
                    }

                    zkLocalCache.updateLocalMemTaskStatus(zkTaskId, status);
                    //数据的更新顺序，先更新job_cache，再更新engine_batch_job
                    dealBatchJobAfterGetStatus(status, taskId);

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

        //从engine获取log
        String jobLog = JobClient.getEngineLog(engineType, pluginInfo, jobIdentifier);
        updateJobEngineLog(jobId, jobLog, computeType);
    }

    private void updateJobEngineLog(String jobId, String jobLog, Integer computeType){

        //写入db
        if(ComputeType.STREAM.getType().equals(computeType)){
            rdosStreamTaskDAO.updateEngineLog(jobId, jobLog);
        }else if(ComputeType.BATCH.getType().equals(computeType)){
            rdosBatchEngineJobDAO.updateEngineLog(jobId, jobLog);
        }else{
            logger.info("----- not support compute type {}.", computeType);
        }
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

        //运行中的stream任务需要更新checkpoint 并且 控制频率
        Integer checkpointCallNum = checkpointGetTotalNumCache.get(engineTaskId, () -> 0);
        if(RdosTaskStatus.RUNNING.getStatus().equals(status)){
            if(checkpointCallNum%checkpointGetRate == 0){
                updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }

            checkpointGetTotalNumCache.put(engineTaskId, checkpointCallNum + 1);
        }

        if(RdosTaskStatus.getStoppedStatus().contains(status)){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);

            if(Strings.isNullOrEmpty(engineTaskId)){
                return;
            }

            //TODO 各个插件的操作移动到插件本身
            updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
        }
    }

    private void updateStreamJobCheckpoints(JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo){
        String checkPointJsonStr = JobClient.getCheckpoints(engineTypeName, pluginInfo, jobIdentifier);
        updateStreamJobCheckPoint(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), checkPointJsonStr);
    }

    private void updateStreamJobCheckPoint(String taskId, String engineTaskId, String checkpointJsonStr){

        if(Strings.isNullOrEmpty(checkpointJsonStr)){
            logger.info(String.format("taskId %s engineTaskId %s can't get checkpoint info.", taskId, engineTaskId));
            return;
        }

        //获取checkpointJsonStr 的第一个存储的触发时间,最后一个存储的触发时间
        try {
            Map<String, Object> cpJson = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if(!cpJson.containsKey(FLINK_CP_HISTORY_KEY)){
                return;
            }

            List<Map<String, Object>> cpList = (List<Map<String, Object>>) cpJson.get(FLINK_CP_HISTORY_KEY);
            if(CollectionUtils.isEmpty(cpList)){
                return;
            }

            Map<String, Object> endNode = cpList.get(0);
            Map<String, Object> startNode = cpList.get(cpList.size() - 1);

            Long startTime = MathUtil.getLongVal(startNode.get(TRIGGER_TIMESTAMP_KEY));
            Long endTime = MathUtil.getLongVal(endNode.get(TRIGGER_TIMESTAMP_KEY));

            Timestamp startTimestamp = new Timestamp(startTime);
            Timestamp endTimestamp = new Timestamp(endTime);

            for (Map<String, Object> entity : cpList) {
                String checkpointID = String.valueOf(entity.get(CHECKPOINT_ID_KEY));
                Long   checkpointTrigger = MathUtil.getLongVal(entity.get(TRIGGER_TIMESTAMP_KEY));
                String checkpointSavepath = String.valueOf(entity.get(CHECKPOINT_SAVEPATH_KEY));

                String checkpointCacheKey = taskId + SEPARATOR + checkpointID;

                if (StringUtils.isEmpty(checkpointInsertedCache.getIfPresent(checkpointCacheKey))) {
                    Timestamp checkpointTriggerTimestamp = new Timestamp(checkpointTrigger);
                    rdosStreamTaskCheckpointDAO.insert(taskId, engineTaskId, checkpointID, checkpointTriggerTimestamp, checkpointSavepath, startTimestamp, endTimestamp);
                    checkpointInsertedCache.put(checkpointCacheKey, "1");  //存在标识
                }
            }
        } catch (IOException e) {
            logger.error("", e);
        }


    }

    private void dealBatchJobAfterGetStatus(Integer status, String jobId){
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

    public void updateJobStatus(String jobId, Integer computeType, Integer status) {
        if (ComputeType.STREAM.getType().equals(computeType)) {
            rdosStreamTaskDAO.updateTaskStatus(jobId, status);
        } else {
            rdosBatchEngineJobDAO.updateJobStatus(jobId, status);
        }
    }
}