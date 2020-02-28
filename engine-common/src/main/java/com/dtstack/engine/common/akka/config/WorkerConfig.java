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
public class WorkerConfig {

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
        String name = null;
        if (AKKA_CONFIG.hasPath(MASTER_CONFIG_PREFIX)) {
            name = AKKA_CONFIG.getString(MASTER_CONFIG_PREFIX + ".masterName");
        }
        if (StringUtils.isBlank(name)) {
            name = Master.class.getSimpleName();
        }
        return name;
    }

    public static String getMasterAddress() {
        String masterAddress = null;
        if (AKKA_CONFIG.hasPath(MASTER_CONFIG_PREFIX)) {
            masterAddress = AKKA_CONFIG.getString(MASTER_CONFIG_PREFIX + ".masterAddress");
        }
        if (StringUtils.isBlank(masterAddress)) {
            throw new IllegalArgumentException("masterAddress is null.");
        }
        return masterAddress;
    }

    public static String getWorkerSystemName() {
        return "WorkerSystem";
    }

    public static String getWorkerName() {
        String name = null;
        if (AKKA_CONFIG.hasPath(WORKER_CONFIG_PREFIX)) {
            name = AKKA_CONFIG.getString(WORKER_CONFIG_PREFIX + ".workerName");
        }
        if (StringUtils.isBlank(name)) {
            name = Worker.class.getSimpleName();
        }
        return name;
    }

    public static String getWorkerRemotePath() {
        String path = null;
        if (AKKA_CONFIG.hasPath(WORKER_CONFIG_PREFIX)) {
            path = AKKA_CONFIG.getString(WORKER_CONFIG_PREFIX + ".workerRemotePath");
        }
        if (StringUtils.isBlank(path)) {
            path = String.format(REMOTE_PATH_TEMPLATE, getWorkerSystemName(), getWorkerIp(), getWorkerPort(), getWorkerName());
        }
        return path;
    }

    public static String getWorkerIp() {
        String workerIp = AKKA_CONFIG.getString("akka.remote.netty.tcp.hostname");
        if (StringUtils.isBlank(workerIp)) {
            workerIp = AddressUtil.getOneIp();
        }
        return workerIp;
    }

    public static int getWorkerPort() {
        int workerPort = AKKA_CONFIG.getInt("akka.remote.netty.tcp.port");
        if (workerPort == 0) {
            workerPort = 2554;
        }
        return workerPort;
    }

    public static Config checkIpAndPort(Config config) {
        HashMap<String, Object> configMap = Maps.newHashMap();
        int port = config.getInt(ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT);
        int endPort = port + 100;
        port = NetUtils.getAvailablePortRange(port, endPort);
        configMap.put(ConfigConstant.AKKA_REMOTE_NETTY_TCP_PORT, port);

        String hostname = config.getString(ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME);
        if (StringUtils.isBlank(hostname)) {
            hostname = AddressUtil.getOneIp();
        }
        configMap.put(ConfigConstant.AKKA_REMOTE_NETTY_TCP_HOSTNAME, hostname);

        Config loadConfig = ConfigFactory.parseMap(configMap).withFallback(config);
        loadConfig(loadConfig);
        return loadConfig;
    }

}
