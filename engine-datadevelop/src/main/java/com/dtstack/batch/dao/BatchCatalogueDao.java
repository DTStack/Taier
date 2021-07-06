package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchCatalogue;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author toutian
 */
public interface BatchCatalogueDao {

    BatchCatalogue getOne(@Param("id") long id);

    BatchCatalogue getByPidAndName(@Param("projectId") long projectId, @Param("nodePid") long nodePid, @Param("name") String name);

    List<BatchCatalogue> listByPidAndProjectId(@Param("nodePid") long nodePid, @Param("projectId") long projectId);

    List<BatchCatalogue> listByLevelAndProjectId(@Param("level") Integer level, @Param("projectId") long projectId);

    BatchCatalogue getByLevelAndProjectIdAndName(@Param("level") Integer level, @Param("projectId") long projectId, @Param("name") String name);

    List<BatchCatalogue> listByNameFuzzy(@Param("projectId") long projectId, @Param("name") String name);

    BatchCatalogue getSystemFunctionCatalogueOne(@Param("engineType") int engineType);

    Integer insert(BatchCatalogue batchCatalogue);

    Integer update(BatchCatalogue batchCatalogue);

    List<BatchCatalogue> listByProjectId(@Param("projectId") Long projectId);

    Integer deleteById(@Param("id") long id);

    BatchCatalogue getAllPathParentCatalogues(@Param("nodePid") Long nodePid);

    BatchCatalogue getByLevelAndParentIdAndProjectIdAndName(@Param("level") Integer level, @Param("parentId") Long parentId , @Param("projectId") long projectId, @Param("name") String name);

    Integer getSubAmountsByNodePid(@Param("nodePid") Long nodePid, @Param("projectId") Long projectId);


    List<BatchCatalogue> getListByTenantIdAndCatalogueType(@Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    BatchCatalogue getProjectRoot(@Param("tenantId") Long tenantId, @Param("catalogueType") Integer catalogueType);

    BatchCatalogue getBeanByTenantIdAndNameAndParentId(@Param("tenantId")Long tenantId, @Param("name")String name, @Param("parentId")Long parentId);

    List<BatchCatalogue> listByPidAndNameFuzzy(@Param("projectId") long projectId, @Param("nodePid") long nodePid, @Param("name") String name);
}
