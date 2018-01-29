package com.dtstack.rdos.engine.execution.mysql;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
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

    @Override
    public void init(Properties prop) throws Exception {

    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
        String submitId = exeQueue.submit(jobClient.getJobName(), jobClient.getSql());
        return JobResult.createSuccessResult(submitId);
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException("mysql client not support MR job");
    }

    @Override
    public JobResult cancelJob(String jobId) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster() {
        return null;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }
}
