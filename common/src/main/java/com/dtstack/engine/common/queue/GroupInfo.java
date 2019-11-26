package com.dtstack.engine.common.queue;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2018/9/10
 */
public class GroupInfo {

    private long priority;

    private int size;

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
