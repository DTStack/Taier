package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchResource;
import com.dtstack.batch.dto.BatchResourceDTO;
import com.dtstack.batch.web.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author sishu.yss
 */
public interface BatchResourceDao {

    BatchResource getOne(@Param("id") Long id);

    BatchResource getByIdIgnoreDeleted(@Param("id") Long id);

    /**
     * 根据函数找出资源文件地址
     *
     * @param functionId
     * @return
     */
    String getResourceURLByFunctionId(@Param("functionId") Long functionId);


    List<BatchResource> listByIds(@Param("ids") List<Long> resourceIdList, @Param("isDeleted") Integer isDeleted);

    List<BatchResource> listByPidAndProjectId(@Param("projectId") Long projectId, @Param("nodePid") Long nodePid);

    List<BatchResource> listByProjectId(@Param("projectId") long projectId);

    Integer deleteById(@Param("id") Long resourceId, @Param("projectId") Long projectId);

    Integer deleteByIds(@Param("ids") List<Long> resourceIds, @Param("projectId") Long projectId);

    String getUrlById(@Param("id") long id);

    List<BatchResource> listByNameAndProjectId(@Param("projectId") Long projectId, @Param("resourceName") String resourceName, @Param("isDeleted") int isDeleted);

    Integer insert(BatchResource batchResource);

    Integer update(BatchResource batchResource);

    Integer batchInsert(@Param("list") List<BatchResource> list);

    Integer countByProjectId(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId);

    List<BatchResource> generalQuery(PageQuery<BatchResourceDTO> query);

    Integer generalCount(@Param("model") BatchResourceDTO model);

    BatchResource getByName(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("resourceName") String resourceName);

    List<BatchResource> listByUrls(@Param("tenantId") Long tenantId, @Param("projectId") Long projectId, @Param("list") List<String> list);

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
