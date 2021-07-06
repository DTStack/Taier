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


package com.dtstack.batch.engine.libra.service;

import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.dao.BatchTableInfoDao;
import com.dtstack.batch.domain.BatchTableInfo;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.domain.Tenant;
import com.dtstack.batch.engine.rdbms.service.IJdbcService;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.table.ITablePublishService;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jiangbo
 * @date 2019/6/27
 */
@Service
public class BatchLibraTablePublishService implements ITablePublishService {

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private ITableService tableServiceImpl;

    @Autowired
    private BatchTableInfoDao batchTableInfoDao;

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Override
    public Integer publish(BatchTableInfo sourceTableInfo, Project sourceProject, Long sourceDtUicTenantId, Tenant produceTenant, Long userId) throws Exception {

        ProjectEngine projectEngine = projectEngineService.getProjectDb(sourceProject.getId(), MultiEngineType.LIBRA.getType());
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support type %s", sourceProject.getId(), MultiEngineType.LIBRA.getType()));

        String createSql = tableServiceImpl.showCreateTable(sourceDtUicTenantId, null, projectEngine.getEngineIdentity(), ETableType.getTableType(sourceTableInfo.getTableType()), sourceTableInfo.getTableName());
        createSql = createSql.replaceAll("(?i)create table", "create table if not exists");
        createSql = SqlFormatUtil.getStandardSql(createSql);

        BatchTableInfo produceTable = batchTableInfoDao.getByTableName(sourceTableInfo.getTableName(),
                sourceProject.getTenantId(), sourceProject.getProduceProjectId(), ETableType.LIBRA.getType());
        if (produceTable == null) {
            ProjectEngine produceProjectEngine = projectEngineService.getProjectDb(sourceProject.getProduceProjectId(), MultiEngineType.LIBRA.getType());
            Preconditions.checkNotNull(produceProjectEngine, String.format("project %d not support type %s", sourceProject.getProduceProjectId(), MultiEngineType.LIBRA.getType()));
            jdbcServiceImpl.executeQueryWithoutResult(produceTenant.getDtuicTenantId(), null, EJobType.LIBRA_SQL, produceProjectEngine.getEngineIdentity(), createSql);
            return 1;
        } else {
            return 0;
        }
    }
}
