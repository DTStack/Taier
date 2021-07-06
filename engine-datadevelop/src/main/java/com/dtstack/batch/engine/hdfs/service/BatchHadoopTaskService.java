package com.dtstack.batch.engine.hdfs.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dtstack.batch.common.env.EnvironmentContext;
import com.dtstack.batch.common.exception.RdosDefineException;
import com.dtstack.batch.domain.BatchTask;
import com.dtstack.batch.engine.rdbms.common.HadoopConf;
import com.dtstack.batch.engine.rdbms.common.HdfsOperator;
import com.dtstack.batch.enums.TaskOperateType;
import com.dtstack.batch.service.job.ITaskService;
import com.dtstack.dtcenter.common.enums.EJobType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * hadoop平台上关联任务
 * Date: 2019/5/23
 * Company: www.dtstack.com
 * @author xuchao
 */

@Service
public class BatchHadoopTaskService implements ITaskService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchHadoopTaskService.class);

    @Autowired
    private EnvironmentContext env;

    /**
     * 执行sql或者脚本上传到hdfs
     * @param dtuicTenantId
     * @param content
     * @param taskType
     * @param taskName
     * @param tenantId
     * @param projectId
     * @return
     */
    @Override
    public String uploadSqlText(final Long dtuicTenantId, String content, final Integer taskType, final String taskName, final Long tenantId, final Long projectId) {
        String hdfsPath = null;
        try {
            // shell任务，创建脚本文件
            String fileName = null;
            if (taskType.equals(EJobType.SHELL.getVal())) {
                fileName = String.format("shell_%s_%s_%s_%s.sh", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.PYTHON.getVal())) {
                fileName = String.format("python_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            } else if (taskType.equals(EJobType.SPARK_PYTHON.getVal())) {
                fileName = String.format("pyspark_%s_%s_%s_%s.py", tenantId, projectId,
                        taskName, System.currentTimeMillis());
            }

            if (fileName != null) {
                hdfsPath = this.env.getHdfsBatchPath() + fileName;
                if (taskType.equals(EJobType.SHELL.getVal())) {
                    content = content.replaceAll("\r\n", System.getProperty("line.separator"));
                }
                HdfsOperator.uploadInputStreamToHdfs(HadoopConf.getConfiguration(dtuicTenantId),HadoopConf.getHadoopKerberosConf(dtuicTenantId), content.getBytes(), hdfsPath);
            }
        } catch (final Exception e) {
            BatchHadoopTaskService.LOG.error("", e);
            throw new RdosDefineException("Update task to HDFS failure:" + e.getMessage());
        }

        return HadoopConf.getDefaultFs(dtuicTenantId) + hdfsPath;
    }

    @Override
    public void readyForPublishTaskInfo(final BatchTask task, final Long dtuicTenantId, final Long projectId) {

        if (task.getTaskType().equals(EJobType.SPARK_PYTHON.getVal()) && StringUtils.isNotBlank(task.getExeArgs())) {
            final JSONObject args = JSON.parseObject(task.getExeArgs());
            if (args.getInteger("operateModel").equals(TaskOperateType.EDIT.getType())) {
                final String fileDir = this.uploadSqlText(dtuicTenantId, task.getSqlText(),
                        task.getTaskType(), task.getName(), task.getTenantId(), projectId);
                args.put("hdfsPath", fileDir);
                task.setExeArgs(args.toJSONString());
            }
        }
    }
}
