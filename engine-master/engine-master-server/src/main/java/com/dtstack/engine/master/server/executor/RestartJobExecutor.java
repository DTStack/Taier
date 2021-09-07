package com.dtstack.engine.master.server.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.server.ScheduleBatchJob;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.engine.master.impl.ScheduleJobService;
import com.dtstack.engine.common.enums.Restarted;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 重跑任务的执行器
 */
@Component
public class RestartJobExecutor extends AbstractJobExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(RestartJobExecutor.class);

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.RESTART;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop RestartJobExecutor----");
    }

    @Override
    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, Boolean isEq) {
        //添加需要重跑的数据
        List<ScheduleJobOperatorRecord> records = scheduleJobOperatorRecordDao.listJobs(startId, nodeAddress, OperatorType.RESTART.getType());
        if (CollectionUtils.isEmpty(records)) {
            return new ArrayList<>();
        }
        List<String> jobIds = records.stream().map(ScheduleJobOperatorRecord::getJobId).collect(Collectors.toList());
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByJobIds(nodeAddress, JobPhaseStatus.CREATE.getCode(), Restarted.RESTARTED.getStatus(), jobIds);
        LOGGER.info("getRestartDataJob nodeAddress {} start scanning since when startId:{}  queryJobSize {} ", nodeAddress, startId, scheduleJobs.size());
        if(jobIds.size() > scheduleJobs.size()){
            //check lost operator records can remove
            Set<String> needSubmit = scheduleJobs.stream().map(ScheduleJob::getJobId).collect(Collectors.toSet());
            jobIds.removeAll(needSubmit);
            scheduleJobService.removeOperatorRecord(jobIds,records);
        }
        return getScheduleBatchJobList(scheduleJobs);
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        return 0L;
    }
}
