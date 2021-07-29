package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.schedule.common.enums.Restarted;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * 重跑任务的执行器
 */
@Component
public class RestartJobExecutor extends AbstractJobExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(RestartJobExecutor.class);

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
        return getRestartDataJob(startId, nodeAddress, isEq);
    }

    protected List<ScheduleBatchJob> getRestartDataJob(Long startId, String nodeAddress, Boolean isEq) {
        //重跑不关心cycStart时间 只关心modifyTime
        Timestamp lasTime = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
        //重跑查询补数据和周期调度的
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listExecJobByCycTimeTypeAddress(startId, nodeAddress, null, null, null,
                JobPhaseStatus.CREATE.getCode(), isEq, lasTime, Restarted.RESTARTED.getStatus());
        LOGGER.info("getRestartDataJob scheduleType {} nodeAddress {} start scanning since when startId:{}  isEq {} queryJobSize {}. lastTime {}", getScheduleType(), nodeAddress, startId, isEq,
                scheduleJobs.size(),lasTime.getTime());
        return getScheduleBatchJobList(scheduleJobs);
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        Pair<String, String> cycTime = this.getCycTime(true);
        Long listMinId = batchJobService.getListMinId(nodeAddress, null, cycTime.getLeft(), cycTime.getRight(), Restarted.RESTARTED.getStatus());
        LOGGER.info("getListMinId scheduleType {} nodeAddress {} isRestart {} lastMinId is {} .", getScheduleType(), nodeAddress, Restarted.RESTARTED.getStatus(), listMinId);
        return Objects.isNull(listMinId) ? 0L: listMinId;
    }

    public Pair<String, String> getCycTime(boolean minJobId) {
        // 重跑
        if(environmentContext.getOpenRestartDataCycTimeLimit()) {
            return jobRichOperator.getCycTimeLimitEndNow(false,true, minJobId);
        }
        return new ImmutablePair<>(null, null);
    }
}
