package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.CustomThreadFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yuebai
 * @date 2020-06-24
 */
public class Test111 {

    public static void main(String[] args) throws Exception{
            ExecutorService taskStatusPool = new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(5), new CustomThreadFactory("testDealJob"));

            Semaphore buildSemaphore = new Semaphore(8);
            CountDownLatch ctl = new CountDownLatch(10000);
            for (int i=0; i < 10000;i++) {
                try {
                    buildSemaphore.acquire();
//                    System.out.println(i);
                    taskStatusPool.submit(() -> {
                        try {
//                            logger.info("--------------:");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            buildSemaphore.release();
                            ctl.countDown();
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
//                    logger.error("[emergency] error:", e);
                }
            }
            ctl.await();
        System.out.println("-----------");
        taskStatusPool.shutdownNow();

    }
}
