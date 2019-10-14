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
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineBatchJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosEngineStreamJobDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosPluginInfoDAO;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineBatchJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.rdos.engine.service.db.dataobject.RdosEngineStreamJob;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.dtstack.rdos.engine.service.task.RestartDealer;
import com.dtstack.rdos.engine.service.util.TaskIdUtil;
import com.dtstack.rdos.engine.service.zk.cache.ZkLocalCache;
import com.dtstack.rdos.engine.service.zk.data.BrokerDataShard;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Timestamp;
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

    public final static String SYS_CANCLED_LOG = "{\"root-exception\":\"The system could not query the task state, so it is set to cancel state actively\"}";

    public final static String FLINK_CP_HISTORY_KEY = "history";

    public final static String FLINK_CP_COUNTS_KEY = "counts";

    public final static String TRIGGER_TIMESTAMP_KEY = "trigger_timestamp";

    public final static String CHECKPOINT_ID_KEY = "id";

    public final static String CHECKPOINT_SAVEPATH_KEY = "external_path";

    /** 开启checkpoint，但未绑定外部存储路径*/
    public  final static String CHECKPOINT_NOT_EXTERNALLY_ADDRESS_KEY = "<checkpoint-not-externally-addressable>";

    private static final String CHECKPOINT_STATUS_KEY = "status";

    private static final String CHECKPOINT_COMPLETED_STATUS = "COMPLETED";

    private static final String TASK_PARAMS_KEY = "taskParams";

    private static final String SQL_CHECKPOINT_INTERVAL_KEY = "sql.checkpoint.interval";
    private static final String FLINK_CHECKPOINT_INTERVAL_KEY = "flink.checkpoint.interval";

    private static final String SQL_CHECKPOINT_CLEANUP_MODE_KEY = "sql.checkpoint.cleanup.mode";
    private static final String FLINK_CHECKPOINT_CLEANUP_MODE_KEY = "flink.checkpoint.cleanup.mode";

    private static final long LISTENER_INTERVAL = 2000;

    /** 已经插入到db的checkpoint，其id缓存数量*/
    private static final long CHECKPOINT_INSERTED_RECORD = 200;

    private static final char SEPARATOR = '_';

    private static final int JOB_CHECKPOINT_CONFIG = 50;

    private static final int JOB_FAILOVER_CONFIG = 50;

    //每隔5次状态获取之后更新一次checkpoint 信息 ===>checkpoint信息没必要那么频繁更新
    private int checkpointGetRate = 10;

	private ZkLocalCache zkLocalCache = ZkLocalCache.getInstance();

	/**记录job 连续某个状态的频次*/
	private Map<String, TaskStatusFrequency> jobStatusFrequency = Maps.newConcurrentMap();

    private RdosEngineStreamJobDAO rdosStreamTaskDAO = new RdosEngineStreamJobDAO();

	private RdosEngineBatchJobDAO rdosBatchEngineJobDAO = new RdosEngineBatchJobDAO();

	private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

	private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private RdosEngineJobCacheDAO engineJobCacheDAO = new RdosEngineJobCacheDAO();

	private RdosPluginInfoDAO pluginInfoDao = new RdosPluginInfoDAO();

	/**失败任务的额外处理：当前只是对(失败任务 or 取消任务)继续更新日志或者更新checkpoint*/
    private Map<String, FailedTaskInfo> failedJobCache = Maps.newConcurrentMap();

    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1,Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("taskStatusListener"));

    private Cache<String, Integer> checkpointGetTotalNumCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 60, TimeUnit.SECONDS).build();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    private Cache<String, Map<String, Object>> checkpointConfigCache = CacheBuilder.newBuilder().maximumSize(JOB_CHECKPOINT_CONFIG).build();

    //  failover log
    private Cache<String, Long> failoverTimestampCache = CacheBuilder.newBuilder().maximumSize(JOB_FAILOVER_CONFIG).build();

    private CheckpointListener checkpointListener;

    public TaskStatusListener() {
        init();
    }

    public void init() {
        checkpointListener = new CheckpointListener();
        checkpointListener.startCheckpointScheduled();
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

                boolean streamAndopenCheckpoint = isFlinkStreamTask(failedTaskInfo) && checkOpenCheckPoint(failedTaskInfo.getJobId());

                if(streamAndopenCheckpoint) {
                    //更新checkpoint
                    updateStreamJobCheckpoints(failedTaskInfo.getJobIdentifier(), failedTaskInfo.getEngineType(), failedTaskInfo.getPluginInfo());
                } else if(isSyncTask(failedTaskInfo)){
                    updateBatchTaskCheckpoint(failedTaskInfo.getPluginInfo(),failedTaskInfo.getJobIdentifier());
                }

                failedTaskInfo.waitClean();

                if(!failedTaskInfo.allowClean()){
                    // filter batch task
                    if(streamAndopenCheckpoint){
                        dealStreamCheckpoint(failedTaskInfo);
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

    private void dealStreamCheckpoint(FailedTaskInfo failedTaskInfo) {
        try {
            JobIdentifier jobIdentifier = failedTaskInfo.getJobIdentifier();
            if (null != jobIdentifier && StringUtils.isNotBlank(jobIdentifier.getEngineJobId())) {
                if (checkOpenCheckPoint(failedTaskInfo.getJobId())) {
                    Boolean sqlCleanMode = MathUtil.getBoolean(getParmaFromJobCache(failedTaskInfo.getJobId(), SQL_CHECKPOINT_CLEANUP_MODE_KEY), false);
                    Boolean flinkCleanMode = MathUtil.getBoolean(getParmaFromJobCache(failedTaskInfo.getJobId(), FLINK_CHECKPOINT_CLEANUP_MODE_KEY), false);
                    if (sqlCleanMode || flinkCleanMode ) {
                        // true then remove all
                        checkpointListener.cleanAllCheckpointByTaskEngineId(jobIdentifier.getEngineJobId());
                    } else {
                        //主动清理超过范围的checkpoint
                        checkpointListener.SubtractionCheckpointRecord(jobIdentifier.getEngineJobId());
                    }
                    //集合中移除该任务
                    checkpointListener.removeByTaskEngineId(jobIdentifier.getEngineJobId());
                    checkpointConfigCache.invalidate(failedTaskInfo.getJobId());
                }
            }
        } catch (Exception e) {
            logger.error("deal stream checkpoint error: {}", ExceptionUtil.getErrorMessage(e));
        }

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

                                    if (computeType == ComputeType.STREAM.getType()) {
                                        dealStreamJob(taskId, engineTypeName, zkTaskId, computeType);
                                        //将流任务ID放入缓存中，定时触发checkpoint清理
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
                    boolean isRestart = RestartDealer.getInstance().checkAndRestart(status, taskId, engineTaskId, appId ,engineTypeName, computeType, pluginInfoStr);
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

                    updateStreamTaskEndTime(taskId);
                }
            }
        } else {
            zkLocalCache.updateLocalMemTaskStatus(zkTaskId, RdosTaskStatus.FAILED.getStatus());
            rdosEngineJobCacheDao.deleteJob(taskId);
        }
    }

    private void updateStreamTaskEndTime(String taskId) {
        try {
            rdosStreamTaskDAO.updateStreamTaskEndTime(taskId);
        } catch (Exception e) {
            logger.error("update stream task endtime error", e);
        }

    }

    private void dealBatchJob(String taskId, String engineTypeName, String zkTaskId, int computeType) throws Exception {
        RdosEngineBatchJob rdosBatchJob  = rdosBatchEngineJobDAO.getRdosTaskByTaskId(taskId);

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

                    if(isSyncTaskAndOpenCheckpoint(taskId, engineTypeName, computeType)){
                        dealSyncTaskCheckpoint(status, jobIdentifier, pluginInfoStr);
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
        try {
            //从engine获取log
            String jobLog = JobClient.getEngineLog(engineType, pluginInfo, jobIdentifier);
            if (StringUtils.isEmpty(jobLog)) {
                return;
            }

            Map<String, Object> logMap = PublicUtil.jsonStrToObject(jobLog, Map.class);
            Long timestamp = MathUtil.getLongVal(logMap.get("timestamp"));

            Long exitJobtimestamp = failoverTimestampCache.getIfPresent(jobId);

            if (null == timestamp || timestamp.equals(exitJobtimestamp)) {
                return;
            }

            failoverTimestampCache.put(jobId, timestamp);

            jobLog = MathUtil.getString(logMap.get("root-exception"));

            updateJobEngineLog(jobId, jobLog, computeType);
        } catch (Throwable e){
            logger.error("update JobEngine Log error jobid {} ,error info {}..", jobId,ExceptionUtil.getErrorMessage(e));
            updateJobEngineLog(jobId, ExceptionUtil.getErrorMessage(e), computeType);
        }
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

        boolean openCheckPoint = checkOpenCheckPoint(jobId);

        if(RdosTaskStatus.getStoppedStatus().contains(status)){

            jobStatusFrequency.remove(jobId);

            rdosEngineJobCacheDao.deleteJob(jobId);


            if(Strings.isNullOrEmpty(engineTaskId)){
                return;
            }

            if (openCheckPoint) {
                updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }

        }

        if (!openCheckPoint) {
            return;
        }

        //运行中的stream任务需要更新checkpoint 并且 控制频率
        Integer checkpointCallNum = checkpointGetTotalNumCache.get(engineTaskId, () -> 0);
        if(RdosTaskStatus.RUNNING.getStatus().equals(status)){
            if(checkpointCallNum%checkpointGetRate == 0){
                updateStreamJobCheckpoints(jobIdentifier, engineTypeName, pluginInfo);
            }

            checkpointGetTotalNumCache.put(engineTaskId, checkpointCallNum + 1);
        }

    }

    private boolean checkOpenCheckPoint(String jobId) throws ExecutionException {
        long sqlCheckpointInterval = MathUtil.getLongVal(getParmaFromJobCache(jobId, SQL_CHECKPOINT_INTERVAL_KEY), 0L);
        long flinkCheckpointInterval = MathUtil.getLongVal(getParmaFromJobCache(jobId, FLINK_CHECKPOINT_INTERVAL_KEY), 0L);
        long checkpointInterval = Math.max(sqlCheckpointInterval, flinkCheckpointInterval);
        if (checkpointInterval <= 0){
            return false;
        }
        return true;
    }

    private Object getParmaFromJobCache(String jobId, String key) throws ExecutionException {

        Map<String, Object> taskParams = checkpointConfigCache.get(jobId, () -> {
            Map<String, Object> result = Maps.newConcurrentMap();

            RdosEngineJobCache jobCache = rdosEngineJobCacheDao.getJobById(jobId);
            String tps = String.valueOf(getValueFromStringInfoKey(jobCache.getJobInfo(), TASK_PARAMS_KEY));

            if (StringUtils.isNotEmpty(tps)) {
                for (String str : tps.split("\n")) {
                    String[] keyAndVal = str.split("=");
                    if (keyAndVal.length > 1) {
                        result.put(keyAndVal[0].trim(), keyAndVal[1].trim());
                    }
                }
            }
            return result;
        });

        return taskParams.get(key);
    }

    private void updateStreamJobCheckpoints(JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo){
        String checkPointJsonStr = JobClient.getCheckpoints(engineTypeName, pluginInfo, jobIdentifier);
        updateStreamJobCheckPoint(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), checkPointJsonStr, pluginInfo);
    }

    private void updateStreamJobCheckPoint(String taskId, String engineTaskId, String checkpointJsonStr, String pluginInfo){

        if(Strings.isNullOrEmpty(checkpointJsonStr)){
            logger.info("taskId {} engineTaskId {} can't get checkpoint info.", taskId, engineTaskId);
            return;
        }

        //获取checkpointJsonStr 的第一个存储的触发时间,最后一个存储的触发时间
        try {
            Map<String, Object> cpJson = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if(!cpJson.containsKey(FLINK_CP_HISTORY_KEY)){
                return;
            }

            List<Map<String, Object>> cpList = cpJson.get(FLINK_CP_HISTORY_KEY) == null ? null : (List<Map<String, Object>>) cpJson.get(FLINK_CP_HISTORY_KEY);
            if(CollectionUtils.isEmpty(cpList)){
                return;
            }
            Map<String, Object> counts = cpJson.get(FLINK_CP_COUNTS_KEY) == null ? null : (Map<String, Object>)cpJson.get(FLINK_CP_COUNTS_KEY);

            String checkpointCounts = "";

            if (null != counts) {
                checkpointCounts = PublicUtil.objToString(counts);
            }

            checkpointIntervalClean(engineTaskId, cpList.get(0), pluginInfo);

            checkpointListener.putTaskEngineIdAndRetainedNum(engineTaskId, pluginInfo);

            for (Map<String, Object> entity : cpList) {
                String checkpointID = String.valueOf(entity.get(CHECKPOINT_ID_KEY));
                Long   checkpointTrigger = MathUtil.getLongVal(entity.get(TRIGGER_TIMESTAMP_KEY));
                String checkpointSavepath = String.valueOf(entity.get(CHECKPOINT_SAVEPATH_KEY));
                String status = String.valueOf(entity.get(CHECKPOINT_STATUS_KEY));

                String checkpointCacheKey = engineTaskId + SEPARATOR + checkpointID;

                if (!StringUtils.equalsIgnoreCase(CHECKPOINT_NOT_EXTERNALLY_ADDRESS_KEY, checkpointSavepath) &&
                        StringUtils.equalsIgnoreCase(CHECKPOINT_COMPLETED_STATUS, status) &&
                        StringUtils.isEmpty(checkpointInsertedCache.getIfPresent(checkpointCacheKey))) {
                    Timestamp checkpointTriggerTimestamp = new Timestamp(checkpointTrigger);

                    rdosStreamTaskCheckpointDAO.insert(taskId, engineTaskId, checkpointID, checkpointTriggerTimestamp, checkpointSavepath, checkpointCounts);
                    checkpointInsertedCache.put(checkpointCacheKey, "1");  //存在标识

                }
            }
        } catch (IOException e) {
            logger.error("taskID:{} ,engineTaskId:{}, error log:{}\n", taskId, engineTaskId, ExceptionUtil.getErrorMessage(e));
        }


    }

    private void checkpointIntervalClean(String engineTaskId, Map<String, Object> entity, String pluginInfo) {
        if (!checkpointListener.existTaskEngineIdAndRetainedNum(engineTaskId)) {
            Integer checkpointID = MathUtil.getIntegerVal(entity.get(CHECKPOINT_ID_KEY));

            if (null != checkpointID) {
                int retainedNum = checkpointListener.parseRetainedNum(pluginInfo);
                rdosStreamTaskCheckpointDAO.batchDeleteByEngineTaskIdAndCheckpointID(engineTaskId, MathUtil.getString(checkpointID - retainedNum + 1));
            }
        }
    }

    private Object getValueFromStringInfoKey(String info, String key) throws IOException {
        Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(info, Map.class);
        return pluginInfoMap.get(key);
    }

    private void dealBatchJobAfterGetStatus(Integer status, String jobId) throws ExecutionException{
        if(RdosTaskStatus.getStoppedStatus().contains(status)){
            jobStatusFrequency.remove(jobId);
            rdosEngineJobCacheDao.deleteJob(jobId);
        }
    }

    private boolean isSyncTaskAndOpenCheckpoint(String jobId, String engineTypeName, int computeType){
        boolean isSyncTask = computeType == ComputeType.BATCH.getType() && EngineType.isFlink(engineTypeName);
        if(!isSyncTask){
            return false;
        }

        RdosEngineJobCache jobCache = engineJobCacheDAO.getJobById(jobId);
        if(jobCache == null){
            logger.warn("Can not get job cache from db with jobId:[{}]", jobId);
            return false;
        }

        String jobInfo = jobCache.getJobInfo();
        if(StringUtils.isEmpty(jobInfo)){
            logger.warn("The jobInfo is null or empty,jobId is:[{}]", jobId);
            return false;
        }

        try {
            ParamAction paramAction = PublicUtil.jsonStrToObject(jobInfo, ParamAction.class);
            Properties confProperties = PublicUtil.stringToProperties(paramAction.getTaskParams());

            return Boolean.parseBoolean(confProperties.getProperty("openCheckpoint"));
        } catch (Exception e){
            logger.warn("Parse job config error,jobInfo is:[{}]", jobInfo);
            return false;
        }
    }

    private void dealSyncTaskCheckpoint(Integer status, JobIdentifier jobIdentifier, String pluginInfo) throws ExecutionException{
        //运行中的stream任务需要更新checkpoint 并且 控制频率
        Integer checkpointCallNum = checkpointGetTotalNumCache.get(jobIdentifier.getEngineJobId(), () -> 0);
        if(RdosTaskStatus.RUNNING.getStatus().equals(status)){
            if(checkpointCallNum%checkpointGetRate == 0){
                updateBatchTaskCheckpoint(pluginInfo, jobIdentifier);
            }

            checkpointGetTotalNumCache.put(jobIdentifier.getEngineJobId(), checkpointCallNum + 1);
        }

        // 任务成功或者取消后要删除记录的
        if(RdosTaskStatus.FINISHED.getStatus().equals(status) || RdosTaskStatus.CANCELED.getStatus().equals(status)
                || RdosTaskStatus.KILLED.getStatus().equals(status)){
            rdosStreamTaskCheckpointDAO.deleteByTaskId(jobIdentifier.getTaskId());
        }

        if(RdosTaskStatus.FAILED.getStatus().equals(status)){
            updateBatchTaskCheckpoint(pluginInfo, jobIdentifier);
        }
    }

    private void updateBatchTaskCheckpoint(String pluginInfo,JobIdentifier jobIdentifier){
        String lastExternalPath = getLastExternalPath(pluginInfo, jobIdentifier);
        if (StringUtils.isEmpty(lastExternalPath)){
            return;
        }

        logger.info("taskId:{}, external:{}", jobIdentifier.getTaskId(), lastExternalPath);
        RdosStreamTaskCheckpoint taskCheckpoint = rdosStreamTaskCheckpointDAO.getByTaskId(jobIdentifier.getTaskId());
        if(taskCheckpoint == null){
            Timestamp now = new Timestamp(System.currentTimeMillis());
            rdosStreamTaskCheckpointDAO.insert(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(),"", now, lastExternalPath, "");
        } else {
            rdosStreamTaskCheckpointDAO.update(jobIdentifier.getTaskId(), lastExternalPath);
        }
    }

    private String getLastExternalPath(String pluginInfo,JobIdentifier jobIdentifier){
        String checkpointJson = JobClient.getCheckpoints(EngineType.Flink.name(), pluginInfo, jobIdentifier);
        if(StringUtils.isEmpty(checkpointJson)){
            return null;
        }

        try {
            Map cpJson = PublicUtil.jsonStrToObject(checkpointJson, Map.class);
            if(!cpJson.containsKey(FLINK_CP_HISTORY_KEY)){
                return null;
            }

            List<Map<String, Object>> cpList = (List<Map<String, Object>>) cpJson.get(FLINK_CP_HISTORY_KEY);
            if(CollectionUtils.isEmpty(cpList)){
                return null;
            }

            List<String> savepointList = new ArrayList<>();
            for (Map<String, Object> entity : cpList) {
                String checkpointSavePath = String.valueOf(entity.get(CHECKPOINT_SAVEPATH_KEY));
                String status = String.valueOf(entity.get(CHECKPOINT_STATUS_KEY));
                if(!CHECKPOINT_NOT_EXTERNALLY_ADDRESS_KEY.equalsIgnoreCase(checkpointSavePath)
                        && CHECKPOINT_COMPLETED_STATUS.equalsIgnoreCase(status)
                        && StringUtils.isNotEmpty(checkpointSavePath)){
                    savepointList.add(checkpointSavePath);
                }
            }

            if(savepointList.size() > 0){
                savepointList.sort(Comparator.naturalOrder());
                return savepointList.get(savepointList.size() - 1);
            }
        } catch (Exception e){
            logger.warn("Parse completed checkpoint path error, json:[{}]", checkpointJson);
        }

        return null;
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