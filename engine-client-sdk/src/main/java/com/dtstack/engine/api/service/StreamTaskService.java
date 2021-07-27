package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ParamActionExt;

import java.util.List;

public interface StreamTaskService  {
    /**
     * 查询checkPoint
     */
    List<EngineJobCheckpoint> getCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd);

    /**
     * 查询生成失败的 checkPoint
     */
    @RequestLine("POST /node/streamTask/getFailedCheckPoint")
    List<EngineJobCheckpoint> getFailedCheckPoint(@Param("taskId") String taskId, @Param("triggerStart") Long triggerStart, @Param("triggerEnd") Long triggerEnd);

    /**
     * 查询savepoint
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/streamTask/getSavePoint")
    ApiResponse<EngineJobCheckpoint> getSavePoint(@Param("taskId") String taskId);

    @RequestLine("POST /node/streamTask/getByTaskIdAndEngineTaskId")
    ApiResponse<EngineJobCheckpoint> getByTaskIdAndEngineTaskId(@Param("taskId") String taskId, @Param("engineTaskId") String engineTaskId);

    /**
     * 查询stream job
     */
    @RequestLine("POST /node/streamTask/getEngineStreamJob")
    ApiResponse<List<ScheduleJob>> getEngineStreamJob(@Param("taskIds") List<String> taskIds);

    /**
     * 获取某个状态的任务task_id
     */
    @RequestLine("POST /node/streamTask/getTaskIdsByStatus")
    ApiResponse<List<String>> getTaskIdsByStatus(@Param("status") Integer status);

    /**
     * 获取任务的状态
     */
    @RequestLine("POST /node/streamTask/getTaskStatus")
    ApiResponse<Integer> getTaskStatus(@Param("taskId") String taskId);

    /**
     * 获取实时计算运行中任务的日志URL
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/streamTask/getRunningTaskLogUrl")
    ApiResponse<List<String>> getRunningTaskLogUrl(@Param("taskId") String taskId);

    /**
     * 语法检测
     *
     * @param paramActionExt
     * @return
     */
    @RequestLine("POST /node/streamTask/grammarCheck")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<CheckResult> grammarCheck(ParamActionExt paramActionExt);


}
