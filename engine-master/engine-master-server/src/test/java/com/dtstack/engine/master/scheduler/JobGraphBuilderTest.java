package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.bo.ScheduleBatchJob;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Map;

/**
 * @Auther: dazhi
 * @Date: 2020/11/14 3:06 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class JobGraphBuilderTest extends AbstractTest {


    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuilderTest.class);

    private static final String DAY_PATTERN = "yyyy-MM-dd";

    private static final ObjectMapper objMapper = new ObjectMapper();

    @Autowired
    private JobGraphBuilderTrigger jobGraphBuilderTrigger;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    @Test
    public void testStartGraphBuildIsMaster() throws Exception {
        // 创建小时任务
        ScheduleTaskShade scheduleHourTaskShade = DataCollection.getData().getCronWeekHour();
        // 创建小时力度的任务
        ScheduleTaskShade scheduleMinTaskShade = DataCollection.getData().getCronWeekMin();

        // 设置环境变量
        System.setProperty("batch.job.graph.build.cron", "01:00:00");

        // 开启
        jobGraphBuilderTrigger.dealMaster(Boolean.TRUE);
        jobGraphBuilderTrigger.run();
//        Thread.sleep(10000000000L);
    }

    @Test
    public void testStopGraphBuildIsMaster() throws Exception {
        logger.info("stopJobGraph");
        jobGraphBuilderTrigger.dealMaster(Boolean.FALSE);
    }

    @Test
    public void testFillBuildIsMaster() throws Exception {
        ScheduleTaskShade scheduleHourTaskShade = DataCollection.getData().getCronWeekHour();

        String taskJSon = "[{\"task\":"+scheduleHourTaskShade.getTaskId()+"}]";
        String fillJobName = "P_testSparkSql_2020_10_28_39_44";
        ArrayNode jsonNode = objMapper.readValue(taskJSon, ArrayNode.class);
        String triggerDay = new DateTime().toString(DAY_PATTERN);

        Map<String, ScheduleBatchJob> stringScheduleBatchJobMap = jobGraphBuilder.buildFillDataJobGraph(jsonNode,
                fillJobName, false, triggerDay, scheduleHourTaskShade.getCreateUserId(),
                null, null, scheduleHourTaskShade.getProjectId(), scheduleHourTaskShade.getDtuicTenantId(),
                false, scheduleHourTaskShade.getAppType(), 1L, scheduleHourTaskShade.getDtuicTenantId());

        Assert.assertNotNull(stringScheduleBatchJobMap);
    }



}
