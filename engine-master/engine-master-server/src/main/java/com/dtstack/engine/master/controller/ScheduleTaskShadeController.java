package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.engine.master.impl.ScheduleTaskShadeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/scheduleTaskShade")
@Api(value = "/node/scheduleTaskShade", tags = {"任务接口"})
public class ScheduleTaskShadeController {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;


    @RequestMapping(value="/addOrUpdate", method = {RequestMethod.POST})
    @ApiOperation(value = "添加或更新任务", notes = "例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息")
    public void addOrUpdate(@RequestBody ScheduleTaskShadeDTO batchTaskShadeDTO) {
        scheduleTaskShadeService.addOrUpdate(batchTaskShadeDTO);
    }

    @RequestMapping(value="/deleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "删除任务", notes = "task删除时触发同步清理")
    public void deleteTask(@RequestParam("taskId") Long taskId, @RequestParam("modifyUserId") long modifyUserId, @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.deleteTask(taskId, modifyUserId, appType);
    }

    @RequestMapping(value="/getTasksByName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据项目id,任务名 获取任务列表")
    public List<ScheduleTaskShade> getTasksByName(@RequestParam("projectId") long projectId,
                                                  @RequestParam("name") String name, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTasksByName(projectId, name, appType);
    }

    @RequestMapping(value="/getByName", method = {RequestMethod.POST})
    public ScheduleTaskShade getByName(@RequestParam("projectId") long projectId,
                                       @RequestParam("name") String name, @RequestParam("appType") Integer appType,@RequestParam("flowId")Long flowId) {
        return scheduleTaskShadeService.getByName(projectId, name, appType, flowId);
    }

    @RequestMapping(value="/updateTaskName", method = {RequestMethod.POST})
    public void updateTaskName(@RequestParam("taskId") long id, @RequestParam("taskName") String taskName, @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.updateTaskName(id, taskName, appType);
    }

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询已提交的任务")
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(@RequestBody ScheduleTaskShadeDTO dto) {
        return scheduleTaskShadeService.pageQuery(dto);
    }


    @RequestMapping(value="/getBatchTaskById", method = {RequestMethod.POST})
    public ScheduleTaskShade getBatchTaskById(@RequestParam("id") Long taskId, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getBatchTaskById(taskId, appType);
    }

    @RequestMapping(value="/queryTasks", method = {RequestMethod.POST})
    public Map<String, Object> queryTasks(@RequestParam("tenantId") Long tenantId,
                                          @RequestParam("projectId") Long projectId,
                                          @RequestParam("name") String name,
                                          @RequestParam("ownerId") Long ownerId,
                                          @RequestParam("startTime") Long startTime,
                                          @RequestParam("endTime") Long endTime,
                                          @RequestParam("scheduleStatus") Integer scheduleStatus,
                                          @RequestParam("taskType") String taskTypeList,
                                          @RequestParam("taskPeriodId") String periodTypeList,
                                          @RequestParam("currentPage") Integer currentPage,
                                          @RequestParam("pageSize") Integer pageSize, @RequestParam("searchType") String searchType,
                                          @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.queryTasks(tenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, currentPage, pageSize, searchType, appType);
    }


    @RequestMapping(value="/frozenTask", method = {RequestMethod.POST})
    @ApiOperation(value = "冻结任务")
    public void frozenTask(@RequestParam("taskIdList") List<Long> taskIdList, @RequestParam("scheduleStatus") int scheduleStatus,
                           @RequestParam("projectId") Long projectId, @RequestParam("userId") Long userId,
                           @RequestParam("appType") Integer appType) {
        scheduleTaskShadeService.frozenTask(taskIdList, scheduleStatus, projectId, userId, appType);
    }


    @RequestMapping(value="/dealFlowWorkTask", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    public ScheduleTaskVO dealFlowWorkTask(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("taskTypes")List<Integer> taskTypes, @RequestParam("ownerId")Long ownerId) {
        return scheduleTaskShadeService.dealFlowWorkTask(taskId, appType, taskTypes, ownerId);
    }

    @RequestMapping(value="/getFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务流下的所有子任务")
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType,@RequestParam("taskTypes")List<Integer> taskTypes,@RequestParam("ownerId")Long ownerId) {
        return scheduleTaskShadeService.getFlowWorkSubTasks(taskId, appType, taskTypes, ownerId);
    }


    @RequestMapping(value="/findTaskId", method = {RequestMethod.POST})
    public ScheduleTaskShade findTaskId(@RequestParam("taskId") Long taskId, @RequestParam("isDeleted") Integer isDeleted, @RequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskId(taskId, isDeleted, appType);
    }


    @RequestMapping(value="/findTaskIds", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name="isSimple",value="若为true，则不查询sql值", dataType = "boolean", required = true)
    })
    public List<ScheduleTaskShade> findTaskIds(@RequestParam("taskIds") List<Long> taskIds, @RequestParam("isDeleted") Integer isDeleted, @RequestParam("appType") Integer appType, @RequestParam("isSimple") boolean isSimple) {
        return scheduleTaskShadeService.findTaskIds(taskIds, isDeleted, appType, isSimple);
    }


    @RequestMapping(value="/info", method = {RequestMethod.POST})
    @ApiOperation(value = "保存任务提交engine的额外信息")
    public void info(@RequestParam("taskId") Long taskId, @RequestParam("appType") Integer appType, @RequestParam("extraInfo") String info) {
        scheduleTaskShadeService.info(taskId, appType, info);
    }

    @RequestMapping(value="/listDependencyTask", method = {RequestMethod.POST})
    public List<Map<String, Object>> listDependencyTask(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("name") String name, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listDependencyTask(taskId, appType, name, projectId);
    }

    @RequestMapping(value="/listByTaskIdsNotIn", method = {RequestMethod.POST})
    public List<Map<String, Object>> listByTaskIdsNotIn(@RequestParam("taskIds") List<Long> taskId, @RequestParam("appType") Integer appType, @RequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listByTaskIdsNotIn(taskId, appType, projectId);
    }

    @RequestMapping(value="/countTaskByType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public Map<String ,Object> countTaskByType(@RequestParam("tenantId") Long tenantId,@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                               @RequestParam("projectId") Long projectId, @RequestParam("appType") Integer appType,
                                               @RequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByType(tenantId, dtuicTenantId, projectId, appType, taskTypes);
    }

    @RequestMapping(value="/countTaskByTypes", method = {RequestMethod.POST})
    List<Map<String ,Object>> countTaskByTypes(@RequestParam("tenantId") Long tenantId,@RequestParam("dtuicTenantId") Long dtuicTenantId,
                                               @RequestParam("projectIds") List<Long> projectIds, @RequestParam("appType") Integer appType,
                                               @RequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByTypes(tenantId, dtuicTenantId, projectIds, appType, taskTypes);
    }
}
