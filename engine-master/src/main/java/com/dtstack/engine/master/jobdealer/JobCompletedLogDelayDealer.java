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

package com.dtstack.engine.master.jobdealer;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dtstack.engine.common.queue.DelayBlockingQueue;
import com.dtstack.engine.domain.ScheduleJobExpand;
import com.dtstack.engine.master.WorkerOperator;
import com.dtstack.engine.master.jobdealer.bo.JobCompletedInfo;
import com.dtstack.engine.pluginapi.CustomThreadFactory;
import com.dtstack.engine.pluginapi.JobIdentifier;
import com.dtstack.engine.pluginapi.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class JobCompletedLogDelayDealer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobCompletedLogDelayDealer.class);

    private ApplicationContext applicationContext;
    private WorkerOperator workerOperator;

    private DelayBlockingQueue<JobCompletedInfo> delayBlockingQueue = new DelayBlockingQueue<JobCompletedInfo>(1000);
    private ExecutorService taskStatusPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1), new CustomThreadFactory(this.getClass().getSimpleName()));

    public JobCompletedLogDelayDealer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        setBean();
        taskStatusPool.execute(this);
    }

    @Override
    public void run() {
        while (true) {
            try {
                JobCompletedInfo taskInfo = delayBlockingQueue.take();
                updateJobEngineLog(taskInfo.getJobId(), taskInfo.getJobIdentifier());
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    public void addCompletedTaskInfo(JobCompletedInfo taskInfo) {
        try {
            delayBlockingQueue.put(taskInfo);
        } catch (InterruptedException e) {
            LOGGER.error("", e);
        }
    }

    private void updateJobEngineLog(String jobId, JobIdentifier jobIdentifier) {
        LambdaUpdateWrapper<ScheduleJobExpand> lambdaUpdate = Wrappers.lambdaUpdate();
        try {
            String jobLog = workerOperator.getEngineLog(jobIdentifier);
            if (jobLog != null) {
                lambdaUpdate.eq(ScheduleJobExpand::getJobId, jobId).set(ScheduleJobExpand::getEngineLog, jobLog);
            }

        } catch (Throwable e) {
            String errorLog = ExceptionUtil.getErrorMessage(e);
            LOGGER.error("update JobEngine Log error jobId:{} ,error info {}..", jobId, errorLog);
            lambdaUpdate.eq(ScheduleJobExpand::getJobId, jobId).set(ScheduleJobExpand::getEngineLog, errorLog);
        }
    }

    private void setBean() {
        this.workerOperator = applicationContext.getBean(WorkerOperator.class);
    }
}
