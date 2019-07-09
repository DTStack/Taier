package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            int retainedNum = taskEngineIdAndRetainedNum.get(taskEngineID);
            List<RdosStreamTaskCheckpoint> threshold = rdosStreamTaskCheckpointDAO.getByTaskEngineIDAndCheckpointIndexAndCount(taskEngineID,retainedNum-1, 1);

            if (threshold.isEmpty()) {
                return;
            }
            RdosStreamTaskCheckpoint thresholdCheckpoint = threshold.get(0);
            rdosStreamTaskCheckpointDAO.deleteRecordByCheckpointIDAndTaskEngineID(thresholdCheckpoint.getTaskEngineId(), thresholdCheckpoint.getCheckpointID());
        } catch (Exception e){
            logger.error("checkpoint clean job run error:{}", ExceptionUtil.getErrorMessage(e));
        }

    }

    public void  cleanAllCheckpointByTaskEngineId(String taskEngineID) {
        rdosStreamTaskCheckpointDAO.cleanAllCheckpointByTaskEngineId(taskEngineID);
    }

    public void putTaskEngineIdAndRetainedNum(String engineTaskId, int retainedNum) {
        taskEngineIdAndRetainedNum.put(engineTaskId, retainedNum);
    }

    public void removeByTaskEngineId(String taskEngineId) {
        taskEngineIdAndRetainedNum.remove(taskEngineId);
    }

}
