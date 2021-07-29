package com.dtstack.batch.engine.hdfs.service;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.impl.HiveSqlBuildService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.table.IFunctionService;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Reason:
 * Date: 2019/6/17
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchHiveFunctionService implements IFunctionService {

    @Autowired
    private HiveSqlBuildService hiveSqlBuildService;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Override
    public void addFunction(Long dtuicTenantId, String dbName, String funcName, String className, String resource, Long projectId) throws Exception {
        DataSourceType metaDataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        String sql = hiveSqlBuildService.buildAddFuncSql(funcName, className, resource);
        jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName, sql);
    }

    @Override
    public void deleteFunction(Long dtuicTenantId, String dbName, String functionName, Long projectId) throws Exception {
        DataSourceType metaDataSourceType = batchDataSourceService.getHadoopDefaultDataSourceByProjectId(projectId);
        String sql = hiveSqlBuildService.buildDropFuncSql(functionName);
        jdbcServiceImpl.executeQueryWithoutResult(dtuicTenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName, sql);
    }

    @Override
    public void addProcedure(Long dtuicTenantId, String dbName, BatchFunction batchFunction) {

    }
}
