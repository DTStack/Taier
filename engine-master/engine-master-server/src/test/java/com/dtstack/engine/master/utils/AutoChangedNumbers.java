package com.dtstack.engine.master.utils;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class AutoChangedNumbers {
    static final AtomicInteger INCR = new AtomicInteger(100);
    static final AtomicLong ID = new AtomicLong(100000000);
}
