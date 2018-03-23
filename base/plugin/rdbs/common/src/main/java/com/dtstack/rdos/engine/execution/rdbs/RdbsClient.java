package com.dtstack.rdos.engine.execution.rdbs;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;
import com.dtstack.rdos.engine.execution.rdbs.executor.RdbsExeQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 和其他类型的client不同--需要等待sql执行完成。
 * Date: 2018/2/27
 * Company: www.dtstack.com
 * @author jingzhen
 */

public abstract class RdbsClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(RdbsClient.class);

    private RdbsExeQueue exeQueue;

    private EngineResourceInfo resourceInfo;

    private ConnFactory connFactory;

    protected String dbType = "rdbs";

    protected abstract ConnFactory getConnFactory();

    @Override
    public void init(Properties prop) throws Exception {

        connFactory = getConnFactory();
        connFactory.init(prop);

        exeQueue = new RdbsExeQueue(connFactory);
        exeQueue.init();
        resourceInfo = new RdbsResourceInfo(exeQueue);
        LOG.warn("-------init {} plugin success-----", dbType);
    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
        String submitId = exeQueue.submit(jobClient);
        return JobResult.createSuccessResult(submitId);
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException(dbType + "client not support MR job");
    }

    @Override
    public JobResult cancelJob(String jobId) {
        boolean cancelResult = exeQueue.cancelJob(jobId);
        if(cancelResult){
            return JobResult.createSuccessResult(jobId);
        }

        return JobResult.createErrorResult("can't not find the job");
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return exeQueue.getJobStatus(jobId);
    }

    @Override
    public String getJobMaster() {
        throw new RdosException(dbType + " client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException(dbType + "client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(String jobId) {
        return exeQueue.getJobLog(jobId);
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        return resourceInfo;
    }
}
