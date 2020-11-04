package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.EScheduleType;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.enums.JobPhaseStatus;
import com.dtstack.schedule.common.enums.Restarted;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 重跑任务的执行器
 */
@Component
public class RestartJobExecutor extends AbstractJobExecutor {

    private final Logger logger = LoggerFactory.getLogger(RestartJobExecutor.class);

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.RESTART;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        logger.info("---stop RestartJobExecutor----");
    }

    @Override
    protected List<ScheduleBatchJob> listExecJob(Long startId, String nodeAddress, String cycStartTime, String cycEndTime, Boolean isEq) {
        //添加需要重跑的数据
        return getRestartDataJob(cycStartTime);
    }

    protected List<ScheduleBatchJob> getRestartDataJob(String cycStartTime) {
        Timestamp lasTime = null;
        if (!StringUtils.isBlank(cycStartTime)) {
            DateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            try {
                Date parse = sdf.parse(cycStartTime);
                if (null != parse) {
                    lasTime = new Timestamp(parse.getTime());
                }
            } catch (ParseException e) {
                logger.error("getRestartDataJob {} error ", cycStartTime, e);
            }
        }
        if (null == lasTime) {
            lasTime = new Timestamp(DateTime.now().withTime(0, 0, 0, 0).getMillis());
        }
        //重跑查询补数据和周期调度的
        List<ScheduleJob> scheduleJobs = scheduleJobDao.listRestartBatchJobList(null, lasTime, JobPhaseStatus.CREATE.getCode());
        return getScheduleBatchJobList(scheduleJobs);
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        return batchJobService.getListMinId(nodeAddress, null, null, null, Restarted.RESTARTED.getStatus());
    }
}
