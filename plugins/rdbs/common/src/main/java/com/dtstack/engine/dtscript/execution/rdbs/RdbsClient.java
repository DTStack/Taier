package com.dtstack.engine.dtscript.execution.rdbs;

import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.AbsClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dtscript.execution.rdbs.constant.ConfigConstant;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.dtscript.execution.rdbs.executor.ConnFactory;
import com.dtstack.engine.dtscript.execution.rdbs.executor.RdbsExeQueue;
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

        exeQueue = new RdbsExeQueue(connFactory, MathUtil.getIntegerVal(prop.get(ConfigConstant.MAX_JOB_POOL_KEY)),
                MathUtil.getIntegerVal(prop.get(ConfigConstant.MIN_JOB_POOL_KEY)));
        exeQueue.init();
        resourceInfo = new RdbsResourceInfo(exeQueue);
        LOG.warn("-------init {} plugin success-----, properties={}", dbType, prop.toString());
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.MR.equals(jobType)){
            jobResult = submitJobWithJar(jobClient);
        }else if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        String submitId = exeQueue.submit(jobClient);
        return JobResult.createSuccessResult(submitId);
    }

    private JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException(dbType + "client not support MR job");
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        boolean cancelResult = exeQueue.cancelJob(jobId);
        if(cancelResult){
            return JobResult.createSuccessResult(jobId);
        }

        return JobResult.createErrorResult("can't not find the job");
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobStatus(jobId);
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new RdosException(dbType + " client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException(dbType + "client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        return exeQueue.getJobLog(jobId);
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return resourceInfo.judgeSlots(jobClient);
    }

}
