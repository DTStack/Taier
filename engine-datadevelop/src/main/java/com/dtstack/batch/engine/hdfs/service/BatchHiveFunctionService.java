/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.batch.engine.hdfs.service;

import com.dtstack.batch.domain.BatchFunction;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.impl.HiveSqlBuildService;
import com.dtstack.batch.mapping.DataSourceTypeJobTypeMapping;
import com.dtstack.batch.service.datasource.impl.DatasourceService;
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
    private DatasourceService datasourceService;

    @Override
    public void addFunction(Long tenantId, String dbName, String funcName, String className, String resource) throws Exception {
        DataSourceType metaDataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        String sql = hiveSqlBuildService.buildAddFuncSql(funcName, className, resource);
        jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName, sql);
    }

    @Override
    public void deleteFunction(Long tenantId, String dbName, String functionName) throws Exception {
        DataSourceType metaDataSourceType = datasourceService.getHadoopDefaultDataSourceByTenantId(tenantId);
        String sql = hiveSqlBuildService.buildDropFuncSql(functionName);
        jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, DataSourceTypeJobTypeMapping.getTaskTypeByDataSourceType(metaDataSourceType.getVal()), dbName, sql);
    }

    @Override
    public void addProcedure(Long tenantId, String dbName, BatchFunction batchFunction) {

    }
}
