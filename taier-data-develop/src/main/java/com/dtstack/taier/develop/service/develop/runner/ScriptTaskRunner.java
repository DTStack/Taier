package com.dtstack.taier.develop.service.develop.runner;

import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.taier.common.enums.EScheduleJobType;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.dao.domain.DevelopSelectSql;
import com.dtstack.taier.dao.domain.Task;
import com.dtstack.taier.develop.dto.devlop.BuildSqlVO;
import com.dtstack.taier.develop.dto.devlop.ExecuteResultVO;
import com.dtstack.taier.develop.service.develop.ITaskRunner;
import com.dtstack.taier.develop.service.develop.impl.DevelopScriptService;
import com.dtstack.taier.develop.service.develop.impl.JobParamReplace;
import com.dtstack.taier.develop.service.task.TaskTemplateService;
import com.dtstack.taier.develop.sql.ParseResult;
import com.dtstack.taier.develop.utils.develop.common.IDownload;
import com.google.common.collect.ImmutableList;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-14 16:28
 */
@Component
public class ScriptTaskRunner implements ITaskRunner {

    @Autowired
    private DevelopScriptService developScriptService;

    @Autowired
    private JobParamReplace jobParamReplace;

    @Override
    public List<EScheduleJobType> support() {
        return ImmutableList.of(EScheduleJobType.PYTHON, EScheduleJobType.SHELL);
    }

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String sql, Task task, List<Map<String, Object>> taskVariables) throws Exception {
        task.setTaskParams(TaskTemplateService.formatEnvTaskParams(task.getTaskParams()));
        sql = jobParamReplace.paramReplace(sql, taskVariables, DateTime.now().toString("yyyyMMddHHmmss"));
        return developScriptService.runScriptWithTask(userId, tenantId, sql, task);
    }

    @Override
    public ExecuteResultVO selectData(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        throw new RdosDefineException("not support");
    }

    @Override
    public ExecuteResultVO selectStatus(Task task, DevelopSelectSql selectSql, Long tenantId, Long userId, Boolean isRoot, Integer taskType) {
        throw new RdosDefineException("not support");
    }

    @Override
    public ExecuteResultVO runLog(String jobId, Integer taskType, Long tenantId, Integer limitNum) {
        throw new RdosDefineException("not support");
    }

    @Override
    public IDownload logDownLoad(Long tenantId, String jobId, Integer limitNum) {
        throw new RdosDefineException("not support");
    }

    @Override
    public List<String> getAllSchema(Long tenantId, Integer taskType) {
        throw new RdosDefineException("not support");
    }

    @Override
    public ISourceDTO getSourceDTO(Long tenantId, Long userId, Integer taskType, boolean useSchema) {
        throw new RdosDefineException("not support");
    }

    @Override
    public String getCurrentDb(Long tenantId, Integer taskType) {
        throw new RdosDefineException("not support");
    }

    @Override
    public BuildSqlVO buildSql(ParseResult parseResult, Long userId, String database, Task task) {
        throw new RdosDefineException("not support");
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(Task task, Long tenantId, Boolean isRoot) {
        throw new RdosDefineException("not support");
    }
}