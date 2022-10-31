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

package com.dtstack.taier.datasource.api.initialize;

import com.dtstack.taier.datasource.api.config.Config;
import com.dtstack.taier.datasource.api.exception.InitializeException;
import com.dtstack.taier.datasource.api.thread.CustomThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * abs env init
 *
 * @author ：wangchuan
 * date：Created in 19:00 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public abstract class AbstractEnvInitialize implements EnvInitialize {

    /**
     * 标记是否已经完成初始化
     */
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /**
     * 周期调度线程池
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * 默认 60s
     */
    private static final int DEFAULT_SCHEDULE_PERIOD = 60;

    /**
     * 默认周期调度线程工程名称
     */
    private static final String DEFAULT_SCHEDULE_NAME = "init-schedule";

    /**
     * 判断是否启动
     *
     * @return 是否已经启动
     */
    public boolean isStart() {
        return isRunning.get();
    }

    @Override
    public void init(Config config) throws InitializeException {
        if (isStart()) {
            return;
        }
        open();
        if (isSchedule()) {
            // 初始化周期调度线程池
            log.info("start manager schedule, name: {}", getScheduleJobName());
            scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(getScheduleJobName()));
            scheduledExecutorService.scheduleAtFixedRate(this::runScheduleJob, 10, getSchedulePeriod(), TimeUnit.SECONDS);
        }
        isRunning.set(true);
    }

    protected abstract void open();

    @Override
    public void destroy() {
        isRunning.set(false);
        if (scheduledExecutorService != null) {
            log.info("stop manager schedule, name: {}", getScheduleJobName());
            scheduledExecutorService.shutdownNow();
        }
        close();
    }

    protected abstract void close();

    /**
     * 运行周期调度任务
     */
    public void runScheduleJob() {}

    /**
     * 是否需要周期调度
     *
     * @return 是否需要周期调度
     */
    public boolean isSchedule() {
        return false;
    }

    /**
     * 周期运行间隔时间, 单位秒
     *
     * @return 间隔时间
     */
    public int getSchedulePeriod() {
        return DEFAULT_SCHEDULE_PERIOD;
    }

    /**
     * 周期运行间隔时间, 单位秒
     *
     * @return 间隔时间
     */
    public String getScheduleJobName() {
        return DEFAULT_SCHEDULE_NAME;
    }
}
