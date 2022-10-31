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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.CallbackHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class RMCallbackHandler implements CallbackHandler {

    private static final Log LOG = LogFactory.getLog(RMCallbackHandler.class);

    private final List<Container> releaseContainers;

    public final List<Container> acquiredWorkerContainers;

    public final Set<String> blackHosts;

    private int neededWorkerContainersCount;

    private final AtomicInteger acquiredWorkerContainersCount;

    private final AtomicBoolean workerContainersExclusive;

    public RMCallbackHandler() {
        releaseContainers = Collections.synchronizedList(new ArrayList<Container>());
        acquiredWorkerContainers = Collections.synchronizedList(new ArrayList<Container>());
        acquiredWorkerContainersCount = new AtomicInteger(0);
        blackHosts = Collections.synchronizedSet(new HashSet<String>());
        workerContainersExclusive = new AtomicBoolean(false);
    }

    public List<String> getBlackHosts() {
        List<String> blackHostList = new ArrayList<>(blackHosts.size());
        blackHostList.addAll(blackHosts);
        return blackHostList;
    }

    public List<Container> getReleaseContainers() {
        return new ArrayList<>(releaseContainers);
    }

    public void startWorkerContainersExclusive() {
        workerContainersExclusive.set(true);
    }

    public int getAllocatedWorkerContainerNumber() {
        return acquiredWorkerContainersCount.get();
    }

    public List<Container> getAcquiredWorkerContainer() {
        return new ArrayList<>(acquiredWorkerContainers);
    }

    public void setNeededWorkerContainersCount(int count) {
        neededWorkerContainersCount = count;
    }

    @Override
    public void onContainersCompleted(List<ContainerStatus> containerStatuses) {
        for (ContainerStatus containerStatus : containerStatuses) {
            LOG.info("Got container status for containerID="
                    + containerStatus.getContainerId() + ", state="
                    + containerStatus.getState() + ", exitStatus="
                    + containerStatus.getExitStatus() + ", diagnostics="
                    + containerStatus.getDiagnostics());
        }
    }

    /**
     * am 与 rm 的通信是异步的
     * acquiredWorkerContainers 、releaseContainers 要注意并发问题
     *
     * @param containers
     */
    @Override
    public void onContainersAllocated(List<Container> containers) {
        for (Container acquiredContainer : containers) {
            LOG.info("Acquired container " + acquiredContainer.getId()
                    + " on host " + acquiredContainer.getNodeId().getHost()
                    + " , with the resource " + acquiredContainer.getResource().toString());
            String host = acquiredContainer.getNodeId().getHost();
            if (!blackHosts.contains(host)) {
                acquiredWorkerContainers.add(acquiredContainer);
                acquiredWorkerContainersCount.incrementAndGet();
                if (workerContainersExclusive.get()) {
                    blackHosts.add(acquiredContainer.getNodeId().getHost());
                }
            } else {
                LOG.info("Add container " + acquiredContainer.getId() + " to release list");
                releaseContainers.add(acquiredContainer);
            }
        }
        LOG.info("Current acquired worker container " + acquiredWorkerContainersCount.get()
                + " / " + neededWorkerContainersCount);
    }

    @Override
    public float getProgress() {
        return 0;
    }

    public void setProgress(float reportProgress) {

    }

    @Override
    public void onShutdownRequest() {
    }

    @Override
    public void onNodesUpdated(List<NodeReport> updatedNodes) {
    }

    @Override
    public void onError(Throwable e) {
        LOG.error("Error from RMCallback: ", e);
    }

}