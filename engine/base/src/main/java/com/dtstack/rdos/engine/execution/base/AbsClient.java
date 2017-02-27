package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.operator.*;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
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
        //根据operator 判断是提交jar任务还是生成sqltable并提交任务
        boolean isJarOperator = false;
        AddJarOperator jarOperator = null;
        for(Operator operator : jobClient.getOperators()){
            if(operator instanceof AddJarOperator){
                isJarOperator = true;
                jarOperator = (AddJarOperator) operator;
                break;
            }
        }

        if(isJarOperator){
            return adaptToJarSubmit(jarOperator);
        }else{
            return submitSqlJob(jobClient);
        }
    }

    public JobResult adaptToJarSubmit(AddJarOperator jarOperator){
        Properties properties = new Properties();
        properties.setProperty("jarpath", jarOperator.getJarPath());
        return submitJobWithJar(properties);
    }

}
