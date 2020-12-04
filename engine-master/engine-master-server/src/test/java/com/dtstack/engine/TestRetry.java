package com.dtstack.engine;

import com.dtstack.engine.common.util.RetryUtil;

/**
 * @author yuebai
 * @date 2020-12-04
 */
public class TestRetry {

    public static void main(String[] args) throws Exception{
        RetryUtil.executeWithRetry(() -> {
            System.out.println("---------------");
            throw new Exception("time out");
        }, 3, 30000, false, null);

        Thread.sleep(100000000);
    }
}
