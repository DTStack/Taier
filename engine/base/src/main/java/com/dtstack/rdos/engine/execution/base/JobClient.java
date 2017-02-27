package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.operator.Operator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobClient {

    private static final Logger logger = LoggerFactory.getLogger(JobClient.class);

    private List<Operator> operators = new ArrayList<Operator>();

    private String jobName;

    private EJobType eJobType;

    private void getStatus(){

    }

    public void addOperator(Operator operator){
        operators.add(operator);
    }

    public List<Operator> getOperators() {
        return operators;
    }


    public void submit(){
        JobSubmitExecutor.getInstance().submitJob(this);
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public EJobType geteJobType() {
        return eJobType;
    }

    public void seteJobType(EJobType eJobType) {
        this.eJobType = eJobType;
    }

    public enum EJobType{
        MR,//提交 mr 任务
        SQL;//提交sql执行
    }
}
