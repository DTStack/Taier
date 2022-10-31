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

package com.dtstack.taier.datasource.plugin.yarn.core.util;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.YarnSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.http.HttpConfig;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

/**
 * yarn config util
 *
 * @author ：wangchuan
 * date：Created in 下午2:08 2022/3/15
 * company: www.dtstack.com
 */
@Slf4j
public class YarnConfUtil {

    /**
     * 初始化 yarn config, 处理 kerberos 逻辑等
     *
     * @param sourceDTO sourceDTO
     * @return YarnConfiguration
     */
    public static YarnConfiguration initYarnConfiguration(ISourceDTO sourceDTO) {
        YarnSourceDTO yarnSourceDTO = (YarnSourceDTO) sourceDTO;
        Configuration configuration = initConfiguration(yarnSourceDTO.getHadoopConf());
        return initYarnConfiguration(yarnSourceDTO.getYarnConf(), configuration);
    }

    private static YarnConfiguration initYarnConfiguration(Map<String, Object> yarnConf, Configuration configuration) {
        YarnConfiguration yarnConfiguration = new YarnConfiguration(Objects.isNull(configuration) ? new Configuration(false) : configuration);

        if (MapUtils.isNotEmpty(yarnConf)) {
            yarnConf.keySet().forEach(key -> {
                Object value = yarnConf.get(key);
                if (value instanceof String) {
                    yarnConfiguration.set(key, (String) value);
                } else if (value instanceof Boolean) {
                    yarnConfiguration.setBoolean(key, (boolean) value);
                }
            });
            yarnConf.put(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);

            // set rpc retry num
            if (yarnConf.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS) != null) {
                yarnConfiguration.set(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS, (String) yarnConf.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS));
            } else {
                yarnConfiguration.setLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_MAX_WAIT_MS, 15000L);
            }

            if (yarnConf.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS) != null) {
                yarnConfiguration.set(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, (String) yarnConf.get(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS));
            } else {
                yarnConfiguration.setLong(YarnConfiguration.RESOURCEMANAGER_CONNECT_RETRY_INTERVAL_MS, 5000L);
            }
        }

        // set yarn failoverProxyProvider
        boolean isEnable = yarnConfiguration.getBoolean("yarn.resourcemanager.ha.enabled", false);
        if (isEnable) {
            String failoverProxyProviderKey = "yarn.client.failover-proxy-provider";
            String failoverProxyProvider = yarnConfiguration.get(failoverProxyProviderKey);
            if (StringUtils.isBlank(failoverProxyProvider)) {
                failoverProxyProvider = "org.apache.hadoop.yarn.client.ConfiguredRMFailoverProxyProvider";
                yarnConfiguration.set(failoverProxyProviderKey, failoverProxyProvider);
            }
        }
        yarnConfiguration.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
        return yarnConfiguration;
    }

    private static Configuration initConfiguration(Map<String, Object> hadoopConf) {
        Configuration configuration = new Configuration(false);
        if (MapUtils.isNotEmpty(hadoopConf)) {
            // 去除压缩格式限制
            hadoopConf.remove("io.compression.codecs");
            // 去除加密
            hadoopConf.remove("dfs.encrypt.data.transfer.cipher.suites");

            hadoopConf.keySet().forEach(key -> {
                Object value = hadoopConf.get(key);
                if (value instanceof String) {
                    configuration.set(key, (String) value);
                } else if (value instanceof Boolean) {
                    configuration.setBoolean(key, (boolean) value);
                }
            });
            hadoopConf.put(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
        }

        // disable fs cache
        configuration.setBoolean("fs.hdfs.impl.disable.cache", true);
        configuration.setBoolean("fs.file.impl.disable.cache", true);

        // set hfds crypto.codec failover
        String cryptoCodec = configuration.get("hadoop.security.crypto.codec.classes.aes.ctr.nopadding");
        if (cryptoCodec == null || "".equals(cryptoCodec.trim())) {
            String failoverCryptoCodec = "org.apache.hadoop.crypto.OpensslAesCtrCryptoCodec, org.apache.hadoop.crypto.JceAesCtrCryptoCodec";
            configuration.set("hadoop.security.crypto.codec.classes.aes.ctr.nopadding", failoverCryptoCodec);
        }

        // set hfds failoverProxyProvider
        String nameServices = configuration.get("dfs.nameservices");
        if (StringUtils.isNotBlank(nameServices)) {
            String failoverProxyProviderKey = String.format("dfs.client.failover.proxy.provider.%s", nameServices);
            String failoverProxyProvider = configuration.get(failoverProxyProviderKey);
            if (StringUtils.isBlank(failoverProxyProvider)) {
                failoverProxyProvider = "org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider";
                configuration.set(failoverProxyProviderKey, failoverProxyProvider);
            }

        }
        configuration.setBoolean(CommonConfigurationKeys.IPC_CLIENT_FALLBACK_TO_SIMPLE_AUTH_ALLOWED_KEY, true);
        return configuration;
    }

    public static String getRMWebAddress(YarnClient yarnClient) {
        String rmId;
        String webAddress = "";
        try {
            Field rmClientField = yarnClient.getClass().getDeclaredField("rmClient");
            rmClientField.setAccessible(true);
            Object rmClient = rmClientField.get(yarnClient);

            Field hField = rmClient.getClass().getSuperclass().getDeclaredField("h");
            hField.setAccessible(true);
            //获取指定对象中此字段的值
            Object h = hField.get(rmClient);
            Object currentProxy = null;

            try {
                Field currentProxyField = h.getClass().getDeclaredField("currentProxy");
                currentProxyField.setAccessible(true);
                currentProxy = currentProxyField.get(h);
            } catch (Exception e) {
                //兼容Hadoop 2.7.3.2.6.4.91-3
                Field proxyDescriptorField = h.getClass().getDeclaredField("proxyDescriptor");
                proxyDescriptorField.setAccessible(true);
                Object proxyDescriptor = proxyDescriptorField.get(h);
                Field currentProxyField = proxyDescriptor.getClass().getDeclaredField("proxyInfo");
                currentProxyField.setAccessible(true);
                currentProxy = currentProxyField.get(proxyDescriptor);
            }

            Field proxyInfoField = currentProxy.getClass().getDeclaredField("proxyInfo");
            proxyInfoField.setAccessible(true);
            rmId = (String) proxyInfoField.get(currentProxy);
        } catch (Exception e) {
            log.error("get proxyDescriptor error: {}", e.getMessage(), e);
            rmId = getYarnRmIdFromConf(yarnClient.getConfig());
        }

        String policyStr = yarnClient.getConfig().get(YarnConfiguration.YARN_HTTP_POLICY_KEY);
        HttpConfig.Policy defaultPolicy = HttpConfig.Policy.HTTP_ONLY;
        HttpConfig.Policy policy = HttpConfig.Policy.fromString(policyStr);
        if (policy == null) {
            policy = defaultPolicy;
        }
        String httpAddress = null;
        String httpsAddress = null;
        switch (policy) {
            case HTTP_ONLY:
            case HTTP_AND_HTTPS:
                httpAddress = getHttpAddressFromConf(yarnClient.getConfig(), rmId);
                webAddress = String.format("http://%s", httpAddress);
                break;
            case HTTPS_ONLY:
                httpsAddress = getHttpsAddressFromConf(yarnClient.getConfig(), rmId);
                webAddress = String.format("https://%s", httpsAddress);
                break;
        }

        // 判断是否开启knox代理
        Configuration config = yarnClient.getConfig();
        String proxy = config.get("proxy");
        if (StringUtils.isNotBlank(proxy)) {
            JSONObject object = JSONObject.parseObject(proxy);
            String type = object.getString("type");
            if ("KNOX".equals(type)) {
                JSONObject proxyConfig = object.getJSONObject("config");
                webAddress = proxyConfig.getString("url");
            }
        }
        if (StringUtils.isBlank(webAddress)) {
            throw new SourceException("Couldn't get rm web app address. " +
                    "it's required " +
                    "Please check rm web address whether be confituration.");
        }
        return webAddress;
    }


    private static String getHttpAddressFromConf(Configuration yarnConf, String rmId) {
        String address = yarnConf.get(YarnConfiguration.RM_WEBAPP_ADDRESS);
        if (StringUtils.isNotBlank(rmId)) {
            String key = YarnConfiguration.RM_WEBAPP_ADDRESS + "." + rmId;
            address = yarnConf.get(key);
        }
        return address;
    }

    private static String getHttpsAddressFromConf(Configuration yarnConf, String rmId) {
        String address = yarnConf.get(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS);
        if (StringUtils.isNotBlank(rmId)) {
            String key = YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS + "." + rmId;
            address = yarnConf.get(key);
        }
        return address;
    }

    private static String getYarnRmIdFromConf(Configuration yarnConf) {
        String rmId = null;
        String rmIdsStr = yarnConf.get(YarnConfiguration.RM_HA_IDS);
        if (StringUtils.isNotEmpty(rmIdsStr)) {
            String[] rmIds = StringUtils.split(rmIdsStr, ",");
            rmId = rmIds[0];
        }
        return rmId;
    }
}
