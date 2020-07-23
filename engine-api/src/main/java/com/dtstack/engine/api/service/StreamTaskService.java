package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;
import org.apache.commons.math3.util.Pair;

import java.util.List;

public interface StreamTaskService extends DtInsightServer {
    /**
     * 查询checkPoint
     */
    @RequestLine("POST /node/streamTask/getCheckPoint")
    List<EngineJobCheckpoint> getCheckPoint(String taskId, Long triggerStart, Long triggerEnd);

    @RequestLine("POST /node/streamTask/getByTaskIdAndEngineTaskId")
    EngineJobCheckpoint getByTaskIdAndEngineTaskId(String taskId, String engineTaskId);

    /**
     * 查询stream job
     */
    @RequestLine("POST /node/streamTask/getEngineStreamJob")
    List<ScheduleJob> getEngineStreamJob(List<String> taskIds);

    /**
     * 获取某个状态的任务task_id
     */
    @RequestLine("POST /node/streamTask/getTaskIdsByStatus")
    List<String> getTaskIdsByStatus(Integer status);

    /**
     * 获取任务的状态
     */
    @RequestLine("POST /node/streamTask/getTaskStatus")
    Integer getTaskStatus(String taskId);

    /**
     * 获取实时计算运行中任务的日志URL
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/streamTask/getRunningTaskLogUrl")
    Pair<String, String> getRunningTaskLogUrl(String taskId);


}
