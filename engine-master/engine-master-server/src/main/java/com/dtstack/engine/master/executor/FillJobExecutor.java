package com.dtstack.engine.master.executor;

import com.dtstack.engine.common.enums.EScheduleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

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

    private final Logger LOGGER = LoggerFactory.getLogger(FillJobExecutor.class);

    @Override
    public EScheduleType getScheduleType() {
        return EScheduleType.FILL_DATA;
    }

    @Override
    public void stop() {
        RUNNING.set(false);
        LOGGER.info("---stop FillJobExecutor----");
    }

    @Override
    protected Long getListMinId(String nodeAddress, Integer isRestart) {
        Long listMinId = super.getListMinId(nodeAddress, isRestart);
        return Objects.isNull(listMinId) ? 0L:listMinId;
    }
}
