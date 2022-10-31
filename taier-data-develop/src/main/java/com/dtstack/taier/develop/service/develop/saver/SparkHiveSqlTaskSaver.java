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

package com.dtstack.taier.develop.service.develop.saver;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.util.SqlFormatUtil;
import com.dtstack.taier.dao.domain.DevelopDataSource;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.service.datasource.impl.DatasourceService;
import com.dtstack.taier.develop.service.develop.impl.DevelopFunctionService;
import com.dtstack.taier.develop.utils.develop.common.SqlUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: qianyi
 * @Date: 2022/05/29/5:14 PM
 */
@Component
public class SparkHiveSqlTaskSaver extends DefaultTaskSaver {

    private static final String CREATE_TEMP_FUNCTION_SQL = "%s %s";

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private DatasourceService datasourceService;

    @Override
    public String processScheduleRunSqlText(Task task) {
        String currentDatabase = "";
        if (EScheduleJobType.SPARK_SQL.getType().equals(task.getTaskType())) {
            DevelopDataSource dataSource = datasourceService.getOne(task.getDatasourceId());
            currentDatabase = dataSource.getSchemaName();
        }
        String sqlPlus = buildCustomFunctionSparkHiveSql(task.getSqlText(), task.getTenantId(), task.getTaskType());
        return processSql(sqlPlus, currentDatabase);
    }

    @Override
    public List<EScheduleJobType> support() {
        return Lists.newArrayList(EScheduleJobType.SPARK_SQL, EScheduleJobType.HIVE_SQL);
    }

    /**
     * 处理spark sql,Hive sql自定义函数
     *
     * @param sqlText
     * @param tenantId
     * @param taskType
     * @return
     */
    public String buildCustomFunctionSparkHiveSql(String sqlText, Long tenantId, Integer taskType) {
        String sqlPlus = SqlFormatUtil.formatSql(sqlText);
        if (EScheduleJobType.SPARK_SQL.getType().equals(taskType) || EScheduleJobType.HIVE_SQL.getType().equals(taskType)) {
            String containFunction = developFunctionService.buildContainFunction(sqlText, tenantId, taskType);
            if (StringUtils.isNotBlank(containFunction)) {
                sqlPlus = String.format(CREATE_TEMP_FUNCTION_SQL, containFunction, sqlPlus);
            }
        }
        return sqlPlus;
    }

    /**
     * 逐条处理sql
     *
     * @param sqlText
     * @param database
     * @return
     */
    protected String processSql(String sqlText, String database) {
        sqlText = SqlUtils.removeComment(sqlText);
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        StringBuilder sqlBuild = new StringBuilder();
        if (StringUtils.isNotBlank(database)) {
            sqlBuild.append("use ").append(database.toLowerCase()).append(";\n");
        }

        if (CollectionUtils.isNotEmpty(sqls)) {
            for (String sql : sqls) {
                sql = SqlFormatUtil.formatSql(sql);
                sql = SqlFormatUtil.getStandardSql(sql);
                if (StringUtils.isEmpty(sql)) {
                    continue;
                }

                sqlBuild.append(sql).append(";\n");
            }
        }
        return sqlBuild.toString();
    }

}
