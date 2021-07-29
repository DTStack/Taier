package com.dtstack.batch.web.controller;

import com.dtstack.batch.mapstruct.vo.BatchJobJobMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.job.impl.BatchJobJobService;
import com.dtstack.batch.web.job.vo.query.BatchJobJobSpringWorkFlowVO;
import com.dtstack.batch.web.job.vo.query.BatchJobJobVO;
import com.dtstack.batch.web.job.vo.result.BatchScheduleJobResultVO;
import com.dtstack.engine.api.vo.ScheduleJobVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "任务依赖管理", tags = {"任务依赖管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchJobJob")
public class BatchJobJobController {

    @Autowired
    private BatchJobJobService batchJobJobService;

    @PostMapping(value = "displayOffSpring")
    @ApiOperation("根据jobId展示该任务依赖视图")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleJobResultVO> displayOffSpring(@RequestBody BatchJobJobVO vo) {
        return new APITemplate<BatchScheduleJobResultVO>() {
            @Override
            protected BatchScheduleJobResultVO process() {
                ScheduleJobVO scheduleJobVO = batchJobJobService.displayOffSpring(vo.getJobId(), vo.getProjectId(), vo.getLevel());
                return BatchJobJobMapstructTransfer.INSTANCE.scheduleJobVOToBatchScheduleJobResultVO(scheduleJobVO);
            }
        }.execute();
    }

    @PostMapping(value = "displayOffSpringWorkFlow")
    @ApiOperation("为工作流节点展开子节点")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleJobResultVO> displayOffSpringWorkFlow(@RequestBody BatchJobJobSpringWorkFlowVO vo) {
        return new APITemplate<BatchScheduleJobResultVO>() {
            @Override
            protected BatchScheduleJobResultVO process() {
                ScheduleJobVO scheduleJobVO = batchJobJobService.displayOffSpringWorkFlow(vo.getJobId());
                return BatchJobJobMapstructTransfer.INSTANCE.scheduleJobVOToBatchScheduleJobResultVO(scheduleJobVO);
            }
        }.execute();
    }

    @PostMapping(value = "displayForefathers")
    @ApiOperation("展开父节点")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleJobResultVO> displayForefathers(@RequestBody BatchJobJobVO vo) {
        return new APITemplate<BatchScheduleJobResultVO>() {
            @Override
            protected BatchScheduleJobResultVO process() {
                ScheduleJobVO scheduleJobVO = batchJobJobService.displayForefathers(vo.getJobId(), vo.getLevel());
                return BatchJobJobMapstructTransfer.INSTANCE.scheduleJobVOToBatchScheduleJobResultVO(scheduleJobVO);
            }
        }.execute();
    }
}
