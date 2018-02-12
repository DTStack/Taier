package com.dtstack.rdos.engine.execution.odps;

import com.aliyun.odps.Odps;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;


/**
 * Odps客户端
 * Date: 2018/2/12
 * Company: www.dtstack.com
 * @author jingzhen
 */
public class OdpsClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(OdpsClient.class);

    private EngineResourceInfo resourceInfo;
    private Odps odps;

    @Override
    public void init(Properties prop) throws Exception {
        resourceInfo = new OdpsResourceInfo();

    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
        //return JobResult.createSuccessResult(submitId);
        return null;
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException("mysql client not support MR job");
    }

    @Override
    public JobResult cancelJob(String jobId) {
        return null;
//        boolean cancelResult = exeQueue.cancelJob(jobId);
//        if(cancelResult){
//            return JobResult.createSuccessResult(jobId);
//        }
//
//        return JobResult.createErrorResult("can't not find the job");
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return null;
        //return exeQueue.getJobStatus(jobId);
    }

    @Override
    public String getJobMaster() {
        throw new RdosException("mysql client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException("mysql client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(String jobId) {
        return null;
//        return exeQueue.getJobLog(jobId);
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        return resourceInfo;
    }
}
