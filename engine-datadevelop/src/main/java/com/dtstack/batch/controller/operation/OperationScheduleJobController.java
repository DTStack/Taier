package com.dtstack.batch.controller.operation;

import com.dtstack.batch.mapstruct.job.JobMapstructTransfer;
import com.dtstack.batch.service.schedule.JobService;
import com.dtstack.batch.vo.schedule.QueryJobListVO;
import com.dtstack.batch.vo.schedule.QueryJobStatusStatisticsVO;
import com.dtstack.batch.vo.schedule.ReturnJobListVO;
import com.dtstack.batch.vo.schedule.ReturnJobStatusStatisticsVO;
import com.dtstack.engine.master.vo.ScheduleJobVO;
import com.dtstack.engine.pager.PageResult;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/23 3:54 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/scheduleJob")
public class OperationScheduleJobController {

    @Autowired
    private JobService jobService;

    @RequestMapping(value = "/queryJobs", method = {RequestMethod.POST})
    @ApiOperation(value = "任务运维 - 搜索")
    public PageResult<List<ReturnJobListVO>> queryJobs(@RequestBody QueryJobListVO vo) throws Exception {
        return jobService.queryJobs(JobMapstructTransfer.INSTANCE.queryJobListVOToQueryJobListDTO(vo));
    }

    @RequestMapping(value = "/queryJobsStatusStatistics", method = {RequestMethod.POST})
    @ApiOperation(value = "任务状态统计")
    public List<ReturnJobStatusStatisticsVO> queryJobsStatusStatistics(@RequestBody QueryJobStatusStatisticsVO vo) {
        return jobService.queryJobsStatusStatistics(JobMapstructTransfer.INSTANCE.queryJobStatusStatisticsVOToQueryJobStatusStatisticsDTO(vo));
    }
}
