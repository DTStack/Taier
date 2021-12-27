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


package com.dtstack.batch.service.impl;

import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.batch.domain.TenantEngine;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.engine.common.annotation.Forbidden;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.domain.Tenant;
import com.dtstack.batch.service.console.TenantService;
import com.dtstack.engine.master.impl.UserService;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/6/14
 */
@Service
public class BatchSqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchSqlExeService.class);

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantEngineService projectEngineService;

//    @Autowired
//    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchFunctionService batchFunctionService;


    @Autowired
    private BatchTaskService batchTaskService;

    private static final String SHOW_LIFECYCLE = "%s表的生命周期为%s天";

    private static final String NOT_GET_COLUMN_SQL_REGEX = "(?i)(create|drop)[\\s|\\S]*";

    private static final String CREATE_AS_REGEX = "(?i)create\\s+((table)|(view))\\s*[\\s\\S]+\\s*as\\s+select\\s+[\\s\\S]+";

    public static final Pattern CACHE_LAZY_SQL_PATTEN = Pattern.compile("(?i)cache\\s+(lazy\\s+)?table.*");

    private static final String CREATE_TEMP_FUNCTION_SQL = "%s %s";

    private static final Set<Integer> notDataMapOpera = new HashSet<>();

    static {
        notDataMapOpera.add(EJobType.ORACLE_SQL.getVal());
        notDataMapOpera.add(EJobType.GaussDB_SQL.getVal());
        notDataMapOpera.add(EJobType.GREENPLUM_SQL.getVal());
        notDataMapOpera.add(EJobType.INCEPTOR_SQL.getVal());
    }

    private String getDbName(final ExecuteContent executeContent) {
        if (StringUtils.isNotBlank(executeContent.getDatabase())) {
            return executeContent.getDatabase();
        }
        String dbName = null;
        final Tenant tenant = tenantService.getByDtUicTenantId(executeContent.getTenantId());
        if (null != tenant) {
            Long projectId = executeContent.getProjectId();

            final TenantEngine projectDb = this.projectEngineService.getByTenantAndEngineType(projectId, executeContent.getEngineType());
            if (projectDb == null) {
                throw new RdosDefineException("引擎不能为空");
            }

            dbName = projectDb.getEngineIdentity();
        }
        executeContent.setDatabase(dbName);
        return dbName;
    }

    private Integer getEngineType(final ExecuteContent executeContent) {
        Integer engineType = executeContent.getEngineType();
        if (engineType == null) {
            engineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(executeContent.getTableType()).getType();
        }

        return engineType;
    }



    /**
     *
     */
    @Forbidden
    public ExecuteResultVO executeSql(final ExecuteContent executeContent) throws Exception {
        final ExecuteResultVO result = new ExecuteResultVO();
        // 前置操作
        this.preExecuteSql(executeContent);
        result.setSqlText(executeContent.getSql());

//        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(executeContent.getEngineType(), executeContent.getDetailType(), executeContent.getProjectId());
        final ISqlExeService sqlExeService = null; //todo
        final ExecuteResultVO engineExecuteResult = sqlExeService.executeSql(executeContent);
        if (!engineExecuteResult.getIsContinue()) {
            return engineExecuteResult;
        }
        PublicUtil.copyPropertiesIgnoreNull(engineExecuteResult, result);

        this.afterExecuteSql(executeContent);

        return result;
    }

    /**
     * 解析sqlList返回sqlId并且封装sql到引擎执行
     *
     * @param executeContent
     * @return
     * @throws Exception
     */
    public ExecuteSqlParseVO batchExeSqlParse(final ExecuteContent executeContent) throws Exception {
        //前置操作
        this.preExecuteSql(executeContent);

//        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(executeContent.getEngineType(), executeContent.getDetailType(), executeContent.getProjectId());
        final ISqlExeService sqlExeService = null; //todo
        ExecuteSqlParseVO executeSqlParseVO = sqlExeService.batchExecuteSql(executeContent);

        this.afterExecuteSql(executeContent);
        return executeSqlParseVO;
    }

    /**
     * 该方法主要做语法校验，本质是 在sql语句之前+explain关键字
     *
     * @param dtuicTenantId
     * @param taskType
     * @param sqlText
     * @param userId
     * @param tenantId
     * @param projectId
     * @param checkSyntax
     * @param isRoot
     * @param engineType
     * @param taskParam
     * @return
     */
    public CheckSyntaxResult processSqlText(final Long dtuicTenantId, Integer taskType, final String sqlText, final Long userId, final Long tenantId, final Long projectId,
                                            final boolean checkSyntax, final Boolean isRoot, final Integer engineType, String taskParam) {
        CheckSyntaxResult result = new CheckSyntaxResult();
        TenantEngine projectEngine = this.projectEngineService.getByTenantAndEngineType(projectId, engineType);
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine type %d", projectId, engineType));

//        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, taskType, projectId);
        final  ISqlExeService sqlExeService = null;    //todo
        result.setCheckResult(true);
        return result;
    }


    /**
     * 执行sql前的操作,操作权限检查
     */
    private void preExecuteSql(final ExecuteContent executeContent) {}

    private void afterExecuteSql(final ExecuteContent content) {}



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


}