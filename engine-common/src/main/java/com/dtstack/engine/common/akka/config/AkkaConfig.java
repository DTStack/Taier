package com.dtstack.engine.common.akka.config;

import com.dtstack.engine.common.akka.Master;
import com.dtstack.engine.common.akka.Worker;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.NetUtils;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;

/**
 * company: www.dtstack.com
 * author: yanxi
 * create: 2020/2/27
 */
public class AkkaConfig {

    private final static String REMOTE_PATH_TEMPLATE = "akka.tcp://%s@%s:%s/user/%s";
    private final static String MASTER_CONFIG_PREFIX = "akka.master";
    private final static String WORKER_CONFIG_PREFIX = "akka.worker";
    private static Config AKKA_CONFIG = null;

    public static void loadConfig(Config config) {
        if (config == null) {
            throw new IllegalArgumentException("unload akka conf.");
        }
        AKKA_CONFIG = config;
    }

    public static String getMasterSystemName() {
        return "MasterSystem";
    }

    public static String getMasterName() {
        String keyName = MASTER_CONFIG_PREFIX + ".masterName";
        String defaultValue = Master.class.getSimpleName();
        return getValueWithDefault(keyName, defaultValue);
    }

    public static String getMasterAddress() {
        String keyName = MASTER_CONFIG_PREFIX + ".masterAddress";
        String masterAddress = getValueWithDefault(keyName, null);
        if (StringUtils.isBlank(masterAddress)) {
            throw new IllegalArgumentException("masterAddress is null.");
        }
        return masterAddress;
    }

    public static String getMasterRemotePath() {
        String keyName = MASTER_CONFIG_PREFIX + ".masterRemotePath";
        String masterRemotePath = String.format(REMOTE_PATH_TEMPLATE, getMasterSystemName(), getAkkaHostname(), getAkkaPort(), getMasterName());
        return getValueWithDefault(keyName, masterRemotePath);
    }

    public static String getWorkerSystemName() {
        return "WorkerSystem";
    }

    public static String getWorkerName() {
        String keyName = WORKER_CONFIG_PREFIX + ".workerName";
        String workerName = Worker.class.getSimpleName();
        return getValueWithDefault(keyName, workerName);
    }

    public static String getWorkerRemotePath() {
        String keyName = WORKER_CONFIG_PREFIX + ".workerRemotePath";
        String workerRemotePath = String.format(REMOTE_PATH_TEMPLATE, getWorkerSystemName(), getAkkaHostname(), getAkkaPort(), getWorkerName());;
        return getValueWithDefault(keyName, workerRemotePath);
    }

    public static String getAkkaHostname() {
        String akkaIp = AKKA_CONFIG.getString(ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME);
        String defaultIp = AddressUtil.getOneIp();
        return getValueWithDefault(akkaIp, defaultIp);
    }

    public static int getAkkaPort() {
        int akkaPort = AKKA_CONFIG.getInt(ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT);
        if (akkaPort == 0) {
            akkaPort = 2554;
        }
        return akkaPort;
    }

    public static Config checkIpAndPort(Config config) {
        HashMap<String, Object> configMap = Maps.newHashMap();

        String hostname = config.getString(ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME);
        if (StringUtils.isBlank(hostname)) {
            hostname = AddressUtil.getOneIp();
        }
        configMap.put(ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME, hostname);

        int port = config.getInt(ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT);
        int endPort = port + 100;
        port = NetUtils.getAvailablePortRange(hostname, port, endPort);
        configMap.put(ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT, port);

        Config loadConfig = ConfigFactory.parseMap(configMap).withFallback(config);
        loadConfig(loadConfig);
        return loadConfig;
    }

    public static String getValueWithDefault(String configKey, String defaultValue){
        String configValue = null;
        if (AKKA_CONFIG.hasPath(configKey)){
            configValue = AKKA_CONFIG.getString(configKey);
        }
        if (StringUtils.isBlank(configValue)) {
            return defaultValue;
        } else {
            return configKey;
        }
    }

}
