package com.dtstack.task.dao;

import com.dtstack.dtcenter.common.pager.PageQuery;
import com.dtstack.task.domain.BatchTaskShade;
import com.dtstack.task.dto.BatchTaskForFillDataDTO;
import com.dtstack.task.dto.BatchTaskShadeDTO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/5/4
 */
public interface BatchTaskShadeDao {

    BatchTaskShade getOne(@Param("taskId") long taskId ,@Param("appType")Integer appType);

    List<BatchTaskShade> listTaskByStatus(@Param("startId") Long startId, @Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus, @Param("batchTaskSize") Integer batchTaskSize);

    Integer countTaskByStatus(@Param("submitStatus") Integer submitStatus, @Param("projectScheduleStatus") Integer projectScheduleStatus);

    List<BatchTaskShade> listByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted,@Param("appType")Integer appType);

    List<BatchTaskShade> listByNameLike(@Param("projectId") long projectId, @Param("name") String name,@Param("appType")Integer appType,@Param("ownerId") Long ownerId);

    List<BatchTaskShade> listByName(@Param("projectId") long projectId, @Param("name") String name);

    List<BatchTaskShade> listByNameLikeFront(@Param("projectId") long projectId, @Param("name") String name);

    List<BatchTaskShade> listByNameLikeTail(@Param("projectId") long projectId, @Param("name") String name);

    BatchTaskShade getByName(@Param("projectId") long projectId, @Param("name") String name);

    List<Map<String,Object>> listDependencyTask(@Param("projectId") long projectId, @Param("name") String name, @Param("taskIds") List<Long> taskIds);

    List<Map<String,Object>> listByTaskIdsNotIn(@Param("projectId") long projectId, @Param("taskIds") List<Long> taskIds);

    Integer updateTaskName(@Param("taskId") long taskId, @Param("name") String name,@Param("appType")Integer appType);

    Integer delete(@Param("taskId") long taskId, @Param("userId") long modifyUserId,@Param("appType")Integer appType);

    Integer insert(BatchTaskShade batchTaskShade);

    Integer update(BatchTaskShade batchTaskShade);

    List<BatchTaskShade> listByType(@Param("projectId") Long projectId, @Param("type") Integer type, @Param("taskName") String taskName);

    List<BatchTaskShade> generalQuery(PageQuery<BatchTaskShadeDTO> pageQuery);

    Integer generalCount(@Param("model") Object model);

    Integer batchUpdateTaskScheduleStatus(@Param("taskIds") List<Long> taskIds, @Param("scheduleStatus") Integer scheduleStatus,@Param("appType")Integer appType);

    List<BatchTaskShade> simpleQuery(PageQuery<BatchTaskShadeDTO> pageQuery);

    Integer simpleCount(@Param("model") BatchTaskShadeDTO pageQuery);

    Integer updatePublishStatus(@Param("list") List<Long> list, @Param("status") Integer status);

    Integer countPublishToProduce(@Param("projectId") Long projectId,@Param("appType")Integer appType);

    List<BatchTaskForFillDataDTO> listSimpleTaskByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted);

    String getSqlTextById(@Param("id") Long id);

    BatchTaskShade getWorkFlowTopNode(@Param("workFlowId") Long workFlowId);

    /**
     *  ps- 省略了一些大字符串 如 sql_text、task_params
     * @param taskIds
     * @param isDeleted
     * @return
     */
    List<BatchTaskShade> listSimpleByTaskIds(@Param("taskIds") Collection<Long> taskIds, @Param("isDeleted") Integer isDeleted);

    BatchTaskShade getOneWithDeleted(@Param("id") Long id, @Param("isDeleted") Integer isDeleted,@Param("appType") Integer appType);

    void updateTaskExtInfo(@Param("taskId") long taskId, @Param("appType")Integer appType, @Param("extraInfo") String extraInfo);

    String getExtInfoByTaskId(@Param("taskId") long taskId, @Param("appType")Integer appType);

    List<BatchTaskShade> getExtInfoByTaskIds(@Param("taskIds") List<Long> taskIds, @Param("appType")Integer appType);

    BatchTaskShade getById(@Param("id") Long id);
}
