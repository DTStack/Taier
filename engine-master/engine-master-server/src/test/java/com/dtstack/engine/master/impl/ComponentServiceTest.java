package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.CustomThreadFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author yuebai
 * @date 2020-06-10
 */
public class ComponentServiceTest  {

    private static final Logger LOGGER = LoggerFactory.getLogger(ComponentServiceTest.class);

    private static ThreadPoolExecutor connectPool =  new ThreadPoolExecutor(5, 5,
            60L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10),
            new CustomThreadFactory("connectPool"));

    @Test
    public void testCount() throws Exception{
        CountDownLatch countDownLatch = new CountDownLatch(10);
        CompletableFuture.runAsync(() -> {
            countDownLatch.countDown();
            if(countDownLatch.getCount()<5L){
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        },connectPool).get(100, TimeUnit.SECONDS);
        try {
            countDownLatch.await();
            LOGGER.error("test connect ----------------  ");
        } catch (InterruptedException e) {
            LOGGER.error("test connect  await {}  error ", e);
        }
    }

}
