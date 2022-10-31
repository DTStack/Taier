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

package com.dtstack.taier.datasource.plugin.kubernetes;

import com.dtstack.taier.datasource.plugin.common.nosql.AbsNoSqlClient;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KubernetesSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * kubernetes client
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Slf4j
public class KubernetesClient extends AbsNoSqlClient {

    @Override
    public Boolean testCon(ISourceDTO source) {
        KubernetesSourceDTO kubernetesSourceDTO = (KubernetesSourceDTO) source;
        io.fabric8.kubernetes.client.KubernetesClient client = null;
        ConfigMap configMap = null;
        try {
            String kubernetesConf = kubernetesSourceDTO.getKubernetesConf();
            io.fabric8.kubernetes.client.Config kubernetes = io.fabric8.kubernetes.client.Config.fromKubeconfig(kubernetesConf);
            client = new DefaultKubernetesClient(kubernetes);
            client.getVersion();
            String namespace = kubernetesSourceDTO.getNamespace();
            if (StringUtils.isNotBlank(namespace)) {
                //新建集群的时候测试联通性 没有namespace 绑定租户的时候含有namespace 需要区分一下
                configMap = testKubernetesNamespace(client, namespace);
            }
        } catch (Exception e) {
            log.error("test error {}", kubernetesSourceDTO.getNamespace(), e);
            if (StringUtils.isNotBlank(kubernetesSourceDTO.getNamespace())
                    && e.getMessage().contains(kubernetesSourceDTO.getNamespace())
                    && e.getMessage().contains("not found")) {
                throw new SourceException(String.format("namespace [%s] not found", kubernetesSourceDTO.getNamespace()), e);
            } else if (e.getMessage().contains("not match") || e.getMessage().contains("doesn't have permission")) {
                throw new SourceException(String.format("namespace [%s] not found or doesn't hive permission", kubernetesSourceDTO.getNamespace()), e);
            } else {
                throw new SourceException(String.format("test kubernetes connection error: %s", e.getMessage()), e);
            }
        } finally {
            if (Objects.nonNull(client)) {
                if (null != configMap) {
                    try {
                        client.configMaps().delete(configMap);
                    } catch (Exception e) {
                        log.error("delete namespace {} config error", kubernetesSourceDTO.getNamespace(), e);
                    }
                }
                client.close();
            }
        }
        return true;
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