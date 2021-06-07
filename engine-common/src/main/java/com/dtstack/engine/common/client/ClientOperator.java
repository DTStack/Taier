package com.dtstack.engine.common.client;

import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.api.pojo.DtScriptAgentLabel;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.enums.EDeployMode;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.common.util.TaskParamsUtil;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Reason:
 * Date: 2018/1/11
 * Company: www.dtstack.com
 *
 * @author xuchao
 */
public class ClientOperator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOperator.class);

    private static ClientCache clientCache;

    private static ClientOperator singleton;

    private ClientOperator() {
    }

    public static ClientOperator getInstance(String pluginPath) {
        if (singleton == null) {
            synchronized (ClientOperator.class) {
                if (singleton == null) {
                    clientCache = ClientCache.getInstance(pluginPath);
                    LOGGER.info("init client operator plugin path {}",pluginPath);
                    singleton = new ClientOperator();
                }
            }
        }
        return singleton;
    }

    public RdosTaskStatus getJobStatus(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);

        String jobId = jobIdentifier.getEngineJobId();
        if (Strings.isNullOrEmpty(jobId)) {
            throw new RdosDefineException("can't get job of jobId is empty or null!");
        }

        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            Object result = client.getJobStatus(jobIdentifier);

            if (result == null) {
                return null;
            }

            return (RdosTaskStatus) result;
        } catch (Exception e) {
            LOGGER.error("getStatus happens error：{}",jobId, e);
            return RdosTaskStatus.NOTFOUND;
        }
    }

    public String getEngineMessageByHttp(String engineType, String path, String pluginInfo) {
        String message;

        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            message = client.getMessageByHttp(path);
        } catch (Exception e) {
            message = ExceptionUtil.getErrorMessage(e);
        }

        return message;
    }

    public String getEngineLog(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);

        String logInfo;
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            logInfo = client.getJobLog(jobIdentifier);
        } catch (Exception e) {
            logInfo = ExceptionUtil.getErrorMessage(e);
        }

        return logInfo;
    }

    public String getCheckpoints(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getCheckpoints(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job checkpoints:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public String getJobMaster(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getJobMaster(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job master exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public JobResult stopJob(JobClient jobClient) throws Exception {
        if(jobClient.getEngineTaskId() == null){
            return JobResult.createSuccessResult(jobClient.getTaskId());
        }
        EDeployMode eDeployMode = TaskParamsUtil.parseDeployTypeByTaskParams(jobClient.getTaskParams(), jobClient.getComputeType().getType(), jobClient.getEngineType());
        JobIdentifier jobIdentifier = new JobIdentifier(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId()
        ,jobClient.getTenantId(),jobClient.getEngineType(),eDeployMode.getType(),jobClient.getUserId(),jobClient.getPluginInfo(),jobClient.getComponentVersion());
        checkoutOperator(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);

        jobIdentifier.setTimeout(getCheckoutTimeout(jobClient));
        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.cancelJob(jobIdentifier);
    }

    public Long getCheckoutTimeout(JobClient jobClient) {
        Long timeout = ConfigConstant.DEFAULT_CHECKPOINT_TIMEOUT;
        Properties taskProps = jobClient.getConfProperties();
        if (taskProps == null || taskProps.size() == 0) {
            return timeout;
        }
        if (taskProps.containsKey(ConfigConstant.SQL_CHECKPOINT_TIMEOUT)) {
            timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.SQL_CHECKPOINT_TIMEOUT));
        } else if (taskProps.containsKey(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT)) {
            timeout = Long.valueOf(taskProps.getProperty(ConfigConstant.FLINK_CHECKPOINT_TIMEOUT));
        }
        return timeout;
    }

    public List<String> containerInfos(JobClient jobClient) throws Exception {
        JobIdentifier jobIdentifier = JobIdentifier.createInstance(jobClient.getEngineTaskId(), jobClient.getApplicationId(), jobClient.getTaskId());
        checkoutOperator(jobClient.getEngineType(), jobClient.getPluginInfo(), jobIdentifier);
        IClient client = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return client.getContainerInfos(jobIdentifier);
    }

    private void checkoutOperator(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        if (null == engineType || null == pluginInfo || null == jobIdentifier) {
            throw new IllegalArgumentException("engineType|pluginInfo|jobIdentifier is null.");
        }
    }

    public JudgeResult judgeSlots(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.judgeSlots(jobClient);
    }

    public JobResult submitJob(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.submitJob(jobClient);
    }

    public ComponentTestResult testConnect(String engineType, String pluginInfo){
        IClient clusterClient = clientCache.getDefaultPlugin(engineType);
        return clusterClient.testConnect(pluginInfo);
    }

    public List<List<Object>> executeQuery(String engineType, String pluginInfo, String sql, String database) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.executeQuery(sql, database);
    }

    public String uploadStringToHdfs(String engineType, String pluginInfo, String bytes, String hdfsPath) throws Exception {
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.uploadStringToHdfs(bytes, hdfsPath);
    }

    public ClusterResource getClusterResource(String engineType, String pluginInfo) throws ClientAccessException{
        IClient client = clientCache.getClient(engineType, pluginInfo);
        return client.getClusterResource();
    }

    public List<String> getRollingLogBaseInfo(String engineType, String pluginInfo, JobIdentifier jobIdentifier) {
        checkoutOperator(engineType, pluginInfo, jobIdentifier);
        try {
            IClient client = clientCache.getClient(engineType, pluginInfo);
            return client.getRollingLogBaseInfo(jobIdentifier);
        } catch (Exception e) {
            throw new RdosDefineException("get job rollingLogBaseInfo:" + jobIdentifier.getEngineJobId() + " exception:" + ExceptionUtil.getErrorMessage(e));
        }
    }

    public CheckResult grammarCheck(JobClient jobClient) throws ClientAccessException {
        IClient clusterClient = clientCache.getClient(jobClient.getEngineType(), jobClient.getPluginInfo());
        return clusterClient.grammarCheck(jobClient);
    }

    public List<DtScriptAgentLabel> getDtScriptAgentLabel(String engineType,String pluginInfo) {
        IClient client = clientCache.getDefaultPlugin(engineType);
        return client.getDtScriptAgentLabel(pluginInfo);
    }
}
