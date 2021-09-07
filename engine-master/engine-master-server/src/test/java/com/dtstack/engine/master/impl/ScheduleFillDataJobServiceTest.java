package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleFillDataJob;
import com.dtstack.engine.api.domain.Tenant;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.common.enums.AppType;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;


import com.dtstack.engine.master.AbstractTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author basion
 * @Classname ScheduleFillDataJobServiceTest
 * @Description unit test for ScheduleFillDataJobService
 * @Date 2020-11-26 17:01:53
 * @Created basion
 */
public class ScheduleFillDataJobServiceTest extends AbstractTest {

    @Autowired
    private ScheduleFillDataJobService scheduleFillDataJobService;

    /**
     * do some mock before test
     */
    @Before
    public void setup() throws Exception {
        //TODO
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testCheckExistsName() {
        ScheduleFillDataJob defaultScheduleFillDataJob = DataCollection.getData().getDefaultScheduleFillDataJob();
        boolean checkExistsName = scheduleFillDataJobService.checkExistsName(defaultScheduleFillDataJob.getJobName(), defaultScheduleFillDataJob.getProjectId());
        Assert.assertTrue(checkExistsName);
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testGetFillJobList() {
        ScheduleFillDataJob defaultScheduleFillDataJob = DataCollection.getData().getDefaultScheduleFillDataJob();
        List<String> jobNames = Lists.newArrayList(defaultScheduleFillDataJob.getJobName());
        List<ScheduleFillDataJob> getFillJobList = scheduleFillDataJobService.getFillJobList(jobNames, defaultScheduleFillDataJob.getProjectId());
        Assert.assertTrue(CollectionUtils.isNotEmpty(getFillJobList));
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testSaveData() {
        Tenant defaultTenant = DataCollection.getData().getDefaultTenant();
        ScheduleFillDataJob saveData = scheduleFillDataJobService.saveData("testSave", defaultTenant.getId(), 1L, "2020-10-20", "2020-10-20", "2020-10-20", 1L, AppType.RDOS.getType(), defaultTenant.getDtUicTenantId());
        Assert.assertNotNull(saveData);
    }
}
