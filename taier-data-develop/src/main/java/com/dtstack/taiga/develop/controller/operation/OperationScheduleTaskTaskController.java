package com.dtstack.taiga.develop.controller.operation;

import com.dtstack.taiga.common.lang.web.R;
import com.dtstack.taiga.develop.mapstruct.job.JobMapstructTransfer;
import com.dtstack.taiga.develop.service.schedule.TaskTaskService;
import com.dtstack.taiga.develop.vo.schedule.QueryTaskDisplayVO;
import com.dtstack.taiga.develop.vo.schedule.ReturnTaskDisplayVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: dazhi
 * @Date: 2021/12/26 10:26 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/scheduleTaskTaskShade")
@Api(value = "/node/scheduleTaskTaskShade", tags = {"运维中心---任务依赖相关接口"})
public class OperationScheduleTaskTaskController {

    @Autowired
    private TaskTaskService tasktaskService;

    @PostMapping(value="/displayOffSpring")
    public R<ReturnTaskDisplayVO> displayOffSpring(@RequestBody QueryTaskDisplayVO vo) {
        return R.ok(tasktaskService.displayOffSpring(JobMapstructTransfer.INSTANCE.queryTaskDisplayVOToQueryTaskDisplayDTO(vo)));
    }

    @PostMapping(value="/getAllFlowSubTasks")
    @ApiOperation(value = "查询工作流全部节点信息 -- 依赖树")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务id", required = true, dataType = "Long"),
    })
    public R<ReturnTaskDisplayVO> getAllFlowSubTasks(@RequestParam("taskId") Long taskId) {
        return R.ok(tasktaskService.getAllFlowSubTasks(taskId));
    }

}
