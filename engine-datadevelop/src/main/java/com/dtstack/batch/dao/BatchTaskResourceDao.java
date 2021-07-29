package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchTaskResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchTaskResourceDao {

    /**
     * @param taskId
     * @param resourceType  资源类型 -- ResourceRefType
     * @param projectId
     * @return
     */
    List<BatchTaskResource> listByTaskId(@Param("taskId") long taskId, @Param("resourceType") Integer resourceType, @Param("projectId") long projectId);

    BatchTaskResource getByTaskIdAndResourceId(@Param("taskId") long taskId, @Param("projectId") long projectId,
                                               @Param("resourceId") long resourceId, @Param("resourceType") Integer resourceType);

    Integer deleteByTaskId(@Param("taskId") long taskId, @Param("projectId") Long projectId, @Param("resourceType") Integer resourceType);

    Integer logicDeleteByTaskId(@Param("taskId") long taskId, @Param("projectId") Long projectId, @Param("resourceType") Integer resourceType);

    List<BatchTaskResource> listByResourceId(@Param("resourceId") Long resourceId);

    Integer insert(BatchTaskResource batchTaskResource);

    Integer update(BatchTaskResource batchTaskResource);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
