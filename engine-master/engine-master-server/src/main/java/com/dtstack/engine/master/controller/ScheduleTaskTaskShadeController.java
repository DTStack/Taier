package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.master.impl.ScheduleTaskTaskShadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/node/scheduleTaskTaskShade")
@Api(value = "/node/scheduleTaskTaskShade", tags = {"任务依赖接口"})
public class ScheduleTaskTaskShadeController {

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @RequestMapping(value="/clearDataByTaskId", method = {RequestMethod.POST})
    public void clearDataByTaskId(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType")Integer appType) {
        scheduleTaskTaskShadeService.clearDataByTaskId(taskId, appType);
    }

    @RequestMapping(value="/saveTaskTaskList", method = {RequestMethod.POST})
    public void saveTaskTaskList(@DtRequestParam("taskTask") String taskLists) {
        scheduleTaskTaskShadeService.saveTaskTaskList(taskLists);
    }

    @RequestMapping(value="/getAllParentTask", method = {RequestMethod.POST})
    public List<ScheduleTaskTaskShade> getAllParentTask(@DtRequestParam("taskId") Long taskId) {
        return scheduleTaskTaskShadeService.getAllParentTask(taskId);
    }

    @RequestMapping(value="/displayOffSpring", method = {RequestMethod.POST})
    public ScheduleTaskVO displayOffSpring(@DtRequestParam("taskId") Long taskId,
                                           @DtRequestParam("projectId") Long projectId,
                                           @DtRequestParam("userId") Long userId,
                                           @DtRequestParam("level") Integer level,
                                           @DtRequestParam("type") Integer directType, @DtRequestParam("appType")Integer appType) {
        return scheduleTaskTaskShadeService.displayOffSpring(taskId, projectId, userId, level, directType, appType);
    }

    @RequestMapping(value="/getAllFlowSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流全部节点信息 -- 依赖树")
    public ScheduleTaskVO getAllFlowSubTasks(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskTaskShadeService.getAllFlowSubTasks(taskId, appType);
    }
}
