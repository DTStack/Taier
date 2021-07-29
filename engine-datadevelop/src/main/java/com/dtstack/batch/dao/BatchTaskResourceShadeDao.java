package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.domain.BatchTaskResourceShade;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by jiangbo on 2017/5/3 0003.
 */
public interface BatchTaskResourceShadeDao {

    BatchTaskResourceShade getOne(@Param("id") Long id);

    List<BatchTaskResourceShade> listByTaskId(@Param("taskId") long taskId, @Param("resourceType") Integer resourceType, @Param("projectId") long projectId);

    Integer deleteByTaskId(@Param("taskId") long taskId);

    Integer insert(BatchTaskResourceShade batchTaskResourceShade);

    Integer update(BatchTaskResourceShade batchTaskResourceShade);

    List<BatchResource> listResourceByTaskId(@Param("taskId") long taskId, @Param("resourceType") Integer resourceType, @Param("projectId") long projectId);

    Integer deleteByProjectId(@Param("projectId") Long projectId);
}
