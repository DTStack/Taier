package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.pojo.CheckResult;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.common.akka.config.AkkaConfig;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.dataCollection.DataCollection;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.fail;

/**
 * @author haier
 * @Description
 * @date 2021/3/5 10:36 上午
 */
public class StreamTaskControllerTest extends AbstractTest {

    @Autowired
    private StreamTaskController streamTaskController;

    @Autowired
    private EngineJobCheckpointDao engineJobCheckpointDao;

    @Autowired(required = false)
    private MasterServer masterServer;


    @Test
    public void testGetFailedCheckPoint() {
        try {
            long time1 = new Date().getTime();
            EngineJobCheckpoint ejc = DataCollection.getData().getFailedEngineJobCheckpoint();
            long time2 = new Date().getTime();
            List<EngineJobCheckpoint> list = streamTaskController.getFailedCheckPoint(ejc.getTaskId(), time1, time2, 1);
            System.out.println(CollectionUtils.isEmpty(list) ? list : list.get(0));
        } catch (Exception e) {
            fail("GetFailedCheckPoint failed: " + e.getMessage());
        }
    }

    @Test
    public void testGrammarCheck(){
        try {
            AkkaConfig.init(ConfigFactory.load());
            ParamActionExt paramActionExt = new ParamActionExt();
            paramActionExt.setTaskType(0);
            paramActionExt.setComputeType(0);
            paramActionExt.setEngineType("flink");
            paramActionExt.setSqlText("select * from a");
            paramActionExt.setAppType(0);
            paramActionExt.setDeployMode("1");
            paramActionExt.setGenerateTime(1615534634216L);
            paramActionExt.setIsFailRetry(true);
            paramActionExt.setLackingCount(0);
            paramActionExt.setName("test");
            paramActionExt.setPriority(0);
            paramActionExt.setRequestStart(0);
            paramActionExt.setStopJobId(0);
            paramActionExt.setTaskId("440dc30b");
            paramActionExt.setTaskParams("sql.env.parallelism=1\\nflink.checkpoint.interval=300000\\nsql.checkpoint.timeout=180000\\nsql.checkpoint.cleanup.mode=false\\nslots=1\\nlogLevel=info\\njob.priority=10\\nsecurity.kerberos.login.contexts=KafkaClient");
            paramActionExt.setTaskType(0);
            paramActionExt.setTenantId(1L);
            CheckResult checkResult = streamTaskController.grammarCheck(paramActionExt);
            System.out.println(checkResult);
        } catch (Exception e) {
            fail("GrammarCheck failed: " + e.getMessage());
        }
    }
}
