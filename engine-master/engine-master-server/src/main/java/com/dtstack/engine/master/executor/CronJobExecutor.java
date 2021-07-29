package com.dtstack.engine.master.executor;

import com.dtstack.engine.common.enums.EScheduleType;
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

    private final Logger LOGGER = LoggerFactory.getLogger(CronJobExecutor.class);

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.NORMAL_SCHEDULE;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop CronJobExecutor----");
    }

}
