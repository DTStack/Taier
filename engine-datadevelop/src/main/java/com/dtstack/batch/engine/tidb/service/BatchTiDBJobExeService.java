package com.dtstack.batch.engine.tidb.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.service.task.impl.BatchTaskParamService;
import com.dtstack.batch.service.task.impl.BatchTaskParamShadeService;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2020-02-20
 */
@Service
public class BatchTiDBJobExeService implements IBatchJobExeService {

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private BatchTaskParamShadeService batchTaskParamShadeService;

    @Autowired
    private BatchTaskParamService batchTaskParamService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String uniqueKey, long projectId, long taskId, String sql, Boolean isRoot, Long dtuicTenantId, BatchTask task, String dtToken, Boolean isEnd, String jobId) throws Exception {

        ExecuteContent content = new ExecuteContent();
        content.setTenantId(tenantId).setProjectId(projectId).setUserId(userId)
                .setSql(sql).setRelationId(taskId).setRelationType(TableRelationType.TASK.getType())
                .setDetailType(task.getTaskType()).setRootUser(isRoot).setCheckSyntax(true)
                .setEngineType(MultiEngineType.TIDB.getType()).setTableType(ETableType.TIDB.getType())
                .setSessionKey(uniqueKey)
                .setDtToken(dtToken)
                .setEnd(isEnd)
                .setDtuicTenantId(dtuicTenantId);
        return batchSqlExeService.executeSql(content);
    }

    @Override
    public ExecuteSqlParseVO startSqlSophisticated(Long userId, Long tenantId, String uniqueKey, long projectId, long taskId, List<String> sqlList, Boolean isRoot, Long dtuicTenantId, BatchTask task, String dtToken, String database) {
        return null;
    }

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long dtuicTenantId, Project project, BatchTask batchTask, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        batchTaskParamService.checkParams(batchTask.getSqlText(), taskParamsToReplace);
        String sql = batchTask.getSqlText();

        CheckSyntaxResult result = batchSqlExeService.processSqlText(dtuicTenantId, batchTask.getTaskType(),sql, batchTask.getCreateUserId(), project.getTenantId(),
                project.getId(), false, false, MultiEngineType.TIDB.getType(), batchTask.getTaskParams());
        sql = result.getSql();
        String taskParams = batchTask.getTaskParams();

        actionParam.put("sqlText", sql);
        actionParam.put("engineType", EngineType.TIDB.getEngineName());
        actionParam.put("taskParams", taskParams);
        actionParam.put("jdbcUrlParams", getJdbcUrlParams(project));
    }


    private JSONObject getJdbcUrlParams(Project project){
        ProjectEngine projectEngine = projectEngineService.getProjectDb(project.getId(), MultiEngineType.TIDB.getType());
        if(projectEngine == null){
            throw new RdosDefineException("当前项目没有配置TiDB引擎");
        }
        JSONObject params = new JSONObject();
        params.put("currentSchema", projectEngine.getEngineIdentity());
        return params;
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(BatchTask batchTask, Long dtuicTenantId, Boolean isRoot) {
        return Collections.EMPTY_MAP;
    }
}
