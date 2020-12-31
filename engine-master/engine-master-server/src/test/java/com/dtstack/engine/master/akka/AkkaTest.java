package com.dtstack.engine.master.akka;

import com.dtstack.engine.api.pojo.ClientTemplate;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.JobIdentifier;
import com.dtstack.engine.common.akka.message.MessageJudgeSlots;
import com.dtstack.engine.common.akka.message.MessageSubmitJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.pojo.JobResult;
import com.dtstack.engine.common.pojo.JudgeResult;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/12/31 4:42 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AkkaTest extends AbstractTest {

    @Autowired
    private MasterServer masterServer;

    @Autowired
    private WorkerOperator workerOperator;

    @Test
    public void testStart() {
        masterServer.updateWorkerInfo();

        try {
            Object o = masterServer.sendMessage("hello, word");
            MessageSubmitJob messageSubmitJob = new MessageSubmitJob(new JobClient());
            masterServer.sendMessage(messageSubmitJob);
            MessageJudgeSlots messageJudgeSlots = new MessageJudgeSlots(new JobClient());
            masterServer.sendMessage(messageJudgeSlots);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void submitJobTest(){
        try {
            JobResult jobResult = workerOperator.submitJob(new JobClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void judgeSlotsTest(){
        try {
            JudgeResult judgeResult = workerOperator.judgeSlots(new JobClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getJobStatusTest(){
        try {
            RdosTaskStatus jobStatus = workerOperator.getJobStatus(new JobIdentifier("1e9b5b54", "1e9b5b54", "1L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEngineLogTest(){
        try {
            String engineLog = workerOperator.getEngineLog(new JobIdentifier("1e9b5b54", "1e9b5b54", "1L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getCheckpointsTest(){
        try {
            String engineLog = workerOperator.getCheckpoints(new JobIdentifier("1e9b5b54", "1e9b5b54", "1L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getRollingLogBaseInfoTest(){
        try {
            List<String> rollingLogBaseInfo = workerOperator.getRollingLogBaseInfo(new JobIdentifier("1e9b5b54", "1e9b5b54", "1L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getJobMasterTest(){
        try {
            String jobMaster = workerOperator.getJobMaster(new JobIdentifier("1e9b5b54", "1e9b5b54", "1L"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void stopJobTest(){
        try {
            JobResult jobResult = workerOperator.stopJob(new JobClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void containerInfosTest(){
        try {
            List<String> strings = workerOperator.containerInfos(new JobClient());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getDefaultPluginConfigTest(){
        try {
            List<ClientTemplate> defaultPluginConfig = workerOperator.getDefaultPluginConfig("", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getEngineMessageByHttpTest(){
        try {
            String engineMessageByHttp = workerOperator.getEngineMessageByHttp("1","/abc","{}");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
