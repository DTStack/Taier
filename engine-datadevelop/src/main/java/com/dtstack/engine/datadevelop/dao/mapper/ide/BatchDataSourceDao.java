package com.dtstack.engine.datadevelop.dao.mapper.ide;

import com.dtstack.engine.api.domain.BatchDataSource;
import com.dtstack.engine.api.dto.BatchDataSourceDTO;
import com.dtstack.engine.api.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/4/16
 */
public interface BatchDataSourceDao {

    BatchDataSource getOne(@Param("id") Long id);

    Integer deleteById(@Param("sourceId") Long sourceId, @Param("gmtModified") Timestamp timestamp, @Param("projectId") Long projectId, @Param("modifyUserId") Long userId);

    List<BatchDataSource> listByProjectId(@Param("projectId") Long projectId);

    Integer insert(BatchDataSource batchDataSource);

    Integer update(BatchDataSource batchDataSource);

    List<BatchDataSource> generalQuery(PageQuery<BatchDataSourceDTO> pageQuery);

    Integer generalCount(@Param("model") BatchDataSourceDTO object);

    BatchDataSource getDataSourceByName(@Param("dataName") String dataName, @Param("projectId") long projectId);

    List<BatchDataSource> listByTenantId(@Param("tenantId") Long tenantId);

    List<BatchDataSource> listByIds(@Param("list") List<Long> list);

    BatchDataSource getDefaultDataSource(@Param("projectId") long projectId, @Param("dataSourceType") Integer dataSourceType);

    BatchDataSource getBeanByProjectIdAndDbTypeAndDbName(@Param("projectId") long projectId, @Param("dataSourceType") Integer dataSourceType, @Param("dataSourceName") String dataSourceName);

    BatchDataSource getDefaultDataSourceByName(@Param("nameList") List<String> nameList, @Param("dataSourceType") Integer dataSourceType, @Param("tenantId") long tenantId);

    List<BatchDataSource> listAll(@Param("pageIndex")Integer pageIndex, @Param("pageSize")Integer pageSize);

    List<BatchDataSource> listAllBatchData();

    Integer deleteByProjectId(@Param("projectId") Long projectId, @Param("userId") Long userId);
}
