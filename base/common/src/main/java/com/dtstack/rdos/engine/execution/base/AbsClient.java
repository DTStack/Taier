package com.dtstack.rdos.engine.execution.base;

import java.io.IOException;

import com.dtstack.rdos.engine.execution.base.enums.EJobType;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class AbsClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbsClient.class);

    @Override
	public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try{
            beforeSubmitFunc(jobClient);
            EJobType jobType = jobClient.getJobType();

            if(EJobType.MR.equals(jobType)){
                jobResult = submitJobWithJar(jobClient);
            }else if(EJobType.SQL.equals(jobType)){
                jobResult = submitSqlJob(jobClient);
            }else if(EJobType.SYNC.equals(jobType)){
                jobResult = submitSyncJob(jobClient);
            }else if(EJobType.PYTHON.equals(jobType)){
                jobResult = submitPythonJob(jobClient);
            }else{
                jobResult = JobResult.createErrorResult("not support job type of " + jobType + "," +
                        " you need to set it in(MR, SQL, SYNC, PYTHON)");
            }
            if (!jobResult.isErr()) {
                jobResult.setData("sql", jobClient.getSql());
            }
        }catch (Exception e){
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        }finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    public JobResult submitJobWithJar(JobClient jobClient){
    	return null;
    }

    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException{
    	return null;
    }

    public JobResult submitSyncJob(JobClient jobClient){
    	return null;
    }

    public JobResult submitPythonJob(JobClient jobClient){
        return null;
    }

    @Override
    public String getJobLog(String jobId) {
        return "";
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        return null;
    }

    public void beforeSubmitFunc(JobClient jobClient){

    }

    public void afterSubmitFunc(JobClient jobClient){

    }
}
