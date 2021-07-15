package com.dtstack.batch.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.mapstruct.vo.BatchServerLogMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.impl.BatchServerLogService;
import com.dtstack.batch.vo.BatchServerLogVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByAppIdVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByAppTypeVO;
import com.dtstack.batch.web.server.vo.query.BatchServerGetLogByJobIdVO;
import com.dtstack.batch.web.server.vo.result.BatchServerLogByAppLogTypeResultVO;
import com.dtstack.batch.web.server.vo.result.BatchServerLogResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "日志管理", tags = {"日志管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchServerLog")
public class BatchServerLogController {

    @Autowired
    private BatchServerLogService batchServerLogService;

    @PostMapping(value = "getLogsByJobId")
    @ApiOperation("根据jobId获取日志")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchServerLogResultVO> getLogsByJobId(@RequestBody BatchServerGetLogByJobIdVO vo) {
        return new APITemplate<BatchServerLogResultVO>() {
            @Override
            protected BatchServerLogResultVO process() {
                BatchServerLogVO logsByJobId = batchServerLogService.getLogsByJobId(vo.getJobId(), vo.getPageInfo());
                return BatchServerLogMapstructTransfer.INSTANCE.batchServerLogVOToBatchServerLogResultVO(logsByJobId);
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppId")
    @ApiOperation("根据appId获取日志")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<JSONObject> getLogsByAppId(@RequestBody BatchServerGetLogByAppIdVO vo) {
        return new APITemplate<JSONObject>() {
            @Override
            protected JSONObject process() {
                return batchServerLogService.getLogsByAppId(vo.getDtuicTenantId(), vo.getTaskType(), vo.getJobId(), vo.getProjectId());
            }
        }.execute();
    }

    @PostMapping(value = "getLogsByAppLogType")
    @ApiOperation("根据类型获取日志")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchServerLogByAppLogTypeResultVO> getLogsByAppLogType(@RequestBody BatchServerGetLogByAppTypeVO vo) {
        return new APITemplate<BatchServerLogByAppLogTypeResultVO>() {
            @Override
            protected BatchServerLogByAppLogTypeResultVO process() {
                return batchServerLogService.getLogsByAppLogType(vo.getDtuicTenantId(), vo.getTaskType(), vo.getJobId(), vo.getLogType(), vo.getProjectId());
            }
        }.execute();
    }
}
