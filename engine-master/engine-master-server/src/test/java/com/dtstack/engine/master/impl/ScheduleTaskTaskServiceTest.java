package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.ScheduleTaskTaskShade;
import com.dtstack.engine.common.enums.DisplayDirect;
import com.dtstack.engine.dao.ScheduleTaskShadeDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.engine.master.vo.ScheduleTaskVO;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author yuebai
 * @date 2020-11-25
 */
public class ScheduleTaskTaskServiceTest extends AbstractTest {

    @Autowired
    private ScheduleTaskTaskShadeService scheduleTaskTaskShadeService;

    @Autowired
    private ScheduleTaskShadeDao scheduleTaskShadeDao;

    @Test
    public void testScheduleTaskTask() {

        //插入工作流数据
        ScheduleTaskShade parentTaskShadeTemplate = Template.getScheduleTaskShadeTemplate();
        parentTaskShadeTemplate.setTaskId(471L);
        parentTaskShadeTemplate.setAppType(1);
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.WORK_FLOW.getType());
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //顶节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.VIRTUAL.getType());
        parentTaskShadeTemplate.setName("virtual");
        parentTaskShadeTemplate.setTaskId(499L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //子节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.SHELL.getType());
        parentTaskShadeTemplate.setName("first");
        parentTaskShadeTemplate.setTaskId(525L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);

        //子节点
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.SHELL.getType());
        parentTaskShadeTemplate.setName("second");
        parentTaskShadeTemplate.setTaskId(600L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);


        //工作流的子任务
        parentTaskShadeTemplate.setFlowId(parentTaskShadeTemplate.getTaskId());
        parentTaskShadeTemplate.setTaskType(EScheduleJobType.SHELL.getType());
        parentTaskShadeTemplate.setName("workFlowSon");
        parentTaskShadeTemplate.setTaskId(700L);
        parentTaskShadeTemplate.setAppType(1);
        scheduleTaskShadeDao.insert(parentTaskShadeTemplate);



        //插入关系
        String taskTaskStr = "[{\"appType\":1,\"dtuicTenantId\":1,\"gmtCreate\":1606101569150,\"gmtModified\":1606101569150,\"id\":233,\"isDeleted\":0,\"parentTaskId\":499,\"projectId\":3,\"taskId\":525,\"tenantId\":1}," +
                "{\"appType\":1,\"dtuicTenantId\":1,\"gmtCreate\":1606101569150,\"gmtModified\":1606101569150,\"id\":233,\"isDeleted\":0,\"parentTaskId\":525,\"projectId\":3,\"taskId\":600,\"tenantId\":1}," +
                "]";
        scheduleTaskTaskShadeService.saveTaskTaskList(taskTaskStr);
        List<ScheduleTaskTaskShade> allParentTask = scheduleTaskTaskShadeService.getAllParentTask(525L, 1);
        Assert.assertNotNull(allParentTask);


        //插入关系
        String workFlowStr = "[{\"appType\":1,\"dtuicTenantId\":1,\"gmtCreate\":1606101569150,\"gmtModified\":1606101569150,\"id\":233,\"isDeleted\":0,\"parentTaskId\":471,\"projectId\":3,\"taskId\":700,\"tenantId\":1}" +
                "]";
        scheduleTaskTaskShadeService.saveTaskTaskList(workFlowStr);

        //查询不存在的
        ScheduleTaskVO scheduleTaskVO = scheduleTaskTaskShadeService.displayOffSpring(19121L, 3L, 1, 0, 1);
        Assert.assertNull(scheduleTaskVO);


        //查询父节点
        scheduleTaskVO = scheduleTaskTaskShadeService.displayOffSpring(600L, 3L, 1, 2, 1);
        Assert.assertNotNull(scheduleTaskVO);

        //查询子节点
        scheduleTaskVO = scheduleTaskTaskShadeService.displayOffSpring(499L, 3L, 1, 2, DisplayDirect.CHILD.getType());
        Assert.assertNotNull(scheduleTaskVO);

        //查询工作流
        scheduleTaskVO = scheduleTaskTaskShadeService.displayOffSpring(471L, 3L, 1, 2, DisplayDirect.CHILD.getType());
        Assert.assertNotNull(scheduleTaskVO);

        //查询不存在的
        Assert.assertNull(scheduleTaskTaskShadeService.getAllFlowSubTasks(-12L,1));

        ScheduleTaskVO allFlowSubTasks = scheduleTaskTaskShadeService.getAllFlowSubTasks(471L, 1);
        Assert.assertNotNull(allFlowSubTasks);

        //查询工作流子节点
        allFlowSubTasks = scheduleTaskTaskShadeService.getAllFlowSubTasks(499L, 1);
        Assert.assertNotNull(allFlowSubTasks);
        scheduleTaskTaskShadeService.clearDataByTaskId(525L, 1);
    }


    @Test
    public void testEmpty(){
        scheduleTaskTaskShadeService.saveTaskTaskList("");
        Assert.assertNull(scheduleTaskTaskShadeService.getFlowWorkSubTasksRefTask(Sets.newHashSet(1001L),0,0,8,10));
    }
}
