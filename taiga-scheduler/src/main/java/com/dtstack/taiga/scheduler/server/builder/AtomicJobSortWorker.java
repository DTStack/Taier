package com.dtstack.taiga.scheduler.server.builder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 7:56 PM
 * @Email: dazhi@dtstack.com
 * @Description:
 */
public class AtomicJobSortWorker implements JobSortWorker {

    private final AtomicInteger atomicInteger;

    public AtomicJobSortWorker () {
        atomicInteger = new AtomicInteger();
    }

    @Override
    public Long getSort() {
        return atomicInteger.longValue();
    }
}
