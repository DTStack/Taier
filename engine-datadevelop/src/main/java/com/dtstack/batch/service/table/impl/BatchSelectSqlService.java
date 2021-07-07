package com.dtstack.batch.service.table.impl;

import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.dao.BatchHiveSelectSqlDao;
import com.dtstack.batch.dao.BatchTaskDao;
import com.dtstack.batch.domain.BatchHiveSelectSql;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.service.impl.MultiEngineServiceFactory;
import com.dtstack.batch.service.job.IBatchSelectSqlService;
import com.dtstack.batch.vo.ExecuteResultVO;
import com.dtstack.batch.vo.ExecuteSelectSqlData;
import com.dtstack.dtcenter.common.enums.ComputeType;
import com.dtstack.dtcenter.common.exception.DtCenterDefException;
import com.dtstack.engine.api.service.ActionService;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;

/**
 * 执行选中的sql或者脚本
 * @author jiangbo
 */
@Service
public class BatchSelectSqlService {

    public static final Logger LOGGER = LoggerFactory.getLogger(BatchSelectSqlService.class);

    @Autowired
    private BatchTaskDao batchTaskDao;

    @Autowired
    private BatchHiveSelectSqlDao batchHiveSelectSqlDao;

    @Autowired
    ActionService actionService;

    @Autowired
    private MultiEngineServiceFactory multiEngineServiceFactory;

    /**
     * 查询sql运行结果
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectData(String jobId,
                                      Long taskId,
                                      Long tenantId,
                                      Long projectId,
                                      Long dtuicTenantId,
                                      Long userId,
                                      Boolean isRoot,
                                      Integer type,
                                      String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectData(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, projectId,
                dtuicTenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * 查询sql运行状态
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectStatus(String jobId,
                                        Long taskId,
                                        Long tenantId,
                                        Long projectId,
                                        Long dtuicTenantId,
                                        Long userId,
                                        Boolean isRoot,
                                        Integer type,
                                        String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectStatus(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, projectId,
                dtuicTenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * 查询sql运行日志
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param projectId
     * @param dtuicTenantId
     * @param userId
     * @param isRoot
     * @param type
     * @param sqlId
     * @return
     * @throws Exception
     */
    public ExecuteResultVO selectRunLog(String jobId,
                                        Long taskId,
                                        Long tenantId,
                                        Long projectId,
                                        Long dtuicTenantId,
                                        Long userId,
                                        Boolean isRoot,
                                        Integer type,
                                        String sqlId) throws Exception {
        ExecuteSelectSqlData selectSqlData = beforeGetResult(jobId, taskId, tenantId, type, sqlId);
        return selectSqlData.getIBatchSelectSqlService().selectRunLog(selectSqlData.getBatchTask(), selectSqlData.getBatchHiveSelectSql(), tenantId, projectId,
                dtuicTenantId, userId, isRoot, selectSqlData.getTaskType());
    }

    /**
     * sql查询前置处理
     *
     * @param jobId
     * @param taskId
     * @param tenantId
     * @param type
     * @param sqlId
     * @return
     */
    private ExecuteSelectSqlData beforeGetResult(String jobId, Long taskId, Long tenantId, Integer type, String sqlId){
        BatchHiveSelectSql batchHiveSelectSql = batchHiveSelectSqlDao.getByJobId(StringUtils.isNotEmpty(sqlId) ? sqlId : jobId, tenantId, null);
        Preconditions.checkNotNull(batchHiveSelectSql, "不存在该临时查询");
        if (StringUtils.isNotEmpty(sqlId)){
            batchHiveSelectSql.setFatherJobId(jobId);
            batchHiveSelectSql.setJobId(sqlId);
        }
        IBatchSelectSqlService selectSqlService = multiEngineServiceFactory.getBatchSelectSqlService(batchHiveSelectSql.getEngineType());
        Preconditions.checkNotNull(selectSqlService, String.format("不支持引擎类型 %d", batchHiveSelectSql.getEngineType()));
        BatchTask batchTask = batchTaskDao.getOne(taskId);;
        Integer taskType = null;
        if (Objects.nonNull(batchTask)) {
            taskType = batchTask.getTaskType();
        }
        if (Objects.isNull(taskType)) {
            throw new DtCenterDefException("任务类型为空");
        }
        return new ExecuteSelectSqlData(batchHiveSelectSql, batchTask, taskType, selectSqlService);
    }


    public BatchHiveSelectSql getByJobId(String jobId, Long tenantId, Integer isDeleted){
        BatchHiveSelectSql selectSql = batchHiveSelectSqlDao.getByJobId(jobId,tenantId, isDeleted);
        if (selectSql == null){
            throw new RdosDefineException("select job not exists");
        }
        return selectSql;
    }

    public void stopSelectJob(String jobId,Long tenantId,Long projectId){
        try {
            actionService.stop(Collections.singletonList(jobId), ComputeType.BATCH.getType());
            // 这里用逻辑删除，是为了在调度端删除可能生成的临时表
            batchHiveSelectSqlDao.deleteByJobId(jobId,tenantId,projectId);
        }catch (Exception e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql,Long tenantId,
                             Long projectId,String sql,Long userId, int engineType) {
        this.addSelectSql(jobId, tempTable, isSelectSql, tenantId, projectId, sql, userId, null,engineType);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addSelectSql(String jobId, String tempTable, int isSelectSql, Long tenantId, Long projectId,
                             String sql, Long userId, String parsedColumns, int engineType){
        BatchHiveSelectSql hiveSelectSql = new BatchHiveSelectSql();
        hiveSelectSql.setJobId(jobId);
        hiveSelectSql.setTempTableName(tempTable);
        hiveSelectSql.setTenantId(tenantId);
        hiveSelectSql.setProjectId(projectId);
        hiveSelectSql.setIsSelectSql(isSelectSql);
        hiveSelectSql.setSqlText(sql);
        hiveSelectSql.setUserId(userId);
        hiveSelectSql.setParsedColumns(parsedColumns);
        hiveSelectSql.setEngineType(engineType);

        batchHiveSelectSqlDao.insert(hiveSelectSql);
    }

    public int updateGmtModify(String jobId, Long tenantId, Long projectId){
        return batchHiveSelectSqlDao.updateGmtModify(jobId, tenantId, projectId);
    }
}
