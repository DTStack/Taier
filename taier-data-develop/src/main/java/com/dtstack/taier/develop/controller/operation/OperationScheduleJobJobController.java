package com.dtstack.taier.develop.controller.operation;

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.mapstruct.job.JobMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobJobService;
import com.dtstack.taier.develop.vo.schedule.QueryJobDisplayVO;
import com.dtstack.taier.develop.vo.schedule.ReturnJobDisplayVO;
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
@RequestMapping("/scheduleJobJob")
@Api(value = "/scheduleJobJob", tags = {"运维中心---周期实例依赖关系相关接口"})
public class OperationScheduleJobJobController {

    @Autowired
    private JobJobService jobJobService;

    @RequestMapping(value = "/displayOffSpring", method = {RequestMethod.POST})
    public R<ReturnJobDisplayVO> displayOffSpring(@RequestBody QueryJobDisplayVO vo) {
        return R.ok(jobJobService.displayOffSpring(JobMapstructTransfer.INSTANCE.queryJobDisplayVOToReturnJobDisplayVO(vo)));
    }

    @RequestMapping(value="/displayOffSpringWorkFlow", method = {RequestMethod.POST})
    @ApiOperation(value = "为工作流节点展开子节点")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "实例id", required = true, dataType = "String"),
    })
    public R<ReturnJobDisplayVO> displayOffSpringWorkFlow(@RequestParam("jobId") String jobId) {
        return R.ok(jobJobService.displayOffSpringWorkFlowJob(jobId));
    }
}
