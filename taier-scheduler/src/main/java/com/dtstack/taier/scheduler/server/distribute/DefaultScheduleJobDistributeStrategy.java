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

package com.dtstack.taier.scheduler.server.distribute;

import com.dtstack.taier.common.enums.EScheduleJobDistributeType;
import com.dtstack.taier.common.exception.TaierDefineException;
import com.dtstack.taier.scheduler.server.JobPartitioner;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 历史默认策略，按调度节点当前负载计算每个节点应接收的实例数。
 * @author xingyi
 */
@Component
public class DefaultScheduleJobDistributeStrategy implements ScheduleJobDistributeStrategy {

    @Autowired
    private JobPartitioner jobPartitioner;

    @Override
    public EScheduleJobDistributeType distributeType() {
        return EScheduleJobDistributeType.DEFAULT;
    }

    @Override
    public Map<ScheduleJobDetails, String> distribute(List<ScheduleJobDetails> scheduleJobDetails,
                                                      Integer scheduleType,
                                                      ScheduleJobDistributeContext distributeContext) {
        Map<ScheduleJobDetails, String> jobNodeMap = Maps.newHashMap();
        Map<String, Integer> nodeJobSize = jobPartitioner.computeBatchJobSize(scheduleType, scheduleJobDetails.size());
        if (MapUtils.isEmpty(nodeJobSize)) {
            throw new TaierDefineException("No available node to distribute schedule jobs");
        }

        Iterator<ScheduleJobDetails> batchJobIterator = scheduleJobDetails.iterator();
        for (Map.Entry<String, Integer> nodeJobSizeEntry : nodeJobSize.entrySet()) {
            String nodeAddress = nodeJobSizeEntry.getKey();
            int nodeSize = nodeJobSizeEntry.getValue();
            while (nodeSize > 0 && batchJobIterator.hasNext()) {
                jobNodeMap.put(batchJobIterator.next(), nodeAddress);
                nodeSize--;
            }
        }
        return jobNodeMap;
    }
}
