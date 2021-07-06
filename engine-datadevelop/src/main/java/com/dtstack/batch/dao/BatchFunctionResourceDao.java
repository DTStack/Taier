package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchFunctionResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BatchFunctionResourceDao {

    List<BatchFunctionResource> listByResourceId(@Param("resourceId") Long resourceId);

    void deleteByFunctionId(@Param("functionId") Long functionId);

    void insert(BatchFunctionResource batchFunctionResource);

    BatchFunctionResource getBeanByResourceIdAndFunctionId(@Param("resourceId") Long resourceId, @Param("functionId") Long functionId);

    List<BatchFunctionResource> listByFunctionId(@Param("functionId") Long functionId);

    List<BatchFunctionResource> listByFunctionResourceId(@Param("resource_Id") Long resource_Id);

    void updateByFunctionId(BatchFunctionResource batchFunctionResource);

    Integer deleteByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据函数id获取函数资源关联关系
     *
     * @param functionId
     * @return
     */
    BatchFunctionResource getResourceFunctionByFunctionId(@Param("functionId") Long functionId);
}
