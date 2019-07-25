package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时清理DB中的checkpoint
 */
public class CheckpointListener implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(CheckpointListener.class);
    /**
     * 存在checkpoint 外部存储路径的taskid
     */
    private Map<String, Integer> taskEngineIdAndRetainedNum = Maps.newConcurrentMap();

    public final static String CHECKPOINT_RETAINED_KEY = "state.checkpoints.num-retained";

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private final static int CHECK_INTERVAL = 1;

    public void startCheckpointScheduled() {
        ScheduledExecutorService checkpointCleanPoll = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("checkpointCleaner"));
        checkpointCleanPoll.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        if (taskEngineIdAndRetainedNum.isEmpty()) {
            return;
        }
        SubtractionCheckpointRecord();
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
            rdosStreamTaskCheckpointDAO.deleteRecordByCheckpointIDAndTaskEngineID(thresholdCheckpoint.getTaskEngineId(), thresholdCheckpoint.getCheckpointID());
        } catch (Exception e){
            logger.error("taskEngineID Id :{}", taskEngineID);
            logger.error("", e);
        }

    }

    public void  cleanAllCheckpointByTaskEngineId(String taskEngineID) {
        rdosStreamTaskCheckpointDAO.cleanAllCheckpointByTaskEngineId(taskEngineID);
    }

    public void putTaskEngineIdAndRetainedNum(String taskEngineID, String pluginInfo) {
        try {
            Map<String, Object> pluginInfoMap = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
            int retainedNum = Integer.valueOf(pluginInfoMap.getOrDefault(CHECKPOINT_RETAINED_KEY, 1).toString());
            taskEngineIdAndRetainedNum.put(taskEngineID, retainedNum);
        } catch (IOException e) {
            logger.error("taskEngineID Id :{}", taskEngineID);
            logger.error("", e);
        }
    }

    public void removeByTaskEngineId(String taskEngineId) {
        taskEngineIdAndRetainedNum.remove(taskEngineId);
    }

}
