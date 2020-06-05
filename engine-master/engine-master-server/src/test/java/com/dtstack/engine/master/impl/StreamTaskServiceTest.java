package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleJob;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.master.BaseTest;
import org.apache.commons.math3.util.Pair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;

/**
 * Date: 2020/6/5
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class StreamTaskServiceTest extends BaseTest {

	@Autowired
	StreamTaskService streamTaskService;



	@Test
	public void testGetEngineStreamJob() {
		List<String> taskIds = Arrays.asList(new String[]{"91ac079e"});
		List<ScheduleJob> jobs = streamTaskService.getEngineStreamJob(taskIds);
		Assert.notNull(jobs);
		Assert.isTrue(jobs.size() > 0);
	}

	@Test
	public void testGetTaskStatus() {
		String taskId = "91ac079e";
		Integer taskStatus = streamTaskService.getTaskStatus(taskId);
		Assert.notNull(taskStatus);
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
