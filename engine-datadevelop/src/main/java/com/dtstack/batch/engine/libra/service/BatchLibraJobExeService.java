package com.dtstack.batch.engine.libra.service;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.bo.ExecuteContent;
import com.dtstack.batch.common.enums.ETableType;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.domain.BatchTaskParamShade;
import com.dtstack.batch.domain.Project;
import com.dtstack.batch.domain.ProjectEngine;
import com.dtstack.batch.engine.core.service.BatchSqlRemoteExeService;
import com.dtstack.batch.enums.TableRelationType;
import com.dtstack.batch.service.impl.BatchSqlExeService;
import com.dtstack.batch.service.impl.ProjectEngineService;
import com.dtstack.batch.service.job.IBatchJobExeService;
import com.dtstack.batch.vo.CheckSyntaxResult;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSqlParseVO;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EngineType;
import com.dtstack.dtcenter.common.enums.MultiEngineType;
import com.dtstack.dtcenter.common.util.PublicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author yuebai
 * @date 2019-06-11
 */
@Service
public class BatchLibraJobExeService implements IBatchJobExeService {

    @Autowired
    private BatchSqlExeService batchSqlExeService;

    @Autowired
    private ProjectEngineService projectEngineService;

    @Autowired
    private BatchSqlRemoteExeService batchSqlRemoteExeService;

    @Override
    public ExecuteResultVO startSqlImmediately(Long userId, Long tenantId, String uniqueKey, long projectId, long taskId, String sql, Boolean isRoot, Long dtuicTenantId, BatchTask task, String dtToken, Boolean isEnd, String jobId) throws Exception {
        if (null == task) {
            throw new RdosDefineException("任务不能为空");
        }
        if (!EJobType.LIBRA_SQL.getVal().equals(task.getTaskType())) {
            throw new RdosDefineException(String.format("不支持%s类型的任务直接运行", EJobType.getEJobType(task.getTaskType()).getName()));
        }

        //固定节点执行LibraSql
        String remoteNode = batchSqlRemoteExeService.getRemoteNode(uniqueKey);
        if (remoteNode != null) {
            return PublicUtil.objectToObject(batchSqlRemoteExeService.remoteExecute(remoteNode, isEnd, userId, tenantId, taskId, sql, dtToken, uniqueKey, projectId, dtuicTenantId), ExecuteResultVO.class);
        }

        ExecuteContent content = new ExecuteContent();
        content.setTenantId(tenantId).setProjectId(projectId).setUserId(userId)
                .setSql(sql).setRelationId(taskId).setRelationType(TableRelationType.TASK.getType())
                .setDetailType(task.getTaskType()).setRootUser(isRoot).setCheckSyntax(true)
                .setEngineType(MultiEngineType.LIBRA.getType()).setTableType(ETableType.LIBRA.getType())
                .setSessionKey(uniqueKey)
                .setDtToken(dtToken)
                .setEnd(isEnd);
        return batchSqlExeService.executeSql(content);
    }

    @Override
    public ExecuteSqlParseVO startSqlSophisticated(Long userId, Long tenantId, String uniqueKey, long projectId, long taskId, List<String> sqlList, Boolean isRoot, Long dtuicTenantId, BatchTask task, String dtToken, String database) {
        return null;
    }

    @Override
    public void readyForTaskStartTrigger(Map<String, Object> actionParam, Long dtuicTenantId, Project project, BatchTask batchTask, List<BatchTaskParamShade> taskParamsToReplace) throws Exception {
        String sql = batchTask.getSqlText();
        CheckSyntaxResult result = batchSqlExeService.processSqlText(dtuicTenantId, batchTask.getTaskType(), sql,batchTask.getCreateUserId(),project.getTenantId(),
                project.getId(),false,false, MultiEngineType.LIBRA.getType(), batchTask.getTaskParams());
        sql = result.getSql();
        sql = concatBeginEnd(sql);

        String taskParams = batchTask.getTaskParams();

        actionParam.put("sqlText", sql);
        actionParam.put("engineType", EngineType.Libra.getEngineName());
        actionParam.put("taskParams", taskParams);
        actionParam.put("jdbcUrlParams", getJdbcUrlParams(project));
    }

    private JSONObject getJdbcUrlParams(Project project){
        ProjectEngine projectEngine = projectEngineService.getProjectDb(project.getId(), MultiEngineType.LIBRA.getType());
        if(projectEngine == null){
            throw new RdosDefineException("当前项目没有配置libra引擎");
        }

        JSONObject params = new JSONObject();
        params.put("currentSchema", projectEngine.getEngineIdentity());
        return params;
    }

    private String concatBeginEnd(String sql){
        return "begin " + sql + " end; ";
    }

    @Override
    public Map<String, Object> readyForSyncImmediatelyJob(BatchTask batchTask, Long dtuicTenantId, Boolean isRoot) {
        return Collections.EMPTY_MAP;
    }
}
