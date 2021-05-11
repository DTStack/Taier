package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.api.domain.TenantResource;
import com.dtstack.engine.api.dto.ScheduleTaskShadeDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ScheduleTaskShadeVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadeCountTaskVO;
import com.dtstack.engine.api.vo.schedule.task.shade.ScheduleTaskShadePageVO;
import com.dtstack.engine.dao.TenantResourceDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.utils.Template;
import com.dtstack.schedule.common.enums.EProjectScheduleStatus;
import com.dtstack.schedule.common.enums.EScheduleJobType;
import com.dtstack.schedule.common.enums.ESubmitStatus;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author yuebai
 * @date 2020-11-26
 */
public class ScheduleTaskShadeServiceTest extends AbstractTest {

    @Autowired
    private ScheduleTaskShadeService scheduleTaskShadeService;

    @Autowired
    private TenantResourceDao tenantResourceDao;

    private static String updateTaskName = "updateTaskName";

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
        ArrayList<Long> taskIds = Lists.newArrayList(scheduleTaskShadeDTO.getTaskId());

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

        List<ScheduleTaskShade> taskByIds = scheduleTaskShadeService.getTaskByIds(taskIds, scheduleTaskShadeDTO.getAppType());
        long findTaskCount = taskByIds.stream().filter(s -> s.getTaskId().equals(scheduleTaskShadeTemplate.getTaskId())
                && s.getAppType().equals(scheduleTaskShadeTemplate.getAppType())).count();
        Assert.assertTrue(findTaskCount > 0);

        Assert.assertNotNull(scheduleTaskShadeService.findTaskIds(taskIds, null, scheduleTaskShadeDTO.getAppType(), true));
        Assert.assertNotNull(scheduleTaskShadeService.findTaskIds(taskIds, null, scheduleTaskShadeDTO.getAppType(), false));
        scheduleTaskShadeService.listByTaskIdsNotIn(taskIds, scheduleTaskShadeDTO.getAppType(), scheduleTaskShadeDTO.getProjectId());

        List<ScheduleTaskShade> simpleTaskRangeAllByIds = scheduleTaskShadeService.getSimpleTaskRangeAllByIds(taskIds, scheduleTaskShadeDTO.getAppType());
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
        scheduleTaskShadeService.updateTaskName(scheduleTaskShadeDTO.getTaskId(), updateTaskName, scheduleTaskShadeDTO.getAppType());
        scheduleTaskShadeDTO.setName(updateTaskName);
        Assert.assertNotNull(scheduleTaskShadeService.getById(scheduleTaskShadeDTO.getId()));
        Assert.assertTrue(CollectionUtils.isEmpty(scheduleTaskShadeService.listDependencyTask(Lists.newArrayList(scheduleTaskShadeDTO.getTaskId()),"testUpdate", scheduleTaskShadeDTO.getProjectId())));
        Assert.assertEquals(scheduleTaskShadeService.getTaskNameByJobKey("cron_" + scheduleTaskShadeDTO.getId() + "_20201200000",scheduleTaskShadeDTO.getAppType()),updateTaskName);

        PageResult<List<ScheduleTaskShadeVO>> listPageResult = scheduleTaskShadeService.pageQuery(scheduleTaskShadeDTO);
        Assert.assertNotNull(listPageResult);
        Assert.assertTrue(listPageResult.getData().stream().anyMatch(d -> d.getTaskId().equals(scheduleTaskShadeDTO.getTaskId())));

        ScheduleTaskShadePageVO scheduleTaskShadePageVO = scheduleTaskShadeService.queryTasks(scheduleTaskShadeDTO.getTenantId(),null,
                scheduleTaskShadeDTO.getProjectId(), null, null, null, null, scheduleTaskShadeDTO.getScheduleStatus(),
                String.join(",", scheduleTaskShadeDTO.getTaskType() + "", scheduleTaskShadeDTO.getTaskType() + ""), null,
                1, 20, null, scheduleTaskShadeDTO.getAppType());
        Assert.assertNotNull(scheduleTaskShadePageVO);

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
        scheduleTaskShadeService.getTaskNameByJobKey("cronTrigger_-19_2020112000", 1);
        scheduleTaskShadeService.getWorkFlowTopNode(null,1);
        scheduleTaskShadeService.dealFlowWorkTask(emptyTaskId, 1, new ArrayList<>(), 1L);
        scheduleTaskShadeService.findTaskId(emptyTaskId, 1, 1);
        scheduleTaskShadeService.findTaskId(scheduleTaskShadeDTO.getTaskId(), 1, 10);
        scheduleTaskShadeService.findTaskIds(new ArrayList<>(), 0, 1, true);
    }


    @Test
    @Rollback
    public void testSyncLimit() {
        TenantResource tenantResource = new TenantResource();
        tenantResource.setTenantId(scheduleTaskShadeDTO.getTenantId().intValue());
        tenantResource.setDtUicTenantId(scheduleTaskShadeDTO.getDtuicTenantId().intValue());
        tenantResource.setTaskType(EScheduleJobType.SYNC.getType());
        tenantResource.setEngineType(EScheduleJobType.SYNC.getName());
        JSONObject params = new JSONObject();
        params.put("jobmanager.memory.mb", "2048");
        params.put("taskmanager.memory.mb", "2048");
        tenantResource.setResourceLimit(params.toJSONString());
        tenantResourceDao.insert(tenantResource);
        Assert.assertTrue(scheduleTaskShadeService.checkResourceLimit(scheduleTaskShadeDTO.getDtuicTenantId(), EScheduleJobType.SYNC.getType(), "## 任务运行方式：\n" +
                "## per_job:单独为任务创建flink yarn session，适用于低频率，大数据量同步\n" +
                "## session：多个任务共用一个flink yarn session，适用于高频率、小数据量同步，默认session\n" +
                "## flinkTaskRunMode=per_job\n" +
                "## per_job模式下jobManager配置的内存大小，默认1024（单位M)\n" +
                "## jobmanager.memory.mb=1024\n" +
                "## per_job模式下taskManager配置的内存大小，默认1024（单位M）\n" +
                "## taskmanager.memory.mb=1024\n" +
                "## per_job模式下启动的taskManager数量\n" +
                "## container=1\n" +
                "## per_job模式下每个taskManager 对应 slot的数量\n" +
                "## slots=1\n" +
                "## checkpoint保存时间间隔\n" +
                "## flink.checkpoint.interval=300000\n" +
                "## 任务优先级, 范围:1-1000\n" +
                "## job.priority=10\n", scheduleTaskShadeDTO.getTaskId()).size() <= 0);

    }


    @Test
    @Rollback
    public void testShellLimit() {
        TenantResource tenantResource = new TenantResource();
        tenantResource.setTenantId(scheduleTaskShadeDTO.getTenantId().intValue());
        tenantResource.setDtUicTenantId(scheduleTaskShadeDTO.getDtuicTenantId().intValue());
        tenantResource.setTaskType(EScheduleJobType.SHELL.getType());
        tenantResource.setEngineType(EScheduleJobType.SHELL.getName());
        JSONObject params = new JSONObject();
        params.put("worker.memory", "2048");
        params.put("worker.num", "20");
        params.put("worker.cores", "2");
        tenantResource.setResourceLimit(params.toJSONString());
        tenantResourceDao.insert(tenantResource);
        Assert.assertTrue(scheduleTaskShadeService.checkResourceLimit(scheduleTaskShadeDTO.getDtuicTenantId(), EScheduleJobType.SHELL.getType(), "## 每个worker所占内存，比如512m\n" +
                "worker.memory=512m\n" +
                "\n" +
                "## 每个worker所占的cpu核的数量\n" +
                "worker.cores=1\n" +
                "\n" +
                "## 任务优先级, 范围:1-1000\n" +
                "job.priority=10", scheduleTaskShadeDTO.getTaskId()).size() <= 0);

    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void  testAddOrUpdateBatchTask(){

        ScheduleTaskShade shade = DataCollection.getData().getScheduleTaskShade();
        ScheduleTaskShadeDTO shadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(shade,shadeDTO);
        String commitId = scheduleTaskShadeService.addOrUpdateBatchTask(Lists.newArrayList(shadeDTO),null);
        Assert.assertNotNull(commitId);
    }


    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testInfoCommit(){

        scheduleTaskShadeService.infoCommit(-1L,1,"提交了", UUID.randomUUID().toString());
    }

    @Test
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    @Rollback
    public void testTaskCommit(){

        ScheduleTaskShade shade = DataCollection.getData().getScheduleTaskShade();
        ScheduleTaskShadeDTO shadeDTO = new ScheduleTaskShadeDTO();
        BeanUtils.copyProperties(shade,shadeDTO);
        String commitId = scheduleTaskShadeService.addOrUpdateBatchTask(Lists.newArrayList(shadeDTO),null);
        Boolean flag = scheduleTaskShadeService.taskCommit(commitId);
        Assert.assertTrue(flag);
    }
}
