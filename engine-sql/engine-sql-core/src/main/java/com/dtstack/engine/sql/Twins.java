package com.dtstack.engine.sql;

import java.io.Serializable;

public final class Twins<L, R> implements Serializable {
    private static final long serialVersionUID = 1L;
    public final L key;
    public final R type;

    public static <L, R> Twins<L, R> of(L key, R type) {
        return new Twins(key, type);
    }

    public Twins(L key, R type) {
        this.key = key;
        this.type = type;
    }

    public L getKey() {
        return key;
    }

    public R getType() {
        return type;
    }

    @Override
    public String toString() {
        return "" + '(' + this.getKey() + ',' + this.getType() + ')';
    }

}
