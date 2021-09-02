package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.CronExceptionVO;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleDetailsVO;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeTypeVO;
import com.dtstack.engine.api.vo.task.NotDeleteTaskVO;
import com.dtstack.engine.api.vo.task.TaskTypeVO;
import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/node/scheduleTaskShade")
@Api(value = "/node/scheduleTaskShade", tags = {"任务接口"})
public class ScheduleTaskShadeController {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;


    @RequestMapping(value = "/addOrUpdate", method = {RequestMethod.POST})
    @ApiOperation(value = "添加或更新任务", notes = "例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息")
    public void addOrUpdate(@RequestBody ScheduleTaskShadeDTO batchTaskShadeDTO) {
        scheduleTaskShadeService.addOrUpdate(batchTaskShadeDTO);
    }

    @RequestMapping(value = "/addOrUpdateBatchTask", method = {RequestMethod.POST})
    @ApiOperation(value = "批量添加或更新任务", notes = "例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息")
    public String addOrUpdateBatchTask(@RequestBody List<ScheduleTaskShadeDTO> batchTaskShadeDTOs, String commitId) {
        return scheduleTaskShadeService.addOrUpdateBatchTask(batchTaskShadeDTOs, commitId);
    }

    @RequestMapping(value = "/infoCommit", method = {RequestMethod.POST})
    @ApiOperation(value = "保存任务提交engine的额外信息,不会直接提交，只有commit之后才会提交")
    public void infoCommit(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("extraInfo") String info, @RequestParam("commitId") String commitId) {
        scheduleTaskShadeService.infoCommit(taskId, appType, info, commitId);
    }

    @RequestMapping(value = "/taskCommit", method = {RequestMethod.POST})
    @ApiOperation(value = "提交任务")
    public void taskCommit(@RequestParam("commitId") String commitId) {
        scheduleTaskShadeService.taskCommit(commitId);
    }

    @RequestMapping(value = "/info", method = {RequestMethod.POST})
    @ApiOperation(value = "保存任务提交engine的额外信息")
    public void info(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("extraInfo") String info) {
        scheduleTaskShadeService.info(taskId, appType, info);
    }

    @RequestMapping(value = "/deleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "删除任务", notes = "task删除时触发同步清理")
    public void deleteTask(@RequestParam("taskId") Long taskId, @RequestParam("modifyUserId") long modifyUserId, @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.deleteTask(taskId, modifyUserId, appType);
    }

    @RequestMapping(value = "/getNotDeleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "获得其他依赖的接口", notes = "task删除时触发同步清理")
    public List<NotDeleteTaskVO> getNotDeleteTask(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getNotDeleteTask(taskId, appType);
    }

    @RequestMapping(value = "/getTasksByName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据项目id,任务名 获取任务列表")
    public List<ScheduleTaskShade> getTasksByName(@RequestParam("projectId") long projectId,
                                                  @RequestParam("name") String name, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTasksByName(projectId, name, appType);
    }

    @RequestMapping(value = "/getByName", method = {RequestMethod.POST})
    public ScheduleTaskShade getByName(@RequestParam("projectId") long projectId,
                                       @RequestParam("name") String name, @RequestParam("appType") Integer appType, @RequestParam("flowId") Long flowId) {
        return scheduleTaskShadeService.getByName(projectId, name, appType, flowId);
    }

    @RequestMapping(value = "/updateTaskName", method = {RequestMethod.POST})
    public void updateTaskName(@RequestParam("taskId") long id, @RequestParam("taskName") String taskName, @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.updateTaskName(id, taskName, appType);
    }

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询已提交的任务")
    @Deprecated
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(@RequestBody ScheduleTaskShadeDTO dto) {
        if (null != dto) {
            // 原逻辑 create modify owner UserId 不生效
            dto.setCreateUserId(null);
            dto.setModifyUserId(null);
            dto.setOwnerUserId(null);
            return scheduleTaskShadeService.pageQuery(dto);
        }
        return new PageResult<>(0, 0, 0, 0, new ArrayList<>(0));
    }



    @RequestMapping(value="/v2/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询已提交的任务")
    public PageResult<List<ScheduleTaskShadeVO>> newPageQuery(@RequestBody ScheduleTaskShadeDTO dto) {
        return scheduleTaskShadeService.pageQuery(dto);
    }

    @RequestMapping(value = "/getBatchTaskById", method = {RequestMethod.POST})
    public ScheduleTaskShade getBatchTaskById(@RequestParam("id") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getBatchTaskById(taskId, appType);
    }

    @RequestMapping(value = "/queryTasks", method = {RequestMethod.POST})
    public ScheduleTaskShadePageVO queryTasks(@RequestParam("tenantId") Long tenantId,
                                              @RequestParam("dt_tenant_id") Long dtTenantId,
                                              @RequestParam("projectId") Long projectId,
                                              @RequestParam("name") String name,
                                              @RequestParam("ownerId") Long ownerId,
                                              @RequestParam("startTime") Long startTime,
                                              @RequestParam("endTime") Long endTime,
                                              @RequestParam("scheduleStatus") Integer scheduleStatus,
                                              @RequestParam("taskType") String taskTypeList,
                                              @RequestParam("taskPeriodId") String periodTypeList,
                                              @RequestParam("currentPage") Integer currentPage,
                                              @RequestParam("pageSize") Integer pageSize,
                                              @RequestParam("searchType") String searchType,
                                              @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.queryTasks(tenantId, dtTenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, currentPage, pageSize, searchType, appType);
    }


    @RequestMapping(value = "/frozenTask", method = {RequestMethod.POST})
    @ApiOperation(value = "冻结任务")
    public void frozenTask(@RequestParam("taskIdList") List<Long> taskIdList, @RequestParam("scheduleStatus") int scheduleStatus,
                           @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.frozenTask(taskIdList, scheduleStatus, appType);
    }


    @RequestMapping(value = "/dealFlowWorkTask", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    public ScheduleTaskVO dealFlowWorkTask(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("taskTypes") List<Integer> taskTypes, @RequestParam("ownerId") Long ownerId) {
        return scheduleTaskShadeService.dealFlowWorkTask(taskId, appType, taskTypes, ownerId);
    }

    @RequestMapping(value = "/getFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务流下的所有子任务")
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("taskTypes") List<Integer> taskTypes, @RequestParam("ownerId") Long ownerId) {
        return scheduleTaskShadeService.getFlowWorkSubTasks(taskId, appType, taskTypes, ownerId);
    }


    @RequestMapping(value = "/findTaskId", method = {RequestMethod.POST})
    public ScheduleTaskShade findTaskId(@RequestParam("taskId") Long taskId, @RequestParam("isDeleted") Integer isDeleted, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskId(taskId, isDeleted, appType);
    }


    @RequestMapping(value = "/findTaskIds", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isSimple", value = "若为true，则不查询sql值", dataType = "boolean", required = true)
    })
    public List<ScheduleTaskShade> findTaskIds(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("isDeleted") Integer isDeleted, @RequestParam("appType") Integer appType, @RequestParam("isSimple") boolean isSimple) {
        return scheduleTaskShadeService.findTaskIds(taskIds, isDeleted, appType, isSimple);
    }


    @RequestMapping(value = "/listDependencyTask", method = {RequestMethod.POST})
    public List<Map<String, Object>> listDependencyTask(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("name") String name, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listDependencyTask(taskId, name, projectId);
    }

    @RequestMapping(value = "/listByTaskIdsNotIn", method = {RequestMethod.POST})
    public List<Map<String, Object>> listByTaskIdsNotIn(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listByTaskIdsNotIn(taskId, appType, projectId);
    }

    @RequestMapping(value = "/countTaskByType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public ScheduleTaskShadeCountTaskVO countTaskByType(@RequestParam("tenantId") Long tenantId, @RequestParam("dtuicTenantId") Long dtuicTenantId,
                                                        @RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType,
                                                        @RequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByType(tenantId, dtuicTenantId, projectId, appType, taskTypes);
    }

    @RequestMapping(value = "/getTaskByIds", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public List<ScheduleTaskShade> getTaskByIds(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTaskByIds(taskIds, appType);
    }

    @RequestMapping(value = "/countTaskByTypes", method = {RequestMethod.POST})
    public List<ScheduleTaskShadeCountTaskVO> countTaskByTypes(@RequestParam("tenantId") Long tenantId, @RequestParam("dtuicTenantId") Long dtuicTenantId,
                                                               @RequestParam("projectIds") List<Long> projectIds, @RequestParam("appType") Integer appType,
                                                               @RequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByTypes(tenantId, dtuicTenantId, projectIds, appType, taskTypes);
    }

    @ApiOperation(value = "校验任务资源参数限制")
    @RequestMapping(value = "/checkResourceLimit", method = {RequestMethod.POST})
    public List<String> checkResourceLimit(@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                           @RequestParam("taskType") Integer taskType,
                                           @RequestParam("resourceParams") String resourceParams) {
        return scheduleTaskShadeService.checkResourceLimit(dtuicTenantId, taskType, resourceParams, null);
    }

    @ApiOperation(value = "模糊查询任务")
    @RequestMapping(value = "/findFuzzyTaskNameByCondition", method = {RequestMethod.POST})
    public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(@RequestParam("name") String name,
                                                                      @RequestParam("appType") Integer appType,
                                                                      @RequestParam("uicTenantId") Long uicTenantId,
                                                                      @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.findFuzzyTaskNameByCondition(name, appType, uicTenantId, projectId);
    }

    @RequestMapping(value = "/findTaskRuleTask", method = {RequestMethod.POST})
    public ScheduleDetailsVO findTaskRuleTask(@RequestParam("taskId") Long taskId,
                                                          @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskRuleTask(taskId, appType);
    }

    @RequestMapping(value = "/checkCronExpression",method = {RequestMethod.POST})
    public CronExceptionVO checkCronExpression(@RequestParam("cron") String cron, @RequestParam("minPeriod") Long minPeriod){
        return scheduleTaskShadeService.checkCronExpression(cron,Objects.isNull(minPeriod)?300L:minPeriod);
    }
    @RequestMapping(value = "/recentlyRunTime",method = {RequestMethod.POST})
    public List<String > recentlyRunTime(@RequestParam("startDate")String startDate, @RequestParam("endDate")String endDate,
                                         @RequestParam("cron")String cron, @RequestParam("num")Integer num){
        if (StringUtils.isBlank(startDate)) {
            startDate = DateTime.now().withTime(0, 0, 0, 0).toString(DateUtil.DATE_FORMAT);
        }
        if (StringUtils.isBlank(endDate)) {
            endDate = DateTime.now().plusDays(1).withTime(0, 0, 0, 0).toString(DateUtil.DATE_FORMAT);
        }
        return scheduleTaskShadeService.recentlyRunTime(startDate,endDate,cron, Objects.isNull(num)?10:num);
    }

    @RequestMapping(value = "/taskType",method = {RequestMethod.POST})
    public List<TaskTypeVO> getTaskType() {
        return scheduleTaskShadeService.getTaskType();
    }


}
