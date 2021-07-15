package com.dtstack.batch.web.controller;


import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.task.impl.BatchTaskRecordService;
import com.dtstack.batch.web.task.vo.query.BatchTaskRecordQueryRecordsVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskRecordQueryRecordsResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "任务操作记录", tags = {"任务操作记录"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTaskRecord")
public class BatchTaskRecordController {

    @Autowired
    private BatchTaskRecordService recordService;

    @PostMapping(value = "queryRecords")
    @ApiOperation("查询操作记录")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchTaskRecordQueryRecordsResultVO> queryRecords(@RequestBody BatchTaskRecordQueryRecordsVO vo) {
        return new APITemplate<BatchTaskRecordQueryRecordsResultVO>() {
            @Override
            protected BatchTaskRecordQueryRecordsResultVO process() {
                return recordService.queryRecords(vo .getTaskId(), vo.getCurrentPage(),
                        vo.getPageSize());
            }
        }.execute();
    }

}
