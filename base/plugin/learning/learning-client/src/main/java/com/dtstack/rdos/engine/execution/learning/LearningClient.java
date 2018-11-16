package com.dtstack.rdos.engine.execution.learning;

import com.dtstack.learning.conf.LearningConfiguration;
import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.JobIdentifier;
import com.dtstack.rdos.engine.execution.base.enums.EJobType;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.learning.client.Client;
import com.google.gson.Gson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * xlearning客户端
 * Date: 2018/6/22
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class LearningClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(LearningClient.class);

    private Client client;

    private LearningConfiguration conf = new LearningConfiguration();

    private static final Gson gson = new Gson();

    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("LearningClien.init ...");
        conf.set("fs.hdfs.impl.disable.cache", "true");
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        String hadoopConfDir = prop.getProperty("hadoop.conf.dir");
        if(StringUtils.isNotBlank(hadoopConfDir)) {
            conf.addResource(new URL("file://" + hadoopConfDir + "/" + "core-site.xml"));
            conf.addResource(new URL("file://" + hadoopConfDir + "/" + "hdfs-site.xml"));
            conf.addResource(new URL("file://" + hadoopConfDir + "/" + "yarn-site.xml"));
        }

        Enumeration enumeration =  prop.propertyNames();
        while(enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            Object value = prop.get(key);
            if(value instanceof String) {
                conf.set(key, (String)value);
            } else if(value instanceof  Integer) {
                conf.setInt(key, (Integer)value);
            } else if(value instanceof  Float) {
                conf.setFloat(key, (Float)value);
            } else if(value instanceof Double) {
                conf.setDouble(key, (Double)value);
            } else if(value instanceof Map) {
                Map<String,String> map = (Map<String, String>) value;
                for(Map.Entry<String,String> entry : map.entrySet()) {
                    conf.set(entry.getKey(), entry.getValue());
                }
            } else {
                conf.set(key, value.toString());
            }
        }
        String queue = prop.getProperty(LearningConfiguration.LEARNING_APP_QUEUE);
        if (StringUtils.isNotBlank(queue)){
            conf.set(LearningConfiguration.LEARNING_APP_QUEUE, queue);
        }
        client = new Client(conf);
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        if(EJobType.PYTHON.equals(jobType)){
            jobResult = submitPythonJob(jobClient);
        }
        return jobResult;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        try {
            client.kill(jobId);
            return JobResult.createSuccessResult(jobId);
        } catch (Exception e) {
            LOG.error("", e);
            return JobResult.createErrorResult(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();

        if(org.apache.commons.lang3.StringUtils.isEmpty(jobId)){
            return null;
        }
        try {
            ApplicationReport report = client.getApplicationReport(jobId);
            YarnApplicationState applicationState = report.getYarnApplicationState();
            switch(applicationState) {
                case KILLED:
                    return RdosTaskStatus.KILLED;
                case NEW:
                case NEW_SAVING:
                    return RdosTaskStatus.CREATED;
                case SUBMITTED:
                    //FIXME 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                    return RdosTaskStatus.WAITCOMPUTE;
                case ACCEPTED:
                    return RdosTaskStatus.SCHEDULED;
                case RUNNING:
                    return RdosTaskStatus.RUNNING;
                case FINISHED:
                    //state 为finished状态下需要兼顾判断finalStatus.
                    FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                    if(finalApplicationStatus == FinalApplicationStatus.FAILED){
                        return RdosTaskStatus.FAILED;
                    }else if(finalApplicationStatus == FinalApplicationStatus.SUCCEEDED){
                        return RdosTaskStatus.FINISHED;
                    }else if(finalApplicationStatus == FinalApplicationStatus.KILLED){
                        return RdosTaskStatus.KILLED;
                    }else{
                        return RdosTaskStatus.RUNNING;
                    }

                case FAILED:
                    return RdosTaskStatus.FAILED;
                default:
                    throw new RdosException("Unsupported application state");
            }
        } catch (YarnException e) {
            LOG.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster() {
        throw new RdosException("learning client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    private JobResult submitPythonJob(JobClient jobClient){
        LOG.info("LearningClient.submitPythonJob");
        try {
            String[] args = LearningUtil.buildPythonArgs(jobClient);
            System.out.println(Arrays.asList(args));
            String jobId = client.submit(args);
            return JobResult.createSuccessResult(jobId);
        } catch(Exception ex) {
            LOG.info("", ex);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(ex));
        }
    }

    @Override
    public EngineResourceInfo getAvailSlots() {
        LearningResourceInfo resourceInfo = new LearningResourceInfo();
        try {
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> acceptedApps = client.getYarnClient().getApplications(enumSet);
            if (acceptedApps.size() > conf.getInt(LearningConfiguration.DT_APP_YARN_ACCEPTER_TASK_NUMBER,1)){
                LOG.warn("yarn 资源不足，任务等待提交");
                return resourceInfo;
            }
            List<NodeReport> nodeReports = client.getNodeReports();
            float capacity = 1;
            if (!conf.getBoolean(LearningConfiguration.DT_APP_ELASTIC_CAPACITY, true)){
                capacity = getQueueRemainCapacity(1,client.getYarnClient().getRootQueueInfos());
            }
            resourceInfo.setCapacity(capacity);
            for(NodeReport report : nodeReports){
                Resource capability = report.getCapability();
                Resource used = report.getUsed();
                int totalMem = capability.getMemory();
                int totalCores = capability.getVirtualCores();
                int usedMem = used.getMemory();
                int usedCores = used.getVirtualCores();

                int freeCores = totalCores - usedCores;
                int freeMem = totalMem - usedMem;

                resourceInfo.addNodeResource(new EngineResourceInfo.NodeResourceDetail(report.getNodeId().toString(), totalCores,usedCores,freeCores, totalMem,usedMem,freeMem));
            }
        } catch (Exception e) {
            LOG.error("", e);
        }

        return resourceInfo;
    }

    private float getQueueRemainCapacity(float coefficient, List<QueueInfo> queueInfos){
        float capacity = 0;
        for (QueueInfo queueInfo : queueInfos){
            if (CollectionUtils.isNotEmpty(queueInfo.getChildQueues())) {
                float subCoefficient = queueInfo.getCapacity() * coefficient;
                capacity = getQueueRemainCapacity(subCoefficient, queueInfo.getChildQueues());
            }
            if (queueInfo.getQueueName().equals(conf.get(LearningConfiguration.LEARNING_APP_QUEUE))){
                capacity = coefficient * queueInfo.getCapacity() * (1 - queueInfo.getCurrentCapacity());
            }
            if (capacity>0){
                return capacity;
            }
        }
        return capacity;
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        Map<String,Object> jobLog = new HashMap<>();
        try {
            ApplicationReport applicationReport = client.getApplicationReport(jobId);
            jobLog.put("msg_info", applicationReport.getDiagnostics());
        } catch (Exception e) {
            LOG.error("", e);
            jobLog.put("msg_info", e.getMessage());
        }
        return gson.toJson(jobLog, Map.class);
    }

}
