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

package com.dtstack.taier.scheduler.server.listener;

import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.scheduler.server.FailoverStrategy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * company: www.dtstack.com
 * @author toutian
 * create: 2019/10/22
 */
public class MasterListener implements LeaderLatchListener, Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MasterListener.class);

    private final static int CHECK_INTERVAL = 10000;

    private final AtomicBoolean isMaster = new AtomicBoolean(false);
    private final FailoverStrategy failoverStrategy;

    private final ScheduledExecutorService scheduledService;
    private LeaderLatch latch;

    public MasterListener(FailoverStrategy failoverStrategy,
                          CuratorFramework curatorFramework,
                          String latchPath,
                          String localAddress) throws Exception {
        this.failoverStrategy = failoverStrategy;

        this.latch = new LeaderLatch(curatorFramework, latchPath, localAddress);
        this.latch.addListener(this);
        this.latch.start();

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                60000,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    public boolean isMaster() {
        return isMaster.get();
    }

    @Override
    public void isLeader() {
        isMaster.set(Boolean.TRUE);
    }

    @Override
    public void notLeader() {
        isMaster.set(Boolean.FALSE);
    }

    @Override
    public void close() throws Exception {
        this.latch.close();
        notLeader();
        scheduledService.shutdownNow();
    }

    @Override
    public void run() {
        LOGGER.info("i am master:{} ...", isMaster.get());

        failoverStrategy.setIsMaster(isMaster.get());
    }

}
