package com.dtstack.engine.master.event;

import com.dtstack.engine.common.enums.RdosTaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuebai
 * @date 2020-07-28
 */
public class ScheduleJobEventPublisher {

    private static volatile ScheduleJobEventPublisher publisher = null;

    private final Logger logger = LoggerFactory.getLogger(ScheduleJobEventPublisher.class);

    private List<ScheduleJobEventLister> scheduleJobEventMulticaster;

    private ScheduleJobEventPublisher() {
        this.scheduleJobEventMulticaster = new ArrayList<>();
    }

    public static ScheduleJobEventPublisher getInstance() {
        if (publisher == null) {
            synchronized (ScheduleJobEventPublisher.class) {
                if (publisher == null) {
                    publisher = new ScheduleJobEventPublisher();
                }
            }
        }
        return publisher;
    }

    public void register(ScheduleJobEventLister lister) {
        scheduleJobEventMulticaster.add(lister);
    }

    public void publishBatchEvent(ScheduleJobBatchEvent event) {
        if (null == event) {
            return;
        }
        if (CollectionUtils.isEmpty(scheduleJobEventMulticaster)) {
            return;
        }
        if (CollectionUtils.isEmpty(event.getJobIds()) || null == event.getStatus()) {
            return;
        }
        if (RdosTaskStatus.getStoppedStatus().contains(event.getStatus())) {
            logger.info("publishBatchEvent {}", event);
        }
        for (ScheduleJobEventLister scheduleJobEventLister : scheduleJobEventMulticaster) {
            scheduleJobEventLister.publishBatchEvent(event);
        }
    }
}
