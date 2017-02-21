package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.entrance.sql.operator.Operator;
import com.dtstack.rdos.engine.execution.pojo.JobExeContext;
import com.dtstack.rdos.engine.execution.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobClient {

    private static final Logger logger = LoggerFactory.getLogger(JobClient.class);

    private JobExeContext exeContext = new JobExeContext();

    private void getStatus(){

    }

    public void addOperator(Operator operator){
        exeContext.addOperator(operator);
    }

    public void submit(){
        JobSubmitExecutor.getInstance().submitJob(this);
    }

}
