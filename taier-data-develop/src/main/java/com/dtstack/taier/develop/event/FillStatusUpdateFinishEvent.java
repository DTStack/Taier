package com.dtstack.taier.develop.event;

import com.dtstack.taier.dao.domain.ScheduleFillDataJob;
import com.dtstack.taier.develop.service.schedule.FillDataService;
import com.dtstack.taier.scheduler.server.action.fill.FillDataRunnable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: dazhi
 * @Date: 2021/12/27 7:58 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Component
public class FillStatusUpdateFinishEvent implements FillDataRunnable.FillFinishEvent {

    @Autowired
    private FillDataService fillDataJobService;

    @Override
    public void finishFill(Long fillId, Integer originalStatus, Integer currentStatus) {
        ScheduleFillDataJob updateFillDataJob = new ScheduleFillDataJob();
        updateFillDataJob.setFillGenerateStatus(currentStatus);
        fillDataJobService.lambdaUpdate()
                .eq(ScheduleFillDataJob::getId, fillId)
                .eq(ScheduleFillDataJob::getFillGenerateStatus, originalStatus)
                .update(updateFillDataJob);
    }
}
