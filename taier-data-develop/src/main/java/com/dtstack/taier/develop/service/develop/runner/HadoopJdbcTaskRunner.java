package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.engine.JdbcInfo;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.common.enums.TempJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.impl.DevelopFunctionService;
import com.dtstack.taier.develop.service.develop.impl.DevelopHadoopSelectSqlService;
import com.dtstack.taier.develop.service.develop.impl.HadoopDataDownloadService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlParserImpl;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.sql.parse.SqlParserFactory;
import com.dtstack.taier.develop.sql.utils.SqlRegexUtil;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.dtstack.taier.develop.utils.develop.service.impl.Engine2DTOService;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.vo.action.ActionJobEntityVO;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author yuebai
 * @date 2022/7/13
 */
public abstract class HadoopJdbcTaskRunner extends JdbcTaskRunner {

    public static final Logger LOGGER = LoggerFactory.getLogger(HadoopJdbcTaskRunner.class);

    public static final Pattern CACHE_LAZY_SQL_PATTEN = Pattern.compile("(?i)cache\\s+(lazy\\s+)?table.*");

    private static final String SIMPLE_QUERY_REGEX = "(?i)select\\s+(?<cols>((\\*|[a-zA-Z0-9_,\\s]*)\\s+|\\*))from\\s+(((?<db>[0-9a-z_]+)\\.)*(?<name>[0-9a-z_]+))(\\s+limit\\s+(?<num>\\d+))*\\s*";

    public static final Pattern SIMPLE_QUERY_PATTERN = Pattern.compile(SIMPLE_QUERY_REGEX);

    private SqlParserFactory parserFactory = SqlParserFactory.getInstance();

    @Autowired
    private DevelopFunctionService developFunctionService;

    @Autowired
    private DevelopHadoopSelectSqlService developHadoopSelectSqlService;

    @Autowired
    private HadoopDataDownloadService hadoopDataDownloadService;

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
        developHadoopSelectSqlService.runSqlByTask(tenantId, parseResult, userId, database, taskId, content.getTaskType(), jobId);
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

    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        String jobId = selectSql.getJobId();
        ExecuteResultVO result = new ExecuteResultVO(jobId);
        if (selectSql.getIsSelectSql() == TempJobType.SIMPLE_SELECT.getType()) {
            result.setResult(queryData(tenantId, selectSql.getSqlText(), taskType));
            result.setSqlText(selectSql.getSqlText());
        } else {
            List<ActionJobEntityVO> entitys = actionService.entitys(Collections.singletonList(selectSql.getJobId()));
            if (entitys == null) {
                return result;
            }
            Integer status = TaskStatus.getShowStatus(entitys.get(0).getStatus());
            result.setStatus(status);
        }
        return result;
    }

    private List<Object> queryData(Long tenantId, String sql, Integer taskType) throws Exception {
        List<Object> queryResult = Lists.newArrayList();
        IDownload resultDownload = hadoopDataDownloadService.getSimpleSelectDownLoader(tenantId, sql, taskType);
        Integer num = getMaxQueryNum(sql, tenantId, taskType);
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
     * @param sql      简单查询sql
     * @param tenantId 租户id
     * @return 最大条数
     */
    public Integer getMaxQueryNum(String sql, Long tenantId, Integer taskType) {
        Matcher matcher = SIMPLE_QUERY_PATTERN.matcher(sql);
        if (!matcher.find()) {
            throw new RdosDefineException("该sql不符合简单查询!");
        }
        String limitStr = matcher.group("num");
        Integer num = null;
        if (StringUtils.isNotEmpty(limitStr)) {
            num = Integer.parseInt(limitStr);
        }
        JdbcInfo jdbcInfo = Engine2DTOService.getJdbcInfo(tenantId, null, EScheduleJobType.getByTaskType(taskType));
        if (Objects.isNull(num) || num > jdbcInfo.getMaxRows()) {
            num = jdbcInfo.getMaxRows();
        }
        return num;
    }

    @Override
    public abstract ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType);


}
