package com.dtstack.engine.dummy;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import com.dtstack.engine.common.util.SFTPHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 用于流程上压测的dummy插件
 * <p>
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/4/13
 */
public class DummyClient extends AbstractClient {

    private Map<String, List<ClientTemplate>> defaultConfigs = new HashMap();

    private static final Logger logger = LoggerFactory.getLogger(DummyClient.class);

    private static Map<String,String> commonConfigFiles = new HashMap<>();

    static {
        commonConfigFiles.put("sftp","sftp-config.yml");
    }
    @Override
    public void init(Properties prop) throws Exception {
    }

    @Override
    public List<ClientTemplate> getDefaultPluginConfig(String componentType) {
        return defaultConfigs.get(componentType);
    }

    public DummyClient() {
        for (String componentType : commonConfigFiles.keySet()) {
            try {
                String configYaml = findPluginConfig(this.getClass(), commonConfigFiles.get(componentType));
                InputStream resourceAsStream = !StringUtils.isEmpty(configYaml) ? new FileInputStream(configYaml) :
                        this.getClass().getClassLoader().getResourceAsStream(commonConfigFiles.get(componentType));
                defaultPlugins = new YamlConfigParser().parse(resourceAsStream);
                logger.info("=======DummyClient============{}", defaultPlugins);
                defaultConfigs.put(componentType,defaultPlugins);
            } catch (Exception e) {
                logger.error("dummy client init default config error ", e);
            }
        }

    }

    @Override
    public String getJobLog(JobIdentifier jobId) {
        Map<String, Object> jobLog = new HashMap<>(2);
        jobLog.put("jobId", jobId.getTaskId());
        jobLog.put("msg_info", System.currentTimeMillis());
        return JSONObject.toJSONString(jobLog);
    }

    @Override
    public boolean judgeSlots(JobClient jobClient) {
        return true;
    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return JobResult.createSuccessResult(jobIdentifier.getTaskId(), jobIdentifier.getEngineJobId());
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return RdosTaskStatus.FINISHED;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return StringUtils.EMPTY;
    }

    @Override
    public String getMessageByHttp(String path) {
        return StringUtils.EMPTY;
    }

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return JobResult.createSuccessResult(jobClient.getTaskId(), jobClient.getTaskId());
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult componentTestResult = new ComponentTestResult();
        try {
            Map config = PublicUtil.jsonStrToObject(pluginInfo, Map.class);
            if ("sftp".equalsIgnoreCase(String.valueOf(config.get(COMPONENT_TYPE)))) {
                SFTPHandler instance = null;
                try {
                    instance = SFTPHandler.getInstance(config);
                    String path = (String) config.get("path");
                    if (StringUtils.isBlank(path)) {
                        componentTestResult.setErrorMsg("SFTP组件path配置不能为空");
                        componentTestResult.setResult(false);
                    } else {
                        //测试路径是否存在
                        instance.listFile(path);
                        componentTestResult.setResult(true);
                    }
                } finally {
                    if (instance != null) {
                        instance.close();
                    }
                }
            }
        } catch (Exception e) {
            componentTestResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            componentTestResult.setResult(false);
        }
        return componentTestResult;
    }
}
