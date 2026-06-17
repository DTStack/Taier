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
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.zookeeper.ZkService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 按 taskType + cycTime 分组的周期实例分配策略。
 * @author xingyi
 */
@Component
public class TaskTypeCycTimeScheduleJobDistributeStrategy implements ScheduleJobDistributeStrategy {

    @Autowired
    private ZkService zkService;

    private final Object nodeLoadAssignLock = new Object();

    @Override
    public EScheduleJobDistributeType distributeType() {
        return EScheduleJobDistributeType.TASK_TYPE_CYCTIME;
    }

    @Override
    public Map<ScheduleJobDetails, String> distribute(List<ScheduleJobDetails> batchJobs,
                                                      Integer scheduleType,
                                                      ScheduleJobDistributeContext distributeContext) {
        Map<ScheduleJobDetails, String> jobNodeMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(batchJobs)) {
            return jobNodeMap;
        }

        List<String> aliveNodes = zkService.getAliveBrokersChildren();
        if (CollectionUtils.isEmpty(aliveNodes)) {
            throw new TaierDefineException("No available node to distribute schedule jobs");
        }

        synchronized (nodeLoadAssignLock) {
            Map<String, Long> globalNodeLoadCache = distributeContext.getGlobalNodeLoadCache();
            Map<String, Map<String, Long>> globalGroupNodeLoadCache = distributeContext.getGlobalGroupNodeLoadCache();
            cleanOfflineNodes(aliveNodes, globalNodeLoadCache, globalGroupNodeLoadCache);

            Map<String, Long> nodeLoad = Maps.newHashMapWithExpectedSize(aliveNodes.size());
            for (String aliveNode : aliveNodes) {
                nodeLoad.put(aliveNode, globalNodeLoadCache.getOrDefault(aliveNode, 0L));
            }

            Map<String, List<ScheduleJobDetails>> groupedJobs = groupJobs(batchJobs);
            if (MapUtils.isEmpty(groupedJobs)) {
                return jobNodeMap;
            }

            Map<String, Map<String, Long>> groupLoadSnapshot = buildGroupLoadSnapshot(aliveNodes, groupedJobs, globalGroupNodeLoadCache);
            distributeGroupedJobs(aliveNodes, nodeLoad, groupLoadSnapshot, groupedJobs, jobNodeMap);
            writeBackLoadSnapshot(aliveNodes, nodeLoad, groupLoadSnapshot, globalNodeLoadCache, globalGroupNodeLoadCache);
            return jobNodeMap;
        }
    }

    private void cleanOfflineNodes(List<String> aliveNodes,
                                   Map<String, Long> globalNodeLoadCache,
                                   Map<String, Map<String, Long>> globalGroupNodeLoadCache) {
        globalNodeLoadCache.keySet().removeIf(node -> !aliveNodes.contains(node));
        for (String aliveNode : aliveNodes) {
            globalNodeLoadCache.putIfAbsent(aliveNode, 0L);
        }
        globalGroupNodeLoadCache.values().forEach(groupLoad -> {
            groupLoad.keySet().removeIf(node -> !aliveNodes.contains(node));
            for (String aliveNode : aliveNodes) {
                groupLoad.putIfAbsent(aliveNode, 0L);
            }
        });
    }

    private Map<String, List<ScheduleJobDetails>> groupJobs(List<ScheduleJobDetails> batchJobs) {
        Map<String, List<ScheduleJobDetails>> groupedJobs = Maps.newLinkedHashMap();
        for (ScheduleJobDetails batchJob : batchJobs) {
            String groupKey = buildTaskTypeCycTimeGroupKey(batchJob.getScheduleJob());
            groupedJobs.computeIfAbsent(groupKey, key -> Lists.newArrayList()).add(batchJob);
        }
        return groupedJobs;
    }

    private Map<String, Map<String, Long>> buildGroupLoadSnapshot(List<String> aliveNodes,
                                                                  Map<String, List<ScheduleJobDetails>> groupedJobs,
                                                                  Map<String, Map<String, Long>> globalGroupNodeLoadCache) {
        Map<String, Map<String, Long>> groupLoadSnapshot = Maps.newHashMapWithExpectedSize(groupedJobs.size());
        for (String groupKey : groupedJobs.keySet()) {
            Map<String, Long> cacheGroupLoad = globalGroupNodeLoadCache.computeIfAbsent(groupKey, k -> new ConcurrentHashMap<>());
            for (String aliveNode : aliveNodes) {
                cacheGroupLoad.putIfAbsent(aliveNode, 0L);
            }
            Map<String, Long> localGroupLoad = Maps.newHashMapWithExpectedSize(aliveNodes.size());
            for (String aliveNode : aliveNodes) {
                localGroupLoad.put(aliveNode, cacheGroupLoad.getOrDefault(aliveNode, 0L));
            }
            groupLoadSnapshot.put(groupKey, localGroupLoad);
        }
        return groupLoadSnapshot;
    }

    private void distributeGroupedJobs(List<String> aliveNodes,
                                       Map<String, Long> nodeLoad,
                                       Map<String, Map<String, Long>> groupLoadSnapshot,
                                       Map<String, List<ScheduleJobDetails>> groupedJobs,
                                       Map<ScheduleJobDetails, String> jobNodeMap) {
        List<Map.Entry<String, List<ScheduleJobDetails>>> sortedGroups = groupedJobs.entrySet().stream()
                .sorted((left, right) -> {
                    int compare = Integer.compare(right.getValue().size(), left.getValue().size());
                    if (compare != 0) {
                        return compare;
                    }
                    return left.getKey().compareTo(right.getKey());
                })
                .collect(Collectors.toList());

        List<ScheduleJobDetails> pendingSmallGroupJobs = Lists.newArrayList();
        for (Map.Entry<String, List<ScheduleJobDetails>> groupedJobsEntry : sortedGroups) {
            String groupKey = groupedJobsEntry.getKey();
            List<ScheduleJobDetails> groupJobs = groupedJobsEntry.getValue();
            if (groupJobs.size() >= aliveNodes.size()) {
                if (CollectionUtils.isNotEmpty(pendingSmallGroupJobs)) {
                    distributeSmallGroupJobs(pendingSmallGroupJobs, aliveNodes, nodeLoad, groupLoadSnapshot, jobNodeMap);
                    pendingSmallGroupJobs.clear();
                }
                distributeLargeGroupJobs(groupKey, groupJobs, aliveNodes, nodeLoad, groupLoadSnapshot.get(groupKey), jobNodeMap);
            } else {
                pendingSmallGroupJobs.addAll(groupJobs);
                if (pendingSmallGroupJobs.size() >= aliveNodes.size()) {
                    distributeSmallGroupJobs(pendingSmallGroupJobs, aliveNodes, nodeLoad, groupLoadSnapshot, jobNodeMap);
                    pendingSmallGroupJobs.clear();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(pendingSmallGroupJobs)) {
            distributeSmallGroupJobs(pendingSmallGroupJobs, aliveNodes, nodeLoad, groupLoadSnapshot, jobNodeMap);
        }
    }

    private void writeBackLoadSnapshot(List<String> aliveNodes,
                                       Map<String, Long> nodeLoad,
                                       Map<String, Map<String, Long>> groupLoadSnapshot,
                                       Map<String, Long> globalNodeLoadCache,
                                       Map<String, Map<String, Long>> globalGroupNodeLoadCache) {
        for (String aliveNode : aliveNodes) {
            globalNodeLoadCache.put(aliveNode, nodeLoad.getOrDefault(aliveNode, 0L));
        }
        for (Map.Entry<String, Map<String, Long>> groupLoadEntry : groupLoadSnapshot.entrySet()) {
            globalGroupNodeLoadCache.put(groupLoadEntry.getKey(), new ConcurrentHashMap<>(groupLoadEntry.getValue()));
        }
    }

    private String buildTaskTypeCycTimeGroupKey(ScheduleJob scheduleJob) {
        Integer taskType = scheduleJob.getTaskType() == null ? -1 : scheduleJob.getTaskType();
        String cycTime = StringUtils.defaultString(scheduleJob.getCycTime());
        String cycMinute = cycTime.length() > 12 ? cycTime.substring(0, 12) : cycTime;
        return taskType + "_" + cycMinute;
    }

    private void distributeSmallGroupJobs(List<ScheduleJobDetails> jobs,
                                          List<String> aliveNodes,
                                          Map<String, Long> nodeLoad,
                                          Map<String, Map<String, Long>> groupLoadSnapshot,
                                          Map<ScheduleJobDetails, String> jobNodeMap) {
        for (ScheduleJobDetails scheduleBatchJob : jobs) {
            String groupKey = buildTaskTypeCycTimeGroupKey(scheduleBatchJob.getScheduleJob());
            Map<String, Long> groupLoad = groupLoadSnapshot.computeIfAbsent(groupKey, key -> {
                Map<String, Long> initLoad = Maps.newHashMapWithExpectedSize(aliveNodes.size());
                for (String node : aliveNodes) {
                    initLoad.put(node, 0L);
                }
                return initLoad;
            });
            String nodeAddress = findMinLoadNodeForGroup(aliveNodes, groupLoad, nodeLoad);
            jobNodeMap.put(scheduleBatchJob, nodeAddress);
            nodeLoad.put(nodeAddress, nodeLoad.get(nodeAddress) + 1L);
            groupLoad.put(nodeAddress, groupLoad.get(nodeAddress) + 1L);
        }
    }

    private void distributeLargeGroupJobs(String groupKey,
                                          List<ScheduleJobDetails> groupJobs,
                                          List<String> aliveNodes,
                                          Map<String, Long> nodeLoad,
                                          Map<String, Long> groupLoad,
                                          Map<ScheduleJobDetails, String> jobNodeMap) {
        for (ScheduleJobDetails scheduleBatchJob : groupJobs) {
            String nodeAddress = findMinLoadNodeForGroup(aliveNodes, groupLoad, nodeLoad);
            jobNodeMap.put(scheduleBatchJob, nodeAddress);
            nodeLoad.put(nodeAddress, nodeLoad.get(nodeAddress) + 1L);
            groupLoad.put(nodeAddress, groupLoad.get(nodeAddress) + 1L);
        }
    }

    private String findMinLoadNodeForGroup(List<String> aliveNodes, Map<String, Long> groupLoad, Map<String, Long> nodeLoad) {
        String minLoadNode = aliveNodes.get(0);
        long minGroupLoad = groupLoad.get(minLoadNode);
        long minGlobalLoad = nodeLoad.get(minLoadNode);
        for (int i = 1; i < aliveNodes.size(); i++) {
            String currentNode = aliveNodes.get(i);
            long currentGroupLoad = groupLoad.get(currentNode);
            long currentGlobalLoad = nodeLoad.get(currentNode);
            if (currentGroupLoad < minGroupLoad
                    || (currentGroupLoad == minGroupLoad && currentGlobalLoad < minGlobalLoad)
                    || (currentGroupLoad == minGroupLoad && currentGlobalLoad == minGlobalLoad && currentNode.compareTo(minLoadNode) < 0)) {
                minLoadNode = currentNode;
                minGroupLoad = currentGroupLoad;
                minGlobalLoad = currentGlobalLoad;
            }
        }
        return minLoadNode;
    }
}
