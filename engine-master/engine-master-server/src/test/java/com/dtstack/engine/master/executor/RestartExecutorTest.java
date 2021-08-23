package com.dtstack.engine.master.executor;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobOperatorRecord;
import com.dtstack.engine.common.enums.OperatorType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.env.EnvironmentContext;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobOperatorRecordDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.ForceCancelFlag;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yuebai
 * @date 2021-07-09
 */
public class RestartExecutorTest extends AbstractTest {

    @Autowired
    private RestartJobExecutor restartJobExecutor;

    @Autowired
    private ScheduleJobOperatorRecordDao scheduleJobOperatorRecordDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private EnvironmentContext environmentContext;

    @Test
    public void testRestart(){
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobDao.insert(scheduleJobTemplate);

        ScheduleJobOperatorRecord scheduleJobOperatorRecord = new ScheduleJobOperatorRecord();
        scheduleJobOperatorRecord.setNodeAddress(environmentContext.getLocalAddress());
        scheduleJobOperatorRecord.setOperatorType(OperatorType.RESTART.getType());
        scheduleJobOperatorRecord.setJobId(scheduleJobTemplate.getJobId());
        scheduleJobOperatorRecord.setForceCancelFlag(ForceCancelFlag.NO.getFlag());
        scheduleJobOperatorRecordDao.insert(scheduleJobOperatorRecord);
        scheduleJobDao.updateJobStatus(scheduleJobTemplate.getJobId(), RdosTaskStatus.RUNNING.getStatus());
        restartJobExecutor.listExecJob(0L, environmentContext.getLocalAddress(), null);
    }
}
