package com.dtstack.batch.engine.tidb.service;

import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchScript;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.dtcenter.common.enums.TaskStatus;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author yuebai
 * @date 2019-06-27
 */
@Service
public class BatchTiDBSelectSqlService implements IBatchSelectSqlService {
    @Override
    public ExecuteResultVO runPythonShellScript(BatchScript script, String content, Long userId) {
        return null;
    }

    @Override
    public ExecuteResultVO runPythonShellWithTask(BatchTask task, String content, Long userId, String jobId) {
        return null;
    }

    @Override
    public String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, Long taskId, int type, String preJobId) {
        return null;
    }

    @Override
    public String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId, Long userId, String database, boolean isCreateAs, Long taskId, int type, String preJobId) {
        return null;
    }

    @Override
    public ExecuteResultVO runCarbonSqlTask(Long dtuicTenantId, String originSql, Long tenantId, Long projectId, Long userId, String database, Long taskId, String dataJson, String jobId) {
        return null;
    }

    @Override
    public ExecuteResultVO selectData(BatchTask batchTask, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception {
        ExecuteResultVO result = new ExecuteResultVO(Objects.nonNull(selectSql) ? selectSql.getJobId() : null);
        return result;
    }

    @Override
    public ExecuteResultVO selectStatus(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO result = new ExecuteResultVO(Objects.nonNull(selectSql) ? selectSql.getJobId() : null);
        result.setStatus(TaskStatus.FINISHED.getStatus());
        return result;
    }

    @Override
    public ExecuteResultVO selectRunLog(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) {
        ExecuteResultVO result = new ExecuteResultVO(Objects.nonNull(selectSql) ? selectSql.getJobId() : null);
        return result;
    }

    @Override
    public ActionJobEntityVO getTaskStatus(String jobId) {
        return null;
    }

    @Override
    public ExecuteResultVO runImpalaSqlTask(String formatSql, Long tenantId, long projectId, Long userId, long taskId, String database) {
        return null;
    }
}
