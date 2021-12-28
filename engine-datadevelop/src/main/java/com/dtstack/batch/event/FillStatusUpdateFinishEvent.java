package com.dtstack.batch.event;

import com.dtstack.batch.service.schedule.FillDataService;
import com.dtstack.engine.domain.ScheduleFillDataJob;
import com.dtstack.engine.master.action.fill.FillDataRunnable;
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
                .eq(ScheduleFillDataJob::getId,fillId)
                .eq(ScheduleFillDataJob::getFillGenerateStatus,originalStatus)
                .update(updateFillDataJob);
    }
}
