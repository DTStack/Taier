package com.dtstack.engine.master.controller;

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
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import com.dtstack.engine.master.router.DtHeader;
import com.dtstack.engine.master.router.DtParamOrHeader;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.*;

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
    public void infoCommit(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("extraInfo") String info, @DtRequestParam("commitId") String commitId) {
        scheduleTaskShadeService.infoCommit(taskId, appType, info, commitId);
    }

    @RequestMapping(value = "/taskCommit", method = {RequestMethod.POST})
    @ApiOperation(value = "提交任务")
    public void taskCommit(@DtRequestParam("commitId") String commitId) {
        scheduleTaskShadeService.taskCommit(commitId);
    }

    @RequestMapping(value = "/info", method = {RequestMethod.POST})
    @ApiOperation(value = "保存任务提交engine的额外信息")
    public void info(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("extraInfo") String info) {
        scheduleTaskShadeService.info(taskId, appType, info);
    }

    @RequestMapping(value = "/deleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "删除任务", notes = "task删除时触发同步清理")
    public void deleteTask(@DtRequestParam("taskId") Long taskId, @DtRequestParam("modifyUserId") long modifyUserId, @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.deleteTask(taskId, modifyUserId, appType);
    }

    @RequestMapping(value = "/getNotDeleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "获得其他依赖的接口", notes = "task删除时触发同步清理")
    public List<NotDeleteTaskVO> getNotDeleteTask(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getNotDeleteTask(taskId, appType);
    }

    @RequestMapping(value = "/getTasksByName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据项目id,任务名 获取任务列表")
    public List<ScheduleTaskShade> getTasksByName(@DtRequestParam("projectId") long projectId,
                                                  @DtRequestParam("name") String name, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTasksByName(projectId, name, appType);
    }

    @RequestMapping(value = "/getByName", method = {RequestMethod.POST})
    public ScheduleTaskShade getByName(@DtRequestParam("projectId") long projectId,
                                       @DtRequestParam("name") String name, @DtRequestParam("appType") Integer appType, @DtRequestParam("flowId") Long flowId) {
        return scheduleTaskShadeService.getByName(projectId, name, appType, flowId);
    }

    @RequestMapping(value = "/updateTaskName", method = {RequestMethod.POST})
    public void updateTaskName(@DtRequestParam("taskId") long id, @DtRequestParam("taskName") String taskName, @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.updateTaskName(id, taskName, appType);
    }

    @RequestMapping(value = "/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询已提交的任务")
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(@RequestBody ScheduleTaskShadeDTO dto) {
        return scheduleTaskShadeService.pageQuery(dto);
    }

    @RequestMapping(value = "/getBatchTaskById", method = {RequestMethod.POST})
    public ScheduleTaskShade getBatchTaskById(@DtRequestParam("id") Long taskId, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getBatchTaskById(taskId, appType);
    }

    @RequestMapping(value = "/queryTasks", method = {RequestMethod.POST})
    public ScheduleTaskShadePageVO queryTasks(@DtRequestParam("tenantId") Long tenantId,
                                              @DtParamOrHeader(value = "dtTenantId",header = "cookie",cookie = "dt_tenant_id") Long dtTenantId,
                                              @DtRequestParam("projectId") Long projectId,
                                              @DtRequestParam("name") String name,
                                              @DtRequestParam("ownerId") Long ownerId,
                                              @DtRequestParam("startTime") Long startTime,
                                              @DtRequestParam("endTime") Long endTime,
                                              @DtRequestParam("scheduleStatus") Integer scheduleStatus,
                                              @DtRequestParam("taskType") String taskTypeList,
                                              @DtRequestParam("taskPeriodId") String periodTypeList,
                                              @DtRequestParam("currentPage") Integer currentPage,
                                              @DtRequestParam("pageSize") Integer pageSize,
                                              @DtRequestParam("searchType") String searchType,
                                              @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.queryTasks(tenantId, dtTenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, currentPage, pageSize, searchType, appType);
    }


    @RequestMapping(value = "/frozenTask", method = {RequestMethod.POST})
    @ApiOperation(value = "冻结任务")
    public void frozenTask(@DtRequestParam("taskIdList") List<Long> taskIdList, @DtRequestParam("scheduleStatus") int scheduleStatus,
                           @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.frozenTask(taskIdList, scheduleStatus, appType);
    }


    @RequestMapping(value = "/dealFlowWorkTask", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    public ScheduleTaskVO dealFlowWorkTask(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("taskTypes") List<Integer> taskTypes, @DtRequestParam("ownerId") Long ownerId) {
        return scheduleTaskShadeService.dealFlowWorkTask(taskId, appType, taskTypes, ownerId);
    }

    @RequestMapping(value = "/getFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务流下的所有子任务")
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("taskTypes") List<Integer> taskTypes, @DtRequestParam("ownerId") Long ownerId) {
        return scheduleTaskShadeService.getFlowWorkSubTasks(taskId, appType, taskTypes, ownerId);
    }


    @RequestMapping(value = "/findTaskId", method = {RequestMethod.POST})
    public ScheduleTaskShade findTaskId(@DtRequestParam("taskId") Long taskId, @DtRequestParam("isDeleted") Integer isDeleted, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskId(taskId, isDeleted, appType);
    }


    @RequestMapping(value = "/findTaskIds", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isSimple", value = "若为true，则不查询sql值", dataType = "boolean", required = true)
    })
    public List<ScheduleTaskShade> findTaskIds(@DtRequestParam("taskIds") List<Long> taskIds, @DtRequestParam("isDeleted") Integer isDeleted, @DtRequestParam("appType") Integer appType, @DtRequestParam("isSimple") boolean isSimple) {
        return scheduleTaskShadeService.findTaskIds(taskIds, isDeleted, appType, isSimple);
    }


    @RequestMapping(value = "/listDependencyTask", method = {RequestMethod.POST})
    public List<Map<String, Object>> listDependencyTask(@DtRequestParam("taskIds") List<Long> taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("name") String name, @DtRequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listDependencyTask(taskId, name, projectId);
    }

    @RequestMapping(value = "/listByTaskIdsNotIn", method = {RequestMethod.POST})
    public List<Map<String, Object>> listByTaskIdsNotIn(@DtRequestParam("taskIds") List<Long> taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listByTaskIdsNotIn(taskId, appType, projectId);
    }

    @RequestMapping(value = "/countTaskByType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public ScheduleTaskShadeCountTaskVO countTaskByType(@DtRequestParam("tenantId") Long tenantId, @DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                                        @DtRequestParam("projectId") Long projectId, @DtRequestParam("appType") Integer appType,
                                                        @DtRequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByType(tenantId, dtuicTenantId, projectId, appType, taskTypes);
    }

    @RequestMapping(value = "/getTaskByIds", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public List<ScheduleTaskShade> getTaskByIds(@DtRequestParam("taskIds") List<Long> taskIds, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTaskByIds(taskIds, appType);
    }

    @RequestMapping(value = "/countTaskByTypes", method = {RequestMethod.POST})
    public List<ScheduleTaskShadeCountTaskVO> countTaskByTypes(@DtRequestParam("tenantId") Long tenantId, @DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                                               @DtRequestParam("projectIds") List<Long> projectIds, @DtRequestParam("appType") Integer appType,
                                                               @DtRequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByTypes(tenantId, dtuicTenantId, projectIds, appType, taskTypes);
    }

    @ApiOperation(value = "校验任务资源参数限制")
    @RequestMapping(value = "/checkResourceLimit", method = {RequestMethod.POST})
    public List<String> checkResourceLimit(@DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                           @DtRequestParam("taskType") Integer taskType,
                                           @DtRequestParam("resourceParams") String resourceParams) {
        return scheduleTaskShadeService.checkResourceLimit(dtuicTenantId, taskType, resourceParams, null);
    }

    @ApiOperation(value = "模糊查询任务")
    @RequestMapping(value = "/findFuzzyTaskNameByCondition", method = {RequestMethod.POST})
    public List<ScheduleTaskShadeTypeVO> findFuzzyTaskNameByCondition(@DtRequestParam("name") String name,
                                                                      @DtRequestParam("appType") Integer appType,
                                                                      @DtRequestParam("uicTenantId") Long uicTenantId,
                                                                      @DtRequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.findFuzzyTaskNameByCondition(name, appType, uicTenantId, projectId);
    }

    @RequestMapping(value = "/findTaskRuleTask", method = {RequestMethod.POST})
    public ScheduleDetailsVO findTaskRuleTask(@DtRequestParam("taskId") Long taskId,
                                                          @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskRuleTask(taskId, appType);
    }

    @RequestMapping(value = "/checkCronExpression",method = {RequestMethod.POST})
    public String checkCronExpression(@DtRequestParam("cron") String cron){
        return scheduleTaskShadeService.checkCronExpression(cron);
    }
    @RequestMapping(value = "/recentlyRunTime",method = {RequestMethod.POST})
    public List<String > recentlyRunTime(@DtRequestParam("startDate")String startDate,@DtRequestParam("endDate")String endDate,
                                         @DtRequestParam("cron")String cron,@DtRequestParam("num")Integer num){
        return scheduleTaskShadeService.recentlyRunTime(startDate,endDate,cron, Objects.isNull(num)?10:num);
    }
}
