package com.dtstack.engine.service.task;

import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.service.db.dao.RdosEngineJobCacheDAO;
import com.dtstack.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.engine.service.db.dataobject.RdosEngineJobCache;
import com.dtstack.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时清理DB中的checkpoint
 */
public class CheckpointListener implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CheckpointListener.class);

    private final static String CHECKPOINT_RETAINED_KEY = "state.checkpoints.num-retained";

    private final static int CHECK_INTERVAL = 1;

    private final static String CHECKPOINT_ID_KEY = "id";

    private final static String CHECKPOINT_SAVEPATH_KEY = "external_path";

    private final static String TRIGGER_TIMESTAMP_KEY = "trigger_timestamp";

    private static final String TASK_PARAMS_KEY = "taskParams";

    private static final char SEPARATOR = '_';

    /** 开启checkpoint，但未绑定外部存储路径*/
    private final static String CHECKPOINT_NOT_EXTERNALLY_ADDRESS_KEY = "<checkpoint-not-externally-addressable>";

    private static final String CHECKPOINT_STATUS_KEY = "status";

    private static final String CHECKPOINT_COMPLETED_STATUS = "COMPLETED";

    private final static String FLINK_CP_HISTORY_KEY = "history";

    private final static String FLINK_CP_COUNTS_KEY = "counts";

    public static final String SQL_CHECKPOINT_INTERVAL_KEY = "sql.checkpoint.interval";
    public static final String FLINK_CHECKPOINT_INTERVAL_KEY = "flink.checkpoint.interval";

    public static final String SQL_CHECKPOINT_CLEANUP_MODE_KEY = "sql.checkpoint.cleanup.mode";
    public static final String FLINK_CHECKPOINT_CLEANUP_MODE_KEY = "flink.checkpoint.cleanup.mode";

    public static final int JOB_CHECKPOINT_CONFIG = 50;

    /** 已经插入到db的checkpoint，其id缓存数量*/
    public static final long CHECKPOINT_INSERTED_RECORD = 200;

    //每隔5次状态获取之后更新一次checkpoint 信息 ===>checkpoint信息没必要那么频繁更新
    public static int CHECKPOINT_GET_RATE = 10;

    /**
     * 存在checkpoint 外部存储路径的taskid
     */
    private Map<String, Integer> taskEngineIdAndRetainedNum = Maps.newConcurrentMap();

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private RdosEngineJobCacheDAO engineJobCacheDAO = new RdosEngineJobCacheDAO();

    private RdosEngineJobCacheDAO rdosEngineJobCacheDao = new RdosEngineJobCacheDAO();

    private Cache<String, Integer> checkpointGetTotalNumCache = CacheBuilder.newBuilder().expireAfterWrite(60 * 60, TimeUnit.SECONDS).build();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    private Cache<String, Map<String, Object>> checkpointConfigCache = CacheBuilder.newBuilder().maximumSize(JOB_CHECKPOINT_CONFIG).build();

    private ScheduledExecutorService checkpointCleanPoll = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("checkpointCleaner"));

    private MasterListener masterListener;

    public CheckpointListener(MasterListener masterListener){
        this.masterListener = masterListener;
        checkpointCleanPoll.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        if(masterListener.isMaster()){
            if (taskEngineIdAndRetainedNum.isEmpty()) {
                return;
            }
            SubtractionCheckpointRecord();
        }
    }

    public void SubtractionCheckpointRecord() {
        if (!taskEngineIdAndRetainedNum.isEmpty()) {
            taskEngineIdAndRetainedNum.forEach((taskEngineID, retainedNum) -> SubtractionCheckpointRecord(taskEngineID));
        }
    }

    public void SubtractionCheckpointRecord(String taskEngineID) {
        try {

            int retainedNum = taskEngineIdAndRetainedNum.getOrDefault(taskEngineID, 1);
            List<RdosStreamTaskCheckpoint> threshold = rdosStreamTaskCheckpointDAO.getByTaskEngineIDAndCheckpointIndexAndCount(taskEngineID,retainedNum-1, 1);

            if (threshold.isEmpty()) {
                return;
            }
            RdosStreamTaskCheckpoint thresholdCheckpoint = threshold.get(0);
            rdosStreamTaskCheckpointDAO.batchDeleteByEngineTaskIdAndCheckpointID(thresholdCheckpoint.getTaskEngineId(), thresholdCheckpoint.getCheckpointID());
        } catch (Exception e){
            logger.error("taskEngineID Id :{}", taskEngineID);
            logger.error("", e);
        }

    }

    public void  cleanAllCheckpointByTaskEngineId(String taskEngineID) {
        rdosStreamTaskCheckpointDAO.cleanAllCheckpointByTaskEngineId(taskEngineID);
    }

    public void putTaskEngineIdAndRetainedNum(String taskEngineID, String pluginInfo) {
        int retainedNum = parseRetainedNum(pluginInfo);
        taskEngineIdAndRetainedNum.put(taskEngineID, retainedNum);

    }

    public int parseRetainedNum(String pluginInfo) {
        Map<String, Object> pluginInfoMap = null;
        try {
            pluginInfoMap = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        } catch (IOException e) {
            logger.error("plugin info parse error ..", e);
        }
        return Integer.valueOf(pluginInfoMap.getOrDefault(CHECKPOINT_RETAINED_KEY, 1).toString());
    }

    public boolean existTaskEngineIdAndRetainedNum(String taskEngineID) {
        return taskEngineIdAndRetainedNum.containsKey(taskEngineID);
    }

    public void removeByTaskEngineId(String taskEngineId) {
        taskEngineIdAndRetainedNum.remove(taskEngineId);
    }

    public void dealStreamCheckpoint(FailedTaskInfo failedTaskInfo) {
        try {
            JobIdentifier jobIdentifier = failedTaskInfo.getJobIdentifier();
            if (null != jobIdentifier && StringUtils.isNotBlank(jobIdentifier.getEngineJobId())) {
                if (checkOpenCheckPoint(failedTaskInfo.getJobId())) {
                    Boolean sqlCleanMode = MathUtil.getBoolean(getParmaFromJobCache(failedTaskInfo.getJobId(), CheckpointListener.SQL_CHECKPOINT_CLEANUP_MODE_KEY), false);
                    Boolean flinkCleanMode = MathUtil.getBoolean(getParmaFromJobCache(failedTaskInfo.getJobId(), CheckpointListener.FLINK_CHECKPOINT_CLEANUP_MODE_KEY), false);
                    if (sqlCleanMode || flinkCleanMode ) {
                        // true then remove all
                        cleanAllCheckpointByTaskEngineId(jobIdentifier.getEngineJobId());
                    } else {
                        //主动清理超过范围的checkpoint
                        SubtractionCheckpointRecord(jobIdentifier.getEngineJobId());
                    }
                    //集合中移除该任务
                    removeByTaskEngineId(jobIdentifier.getEngineJobId());
                    checkpointConfigCache.invalidate(failedTaskInfo.getJobId());
                }
            }
        } catch (Exception e) {
            logger.error("deal stream checkpoint error: {}", ExceptionUtil.getErrorMessage(e));
        }

    }

    public boolean checkOpenCheckPoint(String jobId) throws ExecutionException {
        long sqlCheckpointInterval = MathUtil.getLongVal(getParmaFromJobCache(jobId, SQL_CHECKPOINT_INTERVAL_KEY), 0L);
        long flinkCheckpointInterval = MathUtil.getLongVal(getParmaFromJobCache(jobId, FLINK_CHECKPOINT_INTERVAL_KEY), 0L);
        long checkpointInterval = Math.max(sqlCheckpointInterval, flinkCheckpointInterval);
        if (checkpointInterval <= 0){
            return false;
        }
        return true;
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


    public void updateBatchTaskCheckpoint(String pluginInfo,JobIdentifier jobIdentifier){
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

    public void dealSyncTaskCheckpoint(Integer status, JobIdentifier jobIdentifier, String pluginInfo) throws ExecutionException{
        //运行中的stream任务需要更新checkpoint 并且 控制频率
        Integer checkpointCallNum = checkpointGetTotalNumCache.get(jobIdentifier.getEngineJobId(), () -> 0);
        if(RdosTaskStatus.RUNNING.getStatus().equals(status)){
            if(checkpointCallNum % CHECKPOINT_GET_RATE == 0){
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

    public boolean isSyncTaskAndOpenCheckpoint(String jobId, String engineTypeName, int computeType){
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

            putTaskEngineIdAndRetainedNum(engineTaskId, pluginInfo);

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
        if (!existTaskEngineIdAndRetainedNum(engineTaskId)) {
            Integer checkpointID = MathUtil.getIntegerVal(entity.get(CHECKPOINT_ID_KEY));

            if (null != checkpointID) {
                int retainedNum = parseRetainedNum(pluginInfo);
                rdosStreamTaskCheckpointDAO.batchDeleteByEngineTaskIdAndCheckpointID(engineTaskId, MathUtil.getString(checkpointID - retainedNum + 1));
            }
        }
    }


    public void updateStreamJobCheckpoints(JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo){
        String checkPointJsonStr = JobClient.getCheckpoints(engineTypeName, pluginInfo, jobIdentifier);
        updateStreamJobCheckPoint(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId(), checkPointJsonStr, pluginInfo);
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

    private Object getValueFromStringInfoKey(String info, String key) throws IOException {
        Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(info, Map.class);
        return pluginInfoMap.get(key);
    }

    public Integer getCheckpointCallNum(String engineTaskId) throws ExecutionException {
        return checkpointGetTotalNumCache.get(engineTaskId, () -> 0);
    }

    public void addCheckpointCallNum(String engineTaskId, int num) {

    }
}
