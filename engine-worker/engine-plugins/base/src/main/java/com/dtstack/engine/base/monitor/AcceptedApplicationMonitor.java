package com.dtstack.engine.base.monitor;


import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.util.KerberosUtils;
import com.dtstack.engine.common.CustomThreadFactory;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * company: www.dtstack.com
 * author: xiuzhu
 * create: 2020/07/23
 */

public class AcceptedApplicationMonitor implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(AcceptedApplicationMonitor.class);

    private static final long INTERVAL = 3000;

    private static final long THRESHOLD = 60 * 1000 * 10;

    private static ScheduledExecutorService scheduledService;

    private static final String SPLIT = "_";

    private String queueName;

    private YarnConfiguration yarnConf;

    private BaseConfig config;

    public static void start(YarnConfiguration yarnConf, String queueName, BaseConfig config) {
        AcceptedApplicationMonitor monitor = new AcceptedApplicationMonitor();
        monitor.queueName = queueName;
        monitor.yarnConf = yarnConf;
        monitor.config = config;
        String namePrefix = monitor.getClass().getSimpleName() + SPLIT + queueName;
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(namePrefix));
        scheduledService.scheduleWithFixedDelay(monitor, 0, INTERVAL, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            YarnClient yarnClient = KerberosUtils.login(config, () -> {
                YarnClient client = YarnClient.createYarnClient();
                client.init(yarnConf);
                client.start();
                return client;
            }, yarnConf);
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            List<ApplicationReport> acceptedApps = yarnClient.getApplications(enumSet).stream().
                    filter(report -> report.getQueue().endsWith(queueName)).collect(Collectors.toList());
            for (ApplicationReport report : acceptedApps) {
                long startTime = report.getStartTime();
                long currentTime = System.currentTimeMillis();
                if (currentTime - startTime > THRESHOLD) {
                    ApplicationId appId = report.getApplicationId();
                    yarnClient.killApplication(appId);
                }
            }
        } catch (Exception e) {
            logger.error("Monitor Accepted Application Exception: " + e.getMessage());
        }
    }

}
