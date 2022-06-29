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

package com.dtstack.taier.develop.service.develop.impl;

import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.dtstack.taier.develop.utils.develop.service.IJdbcService;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DevelopSparkHiveSqlExeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DevelopSparkHiveSqlExeService.class);

    private static final String INSERT_REGEX = "(?i)insert\\s+.*";

    private static final String ONLY_FROM = "(?i)\\s+from\\s+";

    private static final Pattern FROM_PATTERN = Pattern.compile(ONLY_FROM);

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    private static final String SIMPLE_QUERY_REGEX_ONLY = "(?i)select\\s+.*";

    private static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    @Autowired
    private IJdbcService jdbcServiceImpl;

    @Autowired
    private DevelopSqlExeService batchSqlExeService;

    @Autowired
    protected DevelopHadoopSelectSqlService batchHadoopSelectSqlService;

    @Autowired
    protected DevelopTenantComponentService developTenantComponentService;

    @Autowired
    protected DevelopSelectSqlService selectSqlService;

    @Autowired
    protected DevelopFunctionService batchFunctionService;

    /**
     * 直连jdbc执行sql
     *
     * @param executeContent
     * @param tenantId
     * @param parseResult
     * @param result
     * @param tenantEngine
     * @param eScheduleJobType
     */
    protected void exeSqlDirect(ExecuteContent executeContent, Long tenantId, ParseResult parseResult, ExecuteResultVO<List<Object>> result, TenantComponent tenantEngine, EScheduleJobType eScheduleJobType) {
        try {
            if (SqlType.getShowType().contains(parseResult.getSqlType())) {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(tenantId, null, eScheduleJobType, tenantEngine.getComponentIdentity(), parseResult.getStandardSql());
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setResult(executeResult);
            } else {
                jdbcServiceImpl.executeQueryWithoutResult(tenantId, null, eScheduleJobType, tenantEngine.getComponentIdentity(), parseResult.getStandardSql());
            }
        } catch (Exception e) {
            LOGGER.error("exeHiveSqlDirect error {}", executeContent.getSql(), e);
            throw e;
        }
    }

    /**
     * 逐条处理sql
     *
     * @param sqlText
     * @param database
     * @return
     */
    protected String processSql(String sqlText, String database) {
        sqlText = batchSqlExeService.removeComment(sqlText);
        if (!sqlText.endsWith(";")) {
            sqlText = sqlText + ";";
        }

        List<String> sqls = SqlFormatUtil.splitSqlText(sqlText);
        StringBuilder sqlBuild = new StringBuilder();
        sqlBuild.append("use ").append(database.toLowerCase()).append(";\n");

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

    /**
     * 判断是否简单查询
     *
     * @param sql
     * @return
     */
    protected boolean isSimpleQuery(String sql) {
        sql = SqlFormatUtil.formatSql(sql);
        sql = SqlFormatUtil.getStandardSql(sql);
        Matcher matcher = FROM_PATTERN.matcher(sql);
        int fromCount = 0;
        while (matcher.find()) {
            fromCount++;
        }
        Matcher matcherSimple = SIMPLE_QUERY_PATTERN.matcher(sql);
        if ((matcherSimple.matches() && fromCount == 1) || (sql.matches(SIMPLE_QUERY_REGEX_ONLY) && fromCount == 0)) {
            //形如select DISTINCT id,t_int FROM chellner;的sql会被识别为简单查询
            String cols = null;
            try {
                cols = matcherSimple.group("cols");
            } catch (IllegalStateException e) {
                LOGGER.info("can not match 'cols',{}", e);
            }
            if (StringUtils.isNotEmpty(cols)) {
                String[] split = cols.split(",");
                for (int i = 0; i < split.length; i++) {
                    String col = split[i].toUpperCase();
                    if (col.startsWith("DISTINCT ")) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 执行sql
     *
     * @param executeContent
     * @param scheduleJobType
     * @return
     */
    protected ExecuteResultVO executeSql(ExecuteContent executeContent, EScheduleJobType scheduleJobType) {
        // 判断血缘解析结果，防止空指针
        if (null == executeContent.getParseResult()) {
            throw new RdosDefineException("SQL解析异常，结果为空");
        }
        Long tenantId = executeContent.getTenantId();
        Long userId = executeContent.getUserId();
        Long taskId = executeContent.getTaskId();
        String preJobId = executeContent.getPreJobId();
        String currDb = executeContent.getParseResult().getCurrentDb();
        ParseResult parseResult = executeContent.getParseResult();

        // 校验是否含有自定义函数
        boolean useSelfFunction = batchFunctionService.validContainSelfFunction(executeContent.getSql(), tenantId, null, scheduleJobType.getType());

        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        if (Objects.nonNull(parseResult) && Objects.nonNull(parseResult.getStandardSql()) && isSimpleQuery(parseResult.getStandardSql()) && !useSelfFunction) {
            result = simpleQuery(tenantId, parseResult, currDb, userId, scheduleJobType);
            if (!result.getContinue()) {
                return result;
            }
        }

        if (SqlType.INSERT.equals(parseResult.getSqlType())
                || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                || SqlType.QUERY.equals(parseResult.getSqlType())
                || SqlType.CREATE_AS.equals(parseResult.getSqlType())
                || useSelfFunction) {
            String jobId = batchHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, currDb.toLowerCase(), taskId, scheduleJobType.getType(), preJobId);
            result.setJobId(jobId);
        } else {
            TenantComponent tenantEngine = developTenantComponentService.getByTenantAndTaskType(executeContent.getTenantId(), executeContent.getTaskType());
            this.exeSqlDirect(executeContent, tenantId, parseResult, result, tenantEngine, scheduleJobType);
        }
        result.setContinue(true);
        return result;
    }

    /**
     * 简单查询结果
     *
     * @param tenantId
     * @param parseResult
     * @param currentDb
     * @param tenantId
     * @param userId
     * @param scheduleJobType
     * @return
     */
    protected ExecuteResultVO<List<Object>> simpleQuery(Long tenantId, ParseResult parseResult, String currentDb, Long userId, EScheduleJobType scheduleJobType) {
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(parseResult.getStandardSql());
        if (matcher.find()) {
            String tableName = parseResult.getMainTable().getName();
            //这里增加一条记录，保证简单查询sql也能下载数据
            String jobId = UUID.randomUUID().toString();
            String parseColumnsString = "{}";
            selectSqlService.addSelectSql(jobId, tableName, TempJobType.SIMPLE_SELECT.getType(), tenantId, parseResult.getStandardSql(), userId, parseColumnsString, scheduleJobType.getType());
            result.setJobId(jobId);
            result.setContinue(false);
        } else {
            try {
                List<List<Object>> executeResult = jdbcServiceImpl.executeQuery(tenantId, null, scheduleJobType, currentDb.toLowerCase(), parseResult.getStandardSql());
                batchSqlExeService.dealResultDoubleList(executeResult);
                result.setStatus(TaskStatus.FINISHED.getStatus());
                result.setResult(executeResult);
            } catch (Exception e) {
                LOGGER.error("", e);
                result.setStatus(TaskStatus.FAILED.getStatus());
                result.setMsg(e.getMessage());
            }
            result.setContinue(false);
        }
        return result;
    }

}
