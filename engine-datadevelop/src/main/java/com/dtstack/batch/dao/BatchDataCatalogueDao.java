package com.dtstack.batch.dao;

import com.dtstack.batch.domain.BatchDataCatalogue;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 数据类目
 * @author sanyue
 */
public interface BatchDataCatalogueDao {

    BatchDataCatalogue getOne(@Param("id") long id);

    Integer insert(BatchDataCatalogue catalogue);

    Integer update(BatchDataCatalogue catalogue);

    Integer deleteById(@Param("id") Long id, @Param("gmtModified") Timestamp gmtModified);

    List<BatchDataCatalogue> listByTenantIdAndPId(@Param("tenantId") long tenantId, @Param("nodePid") long nodePid);

    List<BatchDataCatalogue> listByTenantId(@Param("tenantId") long tenantId);

    Integer countByNodePid(@Param("nodePid") long nodePid);

    BatchDataCatalogue getByNodeNameAndNodePid(@Param("nodeName") String nodeName, @Param("nodePid") Long nodePid, @Param("tenantId") long tenantId);

    List<BatchDataCatalogue> listByIds(@Param("ids") List<Long> ids, @Param("tenantId") Long tenantId);

    Long getRootIdByTenantId(@Param("tenantId") Long tenantId);

    BatchDataCatalogue getRootByTenantId(@Param("tenantId") Long tenantId);
}
