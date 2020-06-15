package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.ComputeType;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.dao.EngineJobCacheDao;
import com.dtstack.engine.dao.EngineJobCheckpointDao;
import com.dtstack.engine.dao.ScheduleJobDao;
import com.dtstack.engine.master.BaseTest;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Date: 2020/6/5
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class StreamTaskServiceTest extends BaseTest {

	@Autowired
	private EngineJobCheckpointDao engineJobCheckpointDao;

	@Autowired
	StreamTaskService streamTaskService;

	@Test
	public void testGetCheckPoint() {
		engineJobCheckpointDao.insert("9999", "888888",
			"888", Timestamp.valueOf("2020-06-14 10:01:00"),
			"hdfs://tmp/flink/checkpoint/test", "3");
		Long triggerStart = Long.valueOf(Timestamp.valueOf("2020-06-14 10:00:00").getTime());
		Long triggerEnd = Long.valueOf(Timestamp.valueOf("2020-06-14 10:02:00").getTime());
		List<EngineJobCheckpoint> engineJobCheckpoints = streamTaskService.getCheckPoint("9999", triggerStart, triggerEnd);
		Assert.notNull(engineJobCheckpoints);
		Assert.isTrue(engineJobCheckpoints.size() > 0);
		engineJobCheckpointDao.deleteByTaskId("9999");
	}

	@Test
	public void testGetByTaskIdAndEngineTaskId() {
		engineJobCheckpointDao.insert("9998", "888886",
			"888", Timestamp.valueOf("2020-06-14 10:01:00"),
			"hdfs://tmp/flink/checkpoint/test", "3");

		EngineJobCheckpoint engineJobCheckpoint = streamTaskService.getByTaskIdAndEngineTaskId("9998", "888886");
		Assert.notNull(engineJobCheckpoint);
		engineJobCheckpointDao.deleteByTaskId("9998");
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetEngineStreamJob() {
		ScheduleJob streamJob = dataCollection.getScheduleJobStream();

		List<String> taskIds = Arrays.asList(new String[]{streamJob.getJobId()});
		List<ScheduleJob> jobs = streamTaskService.getEngineStreamJob(taskIds);
		Assert.notNull(jobs);
		Assert.isTrue(jobs.size() > 0);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskIdsByStatus() {
		ScheduleJob streamJob = dataCollection.getScheduleJobStream();

		List<String> taskIds = streamTaskService.getTaskIdsByStatus(streamJob.getStatus());
		Assert.notNull(taskIds);
		Assert.isTrue(taskIds.contains(streamJob.getTaskId()));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskStatus() {
		ScheduleJob streamJob = dataCollection.getScheduleJobStream();

		Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());
		Assert.notNull(taskStatus);
		Assert.isTrue(taskStatus == 14);
	}

	@Test
	public void testGetRunningTaskLogUrl() {
		String taskId = "91ac079e";

		Integer taskStatus = streamTaskService.getTaskStatus(taskId);
		Assert.isTrue(RdosTaskStatus.RUNNING.getStatus().equals(taskStatus));

		Pair<String, String> taskLogUrl = streamTaskService.getRunningTaskLogUrl(taskId);
		Assert.notNull(taskLogUrl.getKey());
		Assert.notNull(taskLogUrl.getValue());
	}

}
