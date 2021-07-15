package com.dtstack.batch.web.controller;

import com.dtstack.batch.mapstruct.vo.TaskMapstructTransfer;
import com.dtstack.batch.service.auth.AuthCode;
import com.dtstack.batch.service.task.impl.BatchTaskTaskService;
import com.dtstack.batch.web.task.vo.query.BatchScheduleTaskResultVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskTaskAddOrUpdateVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskTaskDisplayOffSpringVO;
import com.dtstack.batch.web.task.vo.query.BatchTaskTaskFindTaskRuleTaskVO;
import com.dtstack.batch.web.task.vo.result.BatchTaskTaskFindTaskRuleTaskResultVO;
import dt.insight.plat.autoconfigure.web.security.permissions.annotation.Security;
import dt.insight.plat.lang.coc.template.APITemplate;
import dt.insight.plat.lang.exception.biz.BizException;
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
@RequestMapping(value = "/api/rdos/batch/batchTaskTask")
public class BatchTaskTaskController {

    @Autowired
    private BatchTaskTaskService taskService;

    @PostMapping(value = "addOrUpdateTaskTask")
    @ApiOperation("添加或者修改任务依赖")
    public R<Void> addOrUpdateTaskTask(@RequestBody BatchTaskTaskAddOrUpdateVO taskVO) {
        return new APITemplate<Void>() {
            @Override
            protected Void process() {
                taskService.addOrUpdateTaskTask(taskVO.getTaskId(), TaskMapstructTransfer.INSTANCE.
                        batchTaskTaskAddOrUpdateDependencyVOsToBatchTasks(taskVO.getDependencyVOS()));
                return null;
            }
        }.execute();
    }

    @PostMapping(value = "displayOffSpring")
    @ApiOperation("所有的任务关联关系的显示都是基于已经发布的任务数据")
    @Security(code = AuthCode.MAINTENANCE_BATCH_QUERY)
    public R<BatchScheduleTaskResultVO> displayOffSpring(@RequestBody BatchTaskTaskDisplayOffSpringVO taskVO) {
        return new APITemplate<BatchScheduleTaskResultVO>() {
            @Override
            protected BatchScheduleTaskResultVO process() {
                return TaskMapstructTransfer.INSTANCE.ScheduleTaskVOToBatchScheduleTaskResultVO(taskService.displayOffSpring(taskVO.getTaskId(),
                        taskVO.getProjectId(), taskVO.getUserId(), taskVO.getLevel(), taskVO.getType(), taskVO.getAppType()));
            }
        }.execute();
    }

    @ApiOperation(value = "根据任务Id获取任务信息，hover事件详情信息")
    @PostMapping(value = "findTaskRuleTask")
    public R<BatchTaskTaskFindTaskRuleTaskResultVO> findTaskRuleTask(@RequestBody BatchTaskTaskFindTaskRuleTaskVO vo) {
        return new APITemplate<BatchTaskTaskFindTaskRuleTaskResultVO>() {
            @Override
            protected BatchTaskTaskFindTaskRuleTaskResultVO process() throws BizException {
                return TaskMapstructTransfer.INSTANCE.scheduleDetailsVOToBatchTaskTaskFindTaskRuleTaskResultVO(taskService.findTaskRuleTask(vo.getTaskId(), vo.getAppType()));
            }
        }.execute();
    }

}
