package com.dtstack.engine.master.scheduler;

import com.dtstack.engine.common.CustomThreadFactory;
import com.dtstack.engine.master.env.EnvironmentContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * company: www.dtstack.com
 *
 * @author: toutian
 * create: 2019/10/22
 */
@Component
public class JobGraphBuilderTrigger implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(JobGraphBuilderTrigger.class);

    private static final long PERIOD_DAY = 24 * 3600 * 1000;

    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);

    @Autowired
    private EnvironmentContext environmentContext;

    @Autowired
    private JobGraphBuilder jobGraphBuilder;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd");

    private ScheduledExecutorService scheduledService;

    public JobGraphBuilderTrigger() {
        scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobGraphTrigger"));
    }

    public void dealMaster(boolean isMaster) {
        try {
            if (isMaster) {
                startJobGraph();
            } else {
                stopJobGraph();
            }
        } catch (Throwable e) {
            logger.error("{}", e);
        }
    }

    private void startJobGraph() {
        if (RUNNING.get()) {
            return;
        }
        if (scheduledService.isShutdown()) {
            scheduledService = new ScheduledThreadPoolExecutor(1, new CustomThreadFactory("JobGraphTrigger"));
        }
        scheduledService.scheduleAtFixedRate(
                this,
                getDelayTime(),
                PERIOD_DAY,
                TimeUnit.MILLISECONDS);
        RUNNING.compareAndSet(false, true);
        logger.info("start job graph trigger...");
    }

    private void stopJobGraph() {
        if (scheduledService != null) {
            scheduledService.shutdownNow();
        }
        RUNNING.compareAndSet(true, false);
        logger.info("stop job graph trigger...");
    }

    private long getDelayTime() {
        String cron = environmentContext.getJobGraphBuildCron();
        long mill = getTimeMillis(cron);
        long delay = mill - System.currentTimeMillis();
        delay = delay > 0 ? delay : delay + PERIOD_DAY;
        return delay;
    }

    private long getTimeMillis(String time) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
            SimpleDateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");
            Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);
            return curDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void run() {
        if (RUNNING.get()) {
            logger.warn("---trigger to build job graph start---");

//            DateTime dateTime = DateTime.now();
//            dateTime = dateTime.plusDays(1);
//
//            String triggerDay = dateTime.toString(dateTimeFormatter);
//            try {
//                jobGraphBuilder.buildTaskJobGraph(triggerDay);
//            } catch (Exception e) {
//                logger.error("", e);
//            }
            //注意不需要将jobList直接加入到缓存队列里面。等待执行到当天数据的时候再去获取
//            logger.warn("---trigger to build day:{} job graph end!--", triggerDay);
        } else {
            logger.warn("---triggering, but Running is false---");
        }
    }
}
