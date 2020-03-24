package com.dtstack.engine.service.task;

import com.dtstack.engine.common.ClientCache;
import com.dtstack.engine.common.IClient;
import com.dtstack.engine.common.JobClient;
import com.dtstack.engine.common.enums.RdosTaskStatus;
import com.dtstack.engine.common.exception.ClientAccessException;
import com.dtstack.engine.common.exception.ClientArgumentException;
import com.dtstack.engine.common.exception.LimitResourceException;
import com.dtstack.engine.common.pojo.JobResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 发送具体任务线程
 * Date: 2017/11/27
 * Company: www.dtstack.com
 *
 * @author xuchao
 */

public class JobSubmitProcessor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobSubmitProcessor.class);

    private JobClient jobClient;
    private Handler handler;

    public JobSubmitProcessor(JobClient jobClient, Handler handler) {
        this.jobClient = jobClient;
        this.handler = handler;
    }

    @Override
    public void run() {


    }


    public interface Handler {

        /**
         * Something has happened, so handle it.
         */
        void handle();
    }
}
