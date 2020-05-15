package com.dtstack.engine.common.client;

import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.config.YamlConfigParser;
import com.dtstack.engine.common.enums.EJobType;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reason:
 * Date: 2017/2/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public abstract class AbstractClient implements IClient{

    private static final Logger logger = LoggerFactory.getLogger(AbstractClient.class);
    public final static String PLUGIN_DEFAULT_CONFIG_NAME = "default-config.yaml";

    public String defaultPlugins;

    public AbstractClient() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(PLUGIN_DEFAULT_CONFIG_NAME);
            if (Objects.isNull(resourceAsStream)) {
                logger.info("plugin client default-config.yaml not exist!");
                return;
            }
            Map<String, Object> config = YamlConfigParser.INSTANCE.parse(resourceAsStream);
            defaultPlugins = PublicUtil.objToString(config);
            logger.info("======= plugin client============{}", defaultPlugins);
        } catch (Exception e) {
            logger.error("plugin client init default config error {}", e);
        }
    }

    @Override
	public JobResult submitJob(JobClient jobClient) {

        JobResult jobResult;
        try{
            beforeSubmitFunc(jobClient);
            jobResult = processSubmitJobWithType(jobClient);
            if (jobResult == null){
                jobResult = JobResult.createErrorResult("not support job type of " + jobClient.getJobType() + "," +
                        " you need to set it in(" + StringUtils.join(EJobType.values(),",") + ")");
            }
        }catch (Exception e){
            logger.error("", e);
            jobResult = JobResult.createErrorResult(e);
        }finally {
            afterSubmitFunc(jobClient);
        }

        return jobResult;
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
    public boolean judgeSlots(JobClient jobClient) {
        return false;
    }

    protected void beforeSubmitFunc(JobClient jobClient){
    }

    protected void afterSubmitFunc(JobClient jobClient){
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
    public String getDefaultPluginConfig() {
        return defaultPlugins;
    }


}
