package com.dtstack.rdos.engine.execution.sparkyarn;

import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by softfly on 17/8/10.
 */
public class SparkYarnClient extends AbsClient {
    @Override
    public void init(Properties prop) throws Exception {

    }

    @Override
    public JobResult cancelJob(ParamAction jobId) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        return null;
    }

    @Override
    public String getJobDetail(String jobId) {
        return null;
    }

    @Override
    public JobResult immediatelySubmitJob(JobClient jobClient) {
        return null;
    }

    @Override
    public String getJobMaster() {
        return null;
    }
}
