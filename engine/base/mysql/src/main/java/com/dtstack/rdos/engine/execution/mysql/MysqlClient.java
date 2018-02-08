package com.dtstack.rdos.engine.execution.mysql;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.mysql.executor.ConnFactory;
import com.dtstack.rdos.engine.execution.mysql.executor.MetaDataConnPool;
import com.dtstack.rdos.engine.execution.mysql.executor.MysqlExeQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

/**
 * 和其他类型的client不同--需要等待sql执行完成。
 * Date: 2018/1/29
 * Company: www.dtstack.com
 * @author xuchao
 */

public class MysqlClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(MysqlClient.class);

    private MysqlExeQueue exeQueue;

    private EngineResourceInfo resourceInfo;

    private ConnFactory connFactory;

    @Override
    public void init(Properties prop) throws Exception {

        connFactory = new ConnFactory();
        connFactory.init(prop);

        exeQueue = new MysqlExeQueue(connFactory);
        exeQueue.init();
        resourceInfo = new MysqlResourceInfo(exeQueue);

        MetaDataConnPool.getInstance();
        ConfigParse.getEngineTypeList();

        LOG.warn("-------init mysql plugin success-----");
    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
        String submitId = exeQueue.submit(jobClient);
        return JobResult.createSuccessResult(submitId);
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException("mysql client not support MR job");
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
        throw new RdosException("mysql client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException("mysql client not support method 'getMessageByHttp'");
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
