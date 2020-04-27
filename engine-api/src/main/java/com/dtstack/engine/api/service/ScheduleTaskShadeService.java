package com.dtstack.engine.api.service;

import com.dtstack.engine.api.annotation.Param;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskShadeService {

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    public void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO);

    /**
     * web 接口
     * task删除时触发同步清理
     */
    public void deleteTask(@Param("taskId") Long taskId, @Param("modifyUserId") long modifyUserId, @Param("appType") Integer appType);


    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<ScheduleTaskShade> getTasksByName(@Param("projectId") long projectId,
                                                  @Param("name") String name, @Param("appType") Integer appType);

    public ScheduleTaskShade getByName(@Param("projectId") long projectId,
                                       @Param("name") String name, @Param("appType") Integer appType,@Param("flowId")Long flowId);

    public void updateTaskName(@Param("taskId") long id, @Param("taskName") String taskName, @Param("appType") Integer appType);

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto);


    public ScheduleTaskShade getBatchTaskById(@Param("id") Long taskId, @Param("appType") Integer appType);

    public Map<String, Object> queryTasks(@Param("tenantId") Long tenantId,
                                          @Param("projectId") Long projectId,
                                          @Param("name") String name,
                                          @Param("ownerId") Long ownerId,
                                          @Param("startTime") Long startTime,
                                          @Param("endTime") Long endTime,
                                          @Param("scheduleStatus") Integer scheduleStatus,
                                          @Param("taskType") String taskTypeList,
                                          @Param("taskPeriodId") String periodTypeList,
                                          @Param("currentPage") Integer currentPage,
                                          @Param("pageSize") Integer pageSize, @Param("searchType") String searchType,
                                          @Param("appType") Integer appType);


    /**
     * 冻结任务
     *
     * @param taskIdList
     * @param scheduleStatus
     * @param projectId
     * @param userId
     * @param appType
     */
    public void frozenTask(@Param("taskIdList") List<Long> taskIdList, @Param("scheduleStatus") int scheduleStatus,
                           @Param("projectId") Long projectId, @Param("userId") Long userId,
                           @Param("appType") Integer appType);


    /**
     * 查询工作流下子节点
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("taskTypes")List<Integer> taskTypes,@Param("ownerId")Long ownerId);

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskShade> getFlowWorkSubTasks(@Param("taskId") Long taskId, @Param("appType") Integer appType,@Param("taskTypes")List<Integer> taskTypes,@Param("ownerId")Long ownerId);


    public ScheduleTaskShade findTaskId(@Param("taskId") Long taskId, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    /**
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple  不查询sql
     * @return
     */
    public List<ScheduleTaskShade> findTaskIds(@Param("taskIds") List<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType, @Param("isSimple") boolean isSimple);


    /**
     * 保存任务提交engine的额外信息
     *
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    public void info(@Param("taskId") Long taskId, @Param("appType") Integer appType, @Param("extraInfo") String info);


    public List<Map<String, Object>> listDependencyTask(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("name") String name, @Param("projectId") Long projectId);


    public List<Map<String, Object>> listByTaskIdsNotIn(@Param("taskIds") List<Long> taskId, @Param("appType") Integer appType, @Param("projectId") Long projectId);

    /**
     * 根据任务类型查询已提交到task服务的任务数
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    public Map<String ,Object> countTaskByType(@Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId,
                                               @Param("projectId") Long projectId, @Param("appType") Integer appType,
                                               @Param("taskTypes") List<Integer> taskTypes);


}
