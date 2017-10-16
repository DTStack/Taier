package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.*;
import com.dtstack.rdos.engine.execution.base.operator.Operator;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dtstack.rdos.engine.execution.base.components.OrderObject;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class JobClient extends OrderObject{

    private JobClientCallBack jobClientCallBack;

    private static final Logger logger = LoggerFactory.getLogger(JobClient.class);

    private List<Operator> operators;

    private Properties confProperties;

    private String sql;

    private String taskParams;

    private String jobName;

    private String taskId;

    private String engineTaskId;

    private EJobType jobType;

    private ComputeType computeType;

    private String engineType;

    private JobResult jobResult;

    private Restoration isRestoration;

    private Long actionLogId;

    /**提交MR执行的时候附带的执行参数*/
    private String classArgs;

    private static LinkedBlockingQueue<JobClient> queue;

    /***
     * 获取engine上job执行的状态
     * @param engineTaskId engine jobId
     * @return
     */
    public static RdosTaskStatus getStatus(String engineType, String engineTaskId) {
        return JobSubmitExecutor.getInstance().getJobStatus(engineType, engineTaskId);
    }
    
    /**
     * 获取engine上jobManager url
     * @return
     */
    public static Map<String,String> getJobMaster(){
    	return JobSubmitExecutor.getInstance().getJobMaster();
    }

    public static LinkedBlockingQueue<JobClient> getQueue() {
        return queue;
    }

    public static void setQueue(LinkedBlockingQueue<JobClient> queue) {
        if (JobClient.queue == null) {
            synchronized (JobClient.class) {
                if (JobClient.queue == null) {
                    JobClient.queue = queue;
                }
            }
        }
    }

    public JobClient() {

    }

    public JobClient(ParamAction paramAction) throws Exception {
        this.sql = paramAction.getSqlText();
        this.taskParams = paramAction.getTaskParams();
        this.jobName = paramAction.getName();
        this.taskId = paramAction.getTaskId();
        this.engineTaskId = paramAction.getEngineTaskId();
        this.jobType = EJobType.getEJobType(paramAction.getTaskType());
        this.computeType = ComputeType.getComputeType(paramAction.getComputeType());
        this.isRestoration = Restoration.getRestoration(paramAction.getIsRestoration());
        this.actionLogId = paramAction.getActionLogId();
        this.engineType = paramAction.getEngineType();
        this.classArgs = paramAction.getExeArgs();
    }

    public void submit() throws Exception {
        JobSubmitExecutor.getInstance().submitJob(this);
    }

    public static JobResult stop(ParamAction paramAction) throws Exception {
        return JobSubmitExecutor.getInstance().stopJob(paramAction);
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

    public void addOperators(List<Operator> operators) {
        this.operators.addAll(operators);
    }

    public void addOperator(Operator operator) {
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

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public Properties getConfProperties() {
        return confProperties;
    }

    public JobClientCallBack getJobClientCallBack() {
        return jobClientCallBack;
    }

    public void setJobClientCallBack(JobClientCallBack jobClientCallBack) {
        this.jobClientCallBack = jobClientCallBack;
    }

    public String getSql() {
        return sql;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setConfProperties(Properties confProperties) {
        this.confProperties = confProperties;
    }

    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public String getClassArgs() {
        return classArgs;
    }

    public void setClassArgs(String classArgs) {
        this.classArgs = classArgs;
    }
}
