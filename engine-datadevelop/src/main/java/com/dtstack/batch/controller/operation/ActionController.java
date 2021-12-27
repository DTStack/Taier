package com.dtstack.batch.controller.operation;

import com.dtstack.batch.service.schedule.JobService;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByJobIdVO;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.master.enums.RestartType;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.vo.JobLogVO;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/6 10:52 AM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/action")
@Api(value = "/node/action", tags = {"任务动作接口"})
public class ActionController {

    @Autowired
    private ActionService actionService;

    @Autowired
    private JobService jobService;

    @PostMapping(value = "/log/unite")
    public JobLogVO logUnite(@RequestBody BatchServerGetLogByJobIdVO vo) {
        return actionService.logUnite(vo.getJobId(),vo.getPageInfo());
    }

    @ApiOperation(value = "重跑任务")
    @PostMapping(value = "/restartJob")
    public boolean restartJob(@RequestParam("jobIds") List<String> jobIds,
                              @RequestParam("restartType") Integer restartType) {
        RestartType byCode = RestartType.getByCode(restartType);

        if (byCode == null) {
            throw new RdosDefineException("请选择正确的重跑模式");
        }

        return actionService.restartJob(byCode, jobIds);
    }

    @PostMapping(value = "/status")
    public Integer status(@RequestParam("jobId") String jobId) throws Exception {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
        return null == scheduleJob ? RdosTaskStatus.NOTFOUND.getStatus() : scheduleJob.getStatus();
    }

    @PostMapping(value = "/generateUniqueSign")
    public String generateUniqueSign() {
        return actionService.generateUniqueSign();
    }

}
