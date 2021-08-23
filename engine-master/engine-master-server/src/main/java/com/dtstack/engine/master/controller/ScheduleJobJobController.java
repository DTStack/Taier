package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.vo.ScheduleJobVO;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.master.impl.ScheduleJobJobService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/node/scheduleJobJob")
@Api(value = "/node/scheduleJobJob", tags = {"任务实例依赖接口"})
public class ScheduleJobJobController {

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private EnvironmentContext context;

    @RequestMapping(value="/displayOffSpring", method = {RequestMethod.POST})
    public ScheduleJobVO displayOffSpring(@RequestParam("jobId") Long jobId,
                                          @RequestParam("level") Integer level) throws Exception {

        if(context.getUseOptimize()) {
            return scheduleJobJobService.displayOffSpringNew(jobId, level);
        }else{
            return scheduleJobJobService.displayOffSpring(jobId, level);
        }
    }

    @RequestMapping(value="/displayOffSpringWorkFlow", method = {RequestMethod.POST})
    @ApiOperation(value = "为工作流节点展开子节点")
    public ScheduleJobVO displayOffSpringWorkFlow(@RequestParam("jobId") Long jobId, @RequestParam("appType")Integer appType) throws Exception {
        return scheduleJobJobService.displayOffSpringWorkFlow(jobId, appType);
    }

    /**
     * @author newman
     * @Description 展开上游工作实例
     * @Date 2021/1/6 5:49 下午
     * @param jobId:
     * @param level:
     * @return: com.dtstack.engine.api.vo.ScheduleJobVO
     **/
    @RequestMapping(value="/displayForefathers", method = {RequestMethod.POST})
    public ScheduleJobVO displayForefathers(@RequestParam("jobId") Long jobId, @RequestParam("level") Integer level) throws Exception {
        if(context.getUseOptimize()) {
            return scheduleJobJobService.displayForefathersNew(jobId, level);
        }else{
            return scheduleJobJobService.displayForefathers(jobId, level);
        }
    }
}
