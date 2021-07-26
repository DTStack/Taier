package com.dtstack.engine.remote.config;

/**
 * @Auther: dazhi
 * @Date: 2020/9/8 5:14 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class FallbackContext {
    private static final ThreadLocal<Exception> threadLocal = new ThreadLocal<>();

    public static Boolean set(Exception t) {
        threadLocal.set(t);
        return Boolean.TRUE;
    }

    public static Exception get(){
        return threadLocal.get();
    }

    public static void remove(){
        threadLocal.remove();
    }
}
