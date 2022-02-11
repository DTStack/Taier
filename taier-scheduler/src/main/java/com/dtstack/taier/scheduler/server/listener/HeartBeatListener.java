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

import com.dtstack.taier.common.util.LogCountUtil;
import com.dtstack.taier.pluginapi.CustomThreadFactory;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import com.dtstack.taier.scheduler.zookeeper.data.BrokerHeartNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2019/10/22
 */
public class HeartBeatListener implements Listener {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeartBeatListener.class);

    private int logOutput = 0;
    private final static int MULTIPLES = 10;
    private final static int CHECK_INTERVAL = 1000;

    private final ScheduledExecutorService scheduledService;

    private ZkService zkService;

    public HeartBeatListener(ZkService zkService) {
        this.zkService = zkService;

        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory(this.getClass().getSimpleName()));
        scheduledService.scheduleWithFixedDelay(
                this,
                0,
                CHECK_INTERVAL,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {
            logOutput++;
            BrokerHeartNode brokerHeartNode = BrokerHeartNode.initBrokerHeartNode();
            brokerHeartNode.setSeq(1L);
            brokerHeartNode.setAlive(true);
            zkService.updateSynchronizedLocalBrokerHeartNode(zkService.getLocalAddress(), brokerHeartNode, false);
            if (LogCountUtil.count(logOutput, MULTIPLES)) {
                LOGGER.info("HeartBeatListener start again...");
            }
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }

    @Override
    public void close() throws Exception {
        scheduledService.shutdownNow();
    }
}
