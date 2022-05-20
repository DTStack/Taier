package com.dtstack.taier.develop.controller.operation;

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.TaskService;
import com.dtstack.taier.develop.vo.schedule.QueryTaskListVO;
import com.dtstack.taier.develop.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.taier.develop.vo.schedule.ReturnTaskSupportTypesVO;
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
@RequestMapping("/scheduleTaskShade")
@Api(value = "/scheduleTaskShade", tags = {"运维中心---任务相关接口"})
public class OperationScheduleTaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/queryTasks")
    @ApiOperation(value = "运维中心任务管理 -> 任务列表接口")
    public R<PageResult<List<ReturnScheduleTaskVO>>> queryTasks(@RequestBody @Validated QueryTaskListVO vo) {
        return R.ok(taskService.queryTasks(ScheduleTaskMapstructTransfer.INSTANCE.queryTasksVoToDto(vo)));
    }

    @RequestMapping(value = "/queryFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long"),
    })
    public R<List<ReturnScheduleTaskVO>> queryFlowWorkSubTasks(@RequestParam("taskId") Long taskId) {
        return R.ok(ScheduleTaskMapstructTransfer.INSTANCE.beanToTaskVO(taskService.findAllFlowTasks(taskId)));
    }

    @PostMapping(value = "/querySupportJobTypes")
    @ApiOperation(value = "查询所有任务类型")
    public R<List<ReturnTaskSupportTypesVO>> querySupportJobTypes() {
        return R.ok(taskService.querySupportJobTypes());
    }
}
