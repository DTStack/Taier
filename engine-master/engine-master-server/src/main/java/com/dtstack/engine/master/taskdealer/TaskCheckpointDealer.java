package com.dtstack.engine.master.taskdealer;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.ParamAction;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.api.domain.EngineJobCache;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.FailedTaskInfo;
import com.dtstack.engine.master.bo.TaskCheckpointInfo;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 定时清理DB中的checkpoint
 */
@Component
public class TaskCheckpointDealer implements InitializingBean, Runnable {

    private static Logger logger = LoggerFactory.getLogger(TaskCheckpointDealer.class);

    private final static String CHECKPOINT_RETAINED_KEY = "state.checkpoints.num-retained";

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

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired
    private EngineJobCacheDao engineJobCacheDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private WorkerOperator workerOperator;

    private Map<String, TaskCheckpointInfo> checkpointJobMap = Maps.newHashMap();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    private Cache<String, Map<String, Object>> checkpointConfigCache = CacheBuilder.newBuilder().maximumSize(JOB_CHECKPOINT_CONFIG).build();

    private DelayBlockingQueue<TaskCheckpointInfo> delayBlockingQueue = new DelayBlockingQueue<>(1000);

    private ExecutorService checkpointPool = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(true), new CustomThreadFactory(this.getClass().getSimpleName()));


    @Override
    public void afterPropertiesSet() throws Exception {
        checkpointPool.execute(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                TaskCheckpointInfo taskInfo = delayBlockingQueue.take();
                String engineJobId = taskInfo.getJobIdentifier().getEngineJobId();

                updateStreamJobCheckpoints(taskInfo.getJobIdentifier(), taskInfo.getEngineTypeName(), taskInfo.getPluginInfo());
                subtractionCheckpointRecord(engineJobId);

                ScheduleJob jobInfo = scheduleJobDao.getByJobId(taskInfo.getJobIdentifier().getEngineJobId(), 0);
                int status = jobInfo.getStatus().intValue();
                if (RdosTaskStatus.RUNNING.getStatus().equals(status)) {
                    taskInfo.refreshExpired();
                    delayBlockingQueue.put(taskInfo);
                }

                if (RdosTaskStatus.FAILED.getStatus().equals(status) || RdosTaskStatus.STOP_STATUS.contains(status)){
                    if (isCheckpointStopClean(engineJobId)) {
                        engineJobCheckpointDao.cleanAllCheckpointByTaskEngineId(engineJobId);
                    }
                    taskEngineIdAndRetainedNum.remove(engineJobId);
                    checkpointConfigCache.invalidate(taskInfo.getTaskId());
                }
            } catch (Exception e) {
                logger.error("", e);
            }
        }
    }

    public void updateStreamJobCheckpoints(JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo) {
        String checkpointJsonStr = workerOperator.getCheckpoints(engineTypeName, pluginInfo, jobIdentifier);
        String engineTaskId = jobIdentifier.getEngineJobId();
        String taskId = jobIdentifier.getTaskId();

        if (Strings.isNullOrEmpty(checkpointJsonStr)) {
            logger.info("taskId {} engineTaskId {} can't get checkpoint info.", taskId, engineTaskId);
            return;
        }
        try {
            Map<String, Object> checkpointInfo = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if (!checkpointInfo.containsKey(FLINK_CP_HISTORY_KEY) || null == checkpointInfo.get(FLINK_CP_HISTORY_KEY)) {
                return;
            }

            List<Map<String, Object>> checkpointHistoryInfo = (List<Map<String, Object>>) checkpointInfo.get(FLINK_CP_HISTORY_KEY);
            String checkpointCountsInfo = PublicUtil.objToString(checkpointInfo.get(FLINK_CP_COUNTS_KEY));

            for (Map<String, Object> entity : checkpointHistoryInfo) {
                String checkpointId = String.valueOf(entity.get(CHECKPOINT_ID_KEY));
                Long checkpointTrigger = MathUtil.getLongVal(entity.get(TRIGGER_TIMESTAMP_KEY));
                String checkpointSavePath = String.valueOf(entity.get(CHECKPOINT_SAVEPATH_KEY));
                String status = String.valueOf(entity.get(CHECKPOINT_STATUS_KEY));
                String checkpointCacheKey = engineTaskId + SEPARATOR + checkpointId;

                if (!StringUtils.equalsIgnoreCase(CHECKPOINT_NOT_EXTERNALLY_ADDRESS_KEY, checkpointSavePath)
                        && StringUtils.equalsIgnoreCase(CHECKPOINT_COMPLETED_STATUS, status)
                        && StringUtils.isEmpty(checkpointInsertedCache.getIfPresent(checkpointCacheKey))) {

                    Timestamp checkpointTriggerTimestamp = new Timestamp(checkpointTrigger);
                    engineJobCheckpointDao.insert(taskId, engineTaskId, checkpointId, checkpointTriggerTimestamp, checkpointSavePath, checkpointCountsInfo);
                    checkpointInsertedCache.put(checkpointCacheKey, "1");
                }
            }
        } catch (IOException e) {
            logger.error("taskID:{} ,engineTaskId:{}, error log:{}\n", taskId, engineTaskId, ExceptionUtil.getErrorMessage(e));
        }
    }


    public void addCheckpointTaskForQueue(Integer computeType, String taskId, JobIdentifier jobIdentifier, String engineTypeName, String pluginInfo) {
        TaskCheckpointInfo taskCheckpointInfo = checkpointJobMap.computeIfAbsent(taskId, (info) -> {
            try {
                int retainedNum = parseRetainedNumFromPluginInfo(pluginInfo);
                taskEngineIdAndRetainedNum.put(jobIdentifier.getEngineJobId(), retainedNum);

                long checkpointInterval = getCheckpointInterval(taskId);
                TaskCheckpointInfo taskInfo = new TaskCheckpointInfo(computeType, taskId, jobIdentifier, engineTypeName, pluginInfo, checkpointInterval);

                delayBlockingQueue.put(taskInfo);
                return taskInfo;
            } catch (Exception e) {
                logger.error("", e);
            }
            return null;
        });

        logger.warn("add task to checkpoint delay queue,{}", taskCheckpointInfo);
    }

    /**
     *  根据retainedNum数量清理超期checkpoint
     * @param taskEngineId
     */
    public void subtractionCheckpointRecord(String taskEngineId) {
        try {
            int retainedNum = taskEngineIdAndRetainedNum.getOrDefault(taskEngineId, 1);
            List<EngineJobCheckpoint> threshold = engineJobCheckpointDao.getByTaskEngineIdAndCheckpointIndexAndCount(taskEngineId, retainedNum - 1, 1);
            if (!threshold.isEmpty()) {
                EngineJobCheckpoint thresholdCheckpoint = threshold.get(0);
                engineJobCheckpointDao.batchDeleteByEngineTaskIdAndCheckpointId(thresholdCheckpoint.getTaskEngineId(), thresholdCheckpoint.getCheckpointId());
            }
        } catch (Exception e) {
            logger.error("taskEngineID Id :{}", taskEngineId);
            logger.error("", e);
        }
    }

    /**
     *   获取任务对应的环境配置信息
     * @param jobId
     * @return
     * @throws ExecutionException
     */
    private Map getJobParamsByJobId(String jobId) throws ExecutionException {
        Map<String, Object> taskParams = checkpointConfigCache.get(jobId, () -> {
            Map<String, Object> paramInfo = Maps.newConcurrentMap();
            String jobInfo = engineJobCacheDao.getOne(jobId).getJobInfo();
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
            String taskParamsStr = String.valueOf(pluginInfoMap.get(TASK_PARAMS_KEY));

            if (StringUtils.isNotEmpty(taskParamsStr)) {
                paramInfo = Arrays.stream(taskParamsStr.split("\n"))
                        .map(param -> param.split("="))
                        .filter(paramKv -> paramKv.length > 1)
                        .collect(Collectors.toMap((kv) -> kv[0].trim(), (kv) -> kv[1].trim()));
            }
            return paramInfo;
        });

        return taskParams;
    }


    public int parseRetainedNumFromPluginInfo(String pluginInfo) {
        Map<String, Object> pluginInfoMap = null;
        try {
            pluginInfoMap = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        } catch (IOException e) {
            logger.error("plugin info parse error ..", e);
        }
        return Integer.valueOf(pluginInfoMap.getOrDefault(CHECKPOINT_RETAINED_KEY, 1).toString());
    }


    public boolean isCheckpointStopClean(String engineJobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(engineJobId);
        Boolean sqlCleanMode = MathUtil.getBoolean(params.get(SQL_CHECKPOINT_CLEANUP_MODE_KEY), false);
        Boolean flinkCleanMode = MathUtil.getBoolean(params.get(FLINK_CHECKPOINT_CLEANUP_MODE_KEY), false);
        return sqlCleanMode || flinkCleanMode;
    }

    public long getCheckpointInterval(String jobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(jobId);
        long sqlCheckpointInterval = MathUtil.getLongVal(params.get(SQL_CHECKPOINT_INTERVAL_KEY), 0L);
        long flinkCheckpointInterval = MathUtil.getLongVal(params.get(FLINK_CHECKPOINT_INTERVAL_KEY), 0L);
        return Math.max(sqlCheckpointInterval, flinkCheckpointInterval);
    }

}
