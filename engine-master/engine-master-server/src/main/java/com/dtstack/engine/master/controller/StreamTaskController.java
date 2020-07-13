package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/streamTask")
@Api(value = "/node/streamTask", tags = {"流任务接口"})
public class StreamTaskController {

    @Autowired
    private StreamTaskController streamTaskController;

    @RequestMapping(value="/getCheckPoint", method = {RequestMethod.POST})
    @ApiOperation(value = "查询checkPoint")
    public List<EngineJobCheckpoint> getCheckPoint(@RequestParam("taskId") String taskId, @RequestParam("triggerStart") Long triggerStart, @RequestParam("triggerEnd") Long triggerEnd) {
        return streamTaskController.getCheckPoint(taskId, triggerStart, triggerEnd);
    }

    @RequestMapping(value="/getByTaskIdAndEngineTaskId", method = {RequestMethod.POST})
    public EngineJobCheckpoint getByTaskIdAndEngineTaskId(@RequestParam("taskId") String taskId, @RequestParam("engineTaskId") String engineTaskId) {
        return streamTaskController.getByTaskIdAndEngineTaskId(taskId, engineTaskId);
    }

    @RequestMapping(value="/getEngineStreamJob", method = {RequestMethod.POST})
    @ApiOperation(value = "查询stream job")
    public List<ScheduleJob> getEngineStreamJob(@RequestParam("taskIds") List<String> taskIds) {
        return streamTaskController.getEngineStreamJob(taskIds);
    }

    @RequestMapping(value="/getTaskIdsByStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取某个状态的任务task_id")
    public List<String> getTaskIdsByStatus(@RequestParam("status") Integer status) {
        return streamTaskController.getTaskIdsByStatus(status);
    }

    @RequestMapping(value="/getTaskStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务的状态")
    public Integer getTaskStatus(@RequestParam("taskId") String taskId) {
        return streamTaskController.getTaskStatus(taskId);
    }

    @RequestMapping(value="/getRunningTaskLogUrl", method = {RequestMethod.POST})
    @ApiOperation(value = "获取实时计算运行中任务的日志URL")
    public Pair<String, String> getRunningTaskLogUrl(@RequestParam("taskId") String taskId) {
        return streamTaskController.getRunningTaskLogUrl(taskId);
    }
}
