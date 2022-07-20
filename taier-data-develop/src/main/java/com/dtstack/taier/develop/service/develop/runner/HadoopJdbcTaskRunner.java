package com.dtstack.taier.develop.service.develop.runner;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.constant.CommonConstant;
import com.dtstack.taier.common.enums.EComponentType;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.util.SqlFormatUtil;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobExpand;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.impl.DevelopFunctionService;
import com.dtstack.taier.develop.service.develop.impl.DevelopSelectSqlService;
import com.dtstack.taier.develop.service.develop.impl.DevelopSqlExeService;
import com.dtstack.taier.develop.service.develop.impl.DevelopTaskParamService;
import com.dtstack.taier.develop.service.develop.impl.HiveSelectDownload;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlParserImpl;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.sql.parse.SqlParserFactory;
import com.dtstack.taier.develop.sql.utils.SqlRegexUtil;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.develop.utils.develop.hive.service.LogPluginDownload;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.util.RetryUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * @author yuebai
 * @date 2022/7/13
 */
public abstract class HadoopJdbcTaskRunner extends JdbcTaskRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(HadoopJdbcTaskRunner.class);

    public static final Pattern CACHE_LAZY_SQL_PATTEN = Pattern.compile("(?i)cache\\s+(lazy\\s+)?table.*");

    private SqlParserFactory parserFactory = SqlParserFactory.getInstance();

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private DevelopSelectSqlService developSelectSqlService;

    @Autowired
    protected DevelopTaskParamService developTaskParamService;

    @Autowired
    protected DevelopSqlExeService developSqlExeService;

    private static final String USE_DB = "use %s; %s ;";

    private static final String USER_DB_TEMP_FUNCTION = "use %s; %s %s ;";

    public static final String TEMP_TABLE_PREFIX = "select_sql_temp_table_";

    private static final String CREATE_FUNCTION_TEMP_TABLE = "use %s;%s create table %s stored as orc as select * from (%s)temp";

    private static final String CREATE_TEMP_TABLE = "use %s; create table %s stored as orc as select * from (%s)temp";

    @Override
    public abstract List<EScheduleJobType> support();

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, Long taskId, String sql, Task task, String jobId) throws Exception {
        ExecuteContent content = new ExecuteContent();
        content.setTenantId(tenantId)
                .setUserId(userId)
                .setSql(sql)
                .setTaskId(taskId)
                .setTaskType(task.getTaskType())
                .setJobId(jobId);
        // 前置操作
        prepareExecuteContent(content);
        if (divertTask(content)) {
            //直连jdbc
            return super.startSqlImmediately(userId, tenantId, taskId, sql, task, jobId);
        } else {
            //异步执行
            return startRunInScheduler(userId, tenantId, taskId, jobId, content);
        }
    }

    public ExecuteResultVO startRunInScheduler(Long userId, Long tenantId, Long taskId, String jobId, ExecuteContent content) {
        ParseResult parseResult = content.getParseResult();
        String database = content.getDatabase();
        developSelectSqlService.runSqlByTask(tenantId, parseResult, userId, database, taskId, content.getTaskType(), jobId);
        ExecuteResultVO<List<Object>> result = new ExecuteResultVO<>();
        result.setJobId(jobId);
        result.setContinue(true);
        result.setJobId(jobId);
        return result;
    }


    protected boolean divertTask(ExecuteContent executeContent) {
        ParseResult parseResult = executeContent.getParseResult();
        Long tenantId = executeContent.getTenantId();
        // 校验是否含有自定义函数
        boolean useSelfFunction = developFunctionService.validContainSelfFunction(executeContent.getSql(), tenantId, null, executeContent.getTaskType());

        if (Objects.nonNull(parseResult) && Objects.nonNull(parseResult.getStandardSql())
                && SqlRegexUtil.isSimpleQuery(parseResult.getStandardSql()) && !useSelfFunction) {
            return true;
        }

        if (SqlType.INSERT.equals(parseResult.getSqlType())
                || SqlType.INSERT_OVERWRITE.equals(parseResult.getSqlType())
                || SqlType.QUERY.equals(parseResult.getSqlType())
                || SqlType.CREATE_AS.equals(parseResult.getSqlType())
                || useSelfFunction) {
            return false;
        } else {
            return true;
        }
    }

    private void prepareExecuteContent(final ExecuteContent executeContent) {
        executeContent.setDatabase(getCurrentDb(executeContent.getTenantId(), executeContent.getTaskType()));

        String sql = executeContent.getSql();

        //set sql / cache lazy table 暂时不解析血缘
        if (StringUtils.isNotBlank(sql)
                && (sql.toLowerCase().trim().startsWith("set") || CACHE_LAZY_SQL_PATTEN.matcher(sql).matches())) {
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
            executeContent.getSqlList().forEach(x -> {
                if (!x.trim().startsWith("set")) {
                    executeContent.setSql(x);
                    ParseResult batchParseResult = this.parseSql(executeContent);
                    parseResultList.add(batchParseResult);
                } else {
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
        SqlParserImpl sqlParser = parserFactory.getSqlParser(ETableType.HIVE);
        ParseResult parseResult = null;
        try {
            parseResult = sqlParser.parseSql(executeContent.getSql(), executeContent.getDatabase(), new HashMap<>());
        } catch (final Exception e) {
            LOGGER.error("解析sql异常，sql：{}", executeContent.getSql(), e);
            parseResult = new ParseResult();
            parseResult.setFailedMsg(ExceptionUtils.getStackTrace(e));
            parseResult.setStandardSql(SqlFormatUtil.getStandardSql(executeContent.getSql()));
        }
        return parseResult;
    }

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long tenantId, Task task, List<DevelopTaskParamShade> taskParamsToReplace) throws Exception {
        String sql = task.getSqlText() == null ? "" : task.getSqlText();
        String taskParams = task.getTaskParams();
        developTaskParamService.checkParams(sql, taskParamsToReplace);
        // 构建运行的SQL
        sql = developSqlExeService.processSqlText(tenantId, task.getTaskType(), sql);
        actionParam.put("sqlText", sql);
        actionParam.put("taskParams", taskParams);
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        if (selectSql.getIsSelectSql() == TempJobType.SELECT.getType()) {
            result.setResult(queryData(tenantId, selectSql.getTempTableName(), taskType));
            result.setSqlText(selectSql.getSqlText());
        } else {
            ScheduleJob scheduleJob = jobService.getScheduleJob(selectSql.getJobId());
            Integer status = TaskStatus.getShowStatus(scheduleJob.getStatus());
            result.setStatus(status);
        }
        return result;
    }

    private List<Object> queryData(Long tenantId, String tableName, Integer taskType) throws Exception {
        List<Object> queryResult = Lists.newArrayList();
        IDownload resultDownload = new HiveSelectDownload(getSourceDTO(tenantId, null, taskType), tableName);
        Integer num = environmentContext.getSelectLimit();
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

    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        ExecuteResultVO resultVO = new ExecuteResultVO();
        StringBuilder log = new StringBuilder();
        IDownload download = logDownLoad(tenantId, jobId, Objects.isNull(limitNum) ? environmentContext.getLogsLimitNum() : limitNum);
        if (Objects.nonNull(download)) {
            LOGGER.error("-----日志文件导出失败-----");
            while (!download.reachedEnd()) {
                Object row = download.readNext();
                log.append(row);
            }
        } else {
            log.append(scheduleRunLog(jobId));
        }
        resultVO.setDownload(String.format(CommonConstant.DOWNLOAD_LOG, jobId, taskType, tenantId));
        resultVO.setMsg(log.toString());
        return resultVO;
    }

    @Override
    public String scheduleRunLog(String jobId) {
        ScheduleJobExpand jobExpand = jobExpandService.selectOneByJobId(jobId);
        String logInfo = jobExpand.getLogInfo();
        String engineLog = jobExpand.getEngineLog();

        StringBuilder logBuild = new StringBuilder();
        if (StringUtils.isNotBlank(logInfo)) {
            JSONObject baseLogJSON = JSONObject.parseObject(logInfo);
            logBuild.append("====================基本日志====================").append("\n");
            logBuild.append(baseLogJSON.getString("msg_info")).append("\n");
            if (StringUtils.isNotBlank(engineLog) && isJSON(engineLog)) {
                try {
                    JSONObject appLogJSON = JSONObject.parseObject(engineLog);
                    JSONArray appLogs = appLogJSON.getJSONArray("appLog");
                    if (appLogs != null) {
                        logBuild.append("====================appLogs====================").append("\n");
                        for (Object log : appLogs) {
                            logBuild.append(((JSONObject) log).getString("value")).append("\n");
                        }
                    } else {
                        logBuild.append(engineLog).append("\n");
                    }
                } catch (JSONException e) {
                    LOGGER.error("", e);
                    logBuild.append(engineLog).append("\n");
                }
            } else if (StringUtils.isNotBlank(engineLog)) {
                logBuild.append(engineLog).append("\n");
            }
        }
        return logBuild.toString();
    }

    private boolean isJSON(String str) {
        try {
            JSON.parse(str);
            return true;
        } catch (Exception ex) {
            LOGGER.error("字符串解析失败");
        }
        return false;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        ScheduleJob scheduleJob = jobService.getScheduleJob(jobId);
        if (StringUtils.isBlank(scheduleJob.getApplicationId())) {
            return null;
        }
        IDownload iDownload = null;
        try {
            iDownload = RetryUtil.executeWithRetry(() -> {
                Map yarnConf = clusterService.getComponentByTenantId(tenantId, EComponentType.YARN.getTypeCode(), false,
                        Map.class, null);
                Map hadoopConf = clusterService.getComponentByTenantId(tenantId, EComponentType.HDFS.getTypeCode(), false,
                        Map.class, null);
                final LogPluginDownload downloader = new LogPluginDownload(scheduleJob.getApplicationId(), yarnConf, hadoopConf,
                        scheduleJob.getSubmitUserName(), limitNum);
                return downloader;
            }, 3, 1000L, false);
        } catch (Exception e) {
            LOGGER.error("downloadJobLog {}  失败:{}", jobId, e);
            return null;
        }
        return iDownload;
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long tenantId, Long userId, String database, Long taskId) {
        Task task = developTaskService.getDevelopTaskById(taskId);
        String originSql = parseResult.getStandardSql();
        String tempTable = TEMP_TABLE_PREFIX + System.nanoTime();
        if (StringUtils.isEmpty(originSql)) {
            return null;
        }
        int isSelectSql;
        String sql = null;
        if (SqlType.QUERY.equals(parseResult.getSqlType())) {
            isSelectSql = TempJobType.SELECT.getType();
            sql = buildSelectSqlCustomFunction(originSql, tenantId, database, tempTable, task.getTaskType());
        } else {
            isSelectSql = TempJobType.OTHER.getType();
            sql = buildCustomFunctionAndDbSql(originSql, tenantId, database, true, task.getTaskType());
        }
        BuildSqlVO buildSqlVO = new BuildSqlVO();
        buildSqlVO.setSql(sql);
        buildSqlVO.setTaskParam(task.getTaskParams());
        buildSqlVO.setIsSelectSql(isSelectSql);
        buildSqlVO.setOriginSql(originSql);
        buildSqlVO.setTenantId(tenantId);
        buildSqlVO.setTempTable(tempTable);
        buildSqlVO.setUserId(userId);
        return buildSqlVO;
    }

    /**
     * 构建sql
     *
     * @param originSql
     * @param tenantId
     * @param database
     * @param addCustomFunction
     * @param taskType
     * @return
     */
    private String buildCustomFunctionAndDbSql(String originSql, Long tenantId, String database, Boolean addCustomFunction, Integer taskType) {
        if (BooleanUtils.isTrue(addCustomFunction)) {
            String functionSql = developFunctionService.buildContainFunction(originSql, tenantId, taskType);
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
     * @param tenantId
     * @param database
     * @param tempTable
     * @return
     */
    private String buildSelectSqlCustomFunction(String originSql, Long tenantId, String database, String tempTable, Integer taskType) {
        // 判断是否是自定义函数
        String createFunction = developFunctionService.buildContainFunction(originSql, tenantId, taskType);
        if (StringUtils.isNotBlank(createFunction)) {
            return String.format(CREATE_FUNCTION_TEMP_TABLE, database, createFunction, tempTable, originSql);
        }
        return String.format(CREATE_TEMP_TABLE, database, tempTable, originSql);
    }

    @Override
    public abstract ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType);


    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        return null;
    }
}
