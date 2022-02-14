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

package com.dtstack.taier.scheduler.server;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.scheduler.server.builder.CycleJobBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/22
 */
@Component
public class JobGraphBuilderTrigger implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobGraphBuilderTrigger.class);

    private static final long CHECK_JOB_BUILD_INTERVAL = 60 * 10 * 1000L;

    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private CycleJobBuilder cycleJobBuilder;

    private ScheduledExecutorService scheduledService;

    public JobGraphBuilderTrigger() {
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobGraphTrigger"));
    }

    public void dealMaster(boolean isMaster) {
        try {
            if (isMaster) {
                startJobGraph();
            } else {
                stopJobGraph();
            }
        } catch (Throwable e) {
            LOGGER.error("JobGraphBuilderTrigger.dealMaster error:", e);
        }
    }

    private void startJobGraph() {
        if (RUNNING.get()) {
            return;
        }
        if (scheduledService.isShutdown()) {
            scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobGraphTrigger"));
        }
        scheduledService.scheduleAtFixedRate(
                this,
                100,
                CHECK_JOB_BUILD_INTERVAL,
                TimeUnit.MILLISECONDS);
        RUNNING.compareAndSet(false, true);
        LOGGER.info("start job graph trigger...");
    }

    private void stopJobGraph() {
        if (scheduledService != null) {
            scheduledService.shutdownNow();
        }
        RUNNING.compareAndSet(true, false);
        LOGGER.info("stop job graph trigger...");
    }

    private String getTriggerDay(String time) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        SimpleDateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
        Date triggerDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
        if (triggerDate.after(new Date())) {
            //校验当天运行的
            return new DateTime().toString("yyyy-MM-dd");
        }
        return new DateTime().plusDays(1).toString("yyyy-MM-dd");
    }

    @Override
    public void run() {
        try {
            if (RUNNING.get()) {
                try {
                    String triggerDay = getTriggerDay(environmentContext.getJobGraphBuildCron());
                    LOGGER.warn("---check jobGraph build day:{} job graph start!--", triggerDay);
                    cycleJobBuilder.buildTaskJobGraph(triggerDay);
                    LOGGER.warn("---check jobGraph build day:{} job graph end!--", triggerDay);
                } catch (Exception e) {
                    LOGGER.error("", e);
                }
            } else {
                LOGGER.warn("---triggering, but Running is false---");
            }
        } catch (Exception e) {
            LOGGER.error("---trigger job graph error---", e);
        }
    }
}
