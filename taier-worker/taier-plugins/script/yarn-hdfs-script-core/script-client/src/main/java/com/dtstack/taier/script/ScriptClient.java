package com.dtstack.taier.script;

import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.base.exception.EnginePluginsBaseException;
import com.dtstack.taier.base.monitor.AcceptedApplicationMonitor;
import com.dtstack.taier.base.util.HadoopUtils;
import com.dtstack.taier.base.util.KerberosUtils;
import com.dtstack.taier.script.client.Client;
import com.dtstack.taier.pluginapi.JobClient;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.client.AbstractClient;
import com.dtstack.taier.pluginapi.enums.EJobType;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.pluginapi.pojo.JobResult;
import com.dtstack.taier.pluginapi.pojo.JudgeResult;
import com.dtstack.taier.pluginapi.util.PublicUtil;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author qiuyun
 * @version 1.0
 * @date 2022-08-16 17:34
 */
public class ScriptClient extends AbstractClient {
    private static final Logger LOG = LoggerFactory.getLogger(ScriptClient.class);

    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";

    private static final String APP_URL_FORMAT = "http://%s";

    /**
     * 控制台集群原生配置
     */
    private BaseConfig configMap;

    private Client client;

    /**
     * 控制台集群原生 yarn/hdfs 配置
     */
    private YarnConfiguration yarnconf = new YarnConfiguration();

    /**
     * 控制台集群原生 script 配置信息
     */
    private ScriptConfiguration dtconf = new ScriptConfiguration(false);

    private static final Gson GSON = new Gson();

    @Override
    public void init(Properties prop) throws Exception {
        LOG.info("ScriptClient init ...");
        String propStr = PublicUtil.objToString(prop);
        configMap = PublicUtil.jsonStrToObject(propStr, BaseConfig.class);

        // 从配置信息里面获取 hadoopConf、yarnConf
        YarnConfiguration yarnConfiguration = HadoopUtils.initYarnConfiguration(configMap, (Map<String, Object>) prop.get("hadoopConf"), (Map<String, Object>) prop.get("yarnConf"));
        yarnconf.addResource(yarnConfiguration);

        dtconf = ScriptUtil.initScriptConfiguration(prop, new ScriptConfiguration(false));
        client = new Client(yarnconf, dtconf, configMap);
        // 是否监控 yarn accepted 状态任务
        if (dtconf.getBoolean("monitorAcceptedApp", false)) {
            AcceptedApplicationMonitor.start(yarnconf, prop.getProperty(ScriptConfiguration.APP_QUEUE), configMap);
        }
        LOG.info("ScriptClient init ok");
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        EJobType jobType = jobClient.getJobType();
        JobResult jobResult = null;
        // shell/python 的 jobType 都是 python
        if (!EJobType.PYTHON.equals(jobType)) {
            return jobResult;
        }
        jobResult = submitPythonJob(jobClient);
        return jobResult;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        String jobId = jobIdentifier.getEngineJobId();
        try {
            return KerberosUtils.login(configMap, ()->{
                try {
                    client.kill(jobId);
                    return JobResult.createSuccessResult(jobId);
                } catch (Exception e) {
                    LOG.error("killJob error, jobId:{}", jobId, e);
                    return JobResult.createErrorResult(e.getMessage());
                }
            }, yarnconf);
        } catch (Exception e) {
            LOG.error("cancelJob error, jobId:{}", jobId, e);
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return KerberosUtils.login(configMap, () -> {
                try {
                    ScriptResourceInfo resourceInfo = ScriptResourceInfo.scriptResourceInfoBuilder()
                            .withYarnClient(client.getYarnClient())
                            .withQueueName(dtconf.get(ScriptConfiguration.APP_QUEUE))
                            .withYarnAccepterTaskNumber(dtconf.getInt(ScriptConfiguration.APP_YARN_ACCEPTER_TASK_NUMBER,1))
                            .withScriptConf(ScriptUtil.buildScriptConf(jobClient, dtconf))
                            .build();
                    return resourceInfo.judgeSlots(jobClient);
                } catch (Exception e) {
                    LOG.error("jobId:{} judgeSlots error:", jobClient.getJobId(), e);
                    return JudgeResult.exception("judgeSlots error" + ExceptionUtil.getErrorMessage(e));
                }
            }, yarnconf);
        } catch (Exception e) {
            LOG.error("jobId:{} judgeSlots error:", jobClient.getJobId(), e);
            return JudgeResult.exception("judgeSlots error:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public TaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();
        if(StringUtils.isEmpty(jobId)){
            return null;
        }
        try {
            return KerberosUtils.login(configMap, () -> {
                try {
                    ApplicationReport report = client.getApplicationReport(jobId);
                    YarnApplicationState applicationState = report.getYarnApplicationState();
                    switch(applicationState) {
                        case KILLED:
                            return TaskStatus.KILLED;
                        case NEW:
                        case NEW_SAVING:
                            return TaskStatus.CREATED;
                        case SUBMITTED:
                            // 特殊逻辑,认为已提交到计算引擎的状态为等待资源状态
                            return TaskStatus.WAITCOMPUTE;
                        case ACCEPTED:
                            return TaskStatus.SCHEDULED;
                        case RUNNING:
                            return TaskStatus.RUNNING;
                        case FINISHED:
                            //state 为finished状态下需要兼顾判断finalStatus.
                            FinalApplicationStatus finalApplicationStatus = report.getFinalApplicationStatus();
                            if(finalApplicationStatus == FinalApplicationStatus.FAILED){
                                return TaskStatus.FAILED;
                            }else if(finalApplicationStatus == FinalApplicationStatus.SUCCEEDED){
                                return TaskStatus.FINISHED;
                            }else if(finalApplicationStatus == FinalApplicationStatus.KILLED){
                                return TaskStatus.KILLED;
                            }else{
                                return TaskStatus.RUNNING;
                            }

                        case FAILED:
                            return TaskStatus.FAILED;
                        default:
                            throw new EnginePluginsBaseException("Unsupported application state:" + applicationState);
                    }
                } catch (Exception e) {
                    LOG.error("getJobStatus error, return NOTFOUND", e);
                    return TaskStatus.NOTFOUND;
                }
            },yarnconf);
        } catch (Exception e) {
            LOG.error("getJobStatus error, return RUNNING", e);
            return TaskStatus.RUNNING;
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(configMap, ()-> {
                String engineJobId = jobIdentifier.getEngineJobId();
                Map<String, Object> jobLog = new HashMap<>(4);
                try {
                    ApplicationReport applicationReport = client.getApplicationReport(engineJobId);
                    jobLog.put("msg_info", applicationReport.getDiagnostics());
                } catch (Exception e) {
                    LOG.error("getJobLog error", e);
                    jobLog.put("msg_info", e.getMessage());
                }
                return GSON.toJson(jobLog, Map.class);
            }, yarnconf);
        } catch (Exception e) {
            LOG.error("getJobLog error", e);
            Map<String,Object> jobLog = new HashMap<>();
            jobLog.put("msg_info", e.getMessage());
            return GSON.toJson(jobLog, Map.class);
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        YarnClient yarnClient = client.getYarnClient();
        String url = "";
        try{
            //调用一次远程,防止rm切换本地没有及时切换
            yarnClient.getNodeReports();
            Field rmClientField = yarnClient.getClass().getDeclaredField("rmClient");
            rmClientField.setAccessible(true);
            Object rmClient = rmClientField.get(yarnClient);

            Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
            hField.setAccessible(true);
            //获取指定对象中此字段的值
            Object h = hField.get(rmClient);

            Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
            currentProxyField.setAccessible(true);
            Object currentProxy = currentProxyField.get(h);

            Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
            proxyInfoField.setAccessible(true);
            String proxyInfoKey = (String) proxyInfoField.get(currentProxy);

            String key = YARN_RM_WEB_KEY_PREFIX + proxyInfoKey;
            String addr = yarnconf.get(key);

            if(addr == null) {
                addr = yarnconf.get("yarn.resourcemanager.webapp.address");
            }

            url = String.format(APP_URL_FORMAT, addr);
        }catch (Exception e){
            LOG.error("Getting URL failed" + e);
        }
        LOG.info("get req url=" + url);
        return url;
    }

    private JobResult submitPythonJob(JobClient jobClient) {
        try {
            return KerberosUtils.login(configMap, () -> {
                        try {
                            ScriptConfiguration execDtconf = ScriptUtil.buildScriptConf(jobClient, dtconf);
                            String applicationId = client.submit(execDtconf);
                            return JobResult.createSuccessResult(applicationId);
                        } catch (Exception e) {
                            LOG.info("", e);
                            return JobResult.createErrorResult(
                                    "submit job get unknown error\n" + ExceptionUtil.getErrorMessage(e));
                        }
                    },
                    yarnconf);
        } catch (Exception e) {
            LOG.info("", e);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(e));
        }
    }
}