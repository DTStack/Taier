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

package com.dtstack.taiga.base.monitor;


import com.dtstack.taiga.base.BaseConfig;
import com.dtstack.taiga.base.util.KerberosUtils;
import com.dtstack.taiga.pluginapi.CustomThreadFactory;
import com.dtstack.taiga.pluginapi.exception.PluginDefineException;
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

    private static final long INTERVAL = 60000;

    private static final long THRESHOLD = 60L * 1000 * 10;

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

        try (
            YarnClient yarnClient = KerberosUtils.login(config, () -> {
                YarnClient client = YarnClient.createYarnClient();
                client.init(yarnConf);
                client.start();
                return client;}, yarnConf);
        ) {
            EnumSet<YarnApplicationState> enumSet = EnumSet.noneOf(YarnApplicationState.class);
            enumSet.add(YarnApplicationState.ACCEPTED);
            if (yarnClient == null) {
                throw new PluginDefineException("AcceptedApplicationMonitor init yarnClient fail");
            }
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
