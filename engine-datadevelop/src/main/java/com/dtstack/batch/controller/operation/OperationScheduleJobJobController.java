package com.dtstack.batch.controller.operation;

import com.dtstack.batch.mapstruct.job.JobMapstructTransfer;
import com.dtstack.batch.service.schedule.JobJobService;
import com.dtstack.batch.vo.schedule.QueryJobDisplayVO;
import com.dtstack.batch.vo.schedule.ReturnJobDisplayVO;
import io.swagger.annotations.Api;
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
@RequestMapping("/node/scheduleJob")
@Api(value = "/node/scheduleJob", tags = {"实例接口"})
public class OperationScheduleJobJobController {

    @Autowired
    private JobJobService jobJobService;

    @RequestMapping(value = "/displayOffSpring", method = {RequestMethod.POST})
    public ReturnJobDisplayVO displayOffSpring(@RequestBody QueryJobDisplayVO vo) throws Exception {
        return jobJobService.displayOffSpring(JobMapstructTransfer.INSTANCE.queryJobDisplayVOToReturnJobDisplayVO(vo));
    }

    @RequestMapping(value="/displayOffSpringWorkFlow", method = {RequestMethod.POST})
    @ApiOperation(value = "为工作流节点展开子节点")
    public ReturnJobDisplayVO displayOffSpringWorkFlow(@RequestParam("jobId") String jobId) throws Exception {
        return jobJobService.displayOffSpringWorkFlowJob(jobId);
    }
}
