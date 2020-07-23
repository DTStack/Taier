package com.dtstack.engine.api.service;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.ScheduleTaskVO;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public interface ScheduleTaskShadeService extends DtInsightServer {

    /**
     * web 接口
     * 例如：离线计算BatchTaskService.publishTaskInfo 触发 batchTaskShade 保存task的必要信息
     */
    @RequestLine("POST /node/scheduleTaskShade/addOrUpdate")
    void addOrUpdate(ScheduleTaskShadeDTO batchTaskShadeDTO);

    /**
     * web 接口
     * task删除时触发同步清理
     */
    @RequestLine("POST /node/scheduleTaskShade/deleteTask")
    void deleteTask(Long taskId, long modifyUserId, Integer appType);


    /**
     * 数据开发-根据项目id,任务名 获取任务列表
     *
     * @param projectId
     * @return
     * @author toutian
     */
    @RequestLine("POST /node/scheduleTaskShade/getTasksByName")
    List<ScheduleTaskShade> getTasksByName(long projectId,
                                           String name, Integer appType);

    @RequestLine("POST /node/scheduleTaskShade/getByName")
    ScheduleTaskShade getByName(long projectId,
                                String name, Integer appType, Long flowId);

    @RequestLine("POST /node/scheduleTaskShade/updateTaskName")
    void updateTaskName(long id, String taskName, Integer appType);

    /**
     * 分页查询已提交的任务
     */
    @RequestLine("POST /node/scheduleTaskShade/pageQuery")
    PageResult<List<ScheduleTaskShadeVO>> pageQuery(ScheduleTaskShadeDTO dto);


    @RequestLine("POST /node/scheduleTaskShade/getBatchTaskById")
    ScheduleTaskShade getBatchTaskById(Long taskId, Integer appType);

    @RequestLine("POST /node/scheduleTaskShade/queryTasks")
    Map<String, Object> queryTasks(Long tenantId,
                                   Long projectId,
                                   String name,
                                   Long ownerId,
                                   Long startTime,
                                   Long endTime,
                                   Integer scheduleStatus,
                                   String taskTypeList,
                                   String periodTypeList,
                                   Integer currentPage,
                                   Integer pageSize, String searchType,
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
    @RequestLine("POST /node/scheduleTaskShade/frozenTask")
    void frozenTask(List<Long> taskIdList, int scheduleStatus,
                    Long projectId, Long userId,
                    Integer appType);


    /**
     * 查询工作流下子节点
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/dealFlowWorkTask")
    ScheduleTaskVO dealFlowWorkTask(Long taskId, Integer appType, List<Integer> taskTypes, Long ownerId);

    /**
     * 获取任务流下的所有子任务
     *
     * @param taskId
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/getFlowWorkSubTasks")
    List<ScheduleTaskShade> getFlowWorkSubTasks(Long taskId, Integer appType, List<Integer> taskTypes, Long ownerId);


    @RequestLine("POST /node/scheduleTaskShade/findTaskId")
    ScheduleTaskShade findTaskId(Long taskId, Integer isDeleted, Integer appType);

    /**
     * @param taskIds
     * @param isDeleted
     * @param appType
     * @param isSimple  不查询sql
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/findTaskIds")
    List<ScheduleTaskShade> findTaskIds(List<Long> taskIds, Integer isDeleted, Integer appType, boolean isSimple);


    /**
     * 保存任务提交engine的额外信息
     *
     * @param taskId
     * @param appType
     * @param info
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/info")
    void info(Long taskId, Integer appType, String info);


    @RequestLine("POST /node/scheduleTaskShade/listDependencyTask")
    List<Map<String, Object>> listDependencyTask(List<Long> taskId, Integer appType, String name, Long projectId);


    @RequestLine("POST /node/scheduleTaskShade/listByTaskIdsNotIn")
    List<Map<String, Object>> listByTaskIdsNotIn(List<Long> taskId, Integer appType, Long projectId);

    /**
     * 根据任务类型查询已提交到task服务的任务数
     *
     * @param tenantId
     * @param dtuicTenantId
     * @param projectId
     * @param appType
     * @param taskTypes
     * @return
     */
    @RequestLine("POST /node/scheduleTaskShade/countTaskByType")
    Map<String, Object> countTaskByType(Long tenantId, Long dtuicTenantId,
                                        Long projectId, Integer appType,
                                        List<Integer> taskTypes);

    @RequestLine("POST /node/scheduleTaskShade/countTaskByTypes")
    List<Map<String, Object>> countTaskByTypes(Long tenantId, Long dtuicTenantId,
                                               List<Long> projectIds, Integer appType,
                                               List<Integer> taskTypes);
}
