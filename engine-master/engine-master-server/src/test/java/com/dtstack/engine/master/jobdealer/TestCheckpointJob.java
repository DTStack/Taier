package com.dtstack.engine.master.jobdealer;

import com.dtstack.engine.domain.EngineJobCache;
import com.dtstack.engine.domain.ScheduleJob;
import com.dtstack.engine.pluginapi.enums.EngineType;
import com.dtstack.engine.dao.TestEngineJobCacheDao;
import com.dtstack.engine.dao.TestScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.utils.Template;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author yuebai
 * @date 2021-05-10
 */
public class TestCheckpointJob extends AbstractTest {

    @Autowired
    private JobCheckpointDealer jobCheckpointDealer;

    @Autowired
    private TestEngineJobCacheDao engineJobCacheDao;

    @Autowired
    private TestScheduleJobDao scheduleJobDao;

    @Test
    public void testFlinkJar() {
        try {
            String jobId = UUID.randomUUID().toString().replace("-", "");
            EngineJobCache engineJobCacheTemplate = Template.getEngineJobCacheTemplate();
            engineJobCacheTemplate.setJobId(jobId);
            engineJobCacheTemplate.setEngineType(EngineType.Flink.name());
            engineJobCacheTemplate.setJobInfo("{\"appType\":7,\"computeType\":0,\"engineType\":\"flink\",\"exeArgs\":\"\",\"stopJobId\":0,\"submitExpiredTime\":0,\"taskId\":\"a44b4af9\",\"taskParams\":\"slots=1\\njob.priority=10\\nopenCheckpoint=true\",\"taskType\":2,\"tenantId\":1}");
            engineJobCacheDao.insert(engineJobCacheTemplate);
            ScheduleJob scheduleJobTemplate = Template.getScheduleJobTemplate();
            scheduleJobTemplate.setJobId(jobId);
            scheduleJobDao.insert(scheduleJobTemplate);
            scheduleJobDao.updateJobExtraInfo("{\"checkpoint_interval\":\"3000\",\"job_graph\":\"{\\\"jobId\\\":\\\"6fad1f8a1247d5c9f4c4815db7745514\\\",\\\"startTime\\\":1610678728832,\\\"taskVertices\\\":[{\\\"output\\\":[],\\\"inputs\\\":[],\\\"maxParallelism\\\":-1,\\\"parallelism\\\":1,\\\"subJobVertices\\\":[{\\\"name\\\":\\\"Source: luna_ods_foo \\\",\\\"id\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\"},{\\\"name\\\":\\\" SourceConversion(table=[Unregistered_DataStream_147], fields=[id, name]) \\\",\\\"id\\\":\\\"570f707193e0fe32f4d86d067aba243b\\\"},{\\\"name\\\":\\\" SinkConversionToRow \\\",\\\"id\\\":\\\"ba40499bacce995f15693b1735928377\\\"},{\\\"name\\\":\\\" SourceConversion(table=[default_catalog.default_database.ods_foo], fields=[id, name, PROCTIME]) \\\",\\\"id\\\":\\\"cf155f65686cb012844f7c745ec70a3c\\\"},{\\\"name\\\":\\\" Calc(select=[1 AS id, name]) \\\",\\\"id\\\":\\\"04cc3374bd37b54582d262bc93f71921\\\"},{\\\"name\\\":\\\" SinkConversionToTuple2 \\\",\\\"id\\\":\\\"483059c232d65e08192496001e4c9109\\\"},{\\\"name\\\":\\\" Sink: Unnamed\\\",\\\"id\\\":\\\"30ba221650099eb21c298598aa0ee943\\\"}],\\\"jobVertexId\\\":\\\"cbc357ccb763df2852fee8c4fc7d55f2\\\",\\\"jobVertexName\\\":\\\"Source: luna_ods_foo -> SourceConversion(table=[Unregistered_DataStream_147], fields=[id, name]) -> SinkConversionToRow -> SourceConversion(table=[default_catalog.default_database.ods_foo], fields=[id, name, PROCTIME]) -> Calc(select=[1 AS id, name]) -> SinkConversionToTuple2 -> Sink: Unnamed\\\"}]}\"}",jobId);
            long checkpointInterval = jobCheckpointDealer.getCheckpointInterval(jobId);
            Assert.assertEquals(checkpointInterval,3000);
        } catch (ExecutionException e) {
            Assert.fail();
        }

    }
}
