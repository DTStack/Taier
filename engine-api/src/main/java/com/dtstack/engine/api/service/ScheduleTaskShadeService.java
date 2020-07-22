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
    public void deleteTask( Long taskId,  long modifyUserId,  Integer appType);


    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    public List<ScheduleTaskShade> getTasksByName( long projectId,
                                                   String name,  Integer appType);

    public ScheduleTaskShade getByName( long projectId,
                                        String name,  Integer appType,Long flowId);

    public void updateTaskName( long id,  String taskName,  Integer appType);

    /**
     * 分页查询已提交的任务
     */
    public PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto);


    public ScheduleTaskShade getBatchTaskById( Long taskId,  Integer appType);

    public Map<String, Object> queryTasks( Long tenantId,
                                           Long projectId,
                                           String name,
                                           Long ownerId,
                                           Long startTime,
                                           Long endTime,
                                           Integer scheduleStatus,
                                           String taskTypeList,
                                           String periodTypeList,
                                           Integer currentPage,
                                           Integer pageSize,  String searchType,
                                           Integer appType);


    /**
     * 冻结任务
     *
     * @param taskIdList
     * @param scheduleStatus
     * @param projectId
     * @param userId
     * @param appType
     */
    public void frozenTask( List<Long> taskIdList,  int scheduleStatus,
                            Long projectId,  Long userId,
                            Integer appType);


    /**
     * 查询工作流下子节点
     *
     * @param taskId
     * @return
     */
    public ScheduleTaskVO dealFlowWorkTask( Long taskId,  Integer appType,List<Integer> taskTypes,Long ownerId);

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    public List<ScheduleTaskShade> getFlowWorkSubTasks( Long taskId,  Integer appType,List<Integer> taskTypes,Long ownerId);


    public ScheduleTaskShade findTaskId( Long taskId,  Integer isDeleted,  Integer appType);

    /**
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple  不查询sql
     * @return
     */
    public List<ScheduleTaskShade> findTaskIds( List<Long> taskIds,  Integer isDeleted,  Integer appType,  boolean isSimple);


    /**
     * 保存任务提交engine的额外信息
     *
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    public void info( Long taskId,  Integer appType,  String info);


    public List<Map<String, Object>> listDependencyTask( List<Long> taskId,  Integer appType,  String name,  Long projectId);


    public List<Map<String, Object>> listByTaskIdsNotIn( List<Long> taskId,  Integer appType,  Long projectId);

    /**
     * 根据任务类型查询已提交到task服务的任务数
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    public Map<String ,Object> countTaskByType( Long tenantId, Long dtuicTenantId,
                                                Long projectId,  Integer appType,
                                                List<Integer> taskTypes);

    public List<Map<String ,Object>> countTaskByTypes( Long tenantId, Long dtuicTenantId,
                      List<Long> projectIds,  Integer appType,
                      List<Integer> taskTypes);
}
