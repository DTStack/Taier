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

package com.dtstack.taier.scheduler.jobdealer;

import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.queue.DelayBlockingQueue;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.pluginapi.JobIdentifier;
import com.dtstack.taier.pluginapi.exception.ExceptionUtil;
import com.dtstack.taier.scheduler.WorkerOperator;
import com.dtstack.taier.scheduler.enums.EJobLogType;
import com.dtstack.taier.scheduler.jobdealer.bo.JobLogInfo;
import com.dtstack.taier.scheduler.service.ScheduleJobExpandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取任务运行结束日志 需要延迟
 * 获取任务重试日志  无需延迟
 */
@Component
public class JobLogDealer implements InitializingBean, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobLogDealer.class);

    @Autowired
    private WorkerOperator workerOperator;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private EnvironmentContext environmentContext;

    private DelayBlockingQueue<JobLogInfo> delayBlockingQueue = new DelayBlockingQueue<>(1000);
    private ExecutorService logExecutePool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()));

    private ExecutorService logGetPool;


    @Override
    public void run() {
        while (true) {
            try {
                JobLogInfo taskInfo = delayBlockingQueue.take();
                EJobLogType logType = taskInfo.getLogType();
                switch (logType) {
                    case FINISH_LOG:
                        logGetPool.execute(() -> updateJobEngineLog(taskInfo));
                }

            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    public void executeLogRunnable(Runnable runnable) {
        logGetPool.execute(runnable);
    }

    public void addJobInfo(JobLogInfo jobLogInfo) {
        try {
            LOGGER.info("add job {} into log dealer", jobLogInfo.getJobId());
            delayBlockingQueue.put(jobLogInfo);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }


    private void updateJobEngineLog(JobLogInfo jobLogInfo) {
        JobIdentifier jobIdentifier = jobLogInfo.getJobIdentifier();
        String jobId = jobIdentifier.getJobId();
        try {
            String jobLog = workerOperator.getEngineLog(jobIdentifier);
            if (jobLog != null) {
                scheduleJobExpandService.updateEngineLog(jobId, jobLog);
            }
        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            scheduleJobExpandService.updateEngineLog(jobId, errorLog);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        logGetPool = new ThreadPoolExecutor(2, environmentContext.getLogPoolSize(), 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100), new CustomThreadFactory(this.getClass().getSimpleName()));
        logExecutePool.execute(this);
    }


}
