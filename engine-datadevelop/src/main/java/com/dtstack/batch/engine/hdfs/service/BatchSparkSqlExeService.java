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

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.enums.SqlTypeEnums;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.sync.job.SourceType;
import com.dtstack.batch.vo.BuildSqlVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.vo.SqlResultVO;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.lineage.vo.SqlType;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Reason:
 * Date: 2019/5/13
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

@Service
public class BatchSparkSqlExeService extends BatchSparkHiveSqlExeService implements ISqlExeService {

    private static final String SHOW_LIFECYCLE = "%s表的生命周期为%s天";

    @Forbidden
    @Override
    public ExecuteResultVO executeSql(ExecuteContent executeContent) {
        return executeSql(executeContent, EJobType.SPARK_SQL);
    }

    @Override
    public ExecuteSqlParseVO batchExecuteSql(ExecuteContent executeContent) {
        String preJobId = executeContent.getPreJobId();
        Integer relationType = executeContent.getRelationType();
        String currDb = executeContent.getParseResult().getCurrentDb();
        Long dtuicTenantId = executeContent.getTenantId();
        Long tenantId = executeContent.getTenantId();
        Long projectId = executeContent.getProjectId();
        Long userId = executeContent.getUserId();
        Long relationId = executeContent.getRelationId();


        List<ParseResult> parseResultList = executeContent.getParseResultList();
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();

        boolean useSelfFunction = batchFunctionService.validContainSelfFunction(executeContent.getSql(), projectId, null);
        ExecuteSqlParseVO executeSqlParseVO = new ExecuteSqlParseVO();
        List<SqlResultVO> sqlIdList = Lists.newArrayList();
        List<String> sqlList = Lists.newArrayList();
        BuildSqlVO buildSqlVO = new BuildSqlVO();
        for (ParseResult parseResult : parseResultList) {
            // 简单查询
            if (Objects.nonNull(parseResult.getStandardSql()) && isSimpleQuery(parseResult.getStandardSql()) && !useSelfFunction) {
                result = simpleQuery(dtuicTenantId, parseResult, currDb, tenantId, userId, executeContent.getEngineType(), EJobType.SPARK_SQL);
                if (!result.getIsContinue()) {
                    SqlResultVO<List<Object>> sqlResultVO = new SqlResultVO<>();
                    sqlResultVO.setSqlId(result.getJobId());
                    sqlResultVO.setType(SqlTypeEnums.SELECT_DATA.getType());
                    sqlIdList.add(sqlResultVO);
                    continue;
                }
            }

            if (SqlType.CREATE_AS.equals(parseResult.getSqlType())) {
                buildSqlVO = batchHadoopSelectSqlService.getSqlIdAndSql(dtuicTenantId, parseResult, tenantId, projectId, userId,
                        currDb.toLowerCase(), true, relationId, relationType, preJobId);
                SqlResultVO<List<Object>> sqlResultVO = new SqlResultVO<>();
                sqlResultVO.setSqlId(buildSqlVO.getJobId());
                sqlResultVO.setType(SqlTypeEnums.SELECT_DATA.getType());
                sqlIdList.add(sqlResultVO);
                sqlList.add(buildSqlVO.getSql());
            } else if (SqlType.INSERT.equals(parseResult.getSqlType())
                    || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                    || SqlType.QUERY.equals(parseResult.getSqlType())
                    || useSelfFunction) {

                buildSqlVO = batchHadoopSelectSqlService.getSqlIdAndSql(dtuicTenantId, parseResult, tenantId, projectId,
                        userId, currDb.toLowerCase(), false, relationId, relationType, preJobId);
                //insert和insert overwrite都没有返回结果
                sqlIdList.add(new SqlResultVO().setSqlId(buildSqlVO.getJobId()).setType(SqlTypeEnums.SELECT_DATA.getType()));
                sqlList.add(buildSqlVO.getSql());

            } else {
                if (!executeContent.isExecuteSqlLater()) {
                    ProjectEngine projectDb = projectEngineService.getProjectDb(executeContent.getProjectId(), EngineType.Spark.getVal());
                    Preconditions.checkNotNull(projectDb, "引擎不能为空");
                    SqlResultVO<List<Object>> sqlResultVO = new SqlResultVO<>();
                    sqlResultVO.setSqlText(parseResult.getStandardSql());
                    sqlResultVO.setType(SqlTypeEnums.NO_SELECT_DATA.getType());
                    if (SqlType.CREATE.equals(parseResult.getSqlType())
                            || SqlType.CREATE_LIKE.equals(parseResult.getSqlType())) {
                        executeCreateTableSql(parseResult, dtuicTenantId, projectDb.getEngineIdentity().toLowerCase(), EJobType.SPARK_SQL);
                        sqlResultVO.setMsg(String.format(SHOW_LIFECYCLE, parseResult.getMainTable().getName(), parseResult.getMainTable().getLifecycle()));
                        sqlIdList.add(sqlResultVO);
                    } else {
                        this.exeSqlDirect(executeContent, dtuicTenantId, parseResult, result, projectDb, DataSourceType.Spark);
                        sqlResultVO.setResult(result.getResult());
                        sqlIdList.add(sqlResultVO);
                    }
                }
            }
        }

        String sqlToEngine = StringUtils.join(sqlList,";");
        //除简单查询，其他sql发送到engine执行
        String jobId =  batchHadoopSelectSqlService.sendSqlTask(dtuicTenantId, sqlToEngine, SourceType.TEMP_QUERY, buildSqlVO.getTaskParam(), preJobId, relationId, relationType, userId, projectId);

        //记录发送到engine的id
        selectSqlService.addSelectSql(jobId, StringUtils.EMPTY, 0, tenantId, projectId,
                sqlToEngine, userId, StringUtils.EMPTY, MultiEngineType.HADOOP.getType());

        sqlIdList.sort(Comparator.comparingInt(SqlResultVO::getType));
        executeSqlParseVO.setJobId(jobId);
        executeSqlParseVO.setSqlIdList(sqlIdList);

        return executeSqlParseVO;

    }

    @Override
    public String process(String sqlText, String database) {
        return processSql(sqlText, database);
    }


    @Override
    public void checkSingleSqlSyntax(Long projectId, Long dtuicTenantId, String sql, String db, String taskParam) {
        checkSingleSqlSyntax(projectId, dtuicTenantId, sql, db, taskParam, EJobType.SPARK_SQL);
    }

}
