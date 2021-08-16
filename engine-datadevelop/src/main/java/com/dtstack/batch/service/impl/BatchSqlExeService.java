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
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.common.util.SqlFormatUtil;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.utils.ParseResultUtils;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.vo.TaskCheckResultVO;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.domain.BatchTask;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.api.domain.User;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.lineage.impl.LineageService;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author jiangbo
 * @date 2019/6/14
 */
@Service
public class BatchSqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchSqlExeService.class);

    @Resource(name = "batchProjectService")
    private ProjectService projectService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    private LineageService lineageService;

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
        notDataMapOpera.add(EJobType.LIBRA_SQL.getVal());
        notDataMapOpera.add(EJobType.GREENPLUM_SQL.getVal());
        notDataMapOpera.add(EJobType.INCEPTOR_SQL.getVal());
    }

    @Forbidden
    public void directExecutionSql(final ExecuteContent executeContent) throws Exception {
        final Integer engineType = this.getEngineType(executeContent);
        executeContent.setEngineType(engineType);
        final String dbName = this.getDbName(executeContent);
        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        Long dtuicUserId = null;
        User user = userService.getUser(executeContent.getUserId());
        if (Objects.nonNull(user)) {
            dtuicUserId = user.getDtuicUserId();
        }
        sqlExeService.directExecutionSql(executeContent.getDtuicTenantId(),dtuicUserId, dbName, executeContent.getSql());
    }

    private String getDbName(final ExecuteContent executeContent) {
        String dbName = null;
        final Tenant tenantByDtUicTenantId = this.tenantService.getTenantByDtUicTenantId(executeContent.getDtuicTenantId());
        if (null != tenantByDtUicTenantId) {
            Long projectId = executeContent.getProjectId();
            if (projectId == null) {
                final Project project = this.projectService.getByName(executeContent.getProjectName(), tenantByDtUicTenantId.getId());
                if (project == null) {
                    throw new RdosDefineException("项目不能为空");
                }
                projectId = project.getId();
            }

            final ProjectEngine projectDb = this.projectEngineService.getProjectDb(projectId, executeContent.getEngineType());
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

    private void prepareExecuteContent(final ExecuteContent executeContent){
        final Integer engineType = this.getEngineType(executeContent);
        String taskParam = "";
        if (TableRelationType.TASK.getType().equals(executeContent.getRelationType())){
            BatchTask one = batchTaskService.getOne(executeContent.getRelationId());
            taskParam = one.getTaskParams();
        }
        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        final String sql = executeContent.getSql();
        //TODO cache lazy table 暂时不解析血缘，不知道这种类型的sql如何处理
        if(StringUtils.isNotBlank(sql) && (sql.toLowerCase().trim().startsWith("set") || CACHE_LAZY_SQL_PATTEN.matcher(sql).matches())){
            //set sql 不解析
            final ParseResult parseResult = new ParseResult();
            parseResult.setParseSuccess(true);
            parseResult.setOriginSql(executeContent.getSql());
            parseResult.setStandardSql(executeContent.getSql());
            executeContent.setParseResult(parseResult);
            return;
        }

        //单条sql解析
        if (StringUtils.isNotBlank(executeContent.getSql())) {
            //校验语法
            if (executeContent.isCheckSyntax()) {
                sqlExeService.checkSingleSqlSyntax(executeContent.getProjectId(), executeContent.getDtuicTenantId(), executeContent.getSql(), executeContent.getDatabase(), taskParam);
            }

            final ParseResult parseResult = this.parseSql(executeContent);
            executeContent.setParseResult(parseResult);

            boolean isExecuteSqlLater = false;
            if (SqlType.DROP.equals(parseResult.getSqlType())) {
                isExecuteSqlLater = true;
            }
            executeContent.setExecuteSqlLater(isExecuteSqlLater);
        }


        //批量解析sql
        List<ParseResult> parseResultList = Lists.newLinkedList();

        if (CollectionUtils.isNotEmpty(executeContent.getSqlList())){
            String finalTaskParam = taskParam;
            executeContent.getSqlList().forEach(x->{
                if (!x.trim().startsWith("set")){
                    if (executeContent.isCheckSyntax()) {
                        sqlExeService.checkSingleSqlSyntax(executeContent.getProjectId(), executeContent.getDtuicTenantId(), x, executeContent.getDatabase(), finalTaskParam);
                        executeContent.setSql(x);
                        final ParseResult batchParseResult = this.parseSql(executeContent);
                        parseResultList.add(batchParseResult);
                    }
                }else {
                    //set sql 不解析
                    final ParseResult batchParseResult = new ParseResult();
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
    private ParseResult parseSql(final ExecuteContent executeContent){
        final String dbName = this.getDbName(executeContent);
        executeContent.setDatabase(dbName);
        final Integer engineType = this.getEngineType(executeContent);
        final Integer taskType = executeContent.getDetailType();
        DataSourceType dataSourceType = multiEngineServiceFactory.getDataSourceTypeByEngineTypeAndTaskType(engineType, taskType, executeContent.getProjectId());
        ParseResult parseResult = null;
        try {
            com.dtstack.sqlparser.common.client.domain.ParseResult originParseResult = lineageService.parseSql(executeContent.getSql(), dbName, dataSourceType.getVal());
            parseResult = ParseResultUtils.convertParseResult(originParseResult);
        } catch (final Exception e) {
            BatchSqlExeService.LOG.error("解析sql异常:{}",e);
            parseResult = new ParseResult();
            //libra解析失败也提交sql执行
            if (MultiEngineType.HADOOP.getType() == engineType){
                parseResult.setParseSuccess(false);
            }
            parseResult.setFailedMsg(ExceptionUtils.getStackTrace(e));
            parseResult.setStandardSql(SqlFormatUtil.getStandardSql(executeContent.getSql()));
        }
        return parseResult;
    }

    /**
     *
     */
    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public ExecuteResultVO executeSql(final ExecuteContent executeContent) throws Exception {
        final ExecuteResultVO result = new ExecuteResultVO();
        final Integer engineType = this.getEngineType(executeContent);
        // 前置操作  权限校验放到最前面
        this.preExecuteSql(executeContent);
        result.setSqlText(executeContent.getSql());

        this.prepareExecuteContent(executeContent);

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        final ExecuteResultVO engineExecuteResult = sqlExeService.executeSql(executeContent);
        if (!engineExecuteResult.getIsContinue()) {
            return engineExecuteResult;
        }
        PublicUtil.copyPropertiesIgnoreNull(engineExecuteResult, result);

        return result;
    }

    /**
     *解析sqlList返回sqlId并且封装sql到引擎执行
     * @param executeContent
     * @return
     * @throws Exception
     */
    public ExecuteSqlParseVO batchExeSqlParse(final ExecuteContent executeContent)throws Exception {
        final Integer engineType = this.getEngineType(executeContent);
//        this.prepareExecuteContent(executeContent);
//        List<ParseResult> parseResultList = executeContent.getParseResultList();
//        final ExecuteSqlParseVO sqlParseVO = new ExecuteSqlParseVO();
//        if (MultiEngineType.HADOOP.getType() == engineType) {
//            parseResultList.forEach(parseResult -> {
//                if (!parseResult.isParseSuccess()) {
//                    sqlParseVO.setStatus(TaskStatus.FAILED.getStatus());
//                    sqlParseVO.setMsg(parseResult.getFailedMsg());
//                    sqlParseVO.setSqlText(parseResult.getOriginSql());
//                }
//            });
//            if (Objects.nonNull(sqlParseVO.getStatus()) && TaskStatus.FAILED.getStatus().equals(sqlParseVO.getStatus())){
//                return sqlParseVO;
//            }
//        }
//        //前置操作
//        this.preExecuteSql(executeContent);

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        return sqlExeService.batchExecuteSql(executeContent);
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
        ProjectEngine projectEngine = this.projectEngineService.getProjectDb(projectId, engineType);
        Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine type %d", projectId, engineType));

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, taskType, projectId);
        // 校验语法
//        String sqlPlus = buildCustomFunctionSparkSql(sqlText, projectId, taskType, engineType);
//        String sqls = sqlExeService.process(sqlPlus, projectEngine.getEngineIdentity());
//        result.setSql(sqls);
//        if (checkSyntax) {
//            List<ParseResult> parseResultList;
//            try {
//                parseResultList = sqlExeService.checkMulitSqlSyntax(dtuicTenantId, sqls, userId, projectId, taskParam);
//            }catch (Exception e){
//                LOG.error(String.format("语法校验失败，原因是:%s", e.getMessage()), e);
//                result.setMessage(e.getMessage());
//                result.setCheckResult(false);
//                return result;
//            }
//            ETableType tableType = TableTypeEngineTypeMapping.getTableTypeByEngineType(engineType);
//            ExecuteContent content;
//            for (ParseResult parseResult : parseResultList) {
//                content = new ExecuteContent();
//                content.setParseResult(parseResult)
//                        .setTenantId(tenantId)
//                        .setSql(parseResult.getOriginSql())
//                        .setUserId(userId)
//                        .setEngineType(engineType)
//                        .setProjectId(projectId)
//                        .setTableType(tableType.getType())
//                        .setProjectId(projectId)
//                        .setSql(parseResult.getStandardSql())
//                        .setRootUser(isRoot);
//                this.preExecuteSql(content);
//            }
//        }
        result.setCheckResult(true);
        return result;
    }


    /**
     * 执行sql前的操作,操作权限检查
     */
    private TaskCheckResultVO preExecuteSql(final ExecuteContent executeContent) {
        String dbName = getDbName(executeContent);
        ParseResult parseResult = new ParseResult();
        parseResult.setSqlType(SqlType.QUERY);
        parseResult.setStandardSql(SqlFormatUtil.getStandardSql(executeContent.getSql()));
        executeContent.setParseResult(parseResult);
        executeContent.setDatabase(dbName);
        return null;
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


}