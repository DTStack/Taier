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

package com.dtstack.taier.script.am;


import com.dtstack.taier.script.ScriptConfiguration;
import com.dtstack.taier.script.common.ContainerStatus;
import com.dtstack.taier.script.common.HeartbeatRequest;
import com.dtstack.taier.script.container.ContainerEntity;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ContainerLostDetector implements Runnable {

    private static final Log LOG = LogFactory.getLog(ContainerLostDetector.class);

    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    private ApplicationContainerListener parent;

    private long heartBeatTimeout;
    private long heartBeatInterval;
    private long localResourceTimeOut;

    public ContainerLostDetector(ApplicationContainerListener applicationContainerListener) {
        parent = applicationContainerListener;
        Configuration configuration = parent.getConfig();
        this.heartBeatInterval = configuration.getLong(ScriptConfiguration.SCRIPT_CONTAINER_HEARTBEAT_INTERVAL, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_INTERVAL);
        this.heartBeatTimeout = configuration.getLong(ScriptConfiguration.SCRIPT_CONTAINER_HEARTBEAT_TIMEOUT, ScriptConfiguration.DEFAULT_SCRIPT_CONTAINER_HEARTBEAT_TIMEOUT);
        this.localResourceTimeOut = configuration.getInt(ScriptConfiguration.SCRIPT_LOCALRESOURCE_TIMEOUT, ScriptConfiguration.DEFAULT_SCRIPT_LOCALRESOURCE_TIMEOUT);
        LOG.info("HeartBeatParams, heartBeatTimeout: " + heartBeatTimeout + ", heartBeatInterval:" + heartBeatInterval + ", localResourceTimeOut:" + localResourceTimeOut);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        for (ContainerEntity entity : parent.getAllContainers()) {
            if (entity.getDtContainerStatus().equals(ContainerStatus.UNDEFINED)) {
                if (now - entity.getLastBeatTime() > localResourceTimeOut) {
                    String errorMsg = "Container " + entity.getContainerId().toString() + " local resource timed out after "
                            + localResourceTimeOut / 1000 + " seconds";
                    LOG.info(errorMsg);
                    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                    heartbeatRequest.setXlearningContainerStatus(ContainerStatus.FAILED);
                    heartbeatRequest.setErrMsg(errorMsg);
                    parent.heartbeat(entity.getContainerId(), heartbeatRequest);
                }
            } else {
                if (now - entity.getLastBeatTime() > heartBeatTimeout) {
                    String errorMsg = "Container " + entity.getContainerId().toString() + " timed out after "
                            + heartBeatTimeout / 1000 + " seconds";
                    LOG.info(errorMsg);
                    HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
                    heartbeatRequest.setXlearningContainerStatus(ContainerStatus.FAILED);
                    heartbeatRequest.setErrMsg(errorMsg);
                    parent.heartbeat(entity.getContainerId(), heartbeatRequest);
                }
            }
        }
    }

    public void start() {
        scheduledExecutorService.scheduleAtFixedRate(this, heartBeatInterval, heartBeatInterval, TimeUnit.MILLISECONDS);
    }

}
