package com.dtstack.engine.master.data;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.anno.DatabaseDeleteOperation;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.utils.ValueUtils;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class DataCollection {

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobFirst() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedId());
        sj.setStatus(5);
        sj.setJobId(ValueUtils.changedStr("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStr("jobKey"));
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setTaskId(-1L);
        sj.setJobName("Python");
        sj.setCreateUserId(0L);
        sj.setIsDeleted(0);
        sj.setBusinessDate("20200608234500");
        sj.setCycTime("20200609234500");
        sj.setTaskType(0);
        sj.setAppType(0);
        sj.setType(2);
        sj.setIsRestart(0);
        sj.setDependencyType(0);
        sj.setFlowJobId("0");
        sj.setPeriodType(0);
        sj.setMaxRetryNum(0);
        sj.setRetryNum(0);
        sj.setComputeType(1);

        return sj;
    }

    @DatabaseInsertOperation(dao = TestScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJobSecond() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(ValueUtils.changedId());
        sj.setStatus(5);
        sj.setJobId(ValueUtils.changedStr("jobId"));
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey(ValueUtils.changedStr("jobKey"));
        sj.setExecStartTime(new Timestamp(1591805197000L));
        sj.setExecEndTime(new Timestamp(1591805197100L));
        sj.setTaskId(-1L);
        sj.setJobName("Python");
        sj.setCreateUserId(0L);
        sj.setIsDeleted(0);
        sj.setBusinessDate("20200608234500");
        sj.setCycTime("20200609234500");
        sj.setTaskType(0);
        sj.setAppType(0);
        sj.setType(2);
        sj.setIsRestart(0);
        sj.setDependencyType(0);
        sj.setFlowJobId("0");
        sj.setPeriodType(0);
        sj.setMaxRetryNum(0);
        sj.setRetryNum(0);
        sj.setComputeType(1);

        return sj;
    }
}
