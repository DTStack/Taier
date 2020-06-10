package com.dtstack.engine.master.data;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.anno.DatabaseDeleteOperation;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import org.springframework.stereotype.Component;

@Component
public class DataCollection {

    @DatabaseInsertOperation(dao = ScheduleJobDao.class, method = "insert")
    @DatabaseDeleteOperation(dao = TestScheduleJobDao.class, method = "deleteById", field = "id")
    public ScheduleJob getScheduleJob() {
        ScheduleJob sj = new ScheduleJob();
        sj.setId(-10000L);
        sj.setStatus(5);
        sj.setJobId("dhsd_3432_iekd_56jfnv");
        sj.setTenantId(15L);
        sj.setProjectId(-1L);
        sj.setJobKey("test_scheduleJob_0010");
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
