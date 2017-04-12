package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.*;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

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

    private ClientType clientType;

    private JobResult jobResult;
    
    private Restoration isRestoration;
    
    private Long actionLogId;

    private static LinkedBlockingQueue<JobClient> queue;

    /***
     * 获取engine上job执行的状态
     * @param engineTaskId engine jobId
     * @return
     */
    public static RdosTaskStatus getStatus(ClientType clientType, String engineTaskId){
		return JobSubmitExecutor.getInstance().getJobStatus(clientType, engineTaskId);
    }

    public static LinkedBlockingQueue<JobClient> getQueue() {
		return queue;
	}

	public static void setQueue(LinkedBlockingQueue<JobClient> queue) {
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
	
	public JobClient (List<Operator> operators,String jobName ,String taskId,String engineTaskId,EJobType jobType,ComputeType computeType,Restoration isRestoration,Long actionLogId){
		this.operators.addAll(operators);
        this.jobName = jobName;
        this.taskId = taskId;
        this.engineTaskId = engineTaskId;
        this.jobType = jobType;
        this.computeType = computeType;
        this.isRestoration = isRestoration;
        this.actionLogId = actionLogId ; 
	}
	

    public void submit(){
        JobSubmitExecutor.getInstance().submitJob(this);
    }
    
    public static JobResult stop(ClientType clientType, String engineTaskId){
    	return JobSubmitExecutor.getInstance().stopJob(clientType, engineTaskId);
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

	public Restoration getIsRestoration() {
		return isRestoration;
	}

	public void setIsRestoration(Restoration isRestoration) {
		this.isRestoration = isRestoration;
	}

	public Long getActionLogId() {
		return actionLogId;
	}

	public void setActionLogId(Long actionLogId) {
		this.actionLogId = actionLogId;
	}

    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
}
