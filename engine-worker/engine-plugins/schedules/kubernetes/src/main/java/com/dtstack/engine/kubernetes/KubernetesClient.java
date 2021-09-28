/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dtstack.engine.kubernetes;

import com.dtstack.engine.pluginapi.pojo.ComponentTestResult;
import com.dtstack.engine.pluginapi.JobClient;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.client.AbstractClient;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import com.dtstack.engine.pluginapi.exception.RdosDefineException;
import com.dtstack.engine.pluginapi.pojo.JobResult;
import com.dtstack.engine.pluginapi.util.PublicUtil;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
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
        ConfigMap configMap = null;
        try {
            Map<String, Object> conf = allConfig.getKubernetesConf();
            String kubernetesConf = (String) conf.get("kubernetesConf");
            io.fabric8.kubernetes.client.Config kubernetes = io.fabric8.kubernetes.client.Config.fromKubeconfig(kubernetesConf);
            client = new DefaultKubernetesClient(kubernetes);
            client.getVersion();
            testResult.setResult(true);
            String namespace = allConfig.getNamespace();
            if (StringUtils.isNotBlank(namespace)) {
                //新建集群的时候测试联通性 没有namespace 绑定租户的时候含有namespace 需要区分一下
                configMap = testKubernetesNamespace(client, namespace);
            }

        } catch (Exception e) {
            LOG.error("test error {}", allConfig.getNamespace(), e);
            testResult.setResult(false);
            testResult.setErrorMsg(ExceptionUtil.getErrorMessage(e));
            if (StringUtils.isNotBlank(allConfig.getNamespace())
                    && e.getMessage().contains(allConfig.getNamespace())
                    && e.getMessage().contains("not found")) {
                testResult.setErrorMsg("namespace不存在或者无权限");
            } else if (e.getMessage().contains("not match")) {
                testResult.setErrorMsg("namespace不存在或者无权限");
            } else if (e.getMessage().contains("doesn't have permission")) {
                testResult.setErrorMsg("namespace不存在或者无权限");
            }
        } finally {
            if (Objects.nonNull(client)) {
                if (null != configMap) {
                    try {
                        client.configMaps().delete(configMap);
                    } catch (Exception e) {
                        LOG.error("delete namespace {} config error", allConfig.getNamespace(), e);
                    }
                }
                client.close();
            }
        }
        return testResult;
    }


    private ConfigMap testKubernetesNamespace(io.fabric8.kubernetes.client.KubernetesClient client, String namespace) {
        ObjectMeta meta = new ObjectMetaBuilder()
                .withNamespace(namespace)
                .withName("test-configmap")
                .build();
        Map<String, String> data = new HashMap<>();
        data.put("test-key1", "test1");
        data.put("test-key2", "test2");
        ConfigMap configMap = new ConfigMap();
        configMap.setApiVersion(client.getApiVersion());
        configMap.setMetadata(meta);
        configMap.setData(data);
        client.configMaps().create(configMap);
        return configMap;
    }

}
