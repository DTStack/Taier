package com.dtstack.engine.master.impl;

import com.dtstack.engine.common.CustomThreadFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yuebai
 * @date 2020-06-24
 */
public class Test111 {

    public static void main(String[] args) throws Exception {
        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime yyyyMMddHHmmss = DateTime.parse("2020-06-28 20:32:40", timeFormatter);
        DateTime dateTime = yyyyMMddHHmmss.plusMillis(600000);
        System.out.println(dateTime.toString("yyyy-MM-dd HH:mm:ss"));


        DateTime yyyyMMddHHmmss2 = DateTime.parse("2020-06-28 14:16:36", timeFormatter);
        DateTime dateTime2 = yyyyMMddHHmmss2.plusMillis(27803920);
        System.out.println(dateTime2.toString("yyyy-MM-dd HH:mm:ss"));

        ScheduledExecutorService scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobGraphTrigger"));
        scheduledService.scheduleAtFixedRate(
                () -> {
                    System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss" + "------------"));
                },
                500,
                60 * 1000,
                TimeUnit.MILLISECONDS);
        Thread.sleep(100000000L);
    }
}
