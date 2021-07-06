package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.dto.BatchFunctionDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface BatchFunctionDao {

    BatchFunction getOne(@Param("id") Long id);

    List<BatchFunction> listByNodePidAndProjectId(@Param("projectId") Long projectId, @Param("nodePid") Long nodePid);

    List<BatchFunction> listSystemFunction(@Param("engineType") Integer engineType);

    List<BatchFunction> listProjectFunction(@Param("projectId") Long projectId, @Param("functionType") Integer functionType, @Param("engineType") Integer engineType);

    List<BatchFunction> listByNameAndProjectId(@Param("projectId") Long projectId, @Param("name") String name, @Param("type") Integer type);

    BatchFunction getByNameAndProjectId(@Param("projectId") Long projectId, @Param("name") String name);

    Integer insert(BatchFunction batchFunction);

    Integer update(BatchFunction batchFunction);

    List<String> listNameByProjectId(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("engineType") Integer engineType);

    Integer countByProjectIdAndType(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("type") Integer type);

    List<BatchFunction> generalQuery(PageQuery<BatchFunctionDTO> query);

    Integer generalCount(@Param("model") BatchFunctionDTO model);

    List<BatchFunction> listByIds(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("list") List<Long> list, @Param("isDeleted") Integer isDeleted, @Param("type") Integer type);

    List<BatchFunction> listByProjectIdAndType(@Param("projectId") Long projectId, @Param("type") Integer type);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
