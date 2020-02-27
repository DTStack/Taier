package com.dtstack.engine.worker.config;

import com.dtstack.engine.common.akka.Master;
import com.dtstack.engine.common.akka.Worker;
import com.dtstack.engine.common.util.AddressUtil;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2020/2/27
 */
public class WorkerConfig {

    private final static String REMOTE_PATH_TEMPLATE = "akka.tcp://%s@%s:%s/user/%s";
    private final static Properties WORK_CONFIG = new Properties();

    public static void loadConfig() throws IOException {
        String file = System.getProperty("user.dir") + "/conf/worker.properties";
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        WORK_CONFIG.load(bufferedReader);
    }

    public static String getMasterSystemName() {
        String name = WORK_CONFIG.getProperty("masterSystemName");
        if (StringUtils.isBlank(name)) {
            name = Master.class.getSimpleName();
        }
        return name;
    }


    public static String getMasterRemotePath() {
        String path = WORK_CONFIG.getProperty("masterRemotePath");
        if (StringUtils.isBlank(path)) {
            path = String.format(REMOTE_PATH_TEMPLATE, getMasterSystemName(), getMasterIp(), getMasterPort(), getMasterSystemName());
        }
        return path;
    }

    public static String getMasterIp() {
        String masterIp = WORK_CONFIG.getProperty("masterIp");
        if (StringUtils.isBlank(masterIp)) {
            throw new IllegalArgumentException("masterIp is null.");
        }
        return masterIp;
    }

    public static String getMasterPort() {
        String masterPort = WORK_CONFIG.getProperty("masterPort");
        if (StringUtils.isBlank(masterPort)) {
            throw new IllegalArgumentException("masterPort is null.");
        }
        return masterPort;
    }

    public static String getWorkerSystemName() {
        String name = WORK_CONFIG.getProperty("workerSystemName");
        if (StringUtils.isBlank(name)) {
            name = Worker.class.getSimpleName();
        }
        return name;
    }

    public static String getWorkerRemotePath() {
        String path = WORK_CONFIG.getProperty("workerRemotePath");
        if (StringUtils.isBlank(path)) {
            path = String.format(REMOTE_PATH_TEMPLATE, getWorkerSystemName(), getWorkerIp(), getWorkerPort(), getWorkerSystemName());
        }
        return path;
    }

    public static String getWorkerIp() {
        String workerIp = WORK_CONFIG.getProperty("workerIp");
        if (StringUtils.isBlank(workerIp)) {
            workerIp = AddressUtil.getOneIp();
        }
        return workerIp;
    }

    public static String getWorkerPort() {
        String workerPort = WORK_CONFIG.getProperty("workerPort");
        if (StringUtils.isBlank(workerPort)) {
            workerPort = "10000";
        }
        return workerPort;
    }

}
