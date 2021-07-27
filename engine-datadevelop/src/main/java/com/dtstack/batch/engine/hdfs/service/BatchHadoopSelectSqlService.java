package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.enums.TempJobType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.domain.*;
import com.dtstack.batch.engine.rdbms.common.IDownload;
import com.dtstack.batch.engine.rdbms.service.impl.Engine2DTOService;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.service.impl.*;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import com.dtstack.batch.service.table.impl.BatchSelectSqlService;
import com.dtstack.batch.service.task.impl.BatchTaskService;
import com.dtstack.batch.sync.job.SourceType;
import com.dtstack.batch.vo.BuildSqlVO;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.dtcenter.common.constant.TaskStatusConstrant;
import com.dtstack.dtcenter.common.engine.JdbcInfo;
import com.dtstack.dtcenter.common.enums.*;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.dtcenter.common.util.DtStringUtil;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.lineage.ColumnLineage;
import com.dtstack.engine.api.service.ScheduleJobService;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.lineage.SqlType;
import com.dtstack.engine.master.impl.ActionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 执行选中的sql或者脚本
 *
 * @author jiangbo
 */
@Service
public class BatchHadoopSelectSqlService implements IBatchSelectSqlService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BatchHadoopSelectSqlService.class);

    @Autowired
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private HadoopDataDownloadService hadoopDataDownloadService;

    @Autowired
    private BatchDownloadService batchDownloadService;

    @Autowired
    private BatchTaskService batchTaskService;

    @Autowired
    private RoleUserService roleUserService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchSelectSqlService batchSelectSqlService;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private BatchFunctionService batchFunctionService;

    @Autowired
    private UserService userService;

    @Autowired
    private ActionService actionService;

    private static ObjectMapper objectMapper = new ObjectMapper();

    private static final String CREATE_TEMP_TABLE = "use %s; create table %s stored as orc as select * from (%s)temp";

    private static final String CREATE_FUNCTION_TEMP_TABLE = "use %s;%s create table %s stored as orc as select * from (%s)temp";

    private static final String CREATE_TEMP_WITH_TABLE = "use %s; create table %s stored as orc as %s";

    private static final String USE_DB = "use %s; %s ;";

    private static final String USER_DB_TEMP_FUNCTION = "use %s; %s %s ;";

    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    public static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    private static final String WITH_SQL_REGET = "(?i)with[a-zA-Z0-9_,\\s]*as\\s*\\(\\s*select\\s*(\\*|[a-zA-Z0-9_,\\s]*?)\\s*from[a-zA-Z0-9_,\\s]*\\)\\s*(?<option>[a-zA-Z]+)\\s*";

    private static final Pattern WITH_SQL_PATTERN = Pattern.compile(WITH_SQL_REGET);

    private static final Pattern SELECT_PATTERN = Pattern.compile("(?i)select\\s+(?<cols>(\\*|[a-zA-Z0-9_,\\s]*?))\\s+from\\s+");

    public static final String SQL_AS_REDEX = "(?i)[0-9a-z_]+\\s+?as\\s+[0-9a-z_]+";

    private static final String TASK_NAME_PREFIX = "run_%s_task_%s";

    private static final String DOWNLOAD_LOG = "/api/rdos/download/batch/batchDownload/downloadJobLog?jobId=%s&taskType=%s&projectId=%s";


    @Override
    public String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, Long taskId, int type, String preJobId) {
        return runSqlByTask(dtuicTenantId, parseResult, tenantId, projectId, userId, database, false, taskId, type, preJobId);
    }

    /**
     * 使用任务的方式运行sql
     */
    @Override
    public String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, boolean isCreateAs, Long taskId, int type, String preJobId) {
        try {
            Long dtuicUserId = userService.getUser(userId).getDtuicUserId();
            BuildSqlVO buildSqlVO = buidSql(parseResult, tenantId, projectId, userId, database, isCreateAs, taskId, type);
            // 发送sql任务
            sendSqlTask(dtuicTenantId, buildSqlVO.getSql(), SourceType.TEMP_QUERY, buildSqlVO.getTaskParam(), preJobId, taskId, type, dtuicUserId, projectId);

            // 记录job
            batchSelectSqlService.addSelectSql(preJobId, buildSqlVO.getTempTable(), buildSqlVO.getIsSelectSql(), tenantId, projectId,
                    parseResult.getOriginSql(), userId, buildSqlVO.getParsedColumns(), MultiEngineType.HADOOP.getType());
            return preJobId;
        } catch (Exception e) {
            LOGGER.error("{}", e);
            if (e instanceof DtCenterDefException && ((DtCenterDefException) e).getErrorMsg().contains("network")) {
                throw new DtCenterDefException("Engine未启动，请检查服务健康情况");
            }
            throw new RdosDefineException("任务执行sql失败");
        }
    }

    /**
     * 获取查询sql对应的id和拼接参数之后的sql
     * @param dtuicTenantId
     * @param parseResult
     * @param tenantId
     * @param projectId
     * @param userId
     * @param database
     * @param isCreateAs
     * @param taskId
     * @param type
     * @param preJobId
     * @return
     */
    public BuildSqlVO  getSqlIdAndSql(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, boolean isCreateAs, Long taskId, int type, String preJobId){
        BuildSqlVO buildSqlVO = buidSql(parseResult, tenantId, projectId, userId, database, isCreateAs, taskId, type);
        String jobId = UUID.randomUUID().toString();
        // 记录job
        batchSelectSqlService.addSelectSql(jobId, buildSqlVO.getTempTable(), buildSqlVO.getIsSelectSql(), tenantId, projectId,
                buildSqlVO.getOriginSql(), userId, buildSqlVO.getParsedColumns(), MultiEngineType.HADOOP.getType());
        return buildSqlVO.setJobId(jobId);
    }

    /**
     * 构建 SQL，需要处理函数和 use db 操作
     *
     * @param originSql
     * @param projectId
     * @param database
     * @param addCustomFunction
     * @return
     */
    public String buildCustomFunctionAndDbSql(String originSql, Long projectId, String database, Boolean addCustomFunction) {
        if (BooleanUtils.isTrue(addCustomFunction)) {
            String functionSql = batchFunctionService.buildContainFunction(originSql, projectId);
            if (StringUtils.isNotBlank(functionSql)) {
                return String.format(USER_DB_TEMP_FUNCTION, database, functionSql, originSql);
            }
        }
        return String.format(USE_DB, database, originSql);
    }

    /**
     * sql类型为select的语句判断是否有自定义函数
     *
     * @param originSql
     * @param projectId
     * @param database
     * @param tempTable
     * @return
     */
    public String buildSelectSqlCustomFunction(String originSql, Long projectId, String database, String tempTable) {
        // 判断是否是自定义函数
        String createFunction = batchFunctionService.buildContainFunction(originSql, projectId);
        if (StringUtils.isNotBlank(createFunction)) {
            return String.format(CREATE_FUNCTION_TEMP_TABLE, database, createFunction, tempTable, originSql);
        }
        return String.format(CREATE_TEMP_TABLE, database, tempTable, originSql);

    }



    /**
     * 解析sql
     * @param parseResult
     * @param tenantId
     * @param projectId
     * @param userId
     * @param database
     * @param isCreateAs
     * @param taskId
     * @param type
     * @return
     */
    public BuildSqlVO buidSql(ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, boolean isCreateAs, Long taskId, int type) {
        String originSql = parseResult.getStandardSql();
        // 生成临时表名
        String tempTable = TEMP_TABLE_PREFIX + System.nanoTime();

        if (StringUtils.isEmpty(originSql)) {
            return null;
        }

        String parsedColumns = "";
        int isSelectSql;
        Matcher witchMatcher = WITH_SQL_PATTERN.matcher(originSql);
        String sql = null;
        if (SqlType.CREATE.equals(parseResult.getSqlType())) {
            isSelectSql = TempJobType.CREATE.getType();
            sql = buildCustomFunctionAndDbSql(originSql, projectId, database, true);
        } else if (isCreateAs) {
            isSelectSql = TempJobType.CREATE_AS.getType();
            sql = buildCustomFunctionAndDbSql(originSql, projectId, database, true);
        } else if (SqlType.INSERT.equals(parseResult.getSqlType()) || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())) {
            isSelectSql = TempJobType.INSERT.getType();
            sql = buildCustomFunctionAndDbSql(originSql, projectId, database, true);
        } else if (witchMatcher.find()) {
            TempJobType jobType = getTempJobType(witchMatcher.group("option"));
            isSelectSql = jobType.getType();
            sql = formatSql(jobType, database, tempTable, originSql);
        } else {
            isSelectSql = TempJobType.SELECT.getType();
            sql = buildSelectSqlCustomFunction(originSql, projectId, database, tempTable);
        }

        String taskParam = "";
        if (type == TableRelationType.TASK.getType()) {
            //仅任务需要环境参数
            BatchTask batchTask = batchTaskService.getBatchTaskById(taskId);
            taskParam = batchTask.getTaskParams();
        }
        return new BuildSqlVO().
                setSql(sql)
                .setTaskParam(taskParam)
                .setIsSelectSql(isSelectSql)
                .setOriginSql(originSql)
                .setParsedColumns(parsedColumns)
                .setProjectId(projectId)
                .setTempTable(tempTable)
                .setTenantId(tenantId)
                .setUserId(userId);
    }

    private TempJobType getTempJobType(String option) {
        if ("insert".equalsIgnoreCase(option)) {
            return TempJobType.INSERT;
        }
        return TempJobType.SELECT;
    }

	private String formatSql(TempJobType jobType, String database, String tempTable, String originSql) {
		if (TempJobType.INSERT.equals(jobType) && !originSql.contains(String.format(USE_DB, database,StringUtils.EMPTY))) {
			return String.format("use %s;%s", database, originSql);
		}
		return String.format(CREATE_TEMP_WITH_TABLE, database, tempTable, originSql);
	}

    /**
     * 从临时表查询数据
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    @Override
    public ExecuteResultVO selectData(BatchTask batchTask, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        // 是否需要脱敏，非admin用户并且是sparkSql needMask才会为true ps:hiveSql不支持脱敏
        boolean needMask = !roleUserService.isAdmin(userId, selectSql.getProjectId(), isRoot) && !taskType.equals(EJobType.HIVE_SQL.getVal());
        if (selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            Project project = projectService.getProjectById(selectSql.getProjectId());
            result.setResult(queryData(dtuicTenantId, selectSql.getSqlText(), project.getId(), needMask, taskType));
            result.setSqlText(selectSql.getSqlText());
        } else {
            ActionJobEntityVO engineEntity = null;
            //高级运行的临时表记录和发送到engine的jobId不是同一条记录
            if (StringUtils.isNotEmpty(selectSql.getFatherJobId())){
                engineEntity = getTaskStatus(selectSql.getFatherJobId());
            }else {
                engineEntity = getTaskStatus(jobId);
            }
            if (engineEntity == null) {
                return result;
            }
            Integer status = TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
            result.setStatus(status);

            if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
                buildHiveSqlData(result, status, jobId, taskType, engineEntity, selectSql, projectId, dtuicTenantId, batchTask, tenantId, userId);
            } else {
                if (buildDataWithCheckTaskStatus(batchTask, selectSql, tenantId, projectId, dtuicTenantId, userId, result,
                        StringUtils.isNotEmpty(selectSql.getFatherJobId()) ? selectSql.getFatherJobId() : jobId, needMask, engineEntity, status)) {
                    return result;
                }
            }
            // update time
            batchSelectSqlService.updateGmtModify(jobId, tenantId, projectId);
        }
        return result;
    }

    /**
     * 获取sql运行日志
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectRunLog(BatchTask batchTask, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        // 是否需要脱敏，非admin用户并且是sparkSql needMask才会为true ps:hiveSql不支持脱敏
        boolean needMask = !roleUserService.isAdmin(userId, selectSql.getProjectId(), isRoot) && !taskType.equals(EJobType.HIVE_SQL.getVal());
        if (TempJobType.SIMPLE_SELECT.getType().equals(selectSql.getIsSelectSql())) {
            return result;
        }

        ActionJobEntityVO engineEntity = null;
        //高级运行的临时表记录和发送到engine的jobId不是同一条记录
        if (StringUtils.isNotEmpty(selectSql.getFatherJobId())) {
            engineEntity = getTaskStatus(selectSql.getFatherJobId());
        } else {
            engineEntity = getTaskStatus(jobId);
        }
        if (engineEntity == null) {
            return result;
        }
        Integer status = TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
        result.setStatus(status);

        if (EJobType.HIVE_SQL.getVal().equals(taskType)) {
            buildHiveSqlRunLog(result, status, jobId, taskType, engineEntity, dtuicTenantId);
        } else {
            if (buildLogsWithCheckTaskStatus(batchTask, selectSql, tenantId, projectId, dtuicTenantId, userId, result,
                    StringUtils.isNotEmpty(selectSql.getFatherJobId()) ? selectSql.getFatherJobId() : jobId, needMask, engineEntity, status)) {
                return result;
            }
        }
        // update time
        batchSelectSqlService.updateGmtModify(jobId, tenantId, projectId);
        return result;
    }

    /**
     * 获取sql 执行结果
     * @param task
     * @param selectSql
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param taskType
     * @return
     */
    @Override
    public ExecuteResultVO selectStatus(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO executeResultVO = new ExecuteResultVO(selectSql.getJobId());
        executeResultVO.setStatus(getExecuteSqlStatus(selectSql));
        return executeResultVO;
    }

    /**
     * 获取sql 执行结果
     *
     * @param selectSql
     * @return
     */
    public Integer getExecuteSqlStatus(BatchHiveSelectSql selectSql){
        Integer status = TaskStatus.UNSUBMIT.getStatus();
        if (selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            return TaskStatus.FINISHED.getStatus();
        }
        ActionJobEntityVO engineEntity = null;
        //高级运行的临时表记录和发送到engine的jobId不是同一条记录
        if (StringUtils.isNotEmpty(selectSql.getFatherJobId())) {
            engineEntity = getTaskStatus(selectSql.getFatherJobId());
        } else {
            engineEntity = getTaskStatus(selectSql.getJobId());
        }
        if (engineEntity == null) {
            return status;
        }
        return TaskStatusConstrant.getShowStatus(engineEntity.getStatus());
    }

    /**
     * 组装HiveSql 运行日志
     *
     * @param result
     * @param status
     * @param jobId
     * @param taskType
     * @param engineEntity
     * @param dtuicTenantId
     * @throws Exception
     */
    private void buildHiveSqlRunLog(ExecuteResultVO result, Integer status, String jobId, Integer taskType, ActionJobEntityVO engineEntity, Long dtuicTenantId) throws Exception {
        // HIVE SQL 没有日志统一处理 hive 逻辑，不管成功或者失败都走表查询
        if ((TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.FAILED.getStatus().equals(status))
                && EJobType.HIVE_SQL.getVal().equals(taskType)) {
            ScheduleJob batchEngineJob = scheduleJobService.getByJobId(jobId, Deleted.NORMAL.getStatus()).getData();
            buildLog(engineEntity.getLogInfo(), batchEngineJob != null && StringUtils.isNotBlank(batchEngineJob.getEngineLog()) ?
                    batchEngineJob.getEngineLog() : null, dtuicTenantId, jobId, false, result);
            result.setDownload(null);
        }
    }

    /**
     * 组装HiveSql 执行结果
     *
     * @param result
     * @param status
     * @param taskType
     * @param selectSql
     * @param projectId
     * @param dtuicTenantId
     * @throws Exception
     */
    private void buildHiveSqlData(ExecuteResultVO result, Integer status, String jobId, Integer taskType, ActionJobEntityVO engineEntity,
                                  BatchHiveSelectSql selectSql, Long projectId, Long dtuicTenantId, BatchTask batchTask,
                                  Long tenantId, Long userId) throws Exception {
        // HIVE SQL 没有日志统一处理 hive 逻辑，不管成功或者失败都走表查询
        if ((TaskStatus.FINISHED.getStatus().equals(status) || TaskStatus.FAILED.getStatus().equals(status)) && EJobType.HIVE_SQL.getVal().equals(taskType)) {
            if (selectSql.getIsSelectSql() == TempJobType.SELECT.getType()
                    || selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
                ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, null);
                Preconditions.checkNotNull(projectEngine, String.format("project %d not support engine.", projectId));
                List<Object> data = hadoopDataDownloadService.queryDataFromHiveServerTempTable(dtuicTenantId, selectSql.getTempTableName(),
                        projectEngine.getEngineIdentity(), projectId);
                result.setResult(data);
            }
        }
    }

    /**
     * 组装运行日志
     *
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param result
     * @param jobId
     * @param needMask
     * @param engineEntity
     * @param status
     * @return
     * @throws Exception
     */
    private boolean buildLogsWithCheckTaskStatus(BatchTask batchTask, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, ExecuteResultVO result, String jobId, boolean needMask, ActionJobEntityVO engineEntity, Integer status) throws Exception {
        if (TaskStatus.FINISHED.getStatus().equals(status)) {
            List<TempJobType> values = Arrays.asList(TempJobType.values());
            List<Integer> types = values.stream().map(TempJobType::getType).collect(Collectors.toList());
            if (types.contains(selectSql.getIsSelectSql()) && StringUtils.isEmpty(selectSql.getFatherJobId())) {
                buildLog(engineEntity.getLogInfo(), engineEntity.getEngineLog(), dtuicTenantId, jobId, true, result);
                result.setDownload(String.format(DOWNLOAD_LOG, jobId, EJobType.SPARK_SQL.getVal(),projectId));
            }
            if (TempJobType.INSERT.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.CREATE_AS.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.PYTHON_SHELL.getType().equals(selectSql.getIsSelectSql())) {
                return true;
            }
        } else if (TaskStatus.FAILED.getStatus().equals(status)) {
            buildLog(engineEntity.getLogInfo(), engineEntity.getEngineLog(), dtuicTenantId, jobId, true, result);
            result.setDownload(String.format(DOWNLOAD_LOG, jobId, EJobType.SPARK_SQL.getVal(),projectId));
        }
        return false;
    }

    /**
     * 组装sql执行结果
     *
     * @param batchTask
     * @param selectSql
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param result
     * @param jobId
     * @param needMask
     * @param engineEntity
     * @param status
     * @return
     * @throws Exception
     */
    private boolean buildDataWithCheckTaskStatus(BatchTask batchTask, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, ExecuteResultVO result, String jobId, boolean needMask, ActionJobEntityVO engineEntity, Integer status) throws Exception {
        if (TaskStatus.FINISHED.getStatus().equals(status)) {
            if (TempJobType.INSERT.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.CREATE_AS.getType().equals(selectSql.getIsSelectSql())
                    || TempJobType.PYTHON_SHELL.getType().equals(selectSql.getIsSelectSql())) {
                if (!TempJobType.PYTHON_SHELL.getType().equals(selectSql.getIsSelectSql())) {
                    result.setSqlText(selectSql.getSqlText());
                }
                return true;
            }
            ProjectEngine projectEngine = projectEngineService.getProjectDb(projectId, MultiEngineType.HADOOP.getType());
            Preconditions.checkNotNull(projectEngine, String.format("project %d not support hadoop engine.", projectId));
            List<Object> data = hadoopDataDownloadService.queryDataFromTempTable(dtuicTenantId, selectSql.getTempTableName(),
                    projectEngine.getEngineIdentity(), selectSql, needMask, projectId);
            result.setSqlText(selectSql.getSqlText());
            result.setResult(data);
        }
        return false;
    }


    /**
     * 获取简单查询的查询结果字段
     *
     * @param sql
     * @return
     */
    public static List<String> getSimpleSqlCols(String sql) {
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            return Collections.emptyList();
        }
        String colsStr = matcher.group("cols");
        return Arrays.asList(DtStringUtil.splitIgnoreQuota(colsStr, ","));
    }

    /**
     * 获取简单查询的字段
     *
     * @param sql
     * @param aliasNames
     * @return
     */
    public static List<String> getSimpleQueryFieldNames(String sql, Boolean aliasNames) {
        List<String> sqlCols = getSimpleSqlCols(sql);
        List<String> fieldNames = new ArrayList<>();
        for (String colStr : sqlCols) {
            colStr = colStr.trim();
            if (colStr.equals("*")) {
                break;
            }
            if (colStr.matches(SQL_AS_REDEX)) {
                String[] colAndAlias = DtStringUtil.splitIgnoreQuotaBrackets(colStr, "(?i)\\s+as\\s+");
                fieldNames.add(BooleanUtils.isTrue(aliasNames) ? colAndAlias[1] : colAndAlias[0]);
            } else {
                fieldNames.add(colStr);
            }
        }
        return fieldNames;
    }

    /**
     * 查询表数据 - 简单查询
     *
     * @param dtUicTenantId uic租户id
     * @param sql 查询sql
     * @param projectId 项目id
     * @param needMask 是否需要脱敏，仅对sparkSql有效
     * @param taskType 任务类型
     * @return 查询数据
     * @throws Exception
     */
    private List<Object> queryData(Long dtUicTenantId, String sql, Long projectId, boolean needMask, Integer taskType) throws Exception {
        List<Object> queryResult = Lists.newArrayList();
        IDownload resultDownload = hadoopDataDownloadService.getSimpleSelectDownLoader(dtUicTenantId, projectId, sql, needMask, taskType);
        Integer num = getMaxQueryNum(sql, dtUicTenantId, taskType);
        int readCounter = 0;
        // 第一行插入传字段信息
        queryResult.add(resultDownload.getMetaInfo());
        while (!resultDownload.reachedEnd()) {
            if (readCounter >= num) {
                break;
            }
            queryResult.add(resultDownload.readNext());
            readCounter++;
        }
        return queryResult;
    }

    /**
     * 从简单查询sql中获取最大条数
     *
     * @param sql 简单查询sql
     * @param dtUicTenantId uic租户id
     * @return 最大条数
     */
    public Integer getMaxQueryNum(String sql, Long dtUicTenantId, Integer taskType) {
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            throw new RdosDefineException("该sql不符合简单查询!");
        }
        String limitStr = matcher.group("num");
        Integer num = null;
        if (StringUtils.isNotEmpty(limitStr)) {
            num = Integer.parseInt(limitStr);
        }
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(dtUicTenantId, null, EJobType.getEJobType(taskType));
        if (num == null || num > jdbcInfo.getMaxRows()) {
            num = jdbcInfo.getMaxRows();
        }
        return num;
    }


    /**
     * 获取任务状态
     *
     * @param jobId
     * @return
     */
    @Override
    public ActionJobEntityVO getTaskStatus(String jobId) {
        List<ActionJobEntityVO> engineEntities = actionService.entitys(Arrays.asList(jobId));
        if (CollectionUtils.isNotEmpty(engineEntities)) {
            return engineEntities.get(0);
        }
        return null;
    }

    public BatchHiveSelectSql getByJobId(String jobId, Long tenantId, Integer isDeleted) {
        BatchHiveSelectSql selectSql = batchHiveSelectSqlDao.getByJobId(jobId, tenantId, isDeleted);
        if (selectSql == null) {
            throw new RdosDefineException("select job not exists");
        }
        return selectSql;
    }

    public void stopSelectJob(Long dtuicTenantId, String jobId, Long tenantId, Long projectId) {
        try {
            actionService.stop(Collections.singletonList(jobId));
            // 这里用逻辑删除，是为了在调度端删除可能生成的临时表
            batchHiveSelectSqlDao.deleteByJobId(jobId, tenantId, projectId);
        } catch (Exception e) {
            LOGGER.error("{}", e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, Long projectId, String sql, Long userId) {
        this.addSelectSql(jobId, tempTable, isSelectSql, tenantId, projectId, sql, userId, "");
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, Long projectId, String sql, Long userId, String parsedColumns) {
        BatchHiveSelectSql hiveSelectSql = new BatchHiveSelectSql();
        hiveSelectSql.setJobId(jobId);
        hiveSelectSql.setTempTableName(tempTable);
        hiveSelectSql.setTenantId(tenantId);
        hiveSelectSql.setProjectId(projectId);
        hiveSelectSql.setIsSelectSql(isSelectSql);
        hiveSelectSql.setSqlText(sql);
        hiveSelectSql.setUserId(userId);
        hiveSelectSql.setParsedColumns(parsedColumns);

        batchHiveSelectSqlDao.insert(hiveSelectSql);
    }

    public String sendSqlTask(Long dtuicTenantId, String sql, SourceType sourceType, String taskParams, String jobId, Long taskId, Integer relationType, Long dtuicUserId, Long projectId) {
        JSONObject taskParam = new JSONObject();

        putEngineType(taskParam, taskId, relationType);
        taskParam.put("sqlText", sql);
        taskParam.put("computeType", ComputeType.BATCH.getType());
        taskParam.put("taskId", jobId);
        taskParam.put("name", String.format(TASK_NAME_PREFIX, "sql", System.currentTimeMillis()));
        taskParam.put("pluginInfo", null);
        taskParam.put("sourceType", sourceType.getType());
        taskParam.put("taskParams", taskParams);
        //dtuicTenantId
        taskParam.put("tenantId", dtuicTenantId);
        taskParam.put("isFailRetry", false);
        taskParam.put("maxRetryNum", 0);
        taskParam.put("userId", dtuicUserId);
        taskParam.put("appType", AppType.RDOS.getType());
        taskParam.put("taskSourceId", taskId);
        taskParam.put("projectId", projectId);
        ParamActionExt actionExt = null;
        try {
            if (taskParam.containsKey("job")){
                taskParam.remove("job").toString();
            }
            actionExt = objectMapper.readValue(taskParam.toJSONString(), ParamActionExt.class);
        } catch (IOException e) {
            LOGGER.error("",e);
            throw new DtCenterDefException("参数异常");
        }
        actionService.start(actionExt);
        return jobId;
    }

    private void putEngineType(JSONObject taskParam, Long taskId, Integer relationType) {
        taskParam.put("engineType", EngineType.Spark.getEngineName());
        taskParam.put("taskType", EJobType.SPARK_SQL.getVal());
        if (taskId == null || !TableRelationType.TASK.getType().equals(relationType)) {
            return;
        }

        BatchTask task = batchTaskService.getBatchTaskById(taskId);
        if (EngineType.HIVE.getVal() == task.getEngineType()) {
            taskParam.put("engineType", EngineType.HIVE.getEngineName());
            taskParam.put("taskType", EJobType.HIVE_SQL.getEngineJobType());
            return;
        }

        if (EngineType.IMPALA.getVal() == task.getEngineType()) {
            taskParam.put("engineType", EngineType.IMPALA.getEngineName());
            taskParam.put("taskType", EJobType.IMPALA_SQL.getEngineJobType());
            return;
        }
    }

    /**
     * 生成日志，优先取hdfs前30000个字节，如果hdfs日志获取失败，则取engine日志
     * @param logInfo
     * @param engineLog
     * @param dtuicTenantId 租户ID
     * @param jobId jobId
     * @param needDownload 是否需要去hdfs下载日志
     * @return 日志
     */
    private void buildLog(String logInfo, String engineLog, Long dtuicTenantId, String jobId, boolean needDownload, ExecuteResultVO result) {
        String log = "";
        if (needDownload) {
            try {
                log = batchDownloadService.loadJobLog(dtuicTenantId, EJobType.SPARK_SQL.getVal(), jobId, 30000);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        result.setRetryLog(false);
        if (StringUtils.isEmpty(log)) {
            if (needDownload) {
                result.setRetryLog(true);
            }
            log = getEngineLog(logInfo, engineLog);
        }
        result.setMsg(log);
    }

    /**
     * 获取engine日志
     * @param logInfo logInfo
     * @param engineLog engineLog
     * @return engine日志
     */
    private String getEngineLog(String logInfo, String engineLog) {
        StringBuilder logBuild = new StringBuilder();
        if (StringUtils.isNotBlank(logInfo)) {
            JSONObject baseLogJSON = JSONObject.parseObject(logInfo);
            logBuild.append("====================基本日志====================").append("\n");
            logBuild.append(baseLogJSON.getString("msg_info")).append("\n");
            if (StringUtils.isNotBlank(engineLog)) {
                try {
                    JSONObject appLogJSON = JSONObject.parseObject(engineLog);
                    JSONArray appLogs = appLogJSON.getJSONArray("appLog");
                    if (appLogs != null) {
                        logBuild.append("====================appLogs====================").append("\n");
                        for (Object log : appLogs) {
                            logBuild.append(((JSONObject) log).getString("value")).append("\n");
                        }
                    }
                } catch (JSONException e) {
                    LOGGER.error("", e);
                    logBuild.append(engineLog).append("\n");
                }
            }
        }
        return logBuild.toString();
    }
}
