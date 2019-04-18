package com.dtstack.rdos.engine.service.node;

import com.dtstack.rdos.common.config.ConfigParse;
import com.dtstack.rdos.engine.execution.base.CustomThreadFactory;
import com.dtstack.rdos.engine.execution.base.pojo.ParamAction;
import com.dtstack.rdos.engine.service.enums.StoppedStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务停止消息
 * 不需要区分是不是主节点才启动处理线程
 * Date: 2018/1/22
 * Company: www.dtstack.com
 * @author xuchao
 */

public class JobStopQueue {

    private static final Logger LOG = LoggerFactory.getLogger(JobStopQueue.class);

    private DelayQueue<StoppedJob<ParamAction>> queue = new DelayQueue<StoppedJob<ParamAction>>();

    private JobStopAction jobStopAction;

    private final int jobStoppedRetry;
    /**
     * delay 3 second
     */
    private final long jobStoppedDelay;

    private ExecutorService simpleES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new CustomThreadFactory("stopProcessor"));

    private StopProcessor stopProcessor = new StopProcessor();

    public JobStopQueue(WorkNode workNode) {
        this.jobStopAction = new JobStopAction(workNode);
        this.jobStoppedRetry = ConfigParse.getJobStoppedRetry();
        this.jobStoppedDelay = ConfigParse.getJobStoppedDelay();
    }

    public void start(){
        if(simpleES.isShutdown()){
            simpleES = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new CustomThreadFactory("stopProcessor"));
            stopProcessor.reStart();
        }

        simpleES.submit(stopProcessor);
    }

    public void stop(){
        stopProcessor.stop();
        simpleES.shutdownNow();
    }

    public void addJob(ParamAction paramAction) {
        queue.add(new StoppedJob<ParamAction>(paramAction));
    }


    class StopProcessor implements Runnable{

        private boolean run = true;

        @Override
        public void run() {

            LOG.info("job stop process thread is start...");

            while (run){
                try {
                    StoppedJob<ParamAction> stoppedJob = queue.take();
                    StoppedStatus stoppedStatus = jobStopAction.stopJob(stoppedJob.job);
                    switch (stoppedStatus) {
                        case STOPPED:
                        case MISSED:
                            break;
                        case STOPPING:
                            if (!stoppedJob.isRetry()) {
                                break;
                            }
                            stoppedJob.incrCount();
                            stoppedJob.reset();
                            queue.add(stoppedJob);
                        default:
                    }
                } catch (Exception e) {
                    LOG.error("", e);
                }
            }

            LOG.info("job stop process thread is shutdown...");

        }

        public void stop() {
            this.run = false;
        }

        public void reStart() {
            this.run = true;
        }
    }

    private class StoppedJob<T> implements Delayed {
        private int count;
        private T job;
        private int retry;
        private long now;
        private long expired;
        private StoppedJob(T job) {
            this.job = job;
            this.retry = jobStoppedRetry;
            this.now = System.currentTimeMillis();
            this.expired = now + jobStoppedDelay;
        }
        private void incrCount() {
            count += 1;
        }
        private boolean isRetry() {
            return retry == 0 || count <= retry;
        }
        private void reset() {
            this.now = System.currentTimeMillis();
            this.expired = now + jobStoppedDelay;
        }
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(this.expired - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }
        @Override
        public int compareTo(Delayed o) {
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }
    }
}
