package com.dtstack.engine.alert.client;

import com.dtstack.engine.alert.domian.AlertEvent;
import com.dtstack.engine.alert.enums.AGgateType;
import com.dtstack.engine.alert.pool.CustomDiscardPolicy;
import com.dtstack.engine.alert.pool.CustomThreadFactory;
import com.dtstack.lang.data.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class AlertGateApiFacade {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    AlertServiceProvider alertServiceProvider;

    @Value("${sms.rcv.threadpool.size:10}")
    private int smsThreadPoolSize;

    @Value("${sms.rcv.threadpool.queue.size:10000}")
    private int smsThreadPoolQueueSize;

    private ThreadPoolExecutor smsExecutor;

    @PostConstruct
    public void init() {
        smsExecutor = new ThreadPoolExecutor(smsThreadPoolSize, smsThreadPoolSize, 0, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(smsThreadPoolQueueSize), new CustomThreadFactory("smsRcv"), new CustomDiscardPolicy(logger));
    }






    /**
     * <p>
     * 提供同步发送短信服务
     * </p>
     *
     * @param alertEvent
     * @return
     */
    public R sendSync(AlertEvent alertEvent, AGgateType aGgateType) {

        return alertServiceProvider.send(alertEvent,aGgateType);
    }

    public void sendAsync(AlertEvent alertEvent,AGgateType aGgateType) {
        smsExecutor.execute(() -> sendSync(alertEvent,aGgateType));
    }
}

