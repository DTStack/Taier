package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.master.impl.StreamTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/streamTask")
@Api(value = "/node/streamTask", tags = {"流任务接口"})
public class StreamTaskController {

    @Autowired
    private StreamTaskService streamTaskService;

    @RequestMapping(value="/getCheckPoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询checkPoint")
    public List<EngineJobCheckpoint> getCheckPoint(@DtRequestParam("taskId") String taskId, @DtRequestParam("triggerStart") Long triggerStart, @DtRequestParam("triggerEnd") Long triggerEnd) {
        return streamTaskService.getCheckPoint(taskId, triggerStart, triggerEnd);
    }

    @RequestMapping(value="/getSavePoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询savePoint")
    public EngineJobCheckpoint getSavePoint(@DtRequestParam("taskId") String taskId) {
        return streamTaskService.getSavePoint(taskId);
    }

    @RequestMapping(value="/getByTaskIdAndEngineTaskId", method = {RequestMethod.POST})
    public EngineJobCheckpoint getByTaskIdAndEngineTaskId(@DtRequestParam("taskId") String taskId, @DtRequestParam("engineTaskId") String engineTaskId) {
        return streamTaskService.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    @RequestMapping(value="/getEngineStreamJob", method = {RequestMethod.POST})
    @ApiOperation(value = "查询stream job")
    public List<ScheduleJob> getEngineStreamJob(@DtRequestParam("taskIds") List<String> taskIds) {
        return streamTaskService.getEngineStreamJob(taskIds);
    }

    @RequestMapping(value="/getTaskIdsByStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取某个状态的任务task_id")
    public List<String> getTaskIdsByStatus(@DtRequestParam("status") Integer status) {
        return streamTaskService.getTaskIdsByStatus(status);
    }

    @RequestMapping(value="/getTaskStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态")
    public Integer getTaskStatus(@DtRequestParam("taskId") String taskId) {
        return streamTaskService.getTaskStatus(taskId);
    }

    @RequestMapping(value="/getRunningTaskLogUrl", method = {RequestMethod.POST})
    @ApiOperation(value = "获取实时计算运行中任务的日志URL")
    public List<String> getRunningTaskLogUrl(@DtRequestParam("taskId") String taskId) {
        return streamTaskService.getRunningTaskLogUrl(taskId);
    }
}
