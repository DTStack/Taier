package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import org.apache.commons.math3.util.Pair;

import java.util.List;

public interface StreamTaskService {
    /**
     * 查询checkPoint
     */
    public List<EngineJobCheckpoint> getCheckPoint( String taskId,  Long triggerStart,  Long triggerEnd);

    public EngineJobCheckpoint getByTaskIdAndEngineTaskId( String taskId,  String engineTaskId);

    /**
     * 查询stream job
     */
    public List<ScheduleJob> getEngineStreamJob( List<String> taskIds);

    /**
     * 获取某个状态的任务task_id
     */
    public List<String> getTaskIdsByStatus( Integer status);

    /**
     * 获取任务的状态
     */
    public Integer getTaskStatus( String taskId);

    /**
     * 获取实时计算运行中任务的日志URL
     * @param taskId
     * @return
     */
    public Pair<String, String> getRunningTaskLogUrl( String taskId);


}
