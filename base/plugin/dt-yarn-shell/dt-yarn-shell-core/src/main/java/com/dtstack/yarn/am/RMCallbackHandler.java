package com.dtstack.yarn.am;

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
        blackHosts = Collections.synchronizedSet(new HashSet<String>());
        acquiredWorkerContainersCount = new AtomicInteger(0);
        workerContainersExclusive = new AtomicBoolean(false);
    }

    public List<String> getBlackHosts() {
        List<String> blackHostList = new ArrayList<>(blackHosts.size());
        for (String host : blackHosts) {
            blackHostList.add(host);
        }
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
            LOG.info("Container " + containerStatus.getContainerId() + " completed with status "
                    + containerStatus.getState().toString());
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
        List<Container> allocatedContainers = new ArrayList<>(containers.size());
        for (Container acquiredContainer : containers) {
            LOG.info("Acquired container " + acquiredContainer.getId()
                    + " on host " + acquiredContainer.getNodeId().getHost()
                    + " , with the resource " + acquiredContainer.getResource().toString());
            String host = acquiredContainer.getNodeId().getHost();
            if (!blackHosts.contains(host)) {
                allocatedContainers.add(acquiredContainer);
                if (workerContainersExclusive.get()) {
                    blackHosts.add(acquiredContainer.getNodeId().getHost());
                }
            } else {
                LOG.info("Add container " + acquiredContainer.getId() + " to release list");
                releaseContainers.add(acquiredContainer);
            }
        }
        acquiredWorkerContainers.addAll(allocatedContainers);
        acquiredWorkerContainersCount.addAndGet(allocatedContainers.size());
        LOG.info("Current acquired worker container " + acquiredWorkerContainersCount.get()
                + " / " + neededWorkerContainersCount);
    }

    public void removeLaunchFailed(String nodeHost) {
        if (workerContainersExclusive.get()) {
            blackHosts.remove(nodeHost);
        }
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

    public void resetAllocatedWorkerContainerNumber() {
        acquiredWorkerContainersCount.set(0);
    }

    public void resetAcquiredWorkerContainers() {
        acquiredWorkerContainers.clear();
    }

    public void removeReleaseContainers(List<Container> removeContainers) {
        releaseContainers.removeAll(removeContainers);
    }
}