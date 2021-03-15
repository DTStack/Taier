package com.dtstack.engine.common.client;

import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.lineage.Column;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JobStatusFrequency;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public abstract class AbstractClient implements IClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    public Map<String, JobStatusFrequency> jobStatusMap = Maps.newConcurrentMap();

    public AbstractClient() {
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try {
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null) {
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(), ",") + ")");
            }
        } catch (Exception e) {
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        } finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        RdosTaskStatus status = RdosTaskStatus.NOTFOUND;
        try {
            status = processJobStatus(jobIdentifier);
        }catch (Exception e) {
            logger.error("get job status error: {}", e.getMessage());
        } finally {
            handleJobStatus(jobIdentifier, status);
        }
        return status;
    }

    protected RdosTaskStatus processJobStatus(JobIdentifier jobIdentifier) {
        return RdosTaskStatus.NOTFOUND;
    }

    protected void handleJobStatus(JobIdentifier jobIdentifier, RdosTaskStatus status) {
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return null;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.notOk( "");
    }

    protected void beforeSubmitFunc(JobClient jobClient) {
    }

    protected void afterSubmitFunc(JobClient jobClient) {
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }


    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        return null;
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        return null;
    }

    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        return null;
    }

    @Override
    public ClusterResource getClusterResource() {
        return null;
    }

    @Override
    public List<Column> getAllColumns(String tableName,String schemaName, String dbName) {
        return null;
    }
}
