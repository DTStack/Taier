package com.dtstack.taier.develop.controller.operation;

import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.develop.mapstruct.job.ActionMapStructTransfer;
import com.dtstack.taier.develop.service.schedule.ActionService;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.vo.schedule.ActionJobKillVO;
import com.dtstack.taier.develop.vo.schedule.QueryJobLogVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobLogVO;
import com.dtstack.taier.pluginapi.enums.RdosTaskStatus;
import com.dtstack.taier.scheduler.enums.RestartType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 10:52 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/action")
@Api(value = "/action", tags = {"运维中心---任务动作相关接口"})
public class OperationActionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationActionController.class);

    @Autowired
    private JobService jobService;

    @Autowired
    private ActionService actionService;

    @ApiOperation(value = "重跑任务")
    @PostMapping(value = "/restartJob")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "选择的实例id", required = true, dataType = "array"),
            @ApiImplicitParam(name = "restartType", value = "重跑当前节点: RESTART_CURRENT_NODE(0)\n重跑及其下游: RESTART_CURRENT_AND_DOWNSTREAM_NODE(1)\n置成功并恢复调度:SET_SUCCESSFULLY_AND_RESUME_SCHEDULING(2)\n", required = true, dataType = "Integer")
    })
    public R<Boolean> restartJob(@RequestParam("jobIds") List<String> jobIds,
                                 @RequestParam("restartType") Integer restartType) {
        RestartType byCode = RestartType.getByCode(restartType);

        if (byCode == null) {
            throw new RdosDefineException("请选择正确的重跑模式");
        }

        return R.ok(actionService.restartJob(byCode, jobIds));
    }

    @ApiOperation(value = "批量停止任务")
    @PostMapping(value = "/batchStopJobs")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobIds", value = "选择的实例id", required = true, dataType = "array")
    })
    public R<Integer> batchStopJobs(@RequestParam("jobIds") List<String> jobIds) {
        return R.ok(actionService.batchStopJobs(jobIds));
    }

    @ApiOperation(value = "按照补数据停止任务")
    @RequestMapping(value = "/stopFillDataJobs", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fillId", value = "选择的实例id", required = true, dataType = "array")
    })
    public R<Integer> stopFillDataJobs(@RequestParam("fillId") Long fillId) {
        return R.ok(actionService.stopFillDataJobs(fillId));
    }

    @ApiOperation(value = "按照添加停止任务")
    @RequestMapping(value="/stopJobByCondition", method = {RequestMethod.POST})
    public R<Integer> stopJobByCondition(@RequestBody ActionJobKillVO vo) {
        return R.ok(actionService.stopJobByCondition(ActionMapStructTransfer.INSTANCE.actionJobKillVOToActionJobKillDTO(vo)));
    }

    @ApiOperation(value = "查看实例日志")
    @PostMapping(value = "/queryJobLog")
    public R<ReturnJobLogVO> queryJobLog(@RequestBody @Valid QueryJobLogVO vo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            LOGGER.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            throw new RdosDefineException(bindingResult.getFieldError().getDefaultMessage());
        }
        return R.ok(actionService.queryJobLog(vo.getJobId(), vo.getPageInfo()));
    }

    @ApiOperation(value = "查看实例状态")
    @PostMapping(value = "/status")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "实例id", required = true, dataType = "String")
    })
    public R<Integer> status(@RequestParam("jobId") String jobId) throws Exception {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
        return R.ok(null == scheduleJob ? RdosTaskStatus.NOTFOUND.getStatus() : scheduleJob.getStatus());
    }
}
