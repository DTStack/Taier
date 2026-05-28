/*
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *     
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
 
package com.dtstack.taier.metrics.prometheus;

import io.micrometer.core.instrument.util.NamedThreadFactory;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.PushGateway;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Class that can be used to manage the pushing of metrics to a {@link PushGateway
 * Prometheus PushGateway}. Handles the scheduling of push operations, error handling and
 * shutdown operations.
 *
 * @author David J. M. Karlsen
 * @author Phillip Webb
 * @since 2.1.0
 */
public class PrometheusPushGatewayManager {

    private static final Log logger = LogFactory.getLog(PrometheusPushGatewayManager.class);

    private final PushGateway pushGateway;

    private final CollectorRegistry registry;

    private final String job;

    private final Map<String, String> groupingKey;

    private final ShutdownOperation shutdownOperation;

    private final ScheduledExecutorService scheduler;

    private ScheduledFuture<?> scheduled;

    /**
     * Create a new {@link PrometheusPushGatewayManager} instance using a single threaded
     * {@link ThreadPoolExecutor}.
     *
     * @param pushGateway       the source push gateway
     * @param registry          the collector registry to push
     * @param pushRate          the rate at which push operations occur
     * @param job               the job ID for the operation
     * @param groupingKeys      an optional set of grouping keys for the operation
     * @param shutdownOperation the shutdown operation that should be performed when
     *                          context is closed.
     */
    public PrometheusPushGatewayManager(PushGateway pushGateway, CollectorRegistry registry, long pushRate,
                                        String job, Map<String, String> groupingKeys,
                                        ShutdownOperation shutdownOperation) {
        this(pushGateway, registry, new ScheduledThreadPoolExecutor(1, new NamedThreadFactory(job)),
                pushRate, job, groupingKeys, shutdownOperation);
    }

    /**
     * Create a new {@link PrometheusPushGatewayManager} instance.
     *
     * @param pushGateway       the source push gateway
     * @param registry          the collector registry to push
     * @param scheduler         the scheduler used for operations
     * @param pushRate          the rate at which push operations occur
     * @param job               the job ID for the operation
     * @param groupingKey       an optional set of grouping keys for the operation
     * @param shutdownOperation the shutdown operation that should be performed when
     *                          context is closed.
     */
    public PrometheusPushGatewayManager(PushGateway pushGateway, CollectorRegistry registry,
                                        ScheduledExecutorService scheduler,
                                        long pushRate, String job, Map<String, String> groupingKey,
                                        ShutdownOperation shutdownOperation) {
        this.pushGateway = pushGateway;
        this.registry = registry;
        this.job = job;
        this.groupingKey = groupingKey;
        this.shutdownOperation = (shutdownOperation != null) ? shutdownOperation : ShutdownOperation.NONE;
        this.scheduler = scheduler;
        this.scheduled = this.scheduler.scheduleAtFixedRate(this::push, 20, pushRate, TimeUnit.SECONDS);
    }

    private void push() {
        try {
            this.pushGateway.pushAdd(this.registry, this.job, this.groupingKey);
        } catch (Throwable ex) {
            logger.warn("Unexpected exception thrown while pushing metrics to Prometheus Pushgateway", ex);
        }
    }

    private void delete() {
        try {
            this.pushGateway.delete(this.job, this.groupingKey);
        } catch (Throwable ex) {
            logger.warn("Unexpected exception thrown while deleting metrics from Prometheus Pushgateway", ex);
        }
    }

    /**
     * Shutdown the manager, running any {@link ShutdownOperation}.
     */
    public void shutdown() {
        shutdown(this.shutdownOperation);
    }

    private void shutdown(ShutdownOperation shutdownOperation) {
        this.scheduled.cancel(false);
        switch (shutdownOperation) {
            case PUSH:
                push();
                break;
            case DELETE:
                delete();
                break;
        }
    }

    /**
     * The operation that should be performed on shutdown.
     */
    public enum ShutdownOperation {

        /**
         * Don't perform any shutdown operation.
         */
        NONE,

        /**
         * Perform a 'push' before shutdown.
         */
        PUSH,

        /**
         * Perform a 'delete' before shutdown.
         */
        DELETE

    }

}
