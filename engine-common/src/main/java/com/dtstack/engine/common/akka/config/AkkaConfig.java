package com.dtstack.engine.common.akka.config;

import akka.actor.ActorSystem;
import com.dtstack.engine.common.akka.Master;
import com.dtstack.engine.common.akka.Worker;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.util.AddressUtil;
import com.dtstack.engine.common.util.NetUtils;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * company: www.dtstack.com
 * author: yanxi
 * create: 2020/2/27
 */
public class AkkaConfig {

    private final static String LOCAL_PATH_TEMPLATE = "akka://%s/user/%s";
    private final static String REMOTE_PATH_TEMPLATE = "akka.tcp://%s@%s:%s/user/%s";
    private static Config AKKA_CONFIG = null;
    private static boolean LOCAL_MODE = false;
    private static ActorSystem actorSystem;

    private static void loadConfig(Config config) {
        if (config == null) {
            throw new IllegalArgumentException("unload akka conf.");
        }
        AKKA_CONFIG = config;
    }

    public static synchronized ActorSystem initActorSystem(Config config) {
        if (actorSystem == null) {
            actorSystem = ActorSystem.create(getDagScheduleXSystemName(), config);
        }
        return actorSystem;
    }

    public static String getDagScheduleXSystemName() {
        return ConfigConstant.AKKA_DAGSCHEDULEX_SYSTEM;
    }

    public static String getMasterName() {
        return Master.class.getSimpleName();
    }

    public static String getMasterAddress() {
        if (LOCAL_MODE) {
            return StringUtils.EMPTY;
        } else {
            String keyName = ConfigConstant.AKKA_MASTER_MASTERADDRESS;
            String masterAddress = getValueWithDefault(keyName, StringUtils.EMPTY);
            if (StringUtils.isBlank(masterAddress)) {
                throw new IllegalArgumentException(keyName + " is null.");
            }
            return masterAddress;
        }
    }

    public static String getMasterPath(String hostName, String port) {
        String masterPath;
        if (LOCAL_MODE) {
            masterPath = String.format(LOCAL_PATH_TEMPLATE, getDagScheduleXSystemName(), getMasterName());
        } else {
            masterPath = String.format(REMOTE_PATH_TEMPLATE, getDagScheduleXSystemName(), hostName, port, getMasterName());
        }
        return masterPath;
    }

    public static String getWorkerName() {
        return Worker.class.getSimpleName();
    }

    public static String getWorkerPath() {
        String workerPath;
        if (LOCAL_MODE) {
            workerPath = String.format(LOCAL_PATH_TEMPLATE, getDagScheduleXSystemName(), getWorkerName());
        } else {
            workerPath = String.format(REMOTE_PATH_TEMPLATE, getDagScheduleXSystemName(), getAkkaHostname(), getAkkaPort(), getWorkerName());
        }
        return workerPath;
    }

    public static String getAkkaHostname() {
        if (LOCAL_MODE) {
            return "localhost";
        } else {
            String keyName = ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME;
            String defaultIp = AddressUtil.getOneIp();
            return getValueWithDefault(keyName, defaultIp);
        }
    }

    public static int getAkkaPort() {
        if (LOCAL_MODE) {
            return 0;
        } else {
            String keyName = ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT;
            return Integer.valueOf(getValueWithDefault(keyName, "2555"));
        }
    }

    public static Long getAkkaAskTimeout() {
        String keyName = ConfigConstant.AKKA_ASK_TIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "120"));
    }

    public static Long getAkkaAskResultTimeout() {
        String keyName = ConfigConstant.AKKA_ASK_RESULTTIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "90"));
    }

    public static Long getAkkaAskSubmitTimeout() {
        String keyName = ConfigConstant.AKKA_ASK_SUMBIT_TIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "180"));
    }

    public static Config init(Config config) {
        if (config.hasPath(ConfigConstant.AKKA_LOCALMODE)) {
            String localMode = config.getString(ConfigConstant.AKKA_LOCALMODE);
            LOCAL_MODE = BooleanUtils.toBoolean(localMode);
        }


        HashMap<String, Object> configMap = Maps.newHashMap();
        if (isLocalMode()) {
            configMap.put(ConfigConstant.AKKA_ACTOR_PROVIDER, "akka.actor.LocalActorRefProvider");
        }


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

    private static String getValueWithDefault(String configKey, String defaultValue) {
        String configValue = null;
        if (AKKA_CONFIG.hasPath(configKey)) {
            configValue = AKKA_CONFIG.getString(configKey);
        }
        if (null == configValue) {
            return defaultValue;
        } else {
            return configValue;
        }
    }

    public static long getSystemResourceProbeInterval() {
        String keyName = ConfigConstant.AKKA_WORKER_SYSTEMRESOURCE_PROBE_INTERVAL;
        return Integer.valueOf(getValueWithDefault(keyName, "5000"));
    }

    public static String getNodeLabels() {
        String keyName = ConfigConstant.AKKA_WORKER_NODE_LABELS;
        return getValueWithDefault(keyName, "default");
    }

    public static long getWorkerTimeout() {
        String keyName = ConfigConstant.AKKA_WORKER_TIMEOUT;
        return Long.valueOf(getValueWithDefault(keyName, "300000"));
    }

    public static String getWorkerLogstoreJdbcUrl() {
        String keyName = ConfigConstant.AKKA_WORKER_LOGSTORE_JDBCURL;
        return getValueWithDefault(keyName, StringUtils.EMPTY);
    }

    public static String getWorkerLogstoreUsername() {
        String keyName = ConfigConstant.AKKA_WORKER_LOGSTORE_USERNAME;
        return getValueWithDefault(keyName, StringUtils.EMPTY);
    }

    public static String getWorkerLogstorePassword() {
        String keyName = ConfigConstant.AKKA_WORKER_LOGSTORE_PASSWORD;
        return getValueWithDefault(keyName, StringUtils.EMPTY);
    }


    public static Integer getAkkaAskConcurrent() {
        String keyName = ConfigConstant.AKKA_ASK_CONCURRENT;
        return Integer.valueOf(getValueWithDefault(keyName, "50"));
    }

    public static boolean isLocalMode() {
        return LOCAL_MODE;
    }
}
