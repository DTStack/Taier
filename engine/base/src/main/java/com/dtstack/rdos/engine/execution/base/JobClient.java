package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

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
    
    private String taskId;
    
    private String engineTaskId;

    private EJobType eJobType;

    private static ArrayBlockingQueue<JobClient> queue;
    
    private RdosTaskStatus getStatus(){
		return null;
    }

    public static ArrayBlockingQueue<JobClient> getQueue() {
		return queue;
	}

	public static void setQueue(ArrayBlockingQueue<JobClient> queue) {
		if(JobClient.queue==null){
			synchronized(JobClient.class){
				if(JobClient.queue==null){
					JobClient.queue = queue;
				}
			}
		}
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getEngineTaskId() {
		return engineTaskId;
	}

	public void setEngineTaskId(String engineTaskId) {
		this.engineTaskId = engineTaskId;
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
