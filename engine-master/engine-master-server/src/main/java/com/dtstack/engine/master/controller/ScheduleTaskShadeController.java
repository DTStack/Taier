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
import com.dtstack.engine.master.router.DtRequestParam;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/node/scheduleTaskShade")
@Api(value = "/node/scheduleTaskShade", tags = {"任务接口"})
public class ScheduleTaskShadeController implements com.dtstack.engine.api.service.ScheduleTaskShadeService {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;


    @RequestMapping(value="/addOrUpdate", method = {RequestMethod.POST})
    @ApiOperation(value = "添加或更新任务", notes = "例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息")
    public void addOrUpdate(@RequestBody ScheduleTaskShadeDTO batchTaskShadeDTO) {
        scheduleTaskShadeService.addOrUpdate(batchTaskShadeDTO);
    }

    @RequestMapping(value="/deleteTask", method = {RequestMethod.POST})
    @ApiOperation(value = "删除任务", notes = "task删除时触发同步清理")
    public void deleteTask(@DtRequestParam("taskId") Long taskId, @DtRequestParam("modifyUserId") long modifyUserId, @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.deleteTask(taskId, modifyUserId, appType);
    }

    @RequestMapping(value="/getTasksByName", method = {RequestMethod.POST})
    @ApiOperation(value = "根据项目id,任务名 获取任务列表")
    public List<ScheduleTaskShade> getTasksByName(@DtRequestParam("projectId") long projectId,
                                                  @DtRequestParam("name") String name, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getTasksByName(projectId, name, appType);
    }

    @RequestMapping(value="/getByName", method = {RequestMethod.POST})
    public ScheduleTaskShade getByName(@DtRequestParam("projectId") long projectId,
                                       @DtRequestParam("name") String name, @DtRequestParam("appType") Integer appType,@DtRequestParam("flowId")Long flowId) {
        return scheduleTaskShadeService.getByName(projectId, name, appType, flowId);
    }

    @RequestMapping(value="/updateTaskName", method = {RequestMethod.POST})
    public void updateTaskName(@DtRequestParam("taskId") long id, @DtRequestParam("taskName") String taskName, @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.updateTaskName(id, taskName, appType);
    }

    @RequestMapping(value="/pageQuery", method = {RequestMethod.POST})
    @ApiOperation(value = "分页查询已提交的任务")
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(@RequestBody ScheduleTaskShadeDTO dto) {
        return scheduleTaskShadeService.pageQuery(dto);
    }


    @RequestMapping(value="/getBatchTaskById", method = {RequestMethod.POST})
    public ScheduleTaskShade getBatchTaskById(@DtRequestParam("id") Long taskId, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.getBatchTaskById(taskId, appType);
    }

    @RequestMapping(value="/queryTasks", method = {RequestMethod.POST})
    public Map<String, Object> queryTasks(@DtRequestParam("tenantId") Long tenantId,
                                          @DtRequestParam("projectId") Long projectId,
                                          @DtRequestParam("name") String name,
                                          @DtRequestParam("ownerId") Long ownerId,
                                          @DtRequestParam("startTime") Long startTime,
                                          @DtRequestParam("endTime") Long endTime,
                                          @DtRequestParam("scheduleStatus") Integer scheduleStatus,
                                          @DtRequestParam("taskType") String taskTypeList,
                                          @DtRequestParam("taskPeriodId") String periodTypeList,
                                          @DtRequestParam("currentPage") Integer currentPage,
                                          @DtRequestParam("pageSize") Integer pageSize, @DtRequestParam("searchType") String searchType,
                                          @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.queryTasks(tenantId, projectId, name, ownerId, startTime, endTime, scheduleStatus, taskTypeList, periodTypeList, currentPage, pageSize, searchType, appType);
    }


    @RequestMapping(value="/frozenTask", method = {RequestMethod.POST})
    @ApiOperation(value = "冻结任务")
    public void frozenTask(@DtRequestParam("taskIdList") List<Long> taskIdList, @DtRequestParam("scheduleStatus") int scheduleStatus,
                           @DtRequestParam("projectId") Long projectId, @DtRequestParam("userId") Long userId,
                           @DtRequestParam("appType") Integer appType) {
        scheduleTaskShadeService.frozenTask(taskIdList, scheduleStatus, projectId, userId, appType);
    }


    @RequestMapping(value="/dealFlowWorkTask", method = {RequestMethod.POST})
    @ApiOperation(value = "查询工作流下子节点")
    public ScheduleTaskVO dealFlowWorkTask(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("taskTypes")List<Integer> taskTypes, @DtRequestParam("ownerId")Long ownerId) {
        return scheduleTaskShadeService.dealFlowWorkTask(taskId, appType, taskTypes, ownerId);
    }

    @RequestMapping(value="/getFlowWorkSubTasks", method = {RequestMethod.POST})
    @ApiOperation(value = "获取任务流下的所有子任务")
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType,@DtRequestParam("taskTypes")List<Integer> taskTypes,@DtRequestParam("ownerId")Long ownerId) {
        return scheduleTaskShadeService.getFlowWorkSubTasks(taskId, appType, taskTypes, ownerId);
    }


    @RequestMapping(value="/findTaskId", method = {RequestMethod.POST})
    public ScheduleTaskShade findTaskId(@DtRequestParam("taskId") Long taskId, @DtRequestParam("isDeleted") Integer isDeleted, @DtRequestParam("appType") Integer appType) {
        return scheduleTaskShadeService.findTaskId(taskId, isDeleted, appType);
    }


    @RequestMapping(value="/findTaskIds", method = {RequestMethod.POST})
    @ApiImplicitParams({
            @ApiImplicitParam(name="isSimple",value="若为true，则不查询sql值", dataType = "boolean", required = true)
    })
    public List<ScheduleTaskShade> findTaskIds(@DtRequestParam("taskIds") List<Long> taskIds, @DtRequestParam("isDeleted") Integer isDeleted, @DtRequestParam("appType") Integer appType, @DtRequestParam("isSimple") boolean isSimple) {
        return scheduleTaskShadeService.findTaskIds(taskIds, isDeleted, appType, isSimple);
    }


    @RequestMapping(value="/info", method = {RequestMethod.POST})
    @ApiOperation(value = "保存任务提交engine的额外信息")
    public void info(@DtRequestParam("taskId") Long taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("extraInfo") String info) {
        scheduleTaskShadeService.info(taskId, appType, info);
    }

    @RequestMapping(value="/listDependencyTask", method = {RequestMethod.POST})
    public List<Map<String, Object>> listDependencyTask(@DtRequestParam("taskIds") List<Long> taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("name") String name, @DtRequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listDependencyTask(taskId, appType, name, projectId);
    }

    @RequestMapping(value="/listByTaskIdsNotIn", method = {RequestMethod.POST})
    public List<Map<String, Object>> listByTaskIdsNotIn(@DtRequestParam("taskIds") List<Long> taskId, @DtRequestParam("appType") Integer appType, @DtRequestParam("projectId") Long projectId) {
        return scheduleTaskShadeService.listByTaskIdsNotIn(taskId, appType, projectId);
    }

    @RequestMapping(value="/countTaskByType", method = {RequestMethod.POST})
    @ApiOperation(value = "根据任务类型查询已提交到task服务的任务数")
    public Map<String ,Object> countTaskByType(@DtRequestParam("tenantId") Long tenantId,@DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                               @DtRequestParam("projectId") Long projectId, @DtRequestParam("appType") Integer appType,
                                               @DtRequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByType(tenantId, dtuicTenantId, projectId, appType, taskTypes);
    }

    @RequestMapping(value="/countTaskByTypes", method = {RequestMethod.POST})
    public List<Map<String ,Object>> countTaskByTypes(@DtRequestParam("tenantId") Long tenantId,@DtRequestParam("dtuicTenantId") Long dtuicTenantId,
                                               @DtRequestParam("projectIds") List<Long> projectIds, @DtRequestParam("appType") Integer appType,
                                               @DtRequestParam("taskTypes") List<Integer> taskTypes) {
        return scheduleTaskShadeService.countTaskByTypes(tenantId, dtuicTenantId, projectIds, appType, taskTypes);
    }
}
