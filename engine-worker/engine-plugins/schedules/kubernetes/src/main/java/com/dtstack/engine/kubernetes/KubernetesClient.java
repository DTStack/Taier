package com.dtstack.engine.kubernetes;

import com.dtstack.engine.api.pojo.ComponentTestResult;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.client.AbstractClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ExceptionUtil;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.util.PublicUtil;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author yuebai
 * @date 2020-05-27
 */
public class KubernetesClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(KubernetesClient.class);

    @Override
    protected JobResult processSubmitJobWithType(JobClient jobClient) {
        return null;
    }

    @Override
    public void init(Properties prop) throws Exception {

    }

    @Override
    public JobResult cancelJob(JobIdentifier jobIdentifier) {
        return null;
    }

    @Override
    public RdosTaskStatus getJobStatus(JobIdentifier jobIdentifier) throws IOException {
        return null;
    }

    @Override
    public String getJobMaster(JobIdentifier jobIdentifier) {
        return null;
    }

    /**
     * 测试hdfs 和 k8s的联通性
     * @param pluginInfo
     * @return
     */
    @Override
    public ComponentTestResult testConnect(String pluginInfo) {
        ComponentTestResult testResult = new ComponentTestResult();
        testResult.setResult(false);
        try {
            Config allConfig = PublicUtil.jsonStrToObject(pluginInfo, Config.class);
            try {
                return testKubernetesConnect(testResult, allConfig);
            } catch (Exception e) {
                throw new RdosDefineException(e);
            }
        } catch (Exception e) {
            LOG.error("test k8s connect error", e);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
        }
        return testResult;
    }

    private ComponentTestResult testKubernetesConnect(ComponentTestResult testResult, Config allConfig) throws Exception {
        io.fabric8.kubernetes.client.KubernetesClient client = null;
        try {
            Map<String, Object> conf = allConfig.getKubernetesConf();
            String kubernetesConf = (String) conf.get("kubernetesConf");
            io.fabric8.kubernetes.client.Config kubernetes = io.fabric8.kubernetes.client.Config.fromKubeconfig(kubernetesConf);
            client = new DefaultKubernetesClient(kubernetes);
            client.getVersion();
            testResult.setResult(true);
        } finally {
            if (Objects.nonNull(client)) {
                client.close();
            }
        }
        return testResult;
    }
}
