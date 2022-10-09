package com.dtstack.taier.datasource.api.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 重试工具类
 *
 * @author ：wangchuan
 * date：Created in 13:49 2022/9/23
 * company: www.dtstack.com
 */
@Slf4j
public final class RetryUtils {

    /**
     * 最休眠时间
     */
    private static final long MAX_SLEEP_MILLISECOND = TimeUnit.SECONDS.toMillis(256L);

    /**
     * 在外部线程执行并且重试。每次执行需要在timeoutMs内执行完，不然视为失败。
     * 执行异步操作的线程池从外部传入，线程池的共享粒度由外部控制。比如，HttpClientUtil共享一个线程池。
     * <p/>
     * 限制条件：仅仅能够在阻塞的时候interrupt线程
     *
     * @param callable               实际逻辑
     * @param retryTimes             最大重试次数（>1）
     * @param sleepTimeInMilliSecond 运行失败后休眠对应时间再重试
     * @param exponential            休眠时间是否指数递增
     * @param timeoutMs              callable执行超时时间，毫秒
     * @param executor               执行异步操作的线程池
     * @param <T>                    返回值类型
     * @return 经过重试的callable的执行结果
     */
    public static <T> T asyncExecuteWithRetry(Callable<T> callable,
                                              int retryTimes,
                                              long sleepTimeInMilliSecond,
                                              boolean exponential,
                                              long timeoutMs,
                                              ThreadPoolExecutor executor) throws Exception {
        Retry retry = new AsyncRetry(timeoutMs, executor);
        return retry.doRetry(callable, retryTimes, sleepTimeInMilliSecond, exponential);
    }


    /**
     * 重试抽象
     */
    private static class Retry {

        /**
         * 进行重试
         *
         * @param callable               执行回调
         * @param retryTimes             重试次数
         * @param sleepTimeInMilliSecond 重试间隔时间
         * @param exponential            休眠时间是否指数递增
         * @param <T>                    结果范型
         * @return 执行结果
         * @throws Exception 执行异常
         */
        public <T> T doRetry(Callable<T> callable, int retryTimes, long sleepTimeInMilliSecond, boolean exponential)
                throws Exception {

            if (null == callable) {
                throw new IllegalArgumentException("callable can't be null.");
            }

            if (retryTimes < 1) {
                throw new IllegalArgumentException("retryTimes cannot be less than 1");
            }

            Exception saveException = null;
            for (int i = 0; i < retryTimes; i++) {
                try {
                    return call(callable);
                } catch (Exception e) {
                    saveException = e;
                    if (i == 0) {
                        log.error("Exception when calling callable.", saveException);
                    }

                    if (i + 1 < retryTimes && sleepTimeInMilliSecond > 0) {
                        long startTime = System.currentTimeMillis();

                        long timeToSleep;
                        if (exponential) {
                            // 等待时间指数增加
                            timeToSleep = sleepTimeInMilliSecond * (long) Math.pow(2, i);
                        } else {
                            timeToSleep = sleepTimeInMilliSecond;
                        }
                        if (timeToSleep >= MAX_SLEEP_MILLISECOND) {
                            timeToSleep = MAX_SLEEP_MILLISECOND;
                        }

                        try {
                            Thread.sleep(timeToSleep);
                        } catch (InterruptedException ignored) {
                        }

                        long realTimeSleep = System.currentTimeMillis() - startTime;

                        // 记录计划等待时间和实际等待时间
                        log.error(String.format("Exception when calling callable, Attempting to perform the [%s]st retry The " +
                                        "planned waiting time for this retry is [%s] MS, and the actual waiting time is [%s] MS",
                                i + 1, timeToSleep, realTimeSleep));

                    }
                }
            }
            throw saveException;
        }

        /**
         * 真正执行的逻辑
         *
         * @param callable 执行回调
         * @param <T>      结果范型
         * @return 执行结果
         * @throws Exception 执行时的异常信息
         */
        protected <T> T call(Callable<T> callable) throws Exception {
            return callable.call();
        }
    }

    /**
     * 异步执行策略类
     */
    private static class AsyncRetry extends Retry {

        /**
         * 执行等待超时时间
         */
        private final long timeoutMs;

        /**
         * 执行使用的线程池
         */
        private final ThreadPoolExecutor executor;

        public AsyncRetry(long timeoutMs, ThreadPoolExecutor executor) {
            this.timeoutMs = timeoutMs;
            this.executor = executor;
        }

        /**
         * 使用传入的线程池异步执行任务, 增加超时取消逻辑
         *
         * @param callable 回调执行
         * @param <T>      执行结果范型
         * @return 执行结果
         * @throws Exception 执行异常
         */
        @Override
        protected <T> T call(Callable<T> callable) throws Exception {
            Future<T> future = executor.submit(callable);
            try {
                return future.get(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                log.warn("Try once failed", e);
                throw e;
            } finally {
                if (!future.isDone()) {
                    future.cancel(true);
                    log.warn("Try once task not done, cancel it, active count: " + executor.getActiveCount());
                }
            }
        }
    }
}
