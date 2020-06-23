package com.dtstack.engine.master.impl;

import com.dtstack.engine.api.domain.ScheduleTaskShade;
import com.dtstack.engine.master.AbstractTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Date: 2020/6/21
 * Company: www.dtstack.com
 * @author xiuzhu
 */

public class ScheduleTaskShadeServiceTest extends AbstractTest {

	@Autowired
	ScheduleTaskShadeService scheduleTaskShadeService;


	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testFindTaskId() {
		ScheduleTaskShade scheduleTaskShade = dataCollection.getScheduleTaskShade();

		ScheduleTaskShade taskShade = scheduleTaskShadeService.findTaskId(
			scheduleTaskShade.getTaskId(),
			scheduleTaskShade.getIsDeleted(), scheduleTaskShade.getAppType());

		Assert.notNull(taskShade);

	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetBatchTaskById() {
		ScheduleTaskShade stsDelete = dataCollection.getScheduleTaskShadeDelete();

		ScheduleTaskShade taskShade = null;
		try {
			taskShade = scheduleTaskShadeService.getBatchTaskById(stsDelete.getTaskId(), stsDelete.getAppType());
		} catch (Exception e) {
			e.printStackTrace();
		}
		Assert.isNull(taskShade);

		ScheduleTaskShade sts = dataCollection.getScheduleTaskShade();
		taskShade = scheduleTaskShadeService.getBatchTaskById(sts.getTaskId(), sts.getAppType());
		Assert.notNull(taskShade);
	}

	@Test
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	@Rollback
	public void testGetByName() {
		ScheduleTaskShade sts = dataCollection.getScheduleTaskShade();
		ScheduleTaskShade taskShade = scheduleTaskShadeService.getByName(
			sts.getProjectId(), sts.getName(), sts.getAppType(), sts.getFlowId());

		Assert.isTrue(sts.getName().equals(taskShade.getName()));
	}


}
