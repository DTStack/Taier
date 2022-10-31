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

package com.dtstack.taier.script.container;

import com.dtstack.taier.script.ScriptConfiguration;
import com.dtstack.taier.script.api.ApplicationContainerProtocol;
import com.dtstack.taier.script.common.ContainerStatus;
import com.dtstack.taier.script.common.HeartbeatRequest;
import com.dtstack.taier.script.common.HeartbeatResponse;
import com.dtstack.taier.script.util.Utilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ContainerStatusNotifier implements Runnable {

    private static final Log LOG = LogFactory.getLog(ContainerStatusNotifier.class);

    private ApplicationContainerProtocol protocol;

    private Configuration conf;

    private ScriptContainerId containerId;

    private HeartbeatRequest heartbeatRequest;

    private HeartbeatResponse heartbeatResponse;

    private int heartbeatInterval;

    private int heartbeatRetryMax;

    private ScheduledExecutorService scheduledExecutorService;

    private volatile Boolean isCompleted = false;

    private long interResultTimeStamp;

    public ContainerStatusNotifier(ApplicationContainerProtocol protocol, Configuration conf, ScriptContainerId xlearningContainerId) {
        this.protocol = protocol;
        this.conf = conf;
        this.containerId = xlearningContainerId;
        // 自定义 CustomThreadFactory 对线程设置为守护线程
        this.scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(containerId.toString() + "heartbeat"));
        this.heartbeatRequest = new HeartbeatRequest();
        this.heartbeatResponse = new HeartbeatResponse();
        this.heartbeatRequest.setContainerUserDir(System.getProperty("user.dir"));
        this.isCompleted = false;
        this.heartbeatInterval = this.conf.getInt(ScriptConfiguration.SCRIPT_CONTAINER_HEARTBEAT_INTERVAL, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_INTERVAL);
        this.heartbeatRetryMax = this.conf.getInt(ScriptConfiguration.SCRIPT_CONTAINER_HEARTBEAT_RETRY, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_RETRY);
    }

    public void setContainerErrorMessage(String msg) {
        heartbeatRequest.setErrMsg(msg);
    }

    public void setContainerStatus(ContainerStatus containerStatus) {
        this.heartbeatRequest.setXlearningContainerStatus(containerStatus);
    }

    public void setContainersStartTime(String startTime) {
        this.heartbeatRequest.setContainersStartTime(startTime);
    }

    public void setContainersFinishTime(String finishTime) {
        this.heartbeatRequest.setContainersFinishTime(finishTime);
    }

    public void reportContainerStatusNow(ContainerStatus containerStatus) {
        heartbeatRequest.setXlearningContainerStatus(containerStatus);
        heartbeatWithRetry();
    }

    public Boolean getCompleted() {
        return isCompleted;
    }

    public HeartbeatResponse heartbeatWithRetry() {
        int retry = 0;
        while (true) {
            try {
                heartbeatResponse = protocol.heartbeat(containerId, heartbeatRequest);
                LOG.debug("Send HeartBeat to ApplicationMaster");
                return heartbeatResponse;
            } catch (Exception e) {
                retry++;
                if (retry <= heartbeatRetryMax) {
                    LOG.warn("Send heartbeat to ApplicationMaster failed in retry " + retry);
                    Utilities.sleep(heartbeatInterval);
                } else {
                    LOG.warn("Send heartbeat to ApplicationMaster failed in retry " + retry
                            + ", container will suicide!", e);
                    System.exit(1);
                }
            }
        }
    }

    public void heartbeatResponseHandle(HeartbeatResponse heartbeatResponse) {
        LOG.info("Received the heartbeat response from the AM. CurrentJob finished " + heartbeatResponse.getIsCompleted()
                + " , currentInnerModelSavedTimeStamp is " + heartbeatResponse.getInterResultTimeStamp());
        if (!heartbeatResponse.getIsCompleted()) {
            if (!heartbeatResponse.getInterResultTimeStamp().equals(interResultTimeStamp)) {
                this.interResultTimeStamp = heartbeatResponse.getInterResultTimeStamp();
            }
            LOG.debug("container " + containerId + " currentStatus:" + heartbeatRequest.getXlearningContainerStatus());
        }

        this.isCompleted = heartbeatResponse.getIsCompleted();
    }

    @Override
    public void run() {
        heartbeatResponse = heartbeatWithRetry();
        heartbeatResponseHandle(heartbeatResponse);
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, 0L, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduledExecutorService.shutdown();
    }


    private class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        public CustomThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            namePrefix = "pool-" + name + "-" +
                    poolNumber.getAndIncrement() +
                    "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            t.setDaemon(true);
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }
}