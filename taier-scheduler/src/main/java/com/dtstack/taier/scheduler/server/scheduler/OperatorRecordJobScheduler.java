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

package com.dtstack.taier.scheduler.server.scheduler;

import com.dtstack.taier.common.enums.Deleted;
import com.dtstack.taier.common.enums.OperatorType;
import com.dtstack.taier.dao.domain.ScheduleJobCache;
import com.dtstack.taier.dao.domain.ScheduleJob;
import com.dtstack.taier.dao.domain.ScheduleJobJob;
import com.dtstack.taier.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taier.pluginapi.enums.TaskStatus;
import com.dtstack.taier.scheduler.server.ScheduleJobDetails;
import com.dtstack.taier.scheduler.service.ScheduleJobCacheService;
import com.dtstack.taier.scheduler.service.ScheduleJobJobService;
import com.dtstack.taier.scheduler.service.ScheduleJobOperatorRecordService;
import com.dtstack.taier.scheduler.service.ScheduleJobService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Auther: dazhi
 * @Date: 2022/1/10 7:02 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public abstract class OperatorRecordJobScheduler extends AbstractJobSummitScheduler {

    @Autowired
    protected ScheduleJobService scheduleJobService;

    @Autowired
    protected ScheduleJobJobService scheduleJobJobService;

    @Autowired
    protected ScheduleJobCacheService scheduleJobCacheService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    private Long operatorRecordStartId = 0L;

    @Override
    protected List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq) {
        List<ScheduleJobOperatorRecord> records = scheduleJobOperatorRecordService.listOperatorRecord(operatorRecordStartId, nodeAddress, getOperatorType().getType(), isEq);
        //empty
        if (CollectionUtils.isEmpty(records)) {
            operatorRecordStartId = 0L;
            return new ArrayList<>();
        }

        Set<String> jobIds = records.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toSet());
        List<ScheduleJob> scheduleJobList = getScheduleJob(jobIds);

        if (CollectionUtils.isEmpty(scheduleJobList)) {
            operatorRecordStartId = 0L;
            removeOperatorRecord(Lists.newArrayList(jobIds));
        }

        //set max
        records.stream().max(Comparator.comparing(ScheduleJobOperatorRecord::getId))
                .ifPresent(scheduleJobOperatorRecord -> operatorRecordStartId = scheduleJobOperatorRecord.getId());

        if (jobIds.size() != scheduleJobList.size()) {
            List<String> jodExecIds = scheduleJobList.stream().map(ScheduleJob::getJobId).collect(Collectors.toList());
            // 过滤出来已经提交运行的实例，删除操作记录
            List<String> deleteJobIdList = jobIds.stream().filter(jobId -> !jodExecIds.contains(jobId)).collect(Collectors.toList());
            removeOperatorRecord(deleteJobIdList);
        }

        List<String> jobKeys = scheduleJobList.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
        List<ScheduleJobJob> scheduleJobJobList = scheduleJobJobService.listByJobKeys(jobKeys);
        Map<String, List<ScheduleJobJob>> jobJobMap = scheduleJobJobList.stream().collect(Collectors.groupingBy(ScheduleJobJob::getJobKey));

        return scheduleJobList.stream().map(scheduleJob -> {
            ScheduleJobDetails scheduleJobDetails = new ScheduleJobDetails();
            scheduleJobDetails.setScheduleJob(scheduleJob);
            scheduleJobDetails.setJobJobList(jobJobMap.get(scheduleJob.getJobKey()));
            return scheduleJobDetails;
        }).collect(Collectors.toList());
    }

    /**
     * 删除没有用的操作记录
     *
     * @param deleteJobIdList 实例id
     */
    private void removeOperatorRecord(List<String> deleteJobIdList) {
        // 删除OperatorRecord记录时，需要考虑的问题
        // 1. 因为入jobId已经对应的实例已经提交状态，未提交的id不会进入
        // 2. 就是cache是空的情况下，有两种可能性
        //      第一就是还没有创建cache，这个时候Operator不能删
        //      第二是job运行完成后，这个时候Operator需要删除

        // 查询cache表的数据
        Map<String, ScheduleJobCache> scheduleEngineJobCacheMaps = scheduleJobCacheService
                .lambdaQuery()
                .in(ScheduleJobCache::getJobId, deleteJobIdList)
                .eq(ScheduleJobCache::getIsDeleted, Deleted.NORMAL.getStatus())
                .list()
                .stream()
                .collect(Collectors.toMap(ScheduleJobCache::getJobId, g -> (g)));

        // 查询需要删除的OperatorRecord的实例信息
        Map<String, ScheduleJob> scheduleJobMap = scheduleJobService
                .lambdaQuery()
                .in(ScheduleJob::getJobId, deleteJobIdList)
                .eq(ScheduleJob::getIsDeleted, Deleted.NORMAL.getStatus())
                .list()
                .stream()
                .collect(Collectors.toMap(ScheduleJob::getJobId, g -> (g)));
        List<String> needDeleteJobIdList = Lists.newArrayList();

        for (String jobId : deleteJobIdList) {
            ScheduleJobCache scheduleJobCache = scheduleEngineJobCacheMaps.get(jobId);

            if (scheduleJobCache != null) {
                // cache是空的 两种情况 就是上面2的两种情况，这时我们需要判断job的状态
                ScheduleJob scheduleJob = scheduleJobMap.get(jobId);

                // 如果周期实例是停止状态，那说明job运行完成后，这个时候Operator需要删除
                if (scheduleJob != null && TaskStatus.STOPPED_STATUS.contains(scheduleJob.getStatus())) {
                    needDeleteJobIdList.add(jobId);
                }

                if (scheduleJob == null) {
                    // 实例查询不到的情况，一般不会出现，但是出来的OperatorRecord也是没有存在的必要，所以需要直接删除
                    needDeleteJobIdList.add(jobId);
                }

            } else {
                if (TaskStatus.STOPPED_STATUS.contains(scheduleJobMap.get(jobId).getStatus())) {
                    // 任务停止才删除
                    needDeleteJobIdList.add(jobId);
                }
            }
        }

        // 删除OperatorRecord记录
        if (CollectionUtils.isNotEmpty(needDeleteJobIdList)) {
            scheduleJobOperatorRecordService
                    .lambdaUpdate()
                    .in(ScheduleJobOperatorRecord::getJobId, needDeleteJobIdList)
                    .remove();
        }
    }

    /**
     * 获得operator类型
     *
     * @return 类型值
     */
    public abstract OperatorType getOperatorType();

    /**
     * 查询实例方法
     *
     * @param jobIds 实例id
     * @return 实例
     */
    protected abstract List<ScheduleJob> getScheduleJob(Set<String> jobIds);
}
