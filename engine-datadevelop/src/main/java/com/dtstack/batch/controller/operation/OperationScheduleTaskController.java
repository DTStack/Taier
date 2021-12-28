package com.dtstack.batch.controller.operation;

import com.dtstack.batch.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.batch.vo.schedule.QueryTaskListVO;
import com.dtstack.batch.service.schedule.TaskService;
import com.dtstack.batch.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.engine.pager.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 1:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/scheduleTaskShade")
@Api(value = "/node/scheduleTaskShade", tags = {"运维中心任务接口"})
public class OperationScheduleTaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/queryTasks")
    @ApiOperation(value = "运维中心任务管理 -> 任务列表接口")
    public PageResult<List<ReturnScheduleTaskVO>> queryTasks(@RequestBody @Validated QueryTaskListVO vo) {
        return taskService.queryTasks(ScheduleTaskMapstructTransfer.INSTANCE.queryTasksVoToDto(vo));
    }

    @PostMapping(value = "/frozenTask")
    @ApiOperation(value = "运维中心任务管理 -> 冻结和解冻任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskIdList", value = "任务id", required = true, dataType = "array"),
            @ApiImplicitParam(name = "scheduleStatus", value = " 调度状态：0 正常 1冻结 2停止", required = true, dataType = "Integer")
    })
    public void frozenTask(@RequestParam("taskIdList") List<Long> taskIdList,
                           @RequestParam("scheduleStatus") Integer scheduleStatus) {
        taskService.frozenTask(taskIdList, scheduleStatus);
    }

    @RequestMapping(value = "/queryFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long"),
    })
    public List<ReturnScheduleTaskVO> queryFlowWorkSubTasks(@RequestParam("taskId") Long taskId) {
        return taskService.dealFlowWorkTask(taskId);
    }




}
