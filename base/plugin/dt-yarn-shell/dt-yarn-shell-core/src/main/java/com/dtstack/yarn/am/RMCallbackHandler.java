package com.dtstack.yarn.am;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.yarn.api.records.Container;
import org.apache.hadoop.yarn.api.records.ContainerStatus;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.client.api.async.AMRMClientAsync.CallbackHandler;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class RMCallbackHandler implements CallbackHandler {

    private static final Log LOG = LogFactory.getLog(RMCallbackHandler.class);

    private final BlockingQueue<Container> allocatedWorkerContainers = new LinkedBlockingQueue<>();


    public RMCallbackHandler() {
        LOG.info("RMCallbackHandler init started");
        LOG.info("RMCallbackHandler init ended");
    }

    public Container take() throws InterruptedException {
        return allocatedWorkerContainers.take();
    }

    @Override
    public void onContainersCompleted(List<ContainerStatus> containerStatuses) {
        for (ContainerStatus containerStatus : containerStatuses) {
            LOG.info("Container " + containerStatus.getContainerId() + " completed with status "
                    + containerStatus.getState().toString());
        }
    }

    @Override
    public void onContainersAllocated(List<Container> containers) {
        for (Container acquiredContainer : containers) {
            LOG.info("Acquired container " + acquiredContainer.getId()
                    + " on host " + acquiredContainer.getNodeId().getHost()
                    + " , with the resource " + acquiredContainer.getResource().toString());
            try {
                allocatedWorkerContainers.put(acquiredContainer);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
}
