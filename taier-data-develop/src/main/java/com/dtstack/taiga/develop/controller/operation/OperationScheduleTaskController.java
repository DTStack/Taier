package com.dtstack.taiga.develop.controller.operation;

import com.dtstack.taiga.common.exception.ErrorCode;
import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.dao.pager.PageResult;
import com.dtstack.taiga.develop.mapstruct.task.ScheduleTaskMapstructTransfer;
import com.dtstack.taiga.develop.service.schedule.TaskService;
import com.dtstack.taiga.develop.vo.schedule.QueryTaskListVO;
import com.dtstack.taiga.develop.vo.schedule.ReturnScheduleTaskVO;
import com.dtstack.taiga.develop.vo.schedule.ReturnTaskSupportTypesVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 1:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/scheduleTaskShade")
@Api(value = "/node/scheduleTaskShade", tags = {"运维中心---任务相关接口"})
public class OperationScheduleTaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping(value = "/queryTasks")
    @ApiOperation(value = "运维中心任务管理 -> 任务列表接口")
    public R<PageResult<List<ReturnScheduleTaskVO>>> queryTasks(@RequestBody @Validated QueryTaskListVO vo) {
        return R.ok(taskService.queryTasks(ScheduleTaskMapstructTransfer.INSTANCE.queryTasksVoToDto(vo)));
    }

    @PostMapping(value = "/frozenTask")
    @ApiOperation(value = "运维中心任务管理 -> 冻结和解冻任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskIdList", value = "任务id", required = true, dataType = "array"),
            @ApiImplicitParam(name = "scheduleStatus", value = " 调度状态：0 正常 1冻结 2停止", required = true, dataType = "Integer")
    })
    public R<Boolean> frozenTask(@RequestParam("taskIdList") List<Long> taskIdList,
                           @RequestParam("scheduleStatus") Integer scheduleStatus) {
        if (taskService.frozenTask(taskIdList, scheduleStatus)) {
            return R.ok(true);
        }
        return R.fail(ErrorCode.UPDATE_EXCEPTION);
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
