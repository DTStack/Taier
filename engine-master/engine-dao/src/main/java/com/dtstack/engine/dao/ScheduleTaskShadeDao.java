package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskForFillDataDTO;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface ScheduleTaskShadeDao {

    ScheduleTaskShade getOne(@Param("taskId") long taskId , @Param("appType")Integer appType);

    List<ScheduleTaskShade> listTaskByStatus(@Param("startId") Long startId, @Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus, @Param("batchTaskSize") Integer batchTaskSize);

    Integer countTaskByStatus(@Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus);

    List<Map<String,Object>> countTaskByType(@Param("tenantId") Long tenantId,@Param("dtuicTenantId") Long dtuicTenantId,@Param("projectIds")List<Long> projectIds,@Param("appType")Integer appType,@Param("taskTypes")List<Integer> taskTypes);

    List<ScheduleTaskShade> listByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted, @Param("appType")Integer appType);

    List<ScheduleTaskShade> listByNameLike(@Param("projectId") long projectId, @Param("name") String name, @Param("appType")Integer appType, @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds);

    List<ScheduleTaskShade> listByNameLikeWithSearchType(@Param("projectId") long projectId, @Param("name") String name, @Param("appType")Integer appType,
                                                   @Param("ownerId") Long ownerId, @Param("projectIds") List<Long> projectIds,@Param("searchType")Integer searchType);

    List<ScheduleTaskShade> listByName(@Param("projectId") long projectId, @Param("name") String name);

    List<ScheduleTaskShade> listByNameLikeFront(@Param("projectId") long projectId, @Param("name") String name);

    List<ScheduleTaskShade> listByNameLikeTail(@Param("projectId") long projectId, @Param("name") String name);

    ScheduleTaskShade getByName(@Param("projectId") long projectId, @Param("name") String name, @Param("appType") Integer appType,@Param("flowId") Long flowId);

    List<Map<String,Object>> listDependencyTask(@Param("projectId") long projectId, @Param("name") String name, @Param("taskIds") List<Long> taskIds);

    List<Map<String,Object>> listByTaskIdsNotIn(@Param("projectId") long projectId, @Param("taskIds") List<Long> taskIds);

    Integer updateTaskName(@Param("taskId") long taskId, @Param("name") String name,@Param("appType")Integer appType);

    Integer delete(@Param("taskId") long taskId, @Param("userId") long modifyUserId,@Param("appType")Integer appType);

    Integer insert(ScheduleTaskShade batchTaskShade);

    Integer update(ScheduleTaskShade batchTaskShade);

    List<ScheduleTaskShade> listByType(@Param("projectId") Long projectId, @Param("type") Integer type, @Param("taskName") String taskName);

    List<ScheduleTaskShade> generalQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer generalCount(@Param("model") Object model);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus,@Param("appType")Integer appType);

    List<ScheduleTaskShade> simpleQuery(PageQuery<ScheduleTaskShadeDTO> pageQuery);

    Integer simpleCount(@Param("model") ScheduleTaskShadeDTO pageQuery);

    Integer updatePublishStatus(@Param("list") List<Long> list, @Param("status") Integer status);

    Integer countPublishToProduce(@Param("projectId") Long projectId,@Param("appType")Integer appType);

    List<ScheduleTaskForFillDataDTO> listSimpleTaskByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType")Integer appType);

    String getSqlTextById(@Param("id") Long id);

    ScheduleTaskShade getWorkFlowTopNode(@Param("workFlowId") Long workFlowId);

    /**
     *  ps- 省略了一些大字符串 如 sql_text、task_params
     * @param taskIds
     * @param isDeleted
     * @return
     */
    List<ScheduleTaskShade> listSimpleByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType") Integer appType);

    ScheduleTaskShade getOneWithDeleted(@Param("id") Long id, @Param("isDeleted") Integer isDeleted, @Param("appType") Integer appType);

    void updateTaskExtInfo(@Param("taskId") long taskId, @Param("appType")Integer appType, @Param("extraInfo") String extraInfo);

    String getExtInfoByTaskId(@Param("taskId") long taskId, @Param("appType")Integer appType);

    List<ScheduleTaskShade> getExtInfoByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType")Integer appType);

    ScheduleTaskShade getById(@Param("id") Long id);

    void updateProjectScheduleStatus(@Param("projectId")Long projectId,@Param("appType")Integer appType,@Param("scheduleStatus") Integer scheduleStatus);
}
