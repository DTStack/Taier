package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.EngineJobCheckpoint;
import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.http.PoolHttpClient;
import com.dtstack.engine.master.AbstractTest;
import com.dtstack.engine.master.akka.WorkerOperator;
import com.dtstack.engine.master.dataCollection.DataCollection;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Date: 2020/6/5
 * Company: www.dtstack.com
 * @author xiuzhu
 */

@PrepareForTest({PoolHttpClient.class, WorkerOperator.class})
public class StreamTaskServiceTest extends AbstractTest {

	@Mock
	private WorkerOperator workerOperator;

	@Autowired
	@InjectMocks
	StreamTaskService streamTaskService;

	@Before
	public void setup() throws Exception{
		MockitoAnnotations.initMocks(this);
		PowerMockito.mockStatic(WorkerOperator.class);
		when(workerOperator.getJobMaster(any())).thenReturn("http://dtstack01:8088/");

		PowerMockito.mockStatic(PoolHttpClient.class);
		when(PoolHttpClient.get(any())).thenReturn("{\"app\":{\"amContainerLogs\":\"http://dtstack01:8088/ws/v1/cluster/apps/application_9527\"}}");

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
		Assert.notNull(engineJobCheckpoints);
		Assert.isTrue(engineJobCheckpoints.size() > 0);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetByTaskIdAndEngineTaskId() {
		EngineJobCheckpoint engineJobCheckpoint = DataCollection.getData().getEngineJobCheckpoint();

		EngineJobCheckpoint resJobCheckpoint = streamTaskService.getByTaskIdAndEngineTaskId(
			engineJobCheckpoint.getTaskId(), engineJobCheckpoint.getCheckpointId());
		Assert.notNull(resJobCheckpoint);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetEngineStreamJob() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		List<String> taskIds = Arrays.asList(new String[]{streamJob.getJobId()});
		List<ScheduleJob> jobs = streamTaskService.getEngineStreamJob(taskIds);
		Assert.notNull(jobs);
		Assert.isTrue(jobs.size() > 0);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskIdsByStatus() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		List<String> taskIds = streamTaskService.getTaskIdsByStatus(streamJob.getStatus());
		Assert.notNull(taskIds);
		Assert.isTrue(taskIds.contains(streamJob.getTaskId()));
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetTaskStatus() {
		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();

		Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());
		Assert.notNull(taskStatus);
		Assert.isTrue(taskStatus == 14);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetRunningTaskLogUrl() throws Exception {

		ScheduleJob streamJob = DataCollection.getData().getScheduleJobStream();
		DataCollection.getData().getEngineJobCache();

		Integer taskStatus = streamTaskService.getTaskStatus(streamJob.getJobId());
		Assert.isTrue(RdosTaskStatus.RUNNING.getStatus().equals(taskStatus));

//		List<String> taskLogUrl = streamTaskService.getRunningTaskLogUrl(streamJob.getJobId());
//		Assert.notNull(taskLogUrl.getKey());
//		Assert.notNull(taskLogUrl.getValue());
	}

}
