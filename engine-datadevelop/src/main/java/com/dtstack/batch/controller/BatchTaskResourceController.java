package com.dtstack.batch.controller;

import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.task.impl.BatchTaskResourceService;
import com.dtstack.batch.web.task.vo.query.BatchTaskResourceGetResourcesVO;
import com.dtstack.batch.web.task.vo.result.BatchResourceResultVO;
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

import java.util.List;

@Api(value = "资源任务管理", tags = {"资源任务管理"})
@RestController
@RequestMapping(value = "/api/rdos/batch/batchTaskResource")
public class BatchTaskResourceController {

    @Autowired
    private BatchTaskResourceService resourceService;

    @PostMapping(value = "getResources")
    @ApiOperation("获得 资源-任务 列表")
    @Security(code = AuthCode.DATADEVELOP_BATCH_TASKMANAGER_QUERY)
    public R<List<BatchResourceResultVO>> getResources(@RequestBody BatchTaskResourceGetResourcesVO vo) {
        return new APITemplate<List<BatchResourceResultVO> >() {
            @Override
            protected List<BatchResourceResultVO> process() {
                return TaskMapstructTransfer.INSTANCE.BatchResourceListToBatchResourceResultVOList(resourceService.getResources(vo.getTaskId(),
                        vo.getProjectId(), vo.getType()));
            }
        }.execute();
    }
}
