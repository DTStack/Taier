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

package com.dtstack.taiga.scheduler.event;

import com.dtstack.taiga.pluginapi.enums.RdosTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobEventPublisher {

    private static volatile ScheduleJobEventPublisher publisher = null;

    private final Logger logger = LoggerFactory.getLogger(ScheduleJobEventPublisher.class);

    private List<ScheduleJobEventLister> scheduleJobEventMulticaster;

    private ScheduleJobEventPublisher() {
        this.scheduleJobEventMulticaster = new ArrayList<>();
    }

    public static ScheduleJobEventPublisher getInstance() {
        if (publisher == null) {
            synchronized (ScheduleJobEventPublisher.class) {
                if (publisher == null) {
                    publisher = new ScheduleJobEventPublisher();
                }
            }
        }
        return publisher;
    }

    public void register(ScheduleJobEventLister lister) {
        scheduleJobEventMulticaster.add(lister);
    }

    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        if (null == event) {
            return;
        }
        if (CollectionUtils.isEmpty(scheduleJobEventMulticaster)) {
            return;
        }
        if (CollectionUtils.isEmpty(event.getJobIds()) || null == event.getStatus()) {
            return;
        }
        if (RdosTaskStatus.getStoppedStatus().contains(event.getStatus())) {
            logger.info("publishBatchEvent {}", event);
        }
        for (ScheduleJobEventLister scheduleJobEventLister : scheduleJobEventMulticaster) {
            scheduleJobEventLister.publishBatchEvent(event);
        }
    }
}
