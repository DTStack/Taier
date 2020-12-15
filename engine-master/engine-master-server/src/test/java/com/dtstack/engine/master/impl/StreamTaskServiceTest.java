package com.dtstack.engine.master.impl;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.EJobCacheStage;
import com.dtstack.engine.common.enums.EngineType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/6/5
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class StreamTaskServiceTest extends AbstractTest {

	@Autowired
    private ScheduleJobDao scheduleJobDao;

	@Autowired
    private EngineJobCacheDao engineJobCacheDao;

	@Autowired
	StreamTaskService streamTaskService;

	@MockBean
    private WorkerOperator workerOperator;

	@Before
    public void setup(){
	    when(workerOperator.getRollingLogBaseInfo(any())).thenReturn(new ArrayList<>());
    }

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetCheckPoint() {
		EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobCheckpoint();

		Long triggerStart = engineJobCheckpoint.getCheckpointTrigger().getTime() - 1;
		Long triggerEnd = engineJobCheckpoint.getCheckpointTrigger().getTime() + 1;
		List<EngineJobCheckpoint> engineJobCheckpoints = streamTaskService.getCheckPoint(
			engineJobCheckpoint.getTaskId(), triggerStart, triggerEnd);
		Assert.assertNotNull(engineJobCheckpoints);
		Assert.assertTrue(engineJobCheckpoints.size() > 0);
    }

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetByTaskIdAndEngineTaskId() {
		EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobCheckpoint();

		EngineJobCheckpoint resJobCheckpoint = streamTaskService.getByTaskIdAndEngineTaskId(
			engineJobCheckpoint.getTaskId(), engineJobCheckpoint.getTaskEngineId());
		Assert.assertNotNull(resJobCheckpoint);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetEngineStreamJob() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		List<String> taskIds = Collections.singletonList(streamJob.getJobId());
		List<ScheduleJob> jobs = streamTaskService.getEngineStreamJob(taskIds);
		Assert.assertNotNull(jobs);
		Assert.assertTrue(jobs.size() > 0);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskIdsByStatus() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		Integer status = streamJob.getStatus();
		List<String> taskIds = streamTaskService.getTaskIdsByStatus(status);
		Assert.assertNotNull(taskIds);
		Assert.assertTrue(taskIds.contains(streamJob.getJobId()));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskStatus() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());
		Assert.assertNotNull(taskStatus);
		Assert.assertTrue(taskStatus == 4);
	}


	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetRunningTaskLogUrl(){

        try {
            ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();
            DataCollection.getData().getEngineJobCache();

            Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());
            Assert.assertEquals(RdosTaskStatus.RUNNING.getStatus(), taskStatus);
            List<String> taskLogUrl = streamTaskService.getRunningTaskLogUrl(streamJob.getJobId());
            Assert.assertNotNull(taskLogUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	@Test
    public void testSavePoint(){
        EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobSavepoint();
        EngineJobCheckpoint savePoint = streamTaskService.getSavePoint(engineJobCheckpoint.getTaskId());
        Assert.assertNotNull(savePoint);
    }


	@Test
    public void testException(){
        String taskId = UUID.randomUUID().toString();
        Integer taskStatus = streamTaskService.getTaskStatus(taskId);
        Assert.assertNull(taskStatus);

        try {
            streamTaskService.getRunningTaskLogUrl("");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("taskId can't be empty"));
        }

        try {
            streamTaskService.getRunningTaskLogUrl(taskId);
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("can't find record by taskId"));
        }

        ScheduleJob scheduleJobStream = DataCollection.getData().getScheduleJobStream();

        try {
            scheduleJobDao.updateJobStatus(scheduleJobStream.getJobId(),RdosTaskStatus.CANCELED.getStatus());
            streamTaskService.getRunningTaskLogUrl(scheduleJobStream.getJobId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("not running status"));
        }


        try {
            scheduleJobDao.updateJobStatus(scheduleJobStream.getJobId(),RdosTaskStatus.RUNNING.getStatus());
            scheduleJobDao.updateJobSubmitSuccess(scheduleJobStream.getJobId(),
                    scheduleJobStream.getEngineJobId(),"","","");
            streamTaskService.getRunningTaskLogUrl(scheduleJobStream.getJobId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("not running in perjob"));
        }


        try {
            scheduleJobDao.updateJobSubmitSuccess(scheduleJobStream.getJobId(),
                    scheduleJobStream.getEngineJobId(),"application_1605237729642_101961","","");
            engineJobCacheDao.delete(scheduleJobStream.getJobId());
            streamTaskService.getRunningTaskLogUrl(scheduleJobStream.getJobId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("not exist in job cache table"));
        }


        try {
            engineJobCacheDao.insert(scheduleJobStream.getJobId(),
                    EngineType.Flink.name(), ComputeType.STREAM.getType(), EJobCacheStage.DB.getStage(), "",
                    "127.0.0.1:8090", scheduleJobStream.getJobName(), 100L, "");
            streamTaskService.getRunningTaskLogUrl(scheduleJobStream.getJobId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("ref application url error"));
        }

        try {
            String jobInfo = "{\"appType\":7,\"computeType\":0,\"engineType\":\"flink\",\"generateTime\":1606129577411,\"groupName\":\"default_a\",\"lackingCount\":0,\"maxRetryNum\":0,\"name\":\"dev_2020_45330575\",\"priority\":1606130577411,\"requestStart\":0,\"sqlText\":\"CREATE TABLE MyTable(\\n    id int,\\n    name varchar\\n )WITH(\\n    type ='kafka11',\\n    bootstrapServers ='kudu1:9092',\\n    offsetReset ='latest',\\n    topic ='tiezhu_in',\\n    timezone='Asia/Shanghai',\\n    updateMode ='append',\\n    enableKeyPartitions ='false',\\n    topicIsPattern ='false',\\n    parallelism ='1'\\n );\\n\\nCREATE TABLE MyResult(\\n    id INT,\\n    name VARCHAR\\n )WITH(\\n    type ='console'\\n );\\n\\nINSERT  \\nINTO\\n    MyResult\\n    SELECT\\n        a.id ,\\n        a.name       \\n    FROM\\n        MyTable a;\\n        \\n\\n\\n\",\"stopJobId\":0,\"taskId\":\"45330575\",\"taskParams\":\"sql.env.parallelism=1\\nflink.checkpoint.interval=60000\\nsql.checkpoint.cleanup.mode=false\\njob.priority=10\\nslots=1\",\"taskType\":0,\"tenantId\":1}";
            JSONObject jobInfoObject = JSONObject.parseObject(jobInfo);
            jobInfoObject.put("taskId",scheduleJobStream.getJobId());
            engineJobCacheDao.updateJobInfo(jobInfoObject.toJSONString(),scheduleJobStream.getJobId());
            streamTaskService.getRunningTaskLogUrl(scheduleJobStream.getJobId());
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("had stop"));
        }

    }

}
