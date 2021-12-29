package com.dtstack.engine.master.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtstack.engine.common.enums.ForceCancelFlag;
import com.dtstack.engine.common.enums.IsDeletedEnum;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.Restarted;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.mapper.ScheduleJobMapper;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.pluginapi.enums.RdosTaskStatus;
import com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Auther: dazhi
 * @Date: 2021/12/28 8:14 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Service("job")
public class ScheduleJobService extends ServiceImpl<ScheduleJobMapper, ScheduleJob> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleJobService.class);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private ScheduleJobExpandService scheduleJobExpandService;

    @Autowired
    private ScheduleJobOperatorRecordService scheduleJobOperatorRecordService;

    /**
     * 批量设置实例状态
     *
     * @param resumeBatchJobs 实例集合
     */
    public void batchRestartScheduleJob(Map<String, String> resumeBatchJobs) {
        if (MapUtils.isNotEmpty(resumeBatchJobs)) {
            List<String> restartJobId = new ArrayList<>(resumeBatchJobs.size());
            resumeBatchJobs.entrySet()
                    .stream()
                    .sorted(Comparator.nullsFirst(Map.Entry.comparingByValue(Comparator.nullsFirst(String::compareTo))))
                    .forEachOrdered(v -> {
                        if (null!= v && StringUtils.isNotBlank(v.getKey())) {
                            restartJobId.add(v.getKey());
                        }
                    });

            List<List<String>> partition = Lists.partition(restartJobId, environmentContext.getRestartOperatorRecordMaxSize());
            for (List<String> scheduleJobs : partition) {
                Set<String> jobIds = new HashSet<>(scheduleJobs.size());
                Set<ScheduleJobOperatorRecord> records = new HashSet<>(scheduleJobs.size());
                //更新任务为重跑任务--等待调度器获取并执行
                for (String jobId : scheduleJobs) {
                    jobIds.add(jobId);
                    ScheduleJobOperatorRecord record = new ScheduleJobOperatorRecord();
                    record.setJobId(jobId);
                    record.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
                    record.setOperatorType(OperatorType.RESTART.getType());
                    record.setNodeAddress(environmentContext.getLocalAddress());
                    records.add(record);
                }

                ScheduleJob scheduleJob = new ScheduleJob();
                scheduleJob.setStatus(RdosTaskStatus.UNSUBMIT.getStatus());
                scheduleJob.setPhaseStatus(JobPhaseStatus.CREATE.getCode());
                scheduleJob.setIsRestart(JobPhaseStatus.CREATE.getCode());
                scheduleJob.setNodeAddress(environmentContext.getLocalAddress());

                // 更新状态
                 this.lambdaUpdate()
                        .eq(ScheduleJob::getIsDeleted, IsDeletedEnum.NOT_DELETE.getType())
                        .in(ScheduleJob::getJobId, jobIds)
                        .update(scheduleJob);

                 // 清除日志
                scheduleJobExpandService.clearData(jobIds);
                scheduleJobOperatorRecordService.saveBatch(records);
                LOGGER.info("reset job {}", jobIds);
            }
        }
    }

    public Integer updateJobStatusAndExecTime(String jobId, Integer status) {
        if (StringUtils.isNotBlank(jobId) && status != null) {
            return this.baseMapper.updateJobStatusAndExecTime(jobId, status);
        }
        return 0;
    }
}
