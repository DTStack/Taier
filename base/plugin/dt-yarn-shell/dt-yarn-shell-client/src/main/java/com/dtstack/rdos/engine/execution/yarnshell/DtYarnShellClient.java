package com.dtstack.rdos.engine.execution.yarnshell;

import com.dtstack.rdos.commom.exception.ExceptionUtil;
import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.AbsClient;
import com.dtstack.rdos.engine.execution.base.JobClient;
import com.dtstack.rdos.engine.execution.base.enums.RdosTaskStatus;
import com.dtstack.rdos.engine.execution.base.pojo.EngineResourceInfo;
import com.dtstack.rdos.engine.execution.base.pojo.JobResult;
import com.dtstack.yarn.DtYarnConfiguration;
import com.dtstack.yarn.client.Client;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * dt-yarn-shell客户端
 * Date: 2018/9/14
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class DtYarnShellClient extends AbsClient {

    private static final Logger LOG = LoggerFactory.getLogger(DtYarnShellClient.class);

    private static final Gson gson = new Gson();

    private Client client;


    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("DtYarnShellClient init ...");
        DtYarnConfiguration conf = new DtYarnConfiguration();
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
            } else {
                conf.set(key, value.toString());
            }
        }
        client = new Client(conf);
    }

    @Override
    public JobResult cancelJob(String jobId) {
        try {
            client.kill(jobId);
            return JobResult.createSuccessResult(jobId);
        } catch (Exception e) {
            LOG.error("", e);
            return JobResult.createErrorResult(e.getMessage());
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(String jobId) throws IOException {
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

    @Override
    public JobResult submitPythonJob(JobClient jobClient){
        LOG.info("LearningClient.submitPythonJob");
        try {
            String[] args = DtYarnShellUtil.buildPythonArgs(jobClient);
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
        DtYarnShellResourceInfo resourceInfo = new DtYarnShellResourceInfo();
        try {
            List<NodeReport> nodeReports = client.getNodeReports();
            for(NodeReport report : nodeReports){
                Resource capability = report.getCapability();
                Resource used = report.getUsed();
                int totalMem = capability.getMemory();
                int totalCores = capability.getVirtualCores();

                int usedMem = used.getMemory();
                int usedCores = used.getVirtualCores();

                Map<String, Object> workerInfo = Maps.newHashMap();
                workerInfo.put(DtYarnShellResourceInfo.CORE_TOTAL_KEY, totalCores);
                workerInfo.put(DtYarnShellResourceInfo.CORE_USED_KEY, usedCores);
                workerInfo.put(DtYarnShellResourceInfo.CORE_FREE_KEY, totalCores - usedCores);

                workerInfo.put(DtYarnShellResourceInfo.MEMORY_TOTAL_KEY, totalMem);
                workerInfo.put(DtYarnShellResourceInfo.MEMORY_USED_KEY, usedMem);
                workerInfo.put(DtYarnShellResourceInfo.MEMORY_FREE_KEY, totalMem - usedMem);

                resourceInfo.addNodeResource(report.getNodeId().toString(), workerInfo);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }

        return resourceInfo;
    }

    @Override
    public String getJobLog(String jobId) {
        try {
            ApplicationReport applicationReport = client.getApplicationReport(jobId);
            Map<String,Object> jobLog = new HashMap<>();
            jobLog.put("msgInfo", applicationReport.getDiagnostics());
            return gson.toJson(jobLog, Map.class);
        } catch (Exception e) {
            LOG.error("", e);
            return e.getMessage();
        }
    }
}
