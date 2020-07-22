package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/scheduleJobJob")
@Api(value = "/node/scheduleJobJob", tags = {"任务实例依赖接口"})
public class ScheduleJobJobController implements com.dtstack.engine.api.service.ScheduleJobJobService {

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @RequestMapping(value="/displayOffSpring", method = {RequestMethod.POST})
    public ScheduleJobVO displayOffSpring(@DtRequestParam("jobId") Long jobId,
                                          @DtRequestParam("projectId") Long projectId,
                                          @DtRequestParam("level") Integer level) throws Exception {
        return scheduleJobJobService.displayOffSpring(jobId, projectId, level);
    }

    @RequestMapping(value="/displayOffSpringWorkFlow", method = {RequestMethod.POST})
    @ApiOperation(value = "为工作流节点展开子节点")
    public ScheduleJobVO displayOffSpringWorkFlow(@DtRequestParam("jobId") Long jobId, @DtRequestParam("appType")Integer appType) throws Exception {
        return scheduleJobJobService.displayOffSpringWorkFlow(jobId, appType);
    }

    @RequestMapping(value="/displayForefathers", method = {RequestMethod.POST})
    public ScheduleJobVO displayForefathers(@DtRequestParam("jobId") Long jobId, @DtRequestParam("level") Integer level) throws Exception {
        return scheduleJobJobService.displayForefathers(jobId, level);
    }
}
