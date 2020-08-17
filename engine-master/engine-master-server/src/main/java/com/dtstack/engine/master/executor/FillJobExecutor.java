package com.dtstack.engine.master.executor;

import com.dtstack.engine.common.enums.EScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 补数据任务的执行器
 * <p>
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/30
 */
@Component
public class FillJobExecutor extends AbstractJobExecutor {

    private final Logger logger = LoggerFactory.getLogger(FillJobExecutor.class);

    @Override
    public Integer getScheduleType() {
        return EScheduleType.FILL_DATA.getType();
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        this.disasterPreparedness();
        logger.info("---stop FillJobExecutor----");
    }

}
