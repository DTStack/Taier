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

import com.dtstack.taier.common.annotation.Forbidden;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.common.enums.MultiEngineType;
import com.dtstack.taier.common.util.PublicUtil;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.dao.domain.TenantComponent;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ISqlExeService;
import com.dtstack.taier.develop.service.develop.MultiEngineServiceFactory;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlParserImpl;
import com.dtstack.taier.develop.sql.parse.SqlParserFactory;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class DevelopSqlExeService {

    public static final Logger LOGGER = LoggerFactory.getLogger(DevelopSqlExeService.class);

    @Autowired
    private DevelopTenantComponentService developTenantComponentService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private DevelopFunctionService batchFunctionService;

    private SqlParserFactory parserFactory = SqlParserFactory.getInstance();

    @Autowired
    private DevelopTaskService batchTaskService;

    public static final Pattern CACHE_LAZY_SQL_PATTEN = Pattern.compile("(?i)cache\\s+(lazy\\s+)?table.*");

    private static final String CREATE_TEMP_FUNCTION_SQL = "%s %s";

    /**
     * 执行SQL
     */
    public ExecuteResultVO executeSql(final ExecuteContent executeContent) throws Exception {
        ExecuteResultVO result = new ExecuteResultVO();
        // 前置操作
        this.prepareExecuteContent(executeContent);
        result.setSqlText(executeContent.getSql());

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(executeContent.getTaskType());
        final ExecuteResultVO engineExecuteResult = sqlExeService.executeSql(executeContent);
        if (!engineExecuteResult.getContinue()) {
            return engineExecuteResult;
        }
        PublicUtil.copyPropertiesIgnoreNull(engineExecuteResult, result);

        return result;
    }

    /**
     * 处理自定义函数 和 构建真正运行的SQL
     *
     * @param tenantId
     * @param taskType
     * @param sqlText
     * @return
     */
    public String processSqlText(Long tenantId, Integer taskType, String sqlText) {
        TenantComponent tenantEngine = this.developTenantComponentService.getByTenantAndEngineType(tenantId, taskType);
        ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(taskType);
        // 处理自定义函数
        String sqlPlus = buildCustomFunctionSparkSql(sqlText, tenantId, taskType);

        // 构建真正运行的SQL，去掉注释，加上use db 同时格式化SQL
        return sqlExeService.process(sqlPlus, tenantEngine.getComponentIdentity());
    }


    /**
     * 清除sql中的注释
     *
     * @param sql
     * @return
     */
    public String removeComment(final String sql) {
        final StringBuilder stringBuilder = new StringBuilder();
        final String[] split = sql.split("\n");
        for (String s : split) {
            if (StringUtils.isNotBlank(s)) {
                //注释开头
                if (!s.trim().startsWith("--")) {
                    s = removeCommentByQuotes(s);
                    stringBuilder.append(" ").append(s);
                }
            }
        }
        //去除/**/跨行的情况
        return removeMoreCommentByQuotes(stringBuilder.toString());
    }

    /**
     * 去除 --注释  避免了" '的影响
     * @param sql
     * @return
     */
    private String removeCommentByQuotes(String sql) {
        StringBuffer buffer = new StringBuffer();
        Character quote = null;
        //用于标示是否进入了"之内 如果进入了就忽略 --
        Boolean flag = false;
        char[] sqlindex = sql.toCharArray();
        for (int i = 0; sql.length() > i; i++) {
            if (!flag) {
                //如果符合条件 就标示进入了引号之内 记录是单引号 还是双引号
                if (sqlindex[i] == '\"' || sqlindex[i] == '\'') {
                    quote = sqlindex[i];
                    flag = true;
                } else {
                    if (sqlindex[i] == '-' && i + 1 < sql.length() && sqlindex[i + 1] == '-') {
                        break;
                    }
                }
                buffer.append(sqlindex[i]);
            } else {
                //再次发现记录的值 说明出了 引号之内
                if (sqlindex[i] == quote) {
                    quote = null;
                    flag = false;
                }
                buffer.append(sqlindex[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 专门删除 多行注释的方法 **这种的
     *
     * @param osql
     * @return
     */
    private String removeMoreCommentByQuotes(String osql) {
        StringBuffer buffer = new StringBuffer();

        Boolean flag = false;
        Boolean dhzs = false;
        char[] sqlindex = osql.toCharArray();
        for (int i = 0; osql.length() > i; ++i) {
            if (!flag) {
                if (!dhzs) {
                    if (sqlindex[i] != '"' && sqlindex[i] != '\'') {
                        if (sqlindex[i] == '/' && i + 1 < osql.length() && sqlindex[i + 1] == '*') {
                            i++;
                            dhzs = true;
                            continue;
                        }
                    } else {
                        flag = true;
                    }
                    buffer.append(sqlindex[i]);
                } else {
                    if (sqlindex[i] == '*' && i + 1 < osql.length() && sqlindex[i + 1] == '/') {
                        i++;
                        dhzs = false;
                        continue;
                    }
                }
            } else {
                if (sqlindex[i] == '"' || sqlindex[i] == '\'') {
                    flag = false;
                }

                buffer.append(sqlindex[i]);
            }
        }
        return buffer.toString();
    }

    /**
     * 处理返回结果
     *
     * @param result
     */
    public void dealResultDoubleList(List<List<Object>> result) {
        if (CollectionUtils.isEmpty(result)) {
            return;
        }

        for (List<Object> objects : result) {
            if (CollectionUtils.isEmpty(objects)) {
                continue;
            }

            for (int i = 0; i < objects.size(); i++) {
                if (objects.get(i) == null) {
                    continue;
                }

                if (objects.get(i) instanceof Double
                        || objects.get(i) instanceof Float
                        || objects.get(i) instanceof BigDecimal
                        || objects.get(i) instanceof Number) {
                    BigDecimal decimal = new BigDecimal(0);
                    try {
                        decimal = new BigDecimal(objects.get(i).toString());
                        objects.set(i, decimal.toPlainString());
                    } catch (Exception e) {
                        objects.set(i, "NaN");
                    }
                    continue;
                }else if (objects.get(i) instanceof Timestamp){
                    objects.set(i, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(objects.get(i)));
                }
            }
        }
    }


    /**
     * 进行SQL解析
     * @param executeContent
     */
    private void prepareExecuteContent(final ExecuteContent executeContent) {
        Task one = batchTaskService.getOneWithError(executeContent.getTaskId());
        String taskParam = one.getTaskParams();

        String sql = executeContent.getSql();

        //TODO cache lazy table 暂时不解析血缘，不知道这种类型的sql如何处理
        if (StringUtils.isNotBlank(sql) && (sql.toLowerCase().trim().startsWith("set")
                || CACHE_LAZY_SQL_PATTEN.matcher(sql).matches())) {
            //set sql 不解析
            ParseResult parseResult = new ParseResult();
            parseResult.setParseSuccess(true);
            parseResult.setOriginSql(executeContent.getSql());
            parseResult.setStandardSql(executeContent.getSql());
            executeContent.setParseResult(parseResult);
            return;
        }

        //单条sql解析
        if (StringUtils.isNotBlank(executeContent.getSql())) {
            ParseResult parseResult = this.parseSql(executeContent);
            executeContent.setParseResult(parseResult);
        }

        //批量解析sql
        List<ParseResult> parseResultList = Lists.newLinkedList();

        if (CollectionUtils.isNotEmpty(executeContent.getSqlList())) {
            String finalTaskParam = taskParam;
            executeContent.getSqlList().forEach(x -> {
                if (!x.trim().startsWith("set")) {
                    executeContent.setSql(x);
                    ParseResult batchParseResult = this.parseSql(executeContent);
                    parseResultList.add(batchParseResult);
                } else {
                    //set sql 不解析
                    ParseResult batchParseResult = new ParseResult();
                    batchParseResult.setParseSuccess(true);
                    batchParseResult.setOriginSql(x);
                    batchParseResult.setStandardSql(x);
                    parseResultList.add(batchParseResult);
                }
            });
            executeContent.setParseResultList(parseResultList);
        }
    }


    /**
     * 解析sql
     *
     * @param executeContent
     * @return
     */
    private ParseResult parseSql(ExecuteContent executeContent) {
        TenantComponent tenantEngine = developTenantComponentService.getByTenantAndEngineType(executeContent.getTenantId(), executeContent.getTaskType());
        executeContent.setDatabase(tenantEngine.getComponentIdentity());

        SqlParserImpl sqlParser = parserFactory.getSqlParser(ETableType.HIVE);
        ParseResult parseResult = null;
        try {
            parseResult = sqlParser.parseSql(executeContent.getSql(), tenantEngine.getComponentIdentity(), new HashMap<>());
        } catch (final Exception e) {
            LOGGER.error("解析sql异常，sql：{}", executeContent.getSql(), e);
            parseResult = new ParseResult();
            if (MultiEngineType.HADOOP.getType() == executeContent.getTaskType()) {
                parseResult.setParseSuccess(false);
            }
            parseResult.setFailedMsg(ExceptionUtils.getStackTrace(e));
            parseResult.setStandardSql(SqlFormatUtil.getStandardSql(executeContent.getSql()));
        }
        return parseResult;
    }

    /**
     * 处理spark sql自定义函数
     * @param sqlText
     * @param tenantId
     * @param taskType
     * @return
     */
    public String buildCustomFunctionSparkSql(String sqlText, Long tenantId, Integer taskType) {
        String sqlPlus = SqlFormatUtil.formatSql(sqlText);
        if (EScheduleJobType.SPARK_SQL.getType().equals(taskType)) {
            String containFunction = batchFunctionService.buildContainFunction(sqlText, tenantId, taskType);
            if (StringUtils.isNotBlank(containFunction)) {
                sqlPlus = String.format(CREATE_TEMP_FUNCTION_SQL,containFunction,sqlPlus);
            }
        }
        return sqlPlus;
    }

}