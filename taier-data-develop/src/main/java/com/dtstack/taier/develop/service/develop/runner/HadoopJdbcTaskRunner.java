package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.enums.ETableType;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.DevelopTaskParamShade;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.bo.ExecuteContent;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.impl.DevelopFunctionService;
import com.dtstack.taier.develop.service.develop.impl.DevelopHadoopSelectSqlService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.sql.SqlParserImpl;
import com.dtstack.taier.develop.sql.SqlType;
import com.dtstack.taier.develop.sql.parse.SqlParserFactory;
import com.dtstack.taier.develop.sql.utils.SqlRegexUtil;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.dtstack.taier.develop.utils.develop.common.util.SqlFormatUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
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
    private DevelopHadoopSelectSqlService developHadoopSelectSqlService;

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
        return null;
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        return null;
    }

    @Override
    public ExecuteResultVO runLogShow(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        return null;
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum, String logType) {
        return null;
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        return null;
    }

    @Override
    public abstract ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType);


}
