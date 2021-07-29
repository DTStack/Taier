package com.dtstack.batch.service.job;

import com.dtstack.batch.bo.ParseResult;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchScript;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;

/**
 * 执行选中的sql语句或者脚本代码
 * Date: 2019/5/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public interface IBatchSelectSqlService {

    String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId,
                        Long projectId, Long userId, String database, Long taskId, int type, String preJobId);

    String runSqlByTask(Long dtuicTenantId, ParseResult parseResult, Long tenantId, Long projectId,
                        Long userId, String database, boolean isCreateAs, Long taskId, int type, String preJobId);

    /**
     * 根据jobId 获取任务执行结果
     * @param task
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
    ExecuteResultVO selectData(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;

    /**
     * 根据jobId 获取任务执行状态
     *
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
    ExecuteResultVO selectStatus(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType);

    /**
     * 根据jobId 获取任务执行日志
     *
     * @param task
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
    ExecuteResultVO selectRunLog(BatchTask task, BatchHiveSelectSql selectSql, Long tenantId, Long projectId, Long dtuicTenantId, Long userId, Boolean isRoot, Integer taskType) throws Exception;;

    ActionJobEntityVO getTaskStatus(String jobId);
}
