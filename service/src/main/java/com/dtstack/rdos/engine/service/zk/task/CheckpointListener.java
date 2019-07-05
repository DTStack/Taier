package com.dtstack.rdos.engine.service.zk.task;

import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.service.db.dao.RdosStreamTaskCheckpointDAO;
import com.dtstack.rdos.engine.service.db.dataobject.RdosStreamTaskCheckpoint;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时清理DB中的checkpoint
 */
public class CheckpointListener implements Runnable {

    /**
     * 存在checkpoint 外部存储路径的taskid
     */
    private Set<String> hasCheckpointPathTaskID = Sets.newConcurrentHashSet();

    private RdosStreamTaskCheckpointDAO rdosStreamTaskCheckpointDAO = new RdosStreamTaskCheckpointDAO();

    private final static int CHECK_INTERVAL = 5;

    //保留最大checkpoint的数量
    private final static int RETAINED_CHECKPOINT_MAX_COUT = 50;

    public CheckpointListener() {
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

    public void SubtractionCheckpointRecord(String taskId) {
        List<RdosStreamTaskCheckpoint> retainedCheckpointList = rdosStreamTaskCheckpointDAO.listByTaskIdAndRangeTime(taskId, null, null);
        if (retainedCheckpointList.size() <= RETAINED_CHECKPOINT_MAX_COUT) {
            return;
        }
        RdosStreamTaskCheckpoint threshold = retainedCheckpointList.get(RETAINED_CHECKPOINT_MAX_COUT - 1);
        rdosStreamTaskCheckpointDAO.deleteRecordByCheckpointIDAndTaskID(threshold.getTaskId());

    }

    public Set<String> getHasCheckpointPathTaskID() {
        return hasCheckpointPathTaskID;
    }

}
