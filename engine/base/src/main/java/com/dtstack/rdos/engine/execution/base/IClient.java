package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.pojo.JobResult;

import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public interface IClient {

    /**
     * FIXME 根据zk做初始化的时候的操作
     * @param prop
     */
    void init(Properties prop);

    JobResult submitJobWithJar(Properties properties);

    JobResult submitSqlJob(String sql);

    /**
     * FIXME 提交的时候先判断下计算资源是否足够
     * @param jobClient
     * @return
     */
    JobResult submitJob(JobClient jobClient);

    String cancleJob(String jobId);

    String getJobStatus(String jobId);

}
