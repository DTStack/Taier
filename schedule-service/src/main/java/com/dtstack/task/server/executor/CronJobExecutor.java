package com.dtstack.task.server.executor;

import com.dtstack.task.common.enums.EScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 调度任务的执行器
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/22
 */
@Component
public class CronJobExecutor extends AbstractJobExecutor {

    private final Logger logger = LoggerFactory.getLogger(CronJobExecutor.class);

    @Override
    public Integer getScheduleType() {
        return EScheduleType.NORMAL_SCHEDULE.getType();
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        logger.info("---stop CronJobExecutor----");
    }

}
