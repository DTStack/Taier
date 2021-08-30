package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.bo.JobCheckpointInfo;
import com.dtstack.engine.master.impl.ClusterService;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 *  checkpoint管理
 * @author maqi
 */
@Component
public class JobCheckpointDealer implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(JobCheckpointDealer.class);

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

    @Autowired
    private ClusterService clusterService;

    private Map<String,String> queuePutRecord = new ConcurrentHashMap<>();

    private Cache<String, String> checkpointInsertedCache = CacheBuilder.newBuilder().maximumSize(CHECKPOINT_INSERTED_RECORD).build();

    private Cache<String, Map<String, Object>> checkpointConfigCache = CacheBuilder.newBuilder().maximumSize(JOB_CHECKPOINT_CONFIG).build();

    private DelayBlockingQueue<JobCheckpointInfo> delayBlockingQueue = new DelayBlockingQueue<>(1000);

    private ExecutorService checkpointPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()));


    @Override
    public void afterPropertiesSet() {
        checkpointPool.submit(() -> {
            while (true) {
                String taskId = "";
                String engineJobId = "";
                try {
                    JobCheckpointInfo taskInfo = delayBlockingQueue.take();
                    if (null != taskInfo.getJobIdentifier()) {
                        engineJobId = taskInfo.getJobIdentifier().getEngineJobId();
                    }
                    taskId = taskInfo.getTaskId();
                    String recordEngineJobId = queuePutRecord.get(taskId);
                    if(StringUtils.isBlank(recordEngineJobId) || !recordEngineJobId.equalsIgnoreCase(engineJobId)){
                        logger.warn("delay queue jobId:{} engineJobId :{} is not same to record:{} so skip", taskId, engineJobId, recordEngineJobId);
                        continue;
                    }
                    Integer status = scheduleJobDao.getStatusByJobId(taskId);
                    updateCheckpointImmediately(taskInfo, engineJobId, status);

                } catch (Exception e) {
                    logger.error("update delay checkpoint jobId {}  engineJobId {} error ", taskId, engineJobId, e);
                    if (StringUtils.isNotBlank(taskId)) {
                        queuePutRecord.remove(taskId);
                    }
                }
            }
        });
    }

    /**
     *  获取最新并移除无效checkpoint
     * @param taskInfo
     * @param engineJobId
     */
    public void updateCheckpointImmediately(JobCheckpointInfo taskInfo, String engineJobId, Integer status) {
        String taskId = "";
        try {
            taskId = taskInfo.getJobIdentifier().getTaskId();
            if (getCheckpointInterval(taskId) > 0 || RdosTaskStatus.getStoppedStatus().contains(status)) {
                updateJobCheckpoints(taskInfo.getJobIdentifier());
                subtractionCheckpointRecord(engineJobId, taskId);

                if (RdosTaskStatus.RUNNING.getStatus().equals(status)) {
                    taskInfo.refreshExpired();
                    delayBlockingQueue.put(taskInfo);
                }

                if (RdosTaskStatus.getStoppedStatus().contains(status)) {
                    boolean checkpointStopClean = isCheckpointStopClean(taskId);
                    logger.info(" taskId {}  status is stop {}  cleanCheckpoint {}", taskId, status, checkpointStopClean);
                    if (checkpointStopClean) {
                        engineJobCheckpointDao.cleanAllCheckpointByTaskEngineId(engineJobId);
                    }
                    taskEngineIdAndRetainedNum.remove(engineJobId);
                    checkpointConfigCache.invalidate(taskId);
                    queuePutRecord.remove(taskId);
                }
            }
        } catch (Exception e) {
            logger.error(" taskId {}  engineJobId {}  updateCheckpointImmediately error", taskId, engineJobId, e);
        }
    }

    public void updateJobCheckpoints(JobIdentifier jobIdentifier) {
        String checkpointJsonStr = workerOperator.getCheckpoints(jobIdentifier);
        String engineTaskId = jobIdentifier.getEngineJobId();
        String taskId = jobIdentifier.getTaskId();

        if (Strings.isNullOrEmpty(checkpointJsonStr)) {
            logger.info("taskId {} engineTaskId {} can't get checkpoint info.", taskId, engineTaskId);
            return;
        }
        try {
            Map<String, Object> checkpointInfo = PublicUtil.jsonStrToObject(checkpointJsonStr, Map.class);
            if (!checkpointInfo.containsKey(FLINK_CP_HISTORY_KEY) || null == checkpointInfo.get(FLINK_CP_HISTORY_KEY)) {
                logger.info("taskId {} engineTaskId {} can't get checkpoint history key....", taskId, engineTaskId);
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
                } else {
                    logger.info("no add checkpoint to db checkpointId [{}]  checkpointSavePath {} status {} checkpointCacheKey {}",
                            checkpointId, checkpointSavePath, status, checkpointCacheKey);
                }
            }
        } catch (Exception e) {
            engineJobCheckpointDao.insert(taskId, engineTaskId, null, null, null, null);
            logger.error("taskID:{} ,engineTaskId:{}, error:", taskId, engineTaskId, e);
        }
    }


    public void addCheckpointTaskForQueue(Integer computeType, String taskId, JobIdentifier jobIdentifier, String engineTypeName) throws ExecutionException {
        long checkpointInterval = getCheckpointInterval(taskId);
        boolean canPutQueue = !queuePutRecord.containsKey(taskId);
        if (queuePutRecord.containsKey(taskId) && !queuePutRecord.get(taskId).equalsIgnoreCase(jobIdentifier.getEngineJobId())) {
            //jobId flink are not same
            canPutQueue = true;
        }
        if (checkpointInterval > 0 && canPutQueue) {
            //queuePutRecord去重 保证队列中taskId唯一 后续通过refreshExpired来间隔获取
            try {
                String pluginInfo = clusterService.pluginInfoJSON(jobIdentifier.getTenantId(),
                        jobIdentifier.getEngineType(), jobIdentifier.getUserId(), jobIdentifier.getDeployMode()).toJSONString();
                int retainedNum = getRetainedNumFromPluginInfo(pluginInfo);
                taskEngineIdAndRetainedNum.put(jobIdentifier.getEngineJobId(), retainedNum);

                JobCheckpointInfo taskInfo = new JobCheckpointInfo(computeType, taskId, jobIdentifier, engineTypeName, checkpointInterval);

                delayBlockingQueue.put(taskInfo);
                queuePutRecord.put(taskId, jobIdentifier.getEngineJobId());
                logger.info("add taskId {} to checkpoint delay queue,{}", taskId, taskInfo);
            } catch (Exception e) {
                logger.error("taskId {} addCheckpointTaskForQueue error ", taskId, e);
            }
        }

    }

    /**
     *  根据retainedNum数量清理超期checkpoint
     * @param engineJobId flink job id
     */
    public void subtractionCheckpointRecord(String engineJobId,String taskId) {
        try {
            int retainedNum = taskEngineIdAndRetainedNum.getOrDefault(engineJobId, 1);
            List<EngineJobCheckpoint> threshold = engineJobCheckpointDao.getByTaskEngineIdAndCheckpointIndexAndCount(engineJobId, taskId,retainedNum - 1, 1);
            if (!threshold.isEmpty()) {
                EngineJobCheckpoint thresholdCheckpoint = threshold.get(0);
                engineJobCheckpointDao.batchDeleteByEngineTaskIdAndCheckpointId(thresholdCheckpoint.getTaskEngineId(), thresholdCheckpoint.getCheckpointId());
            }
        } catch (Exception e) {
            logger.error("engineJobId Id :{}  error ", engineJobId, e);
        }
    }

    /**
     *   获取任务对应的环境配置信息
     * @param jobId
     * @return
     * @throws ExecutionException
     */
    private Map<String, Object> getJobParamsByJobId(String jobId) throws ExecutionException{
        return checkpointConfigCache.get(jobId, () -> {
            Map<String, Object> paramInfo = Maps.newConcurrentMap();
            String jobInfo = engineJobCacheDao.getOne(jobId).getJobInfo();
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(jobInfo, Map.class);
            String taskParamsStr = String.valueOf(pluginInfoMap.get(TASK_PARAMS_KEY));

            if (StringUtils.isNotEmpty(taskParamsStr)) {
                paramInfo = Arrays.stream(taskParamsStr.split("\n"))
                        .map(param -> param.split("="))
                        .filter(paramKv -> paramKv.length > 1)
                        .collect(Collectors.toConcurrentMap((kv) -> kv[0].trim(), (kv) -> kv[1].trim(), (oldValue, newValue) -> newValue));
            }
            return paramInfo;
        });
    }


    private int getRetainedNumFromPluginInfo(String pluginInfo) {
        Map<String, Object> pluginInfoMap = null;
        try {
            pluginInfoMap = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
        } catch (IOException e) {
            logger.error("plugin info {}  parse error ..", pluginInfo, e);
        }
        if (MapUtils.isEmpty(pluginInfoMap)) {
            return 1;
        }

        return Integer.parseInt(pluginInfoMap.getOrDefault(CHECKPOINT_RETAINED_KEY, 1).toString());
    }


    private boolean isCheckpointStopClean(String jobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(jobId);
        if (null == params) {
            return false;
        }
        Boolean sqlCleanMode = MathUtil.getBoolean(params.get(SQL_CHECKPOINT_CLEANUP_MODE_KEY), false);
        Boolean flinkCleanMode = MathUtil.getBoolean(params.get(FLINK_CHECKPOINT_CLEANUP_MODE_KEY), false);
        return sqlCleanMode || flinkCleanMode;
    }

    public long getCheckpointInterval(String jobId) throws ExecutionException {
        Map<String, Object> params = getJobParamsByJobId(jobId);
        if (null == params) {
            return 0L;
        }
        long sqlCheckpointInterval = MathUtil.getLongVal(params.get(SQL_CHECKPOINT_INTERVAL_KEY), 0L);
        long flinkCheckpointInterval = MathUtil.getLongVal(params.get(FLINK_CHECKPOINT_INTERVAL_KEY), 0L);
        return Math.max(sqlCheckpointInterval, flinkCheckpointInterval);
    }

}
