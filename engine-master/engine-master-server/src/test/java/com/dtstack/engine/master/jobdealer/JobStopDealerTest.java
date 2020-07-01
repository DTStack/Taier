package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.BaseTest;
import com.dtstack.engine.master.jobdealer.JobStopDealer;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class JobStopDealerTest extends BaseTest {

    @Autowired
    private JobStopDealer jobStopDealer;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Test
    public void testAddStopFunction() {
        jobStopDealer.addStopJobs(getResourcesFromTaskId());
    }

    private List<ScheduleJob> getResourcesFromJobId() {
        List<Long> jobIdList = new ArrayList<>();
        jobIdList.add(50045L);
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.listByJobIds(jobIdList));
        return jobs;
    }

    private List<ScheduleJob> getResourcesFromTaskId() {
        List<String> taskIdsList = new ArrayList<>();
        taskIdsList.add("0243ba9f");
        List<ScheduleJob> jobs = new ArrayList<>(scheduleJobDao.getRdosJobByJobIds(taskIdsList));
        return jobs;
    }
}
