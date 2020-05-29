package com.dtstack.engine.learning;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.learning.conf.LearningConfiguration;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.util.MathUtil;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.learning.client.Client;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * xlearning客户端
 * Date: 2018/6/22
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class LearningClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(LearningClient.class);

    private Client client;

    private LearningConfiguration conf = new LearningConfiguration();

    private static final Gson GSON = new Gson();

    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("LearningClien.init ...");
        conf.set("fs.hdfs.impl.disable.cache", "true");
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());

        Boolean useLocalEnv = MathUtil.getBoolean(prop.get("use.local.env"), false);
        if(useLocalEnv){
            //从本地环境变量读取
            String hadoopConfDir = System.getenv("HADOOP_CONF_DIR");
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
                    conf.set(entry.getKey(), MapUtils.getString(map,entry.getKey()));
                }
            } else {
                conf.set(key, value.toString());
            }
        }

        String queue = prop.getProperty(LearningConfiguration.XLEARNING_APP_QUEUE);
        if (StringUtils.isNotBlank(queue)){
            conf.set(LearningConfiguration.XLEARNING_APP_QUEUE, queue);
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
                    throw new RdosDefineException("Unsupported application state");
            }
        } catch (YarnException e) {
            LOG.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        throw new RdosDefineException("learning client not support method 'getJobMaster'");
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        LearningResourceInfo resourceInfo = new LearningResourceInfo();
        try {
            resourceInfo.getYarnSlots(client.getYarnClient(), conf.get(LearningConfiguration.XLEARNING_APP_QUEUE), conf.getInt(LearningResourceInfo.DT_APP_YARN_ACCEPTER_TASK_NUMBER,1));
            return resourceInfo.judgeSlots(jobClient);
        } catch (YarnException e) {
            LOG.error("", e);
            return false;
        }
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
        return GSON.toJson(jobLog, Map.class);
    }

}
