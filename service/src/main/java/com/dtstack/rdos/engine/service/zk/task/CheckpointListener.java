package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
    private Set<String> hasCheckpointPathTaskID = Sets.newConcurrentHashSet();

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private final static int CHECK_INTERVAL = 5;

    //保留最大checkpoint的数量
    private final static int RETAINED_CHECKPOINT_MAX_COUT = 50;

    private String lastMaxCheckpointID = "0";

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
        if (hasCheckpointPathTaskID.isEmpty()) {
            return;
        }
        SubtractionCheckpointRecord();
    }

    public void SubtractionCheckpointRecord() {
        if (!hasCheckpointPathTaskID.isEmpty()) {
            hasCheckpointPathTaskID.stream().forEach(taskid -> SubtractionCheckpointRecord(taskid));
        }
    }

    public void SubtractionCheckpointRecord(String taskEngineID) {
        try {
            List<RdosStreamTaskCheckpoint> retainedCheckpointList = rdosStreamTaskCheckpointDAO.listByTaskIdAndRangeTimeAndMaxCheckpointID(taskEngineID, null, null, lastMaxCheckpointID);

            if (retainedCheckpointList.isEmpty()) {
                return;
            }

            lastMaxCheckpointID = retainedCheckpointList.get(0).getCheckpointID();

            if (retainedCheckpointList.size() <= RETAINED_CHECKPOINT_MAX_COUT) {
                return;
            }

            RdosStreamTaskCheckpoint threshold = retainedCheckpointList.get(RETAINED_CHECKPOINT_MAX_COUT - 1);

            rdosStreamTaskCheckpointDAO.deleteRecordByCheckpointIDAndTaskEngineID(threshold.getTaskEngineId(), threshold.getCheckpointID());
        } catch (Exception e){
            logger.error("checkpoint clean job run error:{}", ExceptionUtil.getErrorMessage(e));
        }

    }

    public Set<String> getHasCheckpointPathTaskID() {
        return hasCheckpointPathTaskID;
    }

}
