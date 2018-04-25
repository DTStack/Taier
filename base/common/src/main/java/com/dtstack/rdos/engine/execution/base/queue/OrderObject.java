package com.dtstack.rdos.engine.execution.base.queue;

import java.io.Serializable;
import java.util.UUID;


/**
 *
 * @author sishu.yss
 *
 */
public class OrderObject implements Serializable{

    protected String id = UUID.randomUUID().toString();

    protected int priority = 0;

    private long generateTime = System.nanoTime();

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public long getGenerateTime() {
        return generateTime;
    }

    public void setGenerateTime(long generateTime) {
        this.generateTime = generateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
