package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.operator.*;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public abstract class AbsClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbsClient.class);

    @Override
    public JobResult submitJob(JobClient jobClient) {

        EJobType jobType = jobClient.getJobType();
        JobResult jobResult;

        if(EJobType.MR.equals(jobType)){
            try{
                jobResult = adaptToJarSubmit(jobClient);
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
            jobResult = JobResult.createErrorResult("not support type of " + jobType);
        }

        return jobResult;
    }

    public JobResult adaptToJarSubmit(JobClient jobClient){

        AddJarOperator jarOperator = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                jarOperator = (AddJarOperator) operator;
                break;
            }
        }

        if(jarOperator == null){
            return JobResult.createErrorResult("submit type of MR need have add jar operator.");
        }

        Properties properties = new Properties();
        properties.setProperty("jarpath", jarOperator.getJarPath());
        return submitJobWithJar(properties);
    }

}
