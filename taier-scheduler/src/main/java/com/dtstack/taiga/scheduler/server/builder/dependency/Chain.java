package com.dtstack.taiga.scheduler.server.builder.dependency;

/**
 * @Auther: dazhi
 * @Date: 2022/1/4 5:49 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface Chain<T> {

    /**
     * 获取下一个
     */
    T next();

    /**
     * 设置下一个
     */
    void setNext(T t);
}
