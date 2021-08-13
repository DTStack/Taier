package com.dtstack.batch.service.job;

import com.dtstack.engine.api.domain.BatchTask;

/**
 * 任务操作相关
 * Date: 2019/5/23
 * Company: www.dtstack.com
 * @author xuchao
 */
public interface ITaskService {

    /**
     * 上传sql到指定的位置
     * eg:
     *    hadoop平台的会将执行sql上传到hdfs上
     * @param dtuicTenantId
     * @param content
     * @param taskType
     * @param taskName
     * @param tenantId
     * @param projectId
     * @return
     */
    String uploadSqlText(Long dtuicTenantId, String content, Integer taskType, String taskName, Long tenantId, Long projectId);

    void readyForPublishTaskInfo(BatchTask task, Long dtuicTenantId, Long projectId);
}
