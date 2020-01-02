package com.dtstack.engine.common.queue;

import java.io.Serializable;
import java.util.UUID;


/**
 *
 * @author sishu.yss
 *
 */
public class OrderObject implements Serializable{

    protected String id = UUID.randomUUID().toString();

    protected long priority = 0;

    public long getPriority() {
        return priority;
    }

    public void setPriority(long priority) {
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
