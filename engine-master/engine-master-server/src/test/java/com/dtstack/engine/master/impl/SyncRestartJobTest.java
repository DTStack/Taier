package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleJobJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.vo.ScheduleJobKillJobVO;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.dao.ScheduleJobJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;

/**
 * @author yuebai
 * @date 2021-02-03
 */
public class SyncRestartJobTest extends AbstractTest {

    @Autowired
    private ScheduleJobService scheduleJobService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ScheduleJobJobDao scheduleJobJobDao;

    @Autowired
    private ScheduleJobDao scheduleJobDao;

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Test
    @Rollback
    @Transactional
    public void testSyncRestartRepeat(){
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        String key = "syncRestartJob" + scheduleJobTemplate.getId();
        redisTemplate.opsForValue().set(key,"-1");
        boolean syncFlag = scheduleJobService.syncRestartJob(scheduleJobTemplate.getId(), true, false, null);
        Assert.assertTrue(syncFlag);

    }


    @Test
    @Rollback
    public void testSyncJob(){
        ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShadeTemplate.setTaskId(-10L);
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        ScheduleJob scheduleJobTemplateParent = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplateParent.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplateParent.setJobKey("cronTrigger_5251_20210203204000");
        scheduleJobTemplate.setJobKey("cronTrigger_5250_20210203204000");
        scheduleJobTemplateParent.setJobId("sc12asxa");
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey(scheduleJobTemplate.getJobKey());
        scheduleJobJob.setParentJobKey(scheduleJobTemplateParent.getJobKey());
        scheduleJobJob.setProjectId(scheduleJobTemplate.getProjectId());
        scheduleJobJob.setTenantId(scheduleJobTemplate.getTenantId());
        scheduleJobJob.setDtuicTenantId(scheduleJobTemplate.getDtuicTenantId());
        scheduleJobJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleJobJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleJobJob.setAppType(scheduleJobTemplate.getAppType());
        scheduleJobDao.insert(scheduleJobTemplate);
        scheduleJobDao.insert(scheduleJobTemplateParent);
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(scheduleTaskShadeTemplate, scheduleTaskShadeDTO);
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
        scheduleJobJobDao.insert(scheduleJobJob);
        String key = "syncRestartJob" + scheduleJobTemplate.getId();
        redisTemplate.delete(key);
        boolean syncFlag = scheduleJobService.syncRestartJob(scheduleJobTemplateParent.getId(), false, false, null);
        Assert.assertTrue(syncFlag);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Integer sonStatus = scheduleJobDao.getStatusByJobId(scheduleJobTemplate.getJobId());
        Assert.assertNotEquals(sonStatus,scheduleJobTemplate.getStatus());
        Integer parentStatus = scheduleJobDao.getStatusByJobId(scheduleJobTemplateParent.getJobId());
        Assert.assertNotEquals(parentStatus,scheduleJobTemplateParent.getStatus());
    }



    @Test
    @Rollback
    public void testSyncJustSelfJob(){
        ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShadeTemplate.setTaskId(-11L);
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        ScheduleJob scheduleJobTemplateParent = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplateParent.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplateParent.setJobKey("cronTrigger_5221_20210203204000");
        scheduleJobTemplate.setJobKey("cronTrigger_5220_20211203204000");
        scheduleJobTemplateParent.setJobId("sc12as1xa");
        scheduleJobTemplate.setJobId("sc12as1xa1");
        ScheduleJobJob scheduleJobJob = new ScheduleJobJob();
        scheduleJobJob.setJobKey(scheduleJobTemplate.getJobKey());
        scheduleJobJob.setParentJobKey(scheduleJobTemplateParent.getJobKey());
        scheduleJobJob.setProjectId(scheduleJobTemplate.getProjectId());
        scheduleJobJob.setTenantId(scheduleJobTemplate.getTenantId());
        scheduleJobJob.setDtuicTenantId(scheduleJobTemplate.getDtuicTenantId());
        scheduleJobJob.setGmtModified(new Timestamp(System.currentTimeMillis()));
        scheduleJobJob.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        scheduleJobJob.setAppType(scheduleJobTemplate.getAppType());
        scheduleJobDao.insert(scheduleJobTemplate);
        scheduleJobDao.insert(scheduleJobTemplateParent);
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(scheduleTaskShadeTemplate, scheduleTaskShadeDTO);
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
        scheduleJobJobDao.insert(scheduleJobJob);
        String key = "syncRestartJob" + scheduleJobTemplate.getId();
        redisTemplate.delete(key);
        boolean syncFlag = scheduleJobService.syncRestartJob(scheduleJobTemplateParent.getId(), true, false, null);
        Assert.assertTrue(syncFlag);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer sonStatus = scheduleJobDao.getStatusByJobId(scheduleJobTemplate.getJobId());
        Assert.assertEquals(sonStatus,scheduleJobTemplate.getStatus());
        Integer parentStatus = scheduleJobDao.getStatusByJobId(scheduleJobTemplateParent.getJobId());
        Assert.assertNotEquals(parentStatus,scheduleJobTemplateParent.getStatus());
    }


    @Test
    @Rollback
    public void testSetSuccess(){
        ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShadeTemplate.setTaskId(-12L);
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplate.setJobKey("cronTrigger_5210_20210203204000");
        scheduleJobTemplate.setJobId("sc12as1xa12");
        scheduleJobDao.insert(scheduleJobTemplate);
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(scheduleTaskShadeTemplate, scheduleTaskShadeDTO);
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
        String key = "syncRestartJob" + scheduleJobTemplate.getId();
        redisTemplate.delete(key);
        boolean syncFlag = scheduleJobService.syncRestartJob(scheduleJobTemplate.getId(), true, true, null);
        Assert.assertTrue(syncFlag);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Integer sonStatus = scheduleJobDao.getStatusByJobId(scheduleJobTemplate.getJobId());
        Assert.assertEquals(RdosTaskStatus.MANUALSUCCESS.getStatus(),sonStatus);
    }

    @Test
    @Rollback
    public void testStopByCondition(){
        ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        scheduleTaskShadeTemplate.setTaskId(-13L);
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setTaskId(scheduleTaskShadeTemplate.getTaskId());
        scheduleJobTemplate.setJobKey("cronTrigger_5220_20210203204000");
        scheduleJobTemplate.setJobId("sc12as1xa123");
        scheduleJobDao.insert(scheduleJobTemplate);
        ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(scheduleTaskShadeTemplate, scheduleTaskShadeDTO);
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
        ScheduleJobKillJobVO vo = new ScheduleJobKillJobVO();
        vo.setProjectId(scheduleJobTemplate.getProjectId());
        vo.setDtuicTenantId(scheduleJobTemplate.getDtuicTenantId());
        vo.setBizStartDay(DateTime.now().plusDays(-1).getMillis()/1000);
        vo.setBizEndDay(DateTime.now().plusDays(1).getMillis()/1000);
        vo.setTaskIds(Lists.newArrayList(-13L));
        scheduleJobService.stopJobByCondition(vo);
    }

    @Test
    @Rollback
    public void testGetSubFlow() {
        ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
        scheduleJobTemplate.setTaskId(-14L);
        scheduleJobTemplate.setJobKey("cronTrigger_5220_20210203204000");
        scheduleJobTemplate.setJobId("adas121");
        scheduleJobTemplate.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleJobDao.insert(scheduleJobTemplate);

        ScheduleJob subJob = Template.getScheduleJobTemplate();
        subJob.setTaskId(-15L);
        subJob.setJobKey("cronTrigger_5221_20210203204000");
        subJob.setJobId("subJob");
        subJob.setTaskType(EScheduleJobType.SHELL.getType());
        subJob.setFlowJobId(scheduleJobTemplate.getJobId());
        scheduleJobDao.insert(subJob);

        Assert.assertTrue(CollectionUtils.isEmpty(scheduleJobDao.getSubJobsAndStatusByFlowId("0")));
        Assert.assertEquals(scheduleJobDao.getSubJobsAndStatusByFlowId(scheduleJobTemplate.getJobId()).size(),1);
        Assert.assertEquals(scheduleJobDao.getSubJobsAndStatusByFlowId(subJob.getJobId()).size(),0);
    }
}
