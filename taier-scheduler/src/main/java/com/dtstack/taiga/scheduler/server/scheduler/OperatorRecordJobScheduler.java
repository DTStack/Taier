package com.dtstack.taiga.scheduler.server.scheduler;

import com.dtstack.taiga.common.enums.OperatorType;
import com.dtstack.taiga.dao.domain.ScheduleJob;
import com.dtstack.taiga.dao.domain.ScheduleJobJob;
import com.dtstack.taiga.dao.domain.ScheduleJobOperatorRecord;
import com.dtstack.taiga.scheduler.server.ScheduleJobDetails;
import com.dtstack.taiga.scheduler.server.scheduler.exec.JudgeJobExecOperator;
import com.dtstack.taiga.scheduler.service.ScheduleJobJobService;
import com.dtstack.taiga.scheduler.service.ScheduleJobOperatorRecordService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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

    private final Logger LOGGER = LoggerFactory.getLogger(OperatorRecordJobScheduler.class);

    @Autowired
    private ScheduleJobJobService scheduleJobJobService;

    @Autowired
    private List<JudgeJobExecOperator> judgeJobExecOperators;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    @Override
    protected List<ScheduleJobDetails> listExecJob(Long startSort, String nodeAddress, Boolean isEq) {
        List<ScheduleJobOperatorRecord> records = scheduleJobOperatorRecordService.listOperatorRecord(startSort, nodeAddress, getOperatorType().getType(), isEq);

        if (CollectionUtils.isNotEmpty(records)) {
            Set<String> jobIds = records.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toSet());
            List<ScheduleJob> scheduleJobList = getScheduleJob(jobIds);

            if (CollectionUtils.isNotEmpty(scheduleJobList)) {
                List<String> jodExecIds = scheduleJobList.stream().map(ScheduleJob::getJobId).collect(Collectors.toList());
                if (jobIds.size() != scheduleJobList.size()) {
                    // 过滤出来已经提交运行的实例，删除操作记录
                    List<String> deleteJobIdList = jobIds.stream().filter(jobId -> !jodExecIds.contains(jobId)).collect(Collectors.toList());
                    removeOperatorRecord(deleteJobIdList);
                }

                List<String> jobKeys = scheduleJobList.stream().map(ScheduleJob::getJobKey).collect(Collectors.toList());
                List<ScheduleJobJob> scheduleJobJobList = scheduleJobJobService.listByJobKeys(jobKeys);
                Map<String, List<ScheduleJobJob>> jobJobMap = scheduleJobJobList.stream().collect(Collectors.groupingBy(ScheduleJobJob::getJobKey));
                List<ScheduleJobDetails> scheduleJobDetailsList = new ArrayList<>(scheduleJobList.size());

                for (ScheduleJob scheduleJob : scheduleJobList) {
                    ScheduleJobDetails scheduleJobDetails = new ScheduleJobDetails();
                    scheduleJobDetails.setScheduleJob(scheduleJob);
                    scheduleJobDetails.setJobJobList(jobJobMap.get(scheduleJob.getJobKey()));
                    scheduleJobDetailsList.add(scheduleJobDetails);
                }
                return scheduleJobDetailsList;
            }
        }
        return Lists.newArrayList();
    }

    /**
     * 删除没有用的操作记录
     *
     * @param deleteJobIdList 实例id
     */
    private void removeOperatorRecord(List<String> deleteJobIdList) {
        scheduleJobOperatorRecordService.lambdaUpdate().in(ScheduleJobOperatorRecord::getJobId,deleteJobIdList).remove();
    }

    /**
     * 获得operator类型
     * @return 类型值
     */
    public abstract OperatorType getOperatorType();

    /**
     * 查询实例方法
     * @param jobIds 实例id
     * @return 实例
     */
    protected abstract List<ScheduleJob> getScheduleJob(Set<String> jobIds);
}
