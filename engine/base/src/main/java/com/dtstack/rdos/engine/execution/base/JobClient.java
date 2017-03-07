package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ComputeType;
import com.dtstack.rdos.engine.execution.base.enumeration.EJobType;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
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

    private EJobType jobType;
    
    private ComputeType computeType;

    private JobResult jobResult;

    private static ArrayBlockingQueue<JobClient> queue;

    /***
     * 获取Flink上job执行的状态
     * @param engineTaskId Flink jobId
     * @return
     */
    public static RdosTaskStatus getStatus(String engineTaskId){
		return JobSubmitExecutor.getInstance().getJobStatus(engineTaskId);
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
	
	public JobClient(){
		
	}
	
	public JobClient (List<Operator> operators){
		this.operators.addAll(operators);
	}
	

    public void submit(){
        JobSubmitExecutor.getInstance().submitJob(this);
    }
    
    public static JobResult stop(String engineTaskId){
    	return JobSubmitExecutor.getInstance().stopJob(engineTaskId);
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
	
	public void addOperators(List<Operator> operators){
        this.operators.addAll(operators);
    }

	public void addOperator(Operator operator){
        operators.add(operator);
    }

    public List<Operator> getOperators() {
        return operators;
    }


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public EJobType getJobType() {
        return jobType;
    }

    public void setJobType(EJobType jobType) {
        this.jobType = jobType;
    }

    public JobResult getJobResult() {
        return jobResult;
    }

    public void setJobResult(JobResult jobResult) {
        this.jobResult = jobResult;
    }

	public ComputeType getComputeType() {
		return computeType;
	}

	public void setComputeType(ComputeType computeType) {
		this.computeType = computeType;
	}
}
