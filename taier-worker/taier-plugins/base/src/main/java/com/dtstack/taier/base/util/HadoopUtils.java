package com.dtstack.taier.base.util;

import com.dtstack.taier.base.BaseConfig;
import com.dtstack.taier.pluginapi.exception.PluginDefineException;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.http.HttpConfig;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Date: 2021/09/13
 * Company: www.dtstack.com
 *
 * @author xiuzhu
 */
public class HadoopUtils {

    private static final Logger LOG = LoggerFactory.getLogger(HadoopUtils.class);

    public static String getHadoopUserName(BaseConfig config) {
        String hadoopUserName = config.getHadoopUserName();
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = config.getDtProxyUserName();
        }
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = System.getenv("HADOOP_USER_NAME");
        }
        if (StringUtils.isBlank(hadoopUserName)) {
            hadoopUserName = System.getProperty("HADOOP_USER_NAME");
        }
        Preconditions.checkNotNull(hadoopUserName, "hadoopUserName is null");
        return hadoopUserName;
    }

    public static Configuration initConfiguration(BaseConfig baseConfig, Map<String, Object> hadoopConf) {
        return initConfiguration(baseConfig, hadoopConf, false);
    }

    public static Configuration initConfiguration(BaseConfig baseConfig, Map<String, Object> hadoopConf, boolean loadDefaults) {
        Configuration configuration = new Configuration(loadDefaults);

        if (MapUtils.isNotEmpty(hadoopConf)) {
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

    public static YarnConfiguration initYarnConfiguration(BaseConfig baseConfig, Map<String, Object> hadoopConf, Map<String, Object> yarnConf) {
        return initYarnConfiguration(baseConfig, hadoopConf, yarnConf, false);
    }

    public static YarnConfiguration initYarnConfiguration(BaseConfig baseConfig, Map<String, Object> hadoopConf, Map<String, Object> yarnConf, boolean loadDefaults) {
        Configuration configuration = initConfiguration(baseConfig, hadoopConf, loadDefaults);
        YarnConfiguration yarnConfiguration = new YarnConfiguration(configuration);

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


    public static String getRMWebAddress(YarnConfiguration yarnConf, YarnClient yarnClient) {
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
            LOG.error("get proxyDescriptor error: {}", e);
            rmId = getYarnRmIdFromConf(yarnConf);
        }

        String policyStr = yarnConf.get(YarnConfiguration.YARN_HTTP_POLICY_KEY);
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
                httpAddress = getHttpAddressFromConf(yarnConf, rmId);
                webAddress = String.format("http://%s", httpAddress);
                break;
            case HTTPS_ONLY:
                httpsAddress = getHttpsAddressFromConf(yarnConf, rmId);
                webAddress = String.format("https://%s", httpsAddress);
                break;
        }

        if (StringUtils.isBlank(webAddress)) {
            throw new PluginDefineException("Couldn't get rm web app address. " +
                    "it's required " +
                    "Please check rm web address whether be confituration.");
        }
        return webAddress;
    }

    private static String getHttpAddressFromConf(YarnConfiguration yarnConf, String rmId) {
        String address = yarnConf.get(YarnConfiguration.RM_WEBAPP_ADDRESS);
        if (StringUtils.isNotBlank(rmId)) {
            String key = YarnConfiguration.RM_WEBAPP_ADDRESS + "." + rmId;
            address = yarnConf.get(key);
        }
        return address;
    }

    private static String getHttpsAddressFromConf(YarnConfiguration yarnConf, String rmId) {
        String address = yarnConf.get(YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS);
        if (StringUtils.isNotBlank(rmId)) {
            String key = YarnConfiguration.RM_WEBAPP_HTTPS_ADDRESS + "." + rmId;
            address = yarnConf.get(key);
        }
        return address;
    }

    private static String getYarnRmIdFromConf(YarnConfiguration yarnConf) {
        String rmId = null;
        String rmIdsStr = yarnConf.get(YarnConfiguration.RM_HA_IDS);
        if (StringUtils.isNotEmpty(rmIdsStr)) {
            String[] rmIds = StringUtils.split(rmIdsStr, ",");
            rmId = rmIds[0];
        }
        return rmId;
    }
}
