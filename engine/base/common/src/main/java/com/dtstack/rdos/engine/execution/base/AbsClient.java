package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public abstract class AbsClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbsClient.class);

    public static final String JOB_JAR_PATH_KEY = "job.jar.path";

    public static final String JOB_MAIN_CLASS_KEY = "job.main.class";

    public static final String JOB_APP_NAME_KEY = "job.name";

    @Override
    public JobResult submitJob(JobClient jobClient) {

        EJobType jobType = jobClient.getJobType();
        JobResult jobResult;

        if(EJobType.MR.equals(jobType)){
            try{
                jobResult = submitJobWithJar(jobClient);
            }catch (Exception e){
                logger.error("", e);
                jobResult = JobResult.createErrorResult(e);
            }

        }else if(EJobType.SQL.equals(jobType)){
            try{
                jobResult = submitSqlJob(jobClient);
            }catch (Exception e){
                logger.error("", e);
                jobResult = JobResult.createErrorResult(e);
            }
        }else{
            jobResult = JobResult.createErrorResult("not support job type of " + jobType + "," +
                    " you need to set it in(MR, SQL)");
        }

        return jobResult;
    }    
}
