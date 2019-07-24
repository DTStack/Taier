package com.dtstack.rdos.engine.execution.flink150.restart;

import com.dtstack.rdos.common.util.PublicUtil;
import com.dtstack.rdos.engine.execution.base.restart.IJobRestartStrategy;
import com.dtstack.rdos.engine.execution.flink150.FlinkPerJobResourceInfo;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * @description:
 * @author: maqi
 * @create: 2019/07/17 17:36
 */
public class FlinkUndoRestart implements IJobRestartStrategy {

    @Override
    public String restart(String jobInfo, int retryNum) {
        return jobInfo;
    }

}