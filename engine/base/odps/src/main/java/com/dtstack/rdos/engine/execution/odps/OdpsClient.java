package com.dtstack.rdos.engine.execution.odps;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.task.SQLTask;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enumeration.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.rdos.engine.execution.odps.util.OdpsUtil;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;


/**
 * Odps客户端
 * Date: 2018/2/12
 * Company: www.dtstack.com
 * @author jingzhen
 */
public class OdpsClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(OdpsClient.class);

    private EngineResourceInfo resourceInfo;
    private Odps odps;

    @Override
    public void init(Properties prop) throws Exception {
        resourceInfo = new OdpsResourceInfo();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> configMap = objectMapper.readValue(objectMapper.writeValueAsBytes(prop), Map.class);
        odps = OdpsUtil.initOdps(configMap);
    }

    @Override
    public JobResult submitSqlJob(JobClient jobClient) throws IOException, ClassNotFoundException {
        try {
            Instance instance = SQLTask.run(odps, jobClient.getSql());
            return JobResult.createSuccessResult(instance.getId());
        } catch (OdpsException e) {
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public JobResult submitJobWithJar(JobClient jobClient) {
        throw new RdosException("odps client not support MR job?");
    }

    @Override
    public JobResult cancelJob(String jobId) {
        Instance instance = odps.instances().get(jobId);

        if (instance == null) {
            throw new RuntimeException("can't find odps task: " + jobId);
        }

        try {
            instance.stop();
        } catch (OdpsException e) {
            return JobResult.createErrorResult(e);
        }

        JobResult jobResult = JobResult.newInstance(false);
        jobResult.setData("jobid", jobId);
        return jobResult;
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
        Instance instance = odps.instances().get(jobId);

        if (instance == null) {
           throw new RuntimeException("can't find odps task: " + jobId);
        }

        Instance.TaskStatus.Status taskStatus  = null;

        try {
            Map<String, Instance.TaskStatus> statusMap = instance.getTaskStatus();

            for (Map.Entry<String, Instance.TaskStatus> status : statusMap.entrySet()) {
                taskStatus = status.getValue().getStatus();
                break;
            }
            if (taskStatus == null) {
                throw new RuntimeException("can't find task status for task: " + jobId);
            }
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }

        RdosTaskStatus rdosTaskStatus = null;
        switch(taskStatus) {
            case WAITING:
                rdosTaskStatus = RdosTaskStatus.SUBMITTING;
                break;
            case SUCCESS:
                rdosTaskStatus = RdosTaskStatus.FINISHED;
                break;
            case SUSPENDED:
                rdosTaskStatus = RdosTaskStatus.KILLED;
                break;
            case CANCELLED:
                rdosTaskStatus = RdosTaskStatus.CANCELED;
                break;
            default:
                rdosTaskStatus = RdosTaskStatus.valueOf(taskStatus.name().toUpperCase());
        }

        return rdosTaskStatus;
    }

    @Override
    public String getJobMaster() {
        throw new RdosException("odps client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException("odps client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(String jobId) {
        return null;
//        return exeQueue.getJobLog(jobId);
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        return resourceInfo;
    }
}
