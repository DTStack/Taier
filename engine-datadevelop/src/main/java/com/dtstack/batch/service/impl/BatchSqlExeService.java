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
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.PublishTaskStatusEnum;
import com.dtstack.batch.common.enums.RelationResultType;
import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchTableInfoDao;
import com.dtstack.batch.dao.BatchTablePermissionDao;
import com.dtstack.batch.dao.BatchTableRelationDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.service.ITableService;
import com.dtstack.batch.enums.HiveTablePermissionType;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.mapping.TableTypeEngineTypeMapping;
import com.dtstack.batch.service.auth.IAuthService;
import com.dtstack.batch.service.datamask.impl.DataMaskConfigService;
import com.dtstack.batch.service.datamask.impl.LineageService;
import com.dtstack.batch.service.datasource.impl.BatchDataSourceService;
import com.dtstack.batch.service.table.ISqlExeService;
import com.dtstack.batch.service.table.impl.BatchActionRecordService;
import com.dtstack.batch.service.table.impl.BatchTableInfoService;
import com.dtstack.batch.service.table.impl.BatchTableRelationService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.utils.ParseResultUtils;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.batch.vo.TaskCheckResultVO;
import com.dtstack.dtcenter.common.annotation.Forbidden;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.login.domain.LicenseProductComponent;
import com.dtstack.dtcenter.common.login.domain.LicenseProductComponentWidget;
import com.dtstack.dtcenter.common.util.PublicUtil;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.engine.api.enums.TableOperateEnum;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.api.pojo.lineage.Table;
import com.dtstack.engine.api.vo.lineage.ColumnLineageParseInfo;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.api.vo.lineage.param.ParseColumnLineageParam;
import com.dtstack.sqlparser.common.utils.SqlFormatUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author jiangbo
 * @date 2019/6/14
 */
@Service
public class BatchSqlExeService {

    public static Logger LOG = LoggerFactory.getLogger(BatchSqlExeService.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private BatchTableInfoService batchTableInfoService;

    @Autowired
    private BatchTableRelationService batchTableRelationService;

    @Autowired
    private BatchActionRecordService batchActionRecordService;

    @Autowired
    private BatchTablePermissionDao batchTablePermissionDao;

    @Autowired
    private BatchTableInfoDao batchTableInfoDao;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DataMaskConfigService dataMaskConfigService;

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    @Autowired
    private IAuthService authService;

    @Autowired
    private BatchDataSourceService batchDataSourceService;

    @Autowired
    private UserService userService;

    @Autowired
    private BatchTableRelationDao batchTableRelationDao;

    @Autowired
    private ITableService iTableServiceImpl;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private LineageService lineageService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private com.dtstack.engine.api.service.LineageService engineLineageService;

    private static final String SHOW_LIFECYCLE = "%s表的生命周期为%s天";

    private static final String NOT_GET_COLUMN_SQL_REGEX = "(?i)(create|drop)[\\s|\\S]*";

    private static final String CREATE_AS_REGEX = "(?i)create\\s+((table)|(view))\\s*[\\s\\S]+\\s*as\\s+select\\s+[\\s\\S]+";

    public static final Pattern CACHE_LAZY_SQL_PATTEN = Pattern.compile("(?i)cache\\s+(lazy\\s+)?table.*");

    private static final String CREATE_TEMP_FUNCTION_SQL = "%s %s";

    private static final List<SqlType> CreateTableTypeList = Arrays.asList(
            SqlType.CREATE, SqlType.CREATE_AS, SqlType.CREATE_LIKE
    );

    private static final Set<Integer> notDataMapOpera = new HashSet<>();
    static {
        notDataMapOpera.add(EJobType.ORACLE_SQL.getVal());
        notDataMapOpera.add(EJobType.LIBRA_SQL.getVal());
        notDataMapOpera.add(EJobType.GREENPLUM_SQL.getVal());
        notDataMapOpera.add(EJobType.INCEPTOR_SQL.getVal());
    }

    /**
     * 不允许执行的sql类型
     */
    private static final Set<SqlType> notSupportExecuteSql = Sets.newHashSet(SqlType.WITH_QUERY);

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
        final String formatSql = SqlFormatUtil.formatSql(executeContent.getSql());
        final boolean needColumns = StringUtils.isNotEmpty(formatSql) && (!formatSql.matches(BatchSqlExeService.NOT_GET_COLUMN_SQL_REGEX) || formatSql.matches(BatchSqlExeService.CREATE_AS_REGEX));
        final String dbName = this.getDbName(executeContent);
        executeContent.setDatabase(dbName);
        final Integer engineType = this.getEngineType(executeContent);
        final Integer taskType = executeContent.getDetailType();
        DataSourceType dataSourceType = multiEngineServiceFactory.getDataSourceTypeByEngineTypeAndTaskType(engineType, taskType, executeContent.getProjectId());
        ParseResult parseResult = null;
        Map<String, List<Column>> tableColsMap = new HashMap<>();
        if (needColumns) {
            try {
                final List<Table> tables = engineLineageService.parseTables(executeContent.getSql(), dbName, dataSourceType.getVal()).getData();
                tableColsMap = iTableServiceImpl.getTablesColumns(executeContent.getDtuicTenantId(), null, ETableType.getTableType(executeContent.getTableType()), tables);
            } catch (final Exception e) {
                parseResult = new ParseResult();
                executeContent.setParseResult(parseResult);
                //单 session  sql解析出错也允许执行
                parseResult.setParseSuccess(true);
                parseResult.setStandardSql(SqlFormatUtil.formatSql(executeContent.getSql()));
                return parseResult;
            }
        }
        if (tableColsMap == null){
            tableColsMap = new HashMap<>();
        }
        try {
            ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
            parseColumnLineageParam.setSql(executeContent.getSql());
            parseColumnLineageParam.setDefaultDb(dbName);
            parseColumnLineageParam.setDataSourceType(dataSourceType.getVal());
            parseColumnLineageParam.setTableColumnsMap(tableColsMap);
            ColumnLineageParseInfo columnLineageParseInfo = engineLineageService.parseColumnLineage(parseColumnLineageParam).getData();
            parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
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
     * @param executeContent
     * @param result
     */
    private void checkCreateTableExist(final ExecuteContent executeContent, final ExecuteResultVO result){
        final ParseResult parseResult = executeContent.getParseResult();

        final Integer engineType = this.getEngineType(executeContent);

        result.setSqlText(executeContent.getSql());

        //建表语句添加生命周期提示
        //parseResult.getMainTable().isIgnore() == true 意味有if not exists
        if (BatchSqlExeService.CreateTableTypeList.contains(parseResult.getSqlType())
                && parseResult.getMainTable().getLifecycle() != null && !parseResult.getMainTable().isIgnore()) {
            final Long projectIdFromMainTable = this.getProjectIdFromMainTable(parseResult, executeContent.getTenantId(), engineType);
            final ETableType tableType = TableTypeEngineTypeMapping.getTableTypeByEngineType(engineType);
            final BatchTableInfo tableInfo = this.batchTableInfoDao.getByTableName(parseResult.getMainTable().getName(), executeContent.getTenantId(),
                    projectIdFromMainTable == null ? executeContent.getProjectId() : projectIdFromMainTable, null == tableType ? 0 : tableType.getType());

            if (tableInfo != null) {
                result.setMsg(parseResult.getMainTable().getName() + " 表已存在，无须重复创建");
                result.setStatus(TaskStatus.FAILED.getStatus());
                return;
            }
            if (ETableType.HIVE.equals(tableType)) {
                //libra 没有lifecycle
                result.setMsg(String.format(BatchSqlExeService.SHOW_LIFECYCLE, parseResult.getMainTable().getName(), parseResult.getMainTable().getLifecycle()));
            }
        }
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
        TaskCheckResultVO checkPermissionVO = this.preExecuteSql(executeContent);
        result.setSqlText(executeContent.getSql());
        if (!PublishTaskStatusEnum.NOMAL.getType().equals(checkPermissionVO.getErrorSign())){
            result.setMsg(checkPermissionVO.getErrorMessage());
            result.setStatus(TaskStatus.FAILED.getStatus());
            return result;
        }
        this.prepareExecuteContent(executeContent);
        final ParseResult parseResult = executeContent.getParseResult();

        if (MultiEngineType.HADOOP.getType() == engineType){
            if (!parseResult.isParseSuccess()){
                final ExecuteResultVO resultVO = new ExecuteResultVO();
                resultVO.setStatus(TaskStatus.FAILED.getStatus());
                resultVO.setMsg(parseResult.getFailedMsg());
                return resultVO;
            }
        }


        //建表语句检查
        if (parseResult.getExtraType() == null || !SqlType.CREATE_TEMP.equals(parseResult.getExtraType())) {
            this.checkCreateTableExist(executeContent, result);
        }
        if (TaskStatus.FAILED.getStatus().equals(result.getStatus())){
            return result;
        }

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        final ExecuteResultVO engineExecuteResult = sqlExeService.executeSql(executeContent);
        if (!engineExecuteResult.getIsContinue()) {
            return engineExecuteResult;
        }
        PublicUtil.copyPropertiesIgnoreNull(engineExecuteResult, result);

        try {
            this.afterExecuteSql(executeContent);
        } catch (final Exception e) {
            this.dropTableIfAddTableFail(executeContent);
            throw e;
        }

        // 删除操作把sql放到最后执行
        if (executeContent.isExecuteSqlLater()) {
            this.directExecutionSql(executeContent);
        }

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
        this.prepareExecuteContent(executeContent);
        List<ParseResult> parseResultList = executeContent.getParseResultList();
        final ExecuteSqlParseVO sqlParseVO = new ExecuteSqlParseVO();
        if (MultiEngineType.HADOOP.getType() == engineType) {
            parseResultList.forEach(parseResult -> {
                if (!parseResult.isParseSuccess()) {
                    sqlParseVO.setStatus(TaskStatus.FAILED.getStatus());
                    sqlParseVO.setMsg(parseResult.getFailedMsg());
                    sqlParseVO.setSqlText(parseResult.getOriginSql());
                }
            });
            if (Objects.nonNull(sqlParseVO.getStatus()) && TaskStatus.FAILED.getStatus().equals(sqlParseVO.getStatus())){
                return sqlParseVO;
            }
        }
        // 前置操作
        this.preExecuteSql(executeContent);

        final ISqlExeService sqlExeService = this.multiEngineServiceFactory.getSqlExeService(engineType, executeContent.getDetailType(), executeContent.getProjectId());
        return sqlExeService.batchExecuteSql(executeContent);
    }

    private void dropTableIfAddTableFail(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        if (SqlType.CREATE.equals(parseResult.getSqlType())
                || SqlType.CREATE_LIKE.equals(parseResult.getSqlType())) {
            final ProjectEngine projectDb = this.projectEngineService.getProjectDb(content.getProjectId(), content.getEngineType());
            Preconditions.checkNotNull(projectDb, "引擎不能为空");
            iTableServiceImpl.dropTable(content.getDtuicTenantId(), null, projectDb.getEngineIdentity(), ETableType.getTableType(content.getTableType()), parseResult.getMainTable().getName());
        }
    }

    private void checkLicense() {
        final String uicUrl = this.environmentContext.getDtUicUrl();
        final String componentCode = DTComponentCode.BATCH.getCode();
        final LicenseProductComponent licenseProductComponent = this.authService.fetchLicense(uicUrl,componentCode);

        if (licenseProductComponent == null) {
            throw new RdosDefineException("缺少 License");
        }

        final Optional<LicenseProductComponentWidget> optionalLicenseProductComponentWidget = licenseProductComponent
                .getWidgets()
                .stream()
                .filter(w -> w.getWidgetType().equals(1))
                .filter(w -> "batchTableInfo/ddlCreateTable;batchTableInfo/createDirtyTable"
                        .equals(w.getEffectApi()))
                .findFirst();

        if (optionalLicenseProductComponentWidget.isPresent()) {
            final LicenseProductComponentWidget widget = optionalLicenseProductComponentWidget.get();

            if (!this.batchTableInfoService.capableOfCreate((Integer) widget.getValue())) {
                throw new RdosDefineException("超过 License 限制");
            }
        }
    }

    private Long getProjectIdFromMainTable(final ParseResult parseResult, final Long tenantId, final Integer engineType) {
        if (parseResult.getMainTable() != null) {
            if (StringUtils.isNotEmpty(parseResult.getMainTable().getDb())) {
                final Project project = this.projectEngineService.getProjectByDbName(parseResult.getMainTable().getDb(), engineType, tenantId);
                if (project != null) {
                    return project.getId();
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 处理spark sql和Hive sql自定义函数
     * @param sqlText
     * @param projectId
     * @param taskType
     * @param engineType
     * @return
     */
    public String buildCustomFunctionSparkSql(String sqlText, Long projectId, Integer taskType, Integer engineType) {
        String sqlPlus = SqlFormatUtil.formatSql(sqlText);
        if (engineType == MultiEngineType.HADOOP.getType() && !EJobType.IMPALA_SQL.getVal().equals(taskType)) {
            String containFunction = batchFunctionService.buildContainFunction(sqlText, projectId);
            if (StringUtils.isNotBlank(containFunction)) {
                sqlPlus = String.format(CREATE_TEMP_FUNCTION_SQL,containFunction,sqlPlus);
            }
        }
        return sqlPlus;
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
        String sqlPlus = buildCustomFunctionSparkSql(sqlText, projectId, taskType, engineType);
        String sqls = sqlExeService.process(sqlPlus, projectEngine.getEngineIdentity());
        result.setSql(sqls);
        if (checkSyntax) {
            List<ParseResult> parseResultList;
            try {
                parseResultList = sqlExeService.checkMulitSqlSyntax(dtuicTenantId, sqls, userId, projectId, taskParam);
            }catch (Exception e){
                LOG.error(String.format("语法校验失败，原因是:%s", e.getMessage()), e);
                result.setMessage(e.getMessage());
                result.setCheckResult(false);
                return result;
            }
            ETableType tableType = TableTypeEngineTypeMapping.getTableTypeByEngineType(engineType);
            ExecuteContent content;
            for (ParseResult parseResult : parseResultList) {
                content = new ExecuteContent();
                content.setParseResult(parseResult)
                        .setTenantId(tenantId)
                        .setSql(parseResult.getOriginSql())
                        .setUserId(userId)
                        .setEngineType(engineType)
                        .setProjectId(projectId)
                        .setTableType(tableType.getType())
                        .setProjectId(projectId)
                        .setSql(parseResult.getStandardSql())
                        .setRootUser(isRoot);
                this.preExecuteSql(content);
            }
        }
        result.setCheckResult(true);
        return result;
    }

    public TaskCheckResultVO checkTablePermission(List<String> sqlList,ExecuteContent executeContent, Boolean ignoreCheck){
        TaskCheckResultVO checkPermissionVO = new TaskCheckResultVO();
         if (CollectionUtils.isNotEmpty(sqlList) && (!executeContent.getTableType().equals(ETableType.LIBRA.getType()))){
             for (String sql : sqlList){

                 if (sql.toLowerCase().startsWith("use")) {
                     continue;
                 }

                 if (sql.toLowerCase().startsWith("set")) {
                     continue;
                 }
                 checkPermissionVO =  preExecuteSql(executeContent.setSql(sql));
                 if (!PublishTaskStatusEnum.NOMAL.getType().equals(checkPermissionVO.getErrorSign())){
                     if (!ignoreCheck && PublishTaskStatusEnum.CHECKSYNTAXERROR.getType().equals(checkPermissionVO.getErrorSign())) {
                        continue;
                     }
                     return checkPermissionVO;
                 }
             }
         }
        checkPermissionVO.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());
         return checkPermissionVO;

    }

    /**
     * 执行sql前的操作,操作权限检查
     */
    private TaskCheckResultVO preExecuteSql(final ExecuteContent executeContent) {
        Long dtuicTenantId = this.tenantService.getDtuicTenantId(executeContent.getTenantId());
        executeContent.setDtuicTenantId(dtuicTenantId);
        TaskCheckResultVO checkPermissionVO = new TaskCheckResultVO();
        checkPermissionVO.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());
        String dbName = getDbName(executeContent);
        //只校验hadoop引擎的任务 只有sparkSql和hiveSql
        if (MultiEngineType.HADOOP.getType() != executeContent.getEngineType()) {
            return checkPermissionVO;
        }

        if (StringUtils.isBlank(dbName)){
            //进入这里说明dtUicTenantId 在数栈中找不到对应的项目
            checkPermissionVO.setErrorSign(PublishTaskStatusEnum.PERMISSIONERROR.getType());
            return checkPermissionVO;
        }
        //提前做一次血缘解析 主要是为了 校验权限
        if (BatchSqlExeService.CACHE_LAZY_SQL_PATTEN.matcher(executeContent.getSql().toLowerCase()).matches()){
            //cache table 替换成create table
            executeContent.setSql(executeContent.getSql().trim().replaceAll("(?i)^cache\\s+(lazy\\s+)?table","create table "));
        }
        ParseResult parseResult ;
        try {
            ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
            parseColumnLineageParam.setSql(executeContent.getSql());
            parseColumnLineageParam.setDefaultDb(executeContent.getDatabase());
            parseColumnLineageParam.setDataSourceType(DataSourceType.Spark.getVal());
            parseColumnLineageParam.setTableColumnsMap(Maps.newHashMap());
            ColumnLineageParseInfo columnLineageParseInfo = engineLineageService.parseColumnLineage(parseColumnLineageParam).getData();
            parseResult = ParseResultUtils.convertParseResult(columnLineageParseInfo);
        } catch (final Exception e) {
            BatchSqlExeService.LOG.error("权限校验 解析sql异常:{}",e);
            //防止空指针 先塞入默认值
            parseResult = new ParseResult();
            parseResult.setSqlType(SqlType.QUERY);
            parseResult.setFailedMsg(ExceptionUtils.getStackTrace(e));
            parseResult.setStandardSql(SqlFormatUtil.getStandardSql(executeContent.getSql()));
        }
        executeContent.setParseResult(parseResult);
        //校验sql是否可以运行
        if (!checkSqlCanExecute(checkPermissionVO, executeContent)) {
            return checkPermissionVO;
        }
        // 权限判断
        List<ProjectEngine> projectByDbNameList = projectEngineService.getProjectByDbNameList(executeContent.getEngineType(), null);
        List<ProjectEngine> userCanLookProject = projectEngineService.getProjectListByUserId(executeContent.getEngineType(), executeContent.getUserId());
        if (CollectionUtils.isNotEmpty(projectByDbNameList) && CollectionUtils.isNotEmpty(userCanLookProject)) {
            try {
                checkPermissionVO = checkSchemaAndTableAuth(executeContent, projectByDbNameList, userCanLookProject);
            }catch (Exception e){
                LOG.error(String.format("校验权限失败，原因是：%s", e.getMessage()), e);
                checkPermissionVO.setErrorSign(PublishTaskStatusEnum.PERMISSIONERROR.getType());
            }
        }else {
            if (CollectionUtils.isEmpty(projectByDbNameList)){
                throw new RdosDefineException(String.format("鉴权失败，查询不到engineType = %s 的项目",executeContent.getEngineType()));
            }
            if (CollectionUtils.isEmpty(userCanLookProject)){
                throw new RdosDefineException(String.format("鉴权失败，根据当前 engineType = %s userId = %s 查询不到可用的项目",executeContent.getEngineType(),executeContent.getUserId()));
            }
        }
        return checkPermissionVO;
    }

    /**
     * 校验sql的类型是否可以运行
     *
     * @param checkSyntaxResult
     * @param executeContent
     * @return
     */
    private Boolean checkSqlCanExecute(TaskCheckResultVO checkSyntaxResult, ExecuteContent executeContent){

        SqlType type = executeContent.getParseResult().getSqlType();

        // 特殊校验 license
        if (BatchSqlExeService.CreateTableTypeList.contains(type)) {
            this.checkLicense();
        }

        // 部分sql不允许执行
        if (SqlType.getForbidenType().contains(type)) {
            throw new RdosDefineException(String.format("can not execute this sql: %s", executeContent.getParseResult().getOriginSql()));
        }

        // explain句式不做处理
        if (SqlType.EXPLAIN.equals(type)) {
            return false;
        }

        if (notSupportExecuteSql.contains(type)) {
            //修改为不抛出异常 返回错误信息
            checkSyntaxResult.setErrorMessage(String.format("页面运行暂不支持%s语句，周期运行无此限制。", type.getType()));
            checkSyntaxResult.setErrorSign(PublishTaskStatusEnum.CHECKSYNTAXERROR.getType());
            return false;
        }

        if (executeContent.isRootUser()) {
            return false;
        }
        return true;
    }

    /**
     * 向map中增加数据
     * @param schemaAndTable
     * @param table
     */
    private void insetSchameAndTableMap(Map<String,Set<String>> schemaAndTable, Table table){
        if (schemaAndTable.containsKey(table.getDb())){
            schemaAndTable.get(table.getDb()).add(table.getName());
        }else {
            Set<String> tableList = new HashSet<>();
            tableList.add(table.getName());
            schemaAndTable.put(table.getDb(),tableList);
        }
    }


    /**
     * 执行sql后的操作
     */
    private void afterExecuteSql(final ExecuteContent content) throws Exception {
        if (notDataMapOpera.contains(content.getDetailType())) {
            return;
        }
        final SqlType sqlType = content.getParseResult().getSqlType();
        boolean addRelation = true;
        boolean addRecord = false;
        Boolean createSqlCheck = SqlType.CREATE.equals(sqlType)
                || SqlType.CREATE_LIKE.equals(sqlType)
                || (content.getTableType() == ETableType.ADB_FOR_PG.getType() && SqlType.CREATE_AS.equals(sqlType));
        if (createSqlCheck) {
            addRelation = this.processCreateTable(content);
            addRecord = true;
        } else if (SqlType.EXPLAIN.equals(sqlType)) {
            addRelation = false;
        } else if (SqlType.getShowType().contains(sqlType) || SqlType.TRUNCATE.equals(sqlType)) {
            this.processTableTime(content);
        } else if (SqlType.DROP.equals(sqlType)) {
            this.processDropTable(content);
            addRelation = false;
        } else if (SqlType.ALTER.equals(sqlType)) {
            this.processAlterTable(content);
            addRelation = false;
        } else if (SqlType.INSERT.equals(sqlType) || SqlType.INSERT_OVERWRITE.equals(sqlType)) {
            this.processDMLSql(content);
        }

        if (addRelation) {
            this.addRelation(content);
        }
        // 所有的sql操作都添加操作记录
        if (addRecord){
            this.addRecord(content);
        }
        if (MultiEngineType.TIDB.getType() == content.getEngineType() && Objects.nonNull(content.getRelationId())) {
            // TIDB血缘关系维护
            BatchTask batchTask = batchTaskService.getBatchTaskById(content.getRelationId());
            lineageService.parseLineageFromSql(content.getSql(), content.getTenantId(), content.getProjectId(),
                    content.getDatabase(), content.getDtuicTenantId(), content.getUserId(), MultiEngineType.TIDB.getType(), batchTask);
        }
    }

    private void addRecord(final ExecuteContent content){
        this.batchActionRecordService.addRecord(content.getParseResult(), content.getTenantId(), content.getUserId(), content.getEngineType());
    }

    private boolean processCreateTable(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        if (parseResult.getExtraType() != null && SqlType.CREATE_TEMP.equals(parseResult.getExtraType())) {
            return false;
        }
        final Long projectIdFromMainTable = this.getProjectIdFromMainTable(parseResult, content.getTenantId(), content.getEngineType());

        BatchTableInfo tableInfo = this.batchTableInfoDao.getByTableName(parseResult.getMainTable().getName(),
                content.getTenantId(), projectIdFromMainTable == null ? content.getProjectId() : projectIdFromMainTable, content.getTableType());
        if (null != tableInfo && parseResult.getMainTable().isIgnore()) {
            return false;
        }

        if (null == tableInfo) {
            tableInfo = new BatchTableInfo();
            tableInfo.setTableType(content.getTableType());
        }

        final MultiEngineType engineType = TableTypeEngineTypeMapping.getEngineTypeByTableType(tableInfo.getTableType());
        Preconditions.checkNotNull(engineType, String.format("not support table type:%d", tableInfo.getTableType()));

        //增加表元数据时需要使用真实项目
        final Project realProject = this.projectEngineService.getProjectByDbName(parseResult.getMainTable().getDb(), engineType.getType(), content.getTenantId());
        if (Objects.isNull(realProject)) {
            BatchSqlExeService.LOG.warn("realProject can not find, db={}, engineType ={}, tenantId={}", parseResult.getMainTable().getDb(), engineType.getType(), content.getTenantId());
            return false;
        }
        this.batchTableInfoService.addTableFromSql(content.getDtuicTenantId(), content.getTenantId(), realProject.getId(),
                parseResult.getMainTable().getName(), parseResult.getMainTable().getLifecycle(),
                parseResult.getMainTable().getCatalogueId(), content.getUserId(),
                0, false, content.getTableType(), parseResult.getMainTable().getDb());

        return true;
    }

    private void processDMLSql(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        Long projectIdFromMainTable = null;
        try {
            projectIdFromMainTable = this.getProjectIdFromMainTable(parseResult, content.getTenantId(), content.getEngineType());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        if (Objects.isNull(projectIdFromMainTable)){
            return;
        }
        final BatchTableInfo tableInfo = this.batchTableInfoDao.getByTableName(parseResult.getMainTable().getName(), content.getTenantId(),
                projectIdFromMainTable, content.getTableType());

        if (tableInfo == null) {
            //操作的表的db/schema不在数栈管理中时，不执行后续操作
            return;
        }

        // 修改表的dml
        this.batchTableInfoDao.updateDMLTime(tableInfo.getTableName(), tableInfo.getProjectId());
    }

    private void processDropTable(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        final Long projectIdFromMainTable = this.getProjectIdFromMainTable(parseResult, content.getTenantId(), content.getEngineType());
        final BatchTableInfo tableInfo = this.batchTableInfoDao.getByTableName(parseResult.getMainTable().getName(),
                content.getTenantId(), projectIdFromMainTable == null ? content.getProjectId() : projectIdFromMainTable, content.getTableType());

        if (tableInfo == null) {
            //如表为空则表示当前表不受数栈管理可直接删除
            return;
        }

        final Long tableId = tableInfo.getId();
        deleteTable(content.getProjectId(),content.getRelationId(), content.getTenantId(), tableInfo, tableId, content.getUserId());

        // 操作记录
        this.batchActionRecordService.addRecord(tableInfo, content.getUserId(), parseResult.getStandardSql(), TableOperateEnum.ALTER);

        DataSourceType dataSourceType = multiEngineServiceFactory.getDataSourceTypeByEngineTypeAndProjectId(content.getEngineType(), content.getProjectId());
        if (dataSourceType != null) {
            ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
            parseColumnLineageParam.setAppType(AppType.RDOS.getType());
            parseColumnLineageParam.setDataSourceType(dataSourceType.getVal());
            parseColumnLineageParam.setDefaultDb(parseResult.getMainTable().getDb());
            parseColumnLineageParam.setSql(parseResult.getOriginSql());
            parseColumnLineageParam.setDtUicTenantId(content.getDtuicTenantId());
            parseColumnLineageParam.setProjectId(tableInfo.getProjectId());
            engineLineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
        }
    }

    @Forbidden
    @Transactional(rollbackFor = Exception.class)
    public void deleteTable(final Long projectId,final Long relationId, final Long tenantId, final BatchTableInfo tableInfo, final Long tableId, final Long userId) {

        //删除表与任务的关系
        if (relationId != null) {
            this.batchTableRelationDao.deleteByRelationId(relationId, projectId);
        }

        // 删除表
        this.batchTableInfoService.delete(tenantId, projectId, tableInfo);

        // 删除表和任务，脚本的关系
        this.batchTableRelationService.deleteByTableName(tableInfo.getTableName(), tableInfo.getTenantId());

        this.dataMaskConfigService.deleteMaskColumnsInfoByTableId(tableId, userId);

        // 处理bug 发现需求变动 在数据地图删除表的时候 不处理表权限和删除申请记录
    }

    private void processAlterTable(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        final Long projectIdFromMainTable = this.getProjectIdFromMainTable(parseResult, content.getTenantId(), content.getEngineType());
        BatchTableInfo tableInfo = this.batchTableInfoDao.getByTableName(parseResult.getMainTable().getName(), content.getTenantId(),
                projectIdFromMainTable == null ? content.getProjectId() : projectIdFromMainTable, content.getTableType());

        if (parseResult.getAlterResult().getAlterType().equals(TableOperateEnum.ALTERTABLE_RENAME)) {
            DataSourceType dataSourceType = multiEngineServiceFactory.getDataSourceTypeByEngineTypeAndProjectId(content.getEngineType(), content.getProjectId());
            if (dataSourceType != null) {
                ParseColumnLineageParam parseColumnLineageParam = new ParseColumnLineageParam();
                parseColumnLineageParam.setAppType(AppType.RDOS.getType());
                parseColumnLineageParam.setDataSourceType(dataSourceType.getVal());
                parseColumnLineageParam.setDefaultDb(parseResult.getMainTable().getDb());
                parseColumnLineageParam.setSql(parseResult.getOriginSql());
                parseColumnLineageParam.setDtUicTenantId(content.getDtuicTenantId());
                parseColumnLineageParam.setProjectId(tableInfo.getProjectId());
                engineLineageService.parseAndSaveColumnLineage(parseColumnLineageParam);
            }
        }

        if (tableInfo == null) {
            throw new RdosDefineException("该表不存在:" + parseResult.getMainTable().getName());
        }

        // 修改表信息
        this.batchTableInfoService.alterTableInfo(parseResult, tableInfo, content.getUserId());

        // 操作记录
        batchActionRecordService.addRecord(tableInfo, content.getUserId(), parseResult.getStandardSql(), TableOperateEnum.ALTER);

        // 添加sql和任务，脚本的关联关系
        if (content.getRelationId() != null) {
            this.batchTableRelationService.addRelation(content.getTenantId(), content.getProjectId(), this.batchDataSourceService.getDefaultDataSourceByTableType(tableInfo.getTableType(),content.getProjectId()),
                    tableInfo.getTableName(), tableInfo.getId(), content.getRelationId(), content.getRelationType(), content.getDetailType(), RelationResultType.IS_RESULT.getVal());
        }
    }

    private void addRelation(final ExecuteContent content) {
        final ParseResult parseResult = content.getParseResult();
        // 添加sql和任务，脚本的关联关系
        if (content.getRelationId() != null) {
            this.batchTableRelationService.addRelation(parseResult.getTables(), content.getRelationId(), content.getRelationType(),
                    content.getDetailType(), content.getTenantId(), content.getProjectId(), content.getTableType());
        }
    }

    private void processTableTime(final ExecuteContent content) {
        final List<Table> tables = content.getParseResult().getTables();
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }

        final List<Project> projects = this.projectService.getAllProjects(content.getTenantId(), Boolean.TRUE, content.getUserId(), false);
        final Map<String, Long> pNameIdMap = new HashMap<>(projects.size());
        for (final Project project : projects) {
            pNameIdMap.put(project.getProjectName(), project.getId());
        }

        for (final Table table : tables) {
            if (TableOperateEnum.getDDLOperate().contains(table.getOperate())) {
                this.batchTableInfoDao.updateDDLTime(table.getName(), pNameIdMap.get(table.getDb()));
            } else if (TableOperateEnum.getDMLOperate().contains(table.getOperate())) {
                this.batchTableInfoDao.updateDMLTime(table.getName(), pNameIdMap.get(table.getDb()));
            }
        }
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
     * 校验sql中 涉及的表和scheam是否有权限
     * @param executeContent
     * @return
     */
    private TaskCheckResultVO checkSchemaAndTableAuth(ExecuteContent executeContent,List<ProjectEngine> projectByDbNameList,List<ProjectEngine> userCanLookProject){
        TaskCheckResultVO vo = new TaskCheckResultVO();
        Map<String,Set<String>> schemaAndTable = new HashMap<>();
        ParseResult parseResult = executeContent.getParseResult();
        //判断解析是否成功 就是判断tables和mainTable是否都是空的 如果都是空的 则返回true
        if (CollectionUtils.isEmpty(parseResult.getTables())){
            if (parseResult.getMainTable() == null || StringUtils.isBlank(parseResult.getMainTable().getName())){
                //认为解析失败
                vo.setErrorSign(PublishTaskStatusEnum.NOMAL.getType());
                return vo;
            }else {
                //alter类型和other类型
                insetSchameAndTableMap(schemaAndTable,parseResult.getMainTable());
            }
        }else {
            //tables有值 且一定包含mainTable
            List<Table> tables = parseResult.getTables();
            for (Table table : tables){
                insetSchameAndTableMap(schemaAndTable,table);
            }
        }

        //校验逻辑 先校验schema
        Set<String> schemaNames = schemaAndTable.keySet();
        //分一下 没有权限 和 不在数栈里
        // 这个集合是放数据库里存在的schema
        Set<String> dbSchemaNames = new HashSet<>();
        // 这个集合放 该sql涉及到的 存在数据库中db
        Set<String> exitDB = new HashSet<>();
        // 这个集合放 在数栈里但是用户没有权限的db
        Set<String> notPermissionDb = new HashSet<>();
        // 这个集合里面放的是 不在数据库里的schema
        Set<String> noExitsDb = new HashSet<>();

        if (CollectionUtils.isNotEmpty(projectByDbNameList)){
            dbSchemaNames = projectByDbNameList.stream().map(ProjectEngine::getEngineIdentity).collect(Collectors.toSet());
        }
        if (CollectionUtils.isNotEmpty(schemaNames)) {
            for (String dbName : schemaNames) {
                if (!dbSchemaNames.contains(dbName)) {
                    noExitsDb.add(dbName);
                } else {
                    exitDB.add(dbName);
                }
            }
        }

        //首先校验schema 判断  如果用户在项目对应的 有角色id 就可以查看到项目
        Set<String> userCanLookDB = new HashSet<>();
        if (CollectionUtils.isNotEmpty(userCanLookProject)){
            userCanLookDB = userCanLookProject.stream().map(ProjectEngine::getEngineIdentity).collect(Collectors.toSet());
            if (CollectionUtils.isNotEmpty(exitDB)){
                for (String dbName : exitDB){
                    if (!userCanLookDB.contains(dbName)){
                        notPermissionDb.add(dbName);
                    }
                }
            }
        }else {
            notPermissionDb.addAll(exitDB);
        }

        //做一个名称和id对应map 用于后面查询
        Map<String,List<ProjectEngine>> projectNameAndIdMap = new HashMap<>();
        for (ProjectEngine projectEngine : projectByDbNameList){
            if (projectNameAndIdMap.containsKey(projectEngine.getEngineIdentity())) {
                projectNameAndIdMap.get(projectEngine.getEngineIdentity()).add(projectEngine);
            }else {
                List<ProjectEngine> list = new ArrayList<>();
                list.add(projectEngine);
                projectNameAndIdMap.put(projectEngine.getEngineIdentity(), list);
            }
        }

        //判断表的权限
        //放不存在的表
        Set<String> noExitTable = new HashSet<>();
        //放没有权限的表
        Set<String> noPermissionTable = new HashSet<>();

        //-- 根据db 查询对应的projecId tenantId查询表
        for (String schame : schemaAndTable.keySet()){
            if (!noExitsDb.contains(schame)) {
                if (!userCanLookDB.contains(schame)) {
                    //用户看不到这个schema时 才需要查询表权限就拥有权限
                    if (parseResult.getSqlType().equals(SqlType.CREATE) || parseResult.getSqlType().equals(SqlType.CREATE_AS)){
                        if (schame.equals(parseResult.getMainTable().getDb())) {
                            notPermissionDb.add(String.format("%s", schame));
                            break;
                        }
                    }
                    List<ProjectEngine> projectEngine = projectNameAndIdMap.get(schame);
                    if (CollectionUtils.isEmpty(projectEngine)){
                        //如果根据当前表的schema没拿到数据 说明 血缘解析有问题  直接标记不存在
                        noExitsDb.add(schame);
                        break;
                    }
                    List<Long> projectIds = projectEngine.stream().map(ProjectEngine::getProjectId).collect(Collectors.toList());
                    for (String tableName : schemaAndTable.get(schame)) {
                        List<BatchTableInfo> byTableName = batchTableInfoDao.listByTableNameAndProjectIds(tableName, projectIds,executeContent.getTableType());
                        if (CollectionUtils.isEmpty(byTableName)) {
                            noExitTable.add(tableName);
                        } else {
                            Boolean aBoolean = checkTablePermission(schame,byTableName, executeContent);
                            if (!aBoolean) {
                                noPermissionTable.add(String.format("%s.%s",schame,tableName));
                            } else {
                                //如果 有该表的权限 就把该表的schema从删除
                                notPermissionDb.remove(schame);
                            }
                        }
                    }
                }
            }else {
                for (String tableName : schemaAndTable.get(schame)) {
                    noExitTable.add(String.format("%s.%s",schame,tableName));
                }
            }
        }

        //拼装错误信息
        //先拼装db错误的
        Boolean isPass = true;
        StringBuffer errMsg = new StringBuffer();
        if (CollectionUtils.isNotEmpty(noExitsDb)){
            isPass = false;
            errMsg.append("以下schema在系统中不存在\n");
            for (String dbName : noExitsDb){
                errMsg.append(dbName).append(" ");
            }
            errMsg.append("\n");
        }

        if (CollectionUtils.isNotEmpty(notPermissionDb)){
            isPass = false;
            errMsg.append("以下schema当前用户无权限操作\n");
            for (String dbName : notPermissionDb){
                errMsg.append(dbName).append(" ");
            }
            errMsg.append("\n");
        }


        if (CollectionUtils.isNotEmpty(noExitTable)){
            isPass = false;
            errMsg.append("以下表在系统中不存在\n");
            for (String tableName : noExitTable){
                errMsg.append(tableName).append(" ");
            }
            errMsg.append("\n");
        }

        if (CollectionUtils.isNotEmpty(noPermissionTable)){
            isPass = false;
            errMsg.append("以下表当前用户无权限\n");
            for (String tableName : noPermissionTable){
                errMsg.append(tableName).append(" ");
            }
            errMsg.append("\n");
        }
        vo.setErrorSign(isPass ? PublishTaskStatusEnum.NOMAL.getType() : PublishTaskStatusEnum.PERMISSIONERROR.getType());
        vo.setErrorMessage(errMsg.toString());
        return vo;
    }

    /**
     * 校验table权限 分两步
     * 1.看表的归属人是不是自己
     * 2.看自己有没有表权限
     * mainTable sql的主表
     * SqlType 这是一条什么sql
     * schema 被判断的表的schema 用于判断是否为主表
     * mainSchema 主表的schema
     */
    private Boolean checkTablePermission(String schema,List<BatchTableInfo> batchTableInfos,ExecuteContent executeContent){
        SqlType sqlType = executeContent.getParseResult().getSqlType();
        Table mainTable = executeContent.getParseResult().getMainTable();
        //create语句 主表 不参与校验
        // 其他主表 根据 sql类型 校验
        // 首先校验表是否存在  不存在 记录
        // 校验存在的表 主表的话 判断sql类型  非主表 只校验select权限。
        for (BatchTableInfo batchTableInfo : batchTableInfos) {
            if (batchTableInfo.getChargeUserId().equals(executeContent.getUserId())) {
                return true;
            }
        }
        if (sqlType.equals(SqlType.CREATE)) {
            return true;
        }

        // 表结构的查看没有限制
        if (sqlType.equals(TableOperateEnum.SHOW)) {
            return true;
        }

        // 脏数据表不进行权限检测
        if (batchTableInfos.get(0).getIsDirtyDataTable() == 1) {
            return true;
        }
        List<Long> tableIds = batchTableInfos.stream().map(BaseEntity::getId).collect(Collectors.toList());

        List<BatchTablePermission> permission = batchTablePermissionDao.listByUserIdAndTableIds(executeContent.getUserId(), tableIds);

        if (batchTableInfos.get(0).getTableName().equals(mainTable.getName()) && schema.equals(mainTable.getDb())){
            if (CollectionUtils.isEmpty(permission)){
                return false;
            }
            //查找最高权限
            Integer permissionType = 0;
            for (BatchTablePermission tablePermission :permission){
                permissionType = tablePermission.getPermissionType()>permissionType ? tablePermission.getPermissionType() : permissionType;
            }
            //如果是主表 就需要校验sqlType
            if (sqlType.equals(SqlType.ALTER)||sqlType.equals(SqlType.DROP)){
                if (!permissionType.equals(HiveTablePermissionType.ALTER.getType())){
                    return false;
                }
            }else if (sqlType.equals(SqlType.INSERT)||sqlType.equals(SqlType.INSERT_OVERWRITE) || sqlType.equals(SqlType.TRUNCATE) ){
                if (permissionType.equals(HiveTablePermissionType.READ.getType())){
                    return false;
                }
            }

        }
        //如果不是主表 只需要校验 select权限就可以了
        return true;
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