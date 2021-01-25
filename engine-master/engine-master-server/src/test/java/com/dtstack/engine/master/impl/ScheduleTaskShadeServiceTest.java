package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.EProjectScheduleStatus;
import com.dtstack.schedule.common.enums.ESubmitStatus;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-11-26
 */
public class ScheduleTaskShadeServiceTest extends AbstractTest {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    ScheduleTaskShade scheduleTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
    ScheduleTaskShadeDTO scheduleTaskShadeDTO = new ScheduleTaskShadeDTO();

    @Before
    public void insert() {
        scheduleTaskShadeTemplate.setProjectScheduleStatus(null);
        scheduleTaskShadeTemplate.setNodePid(null);
        scheduleTaskShadeTemplate.setFlowId(null);
        BeanUtils.copyProperties(scheduleTaskShadeTemplate, scheduleTaskShadeDTO);
        //添加任务
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
    }

    @Test
    public void testTaskShade() {

        Assert.assertNotNull(scheduleTaskShadeService.findTaskId(scheduleTaskShadeTemplate.getTaskId(), scheduleTaskShadeTemplate.getIsDeleted(),
                scheduleTaskShadeTemplate.getAppType()));
        scheduleTaskShadeDTO.setTaskDesc("update");
        //更新
        scheduleTaskShadeService.addOrUpdate(scheduleTaskShadeDTO);
        //查询
        List<ScheduleTaskShade> scheduleTaskShades = scheduleTaskShadeService.listTaskByStatus(0L, ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus(), 100);
        Assert.assertNotNull(scheduleTaskShades);
        long count = scheduleTaskShades.stream().filter(s -> s.getTaskId().equals(scheduleTaskShadeTemplate.getTaskId())
                && s.getAppType().equals(scheduleTaskShadeTemplate.getAppType())).count();
        Assert.assertTrue(count > 0);
        Integer countTaskByStatus = scheduleTaskShadeService.countTaskByStatus(ESubmitStatus.SUBMIT.getStatus(), EProjectScheduleStatus.NORMAL.getStatus());
        Assert.assertTrue(countTaskByStatus > 0);

        List<ScheduleTaskShadeCountTaskVO> scheduleTaskShadeCountTaskVOS = scheduleTaskShadeService.countTaskByTypes(scheduleTaskShadeTemplate.getTenantId(),
                scheduleTaskShadeTemplate.getDtuicTenantId(),
                Lists.newArrayList(scheduleTaskShadeDTO.getProjectId()),
                scheduleTaskShadeDTO.getAppType(),
                Lists.newArrayList(scheduleTaskShadeDTO.getTaskType()));
        Assert.assertNotNull(scheduleTaskShadeCountTaskVOS);

        List<ScheduleTaskShade> taskByIds = scheduleTaskShadeService.getTaskByIds(Lists.newArrayList(scheduleTaskShadeDTO.getTaskId()), scheduleTaskShadeDTO.getAppType());
        long findTaskCount = taskByIds.stream().filter(s -> s.getTaskId().equals(scheduleTaskShadeTemplate.getTaskId())
                && s.getAppType().equals(scheduleTaskShadeTemplate.getAppType())).count();
        Assert.assertTrue(findTaskCount > 0);

        List<ScheduleTaskShade> simpleTaskRangeAllByIds = scheduleTaskShadeService.getSimpleTaskRangeAllByIds(Lists.newArrayList(scheduleTaskShadeDTO.getTaskId()), scheduleTaskShadeDTO.getAppType());
        long simpleRangeCount = simpleTaskRangeAllByIds.stream().filter(s -> s.getTaskId().equals(scheduleTaskShadeTemplate.getTaskId())
                && s.getAppType().equals(scheduleTaskShadeTemplate.getAppType())).count();
        Assert.assertTrue(simpleRangeCount > 0);

        ScheduleTaskShade scheduleTaskShade = scheduleTaskShadeService.getByName(scheduleTaskShadeDTO.getProjectId(), scheduleTaskShadeDTO.getName(),
                null, scheduleTaskShadeDTO.getFlowId());
        Assert.assertNotNull(scheduleTaskShade);
        scheduleTaskShadeDTO.setId(scheduleTaskShade.getId());
        List<ScheduleTaskShade> tasksByName = scheduleTaskShadeService.getTasksByName(scheduleTaskShadeDTO.getProjectId(), scheduleTaskShadeDTO.getName(),
                scheduleTaskShadeDTO.getAppType());
        Assert.assertNotNull(tasksByName);
        //更新任务名
        scheduleTaskShadeService.updateTaskName(scheduleTaskShadeDTO.getTaskId(), "testUpdate", scheduleTaskShadeDTO.getAppType());
        Assert.assertNotNull(scheduleTaskShadeService.getById(scheduleTaskShadeDTO.getId()));
        Assert.assertTrue(CollectionUtils.isEmpty(scheduleTaskShadeService.listDependencyTask(Lists.newArrayList(scheduleTaskShadeDTO.getTaskId()),"testUpdate", scheduleTaskShadeDTO.getProjectId())));
        Assert.assertEquals(scheduleTaskShadeService.getTaskNameByJobKey("cron_" + scheduleTaskShadeDTO.getId() + "_20201200000",scheduleTaskShadeDTO.getAppType()),"testUpdate");


        scheduleTaskShadeService.info(scheduleTaskShadeDTO.getTaskId(), scheduleTaskShadeDTO.getAppType(), "{}");
        scheduleTaskShadeService.frozenTask(Lists.newArrayList(scheduleTaskShadeDTO.getTaskId()), EProjectScheduleStatus.PAUSE.getStatus(), scheduleTaskShade.getAppType());
        scheduleTaskShadeService.deleteTask(scheduleTaskShadeDTO.getTaskId(), 0L, scheduleTaskShadeDTO.getAppType());
    }

    @Test
    public void testEmpty() {
        long emptyTaskId = -111L;
        try {
            ScheduleTaskShadeDTO testDto = scheduleTaskShadeDTO;
            testDto.setTaskId(emptyTaskId);
            testDto.setDtuicTenantId(-1L);
            scheduleTaskShadeService.addOrUpdate(testDto);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("租户dtuicTenantId"));
        }
        scheduleTaskShadeService.countTaskByType(-1L, -1L, -1L, 2,
                Lists.newArrayList(scheduleTaskShadeDTO.getTaskType()));
        scheduleTaskShadeService.getTaskByIds(Lists.newArrayList(), 2);
        scheduleTaskShadeService.getSimpleTaskRangeAllByIds(Lists.newArrayList(), 2);
        scheduleTaskShadeService.getTaskNameByJobKey("cronTrigger_19", 1);
        scheduleTaskShadeService.getWorkFlowTopNode(null);
        scheduleTaskShadeService.dealFlowWorkTask(emptyTaskId, 1, new ArrayList<>(), 1L);
        scheduleTaskShadeService.findTaskId(emptyTaskId, 1, 1);
        scheduleTaskShadeService.findTaskId(scheduleTaskShadeDTO.getTaskId(), 1, 10);
    }

}
