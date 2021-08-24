package com.dtstack.batch.controller;

import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.service.task.impl.BatchTaskShadeService;
import com.dtstack.batch.web.pager.PageResult;
import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskShadeVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskShadePageQueryResultVO;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.web.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "任务发布管理", tags = {"任务发布管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTaskShade")
public class BatchTaskShadeController {

    @Autowired
    private BatchTaskShadeService batchTaskShadeService;

    @PostMapping(value = "pageQuery")
    @ApiOperation("分页查询任务信息")
    public R<PageResult<List<BatchTaskShadePageQueryResultVO>>> pageQuery(@RequestBody BatchScheduleTaskShadeVO batchScheduleTaskShadeVO) {
        return new APITemplate<PageResult<List<BatchTaskShadePageQueryResultVO>>>() {
            @Override
            protected PageResult<List<BatchTaskShadePageQueryResultVO>> process() {
                return TaskMapstructTransfer.INSTANCE.BatchTaskShadePageQueryResultVOListTOBatchTaskShadePageQueryResultVOList(
                        batchTaskShadeService.pageQuery(TaskMapstructTransfer.INSTANCE.BatchScheduleTaskShadeVOToScheduleTaskShadeDTO(batchScheduleTaskShadeVO)));
            }
        }.execute();
    }

}
