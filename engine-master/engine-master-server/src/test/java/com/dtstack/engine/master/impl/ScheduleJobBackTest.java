package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.BaseTest;
import com.dtstack.engine.master.scheduler.ScheduleJobBack;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.*;

/**
 * @author yuebai
 * @date 2020-06-23
 */
public class ScheduleJobBackTest extends BaseTest {


    private static Logger logger = LoggerFactory.getLogger(ScheduleJobBackTest.class);

    @Autowired
    private ScheduleJobBack scheduleJobBack;

    @Test
    public void testScheduleJobBack(){
        scheduleJobBack.setIsMaster(true);
    }

    @Test
    public void testOut() throws Exception{
        ExecutorService taskStatusPool = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS,
                new SynchronousQueue<>(true), new CustomThreadFactory("testDealJob"));

        Semaphore buildSemaphore = new Semaphore(5);
        CountDownLatch ctl = new CountDownLatch(10000);
        for (int i = 0; i < 10000; i++) {
            try {
                buildSemaphore.acquire();
                taskStatusPool.submit(() -> {
                    try {
                        Thread.sleep(100);
                        logger.info("--------------:");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        buildSemaphore.release();
                        ctl.countDown();
                    }
                });
            } catch (Throwable e) {
                logger.error("[emergency] error:", e);
            }
        }
        ctl.await();
        System.out.println("-----------");
        taskStatusPool.shutdownNow();
    }
}
