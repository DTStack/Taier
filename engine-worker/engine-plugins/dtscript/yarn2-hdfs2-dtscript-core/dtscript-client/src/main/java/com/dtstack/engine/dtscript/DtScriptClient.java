package com.dtstack.engine.dtscript;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.monitor.AcceptedApplicationMonitor;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.dtscript.client.Client;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * dt-yarn-shell客户端
 * Date: 2018/9/14
 * Company: www.dtstack.com
 *
 * @author jingzhen
 */
public class DtScriptClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(DtScriptClient.class);

    private static final String YARN_RM_WEB_KEY_PREFIX = "yarn.resourcemanager.webapp.address.";

    private static final String APP_URL_FORMAT = "http://%s";

    private static final Gson GSON = new Gson();

    private Client client;

    private DtYarnConfiguration conf = new DtYarnConfiguration();

    private BaseConfig configMap;

    private List<String> removeConf = Lists.newArrayList("sftpConf", "hiveConf");


    @Override
    public void init(Properties prop) throws Exception {

        LOG.info("DtScriptClient init ...");

        conf.set("fs.hdfs.impl.disable.cache", "true");
        conf.set("fs.hdfs.impl", DistributedFileSystem.class.getName());
        conf.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);

        String propStr = PublicUtil.objToString(prop);
        configMap = PublicUtil.jsonStrToObject(propStr, BaseConfig.class);
        //其中有sftp 的配置 和hadoop yarn hdfs配置

        Enumeration enumeration =  prop.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (removeConf.contains(key)) {
                continue;
            }
            Object value = prop.get(key);
            if (value instanceof String) {
                conf.set(key, (String) value);
            } else if (value instanceof Integer) {
                conf.setInt(key, (Integer) value);
            } else if (value instanceof Float) {
                conf.setFloat(key, (Float) value);
            } else if (value instanceof Double) {
                conf.setDouble(key, (Double) value);
            } else if (value instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) value;
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    conf.set(entry.getKey(), MapUtils.getString(map, entry.getKey()));
                }
            } else {
                conf.set(key, value.toString());
            }
        }

        String queue = prop.getProperty(DtYarnConfiguration.DT_APP_QUEUE);
        if (StringUtils.isNotBlank(queue)) {
            LOG.warn("curr queue is {}", queue);
            conf.set(DtYarnConfiguration.DT_APP_QUEUE, queue);
        }

        client = new Client(conf, configMap);

        if (conf.getBoolean("monitorAcceptedApp", false)) {
            AcceptedApplicationMonitor.start(conf, queue, configMap);
        }
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
        try {
            return KerberosUtils.login(configMap, ()->{
                String jobId = jobIdentifier.getEngineJobId();
                try {
                    client.kill(jobId);
                    return JobResult.createSuccessResult(jobId);
                } catch (Exception e) {
                    LOG.error("", e);
                    return JobResult.createErrorResult(e.getMessage());
                }
            }, conf);
        } catch (Exception e) {
            LOG.error("cancelJob error:", e);
            return JobResult.createErrorResult(e);
        }
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        String jobId = jobIdentifier.getEngineJobId();

        if(org.apache.commons.lang3.StringUtils.isEmpty(jobId)){
            return null;
        }

        try {
            return KerberosUtils.login(configMap, () -> {
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
                } catch (Exception e1) {
                    LOG.error("", e1);
                    return RdosTaskStatus.NOTFOUND;
                }
            },conf);
        } catch (Exception e) {
            LOG.error("", e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        YarnClient  yarnClient = client.getYarnClient();
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
            String addr = conf.get(key);

            if(addr == null) {
                addr = conf.get("yarn.resourcemanager.webapp.address");
            }

            url = String.format(APP_URL_FORMAT, addr);
        }catch (Exception e){
            LOG.error("Getting URL failed" + e);
        }

        LOG.info("get req url=" + url);
        return url;
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    private JobResult submitPythonJob(JobClient jobClient) {
        try {
            return KerberosUtils.login(configMap, () -> {
                try {
                    String[] args = DtScriptUtil.buildPythonArgs(jobClient);
                    System.out.println(Arrays.asList(args));
                    String jobId = client.submit(args);
                    return JobResult.createSuccessResult(jobId);
                } catch (Exception e) {
                    LOG.info("", e);
                    return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(e));
                }
            },conf);
        } catch (Exception e) {
            LOG.info("", e);
            return JobResult.createErrorResult("submit job get unknown error\n" + ExceptionUtil.getErrorMessage(e));
        }
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        try {
            return KerberosUtils.login(configMap, () -> {
                try {
                    DtScriptResourceInfo resourceInfo = DtScriptResourceInfo.DtScriptResourceInfoBuilder()
                            .withYarnClient(client.getYarnClient())
                            .withQueueName(conf.get(DtYarnConfiguration.DT_APP_QUEUE))
                            .withYarnAccepterTaskNumber(conf.getInt(DtYarnConfiguration.DT_APP_YARN_ACCEPTER_TASK_NUMBER,1))
                            .build();
                    return resourceInfo.judgeSlots(jobClient);
                } catch (Exception e) {
                    LOG.error("", e);
                    return JudgeResult.notOk("judgeSlots error");
                }
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            throw new RdosDefineException("JudgeSlots error " + e.getMessage());
        }
    }

    @Override
    public String getJobLog(JobIdentifier jobIdentifier) {
        try {
            return KerberosUtils.login(configMap, ()-> {
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
            }, conf);
        } catch (Exception e) {
            LOG.error("", e);
            Map<String,Object> jobLog = new HashMap<>();
            jobLog.put("msg_info", e.getMessage());
            return GSON.toJson(jobLog, Map.class);
        }
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {

        String jobId = jobIdentifier.getEngineJobId();
        try {
            return client.getContainerInfos(jobId);
        } catch (Exception e) {
            LOG.error("", e);
            return null;
        }
    }

}
