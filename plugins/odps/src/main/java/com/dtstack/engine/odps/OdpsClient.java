package com.dtstack.engine.odps;

import com.aliyun.odps.Job;
import com.aliyun.odps.task.SQLTask;
import com.dtstack.engine.common.exception.RdosException;
import com.dtstack.engine.common.AbstractClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.base.resource.EngineResourceInfo;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.odps.util.OdpsUtil;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import com.aliyun.odps.Instance;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.Odps;


/**
 * Odps客户端
 * Date: 2018/2/12
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class OdpsClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(OdpsClient.class);

    private EngineResourceInfo resourceInfo;

    private Odps odps;

    private static final String SPLIT = ";";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public Odps getOdps() {
        return odps;
    }


    @Override
    public void init(Properties prop) throws Exception {
        resourceInfo = new OdpsResourceInfo();
        Map<String, String> configMap = objectMapper.readValue(objectMapper.writeValueAsBytes(prop), Map.class);
        odps = OdpsUtil.initOdps(configMap);
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.SQL.equals(jobType)){
            jobResult = submitSqlJob(jobClient);
        }
        return jobResult;
    }

    private JobResult submitSqlJob(JobClient jobClient) {
        try {
            String[] sqls = jobClient.getSql().split(SPLIT);
            Job job = new Job();
            String guid = UUID.randomUUID().toString();
            String taskName = "query_task_" + Calendar.getInstance().getTimeInMillis();
            for (int i = 0; i < sqls.length; i++) {
                String sql = sqls[i];
                if(StringUtils.isEmpty(sql.trim())){
                    continue;
                }

                if (!sql.endsWith(SPLIT)) {
                    sql = sql + SPLIT;
                }

                SQLTask task = new SQLTask();
                task.setName(String.format("%s_%s", taskName, i));
                task.setQuery(sql);
                task.setProperty("guid", guid);
                job.addTask(task);
            }

            Instance instance = odps.instances().create(job);
            return JobResult.createSuccessResult(instance.getId());
        } catch (OdpsException e) {
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
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
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {

        String jobId = jobIdentifier.getEngineJobId();
        Instance instance = odps.instances().get(jobId);

        if (instance == null) {
            throw new RuntimeException("can't find odps task: " + jobId);
        }

        Instance.TaskStatus.Status taskStatus = null;

        try {
            Map<String, Instance.TaskStatus> statusMap = instance.getTaskStatus();
            if (statusMap == null || statusMap.size() == 0) {
                throw new RuntimeException("statusMap empty: " + jobId);
            }
            taskStatus = statusMap.entrySet().iterator().next().getValue().getStatus();

            if (taskStatus == null) {
                throw new RuntimeException("can't find task status for task: " + jobId);
            }
        } catch (Exception e) {
            throw new RdosException(e.getMessage());
        }

        RdosTaskStatus rdosTaskStatus = null;
        switch (taskStatus) {
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
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new RdosException("odps client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        throw new RdosException("odps client not support method 'getMessageByHttp'");
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        if (!hasLog(jobId)) {
            return "";
        }

        Instance instance = odps.instances().get(jobId);

        if (instance == null) {
            throw new RuntimeException("can't find odps task: " + jobId);
        }

        try {
            return instance.getTaskResults().toString();
        } catch (Exception e) {
            throw new RuntimeException("getLog error: " + jobId + " msg:" + e.getMessage());
        }

    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return resourceInfo.judgeSlots(jobClient);
    }

    private boolean hasLog(String jobId) {
        try {
            RdosTaskStatus taskStatus = getJobStatus(JobIdentifier.createInstance(jobId, null, null));
            return taskStatus.equals(RdosTaskStatus.FAILED);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
