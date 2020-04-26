package com.dtstack.schedule.common.metric;

/**
 * Reason:
 * Date: 2018/10/9
 * Company: www.dtstack.com
 * @author xuchao
 */

public class Tuple<T, V> {

    private final T one;

    private final V two;

    public Tuple(T one, V two){
        this.one = one;
        this.two = two;
    }

    public T getOne() {
        return one;
    }

    public V getTwo() {
        return two;
    }
}
