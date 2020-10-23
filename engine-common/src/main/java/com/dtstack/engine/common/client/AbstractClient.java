package com.dtstack.engine.common.client;

import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.api.pojo.ClusterResource;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Objects;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public abstract class AbstractClient implements IClient {

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);

    public final static String PLUGIN_DEFAULT_CONFIG_NAME = "default-config.yaml";

    public final static String COMPONENT_TYPE = "componentType";

    public List<ClientTemplate> defaultPlugins;

    public AbstractClient() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            String configYaml = findPluginConfig(this.getClass(), PLUGIN_DEFAULT_CONFIG_NAME);
            InputStream resourceAsStream = !StringUtils.isEmpty(configYaml) ? new FileInputStream(configYaml) :
                    this.getClass().getClassLoader().getResourceAsStream(PLUGIN_DEFAULT_CONFIG_NAME);
            if (null == resourceAsStream) {
                logger.info("plugin client default-config.yaml not exist!");
                return;
            }
            defaultPlugins = new YamlConfigParser().parse(resourceAsStream);
            logger.info("======= plugin client============{}", defaultPlugins);
        } catch (Exception e) {
            logger.error("plugin client init default config error ", e);
        }
    }

    @Override
    public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try {
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null) {
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(), ",") + ")");
            }
        } catch (Exception e) {
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        } finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
    }

    @Override
    public List<String> getRollingLogBaseInfo(JobIdentifier jobIdentifier) {
        return null;
    }

    /**
     * job 处理具体实现的抽象
     *
     * @param jobClient 对象参数
     * @return 处理结果
     */
    protected abstract JobResult processSubmitJobWithType(JobClient jobClient);

    @Override
    public String getJobLog(JobIdentifier jobId) {
        return "";
    }

    @Override
    public JudgeResult judgeSlots(JobClient jobClient) {
        return JudgeResult.notOk( "");
    }

    protected void beforeSubmitFunc(JobClient jobClient) {
    }

    protected void afterSubmitFunc(JobClient jobClient) {
    }

    @Override
    public String getMessageByHttp(String path) {
        return null;
    }

    @Override
    public List<String> getContainerInfos(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public String getCheckpoints(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public List<ClientTemplate> getDefaultPluginConfig(String engineType) {
        return defaultPlugins;
    }

    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        return null;
    }

    @Override
    public List<List<Object>> executeQuery(String sql, String database) {
        return null;
    }

    @Override
    public String uploadStringToHdfs(String bytes, String hdfsPath) {
        return null;
    }

    @Override
    public ClusterResource getClusterResource() {
        return null;
    }


    protected String findPluginConfig(Class<?> clazz, String fileName) {
        URL[] urLs = ((URLClassLoader) clazz.getClassLoader()).getURLs();
        if (urLs.length > 0) {
            String jarPath = urLs[0].getPath();
            String pluginDir = jarPath.substring(0, jarPath.lastIndexOf("/"));
            String filePath = pluginDir + File.separator + fileName;
            return new File(filePath).exists() ? filePath : null;
        }
        return null;
    }

}
